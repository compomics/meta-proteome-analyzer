package de.mpa.client.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.BevelBorder;

/**
 * A class which implements a simple one-line bevel border.
 * 
 * @author Alex Behne
 */
public class ThinBevelBorder extends BevelBorder {
	
	private Insets insets;

	/**
	 * Creates a thin bevel border with the specified type and
	 * whose colors will be derived from the background color
	 * of the component passed into the paintBorder method.
	 * @param bevelType the type of bevel for the border
	 */
	public ThinBevelBorder(int bevelType) {
		this(bevelType, new Insets(1, 1, 1, 1));
	}
	
	public ThinBevelBorder(int bevelType, Insets insets) {
		super(bevelType);
		this.insets = insets;
	}
	
	@Override
	public Insets getBorderInsets(Component c) {
		return insets;
	}
    
	@Override
	public Insets getBorderInsets(Component c, Insets insets) {
		insets.set(this.insets.top, this.insets.left, 
				this.insets.bottom, this.insets.right);
		return insets;
	}
	
	protected void paintRaisedBevel(Component c, Graphics g,
									int x, int y,
									int width, int height)  {
		Color oldColor = g.getColor();
		int h = height;
		int w = width;

		g.translate(x, y);

		g.setColor(getHighlightOuterColor(c));
		g.drawLine(insets.left-1, insets.top-1, insets.left-1, h-insets.bottom);	// left
		g.drawLine(insets.left, insets.top-1, w-insets.right, insets.top-1);		// top

		g.setColor(getShadowOuterColor(c));
		g.drawLine(insets.left, h-insets.bottom, w-insets.right, h-insets.bottom);	// bottom
		g.drawLine(w-insets.right, insets.top, w-insets.right, h-insets.bottom-1);	// right

		g.translate(-x, -y);
		g.setColor(oldColor);

	}

	protected void paintLoweredBevel(Component c, Graphics g,
									 int x, int y,
									 int width, int height)  {
		Color oldColor = g.getColor();
		int h = height;
		int w = width;

		g.translate(x, y);

		g.setColor(getShadowInnerColor(c));
		g.drawLine(insets.left-1, insets.top-1, insets.left-1, h-insets.bottom);	// left
		g.drawLine(insets.left, insets.top-1, w-insets.right, insets.top-1);		// top

		g.setColor(getHighlightOuterColor(c));
		g.drawLine(insets.left, h-insets.bottom, w-insets.right, h-insets.bottom);	// bottom
		g.drawLine(w-insets.right, insets.top, w-insets.right, h-insets.bottom-1);	// right

		g.translate(-x, -y);
		g.setColor(oldColor);

	}
}
