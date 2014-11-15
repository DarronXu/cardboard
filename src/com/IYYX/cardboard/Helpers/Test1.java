package com.IYYX.cardboard.Helpers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.*;

import com.IYYX.cardboard.CardboardRenderer;
import com.IYYX.cardboard.myAPIs.Model;
import com.IYYX.cardboard.myAPIs.ModelIO;
import com.IYYX.cardboard.myAPIs.MyCallback;

import android.R;

public class Test1 {
	/*public static void main(String[] args) {
		try {
			Model[] police = Model.readPartitionedModel("police.obj", new TestCallback());
			ModelIO.save(police, new FileOutputStream("./assets/police.objpp"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/

	public static void main(String[] args){
		float[] eyes=new float[]{2.3088949f,0,-1.0076755f};
		float[] look=new float[]{2.4088948f,0.0f,-1.0076755f};

		float[] eyeDirection = new float[3];
		eyeDirection[0]=look[0]-eyes[0];
		eyeDirection[1]=look[1]-eyes[1];
		eyeDirection[2]=look[2]-eyes[2];
		CardboardRenderer.normalizeV(eyeDirection);
		float[] ans=CardboardRenderer.getAxisAngleForRotationBetweenVectors(new float[]{0,-1,0},new float[]{0,0,1},true);;
		String out="";
		for(int i=0;i<4;i++){
			out+=ans[i];
			out+=',';
		}
		System.out.println(out);
	}
	
}