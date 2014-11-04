package com.IYYX.cardboard.myAPIs;

import android.opengl.Matrix;


/**
 * TranslateAnimator<br/>
 * <br/>
 * Do not forget: The arguments needed when translating the object on a circle-shaped trait may not accord with common instinct.<br/>
 * <b>Circle class only accepts Axis as vectors, and the axis of circles must cross the origin point (0,0,0).</b>
 * 
 * @author Mark
 *
 */
public abstract class TranslateAnimator {
	final GameObject mObj;
	
	private long mDurationMS;
	private long mStartTime;
	
	private boolean mPaused=true;
	private boolean mIsValid=false;
	
	private Line mLine=null;
	private Circle mCircle=null;
	
	public TranslateAnimator(GameObject obj) {mObj=obj;}
	public void setAnimation(Line line) {
		mIsValid=true;
		mLine=line;
		mCircle=null;
	}
	public void setAnimation(Circle circle) {
		mIsValid=true;
		mLine=null;
		mCircle=circle;
	}
	public void startAnimation(long durationMilliseconds){
		if(mIsValid) {
			mStartTime=System.currentTimeMillis();
			mDurationMS=durationMilliseconds;
			mPaused=false;
		} else System.err.println("[ScaleAnimator] User attempted to call startAnimation() before setAnimation().");
	}
	/**
	 * Start animation by specifying velocities.<br/>
	 * @param velocityUnitPerSecond
	 * @param isAngularVelocity
	 * <br/>This argument is important. If set to true, then the speed is angular speed. Or it will be linear velocity.<br/>
	 * And the unit of angular velocity is degree/s
	 */
	public void startAnimation(float velocityUnitPerSecond, boolean isAngularVelocity) {
		if(mIsValid) {
			double durationSeconds=0;
			if(mLine!=null) {
				if(isAngularVelocity) {
					System.err.println("[ScaleAnimator] Invalid argument! You cannot specify angular velocity for a Line-shaped Translate Animation!");
					return;
				}
				durationSeconds=mLine.mLength/velocityUnitPerSecond;
			}
			else if(mCircle!=null) {
				if(isAngularVelocity)
					durationSeconds=mCircle.mAngleDelta/velocityUnitPerSecond;
				else durationSeconds=mCircle.mArcLength/velocityUnitPerSecond;
			}
			mStartTime=System.currentTimeMillis();
			mDurationMS=(long)(durationSeconds*1000.0);
			mPaused=false;
		} else System.err.println("[ScaleAnimator] User attempted to call startAnimation() before setAnimation().");
	}
	public void pauseAnimation(){
		mPaused=true;
	}
	public void resumeAnimation(){
		if(mIsValid)
			mPaused=false;
		else System.err.println("[ScaleAnimator] User attempted to call resumeAnimation() before setAnimation().");
	}
	public void stopAndResetAnimation(){
		mIsValid=false;
		mPaused=true;
		mLine=null;
		mCircle=null;
	}
	
	public void doAnimation() {
		if(mPaused||!mIsValid||(mLine==null&&mCircle==null)) return;
		if(System.currentTimeMillis()-mStartTime>mDurationMS) {
			stopAndResetAnimation();
			return;
		}
		if(mLine!=null) {
			double percent=(System.currentTimeMillis()-mStartTime)/(double)mDurationMS;
			double[] deltas=new double[3];
			deltas[0]=mLine.mDeltas[0]*percent;
			deltas[1]=mLine.mDeltas[1]*percent;
			deltas[2]=mLine.mDeltas[2]*percent;
			Matrix.translateM(mObj.mModelMatrix, 0, (float)deltas[0], (float)deltas[1], (float)deltas[2]);
		}
		else if(mCircle!=null) {
			double percent=(System.currentTimeMillis()-mStartTime)/(double)mDurationMS;
			double delta=percent*mCircle.mAngleDelta;
			float[] matrix=new float[16];
			Matrix.setIdentityM(matrix, 0);
			Matrix.translateM(matrix, 0, (float)mCircle.mStartPoint[0], (float)mCircle.mStartPoint[1], (float)mCircle.mStartPoint[2]);
			Matrix.rotateM(matrix, 0, (float)delta, (float)mCircle.mAxis[0], (float)mCircle.mAxis[1], (float)mCircle.mAxis[2]);
			Matrix.multiplyMM(mObj.mModelMatrix, 0, matrix, 0, mObj.mModelMatrix, 0);
		}
	}

	static double[] copyFloat(float[] x) {
		double[] ans=new double[x.length];
		for(int i=0;i<x.length;i++) ans[i]=x[i];
		return ans;
	}
	
	/**
	 * 1.Axis of Circles <b>must</b> cross right through (0,0,0)<br/>
	 * 2.This class only asks for the vector of axis.
	 * @author Mark
	 *
	 */
	
	public static class Circle {
		final double[] mAxis;
		final double[] mStartPoint;
		final double mRadius;
		/**
		 * mAngleDelta is in Degrees, not Radian
		 */
		final double mAngleDelta;
		/**
		 * mRAngleDelta is in Radian, not Degrees
		 */
		final double mRAngleDelta;
		final double mArcLength;
		
		public Circle(float[] axis,float[] startPoint, float angleDelta) {
			mAxis=copyFloat(axis);
			mStartPoint=copyFloat(startPoint);
			mRadius=Math.sqrt(
					Math.pow(startPoint[0], 2.0)+
					Math.pow(startPoint[1], 2.0)+
					Math.pow(startPoint[2], 2.0));
			mAngleDelta=angleDelta;
			mRAngleDelta=mAngleDelta*Math.PI/180.0;
			mArcLength=Math.abs(mRAngleDelta)*mRadius;
		}
	}
	
	public static class Line {

		final double[] mPointFrom,mPointTo;
		final double[] mDeltas;
		final double mLength;
		
		public Line(float[] pointFrom,float[] pointTo) {
			mPointFrom=copyFloat(pointFrom);
			mPointTo=copyFloat(pointTo);
			mDeltas=new double[3];
			mDeltas[0]=mPointTo[0]-mPointFrom[0];
			mDeltas[1]=mPointTo[1]-mPointFrom[1];
			mDeltas[2]=mPointTo[2]-mPointFrom[2];
			mLength=Math.sqrt(
					Math.pow(mDeltas[0], 2)+
					Math.pow(mDeltas[1], 2)+
					Math.pow(mDeltas[2], 2));
		}
		
		public Line(float[] pointFrom,float[] directionVector,float length) {
			mPointFrom=copyFloat(pointFrom);
			mPointTo=new double[3];
			double abs=Math.pow(directionVector[0], 2)+
					Math.pow(directionVector[1], 2)+
					Math.pow(directionVector[2], 2);
			abs=Math.sqrt(abs);
			double[] normalVector=new double[3];
			normalVector[0]=(double)directionVector[0]/abs;
			normalVector[1]=(double)directionVector[1]/abs;
			normalVector[2]=(double)directionVector[2]/abs;
			mPointTo[0]=(double)mPointFrom[0]+((double)length)*normalVector[0];
			mPointTo[1]=(double)mPointFrom[1]+((double)length)*normalVector[1];
			mPointTo[2]=(double)mPointFrom[2]+((double)length)*normalVector[2];
			mDeltas=new double[3];
			mDeltas[0]=mPointTo[0]-mPointFrom[0];
			mDeltas[1]=mPointTo[1]-mPointFrom[1];
			mDeltas[2]=mPointTo[2]-mPointFrom[2];
			mLength=length;
		}
	}
}