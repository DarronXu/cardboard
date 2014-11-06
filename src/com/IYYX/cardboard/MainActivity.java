package com.IYYX.cardboard;


import java.util.ArrayList;

import com.IYYX.cardboard.myAPIs.GameObject;
import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;

import android.graphics.PixelFormat;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends CardboardActivity {

	CardboardView cardboardView=null;
	CardboardOverlayView mOverlayView=null;
	CardboardRenderer renderer=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        setContentView(R.layout.common_ui);
        cardboardView = (CardboardView) findViewById(R.id.cardboard_view);
        mOverlayView = (CardboardOverlayView) findViewById(R.id.overlay);
        cardboardView.setEGLContextClientVersion(2);
		cardboardView.setEGLConfigChooser(8, 8, 8, 8, 0, 0);
        cardboardView.getHolder().setFormat(PixelFormat.RGBA_8888);
        cardboardView.setRenderer(renderer=new CardboardRenderer(getResources(),cardboardView,mOverlayView,this));
        setCardboardView(cardboardView);
        mOverlayView.show3DToast("Please hold you phone so that it's vertical to the ground.\nThen, please turn your head around to search for the object.");
	}
	
	/*
	 * I misunderstood that the cardboardView can automatically pause and resume OpenGL,
	 * but actually we still have to do this manually.
	 * 
	 * After adding this code, the problem that our program freezes when it's resumed is solved.
	 * */
	protected void onPause() {
		super.onPause();
		if(cardboardView!=null) cardboardView.onPause();
	}
	
	protected void onResume() {
		super.onResume();
		if(cardboardView!=null) cardboardView.onResume();
	}
	
    @Override
    public void onCardboardTrigger() {
        Log.i("MainActivity", "onCardboardTrigger");
        if(renderer!=null) if(renderer.mTextureProgram!=null) if(renderer.headInfo!=null){

			Log.e("EYE-Direction", renderer.currentEyeDirection[0]+","+renderer.currentEyeDirection[1]+","+renderer.currentEyeDirection[2]);
        	/*
        	 * METHOD 1
        	 * 
        	 * renderer.initEye[0]+=renderer.currentEyeDirection[0]*0.3f;
        	renderer.initEye[1]+=renderer.currentEyeDirection[1]*0.3f;
        	renderer.initEye[2]+=renderer.currentEyeDirection[2]*0.3f;
        	renderer.initLook[0]+=renderer.currentEyeDirection[0]*0.3f;
        	renderer.initLook[1]+=renderer.currentEyeDirection[1]*0.3f;
        	renderer.initLook[2]+=renderer.currentEyeDirection[2]*0.3f;

    		Matrix.setLookAtM(renderer.mCameraMatrix, 0,
    				renderer.initEye[0], renderer.initEye[1], renderer.initEye[2],
    				renderer.initLook[0], renderer.initLook[1], renderer.initLook[2],
    				renderer.initUp[0], renderer.initUp[1], renderer.initUp[2]);*/
			/*
			 * METHOD 2
			 * 
			 * float[] matrix=new float[16];
			float[] ans=new float[16];
			Matrix.setIdentityM(matrix, 0);
			Matrix.translateM(matrix, 0, -renderer.currentEyeDirection[0], -renderer.currentEyeDirection[1], -renderer.currentEyeDirection[2]);
			Matrix.multiplyMM(ans, 0, renderer.mCameraMatrix, 0, matrix, 0);
			renderer.mCameraMatrix=ans;*/
    		
			
			//COMMON COMMAND: renderer.resetInitHeadRotate=true;
        }
        
    }
}
