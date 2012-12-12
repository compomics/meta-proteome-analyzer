package de.mpa.client.settings;

import java.util.ArrayList;
import java.util.List;

import de.mpa.client.DbSearchSettings;

public class XTandemParameters extends DbSearchSettings implements ParameterSet{
	
	private List<Parameter> params;
	
	public XTandemParameters() {
		this.setDefaults();
	}
	
	private void setDefaults() {
		params = new ArrayList<Parameter>();
		// TODO: Class type not needed anymore!
		params.add(new Parameter("<html>Fragment Mass<br>Error Unit</html>", new Object[] { "Daltons", "ppm" }, List.class, "Spectrum", "Fragment monoisotopic mass error units: Daltons|ppm"));
		params.add(new Parameter("Fragment Mass Type", new Object[] { "monoisotopic", "average" }, List.class, "Spectrum", "Fragment mass type: monoisotopic|average"));
		params.add(new Parameter("Total Peaks", 50, Integer.class, "Spectrum", " The number of most intense peaks to be taken."));
		params.add(new Parameter("Minimum Peaks", 15, Integer.class, "Spectrum", "The number of minimum peaks to be considered for the search."));
		params.add(new Parameter("Minimum precursor mass", 500.0, Double.class, "Spectrum", "The minimum parent mass."));
		params.add(new Parameter("Minimum fragment m/z", 150.0, Double.class, "Spectrum", "The minimum fragment m/z."));
		params.add(new Parameter("Worker threads", 1, Integer.class, "Spectrum", "The number of threads X!Tandem is using for processing."));
		params.add(new Parameter("<html>Fasta sequence<br>batch size</html>", 1000, Integer.class, "Spectrum", "The number of FASTA sequences X!Tandem is processing in one batch at the same time."));
		params.add(new Parameter("Use first-pass search (refinement)", true, Boolean.class, "Refinement", "Enable first-pass search (refinement)"));
		params.add(new Parameter("<html>Predict synthetic spectra<br>and return ions</html>", true, Boolean.class, "Refinement", "predict a synthetic spectrum and reward ions that agree with the predicted spectrum"));
		params.add(new Parameter("<html>E-value cut-off for<br>second-pass search</html>", 0.1, Double.class, "Refinement", "Only hits below this threshold are considered for a second-pass search."));
		params.add(new Parameter("Allow point mutations", true, Boolean.class, "Refinement", "Enables the use of amino acid single point mutations (using PAM matrix)."));
		params.add(new Parameter("<html>Minimum required number<br>of ions per spectrum</html>", 4, Integer.class, "Scoring parameters", "Minimum number of ions that a spectrum has to contain."));
		params.add(new Parameter("<html>Maximum number of<br>missed cleavages allowed</html>", 1, Integer.class, "Scoring parameters", "Maximum number of missed cleavage sites allowed."));
		params.add(new Parameter("Use x ions in spectrum scoring", false, Boolean.class, "Scoring parameters", "Allows peptide sequence x ions to be used in the spectrum scoring algorithm."));
		params.add(new Parameter("Use y ions in spectrum scoring", true, Boolean.class, "Scoring parameters", "Allows peptide sequence y ions to be used in the spectrum scoring algorithm."));
		params.add(new Parameter("Use z ions in spectrum scoring", false, Boolean.class, "Scoring parameters", "Allows peptide sequence z ions to be used in the spectrum scoring algorithm."));
		params.add(new Parameter("Use a ions in spectrum scoring", false, Boolean.class, "Scoring parameters", "Allows peptide sequence a ions to be used in the spectrum scoring algorithm."));
		params.add(new Parameter("Use b ions in spectrum scoring", true, Boolean.class, "Scoring parameters", "Allows peptide sequence b ions to be used in the spectrum scoring algorithm."));
		params.add(new Parameter("Use c ions in spectrum scoring", false, Boolean.class, "Scoring parameters", "Allows peptide sequence c ions to be used in the spectrum scoring algorithm."));
	}

