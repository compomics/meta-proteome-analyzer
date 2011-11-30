package de.mpa.client.ui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

public class CheckBoxTreeCellRenderer extends JPanel implements TreeCellRenderer {
	private JPanel panel = this;
	private CheckBoxTreeSelectionModel selectionModel;
	private TreeCellRenderer delegateTCR;
//	private TristateCheckBox checkBox = new TristateCheckBox("");
	private JCheckBox checkBox = new JCheckBox();

	public CheckBoxTreeCellRenderer(TreeCellRenderer delegateTCR, CheckBoxTreeSelectionModel selectionModel) {
		this.delegateTCR = delegateTCR;
		this.selectionModel = selectionModel;
		panel.setLayout(new BorderLayout());
		panel.setOpaque(false);
		checkBox.setOpaque(false);
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
												  boolean expanded, boolean leaf, int row,
												  boolean hasFocus) {
		Component renderer = delegateTCR.getTreeCellRendererComponent(tree, value, selected,
																   expanded, leaf, row,
																   hasFocus);
		TreePath path = tree.getPathForRow(row);
		if (path != null) {
//			TristateButtonModel tbm = checkBox.getTristateModel();
			JCheckBox tbm = checkBox;
			if (selectionModel.isPathSelected(path, true)) {
				tbm.setEnabled(true);
				tbm.setSelected(true);
			} else {
				if (selectionModel.isPartiallySelected(path)) {
					tbm.setEnabled(false);
					tbm.setSelected(true);
//					tbm.setIndeterminate();
				} else {
					tbm.setEnabled(true);
					tbm.setSelected(false);
				}
			}
		}
		panel.removeAll();
		panel.add(checkBox, BorderLayout.WEST);
		panel.add(renderer, BorderLayout.CENTER);
		return panel;

	}
	
}