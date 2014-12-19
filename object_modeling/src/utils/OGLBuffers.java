package utils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL2;

import com.jogamp.common.nio.Buffers;

public class OGLBuffers {
	static public class Attrib {
		String name;
		int dimension;
		boolean normalize = false;
		int offset = -1;
		public Attrib(String name, int dimension) {
			this.name = name;
			this.dimension = dimension;
		}
		public Attrib(String name, int dimension, int offsetInFloats) {
			this.name = name;
			this.dimension = dimension;
			this.offset = 4 * offsetInFloats;
		}
		public Attrib(String name, int dimension, boolean normalize) {
			this.name = name;
			this.dimension = dimension;
			this.normalize = normalize;
		}
		public Attrib(String name, int dimension, boolean normalize, int offsetInFloats) {
			this.name = name;
			this.dimension = dimension;
			this.normalize = normalize;
			this.offset = 4 * offsetInFloats;
		}
	}
	
	protected class VertexBuffer {
		int id, stride;
		Attrib[] attributes;
		public VertexBuffer(int id, int stride, Attrib[] attributes) {
			this.id = id;
			this.stride = stride;
			this.attributes = attributes;
		}
	}
	
	protected GL2 gl;
	protected ArrayList<VertexBuffer> vertexBuffers = new ArrayList<>();
	protected ArrayList<Integer> attribArrays = null;
	protected int[] indexBuffer;
	protected int vertexCount = -1, indexCount = -1;

	public OGLBuffers(GL2 gl) {
		this.gl = gl;
	}
	
	public OGLBuffers(GL2 gl, float[] vertexData, Attrib[] attributes, int[] indexData) {
		this.gl = gl;
		addVertexBuffer(vertexData, attributes);
		if (indexData != null) setIndexBuffer(indexData);
	}

	public OGLBuffers(GL2 gl, float[] vertexData, int floatsPerVertex, Attrib[] attributes, int[] indexData) {
		this.gl = gl;
		addVertexBuffer(vertexData, attributes);
		if (indexData != null) setIndexBuffer(indexData);
	}
	
	public void addVertexBuffer(float[] data, Attrib[] attributes) {
		if (attributes == null || attributes.length == 0)
			return;

		int floatsPerVertex = 0;
		for (int i = 0; i < attributes.length; i++)
			floatsPerVertex += attributes[i].dimension;
		
		addVertexBuffer(data, floatsPerVertex,attributes);
	}

	public void addVertexBuffer(float[] data, int floatsPerVertex, Attrib[] attributes) {
		int[] bufferID = new int[1];
		FloatBuffer buffer = Buffers.newDirectFloatBuffer(data);
		gl.glGenBuffers(1, bufferID, 0);
		gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, bufferID[0]);
		gl.glBufferData(GL2.GL_ARRAY_BUFFER, data.length * 4, buffer,
				GL2.GL_STATIC_DRAW);

		if (data.length % floatsPerVertex != 0)
			throw new RuntimeException(
					"The total number of floats is incongruent with the number of floats per vertex.");
		if (vertexCount < 0)
			vertexCount = data.length / floatsPerVertex;
		else if (vertexCount != data.length / floatsPerVertex)
			System.out.println("Warning: GLBuffers.addVertexBuffer: vertex count differs from the first one.");

		vertexBuffers.add(new VertexBuffer(bufferID[0], floatsPerVertex * 4, attributes));
	}
	
	public void setIndexBuffer(int[] data) {
		indexBuffer = new int[1];
		indexCount = data.length;
		IntBuffer buffer = Buffers.newDirectIntBuffer(data);
		gl.glGenBuffers(1, indexBuffer, 0);
		gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, indexBuffer[0]);
		gl.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER, data.length * 4, buffer,
				GL2.GL_STATIC_DRAW);
	}
	
	public void bind(int shaderProgram) {
		if (attribArrays != null)
			for (Integer attrib : attribArrays)
				gl.glDisableVertexAttribArray(attrib);
		attribArrays = new ArrayList<>();
		for (VertexBuffer vb : vertexBuffers) {
			gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, vb.id);
			int offset = 0;
			for (int j = 0; j < vb.attributes.length; j++) {
				int location = gl.glGetAttribLocation(shaderProgram,
						vb.attributes[j].name);
				attribArrays.add(location);
				gl.glEnableVertexAttribArray(location);
				gl.glVertexAttribPointer(location, vb.attributes[j].dimension,
						GL2.GL_FLOAT, vb.attributes[j].normalize, vb.stride,
						vb.attributes[j].offset < 0 ? offset
								: vb.attributes[j].offset);
				offset += 4 * vb.attributes[j].dimension;
			}
		}
		if (indexBuffer != null)
			gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, indexBuffer[0]);

	}

	public void unbind() {
		if (attribArrays != null) {
			for (Integer attrib : attribArrays)
				gl.glDisableVertexAttribArray(attrib);
			attribArrays = null;
		}
	}
	
	public void draw(int topology, int shaderProgram) {
		gl.glUseProgram(shaderProgram); 
		bind(shaderProgram);
		if (indexBuffer == null) {
			gl.glDrawArrays(topology, 0, vertexCount);
		} else {
			gl.glDrawElements(topology, indexCount, GL2.GL_UNSIGNED_INT, 0);
		}
		unbind();
	}

	public void draw(int topology, int shaderProgram, int count) {
		gl.glUseProgram(shaderProgram); 
		bind(shaderProgram);
		if (indexBuffer == null) {
			gl.glDrawArrays(topology, 0, count);
		} else {
			gl.glDrawElements(topology, count, GL2.GL_UNSIGNED_INT, 0);
		}
		unbind();
	}

	public void draw(int topology, int shaderProgram, int count, int start) {
		gl.glUseProgram(shaderProgram); 
		bind(shaderProgram);
		if (indexBuffer == null) {
			gl.glDrawArrays(topology, start, count);
		} else {
			gl.glDrawElements(topology, count, GL2.GL_UNSIGNED_INT, start);
		}
		unbind();
	}
				
}
