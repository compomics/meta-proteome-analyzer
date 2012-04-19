package de.mpa.client.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Insets;

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

	public static Font getTitleFont() {
		if(ttlFont == null) init();
		return ttlFont;
	}
	

	public static Painter getTitlePainter() {
		if(ttlPainter == null) init();
		return ttlPainter;
	}

	public static Border getTitleBorder() {
		if(ttlBorder == null) init();
		return ttlBorder;
	}

	public static Color getTitleForeground() {
		if(ttlForeground == null) init();
		return ttlForeground;
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
