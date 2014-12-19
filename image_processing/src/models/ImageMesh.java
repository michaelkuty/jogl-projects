package models;

import javax.media.opengl.GL2;

import utils.OGLBuffers;

public class ImageMesh implements Mesh {
	
	public OGLBuffers buff;
	
	public ImageMesh(GL2 gl, int m, int n, String uniformName) {
		 float[] vertices = {
		    	-1, 1,
				 1, 1,
				 1,-1,
				-1,-1
		};

		int[] indexBufferData = { 0, 1, 2, 3 };		 
		 
		OGLBuffers.Attrib[] attributes = {
				new OGLBuffers.Attrib(uniformName, 2)
		};
		
		buff = new OGLBuffers(gl, vertices, attributes, indexBufferData);
	}
	
	@Override
	public void draw(int shaderProgram) {
		
		buff.draw(GL2.GL_QUADS, shaderProgram);
	}

}
