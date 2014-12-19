#version 330
#define M_PI 3.14159265359

in vec2 inPosition; // vstup z vertex bufferu

uniform mat4 mat;
uniform mat4 projection;
uniform vec3 lightPos;
uniform vec3 eyePos;
uniform float renderTexture;
uniform float paramFunc;
uniform float lightType;
uniform float mappingType;


out vec3 vertColor; // vystup do dalsich casti retezce
out vec3 normal;
out vec3 lightVec;
out vec3 eyeVec;
out vec2 vertInPosition;
out vec4 outColor;
out mat3 tbn;
out vec2 texCoord;

const float delta = 0.001;

out Data
{
    vec4 position;
    vec4 normal;
    vec4 color;
    mat4 mvp;
} vdata;

// funkce
vec3 funcSaddle(vec2 inPos) {
	float s = inPos.x * 2 - 1;
	float t = inPos.y * 2 -1;
	return vec3(s,t,s*s - t*t);
}

vec3 funcSombrero(vec2 inPos) {
	// sombrero
		float s = M_PI * 0.5 - M_PI * inPos.x *2;
		float t = 2 * M_PI * inPos.y;
		
		return vec3(
				t*cos(s),
				t*sin(s),
				2*sin(t))/2;
}

vec3 funcSphere(vec2 inPos) {
	//zemekoule
	float s = M_PI * 0.5 - M_PI * inPos.x;
	float t = 2* M_PI * inPos.y;
	float r = 2;
	
	return vec3(
			cos(t) * cos(s) * r,
			sin(t) * cos(s) * r, 
			sin(s) * r);
}

vec3 funcSnake(vec2 inPos){
	// snake
	float s = inPos.x * 2;
	float t = inPos.y * 2 * M_PI;
	return vec3((1-s)*(3+cos(t))*cos(2*M_PI*s),(1-s)*(3+cos(t))*sin(2*M_PI*s),6*s+(1-s)*sin(t))/2;

}

vec3 funcBumpySphere(vec2 inPos){
	// divno ojekt :D
	float r = 2;
	float s = (M_PI * 2 - M_PI * inPos.y * 2) - 1;
	float t = 2 * M_PI * inPos.x - 1;
	return vec3(r * cos(t) * cos(s),
				r * cos(t) * sin(s),
				r * sin(t));
}

vec3 funcCone(vec2 inPos){
	float s = M_PI * 0.5 - M_PI * inPos.x * 2 - 2;
	float t = 2 * M_PI * inPos.y -2;
	
	return vec3(t*cos(s),
				t*sin(s),
				t);
}

vec3 funcGlass(vec2 inPos){
	float s= M_PI * 0.5 - M_PI * inPos.x * 2;
	float t= M_PI * 0.5 - M_PI * inPos.y * 2;
	
	float r = 1 + cos(t);
	float th = s;
	
	return vec3(
		r*cos(th),
		r*sin(th),
		t);
}

vec3 paramPos(vec2 inPosition){
	vec3 position;
	
	if (paramFunc == 0) {
		position = funcSaddle(inPosition);	
	} else if (paramFunc == 1) {
		position = funcSombrero(inPosition);	
	} else if (paramFunc == 2) {
		position = funcSphere(inPosition);	
	} else if (paramFunc == 3) {
		position = funcSnake(inPosition);	
	} else if (paramFunc == 4) {
		position = funcBumpySphere(inPosition);	
	} else if (paramFunc == 5) {
		position = funcCone(inPosition);	
	} else if (paramFunc == 6) {
		position = funcGlass(inPosition);	
	}
	return position;
}

/* obecny vypocet normaly */
vec3 paramNormal(vec2 inPos){
	vec3 tx = paramPos(inPos + vec2(delta,0)) - paramPos(inPos - vec2(delta,0));
	vec3 ty = paramPos(inPos + vec2(0,delta)) - paramPos(inPos - vec2(0,delta));
	return cross(tx,ty);
}

mat3 paramTangent(vec2 inPos){
	float delta = 0.001;
	vec3 tx = paramPos(inPos + vec2(delta,0)) - paramPos(inPos - vec2(delta,0));
	vec3 ty = paramPos(inPos + vec2(0,delta)) - paramPos(inPos - vec2(0,delta));
	tx= normalize(tx);
	ty = normalize(ty);
	vec3 tz = cross(tx,ty);
	ty = cross(tz,tx);
	return mat3(tx,ty,tz);
}

void main() {
	vec3 position = paramPos(inPosition);
	normal = normalize(paramNormal(inPosition));
	gl_Position = mat * vec4(position,1.0);
	lightVec = normalize(lightPos - position);
	eyeVec = normalize(eyePos - position);
	vertInPosition = inPosition;
	
	// paralax mapping
	if(mappingType==1){
		/* parallax mapping */
		tbn = paramTangent(inPosition);
		//eyeVec*=tbn;
	}
	
	// osvetleni per vertex
	if (lightType == 0) {

		float diff = max(0,dot(normal, lightVec));
		vec3 halfVec = normalize(eyeVec + lightVec);
		float spec = dot(normal, halfVec);
		spec = max(0,spec);
		spec = pow(spec, 10);
		float ambient = 0.1;
		
		if (renderTexture == 1) {
			vertColor=vec3(1,1,1) * (min(ambient + diff,1)) + vec3(1,1,1) * spec;
		} else {
			vertColor=vec3(inPosition,0) * (min(ambient + diff,1)) + vec3(1,1,1) * spec;
		}

	}
	
	//textury

	if (renderTexture == 1) {
		int aux = int(dot(abs(normal) * vec3(0, 1, 2), vec3(1, 1, 1)));
		texCoord = vec2(inPosition[(aux + 1) % 3], inPosition[(aux + 2) % 3]);
	}
	
	/*
    vdata.mvp = projection * mat;
    vdata.position = vec4(position.xyz, 1);
    vdata.normal = vec4(normal.xyz, 1);
	*/
	
} 
 