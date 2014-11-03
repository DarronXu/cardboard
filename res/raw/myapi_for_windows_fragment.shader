precision mediump float;
uniform int u_hasTexUV;
uniform vec4 u_fallbackColor;
varying vec2 v_UV;
uniform sampler2D myTextureSampler;
void main()
{
	vec4 ansColor=u_fallbackColor.rgba;
	if(u_hasTexUV==1) {
		float x=v_UV[0];
		float y=v_UV[1];
		vec2 realUV=vec2(x,1.0-y);
		ansColor = texture2D(myTextureSampler,realUV).rgba;
		if(ansColor[3] == 0.0)
		{
			discard;
		}
	}
	gl_FragColor = ansColor;
}