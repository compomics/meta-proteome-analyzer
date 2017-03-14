package de.mpa.client.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.TreePath;

import org.apache.commons.lang3.math.NumberUtils;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.AbstractHighlighter;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.CompoundHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.table.ColumnControlButton;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;
import org.jdesktop.swingx.treetable.TreeTableNode;

import de.mpa.client.Constants;
import de.mpa.util.ColorUtils;

/**
 * Helper class for JTable layout functionalities.
 * @author Alex Behne
 *
 */
public class TableConfig {
	
	/**
	 * A simple white-gray striping highlighter for tables
	 */
	private static Highlighter simpleStriping;
	
	/**
	 * A simple white-gray striping highlighter for disabled tables
	 */
	private static Highlighter disabledStriping;

	/**
	 * Sets the preferred width of the visible column specified by vColIndex. 
	 * The column will be just wide enough to show the column head and the widest cell in the column.
	 * Margin pixels are added to the left and right resulting in an additional width of 2*margin pixels).
	 * 
	 * @param table The JTable object.
	 * @param vColIndex Visible column index. 
	 * @param margin The number of margin pixels.
	 */
	public static void packColumn(JTable table, int vColIndex, int margin) {
		DefaultTableColumnModel colModel = (DefaultTableColumnModel)table.getColumnModel();
		TableColumn col = colModel.getColumn(vColIndex);
		int width = 0;

		// Get width of column header
		TableCellRenderer renderer = col.getHeaderRenderer();
		if (renderer == null) {
			renderer = table.getTableHeader().getDefaultRenderer();
		}
		Component comp = renderer.getTableCellRendererComponent(table, col.getHeaderValue(), false, false, 0, 0);
		width = comp.getPreferredSize().width;

		// Get maximum width of column data
		for (int r=0; r<table.getRowCount(); r++) {
			renderer = table.getCellRenderer(r, vColIndex);
			comp = renderer.getTableCellRendererComponent(table, table.getValueAt(r, vColIndex), false, false, r, vColIndex);
			width = Math.max(width, comp.getPreferredSize().width);
		}

		// Add margin
		width += 2*margin;

		// Set the width
		col.setMinWidth(width);
		col.setPreferredWidth(width);
	}
	
	/**
	 * This method clears the project table.
	 * @param table The JTable component.
	 */
	public static void clearTable(JTable table) {
		if (table instanceof JXTreeTable) {
			table.clearSelection();
			// get model and root instances
			DefaultTreeTableModel model =
				(DefaultTreeTableModel) ((JXTreeTable) table).getTreeTableModel();
			MutableTreeTableNode root = (MutableTreeTableNode) model.getRoot();
			
			// remove and destroy root children
			if (root instanceof CheckBoxTreeTableNode) {
				((CheckBoxTreeTableNode) root).removeAllChildren();
			} else {
				//				for (int i = root.getChildCount() - 1; i >= 0; i--) {
				//					root.remove(i);
				//				}
				Enumeration<? extends MutableTreeTableNode> children = root.children();
				while (children.hasMoreElements()) {
					MutableTreeTableNode child = (MutableTreeTableNode) children.nextElement();
					model.removeNodeFromParent(child);
					child = null;
				}
			}
			// refresh model by re-setting root
			model.setRoot(root);
		} else {
			// for simple JTables setRowCount(0) is sufficient
			((DefaultTableModel) table.getModel()).setRowCount(0);
		}
	}
	
	/**
	 * Dumps the contents of the specified tree table to the specified file in a tab-separated CSV format.
	 * @param treeTbl the tree table to dump
	 * @param file the file to dump to
	 * @throws IOException if an I/O error occurs
	 */
	public static void dumpTableToCSV(CheckBoxTreeTable treeTbl, File file) throws IOException {
		// Create a writer to save the table
		try (FileWriter fw = new FileWriter(file)) {
			// Table is a checkbox treetable, so get at first the rootmpde
			TreeTableNode root = (TreeTableNode) treeTbl.getTreeTableModel().getRoot();
			// Get maximum tree length 
			int maxDepth = getMaximumDepth(0, root);
			// Get separators for the csv file
			String rowSep = System.getProperty("line.separator");
			String colSep = Constants.TSV_FILE_SEPARATOR;
			
			// store expansion state and expand whole tree, restore state after dumping
			Enumeration<?> expanded = treeTbl.getExpandedDescendants(new TreePath(root));
			treeTbl.expandAll();
			
			int rowCount = treeTbl.getRowCount();
			int colCount = treeTbl.getColumnCount();
			
			// write header line and go though all levels
			for (int i = 1; i < maxDepth; i++) {
				fw.write("Level " + i);
				fw.write(colSep);
			}
			for (int i = 0; i < colCount; i++) {
				fw.write(treeTbl.getColumn(i).getIdentifier().toString());
				fw.write(colSep);
			}
			fw.write(rowSep);
			
			CheckBoxTreeSelectionModel cbtsm = treeTbl.getCheckBoxTreeSelectionModel();
			// write table contents
			for (int row = 0; row < rowCount; row++) {
				TreePath path = treeTbl.getPathForRow(row);
				TreeTableNode node = (TreeTableNode) path.getLastPathComponent();
				if (node.isLeaf()) {
					if (cbtsm.isPathSelected(path, true)) {
						int len = path.getPathCount() - 1;
						// write hierarchical elements
						for (int i = 1; i < len; i++) {
							fw.write(path.getPathComponent(i).toString());
							fw.write(colSep);
						}
						// if path is shorter than max depth pad with empty columns
						// TODO: maybe pad using 'Uncharacterized' or something to that effect
						for (int i = len; i < maxDepth; i++) {
							fw.write(colSep);
						}
						// write other column elements
						for (int col = 0; col < colCount; col++) {
							Object value = treeTbl.getValueAt(row, col);
							if (value != null) {
								fw.write(value.toString());
							}
							fw.write(colSep);
						}
						fw.write(rowSep);
					}
				}
			}
			
			// restore original expansion state
			treeTbl.collapseAll();
			while (expanded.hasMoreElements()) {
				treeTbl.expandPath((TreePath) expanded.nextElement());
			}
		}
	}
	
