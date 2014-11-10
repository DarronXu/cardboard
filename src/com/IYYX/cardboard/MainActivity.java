package com.IYYX.cardboard;


import java.util.ArrayList;

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
        if(renderer!=null) if(renderer.mTextureProgram!=null) if(!renderer.requestStepForward){
        	renderer.requestStepForward=true; 
        }
        
    }
}
