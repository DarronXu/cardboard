package com.IYYX.cardboard.myAPIs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public abstract class GLProgram {

	public abstract void loadIntoGLES();
	
	public ArrayList<GameObject> objects = new ArrayList<GameObject>();
	
	public void updateAllGameObjects() {
		for(GameObject obj:objects) if(obj.mUpdater!=null)
			obj.mUpdater.update(obj);
	}
	public abstract void renderAllGameObjects();
	

    protected String readRawTextFile(InputStream inputStream) {
        StringBuilder ans = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null)
                ans.append(line).append("\n");
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ans.toString();
    }
}
