package de.mpa.client.ui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * Extension file filter implementation allowing for multiple extensions at the
 * same time.
 * 
 * @author A. Behne
 */
public class MultiExtensionFileFilter extends ExtensionFileFilter {

	/**
	 * The array of referenced filters.
	 */
	private FileFilter[] filters;
	
	/**
	 * Constructs an extension file filter filtering on the extensions defined
	 * inside the specified file filters.
	 * 
	 * @param aDescription the description string to be displayed in file chooser dialogs
	 * @param filters the file filters to combine
	 */
	public MultiExtensionFileFilter(String aDescription, FileFilter... filters) {
		super("", false, aDescription);
		this.filters = filters;
	}

	@Override
	public boolean accept(File pathname) {
		boolean result = false;
		for (FileFilter filter : filters) {
			result |= filter.accept(pathname);
			if (result) {
				break;
			}
		}
		return result;
	}

}
