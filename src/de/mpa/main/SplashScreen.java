package de.mpa.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

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
	private Image image;
	
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
		this.image = getToolkit().getImage(getClass().getResource(Constants.SPLASHSCREEN_IMAGE_LOCATION));
		
	    JPanel contentPane = new JPanel(); 
	    contentPane.setLayout(new BorderLayout());
	    contentPane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 10));
		contentPane.setBackground(Color.white);
		
		// Version label
		JLabel versionLbl = new JLabel("MPA Version: " + Constants.VER_NUMBER + " ", JLabel.RIGHT);
		
		// Image label
	    JLabel imageLbl = new JLabel(new ImageIcon(image), JLabel.CENTER);
	    
	    // Copyright label
	    JLabel copyrightLbl = new JLabel("Copyright 2014 - Max Planck Institute Magdeburg / Germany", JLabel.CENTER);
	    copyrightLbl.setFont(new Font("Sans-Serif", Font.BOLD, 12));
		contentPane.add(versionLbl, BorderLayout.NORTH);
		contentPane.add(imageLbl, BorderLayout.CENTER);
		contentPane.add(copyrightLbl, BorderLayout.SOUTH);
		this.setContentPane(contentPane);
		this.setUndecorated(true);
		this.setAlwaysOnTop(true);
		this.setSize(500, 240);
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
