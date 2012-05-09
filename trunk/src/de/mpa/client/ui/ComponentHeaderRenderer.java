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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.RowSorter.SortKey;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

public class ComponentHeaderRenderer extends DefaultTableHeaderCellRenderer {

	private JLabel label;
	private JComponent comp;
	private JPanel panel;
	private Icon icon;
	
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
	
	public ComponentHeaderRenderer(JComponent component) {
		this(component, null);
		final int iconWidth = UIManager.getIcon("Table.ascendingSortIcon").getIconWidth();
		final int iconHeight = UIManager.getIcon("Table.ascendingSortIcon").getIconHeight();
		this.icon = new Icon() {
			public void paintIcon(Component c, Graphics g, int x, int y) { }
			public int getIconWidth() { return iconWidth; }
			public int getIconHeight() { return iconHeight; }
		};
	}

	public ComponentHeaderRenderer(JComponent component, Icon icon) {
		super();
		setHorizontalTextPosition(RIGHT);
		label = new JLabel("", SwingConstants.CENTER);
		comp = component;
		panel = new JPanel(new FormLayout("0px, l:m, 0px, 0px:g, 0px, r:p, 1px", "p"));
		panel.add(this, CC.xy(2, 1));
		panel.add(label, CC.xy(4, 1));
		panel.add(comp, CC.xy(6, 1));
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
			return (SortKey) sortedColumns.get(1);
		}
		return null;
	}

}
