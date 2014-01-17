package de.mpa.algorithms.fragmentation;


public class SpectrumPeak implements IPeak {
	
	private int iId;
	
	/**
	 * This double holds the m/z.
	 */
	private double iMz;
	/**
	 * This double holds the intensity.
	 */
	private double iIntensity;
	/**
	 * This Integer holds the charge.
	 */
	private int iCharge;

	/**
	 * Empty constructor for a spectrum peak.
	 */
	public SpectrumPeak() {
	}
	
	/**
	 * Constructor gets the m/z and the intensity.
	 * 
	 * @param aMz
	 * @param aIntensity
	 * 
	 */
	public SpectrumPeak(double aMz, double aIntensity) {
		iMz = aMz;
		iIntensity = aIntensity;
	}
	
	/**
	 * Constructor gets the m/z, the intensity and the charge.
	 * 
	 * @param aId
	 * @param aMz
	 * @param aIntensity
	 * 
	 */
	public SpectrumPeak(int aId, double aMz, double aIntensity) {
		iId = aId;
		iMz = aMz;
		iIntensity = aIntensity;
	}

	/**
	 * Constructor gets the m/z, the intensity and the charge.
	 * 
	 * @param aMz
	 * @param aIntensity
	 * @param aCharge
	 */
	public SpectrumPeak(double aMz, double aIntensity, int aCharge) {
		iMz = aMz;
		iIntensity = aIntensity;
		iCharge = aCharge;
	}

	
	public int getId() {
		return iId;
	}

	public void setId(int iId) {
		this.iId = iId;
	}

	/**
	 * Sets the charge.
	 * 
	 * @param aCharge
	 */
	public void setCharge(int aCharge) {
		iCharge = aCharge;
	}

	/**
	 * Returns the charge.
	 * 
	 * @return the charge
	 */
	public int getCharge() {
		return iCharge;
	}

	/**
	 * Sets the intensity.
	 * 
	 * @param aIntensity
	 */
	public void setIntensity(double aIntensity) {
		iIntensity = aIntensity;
	}

	/**
	 * Returns the intensity.
	 * 
	 * @return the intensity
	 */
	public double getIntensity() {
		return iIntensity;
	}

	/**
	 * Sets the m/z.
	 * 
	 * @param aMz
	 */
	public void setMz(double aMz) {
		iMz = aMz;
	}

	/**
	 * Returns the m/z.
	 * 
	 * @return the m/z
	 */
	public double getMZ() {
		return iMz;
	}

}
