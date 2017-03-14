package de.mpa.client.ui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * This class allows customizable file extension filtering.
 *
 * @author T. Muth, A. Behne
 * @version $Id$
 */
public class ExtensionFileFilter extends FileFilter {

    /**
     * The extension to filter on. Will always be prefixed with a '.'.
     */
    private String extension;
    
    /**
     * The description String to be displayed in file chooser dialogs.
     */
    private String description;
    
    /**
     * Boolean to indicate whether the matching needs to be case-sensitive.
     */
    private boolean caseSensitive;
    
    /**
     * Constructs a file filter that filters on the specified extension.
     * 
     * @param aExtension The extension String. Will automatically prepend '.' if not already present.
     */
    public ExtensionFileFilter(String aExtension) {
    	this(aExtension, false);
    }

    /**
     * Constructs a file filter that filters on the specified extension.
     * 
     * @param aExtension The extension String. Will automatically prepend '.' if not already present.
     * @param aCaseSensitive boolean denoting case sensitivity. <code>true</code> if case-sensitive,
     * <code>false</code> otherwise.
     */
    public ExtensionFileFilter(String aExtension, boolean aCaseSensitive) {
    	this(aExtension, aCaseSensitive, null);
    }
    
    /**
     * Constructs a file filter that filters on the specified extension.
     * 
     * @param aExtension The extension String. Will automatically prepend '.' if not already present.
     * @param aCaseSensitive boolean denoting case sensitivity. <code>true</code> if case-sensitive,
     * <code>false</code> otherwise.
     * @param aDescription The description String to be displayed in file chooser dialogs. Will use 
     * default ".ext files" pattern if <code>null</code> is provided.
     */
    public ExtensionFileFilter(String aExtension, boolean aCaseSensitive,
			String aDescription) {
        
        // Check whether description was provided, if not, fall back to default.
        if (aDescription == null) {
        	aDescription = aExtension.toUpperCase() + " Files";
        }
        
        // Check for prefixed '.', if not present, prepend it.
        if (!aExtension.startsWith(".")) {
            aExtension = "." + aExtension;
        }
        
        // Case sensitivity.
        if (!this.caseSensitive) {
            aExtension = aExtension.toLowerCase();
        }

        extension = aExtension;
        caseSensitive = aCaseSensitive;
        description = aDescription;
	}

	/**
     * This method checks whether the specified file is acceptable.
     *
     * @param pathname File with the full pathname for the file.
     * @return <code>true</code> if the file is acceptable, <code>false</code> otherwise.
     */
    public boolean accept(File pathname) {
        boolean result = false;
        if (pathname.isFile()) {
            String name = pathname.getName();
            // See if we need to work case-insensitive...
            if (!this.caseSensitive) {
                name = name.toLowerCase();
            }
            // Check the extension.
            if (name.endsWith(this.extension)) {
                result = true;
            }
        } else {
            // One can always browse folders.
            result = true;
        }
        return result;
    }

    /**
     * Returns whether the filter is case-sensitive.
     *
     * @return <code>true</code> if the filter is case-sensitive, <code>false</code> otherwise.
     */
    public boolean isCaseSensitive() {
        return this.caseSensitive;
    }

    /**
     * Returns the extension String.
     *
     * @return the extension String.
     */
    public String getExtension() {
        return this.extension;
    }

    /**
     * The description of this filter. For example: "JPG and GIF Images"
     *
     * @see javax.swing.filechooser.FileView#getName
     */
    public String getDescription() {
        return this.description;
    }
}
