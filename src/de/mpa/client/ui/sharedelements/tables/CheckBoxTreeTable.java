package de.mpa.client.ui.sharedelements.tables;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.AbstractLayoutCache.NodeDimensions;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.CompoundHighlighter;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.hyperlink.AbstractHyperlinkAction;
import org.jdesktop.swingx.renderer.CellContext;
import org.jdesktop.swingx.renderer.ComponentProvider;
import org.jdesktop.swingx.renderer.DefaultTreeRenderer;
import org.jdesktop.swingx.renderer.IconValue;
import org.jdesktop.swingx.rollover.RolloverProducer;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;

import de.mpa.util.URLstarter;

/**
 * A specialized {@link JXTreeTable tree table} containing checkboxes and
 * hyperlinks in its hierarchical column.
 * 
 * @author A. Behne
 */
@SuppressWarnings("serial")
public class CheckBoxTreeTable extends JXTreeTable {

	/**
	 * The tree table instance.
	 */
	private final JXTreeTable treeTable = this;

	/**
	 * The tree inside the hierarchical column of this tree table.
	 */
	private final JXTree tree;

	/**
	 * The selection model for the checkboxes inside the hierarchical column.
	 */
	private CheckBoxTreeSelectionModel cbtsm;

	/**
	 * The editor instance for the hierarchical column.
	 */
	private CheckBoxTreeTable.CheckBoxTreeCellEditor cbtce;

	/**
	 * An additional margin between a tree cell's expansion handle and the
	 * checkbox part inside the hierarchical column.
	 */
	private int indent;

	/**
	 * A highlighter used for when the tree table is enabled.
	 */
	private final Highlighter enabledHighlighter;

	/**
	 * A highlighter used for when the tree table is disabled.
	 */
	private final Highlighter disabledHighlighter;

	/**
	 * A data structure allowing storage of context-sensitive icons inside the
	 * hierarchical column.
	 */
	private IconValue iconValue;

	/**
	 * Convenience constructor to create a checkbox tree table from a specified
	 * root tree node.
	 * 
	 * @param rootNode
	 *            The root node of the tree table.
	 */
	public CheckBoxTreeTable(TreeTableNode rootNode) {
		this(new DefaultTreeTableModel(rootNode));
	}

