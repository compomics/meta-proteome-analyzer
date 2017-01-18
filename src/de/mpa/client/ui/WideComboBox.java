package de.mpa.client.ui;

import java.awt.Dimension;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

/**
 * Combo box implementation capable of showing extra-wide popups.<br>
 * Based on code by <a href="http://www.jroller.com/santhosh/entry/make_jcombobox_popup_wide_enough">
 * Santhosh Kumar</a>
 * 
 * @author A. Behne
 */
public class WideComboBox extends JComboBox {

	/**
	 * Flag denoting whether this component is currently laying out its
	 * sub-components.
	 */
	private boolean layingOut = false;

	/** 
     * Creates a <code>JComboBox</code> that contains the elements
     * in the specified array.  By default the first item in the array
     * (and therefore the data model) becomes selected.
     *
     * @param items  an array of objects to insert into the combo box
     * @see DefaultComboBoxModel
     */
	public WideComboBox(Object[] items) {
		super(items);
	}

	@Override
	public void doLayout() {
		try {
			this.layingOut = true;
			super.doLayout();
		} finally {
			this.layingOut = false;
		}
	}

	@Override
	public Dimension getSize() {
		Dimension dim = super.getSize();
		if (!layingOut) {
			dim.width = Math.max(dim.width, this.getPreferredSize().width);
		}
		return dim;
	}
	
}