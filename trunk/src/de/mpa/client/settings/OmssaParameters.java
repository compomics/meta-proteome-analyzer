package de.mpa.client.settings;

import java.util.ArrayList;
import java.util.List;

import de.mpa.client.DbSearchSettings;

public class OmssaParameters extends DbSearchSettings implements ParameterSet {

	private List<Parameter> params;
	
	public OmssaParameters() {
		this.setDefaults();
	}
	
	public void setDefaults() {
		params = new ArrayList<Parameter>();
		// Spectrum parameters.		
		params.add(new Parameter("<html>Fragment Mass<br>Error Unit</html>", new Object[] { "Daltons", "ppm" }, "Spectrum", "Fragment monoisotopic mass error units: Daltons|ppm"));
		params.add(new Parameter("Fragment Mass Type", new Object[] { "monoisotopic=0", "average=1", "monoisotopicN15=3" }, "Spectrum", "Fragment mass type: monoisotopic|average|monoisotopicN15"));
		params.add(new Parameter("Precursor Mass Type", new Object[] { "monoisotopic=0", "average=1", "monoisotopicN15=3" }, "Spectrum", "Fragment mass type: monoisotopic|average|monoisotopicN15"));
		params.add(new Parameter("Precursor tol / charge scaling", false, "Spectrum", "Precursor mass tolerance scales according to charge state."));
		params.add(new Parameter("Most Intense Peaks", 6, "Spectrum", "Number of m/z values corresponding to the most intense peaks."));
		params.add(new Parameter("Worker threads", 0, "Spectrum", "The number of threads OMSSA is using for processing."));
		
		// Scoring parameters.
		params.add(new Parameter("<html>Required number<br>of m/z matches per spectrum</html>", 2, "Scoring parameters", "The number of m/z matches a sequence library peptide must have for the hit to the peptide to be recorded."));
		params.add(new Parameter("Maximum number of hits", 30, "Scoring parameters", "Maximum number of hits retained per precursor charge state per spectrum."));
		params.add(new Parameter("Maximum e-value allowed", 1000.00, "Scoring parameters", "Maximum e-value allowed in the hit list."));
		params.add(new Parameter("Use x ions in spectrum scoring", false, "Scoring parameters", "Allows peptide sequence x ions to be used in the spectrum scoring algorithm."));
		params.add(new Parameter("Use y ions in spectrum scoring", true, "Scoring parameters", "Allows peptide sequence y ions to be used in the spectrum scoring algorithm."));
		params.add(new Parameter("Use z ions in spectrum scoring", false, "Scoring parameters", "Allows peptide sequence z ions to be used in the spectrum scoring algorithm."));
		params.add(new Parameter("Use a ions in spectrum scoring", false, "Scoring parameters", "Allows peptide sequence a ions to be used in the spectrum scoring algorithm."));
		params.add(new Parameter("Use b ions in spectrum scoring", true, "Scoring parameters", "Allows peptide sequence b ions to be used in the spectrum scoring algorithm."));
		params.add(new Parameter("Use c ions in spectrum scoring", false, "Scoring parameters", "Allows peptide sequence c ions to be used in the spectrum scoring algorithm."));
	}

	@Override
	public List<Parameter> getParameters() {
		return params;
	}

}
