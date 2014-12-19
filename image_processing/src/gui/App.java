package gui;



import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import renderer.Renderer;

import com.jogamp.opengl.util.FPSAnimator;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class App {
	private static final int FPS = 60; // animator's target frames per second
	
	public static void main(String[] args) {
		try {
			Frame appFrame = new Frame("PGRF3 - Michael Kutý");
			appFrame.setSize(512, 384);
			appFrame.setLocationRelativeTo(null);
			// setup OpenGL Version 2
	    	GLProfile profile = GLProfile.get(GLProfile.GL2);
	    	GLCapabilities capabilities = new GLCapabilities(profile);
	    	
	    	Menu app = new Menu();
	    	GLCanvas canvas = new GLCanvas(capabilities);
	    	Renderer ren = new Renderer(app, 512, 384);
	    	
			canvas.addGLEventListener(ren);
			canvas.addMouseListener(ren);
			canvas.addMouseMotionListener(ren);
			canvas.addKeyListener(ren);
	    	canvas.setSize( 512, 384 );	    	
	    	appFrame.add(canvas);
			       			
	    	final FPSAnimator animator = new FPSAnimator(canvas, FPS, true);
	    	 
	    	appFrame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					new Thread() {
	                     @Override
	                     public void run() {
	                        if (animator.isStarted()) animator.stop();
	                        System.exit(0);
	                     }
	                  }.start();
				}
			});

	    	Menu.setRenderer(ren);

	    	appFrame.pack();
	    	appFrame.setVisible(true);
            animator.start(); // start the animation loop
            
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}