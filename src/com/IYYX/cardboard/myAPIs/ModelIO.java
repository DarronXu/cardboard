package com.IYYX.cardboard.myAPIs;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.IYYX.cardboard.R;
import com.IYYX.cardboard.myAPIs.Model;

public class ModelIO {
	
	//Format detials:
	//(total number of Model obejcts)+' '+
	//for(each Model object) {
	//	name
	//	fcount
	//	vertexes						[capacity+' '+content]
	//	hasTextureUV
	//	if(hasTextureUV) textureUVs		[capacity+' '+content]
	//	hasNormal
	//	if(hasNormal) normals			[capacity+' '+content]
	//}
	
	public static Model loadWhole(InputStream stream, MyCallback callback) throws IOException, ClassNotFoundException {
		callback.showToast3D(R.string.myAPI_LoadingObjFile);
		ObjectInputStream istream=new ObjectInputStream(stream);
		int total=istream.readInt();
		if(total!=1) throw new RuntimeException("ModelIO: Invalid Operation: read a partitionedModel as whole Model. This can only be done through Model.readXXX, not through ModelIO.XXX");
		Model ans=subLoad(istream);
		istream.close();
		stream.close();
		return ans;
	};
	public static Model[] loadPartitioned(InputStream stream, MyCallback callback) throws IOException, ClassNotFoundException {
		ObjectInputStream istream=new ObjectInputStream(stream);
		int total=istream.readInt();
		Model[] ans=new Model[total];
		for(int i=0;i<total;i++){
			callback.showToast3D(R.string.myAPI_LoadingObjFile);
			ans[i]=subLoad(istream);
		}
		istream.close();
		stream.close();
		return ans;
	}
	public static void save(Model wholeModel, OutputStream stream) throws IOException {
		ObjectOutputStream ostream=new ObjectOutputStream(stream);
		ostream.writeInt(1);
		subSave(wholeModel,ostream);
		ostream.close();
		stream.close();
	}
	public static void save(Model[] partitionedModel, OutputStream stream) throws IOException  {
		ObjectOutputStream ostream=new ObjectOutputStream(stream);
		ostream.writeInt(partitionedModel.length);
		for(int i=0;i<partitionedModel.length;i++)
			subSave(partitionedModel[i],ostream);
		ostream.close();
		stream.close();
	}
	static void subSave(Model x,ObjectOutputStream ostream) throws IOException {
		float[] tmp;
		
		ostream.writeObject(x.name);
		ostream.writeInt(x.fCount);
		
		tmp=new float[x.vertices.capacity()];
		x.vertices.get(tmp);
		ostream.writeInt(x.vertices.capacity());
		ostream.writeObject(tmp);
		
		ostream.writeBoolean(x.hasTextureUV); if(x.hasTextureUV) {
			tmp=new float[x.textureUVs.capacity()];
			x.textureUVs.get(tmp);
			ostream.writeInt(x.textureUVs.capacity());
			ostream.writeObject(tmp);
		}

		ostream.writeBoolean(x.hasNormal); if(x.hasNormal) {
			tmp=new float[x.normals.capacity()];
			x.normals.get(tmp);
			ostream.writeInt(x.normals.capacity());
			ostream.writeObject(tmp);
		}
	}
	static Model subLoad(ObjectInputStream istream) throws IOException, ClassNotFoundException {
		Model ans=new Model();
		ans.name=(String)istream.readObject();
		ans.fCount=istream.readInt();
		
		ans.vertices=ByteBuffer.allocateDirect(istream.readInt()*Model.mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
		ans.vertices.position(0);
		ans.vertices.put((float[])istream.readObject());
		ans.vertices.position(0);
		
		if(ans.hasTextureUV=istream.readBoolean()) {
			ans.textureUVs=ByteBuffer.allocateDirect(istream.readInt()*Model.mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
			ans.textureUVs.position(0);
			ans.textureUVs.put((float[])istream.readObject());
			ans.textureUVs.position(0);
		}

		if(ans.hasNormal=istream.readBoolean()) {
			ans.normals=ByteBuffer.allocateDirect(istream.readInt()*Model.mBytesPerFloat).order(ByteOrder.nativeOrder()).asFloatBuffer();
			ans.normals.position(0);
			ans.normals.put((float[])istream.readObject());
			ans.normals.position(0);
		}
		return ans;
	}
}
