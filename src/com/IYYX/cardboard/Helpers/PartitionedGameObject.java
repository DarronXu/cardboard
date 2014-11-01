package com.IYYX.cardboard.Helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.HashMap;

import javax.media.opengl.GL2;

import com.IYYX.cardboard.myAPIs.*;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * Prerequisite: For every element in the GameObject[] passed in, its mPrototype.name corresponds with the resource name of the texture that should be attached to it.
 * @author c4phone
 */
class PartitionedGameObject {
	public GameObject[] mPartitionedObject;
	
	public PartitionedGameObject(Model[] modelForEachPart, String textureInfoFilename, GameObjectUpdater commonUpdater, GL2 gl) {
		final int howManyParts=modelForEachPart.length;
		try {
			HashMap<String,String> nameToTex=loadTexInfo("TextureInfo/"+textureInfoFilename);
			mPartitionedObject = new GameObject[howManyParts];
			for(int i=0;i<howManyParts;i++) {
				GameObject part = mPartitionedObject[i] = new GameObject(modelForEachPart[i], commonUpdater, null);
				String name=part.mPrototype.name;
				part.mTexture=getTexture("./assets/"+nameToTex.get(name),gl);
			}
		} catch (IOException|ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot create PartitionedGameObject!");
		}
	}

	public PartitionedGameObject(Model[] modelForEachPart, String textureInfoFilename, GameObjectUpdater[] commonUpdaters, GL2 gl) {
		final int howManyParts=modelForEachPart.length;
		try {
			HashMap<String,String> nameToTex=loadTexInfo("TextureInfo/"+textureInfoFilename);
			mPartitionedObject = new GameObject[howManyParts];
			for(int i=0;i<howManyParts;i++) {
				GameObject part = mPartitionedObject[i] = new GameObject(modelForEachPart[i], commonUpdaters[i], null);
				String name=part.mPrototype.name;
				part.mTexture=getTexture("./assets/"+nameToTex.get(name),gl);
			}
		} catch (IOException|ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot create PartitionedGameObject!");
		}
	}

	private static HashMap<String,String> loadTexInfo(String TexInfoFile) throws IOException, ClassNotFoundException {
		InputStream texinfo=new FileInputStream("./assets/"+TexInfoFile);
		HashMap<String,String> ans;
        ObjectInputStream reader=new ObjectInputStream(texinfo);
        ans=(HashMap<String,String>)reader.readObject();
		reader.close();
		return ans;
	}
	
	private String getTexFilename(String TexInfoFile) throws IOException {
		InputStream texinfo;
		texinfo=new FileInputStream("./assets/"+TexInfoFile);
        BufferedReader reader = new BufferedReader(new InputStreamReader(texinfo));
        String sInfo=reader.readLine();			//The first line contains the path of this texture
        reader.close();
        return "./assets/"+sInfo;
	}
	
	private static Texture getTexture(String filename, GL2 gl) throws IOException{
        TextureData data = TextureIO.newTextureData(gl.getGLProfile(), new File(filename), false, null);
		gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
		gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
        return new Texture(TextureIO.newTexture(data),gl);
	}
	
	public void addToGLProgram(GLProgram program) {
		for(GameObject obj:mPartitionedObject) {
			program.objects.add(obj);
		}
	}
	
	public void setVisible(boolean visible) {
		for(GameObject obj:mPartitionedObject) {
			obj.isVisible=visible;
		}
	}
}
