package com.IYYX.cardboard.Helpers;

import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import com.IYYX.cardboard.myAPIs.GameObject;
import com.IYYX.cardboard.myAPIs.GameObjectUpdater;
import com.IYYX.cardboard.myAPIs.Model;
import com.IYYX.cardboard.myAPIs.MyCallback;
import com.IYYX.cardboard.myAPIs.Test_GLTextureProgram;
import com.IYYX.cardboard.myAPIs.Texture;
import com.jogamp.opengl.util.FPSAnimator;

public class Test2 {
	
	static String objPath;

	static class glEventer implements GLEventListener{
		
		private float[] mCameraMatrix = new float[16];			//The position and orientation of Camera
		private float[] mIdentityMatrix = new float[16];
		
		
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
			gl.glEnable(GL2.GL_BLEND); 
			gl.glBlendFunc(GL2.GL_ONE, GL2.GL_ONE_MINUS_SRC_ALPHA);
		}
		
		boolean firstTime=true;
		
		public void realInit(){
			//---------------Set up View Matrix-----------------
			final float[] eye = {0.5f,0.0f,0.0f};
			final float[] look = {-0.5f,0.0f,0.0f};
			final float[] up = {0.0f,0.0f,0.5f};
			gl.glMatrixMode(GL2.GL_MODELVIEW);
			gl.glPushMatrix();
			gl.glLoadIdentity();
			gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, mIdentityMatrix, 0);
			GLU.createGLU(gl).gluLookAt(
					eye[0], eye[1], eye[2],
					look[0], look[1], look[2],
					up[0], up[1], up[2]);
			gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, mCameraMatrix, 0);
			gl.glPopMatrix();
			
			mTextureProgram = new Test_GLTextureProgram(gl);
			
			//------------------------ Load in Models and Textures --------------------------

			try {
				boyModel = Model.readPartitionedModel(objPath, 3, new TestCallback());
			} catch (IOException e) {e.printStackTrace();}
			
			boyA = new PartitionedGameObject(boyModel, new GameObjectUpdater(){
				int degreeUD=0,degreeLR=0;
				float scale=0.7f;
				public void update(GameObject obj) {
					
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
					
					gl.glMatrixMode(GL2.GL_MODELVIEW);
					gl.glPushMatrix();
					gl.glLoadIdentity();
					gl.glTranslatef(0, 0.0f, 0.0f);
					gl.glScalef(scale,scale,scale);
					gl.glRotatef(degreeUD, 0.0f, 1.0f, 0.0f);
					gl.glRotatef(degreeLR, 0.0f, 0.0f, 1.0f);
					gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, obj.mModelMatrix, 0);
					gl.glPopMatrix();
				}
			}, gl);
			
			boyA.addToGLProgram(mTextureProgram);
			firstTime=false;
		}
		
		public void display(GLAutoDrawable arg0) {
			gl=arg0.getGL().getGL2();
			if(firstTime) realInit();
			mTextureProgram.loadIntoGLES();
			mTextureProgram.updateAllGameObjects(); 			/* The original "update()" */
	        mTextureProgram.resetViewMatrix(mCameraMatrix);
	        mTextureProgram.resetProjectionMatrix(mIdentityMatrix);
	        //-------------- The following part is originally named "render()" ----------------
			gl.glClear(GL2.GL_DEPTH_BUFFER_BIT|GL2.GL_COLOR_BUFFER_BIT);
			mTextureProgram.renderAllGameObjects();
		}
		public void dispose(GLAutoDrawable arg0) {
		}
		public void reshape(GLAutoDrawable arg0, int x, int y, int width,
				int height) {
			arg0.getGL().getGL2().glViewport(x, y, width, height);
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
		
		GLProfile glp=GLProfile.getDefault();
		GLCapabilities caps=new GLCapabilities(glp);
		GLCanvas canvas=new GLCanvas(caps);
		canvas.addGLEventListener(new glEventer());
		
		Frame frame=new Frame("AWT Canvas");
		frame.setSize(300,300);
		frame.add(canvas);
		frame.setVisible(true);
		frame.addKeyListener(new MainframeKeyListener());

        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        FPSAnimator anim1=new FPSAnimator(canvas,60);
        anim1.start();
	}

	static boolean isDownKeyPressed = false;
	static boolean isUpKeyPressed = false;
	static boolean isLeftKeyPressed = false;
	static boolean isRightKeyPressed = false;
	static boolean isPlusKeyPressed = false;
	static boolean isMinusKeyPressed = false;
	
	static class MainframeKeyListener implements KeyListener {
		public void keyPressed(KeyEvent e) {
			switch(e.getKeyCode()){
			case KeyEvent.VK_UP: isUpKeyPressed=true;break;
			case KeyEvent.VK_DOWN: isDownKeyPressed=true;break;
			case KeyEvent.VK_RIGHT: isRightKeyPressed=true;break;
			case KeyEvent.VK_LEFT: isLeftKeyPressed=true;break;
			}
		}
		public void keyReleased(KeyEvent e) {
			switch(e.getKeyCode()){
			case KeyEvent.VK_UP: isUpKeyPressed=false;break;
			case KeyEvent.VK_DOWN: isDownKeyPressed=false;break;
			case KeyEvent.VK_RIGHT: isRightKeyPressed=false;break;
			case KeyEvent.VK_LEFT: isLeftKeyPressed=false;break;
			}
		}  
		public void keyTyped(KeyEvent e) {
			isPlusKeyPressed=isMinusKeyPressed=false;
			if(e.getKeyChar()=='=') isPlusKeyPressed=true;
			if(e.getKeyChar()=='-') isMinusKeyPressed=true;
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