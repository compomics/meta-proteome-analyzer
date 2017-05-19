package de.mpa.client.ui.resultspanel;

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
@SuppressWarnings("serial")
public class BarChartPanel extends JPanel {

	/**
	 * Label for left-hand total value.
	 */
	private final JLabel totalLbl;

	/**
	 * Label for right-hand fractional value.
	 */
	private final JLabel fracLbl;

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
		// paint background gradient
		Graphics2D g2 = (Graphics2D) g;
		int bgWidth = getWidth();
		Point pt1 = new Point(bgWidth, 0);
		Point pt2 = new Point();
		g2.setPaint(new GradientPaint(
				pt1, UIManager.getColor("barChartPanel.backgroundStartColor"),
				pt2, UIManager.getColor("barChartPanel.backgroundEndColor")));
		g2.fillRect(0, 0, bgWidth, getHeight());

		try {
			// parse total and fractional values
			double total = Double.parseDouble(this.totalLbl.getText());
			if (total > 0.0) {
				double rel = Double.parseDouble(this.fracLbl.getText()) / total;
				int barWidth = (int) (bgWidth * rel);
				// paint foreground gradient
				g2.setPaint(new GradientPaint(
						pt1, UIManager.getColor("barChartPanel.foregroundStartColor"),
						pt2, UIManager.getColor("barChartPanel.foregroundEndColor")));
				g2.fillRect(bgWidth - barWidth, 0, barWidth, getHeight());
				
				// calculate label bounds
				String str = String.format("%.1f", rel * 100.0) + "%";
				Rectangle2D strBounds = g2.getFontMetrics().getStringBounds(str, g2);

				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				
				// calculate label position
//				float x = (float) (this.getWidth() - width - bounds.getWidth() - 2.0f);
//				if (x < 2.0f) {
//					x = this.getWidth() - width + 2.0f;
//				}
//				float y = (float) (this.getHeight() - bounds.getY()) / 2.0f - 1.0f;
				float x;
				if (barWidth < (strBounds.getWidth() + 4.0f)) {
					x = (float) (bgWidth - barWidth - strBounds.getWidth() - 2.0f);
				} else {
					x = bgWidth - barWidth + 2.0f;
				}
				float y = (float) (getHeight() - strBounds.getY()) / 2.0f - 1.0f;
				
				// paint label
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
	}

    @Override
	public Dimension getPreferredSize() {
		return new Dimension(0,
				Math.max(this.totalLbl.getPreferredSize().height, this.fracLbl.getPreferredSize().height));
	}
	
}