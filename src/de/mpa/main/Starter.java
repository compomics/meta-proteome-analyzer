package de.mpa.main;

import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.jgoodies.looks.HeaderStyle;
import com.jgoodies.looks.Options;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.theme.SkyBlue;

import de.mpa.client.ui.ClientFrame;

public class Starter {
	
	/**
	 * This method sets the look&feel for the application.
	 */
	private static void setLookAndFeel() {
		UIManager.put(Options.USE_SYSTEM_FONTS_APP_KEY, Boolean.TRUE);
		Options.setUseSystemFonts(true);
		//Options.setDefaultIconSize(new Dimension(18, 18));
		UIManager.put(Options.HEADER_STYLE_KEY, HeaderStyle.BOTH);
		Options.setPopupDropShadowEnabled(true);
		UIManager.put("OptionPane.buttonOrientation", SwingConstants.RIGHT);
		try {
			// Set Plastic3DLook&Feel as default for all OS.
			Plastic3DLookAndFeel.setPlasticTheme(new SkyBlue());
			UIManager.setLookAndFeel(Plastic3DLookAndFeel.class.getName());
			Options.setPopupDropShadowEnabled(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Main method ==> Entry point to the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		final boolean viewerMode;
		if (args.length > 0) {
			viewerMode = Boolean.parseBoolean(args[0]);
		} else {
			viewerMode = false;
		}
		
		// Set the look&feel
		setLookAndFeel();
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ClientFrame.getInstance(viewerMode);
			}
		});
	}
}
