package de.mpa.model.algorithms.fragmentation;

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
    double getMZ();

    /**
     * This method returns the intensity.
     *
     * @return double
     */
    double getIntensity();

    /**
     * Returns charge information for this peak.
     * 
     * @return int
     */
    int getCharge();
}
