package PureTcpIp;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.Scanner;
public class NetServer {
	
	static int PORT_NUM=1111; 
	
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
				Thread speaker=new Thread(new ServerSpeaker(client));
				speaker.start();
				System.out.println("["+client.getInetAddress()+"] CLIENT ACCEPTED.");
			} catch(Exception err) {
				System.err.println("Failed to start a conversation thread with a client.\n"+err.toString());
			}
		}
	}

	static class ServerSpeaker implements Runnable{
		Socket client;
		public ServerSpeaker(Socket sClient) {
			client=sClient;
		}
		public void run() {
			InputStream istream;
			try {
				istream=client.getInputStream();
			} catch (IOException e) {
				System.err.println("Failed to Open Input Stream from Client"+client.getInetAddress().toString()+":\n"+e.toString());
				return;
			}
			Scanner scan=new Scanner(istream);
			while(client.isConnected()){
				String line=scan.nextLine();
				if(line != null && line.length()>0) System.out.println("["+client.getInetAddress()+"] "+line);
			}
		}
	}
}
