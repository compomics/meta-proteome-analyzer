package de.mpa.client.ui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;

/**
 * Wrapper for the {@link Color} class to redirect to a {@link GradientPaint}
 * context for use with {@link Graphics} objects.
 * 
 * @author A. Behne
 * 
 * @see Graphics2D#setPaint
 */
@SuppressWarnings("serial")
public class GradientColorAdapter extends Color {

	/**
	 * The start color of the gradient.
	 */
	private final Color startColor;
	
	/**
	 * The end color of the gradient.
	 */
	private final Color endColor;

	/**
	 * Flag denoting whether the device bounds or the user bounds should be used
	 * when creating the painting context.
	 */
	private final boolean useDeviceBounds;

	/**
	 * Constructs a gradient color adapter from the specified start and end
	 * colors.
	 * @param startColor the start color
	 * @param endColor the end color
	 */
	public GradientColorAdapter(Color startColor, Color endColor) {
		this(startColor, endColor, false);
	}

	/**
	 * Constructs a gradient color adapter from the specified start and end
	 * colors.
	 * @param startColor the start color
	 * @param endColor the end color
	 */
	public GradientColorAdapter(Color startColor, Color endColor, boolean useDeviceBounds) {
		super(0);
		this.startColor = startColor;
		this.endColor = endColor;
		this.useDeviceBounds = useDeviceBounds;
	}
	
	@Override
	public int getRGB() {
		return 0;
	}
	
	@Override
	public synchronized PaintContext createContext(ColorModel cm, Rectangle r,
			Rectangle2D r2d, AffineTransform xform, RenderingHints hints) {
		Rectangle r2 = (useDeviceBounds) ? r : r2d.getBounds();
		GradientPaint gp = new GradientPaint(
				r2.x, r2.y, startColor,
				r2.x + r2.width, r2.y + r2.height, endColor);
		return gp.createContext(cm, r, r2d, xform, hints);
	}
	
}
