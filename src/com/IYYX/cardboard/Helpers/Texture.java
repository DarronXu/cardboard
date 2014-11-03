package com.IYYX.cardboard.Helpers;

import java.io.InputStream;

import javax.media.opengl.GL2;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

class Texture extends com.IYYX.cardboard.myAPIs.Texture {
	
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
	
	public Texture(InputStream istream) {
		Bitmap tmp=loadBitmapFromStream(istream);
		mTextureHandle = loadGLTexture(tmp);
		tmp.recycle();
		
		bIsValid = true;
	}
	
	public Texture(com.jogamp.opengl.util.texture.Texture texture, GL2 gl){
		mTextureHandle = texture.getTextureObject(gl);
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
        options.inScaled = false;
        options.inPreferredConfig=Bitmap.Config.ARGB_8888;

        final Bitmap bitmap = BitmapFactory.decodeResource(res, resourceID, options);
        return bitmap;
	}
	
	public static Bitmap loadBitmapFromStream(InputStream istream) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inPreferredConfig=Bitmap.Config.ARGB_8888;

        final Bitmap bitmap = BitmapFactory.decodeStream(istream, null, options);
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
