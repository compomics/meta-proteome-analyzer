package de.mpa.client.ui.chart;

import java.util.List;

import org.jfree.data.general.Dataset;
import org.jfree.data.statistics.HistogramDataset;

import de.mpa.client.model.dbsearch.ProteinHit;

public class HistogramData implements ChartData {
	
	private HistogramDataset histDataset;
	private List<Double> values;
	private int binCount;
	
	/**
	 * Creates a histogram data object from the specified values list and bin count parameter.
	 * @param values the list of values
	 * @param binCount the number of bins
	 */
	public HistogramData(List<Double> values, int binCount) {
		this.values = values;
		this.binCount = binCount;
		init();
	}

	@Override
	public Dataset getDataset() {
		return histDataset;
	}

	@Override
	public List<ProteinHit> getProteinHits(String key) {
		// does not apply here, just return null
		return null;
	}

	@Override
	public void init() {
		histDataset = new HistogramDataset();

		// Set data
		int size = this.values.size();
		double[] values = new double[size];
		for (int i = 0; i < size; i++) {
			values[i] = this.values.get(i).doubleValue();
		}
		histDataset.addSeries("key", values, binCount);
	}

}
