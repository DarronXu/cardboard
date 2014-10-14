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
import com.IYYX.cardboard.myAPIs.MyCardboardRenderer;
import com.IYYX.cardboard.myAPIs.Texture;
import com.google.vrtoolkit.cardboard.*;
public class CardboardRenderer4 extends MyCardboardRenderer { 
	
	private float[] mCameraMatrix = new float[16];			//The position and orientation of Camera
	private float[] mHeadViewMatrix = new float[16];		//Given by Cardboard API, currently not used.
	
	GLTextureProgram mTextureProgram;
	Model earthModel;
	GameObject earthA;
	GameObject earthB;
	Texture earthTexture;
	
	public CardboardRenderer4(Resources res,CardboardOverlayView overlay, Activity dad) {
		super(res, overlay, dad);
	}
	
	public void onDrawEye(EyeTransform arg0) {
		float[] viewMatrix = new float[16];
		
		mTextureProgram.updateAllGameObjects(); 			/* The original "update()" */
		
        Matrix.multiplyMM(viewMatrix, 0, arg0.getEyeView(), 0, mCameraMatrix, 0);
        mTextureProgram.resetViewMatrix(viewMatrix);
        mTextureProgram.resetProjectionMatrix(arg0.getPerspective());
        
        //-------------- The following part is originally named "render()" ----------------
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT|GLES20.GL_COLOR_BUFFER_BIT);
		mTextureProgram.renderAllGameObjects();
	}

	public void onNewFrame(HeadTransform arg0) {
		arg0.getHeadView(mHeadViewMatrix, 0);				//Currently unused.
		mTextureProgram.loadIntoGLES();
	}
	
	public void onSurfaceCreated(EGLConfig arg0) {
		GLES20.glClearColor(0.3f, 0.3f, 0.3f, 0.5f);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glDepthFunc(GLES20.GL_LESS);					//IMPORTANT!!
		
		//---------------Set up View Matrix-----------------
		final float[] eye = {0.0f,5.0f,0.0f};
		final float[] look = {0.0f,-5.0f,0.0f};
		final float[] up = {0.0f,0.0f,5.0f};
		Matrix.setLookAtM(mCameraMatrix, 0,
				eye[0], eye[1], eye[2],
				look[0], look[1], look[2],
				up[0], up[1], up[2]);
		
		//------------------------ Load in Models and Textures --------------------------

		try {
			earthModel = Model.readObjAsWholeModel("earth.obj", 4, this.getMyCallback());
			earthTexture = new Texture(res, R.drawable.earth_texture, false); 
		} catch (IOException e) {e.printStackTrace();}
		
		earthA = new GameObject(earthModel, new GameObjectUpdater(){
			float angleInDegrees;
			public void update(GameObject obj) {
				long time=SystemClock.uptimeMillis()%10000L;
				angleInDegrees = (360.0f / 10000.0f) * ((int) time);
				Matrix.setIdentityM(obj.mModelMatrix, 0);
				Matrix.rotateM(obj.mModelMatrix, 0, angleInDegrees, 0.0f, 0.0f, 1.0f);
			}
		}, earthTexture);
		

		earthB = new GameObject(earthModel, new GameObjectUpdater(){
			float angleInDegrees;
			public void update(GameObject obj) {
				long time=SystemClock.uptimeMillis()%10000L;
				angleInDegrees = (360.0f / 10000.0f) * ((int) time);
				Matrix.setIdentityM(obj.mModelMatrix, 0);
				Matrix.translateM(obj.mModelMatrix, 0, (3*angleInDegrees-180.f)/180.0f, 3.0f, 2.0f);
				Matrix.rotateM(obj.mModelMatrix, 0, angleInDegrees, 0.0f, 0.0f, 1.0f);
			}
		}, earthTexture);
		
		mTextureProgram = new GLTextureProgram(res);
		mTextureProgram.addGameObject(earthA);
		mTextureProgram.addGameObject(earthB);
	}

	@Override
	public void onRendererShutdown() {}
	public void onFinishFrame(Viewport arg0) {}
	public void onSurfaceChanged(int width, int height) {}
}