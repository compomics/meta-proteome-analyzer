package de.mpa.client.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.decorator.PainterHighlighter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.renderer.JRendererLabel;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;

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
		if (table instanceof JXTreeTable) {
			MutableTreeTableNode root =
				(MutableTreeTableNode) ((JXTreeTable) table).getTreeTableModel().getRoot();
			for (int i = 0; i < root.getChildCount(); i++) {
				root.remove(i);
			}
		} else {
			((DefaultTableModel) table.getModel()).setRowCount(0);
		}
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
	 * Convenience method to create a bar chart-like highlighter for a specific table column.
	 * 
	 * @param column The table column.
	 * @param maxValue 
	 * @param baseline
	 * @param startColor The gradient's start color.
	 * @param endColor The gradient's end color.
	 * @return A painter highlighter drawing a gradient next to the cell contents.
	 * @deprecated Replaced by {@link BarChartHighlighter BarChartHighlighter} class.
	 */
	@Deprecated
	public static Highlighter createGradientHighlighter(int column, double maxValue, int baseline, Color startColor, Color endColor) {
		return createGradientHighlighter(column, maxValue, baseline, SwingConstants.HORIZONTAL, startColor, endColor, new DecimalFormat());
	}
	
	/**
	 * Convenience method to create a bar chart-like highlighter for a specific table column.
	 * 
	 * @param column
	 * @param maxValue
	 * @param baseline
	 * @param orientation
	 * @param startColor
	 * @param endColor
	 * @return A painter highlighter drawing a gradient next to the cell contents.
	 * @deprecated Replaced by <code>BarChartHighlighter</code> class.
	 */
	@Deprecated
	public static Highlighter createGradientHighlighter(int column, double maxValue, int baseline, int orientation, Color startColor, Color endColor) {
		return createGradientHighlighter(column, maxValue, baseline, orientation, startColor, endColor, new DecimalFormat());
	}
	
	/**
	 * Convenience method to create a bar chart-like highlighter for a specific table column.
	 * 
	 * @param column The table column.
	 * @param maxValue
	 * @param baseline
	 * @param orientation
	 * @param startColor The gradient's start color.
	 * @param endColor The gradient's end color.
	 * @param formatter The number formatter for the column cells.
	 * @return A painter highlighter drawing a gradient next to the cell contents.
	 * @deprecated Replaced by <code>BarChartHighlighter</code> class.
	 */
	@Deprecated
	public static Highlighter createGradientHighlighter(final int column, final double maxValue, final int baseline, int orientation, Color startColor, Color endColor, final NumberFormat formatter) {
		HighlightPredicate predicate = new HighlightPredicate() {
			public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
				return (adapter.column == adapter.convertColumnIndexToView(column));
			}
		};
		// TODO: parametrize some settings, e.g. horizontal alignment, or create proper sub-class for better control
		final int x = (orientation == SwingConstants.HORIZONTAL) ? 1 : 0;
		final int y = (orientation == SwingConstants.VERTICAL) ? 1 : 0;
		PainterHighlighter ph = new PainterHighlighter(predicate, new MattePainter(new GradientPaint(
				0, y, startColor, x, 0, endColor), true) {
			protected void doPaint(Graphics2D g, Object component, int width, int height) {
				JRendererLabel label = (JRendererLabel) component;
				label.setHorizontalAlignment(SwingConstants.RIGHT);
				
				try {
					NumberFormat format = NumberFormat.getInstance(Locale.getDefault());
				    Number number = format.parse(label.getText());
				    double value = number.doubleValue();

					String text = formatter.format(value);
					label.setText(text);
					int xOffset = (baseline > 0) ? baseline + 4 : 2;
					label.setBounds(0, 0, baseline, height);
					width -= xOffset + 2;
					int clipWidth = ((width < 0) ? 0 : (x == 0) ? width : (int) (value/maxValue * width));
					int clipHeight = (y == 0) ? (height - 4) : (int) (value/maxValue * (height - 4));
					int yOffset = height - clipHeight - 2;
					g.translate(xOffset, 0);
					g.clipRect(0, yOffset, clipWidth, clipHeight);
					super.doPaint(g, component, width, height);
				} catch (Exception e) {
					JXErrorPane.showDialog(e);
				}
			}
		});
		return ph;
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
		TableColumnModel tcm = table.getColumnModel();
		for (int i = 0; i < tcm.getColumnCount(); i++) {
			TableColumn tc = tcm.getColumn(i);
			tc.setPreferredWidth((int) (weights[i]));
			tc.setMaxWidth(tc.getPreferredWidth()*20000);
		}
	}
	
	/**
	 * Automatically determines and sets the minimum widths of a table's columns.
	 * @param table The table to be adjusted.
	 * @param padding The amount of pixel padding to either side.
	 */
	public static void setColumnMinWidths(JTable table, int padding) {
		setColumnMinWidths(table, padding, padding);
	}
	
	/**
	 * Automatically determines and sets the minimum widths of a table's columns.
	 * @param table The table to be adjusted.
	 * @param leftPadding The amount of left-hand side pixel padding.
	 * @param rightPadding The amount of right-hand side pixel padding.
	 */
	public static void setColumnMinWidths(JTable table, int leftPadding, int rightPadding) {
		TableColumnModel tcm = table.getColumnModel();
		FontMetrics fm = table.getFontMetrics(new JLabel().getFont());
		for (int col = 0; col < tcm.getColumnCount(); col++) {
			TableColumn tc = tcm.getColumn(col);
			// TODO: parametrize hard-coded font
			tc.setMinWidth(leftPadding + fm.stringWidth(tc.getHeaderValue().toString()) + rightPadding);
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

			if (value instanceof Number) {
				value = formatter.format((Number) value);
			}
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column );
		}
	}
	
}
