package de.mpa.client.settings;

import java.io.File;
import java.util.Map.Entry;

import de.mpa.client.settings.Parameter.BooleanMatrixParameter;
import de.mpa.client.settings.Parameter.NumberParameter;
import de.mpa.client.settings.Parameter.OptionParameter;

/**
 * Class for storing OMSSA search engine-specific settings.
 * 
 * @author T. Muth, F. Kohrs, A. Behne
 */
public class OmssaParameters extends ParameterMap {
	
	/**
	 * Constructs a parameter map initialized with default values.
	 */
	public OmssaParameters() {
		this.initDefaults();
	}
	
	@Override
	public void initDefaults() {
		/* Configurable settings */
		// Protein section
		this.put("e", new OptionParameter(new Object[] { "Trypsin", "Arg-C", "CNBr", "Chymotrypsin", "Formic Acid", "Lys-C", "Lys-C, no P rule", "Pepsin A", "Trypsin + CNBr", "Trypsin + Chymotrypsin", "Trypsin, no P rule", "Whole Protein", "Asp-N", "Glu-C", "Asp-N + Glu-C", "Top-Down", "Semi-Tryptic", "No Enzyme", "Chymotrypsin, no P rule", "Asp-N", "Glu-C", "Lys-N", "Thermolysin, no P rule", "Semi-Chymotrypsin", "Semi-Glu-C"},
				0, "Cleavage enzyme", "Cleavage enzyme to be used by the search engine.", "Protein"));
		// Spectrum section
		this.put("tom", new OptionParameter(new Object[] { "monoisotopic", "average", "mono N15", "exact" },
				0, "Fragment mass type", "<html>Monoisotopic: peptides consist entirely of carbon-12.<br>Average: use average natural isotopic mass of peptides.<br>Exact: use most abundant isotopic peak for a given mass range.</html>", "Spectrum"));
		this.put("tem", new OptionParameter(new Object[] { "monoisotopic", "average", "mono N15", "exact" },
				0, "Precursor mass type", "<html>Monoisotopic: peptides consist entirely of carbon-12.<br>Average: use average natural isotopic mass of peptides.<br>Exact: use most abundant isotopic peak for a given mass range.</html>", "Spectrum"));
		this.put("zt", new NumberParameter(3, 0, null, "Charge threshold", "Minimum precursor charge to start considering multiply charged products.", "Spectrum")); 
		this.put("ht", new NumberParameter(6, 1, null, "Most intense peaks", "The number of m/z values corresponding to the most intense peaks that must include one match to the theoretical peptide.", "Spectrum"));
		this.put("nt", new NumberParameter(8, 0, null, "Search threads", "The number of search threads to use, 0=autodetect.", "Spectrum"));
		// Scoring section
		this.put("hm", new NumberParameter(2, 1, null, "m/z matches per spectrum", "The number of m/z matches a sequence library peptide must have for the hit to the peptide to be recorded.", "Scoring"));
		this.put("hl", new NumberParameter(30, 1, null, "Maximum number of hits", "The maximum number of hits retained per precursor charge state per spectrum.", "Scoring"));
		this.put("he", new NumberParameter(1000.0, 0.0, null, "Maximum e-value allowed", "Maximum e-value allowed in the hit list.", "Scoring"));
		this.put("i", new BooleanMatrixParameter(3, 3, new Boolean[] { false, true, false, false, true, false, false, false, false },
				new String[] { "use a ions", "use b ions", "use c ions", "use x ions", "use y ions", "use zdot ions", "use adot ions", "<html>use x-CO<sub>2</sub> ions</html>", "<html>use adot-CO<sub>2</sub> ions</html>" },
				new String[] { "Allows the use of a ions in scoring.", "Allows the use of b ions in scoring.", "Allows the use of c ions in scoring.",
							   "Allows the use of x ions in scoring.", "Allows the use of y ions in scoring.", "Allows the use of zdot ions in scoring.",
							   "Allows the use of adot ions in scoring.", "<html>Allows the use of x-CO<sub>2</sub> ions in scoring.</html>", "<html>Allows the use of adot-CO<sub>2</sub> ions in scoring.</html>" }, "Scoring"));
	}

	@Override
	public String toString() {
		// Set up builder for command line argument structure
		StringBuilder sb = new StringBuilder();

		// Iterate stored parameter values and compare them to the defaults
		for (Entry<String, Parameter> entry : this.entrySet()) {
			String key = entry.getKey();
			Parameter param = entry.getValue();
			Object value = param.getValue();
			if (param instanceof OptionParameter) {
				value = ((OptionParameter) param).getIndex();
			}
			if (value instanceof Boolean[]) {
				Boolean[] selections = (Boolean[]) value;
				int[] ionOptions = { 0, 1, 2, 3, 4, 5, 10, 11, 12 };
				boolean first = true;
				for (int i = 0; i < selections.length; i++) {
					if (selections[i]) {
						if (first) {
							sb.append("-" + key + " ");
							first = false;
						} else {
							sb.append(",");
						}
						sb.append(ionOptions[i]);
					}
				}
				sb.append(" ");
			} else {
				if (value instanceof Boolean) {
					// turn true/false into 1/0
					value = ((Boolean) value) ? 1 : 0;
				}
				// Write commandline parameter containing non-default value
				sb.append("-" + key + " " + value + " ");
			}
		}
		return sb.toString().trim();
	}

	@Override
	public File toFile(String path) {
		// TODO: find template for OMSSA xml input
		return null;
	}

}