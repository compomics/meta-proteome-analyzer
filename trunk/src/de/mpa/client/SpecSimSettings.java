package de.mpa.client;

import de.mpa.interfaces.SpectrumComparator;

/**
 * Class to store spectral similarity search settings.
 * 
 * @author Alexander Behne
 */
public class SpecSimSettings {
	
	/**
	 * The precursor ion mass tolerance window.
	 */
	private double tolMz;
	
	/**
	 * A flag determining whether only annotated spectra are considered in database searching.
	 */
	private boolean annotatedOnly;
	
	/**
	 * An experiment's database entry's id used as filter criterion for database searching.
	 */
	private long experimentID;
	
	/**
	 * The number of most intensive peaks that shall be used for spectral comparison.
	 */
	private int pickCount;
	
	/**
	 * The general method of spectral similarity calculation.
	 */
	private SpectrumComparator specComp;
	
	/**
	 * The threshold below which scored results are denoted as negative hits.
	 */
	private double threshScore;

	/**
	 * Class constructor.
	 * @param tolMz
	 * @param annotatedOnly
	 * @param experimentID
	 * @param pickCount
	 * @param specComp
	 * @param threshScore
	 */
	public SpecSimSettings(double tolMz, boolean annotatedOnly, long experimentID, int pickCount,
						   SpectrumComparator specComp, double threshScore) {
		this.tolMz = tolMz;
		this.annotatedOnly = annotatedOnly;
		this.experimentID = experimentID;
		this.pickCount = pickCount;
		this.specComp = specComp;
		this.threshScore = threshScore;
	}

	/**
	 * @return The precursor ion mass tolerance.
	 */
	public double getTolMz() {
		return tolMz;
	}

	/**
	 * @return The flag denoting whether only annotated spectra will be searched.
	 */
	public boolean isAnnotatedOnly() {
		return annotatedOnly;
	}

	/**
	 * @return The experiment id.
	 */
	public long getExperimentID() {
		return experimentID;
	}

	/**
	 * @return The amount of most intensive peaks.
	 */
	public int getPickCount() {
		return pickCount;
	}

	/**
	 * @return The method of spectral comparison.
	 */
	public SpectrumComparator getSpecComparator() {
		return specComp;
	}

	/**
	 * @param specComp the specComp to set
	 */
	public void setSpecComparator(SpectrumComparator specComp) {
		this.specComp = specComp;
	}

	/**
	 * @return The score threshold.
	 */
	public double getThreshScore() {
		return threshScore;
	}
}
