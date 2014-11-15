package PureTcpIp;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;


public class PiClientA {
	static int PORT_NUM=1111; 
	public static void main(String[] args) {
		Scanner stdin=new Scanner(System.in);
		System.out.print("Please input server IP or Hostname: ");
		String ip=stdin.next();
		System.out.print("Please input server Port: ");
		int port=stdin.nextInt();
		Socket socket;
		try {
			socket=new Socket(ip,port);
		} catch (Exception e) {
			System.err.println("Failed to connect to server.");
			e.printStackTrace();
			return;
		}
		OutputStream out;
		try {
			out=socket.getOutputStream();
		} catch (IOException e) {
			System.err.println("Failed to create OutputStream from socket.");
			e.printStackTrace();
			return;
		}
		OutputStreamWriter writer=new OutputStreamWriter(out);
		while(true) {
			try {
				/* String str=stdin.nextLine();
				* writer.write(str+System.lineSeparator());
				* writer.flush();
				* */
				// TODO WRITE PI-ALGORITHM HERE
				long i,hits=0;
				for(i=0;i<10000000;i++) {
					double px=Math.random();
					double py=Math.random();
					if((px*px+py*py)<1.0) hits++;
				}
				writer.write(Long.valueOf(i).toString());
				writer.write(System.lineSeparator());
				writer.write(Long.valueOf(hits).toString());
				writer.write(System.lineSeparator());
				writer.write(Long.valueOf(i^hits).toString());
				writer.write(System.lineSeparator());
				writer.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
