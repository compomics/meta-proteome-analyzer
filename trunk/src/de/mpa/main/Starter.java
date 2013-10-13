package de.mpa.main;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.util.Locale;

import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.log4j.Logger;

import com.jgoodies.looks.HeaderStyle;
import com.jgoodies.looks.Options;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.theme.SkyBlue;

import de.mpa.client.ui.ClientFrame;

/**
 * Starter class for the main application.
 * @author T. Muth
 *
 */
public class Starter {
	
	private static boolean jarExport = false;
	private static Logger log = Logger.getLogger(Starter.class);
	private final static boolean LOCK_ACTIVE = true;
	
	/**
	 * This method sets the look&feel for the application.
	 */
	private static void setLookAndFeel() {
		UIManager.put(Options.USE_SYSTEM_FONTS_APP_KEY, Boolean.TRUE);
		Options.setUseSystemFonts(true);
		UIManager.put(Options.HEADER_STYLE_KEY, HeaderStyle.BOTH);
		Options.setPopupDropShadowEnabled(true);
		UIManager.put("OptionPane.buttonOrientation", SwingConstants.RIGHT);
		Locale.setDefault(Locale.US);
		try {
			// Set Plastic3DLook&Feel as default for all OS.
			SkyBlue skyBlue = new SkyBlue();
			Plastic3DLookAndFeel.setPlasticTheme(skyBlue);
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
	public static void main(final String[] args) {
		// Set the look&feel
		setLookAndFeel();
		
		// Lock file instance.
		boolean unlocked = true;
		if (LOCK_ACTIVE) unlocked = lockInstance("filelock");
		
		if (unlocked) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					boolean viewerMode = false;
					boolean debugMode = false;
					if (args.length > 0) {
						for(String arg : args) {
							if(arg.equalsIgnoreCase("-debug")) {
								debugMode = true;
							} else if (arg.equalsIgnoreCase("-viewer")) {
								viewerMode = true;
							}
						}
					} 
					if (jarExport) ClientFrame.getInstance(viewerMode, debugMode);
					else ClientFrame.getInstance(viewerMode, debugMode);
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
	 * @return True if the application is being exported as jar else false.
	 */
	public static boolean isJarExport() {
		return jarExport;
	}
	
	/**
	 * Locks an instance to a file by random access.
	 * @param lockFile Lock file string.
	 * @return True if the file has been locked.
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
}
