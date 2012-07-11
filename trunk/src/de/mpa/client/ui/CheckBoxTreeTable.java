package de.mpa.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.EventObject;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.renderer.CellContext;
import org.jdesktop.swingx.renderer.ComponentProvider;
import org.jdesktop.swingx.renderer.DefaultTreeRenderer;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;

public class CheckBoxTreeTable extends JXTreeTable {

	private JXTreeTable treeTable = this;
	private JXTree tree;
	private CheckBoxTreeSelectionModel selectionModel;
	private CheckBoxTreeCellEditor stce;
	private int hotspot;
	private int center;
	private Highlighter enabledHighlighter;
	private Highlighter disabledHighlighter;
	
	public CheckBoxTreeTable(TreeTableNode rootNode) {
		this(new DefaultTreeTableModel(rootNode));
	}
	
	public CheckBoxTreeTable(TreeTableModel treeModel) {
		
		super(treeModel);
		
		tree = (JXTree) this.getCellRenderer(-1, this.getHierarchicalColumn());
		selectionModel = new CheckBoxTreeSelectionModel(treeModel);

		final Color bgCol = UIManager.getColor("Table.background");
		// create checkbox for use as tree cell editor
		final JCheckBox editorBox = new JCheckBox() {
			public void paint(Graphics g) {
				int row = rowAtPoint(this.getLocation());
				int x = tree.getRowBounds(row).x;
				g.setColor(bgCol);
				g.fillRect(x+center, 5, 9, 9);
				super.paint(g);
				if (selectionModel.isPartiallySelected(getPathForRow(row))) {
					setSelected(false);
					g.setColor(Color.BLACK);
					g.fillRect(x+center, 8, 8, 2);
				}
			}
		};
		editorBox.setOpaque(false);
		editorBox.setInputMap(JComponent.WHEN_FOCUSED, null);	// make checkbox ignore keyboard input
		
//		Insets insets = (Insets) UIManager.get("CheckBox.totalInsets");
		Insets insets = new Insets(4, 4, 4, 4);
		hotspot = editorBox.getPreferredSize().width;
		center = insets.left + (hotspot-insets.right)/2 - 6;
		
		stce = new CheckBoxTreeCellEditor(editorBox, tree);
		
		tree.setCellRenderer(new DefaultTreeRenderer(
				new ComponentProvider<IconCheckBox>() {

			@Override
			protected void configureState(CellContext context) { }	// do nothing

			@Override
			protected IconCheckBox createRendererComponent() {
				return new IconCheckBox();
			}

			@Override
			protected void format(CellContext context) {
				IconCheckBox checkbox = (IconCheckBox) rendererComponent;
				checkbox.setText(getValueAsString(context));
				checkbox.setIcon(context.getIcon());
				checkbox.setEnabled(treeTable.isEnabled());
				CheckBoxTreeTableNode node = (CheckBoxTreeTableNode) context.getValue();
				if (node != null) {
					TreePath path = node.getPath();
					Boolean selected = false;
					if (selectionModel.isPathSelected(path, true)) {
						selected = true;
					} else if (selectionModel.isPartiallySelected(path)) {
						selected = null;
					}
					checkbox.setSelected(selected);
					checkbox.setFixed(node.isFixed());
				}
			}
			
		}));

		treeTable.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent me) {
				if (isEnabled()) {
					int col = columnAtPoint(me.getPoint());
					if (col == getHierarchicalColumn()) {
						int row = rowAtPoint(me.getPoint());
						if (!isEditing()) {
							editCellAt(row, getHierarchicalColumn(), me);
						} else {
							if (!stce.isCellEditable(me)) {
								stce.cancelCellEditing();
							}
						}
					}
				}
			}
		});
		treeTable.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent ke) {
				int row = treeTable.getSelectedRow();
				switch (ke.getKeyCode()) {
				case KeyEvent.VK_DOWN:
					row = (row < treeTable.getRowCount()-1) ? row+1 : row;
					editCellAt(row, editingColumn, ke);
					break;
				case KeyEvent.VK_UP:
					row = (row > 0) ? row-1 : row;
					editCellAt(row, editingColumn, ke);
					break;
				case KeyEvent.VK_LEFT:
					if (treeTable.isExpanded(row)) {
						treeTable.collapseRow(row);
					} else {
						TreePath parentPath = treeTable.getPathForRow(row).getParentPath();
						if (parentPath.getPathCount() > 1) {
							row = treeTable.getRowForPath(parentPath);
							tree.setSelectionInterval(row, row);
							editCellAt(row, editingColumn, ke);
						}
					}
					break;
				case KeyEvent.VK_RIGHT:
					if (!treeTable.isExpanded(row)) {
						tree.expandRow(row);
					} else {
						treeTable.dispatchEvent(new KeyEvent(ke.getComponent(),
								ke.getID(), ke.getWhen(), ke.getModifiers(),
								KeyEvent.VK_DOWN, ke.getKeyChar()));
					}
					break;
				case KeyEvent.VK_SPACE:
					if (row != editingRow) {
						editCellAt(row, editingColumn, ke);
					}
					editorBox.getModel().setArmed(true);
					break;
				}
			}
			@Override
			public void keyReleased(KeyEvent ke) {
				if (ke.getKeyCode() == KeyEvent.VK_SPACE) {
					editorBox.doClick();
				}
			}
		});
		editorBox.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent me) {
				// redirect to tree table event handling
				treeTable.dispatchEvent(SwingUtilities.convertMouseEvent(editorBox, me, treeTable));
			}
		});
		editorBox.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent ke) {
				treeTable.dispatchEvent(ke);
			}
		});

		treeTable.setColumnMargin(1);
		treeTable.setRowMargin(1);
		treeTable.setShowGrid(true);
		treeTable.getTableHeader().setReorderingAllowed(false);
		treeTable.setColumnControlVisible(true);
		treeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
