package de.mpa.client.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.SwingConstants;

/**
 * Convenience class to paint a very small triangular arrow icon.
 * @author A. Behne
 */
public class TinyArrowIcon implements Icon {
	
	/**
	 * The direction in which the arrow is pointing.<br>
	 * Either one of <code>SwingConstants.NORTH</code>,
	 *  <code>EAST</code>, <code>SOUTH</code> or <code>WEST</code>
	 */
	private int direction;
	
	/**
	 * Constructs a very small triangular arrow icon pointing in the specified direction.
	 * @param direction either one of <code>SwingConstants.NORTH</code>,
	 *  <code>EAST</code>, <code>SOUTH</code> or <code>WEST</code>
	 */
	public TinyArrowIcon(int direction) {
		this.direction = direction;
	}
	
	/**
	 * Returns whether the icon is horizontal (i.e. pointing up or down).
	 * @return <code>true</code> if the arrow is pointing up or down,
	 *  <code>false</code> otherwise
	 */
	public boolean isHorizontal() {
		return (this.direction == SwingConstants.LEFT) ||
				(this.direction == SwingConstants.RIGHT);
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		g.setColor(Color.BLACK);
		switch (this.direction) {
		case SwingConstants.NORTH:
			g.drawLine(x - 1, y + 1, x + 3, y + 1);
			g.drawLine(x    , y    , x + 2, y    );
			g.drawLine(x + 1, y - 1, x + 1, y - 1);
			break;
		case SwingConstants.EAST:
			g.drawLine(x - 1, y - 1, x - 1, y + 3);
			g.drawLine(x    , y    , x    , y + 2);
			g.drawLine(x + 1, y + 1, x + 1, y + 1);
			break;
		case SwingConstants.SOUTH:
			g.drawLine(x - 1, y - 1, x + 3, y - 1);
			g.drawLine(x    , y    , x + 2, y    );
			g.drawLine(x + 1, y + 1, x + 1, y + 1);
			break;
		case SwingConstants.WEST:
			g.drawLine(x + 1, y - 1, x + 1, y + 3);
			g.drawLine(x    , y    , x    , y + 2);
			g.drawLine(x - 1, y + 1, x - 1, y + 1);
			break;
		}
	}

	@Override
	public int getIconWidth() {
		if (this.isHorizontal()) {
			return 5;
		} else {
			return 3;
		}
	}

	@Override
	public int getIconHeight() {
		if (this.isHorizontal()) {
			return 3;
		} else {
			return 5;
		}
	}
	
}
