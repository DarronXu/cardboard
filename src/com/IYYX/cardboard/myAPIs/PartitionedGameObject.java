package com.IYYX.cardboard.myAPIs;

import java.util.HashMap;

import android.content.res.Resources;
import android.util.Log;

/**
 * Prerequisite: For every element in the GameObject[] passed in, its mPrototype.name corresponds with the resource name of the texture that should be attached to it.
 * @author c4phone
 */
public class PartitionedGameObject {
	public static HashMap<String,Texture> database=new HashMap<String,Texture>();
	public GameObject[] mPartitionedObject;
	Resources res;
	
	public PartitionedGameObject(Model[] modelForEachPart, GameObjectUpdater commonUpdater, Resources res, String packageName, boolean zoomTextureForBetterPerformance) {
		this.res=res;
		int howManyParts=modelForEachPart.length;
		mPartitionedObject = new GameObject[howManyParts];
		for(int i=0;i<howManyParts;i++) {
			GameObject part = mPartitionedObject[i] = new GameObject(modelForEachPart[i], commonUpdater, null);
			String name=part.mPrototype.name;
			if(database.containsKey(name))
				part.mTexture=database.get(name);
			else {
				int ID=res.getIdentifier(name, "drawable", packageName);
				Log.d("PartitionedGameObject","ID="+ID);
				Texture texture = new Texture(res, ID, zoomTextureForBetterPerformance);
				database.put(name, texture);
				part.mTexture=texture;
			}
		}
	}
	
	public PartitionedGameObject(Model[] modelForEachPart, GameObjectUpdater[] updaterForEachPart, Resources res, String packageName, boolean zoomTextureForBetterPerformance) {
		this.res=res;
		int howManyParts=modelForEachPart.length;
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
