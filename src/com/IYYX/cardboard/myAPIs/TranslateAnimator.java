package com.IYYX.cardboard.myAPIs;

public abstract class TranslateAnimator {
	final GameObject mObj;
	public TranslateAnimator(GameObject obj) {mObj=obj;}
	public abstract void setAnimation();
	public abstract void startAnimation(long durationMilliseconds);
	public abstract void pauseAnimation();
	public abstract void resumeAnimation();
	public abstract void stopAndResetAnimation();
	
	public abstract void doAnimation();
}