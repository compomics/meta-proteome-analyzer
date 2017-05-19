package de.mpa.client.ui.sharedelements.tables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableModel;
import javax.swing.tree.TreeNode;

import org.jdesktop.swingx.treetable.MutableTreeTableNode;
import org.jdesktop.swingx.treetable.TreeTableNode;

import de.mpa.util.AlphanumComparator;

/**
 * TODO: API
 * 
 * @author behne
 */
public class SortableCheckBoxTreeTableNode extends CheckBoxTreeTableNode
		implements SortableTreeNode {

	private boolean sorted;
	private int[] modelToView;
	private SortableCheckBoxTreeTableNode.Row[] viewToModel;

	public SortableCheckBoxTreeTableNode() {
    }

	public SortableCheckBoxTreeTableNode(Object... userObjects) {
		super(userObjects);
	}

	public SortableCheckBoxTreeTableNode(Object userObject, boolean fixed) {
		super(userObject, fixed);
	}

	/**
	 * Gets the underlying values for this node that correspond to a particular tabular column.<br><br>
	 * Sub-classes need to override this method if more than the singular column value returned by
	 * <code>getValueAt()</code> wrapped in a list is to be returned.
	 * @param column the column index
	 * @return a collection of underlying values
	 */
	public Collection<?> getValuesAt(int column) {
		List<Object> res = new ArrayList<Object>();
		res.add(getValueAt(column));
		return res;
	}

	@Override
	public int convertRowIndexToModel(int viewIndex) {
		return (this.sorted) ? this.viewToModel[viewIndex].modelIndex : viewIndex;
	}

	@Override
	public int convertRowIndexToView(int modelIndex) {
		return (!this.sorted || (modelIndex < 0) || (modelIndex >= this.modelToView.length)) ? modelIndex
				: this.modelToView[modelIndex];
	}

	@Override
	public boolean canSort() {
		return !this.isLeaf();
	}

	@Override
	public boolean canSort(List<? extends RowSorter.SortKey> sortKeys) {
		// TODO: re-enable this check after aggregate functions have been re-implemented
//		for (SortKey sortKey : sortKeys) {
//			if (!(getValueAt(sortKey.getColumn()) instanceof Comparable<?>)) {
//				return false;
//			}
//		}
		return true;
	}

	@Override
	public void reset() {
		sorted = false;
	}

	@Override
	public int getChildCount() {
		return (sorted) ? viewToModel.length : super.getChildCount();
	}

//	@Override
//	public void remove(int index) {
//		super.remove(index);
//
//		Row[] viewToModel = new Row[this.viewToModel.length - 1];
//		System.arraycopy(this.viewToModel, 0, viewToModel, 0, index);
//		System.arraycopy(this.viewToModel, index + 1, viewToModel, index, this.viewToModel.length - index - 1);
//		this.viewToModel = viewToModel;
//
//		int[] modelToView = new int[this.modelToView.length - 1];
//		System.arraycopy(this.modelToView, 0, modelToView, 0, index);
//		System.arraycopy(this.modelToView, index + 1, modelToView, index, this.modelToView.length - index - 1);
//		this.modelToView = modelToView;
//	}

	@Override
	public void removeAllChildren() {
		sorted = false;
		super.removeAllChildren();
	}

	@Override
	public boolean isLeaf() {
		return (children.size() == 0);
	}

	@Override
	public void sort(List<? extends RowSorter.SortKey> sortKeys,
			RowFilter<? super TableModel, ? super Integer> filter,
			boolean hideEmpty) {

		int childCount = children.size();
		int excludedCount = 0;

		// build view-to-model mapping
		modelToView = new int[childCount];
		List<SortableCheckBoxTreeTableNode.Row> viewToModelList = new ArrayList<SortableCheckBoxTreeTableNode.Row>(childCount);

		for (int i = 0; i < childCount; i++) {
			MutableTreeTableNode child = children.get(i);
			SortableCheckBoxTreeTableNode.Row<TableModel, Integer> row = new SortableCheckBoxTreeTableNode.Row<TableModel, Integer>(child, i, sortKeys);

			// check whether the child node is eligible for filtering (only
			// leaves can be actively filtered, parent nodes will automatically
			// become invisible when they have no visible children)
			if (child.isLeaf()) {
				// check whether the filter permits this child (if any filter is
				// configured at all)
				if ((filter == null) || (filter.include(row))) {
					viewToModelList.add(row);
				} else {
					excludedCount++;
				}
			} else {
				// check whether the child node has any visible children of its
				// own, treat as excluded if the corresponding flag is set
				if ((child.getChildCount() == 0) && hideEmpty) {
					excludedCount++;
				} else {
					viewToModelList.add(row);
				}
			}

//			if ((filter == null) || (filter.include(row))) {
//				viewToModelList.add(row);
//			} else {
//				excludedCount++;
//			}

			// initialize model-to-view mapping while we're at it
			modelToView[i] = -1;
		}
		viewToModel = viewToModelList.toArray(new SortableCheckBoxTreeTableNode.Row[childCount - excludedCount]);

		// sort view-to-model mapping
		if (sortKeys != null) {
			Arrays.sort(viewToModel);
		}

		// build model-to-view mapping
		for (int i = 0; i < viewToModel.length; i++) {
			modelToView[viewToModel[i].modelIndex] = i;
		}

		sorted = true;
	}


	@Override
	public TreeTableNode getChildAt(int childIndex) {
		if ((sorted) && (childIndex > viewToModel.length)) {
			return new SortableCheckBoxTreeTableNode("I SHOULD BE INVISIBLE");
		}
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

	@Override
	public void setParent(MutableTreeTableNode newParent) {
		super.setParent(newParent);
	}

	/**
	 * Provides a child node with the ability to be sorted and/or filtered.
	 */
	protected class Row<M, I> extends RowFilter.Entry<M, I> implements Comparable<SortableCheckBoxTreeTableNode.Row> {
		/**
		 * The tree table node containing the row's cell values.
		 */
		protected TreeTableNode node;
		/**
		 * The row's model index.
		 */
		protected int modelIndex;
		/**
		 * The row's list of column indices to be sorted and their respective
		 * sort orders.
		 */
		protected List<? extends RowSorter.SortKey> sortKeys;

		/**
		 * Constructs a row object.
		 * @param node The node upon which comparisons are evaluated.
		 * @param modelIndex The row index.
		 * @param sortKeys The list of sort keys.
		 */
		public Row(TreeTableNode node, int modelIndex,
				List<? extends RowSorter.SortKey> sortKeys) {
			this.node = node;
			this.modelIndex = modelIndex;
			this.sortKeys = sortKeys;
		}

		@Override
		public String toString() {
			return ("" + this.modelIndex + " " + node.getUserObject()
					.toString());
		}

		@Override
		public M getModel() {
			return null; // we don't need this
		}

		@Override
		public int getValueCount() {
			return children.get(modelIndex).getColumnCount();
		}

		@Override
		public Object getValue(int index) {
			// special case to return underlying node
			if (index == -1) {
				return this.node;
			}
			// return column value
			return children.get(modelIndex).getValueAt(index);
		}

		@Override
		public String getStringValue(int index) {
			Object value = children.get(modelIndex).getValueAt(index);
			return (value == null) ? "" : value.toString();
		}

		@Override
		public I getIdentifier() {
			return null; // we don't need this
		}

		@Override
		public int compareTo(SortableCheckBoxTreeTableNode.Row that) {
			// trivial case
			if (that == null) {
				return 1;
			}
			// initialize result with fall-back value
			int result = this.modelIndex - that.modelIndex;
			for (RowSorter.SortKey sortKey : sortKeys) {
				if (sortKey.getSortOrder() != SortOrder.UNSORTED) {
					// sort leaf nodes to always appear below non-leaves
					if (node.isLeaf() != that.node.isLeaf()) {
						// either one node might be a leaf
						result = that.node.getChildCount() - node.getChildCount();
					} else {
						int column = sortKey.getColumn();
						Object this_value = node.getValueAt(column);
						Object that_value = that.node.getValueAt(column);
						// define null as less than not-null
						if (this_value == null) {
							result = (that_value == null) ? 0 : -1;
						} else if (that_value == null) {
							result = 1;
						} else {
							// both value objects are not null, invoke comparison
							if (this_value instanceof String) {
								// special case for strings to get a more natural sorting
								result = AlphanumComparator.getInstance().compare(this_value, that_value);
							} else {
								try {
									result = ((Comparable<Object>) this_value).compareTo(that_value);
								} catch (ClassCastException e) {
									// user miss-clicked, do nothing
								}
							}
						}
						// correct result w.r.t. sort order
						if (sortKey.getSortOrder() == SortOrder.DESCENDING) {
							result *= -1;
						}
					}
				}
				if (result != 0) {
					break;
				}
			}
			return result;
		}
		
	}
	
}