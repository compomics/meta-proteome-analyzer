package de.mpa.fragmentation;


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
    private double iMz = 0.0;
    /**
     * The intensity of the fragment ion.
     */
    private double iIntensity = 0.0;
    /**
     * The number of the fragment ion. For example: b1 ion has the number 1, b4 has number 4 etc.
     */
    private int iNumber = 0;
    /**
     * The score of the fragment ion.
     */
    private int iScore = 0;
    /**
     * The type of this ion as defined int the Ion interface.
     */
    private int iType;
    /**
     * The error margin of the fragment ion. Is needed later for the spectrum panel.
     */
    private double iErrorMargin = 0.0;
    /**
     * This variable holds the calculated theoretical vs. experimental error.
     */
    private double iTheoreticalExperimentalMassError;
    /*
     * The charge of the ion
     */
    private int iCharge;

    
    public FragmentIon(double aMz, int aType, int aNumber, int aCharge) {
        iMz = aMz;
        iType = aType;
        iNumber = aNumber;
        iCharge = aCharge;
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
    public FragmentIon(double aMz, int aType, int aNumber, int aCharge, double aErrorMargin) {
        iMz = aMz;
        iType = aType;
        iNumber = aNumber;
        iCharge = aCharge;
        iErrorMargin = aErrorMargin;
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
    public FragmentIon(double aMz, double aIntensity, int aType, int aNumber, int aCharge, double aErrorMargin) {
        iMz = aMz;
        iIntensity = aIntensity;
        iType = aType;
        iNumber = aNumber;
        iCharge = aCharge;
        iType = aType;
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
    public int getType() {
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
        switch (iType) {
            case FragmentIon.A_ION:
                return "a";
            case FragmentIon.AH2O_ION:
                return "a°";
            case FragmentIon.ANH3_ION:
                return "a*";
            case FragmentIon.B_ION:
                return "b";
            case FragmentIon.BH2O_ION:
                return "b°";
            case FragmentIon.BNH3_ION:
                return "b*";
            case FragmentIon.C_ION:
                return "c";
            case FragmentIon.X_ION:
                return "x";
            case FragmentIon.Y_ION:
                return "y";
            case FragmentIon.YH2O_ION:
                return "y°";
            case FragmentIon.YNH3_ION:
                return "y*";
            case FragmentIon.Z_ION:
                return "z";
            case FragmentIon.MH_ION:
                return "MH";
            case FragmentIon.MHNH3_ION:
                return "MH*";
            case FragmentIon.MHH2O_ION:
                return "MH°";
        }
        return null;

    }

}
