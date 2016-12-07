package de.mpa.client.settings;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

import de.mpa.client.settings.Parameter.NumberParameter;
import de.mpa.client.settings.Parameter.OptionParameter;

/**
 * Class for storing MS-GF+ search engine-specific settings.
 * 
 * @author Thilo Muth
 */
public class MSGFParameters extends ParameterMap {
	
	/**
	 * Constructs a parameter map initialized with default values.
	 */
	public MSGFParameters() {
		initDefaults();
	}

	@Override
	public void initDefaults() {
		// Spectrum section
		this.put("m", new OptionParameter(new Object[] { "0 (default)", "1 (CID)", "2 (ETD)", "3 (HCD)"}, 0, "FragmentMethodID", "Fragmentation method from the MS instrument.", "Spectrum"));
		this.put("inst", new OptionParameter(new Object[] { "0 (low-res LCQ/LTQ default)", "1 (high-res LTQ)", "2 (TOF)", "3 (Q-Exactive)" }, 0, "InstrumentID", "MS Instrument Type", "Spectrum"));
		this.put("protocol", new OptionParameter(new Object[] { "0 (No Protocol default)", "1 (Phoshorylation)", "2 (iTRAQ)", "3 (iTRAQPhospho)" }, 0, "ProtocolID", "MS Protocol, e.g. iTRAQ labeling", "Spectrum"));
		this.put("ntt", new NumberParameter(2, 0, 2, "Number of tolerable termini", "E.g. for trypsin, 0: non-tryptic, 1: semi-tryptic, 2: fully-tryptic peptides only. ", "Spectrum")); 
		this.put("minCharge", new NumberParameter(2, 1, 6, "MinCharge", "Minimum precursor charge to consider if charges are not specified in the spectrum file, default: 2.", "Spectrum"));
		this.put("maxCharge", new NumberParameter(3, 1, 6, "MaxCharge", "Maximum precursor charge to consider if charges are not specified in the spectrum file, default: 3.", "Spectrum"));
		this.put("minLength", new NumberParameter(6, 6, null, "MinPepLength", "Minimum peptide length to consider, default: 6.", "Spectrum"));
		this.put("maxLength", new NumberParameter(40, 6, null, "MaxPepLength", "Maximum peptide length to consider, default: 40.", "Spectrum"));
		this.put("thread", new NumberParameter(4, 1, null, "Search threads", "The number of search threads to use", "Spectrum"));
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
				// Write CLI parameter containing non-default value
				sb.append("-" + key + " " + value + " ");
			}
		}
		return sb.toString().trim();
	}

	@Override
	public File toFile(String path) throws IOException {
		return null;
	}

}
