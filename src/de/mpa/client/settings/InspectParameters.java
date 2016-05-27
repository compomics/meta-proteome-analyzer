package de.mpa.client.settings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map.Entry;

import de.mpa.client.settings.Parameter.BooleanParameter;
import de.mpa.client.settings.Parameter.NumberParameter;
import de.mpa.client.settings.Parameter.OptionParameter;
import de.mpa.client.settings.Parameter.TextAreaParameter;
import de.mpa.client.settings.Parameter.TextParameter;

/**
 * Class for storing InsPecT search engine-specific settings.
 * 
 * @author T. Muth, F. Kohrs, A. Behne
 */
@SuppressWarnings("serial")
public class InspectParameters extends ParameterMap {
	
	/**
	 * Constructs a parameter map initialized with default values.
	 */
	public InspectParameters() {
		this.initDefaults();
	}

	@Override
	public void initDefaults() {
		/* Non-configurable settings */
		this.put("protease", new TextParameter("Trypsin", "Protease", "The proteolytic enzyme to use.", "General"));
		
		/* Configurable settings */
		// PTM section
		this.put("mod", new TextAreaParameter("+57,C,fix\n+16,M,opt\n", "Searchable PTMs", "<html>Specify PTMs in the format <b>[MASS]</b>,<b>[RESIDUES]</b>,<b>[TYPE]</b>,<b>[NAME]</b>.<br>The <i>mass</i> (in Da) and affected amino acid <i>residues</i> are mandatory.<br>Valid values for <i>type</i> are <i>fix</i>, <i>cterminal</i>, <i>nterminal</i>, and <i>opt</i> (default).<br>The first four characters of the <i>name</i> parameter should be unique.</html>", "PTM"));
		this.put("Mods", new NumberParameter(1, 0, null, "PTMs per peptide", "Number of PTMs permitted in a single peptide. Set this to 1 (or higher) if you specify PTMs to search for.", "PTM"));
		this.put("Unrestrictive", new BooleanParameter(false, "Perform unrestrictive search", "<html>If checked, use the MS-Alignment algorithm to perform an unrestrictive search (allowing arbitrary modification masses).<br>Running an unrestrictive search is slower than the normal (tag-based) search.</html>", "PTM"));
		// Scoring section
		this.put("MultiCharge", new BooleanParameter(true, "Multiple precursor charge", "Attempt to guess the precursor charge and mass and consider multiple charge states if feasible.", "Scoring"));
		this.put("Instrument", new OptionParameter(new Object[] { "ESI-ION-TRAP", "QTOF", "FT-HYBRID" }, 0, "Instrument Type", "The type of instrument used. Affects fragmentation model for spectrum matching.", "Scoring"));
		this.put("TagCount", new NumberParameter(100, 1, null, "Tag Count", "Number of tags to generate.", "Scoring"));
		this.put("TagLength", new NumberParameter(3, 1, 6, "Tag Length", "Length of peptide sequence tags.", "Scoring"));
	}

	@Override
	public String toString() {
		// Set up buffer for parameter file
		StringBuffer sb = new StringBuffer();
		
		// Iterate stored parameter values
		for (Entry<String, Parameter> entry : this.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue().getValue();
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
					// turn true/false into 1/0
					value = ((Boolean) value) ? 1 : 0;
				}
				// Write line containing value
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
					// turn true/false into 1/0
					value = ((Boolean) value) ? 1 : 0;
				}
				// Write line containing non-default value
				bw.append(key + "," + value);
				bw.newLine();
			}
		}
		
		// Close writer
		bw.flush();
		bw.close();
		
		return file;
	}

}
