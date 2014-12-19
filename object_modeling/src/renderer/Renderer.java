package renderer;

import gui.Menu;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import models.Grid;
import models.Mesh;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import transforms.Camera;
import transforms.Mat4;
import transforms.Mat4PerspRH;
import transforms.Vec3D;
import utils.OGLBuffers;
import utils.OGLRenderTarget;
import utils.OGLTexture;
import utils.ShaderUtils;
import utils.ToFloatArray;

public class Renderer implements GLEventListener, MouseListener,
		MouseMotionListener, KeyListener {

	public GL2 gl;
	public GLAutoDrawable glDrawable;
	GLU glu;
	int width, height, ox, oy, gWidth = 50, gHeight = 50;

	public OGLBuffers buffers;
	public Mesh grid, plocha, koule;

	public int locMatCube, shaderProgramGrid, shaderProgramGeometry,
		locMatGrid,locLightPos, locEyePos, actualShader, paramFunc,renderTexture,
		lightType, mappingType, projectionMat, noMapping;
	
	public Integer actualObject = 1;

	Menu app;
	
	public Renderer(Menu app, int width, int height){
		this.app = app;
		this.width = width;
		this.height = height;
	}
	
	Camera cam = new Camera();
	Mat4 proj; // vytvarena v reshape
	Vec3D lightPos = new Vec3D(0.5,0.5,20);
	OGLTexture texture, texture1, texture_n, texture1_n, texture_h, earth_texture_n, earth_texture;
	
	OGLRenderTarget renderTarget;
	
	public void init(GLAutoDrawable drawable) {
		glDrawable = drawable;
		gl = glDrawable.getGL().getGL2();

		System.out.println("Init GL is " + gl.getClass().getName());
		System.out.println("OpenGL version " + gl.glGetString(GL2.GL_VERSION));
		System.out.println("OpenGL vendor " + gl.glGetString(GL2.GL_VENDOR));
		System.out
				.println("OpenGL renderer " + gl.glGetString(GL2.GL_RENDERER));
		System.out.println("OpenGL extension "
				+ gl.glGetString(GL2.GL_EXTENSIONS));

		shaderProgramGrid = ShaderUtils.loadProgram(gl, "./shader/grid");
		grid = new Grid(gl,gHeight,gWidth, "inPosition");

		texture = new OGLTexture(gl, "textures/bricks.jpg");
		texture_n = new OGLTexture(gl, "textures/bricksn.png");
		texture_h = new OGLTexture(gl, "textures/bricksh.png");
		texture1 = new OGLTexture(gl, "textures/earth.png");
		earth_texture = new OGLTexture(gl, "textures/earthwowater.jpg");
		earth_texture_n = new OGLTexture(gl, "textures/earthwowater_n.jpg");
		
		locMatGrid = gl.glGetUniformLocation(shaderProgramGrid, "mat");
		projectionMat = gl.glGetUniformLocation(shaderProgramGrid, "projection");
		locLightPos = gl.glGetUniformLocation(shaderProgramGrid, "lightPos");
		locEyePos = gl.glGetUniformLocation(shaderProgramGrid,"eyePos");
		
		/* parametrizace shaderu */
		renderTexture = gl.glGetUniformLocation(shaderProgramGrid,"renderTexture");
		paramFunc = gl.glGetUniformLocation(shaderProgramGrid,"paramFunc");
		lightType = gl.glGetUniformLocation(shaderProgramGrid,"lightType");
		mappingType = gl.glGetUniformLocation(shaderProgramGrid,"mappingType");
		noMapping = gl.glGetUniformLocation(shaderProgramGrid,"noMapping");
		
		renderTarget = new OGLRenderTarget(gl, width, height);
		
		cam.setPosition(new Vec3D(5, 5, 2.5));
		cam.setAzimuth(Math.PI * 1.25);
		cam.setZenith(Math.PI * -0.125);
		
		gl.glEnable(GL2.GL_DEPTH_TEST);
	}


	
	public void display(GLAutoDrawable drawable) {
		gl.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
					
		Mat4 mat = cam.getViewMatrix().mul(proj);
		
		renderTarget.bind();
		gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, 0);
		gl.glViewport(0, 0, width, height);

		gl.glUniformMatrix4fv(locMatCube, 1, false,
				ToFloatArray.convert(mat), 0);
		
		gl.glUniformMatrix4fv(locMatGrid,1,false,ToFloatArray.convert(mat),0);
		gl.glUniform3fv(locLightPos, 3, ToFloatArray.convert(lightPos), 0);
		gl.glUniform3fv(locEyePos, 3, ToFloatArray.convert(cam.getEye()), 0);
		gl.glUniform3fv(projectionMat, 4, ToFloatArray.convert(proj), 0);
		gl.glUseProgram(shaderProgramGrid);

		gl.glUniform1f(paramFunc, (float) app.renderObject); // set rendrovane funkce
		
	
		// vyber textury
		if (app.renderTexture.isSelected()){
			gl.glUniform1f(renderTexture, 1);
			
			if (app.textureList.getSelectedIndex() == 0) {
				earth_texture.bind(shaderProgramGrid, "texture", 3);
				earth_texture_n.bind(shaderProgramGrid, "normTex", 4);
				
			} else if (app.textureList.getSelectedIndex() == 1) {
				texture.bind(shaderProgramGrid, "texture", 0);
				texture_n.bind(shaderProgramGrid, "normTex", 1);
				texture_h.bind(shaderProgramGrid, "heightTexTex", 2);
			}
		} else {
			gl.glUniform1f(renderTexture, 0);
		}
		
		gl.glUniform1f(mappingType, (float) app.mappingList.getSelectedIndex()); // set mappingType
		gl.glUniform1f(lightType, (float) app.lightList.getSelectedIndex()); // set lightType
		
		grid.draw(shaderProgramGrid);
		
	}

	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		this.width = width;
		this.height = height;
		proj = new Mat4PerspRH(Math.PI / 4, height / (double) width, 0.01, 1000.0);
	}

	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged) {
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		ox = e.getX();
		oy = e.getY();
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
		cam.addAzimuth((double) Math.PI * (ox - e.getX())
				/ width);
		cam.addZenith((double) Math.PI * (e.getY() - oy)
				/ width);
		ox = e.getX();
		oy = e.getY();
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_W:
			cam.forward(1);
			break;
		case KeyEvent.VK_D:
			cam.right(1);
			break;
		case KeyEvent.VK_S:
			cam.backward(1);
			break;
		case KeyEvent.VK_A:
			cam.left(1);
			break;
		case KeyEvent.VK_SHIFT:
			cam.down(1);
			break;
		case KeyEvent.VK_CONTROL:
			cam.up(1);
			break;
		case KeyEvent.VK_SPACE:
			cam.setFirstPerson(!cam.getFirstPerson());
			break;
		case KeyEvent.VK_R:
			cam.mulRadius(0.9f);
			break;
		case KeyEvent.VK_F:
			cam.mulRadius(1.1f);
			break;
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public void dispose(GLAutoDrawable arg0) {
	}
}