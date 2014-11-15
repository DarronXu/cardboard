package com.IYYX.cardboard.myAPIs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
	
	public static void initiate(String serverIPorHostname){
		if(bIsInitiating) return;
		String[] portStrs=readHTTP("cforphone.ngrok.com").split("\n");
		int port=Integer.parseInt(portStrs[portStrs.length]);
		try {
			socketToServer = new Socket(serverIPorHostname,port);
			toServer = socketToServer.getOutputStream();
			fromServer = socketToServer.getInputStream();
		} catch(IOException err) {
			err.printStackTrace();
			throw new RuntimeException("Cannot connect to server!");
		}
		bIsInitiating=true;
		bIsInitiated=true;
	}
	public static void setListener(OnCallSucceedListener listener){}
	public static void setListener(OnBeingCalledListener listener){}
	public static void setListener(OnNewDataListener listener){}
	public static void disableOnNewDataListener(){}
	public static boolean isInitiated(){return bIsInitiated;}
	public static void call(String contactName){}
	
	public static void sendObj(Object obj){}
	public static Object getLatestObj(){return null;}
	
	public static interface OnCallSucceedListener{
		public void OnCallSucceed();
	}
	public static interface OnBeingCalledListener{
		public void OnBeingCalled(String contactName);
	}
	public static interface OnNewDataListener{
		public void OnNewData();
	}
}