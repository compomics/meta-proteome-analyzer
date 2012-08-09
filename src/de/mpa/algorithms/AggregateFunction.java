package de.mpa.algorithms;

/**
 * Class representing simple aggregate functions to combine multiple numeric values into one.
 * 
 * @author A. Behne
 */
public abstract class AggregateFunction {
	
	/** Returns null always. */
	public static final AggregateFunction NONE = new AggregateFunction() {
		public Double aggregate(double[] values) {
			return null;
		}
	};
	
	/** Computes the sum. */
	public static final AggregateFunction SUM = new AggregateFunction() {
		public Double aggregate(double[] values) {
			double sum = 0.0;
			for (int i = 0; i < values.length; i++) {
				sum += values[i];
			}
			return sum;
		}
	};
	
	/** Computes the arithmetic mean. */
	public static final AggregateFunction MEAN = new AggregateFunction() {
		public Double aggregate(double[] values) {
			return SUM.aggregate(values) / values.length;
		}
	};
	
	/** Computes the element count. */
	public static final AggregateFunction COUNT = new AggregateFunction() {
		public Double aggregate(double[] values) {
			return new Double(values.length);
		}
	};
	
	/** Computes the minimum. */
	public static final AggregateFunction MIN = new AggregateFunction() {
		public Double aggregate(double[] values) {
			double min = Double.MAX_VALUE;
			for (int i = 0; i < values.length; i++) {
				min = Math.min(values[i], min);
			}
			return min;
		}
	};
	
	/** Computes the maximum. */
	public static final AggregateFunction MAX = new AggregateFunction() {
		public Double aggregate(double[] values) {
			double max = Double.MIN_VALUE;
			for (int i = 0; i < values.length; i++) {
				max = Math.max(values[i], max);
			}
			return max;
		}
	};
	
	// TODO: possibly add median and mode constants

	/**
	 * Template method for combining multiple values into one.
	 * @param values The array of values to be combined.
	 * @return The result of the aggregation computation.
	 */
	public abstract Double aggregate(double[] values);
}
