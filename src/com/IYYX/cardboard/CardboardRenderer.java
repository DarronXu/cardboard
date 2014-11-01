package com.IYYX.cardboard;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;

import android.app.Activity;
import android.content.res.Resources;
import android.opengl.*;
import android.os.SystemClock;

import com.IYYX.cardboard.myAPIs.GLTextureProgram;
import com.IYYX.cardboard.myAPIs.GameObject;
import com.IYYX.cardboard.myAPIs.GameObjectUpdater;
import com.IYYX.cardboard.myAPIs.Model;
import com.IYYX.cardboard.myAPIs.ModelIO;
import com.IYYX.cardboard.myAPIs.MyCardboardRenderer;
import com.IYYX.cardboard.PartitionedGameObject;
import com.google.vrtoolkit.cardboard.*;
public class CardboardRenderer extends MyCardboardRenderer { 
	
	private float[] mCameraMatrix = new float[16];			//The position and orientation of Camera
	private float[] mHeadViewMatrix = new float[16];		//Given by Cardboard API, currently not used.
	
	GLTextureProgram mTextureProgram;
	Model[] boyModel,policeModel,chofsecretModel;
	PartitionedGameObject boyA,chofsecretA;
	
	public CardboardRenderer(Resources res,CardboardView cardboardView,CardboardOverlayView overlay, Activity dad) {
		super(res,cardboardView, overlay, dad);
	}
	
	public void onDrawEye(EyeTransform arg0) {
		float[] viewMatrix = new float[16];
        Matrix.multiplyMM(viewMatrix, 0, arg0.getEyeView(), 0, mCameraMatrix, 0);
		mTextureProgram.updateAllGameObjects();
        mTextureProgram.resetViewMatrix(viewMatrix);
        mTextureProgram.resetProjectionMatrix(arg0.getPerspective());
        
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT|GLES20.GL_COLOR_BUFFER_BIT);
		mTextureProgram.renderAllGameObjects();
	}

	public void onNewFrame(HeadTransform arg0) {
		arg0.getHeadView(mHeadViewMatrix, 0);				//Currently unused.
		mTextureProgram.loadIntoGLES();
	}

	public void onSurfaceCreated(EGLConfig arg0) {
		PartitionedGameObject.resetOpenedTextures();		//VERY IMPORTANT. When Activity is Paused, old OpenGL Handls expired and the old Texture 'pointers' can't be used anymore.
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.7f);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glDepthFunc(GLES20.GL_LESS);
		GLES20.glEnable(GLES20.GL_BLEND); 
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);		//IMPORTANT for Alpha !! 
		
		//---------------Set up View Matrix-----------------
		final float[] eye = {0.0f,0.0f,0.0f};
		final float[] look = {0f,0f,0.5f};
		final float[] up = {0.0f,1.0f,0f};
		Matrix.setLookAtM(mCameraMatrix, 0,
				eye[0], eye[1], eye[2],
				look[0], look[1], look[2],
				up[0], up[1], up[2]);
		
		mTextureProgram = new GLTextureProgram(res);
		
		//------------------------ Load in Models and Textures --------------------------

		try {
			//These new IO functions can read Models much faster.
			boyModel = ModelIO.loadPartitioned(assets.open("boy.objpp"), getMyCallback());
			chofsecretModel = ModelIO.loadPartitioned(assets.open("chofsecret.objpp"), getMyCallback());
		} catch (IOException | ClassNotFoundException e) {e.printStackTrace();}
		
		boyA = new PartitionedGameObject(boyModel, "boy.obj-info", new GameObjectUpdater(){
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
			}
		}, getMyCallback());
		
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
