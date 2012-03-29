package de.mpa.fragmentation;

import java.util.List;

/**
 * This class represents an Ion object. It holds the ion number,
 * the ion score and the 6 different types of ions:
 * 1) a-c ions
 * 2) x-z ions
 * 
 * @author Thilo Muth
 */
public interface Ion {

    /**
     * This int is the identifier for an a ion.
     */
    public final static int A_ION = 0;
    /**
     * This int is the identifier for an a* ion.
     */
    public final static int ANH3_ION = 1;
    /**
     * This int is the identifier for an a� ion.
     */
    public final static int AH2O_ION = 2;
    /**
     * This int is the identifier for a b ion.
     */
    public final static int B_ION = 3;
    /**
     * This int is the identifier for a b* ion.
     */
    public final static int BNH3_ION = 4;
    /**
     * This int is the identifier for a b� ion.
     */
    public final static int BH2O_ION = 5;
    /**
     * This int is the identifier for a c ion.
     */
    public final static int C_ION = 6;
    /**
     * This int is the identifier for a x ion.
     */
    public final static int X_ION = 7;
    /**
     * This int is the identifier for a y ion.
     */
    public final static int Y_ION = 8;
    /**
     * This int is the identifier for a y* ion.
     */
    public final static int YNH3_ION = 9;
    /**
     * This int is the identifier for a y� ion.
     */
    public final static int YH2O_ION = 10;
    /**
     * This int is the identifier for a z ion.
     */
    public final static int Z_ION = 11;
     /**
     * This int is the identifier for an MH ion.
     */
    public final static int MH_ION = 12;
     /**
     * This int is the identifier for an MH-NH3 ion.
     */
    public final static int MHNH3_ION = 13;
     /**
     * This int is the identifier for an MH-H2O ion.
     */
    public final static int MHH2O_ION = 14;

    /**
     * This method checks whether the ion matched with a given set of peaks
     *
     * @param peaks
     * @param aMassError
     * @return boolean
     */
    public boolean isMatch(List<SpectrumPeak> peaks, double aMassError, double threshold);

    /**
     * Returns the m/z.
     *
     * @return double
     */
    public double getMZ();

    /**
     * Returns the intensity.
     *
     * @return double
     */
    public double getIntensity();

    /**
     * Returns the type of ion.
     *
     * @return int
     */
    public int getType();

    /**
     * Returns the ion number.
     *
     * @return int
     */
    public int getNumber();

    /**
     * Returns the score.
     *
     * @return double
     */
    public double getScore();
}
