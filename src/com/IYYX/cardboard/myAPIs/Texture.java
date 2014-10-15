package com.IYYX.cardboard.myAPIs;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
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
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, image, 0);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		return newTextureHandles[0];
	}
	
	public static void deleteGLTexture(int textureHandle) {
		int[] handles = new int[]{textureHandle};
		GLES20.glDeleteTextures(1, handles, 0);
	}
	
	public static Drawable readDrawableFromResources(Resources res, int resourceID){
		return res.getDrawable(resourceID);
	}
	
	public static Bitmap loadBitmapFromResources(Resources res, int resourceID, boolean zoomForBetterPerformance) {
	    // pull in the resource
	    Bitmap bitmap = null;
	    Drawable image = res.getDrawable(resourceID);
	    float density = res.getDisplayMetrics().density;
	    int origWidth = (int)(image.getIntrinsicWidth() / density);
	    int origHeight = (int)(image.getIntrinsicHeight() / density);
	    int powWidth = getNextHighestPO2(origWidth);
	    int powHeight = getNextHighestPO2(origHeight);

	    if (zoomForBetterPerformance) image.setBounds(0, 0, powWidth, powHeight);
	    else image.setBounds(0, 0, origWidth, origHeight);

	    // Create an empty, mutable bitmap
	    bitmap = Bitmap.createBitmap(powWidth, powHeight, Bitmap.Config.ARGB_4444);
	    // get a canvas to paint over the bitmap
	    Canvas canvas = new Canvas(bitmap);
	    bitmap.eraseColor(0);
	    image.draw(canvas); // draw the image onto our bitmap
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
