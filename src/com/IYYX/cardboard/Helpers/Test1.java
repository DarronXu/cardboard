package com.IYYX.cardboard.Helpers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.*;

import com.IYYX.cardboard.myAPIs.Model;
import com.IYYX.cardboard.myAPIs.MyCallback;

import android.R;

public class Test1 {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Model[] ans=null;
		System.out.println("This is not a class used in Android.");
		System.out.print("Loading");
		try {
			ans=Model.readPartitionedModel("boy.obj", 4, new TestCallback());
			System.out.println();
			int i=0;
			for(Model m : ans){
				System.out.println(i+" -- "+m.name);
				i++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    JFrame window = new JFrame();
	    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    window.setBounds(30, 30, 700, 700);
	    window.getContentPane().add(new MyCanvas(ans[5]));
	    window.setVisible(true);
	}
}

class MyCanvas extends JComponent {

	Model mModel;
	public MyCanvas(Model m){
		mModel=m;
	}
	
  public void paint(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;

    Image img1 = Toolkit.getDefaultToolkit().getImage("./res/drawable/boy_pivot_boy_pivot__feb7db1_dds.png");
    g2.drawImage(img1, 0, 0, this);
    
    float w=128;
    float h=128;
    
    for(int i=0;i<mModel.fCount;i++)
    {
    	int x1=(int)(w*mModel.textureUVs.get(i*6+0));
    	int y1=(int)(h*mModel.textureUVs.get(i*6+1));
    	int x2=(int)(w*mModel.textureUVs.get(i*6+2));
    	int y2=(int)(h*mModel.textureUVs.get(i*6+3));
    	int x3=(int)(w*mModel.textureUVs.get(i*6+4));
    	int y3=(int)(h*mModel.textureUVs.get(i*6+5));
    	g2.setColor(new Color(255, 0, 0));
    	g2.drawLine(x1, y1, x2, y2);
    	g2.drawLine(x2, y2, x3, y3);
    	g2.drawLine(x3, y3, x1, y1);
    }
    g2.finalize();
  }
}

class TestCallback extends MyCallback {
	public InputStream openAssetInput(String assetsName) throws IOException
	{
		return new FileInputStream("./assets/"+assetsName);
	}
	int count=0;
	public void showToast3D(String textAsString)
	{
		System.out.print(textAsString);
		count++;
		if(count>100) {
			System.out.println();
			count=0;
		}
	}
	public void showToast3D(int textAsResourceID)
	{
		if(textAsResourceID==com.IYYX.cardboard.R.string.myAPI_LoadingObjFile)
			showToast3D(".");
	}
}