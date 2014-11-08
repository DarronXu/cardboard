package com.IYYX.cardboard.Helpers;

import com.IYYX.cardboard.Helpers.Matrix;
import com.IYYX.cardboard.Helpers.PartitionedGameObject;

import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import com.IYYX.cardboard.myAPIs.GameObject;
import com.IYYX.cardboard.myAPIs.GameObjectUpdater;
import com.IYYX.cardboard.myAPIs.Model;
import com.IYYX.cardboard.myAPIs.MyCallback;
import com.IYYX.cardboard.myAPIs.Texture;
import com.jogamp.opengl.util.FPSAnimator;

public class Test2 {
	
	static String objPath;
	static String objInfoName;

	static class glEventer implements GLEventListener{
		
		private float[] mCameraMatrix = new float[16];			//The position and orientation of Camera
		private float[] mProjectionMatrix = new float[16];
		
		
		Test_GLTextureProgram mTextureProgram;
		Model earthModel;
		Model[] boyModel;
		GameObject earthA;
		GameObject earthB;
		PartitionedGameObject boyA;
		Texture earthTexture;
		GL2 gl;
		
		public void init(GLAutoDrawable arg0) {
			gl=arg0.getGL().getGL2();
			gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
			gl.glEnable(GL2.GL_DEPTH_TEST);
			gl.glDepthFunc(GL2.GL_LESS);
			
			gl.glPointSize(8);
			gl.glLineWidth(5);
			
			gl.glEnable(GL2.GL_POINT_SMOOTH);
			gl.glEnable(GL2.GL_LINE_SMOOTH);
			gl.glHint(GL2.GL_POINT_SMOOTH_HINT, GL2.GL_NICEST);
			gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
			gl.glEnable(GL2.GL_BLEND);
			gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA);
		}
		
		boolean firstTime=true;
		float[] eye = {0.0f,0.0f,0.0f};
		float[] look = {0.5f,0.0f,0.0f};
		float[] up = {0.0f,0.0f,0.5f};
		
		Model floorModel;
		
		public void realInit(){
			//---------------Set up View Matrix-----------------
			
			Matrix.setIdentityM(mProjectionMatrix, 0);
			Matrix.setLookAtM(mCameraMatrix, 0, eye[0], eye[1], eye[2], look[0], look[1], look[2], up[0], up[1], up[2]);
			//setCameraMatrix();
			
			mTextureProgram = new Test_GLTextureProgram(gl);
			
			//------------------------ Load in Models and Textures --------------------------

			try {
				floorModel = Model.readWholeModel("floor.obj", new TestCallback());
				boyModel = Model.readPartitionedModel(objPath, new TestCallback());
			} catch (IOException e) {e.printStackTrace();}
			
			GameObjectUpdater commonUpdater=new GameObjectUpdater(){
				int degreeUD=0,degreeLR=0,degreeFB=0;
				float scale=0.7f;
				public void update(GameObject obj) {
					float[] modelM=obj.mModelMatrix;
					if(isPDownKeyPressed^isPUpKeyPressed) {
						if(isPDownKeyPressed) degreeFB-=1;
						if(isPUpKeyPressed) degreeFB+=1;
					} 
					if(isDownKeyPressed^isUpKeyPressed) {
						if(isDownKeyPressed) degreeUD-=1;
						if(isUpKeyPressed) degreeUD+=1;
					} 
					
					if(isLeftKeyPressed^isRightKeyPressed) {
						if(isLeftKeyPressed) degreeLR-=1;
						if(isRightKeyPressed) degreeLR+=1;
					}
					
					if(isMinusKeyPressed^isPlusKeyPressed) {
						if(isMinusKeyPressed) scale-=0.05f; 
						if(isPlusKeyPressed) scale+=0.05f;
						isPlusKeyPressed=isMinusKeyPressed=false;
					}
					Matrix.setIdentityM(modelM, 0);
					Matrix.rotateM(modelM, 0, degreeFB, 1f, 0, 0);
					Matrix.rotateM(modelM, 0, degreeUD, 0, 1f, 0);
					Matrix.rotateM(modelM, 0, degreeLR, 0, 0, 1f);
					Matrix.scaleM(modelM, 0, scale, scale, scale);
				}
			};
			
			boyA = new PartitionedGameObject(boyModel,objInfoName, commonUpdater, gl);
			GameObject xOyFloor,yOzFloor,zOxFloor;
			xOyFloor=new GameObject(floorModel, null);
			yOzFloor=new GameObject(floorModel, null);
			zOxFloor=new GameObject(floorModel, null);
			xOyFloor.fbColorArr=new float[]{1.0f,0,0,0.5f};
			yOzFloor.fbColorArr=new float[]{0,1.0f,0,0.5f};
			zOxFloor.fbColorArr=new float[]{0,0,1.0f,0.5f};
			//Matrix.setIdentityM(xOyFloor.mModelMatrix, 0);
			Matrix.setRotateM(xOyFloor.mModelMatrix, 0, 90f, 0, 0, 1f);
			Matrix.setRotateM(yOzFloor.mModelMatrix, 0, 90f, 0, 1f, 0);
			Matrix.setRotateM(zOxFloor.mModelMatrix, 0, 90f, 1f, 0, 0);
			//mTextureProgram.addGameObject(xOyFloor);
			//mTextureProgram.addGameObject(yOzFloor);
			//mTextureProgram.addGameObject(zOxFloor);
			boyA.addToGLProgram(mTextureProgram);
			firstTime=false;
		}
		
