package de.mpa.algorithms.similarity;

/**
 * Enumeration holding several transformation methods for scalar double values.
 * 
 * @author A. Behne
 */
public enum Transformation {
	
	NONE {
		@Override
		public double transform(double input) {
			return input;
		}
	},
	SQRT {
		@Override
		public double transform(double input) {
			return (input > 0.0) ? Math.sqrt(input) : 0.0;
		}
	},
	LOG {
		@Override
		public double transform(double input) {
			return (input > 0.0) ? Math.log(input) : 0.0;
		}
	};
	
	public abstract double transform(double input);
	
}
