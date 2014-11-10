import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.Scanner;

public class PiClientB {
	static int PORT_NUM = 2222;

	static final BigDecimal _one=new BigDecimal("-1");
	static final BigDecimal one=new BigDecimal("1");
	static final BigDecimal two=new BigDecimal("2");
	static final BigDecimal zero=new BigDecimal("0");
	
	
	public static void main(String[] args) {
		Scanner stdin = new Scanner(System.in);
		System.out.print("Please input server IP or Hostname: ");
		String ip = stdin.next();

		int port = PORT_NUM;

		while (true) {
			Socket socket;
			Scanner scan;
			ObjectOutputStream objwriter;
			try {
				socket = new Socket(ip, port);
				scan=new Scanner(socket.getInputStream());
			} catch (Exception e) {
				System.err.println("Failed to connect to server.");
				e.printStackTrace();
				continue;
			}
			OutputStream out;
			String sThisN,sInterval,sAdd;
			BigDecimal thisN,interval,sum,ans=new BigDecimal("0");
			try {
				out = socket.getOutputStream();
				objwriter= new ObjectOutputStream(out);
				sThisN=scan.next();
				sInterval=scan.next();
				sAdd=scan.next();
				thisN=new BigDecimal(sThisN);
				interval=new BigDecimal(sInterval);
				sum=new BigDecimal(sAdd);
				if(sum.compareTo(thisN.add(interval))!=0) throw new Exception("Sum validation failed!");
			} catch (Exception e) {
				System.err.println("Failed to create OutputStream from socket.");
				e.printStackTrace();
				continue;
			}
			try {
				// TODO WRITE PI-ALGORITHM HERE
				BigDecimal i,two_i,tmp,tmp2;
				i=thisN;
				two_i=thisN.multiply(two);
				while (i.compareTo(sum)<0) {
					if(i.divideAndRemainder(two)[1].compareTo(one)==0)  tmp=_one;
					else tmp=one;
					tmp2=two_i.add(one);
					
					//int l=tmp2.toString().length()+2;
					//tmp=tmp.divide(tmp2,l*l,BigDecimal.ROUND_DOWN);
					
					int l=sum.toString().length();
					tmp=tmp.divide(tmp2,l*l,BigDecimal.ROUND_DOWN);
					
					ans=ans.add(tmp);
					i=i.add(one);
					two_i=two_i.add(two);
				}
				objwriter.writeObject(ans);
				objwriter.flush();
				objwriter.close();
				scan.close();
				socket.close();
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}
}
