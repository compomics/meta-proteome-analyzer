package de.mpa.client.ui;

import javax.swing.plaf.ColorUIResource;

import de.mpa.client.Constants.UIColor;

/**
 * Convenience class to delegate color lookups.
 * @author A. Behne
 */
public class DelegateColor extends ColorUIResource {
	
	/**
	 * The delegate color.
	 */
	private UIColor delegate;
	
	/**
	 * Constructs a color getting its value from the specified delegate
	 * color.
	 * @param delegate the delegate color
	 */
	public DelegateColor(UIColor delegate) {
		super(0);
		this.delegate = delegate;
	}
	
	@Override
	public int getRGB() {
		return delegate.getColor().getRGB();
	}
	
	/* Hack to circumvent buffering strategies of certain UI components */
	@Override
	public boolean equals(Object obj) {
		return false;
	}
}