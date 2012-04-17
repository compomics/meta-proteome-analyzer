package de.mpa.client.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Insets;

import javax.swing.UIManager;
import javax.swing.border.Border;

import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.Painter;

/**
 * Helper class for the panel configuration such as JXTitledPanel.
 * @author A. Behne, T. Muth
 *
 */
public class PanelConfig {
	
	/**
	 * The titled font.
	 */
	private static Font ttlFont;
	
	/**
	 * The titled painter.
	 */
	private static Painter ttlPainter;
	
	/**
	 * The titled border.
	 */
	private static Border ttlBorder;
	
	/**
	 * The title panel foreground.
	 */
	private static Color ttlForeground;
	
	/**
	 * Initializes the titled panel and its corresponding variables.
	 */
	private static void init() {
		// Titled panel variables
		JXTitledPanel dummyPnl = new JXTitledPanel(" ");
		UIManager.put("JXTitledPanel.leftDecorationInsets", new Insets(0, 6, 0, 0));
		
		ttlFont = dummyPnl.getTitleFont().deriveFont(Font.BOLD);
		ttlPainter = new MattePainter(new GradientPaint(0, 0, new Color(166, 202, 240), 0, 20, new Color(107, 147, 193)));
		ttlBorder = UIManager.getBorder("TitledBorder.border");
		ttlForeground = dummyPnl.getTitleForeground();
	}

	public static Font getTtlFont() {
		if(ttlFont == null) init();
		return ttlFont;
	}
	

	public static Painter getTtlPainter() {
		if(ttlPainter == null) init();
		return ttlPainter;
	}

	public static Border getTtlBorder() {
		if(ttlBorder == null) init();
		return ttlBorder;
	}

	public static Color getTtlForeground() {
		if(ttlForeground == null) init();
		return ttlForeground;
	}
	
	
	
	
	
}