	/**
	 * Constructs a checkbox tree table from a specified tree table model.
	 * 
	 * @param treeModel
	 *            The tree table model.
	 */
	public CheckBoxTreeTable(TreeTableModel treeModel) {
		super(treeModel);

		this.tree = (JXTree) getCellRenderer(-1, getHierarchicalColumn());

		CheckBoxTreeTable.IconCheckBox editorBox = new CheckBoxTreeTable.IconCheckBox();
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
				firePropertyChange("checkBoxSelectionChanged", false, true);
				treeTable.repaint();
			}
		});

		// Install mouse adapter on editor component to capture selection
		// dragging
		MouseAdapter ma = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent evt) {
				this.forwardEvent(evt);
			}

			@Override
			public void mouseReleased(MouseEvent evt) {
				this.forwardEvent(evt);
			}

			@Override
			public void mouseDragged(MouseEvent evt) {
				this.forwardEvent(evt);
			}

			/** Forwards mouse events to the parent tree table. */
			private void forwardEvent(MouseEvent evt) {
				CheckBoxTreeTable treeTbl = CheckBoxTreeTable.this;
				if (evt.getSource() != treeTbl) {
					Rectangle bounds = editorComp.getBounds();
					treeTbl.dispatchEvent(
							new MouseEvent(treeTbl, evt.getID(), evt.getWhen(), evt.getModifiers(), bounds.x,
									bounds.y + evt.getY(), evt.getClickCount(), evt.isPopupTrigger(), evt.getButton()));
				}
			}
		};
		editorBox.addMouseListener(ma);
		editorBox.addMouseMotionListener(ma);

		// Install mouse adapter on tree table to properly process editor-based
		// selection dragging
		this.addMouseMotionListener(new MouseAdapter() {
			/**
			 * {@inheritDoc}
			 * <p>
			 * Adapted from <code>BasicTableUI.Handler</code>
			 */
			@Override
			public void mouseDragged(MouseEvent evt) {
				CheckBoxTreeTable treeTbl = CheckBoxTreeTable.this;
				if (treeTbl.isEditing()) {
					Point p = evt.getPoint();
					int row = treeTbl.rowAtPoint(p);
					int column = treeTbl.columnAtPoint(p);
					if ((column == -1) || (row == -1)) {
						return;
					}
					treeTbl.changeSelection(row, column, this.isMenuShortcutKeyDown(evt), true);

					Rectangle bounds = editorComp.getBounds();
					prepareEditor(cellEditor, treeTbl.rowAtPoint(new Point(bounds.x, bounds.y)), column);
				}
			}

			private boolean isMenuShortcutKeyDown(InputEvent event) {
				return (event.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) != 0;
			}
		});

		// Install checkbox selection model
		cbtsm = new CheckBoxTreeSelectionModel(treeModel);

		// Install tree cell editor
		cbtce = new CheckBoxTreeTable.CheckBoxTreeCellEditor(editorBox);

		// Install tree cell renderer
		this.setTreeCellRenderer(new DefaultTreeRenderer(new CheckBoxTreeCellRenderer()));

		tree.setUI(new FillToEdgeTreeUI());

		// Install rollover functionality
		treeTable.addPropertyChangeListener(RolloverProducer.ROLLOVER_KEY, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent pce) {
				Point coords = (Point) pce.getNewValue();
				if (coords != null) {
					if (coords.x == getHierarchicalColumn()) {
						editCellAt(coords.y, coords.x);
					} else {
						CheckBoxTreeTable.this.cbtce.cancelCellEditing();
					}
				} else {
					// TODO: null means we're either inside one of the editor's
					// sub-components or outside the table - how to distinguish
					// one from the other?
					// cbtce.cancelCellEditing();
				}
			}
		});

		// Install mouse listener to work around background colors not being
		// updated when editing row is selected
		treeTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent me) {
				if (treeTable.columnAtPoint(me.getPoint()) == treeTable.getHierarchicalColumn()) {
					// force re-creation of editor on click
					editCellAt(getRowForPath(getPathForLocation(me.getX(), me.getY())), getHierarchicalColumn());
				}
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
		// treeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
			// avoid calling setValueAt(), hierarchical column is not editable
			// per se
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
	 * Installs the specified checkbox tree cell editor.
	 * 
	 * @param cbtce
	 *            the tree cell editor
	 */
	public void setTreeCellEditor(CheckBoxTreeTable.CheckBoxTreeCellEditor cbtce) {
		this.cbtce = cbtce;
	}

	/**
	 * Convenience method to set the hierarchical column's child indents.
	 * 
	 * @param left
	 *            The pixel distance between the center of the node handle and
	 *            the parent's left side.
	 * @param right
	 *            The pixel distance between the center of the node handle and
	 *            this node's left side.
	 * @param margin
	 *            An additional pixel margin to the right of the handle.
	 */
	public void setIndents(int left, int right, int margin) {
		((BasicTreeUI) tree.getUI()).setLeftChildIndent(left);
		((BasicTreeUI) tree.getUI()).setRightChildIndent(right);
		indent = margin;
		// Refresh tree renderer using new indents
		tree.setCellRenderer(new DefaultTreeRenderer(new CheckBoxTreeCellRenderer()));
	}

	/**
	 * Returns the checkbox selection model.
	 * 
	 * @return The selection model
	 */
	public CheckBoxTreeSelectionModel getCheckBoxTreeSelectionModel() {
		return cbtsm;
	}

	/**
	 * Sets the checkbox selection model.
	 * 
	 * @param selectionModel
	 *            The selection model to set
	 */
	public void setCheckBoxTreeSelectionModel(CheckBoxTreeSelectionModel selectionModel) {
		this.cbtsm = selectionModel;
	}

	/**
	 * Gets the value-to-icon converter of this tree table.
	 * 
	 * @return the iconValue
	 */
	public IconValue getIconValue() {
		return iconValue;
	}

	/**
	 * Sets the value-to-icon converter for this tree table.
	 * 
	 * @param iconValue
	 *            the iconValue to set
	 */
	public void setIconValue(IconValue iconValue) {
		this.iconValue = iconValue;
	}

	private class FillToEdgeTreeUI extends BasicTreeUI {
		@Override
		protected NodeDimensions createNodeDimensions() {
			return new NodeDimensionsHandler() {
				@Override
				public Rectangle getNodeDimensions(Object value, int row, int depth, boolean expanded, Rectangle size) {
					Rectangle dimensions = super.getNodeDimensions(value, row, depth, expanded, size);
					dimensions.width = tree.getWidth() - getRowX(row, depth) - 1;
					return dimensions;
				}
			};
		}

		@Override
		protected void paintHorizontalLine(Graphics g, JComponent c, int y, int left, int right) {
		} // do nothing

		@Override
		protected void paintVerticalLine(Graphics g, JComponent c, int x, int top, int bottom) {
		} // do nothing

		@Override
		protected void drawCentered(Component c, Graphics graphics, Icon icon, int x, int y) {
			icon.paintIcon(c, graphics, x - icon.getIconWidth() / 2 - 1, y - icon.getIconHeight() / 2);

		}
	}

	/**
	 * TODO: API
	 *
	 * @author A. Behne
	 */
	public class CheckBoxTreeCellRenderer extends ComponentProvider<CheckBoxTreeTable.IconCheckBox> {

		@Override
		protected void configureState(CellContext context) {

			CheckBoxTreeTable.IconCheckBox checkBox = rendererComponent;
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

				// checkBox.setText(getValueAsString(context));
				checkBox.setText(node.getValueAt(0).toString());
				if (iconValue != null) {
					checkBox.setIcon(iconValue.getIcon(context));
				} else {
					checkBox.setIcon(context.getIcon());
				}
				checkBox.setEnabled(treeTable.isEnabled());

				// inherit visuals from respective table row
				CheckBoxTreeTable.this.getCompoundHighlighter().highlight(checkBox,
						getComponentAdapter(context.getRow(), context.getColumn()));
			}
		}

		@Override
		protected CheckBoxTreeTable.IconCheckBox createRendererComponent() {
			return new CheckBoxTreeTable.IconCheckBox();
		}

		@Override
		protected void format(CellContext context) {
			// do nothing
		}

	}

	/**
	 * Custom tree cell editor for checkbox trees.
	 * 
	 * @author A. Behne
	 */
	public class CheckBoxTreeCellEditor extends AbstractCellEditor implements TableCellEditor {

		/**
		 * The renderer component.
		 */
		private CheckBoxTreeTable.IconCheckBox checkBox;

		/**
		 * The highlight border.
		 */
		private Border highlightBorder = UIManager.getBorder("Table.focusCellHighlightBorder");

		/**
		 * Creates a tree cell editor for checkbox tree tables.
		 * 
		 * @param checkBox
		 */
		public CheckBoxTreeCellEditor(CheckBoxTreeTable.IconCheckBox checkBox) {
			this.checkBox = checkBox;
		}

		@Override
		public boolean isCellEditable(EventObject evt) {
			return true;
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {

			JXTree tree = (JXTree) table.getCellRenderer(-1, ((JXTreeTable) table).getHierarchicalColumn());

			CheckBoxTreeTableNode node = (CheckBoxTreeTableNode) tree.getPathForRow(row).getLastPathComponent();

			CheckBoxTreeTable.IconCheckBox rendererChk = (CheckBoxTreeTable.IconCheckBox) tree.getCellRenderer()
					.getTreeCellRendererComponent(tree, node, isSelected, tree.isExpanded(row), node.isLeaf(), row,
							true);

			this.checkBox.setURI(node.getURI());
			this.checkBox.setIcon(rendererChk.getIcon());
			this.checkBox.setText(rendererChk.getText());
			this.checkBox.setSelected(rendererChk.isSelected());
			this.checkBox.setEnabled(rendererChk.isEnabled());
			this.checkBox.setFixed(rendererChk.isFixed());

			// inherit visuals from respective table row by applying
			// highlighters
			ComponentAdapter adapter = CheckBoxTreeTable.this.getComponentAdapter(row, column);
			getCompoundHighlighter().highlight(this.checkBox, adapter);
			Highlighter[] highlighters = getColumnExt(column).getHighlighters();
			new CompoundHighlighter(highlighters).highlight(this.checkBox, adapter);

			Rectangle cellRect = table.getCellRect(0, column, false);
			Rectangle nodeRect = tree.getRowBounds(row);
			// int nodeStart = 1 + indent + cellRect.x + nodeRect.x;
			int nodeStart = CheckBoxTreeTable.this.indent + 1;
			nodeStart += cellRect.x;
			if (nodeRect != null) {
				nodeStart += nodeRect.x;
			}

			Border border;
			if (table.isColumnSelected(column) && table.isRowSelected(row)) {
				border = BorderFactory.createCompoundBorder(this.highlightBorder,
						BorderFactory.createEmptyBorder(0, nodeStart - 1, 0, 0));
			} else {
				border = BorderFactory.createEmptyBorder(1, nodeStart, 0, 1);
			}
			this.checkBox.setBorder(border);

			return this.checkBox;
		}

		@Override
		public Object getCellEditorValue() {
			return this.checkBox.isSelected();
		}

	}

	/**
	 * Helper class for displaying a three-state check box with an additional
	 * image label.
	 *
	 * @author A. Behne
	 */
	public class IconCheckBox extends JPanel {

		/**
		 * The checkbox sub-component.
		 */
		private final TriStateCheckBox checkBox;

		/**
		 * The hyperlink sub-component.
		 */
		private final JXHyperlink hyperlink;

		/**
		 * The selection state of this component. <code>null</code> denotes
		 * indeterminate state.
		 */
		private Boolean selected;

		/**
		 * The flag denoting whether the selection state of this component
		 * cannot be changed.
		 */
		private boolean fixed;

		/**
		 * Constructs a checkbox panel containing a tri-state checkbox and a
		 * hyperlink label, capable of bearing an icon.
		 */
		public IconCheckBox() {
			this(0);
		}

		/**
		 * Constructs a checkbox panel containing a tri-state checkbox and a
		 * hyperlink label, capable of bearing an icon.
		 * 
		 * @param vOffset
		 *            a vertical pixel offset for painting the indeterminate
		 *            state
		 */
		public IconCheckBox(int vOffset) {

			setLayout(new BorderLayout());
			setOpaque(false);

			// top checkbox with tri-state visuals
			this.checkBox = new TriStateCheckBox() {
				@Override
				public boolean isPartiallySelected() {
					return (CheckBoxTreeTable.IconCheckBox.this.selected == null);
				}
			};

			this.checkBox.setOpaque(false);
			this.checkBox.setBorder(BorderFactory.createEmptyBorder(2, 0 + CheckBoxTreeTable.this.indent, 1, 4));

			// icon-bearing hyperlink label, uninstall button UI to make it look
			// like a plain label
			this.hyperlink = new JXHyperlink();
			this.hyperlink.setBackground(Color.WHITE);
			this.hyperlink.getUI().uninstallUI(this.hyperlink);
			this.hyperlink.setBorder(BorderFactory.createEmptyBorder(0, 1, -1, 0));
			this.hyperlink.setOpaque(true);
			this.hyperlink.setVerticalTextPosition(SwingConstants.TOP);

			this.hyperlink.setUnclickedColor(UIManager.getColor("Label.foreground"));

			// TODO hyperlink should use URLstarter.openURL(url);

			// lay out components
			add(this.checkBox, BorderLayout.WEST);
			add(this.hyperlink, BorderLayout.CENTER);
		}

		/**
		 * Adds an actionlistener to the checkbox sub-component.
		 * 
		 * @param l
		 *            the <code>ActionListener</code> to be added
		 */
		public void addActionListener(ActionListener l) {
			this.checkBox.addActionListener(l);
		}

		/**
		 * Returns the icon of the hyperlink sub-component.
		 * 
		 * @return the hyperlink <code>Icon</code>
		 */
		public Icon getIcon() {
			return this.hyperlink.getIcon();
		}

		/**
		 * Defines the icon the hyperlink sub-component will display.
		 * 
		 * @param icon
		 *            the <code>Icon</code> to be displayed. Use
		 *            <code>null</code> to display no icon.
		 */
		public void setIcon(Icon icon) {
			this.hyperlink.setIcon(icon);
		}

		/**
		 * Returns the text of the hyperlink sub-component.
		 * 
		 * @return
		 */
		public String getText() {
			return this.hyperlink.getText();
		}

		/**
		 * Defines the single line of text this component will display.<br>
		 * If the value of text is null or an empty string, nothing is
		 * displayed.
		 * 
		 * @param text
		 */
		public void setText(String text) {
			this.hyperlink.setText(text);
			this.hyperlink.setToolTipText(text);
			this.checkBox.setToolTipText(text);
		}

		@Override
		public Color getBackground() {
			Color background = null;
			if (this.hyperlink != null) {
				background = this.hyperlink.getBackground();
			}
			if (background == null) {
				background = super.getBackground();
			}
			return background;
		}

		@Override
		public void setBackground(Color bg) {
			if ((this.hyperlink != null) && (bg != null)) {
				this.hyperlink.setBackground(bg);
			}
		}

		@Override
		public Font getFont() {
			Font font = null;
			if (this.hyperlink != null) {
				this.hyperlink.getFont();
			}
			if (font == null) {
				font = super.getFont();
			}
			return font;
		}

		@Override
		public void setFont(Font font) {
			if ((this.hyperlink != null) && (font != null)) {
				this.hyperlink.setFont(font);
			}
		}

		/**
		 * Sets the URI the hyperlink label shall direct to.<br>
		 * Note, that this resets the text and icon properties of the label.
		 * 
		 * @param uri
		 *            The URI to be set.
		 */
		public void setURI(URI uri) {
			// install/uninstall UI depending on new and old values
			if (uri == null) {
				if (this.hyperlink.getAction() != null) {
					this.hyperlink.getUI().uninstallUI(this.hyperlink);
					this.hyperlink.setUnclickedColor(UIManager.getColor("Label.foreground"));
					this.hyperlink.setBorder(BorderFactory.createEmptyBorder(0, 1, -1, 0));
				}
				this.hyperlink.setAction(null);
			} else {
				if (this.hyperlink.getAction() == null) {

					this.hyperlink.getUI().installUI(this.hyperlink);
				}

				this.hyperlink.setAction(new AbstractHyperlinkAction<String>() {

					@Override
					public void actionPerformed(ActionEvent e) {
						URLstarter.openURL(uri.toString());
					}
				});
				// this.hyperlink.setURI(uri);
			}
		}

		/**
		 * Returns <code>true</code> whether checkbox is selected else
		 * <code>false</code>.
		 * 
		 * @return the checkbox selection state
		 */
		public boolean isCheckBoxSelected() {
			return this.checkBox.isSelected();
		}

		/**
		 * 
		 * @return
		 */
		public Boolean isSelected() {
			return this.selected;
		}

		/**
		 * Sets the selection state of this component's checkbox.
		 * 
		 * @param selected
		 *            <code>true</code> if the button is selected,
		 *            <code>false</code> if not selected, <code>null</code> if
		 *            indeterminate
		 */
		public void setSelected(Boolean selected) {
			if (selected != this.isSelected()) {
				this.selected = selected;
				if (selected == null) {
					this.checkBox.setSelected(false);
				} else {
					this.checkBox.setSelected(selected);
				}
			}
		}

		/**
		 * Sets the enabled state of this component.
		 * 
		 * @param enabled
		 *            <code>true</code> if enabled, <code>false</code> otherwise
		 */
		public void setEnabled(boolean enabled) {
			super.setEnabled(enabled);
			this.checkBox.setEnabled(enabled);
			this.hyperlink.setEnabled(enabled);
		}

		public boolean isFixed() {
			return this.fixed;
		}

		/**
		 * Sets the fixed state of this component's checkbox.
		 * 
		 * @param fixed
		 *            <code>true</code> if the selection state of the checkbox
		 *            cannot be changed, <code>false</code> otherwise
		 */
		public void setFixed(boolean fixed) {
			this.fixed = fixed;
			this.checkBox.setEnabled(isEnabled() && !fixed);
		}

		@Override
		public synchronized void addMouseListener(MouseListener l) {
			this.hyperlink.addMouseListener(l);
		}

		@Override
		public synchronized void removeMouseListener(MouseListener l) {
			this.hyperlink.removeMouseListener(l);
		}

		@Override
		public synchronized void addMouseMotionListener(MouseMotionListener l) {
			this.hyperlink.addMouseMotionListener(l);
		}

		@Override
		public synchronized void removeMouseMotionListener(MouseMotionListener l) {
			this.hyperlink.removeMouseMotionListener(l);
		}

	}

	/**
	 * Updates the highlighters of the column corresponding to the specified
	 * model index.
	 * 
	 * @param column
	 *            the column model index
	 * @param params
	 *            the parameters for configuring the highlighters
	 */
	public void updateHighlighters(int column, Object... params) {
		// do nothing by default, let overrides handle behavior
	}

}
