package de.mpa.client.ui;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JCheckBox;
import javax.swing.UIManager;

/**
 * Custom check box capable of displaying an indeterminate/partially selected state.<br>
 * Subclasses need to implement the condition for this indeterminate state.
 * 
 * @author A. Behne
 */
public abstract class TriStateCheckBox extends JCheckBox {
	
	/**
	 * The horizontal offset for painting the background and indeterminate mark
	 */
	private int hOffset;

	/**
	 * The horizontal offset for painting the background and indeterminate mark
	 */
	private int vOffset;
	
	/**
	 * Constructs a default tri-state checkbox.
	 */
	public TriStateCheckBox() {
		this(0);
	}
	
	/**
	 * Constructs a tri-state check box with a specified horizontal offset.
	 * @param hOffset the horizontal pixel offset
	 */
	public TriStateCheckBox(int hOffset) {
		this(hOffset, 0);
	}

	/**
	 * Constructs a tri-state check box with specified horizontal and 
	 * vertical offsets.
	 * @param hOffset the horizontal pixel offset
	 * @param vOffset the vertical pixel offset
	 */
	public TriStateCheckBox(int hOffset, int vOffset) {
		super();
		setBackground(UIManager.getColor("Table.background"));
		this.hOffset = hOffset;
		this.vOffset = vOffset;
	}
	
	@Override
	public void paint(Graphics g) {
		// paint background rectangle
		if (isEnabled()) {
			g.setColor(getBackground());
			g.fillRect(hOffset-2, 2+vOffset, 11, 11);
		}
		super.paint(g);
		// paint black bar on top of deselected box to visualize indeterminate state
		if (isPartiallySelected()) {
			g.setColor(getForeground());
			g.fillRect(hOffset, 7+vOffset, 8, 2);
		}
	}
	
	@Override
	public Color getForeground() {
		if (isEnabled()) {
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
