package de.mpa.client.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

/**
 * Provides some helper routines for screen configuration.
 * 
 * @author Thilo Muth
 * 
 */
public class ScreenConfig {

	/**
	 * Center the given component in the visible screen.
	 * 
	 * @param aComponent
	 */
	public static void centerInScreen(Component aComponent) {
		Dimension tDim = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (tDim.width - aComponent.getSize().width) / 2;
		int y = (int) ((tDim.height - aComponent.getSize().height) * 0.46);	// slightly above center, feels more balanced
		aComponent.setLocation(x, y);
	}

	/**
	 * Center the given component in the given parent.
	 * 
	 * @param aComponent
	 * @param aParent
	 */
	public static void centerInComponent(Component aComponent, Component aParent) {
		Point tPoint = aParent.getLocation();
		Dimension tDimComp = aComponent.getSize();
		Dimension tDimParent = aParent.getSize();
		int x = (int) tPoint.getX() + (tDimParent.width - tDimComp.width) / 2;
		int y = (int) tPoint.getY() + (tDimParent.height - tDimComp.height) / 2;
		aComponent.setLocation(x, y);
	}
}

