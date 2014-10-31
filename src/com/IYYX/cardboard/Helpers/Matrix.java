package com.IYYX.cardboard.Helpers;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

final class Matrix {
	
	//static void frustumM(float[] m, int offset, float left, float right, float bottom, float top, float near, float far)
	//static boolean invertM(float[] mInv, int mInvOffset, float[] m, int mOffset)
	//static float length(float x, float y, float z)
	//static void multiplyMV(float[] resultVec, int resultVecOffset, float[] lhsMat, int lhsMatOffset, float[] rhsVec, int rhsVecOffset)
	//static void orthoM(float[] m, int mOffset, float left, float right, float bottom, float top, float near, float far)
	//static void setRotateEulerM(float[] rm, int rmOffset, float x, float y, float z)
	//static void transposeM(float[] mTrans, int mTransOffset, float[] m, int mOffset)
	
	static void multiplyMM(float[] result, int resultOffset, float[] lhs, int lhsOffset, float[] rhs, int rhsOffset) {
		GL2 gl=GLU.getCurrentGL().getGL2();
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glLoadMatrixf(lhs, lhsOffset);
		gl.glMultMatrixf(rhs, rhsOffset);
		gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, result, resultOffset);
		gl.glPopMatrix();
	}
	static void perspectiveM(float[] m, int offset, float fovy, float aspect, float zNear, float zFar) {
		GL2 gl=GLU.getCurrentGL().getGL2();
		GLU glu=GLU.createGLU(gl);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		glu.gluPerspective(fovy, aspect, zNear, zFar);
		gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, m, offset);
		gl.glPopMatrix();
	}
	static void rotateM(float[] m, int mOffset, float a, float x, float y, float z) {
		GL2 gl=GLU.getCurrentGL().getGL2();
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glLoadMatrixf(m, mOffset);
		gl.glRotatef(a, x, y, z);
		gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, m, mOffset);
		gl.glPopMatrix();
	}
	static void rotateM(float[] rm, int rmOffset, float[] m, int mOffset, float a, float x, float y, float z) {
		GL2 gl=GLU.getCurrentGL().getGL2();
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glLoadMatrixf(m, mOffset);
		gl.glRotatef(a, x, y, z);
		gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, rm, rmOffset);
		gl.glPopMatrix();
	}
	static void scaleM(float[] sm, int smOffset, float[] m, int mOffset, float x, float y, float z) {
		GL2 gl=GLU.getCurrentGL().getGL2();
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glLoadMatrixf(m, mOffset);
		gl.glScalef(x, y, z);
		gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, sm, smOffset);
		gl.glPopMatrix();
	}
	static void scaleM(float[] m, int mOffset, float x, float y, float z) {
		GL2 gl=GLU.getCurrentGL().getGL2();
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glLoadMatrixf(m, mOffset);
		gl.glScalef(x, y, z);
		gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, m, mOffset);
		gl.glPopMatrix();
	}
	static void setIdentityM(float[] sm, int smOffset) {
		GL2 gl=GLU.getCurrentGL().getGL2();
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, sm, smOffset);
		gl.glPopMatrix();
	}
	static void setLookAtM(float[] rm, int rmOffset, float eyeX, float eyeY, float eyeZ, float centerX, float centerY, float centerZ, float upX, float upY, float upZ) {
		GL2 gl=GLU.getCurrentGL().getGL2();
		GLU glu=GLU.createGLU(gl);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		glu.gluLookAt(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
		gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, rm, rmOffset);
		gl.glPopMatrix();
	}
	static void setRotateM(float[] rm, int rmOffset, float a, float x, float y, float z) {
		GL2 gl=GLU.getCurrentGL().getGL2();
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glRotatef(a, x, y, z);
		gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, rm, rmOffset);
		gl.glPopMatrix();
	}
	static void translateM(float[] m, int mOffset, float x, float y, float z) {
		GL2 gl=GLU.getCurrentGL().getGL2();
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glLoadMatrixf(m, mOffset);
		gl.glTranslatef(x, y, z);
		gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, m, mOffset);
		gl.glPopMatrix();
	}
	static void translateM(float[] tm, int tmOffset, float[] m, int mOffset, float x, float y, float z) {
		GL2 gl=GLU.getCurrentGL().getGL2();
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glLoadMatrixf(m, mOffset);
		gl.glTranslatef(x, y, z);
		gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, tm, tmOffset);
		gl.glPopMatrix();
	}
}