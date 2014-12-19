package gui;

import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Menu extends JFrame implements ActionListener {

	public JComboBox objectList, textureList, coordList, lightList, mappingList;
	public JCheckBox renderTexture;

	String[] functionChoices = { "Saddle (Cartesian)", "Sombrero (Cylindrical)", "Sphere (Spherical)", "Snake (Cartesian)", "Bumpy Sphere(Spherical)", "Cone (Cartesian)", "Glass (Cylindrical)" };

	String[] textureChoices = { "Zemì (Beta)", "Bricks"}; //, "Zemì" neni dodelany n_texture
	String[] lightChoices = { "Per Vertex", "Per Pixel" };
	
	String[] mappingChoices = { "Normal Mapping", "Paralax Mapping" };
	private static final String LOOK_AND_FEEL = "javax.swing.plaf.nimbus.NimbusLookAndFeel";
	public float renderObject = 0;
	
	public void actionPerformed(ActionEvent e) {
		
		renderObject = objectList.getSelectedIndex();
		/*tady by se mohli loadovat textury za behu a a renderer by si je vybiral*/
		if (lightList.getSelectedIndex() == 0){
			mappingList.setEnabled(false);
		}else {
			mappingList.setEnabled(true);
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
		objectList = new JComboBox(functionChoices);
		objectList.setSize(100, 20);
		objectPanel.add(new JLabel("Funkce: "));
		objectPanel.add(objectList);

		JPanel objectPanel2 = new JPanel();
		textureList = new JComboBox(textureChoices);
		textureList.setSize(100, 20);
		objectPanel2.add(new JLabel("Textura: "));
		objectPanel2.add(textureList);
		
		textureList.setSelectedIndex(1);
		
		JPanel objectPanel3 = new JPanel();
		lightList = new JComboBox(lightChoices);
		lightList.setSize(100, 20);
		objectPanel3.add(new JLabel("Osvìtlení: "));
		objectPanel3.add(lightList);
		
		
		JPanel objectPanel4 = new JPanel();
		mappingList = new JComboBox(mappingChoices);
		mappingList.setSize(100, 20);
		objectPanel4.add(new JLabel("Typ mapování: "));
		objectPanel4.add(mappingList);
		
		add(objectPanel);
		add(objectPanel2);
		add(objectPanel3);
		add(objectPanel4);
		
		JPanel pTxt = new JPanel();
		
		
		renderTexture = new JCheckBox("With Texture");
		renderTexture.setSelected(true);
		mappingList.setEnabled(false);
		add(renderTexture);
		objectList.addActionListener(this);

		renderTexture.addActionListener(this);
		lightList.addActionListener(this);
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

}
