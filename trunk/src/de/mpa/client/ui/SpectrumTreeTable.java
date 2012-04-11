package de.mpa.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;
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
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.renderer.BooleanValue;
import org.jdesktop.swingx.renderer.CellContext;
import org.jdesktop.swingx.renderer.ComponentProvider;
import org.jdesktop.swingx.renderer.DefaultTreeRenderer;
import org.jdesktop.swingx.renderer.MappedValue;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.table.ColumnFactory;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.treetable.TreeTableModel;

public class SpectrumTreeTable extends JXTreeTable {
	
	JXTree tree;
	private SpectrumTreeCellEditor stce;
	private int hotspot;
	
	public SpectrumTreeTable(TreeTableModel treeModel) {

		super(treeModel);
		
		this.setColumnFactory(new ColumnFactory() {
			@Override
			public void configureColumnWidths(JXTable table, TableColumnExt columnExt) {
				int modelIndex = columnExt.getModelIndex();
				switch (modelIndex) {
				case 0:
					columnExt.setPreferredWidth(350);
					columnExt.setMaxWidth(3500);
					break;
				case 1:
					columnExt.setPreferredWidth(900);
					columnExt.setMaxWidth(9000);
					break;
				case 2:
				case 3:
				case 4:
					columnExt.setPreferredWidth(100);
					columnExt.setMaxWidth(1000);
					break;
				}
			}
		});
		
		TableColumnModel columnModel = getColumnModel();
		for (int i = 0; i < columnModel.getColumnCount(); i++) {
			TableColumn column = columnModel.getColumn(i);

			if (column instanceof TableColumnExt) {
				getColumnFactory().configureColumnWidths(
						this, (TableColumnExt) column);
			}
		}
		
		tree = (JXTree) this.getCellRenderer(-1, this.getHierarchicalColumn());
		
		final JCheckBox testBox = new JCheckBox() {
			@Override
			public void paint(Graphics g) {
				super.paint(g);
			}
		};
		testBox.setOpaque(false);
		
		hotspot = testBox.getPreferredSize().width;
		
		stce = new SpectrumTreeCellEditor(testBox, tree);

		BooleanValue bv = new BooleanValue() {
			public boolean getBoolean(Object value) {
				return ((SpectrumTreeNode) value).isSelected();
			}
		};
		StringValue sv = new StringValue() {
			public String getString(Object value) {
				return ((SpectrumTreeNode) value).getValueAt(getHierarchicalColumn()).toString();
			}
		};
		
		tree.setCellRenderer(new DefaultTreeRenderer(
				new ComponentProvider<IconCheckBox>(new MappedValue(sv, null, bv)) {

			@Override
			protected void configureState(CellContext context) { }	// do nothing

			@Override
			protected IconCheckBox createRendererComponent() {
				return new IconCheckBox();
			}

			@Override
			protected void format(CellContext context) {
				((IconCheckBox)rendererComponent).setText(getValueAsString(context));
				((IconCheckBox)rendererComponent).setIcon(context.getIcon());
				((IconCheckBox)rendererComponent).setSelected(getValueAsBoolean(context));
			}
			
			protected boolean getValueAsBoolean(CellContext context) {
				if (formatter instanceof BooleanValue) {
					return ((BooleanValue) formatter).getBoolean(context.getValue());
				}
				return Boolean.TRUE.equals(context.getValue());
			}
			
		}));

		// TODO: add key traversal
		this.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent me) {
				int col = columnAtPoint(me.getPoint());
				if (col == getHierarchicalColumn()) {
					int row = rowAtPoint(me.getPoint());
					if (row != editingRow) {
						Rectangle rowBounds = tree.getRowBounds(row);
						if ((rowBounds == null) || 
								(me.getX() < rowBounds.x) ||
								(me.getX() > (rowBounds.x + hotspot))) {
							removeEditor();
						} else {
							editCellAt(row, col, null);
						}
					} else {
						Rectangle rowBounds = tree.getRowBounds(row);
						if ((rowBounds == null) || 
								(me.getX() < rowBounds.x) ||
								(me.getX() > (rowBounds.x + hotspot))) {
							removeEditor();
						}
					}
				}
			}
		});
		testBox.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent me) {
				int row = editingRow;
				Rectangle rowBounds = tree.getRowBounds(row);
				if ((rowBounds == null) ||
						(me.getX() < rowBounds.x) ||
						(me.getX() > (rowBounds.x + hotspot))) {
					removeEditor();
				}
			}
		});

		this.setColumnMargin(1);
		this.setRowMargin(1);
		this.setShowGrid(true);
		this.getTableHeader().setReorderingAllowed(false);
		this.setColumnControlVisible(true);
		
		Color backCol = UIManager.getColor("Table.background");
		float hsbVals[] = Color.RGBtoHSB(
				backCol.getRed(), backCol.getGreen(),
				backCol.getBlue(), null);
		Color altCol = Color.getHSBColor(
				hsbVals[0], hsbVals[1], 0.975f * hsbVals[2]);
		this.addHighlighter(HighlighterFactory.createSimpleStriping(altCol));
		

	}
	
	@Override
	public void editingStopped(ChangeEvent e) {
        // Take in the new value
        if (stce != null) {
            Object value = stce.getCellEditorValue();
            setValueAt(value, editingRow, editingColumn);
//            removeEditor();
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
	
	/**
	 * 
	 * @author behne
	 *
	 */
	private class SpectrumTreeCellEditor extends DefaultCellEditor {

		private JTree tree;
		private DefaultCellEditor dce;
		
		public SpectrumTreeCellEditor(JCheckBox checkBox, JTree tree) {
			super(checkBox);
			this.dce = new DefaultCellEditor(checkBox); 
			this.tree = tree;
		}
		
		@Override
		public boolean isCellEditable(EventObject evt) {
			if (evt instanceof MouseEvent) {
				MouseEvent me = (MouseEvent) evt;
				int col = columnAtPoint(me.getPoint());
				if (col == getHierarchicalColumn()) {
					int row = rowAtPoint(me.getPoint());
					if (row != editingRow) {
						Rectangle rowBounds = tree.getRowBounds(row);
						if ((rowBounds == null) || 
								(me.getX() < rowBounds.x) ||
								(me.getX() > (rowBounds.x + hotspot))) {
							return false;
						} else {
							return true;
						}
					} else {
						Rectangle rowBounds = tree.getRowBounds(row);
						if ((rowBounds == null) || 
								(me.getX() < rowBounds.x) ||
								(me.getX() > (rowBounds.x + hotspot))) {
							return false;
						}
					}
				}
			} else if (evt instanceof KeyEvent) {
				KeyEvent ke = (KeyEvent) evt;
				if (ke.getKeyCode() == KeyEvent.VK_SPACE) {
					return true;
				}
			}
			return false;
		}
		
		@Override
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			
			SpectrumTreeNode node = (SpectrumTreeNode) tree.getPathForRow(row).getLastPathComponent();
			
			Component comp = dce.getTreeCellEditorComponent(tree, value, isSelected, true, false, row);
			
			((JCheckBox) comp).setSelected(node.isSelected());
			
			Rectangle cellRect = table.getCellRect(0, column, false);
            Rectangle nodeRect = tree.getRowBounds(row);
            int nodeStart = 1 + cellRect.x + nodeRect.x; // + iconWidth;
			
			((JComponent) comp).setBorder(BorderFactory.createEmptyBorder(1, nodeStart, 0, 0));
			
			return comp;
		}
	}
	
	/**
	 * Helper class for displaying a check box with an additional image label
	 * 
	 * @author behne
	 */
	private class IconCheckBox extends JPanel {
		
		JCheckBox checkbox;
		JLabel iconLbl;
		
		public IconCheckBox() {
			
			this.setLayout(new BorderLayout(2, 0));
			this.setOpaque(false);
			
			checkbox = new JCheckBox();
			checkbox.setOpaque(false);
			
			this.iconLbl = new JLabel();
			iconLbl.setOpaque(false);
			iconLbl.setVerticalTextPosition(SwingConstants.TOP);
			
			this.add(checkbox, BorderLayout.WEST);
			this.add(iconLbl, BorderLayout.CENTER);
		}
		
		public void setIcon(Icon icon) {
			iconLbl.setIcon(icon);
		}
		
		public void setText(String text) {
			iconLbl.setText(text);
		}
		
		public void setSelected(boolean selected) {
			checkbox.setSelected(selected);
		}
		
	}
	
}
