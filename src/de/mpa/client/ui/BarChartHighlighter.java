package de.mpa.client.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.SwingConstants;

import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.PainterHighlighter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.renderer.JRendererLabel;

/**
 * A simple highlighter for displaying numerical table cell values in a bar chart-like fashion.
 * 
 * @author behne
 */
public class BarChartHighlighter extends PainterHighlighter {

	/**
	 * The minimum value above which the bar gets painted at all.
	 */
	private double minValue;
	
	/**
	 * The maximum value to which the bar height will be scaled.
	 */
	private double maxValue;
	
	/**
	 * The pixel width of the text label. Either <code>SwingConstants.HORIZONTAL</code> 
	 * or <code>SwingConstants.VERTICAL</code>.
	 */
	private int baseline;
	
	/**
	 * The number format to be used on the text label.
	 */
	private NumberFormat formatter;

	/**
	 * Convenience constructor for a bar chart highlighter using a default appearance.
	 */
	public BarChartHighlighter() {
		this(0, 100.0, 50, SwingConstants.HORIZONTAL, Color.GREEN.darker().darker(), Color.GREEN);
	}
	
	/**
	 * Convenience constructor for a bar chart highlighter specifying a single color.
	 * @param color The bar's color.
	 */
	public BarChartHighlighter(Color color) {
		this(color, color);
	}
	
	/**
	 * Convenience constructor for a bar chart highlighter specifying gradient colors.
	 * @param startColor The bar's color at its lower end.
	 * @param endColor The bar's color at its upper end.
	 */
	public BarChartHighlighter(Color startColor, Color endColor) {
		this(startColor, endColor, new DecimalFormat());
	}
	
	/**
	 * Convenience constructor for a bar chart highlighter specifying gradient colors and a number format.
	 * @param startColor The bar's color at its lower end.
	 * @param endColor The bar's color at its upper end.
	 * @param formatter The number format to be used on the text label.
	 */
	public BarChartHighlighter(Color startColor, Color endColor, NumberFormat formatter) {
		this(0, 100.0, 50, SwingConstants.HORIZONTAL, startColor, endColor, formatter);
	}
	
	/**
	 * Constructs a highlighter painting a rectangular bar filled with a gradient color 
	 * mapping the value range between <code>minValue</code> and <code>maxValue</code> to 
	 * a color range from <code>startColor</code> to <code>endColor</code>. Allows painting
	 * a text label next to the bar with a specified <code>baseline</code> width.
	 * 
	 * @param minValue The minimum value above which the bar gets painted at all.
	 * @param maxValue The maximum value to which the bar height will be scaled.
	 * @param baseline The pixel width of the text label.
	 * @param orientation The orientation of the bar. Either <code>SwingConstants.HORIZONTAL</code> 
	 * or <code>SwingConstants.VERTICAL</code>.
	 * @param startColor The bar's color at its lower end.
	 * @param endColor The bar's color at its upper end.
	 */
	public BarChartHighlighter(double minValue, double maxValue, int baseline,
			int orientation, Color startColor, Color endColor) {
		this(minValue, maxValue, baseline, orientation, startColor, endColor, new DecimalFormat());
	}
	
	/**
	 * Constructs a highlighter painting a rectangular bar filled with a gradient color 
	 * mapping the value range between <code>minValue</code> and <code>maxValue</code> to 
	 * a color range from <code>startColor</code> to <code>endColor</code>. Allows painting
	 * a text label next to the bar with a specified <code>baseline</code> width and 
	 * a specified decimal <code>formatter</code>.
	 * 
	 * @param minValue The minimum value above which the bar gets painted at all.
	 * @param maxValue The maximum value to which the bar height will be scaled.
	 * @param baseline The pixel width of the text label.
	 * @param orientation The orientation of the bar.
	 * @param startColor The bar's color at its lower end.
	 * @param endColor The bar's color at its upper end.
	 * @param formatter The number format to be used on the text label.
	 */
	public BarChartHighlighter(double minValue, double maxValue, int baseline,
			int orientation, Color startColor, Color endColor,
			NumberFormat formatter) {
		this(minValue, maxValue, baseline,
				new Point(0, (orientation == SwingConstants.VERTICAL) ? 1 : 0),
				new Point((orientation == SwingConstants.HORIZONTAL) ? 1 : 0, 0),
				startColor, endColor, formatter);
	}
	
