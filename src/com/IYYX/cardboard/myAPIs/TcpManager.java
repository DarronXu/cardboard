package com.IYYX.cardboard.myAPIs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;

public class TcpManager {
	private static boolean bIsInitiated=false;
	private static boolean bIsInitiating=false;
	
	private static Socket socketToServer;
	private static InputStream fromServer;
	private static OutputStream toServer;
	private static ObjectOutputStream serverWriter;
	private static ObjectInputStream serverReader;
	
	private static String readHTTP(String urlStr){
		URL url;
		HttpURLConnection connection = null;
		try {
			// Create connection
			url = new URL(urlStr);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setDoInput(true);

			// Get Response
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\n');
			}
			rd.close();
			return response.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot connect to server!");
		} finally {
			if (connection != null)
				connection.disconnect();
		}
	}
	
	public static void initiate(String serverIPorHostname, String myName){
		if(bIsInitiating) return;
		mMyName=myName;
		String[] portStrs=readHTTP("http://cforphone.ngrok.com/tcp-port.php").split("\n");
		int port=Integer.parseInt(portStrs[portStrs.length-1]);
		System.err.println(port);
		try {
			socketToServer = new Socket(serverIPorHostname,port);
			toServer = socketToServer.getOutputStream();
			fromServer = socketToServer.getInputStream();
			serverWriter = new ObjectOutputStream(toServer);
			serverReader = new ObjectInputStream(fromServer);
			serverWriter.writeObject(myName);
			serverWriter.flush();
		} catch(IOException err) {
			err.printStackTrace();
			throw new RuntimeException("Cannot connect to server!");
		}
		Thread thread=new Thread(threadTwo);
		thread.start();
		bIsInitiated=true;
		bIsInitiating=true;
	}
	static OnCallSucceedListener lCallSucceed;
	static OnBeingCalledListener lBeingCalled;
	static OnNewDataListener lNewData;
	public static void setListener(OnCallSucceedListener listener){
		lCallSucceed=listener;
	}
	public static void setListener(OnBeingCalledListener listener){
		lBeingCalled=listener;
	}
	public static void setListener(OnNewDataListener listener){
		lNewData=listener;
	}
	public static void disableOnNewDataListener(){lNewData=null;}
	public static boolean isInitiated(){return bIsInitiated;}
	private static boolean isCallEstablished=false;
	private static boolean hasServerReceivedContactName=false;
	public static void call(String contactName) {
		boolean success=false;
		try {
			mContactName=contactName;
			serverWriter.writeObject(contactName);
			serverWriter.flush();
			success=true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(!success) return;
		isCallEstablished=true;
		hasServerReceivedContactName=true;
		if(lCallSucceed!=null) lCallSucceed.OnCallSucceed();
	}
	public static String mContactName,mMyName;
	public static Object mLatestObject;
	public static void sendObj(Object obj){
		if(!isCallEstablished) return;
		try {
			if(!hasServerReceivedContactName){
				serverWriter.writeObject(mContactName);
				serverWriter.flush();
				hasServerReceivedContactName=true;
			}
			serverWriter.writeObject(obj);
			serverWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static Object getLatestObj(){return mLatestObject;}
	
	public static interface OnCallSucceedListener{
		public void OnCallSucceed();
	}
	public static interface OnBeingCalledListener{
		public void OnBeingCalled(String contactName);
	}
	public static interface OnNewDataListener{
		public void OnNewData();
	}
	private static Runnable threadTwo=new Runnable(){
		public void run() {
			while(true){
				boolean success=false;
				try{
					mContactName=(String)serverReader.readObject();
					mLatestObject=serverReader.readObject();
					success=true;
				} catch(ClassNotFoundException|IOException e) {
					e.printStackTrace();
				}
				if(!success) continue;
				if(!isCallEstablished)
				{
					isCallEstablished=true;
					if(lBeingCalled!=null) lBeingCalled.OnBeingCalled(mContactName);
				}
				if(lNewData!=null) lNewData.OnNewData();
			}
		}
	};
}