package de.mpa.main;

import java.io.File;
import java.net.URL;
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
	private static boolean jarExport = true;
	
	/**
	 * This method sets the look&feel for the application.
	 */
	private static void setLookAndFeel() {
		try {
			// Read theme configuration files
			File themesFolder;
			if (isJarExport()) {
				themesFolder = new File(Constants.THEME_FOLDER);
			} else {
				URL url = ClassLoader.getSystemResource(Constants.THEME_FOLDER);
				themesFolder = new File(url.toURI());
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
			UIManager.put("ProgressBar.foreground", UIColor.PROGRESS_BAR_FOREGROUND_COLOR.getDelegateColor());

			UIManager.put("TaskPaneContainer.background", UIColor.TASK_PANE_BACKGROUND_COLOR.getDelegateColor());
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
	
	public static boolean is64bit() {
		String arch = System.getProperty("os.arch").toLowerCase();
		return arch.lastIndexOf("64") != -1; 
	}
	
	public static boolean isWindows() {
		String os = System.getProperty("os.name").toLowerCase();
		return (os.indexOf("win") >= 0);
	}

	public static boolean isMac() {
		String os = System.getProperty("os.name").toLowerCase();
		return (os.indexOf("mac") >= 0);

	}

	public static boolean isUnix() {
		String os = System.getProperty("os.name").toLowerCase();
		return (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0);
	}
	
	/**
	 * Main method ==> Entry point to the application.
	 * 
	 * @param args
	 */
	public static void main(final String[] args) {
		// Display splash screen
		final SplashScreen splashScreen = new SplashScreen();
		splashScreen.run();

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					// Set the look&feel
					setLookAndFeel();
					// Default = no debug mode.
					ClientFrame clientFrame = ClientFrame.getInstance(false);
					clientFrame.toFront();
					splashScreen.close();
				} catch (Exception e) {
					JXErrorPane.showDialog(null, new ErrorInfo("Error", "The application could not be launched due to an error.", e.getMessage(), null, e, Level.SEVERE, null));
				}
			}
		});
		

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
}