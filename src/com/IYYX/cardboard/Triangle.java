package com.IYYX.cardboard;

import java.nio.*;

public class Triangle {
	FloatBuffer vertexBuffer;
	static final int COORDS_PER_VERTEX = 3;
	static float triangleCoords[] = { //in counterclockwise order:
		0.0f, 0.622008459f, 0.0f, // top
		-0.5f, -0.311004243f, 0.0f, //bottom left
		0.5f, -0.311004243f, 0.0f
	};
	float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };
	public Triangle(){
		// initialize vertex byte buffer for shape coordinates
		ByteBuffer bb=ByteBuffer.allocateDirect(
			// (number of coordinate values * 4 bytes per float)
			triangleCoords.length*4);
		bb.order(ByteOrder.nativeOrder());
		vertexBuffer = bb.asFloatBuffer();
		vertexBuffer.put(triangleCoords);
		vertexBuffer.position(0);
	}
}
