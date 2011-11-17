package de.mpa.client.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JCheckBox;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

public class CheckBoxTreeManager extends MouseAdapter
								 implements TreeSelectionListener {
	private CheckBoxTreeSelectionModel selectionModel;
	private JTree tree = new JTree();
	private int hotspot = new JCheckBox().getPreferredSize().width;
	JPopupMenu popup = new JPopupMenu();
	
	public CheckBoxTreeManager(JTree tree) {
		this.tree = tree;
		selectionModel = new CheckBoxTreeSelectionModel(tree.getModel());
		tree.setCellRenderer(new CheckBoxTreeCellRenderer(tree.getCellRenderer(), selectionModel));
		tree.addMouseListener(this);
		selectionModel.addTreeSelectionListener(this);
		selectionModel.addSelectionPath(tree.getPathForRow(0));
	} 

	public void mouseClicked(MouseEvent e) { 
		TreePath path = tree.getPathForLocation(e.getX(), e.getY()); 
		if ((path != null) &&											// tree element got hit
			(e.getX() <= (tree.getPathBounds(path).x + hotspot))) {		// checkbox part got hit
			
			boolean selected = selectionModel.isPathSelected(path, true);
			selectionModel.removeTreeSelectionListener(this);

			try {
				if (selected) {
					selectionModel.removeSelectionPath(path);
				} else {
					selectionModel.addSelectionPath(path);
				}
			} finally {
				selectionModel.addTreeSelectionListener(this);
				tree.treeDidChange();
			}
		}
		return;
	}
	
	public CheckBoxTreeSelectionModel getSelectionModel(){ 
		return selectionModel; 
	} 

	public void valueChanged(TreeSelectionEvent e){ 
		tree.treeDidChange(); 
	} 
}