package de.mpa.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.CompoundHighlighter;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.renderer.CellContext;
import org.jdesktop.swingx.renderer.ComponentProvider;
import org.jdesktop.swingx.renderer.DefaultTreeRenderer;
import org.jdesktop.swingx.renderer.IconValue;
import org.jdesktop.swingx.rollover.RolloverProducer;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;

public class CheckBoxTreeTable extends JXTreeTable {

	private JXTreeTable treeTable = this;
	private JXTree tree;
	private CheckBoxTreeSelectionModel selectionModel;
	private CheckBoxTreeCellEditor cbtce;
	private int hotspot;
	private int center;
	private int indent = 0;
	private Highlighter enabledHighlighter;
	private Highlighter disabledHighlighter;
	private IconValue iconValue;
	
	public CheckBoxTreeTable(TreeTableNode rootNode) {
		this(new DefaultTreeTableModel(rootNode));
	}
	
	public CheckBoxTreeTable(TreeTableModel treeModel) {
		
		super(treeModel);
		
		tree = (JXTree) this.getCellRenderer(-1, this.getHierarchicalColumn());
		selectionModel = new CheckBoxTreeSelectionModel(treeModel);

//		final Color bgCol = UIManager.getColor("Table.background");
		// create checkbox for use as tree cell editor
//		final JCheckBox editorBox = new JCheckBox() {
//			public void paint(Graphics g) {
//				int row = rowAtPoint(this.getLocation());
//				int x = tree.getRowBounds(row).x;
//				g.setColor(bgCol);
//				g.fillRect(x+center, 5, 9, 9);
//				super.paint(g);
//				if (selectionModel.isPartiallySelected(getPathForRow(row))) {
//					setSelected(false);
//					g.setColor(Color.BLACK);
//					g.fillRect(x+center, 8, 8, 2);
//				}
//			}
//		};
//		editorBox.setOpaque(false);
//		editorBox.setInputMap(JComponent.WHEN_FOCUSED, null);	// make checkbox ignore keyboard input
		final IconCheckBox editorBox = new IconCheckBox();
		
//		Insets insets = (Insets) UIManager.get("CheckBox.totalInsets");
//		Insets insets = new Insets(4, 4, 4, 4);
		hotspot = UIManager.getIcon("CheckBox.icon").getIconWidth();
//		center = insets.left + indent + (hotspot-insets.right)/2 - 6;
		center = indent + (hotspot)/2 - 4;
		
		cbtce = new CheckBoxTreeCellEditor(editorBox, tree);
		
		tree.setCellRenderer(new DefaultTreeRenderer(new CheckBoxTreeCellRenderer()));
		
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
					// TODO: either we're inside the editor or outside the table, how to distinguish?
//					cbtce.cancelCellEditing();
				}
			}
		});

