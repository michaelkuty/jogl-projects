package utils;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import java.io.File;
import java.io.IOException;

import javax.media.opengl.GL2;

public class OGLTexture {
	protected GL2 gl;
	public Texture texture;

	public OGLTexture(GL2 gl, String fileName) {
		this.gl = gl;
		try {
			System.out.print("Loading texture " + fileName + " ... ");
			texture = TextureIO.newTexture(new File(fileName), true);
		} catch (IOException e) {
			System.out.println("failed");
			System.out.println(e.getMessage());
		}
		if (texture != null)
			System.out.println("OK");

	}

	public void bind(int shaderProgram, String name, int slot) {
		if (texture == null) return;
		gl.glActiveTexture(GL2.GL_TEXTURE0 + slot);
		texture.bind(gl);
		gl.glUniform1i(gl.glGetUniformLocation(shaderProgram, name), slot);
	}
}
