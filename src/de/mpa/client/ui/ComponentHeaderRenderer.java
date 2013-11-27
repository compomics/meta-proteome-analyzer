package de.mpa.client.ui;

import java.awt.Color;
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

/**
 * Table header renderer implementation allowing to embed a component next to
 * the header label.
 * @author A. Behne
 */
public class ComponentHeaderRenderer extends DefaultTableHeaderCellRenderer {

	/**
	 * The header label.
	 */
	private JLabel label;
	
	/**
	 * The header component.
	 */
	private JComponent comp;
	
	/**
	 * The header's compound panel.
	 */
	private JPanel panel;
	
	/**
	 * The header (sort) icon.
	 */
	private Icon icon;
	
	/**
	 * Creates a component header renderer using the provided component and the
	 * default leading orientation.
	 * @param component the component to render
	 */
	public ComponentHeaderRenderer(JComponent component) {
		this(component, null);
	}

	/**
	 * Creates a component header renderer using the provided component and icon
	 * and the default leading orientation.
	 * @param component the component to render
	 * @param icon the icon to render
	 */
	public ComponentHeaderRenderer(JComponent component, Icon icon) {
		this(component, icon, SwingConstants.LEADING);
	}
	
	/**
	 * Creates a component header renderer using the provided component, icon
	 * and horizontal orientation.
	 * @param component the component to render
	 * @param icon the icon to render
	 * @param orientation any of <code>SwingConstants.LEADING</code>,
	 *  <code>TRAILING</code>, <code>LEFT</code> or <code>RIGHT</code>
	 */
	public ComponentHeaderRenderer(JComponent component, Icon icon, int orientation) {
		// invoke super constructor, configure default label
		super();
		this.setHorizontalTextPosition(SwingConstants.RIGHT);
		
		// initialize delegate label and component
		this.label = new JLabel("", SwingConstants.CENTER);
		this.comp = component;
		
		// create and configure panel containing label, icon and component
		this.panel = new JPanel(new FormLayout("1px, 13px, c:0px:g, 13px, " + 
				((orientation == SwingConstants.TRAILING) ? "4px" : "1px"), "p"));
		this.panel.setBackground(new Color(164, 164, 164));
		this.panel.setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		this.panel.setOpaque(false);
		// lay out components, default label is reduced to showing only its icon (e.g. the sort icon)
		this.panel.add(this, CC.xy((orientation == SwingConstants.TRAILING) ? 4 : 2, 1));
		this.panel.add(this.label, CC.xy(3, 1));
		// add component only if not null
		if (this.comp != null) {
			this.panel.add(this.comp, CC.xy((orientation == SwingConstants.TRAILING) ? 2 : 4, 1));
			// forward mouse events on header to component
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
					comp.dispatchEvent(SwingUtilities.convertMouseEvent(panel, me, comp));
				}
			});
		} else {
			// use dummy panel as component, don't add it to layout
			this.comp = new JPanel();
		}

		// create empty dummy icon if no icon was provided
		if (icon == null) {
			final int iconWidth = 13, iconHeight = 13;
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
		// configure default header label
		super.getTableCellRendererComponent(table, value,
				isSelected, hasFocus, row, column);
		// remove border and text
		this.setBorder(null);
		this.setText(null);
		// apply text to delegate label
		this.label.setText(value.toString());
		// return compound panel
		return this.panel;
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
		// return primary sort key if any are present
		List sortedColumns = rowSorter.getSortKeys();
		if (sortedColumns.size() > 0) {
			return (SortKey) sortedColumns.get(0);
		}
		return null;
	}
	
	/**
	 * Returns the renderer component.
	 * @return the renderer component
	 */
	public JComponent getComponent() {
		return this.comp;
	}
	
	/**
	 * Returns the panel containing the renderer components.
	 * @return the header panel
	 */
	public JPanel getPanel() {
		return this.panel;
	}

}
