package de.mpa.main;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.SplashScreen;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.plaf.ColorUIResource;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import com.jgoodies.looks.HeaderStyle;
import com.jgoodies.looks.Options;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.theme.SkyBlue;

import de.mpa.client.Constants;
import de.mpa.client.Constants.UIColor;
import de.mpa.client.Constants.UITheme;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.DelegateColor;
import de.mpa.client.ui.ThinBevelBorder;

/**
 * Starter class for the main application.
 * 
 * @author T. Muth
 */
public class Starter {
	
	/**
	 * Flag denoting whether the application is in jar export mode.
	 */
	private static boolean jarExport = false;
	
	/**
	 * The logger instance.
	 */
	private static Logger log = Logger.getLogger(Starter.class);
																										
	/**
	 * Flag denoting whether an application lock is in effect. 
	 */
	private final static boolean LOCK_ACTIVE = true;
	
	/**
	 * This method sets the look&feel for the application.
	 */
	private static void setLookAndFeel() {
		try {
			// Read theme configuration files
			File themesFolder;
			if (isJarExport()) {
				themesFolder = new File(Constants.THEME_FOLDER_JAR);
			} else {
				themesFolder = new File(Constants.THEME_FOLDER);
			}
			
			List<UITheme> themes = new ArrayList<UITheme>();
			UITheme defaultTheme = null;
			for (File themeFile : themesFolder.listFiles()) {
				if (themeFile.getName().endsWith(".theme")) {
					UITheme theme = new UITheme(themeFile);
					if (Constants.DEFAULT_THEME_NAME.equals(theme.getTitle())) {
						defaultTheme = theme;
					}
					themes.add(theme);
				}
			}
			
			// Apply default theme
			if (defaultTheme == null) {
				defaultTheme = Constants.DEFAULT_THEME;
				themes.add(0, defaultTheme);
			}
			defaultTheme.applyTheme();
			// finalize list of themes
			Constants.THEMES = Collections.unmodifiableList(themes);
			
			// Set Plastic3DLook&Feel as default for all operating systems
			Plastic3DLookAndFeel.setPlasticTheme(new SkyBlue() {
				// replace theme-based color defaults with dynamic delegate colors
				@Override
				public ColorUIResource getFocusColor() {
					return UIColor.BUTTON_FOCUS_COLOR.getDelegateColor();
				}
			});
			UIManager.setLookAndFeel(Plastic3DLookAndFeel.class.getName());
			
			Options.setUseSystemFonts(true);
			Options.setPopupDropShadowEnabled(false);
			
			UIManager.put(Options.HEADER_STYLE_KEY, HeaderStyle.BOTH);
			UIManager.put("OptionPane.buttonOrientation", SwingConstants.RIGHT);
			
			// replace UI manager-based color defaults with dynamic delegate colors
			UIManager.put("Focus.color", UIColor.BUTTON_FOCUS_COLOR.getDelegateColor());
			
			UIManager.put("ScrollBar.thumb", UIColor.SCROLLBAR_THUMB_COLOR.getDelegateColor());

			DelegateColor textFontCol = UIColor.TEXT_SELECTION_FONT_COLOR.getDelegateColor();
			UIManager.put("TextArea.selectionForeground", textFontCol);
			UIManager.put("TextField.selectionForeground", textFontCol);
			UIManager.put("PasswordField.selectionForeground", textFontCol);
			UIManager.put("FormattedTextField.selectionForeground", textFontCol);
			DelegateColor textBackCol = UIColor.TEXT_SELECTION_BACKGROUND_COLOR.getDelegateColor();
			UIManager.put("TextArea.selectionBackground", textBackCol);
			UIManager.put("TextField.selectionBackground", textBackCol);
			UIManager.put("PasswordField.selectionBackground", textBackCol);
			UIManager.put("FormattedTextField.selectionBackground", textBackCol);
			
			UIManager.put("Table.selectionBackground", UIColor.TABLE_SELECTION_COLOR.getDelegateColor());
			UIManager.put("List.selectionBackground", UIColor.TABLE_SELECTION_COLOR.getDelegateColor());
			Border fchb = BorderFactory.createLineBorder(UIColor.TABLE_FOCUS_HIGHLIGHT_COLOR.getDelegateColor());
			UIManager.put("Table.focusCellHighlightBorder", fchb);
			UIManager.put("List.focusCellHighlightBorder", fchb);
			
//			UIManager.put("ProgressBar.foreground", new GradientColorAdapter(
//					UIColor.PROGRESS_BAR_START_COLOR.getDelegateColor(),
//					UIColor.PROGRESS_BAR_END_COLOR.getDelegateColor()));
			UIManager.put("ProgressBar.foreground", UIColor.PROGRESS_BAR_FOREGROUND_COLOR.getDelegateColor());

			UIManager.put("TaskPaneContainer.background",
					UIColor.TASK_PANE_BACKGROUND_COLOR.getDelegateColor());
			UIManager.put("TaskPaneContainer.border", BorderFactory.createCompoundBorder(
					new ThinBevelBorder(BevelBorder.LOWERED),
					BorderFactory.createEmptyBorder(10, 10, 10, 10)));
			UIManager.put("TaskPane.titleForeground", UIColor.TITLED_PANEL_FONT_COLOR.getDelegateColor());
			UIManager.put("TaskPane.titleBackgroundGradientStart", UIColor.TITLED_PANEL_START_COLOR.getDelegateColor());
			UIManager.put("TaskPane.titleBackgroundGradientEnd", UIColor.TITLED_PANEL_END_COLOR.getDelegateColor());
			UIManager.put("TaskPane.titleOver", UIColor.TITLED_PANEL_END_COLOR.getDelegateColor().darker().darker());
			UIManager.put("TaskPane.borderColor", UIColor.TITLED_PANEL_END_COLOR.getDelegateColor());
			
			Locale.setDefault(Locale.US);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Main method ==> Entry point to the application.
	 * 
	 * @param args
	 */
	public static void main(final String[] args) {
		
		// Lock file instance.
		boolean unlocked = false;
		if (LOCK_ACTIVE) {
			unlocked = lockInstance("filelock");
		}
		
		if (unlocked) {
//			// Display splash screen
			new Thread(new SplashRunnable()).start();
			
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						// Set the look&feel
						setLookAndFeel();
						
						boolean viewerMode = false;
						boolean debugMode = false;
						if (args.length > 0) {
							for (String arg : args) {
								if (arg.equalsIgnoreCase("-debug")) {
									debugMode = true;
								} else if (arg.equalsIgnoreCase("-viewer")) {
									viewerMode = true;
								}  
							}
						}
						ClientFrame clientFrame = ClientFrame.getInstance(viewerMode, debugMode);
						clientFrame.toFront();
						
					} catch (Exception e) {
						JXErrorPane.showDialog(null, new ErrorInfo(
								"Error", "The application could not be launched due to an error.",
								e.getMessage(), null, e, Level.SEVERE, null));
					}
					
					SplashScreen splashScreen = SplashScreen.getSplashScreen();
					if (splashScreen != null) {
						splashScreen.close();
					}
				}
			});
		}
	}
	
	/**
	 * Helper method to retrieve the jar file path.
	 * @return full path to jar file.
	 */
	public static String getJarFilePath() {
		String path = Starter.class.getResource("Starter.class").getPath();
		// remove starting 'file:' tag if there
		if (path.startsWith("file:")) {
			path = path.substring("file:".length(), path.indexOf("mpa"));
		} else {
			path = path.substring(0, path.indexOf("mpa"));
		}
		path = path.replace("%20", " ");
		path = path.replace("%5b", "[");
		path = path.replace("%5d", "]");
		return path;
	}
	/**
	 * Checks whether the application is a jar export or not.
	 * @return <code>true</code> if the application is being exported as jar, otherwise <code>false</code>.
	 */
	public static boolean isJarExport() {
		return jarExport;
	}
	
	/**
	 * Locks an instance to a file by random access.
	 * @param lockFile Lock file string.
	 * @return <code>true</code> if the instance has been locked.
	 */
	private static boolean lockInstance(final String lockFile) {
	    try {
	        final File file = new File(lockFile);
	        final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
	        final FileLock fileLock = randomAccessFile.getChannel().tryLock();
	        if (fileLock != null) {
	            Runtime.getRuntime().addShutdownHook(new Thread() {
	                public void run() {
	                    try {
	                        fileLock.release();
	                        randomAccessFile.close();
	                        file.delete();
	                    } catch (Exception e) {
	                        log.error("Unable to remove lock file: " + lockFile, e);
	                    }
	                }
	            });
	            return true;
	        }
	    } catch (Exception e) {
	        log.error("Unable to create and/or lock file: " + lockFile, e);
	    }
	    return false;
	}
	
	/**
	 * Custom runnable for painting onto a splash screen image.
	 * @author A. Behne
	 */
	private static class SplashRunnable implements Runnable {

		@Override
		public void run() {
			SplashScreen splashScreen = SplashScreen.getSplashScreen();
			if (splashScreen != null) {
				Graphics2D g2d = splashScreen.createGraphics();
				g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g2d.setColor(new Color(0, 0, 0, 20));
				
				FontMetrics fm = g2d.getFontMetrics();
				int textH = fm.getHeight();
				
				String versionStr = "Version: " + Constants.VER_NUMBER;
				int versionW = fm.stringWidth(versionStr);
				int versionX = splashScreen.getBounds().width - versionW - 5;
				int versionY = textH;
				
				String copyrightStr = "\u00a92014 - Max Planck Institute Magdeburg, Germany";
				int copyrightW = fm.stringWidth(copyrightStr);
				int copyrightX = (splashScreen.getBounds().width - copyrightW) / 2;
				int copyrightY = splashScreen.getBounds().height - 8;
				
				int time = 960;
				int inc = 60;
				for (int i = 0; i < time; i += inc) {
					g2d.drawString(versionStr, versionX, versionY);
					g2d.drawString(copyrightStr, copyrightX, copyrightY);
					splashScreen.update();
					try {
						Thread.sleep(inc);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
	}
	
}