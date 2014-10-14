package com.IYYX.cardboard;

import java.io.IOException;
import java.io.InputStream;
import java.nio.*;
import java.util.ArrayList;
import java.util.Scanner;

import javax.microedition.khronos.egl.EGLConfig;


import android.app.Activity;
import android.content.res.Resources;
import android.opengl.*;
import android.os.SystemClock;
import android.util.Log;

import com.google.vrtoolkit.cardboard.*;
public class CardboardRenderer2 implements CardboardView.StereoRenderer {

	private final int mBytesPerFloat = 4;
	private Resources res;
	private CardboardOverlayView mOverlayView;
	private Activity mDad;
	final String vertexShader,fragmentShader;
	public CardboardRenderer2(Resources res,CardboardOverlayView overlay,Activity context){
		this.res=res;
		this.mOverlayView=overlay;
		mDad=context;
		vertexShader = res.getString(R.string.vertexShader2);
		fragmentShader = res.getString(R.string.fragmentShader2);
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
	private float[] mCameraMatrix = new float[16];
	private float[] mViewMatrix = new float[16];
	private float[] mProjectionMatrix = new float[16];
	
	private float[] mHeadViewMatrix = new float[16];
	
	private int mMVPMatrixHandle;
	private int mPositionHandle;
	private int mColorHandle;
	
	@Override
	public void onDrawEye(EyeTransform arg0) {
		// TODO Auto-generated method stub
		//Log.d("[Renderer]","OnDrawEye");
		
		update();
		
        Matrix.multiplyMM(mViewMatrix, 0, arg0.getEyeView(), 0, mCameraMatrix, 0);
		mProjectionMatrix=arg0.getPerspective();
		Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
		Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
		
		render();
	}
	
	void update(){
		long time=SystemClock.uptimeMillis()%10000L;
		angleInDegrees = (360.0f / 10000.0f) * ((int) time);
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 0.0f, 0.0f, 1.0f);
	}

	void render(){
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT|GLES20.GL_COLOR_BUFFER_BIT);

		mPositionHandle = GLES20.glGetAttribLocation(mGLProgramHandle, "a_Position");
		mColorHandle = GLES20.glGetAttribLocation(mGLProgramHandle, "a_Color");