	/**
	 * Constructs a highlighter painting a rectangular bar filled with a gradient color 
	 * mapping the value range between <code>minValue</code> and <code>maxValue</code> to 
	 * a color range from <code>startColor</code> to <code>endColor</code>. Allows painting
	 * a text label next to the bar with a specified <code>baseline</code> width and 
	 * a specified decimal <code>formatter</code>.
	 * 
	 * @param minValue The minimum value above which the bar gets painted at all.
	 * @param maxValue The maximum value to which the bar height will be scaled.
	 * @param baseline The pixel width of the text label.
	 * @param startPoint
	 * @param endPoint
	 * @param startColor The bar's color at its lower end.
	 * @param endColor The bar's color at its upper end.
	 * @param formatter The number format to be used on the text label.
	 */
	public BarChartHighlighter(double minValue, double maxValue, int baseline,
			Point2D startPoint, Point2D endPoint, Color startColor, Color endColor,
			NumberFormat formatter) {
		super();
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.baseline = baseline;
		this.formatter = formatter;
		this.setPainter(new BarChartPainter(startPoint, startColor, endPoint, endColor));
	}

	/**
	 * Returns the text label's pixel width. Can be 0 to not display any text at all.
	 * @return The baseline.
	 */
	public int getBaseline() {
		return baseline;
	}

	/**
	 * Sets the text label's pixel width.
	 * @param baseline
	 */
	public void setBaseline(int baseline) {
		this.baseline = baseline;
	}

	/**
	 * Getter for the label's number format.
	 * @return
	 */
	public NumberFormat getFormatter() {
		return formatter;
	}

	/**
	 * Sets both minimum and maximum value of the bar.
	 * @param minValue
	 * @param maxValue
	 */
	public void setRange(double minValue, double maxValue) {
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	
	@Override
	protected Component doHighlight(Component component,
			ComponentAdapter adapter) {
		((BarChartPainter) this.getPainter()).setValue((Number) adapter.getValue());
		return super.doHighlight(component, adapter);
	}

	/**
	 * Class for painting cell contents.
	 */
	private class BarChartPainter extends MattePainter {
		
		/**
		 * The numerical value to be visualized by the bar.
		 */
		private Number value;
		
		/**
		 * Constructs a bar chart painter using a simple acyclic gradient.
		 * 
		 * @param startPoint The gradient's start point.
		 * @param startColor The color at the gradient's start point.
		 * @param endPoint The gradient's end point.
		 * @param endColor The color at the gradient's end point.
		 */
		private BarChartPainter(Point2D startPoint, Color startColor, Point2D endPoint, Color endColor) {
			super(new GradientPaint(startPoint, startColor, endPoint, endColor), true);
		}
		
		@Override
		protected void doPaint(Graphics2D g, Object component, int width, int height) {
			JRendererLabel label = (JRendererLabel) component;
			label.setHorizontalAlignment(SwingConstants.RIGHT);
			// parse value and format label
			double value = this.value.doubleValue();
			label.setText(formatter.format(value));
			// correct value by specified minimum
			value -= minValue;
			double range = maxValue - minValue;
			int xOffset = (baseline > 0) ? baseline + 4 : 2;
			// shrink label to make room for bar
			label.setBounds(0, 0, xOffset, height);
			width -= xOffset + 2;
			// determine clip rectangle
			int x = (int) ((GradientPaint) getFillPaint()).getPoint2().getX();
			int y = (int) ((GradientPaint) getFillPaint()).getPoint1().getY();
			int clipWidth = ((width < 0) ? 0 : (x == 0) ? width : 
				(int) Math.ceil(value/range * width));	// ceil() so always at least 1px gets painted
			int clipHeight = (y == 0) ? (height - 4) : 
				(int) Math.ceil(value/range * (height - 4));
			int yOffset = height - clipHeight - 2;
			// move graphics next to text label and clip painting area
			g.translate(xOffset, 0);
//			g.clip(new Polygon(new int[] { 0, width, width },
//					new int[] { clipHeight + 2, clipHeight + 2, 2 }, 3));
			g.clipRect(0, yOffset, clipWidth, clipHeight);
			super.doPaint(g, component, width, height);
		}

		/**
		 * Sets the numerical value to be used for relative bar heights.
		 * @param value
		 */
		public void setValue(Number value) {
			this.value = value;
		}
		
	}
	
}
