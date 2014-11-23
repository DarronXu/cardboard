package PureTcpIp;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.*;
import java.util.Scanner;

import com.IYYX.cardboard.myAPIs.TcpManager;
public class NetB2 {

	public static void main(String[] args) {
		Scanner stdin=new Scanner(System.in);
		System.out.print("Please input your name: ");
		String clientName = stdin.next();
		
		TcpManager.setListener(new TcpManager.OnNewDataListener(){
			public void OnNewData() {
				System.err.println("\n"+TcpManager.getLatestObj().toString()+"\n");
			}
		});
		
		while(true)
		{
			try {
				String str=stdin.nextLine();
				if(str.equals("^^^")) {
					System.out.print("Please input your friend's name: ");
					String contactName = stdin.next();
					while (contactName.equals("")){
						contactName = stdin.next();
					}
					TcpManager.call(contactName);
				}
				else if(str.equals("#!exit")) {
					System.err.println("EXIT !");
					TcpManager.reset();
					return;
				}
				else {
					TcpManager.sendObj(str);
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("ERR");
			}
		}
	}
}