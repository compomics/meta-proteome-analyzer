package de.mpa.client.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.UIManager;

/**
 * Custom check box capable of displaying an indeterminate/partially selected state.<br>
 * Subclasses need to implement the condition for this indeterminate state.
 * 
 * @author A. Behne
 */
@SuppressWarnings("serial")
public abstract class TriStateCheckBox extends JCheckBox {
	
	/**
	 * Constructs a default tri-state checkbox.
	 */
	public TriStateCheckBox() {
        setBackground(UIManager.getColor("Table.background"));
	}
	
	@Override
	public void paint(Graphics g) {
		// get border margins
		Insets insets = getInsets();
		
		// paint background rectangle
		if (this.isEnabled()) {
			g.setColor(getBackground());
			int x = insets.left;
			int y = insets.top;
			g.fillRect(x, y, 12, 12);
		}
		super.paint(g);
		// paint black bar on top of deselected box to visualize indeterminate state
		if (this.isPartiallySelected()) {
			g.setColor(getForeground());
			int x = insets.left + 2;
			int y = insets.top + 5;
			g.fillRect(x, y, 8, 2);
		}
	}
	
	@Override
	public Color getForeground() {
		if (this.isEnabled()) {
			return super.getForeground();
		} else {
			return UIManager.getColor("controlShadow");
		}
	}
	
	/**
	 * Returns whether this check box is partially selected.
	 * @return <code>true</code> if this check box is partially selected, 
	 * <code>false</code> otherwise
	 */
	public abstract boolean isPartiallySelected();

}
