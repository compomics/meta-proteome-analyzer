package de.mpa.client.settings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CruxParameters extends ParameterMap {
	
	private List<Parameter> params;
	
	public CruxParameters() {
		this.initDefaults();
	}
	
	@Override
	public void initDefaults() {
		params = new ArrayList<Parameter>();
		// Spectrum parameters.		
		params.add(new Parameter("Precursor Window Type", new Object[] { "mass", "m/z", "ppm" }, "Spectrum", "The units for the window that is used to select peptides around the precursor mass location."));
		params.add(new Parameter("Fragment Mass Type", new Object[] { "monoisotopic", "average" }, "Spectrum", "Which isotopes to use in calculating fragment ion mass."));
		params.add(new Parameter("Minimum Peaks", 20, "Spectrum", "Minimum number of peaks a spectrum must have for it to be searched."));
		
		// Scoring parameters.
		params.add(new Parameter("Minimum peptide length", 7, "Scoring parameters", "The minimum length of peptides to consider."));
		params.add(new Parameter("Maximum peptide length", 50, "Scoring parameters", "The maximum length of peptides to consider."));
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
