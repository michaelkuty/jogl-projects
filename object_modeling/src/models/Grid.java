package models;

import javax.media.opengl.GL2;

import utils.OGLBuffers;

public class Grid implements Mesh {
	
	protected OGLBuffers buff;
	
	public Grid(GL2 gl, int m, int n, String uniformName) {
		float[] vertices = new float[2 * m * n];
		int index = 0;
		for (int y = 0; y < m; y++)
			for (int x = 0; x < n; x++) {
				vertices[index ++] = (float) x / (n - 1);
				vertices[index ++] = (float) y / (m - 1);
				//vertices[2 * (y * n + x)] = ...
				//vertices[2 * (y * n + x) + 1] = ...
			}
		
		int[] indices = new int[4 * (m - 1) * (n - 1)];
		index = 0;
		for (int y = 0; y < m - 1; y++)
			for (int x = 0; x < n - 1; x++) {
				indices[index ++] = y * n + x;
				indices[index ++] = y * n + x + 1;
				indices[index ++] = (y + 1) * n + x + 1;
				indices[index ++] = (y + 1) * n + x;
			}
	
		OGLBuffers.Attrib[] attributes = {
				new OGLBuffers.Attrib(uniformName, 2)
		};
		
		buff = new OGLBuffers(gl, vertices, attributes, indices);
	}

	@Override
	public void draw(int shaderProgram) {
		
		buff.draw(GL2.GL_QUADS, shaderProgram);
		
	}

}
