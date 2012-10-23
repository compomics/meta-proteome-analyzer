package de.mpa.client.ui.chart;

import java.util.ArrayList;
import java.util.List;

import org.jfree.data.general.Dataset;
import org.jfree.data.statistics.HistogramDataset;

import de.mpa.client.model.dbsearch.ProteinHit;

public class HistogramData implements ChartData {
	
	private HistogramDataset histDataset;
	private ArrayList<Double> values;
	private int binSize;
	
	public HistogramData(ArrayList<Double> values, int binSize) {
		this.values = values;
		this.binSize = binSize;
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
		histDataset.addSeries("key", values, binSize);
	}

}
