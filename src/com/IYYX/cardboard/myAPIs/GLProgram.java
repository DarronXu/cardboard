package com.IYYX.cardboard.myAPIs;

import java.util.ArrayList;

public abstract class GLProgram {

	public abstract void loadIntoGLES();
	
	ArrayList<GameObject> objects = new ArrayList<GameObject>();
	public void addGameObject(GameObject object) {
		objects.add(object);
	}
	public void updateAllGameObjects() {
		for(GameObject obj:objects)
			obj.mUpdater.update(obj);
	}
	public abstract void renderAllGameObjects();
}
