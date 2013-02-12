package de.mpa.client.ui;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.table.TableModel;
import javax.swing.tree.TreePath;

import org.apache.log4j.lf5.viewer.categoryexplorer.TreeModelAdapter;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;

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
		// Check whether the provided tree model is sortable
		if (!(treeModel instanceof SortableTreeTableModel)) {
			throw new IllegalArgumentException(
					"Model must be a SortableTreeTableModel");
		}

		// Install row sorter to enable sorting by clicking on column header
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
				// iterate cache of expanded paths
				Iterator<TreePath> iterator = expanded.iterator();
				while (iterator.hasNext()) {
					TreePath path = (TreePath) iterator.next();
					if (isPathValid(path)) {
						expandPath(path);
					} else {
						// remove invalid paths
						iterator.remove();
					}
				}
				expanding = false;
			}

			/**
			 * Checks and returns whether the specified path is still part of
			 * the current tree table model.
			 * @param path the path to be checked
			 * @return <code>true</code> if the path is still valid, <code>false</code> otherwise
			 */
			private boolean isPathValid(TreePath path) {
				boolean res = false;
				
				Object root = getTreeTableModel().getRoot();
				Object node = path.getLastPathComponent();
				
				if (node instanceof TreeTableNode) {
					TreeTableNode ttn = (TreeTableNode) node;

		            while (!res && ttn != null) {
		                res = (ttn == root);

		                ttn = ttn.getParent();
		            }
				}
				return res;
			}
		});
		
		// Install expansion listener to keep track of expanded paths
		this.addTreeExpansionListener(new TreeExpansionListener() {
			public void treeExpanded(TreeExpansionEvent event) {
				if (!expanding) expanded.add(event.getPath());
			}
			public void treeCollapsed(TreeExpansionEvent event) {
				if (!expanding) expanded.remove(event.getPath());
			}
		});
		
		// Install tree model listener to sanitize expanded paths cache when
		// messing with the tree structure
		treeModel.addTreeModelListener(new TreeModelAdapter() {
			@Override
			public void treeNodesInserted(TreeModelEvent e) {
				TreePath treePath = new TreePath(e.getPath());
				if (isExpanded(treePath)) {
					expanded.add(treePath);
				}
			}
			@Override
			public void treeNodesRemoved(TreeModelEvent e) {
				TreePath treePath = new TreePath(e.getPath());
				expanded.remove(treePath);
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
