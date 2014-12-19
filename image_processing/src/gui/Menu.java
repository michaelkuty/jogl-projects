package gui;

import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import renderer.Renderer;



public class Menu extends JFrame implements ActionListener {

	public JComboBox viewsEfects;

	String[] viewsChoices = { "Bez efektu", "Hranová detekce", "Gaussovo rozostøení", "Embos", "Negativ", "Odstíny šedi", "Pixelizace", "Jas" };
	
	JFileChooser fc = new JFileChooser();

	public File file = null; // aktualni nacteny file
	public boolean loading = true; // priznak novyho nacitani
	
	private static final String LOOK_AND_FEEL = "javax.swing.plaf.nimbus.NimbusLookAndFeel";

	public float viewEfect = 0;

	static final int MIN = 0;
	static final int MAX = 20;
	static final int INIT = 5;	

	public static Renderer renderer = null;
	
	public JSlider efectStrength = new JSlider(JSlider.HORIZONTAL,
            MIN, MAX, INIT);
	
	public boolean notLoaded = true;
	
	public void actionPerformed(ActionEvent e) {
		viewEfect = viewsEfects.getSelectedIndex();

		if (viewEfect == 2) {
			efectStrength.setValue(MAX);
		}else if(viewEfect == 5) {
			efectStrength.setValue(4);
		}else if(viewEfect == 1) {
			efectStrength.setValue(0);
		}else if(viewEfect == 3) {
			efectStrength.setValue(MAX);
		}
	}
	
	public Menu() {
		initMenu();
	}

	private void initMenu() {

		setTitle("Možnosti");

		setSize(200, 300);
		LayoutManager m = new BoxLayout(getContentPane(), BoxLayout.Y_AXIS);
		setLayout(m);
		setAlwaysOnTop(true);
		JPanel objectPanel = new JPanel();
		viewsEfects = new JComboBox(viewsChoices);
		viewsEfects.setSize(100, 20);
		viewsEfects.addActionListener(this);
		objectPanel.add(new JLabel("Efekt: "));
		objectPanel.add(viewsEfects);
		objectPanel.add(new JLabel("Síla efektu: "));
		objectPanel.add(efectStrength);
		
		//Turn on labels at major tick marks.
		efectStrength.setMajorTickSpacing(2);
		efectStrength.setMinorTickSpacing(1);
		efectStrength.setPaintTicks(true);
		efectStrength.setPaintLabels(true);

		add(objectPanel);
		
		JButton open_file = new JButton("Otevøít");

		objectPanel.add(open_file);
		
		open_file.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int returnVal = fc.showOpenDialog(getParent());
				
		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            file = fc.getSelectedFile();
		            loading = true;
		        } else {
		        	System.out.println("err");
		        }
			}
		});
		
		JButton save_file = new JButton("Uložit");
		objectPanel.add(save_file);
		
		save_file.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(fc.showSaveDialog(getParent()) == JFileChooser.APPROVE_OPTION) {
			        File file = fc.getSelectedFile();
			        String format = "";
			        int i = file.getName().lastIndexOf(".");
			        if(i >= 0){
			                format = file.getName().substring(i+1);
			        }
			        saveCanvasToImage(file, format);
			}
			}
		});
		
		pack();

		try {
			UIManager.setLookAndFeel(LOOK_AND_FEEL);
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException
				| UnsupportedLookAndFeelException e) {
					System.out.println("Nepodarilo se nastavit L&F!\n" +
							"L&F "+LOOK_AND_FEEL+" asi neexistuje.");
					e.printStackTrace();
			}	
		
		setVisible(true);
		
	}
	
	public static void setRenderer(Renderer ren){
		renderer = ren;
	}
	
	private static void saveCanvasToImage(File file, String format){
	       
        try {
            ImageIO.write(renderer.getBufferedImage(), format, file);
        } catch(IOException e) {
        	e.printStackTrace();
        }
	}
}
