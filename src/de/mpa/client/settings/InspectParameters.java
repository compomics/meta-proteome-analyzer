package de.mpa.client.settings;

import java.util.ArrayList;
import java.util.List;

import de.mpa.client.DbSearchSettings;

public class InspectParameters extends DbSearchSettings implements ParameterSet{
	private List<Parameter> params;
	
	public InspectParameters() {
		this.setDefaults();
	}
	
	public void setDefaults() {
		params = new ArrayList<Parameter>();
		// Unrestrictive mode		
		params.add(new Parameter("Unrestrictive Mode", false, Boolean.class, "Unrestrive PTM Mode", "Performs MS-Alignment algorithm to perform unrestrictive search, allowing arbitrary modification masses."));
		params.add(new Parameter("Maximum PTM Size (Da)", 250, Integer.class,  "Unrestrive PTM Mode", "For blind search, specifies the maximum modification size (in Da) to consider. Default=250 Da."));
		
		// Scoring parameters.
		params.add(new Parameter("Multiple precursor charge", true, Boolean.class, "Scoring parameters", "Attempts to guess the precursor charge and mass and consider multiple charge states if feasible."));
		params.add(new Parameter("Instrument Type", new Object[] { "ESI-ION-TRAP", "QTOF", "FT-HYBRID" }, List.class, "Scoring parameters", "Instrument type model."));
		params.add(new Parameter("Tag Length (1-6)", 3, Integer.class, "Scoring parameters", "Tag length 1-6."));
	}

	@Override
	public List<Parameter> getParameters() {
		return params;
	}
}
