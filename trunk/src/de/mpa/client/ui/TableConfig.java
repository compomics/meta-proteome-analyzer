package de.mpa.client.ui;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * Helper class for JTable layout functionalities.
 * @author Robert Heyer
 *
 */
public class TableConfig {
	
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
	 */
	public static void clearTable(JTable table){
		((DefaultTableModel) table.getModel()).setRowCount(0);
	}
	
	/**
	 * 
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
	
}
