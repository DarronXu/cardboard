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
	
	public CardboardRenderer(Resources res,CardboardView cardboardView,CardboardOverlayView overlay, Activity dad) {
		super(res,cardboardView, overlay, dad);
	}
	
	public void onDrawEye(EyeTransform arg0) {
        Matrix.multiplyMM(mViewMatrix, 0, arg0.getEyeView(), 0, MessageQueue.share.mCameraMatrix, 0);
		mTextureProgram.updateAllGameObjects();
        mTextureProgram.resetViewMatrix(mViewMatrix);
        mTextureProgram.resetProjectionMatrix(arg0.getPerspective());
        
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT|GLES20.GL_COLOR_BUFFER_BIT);
		mTextureProgram.renderAllGameObjects();
	}
	
	public void onNewFrame(HeadTransform arg0) {
		if(!MessageQueue.isStartedUp()) {
			MessageQueue.startupInit(startupEye.clone(),startupLook.clone(),startupCameraMatrix.clone());
			MessageQueue.CardboardRendererPackage pkg=new MessageQueue.CardboardRendererPackage();
			pkg.headQuaternion=new float[4];
			pkg.headForwardVector=new float[3];
			pkg.rotateMatrix=new float[16];
			arg0.getQuaternion(pkg.headQuaternion, 0);
			arg0.getForwardVector(pkg.headForwardVector, 0);
			arg0.getHeadView(pkg.rotateMatrix, 0);
			MessageQueue.instance.addPackage(pkg);
		}
		if (!MessageQueue.isNewFramePaused()){
			MessageQueue.CardboardRendererPackage pkg=new MessageQueue.CardboardRendererPackage();
			pkg.headQuaternion=new float[4];
			pkg.headForwardVector=new float[3];
			pkg.rotateMatrix=new float[16];
			arg0.getQuaternion(pkg.headQuaternion, 0);
			arg0.getForwardVector(pkg.headForwardVector, 0);
			arg0.getHeadView(pkg.rotateMatrix, 0);
			MessageQueue.instance.addPackage(pkg);
		}
		mTextureProgram.loadIntoGLES();
	}
	
	final float[] startupEye=new float[]{0,0,0};
	final float[] startupLook=new float[]{0.5f,0,0};
	final float[] startupCameraMatrix = new float[16];			//The position and orientation of Camera

	public void onSurfaceCreated(EGLConfig arg0) {
		MessageQueue.onRestart();
		PartitionedGameObject.resetOpenedTextures();		//VERY IMPORTANT. When Activity is Paused, old OpenGL Handls expired and the old Texture 'pointers' can't be used anymore.
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.7f);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glDepthFunc(GLES20.GL_LESS);
		GLES20.glEnable(GLES20.GL_BLEND); 
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);		//IMPORTANT for Alpha !! 
		
		//---------------Set up View Matrix-----------------

		Matrix.setLookAtM(startupCameraMatrix, 0,
				startupEye[0], startupEye[1], startupEye[2],
				startupLook[0], startupLook[1], startupLook[2],
				0, 1f, 0);
		
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