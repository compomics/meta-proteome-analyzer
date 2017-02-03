package de.mpa.client.settings;

import java.io.File;
import java.util.Map.Entry;

import de.mpa.client.settings.Parameter.NumberParameter;
import de.mpa.client.settings.Parameter.OptionParameter;

/**
 * Class for storing Comet search engine-specific settings.
 * 
 * @author Thilo Muth
 */
public class CometParameters extends ParameterMap {
	
	/**
	 * Constructs a parameter map initialized with default values.
	 */
	public CometParameters() {
		this.initDefaults();
	}
	
	@Override
	public void initDefaults() {
		// Spectrum section
		this.put("search_enzyme_number", new OptionParameter(new Object[] { "0 (No enzyme)", "1 (Trypsin)", "2 (Trypsin/P)", "3 (Lys_C)", "4 (Lys_N)", "5 (Arg_C)", "6 (Asp_N)", "7 (CNBr)", "8 (Glu_C)"}, 1, "Cleavage enzyme", "Cleavage enzyme to be used by the search engine.", "Protein"));
		this.put("minimum_peaks", new NumberParameter(10, 0, null, "MinimumPeaks", "Required minimum number of peaks in spectrum to search (default 10)", "Spectrum")); 
		this.put("minimum_intensity", new NumberParameter(0, 0, null, "MinimumIntensity", "Minimum intensity value to read in", "Spectrum"));
		this.put("remove_precursor_peak", new OptionParameter(new Object[] { "0 (No)", "1 (Yes)", "2 (All charge reduced precursor peaks)"}, 0, "RemovePrecursorPeak", "Removes the precursor peak from the spectrum.", "Spectrum"));
		this.put("thread", new NumberParameter(0, 0, null, "Search threads", "The number of search threads to use: 0 = auto-poll", "Spectrum"));
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
				sb.append(value + ";");
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