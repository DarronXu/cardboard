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

import com.IYYX.cardboard.myAPIs.Model;
import com.IYYX.cardboard.myAPIs.ModelIO;
import com.IYYX.cardboard.myAPIs.MyCallback;

import android.R;

public class Test1 {
	public static void main(String[] args) {
		try {
			Model[] police = Model.readPartitionedModel("police.obj", new TestCallback());
			ModelIO.save(police, new FileOutputStream("./assets/police.objpp"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}