//		treeTable.addMouseMotionListener(new MouseMotionAdapter() {
//			@Override
//			public void mouseMoved(MouseEvent me) {
//				if (isEnabled()) {
//					int col = columnAtPoint(me.getPoint());
//					if (col == getHierarchicalColumn()) {
//						int row = rowAtPoint(me.getPoint());
//						if (!isEditing()) {
//							editCellAt(row, getHierarchicalColumn(), me);
//						} else {
//							if (!cbtce.isCellEditable(me)) {
//								cbtce.cancelCellEditing();
//							}
//						}
//					}
//				}
//			}
//		});
//		treeTable.addKeyListener(new KeyAdapter() {
//			public void keyPressed(KeyEvent ke) {
//				int row = treeTable.getSelectedRow();
//				switch (ke.getKeyCode()) {
//				case KeyEvent.VK_DOWN:
//					row = (row < treeTable.getRowCount()-1) ? row+1 : row;
//					editCellAt(row, editingColumn, ke);
//					break;
//				case KeyEvent.VK_UP:
//					row = (row > 0) ? row-1 : row;
//					editCellAt(row, editingColumn, ke);
//					break;
//				case KeyEvent.VK_LEFT:
//					if (treeTable.isExpanded(row)) {
//						treeTable.collapseRow(row);
//					} else {
//						TreePath parentPath = treeTable.getPathForRow(row).getParentPath();
//						if (parentPath.getPathCount() > 1) {
//							row = treeTable.getRowForPath(parentPath);
//							tree.setSelectionInterval(row, row);
//							editCellAt(row, editingColumn, ke);
//						}
//					}
//					break;
//				case KeyEvent.VK_RIGHT:
//					if (!treeTable.isExpanded(row)) {
//						tree.expandRow(row);
//					} else {
//						treeTable.dispatchEvent(new KeyEvent(ke.getComponent(),
//								ke.getID(), ke.getWhen(), ke.getModifiers(),
//								KeyEvent.VK_DOWN, ke.getKeyChar()));
//					}
//					break;
//				case KeyEvent.VK_SPACE:
//					if (row != editingRow) {
//						editCellAt(row, editingColumn, ke);
//					}
//					editorBox.getModel().setArmed(true);
//					break;
//				}
//			}
//			@Override
//			public void keyReleased(KeyEvent ke) {
//				if (ke.getKeyCode() == KeyEvent.VK_SPACE) {
//					editorBox.doClick();
//				}
//			}
//		});
//		editorBox.addMouseMotionListener(new MouseMotionAdapter() {
//			public void mouseMoved(MouseEvent me) {
//				// redirect to tree table event handling
//				treeTable.dispatchEvent(SwingUtilities.convertMouseEvent(editorBox, me, treeTable));
//			}
//		});
//		editorBox.addKeyListener(new KeyAdapter() {
//			public void keyPressed(KeyEvent ke) {
//				treeTable.dispatchEvent(ke);
//			}
//		});

		treeTable.setColumnMargin(1);
		treeTable.setRowMargin(1);
		treeTable.setShowGrid(true);
		treeTable.getTableHeader().setReorderingAllowed(false);
		treeTable.setColumnControlVisible(true);
		treeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		enabledHighlighter = TableConfig.getSimpleStriping();
		treeTable.addHighlighter(enabledHighlighter);
		
		disabledHighlighter = TableConfig.getDisabledStriping();
	}
	
