package de.mpa.client.ui;

import java.util.HashSet;
import java.util.Set;

import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.table.TableModel;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.treetable.TreeTableModel;

/**
 * Extension of CheckBoxTreeTable to allow column sorting. To be used in 
 * conjunction with {@link SortableTreeTableModel} and 
 * {@link SortableCheckBoxTreeTableNode}.<br><br>
 * Based on <a href="http://java.net/projects/jdnc-incubator/lists/cvs/archive/2008-01/message/82">
 * Ray Turnbull's implementation</a>.
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
		this.setRowSorter(new TreeTableRowSorter<TableModel>(this) {
			@Override
			protected void preCollapse() {
				expanding = true;
				for (TreePath path : expanded) {
					collapsePath(path);
				}
				expanding = false;
			}
			@Override
			protected void reExpand() {
				expanding = true;
				for (TreePath path : expanded) {
					expandPath(path);
				}
				expanding = false;
			}
		});
		
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

	@Override
	public RowFilter<?, ?> getRowFilter() {
		return ((TreeTableRowSorter<?>) getRowSorter()).getRowFilter();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <R extends TableModel> void setRowFilter(RowFilter<? super R, ? super Integer> filter) {
            // all fine, because R extends TableModel
            ((TreeTableRowSorter<R>) getRowSorter()).setRowFilter(filter);
    }
	
}
