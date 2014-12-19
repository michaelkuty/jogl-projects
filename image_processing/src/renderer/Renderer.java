package renderer;

import gui.Menu;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import com.jogamp.opengl.util.awt.AWTGLReadBufferUtil;
import models.ImageMesh;
import models.Mesh;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.FloatBuffer;
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
	public Mesh mesh;

	/* pokus o histogram */
	int HISTOGRAM_SIZE = 256;
	FloatBuffer histogramValues = FloatBuffer.allocate(HISTOGRAM_SIZE * 3);
	// = GLBuffers.newDirectFloatBuffer(HISTOGRAM_SIZE * 3);
	
	public int locMatCube, shaderProgram, shaderProgramGeometry,
		viewEfect, efectStrength;
	
	Menu app;
	
	public Renderer(Menu app, int width, int height){
		this.app = app;
		this.width = width;
		this.height = height;
	}
	
	Camera cam = new Camera();
	Mat4 proj; // vytvarena v reshape
	Vec3D lightPos = new Vec3D(0.5,0.5,20);
	OGLTexture texture;
	
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

		shaderProgram = ShaderUtils.loadProgram(gl, "./shader/grid");

		gl.glUseProgram(shaderProgram);
		
		mesh = new ImageMesh(gl,gHeight,gWidth, "inPosition");

		/* parametrizace shaderu */
		viewEfect = gl.glGetUniformLocation(shaderProgram,"viewEfect");
		efectStrength = gl.glGetUniformLocation(shaderProgram,"efectStrength");
		
		/* uvodni obrazek */
		texture = new OGLTexture(gl, new File("./images/river.jpg").getPath());
		
		renderTarget = new OGLRenderTarget(gl, width, height);
		
		cam.setPosition(new Vec3D(5, 5, 2.5));
		cam.setAzimuth(Math.PI * 1.25);
		cam.setZenith(Math.PI * -0.125);
		
		gl.glEnable(GL2.GL_DEPTH_TEST);	

	}
	
	public void display(GLAutoDrawable drawable) {
		// noqa
		gl.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		
		Mat4 mat = cam.getViewMatrix().mul(proj);
		
		renderTarget.bind();
			
		gl.glEnable(GL2.GL_BLEND);

		gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, 0);
		gl.glViewport(0, 0, width, height);

		gl.glUniformMatrix4fv(locMatCube, 1, false,
				ToFloatArray.convert(mat), 0);

		// propagace slideru
		gl.glUniform1f(efectStrength, (float) app.efectStrength.getValue());	
		// typ efektu
		gl.glUniform1f(viewEfect, (float) app.viewEfect);

		// bind nove textury jen pokud je potreba
		if (app.file != null && app.loading == true) {
			texture = new OGLTexture(gl, app.file.getPath());
			texture.bind(shaderProgram, "texture", 0);
			app.loading = false;
		}else if (app.notLoaded) {
			/* volat jen pokud je to poprve */
			texture.bind(shaderProgram, "texture", 0);
			app.notLoaded = false;
		}
				
		mesh.draw(shaderProgram); // noqa

		// pokus o histogram
		gl.getContext().makeCurrent();
		gl.glHistogram(GL2.GL_HISTOGRAM, HISTOGRAM_SIZE, GL2.GL_RGB, true);
		gl.glEnable(GL2.GL_HISTOGRAM);
		
		gl.glGetHistogram(GL2.GL_HISTOGRAM,          // ziskani histogramu
                true,                         		 // vynulovani histogramu
                GL2.GL_RGB,                          // format histogramu - barvove slozky
                GL2.GL_UNSIGNED_SHORT,               // datovy typ polozek
                histogramValues);
		
		/* vykresleni histogramu */
        gl.glBegin(GL2.GL_LINE_STRIP);
        gl.glColor3f(1.0f, 0.0f, 0.0f);
        for (int i = 0; i < HISTOGRAM_SIZE; i++)
            gl.glVertex2s((short) i, (short) histogramValues.get(i * 3));
        gl.glEnd();

        gl.glBegin(GL2.GL_LINE_STRIP);
        gl.glColor3f(0.0f, 1.0f, 0.0f);
        for (int i = 0; i < HISTOGRAM_SIZE; i++)
            gl.glVertex2s((short) i, (short) histogramValues.get(i * 3 + 1));// [i][1]);
        gl.glEnd();

        gl.glBegin(GL2.GL_LINE_STRIP);
        gl.glColor3f(0.0f, 0.0f, 1.0f);
        for (int i = 0; i < HISTOGRAM_SIZE; i++) {
        	gl.glVertex2s((short) i, (short) histogramValues.get(i * 3 + 2));// [i][2]);
        	//System.out.println(histogramValues.get(i * 3 + 2));
        }
        gl.glEnd();

        gl.glFlush();			
		
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
	
	public BufferedImage getBufferedImage(){

        AWTGLReadBufferUtil bufferUtils = new AWTGLReadBufferUtil(gl.getGLProfile(), true);
        gl.getContext().makeCurrent();
        return bufferUtils.readPixelsToBufferedImage(gl, true);
	}
	
}