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
public class CardboardRenderer extends MyCardboardRenderer { 
	
	private float[] mViewMatrix = new float[16];
	private float[] mCameraMatrix = new float[16];			//The position and orientation of Camera
	
	GLTextureProgram mTextureProgram;
	Model[] boyModel,policeModel,chofsecretModel;
	PartitionedGameObject boyA,chofsecretA;
	
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
	
	HeadTransform headInfo;
	
	final float[] initEye = {0.0f,0.0f,0.0f};
	final float[] initLook = {0f,0f,0.5f};
	final float[] initUp = {0.0f,1.0f,0f};
	
	float[] initHeadRotate = null;
	float[] currentEyeDirection = new float[3];
	
	public void onNewFrame(HeadTransform arg0) {
		this.headInfo=arg0;
		if(initHeadRotate==null) {
			initHeadRotate=new float[4];
			headInfo.getQuaternion(initHeadRotate, 0);
		}
		else {
			float[] currentHeadRotate=new float[4];
			headInfo.getQuaternion(currentHeadRotate, 0);
		}
		mTextureProgram.loadIntoGLES();
	}

	public void onSurfaceCreated(EGLConfig arg0) {
		initHeadRotate=null;
		PartitionedGameObject.resetOpenedTextures();		//VERY IMPORTANT. When Activity is Paused, old OpenGL Handls expired and the old Texture 'pointers' can't be used anymore.
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.7f);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glDepthFunc(GLES20.GL_LESS);
		GLES20.glEnable(GLES20.GL_BLEND); 
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);		//IMPORTANT for Alpha !! 
		
		//---------------Set up View Matrix-----------------
		Matrix.setLookAtM(mCameraMatrix, 0,
				initEye[0], initEye[1], initEye[2],
				initLook[0], initLook[1], initLook[2],
				initUp[0], initUp[1], initUp[2]);
		
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
	}

	@Override
	public void onRendererShutdown() {}
	public void onFinishFrame(Viewport arg0) {}
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