	@Override
	public List<Parameter> getParameters() {
		return params;
	}
	
//	// Spectrum + Spectrum conditioning section
//	/**
//	 * Fragment monoisotopic mass error units: Daltons|ppm
//	 */
//	private String fragmentErrorMassUnit = "Daltons";
//	
//	/**
//	 * Fragment mass type: monoisotopic|average
//	 */
//	private String fragmentMassType = "monoisotopic";
//	
//	/**
//	 * The number of most intense peaks to be taken.
//	 */
//	private int totalPeaks = 50;
//	
//	/**
//	 * The number of minimum peaks to be considered for the search. 
//	 */
//	private int minPeaks = 15;
//	
//	/**
//	 * Minimum parent mass.
//	 */
//	private double minParentMass = 500;
//	
//	/**
//	 * Minimum fragment m/z.
//	 */
//	private double minFragmentMz = 150;
//	
//	/**
//	 * The number of threads X!Tandem is using for processing.
//	 */
//	private int workerThreads = 1;
//	
//	/**
//	 * The number of FASTA sequences X!Tandem is processing in one batch at the same time.
//	 */
//	private int fastaSeqBatchSize = 1000;
//	
//	
//	// Refinement section: The first-pass search settings
//	
//	/**
//	 * Use of the refinement aka. the first-pass search.
//	 */
//	private boolean useRefinement = true;
//	
//	/**
//	 * This parameter effectively predicts a "synthetic" spectrum and rewards ions that agree with the predicted spectrum.
//	 */
//	private boolean spectrumSynthesis = true;
//	
//	/**
//	 * Only hits below this threshold are considered for a second-pass search.
//	 */
//	private double maxValidEValue = 0.1;
//	
//	/**
//	 * Enables the use of amino acid single point mutations (using PAM matrix). 
//	 */
//	private boolean usePointMutations = false;
//	
//	
//	// Scoring parameters 
//	
//	/**
//	 * Minimum number of ions that a spectrum has to contain.
//	 */
//	private int minimumIonCount = 4;
//	
//	/**
//	 * Maximum number of missed cleavage sites allowed.
//	 */
//	private int maxMissedCleavagesites = 1;
//	
//	/**
//	 * Allows peptide sequence x ions to be used in the spectrum scoring algorithm.
//	 */
//	private boolean xIonsAllowed = false;
//	
//	/**
//	 * Allows peptide sequence y ions to be used in the spectrum scoring algorithm.
//	 */
//	private boolean yIonsAllowed = true;
//	
//	/**
//	 * Allows peptide sequence z ions to be used in the spectrum scoring algorithm.
//	 */
//	private boolean zIonsAllowed = false;
//	
//	/**
//	 * Allows peptide sequence a ions to be used in the spectrum scoring algorithm.
//	 */
//	private boolean aIonsAllowed = false;
//	
//	/**
//	 * Allows peptide sequence b ions to be used in the spectrum scoring algorithm.
//	 */
//	private boolean bIonsAllowed = true;
//	
//	/**
//	 * Allows peptide sequence c ions to be used in the spectrum scoring algorithm.
//	 */
//	private boolean cIonsAllowed = false;
//	
//	
//	// Output parameters
//
//	/**The value for this parameter determines which results are written to the output file 
//	 * at the end of a modelling session. The three possible values cover the most interesting 
//	 * use cases for X! TANDEM, from a bioinformatics perspective. The exact form of the reported
//	 * results will depend on the values chosen for other output parameters.
//     *  all - When this value is set, results are reported for all of the spectra 
//     *  	  that were used in the modelling session. This value is not recommended 
//     *  	  for normal use, as most spectra in a large data set will not return useful 
//     *  	  results. Storing the large volume of purely stochastic matches that can results 
//     *  	  is a waste of resources, for most users.
//     *  valid - When this value is set, results that have an expectation value less than output,
//     *  	    maximum valid expectation value are reported. This setting is of the most general 
//     *  	    use.
//     *  stochastic - When this value is set, results that have an expectation value greater than 
//     *  	  		 output, maximum valid expectation value are all reported. This is the compliment 
//     *  	  		 to the valid set, and it can be useful for debugging and bioinformatics purposes.
//	 */
//	
//	/**
//	 * When the outputResultsValue is set to valid, the value of this parameter sets 
//	 * the maximum expectation value recorded in the output file for a modelling session. 
//	 * All results with expectation values less than this value are considered to be 
//	 * statisitically significant and are recorded. In the case that outputResultsValue 
//	 * is set to stochastic, results with expectation values greater than this 
//	 * value are recorded. 
//	 */
////	private double maxValidExpectationValue = 0.1;
//
//	public String getFragmentErrorMassUnit() {
//		return fragmentErrorMassUnit;
//	}
//
//	public void setFragmentErrorMassUnit(String fragmentErrorMassUnit) {
//		this.fragmentErrorMassUnit = fragmentErrorMassUnit;
//	}
//
//	public String getFragmentMassType() {
//		return fragmentMassType;
//	}
//
//	public void setFragmentMassType(String fragmentMassType) {
//		this.fragmentMassType = fragmentMassType;
//	}
//
//	public int getTotalPeaks() {
//		return totalPeaks;
//	}
//
//	public void setTotalPeaks(int totalPeaks) {
//		this.totalPeaks = totalPeaks;
//	}
//
//	public int getMinPeaks() {
//		return minPeaks;
//	}
//
//	public void setMinPeaks(int minPeaks) {
//		this.minPeaks = minPeaks;
//	}
//
//	public double getMinParentMass() {
//		return minParentMass;
//	}
//
//	public void setMinParentMass(double minParentMass) {
//		this.minParentMass = minParentMass;
//	}
//
//	public double getMinFragmentMz() {
//		return minFragmentMz;
//	}
//
//	public void setMinFragmentMz(double minFragmentMz) {
//		this.minFragmentMz = minFragmentMz;
//	}
//
//	public int getWorkerThreads() {
//		return workerThreads;
//	}
//
//	public void setWorkerThreads(int workerThreads) {
//		this.workerThreads = workerThreads;
//	}
//
//	public int getFastaSeqBatchSize() {
//		return fastaSeqBatchSize;
//	}
//
//	public void setFastaSeqBatchSize(int fastaSeqBatchSize) {
//		this.fastaSeqBatchSize = fastaSeqBatchSize;
//	}
//
//	public boolean isUseRefinement() {
//		return useRefinement;
//	}
//
//	public void setUseRefinement(boolean useRefinement) {
//		this.useRefinement = useRefinement;
//	}
//
//	public boolean isSpectrumSynthesis() {
//		return spectrumSynthesis;
//	}
//
//	public void setSpectrumSynthesis(boolean spectrumSynthesis) {
//		this.spectrumSynthesis = spectrumSynthesis;
//	}
//
//	public double getMaxValidEValue() {
//		return maxValidEValue;
//	}
//
//	public void setMaxValidEValue(double maxValidEValue) {
//		this.maxValidEValue = maxValidEValue;
//	}
//
//	public boolean isUsePointMutations() {
//		return usePointMutations;
//	}
//
//	public void setUsePointMutations(boolean usePointMutations) {
//		this.usePointMutations = usePointMutations;
//	}
//
//	public int getMinimumIonCount() {
//		return minimumIonCount;
//	}
//
//	public void setMinimumIonCount(int minimumIonCount) {
//		this.minimumIonCount = minimumIonCount;
//	}
//
//	public int getMaxMissedCleavagesites() {
//		return maxMissedCleavagesites;
//	}
//
//	public void setMaxMissedCleavagesites(int maxMissedCleavagesites) {
//		this.maxMissedCleavagesites = maxMissedCleavagesites;
//	}
//
//	public boolean isxIonsAllowed() {
//		return xIonsAllowed;
//	}
//
//	public void setxIonsAllowed(boolean xIonsAllowed) {
//		this.xIonsAllowed = xIonsAllowed;
//	}
//
//	public boolean isyIonsAllowed() {
//		return yIonsAllowed;
//	}
//
//	public void setyIonsAllowed(boolean yIonsAllowed) {
//		this.yIonsAllowed = yIonsAllowed;
//	}
//
//	public boolean iszIonsAllowed() {
//		return zIonsAllowed;
//	}
//
//	public void setzIonsAllowed(boolean zIonsAllowed) {
//		this.zIonsAllowed = zIonsAllowed;
//	}
//
//	public boolean isaIonsAllowed() {
//		return aIonsAllowed;
//	}
//
//	public void setaIonsAllowed(boolean aIonsAllowed) {
//		this.aIonsAllowed = aIonsAllowed;
//	}
//
//	public boolean isbIonsAllowed() {
//		return bIonsAllowed;
//	}
//
//	public void setbIonsAllowed(boolean bIonsAllowed) {
//		this.bIonsAllowed = bIonsAllowed;
//	}
//
//	public boolean iscIonsAllowed() {
//		return cIonsAllowed;
//	}
//
//	public void setcIonsAllowed(boolean cIonsAllowed) {
//		this.cIonsAllowed = cIonsAllowed;
//	}
}