	/**
	 * Recursively determines the maximum nesting depth of nodes below the
	 * specified parent node from a given starting depth.
	 * @param depth the starting depth
	 * @param parent the parent node
	 * @return the maximum depth
	 */
	private static int getMaximumDepth(int depth, TreeTableNode parent) {
		int maxDepth = depth;
		Enumeration<? extends TreeTableNode> children = parent.children();
		while (children.hasMoreElements()) {
			TreeTableNode child = children.nextElement();
			maxDepth = Math.max(depth, getMaximumDepth(depth + 1, child));
		}
		return maxDepth;
	}
	
	/**
	 * Generates a simple white-gray highlighter for tables.
	 * @return a striping highlighter
	 */
	public static Highlighter getSimpleStriping() {
		if (simpleStriping == null) {
			Color evenCol = UIManager.getColor("Table.background");
			Color oddCol = ColorUtils.getRescaledColor(evenCol, 0.95f);
			Color selOddCol = UIManager.getColor("Table.selectionBackground");
			Color selEvenCol = ColorUtils.getRescaledColor(selOddCol, 1.05f);
			
			ColorHighlighter base = new ColorHighlighter(HighlightPredicate.EVEN, evenCol, null, selEvenCol, null);
	        ColorHighlighter alternate = new ColorHighlighter(HighlightPredicate.ODD, oddCol, null, selOddCol, null);
	        simpleStriping = new CompoundHighlighter(base, alternate);
		}
		return simpleStriping;
	}
	
	/**
	 * Generates a simply gray-dark gray highlighter for disabled tables.
	 * @return a disabled striping highlighter
	 */
	public static Highlighter getDisabledStriping() {
		if (disabledStriping == null) {
			Color backCol = UIManager.getColor("Panel.background");
			Color altCol = ColorUtils.getRescaledColor(backCol, 0.95f);
			disabledStriping = HighlighterFactory.createAlternateStriping(backCol, altCol);
		}
		return disabledStriping;
	}
	
	/**
	 * Sets dynamically weighted column widths for a specified table.
	 * @param table The JTable component.
	 * @param weights The weight array.
	 */
	public static void setColumnWidths(JTable table, double[] weights) {
		setColumnWidths(table, weights, 800);
	}
	
	/**
	 * Sets dynamically weighted column widths for a specified table.
	 * @param table The JTable component.
	 * @param weights The weight array.
	 * @param tableWidth The display width of the table.
	 */
	public static void setColumnWidths(JTable table, double[] weights, int tableWidth) {
		// normalize weights if needed
		double sum = 0.0;
		for (double weight : weights) {
			sum += weight;
		}
		for (int i = 0; i < weights.length; i++) {
			weights[i] *= tableWidth/sum;
		}
		// iterate columns
		if (table instanceof JXTable) {
			JXTable xTable = (JXTable) table;
			for (int i = 0; i < xTable.getColumnCount(); i++) {
				TableColumnExt tc = xTable.getColumnExt(xTable.convertColumnIndexToView(i));
				tc.setPreferredWidth((int) (weights[i]));
				tc.setMaxWidth(tc.getPreferredWidth()*20000);
			}
		} else {
			TableColumnModel tcm = table.getColumnModel();
			for (int i = 0; i < tcm.getColumnCount(); i++) {
				TableColumn tc = tcm.getColumn(i);
				tc.setPreferredWidth((int) (weights[i]));
				tc.setMaxWidth(tc.getPreferredWidth()*20000);
			}
		}
	}
	
	/**
	 * Automatically determines and sets the minimum widths of a table's columns.
	 * @param table The table to be adjusted.
	 * @param padding The amount of pixel padding to either side.
	 */
	public static void setColumnMinWidths(JTable table, int padding) {
		setColumnMinWidths(table, padding, padding, new JLabel().getFont());
	}
	
