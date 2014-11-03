package com.IYYX.cardboard.myAPIs;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.IYYX.cardboard.R;

public class GLTextureProgram extends GLProgram {
	public final int mProgramHandle;
	public final Resources mResources;
	public final String mVertexShader, mFragmentShader;

	private final int mPositionDataSize = 4;
	private final int mUVDataSize = 2;
	
	private float[] mMVPMatrix = new float[16];
	private float[] mViewMatrix;
	private float[] mProjectionMatrix;
	
	public GLTextureProgram(Resources res) {
		mResources = res;
		mVertexShader = this.readRawTextFile(res.openRawResource(R.raw.myapi_vertex));
		mFragmentShader = this.readRawTextFile(res.openRawResource(R.raw.myapi_fragment));
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
		if(compileStatus[0]==GLES20.GL_FALSE){
			String error=GLES20.glGetShaderInfoLog(fragmentShaderHandle);
			GLES20.glDeleteShader(fragmentShaderHandle);
			throw new RuntimeException("Error compiling GL Fragment Shader!"+error);
		}
		//-----------------GL Program------------------
		GLES20.glAttachShader(programHandle, vertexShaderHandle);
		GLES20.glAttachShader(programHandle, fragmentShaderHandle);
		GLES20.glLinkProgram(programHandle);
		GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, compileStatus, 0);
		if(compileStatus[0]==GLES20.GL_FALSE){
			GLES20.glDeleteProgram(programHandle);
			throw  new RuntimeException("Error linking GL Program!");
		}
		mProgramHandle = programHandle;
	}
	public void loadIntoGLES() {
		GLES20.glUseProgram(mProgramHandle);
	}
	public void renderAllGameObjects() {
		for(GameObject obj:objects) {
			int mPositionHandle, mUVHandle;
			int mMVPMatrixHandle;
			
			mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
			mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
			mUVHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_UV");
			int myTextureSamplerHandle = GLES20.glGetUniformLocation(mProgramHandle, "myTextureSampler");
			
			GLES20.glEnableVertexAttribArray(mPositionHandle);
			GLES20.glEnableVertexAttribArray(mUVHandle);

			Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, obj.mModelMatrix, 0);
			Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
			
			GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
			
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, obj.mTexture.mTextureHandle);
			GLES20.glUniform1i(myTextureSamplerHandle, 0);
			
			obj.mPrototype.vertices.position(0);
			GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false, 0, obj.mPrototype.vertices);
			obj.mPrototype.textureUVs.position(0);
			GLES20.glVertexAttribPointer(mUVHandle, mUVDataSize, GLES20.GL_FLOAT, false, 0, obj.mPrototype.textureUVs);
			GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, obj.mPrototype.fCount*3);
			
			GLES20.glDisableVertexAttribArray(mPositionHandle);
			GLES20.glDisableVertexAttribArray(mUVHandle);
		}
	}
	public void resetViewMatrix(float[] viewMatrix){
		mViewMatrix = viewMatrix;
	}
	public void resetProjectionMatrix(float[] projectionMatrix){
		mProjectionMatrix = projectionMatrix;
	}
	
}
