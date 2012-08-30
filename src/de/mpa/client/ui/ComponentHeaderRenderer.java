package de.mpa.client.ui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

public class ComponentHeaderRenderer extends DefaultTableHeaderCellRenderer {

	private JLabel label;
	private JComponent comp;
	private JPanel panel;
	private Icon icon;
	
	public ComponentHeaderRenderer(JComponent component) {
		this(component, null);
	}

	public ComponentHeaderRenderer(JComponent component, Icon icon) {
		this(component, icon, SwingConstants.LEADING);
	}
	
	public ComponentHeaderRenderer(JComponent component, Icon icon, int orientation) {
		super();
		this.setHorizontalTextPosition(RIGHT);
		
		label = new JLabel("", SwingConstants.CENTER);
		comp = component;
		panel = new JPanel(new FormLayout("1px, p, c:0px:g, p, " + 
				((orientation == SwingConstants.TRAILING) ? "4px" : "1px"), "p"));
		panel.add(this, CC.xy((orientation == SwingConstants.TRAILING) ? 4 : 2, 1));
		panel.add(label, CC.xy(3, 1));
		panel.add(comp, CC.xy((orientation == SwingConstants.TRAILING) ? 2 : 4, 1));
		panel.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				forwardEvent(me);
			}
			public void mouseReleased(MouseEvent me) {
				forwardEvent(me);
			}
			public void mouseClicked(MouseEvent me) {
				forwardEvent(me);
			}
			private void forwardEvent(MouseEvent me) {
//				if (comp.getBounds().contains(me.getPoint())) {
					comp.dispatchEvent(SwingUtilities.convertMouseEvent(panel, me, comp));
//				}
			}
		});

		if (icon == null) {
			final int iconWidth = UIManager.getIcon("Table.ascendingSortIcon").getIconWidth();
			final int iconHeight = UIManager.getIcon("Table.ascendingSortIcon").getIconHeight();
			icon = new Icon() {
				public void paintIcon(Component c, Graphics g, int x, int y) { }
				public int getIconWidth() { return iconWidth; }
				public int getIconHeight() { return iconHeight; }
			};
		}
		this.icon = icon;
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		super.getTableCellRendererComponent(table, value,
				isSelected, hasFocus, row, column);
		setBorder(null);
		setText(null);
		label.setText(value.toString());
		return panel;
	}
	
	@Override
	public Icon getIcon() {
		Icon icon = super.getIcon();
		return (icon == null) ? this.icon : icon;
	}
	
	@Override
	protected SortKey getSortKey(JTable table, int column) {
		RowSorter rowSorter = table.getRowSorter();
		if (rowSorter == null) {
			return null;
		}

		List sortedColumns = rowSorter.getSortKeys();
		if (sortedColumns.size() > 0) {
			return (SortKey) sortedColumns.get(0);
		}
		return null;
	}
	
	/**
	 * @return the comp
	 */
	public JComponent getComponent() {
		return comp;
	}

	/**
	 * @return the panel
	 */
	public JPanel getPanel() {
		return panel;
	}

}
