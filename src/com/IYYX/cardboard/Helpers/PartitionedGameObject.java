package com.IYYX.cardboard.Helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import javax.media.opengl.GL2;

import com.IYYX.cardboard.myAPIs.*;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * Prerequisite: For every element in the GameObject[] passed in, its mPrototype.name corresponds with the resource name of the texture that should be attached to it.
 * @author c4phone
 */
public class PartitionedGameObject {
	public static HashMap<String,Texture> database=new HashMap<String,Texture>();
	public GameObject[] mPartitionedObject;
	
	public PartitionedGameObject(Model[] modelForEachPart, GameObjectUpdater commonUpdater, GL2 gl) {
		final int howManyParts=modelForEachPart.length;
		mPartitionedObject = new GameObject[howManyParts];
		for(int i=0;i<howManyParts;i++) {
			GameObject part = mPartitionedObject[i] = new GameObject(modelForEachPart[i], commonUpdater, null);
			String name=part.mPrototype.name;
			if(database.containsKey(name))
				part.mTexture=database.get(name);
			else {
				try{
				Texture texture = getTexture(getTexFilename("TextureInfo/"+name+".info"),gl);
				database.put(name, texture);
				part.mTexture=texture;
				} catch (Exception err) {
					err.printStackTrace();
				}
			}
		}
	}

	private String getTexFilename(String TexInfoFile) throws IOException {
		InputStream texinfo;
		texinfo=new FileInputStream("./assets/"+TexInfoFile);
        BufferedReader reader = new BufferedReader(new InputStreamReader(texinfo));
        String sInfo=reader.readLine();			//The first line contains the path of this texture
        reader.close();
        return "./assets/"+sInfo;
	}

	/*public PartitionedGameObject(Model[] modelForEachPart, GameObjectUpdater[] updaterForEachPart, String packageName, boolean zoomTextureForBetterPerformance) {
		final int howManyParts=modelForEachPart.length;
		mPartitionedObject = new GameObject[howManyParts];
		for(int i=0;i<howManyParts;i++) {
			GameObject part = mPartitionedObject[i] = new GameObject(modelForEachPart[i], updaterForEachPart[i], null);
			String name=part.mPrototype.name;
			if(database.containsKey(name))
				part.mTexture=database.get(name);
			else {
				int ID=res.getIdentifier(name, "drawable", packageName);
				Texture texture = new Texture(res, ID, zoomTextureForBetterPerformance);
				database.put(name, texture);
				part.mTexture=texture;
			}
		}
	}*/
	
	private static Texture getTexture(String filename, GL2 gl) throws IOException{
        TextureData data = TextureIO.newTextureData(gl.getGLProfile(), new File(filename), false, null);
		gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
		gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
        return new Texture(TextureIO.newTexture(data),gl);
	}
	
	public void addToGLProgram(GLProgram program) {
		for(GameObject obj:mPartitionedObject) {
			program.addGameObject(obj);
		}
	}
	
	public void setVisible(boolean visible) {
		for(GameObject obj:mPartitionedObject) {
			obj.isVisible=visible;
		}
	}
}
