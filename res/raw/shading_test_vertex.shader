uniform mat4 u_MVPMatrix;
attribute vec4 a_Position;
attribute vec2 a_UV;
attribute vec3 a_Normal;

varying vec2 v_UV;
varying vec3 v_Normal;

void main()
{
	v_UV = a_UV;
	v_Normal = a_Normal;
	gl_Position = u_MVPMatrix* a_Position;
}