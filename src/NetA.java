import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.*;
import java.util.Scanner;
public class NetA {

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
		while(true)
		{
			try {
				String str=stdin.nextLine();
				writer.write(str+System.lineSeparator());
				writer.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}