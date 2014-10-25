uniform mat4 u_MVPMatrix;
attribute vec4 a_Position;
attribute vec2 a_UV;

varying vec2 v_UV;

void main()
{
	v_UV = a_UV;
	gl_Position = u_MVPMatrix* a_Position;
}