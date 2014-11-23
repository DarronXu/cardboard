varying vec2 v_UV;
varying vec3 v_Normal;
varying vec4 v_Position;

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
	vec3 normal=(MVMatrix*vec4(v_Normal,0)).xyz;
	
	float totIntensity=0.0;
	
	for(int i=0;i<6;i++)
	{	
		vec4 uSun_world=u_sunLights_worldSpace[i];
		vec4 uBulb_world=u_bulbLights_worldSpace[i];
		float bulbDistance=length(uBulb_world.xyz-(u_ModelMatrix*v_Position).xyz);
		
		float sunIntensity=uSun_world[3];
		float bulbIntensity=uBulb_world[3];
		uSun_world[3]=0.0;
		uBulb_world[3]=1.0;
		
		//vec3 sunDirection_cameraSpace;
		vec3 bulbLocation_cameraSpace;
		vec3 bulbDirection_cameraSpace;
		
		vec3 Position_CameraSpace = (MVMatrix*v_Position).xyz;
		vec3 EyeDirection_CameraSpace = vec3(0,0,0)-Position_CameraSpace.xyz;
		bulbLocation_cameraSpace=(u_ViewMatrix*uBulb_world).xyz;
		bulbDirection_cameraSpace=bulbLocation_cameraSpace+EyeDirection_CameraSpace;
		
		
		vec3 n=normalize(normal);
		vec3 l=normalize(bulbDirection_cameraSpace);
		
		float bulbCosTheta=clamp(dot(n,l),0,1);
		//color+=materialColor*sunCosTheta*sunIntensity;
		color+=materialColor*bulbCosTheta*bulbIntensity/bulbDistance/bulbDistance;
		totIntensity+=/*sunIntensity+*/bulbIntensity;
	}
	//if(totIntensity != 0.0) color = color*(1.0/totIntensity);
	color = color+vec4(u_AmbiantColor[0]*materialColor[0],u_AmbiantColor[1]*materialColor[1],u_AmbiantColor[2]*materialColor[2],0);
	color[3] = 1.0;
	gl_FragColor = color; 
}