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
		sCameraMatrix=startupCameraMatrix;
		sEye=startUpEye;
		sLook=startUpLook;
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
					int indexOfLastM=0;
					for(int i=list.size()-1;i>=0;i--) if(list.get(i).mType==1) {
						indexOfLastM=i;
						break;
					}
					CardboardRendererMessagePackage lastCR=null;
					for(int i=indexOfLastM-1;i>=0;i--) if(list.get(i).mType==0) {
						lastCR=(CardboardRendererMessagePackage)list.get(i);
						break;
					}
					//          ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
					
					float[] oldEyeDirection=new float[4];
					float[] newEyeDirection=new float[4];
					oldEyeDirection[0]=sLook[0]-sEye[0];
					oldEyeDirection[1]=sLook[1]-sEye[1];
					oldEyeDirection[2]=sLook[2]-sEye[2];
					oldEyeDirection[3]=0;
					
					Matrix.multiplyMV(newEyeDirection, 0, lastCR.mRotateMatrix, 0, oldEyeDirection, 0);

					normalizeV(oldEyeDirection);
					normalizeV(newEyeDirection);
					
					logArray(newEyeDirection,"newEyeDirection");
					
					sEye[0]+=newEyeDirection[0]*0.3f;
					//sEye[1]+=newEyeDirection[1]*0.3f;					//This line must be commented.						
					sEye[2]-=newEyeDirection[2]*0.3f;

					sLook[0]=sEye[0]+oldEyeDirection[0]*0.3f;
					sLook[1]=sEye[1]+oldEyeDirection[1]*0.3f;		//This line must NOT be commented.
					sLook[2]=sEye[2]+oldEyeDirection[2]*0.3f;
					
					Matrix.setLookAtM(sCameraMatrix, 0,
							sEye[0], sEye[1], sEye[2],
							sLook[0], sLook[1], sLook[2],
							0, 1f, 0);
					 
		    		//          VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
		    		list.clear();
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
	
	public void addPackage(MainActivityMessagePackage pkg){
		if(mIsMainPaused) return;
		list.add(pkg);
		if(mIsMainPaused) {list.remove(pkg);}
		else hasM = true;
	}
	
	public void addPackage(CardboardRendererMessagePackage pkg){
		if(mIsNewFramePaused) return;
		list.add(pkg);
		if(mIsNewFramePaused) {list.remove(pkg);}
	}
	public static MainActivityMessagePackage MPackage=new MainActivityMessagePackage();
	
	//		The following variables are shared by MainActivity and CardboardRenderer.
	static float[] sEye = new float[3];
	static float[] sLook = new float[3];
	static float[] sCameraMatrix = new float[16];
}

abstract class Package{
	int mType;
}

class MainActivityMessagePackage extends Package{
	protected MainActivityMessagePackage() {mType=1;}
}

class CardboardRendererMessagePackage extends Package{
	float[] mRotateMatrix;
	public CardboardRendererMessagePackage() {mType=0;}
	public CardboardRendererMessagePackage(float[] rotateMatrix) {mType=0;mRotateMatrix=rotateMatrix;}
}