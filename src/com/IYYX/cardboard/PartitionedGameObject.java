package com.IYYX.cardboard;

import com.IYYX.cardboard.Texture;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;

import com.IYYX.cardboard.myAPIs.*;

/**
 * Prerequisite: For every element in the GameObject[] passed in, its mPrototype.name corresponds with the resource name of the texture that should be attached to it.
 * @author c4phone
 */
class PartitionedGameObject {
	public GameObject[] mPartitionedObject;
	static HashMap<String,com.IYYX.cardboard.myAPIs.Texture> openedTextures=new HashMap<String,com.IYYX.cardboard.myAPIs.Texture>();
	MyCallback helper;
	
	public static void resetOpenedTextures() {
		openedTextures=new HashMap<String,com.IYYX.cardboard.myAPIs.Texture>();
	}
	
	public PartitionedGameObject(Model[] modelForEachPart, String textureInfoFilename, GameObjectUpdater commonUpdater, MyCallback callback) {
		helper=callback;
		final int howManyParts=modelForEachPart.length;
		try {
			HashMap<String,String> nameToTex=loadTexInfo(textureInfoFilename);
			mPartitionedObject = new GameObject[howManyParts];
			for(int i=0;i<howManyParts;i++) {
				callback.showToast3D(R.string.myAPI_LoadingObjFile);
				GameObject part = mPartitionedObject[i] = new GameObject(modelForEachPart[i], commonUpdater, null);
				String name=nameToTex.get(part.mPrototype.name);
				if(openedTextures.containsKey(name))
					part.mTexture=openedTextures.get(name);
				else openedTextures.put(name, part.mTexture=new Texture(helper.openAssetInput(name)));
			}
		} catch (IOException|ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot create PartitionedGameObject!");
		}
	}

	public PartitionedGameObject(Model[] modelForEachPart, String textureInfoFilename, GameObjectUpdater[] commonUpdaters, MyCallback callback) {
		helper=callback;
		final int howManyParts=modelForEachPart.length;
		try {
			HashMap<String,String> nameToTex=loadTexInfo(textureInfoFilename);
			mPartitionedObject = new GameObject[howManyParts];
			for(int i=0;i<howManyParts;i++) {
				callback.showToast3D(R.string.myAPI_LoadingObjFile);
				GameObject part = mPartitionedObject[i] = new GameObject(modelForEachPart[i], commonUpdaters[i], null);
				String name=nameToTex.get(part.mPrototype.name);
				if(openedTextures.containsKey(name))
					part.mTexture=openedTextures.get(name);
				else openedTextures.put(name, part.mTexture=new Texture(helper.openAssetInput(name)));
			}
		} catch (IOException|ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot create PartitionedGameObject!");
		}
	}

	private HashMap<String,String> loadTexInfo(String TexInfoFile) throws IOException, ClassNotFoundException {
		InputStream texinfo=helper.openAssetInput("TextureInfo/"+TexInfoFile);
		HashMap<String,String> ans;
        ObjectInputStream reader=new ObjectInputStream(texinfo);
        ans=(HashMap<String,String>)reader.readObject();
		reader.close();
		return ans;
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
