package com.IYYX.cardboard;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;

import android.app.Activity;
import android.content.res.Resources;
import android.net.sip.SipManager;
import android.opengl.*;
import android.os.SystemClock;
import android.util.Log;

import com.IYYX.cardboard.myAPIs.GLTextureProgram;
import com.IYYX.cardboard.myAPIs.GameObject;
import com.IYYX.cardboard.myAPIs.GameObjectUpdater;
import com.IYYX.cardboard.myAPIs.Model;
import com.IYYX.cardboard.myAPIs.ModelIO;
import com.IYYX.cardboard.myAPIs.MyCardboardRenderer;
import com.IYYX.cardboard.PartitionedGameObject;
import com.google.vrtoolkit.cardboard.*;
import com.jogamp.opengl.math.Quaternion;
public class CardboardRenderer extends MyCardboardRenderer { 
	
	private float[] mViewMatrix = new float[16];
	
	GLTextureProgram mTextureProgram;
	Model[] boyModel,policeModel,chofsecretModel;
	PartitionedGameObject boyA,chofsecretA;
	String myUsername = null,contactUsername = null;
	
	public CardboardRenderer(Resources res,CardboardView cardboardView,CardboardOverlayView overlay, Activity dad) {
		super(res,cardboardView, overlay, dad);
	}
	
