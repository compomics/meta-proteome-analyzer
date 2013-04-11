package de.mpa.client.settings;

import java.io.File;
import java.util.Map.Entry;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;

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
		/* Non-configurable settings */
		this.put("te", new Parameter(null, 1.0, "General", null));	// precursor tolerance
		this.put("to", new Parameter(null, 0.5, "General", null));	// fragment tolerance
		this.put("v", new Parameter(null, 2, "General", null));		// missed cleavages
		this.put("e", new Parameter(null, 0, "General", null));		// protease
		
		/* Configurable settings */
		// Spectrum section
//		this.put(null, new Parameter("<html>Fragment Mass<br>Error Unit</html>", new Object[] { "Daltons", "ppm" }, "Spectrum", "Fragment monoisotopic mass error units: Daltons|ppm"));
		this.put("tom", new Parameter("Fragment mass type", new DefaultComboBoxModel(new Object[] { "monoisotopic", "average", "mono N15", "exact" }), "Spectrum", "<html>Monoisotopic: peptides consist entirely of carbon-12.<br>Average: use average natural isotopic mass of peptides.<br>Exact: use most abundant isotopic peak for a given mass range.</html>"));
		this.put("tem", new Parameter("Precursor mass type", new DefaultComboBoxModel(new Object[] { "monoisotopic", "average", "mono N15", "exact" }), "Spectrum", "<html>Monoisotopic: peptides consist entirely of carbon-12.<br>Average: use average natural isotopic mass of peptides.<br>Exact: use most abundant isotopic peak for a given mass range.</html>"));
		this.put("zt", new Parameter("Scale precursor tolerance to charge", false, "Spectrum", "Precursor mass tolerance scales according to charge state.")); // TODO: maybe use Integer value here to be closer to what the commandline parameter actually expects
		this.put("ht", new Parameter("Most intense peaks", 6, "Spectrum", "Number of m/z values corresponding to the most intense peaks."));
		this.put("nt", new Parameter("Worker threads", 0, "Spectrum", "The number of threads OMSSA is using for processing."));
		// Scoring section
		this.put("hm", new Parameter("<html>Required number of m/z<br>matches per spectrum</html>", 2, "Scoring", "The number of m/z matches a sequence library peptide must have for the hit to the peptide to be recorded."));
		this.put("hl", new Parameter("Maximum number of hits", 30, "Scoring", "Maximum number of hits retained per precursor charge state per spectrum."));
		this.put("he", new Parameter("Maximum e-value allowed", 1000.00, "Scoring", "Maximum e-value allowed in the hit list."));
		this.put("i", new Parameter("use x ions|use y ions|use z ions||use a ions|use b ions|use c ions", new Boolean[][] { { false, true, false }, { false, true, false }}, "Scoring",
				"Allows the use of x-ions in scoring.|Allows the use of y-ions in scoring.|Allows the use of z-ions in scoring.||Allows the use of a-ions in scoring.|Allows the use of b-ions in scoring.|Allows the use of c-ions in scoring."));
	}

	@Override
	public String toString() {
		// Set up builder for command line argument structure
		StringBuilder sb = new StringBuilder();

		// Grab a set of default parameters for comparison purposes
		OmssaParameters defaults = new OmssaParameters();
		// Iterate stored parameter values and compare them to the defaults
		for (Entry<String, Parameter> entry : this.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue().getValue();
			Object defaultValue = defaults.get(key).getValue();
			if (value instanceof ComboBoxModel) {
				// special case for combobox models, compare selected items
				DefaultComboBoxModel model = (DefaultComboBoxModel) value;
				value = model.getIndexOf(model.getSelectedItem());
				model = (DefaultComboBoxModel) defaultValue;
				defaultValue = model.getIndexOf(model.getSelectedItem());
			}
			// Compare values, if they differ add a line to the configuration file
			if (value instanceof Boolean[][]) {
				// special case for matrix of checkboxes
				Boolean[][] values = (Boolean[][]) value;
				Boolean[][] defaultvalues = (Boolean[][]) defaultValue;
				String ions = "345012";
				int k = 0;
				boolean first = true;
				for (int i = 0; i < values.length; i++) {
					for (int j = 0; j < values[i].length; j++) {
						if (!values[i][j].equals(defaultvalues[i][j])) {
							if (first) {
								// Write commandline parameter
								sb.append("-" + key + " ");
								first = false;
							} else {
								sb.append(",");
							}
							// Write non-default value
							sb.append(ions.charAt(k));
						}
						k++;
					}
				}
			} else if (!value.equals(defaultValue)) {
				if (value instanceof Boolean) {
					// turn true/false into yes/no
					value = (((Boolean) value).booleanValue()) ? 1 : 0;
				}
				// Write commandline parameter containing non-default value
				sb.append("-" + key + " " + value + " ");
			}
		}
		return sb.toString();
	}

	@Override
	public File toFile(String path) {
		// TODO: find template for OMSSA xml input
		return null;
	}

}