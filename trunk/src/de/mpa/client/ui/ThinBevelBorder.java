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
	
	/**
	 * Creates a thin bevel border with the specified type and
	 * whose colors will be derived from the background color
	 * of the component passed into the paintBorder method.
	 * @param bevelType the type of bevel for the border
	 */
	public ThinBevelBorder(int bevelType) {
		super(bevelType);
	}
	
	/**
	 * Returns the insets of the border.
	 * @param c the component for which this border insets value applies
	 */
	public Insets getBorderInsets(Component c) {
		return new Insets(1, 1, 1, 1);
	}
    
	/**
	 * Reinitialize the insets parameter with this Border's current Insets.
	 * @param c the component for which this border insets value applies
	 * @param insets the object to be reinitialized
	 */
	public Insets getBorderInsets(Component c, Insets insets) {
		insets.set(1, 1, 1, 1);
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
		g.drawLine(0, 0, 0, h-1);
		g.drawLine(1, 0, w-1, 0);

		g.setColor(getShadowOuterColor(c));
		g.drawLine(1, h-1, w-1, h-1);
		g.drawLine(w-1, 1, w-1, h-2);

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
		g.drawLine(0, 0, 0, h-1);
		g.drawLine(1, 0, w-1, 0);

		g.setColor(getHighlightOuterColor(c));
		g.drawLine(1, h-1, w-1, h-1);
		g.drawLine(w-1, 1, w-1, h-2);

		g.translate(-x, -y);
		g.setColor(oldColor);

	}
}
