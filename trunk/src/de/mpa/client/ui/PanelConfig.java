package de.mpa.client.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;

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
	 * Initializes the titled panel and its corresponding variables.
	 */
	private static void init() {
		// Titled panel variables
		JXTitledPanel dummyPnl = new JXTitledPanel(" ");
		
		ttlFont = dummyPnl.getTitleFont().deriveFont(Font.BOLD);
		ttlPainter = new MattePainter(new GradientPaint(0, 0, new Color(166, 202, 240), 0, 20, new Color(107, 147, 193)));
		ttlBorder = UIManager.getBorder("TitledBorder.border");
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
	
	
}
