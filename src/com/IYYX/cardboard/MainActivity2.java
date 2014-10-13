package com.IYYX.cardboard;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;

import android.app.Activity;
import android.opengl.*;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity2 extends CardboardActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        setContentView(R.layout.common_ui);
        CardboardView cardboardView = (CardboardView) findViewById(R.id.cardboard_view);
        cardboardView.setRenderer(new CardboardRenderer2(getResources()));
        setCardboardView(cardboardView);
        
		/*
		mGLView = new GLSurfaceView(this);
		mGLView.setEGLContextClientVersion(2);		//VERY IMPORTANT!!!
		mGLView.setRenderer(new Renderer2(getResources()));
		setContentView(mGLView);*/
	}
	
	protected void onResume() {
		super.onResume();
	}
	
	protected void onPause() {
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
