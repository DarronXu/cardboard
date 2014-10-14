package com.IYYX.cardboard;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;

import android.app.Activity;
import android.opengl.*;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;

public class MainActivity2 extends CardboardActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        setContentView(R.layout.common_ui);
        CardboardView cardboardView = (CardboardView) findViewById(R.id.cardboard_view);
        CardboardOverlayView mOverlayView = (CardboardOverlayView) findViewById(R.id.overlay);
        cardboardView.setEGLContextClientVersion(2);
        cardboardView.setRenderer(new CardboardRenderer4(getResources(),mOverlayView,this));
        setCardboardView(cardboardView);
        mOverlayView.show3DToast("Please hold you phone so that it's vertical to the ground.\nThen, please turn your head around to search for the object.");
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
	

    @Override
    public void onCardboardTrigger() {
        Log.i("MainActivity", "onCardboardTrigger");
    }
}
