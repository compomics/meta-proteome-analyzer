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
		this.put("enzyme", new Parameter("Cleavage enzyme", new DefaultComboBoxModel(new Object[] {"trypsin", "elastase", "chymotrypsin", "cyanogen-bromide", "iodosobenzoate", "aspn", "proline-endopeptidase"}), "General", "Cleavage enzyme to used by the Crux search engine."));		// protease
		
		/* Configurable settings */
		// Spectrum section
		this.put("fragment-mass", new Parameter("Fragment Mass Type", new DefaultComboBoxModel(new Object[] { "mono", "average" }), "Spectrum", "Which isotopes to use in calculating fragment ion mass."));
		this.put("min-peaks", new Parameter("Minimum Peaks", 20, "Spectrum", "Minimum number of peaks a spectrum must have for it to be searched."));
		this.put("use-flanking-peaks", new Parameter("Use Flanking Peaks", false, "Spectrum", "Turn on or off the peaks flanking the b/y ions."));
		this.put("top-match", new Parameter("Top Match PSMs", 5, "Spectrum", "The number of psms per spectrum writen to the output files."));
		// Scoring section
		this.put("min-length", new Parameter("Minimum peptide length", 7, "Scoring parameters", "The minimum length of peptides to consider."));
		this.put("max-length", new Parameter("Maximum peptide length", 50, "Scoring parameters", "The maximum length of peptides to consider."));
		this.put("min-mass", new Parameter("Minimum peptide mass", 200.0, "Scoring parameters", "The minimum neutral mass of the peptides to place in the index."));
		this.put("max-mass", new Parameter("Maximum peptide mass", 7200.0, "Scoring parameters", "The maximum neutral mass of the peptides to place in index."));
	}

	@Override
	public String toString() {
		// Set up writer for parameter file
		StringBuffer sb = new StringBuffer();
		
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
		
			// Write line containing non-default value
			sb.append(key + "=" + value + "\n");
		}
		return sb.toString();
	}

	@Override
	public File toFile(String path) throws IOException {
		// Append extension if it's missing
		
		if (!path.endsWith(".params")) {
			path += ".params";
		}
		
		// Create new file at specified path
		File file = new File(path);
		
		// Check for whether path does point to a file (and not a directory)
		if(!file.isFile()) {
			throw new IOException();
		}
		
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