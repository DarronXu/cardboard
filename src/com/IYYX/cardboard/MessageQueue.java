package com.IYYX.cardboard;

import java.util.ArrayList;
import java.util.Queue;

import com.google.vrtoolkit.cardboard.HeadTransform;

import android.opengl.Matrix;
import android.util.Log;

public class MessageQueue implements Runnable {
	public static MessageQueue instance=new MessageQueue();
	private MessageQueue() {}
	private boolean hasM = false;
	
	private static boolean mIsNewFramePaused = true;
	private static boolean mIsMainPaused = true;
	private static boolean startedUp=false;
	public static boolean isStartedUp() {return startedUp;}
	public static Thread startupInit(float[] startUpEye, float[] startUpLook, float[] startupCameraMatrix) {
		share.mCameraMatrix=startupCameraMatrix;
		share.initEye=startUpEye;
		share.initLook=startUpLook;
        Thread messageQueue=new Thread(MessageQueue.instance);
        messageQueue.start();
        logArray(startUpEye,"startUpEye");
        logArray(startUpLook,"startUpLook");
        startedUp=true;
        mIsNewFramePaused=false;
        mIsMainPaused=false;
        return messageQueue;
	}
	public static void logArray(float[] arr, String msg) {
		String str="";
		for(int i=0;i<arr.length;i++)
		{
			str+=arr[i];
			str+=',';
		}
		Log.e(msg,str);
	}
	public static void onRestart() {
		mIsNewFramePaused=true;
		mIsMainPaused=true;
		startedUp=false;
	}

	static void normalizeV(float[] vector) {
		double length=Math.sqrt(
				Math.pow(vector[0], 2.0)+
				Math.pow(vector[1], 2.0)+
				Math.pow(vector[2], 2.0));
		if(length==0) {
			vector[0]=vector[1]=vector[2]=0;
			return;
		}
		vector[0]=(float)((double)vector[0]/length);
		vector[1]=(float)((double)vector[1]/length);
		vector[2]=(float)((double)vector[2]/length);
	}
	/**
	 * get rotation parameters in AxisAngle from Quaternion
	 * @param quaternion, float[4] { qx, qy, qz, qw}
	 * @return float[4] { angle, x, y ,z}
	 */
	static float[] getAxisAngleFromQuaternion(float[] quaternion) {
		//Log.e("QUATERNION", "Qw="+quaternion[3]);
		//logArray(quaternion,"QUATERNION");
		if(quaternion[3]==0) return new float[]{0,0,0,0};
		float[] ans=new float[4];
		double s=Math.sqrt(1.0-quaternion[3]*quaternion[3]);
		ans[0]=(float)(2.0*(Math.acos(quaternion[3])/Math.PI*180.0));
		if(s<0.001) {
			ans[1]=quaternion[0];
			ans[2]=quaternion[1];
			ans[3]=quaternion[2];
		} else {
			ans[1]=(float)(quaternion[0]/s);
			ans[2]=(float)(quaternion[1]/s);
			ans[3]=(float)(quaternion[2]/s);
		}
		return ans;
	}
	
