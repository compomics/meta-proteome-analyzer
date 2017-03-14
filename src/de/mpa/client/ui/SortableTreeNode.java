package de.mpa.client.ui;

import java.util.List;

import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.treetable.TreeTableNode;

/**
 * Common interface for sortable tree table nodes.
 * 
 * @author A. Behne
 */
public interface SortableTreeNode extends TreeTableNode {
	
	/**
	 * Sorts this node's list of children in accordance with the provided sort
	 * keys and filter.
	 * 
	 * @param sortKeys the list of sort keys
	 * @param filter the row filter
	 * @param hideEmpty <code>true</code> if non-leaf nodes with no visible children shall be hidden,
	 *  <code>false</code> otherwise
	 */
    void sort(List<? extends RowSorter.SortKey> sortKeys, RowFilter<? super TableModel, ? super Integer> filter, boolean hideEmpty);

	/**
	 * Returns the model index of the node's child at the specified view index.
	 * @param viewIndex
	 * @return the child's model index
	 */
    int convertRowIndexToModel(int viewIndex);

	/**
	 * Returns the view index of the node's child at the specified model index.
	 * @param modelIndex
	 * @return the child's view index
	 */
    int convertRowIndexToView(int modelIndex);

	/**
	 * Returns whether this node is capable of sorting its children.
	 * @return <code>true</code> if this node can sort its children,
	 *  <code>false</code> otherwise
	 */
    boolean canSort();

	/**
	 * Returns whether this node is capable of sorting specific columns of its children.
	 * @param sortKeys the list of sort keys specifying the columns that are to be sorted
	 * @return <code>true</code> if this node can sort its children,
	 *  <code>false</code> otherwise
	 */
    boolean canSort(List<? extends RowSorter.SortKey> sortKeys);
	
	/**
	 * Returns whether this node's children are sorted.
	 * @return <code>true</code> if this node's children are sorted,
	 *  <code>false</code> otherwise
	 */
    boolean isSorted();
	
	/**
	 * Resets this node's children's sort order to their original order.
	 */
    void reset();
	
}
