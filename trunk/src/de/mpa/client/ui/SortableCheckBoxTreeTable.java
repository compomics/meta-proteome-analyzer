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

	/**
	 * Set holding the {@link TreePaths} of the tree's expanded nodes. 
	 */
	private Set<TreePath> expanded = new HashSet<TreePath>();
	
	/**
	 * Flag denoting whether expansion states are toggled programmatically to circumvent 
	 * local {@link TreeExpansionListener} event handling.
	 */
	private boolean expanding = false;

	/**
	 * Constructs a sortable tree table with checkboxes from a tree table model.
	 * @param treeModel The tree model to be used. Must be an instance of 
	 * {@link SortableTreeTableModel}
	 */
	public SortableCheckBoxTreeTable(TreeTableModel treeModel) {
		super(treeModel);
		// Check whether the provided tree model is sortable.
		if (!(treeModel instanceof SortableTreeTableModel)) {
			throw new IllegalArgumentException(
					"Model must be a SortableTreeTableModel");
		}

		// Install row sorter to enable sorting by clicking on column header.
		this.setSortable(true);
		this.setAutoCreateRowSorter(true);
		this.setRowSorter(new TreeTableRowSorter<TableModel>(this));
		
		// Install expansion listener to keep track of expanded paths.
		addTreeExpansionListener(new TreeExpansionListener() {
			public void treeExpanded(TreeExpansionEvent event) {
				if (!expanding) expanded.add(event.getPath());
			}
			public void treeCollapsed(TreeExpansionEvent event) {
				if (!expanding) expanded.remove(event.getPath());
			}
		});
	}
	
	/* Overrides of sorting-related methods forwarding to JXTreeTable's hooks. */
	@Override
	public void setSortable(boolean sortable) {
		superSetSortable(sortable);
	}
	
	@Override
	public void setAutoCreateRowSorter(boolean autoCreateRowSorter) {
		superSetAutoCreateRowSorter(autoCreateRowSorter);
	}
	
	@Override
	public void setRowSorter(RowSorter<? extends TableModel> sorter) {
		superSetRowSorter(sorter);
	}
	
	/**
	 * Custom {@link RowSorter} for use with {@link JXTreeTables}. 
	 * Use in conjunction with {@link SortableTreeTableModel}.
	 * 
	 * @author A. Behne
	 * @param <M> the type of the underlying model
	 */
	private class TreeTableRowSorter<M extends TableModel> extends RowSorter<TableModel> {
		
		/**
		 * The {@link JXTreeTable} instance to which the sorter is applied.
		 */
		private JXTreeTable treeTable;
		
		/**
		 * The tree table model instance with which the sorter communicates to request sorting.
		 */
		private SortableTreeTableModel treeModel;
		
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
					} else if (sortKeys.get(0).getSortOrder() == SortOrder.DESCENDING) {
						sortOrder = SortOrder.UNSORTED;
					}
				}
				sortKeys.add(0, new SortKey(column, sortOrder));
				// TODO: possibly parametrize maximum sort key count
				if (sortKeys.size() > 3) {
					sortKeys.remove(3);
				}
			}
			setSortKeys(sortKeys);
		}

		@Override
		public void setSortKeys(List<? extends SortKey> sortKeys) {
			if (!sortKeys.equals(getSortKeys())) {
				fireSortOrderChanged();
				preCollapse();
				treeModel.setSortKeys(sortKeys);
				reExpand();
			}
		}

		@Override
		public List<? extends SortKey> getSortKeys() {
			return treeModel.getSortKeys();
		}
		
		/**
		 * Collapses tree before sorting to work around glitchy behavior.
		 */
		private void preCollapse() {
			expanding = true;
			collapseAll();
			expanding = false;
		}
		
		/**
		 * Restores expanded state of nodes before sorting.
		 */
		private void reExpand() {
			expanding = true;
			for (TreePath path : expanded) {
				expandPath(path);
			}
			expanding = false;
		}

		/* We don't need the rest of the overrides; the tree table model takes care of most of it */
		public void allRowsChanged() {}
		public void modelStructureChanged() {}
		public void rowsDeleted(int firstRow, int endRow) {}
		public void rowsInserted(int firstRow, int endRow) {}
		public void rowsUpdated(int firstRow, int endRow) {}
		public void rowsUpdated(int firstRow, int endRow, int column) {}
		
	}
	

}
