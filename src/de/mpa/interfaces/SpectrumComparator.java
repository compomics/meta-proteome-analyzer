package de.mpa.interfaces;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * Abstract class specification for the spectrum comparisons.
 * @author Thilo Muth
 * @author Alexander Behne
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "spectrumComparator", propOrder = {
	    "similarity",
	    "peaksSrc"})
public abstract class SpectrumComparator {
	
	private double similarity;
	private Map<Double,Double> peaksSrc;
	
	/**
	 * Method to set up comparator algorithm. Applies transformations to 
	 * supplied peak map depending on the chosen algorithm.
	 * @param peaksSrc The peak map with which is compared.
	 */
	public void prepare(Map<Double, Double> inputPeaksSrc){}
	
	/**
	 * Method to execute comparator algorithm.
	 * @param peaksTrg The peak map which is to be searched against.
	 */
	public void compareTo(Map<Double, Double> inputPeaksTrg){}

	/**
	 * Method to reset temporary variables to their defaults.
	 */
	public void cleanup(){}

	/**
	 * @return The computed similarity value
	 */
	public double getSimilarity() {
		return this.similarity;
	}
	
	/**
	 * @return The transformed source spectrum map after preparation.
	 */
	public Map<Double, Double> getSourcePeaks() {
		return this.peaksSrc;
	};
		
}
