package de.mpa.client.settings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import de.mpa.client.settings.Parameter.BooleanParameter;

/**
 * Class for storing X!Tandem search engine-specific settings.
 * 
 * @author T. Muth, F. Kohrs, A. Behne
 */
@SuppressWarnings("serial")
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
		private final String name;
		
		/**
		 * The rule string.
		 */
		private final String rule;

		/**
		 * Constructs a cleavage rule from the specified name and rule strings.
		 * @param name the name of the rule
		 * @param rule the rule string
		 */
        CleavageRule(String name, String rule) {
			this.name = name;
			this.rule = rule;
		}
		
		/**
		 * Returns the rule string.
		 * @return the rule string
		 */
		public String getRule() {
			return rule;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}

	/**
	 * Constructs a parameter map initialized with default values.
	 */
	public XTandemParameters() {
        initDefaults();
	}

	@Override
	public void initDefaults() {
		/* Configurable settings */
		// Protein section
        put("protein, cleavage site", new Parameter.OptionParameter(CleavageRule.values(), 0, "Cleavage enzyme", "Cleavage enzyme to be used by the search engine.", "Protein"));
		this.put("protein, cleavage semi", new BooleanParameter(false, "Semi-tryptic cleavage", "Use semi-tryptic cleavage as enzyme parameter.", "Protein"));

		// Spectrum section
		this.put("spectrum, fragment mass type", new Parameter.OptionParameter(new Object[] { "monoisotopic", "average" }, 0, "Fragment mass type", "Use chemical average or monoisotopic mass for fragment ions.", "Spectrum"));
        put("spectrum, total peaks", new Parameter.NumberParameter(50, 1, null, "Total peaks", "Maximum number of peaks to be used from a spectrum.", "Spectrum"));
		this.put("spectrum, minimum peaks", new Parameter.NumberParameter(15, 1, null, "Minimum peaks", "The minimum number of peaks required for a spectrum to be considered.", "Spectrum"));
		this.put("spectrum, minimum parent m+h", new Parameter.NumberParameter(500.0, 0.0, null, "Minimum precursor mass", "The minimum parent mass required for a spectrum to be considered.", "Spectrum"));
		this.put("spectrum, minimum fragment mz", new Parameter.NumberParameter(150.0, 0.0, null, "Minimum fragment m/z", "The minimum fragment m/z to be considered.", "Spectrum"));
		this.put("spectrum, threads", new Parameter.NumberParameter(8, 1, null, "Worker threads", "The number of worker threads to be used for calculation.", "Spectrum"));
		this.put("spectrum, sequence batch size", new Parameter.NumberParameter(1000, 1, null, "<html>Fasta sequence<br>batch size</html>", "The number of FASTA sequences that are processed per batch.", "Spectrum"));
		// Refinement section
		this.put("refine", new BooleanParameter(true, "Use first-pass search (refinement)", "Enable first-pass search (refinement)", "Refinement"));
		this.put("refine, spectrum synthesis", new BooleanParameter(true, "Use spectrum synthesis scoring", "Predict a synthetic spectrum and reward ions that agree with the prediction.", "Refinement"));
		this.put("refine, maximum valid expectation value", new Parameter.NumberParameter(0.1, 0.0, null, "E-value cut-off for refinement", "Only hits below this threshold are considered for a second-pass search.", "Refinement"));
		this.put("refine, point mutations", new BooleanParameter(false, "Allow point mutations", "Enables the use of amino acid single point mutations (using PAM matrix).", "Refinement"));
		// Scoring section
		this.put("scoring, minimum ion count", new Parameter.NumberParameter(4, 1, null, "Minimum number of ions required", "The minimum number of ions required for a peptide to be scored.", "Scoring"));
        put("scoring, ions", new Parameter.BooleanMatrixParameter(3, 2, new Boolean[] { false, true, false, false, true, false },
				new String[] { "use a ions", "use b ions", "use c ions", "use x ions", "use y ions", "use z ions" },
				new String[] { "Allows the use of a ions in scoring.", "Allows the use of b ions in scoring.", "Allows the use of c ions in scoring.",
							   "Allows the use of x ions in scoring.", "Allows the use of y ions in scoring.", "Allows the use of z ions in scoring." }, "Scoring"));
	}

//	/**
//	 * Returns <code>null</code> as X!Tandem does not use command-line
//	 * parameters to specify single settings. Refer to the
//	 * {@link #toFile(String path) toFile()} method.
//	 */
	@Override
	public String toString() {
		// Set up string buffer for BIOML structure
		StringBuffer sb = new StringBuffer();

		// Iterate stored parameter values and compare them to the defaults
		for (Map.Entry<String, Parameter> entry : this.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue().getValue();
			// Compare values, if they differ add a line to the configuration file
			if (value instanceof Boolean[]) {
				Boolean[] selections = (Boolean[]) value;
				char[] ionOptions = { 'a', 'b', 'c', 'x', 'y', 'z' };
				for (int i = 0; i < selections.length; i++) {
					String label = "scoring, " + ionOptions[i]	+ " ions";
					// Turn true/false into yes/no
					value = (selections[i]) ? "yes" : "no";
					sb.append("\t<note type=\"input\" label=\"" + label	+ "\">" + value.toString() + "</note>\n;");
				}
			} else {
				if (value instanceof Boolean) {
					// turn true/false into yes/no
					value = ((Boolean) value) ? "yes" : "no";
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
		for (Map.Entry<String, Parameter> entry : entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue().getValue();
			Object defaultValue = defaults.get(key).getValue();
			// Compare values, if they differ add a line to the configuration file
			if (value instanceof Boolean[]) {
				Boolean[] selections = (Boolean[]) value;
				Boolean[] defaultSelections = (Boolean[]) defaultValue;
				char[] ionOptions = { 'a', 'b', 'c', 'x', 'y', 'z' };
				for (int i = 0; i < selections.length; i++) {
					if (!selections[i].equals(defaultSelections[i])) {
						String label = "scoring, " + ionOptions[i]	+ " ions";
						// Turn true/false into yes/no
						value = (selections[i]) ? "yes" : "no";
						bw.append("\t<note type=\"input\" label=\"" + label + "\">" + value + "</note>");
						bw.newLine();
					}
				}
			} else if (!value.equals(defaultValue)) {
				if (value instanceof Boolean) {
					// Turn true/false into yes/no
					value = ((Boolean) value) ? "yes" : "no";
					defaultValue = ((Boolean) defaultValue) ? "yes" : "no";
				}
				if (!value.equals(defaultValue)) {
					// Write BIOML <note> tag containing non-default value
					bw.append("\t<note type=\"input\" label=\"" + key + "\">" + value + "</note>");
					bw.newLine();
				}
			}
		}
		
		bw.append("</bioml>");
		
		// Close writer
		bw.flush();
		bw.close();
		
		return file;
	}
	
}