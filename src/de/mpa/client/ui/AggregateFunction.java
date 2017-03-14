package de.mpa.client.ui;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Enumeration containing representations of simple aggregate functions to
 * combine multiple numeric values into one.
 * @author A. Behne
 */
public enum AggregateFunction {
	// TODO: lots of unchecked casting to Number is performed here, maybe add instanceof checks to make code more robust
	NONE {
		@Override
		public Object aggregate(Object... values) {
			return null;
		}
	},
	MIN {
		@Override
		public Object aggregate(Object... values) {
			double min = Double.MAX_VALUE;
			for (int i = 0; i < values.length; i++) {
				min = Math.min(((Number) values[i]).doubleValue(), min);
			}
			return min;
		}
	},
	MAX {
		@Override
		public Object aggregate(Object... values) {
			double max = Double.MIN_VALUE;
			for (int i = 0; i < values.length; i++) {
				max = Math.max(((Number) values[i]).doubleValue(), max);
			}
			return max;
		}
	},
	MEAN {
		@Override
		public Object aggregate(Object... values) {
			return ((Number) AggregateFunction.SUM.aggregate(values)).doubleValue() / values.length;
		}
	},
	MEDIAN {
		@Override
		public Object aggregate(Object... values) {
			Arrays.sort(values);
			return values[values.length / 2];
		}
	},
	MODE {
		@Override
		public Object aggregate(Object... values) {
			// map elements of values array to how often they appear in it
			Map<Object, Integer> modeMap = new HashMap<Object, Integer>();
			for (Object value : values) {
				Integer count = modeMap.get(value);
				if (count == null) {
					count = 0;
				}
				modeMap.put(value, count + 1);
			}
			// find maximum of mapped value counts
			Object mode = null;
			int max = Integer.MIN_VALUE;
			for (Map.Entry<Object, Integer> entry : modeMap.entrySet()) {
				Integer value = entry.getValue();
				if (value > max) {
					mode = entry.getKey();
					max = value;
				}
			}
			return mode;
		}
	},
	SUM {
		@Override
		public Object aggregate(Object... values) {
			double sum = 0.0;
			for (int i = 0; i < values.length; i++) {
				sum += ((Number) values[i]).doubleValue();
			}
			return sum;
		}
	},
	DISTINCT {
		@Override
		public Object aggregate(Object... values) {
			if (values != null) {
				if (values.length != 0) {
					Object value = values[0];
					if (value instanceof Collection<?>) {
						if (value instanceof Set<?>) {
							return ((Collection<?>) value).size();
						} else {
							return new HashSet<Object>(((Collection<?>) value)).size();
						}
					}
				}
				return 0;
			}
			return null;
		}
	};
	
//	/** Computes the element count. */
//	public static final AggregateFunction COUNT = new AggregateFunction() {
//		public Double aggregate(double[] values) {
//			return new Double(values.length);
//		}
//	};
	
	/**
	 * Template method for combining multiple values into one.
	 * @param values The array of values to be combined.
	 * @return The result of the aggregation computation.
	 */
	public abstract Object aggregate(Object... values);
	
}
