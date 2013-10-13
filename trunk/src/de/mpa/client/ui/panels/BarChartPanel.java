package de.mpa.client.ui.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import de.mpa.util.ColorUtils;

/**
 * Panel implementation for painting bar chart-like data
 * 
 * @author A. Behne
 */
public class BarChartPanel extends JPanel {

	/**
	 * Label for left-hand total value.
	 */
	private JLabel totalLbl;

	/**
	 * Label for right-hand fractional value.
	 */
	private JLabel fracLbl;

	/**
	 * Constructs a bar chart panel featuring the specified total and fractional labels.
	 * @param totalLbl the left-hand total value label
	 * @param fracLbl the right-hand fractional value label
	 */
	public BarChartPanel(JLabel totalLbl, JLabel fracLbl) {
		this.totalLbl = totalLbl;
		this.fracLbl = fracLbl;
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		Point pt1 = new Point();
		Point pt2 = new Point(getWidth(), 0);
//		g2.setPaint(new GradientPaint(pt1, ColorUtils.DARK_GREEN, pt2,
//				ColorUtils.LIGHT_GREEN));
		g2.setPaint(new GradientPaint(
				pt1, UIManager.getColor("barChartPanel.foregroundStartColor"),
				pt2, UIManager.getColor("barChartPanel.foregroundEndColor")));
		g2.fillRect(0, 0, getWidth(), getHeight());

		try {
			double total = Double.parseDouble(totalLbl.getText());
			if (total > 0.0) {
				double rel = Double.parseDouble(fracLbl.getText()) / total;
				int width = (int) (getWidth() * rel);
//				g2.setPaint(new GradientPaint(pt1, ColorUtils.DARK_ORANGE,
//						pt2, ColorUtils.LIGHT_ORANGE));
//				g2.setPaint(new GradientPaint(pt1, Color.DARK_GRAY,
//						pt2, Color.LIGHT_GRAY));
				g2.setPaint(new GradientPaint(
						pt1, Color.DARK_GRAY,
						pt2, Color.LIGHT_GRAY));
				g2.fillRect(getWidth() - width, 0, width, getHeight());
				String str = String.format("%.1f", rel * 100.0) + "%";
				Rectangle2D bounds = g2.getFontMetrics().getStringBounds(str, g2);

				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				// g2.setPaint(ColorUtils.DARK_ORANGE);
				float x = (float) (getWidth() - width - bounds.getWidth() - 2.0f);
				if (x < 2.0f) {
					x = getWidth() - width + 2.0f;
				}
				float y = (float) (getHeight() - bounds.getY()) / 2.0f - 1.0f;
				g2.setPaint(Color.BLACK);
				g2.drawString(str, x + 1.0f, y + 1.0f);
				g2.setPaint(Color.GRAY);
				g2.drawString(str, x + 1.0f, y + 1.0f);
				g2.setPaint(Color.WHITE);
				g2.drawString(str, x, y);
				g2.drawString(str, x, y);
			}
		} catch (Exception e) {
			// catch NPEs and failed parse attempts, draw error message
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			String str = e.toString();
			Rectangle2D bounds = g2.getFontMetrics().getStringBounds(str, g2);
			float y = (float) (getHeight() - bounds.getY()) / 2.0f - 2.0f;
			g2.setPaint(ColorUtils.DARK_RED);
			g2.drawString(str, 2.0f, y + 1.0f);
			g2.setPaint(Color.RED);
			g2.drawString(str, 2.0f, y + 1.0f);
			g2.setPaint(Color.ORANGE);
			g2.drawString(str, 1.0f, y);
		}
	};
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(50,
				Math.max(totalLbl.getPreferredSize().height, fracLbl.getPreferredSize().height));
	}
	
}