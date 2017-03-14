package de.mpa.client.ui.chart;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.jfree.data.general.Dataset;
import org.jfree.data.statistics.HistogramDataset;

import de.mpa.client.model.dbsearch.ProteinHit;

/**
 * Chart data implementation for histograms.
 * 
 * @author A. Behne
 */
public class HistogramData implements ChartData {
	
	/**
	 * The histogram's dataset.
	 */
	private HistogramDataset histDataset;
	
	/**
	 * The array of values backing the dataset.
	 */
	private final double[] values;
	
	/**
	 * The number of bins to be displayed in the histogram.
	 */
	private final int binCount;
	
	/**
	 * The index marking the lower boundary of a values sub-array.
	 */
	private final int fromIndex;
	
	/**
	 * The index marking the upper (exclusive) boundary of a values sub-array.
	 */
	private final int toIndex;

//	/**
//	 * Creates a histogram data object from the total ion currents stored in the
//	 * specified database search result object and bin count parameters.
//	 * 
//	 * @param dbSearchResult the database search result object
//	 * @param binCount the number of bins
//	 */
//	public HistogramData(DbSearchResult dbSearchResult, int binCount) {
//		this(dbSearchResult.getTotalIonCurrentMap().values(), binCount);
//	}

	/**
	 * Creates a histogram data object from the specified values and bin count
	 * parameters.
	 * 
	 * @param values
	 * @param binCount
	 */
	public HistogramData(double[] values, int binCount) {
		this(values, binCount, 0, values.length);
	}
	
	/**
	 * Creates a histogram data object from the specified values list and bin 
	 * count and index parameters.
	 * 
	 * @param values the collection of values to bin
	 * @param binCount the number of bins
	 * @param fromIndex the index marking the lower boundary of a sorted values sub-array
	 * @param toIndex the index marking the upper (exclusive) boundary of a sorted values sub-array
	 */
	public HistogramData(Collection<Double> values, int binCount, int fromIndex, int toIndex) {
		this(ArrayUtils.toPrimitive(values.toArray(new Double[values.size()])),
				binCount, fromIndex, toIndex);
	}
	
	/**
	 * Creates a histogram data object from the specified values array and bin 
	 * count and index parameters.
	 * 
	 * @param values the collection of values to bin
	 * @param binCount the number of bins
	 * @param fromIndex the index marking the lower boundary of a sorted values sub-array
	 * @param toIndex the index marking the upper (exclusive) boundary of a sorted values sub-array
	 */
	public HistogramData(double[] values, int binCount, int fromIndex, int toIndex) {
		this.values = values;
		this.binCount = binCount;
		this.fromIndex = fromIndex;
		this.toIndex = toIndex;

        this.init();
	}

	@Override
	public Dataset getDataset() {
		return this.histDataset;
	}

	@Override
	public List<ProteinHit> getProteinHits(String key) {
		// does not apply here, just return null
		return null;
	}

	@SuppressWarnings("serial")
	@Override
	public void init() {
		Arrays.sort(this.values);
		
		double[] values;
		if ((this.fromIndex > 0) || (this.toIndex < this.values.length)) {
			int length = this.toIndex - this.fromIndex;
			values = new double[length];
			System.arraycopy(this.values, this.fromIndex, values, 0, length);
		} else {
			values = this.values;
		}

        this.histDataset = new HistogramDataset() {
			/*
			 * Overrides as workaround for when only a single bar is to be
			 * painted which would otherwise have zero width
			 */
			@Override
			public double getStartXValue(int series, int item) {
				double startXValue = super.getStartXValue(series, item);
				double endXValue = super.getEndXValue(series, item);
				if ((endXValue - startXValue) < 1.0) {
					return startXValue - (0.5 / HistogramData.this.binCount);
				}
				return startXValue;
			}
			@Override
			public double getEndXValue(int series, int item) {
				double startXValue = super.getStartXValue(series, item);
				double endXValue = super.getEndXValue(series, item);
				if ((endXValue - startXValue) < 1.0) {
					return endXValue + (0.5 / HistogramData.this.binCount);
				}
				return endXValue;
			}
		};

        this.histDataset.addSeries("key", values, this.binCount);
	}

}
