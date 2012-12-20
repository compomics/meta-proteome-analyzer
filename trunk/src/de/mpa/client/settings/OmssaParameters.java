package de.mpa.client.settings;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OmssaParameters extends ParameterMap {

	private List<Parameter> params;
	
	public OmssaParameters() {
		this.initDefaults();
	}
	
	@Override
	public void initDefaults() {
		params = new ArrayList<Parameter>();
		// Spectrum parameters.		
//		params.add(new Parameter("<html>Fragment Mass<br>Error Unit</html>", new Object[] { "Daltons", "ppm" }, "Spectrum", "Fragment monoisotopic mass error units: Daltons|ppm"));
		params.add(new Parameter("Fragment Mass Type", new Object[] { "monoisotopic", "average", "mono N15", "exact" }, "Spectrum", "<html>Monoisotopic: peptides consist entirely of carbon-12.<br>Average: use average natural isotopic mass of peptides.<br>Exact: use most abundant isotopic peak for a given mass range.</html>"));
		params.add(new Parameter("Precursor Mass Type", new Object[] { "monoisotopic", "average", "mono N15", "exact" }, "Spectrum", "<html>Monoisotopic: peptides consist entirely of carbon-12.<br>Average: use average natural isotopic mass of peptides.<br>Exact: use most abundant isotopic peak for a given mass range.</html>"));
		params.add(new Parameter("Precursor tol / charge scaling", false, "Spectrum", "Precursor mass tolerance scales according to charge state."));
		params.add(new Parameter("Most Intense Peaks", 6, "Spectrum", "Number of m/z values corresponding to the most intense peaks."));
		params.add(new Parameter("Worker threads", 0, "Spectrum", "The number of threads OMSSA is using for processing."));
		
		// Scoring parameters.
		params.add(new Parameter("<html>Required number of m/z<br>matches per spectrum</html>", 2, "Scoring parameters", "The number of m/z matches a sequence library peptide must have for the hit to the peptide to be recorded."));
		params.add(new Parameter("Maximum number of hits", 30, "Scoring parameters", "Maximum number of hits retained per precursor charge state per spectrum."));
		params.add(new Parameter("Maximum e-value allowed", 1000.00, "Scoring parameters", "Maximum e-value allowed in the hit list."));
		params.add(new Parameter("use x ions|use y ions|use z ions||use a ions|use b ions|use c ions", new Boolean[][] { { false, true, false }, { false, true, false }}, "Scoring",
				"Allows the use of a-ions in scoring.|Allows the use of b-ions in scoring.|Allows the use of c-ions in scoring.||Allows the use of x-ions in scoring.|Allows the use of y-ions in scoring.|Allows the use of z-ions in scoring."));
//		params.add(new Parameter("Use x ions in spectrum scoring", false, "Scoring parameters", "Allows peptide sequence x ions to be used in the spectrum scoring algorithm."));
//		params.add(new Parameter("Use y ions in spectrum scoring", true, "Scoring parameters", "Allows peptide sequence y ions to be used in the spectrum scoring algorithm."));
//		params.add(new Parameter("Use z ions in spectrum scoring", false, "Scoring parameters", "Allows peptide sequence z ions to be used in the spectrum scoring algorithm."));
//		params.add(new Parameter("Use a ions in spectrum scoring", false, "Scoring parameters", "Allows peptide sequence a ions to be used in the spectrum scoring algorithm."));
//		params.add(new Parameter("Use b ions in spectrum scoring", true, "Scoring parameters", "Allows peptide sequence b ions to be used in the spectrum scoring algorithm."));
//		params.add(new Parameter("Use c ions in spectrum scoring", false, "Scoring parameters", "Allows peptide sequence c ions to be used in the spectrum scoring algorithm."));
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
