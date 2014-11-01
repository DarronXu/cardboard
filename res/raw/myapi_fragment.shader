precision mediump float;
varying vec2 v_UV;
uniform sampler2D myTextureSampler;
void main()
{
	vec4 ansColor = vec4(1.0,0.0,0.0,1.0).rgba;
	//if(ansColor.rgba[3] == 1.0)
	//{
	//	discard;
	//}
	gl_FragColor = ansColor;
}