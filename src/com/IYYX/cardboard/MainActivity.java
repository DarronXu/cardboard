package com.IYYX.cardboard;


import java.util.ArrayList;

import com.IYYX.cardboard.MessageQueue.MainActivityPackage;
import com.IYYX.cardboard.myAPIs.GameObject;
import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;

import android.graphics.PixelFormat;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

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
        cardboardView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				v.performClick();
				onCardboardTrigger();
				return false;
			}
        	
        });
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
        if(renderer!=null) if(renderer.mTextureProgram!=null) if(!MessageQueue.isMainPaused()){

			Log.e("EYE-Direction", MessageQueue.latestCR.currentEyeDirection[0]+","+MessageQueue.latestCR.currentEyeDirection[1]+","+MessageQueue.latestCR.currentEyeDirection[2]);
        	/*
        	 * METHOD 1
        	 * 
        	 */
			float eye[]=new float[3];
			float look[]=new float[3];
			float camera[] =new float[16];
			eye[0]=MessageQueue.share.initEye[0]+MessageQueue.latestCR.currentEyeDirection[0]*0.3f;
			eye[1]=MessageQueue.share.initEye[1]+MessageQueue.latestCR.currentEyeDirection[1]*0.3f;
			eye[2]=MessageQueue.share.initEye[2]+MessageQueue.latestCR.currentEyeDirection[2]*0.3f;
			look[0]=eye[0]+MessageQueue.latestCR.currentEyeDirection[0]*0.3f;
			look[1]=eye[1]+MessageQueue.latestCR.currentEyeDirection[1]*0.3f;
			look[2]=eye[2]+MessageQueue.latestCR.currentEyeDirection[2]*0.3f;
			
    		Matrix.setLookAtM(camera, 0,
    				eye[0], eye[1], eye[2],
    				look[0], look[1], look[2],
    				0, 1f, 0);
    		
			MessageQueue.instance.addPackage(new MessageQueue.MainActivityPackage(eye,look,
					MessageQueue.latestCR.currentHeadRotate.clone(),camera));
			
			/*
			 * METHOD 2
			 * 
			 *
			float[] matrix=new float[16];
			float[] ans=new float[16];
			Matrix.setIdentityM(matrix, 0);
			Matrix.translateM(matrix, 0, -renderer.currentEyeDirection[0], -renderer.currentEyeDirection[1], -renderer.currentEyeDirection[2]);
			Matrix.multiplyMM(ans, 0, renderer.mCameraMatrix, 0, matrix, 0);
			renderer.mCameraMatrix=ans;*/
    		
			
			//COMMON COMMAND: 
        }
        
    }
}