	public void onDrawEye(EyeTransform arg0) {
        Matrix.multiplyMM(mViewMatrix, 0, arg0.getEyeView(), 0, mCameraMatrix, 0);
		mTextureProgram.updateAllGameObjects();
        mTextureProgram.resetViewMatrix(mViewMatrix);
        mTextureProgram.resetProjectionMatrix(arg0.getPerspective());
        
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT|GLES20.GL_COLOR_BUFFER_BIT);
		mTextureProgram.renderAllGameObjects();
	}
	
	boolean keepStepping=false;
	boolean pasueMainActivity=false;
	
	static float[] crossProduct_rightHanded(float[] vecFrom,float[] vecTo) {
		float[] vec=new float[3];
		vec[0]=vecFrom[1]*vecTo[2]-vecFrom[2]*vecTo[1];
		vec[1]=vecFrom[2]*vecTo[0]-vecFrom[0]*vecTo[2];
		vec[2]=vecFrom[0]*vecTo[1]-vecTo[0]*vecFrom[1];
		return vec;
	}

	static float[] crossProduct_leftHanded(float[] vecFrom,float[] vecTo) {
		float[] vec=new float[3];
		vec[0]=-(vecFrom[1]*vecTo[2]-vecFrom[2]*vecTo[1]);
		vec[1]=-(vecFrom[2]*vecTo[0]-vecFrom[0]*vecTo[2]);
		vec[2]=-(vecFrom[0]*vecTo[1]-vecTo[0]*vecFrom[1]);
		return vec;
	}
	
	static float dotProduct(float[] vec1,float[] vec2) {
		float ans=0;
		ans+=vec1[0]*vec2[0];
		ans+=vec1[1]*vec2[1];
		ans+=vec1[2]*vec2[2];
		return ans;
	}
	
	static float lengthOfVector(float[] vec) {
		float ans=0;
		ans+=vec[0]*vec[0];
		ans+=vec[1]*vec[1];
		ans+=vec[2]*vec[2];
		return (float)Math.sqrt(ans);
	}
	
	/**
	 * 
	 * @param vecFrom
	 * @param vecTo
	 * @return [angle, x, y, z]
	 */
	static float[] getAxisAngleForRotationBetweenVectors(float[] vecFrom, float[] vecTo, boolean isRightHanded){
		float[] axis;
		float[] ans=new float[4];
		if(isRightHanded) axis=crossProduct_rightHanded(vecFrom,vecTo);
		else axis=crossProduct_leftHanded(vecFrom,vecTo);
		ans[0]=dotProduct(vecFrom,vecTo)/(lengthOfVector(vecFrom)*lengthOfVector(vecTo));
		ans[1]=axis[0];
		ans[2]=axis[1];
		ans[3]=axis[2];
		return ans;
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

	public static void logArray(float[] arr, String msg) {
		String str="";
		for(int i=0;i<arr.length;i++)
		{
			str+=arr[i];
			str+=',';
		}
		Log.e(msg,str);
	}
	
	private void stepForward(float scale){						//This function MUST be private
		pasueMainActivity=true;
		float[] oldEyeDirection=new float[4];
		float[] newEyeDirection=new float[4];
		oldEyeDirection[0]=mLook[0]-mEye[0];
		oldEyeDirection[1]=mLook[1]-mEye[1];
		oldEyeDirection[2]=mLook[2]-mEye[2];
		oldEyeDirection[3]=0;
		
		Matrix.multiplyMV(newEyeDirection, 0, currHeadRotateMatrix, 0, oldEyeDirection, 0);

		normalizeV(oldEyeDirection);
		normalizeV(newEyeDirection);
		
		mEye[0]+=newEyeDirection[0]*scale;
		//mEye[1]+=newEyeDirection[1]*scale;					//This line must be commented.						
		mEye[2]-=newEyeDirection[2]*scale;

		mLook[0]=mEye[0]+oldEyeDirection[0]*scale;
		mLook[1]=mEye[1]+oldEyeDirection[1]*scale;				//This line must NOT be commented.
		mLook[2]=mEye[2]+oldEyeDirection[2]*scale;
		
		Matrix.setLookAtM(mCameraMatrix, 0,
				mEye[0], mEye[1], mEye[2],
				mLook[0], mLook[1], mLook[2],
				0, 1f, 0);
		pasueMainActivity=false;
	}
	
	float[] currHeadRotateMatrix=new float[16];
	
	public void onNewFrame(HeadTransform arg0) {
		arg0.getHeadView(currHeadRotateMatrix, 0);
		if(keepStepping) {
			stepForward(0.1f);
		}
		mTextureProgram.loadIntoGLES();
	}
	
	float[] mEye;
	float[] mLook;
	float[] mCameraMatrix=new float[16];			//The position and orientation of Camera
	
	final float[] startupEye=new float[]{0,0,0};
	final float[] startupLook=new float[]{1f,0f,0};

	public void onSurfaceCreated(EGLConfig arg0) {
		//MessageQueue.onRestart();
		PartitionedGameObject.resetOpenedTextures();		//VERY IMPORTANT. When Activity is Paused, old OpenGL Handls expired and the old Texture 'pointers' can't be used anymore.
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.7f);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glDepthFunc(GLES20.GL_LESS);
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);		//IMPORTANT for Alpha !! 
		
		//---------------Set up View Matrix-----------------
		
		mEye=startupEye.clone();
		mLook=startupLook.clone();
		Matrix.setLookAtM(mCameraMatrix, 0,
				startupEye[0], startupEye[1], startupEye[2],
				startupLook[0], startupLook[1], startupLook[2],
				0, 1f, 0f);
		
		mTextureProgram = new GLTextureProgram(res);
		
		//------------------------ Load in Models and Textures --------------------------

		try {
			//These new IO functions can read Models much faster.
			boyModel = ModelIO.loadPartitioned(assets.open("boy.objpp"), getMyCallback());
			chofsecretModel = ModelIO.loadPartitioned(assets.open("chofsecret.objpp"), getMyCallback());
		} catch (IOException | ClassNotFoundException e) {e.printStackTrace();}
		
		boyA = new PartitionedGameObject(boyModel, "boy.obj-info", new BoyUpdater(), getMyCallback());
		
		GameObjectUpdater[] boyUpdaters=new GameObjectUpdater[10];
		for(int i=0;i<10;i++) boyUpdaters[i]=new BoyUpdater();
		PartitionedGameObject boyB = new PartitionedGameObject(boyModel,"boy.obj-info", boyUpdaters, getMyCallback());
		
		chofsecretA= new PartitionedGameObject(chofsecretModel, "chofsecret.obj-info",new GameObjectUpdater(){
			public void update(GameObject obj) {
				Matrix.setIdentityM(obj.mModelMatrix, 0);
				Matrix.scaleM(obj.mModelMatrix, 0, 2, 2, 2);
			}
		}, getMyCallback());
		
		//boyA.addToGLProgram(mTextureProgram);
		chofsecretA.addToGLProgram(mTextureProgram);
		getMyCallback().showToast3D("Hello, "+myUsername+" !");
	}

	@Override
	public void onRendererShutdown() {}
	public void onFinishFrame(Viewport arg0) {
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public void onSurfaceChanged(int width, int height) {}
}


class BoyUpdater implements GameObjectUpdater {
	public void update(GameObject obj) {
		float angleInDegreesA,angleInDegreesB,angleInDegreesC;
		long time=SystemClock.uptimeMillis()%10000L;
		angleInDegreesA = (360.0f / 10000.0f) * ((int) time);
		time=SystemClock.uptimeMillis()%5000L;
		angleInDegreesB = (360.0f / 5000.0f) * ((int) time);
		time=SystemClock.uptimeMillis()%3000L;
		angleInDegreesC = (360.0f / 3000.0f) * ((int) time);
		Matrix.setIdentityM(obj.mModelMatrix, 0);
		Matrix.translateM(obj.mModelMatrix, 0, 0.0f, 3.0f, 0.8f);
		Matrix.rotateM(obj.mModelMatrix, 0, angleInDegreesA, 1.0f, 0.0f, 0.0f);
		Matrix.rotateM(obj.mModelMatrix, 0, angleInDegreesB, 0.0f, 1.0f, 0.0f);
		Matrix.rotateM(obj.mModelMatrix, 0, angleInDegreesC, 0.0f, 0.0f, 1.0f);
		if(obj.mPrototype.name.contains("c7d648bf")){
		}
	}
}