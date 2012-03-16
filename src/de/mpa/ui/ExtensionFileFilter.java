package de.mpa.ui;

import java.io.File;

/**
 * This class allows customizable file extension filtering.
 *
 * @author Thilo Muth
 * @version $Id$
 */
public class ExtensionFileFilter extends javax.swing.filechooser.FileFilter {

    /**
     * The extension to filter on. Will always be prefixed with a '.'.
     */
    private String extension = null;
    /**
     * Boolean to indicate whether the matching needs to be case sensitive.
     */
    private boolean caseSensitive = false;

    /**
     * Constructor that takes the file extension to filter on, and whether filtering
     * should be case sensitive.
     *
     * @param aExtension     String with the extension. If not already prefixed with '.',
     *                       the '.' will automatically be prefixed.
     * @param aCaseSensitive boolean to indicate case sensitivity.
     */
    public ExtensionFileFilter(String aExtension, boolean aCaseSensitive) {
        caseSensitive = aCaseSensitive;
        // Check for prefixed '.', if not present, prefix it automatically.
        if (!aExtension.startsWith(".")) {
            aExtension = "." + aExtension;
        }
        // Case sensitivity.
        if (!caseSensitive) {
            aExtension = aExtension.toLowerCase();
        }
        this.extension = aExtension;
    }

    /**
     * This method checks whether the specified file is acceptable.
     *
     * @param pathname File with the full pathname for the file.
     * @return boolean to indicate acceptance ('true') or refusal ('false')
     */
    public boolean accept(File pathname) {
        boolean result = false;
        if (pathname.isFile()) {
            String name = pathname.getName();
            // See if we need to work case insensitive...
            if (!caseSensitive) {
                name = name.toLowerCase();
            }
            // Check the extension.
            if (name.endsWith(extension)) {
                result = true;
            }
        } else {
            // One can always browse folders.
            result = true;
        }
        return result;
    }

    /**
     * Returns true if the filter is case sensitive.
     *
     * @return true if the filter is case sensitive
     */
    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    /**
     * Returns the extension.
     *
     * @return the extension
     */
    public String getExtension() {
        return extension;
    }

    /**
     * The description of this filter. For example: "JPG and GIF Images"
     *
     * @see javax.swing.filechooser.FileView#getName
     */
    public String getDescription() {
        return extension + " files";
    }
}
