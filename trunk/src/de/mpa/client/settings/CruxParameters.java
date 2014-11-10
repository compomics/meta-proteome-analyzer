package de.mpa.client.settings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map.Entry;

import de.mpa.client.settings.Parameter.BooleanParameter;
import de.mpa.client.settings.Parameter.NumberParameter;
import de.mpa.client.settings.Parameter.OptionParameter;

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
		/* Configurable settings */
		// Spectrum section
		this.put("enzyme", new OptionParameter(new Object[] {"trypsin", "elastase", "chymotrypsin", "cyanogen-bromide", "iodosobenzoate", "aspn", "proline-endopeptidase"},
				0, "Cleavage enzyme", "Cleavage enzyme to be used by the search engine.", "Spectrum"));
		this.put("fragment-mass", new OptionParameter(new Object[] { "mono", "average" },
				0, "Fragment mass type", "Which isotopes to use in calculating fragment ion mass.", "Spectrum"));
		this.put("min-peaks", new NumberParameter(20, 1, null, "Minimum peaks", "Minimum number of peaks a spectrum must have for it to be searched.", "Spectrum"));
		this.put("use-flanking-peaks", new BooleanParameter(false, "Use flanking peaks", "Turn on or off the peaks flanking the b/y ions.", "Spectrum"));
		this.put("top-match", new NumberParameter(5, 1, null, "Top matching PSMs", "The number of PSMs per spectrum written to the output files.", "Spectrum"));
		// Scoring section
		this.put("min-length", new NumberParameter(7, 1, null, "Minimum peptide length", "The minimum length of peptides to consider.", "Scoring parameters"));
		this.put("max-length", new NumberParameter(50, 1, null, "Maximum peptide length", "The maximum length of peptides to consider.", "Scoring parameters"));
		this.put("min-mass", new NumberParameter(200.0, 0.0, null, "Minimum peptide mass", "The minimum neutral mass of the peptides to index.", "Scoring parameters"));
		this.put("max-mass", new NumberParameter(7200.0, 0.0, null, "Maximum peptide mass", "The maximum neutral mass of the peptides to index.", "Scoring parameters")); 
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
			// Compare values, if they differ add a line to the configuration file
			if (!value.equals(defaultValue)) {
				// Write line containing non-default value
				sb.append(key + "=" + value + "\n");
			}
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