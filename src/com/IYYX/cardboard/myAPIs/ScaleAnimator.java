package com.IYYX.cardboard.myAPIs;

public abstract class ScaleAnimator {
	final GameObject mObj;
	public ScaleAnimator(GameObject obj) {mObj=obj;}
	public abstract void setAnimation(float[] zoomSpeeds);
	public abstract void startAnimation(long durationMilliseconds);
	public abstract void pauseAnimation();
	public abstract void resumeAnimation();
	public abstract void stopAndResetAnimation();
	
	public abstract void doAnimation();
}
