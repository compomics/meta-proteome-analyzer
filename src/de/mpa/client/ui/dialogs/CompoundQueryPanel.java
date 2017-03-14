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

import de.mpa.client.ui.BoundsPopupMenuListener;
import de.mpa.client.ui.icons.IconConstants;

/**
 * Panel implementation for dynamic editing of Cypher query blocks.
 * 
 * @author A. Behne
 */
@SuppressWarnings("serial")
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
        this.initComponents(label, items);
	}
	
	/**
	 * Creates and initializes the basic components of this compound query panel.
	 * @param label the text label to be displayed atop the building blocks of this panel
	 * @param items the items to be displayed in the in
	 */
	private void initComponents(String label, Object[] items) {
		FormLayout layout = new FormLayout("3dlu, 21px, p:g, 21px, 3dlu", "3dlu, t:16px, 3dlu");

        setLayout(layout);

        add(new JLabel(label), CC.xyw(2, 2, 2));
		
		int row = 2;
		
		if (items != null) {
			layout.appendRow(RowSpec.decode("f:21px"));
			layout.appendRow(RowSpec.decode("3dlu"));
			
			@SuppressWarnings({ "rawtypes", "unchecked" })
			JComboBox comboBox = new JComboBox(items);
			comboBox.addPopupMenuListener(new BoundsPopupMenuListener(true, false));
			((JTextField) comboBox.getEditor().getEditorComponent()).setMargin(new Insets(1, 3, 2, 1));

            add(comboBox, CC.xyw(2, 4, 2));
			
			row += 2;
		}

		JToggleButton blockBtn = new JToggleButton(IconConstants.PLUGIN_ICON);
		blockBtn.setRolloverIcon(IconConstants.PLUGIN_ROLLOVER_ICON);
		blockBtn.setPressedIcon(IconConstants.PLUGIN_PRESSED_ICON);
		blockBtn.setOpaque(false);
		blockBtn.setContentAreaFilled(false);
		blockBtn.setBorder(null);
		blockBtn.setFocusPainted(false);
		blockBtn.setToolTipText("Append/Remove " + label + " Block");

		JPopupMenu blockPop = new JPopupMenu();

        this.appendItem = new JMenuItem("Append " + label + " Block", IconConstants.ADD_ICON);
        this.appendItem.setRolloverIcon(IconConstants.ADD_ROLLOVER_ICON);
        this.appendItem.setPressedIcon(IconConstants.ADD_PRESSED_ICON);

        this.removeItem = new JMenuItem("Remove " + label + " Block", IconConstants.DELETE_ICON);
        this.removeItem.setRolloverIcon(IconConstants.DELETE_ROLLOVER_ICON);
        this.removeItem.setPressedIcon(IconConstants.DELETE_PRESSED_ICON);
        this.removeItem.setEnabled(false);

        this.removeItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
                CompoundQueryPanel.this.removeBlock();
			}
		});

        this.appendItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
                CompoundQueryPanel.this.appendBlock();
			}
		});

		blockPop.add(this.appendItem);
		blockPop.add(this.removeItem);
		
		blockBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				blockPop.show(blockBtn, -3, blockBtn.getSize().height / 2 + 11);
			}
		});

        add(blockBtn, CC.xy(4, row));
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
		Container parent = getParent();
		if (parent instanceof JViewport) {
			((JViewport) parent).setViewPosition(new Point(0, getHeight()));
		}
	}
}
