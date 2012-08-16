package de.mpa.algorithms;

/**
 * Adapter interface for transformation methods.
 * @author A. Behne
 */
public interface Transformation {
	
	/**
	 * Returns the input value without applying any transformation.
	 */
	public static final Transformation NONE = new Transformation() {
		@Override
		public double transform(double input) {
			return input;
		}
	};
	
	/**
	 * Applies square root transformation to specified inputs.
	 */
	public static final Transformation SQRT = new Transformation() {
		@Override
		public double transform(double input) {
			return Math.sqrt(input);
		}
	};

	/**
	 * Applies natural log transformation to non-negative inputs (returns 0.0
	 * otherwise).
	 */
	public static final Transformation LOG = new Transformation() {
		public double transform(double input) {
			return (input > 0.0) ? Math.log(input) : 0.0;
		}
	};
	
	public double transform(double input);
	
}
