package de.mpa.client.ui;

import java.util.List;

import javax.swing.RowFilter;
import javax.swing.RowSorter.SortKey;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.treetable.TreeTableNode;

public interface SortableTreeNode extends TreeTableNode {
	
	/**
	 * Sorts this node's list of children in accordance with the provided sort
	 * keys and filter.
	 * 
	 * @param sortKeys The list of sort keys.
	 * @param filter The row filter.
	 */
	public void sort(List<? extends SortKey> sortKeys, RowFilter<? super TableModel,? super Integer> filter);
	
	/**
	 * Returns the model index of the node's child at the specified view index.
	 * @param viewIndex
	 * @return The child's model index.
	 */
	public int convertRowIndexToModel(int viewIndex);

	/**
	 * Returns the view index of the node's child at the specified model index.
	 * @param modelIndex
	 * @return The child's view index.
	 */
	public int convertRowIndexToView(int modelIndex);
	
	/**
	 * Returns whether this node is capable of sorting its children.
	 * @return <code>true</code> if this node can sort its children,
	 *         <code>false</code> otherwise.
	 */
	public boolean canSort();
	
	/**
	 * Returns whether this node is capable of sorting specific columns of its children.
	 * @param sortKeys The list of sort keys specifying the columns that are to be sorted.
	 * @return <code>true</code> if this node can sort its children, <code>false</code> otherwise.
	 */
	public boolean canSort(List<? extends SortKey> sortKeys);
	
	/**
	 * Returns whether this node's children are sorted.
	 * @return <code>true</code> if this node's children are sorted, <code>false</code> otherwise.
	 */
	public boolean isSorted();
	
	/**
	 * Resets this node's children's sort order to their original order.
	 */
	public void reset();
	
}
