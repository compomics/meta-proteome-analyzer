package de.mpa.client.ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.SizeRequirements;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.InlineView;
import javax.swing.text.html.ParagraphView;

import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.Painter;

import de.mpa.client.Constants.UIColor;

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
		// Store initial values in UI manager
		UIManager.put("JXTitledPanel.leftDecorationInsets", new Insets(0, 6, 0, 0));
		
		Color startColor = UIColor.BAR_CHART_PANEL_FOREGROUND_START_COLOR.getColor();
		Color endColor = UIColor.BAR_CHART_PANEL_FOREGROUND_END_COLOR.getColor();
		
		// Titled panel variables
		JXTitledPanel dummyPnl = new JXTitledPanel(" ");
		
		ttlFont = dummyPnl.getTitleFont().deriveFont(Font.BOLD);
		
//		List<Comparable> keys = new ArrayList<Comparable>();
//		for (Entry<Object, Object> entry : UIManager.getDefaults().entrySet()) {
//			if (entry.getValue() instanceof Color) {
////				System.out.println(entry.getKey());
//				keys.add((Comparable) entry.getKey());
//			}
//		}
//		Collections.sort(keys);
//		for (Object key : keys) {
//			System.out.println(key);
//		}
		
		GradientPaint paint = new GradientPaint(0, 0, startColor, 0, 1, endColor) {
			@Override
			public Color getColor1() {
				return UIManager.getColor("titledPanel.startColor");
			}
			@Override
			public Color getColor2() {
				return UIManager.getColor("titledPanel.endColor");
			}
		};
		ttlPainter = new MattePainter(paint, true);
		ttlBorder = UIManager.getBorder("TitledBorder.border");
//		ttlForeground = dummyPnl.getTitleForeground();
		ttlForeground = new Color(0) {
			@Override
			public int getRGB() {
				// fetch color from UI manager
				return UIManager.getColor("titledPanel.fontColor").getRGB();
			}
		};
	}

	public static Font getTitleFont() {
		if (ttlFont == null) init();
		return ttlFont;
	}

	public static Painter getTitlePainter() {
		if (ttlPainter == null) init();
		return ttlPainter;
	}

	public static Border getTitleBorder() {
		if (ttlBorder == null) init();
		return ttlBorder;
	}

	public static Color getTitleForeground() {
		if (ttlForeground == null) init();
		return ttlForeground;
	}
	
	/**
	 * Factory method to create a pre-formatted titled panel.
	 * @param title The title string to be displayed.
	 * @param content The container around which the panel shall be wrapped.
	 * @return A pre-formatted titled panel.
	 */
	public static JXTitledPanel createTitledPanel(String title, Container content) {
		return createTitledPanel(title, content, null, null);
	}
	
	/**
	 * Factory method to create a pre-formatted titled panel with decoration components.
	 * @param title The title string to be displayed.
	 * @param content The container around which the panel shall be wrapped.
	 * @param leftDecoration The decoration component to be displayed in the top left corner.
	 * @param rightDecoration The decoration component to be displayed in the top right corner.
	 * @return A pre-formatted titled panel.
	 */
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
		panel.setTitleFont(getTitleFont());
		panel.setTitlePainter(getTitlePainter());
		panel.setBorder(getTitleBorder());
//		panel.setTitleForeground(getTitleForeground());
		return panel;
	}
	
	public static EditorKit getLetterWrapEditorKit() {
		return new HTMLEditorKit() {
			@Override
			public ViewFactory getViewFactory() {
				return new HTMLFactory() {
					public View create(Element e) {
						View v = super.create(e);
						if (v instanceof InlineView) {
							return new InlineView(e) {
								public int getBreakWeight(int axis, float pos,
										float len) {
									return GoodBreakWeight;
								}
								public View breakView(int axis, int p0,
										float pos, float len) {
									if (axis == View.X_AXIS) {
										checkPainter();
										int p1 = getGlyphPainter()
												.getBoundedPosition(this, p0,
														pos, len);
										if (p0 == getStartOffset()
												&& p1 == getEndOffset()) {
											return this;
										}
										return createFragment(p0, p1);
									}
									return this;
								}
							};
						} else if (v instanceof ParagraphView) {
							return new ParagraphView(e) {
								protected SizeRequirements calculateMinorAxisRequirements(
										int axis, SizeRequirements r) {
									if (r == null) {
										r = new SizeRequirements();
									}
									float pref = layoutPool.getPreferredSpan(axis);
									float min = layoutPool.getMinimumSpan(axis);
									// Don't include insets, Box.getXXXSpan will
									// include them.
									r.minimum = (int) min;
									r.preferred = Math.max(r.minimum,
											(int) pref);
									r.maximum = Integer.MAX_VALUE;
									r.alignment = 0.5f;
									return r;
								}

							};
						}
						return v;
					}
				};
			}
		};
	}
}
