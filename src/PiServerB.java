import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.math.BigDecimal;
import java.math.BigInteger;


public class PiServerB {
	static int PORT_NUM=2222;
	public static void main(String[] args) {
		ServerSocket socket;
		try {
			socket=new ServerSocket(PORT_NUM);
		} catch (IOException e) {
			System.err.println("Failed to start listening.");
			e.printStackTrace();
			return;
		}
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
	
	static BigDecimal ans=new BigDecimal("0");
	static BigDecimal nextN=new BigDecimal("0");
	static BigDecimal interval=new BigDecimal("4000000");
	static BigDecimal two=new BigDecimal("2");
	static BigDecimal four=new BigDecimal("4");

	static class ServerSpeaker implements Runnable{
		Socket client;
		public ServerSpeaker(Socket sClient) {
			client=sClient;
		}
		public void run() {
			InputStream istream;
			OutputStream ostream;
			ObjectInputStream reader;
			OutputStreamWriter writer;
			try {
				istream=client.getInputStream();
				ostream=client.getOutputStream();
				reader=new ObjectInputStream(istream);
				writer=new OutputStreamWriter(ostream);
			} catch (IOException e) {
				System.err.println("Failed to Open Input Stream from Client"+client.getInetAddress().toString()+":\n"+e.toString());
				return;
			}
			BigDecimal thisN;
			synchronized(this) {
				thisN=nextN;
				nextN=nextN.add(interval);
			}
			try {
				writer.write(thisN.toString()+System.lineSeparator());
				writer.write(interval.toString()+System.lineSeparator());
				writer.write(thisN.add(interval).toString()+System.lineSeparator());
				writer.flush();
			} catch (Exception err) {
				synchronized(this) {
					nextN=nextN.subtract(interval);
				}
				err.printStackTrace();
				return;
			}
			if(client.isConnected()) {
				try {
					BigDecimal clAns=(BigDecimal)reader.readObject();
					if(clAns==null) throw new Exception("Ans verification failed!");
					ans=ans.add(clAns);
					//System.out.println(clAns.toString());
					System.out.println(ans.multiply(four).toString());
				} catch (Exception err) {
					err.printStackTrace();
				}
			}
		}
	}
}
