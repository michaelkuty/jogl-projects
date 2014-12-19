#version 330
in vec3 vertColor; // vstup z predchozi casti retezce
out vec4 outColor;

in vec3 normal;
in vec3 lightVec;
in vec3 eyeVec;
in vec2 vertInPosition;

uniform float lightType;
uniform sampler2D texture;
uniform sampler2D normTex;
uniform float renderTexture;
uniform float noMapping;
uniform float mappingType;
uniform sampler2D heightTex;

void main() {
	vec3 norm = normal;
	vec2 texCoord = vertInPosition;
	
	if (lightType == 0) {
		outColor = vec4(vertColor, 1.0);
		if (renderTexture == 1){
			
			outColor = vec4(texture2D(texture, vertInPosition).rgb * vertColor,1);
		} else {
			outColor = vec4(vertColor, 1.0);
		}
	} else {

		if (renderTexture == 1){
			if(mappingType != 0){
				// parallax mapping		
				float height = texture2D(heightTex, vertInPosition).r -0.5;
				float v = height * 0.02 - 0.007;
				texCoord = vertInPosition + (eyeVec.xy * v).yx;				
			}
			
			  // normal mapping
				norm = texture2D(normTex, texCoord).xyz;
				norm *= 2;
				norm -= 1;
		}
		
		float diff = dot(norm, lightVec);
		diff = max(0,diff);
		vec3 halfVec = normalize(normalize(eyeVec) + lightVec);
		float spec = dot(norm, halfVec);
		spec = max(0,spec);
		spec = pow(spec,10);
		float ambient = 0.1;
		vec3 diffColor; 
		if (renderTexture == 1) {
			diffColor = texture2D(texture, texCoord).rgb;
		} else {
			diffColor = vec3(texCoord, 0);
		}
		
		vec3 fragColor = diffColor * (min(ambient + diff,1)) + vec3(1,1,1) * spec;
		
		outColor=vec4(fragColor,1);

	}

} 
