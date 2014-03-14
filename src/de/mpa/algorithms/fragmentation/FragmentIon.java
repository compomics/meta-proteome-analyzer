package de.mpa.algorithms.fragmentation;


import java.util.List;

/**
 * This class holds the fragment ion and is an implementation of Ion.
 *
 * @author Thilo Muth
 */
public class FragmentIon implements Ion {

    /**
     * The m/z of the frament ion.
     */
    private double iMz;
    
    /**
     * The intensity of the fragment ion.
     */
    private double iIntensity;
    
    /**
     * The number of the fragment ion. For example: b1 ion has the number 1, b4 has number 4 etc.
     */
    private int iNumber;
    
    /**
     * The score of the fragment ion.
     */
    private int iScore;
    
    /**
     * The type of this ion as defined int the Ion interface.
     */
    private IonType iType;
    
    /**
     * The error margin of the fragment ion. Is needed later for the spectrum panel.
     */
    private double iErrorMargin;
    
    /**
     * This variable holds the calculated theoretical vs. experimental error.
     */
    private double iTheoreticalExperimentalMassError;
    
    /**
     * The charge of the ion
     */
    private int iCharge;

    /**
     * TODO: API
     * @param aMz
     * @param aType
     * @param aNumber
     * @param aCharge
     */
    public FragmentIon(double aMz, IonType aType, int aNumber, int aCharge) {
    	this(aMz, aType, aNumber, aCharge, 0.0);
    }

    /**
     * Constructor gets all the parameters to create an fragment ion object.
     *
     * @param aMz          The m/z value of the fragment ion.
     * @param aType        The type of the fragment ion as defined in the ion interface.
     * @param aNumber      The number of the fragment ion.
     * @param aCharge      The charge of the fragment ion.
     * @param aErrorMargin The error margin of the fragment ion.
     */
    public FragmentIon(double aMz, IonType aType, int aNumber, int aCharge, double aErrorMargin) {
    	this(aMz, 0.0, aType, aNumber, aCharge, aErrorMargin);
    }

    /**
     * The same constructor as above but with the intensity.
     *
     * @param aMz          The m/z value of the fragment ion.
     * @param aIntensity   The intensity of the fragment ion.
     * @param aType        The type of the fragment ion as defined in the ion interface.
     * @param aNumber      The number of the fragment ion.
     * @param aCharge      The charge of the fragment ion.
     * @param aErrorMargin The error margin of the fragment ion.
     */
    public FragmentIon(double aMz, double aIntensity, IonType aType, int aNumber, int aCharge, double aErrorMargin) {
        iMz = aMz;
        iIntensity = aIntensity;
        iType = aType;
        iNumber = aNumber;
        iCharge = aCharge;
        iErrorMargin = aErrorMargin;
    }

    /**
     * This method compares the theoretical mass peak with the experimental one and tells
     * if it's a match using a specific mass error tolerance and calculating the theoretical/
     * experimental mass error.
     *
     * @param aPeaks     The mass peak array
     * @param aMassError The mass error
     * @return matchFlag boolean
     */
    public boolean isMatch(List<SpectrumPeak> aPeaks, double aMassError, double threshold) {
        boolean matchFlag = false;
        for (int i = 0; i < aPeaks.size(); i++) {
            if (-aMassError <= (aPeaks.get(i).getMZ() - iMz) && (aPeaks.get(i).getMZ() - iMz) <= aMassError) {
                iTheoreticalExperimentalMassError = aPeaks.get(i).getMZ() - iMz;
                iIntensity = aPeaks.get(i).getIntensity();
                if(iIntensity > threshold){
                	matchFlag = true;
                }
                break;
            }
        }
        return matchFlag;
    }

    /**
     * Returns the m/z of the fragment ion.
     *
     * @return iMz
     */
    public double getMZ() {
        return iMz;
    }

    /**
     * Returns the intensity of the fragment ion.
     *
     * @return iIntensity
     */
    public double getIntensity() {
        return iIntensity;
    }

    /**
     * Returns the number of the fragment ion.
     *
     * @return iNumber
     */
    public int getNumber() {
        return iNumber;
    }

    /**
     * Returns the score of the fragment ion.
     *
     * @return iScore
     */
    public double getScore() {
        return iScore;
    }

    /**
     * Returns the type of the fragment ion.
     *
     * @return iType
     */
    public IonType getType() {
        return iType;
    }

    /**
     * Returns the error margin.
     *
     * @return iErrorMargin
     */
    public double getErrorMargin() {
        return iErrorMargin;
    }

    /**
     * Returns the theoretical experimental mass error.
     *
     * @return iTheoreticalExperimentalMassError
     */
    public double getTheoreticalExperimentalMassError() {
        return iTheoreticalExperimentalMassError;
    }

    /**
     * Returns the charge.
     *
     * @return iCharge
     */
    public double getCharge() {
        return iCharge;
    }

    /**
     * Returns the fragment ion canonical letter.
     *
     * @return letter string
     */
    public String getLetter() {
    	return (iType != null) ? iType.toString() : null;
    }
    
    @Override
    public String toString() {
    	String str = this.getLetter();
		if (iNumber > 0) {
			str += iNumber;
		}
		if (iCharge > 1) {
			for (int i = 0; i < iCharge; i++) {
				str += "+";
			}
		}
    	return str;
    }

}
