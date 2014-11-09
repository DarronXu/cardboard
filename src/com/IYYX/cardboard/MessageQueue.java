package com.IYYX.cardboard;

import java.util.ArrayList;
import java.util.Queue;

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
	public static Thread startupInit(float[] startupHeadRotate,float[] startUpEye, float[] startUpLook, float[] startupCameraMatrix) {
		share.mCameraMatrix=startupCameraMatrix;
		share.initHeadRotate=startupHeadRotate;
		share.initEye=startUpEye;
		share.initLook=startUpLook;
        Thread messageQueue=new Thread(MessageQueue.instance);
        messageQueue.start();
        mIsNewFramePaused=false;
        mIsMainPaused=false;
        startedUp=true;
        return messageQueue;
	}
	public static void onRestart() {
		mIsNewFramePaused=true;
		mIsMainPaused=true;
		startedUp=false;
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
					//����M      ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
	
					/*LastM.initEye[0]-=crHead.currentHeadRotate[0]*0.3f;
					LastM.initEye[1]-=crHead.currentHeadRotate[1]*0.3f;
					LastM.initEye[2]-=crHead.currentHeadRotate[2]*0.3f;
					float eyeX=(LastM.initEye[0]+=crEnd.currentHeadRotate[0]*0.3f);
					float eyeY=(LastM.initEye[1]+=crEnd.currentHeadRotate[1]*0.3f);
					float eyeZ=(LastM.initEye[2]+=crEnd.currentHeadRotate[2]*0.3f);
					float centerX=(LastM.initLook[0]=LastM.initEye[0]+crEnd.currentHeadRotate[0]*0.3f);
					float centerY=(LastM.initLook[1]=LastM.initEye[1]+crEnd.currentHeadRotate[1]*0.3f);
					float centerZ=(LastM.initLook[2]=LastM.initEye[2]+crEnd.currentHeadRotate[2]*0.3f);
					Matrix.setLookAtM(LastM.mCameraMatrix, 0,
							eyeX, eyeY, eyeZ,
							centerX, centerY, centerZ,
							0, 1f, 0);
					LastM.initHeadRotate=crEnd.currentHeadRotate.clone();*/
		    		//          VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
					//д��2		^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
					
					share.initEye=LastM.initEye.clone();
					share.initLook=LastM.initLook.clone();
					share.mCameraMatrix=LastM.mCameraMatrix.clone();
					share.initHeadRotate=LastM.initHeadRotate.clone();
					
					//           VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
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
				} /*else {
					mIsMainPaused=true;
					mIsNewFramePaused =true;
					
					if(list.size()!=0) {
						CardboardRendererPackage cr=(CardboardRendererPackage)list.get(list.size()-1);
						share.currentEyeDirection=cr.currentEyeDirection;
						share.currentHeadRotate=cr.currentHeadRotate;
					}
					
					mIsNewFramePaused = false;
					mIsMainPaused = false;
				}*/
			} catch (Exception e) {
				e.printStackTrace();
			} try {
				Thread.sleep(100);
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
		float[] initEye;
		float[] initLook;
		float[] initHeadRotate;
		float[] mCameraMatrix;
		public MainActivityPackage(float[] iE,float[] iL, float[] iHR, float[] mCM) {
			this.initEye=iE;
			this.initLook=iL;
			this.initHeadRotate=iHR;
			this.mCameraMatrix=mCM;
		}
		public int getType() {return 1;}
	}
	public static class CardboardRendererPackage extends Package{
		float[] currentHeadRotate;
		float[] currentEyeDirection;
		public int getType() {return 0;}
	}
	public static class SharedVar{
		float[] currentHeadRotate = new float[4];
		float[] currentEyeDirection = new float[4];
		float[] initEye = new float[3];
		float[] initLook = new float[3];
		float[] initHeadRotate = new float[4];
		float[] mCameraMatrix = new float[16];
	}
	
	
}
