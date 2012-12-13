package de.mpa.client.settings;

import java.util.ArrayList;
import java.util.List;

import de.mpa.client.DbSearchSettings;

public class CruxParameters extends DbSearchSettings implements ParameterSet {
	
	private List<Parameter> params;
	
	public CruxParameters() {
		this.setDefaults();
	}
	
	public void setDefaults() {
		params = new ArrayList<Parameter>();
		// Spectrum parameters.		
		params.add(new Parameter("Precursor Window Type", new Object[] { "mass", "m/z", "ppm" }, List.class, "Spectrum", "Precursor window type: mass|m/z|ppm"));
		params.add(new Parameter("Fragment Mass Type", new Object[] { "monoisotopic", "average" }, List.class, "Spectrum", "Fragment mass type: monoisotopic|average|monoisotopicN15"));
		params.add(new Parameter("Minimum Peaks", 20, Integer.class, "Spectrum", "Minimum number of peaks a spectrum must have for it to be searched."));
		
		// Scoring parameters.
		params.add(new Parameter("Minimum peptide length", 7, Integer.class, "Scoring parameters", "The minimum length of peptides to consider. Default=6"));
		params.add(new Parameter("Maximum peptide length", 50, Integer.class, "Scoring parameters", "The maximum length of peptides to consider. Default=50"));
	}

	@Override
	public List<Parameter> getParameters() {
		return params;
	}


}
