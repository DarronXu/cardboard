package com.IYYX.cardboard;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.nio.*;
import java.util.ArrayList;
import java.util.Scanner;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.apache.http.util.EncodingUtils;

import edu.union.graphics.*;
import android.content.res.Resources;
import android.opengl.*;
import android.os.SystemClock;
import android.util.Log;
import android.util.Xml.Encoding;

import com.google.vrtoolkit.cardboard.*;
public class CardboardRenderer2 implements CardboardView.StereoRenderer {

	private final int mBytesPerFloat = 4;
	private Resources res;
	final String vertexShader,fragmentShader;
	public CardboardRenderer2(Resources res){
		this.res=res;
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
		
		//GLES20.glEnableVertexAttribArray(mColorHandle);

		//renderer2_obj.vertices.position(0);
		//GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, renderer2_obj.fCount*3*3*mBytesPerFloat, renderer2_obj.vertices, GLES20.GL_STATIC_DRAW);
		//GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, renderer2_obj.fCount*4*mBytesPerFloat, renderer2_obj.colors, GLES20.GL_STATIC_DRAW);
		
		renderer2_obj.vertices.position(0);
		GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false, 0, renderer2_obj.vertices);
		
		renderer2_obj.colors.position(0);
		GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false, 0, renderer2_obj.colors);
		
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, renderer2_obj.fCount*3);
		checkGLError("After Draw Array");
	}
    private static void checkGLError(String func) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e("[ZZ]", func + ": glError " + error);
            throw new RuntimeException(func + ": glError " + error);
        }
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
		oldSurfaceChanged(width,height);
	}

	@Override
	public void onSurfaceCreated(EGLConfig arg0) {
		// TODO Auto-generated method stub
		oldSurfaceCreated();
	}
	
	//-----------------------OLD CODE-------------------------------
	
	public void oldSurfaceCreated() {
		try {
			renderer2_obj=this.readCompleteVerticesFromObjFile("renderer2.obj");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		GLES20.glClearColor(0.3f, 0.3f, 0.3f, 0.5f);
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		//GLES20.glDepthFunc(GLES20.GL_LESS);
		
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

	public void oldSurfaceChanged(int width, int height) {
		//---------------Set up Projection Matrix-------------
		final float ratio=(float) width/height;
	}
	
	float angleInDegrees;
	
	ObjFile renderer2_obj=null;
	
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
	private final int mColorDataSize = 4; //or 3 if RGB
	
	class ObjFile{
		public FloatBuffer vertices,normals,elements,colors;
		public int vCount,fCount;
	}
	
	ObjFile readCompleteVerticesFromObjFile(String assetsName) throws IOException{
		InputStream istream=res.getAssets().open(assetsName);
		Scanner scan=new Scanner(istream);
		ObjFile ans=new ObjFile();
		ArrayList<Float> _vertex=new ArrayList<Float>();
		ArrayList<Short> _element=new ArrayList<Short>();
		ArrayList<Float> _normal=new ArrayList<Float>();
		ans.fCount=0;
		while(true){
			if(!scan.hasNextLine()) break;
			String line=scan.nextLine();
			StringBufferInputStream sistream= new StringBufferInputStream(line);
			Scanner scan2=new Scanner(sistream);
			String lineHeader=scan2.next();
			if(lineHeader.compareTo("v")==0) {
				_vertex.add(scan2.nextFloat());
				_vertex.add(scan2.nextFloat());
				_vertex.add(scan2.nextFloat());
			} else if(lineHeader.compareTo("f")==0) {ans.fCount++;};
			scan2.close();
		}
		ans.vCount=_vertex.size();
		ans.vertices=ByteBuffer.allocateDirect(ans.fCount*3*3*mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		ans.colors=ByteBuffer.allocateDirect(ans.fCount*3*4*mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		ans.vertices.position(0);
		ans.colors.position(0);
		scan.close();
		
		istream=res.getAssets().open(assetsName);
		scan=new Scanner(istream);
		while(true){
			if(!scan.hasNextLine()) break;
			String line=scan.nextLine();
			line=line.replace("//", "/0/").replace('/', ' ');
			StringBufferInputStream sistream = new StringBufferInputStream(line);
			Scanner scan2= new Scanner(sistream);
			String lineHeader=scan2.next();
			if(lineHeader.compareTo("f")==0) {

				int index,tmp;
				for(int i=0;i<3;i++) {
					if(!scan2.hasNextInt()) throw new RuntimeException("Wrong Format!");
					index=scan2.nextInt();
					if(!scan2.hasNextInt()) throw new RuntimeException("Wrong Format!");
					tmp=scan2.nextInt();
					if(!scan2.hasNextInt()) throw new RuntimeException("Wrong Format!");
					tmp=scan2.nextInt();
					
					index--;
					for(int j=index*3;j<index*3+3;j++) {
						ans.vertices.put(_vertex.get(j));
					}
				}
			}
		}
		ans.vertices.position(0);
		
		for(int i=0;i<ans.fCount*3;i++){
			ans.colors.put((float)Math.random());
			ans.colors.put((float)Math.random());
			ans.colors.put((float)Math.random());
			ans.colors.put(1.0f);
		}
		ans.colors.position(0);

		ans.vertices.position(0);
		scan.close();
		
		return ans;
	}

	/*ObjFile readCompleteVerticesFromObjFile(String assetsName) throws IOException{
		InputStream istream=res.getAssets().open(assetsName);
		Scanner scan=new Scanner(istream);
		ObjFile ans=new ObjFile();
		ArrayList<Float> _vertex=new ArrayList<Float>();
		ArrayList<Short> _element=new ArrayList<Short>();
		ArrayList<Float> _normal=new ArrayList<Float>();
		ans.fCount=0;
		while(true){
			if(!scan.hasNextLine()) break;
			String line=scan.nextLine();
			StringBufferInputStream sistream= new StringBufferInputStream(line);
			Scanner scan2=new Scanner(sistream);
			String lineHeader=scan2.next();
			if(lineHeader.compareTo("v")==0) {
				_vertex.add(scan2.nextFloat());
				_vertex.add(scan2.nextFloat());
				_vertex.add(scan2.nextFloat());
			} else if(lineHeader.compareTo("f")==0) {ans.fCount++;};
			scan2.close();
		}
		ans.vCount=_vertex.size();
		ans.vertices=ByteBuffer.allocateDirect(ans.fCount*3*3*mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		ans.colors=ByteBuffer.allocateDirect(ans.fCount*4*mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		ans.vertices.position(0);
		ans.colors.position(0);
		scan.close();
		
		istream=res.getAssets().open(assetsName);
		scan=new Scanner(istream);
		while(true){
			if(!scan.hasNextLine()) break;
			String line=scan.nextLine();
			line=line.replace('/', ' ');
			StringBufferInputStream sistream = new StringBufferInputStream(line);
			Scanner scan2= new Scanner(sistream);
			String lineHeader=scan2.next();
			//scan2.useDelimiter("[/\\s]");
			if(lineHeader.compareTo("f")==0) {

				int index,tmp;
				for(int i=0;i<3;i++) {
					if(!scan2.hasNextInt()) throw new RuntimeException("Wrong Format!");
					index=scan2.nextInt();
					if(!scan2.hasNextInt()) throw new RuntimeException("Wrong Format!");
					tmp=scan2.nextInt();
					
					index--;
					for(int j=index*3;j<index*3+3;j++) {
						ans.vertices.put(_vertex.get(j));
					}
				}
			}
		}
		ans.vertices.position(0);
		
		for(int i=0;i<ans.fCount;i++){
			ans.colors.put((float)Math.random());
			ans.colors.put((float)Math.random());
			ans.colors.put((float)Math.random());
			ans.colors.put(1.0f);
		}
		ans.colors.position(0);
		
		return ans;
	}*/
}
