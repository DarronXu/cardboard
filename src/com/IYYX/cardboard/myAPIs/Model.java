package com.IYYX.cardboard.myAPIs;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Scanner;

import com.IYYX.cardboard.R;

public class Model {
	Model() {}
	public String name = "(default)";
	public boolean hasTextureUV=false;
	public boolean hasNormal=false;
	public FloatBuffer vertices, textureUVs, normals;
	public int fCount = 0;
	
	public static final Model[] emptyObjFileArray=new Model[]{};
	public static final int mBytesPerFloat = Float.SIZE/8;
	
	public String toString(){
		return name;
	}
	
	public static Model readWholeModel(String assetsName, MyCallback callback) throws IOException {
		
		InputStream istream=callback.openAssetInput(assetsName);
		Scanner scan=new Scanner(istream);
		Model ans=new Model();
		ArrayList<Float> vertex=new ArrayList<Float>();
		ArrayList<Float> uv=new ArrayList<Float>();
		ArrayList<Float> normal=new ArrayList<Float>();
		
		ArrayList<Float> ansVertices=new ArrayList<Float>();
		ArrayList<Float> ansTextureUVs=new ArrayList<Float>();
		ArrayList<Float> ansNormals=new ArrayList<Float>();
		
		while(scan.hasNextLine()) {
			callback.showToast3D(R.string.myAPI_LoadingObjFile);
			final String line=scan.nextLine().replace("//", "/0/").replace('/', ' ');
			final String[] split=line.split(" ");
			if(split[0].equals("v")) {
				vertex.add(Float.parseFloat(split[1]));
				vertex.add(Float.parseFloat(split[2]));
				vertex.add(Float.parseFloat(split[3]));
				vertex.add(1.0f);
			}
			if(split[0].equals("vt")) {
				uv.add(Float.parseFloat(split[1]));
				uv.add(Float.parseFloat(split[2]));
			}
			if(split[0].equals("vn")) {
				normal.add(Float.parseFloat(split[1]));
				normal.add(Float.parseFloat(split[2]));
				normal.add(Float.parseFloat(split[3]));
			}
			if(split[0].equals("f")) {			//We can mix 'f' together with 'v' and 'vt', because in the actual file, for every single object, any 'v' or 'vt' always appears earliers than any 'f'.
				ans.fCount++;
				for(int i=0;i<3;i++) {
					final int vertexIndex=Integer.parseInt(split[1+i*3]) - 1;	// MINUS ONE !!!
					for(int j=vertexIndex*4;j<vertexIndex*4+4;j++)
						ansVertices.add(vertex.get(j));
					final int uvIndex=Integer.parseInt(split[2+i*3]) - 1;		// MINUS ONE !!!
					if(uvIndex>=0) {
						ans.hasTextureUV=true;
						for(int j=uvIndex*2;j<uvIndex*2+2;j++)
							ansTextureUVs.add(uv.get(j));
					}
					final int normalIndex=Integer.parseInt(split[3+i*3]) - 1;
					ans.hasNormal=true;
					for(int j=normalIndex*3;j<normalIndex*3+3;j++)
						ansNormals.add(normal.get(j));
				}
			}
		} scan.close();
		
		ans.textureUVs=ByteBuffer.allocateDirect(ans.fCount*3*2*mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		ans.vertices=ByteBuffer.allocateDirect(ans.fCount*3*4*mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		ans.normals=ByteBuffer.allocateDirect(ans.fCount*3*3*mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		ans.textureUVs.position(0);
		ans.vertices.position(0);
		ans.normals.position(0);
		ans.name=assetsName;
		
		for(int i=0;i<ans.fCount*3;i++){
			callback.showToast3D(R.string.myAPI_LoadingObjFile);
			ans.vertices.put(ansVertices.get(i*4));
			ans.vertices.put(ansVertices.get(i*4+1));
			ans.vertices.put(ansVertices.get(i*4+2));
			ans.vertices.put(ansVertices.get(i*4+3));
			if(ans.hasTextureUV) {
				ans.textureUVs.put(ansTextureUVs.get(i*2));
				ans.textureUVs.put(ansTextureUVs.get(i*2+1));
			}
			if(ans.hasNormal) {
				ans.normals.put(ansNormals.get(i*3));
				ans.normals.put(ansNormals.get(i*3+1));
				ans.normals.put(ansNormals.get(i*3+2));
			}
		}
		ans.vertices.position(0);
		ans.textureUVs.position(0);
		ans.normals.position(0);
		return ans;
	}
	public static Model[] readPartitionedModel(String assetsName, MyCallback callback) throws IOException {
		//long time=SystemClock.uptimeMillis();

		InputStream istream=callback.openAssetInput(assetsName);
		Scanner scan=new Scanner(istream);
		Model currentObj=null;
		ArrayList<Model> ans=new ArrayList<Model>();
		ArrayList<Float[]> vertex=new ArrayList<Float[]>();
		ArrayList<Float[]> uv=new ArrayList<Float[]>();
		ArrayList<Float[]> normal=new ArrayList<Float[]>();
		ArrayList<Float[]> ansVertices=new ArrayList<Float[]>();
		ArrayList<Float[]> ansTextureUVs=new ArrayList<Float[]>();
		ArrayList<Float[]> ansNormals=new ArrayList<Float[]>();
		
		while(scan.hasNextLine()) {
			callback.showToast3D(R.string.myAPI_LoadingObjFile);
			final String line=scan.nextLine().replace("//", "/0/").replace('/', ' ');
			final String[] split=line.split(" ");
			if(split[0].equals("g")) {
				currentObj=new Model();
				ans.add(currentObj);
				if(split.length>1) currentObj.name=split[1];
			}
			if(split[0].equals("v")) {
				vertex.add(new Float[]{Float.parseFloat(split[1]),
				Float.parseFloat(split[2]),
				Float.parseFloat(split[3]),
				1.0f});
			}
			if(split[0].equals("vt")) {
				uv.add(new Float[] {Float.parseFloat(split[1]),1-Float.parseFloat(split[2])});
			}
			if(split[0].equals("vn")) {
				normal.add(new Float[] {Float.parseFloat(split[1]),
						Float.parseFloat(split[2]),
						Float.parseFloat(split[3])
						});
			}
			if(split[0].equals("f")) {			//We can mix 'f' together with 'v' and 'vt', because in the actual file, for every single object, any 'v' or 'vt' always appears earliers than any 'f'.
				currentObj.fCount++;
				for(int i=0;i<3;i++) {
					final int vertexIndex=Integer.parseInt(split[1+i*3]) - 1;	// MINUS ONE !!!
					ansVertices.add(vertex.get(vertexIndex));
					final int uvIndex=Integer.parseInt(split[2+i*3]) - 1;		// MINUS ONE !!!
					if(uvIndex>=0) {
						currentObj.hasTextureUV=true;
						ansTextureUVs.add(uv.get(uvIndex));
					}
					final int normalIndex=Integer.parseInt(split[3+i*3]) - 1;
					currentObj.hasNormal=true;
					ansNormals.add(normal.get(normalIndex));
				}
			}
		} scan.close();

		int currentAnsIndex=0;
		for(int k=0;k<ans.size();k++) {
			Model cObj=ans.get(k);
			cObj.textureUVs=ByteBuffer.allocateDirect(cObj.fCount*3*2*mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
			cObj.vertices=ByteBuffer.allocateDirect(cObj.fCount*3*4*mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
			cObj.normals=ByteBuffer.allocateDirect(cObj.fCount*3*3*mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
			cObj.textureUVs.position(0);
			cObj.vertices.position(0);
			cObj.normals.position(0);
			final int tmp=currentAnsIndex;
			for(;currentAnsIndex<tmp+cObj.fCount*3;currentAnsIndex++){
				callback.showToast3D(R.string.myAPI_LoadingObjFile);
				putAll(cObj.vertices,ansVertices.get(currentAnsIndex));
				if(cObj.hasTextureUV) putAll(cObj.textureUVs,ansTextureUVs.get(currentAnsIndex));
				if(cObj.hasNormal) putAll(cObj.normals,ansNormals.get(currentAnsIndex));
			}
			cObj.vertices.position(0);
			cObj.textureUVs.position(0);
			cObj.normals.position(0);
		}
		return ans.toArray(emptyObjFileArray);
	}
	private static void putAll(FloatBuffer dest, Float[] vals) {
		for(int i=0;i<vals.length;i++)
			dest.put(vals[i]);
	}
}
