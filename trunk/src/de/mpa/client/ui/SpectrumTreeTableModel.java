package de.mpa.client.ui;

import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

public class SpectrumTreeTableModel extends AbstractTreeTableModel {

	SpectrumTreeNode treeRoot;
	
	public SpectrumTreeTableModel(SpectrumTreeNode treeRoot) {
		super(treeRoot);
		this.treeRoot = treeRoot;
	}

	@Override
	public int getColumnCount() {
		return 5;
	}
	
	@Override
	public String getColumnName( int column )
	{
		switch (column) {
		case 0:
			return "Files";
		case 1:
			return "Path/Title";
		case 2:
			return "Peaks";
		case 3:
			return "TIC";
		case 4:
			return "SNR";
		default:
			return null;
		}
	}
	
	@Override
	public boolean isCellEditable(Object node, int column) {
		return (column == getHierarchicalColumn()) ? true : false;
	}

	@Override
	public Object getValueAt(Object node, int column) {
		return ((SpectrumTreeNode) node).getValueAt(column);
	}
	
	@Override
	public void setValueAt(Object value, Object node, int column) {
		((SpectrumTreeNode) node).setValueAt(value, column);
	}

	@Override
	public Object getChild(Object node, int index) {
		return ((SpectrumTreeNode) node).getChildAt(index);
	}

	@Override
	public int getChildCount(Object node) {
		return ((SpectrumTreeNode) node).getChildCount();
	}

	@Override
	public int getIndexOfChild(Object node, Object child) {
		SpectrumTreeNode parentNode = (SpectrumTreeNode) node;
		SpectrumTreeNode childNode = (SpectrumTreeNode) child;
		return parentNode.getIndex(childNode);
	}

}
