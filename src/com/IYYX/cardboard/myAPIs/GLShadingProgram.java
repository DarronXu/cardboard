package com.IYYX.cardboard.myAPIs;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.IYYX.cardboard.R;
import com.IYYX.cardboard.myAPIs.*;

public class GLShadingProgram extends GLProgram {
	public final int mProgramHandle;
	public final String mVertexShader, mFragmentShader;

	public final Resources mResources;
	private final int mPositionDataSize = 4;
	private final int mUVDataSize = 2;
	private final int mNormalDataSize = 3;
	
	private float[] mViewMatrix;
	private float[] mProjectionMatrix;
	public GLShadingProgram(Resources res) {
		mResources = res;
		mVertexShader = this.readRawTextFile(res.openRawResource(R.raw.shading_test_vertex));
		mFragmentShader = this.readRawTextFile(res.openRawResource(R.raw.shading_test_fragment));
		//---------------Create GL Program-------------
		int programHandle = GLES20.glCreateProgram();
		int vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
		int fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
		int[] compileStatus = new int [1];
		if(programHandle==0)
			throw new RuntimeException("Error creating GLProgram!");
		if(vertexShaderHandle==0)
			throw new RuntimeException("Error creating GL Vertex Shader!");
		if(fragmentShaderHandle==0)
			throw new RuntimeException("Error creating GL Fragment Shader!");
		//------------------Vertex Shader----------------
		GLES20.glShaderSource(vertexShaderHandle, mVertexShader);
		GLES20.glCompileShader(vertexShaderHandle);
		GLES20.glGetShaderiv(vertexShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
		if(compileStatus[0]==GLES20.GL_FALSE){
			String tmp=GLES20.glGetShaderInfoLog(vertexShaderHandle);
			Log.e("GLERR",tmp);
			GLES20.glDeleteShader(vertexShaderHandle);
			throw new RuntimeException("Error compiling GL Vertex Shader!");
		}
		//-----------------Fragment Shader-----------------
		GLES20.glShaderSource(fragmentShaderHandle, mFragmentShader);
		GLES20.glCompileShader(fragmentShaderHandle);
		GLES20.glGetShaderiv(fragmentShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
		if(compileStatus[0]==0){
			String error=GLES20.glGetShaderInfoLog(fragmentShaderHandle);
			GLES20.glDeleteShader(fragmentShaderHandle);
			throw new RuntimeException("Error compiling GL Fragment Shader!"+error);
		}
		//-----------------GL Program------------------
		GLES20.glAttachShader(programHandle, vertexShaderHandle);
		GLES20.glAttachShader(programHandle, fragmentShaderHandle);
		GLES20.glLinkProgram(programHandle);
		GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, compileStatus, 0);
		if(compileStatus[0]==0){
			String err=GLES20.glGetProgramInfoLog(programHandle);
			GLES20.glDeleteProgram(programHandle);
			throw  new RuntimeException("Error linking GL Program! "+err);
		}
		mProgramHandle = programHandle;
		
		
		sunLights_Direction=ByteBuffer.allocateDirect(mMaximumSunCount*4*mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		bulbLights_Location=ByteBuffer.allocateDirect(mMaximumBulbCount*4*mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		ambiantColor=ByteBuffer.allocateDirect(1*3*mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		sunLights_Direction.position(0);
		bulbLights_Location.position(0);
		ambiantColor.position(0);
		for(int i=0;i<mMaximumSunCount*4;i++) sunLights_Direction.put(0);
		for(int i=0;i<mMaximumBulbCount*4;i++) bulbLights_Location.put(0);
		for(int i=0;i<3;i++) ambiantColor.put(0);
		sunLights_Direction.position(0);
		bulbLights_Location.position(0);
		ambiantColor.position(0);
	}
	public void loadIntoGLES() {
		GLES20.glUseProgram(mProgramHandle);
	}
	public void renderAllGameObjects() {
		
		float[] zoom=new float[16];
		float[] model=new float[16];
		Matrix.setIdentityM(zoom, 0);
		Matrix.scaleM(zoom, 0, zoomFactor, zoomFactor, zoomFactor);
		
		for(GameObject obj:objects) {
			int mPositionHandle, mUVHandle, mNormalHandle;
			int mModelMatrixHandle,mViewMatrixHandle,mProjectionMatrixHandle;
			int mSunLightsHandle,mBulbLightsHandle;
			int mAmbiantColorHanlde;
			
			mModelMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_ModelMatrix");
			mViewMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_ViewMatrix");
			mProjectionMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_ProjectionMatrix");
			mSunLightsHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_sunLights_worldSpace");
			mBulbLightsHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_bulbLights_worldSpace");
			mAmbiantColorHanlde = GLES20.glGetUniformLocation(mProgramHandle, "u_AmbiantColor");
			
			mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
			mUVHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_UV");
			mNormalHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Normal");
			
			int myTextureSamplerHandle = GLES20.glGetUniformLocation(mProgramHandle, "myTextureSampler");
			
			GLES20.glEnableVertexAttribArray(mPositionHandle);
			GLES20.glEnableVertexAttribArray(mUVHandle);
			GLES20.glEnableVertexAttribArray(mNormalHandle);

			Matrix.multiplyMM(model, 0, obj.mModelMatrix, 0, zoom, 0);
			
			GLES20.glUniformMatrix4fv(mModelMatrixHandle, 1, false, model, 0);
			GLES20.glUniformMatrix4fv(mViewMatrixHandle, 1, false, mViewMatrix, 0);
			GLES20.glUniformMatrix4fv(mProjectionMatrixHandle, 1, false, mProjectionMatrix, 0);
			GLES20.glUniform3fv(mAmbiantColorHanlde, 1, ambiantColor);
			GLES20.glUniform4fv(mSunLightsHandle, mMaximumSunCount, sunLights_Direction);
			GLES20.glUniform4fv(mBulbLightsHandle, mMaximumSunCount, bulbLights_Location);
			
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			if(obj.mTexture!=null) GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, obj.mTexture.mTextureHandle);
			GLES20.glUniform1i(myTextureSamplerHandle, 0);
			
			obj.mPrototype.vertices.position(0);
			GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false, 0, obj.mPrototype.vertices);
			obj.mPrototype.textureUVs.position(0);
			GLES20.glVertexAttribPointer(mUVHandle, mUVDataSize, GLES20.GL_FLOAT, false, 0, obj.mPrototype.textureUVs);
			obj.mPrototype.normals.position(0);
			GLES20.glVertexAttribPointer(mNormalHandle, mNormalDataSize, GLES20.GL_FLOAT, false, 0, obj.mPrototype.normals);
			
			GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, obj.mPrototype.fCount*3);
			
			GLES20.glDisableVertexAttribArray(mPositionHandle);
			GLES20.glDisableVertexAttribArray(mUVHandle);
			GLES20.glDisableVertexAttribArray(mNormalHandle);
		}
	}
	public static final int mMaximumSunCount=6;
	public static final int mMaximumBulbCount=6;
	public static final int mBytesPerFloat=Float.SIZE/8;
	private FloatBuffer sunLights_Direction;	//Parallel
	private FloatBuffer bulbLights_Location;	//Divergent
	private FloatBuffer ambiantColor;
	public void setAmbiantColor(float r,float g,float b){
		ambiantColor.put(0, r);
		ambiantColor.put(1, g);
		ambiantColor.put(2, b);
	}
	/**
	 * 
	 * @param sunID
	 * @param sunLightDirectionVec
	 * Important:<strong>Vector[3] is the intensity of the light</strong>
	 */
	public void setSunLight(int sunID,float[] sunLightDirectionVec){
		int base=sunID*4;
		for(int i=0;i<4&&i<sunLightDirectionVec.length;i++) {
			sunLights_Direction.put(base+i,sunLightDirectionVec[i]);
		}
	}
	/**
	 * 
	 * @param bulbID
	 * @param bulbLightLocationVec
	 * Important:<strong>Vector[3] is the intensity of the light</strong>
	 */
	public void setBulbLight(int bulbID,float[] bulbLightLocationVec){
		int base=bulbID*4;
		for(int i=0;i<4&&i<bulbLightLocationVec.length;i++) {
			bulbLights_Location.put(base+i,bulbLightLocationVec[i]);
		}
	}
	
	float zoomFactor=1.0f;
	
	public void resetZoomFactor(float factor) {zoomFactor=factor;}
	
	public void resetViewMatrix(float[] viewMatrix){
		mViewMatrix = viewMatrix;
	}
	public void resetProjectionMatrix(float[] projectionMatrix){
		mProjectionMatrix = projectionMatrix;
	}
}
