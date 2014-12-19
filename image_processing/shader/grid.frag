#version 330

in vec2 vertInPosition;
out vec4 outColor;
uniform float viewEfect;
uniform float efectStrength;
uniform sampler2D texture;

float threshold(in float thr1, in float thr2 , in float val) {
 if (val < thr1) {return 0.0;}
 if (val > thr2) {return 1.0;}
 return val;
}

// averaged pixel intensity from 3 color channels
float avg_intensity(in vec4 pix) {
 return (pix.r + pix.g + pix.b)/3.;
}

vec4 get_pixel(in vec2 coords, in float dx, in float dy) {
 return texture2D(texture,coords + vec2(dx, dy));
}

// returns pixel color
float IsEdge(in vec2 coords, in float efectPower){
  float dxtex = 1.0 / 512.0 /*image width*/;
  float dytex = 1.0 / 512.0 /*image height*/;
  float pix[9];
  int k = -1;
  float delta;

  // read neighboring pixel intensities
  for (int i=-1; i<2; i++) {
   for(int j=-1; j<2; j++) {
    k++;
    pix[k] = avg_intensity(get_pixel(coords,float(i)*dxtex,
                                          float(j)*dytex));
   }
  }

  // average color differences around neighboring pixels
  delta = (abs(pix[1]-pix[7])+
          abs(pix[5]-pix[3]) +
          abs(pix[0]-pix[8])+
          abs(pix[2]-pix[6])
           )/4.;

  return threshold(efectPower,0.4,clamp(1.8*delta,0.0,1.0));
}

void main()
{
  vec2 newPosition = (vertInPosition+1)/2; //prepocet do texturovacich souradnic
  if(viewEfect==1){
  	  /* hranova detekce */
	  float c = IsEdge(newPosition, efectStrength / 100);
	  vec4 color = vec4(c,c,c,1.0);
	  outColor = color;
  }else if(viewEfect==2){
      /* gauss blur */
  	  vec4 sum = vec4(0.0);
 
 	  float gaussBlurSize = 1 / (efectStrength*30);
 
	  // blur in y (vertical)
	  // take nine samples, with the distance blurSize between them
	  sum += texture2D(texture, vec2(newPosition.x - 4.0*gaussBlurSize, newPosition.y)) * 0.05;
	  sum += texture2D(texture, vec2(newPosition.x - 3.0*gaussBlurSize, newPosition.y)) * 0.09;
	  sum += texture2D(texture, vec2(newPosition.x - 2.0*gaussBlurSize, newPosition.y)) * 0.12;
	  sum += texture2D(texture, vec2(newPosition.x - gaussBlurSize, newPosition.y)) * 0.15;
	  sum += texture2D(texture, vec2(newPosition.x, newPosition.y)) * 0.16;
	  sum += texture2D(texture, vec2(newPosition.x + gaussBlurSize, newPosition.y)) * 0.15;
	  sum += texture2D(texture, vec2(newPosition.x + 2.0*gaussBlurSize, newPosition.y)) * 0.12;
	  sum += texture2D(texture, vec2(newPosition.x + 3.0*gaussBlurSize, newPosition.y)) * 0.09;
	  sum += texture2D(texture, vec2(newPosition.x + 4.0*gaussBlurSize, newPosition.y)) * 0.05;
 
      outColor = sum;
  }else if(viewEfect == 3){
	  /* emboss */
	  vec2 onePixel = vec2(1.0 / (efectStrength * 48), 1.0 / (efectStrength * 30));	 
	  vec2 texCoord = newPosition;
	  vec4 color;
	  color.rgb = vec3(0.5);
	  color -= texture2D(texture, texCoord - onePixel) * 5.0;
	  color += texture2D(texture, texCoord + onePixel) * 5.0;
	  color.rgb = vec3((color.r + color.g + color.b) / 3.0);
	  outColor = vec4(color.rgb, 1);

  }else if(viewEfect == 4){
  	/* negative */
  	outColor = vec4(vec3(1.0) - texture2D(texture, newPosition).rgb, 1.0);
  }else if(viewEfect == 5){
  	/* grayscale */
    vec2 pos = vec2(newPosition.s, newPosition.t);
    float r = texture2D(texture, pos).r;
    float g = texture2D(texture, pos).g;
    float b = texture2D(texture, pos).b;
    float y = (efectStrength/10*r)+(efectStrength/10*g)+(efectStrength/10*b);
    outColor = vec4(y, y, y, 1.0f);

    outColor = vec4(y, y, y, 1.0);     
  }else if(viewEfect == 6) {
  	/* pixelization */
  	vec2 dimensions = vec2(efectStrength * 8, efectStrength * 6);
	vec2 size = vec2(1.0, 1.0) / dimensions;
	vec2 pixel_base = vec2(0.0);
	pixel_base.s = newPosition.s - mod(newPosition.s, size.s);
	pixel_base.t = newPosition.t - mod(newPosition.t, size.t);

	pixel_base += 0.5 * size;

	outColor = texture2D(texture, pixel_base);
	  	
  }else if(viewEfect == 7){
 	/* jas */
 	vec4 originColor = texture2D(texture, newPosition);
	float brightness = (originColor.r + originColor.g + originColor.b) / efectStrength;
	outColor = vec4(brightness, brightness, brightness, originColor.a); 
  }else{
  	  /* nic */
  	  outColor=texture2D(texture, newPosition);
  }
}