package de.mpa.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jfree.layout.FormatLayout;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Constants;

public class SplashScreen extends JFrame implements Runnable {
	
	/**
	 * Serialization ID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Helper boolean variable.
	 */
	private boolean paintCalled = false;
	
	/**
	 * Spash sreen image.
	 */
	private BufferedImage image;
	
	/**
	 * SplashScreen constructor.
	 */
	public SplashScreen() {
		super();
		initComponents();
	}
	
	/**
	 * Initialize the UI components.
	 */
	private void initComponents() {
		try {
			this.image = ImageIO.read(getClass().getResource(Constants.SPLASHSCREEN_IMAGE_LOCATION));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	    Container contentPane = getContentPane();
	    contentPane.setLayout(new FormLayout("p", "t:p:g, b:p:g"));
//	    contentPane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 10));
		contentPane.setBackground(Color.white);
		
		// Version label
		JLabel versionLbl = new JLabel("Version: " + Constants.VER_NUMBER + " ", JLabel.RIGHT);
		versionLbl.setOpaque(false);
		versionLbl.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		// Image label
	    JLabel imageLbl = new JLabel(new ImageIcon(image), JLabel.CENTER);
	    
	    // Copyright label
	    JLabel copyrightLbl = new JLabel("\u00a92014 - Max Planck Institute Magdeburg / Germany", JLabel.CENTER);
	    copyrightLbl.setFont(new Font("Sans-Serif", Font.BOLD, 12));
	    copyrightLbl.setOpaque(false);
	    copyrightLbl.setBorder(BorderFactory.createEmptyBorder(2, 2, 4, 2));
		contentPane.add(versionLbl, CC.xy(1, 1));
		contentPane.add(copyrightLbl, CC.xy(1, 2));
		contentPane.add(imageLbl, CC.xywh(1, 1, 1, 2));
		this.setUndecorated(true);
		this.setAlwaysOnTop(true);
		this.setSize(image.getWidth(), image.getHeight());
		this.setBackground(Color.BLACK);
		this.setLocationRelativeTo(null);
	}
	
	/**
	 * This method closes the splash screen.
	 */
	public void close() {
		setVisible(false);
		dispose();
	}
	
	
	@Override
	public void update(Graphics g) {
		paint(g);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (!paintCalled) {
			paintCalled = true;
			synchronized (this) {
				notifyAll();
			}
		}
	}

	/**
	 * Run-method overwritten.
	 */
	@Override
	public void run() {
		initComponents();
		setVisible(true);
	}
}
