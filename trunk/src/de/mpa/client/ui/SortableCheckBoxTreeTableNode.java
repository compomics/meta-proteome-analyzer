package de.mpa.client.ui;

import java.util.Arrays;
import java.util.List;

import javax.swing.SortOrder;
import javax.swing.RowSorter.SortKey;
import javax.swing.tree.TreeNode;

import org.jdesktop.swingx.treetable.TreeTableNode;

public class SortableCheckBoxTreeTableNode extends CheckBoxTreeTableNode implements SortableTreeNode {

	private boolean sorted;
	private int[] modelToView;
	private Row[] viewToModel;
	
	public SortableCheckBoxTreeTableNode() {
		super();
	}

	public SortableCheckBoxTreeTableNode(Object... userObjects) {
		super(userObjects);
	}
	
	public SortableCheckBoxTreeTableNode(Object userObject, boolean fixed) {
		super(userObject, fixed);
	}
	
	@Override
	public int convertRowIndexToModel(int viewIndex) {
		return (sorted) ? viewToModel[viewIndex].modelIndex : viewIndex;
	}

	@Override
	public int convertRowIndexToView(int modelIndex) {
		return (!sorted || modelIndex < 0 || modelIndex >= modelToView.length) ? modelIndex : modelToView[modelIndex];
	}
	
	@Override
	public boolean canSort() { 
		// no use re-sorting a single child
		return (getChildCount() > 1);
	}

	@Override
	public boolean canSort(List<? extends SortKey> sortKeys) {
		boolean canSort = canSort();
		if (canSort) {
			for (SortKey sortKey : sortKeys) {
				canSort &= getValueAt(sortKey.getColumn()) instanceof Comparable<?>;
			}
		}
		return canSort();
	}

	@Override
	public void reset() {
		sorted = false;
	}

	@Override
	public void sort(List<? extends SortKey> sortKeys) {
		int count = getChildCount();
		if (count == 0) {
			sorted = false;
			return;
		}
		modelToView = new int[count];
		// load view to model array
		viewToModel = new Row[count];
		int i = 0;
		for (TreeTableNode node : children) {
			viewToModel[i] = new Row(node, i++, sortKeys);			
		}
		// sort
		Arrays.sort(viewToModel);
		// load model to view array
        for (i = count-1; i >= 0; i--) {
            modelToView[viewToModel[i].modelIndex] = i;
        }
        sorted = true;
	}
	
	@Override
	public TreeTableNode getChildAt(int childIndex) {
		return super.getChildAt(convertRowIndexToModel(childIndex));
	}
	
	@Override
	public int getIndex(TreeNode node) {
		return convertRowIndexToView(children.indexOf(node));
	}

	@Override
	public boolean isSorted() {
		return sorted;
	}
	
	/**
	 * Private class representing a sortable tree table row. 
	 */
	private class Row implements Comparable<Row> {
		/**
		 * The tree table node containing the row's cell values.
		 */
		private TreeTableNode node;
		/**
		 * The row's model index.
		 */
		private int modelIndex;
		/**
		 * The row's list of column indices to be sorted and their respective sort orders.
		 */
		private List<? extends SortKey> sortKeys;

		/**
		 * Constructs a row object.
		 * @param node The node upon which comparisons are evaluated.
		 * @param modelIndex The row index.
		 * @param sortKeys The list of sort keys.
		 */
		public Row(TreeTableNode node, int modelIndex, List<? extends SortKey> sortKeys) {
			this.node = node;
			this.modelIndex = modelIndex;
			this.sortKeys = sortKeys;
		}

		@Override
		@SuppressWarnings("unchecked")
		public int compareTo(Row row) {
			int result;
			for (SortKey sortKey : this.sortKeys) {
				Object this_value = this.node.getValueAt(sortKey.getColumn());
				Object that_value = row.node.getValueAt(sortKey.getColumn());
				
				if (sortKey.getSortOrder() == SortOrder.UNSORTED) {
					result = this.modelIndex - row.modelIndex;
				} else {
					if (this_value == null) {
						result = (that_value == null) ? 0 : -1;
					} else if (that_value == null) {
						result = 1;
					} else {
						result = ((Comparable<Object>) this_value).compareTo(that_value);
					}
					if (sortKey.getSortOrder() == SortOrder.DESCENDING) {
						result *= -1;
					}
				}
				if (result != 0) {
					return result;
				}
			}
			// If we get here, they're equal. Fall back to model order.
			return this.modelIndex - row.modelIndex;
		}
		
		@Override
		public String toString() {
			return Integer.toString(this.modelIndex);
		}

	}

}
