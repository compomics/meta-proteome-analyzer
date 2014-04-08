package de.mpa.client.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXBusyLabel;

import de.mpa.client.ui.icons.IconConstants;

/**
 * Tabbed pane extension featuring buttons with rollover effects as tab components.
 * 
 * @author A. Behne
 */
public class ButtonTabbedPane extends JTabbedPane {

	/**
     * Creates an empty <code>TabbedPane</code> with the specified tab placement
     * of either: <code>JTabbedPane.TOP</code>, <code>JTabbedPane.BOTTOM</code>,
     * <code>JTabbedPane.LEFT</code>, or <code>JTabbedPane.RIGHT</code>.
     *
     * @param tabPlacement the placement for the tabs relative to the content
     * @see #addTab
     */
	public ButtonTabbedPane(int tabPlacement) {
		super(tabPlacement);
	}
	
	/**
	 * Sets the busy state of the tab button inside the tab corresponding to the
	 * specified index.
	 * @param index the tab index
	 * @param busy <code>true</code> if the tab is to be busy, <code>false</code> otherwise
	 */
	public void setBusyAt(int index, boolean busy) {
		((Busyable) this.getTabComponentAt(index)).setBusy(busy);
	}
	
	/**
     * Adds a <code>component</code> represented by a <code>title</code>
     * and/or <code>icon</code>, either of which can be <code>null</code>. 
     * Cover method for <code>insertTab</code>. 
     *
     * @param title the title to be displayed in this tab
     * @param icon the icon to be displayed in this tab
     * @param component the component to be displayed when this tab is clicked
     *
     * @see #insertTab
     * @see #removeTabAt  
     */
	public void addTab(String title, Icon icon, Component component) {
		// add dummy tab
		super.addTab(" ", component);
		
		// create and insert tab button
		JButton tabButton = new TabPaneButton(this, title, icon);
		this.setTabComponentAt(this.getTabCount() - 1, tabButton);
	}

	@Override
	public void setEnabledAt(int index, boolean enabled) {
		super.setEnabledAt(index, enabled);
		// propagate enable state to tab component
		this.getTabComponentAt(index).setEnabled(enabled);
	}

	/**
	 * Convenience extension of button class for use as tab pane tab component.
	 * @author A. Behne
	 */
	private class TabPaneButton extends JButton implements Busyable {

		/**
		 * The busy label overlaid on top of this button.
		 */
		private JXBusyLabel busyLbl;

		/**
		 * Constructs a button to be used inside JTabbedPane tabs for rollover effects.	
		 * @param tabPane the tabbed pane reference
		 * @param text the text to be displayed as tab title
		 * @param icon the icon to be displayed on the tab
		 */
		public TabPaneButton(final JTabbedPane tabPane, String text, Icon icon) {
			super(text, icon);

			this.setRolloverIcon(IconConstants.createColorRescaledIcon(icon, 1.1f));
			this.setPressedIcon(IconConstants.createColorRescaledIcon(icon, 0.8f));
			this.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 5));
			this.setContentAreaFilled(false);
			this.setFocusable(false);
			this.setHorizontalAlignment(SwingConstants.LEFT);

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
					tabPane.dispatchEvent(SwingUtilities.convertMouseEvent((Component) me.getSource(), me, tabPane));
				}
			});

			this.setLayout(new BorderLayout());

			this.busyLbl = new JXBusyLabel();
			this.busyLbl.setHorizontalAlignment(SwingConstants.CENTER);
			this.busyLbl.setVisible(false);

			this.add(this.busyLbl, BorderLayout.CENTER);
		}

		@Override
		public boolean isBusy() {
			return this.busyLbl.isBusy();
		}

		@Override
		public void setBusy(boolean busy) {
			this.busyLbl.setVisible(busy);
			this.busyLbl.setBusy(busy);
		}

		@Override
		public Dimension getPreferredSize() {
			// as the preferred size is determined by the layout if any components are added we
			// temporarily remove the busy label to get at the proper value - hacky, but it works
			this.remove(this.busyLbl);
			Dimension prefSize = super.getPreferredSize();
			this.add(busyLbl, BorderLayout.CENTER);
			return prefSize;
		}

	}
	
}
