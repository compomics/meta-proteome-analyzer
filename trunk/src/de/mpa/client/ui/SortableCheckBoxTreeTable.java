package de.mpa.client.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JTree;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.table.TableModel;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.TreeTableModel;

/**
 * Extension of CheckBoxTreeTable to allow column sorting. To be used in 
 * conjunction with {@link SortableTreeTableModel} and 
 * {@link SortableCheckBoxTreeTableNode}.<br><br>
 * Based on <a href="http://java.net/projects/jdnc-incubator/
 * lists/cvs/archive/2008-01/message/82">Ray Turnbull's implementation</a>.
 * 
 * @author A. Behne
 */
public class SortableCheckBoxTreeTable extends CheckBoxTreeTable {

	private Set<TreePath> expanded = new HashSet<TreePath>();
	
	private boolean expanding = false;

	/**
	 * Constructs a sortable tree table with checkboxes from a tree table model.
	 * @param treeModel The tree model to be used. Must be an instance of 
	 * {@link SortableTreeTableModel}
	 */
	public SortableCheckBoxTreeTable(TreeTableModel treeModel) {
		super(treeModel);
		if (!(treeModel instanceof SortableTreeTableModel)) {
			throw new IllegalArgumentException(
					"Model must be a SortableTreeTableModel");
		}

		superSetSortable(true);
		setAutoCreateRowSorter(true);
		setRowSorter(new TreeTableRowSorter<TableModel>(this));
		
		addTreeExpansionListener(new TreeExpansionListener() {
			public void treeExpanded(TreeExpansionEvent event) {
				if (!expanding) {
					expanded.add(event.getPath());
				}
			}
			public void treeCollapsed(TreeExpansionEvent event) {
				if (!expanding) {
					expanded.remove(event.getPath());
				}
			}
		});
	}
	
	@Override
	public void setAutoCreateRowSorter(boolean autoCreateRowSorter) {
		superSetAutoCreateRowSorter(autoCreateRowSorter);
	}
	
	@Override
	public void setRowSorter(RowSorter<? extends TableModel> sorter) {
		superSetRowSorter(sorter);
	}
	
	private class TreeTableRowSorter<M extends TableModel> extends RowSorter<TableModel> {
		
		private JXTreeTable treeTable;
		
		private SortableTreeTableModel treeModel;
		
//		private List<SortKey> sortKeys = new ArrayList<SortKey>();
		
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
			JTree tree = (JTree) treeTable.getCellRenderer(-1, treeTable.getHierarchicalColumn());
			return tree.getRowCount();
		}

		@Override
		public void toggleSortOrder(int column) {
			List<SortKey> sortKeys = new ArrayList<SortKey>(getSortKeys());
			if (sortKeys.isEmpty()) {
				sortKeys.add(new SortKey(column, SortOrder.ASCENDING));
			} else {
				SortOrder sortOrder = SortOrder.ASCENDING;
				if (sortKeys.get(0).getColumn() == column) {
					if (sortKeys.get(0).getSortOrder() == SortOrder.ASCENDING) {
						sortOrder = SortOrder.DESCENDING;
					}
				}
				sortKeys.set(0, new SortKey(column, sortOrder));
			}
			setSortKeys(sortKeys);
		}

		@Override
		public void setSortKeys(List<? extends SortKey> sortKeys) {
			if (!sortKeys.equals(getSortKeys())) {
//				this.sortKeys = new ArrayList<SortKey>(sortKeys);
				fireSortOrderChanged();
				treeModel.setSortKeys(sortKeys);
				reExpand();
			}
		}

		@Override
		public List<? extends SortKey> getSortKeys() {
			return treeModel.getSortKeys();
		}
		
		/**
		 * Restores expanded state of nodes that get collapsed due to sorting.
		 */
		private void reExpand() {
			expanding = true;
			for (TreePath path : expanded) {
				expandPath(path);
			}
			expanding = false;
		}

		// don't need the rest for now
		public void allRowsChanged() {}
		public void modelStructureChanged() {}
		public void rowsDeleted(int firstRow, int endRow) {}
		public void rowsInserted(int firstRow, int endRow) {}
		public void rowsUpdated(int firstRow, int endRow) {}
		public void rowsUpdated(int firstRow, int endRow, int column) {}
		
	}
	

}
