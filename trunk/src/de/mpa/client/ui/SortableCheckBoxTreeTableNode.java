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

	private int convertRowIndexToModel(int index) {
		return (sorted) ? viewToModel[index].modelIndex : index;
	}

	private int convertRowIndexToView(int index) {
		return (!sorted || index < 0 || index >= modelToView.length) ? index : modelToView[index];
	}
	
	@Override
	public boolean canSort() { 
		return (getChildCount() > 0);
	}

	@Override
	public boolean canSort(List<? extends SortKey> sortKeys) {
		return canSort();
	}

	@Override
	public void reset() {
		sorted = false;
	}

	@Override
	public void sort(List<? extends SortKey> sortKeys) {
		sort(sortKeys.get(0).getColumn(), sortKeys.get(0).getSortOrder());
	}
	
	private void sort(int column, SortOrder order) {
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
			Row row = new Row(node.getValueAt(column), i, order);
			viewToModel[i++] = row;			
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
		private Comparable<Object> key;
		private int modelIndex;
		private SortOrder order;

		/**
		 * Constructs a row object.
		 * @param key The object upon which comparisons are evaluated.
		 * @param modelIndex The row index.
		 * @param order The sort order.
		 */
		@SuppressWarnings("unchecked")
		public Row(Object key, int modelIndex, SortOrder order) {
			this.key = (Comparable<Object>) key;
			this.modelIndex = modelIndex;
			this.order = order;
		}

		@Override
		public int compareTo(Row row) {
			int result;
			// treat null as less than non-null
			if (this.key == null) {
				result = (row.key == null) ? 0 : -1;
			} else {
				result = (row.key == null) ? 1 : this.key.compareTo(row.key);
			}
			if (order != SortOrder.ASCENDING) {
				result *= -1;
			}
			if (result == 0) {
				// revert to model order
				result = this.modelIndex - row.modelIndex;
			}
			return result;
		}

	}

}
