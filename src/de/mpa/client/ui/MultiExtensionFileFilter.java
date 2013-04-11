package de.mpa.client.ui;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * TODO API
 * 
 * @author heyer
 */
public class MultiExtensionFileFilter extends ExtensionFileFilter {

	private FileFilter[] filters;
	
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
