package de.mpa.client.ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.Border;

import de.mpa.client.Constants;
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
	@SuppressWarnings("rawtypes")
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
		// Store initial values in UI manager
		UIManager.put("JXTitledPanel.leftDecorationInsets", new Insets(0, 6, 0, 0));
		
		// Titled panel variables
		JXTitledPanel dummyPnl = new JXTitledPanel(" ");

        PanelConfig.ttlFont = dummyPnl.getTitleFont().deriveFont(Font.BOLD);
		
		Color dummyCol = new Color(0);
		GradientPaint paint = new GradientPaint(0, 0, dummyCol, 0, 1, dummyCol) {
			@Override
			public Color getColor1() {
				return Constants.UIColor.TITLED_PANEL_START_COLOR.getColor();
			}
			@Override
			public Color getColor2() {
				return Constants.UIColor.TITLED_PANEL_END_COLOR.getColor();
			}
		};
		ttlPainter = new MattePainter(paint, true);

		ttlBorder = UIManager.getBorder("TitledBorder.border");

//		ttlForeground = dummyPnl.getTitleForeground();
		ttlForeground = Constants.UIColor.TITLED_PANEL_FONT_COLOR.getDelegateColor();
	}

	public static Font getTitleFont() {
		if (PanelConfig.ttlFont == null) PanelConfig.init();
		return PanelConfig.ttlFont;
	}

	@SuppressWarnings("rawtypes")
	public static Painter getTitlePainter() {
		if (PanelConfig.ttlPainter == null) PanelConfig.init();
		return PanelConfig.ttlPainter;
	}

	public static Border getTitleBorder() {
		if (PanelConfig.ttlBorder == null) PanelConfig.init();
		return PanelConfig.ttlBorder;
	}

	public static Color getTitleForeground() {
		if (PanelConfig.ttlForeground == null) PanelConfig.init();
		return PanelConfig.ttlForeground;
	}
	
	/**
	 * Factory method to create a pre-formatted titled panel.
	 * @param title The title string to be displayed.
	 * @param content The container around which the panel shall be wrapped.
	 * @return A pre-formatted titled panel.
	 */
	public static JXTitledPanel createTitledPanel(String title, Container content) {
		return PanelConfig.createTitledPanel(title, content, null, null);
	}
	
	/**
	 * Factory method to create a pre-formatted titled panel with decoration components.
	 * @param title The title string to be displayed.
	 * @param content The container around which the panel shall be wrapped.
	 * @param leftDecoration The decoration component to be displayed in the top left corner.
	 * @param rightDecoration The decoration component to be displayed in the top right corner.
	 * @return A pre-formatted titled panel.
	 */
	@SuppressWarnings("serial")
	public static JXTitledPanel createTitledPanel(String title, Container content,
			JComponent leftDecoration, JComponent rightDecoration) {
		JXTitledPanel panel = new JXTitledPanel(title, content) {
			@Override
			public Color getTitleForeground() {
				return PanelConfig.getTitleForeground();
			}
		};
		panel.setLeftDecoration(leftDecoration);
		panel.setRightDecoration(rightDecoration);
        PanelConfig.decorate(panel);
		return panel;
	}
	
	/**
	 * Convenience method to apply font, painter and border styles to the
	 * provided titled panel.
	 * @param panel the titled panel to decorate
	 */
	public static void decorate(JXTitledPanel panel) {
		panel.setTitleFont(PanelConfig.getTitleFont());
		panel.setTitlePainter(PanelConfig.getTitlePainter());
		panel.setBorder(PanelConfig.getTitleBorder());
	}
}
