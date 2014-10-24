package com.IYYX.cardboard.myAPIs;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;

import android.app.Activity;
import android.content.res.Resources;

import com.IYYX.cardboard.CardboardOverlayView;
import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.EyeTransform;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;

public abstract class MyCardboardRenderer implements CardboardView.StereoRenderer {

	protected Activity dad;
	protected Resources res;
	protected CardboardOverlayView overlay;
	protected CardboardView mCardboardView;
	
	public MyCardboardRenderer(Resources res,CardboardView cardboardView,CardboardOverlayView overlay, Activity dad){
		this.res=res;
		this.overlay=overlay;
		this.dad=dad;
		this.mCardboardView=cardboardView;
	}

	@Override
	public abstract void onRendererShutdown();
	public abstract void onFinishFrame(Viewport arg0);
	public abstract void onSurfaceChanged(int width, int height);
	public abstract void onSurfaceCreated(EGLConfig arg0);
	public abstract void onDrawEye(EyeTransform arg0);
	public abstract void onNewFrame(HeadTransform arg0);

	private MyCallback mMyCallback = new MyCallback(){
		class cToast3D implements Runnable{
			public String textToShow="";
			public void run(){
				overlay.show3DToast(textToShow);
			}
		};
		cToast3D mToast3D=new cToast3D();
		public InputStream openAssetInput(String assetsName) throws IOException {return res.getAssets().open(assetsName);}
		public void showToast3D(String textAsString) {mToast3D.textToShow=textAsString;dad.runOnUiThread(mToast3D);}
		public void showToast3D(int textAsResourceID) {mToast3D.textToShow=res.getString(textAsResourceID);dad.runOnUiThread(mToast3D);}
	};
	
	protected MyCallback getMyCallback(){
		return mMyCallback;
	}
}
