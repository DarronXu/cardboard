package com.IYYX.cardboard.myAPIs;

public class GameObject {
	public Model mPrototype;
	public Texture mTexture;
	
	public GameObjectUpdater mUpdater;
	public float[] mModelMatrix = new float[16];
	
	public boolean isVisible = true;
	
	public GameObject(Model prototype,GameObjectUpdater updater,Texture texture) {
		mPrototype=prototype;
		mUpdater=updater;
		mTexture=texture;
	}
}