	public static boolean isNewFramePaused(){
		return mIsNewFramePaused;
	}
	public static boolean isMainPaused(){
		return mIsMainPaused;
	}
	@Override
	public void run() {
		while(true){
			try {
				if(hasM){
					mIsNewFramePaused = true;
					mIsMainPaused = true;
					Log.e("hasM!!!","hasM!!!");
					MainActivityPackage LastM=null;
					int indexOfLastM=0;
					for(int i=list.size()-1;i>=0;i--) if(list.get(i).getType()==1) {
						LastM=(MainActivityPackage)list.get(i);
						indexOfLastM=i;
						break;
					}
					CardboardRendererPackage crHead=null,crEnd=null;
					for(int i=0;i<list.size();i++) if(list.get(i).getType()==0) {
						crHead=(CardboardRendererPackage)list.get(i);
						break;
					}
					for(int i=indexOfLastM-1;i>=0;i--) if(list.get(i).getType()==0) {
						crEnd=(CardboardRendererPackage)list.get(i);
						break;
					}
					//          ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
					
					float[] initRot=getAxisAngleFromQuaternion(crHead.headQuaternion);
					float[] currRot=getAxisAngleFromQuaternion(crEnd.headQuaternion);
					logArray(initRot,"INIT-rot");
					logArray(crHead.headForwardVector,"INIT=forward");
					logArray(currRot,"CURR-rot");
					logArray(crEnd.headForwardVector,"CURR=forward");
					
					float[] oldEyeDirection=new float[4];
					float[] newEyeDirection=new float[4];
					oldEyeDirection[0]=share.initLook[0]-share.initEye[0];
					oldEyeDirection[1]=share.initLook[1]-share.initEye[1];
					oldEyeDirection[2]=share.initLook[2]-share.initEye[2];
					oldEyeDirection[3]=0;

					Log.e("OLD=EYE-Direction", oldEyeDirection[0]+","+oldEyeDirection[1]+","+oldEyeDirection[2]);
					
					float[] matrix=new float[16];
					Matrix.setIdentityM(matrix, 0);
					//if(initRot[0]!=0) Matrix.rotateM(matrix, 0, -initRot[0], initRot[1], initRot[2], -initRot[3]);
					if(currRot[0]!=0) Matrix.rotateM(matrix, 0,  currRot[0], currRot[1], currRot[2], -currRot[3]);
					Matrix.multiplyMV(newEyeDirection, 0, matrix, 0, oldEyeDirection, 0);
					normalizeV(newEyeDirection);
					logArray(newEyeDirection,"newEyeDirection");
					float[] tmp=new float[16];
					Log.e("CURRENT ROT MATRIX INVERSE-ABLE", "BOOLEAN:"+Matrix.invertM(tmp, 0, crEnd.rotateMatrix, 0));
					//Log.e("EYE-Direction", newEyeDirection[0]+","+newEyeDirection[1]+","+newEyeDirection[2]);
					/*
					share.initEye[0]+=newEyeDirection[0]*0.3f;
					share.initEye[1]+=newEyeDirection[1]*0.3f;
					share.initEye[2]+=newEyeDirection[2]*0.3f;
					share.initLook[0]=share.initEye[0]+newEyeDirection[0]*0.3f;
					share.initLook[1]=share.initEye[1]+newEyeDirection[1]*0.3f;
					share.initLook[2]=share.initEye[2]+newEyeDirection[2]*0.3f;
					
					Matrix.setLookAtM(share.mCameraMatrix, 0,
							share.initEye[0], share.initEye[1], share.initEye[2],
							share.initLook[0], share.initLook[1], share.initLook[2],
							0, 1f, 0);
					 */
		    		//          VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
		    		CardboardRendererPackage nextCrHead=null;
		    		for(int i=indexOfLastM+1;i<list.size();i++) if(list.get(i).getType()==0) {
		    			nextCrHead=(CardboardRendererPackage)list.get(i);
		    			break;
		    		}
		    		list.clear();
		    		if(nextCrHead!=null) list.add(nextCrHead);
					hasM = false;
					mIsNewFramePaused = false;
					mIsMainPaused = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	ArrayList<Package> list=new ArrayList<Package>();
	
	public static SharedVar share=new SharedVar();
	
	public void addPackage(MainActivityPackage pkg){
		if(MessageQueue.mIsMainPaused) return;
		list.add(pkg);
		if(MessageQueue.mIsMainPaused) {list.remove(pkg);}
		else hasM = true;
		Log.e("addPackage", "ADDED M "+hasM);
	}
	public static CardboardRendererPackage latestCR=null;
	public void addPackage(CardboardRendererPackage pkg){
		if(MessageQueue.mIsNewFramePaused) return;
		list.add(latestCR=pkg);
		if(MessageQueue.mIsNewFramePaused) {list.remove(pkg);}
	}
	public static abstract class Package{
		public abstract int getType();
	}
	public static class MainActivityPackage extends Package{
		public int getType() {return 1;}
	}
	public static MainActivityPackage MPackage=new MainActivityPackage();
	public static class CardboardRendererPackage extends Package{
		float[] headQuaternion;
		float[] headForwardVector;
		float[] rotateMatrix;
		public int getType() {return 0;}
	}
	public static class SharedVar{
		HeadTransform headInfo;
		//float[] currentHeadRotate = new float[4];
		//float[] currentEyeDirection = new float[4];
		float[] initEye = new float[3];
		float[] initLook = new float[3];
		float[] mCameraMatrix = new float[16];
	}
	
	
}
