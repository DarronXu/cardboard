package com.IYYX.cardboard.Helpers;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.IYYX.cardboard.myAPIs.MyCallback;


class TestCallback extends MyCallback {
	public InputStream openAssetInput(String assetsName) throws IOException
	{
		return new FileInputStream("./assets/"+assetsName);
	}
	int count=0;
	public void showToast3D(String textAsString)
	{
		System.out.print(textAsString);
		count++;
		if(count>100) {
			System.out.println();
			count=0;
		}
	}
	public void showToast3D(int textAsResourceID)
	{
		if(textAsResourceID==com.IYYX.cardboard.R.string.myAPI_LoadingObjFile)
			showToast3D(".");
	}
}