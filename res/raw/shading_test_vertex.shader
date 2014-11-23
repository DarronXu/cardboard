uniform mat4 u_ModelMatrix;
uniform mat4 u_ViewMatrix;
uniform mat4 u_ProjectionMatrix;
attribute vec4 a_Position;
attribute vec2 a_UV;
attribute vec3 a_Normal;

varying vec2 v_UV;
varying vec3 v_Normal;
varying vec4 v_Position;

void main()
{
	mat4 u_MVMatrix=u_ViewMatrix*u_ModelMatrix;
	mat4 u_MVPMatrix=u_ProjectionMatrix*u_MVMatrix;
	v_UV = a_UV;
	v_Normal = a_Normal;
	gl_Position = u_MVPMatrix* a_Position;
	v_Position = a_Position;
}