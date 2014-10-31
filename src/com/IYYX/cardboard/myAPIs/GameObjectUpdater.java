package com.IYYX.cardboard.myAPIs;

/**
 * @author c4phone
 * This interface is called by 'GLProgram' class. 
 * In this interface, you should update attributes such as the ModelMatrix, which is used to place this GameObject in the game space.
 */
public interface GameObjectUpdater {
	public void update(GameObject object);
}
