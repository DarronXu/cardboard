package com.IYYX.cardboard.myAPIs;

import android.opengl.Matrix;

public abstract class RotateAnimator {
	final GameObject mObj;
	float[] mAxis;
	float mAngularSpeed;
	long mDurationMilliseconds;
	boolean pause=true;
	long lastTime;
	public RotateAnimator(GameObject obj) {mObj=obj;}
	public void setAnimation(float[] axis,float angularSpeed){
		mAxis=axis;
		mAngularSpeed=angularSpeed;
	}
	
	public  void startAnimation(long durationMilliseconds){
		lastTime=System.currentTimeMillis();
		mDurationMilliseconds=durationMilliseconds;
	}
	public void pauseAnimation(){
		pause=true;
	}
	public void resumeAnimation(){
		pause=false;
	}
	public void stopAndResetAnimation(){
		pause=true;
		mAxis=null;
		mAngularSpeed=0f;
		mDurationMilliseconds=0;
	}
	
	public void doAnimation(){
		//Matrix.rotateM(mObj.mModelMatrix, 0, a, x, y, z);
		//Matrix.rotateM(answer, 0, readonlyMatrix, 0, a, x, y, z);
		if (pause) return;
		if (System.currentTimeMillis()-lastTime>mDurationMilliseconds)
			stopAndResetAnimation();
		else
			Matrix.rotateM(mObj.mModelMatrix, 0, mAngularSpeed*((System.currentTimeMillis()-lastTime)/1000L), mAxis[0],mAxis[1],mAxis[2]);
		
		
	}
}
