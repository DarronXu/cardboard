package com.IYYX.cardboard;


import java.util.ArrayList;

import com.IYYX.cardboard.myAPIs.GameObject;
import com.example.android.sip.IncomingCallReceiver;
import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;

import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.net.sip.SipAudioCall;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

public class MainActivity extends CardboardActivity {

	CardboardView cardboardView=null;
	CardboardOverlayView mOverlayView=null;
	CardboardRenderer renderer=null;

    public String sipAddress = null;

    public SipManager manager = null;
    public SipProfile me = null;
    public SipAudioCall call = null;
    public IncomingCallReceiver callReceiver;

    private static final int CALL_ADDRESS = 1;
    private static final int SET_AUTH_INFO = 2;
    private static final int UPDATE_SETTINGS_DIALOG = 3;
    private static final int HANG_UP = 4;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


        // Set up the intent filter.  This will be used to fire an
        // IncomingCallReceiver when someone calls the SIP address used by this
        // application.
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.SipDemo.INCOMING_CALL");
        callReceiver = new IncomingCallReceiver();
        this.registerReceiver(callReceiver, filter);

        // "Push to talk" can be a serious pain when the screen keeps turning off.
        // Let's prevent that.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initializeManager();
		
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

	float[] eye=new float[]{0,0,0};
	float[] center=new float[]{0,0,0.5f};
	
    @Override
    public void onCardboardTrigger() {
        Log.i("MainActivity", "onCardboardTrigger");
        if(renderer!=null) if(renderer.mTextureProgram!=null){
        	/*float[] xyzw=new float[4];
        	renderer.headInfo.getQuaternion(xyzw, 0);
        	float[] translatedView=new float[16];
        	Matrix.translateM(translatedView, 0, renderer.mViewMatrix, 0, 0, -0.5f,0);
        	Matrix.rotateM(translatedView, 0, -xyzw[3], xyzw[0], xyzw[1], xyzw[2]);
        	renderer.mCameraMatrix=translatedView;*/
        	//Matrix.
        }
        
    }
}