		GLES20.glEnableVertexAttribArray(mColorHandle);
		GLES20.glEnableVertexAttribArray(mPositionHandle);

		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
		drawSimpleObjects(earth_obj,mColorHandle,mPositionHandle);
		for(ObjFile obj:renderer2_obj) drawSimpleObjects(obj,mColorHandle,mPositionHandle);
	}
	
	/**
	 * <b>Warning:</b><br/><br/>
	 * The function does not pass any MVP Matrix to OpenGL.<br/>
	 * This function can only be used when suitable OpenGL shader exists.<br/>
	 * @param obj ObjFile object that represent the model to be drawn.
	 * @param colorHandle Handle to send Color information to OpenGL shader.
	 * @param positionHandle Handle to send Position information to OpenGL shader.
	 */
	void drawSimpleObjects(ObjFile obj, int colorHandle, int positionHandle){
		obj.vertices.position(0);
		GLES20.glVertexAttribPointer(positionHandle, mPositionDataSize, GLES20.GL_FLOAT, false, 0, obj.vertices);
		obj.colors.position(0);
		GLES20.glVertexAttribPointer(colorHandle, obj.mColorDataSize, GLES20.GL_FLOAT, false, 0, obj.colors);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, obj.fCount*3);
	}
	
	@Override
	public void onFinishFrame(Viewport arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNewFrame(HeadTransform arg0) {
		// TODO Auto-generated method stub
		arg0.getHeadView(mHeadViewMatrix, 0);
		//----------------Really Load in the compiled GLProgram------------
		// Put this block in onDrawFrame() if:
		// there are many different GLPrograms
		GLES20.glUseProgram(mGLProgramHandle);
		//----------------Get Handles to pass values into GL-Script Variables---------------
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mGLProgramHandle, "u_MVPMatrix");
		//We are not going to use mHeadViewMatrix just yet.
	}

	@Override
	public void onRendererShutdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSurfaceChanged(int width, int height) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onSurfaceCreated(EGLConfig arg0) {
		try {
			renderer2_obj=this.readObjFileToArr("renderer2.obj",3);
			earth_obj=this.readObjFileAsAWhole("earth.obj",3);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		GLES20.glClearColor(0.3f, 0.3f, 0.3f, 0.5f);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		GLES20.glDepthFunc(GLES20.GL_LESS);				//IMPORTANT!!
		
		//---------------Set up View Matrix-----------------
		// eye: Position the eye behind the origin
		// look: The direction we are looking towards
		// up: Our 'up' vector. This is where our head would be pointing, were we holding the camera
		// [0] is X. [1] is Y. [2] is Z.
		
		final float[] eye = {0.0f,5.0f,0.0f};
		final float[] look = {0.0f,-5.0f,0.0f};
		final float[] up = {0.0f,0.0f,5.0f};
		Matrix.setLookAtM(mCameraMatrix, 0,
				eye[0], eye[1], eye[2],
				look[0], look[1], look[2],
				up[0], up[1], up[2]);
		
		//Matrix.setLookAtM(mCameraMatrix, 0, 0.0f, 0.0f, 0.01f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
		
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
		mGLProgramHandle=programHandle;
	}
	
	int mGLProgramHandle;
	
	float angleInDegrees;

	ObjFile[] renderer2_obj=null;
	ObjFile earth_obj=null;
	
	private float[] mMVPMatrix = new float[16];
	private final int mPositionDataSize = 3;
	
	class ObjFile {
		public String name = "(default)";
		public boolean hasTextureUV=false;
		public FloatBuffer vertices, textureUVs, colors;
		public int mColorDataSize, vCount = 0, fCount = 0;
	}
	
	/**
	 * @param assetsName
	 * @param colorDataSize
	 *   This value can be 3 or 4.<br/>
	 *   If it's 3, we use RGB, or we use RGBA.
	 * @return
	 *   This function return only one ObjFile object. 
	 *   If the *.obj file actually defines more than two things, 
	 *   <b>This function will treat them as a whole.</b><br/>
	 *   There is another function that will return ObjFile[].
	 * @throws IOException
	 */
	ObjFile readObjFileAsAWhole(String assetsName, int colorDataSize) throws IOException {
		long time=SystemClock.uptimeMillis();
		
		InputStream istream=res.getAssets().open(assetsName);
		Scanner scan=new Scanner(istream);
		ObjFile ans=new ObjFile();
		ArrayList<Float> vertex=new ArrayList<Float>();
		ArrayList<Float> uv=new ArrayList<Float>();
		ArrayList<Float> ansVertices=new ArrayList<Float>();
		ArrayList<Float> ansTextureUVs=new ArrayList<Float>();
		
		while(scan.hasNextLine()) {
			showLoadingWait();
			final String line=scan.nextLine().replace("//", "/0/").replace('/', ' ');
			final String[] split=line.split(" ");
			if(split[0].equals("v")) {
				vertex.add(Float.parseFloat(split[1]));
				vertex.add(Float.parseFloat(split[2]));
				vertex.add(Float.parseFloat(split[3]));
			}
			if(split[0].equals("vt")) {
				uv.add(Float.parseFloat(split[1]));
				uv.add(Float.parseFloat(split[2]));
			}
			if(split[0].equals("f")) {			//We can mix 'f' together with 'v' and 'vt', because in the actual file, for every single object, any 'v' or 'vt' always appears earliers than any 'f'.
				ans.fCount++;
				for(int i=0;i<3;i++) {
					final int vertexIndex=Integer.parseInt(split[1+i*3]) - 1;	// MINUS ONE !!!
					for(int j=vertexIndex*3;j<vertexIndex*3+3;j++)
						ansVertices.add(vertex.get(j));
					final int uvIndex=Integer.parseInt(split[2+i*3]) - 1;		// MINUS ONE !!!
					if(uvIndex>=0) {
						ans.hasTextureUV=true;
						for(int j=uvIndex*2;j<uvIndex*2+2;j++)
							ansTextureUVs.add(uv.get(j));
					}
					Integer.parseInt(split[3+i*3]);
				}
			}
		} scan.close();
		
		ans.textureUVs=ByteBuffer.allocateDirect(ans.fCount*3*2*mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		ans.vertices=ByteBuffer.allocateDirect(ans.fCount*3*3*mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		ans.colors=ByteBuffer.allocateDirect(ans.fCount*3*4*mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		ans.textureUVs.position(0);
		ans.vertices.position(0);
		ans.colors.position(0);
		ans.vCount=vertex.size();
		ans.mColorDataSize=colorDataSize;
		ans.name=assetsName;
		
		for(int i=0;i<ans.fCount*3;i++){
			showLoadingWait();
			ans.vertices.put(ansVertices.get(i*3));
			ans.vertices.put(ansVertices.get(i*3+1));
			ans.vertices.put(ansVertices.get(i*3+2));
			if(ans.hasTextureUV) ans.textureUVs.put(ansTextureUVs.get(i*2));
			if(ans.hasTextureUV) ans.textureUVs.put(ansTextureUVs.get(i*2+1));
			ans.colors.put((float)Math.random());
			ans.colors.put((float)Math.random());
			ans.colors.put((float)Math.random());
			if(colorDataSize==4) ans.colors.put(1.0f);
		}
		ans.vertices.position(0);
		ans.textureUVs.position(0);
		ans.colors.position(0);
		Log.d("readObjAsAWhole","Load " + assetsName + " consumed "+(SystemClock.uptimeMillis()-time) + " ms.");
		return ans;
	}
	

	/**
	 * @param assetsName
	 * @param colorDataSize
	 *   This value can be 3 or 4.<br/>
	 *   If it's 3, we use RGB, or we use RGBA.
	 * @return
	 *   This function return <b>multiple ObjFile objects</b>, 
	 *   each of which represents every different models defined in the *.obj file.<br/>
	 *   There is another function that will treat them as a whole and return only one ObjFile.
	 * @throws IOException
	 */
	ObjFile[] readObjFileToArr(String assetsName, int colorDataSize) throws IOException {
		long time=SystemClock.uptimeMillis();
		
		InputStream istream=res.getAssets().open(assetsName);
		Scanner scan=new Scanner(istream);
		ObjFile currentObj=null;
		ArrayList<ObjFile> ans=new ArrayList<ObjFile>();
		ArrayList<Float> vertex=new ArrayList<Float>();
		ArrayList<Float> uv=new ArrayList<Float>();
		ArrayList<Float> ansVertices=new ArrayList<Float>();
		ArrayList<Float> ansTextureUVs=new ArrayList<Float>();
		int currentAnsIndex=0;
		
		while(scan.hasNextLine()) {
			showLoadingWait();
			final String line=scan.nextLine().replace("//", "/0/").replace('/', ' ');
			final String[] split=line.split(" ");
			if(split[0].equals("o")) {
				currentObj=new ObjFile();
				ans.add(currentObj);
				if(split.length>1) currentObj.name=split[1];
			}
			if(split[0].equals("v")) {
				vertex.add(Float.parseFloat(split[1]));
				vertex.add(Float.parseFloat(split[2]));
				vertex.add(Float.parseFloat(split[3]));
			}
			if(split[0].equals("vt")) {
				uv.add(Float.parseFloat(split[1]));
				uv.add(Float.parseFloat(split[2]));
			}
			if(split[0].equals("f")) {			//We can mix 'f' together with 'v' and 'vt', because in the actual file, for every single object, any 'v' or 'vt' always appears earliers than any 'f'.
				currentObj.fCount++;
				for(int i=0;i<3;i++) {
					final int vertexIndex=Integer.parseInt(split[1+i*3]) - 1;	// MINUS ONE !!!
					for(int j=vertexIndex*3;j<vertexIndex*3+3;j++)
						ansVertices.add(vertex.get(j));
					final int uvIndex=Integer.parseInt(split[2+i*3]) - 1;		// MINUS ONE !!!
					if(uvIndex>=0) {
						currentObj.hasTextureUV=true;
						for(int j=uvIndex*2;j<uvIndex*2+2;j++)
							ansTextureUVs.add(uv.get(j));
					}
					Integer.parseInt(split[3+i*3]);
				}
			}
		} scan.close();
		
		for(int k=0;k<ans.size();k++) {
			ObjFile cObj=ans.get(k);
			cObj.textureUVs=ByteBuffer.allocateDirect(cObj.fCount*3*2*mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
			cObj.vertices=ByteBuffer.allocateDirect(cObj.fCount*3*3*mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
			cObj.colors=ByteBuffer.allocateDirect(cObj.fCount*3*4*mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
			cObj.textureUVs.position(0);
			cObj.vertices.position(0);
			cObj.colors.position(0);
			cObj.vCount=vertex.size();
			cObj.mColorDataSize=colorDataSize;
			final int tmp=currentAnsIndex;
			for(;currentAnsIndex<tmp+cObj.fCount*3;currentAnsIndex++){
				showLoadingWait();
				cObj.vertices.put(ansVertices.get(currentAnsIndex*3));
				cObj.vertices.put(ansVertices.get(currentAnsIndex*3+1));
				cObj.vertices.put(ansVertices.get(currentAnsIndex*3+2));
				if(cObj.hasTextureUV) cObj.textureUVs.put(ansTextureUVs.get(currentAnsIndex*2));
				if(cObj.hasTextureUV) cObj.textureUVs.put(ansTextureUVs.get(currentAnsIndex*2+1));
				cObj.colors.put((float)Math.random());
				cObj.colors.put((float)Math.random());
				cObj.colors.put((float)Math.random());
				if(colorDataSize==4) cObj.colors.put(1.0f);
			}
			cObj.vertices.position(0);
			cObj.textureUVs.position(0);
			cObj.colors.position(0);
			Log.d("readObjToArr",cObj.name);
		}
		Log.d("readObjToArr","Load " + assetsName + " consumed "+(SystemClock.uptimeMillis()-time) + " ms.");
		return ans.toArray(emptyObjFileArray);
	}
	
	final ObjFile[] emptyObjFileArray=new ObjFile[]{};
	
	private Runnable mShowLoadingWait=new Runnable(){
		public void run(){
			mOverlayView.show3DToast("Loading Model, Please wait.\nPlease keep your phone vertical to the ground.");	
		}
	};
	
	void showLoadingWait()
	{
		mDad.runOnUiThread(mShowLoadingWait);
	}
}
