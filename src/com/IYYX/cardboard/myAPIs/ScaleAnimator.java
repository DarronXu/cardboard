package com.IYYX.cardboard.myAPIs;
import android.opengl.*;

public class ScaleAnimator {
	final GameObject mObj;
	float[] mZoomSpeeds;
	long mDurationMilliseconds;
	int mFramePerSecond;
	boolean pause = true;
	long beginTime;
	public ScaleAnimator(GameObject obj) {mObj=obj;}
	public void setAnimation(float[] zoomSpeeds){
		mZoomSpeeds = zoomSpeeds;
	}
	public void startAnimation(long durationMilliseconds){
		beginTime = System.currentTimeMillis();
		mDurationMilliseconds = durationMilliseconds;
	}
	public void pauseAnimation(){
		pause = true;
	}
	public void resumeAnimation(){
		pause = false;
	}
	public void stopAndResetAnimation(){
		pause = true;
		mZoomSpeeds = null;
		mDurationMilliseconds = 0;
		mFramePerSecond = 0;
	}
	
	public void doAnimation(){
		//Matrix.scaleM(mObj.mModelMatrix, 0, x, y, z);
		//Matrix.scaleM(answer, 0, readonlyMatrix, 0, x, y, z);
		if (pause) return;
		if ((System.currentTimeMillis() - beginTime) < mDurationMilliseconds)
			Matrix.scaleM(mObj.mModelMatrix, 0, mZoomSpeeds[0]*((System.currentTimeMillis() - beginTime)/1000L), mZoomSpeeds[1]*((System.currentTimeMillis() - beginTime)/1000L), mZoomSpeeds[2]*((System.currentTimeMillis() - beginTime)/1000L));
		else
			stopAndResetAnimation();
		
	}
}
