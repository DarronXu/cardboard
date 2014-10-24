package com.IYYX.cardboard.myAPIs;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class Texture {
	
	public final int mTextureHandle;
	public boolean bIsValid;
	
	public Texture(Bitmap image) {
		mTextureHandle = loadGLTexture(image);
		bIsValid = true;
	}
	
	public Texture(Resources res, int resourceID, boolean zoomForBetterPerformance) {
		Bitmap tmp=loadBitmapFromResources(res, resourceID, zoomForBetterPerformance);
		mTextureHandle = loadGLTexture(tmp);
		tmp.recycle();
		
		bIsValid = true;
	}
	
	public void release() {
		deleteGLTexture(mTextureHandle);
		bIsValid = false;
	}
	
	public static int loadGLTexture(Bitmap image) {
		int[] newTextureHandles = new int[1]; 
		GLES20.glGenTextures(1, newTextureHandles, 0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, newTextureHandles[0]);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);		
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, image, 0);
		return newTextureHandles[0];
	} 
	
	public static void deleteGLTexture(int textureHandle) {
		int[] handles = new int[]{textureHandle};
		GLES20.glDeleteTextures(1, handles, 0);
	}
	
	public static Bitmap loadBitmapFromResources(Resources res, int resourceID, boolean zoomForBetterPerformance) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;   // No pre-scaling
        
        
        options.inPreferredConfig=Bitmap.Config.ARGB_8888;
        /*
         * THE ABOVE sentence is important, so that
         * the newly added code in MainActivity.java
         * "cardboardView.setEGLConfigChooser(8, 8, 8, 8, 0, 0);"
         * will accord with this actually format of decoded Bitmap
        */
 
        // Read in the resource
        final Bitmap bitmap = BitmapFactory.decodeResource(res, resourceID, options);
        return bitmap;
	}
	
	public static int getNextHighestPO2( int n ) {
	    n -= 1;
	    n = n | (n >> 1);
	    n = n | (n >> 2);
	    n = n | (n >> 4);
	    n = n | (n >> 8);
	    n = n | (n >> 16);
	    n = n | (n >> 32);
	    return n + 1;
	}
}
