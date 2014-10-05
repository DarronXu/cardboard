import java.util.Scanner;


public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner scan=new Scanner(System.in);
		//scan.skip("[\\s\\S]*?\\n"); 0.0000/1.213/13323.3 -123.4/1.1/1.1/ .0/-./-0.1
		//scan.useDelimiter("[/\\s]");
		System.out.println(scan.hasNextInt());
		scan.nextInt();
		System.out.println(scan.hasNextInt());
		//System.out.println(scan.hasNext(
				//"[ \\t\\d.\\-]*/[ \\t\\d.\\-]*/[ \\t\\d.\\-]*"+
				//"[ \\t\\d.\\-]*/[ \\t\\d.\\-]*/[ \\t\\d.\\-]*"+
				//"[ \\t\\d.\\-]*/[ \\t\\d.\\-]*/[ \\t\\d.\\-]*"));
		scan.close();
	}

}
