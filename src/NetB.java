import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.*;
import java.util.Scanner;
public class NetB {

	public static void main(String[] args) {
		Scanner stdin=new Scanner(System.in);
		System.out.print("Please input your name: ");
		String clientName = stdin.next();
		System.out.print("Please input server IP or Hostname: ");
		String serverIP=stdin.next();
		System.out.print("Please input server Port: ");
		int port=stdin.nextInt();
		Socket socket;
		try {
			socket=new Socket(serverIP,port);
		} catch (Exception e) {
			System.err.println("Failed to connect to server.");
			e.printStackTrace();
			return;
		}
		OutputStream out;
		InputStream in;
		try {
			out=socket.getOutputStream();
			in=socket.getInputStream();
		} catch (IOException e) {
			System.err.println("Failed to create OutputStream from socket.");
			e.printStackTrace();
			return;
		}
		OutputStreamWriter writer=new OutputStreamWriter(out);
		Scanner reader = new Scanner(in);
		try {
			writer.write(clientName+"\n");
			writer.flush();
			System.out.print("Please input your friend's name: ");
			String listener = stdin.next();
			writer.write(listener + "\n");
			writer.flush();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		/*
		 * Important reading from server.
		 */
		try {
			socket.setSoTimeout(50);
		} catch (SocketException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		while(true)
		{
			try {
				if (reader.hasNextLine()){
					System.out.println(reader.nextLine());
				}
				if(System.in.available()>0) if(stdin.hasNextLine()) {
					String str=stdin.nextLine();
					writer.write(str+System.lineSeparator());
					writer.flush();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	} 

}