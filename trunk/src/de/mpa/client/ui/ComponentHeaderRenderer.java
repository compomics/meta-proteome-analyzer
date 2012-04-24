package de.mpa.client.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class ComponentHeaderRenderer extends DefaultTableHeaderCellRenderer {

	private JLabel label;
	private JComponent comp;
	private JPanel panel;
	
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
		super();
		setHorizontalTextPosition(RIGHT);
		label = new JLabel("", SwingConstants.CENTER);
		label.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, label.getIconTextGap()));
		comp = component;
		panel = new JPanel(new BorderLayout());
		panel.add(this, BorderLayout.WEST);		// icon
		panel.add(label, BorderLayout.CENTER);	// label
		panel.add(comp, BorderLayout.EAST);		// component
		panel.setBorder(BorderFactory.createCompoundBorder(
				UIManager.getBorder("TableHeader.cellBorder"),
				BorderFactory.createEmptyBorder(0, 0, 0, 3)));
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
		if (icon == null) {
			icon = new Icon() {
				public void paintIcon(Component c, Graphics g, int x, int y) { }
				public int getIconWidth() { return UIManager.getIcon("Table.ascendingSortIcon").getIconWidth(); }
				public int getIconHeight() { return UIManager.getIcon("Table.ascendingSortIcon").getIconHeight(); }
			};
		}
		return icon;
	}

}
