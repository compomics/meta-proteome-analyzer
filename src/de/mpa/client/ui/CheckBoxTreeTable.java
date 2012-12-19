package de.mpa.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.TreePath;
import javax.swing.tree.AbstractLayoutCache.NodeDimensions;

import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.renderer.CellContext;
import org.jdesktop.swingx.renderer.ComponentProvider;
import org.jdesktop.swingx.renderer.DefaultTreeRenderer;
import org.jdesktop.swingx.renderer.IconValue;
import org.jdesktop.swingx.rollover.RolloverProducer;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;

/**
 * A specialized {@link JXTreeTable tree table} containing checkboxes and 
 * hyperlinks in its hierarchical column.
 * 
 * @author A. Behne
 */
public class CheckBoxTreeTable extends JXTreeTable {

	/**
	 * The tree table instance.
	 */
	private JXTreeTable treeTable = this;
	
	/**
	 * The tree inside the hierarchical column of this tree table.
	 */
	private JXTree tree;
	
	/**
	 * The selection model for the checkboxes inside the hierarchical column.
	 */
	private CheckBoxTreeSelectionModel cbtsm;
	
	/**
	 * The editor instance for the hierarchical column.
	 */
	private CheckBoxTreeCellEditor cbtce;
	
	/**
	 * The width of the checkbox part of a cell inside the hierarchical column.
	 */
	private int hotspot;
	
	/**
	 * The center of the checkbox part of a cell inside the hierarchical column.
	 */
	private int center;
	
	/**
	 * An additional margin between a tree cell's expansion handle and the checkbox part inside the hierarchical column.
	 */
	private int indent = 0;
	
	/**
	 * A highlighter used for when the tree table is enabled.
	 */
	private Highlighter enabledHighlighter;
	
	/**
	 * A highlighter used for when the tree table is disabled.
	 */
	private Highlighter disabledHighlighter;
	
	/**
	 * A data structure allowing storage of context-sensitive icons inside the hierarchical column.
	 */
	private IconValue iconValue;
	
	/**
	 * Convenience constructor to create a checkbox tree table from a 
	 * specified root tree node.
	 * @param rootNode The root node of the tree table.
	 */
	public CheckBoxTreeTable(TreeTableNode rootNode) {
		this(new DefaultTreeTableModel(rootNode));
	}
	
