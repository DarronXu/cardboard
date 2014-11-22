varying vec2 v_UV;
varying vec3 v_Normal;
varying vec4 v_Position_CameraSpace;

uniform sampler2D myTextureSampler;
uniform mat4 u_ModelMatrix;
uniform mat4 u_ViewMatrix;

uniform vec4 u_sunLights_worldSpace[6];
uniform vec4 u_bulbLights_worldSpace[6];
uniform vec3 u_AmbiantColor;

void main()
{
	float x=v_UV[0];
	float y=v_UV[1];
	
	vec2 realUV=vec2(x,1.0-y);
	vec4 materialColor = texture2D(myTextureSampler,realUV).rgba;
	if(materialColor[3] == 0.0)
	{
		discard;
	}
	vec4 color=vec4(0,0,0,0);
	
	//mat4 MVMatrix=transpose(u_ModelMatrix);
	mat4 MVMatrix=u_ModelMatrix;
	MVMatrix=u_ViewMatrix*MVMatrix;
	vec4 normal=normalize(MVMatrix*vec4(v_Normal,0));
	float totIntensity=0.0;
	
	for(int i=0;i<6;i++)
	{
		/*
			// Vector that goes from the vertex to the light, in camera space. u_ModelMatrix is ommited because it's identity.
			vec3 LightPosition_cameraspace = ( u_ViewMatrix * vec4(LightPosition_worldspace,1)).xyz;
			LightDirection_cameraspace = LightPosition_cameraspace + EyeDirection_cameraspace;
		*/
		vec4 uSun_world=u_sunLights_worldSpace[i];
		vec4 uBulb_world=u_bulbLights_worldSpace[i];
		vec4 sunDirection_cameraSpace;
		vec4 bulbLocation_cameraSpace;
		vec4 bulbDirection_cameraSpace;
		float sunIntensity=uSun_world[3];
		float bulbIntensity=uBulb_world[3];
		uSun_world[3]=0.0;
		uBulb_world[3]=1.0;
		bulbLocation_cameraSpace=u_ViewMatrix*u_bulbLights_worldSpace[i];
		sunDirection_cameraSpace=normalize(u_ViewMatrix*u_sunLights_worldSpace[i]);
		bulbDirection_cameraSpace=normalize(v_Position_CameraSpace-bulbLocation_cameraSpace);
		float sunCosTheta=clamp(dot(normal,sunDirection_cameraSpace),0,1);
		float bulbCosTheta=clamp(dot(normal,bulbDirection_cameraSpace),0,1);
		color+=materialColor*sunCosTheta*sunIntensity;
		color+=materialColor*bulbCosTheta*bulbIntensity;
		totIntensity+=sunIntensity+bulbIntensity;
	}
	if(totIntensity != 0.0) color = color*(1.0/totIntensity);
	color[3] = 1.0;
	gl_FragColor = color+vec4(u_AmbiantColor[0]*materialColor[0],u_AmbiantColor[1]*materialColor[1],u_AmbiantColor[2]*materialColor[2],0);
}