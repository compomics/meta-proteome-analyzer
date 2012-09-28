package de.mpa.client.ui;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * Custom button UI to create toolbar button-like visuals for title panel components. 
 * 
 * @author A. Behne
 */
public class RoundedHoverButtonUI extends BasicButtonUI {
	
	@Override
	public void update(Graphics g, JComponent c) {
        AbstractButton b = (AbstractButton) c;
		if (b.getModel().isRollover() || b.isSelected()) {
			g.setColor(new Color(255, 255, 255, 159));
			g.fillRoundRect(0, 0, c.getWidth()-2, c.getHeight()-2, 5, 5);

			g.setColor(Color.WHITE);
			g.drawRoundRect(1, 0, c.getWidth()-2, c.getHeight()-2, 5, 5);
			g.drawRoundRect(0, 1, c.getWidth()-2, c.getHeight()-2, 5, 5);
			g.setColor(Color.GRAY);
			g.drawRoundRect(0, 0, c.getWidth()-2, c.getHeight()-2, 5, 5);
        }
        paint(g, c);
        if (b.isSelected()) {
        	paintButtonPressed(g, b);
        }
	}
	
	@Override
	protected void paintButtonPressed(Graphics g, AbstractButton b) {
		if (b.isContentAreaFilled()) {
			g.setColor(new Color(0, 0, 0, 47));
			g.fillRoundRect(0, 0, b.getWidth()-2, b.getHeight()-2, 5, 5);
        }
	}
}
