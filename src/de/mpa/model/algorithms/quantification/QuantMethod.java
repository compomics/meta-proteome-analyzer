package de.mpa.model.algorithms.quantification;

public interface QuantMethod {
	
	/**
	 * Returns the quantification measure.
	 * @return The quantification measure.
	 */
    double getResult();

	/**
	 * Method to calculate the quantification measure.
	 * @param params Variable argument list of parameters.
	 */
    void calculate(Object... params);

}
