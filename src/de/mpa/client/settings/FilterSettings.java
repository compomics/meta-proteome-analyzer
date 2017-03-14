package de.mpa.client.settings;

/**
 * Class for storing spectrum filtering-related variables and methods.
 * 
 * @author A. Behne
 */
public class FilterSettings {

	/**
	 * The minimum peak count.
	 */
	private int minPeaks;
	
	/**
	 * The minimum total ion current.
	 */
	private double minTIC;
	
	/**
	 * The minimum signal-to-noise ratio.
	 */
	private double minSNR;
	
	/**
	 * The intensity level below which peaks are considered to be noise.
	 */
	private double noiseLvl;

	/**
	 * Creates a filter settings object from the specified minimum peak count,
	 * minimum total ion current value, the minimum signal-to-noise ratio and
	 * the noise threshold.
	 * @param minPeaks the minimum peak count
	 * @param minTIC the minimum total ion current
	 * @param minSNR the minimum signal-to-noise ratio
	 * @param noiseLvl the intensity level below which peaks are considered to be noise
	 */
	public FilterSettings(int minPeaks, double minTIC, double minSNR, double noiseLvl) {
		this.minPeaks = minPeaks;
		this.minTIC = minTIC;
		this.minSNR = minSNR;
		this.noiseLvl = noiseLvl;
	}
	
	/**
	 * Returns the minimum peak count
	 * @return the minimum peak count
	 */
	public int getMinPeaks() {
		return this.minPeaks;
	}
	
	/**
	 * Sets the minimum peak count
	 * @param minPeaks the minimum peak count
	 */
	public void setMinPeaks(int minPeaks) {
		this.minPeaks = minPeaks;
	}
	
	/**
	 * Returns the minimum total ion current
	 * @return the minimum total ion current
	 */
	public double getMinTIC() {
		return this.minTIC;
	}
	
	/**
	 * Sets the minimum total ion current
	 * @param minTIC the minimum total ion current
	 */
	public void setMinTIC(double minTIC) {
		this.minTIC = minTIC;
	}
	
	/**
	 * Returns the minimum signal-to-noise ratio.
	 * @return the minimum signal-to-noise ratio.
	 */
	public double getMinSNR() {
		return this.minSNR;
	}
	
	/**
	 * Sets the minimum signal-to-noise ratio.
	 * @param minSNR the minimum signal-to-noise ratio.
	 */
	public void setMinSNR(double minSNR) {
		this.minSNR = minSNR;
	}
	
	/**
	 * Returns the intensity level below which peaks are considered to be noise.
	 * @return the noise threshold
	 */
	public double getNoiseLvl() {
		return this.noiseLvl;
	}
	
	/**
	 * Sets the intensity level below which peaks are considered to be noise.
	 * @param noiseLvl the noise threshold
	 */
	public void setNoiseLvl(double noiseLvl) {
		this.noiseLvl = noiseLvl;
	}
	
	/**
	 * Returns whether the specified values meet the stored filter criteria.
	 * @param peaks the peak count
	 * @param tic the total ion current
	 * @param snr the signal-to-noise ratio
	 * @return <code>true</code> if the stored criteria are met, <code>false</code> otherwise.
	 */
	public boolean matches(int peaks, double tic, double snr) {
		return (peaks >= this.minPeaks) && (tic >= this.minTIC) && (snr >= this.minSNR);
	}
	
}
