package com.IYYX.cardboard.myAPIs;

/**
 * This is a simplified version of GameObjectUpdater, <br/>
 * It simply performs one transform on the GameObject using its mModelMatrix. <br/>
 * Now programmers can <b>set the GameObject's properties through its mModelMatrix elsewhere</b>, <br/>
 * and avoid repeatedly writing similar codes.<br/><br/>
 * 
 * <b>Note that:<br/>
 * The GameObject should be created by GameObject.createSimpleObject()</b>,<br/>
 * if one wants to use SimpleUpdater on it.
 */
public class SimpleUpdater implements GameObjectUpdater {
	public static SimpleUpdater updater=new SimpleUpdater();
	public void update(GameObject obj) {
	}
}