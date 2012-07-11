package de.mpa.client.ui;

import java.util.List;

import javax.swing.RowSorter.SortKey;
import javax.swing.tree.TreeNode;

import org.jdesktop.swingx.treetable.TreeTableNode;

public interface SortableTreeNode {
	
	public void sort(List<? extends SortKey> sortKeys);
	
	public TreeTableNode getChildAt(int childIndex);
	
	public int getIndex(TreeNode node);
	
	public boolean canSort();
	
	public boolean canSort(List<? extends SortKey> sortKeys);
	
	public boolean isSorted();
	
	public void reset();

}
