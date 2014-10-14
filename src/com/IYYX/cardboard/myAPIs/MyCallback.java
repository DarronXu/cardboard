package com.IYYX.cardboard.myAPIs;

import java.io.IOException;
import java.io.InputStream;

public abstract class MyCallback {
	public InputStream openAssetInput(String assetsName) throws IOException {return null;}
	public void showToast3D(String textAsString) {}
	public void showToast3D(int textAsResourceID) {}
}