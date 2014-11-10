import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.math.BigDecimal;
import java.math.BigInteger;


public class PiServerA {
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
		runs=new BigDecimal("0");
		hits=new BigDecimal("0");
		while(true) {
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
	
	static BigDecimal runs,hits;

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
			while(client.isConnected()) {
				String sRuns=scan.nextLine();
				String sHits=scan.nextLine();
				String sXor=scan.nextLine();
				try {
					long clRuns,clHits,clXor;
					clRuns=Long.parseLong(sRuns);
					clHits=Long.parseLong(sHits);
					clXor=Long.parseLong(sXor);
					if(clXor!=(clRuns^clHits)) throw new Exception("Xor verification failed!");
					hits=hits.add(new BigDecimal(sHits));
					runs=runs.add(new BigDecimal(sRuns));
					BigDecimal ans=new BigDecimal("4.0");
					ans=ans.multiply(hits);
					ans=ans.divide(runs,hits.toString().length()+runs.toString().length(),BigDecimal.ROUND_HALF_UP);
					System.out.println(ans.toString());
				} catch (Exception err) {
					err.printStackTrace();
				}
				//if(line != null && line.length()>0) System.out.println("["+client.getInetAddress()+"] "+line);
			}
		}
	}
}
