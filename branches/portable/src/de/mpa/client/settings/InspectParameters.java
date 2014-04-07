package de.mpa.client.settings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map.Entry;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;

/**
 * Class for storing InsPecT search engine-specific settings.
 * 
 * @author T. Muth, F. Kohrs, A. Behne
 */
public class InspectParameters extends ParameterMap {
	
	/**
	 * Constructs a parameter map initialized with default values.
	 */
	public InspectParameters() {
		this.initDefaults();
	}

	@Override
	public void initDefaults() {
		// Protease
		this.put("protease", new Parameter(null, "Trypsin", "General", null));	// protease
		
		/* Configurable settings */
		// PTM section
		this.put("mod", new Parameter("Searchable PTMs", "+57,C,fix\n+16,M,opt\n","PTM","<html>Specify PTMs in the format <b>[MASS]</b>,<b>[RESIDUES]</b>,<b>[TYPE]</b>,<b>[NAME]</b>.<br>The <i>mass</i> (in Da) and affected amino acid <i>residues</i> are mandatory.<br>Valid values for <i>type</i> are <i>fix</i>, <i>cterminal</i>, <i>nterminal</i>, and <i>opt</i> (default).<br>The first four characters of the <i>name</i> parameter should be unique.</html>"));
		this.put("Mods", new Parameter("PTMs per peptide", 1, "PTM", "Number of PTMs permitted in a single peptide. Set this to 1 (or higher) if you specify PTMs to search for."));
		this.put("Unrestrictive", new Parameter("Perform unrestrictive search", false, "PTM", "If checked, use the MS-Alignment algorithm to perform an unrestrictive search (allowing arbitrary modification masses). Running an unrestrictive search is slower than the normal (tag-based) search."));
		// Scoring section
		this.put("MultiCharge", new Parameter("Multiple precursor charge", true, "Scoring", "Attempts to guess the precursor charge and mass and consider multiple charge states if feasible."));
		this.put("Instrument", new Parameter("Instrument Type", new DefaultComboBoxModel(new Object[] { "ESI-ION-TRAP", "QTOF", "FT-HYBRID" }), "Scoring", "Instrument type. Affects fragmentation model for spectrum matching."));
		this.put("TagCount", new Parameter("Tag Count", 100, "Scoring", "Number of tags to generate."));
		this.put("TagLength", new Parameter("Tag Length", new Integer[] { 3, 1, 6 }, "Scoring", "Length of peptide sequence tags."));
	}

	@Override
	public String toString() {
		// Set up writer for parameter file
		StringBuffer sb = new StringBuffer();
		
		// Grab a set of default parameters for comparison purposes
		InspectParameters defaults = new InspectParameters();
		// Iterate stored parameter values and compare them to the defaults
		for (Entry<String, Parameter> entry : this.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue().getValue();
			Object defaultValue = defaults.get(key).getValue();
			if (value instanceof ComboBoxModel) {
				value = ((ComboBoxModel) value).getSelectedItem();
				defaultValue = ((ComboBoxModel) defaultValue).getSelectedItem();
			} else 	if (value instanceof Integer[]) {
				value = ((Integer[]) value)[0];
				defaultValue = ((Integer[]) defaultValue)[0];
			}
			if (value instanceof String) {
				// special case for PTMs, split multi-line string
				String[] values = value.toString().split("\\n");
				for (String val : values) {
					if (!val.isEmpty()) {
						// Write line
						sb.append(key + "," + val + "\n");
					}
				}
			} else {
				if (value instanceof Boolean) {
					// turn true/false into yes/no
					value = (((Boolean) value).booleanValue()) ? 1 : 0;
				}
				// Write line containing non-default value
				sb.append(key + "," + value + "\n");
			}
		}
		return sb.toString();
	}

	@Override
	public File toFile(String path) throws IOException {
		// Append extension if it's missing
		if (!path.endsWith(".txt")) {
			path += ".txt";
		}
		
		// Create new file at specified path
		File file = new File(path);
		
		// Check for whether path does point to a file (and not a directory)
		if (!file.isFile()) {
			throw new IOException();
		}
				
		// Set up writer for parameter file
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		
		// Grab a set of default parameters for comparison purposes
		InspectParameters defaults = new InspectParameters();
		// Iterate stored parameter values and compare them to the defaults
		for (Entry<String, Parameter> entry : this.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue().getValue();
			Object defaultValue = defaults.get(key).getValue();
			// special case for encapsulated values
			if (value instanceof ComboBoxModel) {
				value = ((ComboBoxModel) value).getSelectedItem();
				defaultValue = ((ComboBoxModel) defaultValue).getSelectedItem();
			} else if (value instanceof Integer[]) {
				value = ((Integer[]) value)[0];
				defaultValue = ((Integer[]) defaultValue)[0];
			}
			if (value instanceof String) {
				// special case for PTMs, split multi-line string
				String[] values = value.toString().split("\\n");
				for (String val : values) {
					if (!val.isEmpty()) {
						// Write line
						bw.append(key + "," + val);
						bw.newLine();
					}
				}
			} else if (!value.equals(defaultValue)) {
				if (value instanceof Boolean) {
					// turn true/false into yes/no
					value = (((Boolean) value).booleanValue()) ? 1 : 0;
				}
				// Write line containing non-default value
				bw.append(key + "," + value);
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
