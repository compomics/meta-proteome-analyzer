package de.mpa.client.settings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map.Entry;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;

/**
 * Class for storing Crux search engine-specific settings.
 * 
 * @author T. Muth, F. Kohrs, A. Behne
 */
public class CruxParameters extends ParameterMap {

	/**
	 * Constructs a parameter map initialized with default values.
	 */
	public CruxParameters() {
		this.initDefaults();
	}
	
	@Override
	public void initDefaults() {
		/* Non-configurable settings */
		this.put("precursor-window", new Parameter(null, 1.0, "General", null));	// precursor tolerance
		this.put("mz-bin-width", new Parameter(null, 0.5, "General", null));		// fragment tolerance
//		this.put("mz-bin-offset", new Parameter(null, 0.68, "General", null));
		this.put("missed-cleavages", new Parameter(null, 2, "General", null));		// missed cleavages
		this.put("enzyme", new Parameter(null, "trypsin", "General", null));		// protease
		
		/* Configurable settings */
		// Spectrum section
		this.put("precursor-window-type", new Parameter("Precursor Window Type", new DefaultComboBoxModel(new Object[] { "mass", "mz", "ppm" }), "Spectrum", "The units for the window that is used to select peptides around the precursor mass location."));
		this.put("fragment-mass", new Parameter("Fragment Mass Type", new DefaultComboBoxModel(new Object[] { "mono", "average" }), "Spectrum", "Which isotopes to use in calculating fragment ion mass."));
		this.put("min-peaks", new Parameter("Minimum Peaks", 20, "Spectrum", "Minimum number of peaks a spectrum must have for it to be searched."));
		// Scoring section
		this.put("min-length", new Parameter("Minimum peptide length", 7, "Scoring parameters", "The minimum length of peptides to consider."));
		this.put("max-length", new Parameter("Maximum peptide length", 50, "Scoring parameters", "The maximum length of peptides to consider."));
	}

	@Override
	public String toString() {
		return null;
	}

	@Override
	public File toFile(String path) throws IOException {
		// Append extension if it's missing
		// TODO: add check for whether path does point to a file (and not a directory)
		if (!path.endsWith(".params")) {
			path += ".params";
		}
		
		// Create new file at specified path
		File file = new File(path);
		
		// Set up writer for parameter file
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		
		// Grab a set of default parameters for comparison purposes
		CruxParameters defaults = new CruxParameters();
		// Iterate stored parameter values and compare them to the defaults
		for (Entry<String, Parameter> entry : this.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue().getValue();
			Object defaultValue = defaults.get(key).getValue();
			if (value instanceof ComboBoxModel) {
				// special case for combobox models, compare selected items
				value = ((ComboBoxModel) value).getSelectedItem();
				defaultValue = ((ComboBoxModel) defaultValue).getSelectedItem();
			}
			// Compare values, if they differ add a line to the configuration file
			if (!value.equals(defaultValue)) {
				// Write line containing non-default value
				bw.append(key + "=" + value);
				bw.newLine();
			}
		}
		
		// Close writer
		bw.flush();
		bw.close();
		
		return file;
	}

}