	/**
	 * Constructs a checkbox tree table from a specified tree table model.
	 * @param treeModel The tree table model.
	 */
	public CheckBoxTreeTable(TreeTableModel treeModel) {
		super(treeModel);
		
		tree = (JXTree) this.getCellRenderer(-1, this.getHierarchicalColumn());
		
		hotspot = UIManager.getIcon("CheckBox.icon").getIconWidth();
		center = indent + (hotspot)/2 - 4;
		
		final IconCheckBox editorBox = new IconCheckBox();
		editorBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean selected = editorBox.isCheckBoxSelected();
				TreePath path = getPathForRow(editingRow);
				if (selected) {
					cbtsm.addSelectionPath(path);
				} else {
					cbtsm.removeSelectionPath(path);
				}
				editorBox.setSelected(selected);
				// TODO: can repaint() be avoided?
				treeTable.repaint();
			}
		});

		// Install checkbox selection model
		cbtsm = new CheckBoxTreeSelectionModel(treeModel);
		
		// Install tree cell editor
		cbtce = new CheckBoxTreeCellEditor(editorBox, tree);
		
		// Install tree cell renderer
		tree.setCellRenderer(new DefaultTreeRenderer(new CheckBoxTreeCellRenderer()));
		
		tree.setUI(new FillToEdgeTreeUI());
		
		// Install rollover functionality
		// TODO: rollover seems to become unresponsive when clicking expand/collapse controls - investigate!
		treeTable.addPropertyChangeListener(RolloverProducer.ROLLOVER_KEY, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent pce) {
				Point coords = (Point) pce.getNewValue();
				if (coords != null) {
					if (coords.x == getHierarchicalColumn()) {
						editCellAt(coords.y, coords.x);
					} else {
						cbtce.cancelCellEditing();
					}
				} else {
					// TODO: null means we're either inside one of the editor's sub-components or outside the table - how to distinguish one from the other?
//					cbtce.cancelCellEditing();
				}
			}
		});
		
		// Install mouse listener to work around background colors not being
		// updated when editing row is selected
		treeTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent me) {
				// force re-creation of editor on click
				editCellAt(getRowForPath(getPathForLocation(me.getX(), me.getY())),
						getHierarchicalColumn());
			}
		});
		
		// Install highlighters
		treeTable.setSelectionBackground(null);
		
		treeTable.addHighlighter(enabledHighlighter = TableConfig.getSimpleStriping());
		disabledHighlighter = TableConfig.getDisabledStriping();

		// Misc. settings
		treeTable.setColumnMargin(1);
		treeTable.setRowMargin(1);
		treeTable.setShowGrid(true);
		treeTable.getTableHeader().setReorderingAllowed(false);
		treeTable.setColumnControlVisible(true);
		treeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		treeTable.setLargeModel(true);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Overridden to prevent hierarchical node data from being overwritten.
	 */
	@Override
	public void editingStopped(ChangeEvent ce) {
		if (editingColumn == getHierarchicalColumn()) {
			// avoid calling setValueAt(), hierarchical column is not editable per se
			removeEditor();
		} else {
			super.editingStopped(ce);
		}
	}

	@Override
	public TableCellEditor getCellEditor(int row, int column) {
		if (column == getHierarchicalColumn()) {
			return cbtce;
		} else {
			return super.getCellEditor(row, column);
		}
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		if (enabled) {
			treeTable.removeHighlighter(disabledHighlighter);
			treeTable.addHighlighter(enabledHighlighter);
		} else {
			treeTable.removeHighlighter(enabledHighlighter);
			treeTable.addHighlighter(disabledHighlighter);
			treeTable.clearSelection();
			removeEditor();
		}
		super.setEnabled(enabled);
	}

	/**
	 * Convenience method to set the hierarchical column's child indents.
	 * @param left The pixel distance between the center of the node handle and the parent's left side.
	 * @param right The pixel distance between the center of the node handle and this node's left side.
	 * @param margin An additional pixel margin to the right of the handle.
	 */
	public void setIndents(int left, int right, int margin) {
		((BasicTreeUI) tree.getUI()).setLeftChildIndent(left);
		((BasicTreeUI) tree.getUI()).setRightChildIndent(right);
		indent = margin;
		center = margin + (hotspot)/2 - 4;
		// Refresh tree renderer using new indents
		tree.setCellRenderer(new DefaultTreeRenderer(new CheckBoxTreeCellRenderer()));
	}

	/**
	 * Returns the checkbox selection model.
	 * @return The selection model
	 */
	public CheckBoxTreeSelectionModel getCheckBoxTreeSelectionModel() {
		return cbtsm;
	}
	
	/**
	 * Sets the checkbox selection model.
	 * @param selectionModel The selection model to set
	 */
	public void setCheckBoxTreeSelectionModel(CheckBoxTreeSelectionModel selectionModel) {
		this.cbtsm = selectionModel;
	}

	/**
	 * Gets the value-to-icon converter of this tree table.
	 * @return the iconValue
	 */
	public IconValue getIconValue() {
		return iconValue;
	}

	/**
	 * Sets the value-to-icon converter for this tree table.
	 * @param iconValue the iconValue to set
	 */
	public void setIconValue(IconValue iconValue) {
		this.iconValue = iconValue;
	}
	
	private class FillToEdgeTreeUI extends BasicTreeUI {
		@Override
		protected NodeDimensions createNodeDimensions() {
			return new NodeDimensionsHandler() {
				@Override
				public Rectangle getNodeDimensions(
						Object value, int row, int depth, boolean expanded,
						Rectangle size) {
					Rectangle dimensions = super.getNodeDimensions(value, row,
							depth, expanded, size);
					dimensions.width =
							tree.getWidth() - getRowX(row, depth) - 1;
					return dimensions;
				}
			};
		}
		@Override
		protected void paintHorizontalLine(Graphics g, JComponent c,
				int y, int left, int right) { } // do nothing
		@Override
		protected void paintVerticalLine(Graphics g, JComponent c, int x,
				int top, int bottom) { } // do nothing
		
		@Override
		protected void drawCentered(Component c, Graphics graphics, Icon icon,
				int x, int y) {
			icon.paintIcon(c, graphics, 
					x - icon.getIconWidth() / 2 - 1, 
					y - icon.getIconHeight() / 2);
		    
		}
	}
	
	private class CheckBoxTreeCellRenderer extends ComponentProvider<IconCheckBox> {

		@Override
		protected void configureState(CellContext context) { }	// do nothing

		@Override
		protected IconCheckBox createRendererComponent() {
			return new IconCheckBox();
		}

		@Override
		protected void format(CellContext context) {
			
			IconCheckBox checkBox = (IconCheckBox) rendererComponent;
			CheckBoxTreeTableNode node = (CheckBoxTreeTableNode) context.getValue();
			
			if (node != null) {
				checkBox.setVisible(!context.isSelected());
				TreePath path = node.getPath();
				Boolean selected = false;
				if (cbtsm.isPathSelected(path, true)) {
					selected = true;
				} else if (cbtsm.isPartiallySelected(path)) {
					selected = null;
				}
				checkBox.setSelected(selected);
				checkBox.setFixed(node.isFixed());
				checkBox.setURI(node.getURI());

				checkBox.setText(getValueAsString(context));
				if (iconValue != null) {
					checkBox.setIcon(iconValue.getIcon(context));
				} else {
					checkBox.setIcon(context.getIcon());
				}
				checkBox.setEnabled(treeTable.isEnabled());
			}
		}
	}

	/**
	 * Custom tree cell editor for checkbox trees.
	 * 
	 * @author A. Behne
	 */
	private class CheckBoxTreeCellEditor extends AbstractCellEditor implements
			TableCellEditor {

		private IconCheckBox checkBox;
		private JXTree tree;
		private Border highlightBorder = UIManager.getBorder("Table.focusCellHighlightBorder");
		
		public CheckBoxTreeCellEditor(IconCheckBox checkBox, JXTree tree) {
			this.checkBox = checkBox;
			this.tree = tree;
		}
		
		@Override
		public boolean isCellEditable(EventObject evt) {
			return true;
		}
		
		@Override
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			
			CheckBoxTreeTableNode node = (CheckBoxTreeTableNode) tree.getPathForRow(row).getLastPathComponent();
		
			IconCheckBox rendererChk = (IconCheckBox) tree.getCellRenderer().getTreeCellRendererComponent(
					tree, node, isSelected, tree.isExpanded(row), node.isLeaf(), row, true);

			checkBox.setURI(node.getURI());
			checkBox.setIcon(rendererChk.getIcon());
			checkBox.setText(rendererChk.getText());
			checkBox.setSelected(rendererChk.isSelected());
			checkBox.setEnabled(rendererChk.isEnabled());
			checkBox.setFixed(rendererChk.isFixed());

			// inherit visuals from respective table row
			compoundHighlighter.highlight(checkBox, getComponentAdapter(row, column));
			
			Rectangle cellRect = table.getCellRect(0, column, false);
			Rectangle nodeRect = tree.getRowBounds(row);
			int nodeStart = 1 + indent + cellRect.x + nodeRect.x;

			if (table.isColumnSelected(column) && table.isRowSelected(row)) {
				checkBox.setBorder(BorderFactory.createCompoundBorder(highlightBorder,
						BorderFactory.createEmptyBorder(1, nodeStart - 1, 0, 0)));
			} else {
				checkBox.setBorder(BorderFactory.createEmptyBorder(1, nodeStart, 0, 1));
			}
			
			return checkBox;
		}

		@Override
		public Object getCellEditorValue() {
			return checkBox.isSelected();
		}
	}
	
	/**
	 * Helper class for displaying a three-state check box with an additional image label.
	 * 
	 * @author A. Behne
	 */
	private class IconCheckBox extends JPanel {
		
		/**
		 * The checkbox sub-component.
		 */
		private JCheckBox checkBox;
		
		/**
		 * The hyperlink sub-component.
		 */
		private JXHyperlink hyperlink;
		
		/**
		 * The selection state of this component. <code>null</code> denotes indeterminate 
		 * state.
		 */
		private Boolean selected;
		
		/**
		 * The flag denoting whether the selection state of this component cannot be changed.
		 */
		private boolean fixed;
		
		/**
		 * Constructs a checkbox panel containing a tri-state checkbox and a hyperlink label, 
		 * capable of bearing an icon.
		 */
		public IconCheckBox() {
		    this.setLayout(new BorderLayout());
//		    this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			this.setOpaque(false);

			// top checkbox with tri-state visuals
			checkBox = new TriStateCheckBox(center) {
				@Override
				public boolean isPartiallySelected() {
					return (selected == null);
				}
			};
			
			checkBox.setOpaque(false);
			checkBox.setBorder(BorderFactory.createEmptyBorder(0, 0 + indent, 0, 4));
			
			// icon-bearing hyperlink label, uninstall button UI to make it look like a plain label
			hyperlink = new JXHyperlink();
			hyperlink.getUI().uninstallUI(hyperlink);
			hyperlink.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 0));
			hyperlink.setOpaque(true);
			hyperlink.setVerticalTextPosition(SwingConstants.TOP);

			hyperlink.setUnclickedColor(UIManager.getColor("Label.foreground"));

			// lay out components
			this.add(checkBox, BorderLayout.WEST);
			this.add(hyperlink, BorderLayout.CENTER);
		}
		
		/**
		 * Adds an actionlistener to the checkbox sub-component.
		 * @param l the <code>ActionListener</code> to be added
		 */
		public void addActionListener(ActionListener l) {
			checkBox.addActionListener(l);
		}
		
		/**
		 * Returns the icon of the hyperlink sub-component.
		 * @return the hyperlink <code>Icon</code>
		 */
		public Icon getIcon() {
			return hyperlink.getIcon();
		}
		
		/**
		 * Defines the icon the hyperlink sub-component will display.
		 * @param icon the <code>Icon</code> to be displayed. Use <code>null</code> 
		 * to display no icon.
		 */
		public void setIcon(Icon icon) {
			hyperlink.setIcon(icon);
		}
		
		/**
		 * Returns the text of the hyperlink sub-component.
		 * @return
		 */
		public String getText() {
			return hyperlink.getText();
		}
		
		/**
		 * Defines the single line of text this component will display.<br>
		 * If the value of text is null or an empty string, nothing is displayed.
		 * @param text
		 */
		public void setText(String text) {
			hyperlink.setText(text);
		}
		
		@Override
		public void setBackground(Color bg) {
			if (hyperlink != null) {
				hyperlink.setBackground(bg);
			}
		}
		
		/**
		 * Sets the URI the hyperlink label shall direct to.<br>
		 * Note, that this resets the text and icon properties of the label.
		 * @param uri The URI to be set.
		 */
		public void setURI(URI uri) {
			// install/uninstall UI depending on new and old values
			if (uri == null) {
				if (hyperlink.getAction() != null) {
					hyperlink.getUI().uninstallUI(hyperlink);
					hyperlink.setUnclickedColor(UIManager.getColor("Label.foreground"));
					hyperlink.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 0));
				}
				hyperlink.setAction(null);
			} else {
				if (hyperlink.getAction() == null) {
					hyperlink.getUI().installUI(hyperlink);
				}
				hyperlink.setURI(uri);
			}
		}
		
		public boolean isCheckBoxSelected() {
			return checkBox.isSelected();
		}
		
		public Boolean isSelected() {
			return selected;
		}
		
		/**
		 * Sets the selection state of this component's checkbox.
		 * @param selected <code>true</code> if the button is selected, 
		 * <code>false</code> if not selected, <code>null</code> if indeterminate
		 */
		public void setSelected(Boolean selected) {
			if (selected != isSelected()) {
				this.selected = selected;
				if (selected == null) {
					checkBox.setSelected(false);
				} else {
					checkBox.setSelected(selected);
				}
			}
		}
		
		/**
		 * Sets the enabled state of this component.
		 * @param enabled <code>true</code> if enabled, <code>false</code> otherwise
		 */
		public void setEnabled(boolean enabled) {
			super.setEnabled(enabled);
			checkBox.setEnabled(enabled);
			hyperlink.setEnabled(enabled);
		}
		
		public boolean isFixed() {
			return fixed;
		}
		
		/**
		 * Sets the fixed state of this component's checkbox.
		 * @param fixed <code>true</code> if the selection state of the checkbox 
		 * cannot be changed, <code>false</code> otherwise
		 */
		public void setFixed(boolean fixed) {
			this.fixed = fixed;
			checkBox.setEnabled(this.isEnabled() && !fixed);
		}
		
	}
	
}
