package de.mpa.fragmentation;

/**
 * This class represents the Peak interface.
 *
 * @author Thilo Muth
 */
public interface IPeak {

    /**
     * This method returns the m/z.
     *
     * @return double
     */
    public double getMZ();

    /**
     * This method returns the intensity.
     *
     * @return double
     */
    public double getIntensity();

    /**
     * Returns charge information for this peak.
     * 
     * @return int
     */
    public int getCharge();
}
