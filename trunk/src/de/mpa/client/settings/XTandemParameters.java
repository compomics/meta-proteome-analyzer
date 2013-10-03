package de.mpa.client.settings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map.Entry;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;

/**
 * Class for storing X!Tandem search engine-specific settings.
 * 
 * @author T. Muth, F. Kohrs, A. Behne
 */
public class XTandemParameters extends ParameterMap {
	
	/**
	 * Constructs a parameter map initialized with default values.
	 */
	public XTandemParameters() {
		this.initDefaults();
	}

	@Override
	public void initDefaults() {
		/* Non-configurable settings */
		this.put("spectrum, parent monoisotopic mass isotope error", new Parameter(null, true, "General", null));
		this.put("spectrum, parent monoisotopic mass error plus", new Parameter(null, 1.0, "General", null));	// precursor tolerance
		this.put("spectrum, parent monoisotopic mass error minus", new Parameter(null, 1.0, "General", null));
		this.put("spectrum, fragment monoisotopic mass error", new Parameter(null, 0.5, "General", null));		// fragment tolerance
		this.put("scoring, maximum missed cleavage sites", new Parameter(null, 2, "General", null));			// missed cleavages
		this.put("protein, cleavage site", new Parameter(null, "[KR]|{P}", "General", null));					// protease
//		this.put("list path, taxonomy information", new Parameter(null, "taxonomy.xml", "General", null));
		
		// TODO: add missing parameters with fixed non-default values, if any
		
		/* Configurable settings */
		// Spectrum section
		this.put("spectrum, fragment mass error units", new Parameter("<html>Fragment Mass Error Unit</html>", new DefaultComboBoxModel(new Object[] { "Daltons", "ppm" }), "Spectrum", "Units for fragment ion mass tolerance (chemical average mass)."));
		this.put("spectrum, fragment mass type", new Parameter("Fragment Mass Type", new DefaultComboBoxModel(new Object[] { "monoisotopic", "average" }), "Spectrum", "Use chemical average or monoisotopic mass for fragment ions."));
		this.put("spectrum, total peaks", new Parameter("Total Peaks", 50, "Spectrum", "Maximum number of peaks to be used from a spectrum."));
		this.put("spectrum, minimum peaks", new Parameter("Minimum Peaks", 15, "Spectrum", "The minimum number of peaks required for a spectrum to be considered."));
		this.put("spectrum, minimum parent m+h", new Parameter("Minimum precursor mass", 500.0, "Spectrum", "The minimum parent mass required for a spectrum to be considered."));
		this.put("spectrum, minimum fragment mz", new Parameter("Minimum fragment m/z", 150.0, "Spectrum", "The minimum fragment m/z to be considered.."));
		this.put("spectrum, threads", new Parameter("Worker threads", 1, "Spectrum", "The number of worker threads to be used for calculation."));
		this.put("spectrum, sequence batch size", new Parameter("<html>Fasta sequence<br>batch size</html>", 1000, "Spectrum", "The number of FASTA sequences X!Tandem is processing per batch."));
		// Refinement section
		this.put("refine", new Parameter("Use first-pass search (refinement)", true, "Refinement", "Enable first-pass search (refinement)"));
		this.put("refine, spectrum synthesis", new Parameter("Use spectrum synthesis scoring", true, "Refinement", "Predict a synthetic spectrum and reward ions that agree with the predicted spectrum"));
		this.put("refine, maximum valid expectation value", new Parameter("E-value cut-off for refinement", 0.1, "Refinement", "Only hits below this threshold are considered for a second-pass search."));
		this.put("refine, point mutations", new Parameter("Allow point mutations", true, "Refinement", "Enables the use of amino acid single point mutations (using PAM matrix)."));
		// Scoring section
		this.put("scoring, minimum ion count", new Parameter("Minimum number of ions required", 4, "Scoring", "The minimum number of ions required for a peptide to be scored."));
		this.put("scoring, ions", new Parameter("use x ions|use y ions|use z ions||use a ions|use b ions|use c ions", new Boolean[][] { { false, true, false }, { false, true, false }}, "Scoring",
				"Allows the use of a-ions in scoring.|Allows the use of b-ions in scoring.|Allows the use of c-ions in scoring.||Allows the use of x-ions in scoring.|Allows the use of y-ions in scoring.|Allows the use of z-ions in scoring."));
	}

	/**
	 * Returns <code>null</code> as X!Tandem does not use command-line
	 * parameters to specify single settings. Refer to the
	 * {@link #toFile(String path) toFile()} method.
	 */
	@Override
	public String toString() {
		return null;
	}

	/**
	 * Generates a BIOML-compliant parameter file containing only parameters
	 * with non-default values for use with X!Tandem.
	 * @param path the path string pointing to the file that is to be created
	 * @return the parameter file
	 * @throws IOException if the file could not be written
	 */
	@Override
	public File toFile(String path) throws IOException {
		// Append extension if it's missing
		
		if (!path.endsWith(".xml")) {
			path += ".xml";
		}
		
		// Create new file at specified path
		File file = new File(path);
		
		// Check for whether path does point to a file (and not a directory)
		if (!file.isFile()) {
			throw new IOException();
		}
		// Set up writer for BIOML structure
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		bw.append("<bioml>");
		bw.newLine();
		
		// Grab a set of default parameters for comparison purposes
		XTandemParameters defaults = new XTandemParameters();
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
			if (value instanceof Boolean[][]) {
				// special case for matrix of checkboxes
				Boolean[][] values = (Boolean[][]) value;
				Boolean[][] defaultvalues = (Boolean[][]) defaultValue;
				String ions = "xyzabc";
				int k = 0;
				for (int i = 0; i < values.length; i++) {
					for (int j = 0; j < values[i].length; j++) {
						if (!values[i][j].equals(defaultvalues[i][j])) {
							value = (values[i][j].booleanValue()) ? "yes" : "no";
							// Write BIOML <note> tag containing non-default value
							String label = "scoring, " + ions.charAt(k) + " ions";
							bw.append("\t<note type=\"input\" label=\"" + label + "\">" + value + "</note>");
							bw.newLine();
						}
						k++;
					}
				}
			} else if (!value.equals(defaultValue)) {
				if (value instanceof Boolean) {
					// turn true/false into yes/no
					value = (((Boolean) value).booleanValue()) ? "yes" : "no";
				}
				// Write BIOML <note> tag containing non-default value
				bw.append("\t<note type=\"input\" label=\"" + key + "\">" + value.toString() + "</note>");
				bw.newLine();
			}
		}
		
		bw.append("</bioml>");
		
		// Close writer
		bw.flush();
		bw.close();
		
		return file;
	}
	
}