package com.IYYX.cardboard.myAPIs;

public class GameObject {
	public Model mPrototype;
	public Texture mTexture;
	
	public GameObjectUpdater mUpdater;
	public float[] mModelMatrix = new float[16];
	public float[] fbColorArr = new float[]{0.5f,0.5f,0.5f,0.5f};
	
	public boolean isVisible = true;
	
	public GameObject(Model prototype,GameObjectUpdater updater,Texture texture) {
		mPrototype=prototype;
		mUpdater=updater;
		mTexture=texture;
	}
	
	private GameObject setUpdater(GameObjectUpdater updater) {
		mUpdater=updater;
		return this;
	}
	
	public static GameObject createSimpleObject(Model prototype,Texture texture) {
		return new GameObject(prototype,SimpleUpdater.updater,texture);
	}
}