	/**
	 * Automatically determines and sets the minimum widths of a table's columns.
	 * @param table The table to be adjusted.
	 * @param leftPadding The amount of left-hand side pixel padding.
	 * @param rightPadding The amount of right-hand side pixel padding.
	 * @param font The used font for FontMetrics object.
	 */
	public static void setColumnMinWidths(JTable table, int leftPadding, int rightPadding, Font font) {
		TableColumnModel tcm = table.getColumnModel();
		FontMetrics fm = table.getFontMetrics(font);
		for (int col = 0; col < tcm.getColumnCount(); col++) {
			TableColumn tc = tcm.getColumn(col);
			tc.setMinWidth(leftPadding + fm.stringWidth(tc.getHeaderValue().toString()) + rightPadding);
		}
	}
	
	/**
	 * Convenience method to make table column control border appear the same as
	 * table header and to hide some popup elements.
	 * @param table the table to configure
	 */
	public static void configureColumnControl(JXTable table) {
		table.setColumnControlVisible(true);
		table.getColumnControl().setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(1, 1, 0, 0, Color.WHITE),
				BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY)));
		table.getColumnControl().setOpaque(false);
		((ColumnControlButton) table.getColumnControl()).setAdditionalActionsVisible(false);
	}

	/**
	 * Custom table cell highlighter class providing horizontal alignment 
	 * and decimal formatting capabilities.
	 * 
	 * @author A. Behne
	 */
	public static class FormatHighlighter extends AbstractHighlighter {

		private int alignment;
		private DecimalFormat formatter;
		
		/**
		 * FormatHighlighter constructor
		 * @param alignment the alignment/orientation value defined by SwingConstants class, e.g. SwingConstants.LEFT == 2.
		 */
		public FormatHighlighter(int alignment) {
			this(alignment, "0");
		}
		
		public FormatHighlighter(int alignment, String decimalFormat) {
			this(alignment, new DecimalFormat(decimalFormat));
		}
		
		public FormatHighlighter(int alignment, DecimalFormat formatter) {
			super();
			this.alignment = alignment;
			this.formatter = formatter;
		}
		
		@Override
		protected Component doHighlight(Component component,
				ComponentAdapter adapter) {
			if (component instanceof JLabel) {
				JLabel label = (JLabel) component;
				label.setHorizontalAlignment(this.alignment);
				String text = label.getText();
				if (NumberUtils.isNumber(text)) {
					label.setText(this.formatter.format(Double.parseDouble(text)));
				}
			}
			return component;
		}
		
	}
	
	/**
	 * Custom table cell renderer class providing horizontal alignment 
	 * and decimal formatting capabilities.
	 * 
	 * @author A. Behne
	 */
	@SuppressWarnings("serial")
	public static class FormattedTableCellRenderer extends DefaultTableCellRenderer {
		
		private final DecimalFormat formatter;
		
		/**
		 * Class constructor defining a horizontal alignment.
		 * @param <code>alignment</code> - One of the following constants defined in SwingConstants: 
		 * LEFT, CENTER (the default for image-only labels), RIGHT, 
		 * LEADING (the default for text-only labels) or TRAILING.
		 */
		@Deprecated
		public FormattedTableCellRenderer(int alignment) {
			this(alignment, "0");
		}
		
		/**
		 * Class constructor defining a horizontal alignment and a decimal format pattern.
		 * @param <code>alignment</code> - One of the following constants defined in SwingConstants: 
		 * LEFT, CENTER (the default for image-only labels), RIGHT, 
		 * LEADING (the default for text-only labels) or TRAILING.
		 * @param <code>decimalFormat</code> - A non-localized pattern string.
		 */
		@Deprecated
		public FormattedTableCellRenderer(int alignment, String decimalFormat) {
			formatter = new DecimalFormat(decimalFormat);
			setHorizontalAlignment(alignment);
		}

		public Component getTableCellRendererComponent(
				JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

			if (value instanceof Number) {
				value = formatter.format((Number) value);
			}
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column );
		}
	}
	
	/**
	 * A <code>HighlightPredicate</code> based on row index.<br>
	 * Based on {@link HighlightPredicate#ColumnHighlightPredicate}.
	 * 
	 * @author A. Behne
	 */
	public static class RowHighlightPredicate implements HighlightPredicate {
		
		/**
		 * The indices of the rows that are to be highlighted
		 */
        List<Integer> rowList;
        
        /**
         * Instantiates a predicate which returns true for the
         * given columns in model coordinates.
         * 
         * @param columns the columns to highlight in model coordinates.
         */
        public RowHighlightPredicate(int... rows) {
            rowList = new ArrayList<Integer>();
            for (int i = 0; i < rows.length; i++) {
                rowList.add(rows[i]);
            }
        }
        
        /**
         * {@inheritDoc}
         * @return <code>true</code> if the adapter's row
         * is contained in this predicate's list
         */
        @Override
        public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
            int modelIndex = adapter.convertRowIndexToModel(adapter.row);
            return rowList.contains(modelIndex);
        }

        /**
         * Returns the row indices in model coordinates to highlight.
         * @return the row indices
         */
        public Integer[] getColumns() {
            if (rowList.isEmpty()) return EMPTY_INTEGER_ARRAY;
            return rowList.toArray(new Integer[rowList.size()]);
        }
        
    }
	
}
