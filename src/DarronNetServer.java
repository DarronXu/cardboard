import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.*;
import java.util.HashMap;
import java.util.Scanner;
public class DarronNetServer {
	
	static int i=0;
	static int PORT_NUM=1111; 
	static HashMap<String,Socket> map=new HashMap<String,Socket>();
	static HashMap<String,OutputStreamWriter> map2=new HashMap<String,OutputStreamWriter>();
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
				Scanner scan=new Scanner(istream);
				String name=scan.nextLine();
				map.put(name, client);
				map2.put(name, new OutputStreamWriter(client.getOutputStream()));
				Thread speaker=new Thread(new ServerSpeaker(client,scan));
				speaker.start();
				System.out.println("["+client.getInetAddress()+"] CLIENT ACCEPTED.");
			} catch(Exception err) {
				System.err.println("Failed to start a conversation thread with a client.\n"+err.toString());
			}
		}
	}

	static class ServerSpeaker implements Runnable{
		Socket client;
		Scanner mScan;
		public ServerSpeaker(Socket sClient, Scanner scan) {
			client=sClient;
			mScan=scan;
		}
		public void run() {
			String to=mScan.nextLine();
			while(client.isConnected()){
				String line=mScan.nextLine();
				OutputStreamWriter writer=map2.get(to);
				try {
					writer.write(line+"\n");
					writer.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if(line != null && line.length()>0) System.out.println("["+client.getInetAddress()+"] "+line);
			}
		}
	}
}
