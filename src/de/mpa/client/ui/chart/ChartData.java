package de.mpa.client.ui.chart;

import org.jfree.data.general.Dataset;

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
}
