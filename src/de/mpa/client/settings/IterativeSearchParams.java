package de.mpa.client.settings;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

import de.mpa.client.settings.Parameter.OptionParameter;

public class IterativeSearchParams extends ParameterMap {
	/**
	 * Initializing the iterative search parameters.
	 */
	public IterativeSearchParams() {
		this.initDefaults();
	}
	@Override
	public void initDefaults() {
		this.put("method", new OptionParameter(new Object[] {"Protein-based", "Taxon-based"}, 0, "Database Reduction Method", "Database search space reduction based on protein or taxonomy.", "First search round"));
	}

	@Override
	public File toFile(String path) throws IOException {
		return null;
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


}
