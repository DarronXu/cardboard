package com.IYYX.cardboard;

import java.nio.*;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.res.Resources;
import android.opengl.*;
import android.os.SystemClock;
import android.util.Log;

public class Renderer1 implements GLSurfaceView.Renderer {

	private final FloatBuffer mTriangle1Vertices;
	private final FloatBuffer mTriangle2Vertices;
	private final FloatBuffer mTriangle3Vertices;
	private final int mBytesPerFloat = 4;
	private final float[] triangleVerticesData = {
			// X, Y, Z,
			// R, G, B, A;
			-0.5f, -0.5f, 0.0f,
			1.0f, 0.0f, 0.0f, 1.0f,

			0.5f, -0.5f, 0.0f,
			0.0f, 0.0f, 1.0f, 1.0f,

			0.5f,0.0f,0.0f,
			0.0f,1.0f,0.0f,1.0f
			

			-0.5f, 0f, 0.5f,
			1.0f, 0.0f, 0.0f, 1.0f,

			0.5f, 0f, 0.5f,
			0.0f, 0.0f, 1.0f, 1.0f,

			0.5f,0.0f,0.0f,
			0.0f,1.0f,0.0f,1.0f
			};
	private Resources res;
	final String vertexShader,fragmentShader;
	public Renderer1(Resources res){
		mTriangle1Vertices = ByteBuffer.allocateDirect(triangleVerticesData.length*mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mTriangle2Vertices = ByteBuffer.allocateDirect(triangleVerticesData.length*mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mTriangle3Vertices = ByteBuffer.allocateDirect(triangleVerticesData.length*mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		mTriangle1Vertices.put(triangleVerticesData).position(0);
		mTriangle2Vertices.put(triangleVerticesData).position(21);
		mTriangle3Vertices.put(triangleVerticesData).position(14);
		this.res=res;
		vertexShader = res.getString(R.string.vertexShader1);
		fragmentShader = res.getString(R.string.fragmentShader1);
	}
	
	//There are three types of Matrix in OpenGL:
	//  Model, View, and Projection.
	//  Model: objects
	//  View: player
	//  Projection: other parameters needed to project 3D to 2D
	//  
	//In a Vertex/Fragment Shader Script,
	//  there is usually a var called u_MVPMatrix.
	//  It actually represented the combined
	//  Model/View/Projection matrix.
	private float[] mModelMatrix = new float[16];
	private float[] mViewMatrix = new float[16];
	private float[] mProjectionMatrix = new float[16];
	private int mMVPMatrixHandle;
	private int mPositionHandle;
	private int mColorHandle;
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		GLES20.glClearColor(0.3f, 0.3f, 0.3f, 0.5f);
		
		//---------------Set up View Matrix-----------------
		// eye: Position the eye behind the origin
		// look: The point we are looking at
		// up: Our 'up' vector. This is where our head would be pointing, were we holding the camera 
		// [0] is X. [1] is Y. [2] is Z.
		final float[] eye = {0.0f,0.0f,1.5f};
		final float[] look = {0.0f,0.0f,-5.0f};
		final float[] up = {0.0f,1.0f,0.0f};
		Matrix.setLookAtM(mViewMatrix, 0,
				eye[0], eye[1], eye[2],
				look[0], look[1], look[2],
				up[0], up[1], up[2]);
		//Projection Matrix changes upon onSurfaceChanged()
		//Model Matrix changes upon onDrawFrame()
		//---------------Begin to Load GL Program-------------
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
		GLES20.glShaderSource(vertexShaderHandle, vertexShader);
		GLES20.glCompileShader(vertexShaderHandle);
		GLES20.glGetShaderiv(vertexShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
		if(compileStatus[0]==GLES20.GL_FALSE){
			String tmp=GLES20.glGetShaderInfoLog(vertexShaderHandle);
			Log.e("GLERR",tmp);
			GLES20.glDeleteShader(vertexShaderHandle);
			throw new RuntimeException("Error compiling GL Vertex Shader!");
		}
		//-----------------Fragment Shader-----------------
		GLES20.glShaderSource(fragmentShaderHandle, fragmentShader);
		GLES20.glCompileShader(fragmentShaderHandle);
		GLES20.glGetShaderiv(fragmentShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
		if(compileStatus[0]==0){
			GLES20.glDeleteShader(fragmentShaderHandle);
			throw new RuntimeException("Error compiling GL Fragment Shader!");
		}
		//-----------------GL Program------------------
		// Bind the shaders to the program.
		GLES20.glAttachShader(programHandle, vertexShaderHandle);
		GLES20.glAttachShader(programHandle, fragmentShaderHandle);
		// Bind attributes
		GLES20.glBindAttribLocation(programHandle, 0, "a_Position");
		GLES20.glBindAttribLocation(programHandle, 1, "a_Color");
		// Link these binded resources together into a program.
		GLES20.glLinkProgram(programHandle);
		GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, compileStatus, 0);
		if(compileStatus[0]==0){
			GLES20.glDeleteProgram(programHandle);
			throw  new RuntimeException("Error linking GL Program!");
		}
		//----------------Get Handles to pass values into GL-Script Variables---------------
		mMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix");
		mPositionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position");
		mColorHandle = GLES20.glGetAttribLocation(programHandle, "a_Color");
		//----------------Really Load in the compiled GLProgram------------
		// Put this block in onDrawFrame() if:
		// there are many different GLPrograms
		GLES20.glUseProgram(programHandle);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		//---------------Set up Projection Matrix-------------
		final float aspect=(float)width/(float)height;
		//Matrix.frustumM(mProjectionMatrix, 0,
				//-aspect, aspect, -1.0f, 1.0f, 1.0f, 10.0f);
		Matrix.perspectiveM(mProjectionMatrix, 0,
				60, aspect, 1.0f, 10.0f);
				//left, right, bottom, top, near, far
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub
		update();
		render(gl);
	}
	
	float angleInDegrees;
	
	void update(){
		long time=SystemClock.uptimeMillis()%10000L;
		angleInDegrees = (360.0f / 10000.0f) * ((int) time);
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 0.0f, 0.0f, 1.0f);
	}
	
	void render(GL10 gl){
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT|GLES20.GL_COLOR_BUFFER_BIT);
		drawTriangle(mTriangle1Vertices);
		drawTriangle(mTriangle2Vertices);
	}
	
	/** Allocate storage for the final combined matrix. This will be passed into the shader program. */
	private float[] mMVPMatrix = new float[16];
	/** How many elements per vertex. */
	private final int mStrideBytes = 7 * mBytesPerFloat;
	/** Offset of the position data. */
	private final int mPositionOffset = 0;
	/** Size of the position data in elements. */
	private final int mPositionDataSize = 3;
	/** Offset of the color data. */
	private final int mColorOffset = 3;
	/** Size of the color data in elements. */
	private final int mColorDataSize = 4;
	//Here is the actual drawing process
	/**
	 * Draws a triangle from the given vertex data.
	 *
	 * @param aTriangleBuffer The buffer containing the vertex data.
	 */
	void drawTriangle(final FloatBuffer aTriangleBuffer){
		/*FIRST:send a_Color, a_Position, u_MVPMatrix into the GLProgram*/{
			// Pass in the position information
			// NOTE THAT:
			//   Because we are going to use an automatic API to draw the triangle
			//   we just pass in information for all three Vertex of the triangle
			aTriangleBuffer.position(mPositionOffset);
			GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false, mStrideBytes, aTriangleBuffer);
			GLES20.glEnableVertexAttribArray(mPositionHandle);
			// Pass in the color information
			aTriangleBuffer.position(mColorOffset);
			GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false, mStrideBytes, aTriangleBuffer);
			GLES20.glEnableVertexAttribArray(mColorHandle);
			// This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
		    // (which currently contains model * view).
			Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
			// This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
		    // (which now contains model * view * projection).
			Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
		}
		/*SECOND:Start to draw with the passed in postitions*/{
			//NOTE THAT:
			//  Here we only have one sentence because
			//  the process of drawing a triangle is
			//  already defined in the API. 
			//  
			//  But actually it is right here that the OpenGL Scripts
			//  are used, co-workingly.
			GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
		}
	}
}
