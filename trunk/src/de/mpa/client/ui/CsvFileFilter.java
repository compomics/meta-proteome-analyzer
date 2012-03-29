package de.mpa.client.ui;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * File filter for *.csv files.
 *
 * @author Thilo Muth
 */
public class CsvFileFilter extends FileFilter {

    private final static String csv = "csv";
    private final static String CSV = "CSV";

    /**
     * Accept all directories, *.csv files.
     *
     * @param f
     * @return boolean
     */
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = getExtension(f);
        if (extension != null) {
            if (extension.equals(CsvFileFilter.csv) ||
                    extension.equals(CsvFileFilter.CSV)) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * The description of this filter
     *
     * @return String
     */
    public java.lang.String getDescription() {
        return "CSV Format (tab separated values) (*.csv)";
    }

    /**
     * Get the extension of a file.
     *
     * @param f
     * @return String - the extension of the file f
     */
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }
}
