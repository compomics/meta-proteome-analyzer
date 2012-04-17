package de.mpa.algorithms.quantification;

public interface QuantMethod {
	
	/**
	 * Function to get the results of the labelfree quantification algorithms
	 * @return
	 */
	public double getResult();
	
	/**
	 * Function to calculate the quantification
	 */
	public void calculate(Object... params);

}
