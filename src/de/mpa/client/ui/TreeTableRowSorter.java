package de.mpa.client.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXTreeTable;

/**
 * Custom {@link RowSorter} for use with {@link JXTreeTables}. 
 * Use in conjunction with {@link SortableTreeTableModel}.
 * 
 * @author A. Behne
 * @param <M> the type of the underlying model
 */
public class TreeTableRowSorter<M extends TableModel> extends RowSorter<TableModel> {
	
	/**
	 * The {@link JXTreeTable} instance to which the sorter is applied.
	 */
	protected JXTreeTable treeTable;
	
	/**
	 * The tree table model instance with which the sorter communicates to request sorting.
	 */
	protected SortableTreeTableModel treeModel;

	/**
	 * Constructs a {@link RowSorter} for the provided {@link JXTreeTable}.
	 * @param treeTable The tree table to which the sorter shall be attached.
	 */
	public TreeTableRowSorter(JXTreeTable treeTable) {
		this.treeTable = treeTable;
		this.treeModel = (SortableTreeTableModel) treeTable.getTreeTableModel();
	}
	
	// Leave model/view index conversion to node implementation
	@Override
	public int convertRowIndexToModel(int index) {
		return index;
	}
	@Override
	public int convertRowIndexToView(int index) {
		return index;
	}

	@Override
	public TableModel getModel() {
		return treeTable.getModel();
	}

	@Override
	public int getModelRowCount() {
		return getModel().getRowCount();
	}

	@Override
	public int getViewRowCount() {
		int hc = treeTable.getHierarchicalColumn();
		if (hc == -1) {
			return 0;
		} else {
			JTree tree = (JTree) treeTable.getCellRenderer(-1, hc);
			return tree.getRowCount();
		}
	}

	@Override
	public void toggleSortOrder(int column) {
		// abort if column is not sortable
		if (!treeTable.getColumnExt(treeTable.convertColumnIndexToView(column)).isSortable()) {
			return;
		}
		// configure sort keys
		List<SortKey> sortKeys = new ArrayList<SortKey>(this.getSortKeys());
		if (sortKeys.isEmpty()) {
			// add new sort key
			sortKeys.add(new SortKey(column, SortOrder.ASCENDING));
		} else {
			// configure sort order
			SortOrder sortOrder = SortOrder.ASCENDING;
			SortKey primarySortKey = sortKeys.get(0);
			if (primarySortKey.getColumn() == column) {
				// cycle sort key if same column has been targeted
				if (primarySortKey.getSortOrder() == SortOrder.ASCENDING) {
					sortOrder = SortOrder.DESCENDING;
				} else if (primarySortKey.getSortOrder() == SortOrder.DESCENDING) {
					sortOrder = SortOrder.UNSORTED;
				}
			}
			sortKeys.add(0, new SortKey(column, sortOrder));
			// trim off excess sort keys down to a maximum of three
			// TODO: parametrize maximum sort key count?
			if (sortKeys.size() > 3) {
				sortKeys.remove(3);
			}
		}
		// apply sort keys
		this.setSortKeys(sortKeys);
	}

	@Override
	public List<? extends SortKey> getSortKeys() {
		return treeModel.getSortKeys();
	}

	@Override
	public void setSortKeys(List<? extends SortKey> sortKeys) {
		if (!sortKeys.equals(getSortKeys())) {
			// collapse tree, cache expanded paths
			preCollapse();
			treeModel.setSortKeys(sortKeys);
//			// refresh model
//			treeModel.setRoot(treeModel.getRoot());
			// re-expand cached paths
			reExpand();
			// notify listeners
			fireSortOrderChanged();
		}
	}

	public RowFilter<? super M,? super Integer> getRowFilter() {
		return treeModel.getRowFilter();
	}
	
	@SuppressWarnings("unchecked")
	public void setRowFilter(RowFilter<? super M,? super Integer> rowFilter) {
		// force re-sort
		preCollapse();
		treeModel.setRowFilter(
				(RowFilter<? super TableModel, ? super Integer>) rowFilter);
		reExpand();
		// notify listeners
		fireSortOrderChanged();
	}
	
	/**
	 * Collapses tree before sorting to work around glitchy behavior.
	 */
	protected void preCollapse() {
		treeTable.collapseAll();
	}
	
	/**
	 * Restores expanded state of nodes before sorting.
	 */
	protected void reExpand() {
		treeTable.expandAll();
	}
	
	/* We don't need the rest of the overrides; the tree table model takes care of most of it */
	public void allRowsChanged() {}
	public void modelStructureChanged() {}
	public void rowsDeleted(int firstRow, int endRow) {}
	public void rowsInserted(int firstRow, int endRow) {}
	public void rowsUpdated(int firstRow, int endRow) {}
	public void rowsUpdated(int firstRow, int endRow, int column) {}
	
}
