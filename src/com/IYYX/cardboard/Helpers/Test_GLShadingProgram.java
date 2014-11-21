package com.IYYX.cardboard.Helpers;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL2;

import android.opengl.GLES20;

import com.IYYX.cardboard.myAPIs.*;

class Test_GLShadingProgram extends GLProgram {
	public final int mProgramHandle;
	public final String mVertexShader, mFragmentShader;

	private final int mPositionDataSize = 4;
	private final int mUVDataSize = 2;
	private final int mNormalDataSize = 3;
	
	private float[] mViewMatrix;
	private float[] mProjectionMatrix;
	private GL2 gl;
	
	private String checkLogInfo(GL2 gl, int programObject) {
        IntBuffer intValue = ByteBuffer.allocateDirect(1*Integer.SIZE/8).order(ByteOrder.nativeOrder()).asIntBuffer();
        gl.glGetObjectParameterivARB(programObject, GL2.GL_INFO_LOG_LENGTH, intValue);

        int lengthWithNull = intValue.get();

        if (lengthWithNull <= 1) {
            return "";
        }

        ByteBuffer infoLog = ByteBuffer.allocateDirect(lengthWithNull).order(ByteOrder.nativeOrder());

        intValue.flip();
        gl.glGetShaderInfoLog(programObject, lengthWithNull, intValue, infoLog);

        int actualLength = intValue.get();

        byte[] infoBytes = new byte[actualLength];
        infoLog.get(infoBytes);
        return new String(infoBytes);
    }
	
	public Test_GLShadingProgram(GL2 gl) {
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
			
			mVertexShader = this.readRawTextFile(new FileInputStream("./res/raw/shading_test_vertex.shader"));
			mFragmentShader = this.readRawTextFile(new FileInputStream("./res/raw/shading_test_for_windows_fragment.shader"));
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
			//gl.glgetshaderinfo
			String err=checkLogInfo(gl,fragmentShaderHandle);
			gl.glDeleteShader(fragmentShaderHandle);
			throw new RuntimeException("Error compiling GL Fragment Shader! "+err);
		}
		//-----------------GL Program------------------
		gl.glAttachShader(programHandle, vertexShaderHandle);
		gl.glAttachShader(programHandle, fragmentShaderHandle);
		gl.glLinkProgram(programHandle);
		gl.glGetProgramiv(programHandle, GL2.GL_LINK_STATUS, compileStatus, 0);
		if(compileStatus[0]==0){
			String err=checkLogInfo(gl,programHandle);
			gl.glDeleteProgram(programHandle);
			throw  new RuntimeException("Error linking GL Program! "+err);
		}
		mProgramHandle = programHandle;
		sunLights_Direction=ByteBuffer.allocateDirect(mMaximumSunCount*4*mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		bulbLights_Location=ByteBuffer.allocateDirect(mMaximumBulbCount*4*mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		sunLights_Direction.position(0);
		bulbLights_Location.position(0);
		for(int i=0;i<mMaximumSunCount*4;i++) sunLights_Direction.put(0);
		for(int i=0;i<mMaximumBulbCount*4;i++) bulbLights_Location.put(0);
		sunLights_Direction.position(0);
		bulbLights_Location.position(0);
	}
	public void loadIntoGLES() {
		gl.glUseProgram(mProgramHandle);
	}
	public void renderAllGameObjects() {
		for(GameObject obj:objects) {
			int mPositionHandle, mUVHandle, mNormalHandle;
			int mModelMatrixHandle,mViewMatrixHandle,mProjectionMatrixHandle;
			int mSunLightsHandle,mBulbLightsHandle;

			mModelMatrixHandle = gl.glGetUniformLocation(mProgramHandle, "u_ViewMatrix");
			mViewMatrixHandle = gl.glGetUniformLocation(mProgramHandle, "u_ModelMatrix");
			mProjectionMatrixHandle = gl.glGetUniformLocation(mProgramHandle, "u_ProjectionMatrix");
			mSunLightsHandle = gl.glGetUniformLocation(mProgramHandle, "u_sunLights_worldSpace");
			mBulbLightsHandle = gl.glGetUniformLocation(mProgramHandle, "u_bulbLights_worldSpace");
			
			mPositionHandle = gl.glGetAttribLocation(mProgramHandle, "a_Position");
			mUVHandle = gl.glGetAttribLocation(mProgramHandle, "a_UV");
			mNormalHandle = gl.glGetAttribLocation(mProgramHandle, "a_Normal");
			
			int myTextureSamplerHandle = gl.glGetUniformLocation(mProgramHandle, "myTextureSampler");
			
			gl.glEnableVertexAttribArray(mPositionHandle);
			gl.glEnableVertexAttribArray(mUVHandle);
			gl.glEnableVertexAttribArray(mNormalHandle);
			
			gl.glUniformMatrix4fv(mModelMatrixHandle, 1, false, obj.mModelMatrix, 0);
			gl.glUniformMatrix4fv(mViewMatrixHandle, 1, false, mViewMatrix, 0);
			gl.glUniformMatrix4fv(mProjectionMatrixHandle, 1, false, mProjectionMatrix, 0);
			gl.glUniform4fv(mSunLightsHandle, mMaximumSunCount, sunLights_Direction);
			gl.glUniform4fv(mBulbLightsHandle, mMaximumSunCount, bulbLights_Location);
			
			gl.glActiveTexture(GL2.GL_TEXTURE0);
			if(obj.mTexture!=null) gl.glBindTexture(GL2.GL_TEXTURE_2D, obj.mTexture.mTextureHandle);
			gl.glUniform1i(myTextureSamplerHandle, 0);
			
			obj.mPrototype.vertices.position(0);
			gl.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GL2.GL_FLOAT, false, 0, obj.mPrototype.vertices);
			obj.mPrototype.textureUVs.position(0);
			gl.glVertexAttribPointer(mUVHandle, mUVDataSize, GL2.GL_FLOAT, false, 0, obj.mPrototype.textureUVs);
			obj.mPrototype.normals.position(0);
			gl.glVertexAttribPointer(mNormalHandle, mNormalDataSize, GL2.GL_FLOAT, false, 0, obj.mPrototype.normals);
			
			gl.glDrawArrays(GL2.GL_TRIANGLES, 0, obj.mPrototype.fCount*3);
			
			gl.glDisableVertexAttribArray(mPositionHandle);
			gl.glDisableVertexAttribArray(mUVHandle);
			gl.glDisableVertexAttribArray(mNormalHandle);
		}
	}
	public static final int mMaximumSunCount=6;
	public static final int mMaximumBulbCount=6;
	public static final int mBytesPerFloat=Float.SIZE/8;
	private FloatBuffer sunLights_Direction;	//Parallel
	private FloatBuffer bulbLights_Location;	//Divergent
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
	public void resetViewMatrix(float[] viewMatrix){
		mViewMatrix = viewMatrix;
	}
	public void resetProjectionMatrix(float[] projectionMatrix){
		mProjectionMatrix = projectionMatrix;
	}
}
