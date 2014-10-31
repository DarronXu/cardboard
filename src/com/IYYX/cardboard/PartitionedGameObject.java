package com.IYYX.cardboard;

import com.IYYX.cardboard.myAPIs.Texture;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import com.IYYX.cardboard.myAPIs.*;

import android.content.res.AssetManager;
import android.content.res.Resources;

/**
 * Prerequisite: For every element in the GameObject[] passed in, its mPrototype.name corresponds with the resource name of the texture that should be attached to it.
 * @author c4phone
 */
class PartitionedGameObject {
	public static HashMap<String,Texture> database=new HashMap<String,Texture>();
	public GameObject[] mPartitionedObject;
	Resources res;
	AssetManager mAssets;
	
	public PartitionedGameObject(Model[] modelForEachPart, GameObjectUpdater commonUpdater, Resources res, String packageName, boolean zoomTextureForBetterPerformance) {
		this.res=res;
		mAssets=res.getAssets();
		final int howManyParts=modelForEachPart.length;
		mPartitionedObject = new GameObject[howManyParts];
		for(int i=0;i<howManyParts;i++) {
			GameObject part = mPartitionedObject[i] = new GameObject(modelForEachPart[i], commonUpdater, null);
			String name=part.mPrototype.name;
			if(database.containsKey(name))
				part.mTexture=database.get(name);
			else {
				try {
				Texture texture = new Texture(getInputStream("TextureInfo/"+name+".info",mAssets));
				database.put(name, texture);
				part.mTexture=texture;
				} catch (Exception err) {
					err.printStackTrace();
				}
			}
		}
	}
	
	private InputStream getInputStream(String TexInfoFile, AssetManager assets) throws IOException {
		InputStream texinfo;
		texinfo=assets.open(TexInfoFile);
        BufferedReader reader = new BufferedReader(new InputStreamReader(texinfo));
        String sInfo=reader.readLine();			//The first line contains the path of this texture
        System.err.println(sInfo);
        reader.close();
        return assets.open(sInfo);
	}
	
	public PartitionedGameObject(Model[] modelForEachPart, GameObjectUpdater[] updaterForEachPart, Resources res, String packageName, boolean zoomTextureForBetterPerformance) {
		this.res=res;
		this.mAssets=res.getAssets();
		final int howManyParts=modelForEachPart.length;
		mPartitionedObject = new GameObject[howManyParts];
		for(int i=0;i<howManyParts;i++) {
			GameObject part = mPartitionedObject[i] = new GameObject(modelForEachPart[i], updaterForEachPart[i], null);
			String name=part.mPrototype.name;
			if(database.containsKey(name))
				part.mTexture=database.get(name);
			else {
				try {
				Texture texture = new Texture(getInputStream("TextureInfo/"+name+".info",mAssets));
				database.put(name, texture);
				part.mTexture=texture;
				} catch (Exception err) {
					err.printStackTrace();
				}
			}
		}
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
