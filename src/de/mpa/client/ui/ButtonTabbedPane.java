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
@SuppressWarnings("serial")
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
		((Busyable) getTabComponentAt(index)).setBusy(busy);
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
		addTab(" ", component);
		
		// create and insert tab button
		JButton tabButton = new TabPaneButton(this, title, icon);
        setTabComponentAt(getTabCount() - 1, tabButton);
	}

	@Override
	public void setEnabledAt(int index, boolean enabled) {
		super.setEnabledAt(index, enabled);
		// propagate enable state to tab component
        getTabComponentAt(index).setEnabled(enabled);
	}

	/**
	 * Convenience extension of button class for use as tab pane tab component.
	 * @author A. Behne
	 */
	private class TabPaneButton extends JButton implements Busyable {

		/**
		 * The busy label overlaid on top of this button.
		 */
		private final JXBusyLabel busyLbl;

		/**
		 * Constructs a button to be used inside JTabbedPane tabs for rollover effects.	
		 * @param tabPane the tabbed pane reference
		 * @param text the text to be displayed as tab title
		 * @param icon the icon to be displayed on the tab
		 */
		public TabPaneButton(JTabbedPane tabPane, String text, Icon icon) {
			super(text, icon);

            setRolloverIcon(IconConstants.createColorRescaledIcon(icon, 1.1f));
            setPressedIcon(IconConstants.createColorRescaledIcon(icon, 0.8f));
            setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 5));
            setContentAreaFilled(false);
            setFocusable(false);
            setHorizontalAlignment(SwingConstants.LEFT);

            addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent me) {
                    this.forwardEvent(me);
				}
				public void mouseReleased(MouseEvent me) {
                    this.forwardEvent(me);
				}
				public void mouseClicked(MouseEvent me) {
                    this.forwardEvent(me);
				}
				private void forwardEvent(MouseEvent me) {
					tabPane.dispatchEvent(SwingUtilities.convertMouseEvent((Component) me.getSource(), me, tabPane));
				}
			});

            setLayout(new BorderLayout());

            busyLbl = new JXBusyLabel();
            busyLbl.setHorizontalAlignment(SwingConstants.CENTER);
            busyLbl.setVisible(false);

            add(busyLbl, BorderLayout.CENTER);
		}

		@Override
		public boolean isBusy() {
			return busyLbl.isBusy();
		}

		@Override
		public void setBusy(boolean busy) {
            busyLbl.setVisible(busy);
            busyLbl.setBusy(busy);
		}

		@Override
		public Dimension getPreferredSize() {
			// as the preferred size is determined by the layout if any components are added we
			// temporarily remove the busy label to get at the proper value - hacky, but it works
            remove(busyLbl);
			Dimension prefSize = super.getPreferredSize();
            add(this.busyLbl, BorderLayout.CENTER);
			return prefSize;
		}

	}
	
}