//		Color backCol = UIManager.getColor("Table.background");
//		float hsbVals[] = Color.RGBtoHSB(
//				backCol.getRed(), backCol.getGreen(),
//				backCol.getBlue(), null);
//		Color altCol = Color.getHSBColor(
//				hsbVals[0], hsbVals[1], 0.95f * hsbVals[2]);
//		enabledHighlighter = HighlighterFactory.createSimpleStriping(altCol);
		enabledHighlighter = TableConfig.getSimpleStriping();
		treeTable.addHighlighter(enabledHighlighter);
		
		Color backCol = UIManager.getColor("Panel.background");
		float[] hsbVals = Color.RGBtoHSB(
				backCol.getRed(), backCol.getGreen(),
				backCol.getBlue(), null);
		Color altCol = Color.getHSBColor(
				hsbVals[0], hsbVals[1], 0.95f * hsbVals[2]);
		disabledHighlighter = HighlighterFactory.createAlternateStriping(backCol, altCol);
	}
	
	@Override
	public void editingStopped(ChangeEvent e) {
        if (editingColumn == getHierarchicalColumn()) {
            // Take in the new value
            if (stce != null) {
            	TreePath path = getPathForRow(editingRow);
                Boolean selected = (Boolean) stce.getCellEditorValue();
                if (selected) {
                	selectionModel.addSelectionPath(path);
                } else {
                	selectionModel.removeSelectionPath(path);
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
			return stce;
		} else {
			return super.getCellEditor(row, column);
		}
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		if (!enabled) {
			treeTable.setHighlighters(disabledHighlighter);
			treeTable.clearSelection();
			removeEditor();
		} else {
			treeTable.setHighlighters(enabledHighlighter);
		}
		super.setEnabled(enabled);
	}

	/**
	 * @return the selectionModel
	 */
	public CheckBoxTreeSelectionModel getCheckBoxTreeSelectionModel() {
		return selectionModel;
	}

	/**
	 * 
	 * 
	 * @author behne
	 */
	private class CheckBoxTreeCellEditor extends DefaultCellEditor {

		private JTree tree;
		private DefaultCellEditor dce;
		
		public CheckBoxTreeCellEditor(JCheckBox checkBox, JTree tree) {
			super(checkBox);
			this.dce = new DefaultCellEditor(checkBox); 
			this.tree = tree;
		}
		
		@Override
		public boolean isCellEditable(EventObject evt) {
			if (evt instanceof MouseEvent) {
				MouseEvent me = (MouseEvent) evt;
				int row = rowAtPoint(me.getPoint());
				if (row != editingRow) {
					cancelCellEditing();
				}
				Rectangle rowBounds = tree.getRowBounds(row);
				if (rowBounds != null) {
					if ((me.getX() > rowBounds.x) && (me.getX() < (rowBounds.x + hotspot))) {
						return true;
					}
				}
			} else if (evt instanceof KeyEvent) {
//				KeyEvent ke = (KeyEvent) evt;
//				if (ke.getKeyCode() == KeyEvent.VK_SPACE) {
					return true;
//				}
			}
			return false;
		}
		
		@Override
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			
			CheckBoxTreeTableNode node = (CheckBoxTreeTableNode) tree.getPathForRow(row).getLastPathComponent();
			
			Component comp = dce.getTreeCellEditorComponent(tree, value, isSelected, true, false, row);

			TreePath path = getPathForRow(row);
			((JCheckBox) comp).setSelected(selectionModel.isPathSelected(path, true));
			((JCheckBox) comp).setEnabled(!node.isFixed());

			Rectangle cellRect = table.getCellRect(0, column, false);
			Rectangle nodeRect = tree.getRowBounds(row);
			int nodeStart = 1 + cellRect.x + nodeRect.x; // + iconWidth;

			((JComponent) comp).setBorder(BorderFactory.createEmptyBorder(1, nodeStart, 0, 0));
			
			return comp;
		}
	}
	
	/**
	 * Helper class for displaying a three-state check box with an additional image label
	 * 
	 * @author behne
	 */
	private class IconCheckBox extends JPanel {

		private JCheckBox checkbox;
		private JLabel iconLbl;
		private Boolean selected;
		private Color bgCol;
		
		/**
		 * Default class constructor.
		 * Initializes two checkboxes on top of each other to enable three-state visualization 
		 * and a label, capable of bearing an icon next to the checkboxes.
		 */
		public IconCheckBox() {
			bgCol = UIManager.getColor("Table.background");

		    this.setLayout(new BorderLayout());
			this.setOpaque(false);
			
			// top checkbox
			checkbox = new JCheckBox();
			checkbox.setOpaque(false);
			checkbox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));
			
			// icon-bearing label
			this.iconLbl = new JLabel();
			iconLbl.setOpaque(false);
			iconLbl.setVerticalTextPosition(SwingConstants.TOP);

			// lay out components
			this.add(checkbox, BorderLayout.WEST);
			this.add(iconLbl, BorderLayout.CENTER);
		}
		
		@Override
		public void paint(Graphics g) {
			if (isEnabled()) {
				g.setColor(bgCol);
				g.fillRect(2, 4, 10, 10);
			}
			super.paint(g);
			if (selected == null) {
				Color col = (isEnabled()) ? Color.BLACK : UIManager.getColor("controlShadow");
				g.setColor(col);
				g.fillRect(center, 8, 8, 2);
			}
		}
		
		/**
		 * Defines the icon this component will display.
		 * If the value of icon is null, nothing is displayed. 
		 * @param icon
		 */
		public void setIcon(Icon icon) {
			iconLbl.setIcon(icon);
		}
		
		/**
		 * Defines the single line of text this component will display.
		 * If the value of text is null or empty string, nothing is displayed.
		 * @param text
		 */
		public void setText(String text) {
			iconLbl.setText(text);
		}
		
		/**
		 * Sets the state of the button.
		 * @param selected true if the button is selected, false if not selected, null if indeterminate
		 */
		public void setSelected(Boolean selected) {
			this.selected = selected;
			if (selected == null) {
				checkbox.setSelected(false);
			} else {
				checkbox.setSelected(selected);
			}
		}
		
		public void setEnabled(boolean enabled) {
			super.setEnabled(enabled);
			checkbox.setEnabled(enabled);
			iconLbl.setEnabled(enabled);
		}
		
		public void setFixed(boolean fixed) {
			checkbox.setEnabled(this.isEnabled() && !fixed);
		}
		
	}
	
}
