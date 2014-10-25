varying vec2 v_UV;
uniform sampler2D myTextureSampler;
void main()
{
	float x=v_UV[0];
	float y=v_UV[1];
	vec2 realUV=vec2(x,1.0-y);
	vec4 ansColor = texture2D(myTextureSampler,realUV).rgba;
	if(ansColor[3] == 0.0)
	{
		discard;
	}
	gl_FragColor = ansColor;
}