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

	public GameObject(Model prototype,Texture texture) {
		mPrototype=prototype;
		mUpdater=null;
		mTexture=texture;
	}
	
	public void addToGLProgram(GLProgram program) {
		program.objects.add(this);
	}
}
