package de.mpa.client.settings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map.Entry;

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
		// TODO: Idea: parse X!Tandem default_input.xml instead of hard-coding
		this.put("spectrum, fragment mass error units", new Parameter("<html>Fragment Mass Error Unit</html>", new Object[] { "Daltons", "ppm" }, "Spectrum", "Units for fragment ion mass tolerance (chemical average mass)."));
		this.put("spectrum, fragment mass type", new Parameter("Fragment Mass Type", new Object[] { "monoisotopic", "average" }, "Spectrum", "Use chemical average or monoisotopic mass for fragment ions."));
		this.put("spectrum, total peaks", new Parameter("Total Peaks", 50, "Spectrum", "Maximum number of peaks to be used from a spectrum."));
		this.put("spectrum, minimum peaks", new Parameter("Minimum Peaks", 15, "Spectrum", "The minimum number of peaks required for a spectrum to be considered."));
		this.put("spectrum, minimum parent m+h", new Parameter("Minimum precursor mass", 500.0, "Spectrum", "The minimum parent mass required for a spectrum to be considered."));
		this.put("spectrum, minimum fragment mz", new Parameter("Minimum fragment m/z", 150.0, "Spectrum", "The minimum fragment m/z to be considered.."));
		this.put("spectrum, threads", new Parameter("Worker threads", 1, "Spectrum", "The number of worker threads to be used for calculation."));
		this.put("spectrum, sequence batch size", new Parameter("<html>Fasta sequence<br>batch size</html>", 1000, "Spectrum", "The number of FASTA sequences X!Tandem is processing per batch."));
		this.put("refine", new Parameter("Use first-pass search (refinement)", true, "Refinement", "Enable first-pass search (refinement)"));
		this.put("refine, spectrum synthesis", new Parameter("Use spectrum synthesis scoring", true, "Refinement", "Predict a synthetic spectrum and reward ions that agree with the predicted spectrum"));
		this.put("refine, maximum valid expectation value", new Parameter("E-value cut-off for refinement", 0.1, "Refinement", "Only hits below this threshold are considered for a second-pass search."));
		this.put("refine, point mutations", new Parameter("Allow point mutations", true, "Refinement", "Enables the use of amino acid single point mutations (using PAM matrix)."));
		this.put("scoring, minimum ion count", new Parameter("Minimum number of ions required", 4, "Scoring", "The minimum number of ions required for a peptide to be scored."));
//		this.put("scoring, ions", new Parameter("use x ions|use y ions|use z ions||use a ions|use b ions|use c ions", new Boolean[][] { { false, true, false }, { false, true, false }}, "Scoring",
//				"Allows the use of a-ions in scoring.|Allows the use of b-ions in scoring.|Allows the use of c-ions in scoring.||Allows the use of x-ions in scoring.|Allows the use of y-ions in scoring.|Allows the use of z-ions in scoring."));
		this.put("scoring, x ions", new Parameter("Use x ions in spectrum scoring", false, "Scoring", "Allows the use of x-ions in scoring."));
		this.put("scoring, y ions", new Parameter("Use y ions in spectrum scoring", true, "Scoring", "Allows the use of y-ions in scoring."));
		this.put("scoring, z ions", new Parameter("Use z ions in spectrum scoring", false, "Scoring", "Allows the use of z-ions in scoring."));
		this.put("scoring, a ions", new Parameter("Use a ions in spectrum scoring", false, "Scoring", "Allows the use of a-ions in scoring."));
		this.put("scoring, b ions", new Parameter("Use b ions in spectrum scoring", true, "Scoring", "Allows the use of b-ions in scoring."));
		this.put("scoring, c ions", new Parameter("Use c ions in spectrum scoring", false, "Scoring", "Allows the use of c-ions in scoring."));
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
	 * @param path the path string pointing to the directory the file shall be created in
	 * @return the parameter file
	 * @throws IOException if the file could not be written
	 */
	@Override
	public File toFile(String path) throws IOException {
		if (!path.endsWith(".xml")) {
			path += ".xml";
		}
		
		File file = new File(path);
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		bw.append("<bioml>");
		bw.newLine();
		
		XTandemParameters defaults = new XTandemParameters();
		for (Entry<String, Parameter> entry : this.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue().getValue();
			Object defaultValue = defaults.get(key).getValue();
			if (!value.equals(defaultValue)) {
				// TODO: find proper way to handle Object[] values
				if (value instanceof Boolean) {
					value = (((Boolean) value).booleanValue()) ? "yes" : "no";
				}
				bw.append("\t<note type=\"input\" label=\"" + key + "\">" + value + "</note>");
				bw.newLine();
			}
		}
		
		bw.append("</bioml>");
		bw.newLine();
		
		bw.flush();
		bw.close();
		
		return file;
	}
	
}