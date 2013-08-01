package de.mpa.client.ui.dialogs;

import java.awt.Container;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JViewport;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import de.mpa.client.ui.icons.IconConstants;

/**
 * Panel implementation for dynamic editing of Cypher query blocks.
 * 
 * @author A. Behne
 */
public abstract class CompoundQueryPanel extends JPanel {
	
	/**
	 * Context menu item for adding building blocks.
	 */
	protected JMenuItem appendItem;
	
	/**
	 * Context menu item for removing building blocks.
	 */
	protected JMenuItem removeItem;
	
	/**
	 * Constructs a panel containing controls for dynamically editing parts
	 * of a Cypher query.
	 * @param label the label
	 * @param items
	 */
	public CompoundQueryPanel(String label, Object[] items) {
		super();
		initComponents(label, items);
	}
	
	/**
	 * Creates and initializes the basic components of this compound query panel.
	 * @param label the text label to be displayed atop the building blocks of this panel
	 * @param items the items to be displayed in the in
	 */
	private void initComponents(String label, Object[] items) {
		FormLayout layout = new FormLayout("3dlu, 21px, p:g, 21px, 3dlu", "3dlu, t:16px, 3dlu");
		
		this.setLayout(layout);
		
		this.add(new JLabel(label), CC.xyw(2, 2, 2));
		
		int row = 2;
		
		if (items != null) {
			layout.appendRow(RowSpec.decode("f:21px"));
			layout.appendRow(RowSpec.decode("3dlu"));
			
			JComboBox comboBox = new JComboBox(items);
			((JTextField) comboBox.getEditor().getEditorComponent()).setMargin(new Insets(1, 3, 2, 1));
			
			this.add(comboBox, CC.xyw(2, 4, 2));
			
			row += 2;
		}

		final JToggleButton blockBtn = new JToggleButton(IconConstants.PLUGIN_ICON);
		blockBtn.setRolloverIcon(IconConstants.PLUGIN_ROLLOVER_ICON);
		blockBtn.setPressedIcon(IconConstants.PLUGIN_PRESSED_ICON);
		blockBtn.setOpaque(false);
		blockBtn.setContentAreaFilled(false);
		blockBtn.setBorder(null);
		blockBtn.setFocusPainted(false);
		blockBtn.setToolTipText("Append/Remove " + label + " Block");

		final JPopupMenu blockPop = new JPopupMenu();
		
		appendItem = new JMenuItem("Append " + label + " Block", IconConstants.ADD_ICON);
		appendItem.setRolloverIcon(IconConstants.ADD_ROLLOVER_ICON);
		appendItem.setPressedIcon(IconConstants.ADD_PRESSED_ICON);
		
		removeItem = new JMenuItem("Remove " + label + " Block", IconConstants.DELETE_ICON);
		removeItem.setRolloverIcon(IconConstants.DELETE_ROLLOVER_ICON);
		removeItem.setPressedIcon(IconConstants.DELETE_PRESSED_ICON);
		removeItem.setEnabled(false);
		
		removeItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				removeBlock();
			}
		});

		appendItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				appendBlock();
			}
		});

		blockPop.add(appendItem);
		blockPop.add(removeItem);
		
		blockBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				blockPop.show(blockBtn, -3, blockBtn.getSize().height / 2 + 11);
			}
		});
		
		this.add(blockBtn, CC.xy(4, row));
	}
	
	/**
	 * Appends a query building block to this panel.
	 */
	public abstract void appendBlock();
	
	/**
	 * Removes a query building block from this panel.
	 */
	public abstract void removeBlock();
	
	/**
	 * Scrolls this panel's parent viewport to the bottom.
	 */
	protected void scrollToBottom() {
		Container parent = this.getParent();
		if (parent instanceof JViewport) {
			((JViewport) parent).setViewPosition(new Point(0, this.getHeight()));
		}
	}
}
