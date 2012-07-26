package de.mpa.client.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SortOrder;
import javax.swing.RowSorter.SortKey;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;
import org.jdesktop.swingx.treetable.TreeTableNode;

/**
 * Extended tree table model for use with {@link SortableCheckBoxTreeTable} and
 * {@link SortableCheckBoxTreeTableNode}.<br><br>
 * Based on <a href="http://java.net/projects/jdnc-incubator/
 * lists/cvs/archive/2008-01/message/82">Ray Turnbull's implementation</a>.
 * 
 * @author A. Behne
 */
public class SortableTreeTableModel extends DefaultTreeTableModel {

	/**
	 * The list of sort keys containing column indices and their corresponding 
	 * sort orders
	 */
	private List<SortKey> sortKeys = new ArrayList<SortKey>();

	/**
	 * Constructs a sortable tree table model using the specified root node.
	 * @param root The tree table node to be used as root.
	 */
	public SortableTreeTableModel(TreeTableNode root) {
		super(root);
	}
	
	/**
	 * Sets the sort keys. Causes the model to re-sort if the provided keys 
	 * differ from the stored state.
	 * @param sortKeys The new sort keys to set.
	 */
	public void setSortKeys(List<? extends SortKey> sortKeys) {
		if (!sortKeys.equals(this.sortKeys)) {
			this.sortKeys = new ArrayList<SortKey>(sortKeys);
			sort();
		}
	}
	
	/**
	 * Returns the sort keys.
	 * @return The sort keys.
	 */
	public List<? extends SortKey> getSortKeys() {
		return sortKeys;
	}
	
	/**
	 * Returns whether the current sort keys necessitate re-sorting the model.
	 * @return <code>true</code> if sorting is necessary, <code>false</code> 
	 * otherwise 
	 */
	private boolean shouldSort() {
		boolean shouldSort = false;
		for (SortKey sortKey : getSortKeys()) {
			shouldSort |= sortKey.getSortOrder() != SortOrder.UNSORTED;
			if (shouldSort) break;
		}
		return shouldSort;
	}
	
	/**
	 * Returns whether the current sort keys indicate that the model's sort 
	 * order should be reset.
	 * @return <code>true</code> if resetting is in order, <code>false</code> 
	 * otherwise 
	 */
	private boolean shouldReset() {
		boolean shouldReset = true;
		for (SortKey sortKey : getSortKeys()) {
			shouldReset &= sortKey.getSortOrder() == SortOrder.UNSORTED;
			if (!shouldReset) break;
		}
		return shouldReset;
	}
	
	/**
	 * Sorts the whole model w.r.t. the stores sort keys.
	 */
	public void sort() {
		sort(getRoot());
		modelSupport.fireTreeStructureChanged(new TreePath(getRoot()) {
			// hack to work around tree model listener causing the whole table 
			// (including column widths and highlighters) to be recreated, we 
			// want only the displayed data to be updated
			public TreePath getParentPath() {
				return new TreePath(0);
			}
		});
	}
	
	/**
	 * Sorts the part of the model below the specified parent node.
	 * @param parent The node below which the model will be sorted.
	 */
	private void sort(TreeTableNode parent) {
		doSort(parent, shouldReset());
	}

	/**
	 * Recursively sorts or resets the provided parent node's children.
	 * @param parent The parent node whose children shall be sorted/reset.
	 * @param reset <code>true</code> if the original sort order shall be 
	 * restored, <code>false</code> otherwise
	 */
	private void doSort(TreeTableNode parent, boolean reset) {
		if (parent instanceof SortableTreeNode) {
			SortableTreeNode node = (SortableTreeNode) parent;
			
			boolean canSort = node.canSort();
			if (canSort) {
				if (reset && node.isSorted()) {
					node.reset();
				} else if (!reset && node.canSort(getSortKeys())) {
					node.sort(getSortKeys());
				}
			}

			for (int i = 0; i < parent.getChildCount(); i++) {
				int modelIndex = node.convertRowIndexToModel(i);
			    TreeTableNode child = (TreeTableNode) parent.getChildAt(modelIndex);
		        doSort(child, reset);
			}
			
		}
	}
	
	/**
	 * {@inheritDoc} <p>
	 * 
	 * Overridden to sort newly inserted nodes if necessary.
	 */
	@Override
	public void insertNodeInto(MutableTreeTableNode newChild, MutableTreeTableNode parent, int index) {
		parent.insert(newChild, index);
		if (shouldSort()) {
			sort(parent);
			// TODO: add model notification
		} else {
			modelSupport.fireChildAdded(new TreePath(getPathToRoot(parent)),
					index, newChild);
		}
	}
	
}
