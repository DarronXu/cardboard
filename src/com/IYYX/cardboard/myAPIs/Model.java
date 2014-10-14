package com.IYYX.cardboard.myAPIs;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Scanner;

import android.os.SystemClock;
import android.util.Log;

import com.IYYX.cardboard.R;

public class Model {
	private Model() {}
	public String name = "(default)";
	public boolean hasTextureUV=false;
	public FloatBuffer vertices, textureUVs, colors;
	public int mColorDataSize, vCount = 0, fCount = 0;
	
	public static final Model[] emptyObjFileArray=new Model[]{};
	public static final int mBytesPerFloat = Float.SIZE/8;
	
	public static Model readObjAsWholeModel(String assetsName, int colorDataSize, MyCallback callback) throws IOException {
		long time=SystemClock.uptimeMillis();
		
		InputStream istream=callback.openAssetInput(assetsName);
		Scanner scan=new Scanner(istream);
		Model ans=new Model();
		ArrayList<Float> vertex=new ArrayList<Float>();
		ArrayList<Float> uv=new ArrayList<Float>();
		ArrayList<Float> ansVertices=new ArrayList<Float>();
		ArrayList<Float> ansTextureUVs=new ArrayList<Float>();
		
		while(scan.hasNextLine()) {
			callback.showToast3D(R.string.myAPI_LoadingObjFile);
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
			callback.showToast3D(R.string.myAPI_LoadingObjFile);
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
	public static Model[] readObjFileAsModelArr(String assetsName, int colorDataSize, MyCallback callback) throws IOException {
		long time=SystemClock.uptimeMillis();

		InputStream istream=callback.openAssetInput(assetsName);
		Scanner scan=new Scanner(istream);
		Model currentObj=null;
		ArrayList<Model> ans=new ArrayList<Model>();
		ArrayList<Float> vertex=new ArrayList<Float>();
		ArrayList<Float> uv=new ArrayList<Float>();
		ArrayList<Float> ansVertices=new ArrayList<Float>();
		ArrayList<Float> ansTextureUVs=new ArrayList<Float>();
		int currentAnsIndex=0;
		
		while(scan.hasNextLine()) {
			callback.showToast3D(R.string.myAPI_LoadingObjFile);
			final String line=scan.nextLine().replace("//", "/0/").replace('/', ' ');
			final String[] split=line.split(" ");
			if(split[0].equals("o")) {
				currentObj=new Model();
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
			Model cObj=ans.get(k);
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
				callback.showToast3D(R.string.myAPI_LoadingObjFile);
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
	
}