package de.mpa.client.settings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class InspectParameters extends ParameterMap {
	
	private List<Parameter> params;
	
	public InspectParameters() {
		this.initDefaults();
	}

	@Override
	public void initDefaults() {
		params = new ArrayList<Parameter>();
		// Unrestrictive mode		
		params.add(new Parameter("Unrestrictive Mode", false, "Unrestrictive PTM Mode", "Performs MS-Alignment algorithm to perform unrestrictive search, allowing arbitrary modification masses."));
		params.add(new Parameter("Maximum PTM Size (Da)", 250,  "Unrestrictive PTM Mode", "For blind search, specifies the maximum modification size (in Da) to consider."));
		
		// Scoring parameters.
		params.add(new Parameter("Multiple precursor charge", true, "Scoring parameters", "Attempts to guess the precursor charge and mass and consider multiple charge states if feasible."));
		params.add(new Parameter("Instrument Type", new Object[] { "ESI-ION-TRAP", "QTOF", "FT-HYBRID" }, "Scoring parameters", "Instrument type model."));
		params.add(new Parameter("Tag Length", 3, "Scoring parameters", "Tag length 1-6."));
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File toFile(String path) {
		// TODO Auto-generated method stub
		return null;
	}

}
