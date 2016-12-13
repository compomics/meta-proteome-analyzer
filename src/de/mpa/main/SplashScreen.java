package de.mpa.main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import de.mpa.client.Constants;

/**
 * A splash screen frame to be displayed when the main application launches.
 * 
 * @author T. Muth, A. Behne
 */
public class SplashScreen extends JFrame implements Runnable {
	
	/**
	 * Creates the splash screen.
	 */
	public SplashScreen() {
		super(Constants.APPTITLE + " " + Constants.VER_NUMBER + " ");
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(
				this.getClass().getResource("/de/mpa/resources/icons/mpa01.png")));
	}
	
	/**
	 * Initialize the UI components.
	 */
	private void initComponents() {
		try {
			final BufferedImage image = ImageIO.read(getClass().getResource(Constants.SPLASHSCREEN_IMAGE_LOCATION));
			
			// create the background image label
		    JLabel imageLbl = new JLabel(new ImageIcon(image), JLabel.CENTER);
		    imageLbl.setLayout(new BorderLayout(20, 20));
		    imageLbl.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
		    imageLbl.setOpaque(false);
		    
			// create the version label
		    JPanel panel = new JPanel(new BorderLayout());
		    
			JLabel versionLbl1 = new JLabel("Current Version: " + Constants.VER_NUMBER, JLabel.RIGHT);
			JLabel versionLbl2 = new JLabel("Release Date: " + Constants.VER_DATE , JLabel.RIGHT);
			versionLbl1.setOpaque(false);
			versionLbl1.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			versionLbl2.setOpaque(false);
			versionLbl2.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
			panel.setOpaque(false);
			panel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		    panel.add(versionLbl1, BorderLayout.NORTH);
		    panel.add(versionLbl2, BorderLayout.SOUTH);
			
		    // create the copyright label
		    JLabel copyrightLbl = new JLabel("\u00a9" +" MPA Portable Version " + Constants.VER_NUMBER + " (" + Constants.VER_DATE + ")", JLabel.CENTER);
		    copyrightLbl.setFont(new Font("Sans-Serif", Font.BOLD, 14));
		    copyrightLbl.setOpaque(false);
		    copyrightLbl.setBorder(BorderFactory.createEmptyBorder(2, 2, 4, 2));
		    
		    // add text labels to image label
		    imageLbl.add(panel, BorderLayout.NORTH);
		    imageLbl.add(copyrightLbl, BorderLayout.SOUTH);
		    
		    // add image label to content pane
		    this.getContentPane().add(imageLbl);
		    
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Hides and disposes the splash screen.
	 */
	public void close() {
		this.setVisible(false);
		this.dispose();
	}

	@Override
	public void run() {
		// configure frame components
		this.initComponents();
		
		// configure frame visuals
		this.setUndecorated(true);
		this.setAlwaysOnTop(true);
		this.pack();
		
		// move frame to center of screen
		this.setLocationRelativeTo(null);
		
		// show screen
		this.setVisible(true);
		
		try {
			// ensure splash screen is visible for at least a few seconds
			Thread.sleep(2000);
		} catch (Exception e) {
			// hide screen
			this.close();
		}
	}
}