		public void display(GLAutoDrawable arg0) {
			gl=arg0.getGL().getGL2();
			if(firstTime) realInit();
			Matrix.setIdentityM(mProjectionMatrix, 0);
			Matrix.perspectiveM(mProjectionMatrix, 0, 45f, width/height, 0.01f, 5.0f);
			Matrix.setLookAtM(mCameraMatrix, 0, eye[0], eye[1], eye[2], look[0], look[1], look[2], up[0], up[1], up[2]);
			mTextureProgram.loadIntoGLES();
			mTextureProgram.updateAllGameObjects(); 			/* The original "update()" */
	        mTextureProgram.resetViewMatrix(mCameraMatrix);
	        mTextureProgram.resetProjectionMatrix(mProjectionMatrix);
	        //-------------- The following part is originally named "render()" ----------------
			gl.glClear(GL2.GL_DEPTH_BUFFER_BIT|GL2.GL_COLOR_BUFFER_BIT);
			mTextureProgram.renderAllGameObjects();
		}
		public void dispose(GLAutoDrawable arg0) {
		}
		
		float width=1;
		float height=1;
		
		public void reshape(GLAutoDrawable arg0, int x, int y, int width,
				int height) {
			arg0.getGL().getGL2().glViewport(x, y, width, height);
			this.width=width;
			this.height=height;
		}
	}
	
	public static void main(String[] args) {
		
		JFileChooser openObj=new JFileChooser();
		openObj.setDialogTitle("Choose *.obj File");
		openObj.setCurrentDirectory(new File("./assets/"));
		openObj.setFileFilter(new FileFilter(){
			public boolean accept(File f) {
				String[] tmp=f.getName().split("\\.");
				if(tmp.length==0) return false;
				if(f.isDirectory()) return true;
				if(tmp[tmp.length-1].equalsIgnoreCase("obj"))
					return true;
				else return false;
			}
			public String getDescription() {
				return "3D Model File (.obj)";
			}
		});
		int result = openObj.showOpenDialog(null);
		if(result!=JFileChooser.APPROVE_OPTION) System.exit(0);
		else {
			if(!openObj.getSelectedFile().getAbsolutePath().contains(new File("assets/").getAbsolutePath())) {
				System.err.println("Please select files ONLY in the project ./assets/ folder.");
				System.exit(0);
			}
			objPath = openObj.getSelectedFile().getName();
		}
		
		openObj=new JFileChooser();
		openObj.setDialogTitle("Choose TextureInfo File");
		openObj.setCurrentDirectory(new File("./assets/TextureInfo/"));
		result = openObj.showOpenDialog(null);
		if(result!=JFileChooser.APPROVE_OPTION) System.exit(0);
		else {
			if(!openObj.getSelectedFile().getAbsolutePath().contains(new File("assets/").getAbsolutePath())) {
				System.err.println("Please select files ONLY in the project ./assets/ folder.");
				System.exit(0);
			}
			objInfoName = openObj.getSelectedFile().getName();
		}
		
		GLProfile glp=GLProfile.getDefault();
		GLCapabilities caps=new GLCapabilities(glp);
		GLCanvas canvas=new GLCanvas(caps);
		glEventer eventer=new glEventer();
		canvas.addGLEventListener(eventer);
		
		Frame frame=new Frame("AWT Canvas");
		frame.setSize(300,300);
		frame.add(canvas);
		frame.setVisible(true);
		frame.addKeyListener(new MainframeKeyListener(eventer));

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        FPSAnimator anim1=new FPSAnimator(canvas,60);
        anim1.start();
        
        Scanner scan=new Scanner(System.in);
        
        while(true) {
        	String line=scan.nextLine();
        	String[] split=line.split(" ");
        	if(split.length>0) try {
        		if(split[0].equalsIgnoreCase("eye")) {
        			eventer.eye[0]=Float.parseFloat(split[1]);
        			eventer.eye[1]=Float.parseFloat(split[2]);
        			eventer.eye[2]=Float.parseFloat(split[3]);
        		}
        		if(split[0].equalsIgnoreCase("look")) {
        			eventer.look[0]=Float.parseFloat(split[1]);
        			eventer.look[1]=Float.parseFloat(split[2]);
        			eventer.look[2]=Float.parseFloat(split[3]);
        		}
        	}
        	catch (Exception err) {
        		err.printStackTrace();
        	}
        }
	}

	static boolean isPUpKeyPressed = false;
	static boolean isPDownKeyPressed = false;
	static boolean isDownKeyPressed = false;
	static boolean isUpKeyPressed = false;
	static boolean isLeftKeyPressed = false;
	static boolean isRightKeyPressed = false;
	static boolean isPlusKeyPressed = false;
	static boolean isMinusKeyPressed = false;
	
	static class MainframeKeyListener implements KeyListener {
		glEventer mScene;
		public MainframeKeyListener(glEventer scene){
			mScene=scene;
		}
		
		public void keyPressed(KeyEvent e) {
			switch(e.getKeyCode()){
			case KeyEvent.VK_UP: isUpKeyPressed=true;break;
			case KeyEvent.VK_DOWN: isDownKeyPressed=true;break;
			case KeyEvent.VK_RIGHT: isRightKeyPressed=true;break;
			case KeyEvent.VK_LEFT: isLeftKeyPressed=true;break;
			case KeyEvent.VK_PAGE_DOWN: isPDownKeyPressed=true;break;
			case KeyEvent.VK_PAGE_UP: isPUpKeyPressed=true;break;
			}
		}
		public void keyReleased(KeyEvent e) {
			switch(e.getKeyCode()){
			case KeyEvent.VK_UP: isUpKeyPressed=false;break;
			case KeyEvent.VK_DOWN: isDownKeyPressed=false;break;
			case KeyEvent.VK_RIGHT: isRightKeyPressed=false;break;
			case KeyEvent.VK_LEFT: isLeftKeyPressed=false;break;
			case KeyEvent.VK_PAGE_DOWN: isPDownKeyPressed=false;break;
			case KeyEvent.VK_PAGE_UP: isPUpKeyPressed=false;break;
			}
		}  
		public void keyTyped(KeyEvent e) {
			isPlusKeyPressed=isMinusKeyPressed=false;
			if(e.getKeyChar()=='=') isPlusKeyPressed=true;
			if(e.getKeyChar()=='-') isMinusKeyPressed=true;
			if(e.getKeyChar()=='q') {
				mScene.eye[2]+=0.05f;
				mScene.look[2]+=0.05f;
				e.consume();
			}
			if(e.getKeyChar()=='a') {
				mScene.eye[2]-=0.05f;
				mScene.look[2]-=0.05f;
				e.consume();
			}
			if(e.getKeyChar()=='h') {
				mScene.eye[1]+=0.05f;
				mScene.look[1]+=0.05f;
				e.consume();
			}
			if(e.getKeyChar()=='n') {
				mScene.eye[1]-=0.05f;
				mScene.look[1]-=0.05f;
				e.consume();
			}
			if(e.getKeyChar()=='w') {
				mScene.eye[0]+=0.05f;
				mScene.look[0]+=0.05f;
				e.consume();
			}
			if(e.getKeyChar()=='s') {
				mScene.eye[0]-=0.05f;
				mScene.look[0]-=0.05f;
				e.consume();
			}
		}
	}

	static class TestCallback extends MyCallback {
		public InputStream openAssetInput(String assetsName) throws IOException
		{
			return new FileInputStream("./assets/"+assetsName);
		}
		int count=0;
		public void showToast3D(String textAsString)
		{
			System.out.print(textAsString);
			count++;
			if(count>100) {
				System.out.println();
				count=0;
			}
		}
		public void showToast3D(int textAsResourceID)
		{
			if(textAsResourceID==com.IYYX.cardboard.R.string.myAPI_LoadingObjFile)
				showToast3D(".");
		}
	}
}