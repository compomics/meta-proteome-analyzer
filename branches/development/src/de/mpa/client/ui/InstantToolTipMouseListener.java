package de.mpa.client.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ToolTipManager;

import de.mpa.client.Constants;

/**
 * Custom mouse listener to instantly display a component's tooltip on mouse cursor entry.
 * 
 * @author A. Behne
 */
public class InstantToolTipMouseListener extends MouseAdapter {
	
	/**
	 * The original initial delay in milliseconds after which a tooltip is displayed.
	 */
	
	@Override
	public void mouseEntered(MouseEvent e) {
		// since there is no sure-fire way of triggering a component's tooltip directly,
		// set global tooltip delay to zero, revert on mouse cursor exit
		ToolTipManager.sharedInstance().setInitialDelay(10);
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		ToolTipManager.sharedInstance().setInitialDelay(Constants.DEFAULT_TOOLTIP_DELAY);
	}
	
}
