package de.mpa.client.ui;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;

public class CheckBoxTreeManager implements TreeSelectionListener {
	private CheckBoxTreeSelectionModel selectionModel;
	private JTree tree;
	
	public CheckBoxTreeManager(JTree tree) {
		this.tree = tree;
		this.selectionModel = new CheckBoxTreeSelectionModel(tree.getModel());
//		tree.setCellRenderer(new CheckBoxTreeCellRenderer(tree.getCellRenderer(), selectionModel));
		tree.setCellRenderer(new CheckBoxTreeCellRenderer(new DefaultTreeCellRenderer(), selectionModel));
		this.selectionModel.addTreeSelectionListener(this);
		this.selectionModel.addSelectionPath(tree.getPathForRow(0));
	}

	public CheckBoxTreeSelectionModel getSelectionModel() {
		return this.selectionModel;
	}
	
	public TreeModel getModel() {
		return tree.getModel();
	}

	public JTree getTree() {
		return this.tree; 
	}

	public void valueChanged(TreeSelectionEvent e) {
		tree.treeDidChange();
	}
}