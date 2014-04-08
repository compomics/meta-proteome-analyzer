package de.mpa.client.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.RowFilter;
import javax.swing.RowSorter;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.event.TableColumnModelExtListener;
import org.jdesktop.swingx.table.ColumnFactory;
import org.jdesktop.swingx.table.DefaultTableColumnModelExt;
import org.jdesktop.swingx.table.TableColumnExt;
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
		
		// Install column factory to cache and restore visuals after sorting/filtering
		this.setColumnFactory(new SortableColumnFactory(this));

		// Install row sorter to enable sorting by clicking on column header
		this.setSortable(true);
		this.setAutoCreateRowSorter(true);
		this.setRowSorter(new CheckBoxTreeTableRowSorter(this));
		
	}
	
	@Override
	public void setTreeTableModel(TreeTableModel treeModel) {
		// forward model change to column factory
		((SortableColumnFactory) this.getColumnFactory()).setColumnCount(
				treeModel.getColumnCount());
		
		super.setTreeTableModel(treeModel);
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
	
	@Override
	protected TableColumnModel createDefaultColumnModel() {
		return new DefaultTableColumnModelExt() {
			@Override
			public void removeColumn(TableColumn column) {
				super.removeColumn(column);
				this.fireColumnPropertyChange(new PropertyChangeEvent(
						column, "visible", true, ((TableColumnExt) column).isVisible()));
			}
		};
	}
	
	@Override
	protected void postprocessModelChange(TableModelEvent e) {
		super.postprocessModelChange(e);
		ColumnFactory factory = this.getColumnFactory();
		if (factory instanceof SortableColumnFactory) {
			((SortableColumnFactory) factory).reorderColumns();
		}
	}
	
	/**
	 * Calculates aggregate values of non-leaf nodes for the specified column
	 * index and aggregation function.
	 * @param column the column model index to aggregate
	 * @param aggFcn the aggregation function
	 */
	protected void aggregate(int column, AggregateFunction aggFcn) {
		if (aggFcn != null) {
			// start recursion at the tree root
			this.aggregate((TreeTableNode) getTreeTableModel().getRoot(), column, aggFcn);
			// update highlighters on column
			this.updateHighlighters(column);
		}
	}
	
	/**
	 * Recursively aggregates the specified node's children values of the
	 * specified column using the specified aggregation function.
	 * @param node the parent node
	 * @param column the column model index to aggregate
	 * @param aggFcn the aggregation function
	 * @return the aggregated value
	 */
	protected Object aggregate(TreeTableNode node, int column, AggregateFunction aggFcn) {
		if (aggFcn != AggregateFunction.DISTINCT) {
			// check whether parent node is a leaf
			if (node.isLeaf()) {
				// leaf nodes return their column values
				return node.getValueAt(column);
			} else {
				// iterate child nodes
				int childCount = node.getChildCount();
				Object[] values = new Object[childCount];
				for (int i = 0; i < childCount; i++) {
					// recurse
					TreeTableNode child = node.getChildAt(i);
					Object aggVal = this.aggregate(child, column, aggFcn);
					values[i] = aggVal;
				}
				// apply aggregation function
				Object aggVal = aggFcn.aggregate(values);
				// store result in node
				node.setValueAt(aggVal, column);
				// return result to parent recursion call
				return aggVal;
			}
		} else {
			// special case for distinct count aggregation
			if (node instanceof SortableCheckBoxTreeTableNode) {
				SortableCheckBoxTreeTableNode sortableNode = (SortableCheckBoxTreeTableNode) node;
				// check whether parent node is a leaf
				if (sortableNode.isLeaf()) {
					// leaf nodes return their column values
					return sortableNode.getValuesAt(column);
				} else {
					// iterate child nodes
					int childCount = node.getChildCount();
					Set<Object> values = new HashSet<Object>();
					for (int i = 0; i < childCount; i++) {
						// recurse
						TreeTableNode child = node.getChildAt(i);
						Object aggVal = this.aggregate(child, column, aggFcn);
						values.addAll((Collection<?>) aggVal);
					}
					// apply aggregation function and store result in node
					// TODO: at this point simply calling size() would suffice, the implementation of the DISTINCT AggregateFunction member could use some improvement
					node.setValueAt(aggFcn.aggregate(values), column);
					// return value collection to parent recursion call
					return values;
				}
			}
		}
		return null;
	}

	/**
	 * Custom row sorter implementation.
	 * @author A. Behne
	 */
	private class CheckBoxTreeTableRowSorter extends TreeTableRowSorter<TableModel> {
		
		private Enumeration<TreePath> expPaths;

		/**
		 * Constructs a {@link RowSorter} for the provided {@link JXTreeTable}.
		 * @param treeTable The tree table to which the sorter shall be attached.
		 */
		public CheckBoxTreeTableRowSorter(JXTreeTable treeTable) {
			super(treeTable);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		protected void preCollapse() {
			this.expPaths = (Enumeration<TreePath>) this.treeTable.getExpandedDescendants(
					new TreePath(this.treeModel.getRoot()));
		}
		
		@Override
		protected void reExpand() {
			if (this.expPaths != null) {
				while (this.expPaths.hasMoreElements()) {
					TreePath expPath = this.expPaths.nextElement();
					this.treeTable.expandPath(expPath);
				}
			}
		}
		
	}
	
	/**
	 * Column factory extension to automatically cache and restore column properties.
	 * @author A. Behne
	 */
	private class SortableColumnFactory extends ColumnFactory {
		
		/**
		 * The parent tree table reference.
		 */
		private JXTreeTable treeTbl;
		
		/**
		 * The array of column prototypes.
		 */
		private TableColumnExt[] prototypes;

		/**
		 * The array of column view coordinates.
		 */
		private int[] viewToModel;

		/**
		 * Constructs a column factory for the specified tree table.
		 * @param treeTbl the tree table reference
		 */
		public SortableColumnFactory(JXTreeTable treeTbl) {
			super();
			this.treeTbl = treeTbl;
			this.setColumnCount(treeTbl.getColumnCount());
			
			treeTbl.getColumnModel().addColumnModelListener(new TableColumnModelExtListener() {
				@Override
				public void columnMoved(TableColumnModelEvent evt) {
					// check whether columns have been reordered (or are in the process of being reordered)
					if (evt.getFromIndex() != evt.getToIndex()) {
						DefaultTableColumnModelExt model = (DefaultTableColumnModelExt) evt.getSource();
						// only update cache when visible columns are reordered
						if (!model.isAddedFromInvisibleEvent(0)) {
							SortableColumnFactory factory = SortableColumnFactory.this;
							JXTreeTable treeTbl = factory.treeTbl;
							// cache view coordinates
							for (int i = 0; i < treeTbl.getColumnModel().getColumnCount(); i++) {
								int modelIndex = treeTbl.convertColumnIndexToModel(i);
								factory.viewToModel[i] = modelIndex;
							}
							// FIXME: reordering while columns are hidden messes up coordinates (low priority)
						}
					}
				}
				@Override
				public void columnPropertyChange(PropertyChangeEvent evt) {
					if (evt.getNewValue().equals(evt.getOldValue())) {
						return;
					}
					TableColumnExt column = (TableColumnExt) evt.getSource();
					int modelIndex = column.getModelIndex();
					// update prototype cache
					SortableColumnFactory.this.prototypes[modelIndex] = new TableColumnExt2(column);
				}
				/* we don't need these */
				public void columnSelectionChanged(ListSelectionEvent evt) { }
				public void columnMarginChanged(ChangeEvent evt) { }
				public void columnAdded(TableColumnModelEvent evt) { }
				public void columnRemoved(TableColumnModelEvent evt) { }
			});
		}
		
		/**
		 * Sets the number of columns to the specified value.
		 * @param columnCount the number of columns
		 */
		public void setColumnCount(int columnCount) {
			this.prototypes = new TableColumnExt[columnCount];
			this.viewToModel = new int[columnCount];
			
			// init view coordinates
			for (int i = 0; i < this.viewToModel.length; i++) {
				this.viewToModel[i] = i;
			}
		}
		
		/**
		 * Reorders the table's columns using the cached view-to-model coordinate mapping.
		 */
		public void reorderColumns() {
			boolean[] hidden = this.unhideAll();
			
			// cache coordinates locally
			int[] targetIndexes = Arrays.copyOf(this.viewToModel, this.viewToModel.length);
			// initialize current positions
			Vector<Integer> currentIndexes = new Vector<Integer>();
			for (int i = 0; i < targetIndexes.length; i++) {
				currentIndexes.add(i);
			}
			// iterate columns
			for (int i = 0; i < targetIndexes.length; i++) {
				int targetIndex = targetIndexes[i];
				int currentIndex = currentIndexes.indexOf(targetIndex);
				// only move colors to the left
				if (currentIndex > i) {
					this.treeTbl.moveColumn(currentIndex, i);
					// update current positions in a fashion similar to what happens to the column model
					currentIndexes.remove(currentIndex);
					currentIndexes.insertElementAt(targetIndex, i);
				}
			}
//			factory.viewToModel = viewToModel;
			this.hideAll(hidden);
			
		}

		/** Convenience method to unhide all columns and return an array of their visibility states. */
		private boolean[] unhideAll() {
			List<TableColumn> columns =
					((DefaultTableColumnModelExt) this.treeTbl.getColumnModel()).getColumns(true);
			boolean[] hidden = new boolean[columns.size()];
			int i = 0;
			for (TableColumn column : columns) {
				TableColumnExt columnExt = (TableColumnExt) column;
				hidden[i++] = columnExt.isVisible();
				columnExt.setVisible(true);
			}
			return hidden;
		}
		
		/** Convenience method to set the visibility states of all columns. */
		private void hideAll(boolean[] hidden) {
			List<TableColumn> columns =
					((DefaultTableColumnModelExt) this.treeTbl.getColumnModel()).getColumns(true);
			int i = 0;
			for (TableColumn column : columns) {
				TableColumnExt columnExt = (TableColumnExt) column;
				columnExt.setVisible(hidden[i++]);
			}
		}

		@Override
		protected TableCellRenderer getHeaderRenderer(final JXTable table,
				TableColumnExt columnExt) {
			// Check whether we have a ComponentHeader installed in the table
			if (table.getTableHeader() instanceof ComponentTableHeader) {
				TableCellRenderer renderer = columnExt.getHeaderRenderer();
				if (renderer == null) {
					// Create checkbox widget for hierarchical column header
					JComponent comp = null;
					if (columnExt.getModelIndex() == 0) {
						
						JCheckBox selChk = new TriStateCheckBox() {
							/** The tree table reference. */
							private JXTreeTable treeTbl = (JXTreeTable) table;
							{
								/* Hook into tree table checkbox selection model to synchronize 
								 * root node selection state with checkbox selection state */ 
								this.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent evt) {
										TreePath rootPath = new TreePath(
												treeTbl.getTreeTableModel().getRoot());
										TreeSelectionModel cbtsm =
												((CheckBoxTreeTable) treeTbl).getCheckBoxTreeSelectionModel();
										if (isSelected()) {
											cbtsm.addSelectionPath(rootPath);
										} else {
											cbtsm.removeSelectionPath(rootPath);
										}
									}
								});
								TreeSelectionModel cbtsm =
										((CheckBoxTreeTable) treeTbl).getCheckBoxTreeSelectionModel();
								cbtsm.addTreeSelectionListener(new TreeSelectionListener() {
									public void valueChanged(TreeSelectionEvent evt) {
										TreePath rootPath = new TreePath(
												treeTbl.getTreeTableModel().getRoot());
										TreeSelectionModel cbtsm =
												((CheckBoxTreeTable) treeTbl).getCheckBoxTreeSelectionModel();
										if (rootPath.equals(evt.getPath())) {
											setSelected(cbtsm.isPathSelected(rootPath));
										}
										// repaint hierarchical column header
										JTableHeader header = treeTbl.getTableHeader();
										header.repaint(header.getHeaderRect(0));
									}
								});
							}
							@Override
							public boolean isPartiallySelected() {
								TreePath rootPath = new TreePath(
										treeTbl.getTreeTableModel().getRoot());
								CheckBoxTreeSelectionModel cbtsm =
										((CheckBoxTreeTable) treeTbl).getCheckBoxTreeSelectionModel();
								return cbtsm.isPartiallySelected(rootPath);
							}
							@Override
							protected void paintComponent(Graphics g) {
								g.setColor(UIManager.getColor("TableHeader.background"));
								g.fillRect(2, 3, 9, 9);
								super.paintComponent(g);
							}
						};
						selChk.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));
						selChk.setBackground(new Color(0, 0, 0, 0));
						selChk.setOpaque(false);
						
						TreePath rootPath = new TreePath(
								treeTbl.getTreeTableModel().getRoot());
						CheckBoxTreeSelectionModel cbtsm =
								((CheckBoxTreeTable) treeTbl).getCheckBoxTreeSelectionModel();
						selChk.setSelected(cbtsm.isPathSelected(rootPath));
						
						comp = selChk;
					}
					// wrap component in special header renderer
					Icon icon = null;
					int orientation = SwingConstants.TRAILING;
					if (columnExt.getHeaderValue() instanceof Icon) {
						icon = (Icon) columnExt.getHeaderValue();
						orientation = SwingConstants.LEADING;
					}
					renderer = new ComponentHeaderRenderer(comp, icon, orientation);
				}
				return renderer;
			}
			return null;
		}
		
		@Override
		public void configureColumnWidths(JXTable table,
				TableColumnExt columnExt) {
			// get cached values and apply them
			int modelIndex = columnExt.getModelIndex();
			TableColumnExt prototype = this.prototypes[modelIndex];
			
			int prefWidth = prototype.getPreferredWidth();
			if (prefWidth > 0) {
				columnExt.setPreferredWidth(prefWidth);
			}
			
			int minWidth = prototype.getMinWidth();
			columnExt.setMinWidth(minWidth);
			
			int maxWidth = prototype.getMaxWidth();
			if (maxWidth > 0) {
				columnExt.setMaxWidth(maxWidth);
			}
			
		}
		
		@Override
		public TableColumnExt createAndConfigureTableColumn(TableModel model, int modelIndex) {
			if (modelIndex < this.prototypes.length) {
				// get prototype column
				TableColumnExt prototype = this.prototypes[modelIndex];
				// if prototype has not been initialized...
				if (prototype == null) {
					// lazily instantiate default column
					prototype = super.createAndConfigureTableColumn(model, modelIndex);
					this.prototypes[modelIndex] = prototype;
				}
				// create column using prototype properties
				TableColumnExt column = new TableColumnExt2(prototype);
				
				// configure header renderer
				column.setHeaderRenderer(this.getHeaderRenderer(this.treeTbl, column));
				
				return column;
			}
			return null;
		}
		
	}
	
	/**
	 * Extension to table column allowing to specify additional properties (e.g. aggregate functions).
	 * @author A. Behne
	 */
	public class TableColumnExt2 extends TableColumnExt {
		
		/**
		 * The aggregate function of this column.
		 */
		private AggregateFunction aggFcn = null;
		
		/**
		 * Instantiates a new table view column with all properties copied from the given original.
		 * @param columnExt the column to copy properties from
		 */
		public TableColumnExt2(TableColumnExt columnExt) {
			super(columnExt);
			if (columnExt instanceof TableColumnExt2) {
				this.aggFcn = ((TableColumnExt2) columnExt).getAggregateFunction();
			}
		}
		
		/**
		 * Calculates and sets aggregate values of non-leaf nodes for this column.
		 */
		public void aggregate() {
			SortableCheckBoxTreeTable.this.aggregate(this.getModelIndex(), this.getAggregateFunction());
		}

		/**
		 * Returns whether this column can aggregate values for non-leaf nodes, 
		 * i.e. whether an aggregation function has been defined for this column.
		 * @return <code>true</code> if this column can aggregate, <code>false</code> otherwise
		 */
		public boolean canAggregate() {
			return (this.aggFcn != null);
		}

		/**
		 * Returns the aggregate function of this column.
		 * @return the aggregate function
		 */
		public AggregateFunction getAggregateFunction() {
			return this.aggFcn;
		}

		/**
		 * Sets the aggregate function of this column.<br>
		 * If <code>null</code> is provided <code>canAggregate()</code> will return <code>false</code>.
		 * @param aggFcn the aggregate function to set
		 */
		public void setAggregateFunction(AggregateFunction aggFcn) {
			AggregateFunction oldValue = this.getAggregateFunction();
			this.aggFcn = aggFcn;
			this.firePropertyChange("aggregate", oldValue, aggFcn);
			this.aggregate();
		}
		
	}
	
}
