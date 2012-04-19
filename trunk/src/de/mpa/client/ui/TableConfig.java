package de.mpa.client.ui;

import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;

import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;

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
		((DefaultTableModel) table.getModel()).setRowCount(0);
	}
	
	/**
	 * Method to generate a simple gray-white highlighter for tables.
	 * @return
	 */
	public static Highlighter getSimpleStriping() {
		if (simpleStriping == null) {
			Color backCol = UIManager.getColor("Table.background");
			float hsbVals[] = Color.RGBtoHSB(
					backCol.getRed(), backCol.getGreen(),
					backCol.getBlue(), null);
			Color altCol = Color.getHSBColor(
					hsbVals[0], hsbVals[1], 0.95f * hsbVals[2]);
			simpleStriping = HighlighterFactory.createSimpleStriping(altCol);
		}
		return simpleStriping;
	}
	
	/**
	 * Sets dynamically weighted column widths.
	 * @param table The JTable component.
	 * @param weights The weight array.
	 */
	public static void setColumnWidths(JTable table, double[] weights) {
		// normalize weights if needed
		double sum = 0.0;
		for (double weight : weights) {
			sum += weight;
		}
		double tableWidth = table.getPreferredSize().width;
		for (int i = 0; i < weights.length; i++) {
			weights[i] *= tableWidth/sum;
		}
		// iterate columns
		TableColumnModel tcm = table.getColumnModel();
		for (int i = 0; i < weights.length; i++) {
			TableColumn tc = tcm.getColumn(i);
			tc.setPreferredWidth((int) (weights[i]));
			tc.setMaxWidth(tc.getPreferredWidth()*10);
		}
	}
	
	/**
	 * Custom table cell renderer class providing horizontal alignment 
	 * and decimal formatting capabilities.
	 * 
	 * @author A. Behne
	 */
	public static class CustomTableCellRenderer extends DefaultTableCellRenderer {
		
		private final DecimalFormat formatter;
		
		/**
		 * Class constructor defining a horizontal alignment.
		 * @param <code>alignment</code> - One of the following constants defined in SwingConstants: 
		 * LEFT, CENTER (the default for image-only labels), RIGHT, 
		 * LEADING (the default for text-only labels) or TRAILING.
		 */
		public CustomTableCellRenderer(int alignment) {
			this(alignment, "0");
		}
		
		/**
		 * Class constructor defining a horizontal alignment and a decimal format pattern.
		 * @param <code>alignment</code> - One of the following constants defined in SwingConstants: 
		 * LEFT, CENTER (the default for image-only labels), RIGHT, 
		 * LEADING (the default for text-only labels) or TRAILING.
		 * @param <code>decimalFormat</code> - A non-localized pattern string.
		 */
		public CustomTableCellRenderer(int alignment, String decimalFormat) {
			formatter = new DecimalFormat(decimalFormat);
			setHorizontalAlignment(alignment);
		}

		public Component getTableCellRendererComponent(
				JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			return super.getTableCellRendererComponent(
					table, formatter.format((Number) value), isSelected, hasFocus, row, column );
		}
		
	}
	
}
