package com.IYYX.cardboard.Helpers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.media.opengl.GL2;

import com.IYYX.cardboard.myAPIs.*;

class Test_GLTextureProgram extends GLProgram {
	public final int mProgramHandle;
	public final String mVertexShader, mFragmentShader;

	private final int mPositionDataSize = 4;
	private final int mUVDataSize = 2;
	
	private final float[] mMVPMatrix = new float[16];
	private float[] mViewMatrix;
	private float[] mProjectionMatrix;
	private GL2 gl;
	
	public Test_GLTextureProgram(GL2 gl) {
		this.gl=gl;
		try {

			/*
			 * NOTE:
			 * In PC environment, there is also no need to do
			 *     y=1.0-y
			 * for texture images.
			 * 
			 * Thus this windows version of GLProgram uses a different FragmentShader.
			 */
			
			mVertexShader = this.readRawTextFile(new FileInputStream("./res/raw/myapi_vertex.shader"));
			mFragmentShader = this.readRawTextFile(new FileInputStream("./res/raw/myapi_for_windows_fragment.shader"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Error loading GL Shaders!");
		}
		//---------------Create GL Program-------------
		int programHandle = gl.glCreateProgram();
		int vertexShaderHandle = gl.glCreateShader(GL2.GL_VERTEX_SHADER);
		int fragmentShaderHandle = gl.glCreateShader(GL2.GL_FRAGMENT_SHADER);
		int[] compileStatus = new int [1];
		if(programHandle==0)
			throw new RuntimeException("Error creating GLProgram!");
		if(vertexShaderHandle==0)
			throw new RuntimeException("Error creating GL Vertex Shader!");
		if(fragmentShaderHandle==0)
			throw new RuntimeException("Error creating GL Fragment Shader!");
		//------------------Vertex Shader----------------
		gl.glShaderSource(vertexShaderHandle, 1, new String[]{mVertexShader}, new int[]{mVertexShader.length()}, 0);
		gl.glCompileShader(vertexShaderHandle);
		gl.glGetShaderiv(vertexShaderHandle, GL2.GL_COMPILE_STATUS, compileStatus, 0);
		if(compileStatus[0]==GL2.GL_FALSE){
			gl.glDeleteShader(vertexShaderHandle);
			throw new RuntimeException("Error compiling GL Vertex Shader!");
		}
		//-----------------Fragment Shader-----------------
		gl.glShaderSource(fragmentShaderHandle, 1, new String[]{mFragmentShader}, new int[]{mFragmentShader.length()}, 0);
		gl.glCompileShader(fragmentShaderHandle);
		gl.glGetShaderiv(fragmentShaderHandle, GL2.GL_COMPILE_STATUS, compileStatus, 0);
		if(compileStatus[0]==0){
			gl.glDeleteShader(fragmentShaderHandle);
			throw new RuntimeException("Error compiling GL Fragment Shader!");
		}
		//-----------------GL Program------------------
		gl.glAttachShader(programHandle, vertexShaderHandle);
		gl.glAttachShader(programHandle, fragmentShaderHandle);
		gl.glLinkProgram(programHandle);
		gl.glGetProgramiv(programHandle, GL2.GL_LINK_STATUS, compileStatus, 0);
		if(compileStatus[0]==0){
			gl.glDeleteProgram(programHandle);
			throw  new RuntimeException("Error linking GL Program!");
		}
		mProgramHandle = programHandle;
	}
	public void loadIntoGLES() {
		gl.glUseProgram(mProgramHandle);
	}
	public void renderAllGameObjects() {
		for(GameObject obj:objects) {
			int mPositionHandle, mUVHandle;
			int mMVPMatrixHandle;
			int mHasTexUVHandle;
			int mFallbackColorHandle;
			
			mMVPMatrixHandle = gl.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
			mPositionHandle = gl.glGetAttribLocation(mProgramHandle, "a_Position");
			mUVHandle = gl.glGetAttribLocation(mProgramHandle, "a_UV");
			mHasTexUVHandle=gl.glGetUniformLocation(mProgramHandle, "u_hasTexUV");
			mFallbackColorHandle=gl.glGetUniformLocation(mProgramHandle, "u_fallbackColor");
			int myTextureSamplerHandle = gl.glGetUniformLocation(mProgramHandle, "myTextureSampler");
			
			gl.glEnableVertexAttribArray(mPositionHandle);
			gl.glEnableVertexAttribArray(mUVHandle);

			gl.glMatrixMode(GL2.GL_MODELVIEW);
			gl.glPushMatrix();
			
			gl.glLoadMatrixf(mViewMatrix, 0);
			gl.glMultMatrixf(obj.mModelMatrix, 0);
			gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, mMVPMatrix, 0);
			gl.glLoadMatrixf(mProjectionMatrix, 0);
			gl.glMultMatrixf(mMVPMatrix, 0);
			gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, mMVPMatrix, 0);
			
			gl.glPopMatrix();
			
			gl.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
			
			gl.glActiveTexture(GL2.GL_TEXTURE0);
			if(obj.mTexture!=null) gl.glBindTexture(GL2.GL_TEXTURE_2D, obj.mTexture.mTextureHandle);
			gl.glUniform1i(myTextureSamplerHandle, 0);
			
			obj.mPrototype.vertices.position(0);
			gl.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GL2.GL_FLOAT, false, 0, obj.mPrototype.vertices);
			obj.mPrototype.textureUVs.position(0);
			gl.glVertexAttribPointer(mUVHandle, mUVDataSize, GL2.GL_FLOAT, false, 0, obj.mPrototype.textureUVs);
			gl.glUniform4fv(mFallbackColorHandle, 1, obj.fbColorArr, 0);
			if(obj.mPrototype.hasTextureUV) gl.glUniform1i(mHasTexUVHandle, 1);
			else gl.glUniform1i(mHasTexUVHandle, 0);
			gl.glDrawArrays(GL2.GL_TRIANGLES, 0, obj.mPrototype.fCount*3);
			
			gl.glDisableVertexAttribArray(mPositionHandle);
			gl.glDisableVertexAttribArray(mUVHandle);
		}
	}
	public void resetViewMatrix(float[] viewMatrix){
		mViewMatrix = viewMatrix;
	}
	public void resetProjectionMatrix(float[] projectionMatrix){
		mProjectionMatrix = projectionMatrix;
	}
	
}
