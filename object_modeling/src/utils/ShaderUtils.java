package utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.media.opengl.GL2;

public final class ShaderUtils {

	public static int loadProgram(GL2 gl, String shaderFileName) {
		String error;

		String extensions = gl.glGetString(GL2.GL_EXTENSIONS);
		if (extensions.indexOf("GL_ARB_vertex_shader") == -1
				|| extensions.indexOf("GL_ARB_fragment_shader") == -1) {
			throw new RuntimeException(
					"Shaders not supported by OpenGL driver.");
		}
		// vertex shader
		String[] shaderVecSrc = readShaderFromFile(shaderFileName + ".vert");
		int vs = gl.glCreateShader(GL2.GL_VERTEX_SHADER);
		gl.glShaderSource(vs, shaderVecSrc.length, shaderVecSrc, (int[]) null,
				0);

		System.out.print("Compiling vertex shader " + shaderFileName
				+ ".vert ... ");
		gl.glCompileShader(vs);
		error = checkLogInfo(gl, vs, GL2.GL_COMPILE_STATUS);
		if (error == null)
			System.out.println("OK");
		else {
			System.out.println("failed");
			System.out.println("\n" + error);
			return -1;
		}

		// fragment shader
		String[] shaderFragSrc = readShaderFromFile(shaderFileName + ".frag");
		int fs = gl.glCreateShader(GL2.GL_FRAGMENT_SHADER);
		gl.glShaderSource(fs, shaderFragSrc.length, shaderFragSrc,
				(int[]) null, 0);

		System.out.print("Compiling fragment shader " + shaderFileName
				+ ".frag ... ");
		gl.glCompileShader(fs);
		error = checkLogInfo(gl, fs, GL2.GL_COMPILE_STATUS);
		if (error == null)
			System.out.println("OK");
		else {
			System.out.println("failed");
			System.out.println("\n" + error);
			return -1;
		}

		// geometry shader
		String[] shaderGeoSrc = readShaderFromFile(shaderFileName + ".geo");
		int gs = gl.glCreateShader(GL2.GL_GEOMETRY_SHADER_ARB);
		gl.glShaderSource(gs, shaderGeoSrc.length, shaderGeoSrc,
				(int[]) null, 0);

		System.out.print("Compiling fragment shader " + shaderFileName
				+ ".geo ... ");
		gl.glCompileShader(gs);
		error = checkLogInfo(gl, gs, GL2.GL_COMPILE_STATUS);
		if (error == null)
			System.out.println("OK");
		else {
			System.out.println("failed");
			System.out.println("\n" + error);
			return -1;
		}
		
		int shaderProgram = gl.glCreateProgram();
		gl.glAttachShader(shaderProgram, vs);
		gl.glAttachShader(shaderProgram, fs);
		
		//gl.glAttachShader(shaderProgram, gs);
		
		System.out.print("Linking shader program " + shaderFileName + " ... ");
		gl.glLinkProgram(shaderProgram);
		error = checkLogInfo(gl, shaderProgram, GL2.GL_LINK_STATUS);
		if (error == null)
			System.out.println("OK");
		else {
			System.out.println("failed");
			System.out.println("\n" + error);
			return -1;
		}
		return shaderProgram;
	}

	static public void linkProgram(GL2 gl, int shaderProgram) {
		String error;
		System.out.print("Linking shader program ... ");
		gl.glLinkProgram(shaderProgram);
		error = checkLogInfo(gl, shaderProgram, GL2.GL_LINK_STATUS);
		if (error == null)
			System.out.println("OK");
		else {
			System.out.println("failed");
			System.out.println("\n" + error);
		}
	}

	static private String[] readShaderFromFile(String shaderFileName) {
		BufferedReader brv = null;
		try {
			brv = new BufferedReader(new FileReader(shaderFileName));
		} catch (FileNotFoundException e) {
			System.out.println("File not found " + shaderFileName);
			e.printStackTrace();
		}

		String line;
		int index;
		ArrayList<String> shader = new ArrayList<>();
		try {
			while ((line = brv.readLine()) != null) {
				index = line.indexOf("//");
				if (index > 0)
					line = line.substring(0, index);
				shader.add(new String(line + "\n"));
			}
		} catch (IOException e) {
			System.out.println("Read error in " + shaderFileName);
			e.printStackTrace();
		}
		String[] result = new String[shader.size()];
		return shader.toArray(result);
	}

	static private String checkLogInfo(GL2 gl, int programObject, int mode) {
		switch (mode) {
		case GL2.GL_COMPILE_STATUS:
			return checkLogInfoShader(gl, programObject, mode);
		case GL2.GL_LINK_STATUS:
		case GL2.GL_VALIDATE_STATUS:
			return checkLogInfoProgram(gl, programObject, mode);
		default:
			return "Unsupported mode.";
		}
	}

	static private String checkLogInfoShader(GL2 gl, int programObject, int mode) {
		int[] error = new int[] { -1 };
		gl.glGetShaderiv(programObject, mode, error, 0);
		if (error[0] != GL2.GL_TRUE) {
			int[] len = new int[1];
			gl.glGetShaderiv(programObject, GL2.GL_INFO_LOG_LENGTH, len, 0);
			if (len[0] == 0) {
				return null;
			}
			byte[] errorMessage = new byte[len[0]];
			gl.glGetShaderInfoLog(programObject, len[0], len, 0, errorMessage,
					0);
			return new String(errorMessage, 0, len[0]);
		}
		return null;
	}

	static private String checkLogInfoProgram(GL2 gl, int programObject, int mode) {
		int[] error = new int[] { -1 };
		gl.glGetProgramiv(programObject, mode, error, 0);
		if (error[0] != GL2.GL_TRUE) {
			int[] len = new int[1];
			gl.glGetProgramiv(programObject, GL2.GL_INFO_LOG_LENGTH, len, 0);
			if (len[0] == 0) {
				return null;
			}
			byte[] errorMessage = new byte[len[0]];
			gl.glGetProgramInfoLog(programObject, len[0], len, 0, errorMessage,
					0);
			return new String(errorMessage, 0, len[0]);
		}
		return null;
	}

}
