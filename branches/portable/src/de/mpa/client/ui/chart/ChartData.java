package de.mpa.client.ui.chart;

import java.util.List;

import org.jfree.data.general.Dataset;

import de.mpa.client.model.dbsearch.ProteinHit;

/**
 * Specification for ChartData, preparing the data and return a JFreeChart Dataset.
 * @author T.Muth
 * @date 26-07-2012
 *
 */
public interface ChartData {
	
	/**
	 * Prepares the chart data.
	 */
	public void init();
	
	/**
	 * Returns JFreeChart Dataset
	 * @return JFreeChart Dataset object
	 */
	public Dataset getDataset();

	/**
	 * Returns a list of protein hits associated with the provided key.
	 * @param key The key.
	 * @return The list of protein hits.
	 */
	public List<ProteinHit> getProteinHits(String key);
}
