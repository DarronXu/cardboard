package PureTcpIp;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.*;
import java.util.HashMap;
import java.util.Scanner;
public class DarronNetServer1 {
	
	static int i=0;
	static int PORT_NUM=1111; 
	static HashMap<String,Socket> map=new HashMap<String,Socket>();
	static HashMap<String,ObjectOutputStream> map2=new HashMap<String,ObjectOutputStream>();
	public static void main(String[] args) {
		ServerSocket socket;
		
		try {
			socket=new ServerSocket(PORT_NUM);
		} catch (IOException e) {
			System.err.println("Failed to start listening.");
			e.printStackTrace();
			return;
		}
		while(true)
		{
			try {
				Socket client=socket.accept();
				InputStream istream=client.getInputStream();
				ObjectInputStream scan=new ObjectInputStream(istream);
				ObjectOutputStream obj=new ObjectOutputStream(client.getOutputStream());
				String name=(String)scan.readObject();
				map.put(name, client);
				map2.put(name, obj);
				Thread speaker=new Thread(new ServerSpeaker(client,scan,name));
				speaker.start();
				System.out.println("["+name+"] CLIENT ACCEPTED.");
			} catch(Exception err) {
				System.err.println("Failed to start a conversation thread with a client.\n"+err.toString());
			}
		}
	}

	static class ServerSpeaker implements Runnable{
		Socket client;
		ObjectInputStream mScan;
		String mMyName;
		public ServerSpeaker(Socket sClient, ObjectInputStream scan, String myName) {
			client=sClient;
			mScan=scan;
			mMyName=myName;
		}
		public void run() {
			String to;
			try {
				to = (String)mScan.readObject();
			} catch (ClassNotFoundException | IOException e1) {
				e1.printStackTrace();
				return;
			}
			while(client.isConnected()){
				String line=null;
				try {
					line=(String)mScan.readObject();
					ObjectOutputStream writer=map2.get(to);
					writer.writeObject(mMyName);
					writer.writeObject(line);
					writer.flush();
				} catch (IOException|ClassNotFoundException e) {
					e.printStackTrace();
					map.remove(mMyName);
					map2.remove(mMyName);
					return;
				}
				if(line != null && line.length()>0) System.out.println("["+mMyName+" => "+to+"] "+line);
			}
		}
	}
}