//	private void installTreeRenderer() {
//		tree.setCellRenderer(new DefaultTreeRenderer(new ComponentProvider<IconCheckBox>() {
//
//			@Override
//			protected void configureState(CellContext context) { }	// do nothing
//
//			@Override
//			protected IconCheckBox createRendererComponent() {
//				return new IconCheckBox();
//			}
//
//			@Override
//			protected void format(CellContext context) {
//				IconCheckBox checkbox = (IconCheckBox) rendererComponent;
//				checkbox.setText(getValueAsString(context));
//				if (iconValue != null) {
//					checkbox.setIcon(iconValue.getIcon(context));
//				} else {
//					checkbox.setIcon(context.getIcon());
//				}
//				checkbox.setEnabled(treeTable.isEnabled());
//				CheckBoxTreeTableNode node = (CheckBoxTreeTableNode) context.getValue();
//				if (node != null) {
//					TreePath path = node.getPath();
//					Boolean selected = false;
//					if (selectionModel.isPathSelected(path, true)) {
//						selected = true;
//					} else if (selectionModel.isPartiallySelected(path)) {
//						selected = null;
//					}
//					checkbox.setSelected(selected);
//					checkbox.setFixed(node.isFixed());
//				}
//			}
//
//		}));
//	}
	
	@Override
	public void editingStopped(ChangeEvent e) {
        if (editingColumn == getHierarchicalColumn()) {
            // Take in the new value
            if (cbtce != null) {
            	TreePath path = getPathForRow(editingRow);
                Boolean selected = (Boolean) cbtce.getCellEditorValue();
                if (selected != null) {
                    if (selected) {
                    	selectionModel.addSelectionPath(path);
                    } else {
                    	selectionModel.removeSelectionPath(path);
                    }
                }
                // TODO: can repaint be avoided?
                treeTable.repaint();
            }
        } else {
    		super.editingStopped(e);
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
//		installTreeRenderer();
		tree.setCellRenderer(new DefaultTreeRenderer(new CheckBoxTreeCellRenderer()));
	}

	/**
	 * Returns the checkbox selection model.
	 * @return The selection model
	 */
	public CheckBoxTreeSelectionModel getCheckBoxTreeSelectionModel() {
		return selectionModel;
	}
	
	/**
	 * Sets the checkbox selection model.
	 * @param selectionModel The selection model to set
	 */
	public void setCheckBoxTreeSelectionModel(CheckBoxTreeSelectionModel selectionModel) {
		this.selectionModel = selectionModel;
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
				TreePath path = node.getPath();
				Boolean selected = false;
				if (selectionModel.isPathSelected(path, true)) {
					selected = true;
				} else if (selectionModel.isPartiallySelected(path)) {
					selected = null;
				}
				checkBox.setSelected(selected);
				checkBox.setFixed(node.isFixed());
				checkBox.setURI(node.getURI());
			}
			
			checkBox.setText(getValueAsString(context));
			if (iconValue != null) {
				checkBox.setIcon(iconValue.getIcon(context));
			} else {
				checkBox.setIcon(context.getIcon());
			}
			checkBox.setEnabled(treeTable.isEnabled());
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
//		private DefaultCellEditor dce;
		
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
			
			// TODO: find cleverer way to detect background color of current cell 
			// (does not work with additional highlighters)
			Color bgCol = ((ColorHighlighter) ((CompoundHighlighter) treeTable
					.getHighlighters()[0]).getHighlighters()[row % 2])
					.getBackground();
			checkBox.setBackground(bgCol);

			Rectangle cellRect = table.getCellRect(0, column, false);
			Rectangle nodeRect = tree.getRowBounds(row);
			int nodeStart = 1 + indent + cellRect.x + nodeRect.x; // + iconWidth;

			checkBox.setBorder(BorderFactory.createEmptyBorder(1, nodeStart, 0, 0));
			
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

		private JCheckBox checkBox;
		private JXHyperlink hyperlink;
		private Boolean selected;
		private boolean fixed;
		private Color bgCol;
		
		/**
		 * Constructs a checkbox panel containing a tri-state checkbox and a hyperlink label, 
		 * capable of bearing an icon.
		 */
		public IconCheckBox() {
			bgCol = UIManager.getColor("Table.background");

		    this.setLayout(new BorderLayout());
			this.setOpaque(false);

			// top checkbox with tri-state visuals
			checkBox = new JCheckBox() {
				@Override
				public void paint(Graphics g) {
					// paint background rectangle
					if (isEnabled()) {
						g.setColor(bgCol);
						g.fillRect(2 + indent, 4, 10, 10);
					}
					super.paint(g);
					// paint black bar on top of deselected box to visualize indeterminate state
					if (selected == null) {
						Color col = (isEnabled()) ? Color.BLACK : UIManager
								.getColor("controlShadow");
						g.setColor(col);
						g.fillRect(center, 7, 8, 2);
					}
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

			hyperlink.setUnclickedColor(Color.RED);
			hyperlink.setUnclickedColor(UIManager.getColor("Label.foreground"));

			// lay out components
			this.add(checkBox, BorderLayout.WEST);
			this.add(hyperlink, BorderLayout.CENTER);
		}
		
		public Icon getIcon() {
			return hyperlink.getIcon();
		}
		
		/**
		 * Defines the icon this component will display.
		 * If the value of icon is null, nothing is displayed. 
		 * @param icon
		 */
		public void setIcon(Icon icon) {
			hyperlink.setIcon(icon);
		}
		
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
		
		public Boolean isSelected() {
			return selected;
		}
		
		/**
		 * Sets the selection state of this component's checkbox.
		 * @param selected <code>true</code> if the button is selected, 
		 * <code>false</code> if not selected, <code>null</code> if indeterminate
		 */
		public void setSelected(Boolean selected) {
			this.selected = selected;
			if (selected == null) {
				checkBox.setSelected(false);
			} else {
				checkBox.setSelected(selected);
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
