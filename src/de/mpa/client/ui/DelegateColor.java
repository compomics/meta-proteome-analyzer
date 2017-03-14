package de.mpa.client.ui;

import de.mpa.client.Constants;

import javax.swing.plaf.ColorUIResource;

/**
 * Convenience class to delegate color lookups.
 * @author A. Behne
 */
@SuppressWarnings("serial")
public class DelegateColor extends ColorUIResource {
	
	/**
	 * The delegate color.
	 */
	private Constants.UIColor delegate;

	/**
	 * Constructs a color getting its value from the specified delegate
	 * color.
	 * @param delegate the delegate color
	 */
	public DelegateColor(Constants.UIColor delegate) {
		super(0);
		this.delegate = delegate;
	}
	
	@Override
	public int getRGB() {
		return this.delegate.getColor().getRGB();
	}
	
	/* Hack to circumvent buffering strategies of certain UI components */
	@Override
	public boolean equals(Object obj) {
		return false;
	}
}