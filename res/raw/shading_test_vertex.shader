uniform mat4 u_ModelMatrix;
uniform mat4 u_ViewMatrix;
uniform mat4 u_ProjectionMatrix;
attribute vec4 a_Position;
attribute vec2 a_UV;
attribute vec3 a_Normal;

varying vec2 v_UV;
varying vec3 v_Normal;
varying vec4 v_Position_CameraSpace;

varying vec3 Position_worldspace;
varying vec3 Normal_cameraspace;
varying vec3 EyeDirection_cameraspace;

void main()
{
	mat4 u_MVMatrix=u_ViewMatrix*u_ModelMatrix;
	mat4 u_MVPMatrix=u_ProjectionMatrix*u_MVMatrix;
	v_UV = a_UV;
	v_Normal = a_Normal;
	gl_Position = u_MVPMatrix* a_Position;
	v_Position_CameraSpace = u_MVMatrix*a_Position;
	
	
	// Position of the vertex, in worldspace
	Position_worldspace = (u_ModelMatrix * a_Position).xyz;
	
	// Vector that goes from the vertex to the camera, in camera space.
	// In camera space, the camera is at the origin (0,0,0).
	vec3 vertexPosition_cameraspace = v_Position_CameraSpace.xyz;
	EyeDirection_cameraspace = vec3(0,0,0) - vertexPosition_cameraspace;
	
	// Normal of the the vertex, in camera space
	Normal_cameraspace = ( u_ViewMatrix * u_ModelMatrix * vec4(a_Normal,0)).xyz; // Only correct if ModelMatrix does not scale the model ! Use its inverse transpose if not.
	
}