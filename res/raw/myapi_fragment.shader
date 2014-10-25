varying vec2 v_UV;
uniform sampler2D myTextureSampler;
void main()
{
	vec4 ansColor = texture2D(myTextureSampler,v_UV).rgba;
	if(ansColor[3] == 0.0)
	{
		discard;
	}
	gl_FragColor = ansColor;	
}