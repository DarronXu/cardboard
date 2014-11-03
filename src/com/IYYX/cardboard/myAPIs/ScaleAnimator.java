package com.IYYX.cardboard.myAPIs;

import android.opengl.*;

public abstract class ScaleAnimator {
	final GameObject mObj;
	public ScaleAnimator(GameObject obj) {mObj=obj;}
	public abstract void setAnimation(Line line,float speed);
	public abstract void startAnimation(long durationMilliseconds);
	public abstract void pauseAnimation();
	public abstract void resumeAnimation();
	public abstract void stopAndResetAnimation();
	
	public abstract void doAnimation();
	
	public static class Circle {
	}
	
	public static class Line {

		final float[] mPointFrom,mPointTo;
		
		public Line(float[] pointFrom,float[] pointTo) {
			mPointFrom=pointFrom.clone();
			mPointTo=pointTo.clone();
		}
		
		public Line(float[] pointFrom,float[] directionVector,float length) {
			mPointFrom=pointFrom.clone();
			mPointTo=new float[3];
			double abs=Math.pow(directionVector[0], 2)+
					Math.pow(directionVector[1], 2)+
					Math.pow(directionVector[2], 2);
			abs=Math.sqrt(abs);
			double[] normalVector=new double[3];
			normalVector[0]=(double)directionVector[0]/abs;
			normalVector[1]=(double)directionVector[1]/abs;
			normalVector[2]=(double)directionVector[2]/abs;
			mPointTo[0]=(float)((double)mPointFrom[0]+((double)length)*normalVector[0]);
			mPointTo[1]=(float)((double)mPointFrom[1]+((double)length)*normalVector[1]);
			mPointTo[2]=(float)((double)mPointFrom[2]+((double)length)*normalVector[2]);
		}
	}
}