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
	 * Enumeration holding cleavage rules.
	 * 
	 * @author behne
	 */
	private enum CleavageRule {
		TRYPSIN("Trypsin", "[KR]|{P}"),
		ARG_C("Arg-C", "[R]|{P}"),
		CNBR("CNBr", "[M]|{P}"),
		CHYMOTRYPSIN("Chymotrypsin", "[FMWY]|{P}"),
		FORMIC_ACID("Formic Acid", "[D]|{P}"),
		LYS_C("Lys-C", "[K]|{P}"),
		LYS_C_NO_P("Lys-C, no P rule", "[K]|[X]"),
		PEPSIN_A("Pepsin A", "[FL]|[X]"),
		TRYPSIN_CNBR("Trypsin + CNBr", "[KR]|{P},[M]|{P}"),
		TRYPSIN_CHYMOTRYPSIN("Trypsin + Chymotrypsin", "[KR]|{P},[FMWY]|{P}"),
		TRYPSIN_NO_P("Trypsin, no P rule", "[KR]|[X]"),
		ELASTASE("Elastase", "[AGILV]|{P}"),
		CLOSTRIPAIN("Clostripain", "[R]|[X]"),
		ASP_N("Asp-N", "[X]|[D]"),
		GLU_C("Glu-C", "[DE]|{P}"),
		ASP_N_GLU_C("Asp-N + Glu-C", "[X]|[D],[DE]|{P}"),
		TRYPSIN_GLUC("Trypsin Gluc", "[DEKR]|{P}"),
		GLUC_BICARB("Gluc Bicarb", "[E]|{P}"),
		NON_SPECIFIC("Non-Specific", "[X]|[X]");
		
		/**
		 * The name of the rule.
		 */
		private String name;
		
		/**
		 * The rule string.
		 */
		private String rule;

		/**
		 * Constructs a cleavage rule from the specified name and rule strings.
		 * @param name the name of the rule
		 * @param rule the rule string
		 */
		private CleavageRule(String name, String rule) {
			this.name = name;
			this.rule = rule;
		}
		
		/**
		 * Returns the rule string.
		 * @return the rule string
		 */
		public String getRule() {
			return this.rule;
		}
		
		@Override
		public String toString() {
			return this.name;
		}
	}

	/**
	 * Constructs a parameter map initialized with default values.
	 */
	public XTandemParameters() {
		this.initDefaults();
	}

	@Override
	public void initDefaults() {
		/* Configurable settings */
		this.put("protein, cleavage site", new Parameter("Cleavage enzyme", new DefaultComboBoxModel(CleavageRule.values()), "Spectrum", "Cleavage enzyme to use by the X!Tandem search engine."));
		this.put("protein, cleavage semi", new Parameter("Semi-tryptic cleavage", false, "Spectrum", "Use semi-tryptic cleavage as enzyme parameter."));

		// Spectrum section
		this.put("spectrum, fragment mass type", new Parameter("Fragment Mass Type", new DefaultComboBoxModel(new Object[] { "monoisotopic", "average" }), "Spectrum", "Use chemical average or monoisotopic mass for fragment ions."));
		this.put("spectrum, total peaks", new Parameter("Total Peaks", 50, "Spectrum", "Maximum number of peaks to be used from a spectrum."));
		this.put("spectrum, minimum peaks", new Parameter("Minimum Peaks", 15, "Spectrum", "The minimum number of peaks required for a spectrum to be considered."));
		this.put("spectrum, minimum parent m+h", new Parameter("Minimum precursor mass", 500.0, "Spectrum", "The minimum parent mass required for a spectrum to be considered."));
		this.put("spectrum, minimum fragment mz", new Parameter("Minimum fragment m/z", 150.0, "Spectrum", "The minimum fragment m/z to be considered."));
		this.put("spectrum, threads", new Parameter("Worker threads", 8, "Spectrum", "The number of worker threads to be used for calculation."));
		this.put("spectrum, sequence batch size", new Parameter("<html>Fasta sequence<br>batch size</html>", 1000, "Spectrum", "The number of FASTA sequences X!Tandem is processing per batch."));
		// Refinement section
		this.put("refine", new Parameter("Use first-pass search (refinement)", true, "Refinement", "Enable first-pass search (refinement)"));
		this.put("refine, spectrum synthesis", new Parameter("Use spectrum synthesis scoring", true, "Refinement", "Predict a synthetic spectrum and reward ions that agree with the predicted spectrum"));
		this.put("refine, maximum valid expectation value", new Parameter("E-value cut-off for refinement", 0.1, "Refinement", "Only hits below this threshold are considered for a second-pass search."));
		this.put("refine, point mutations", new Parameter("Allow point mutations", false, "Refinement", "Enables the use of amino acid single point mutations (using PAM matrix)."));
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
		// Set up string buffer for BIOML structure
		StringBuffer sb = new StringBuffer();

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
				String ions = "xyzabc";
				int k = 0;
				for (int i = 0; i < values.length; i++) {
					for (int j = 0; j < values[i].length; j++) {
						value = (values[i][j].booleanValue()) ? "yes" : "no";
						// Write BIOML <note> tag containing non-default value
						String label = "scoring, " + ions.charAt(k)	+ " ions";
						sb.append("\t<note type=\"input\" label=\"" + label	+ "\">" + value + "</note>\n;");
						k++;
					}
				}
			} else {
				if (value instanceof Boolean) {
					// turn true/false into yes/no
					value = (((Boolean) value).booleanValue()) ? "yes" : "no";
				}
				// Write BIOML <note> tag containing non-default value
				if (key.equals("protein, cleavage site")) {
//					value = cleavageRules.get(value.toString());
					value = ((CleavageRule) value).getRule();
				}
				sb.append("\t<note type=\"input\" label=\"" + key + "\">" + value.toString() + "</note>\n;");
			}
		}
		return sb.toString();
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