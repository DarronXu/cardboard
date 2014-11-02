package com.IYYX.cardboard.myAPIs;

public abstract class RotateAnimator {
	final GameObject mObj;
	public RotateAnimator(GameObject obj) {mObj=obj;}
	public abstract void setAnimation(float[] axis,float angularSpeed);
	public abstract void startAnimation(long durationMilliseconds);
	public abstract void pauseAnimation();
	public abstract void resumeAnimation();
	public abstract void stopAndResetAnimation();
	
	public abstract void doAnimation();
}
