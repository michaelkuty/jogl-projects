package utils;

import javax.media.opengl.GL2;

public class OGLRenderTarget {
	protected GL2 gl;
	protected int width, height;
	protected int[] colorBuffer = new int[1];
	protected int[] depthBuffer = new int[1];
	protected int[] frameBuffer = new int[1];

	public OGLRenderTarget(GL2 gl, int width, int height) {
		this.gl = gl;
		this.width = width;
		this.height = height;
		gl.glGenTextures(1, colorBuffer, 0);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, colorBuffer[0]);
		gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGBA8, width, height, 0,
				GL2.GL_RGBA, GL2.GL_UNSIGNED_BYTE, null);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S,
				GL2.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T,
				GL2.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER,
				GL2.GL_LINEAR);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER,
				GL2.GL_LINEAR);

		gl.glGenTextures(1, depthBuffer, 0);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, depthBuffer[0]);
		gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_DEPTH_COMPONENT24, width,
				height, 0, GL2.GL_DEPTH_COMPONENT, GL2.GL_FLOAT, null);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S,
				GL2.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T,
				GL2.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER,
				GL2.GL_LINEAR);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER,
				GL2.GL_LINEAR);

		gl.glGenFramebuffers(1, frameBuffer, 0);
		gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, frameBuffer[0]);
		gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT0,
				GL2.GL_TEXTURE_2D, colorBuffer[0], 0);
		gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, GL2.GL_DEPTH_ATTACHMENT,
				GL2.GL_TEXTURE_2D, depthBuffer[0], 0);

		if (gl.glCheckFramebufferStatus(GL2.GL_FRAMEBUFFER) != GL2.GL_FRAMEBUFFER_COMPLETE) {
			System.out.println("There is a problem with the FBO");
		}
	}
	
	public void bind() {
		gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, frameBuffer[0]);
		gl.glViewport(0, 0, width, height);
	}

	public void bindColorTexture(int shaderProgram, String name, int slot) {
		gl.glActiveTexture(GL2.GL_TEXTURE0 + slot);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, colorBuffer[0]);
		gl.glUniform1i(gl.glGetUniformLocation(shaderProgram, name), slot);
	}

	public void bindDepthTexture(int shaderProgram, String name, int slot) {
		gl.glActiveTexture(GL2.GL_TEXTURE0 + slot);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, depthBuffer[0]);
		gl.glUniform1i(gl.glGetUniformLocation(shaderProgram, name), slot);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	@Override
	public void finalize() {
		gl.glDeleteFramebuffers(1, frameBuffer, 0);
		gl.glDeleteTextures(1, colorBuffer, 0);
		gl.glDeleteTextures(1, depthBuffer, 0);
	}
}
