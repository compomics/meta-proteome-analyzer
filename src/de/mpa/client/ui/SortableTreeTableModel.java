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

	private List<SortKey> sortKeys = new ArrayList<SortKey>();

	/**
	 * Constructs a sortable tree table model using the specified root node.
	 * @param root The tree table node to be used as root.
	 */
	public SortableTreeTableModel(TreeTableNode root) {
		super(root);
	}
	
	public void setSortKeys(List<? extends SortKey> sortKeys) {
		if (!sortKeys.equals(this.sortKeys)) {
			this.sortKeys = new ArrayList<SortKey>(sortKeys);
			sort(getRoot());
		}
	}
	
	public List<? extends SortKey> getSortKeys() {
		return sortKeys;
	}
	
	private boolean shouldSort() {
		boolean shouldSort = false;
		for (SortKey sortKey : getSortKeys()) {
			shouldSort |= sortKey.getSortOrder() != SortOrder.UNSORTED;
			if (shouldSort) break;
		}
		return shouldSort;
	}
	
	private boolean shouldReset() {
		boolean shouldReset = true;
		for (SortKey sortKey : getSortKeys()) {
			shouldReset &= sortKey.getSortOrder() == SortOrder.UNSORTED;
			if (!shouldReset) break;
		}
		return shouldReset;
	}
	
	/**
	 * Sorts children of node, and all their children. Node need not be
	 * sortable. (Although if it is not and neither are any descendants, nothing
	 * will happen.) Called automatically if a child is added to or removed from
	 * a node. Usually not necessary to call this directly unless node added
	 * outside model or data changed.
	 * 
	 * @param parent - first node to be sorted.
	 */
	public void sort(TreeTableNode parent) {
		doSort(parent, shouldReset());
		modelSupport.fireTreeStructureChanged(new TreePath(getPathToRoot(parent)));
	}
	
	public void sort(TreePath path) {
		doSort((TreeTableNode) path.getLastPathComponent(), shouldReset());
		modelSupport.fireTreeStructureChanged(path);
	}

	private void doSort(TreeTableNode parent, boolean reset) {
		if (parent instanceof SortableTreeNode) {
			SortableTreeNode node = (SortableTreeNode) parent;
			boolean canSort = node.canSort() && (reset) ? node.isSorted() : node.canSort(getSortKeys());
			if (canSort) {
				if (reset && node.isSorted()) {
					node.reset();
				} else if (!reset && node.canSort(getSortKeys())) {
					node.sort(getSortKeys());
				}
			} else {
				node.reset();
			}
			
			for (int i = 0; i < parent.getChildCount(); i++) {
			    TreeTableNode child = (TreeTableNode) parent.getChildAt(i);
			    // TODO: find working way of notifying incremental changes instead of whole tree structure
//		        modelSupport.firePathChanged(new TreePath(getPathToRoot(child)));
		        doSort(child, reset);
			}
		    modelSupport.fireTreeStructureChanged(new TreePath(getRoot()));
		}
	}

	@Override
	public void insertNodeInto(MutableTreeTableNode newChild,
			MutableTreeTableNode parent, int index) {
		parent.insert(newChild, index);
		if (shouldSort()) {
			sort(parent);
		} else {
			modelSupport.fireChildAdded(new TreePath(getPathToRoot(parent)),
					index, newChild);
		}
	}
	
}
