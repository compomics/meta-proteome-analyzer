package de.mpa.algorithms.quantification;

public interface QuantMethod {
	
	/**
	 * Returns the quantification measure.
	 * @return The quantification measure.
	 */
	public double getResult();

	/**
	 * Method to calculate the quantification measure.
	 * @param params Variable argument list of parameters.
	 */
	public void calculate(Object... params);

}
