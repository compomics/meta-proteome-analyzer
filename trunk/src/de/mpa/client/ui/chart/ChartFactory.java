package de.mpa.client.ui.chart;

/**
 * ChartFactory class to construct a set of charts, e.g. OntologyPieChart or TopBarChart.
 * 
 * @author T.Muth
 * @date 26-07-2012
 *
 */
public class ChartFactory {
	/**
	 * ChartFactory instance.
	 */
	private static ChartFactory instance = null;
	
	/**
	 * Non-visible constructor.
	 */
	private ChartFactory() {}
	
	/**
	 * Returns a singleton object of the ChartFactory.
	 * @return ChartFactory instance.
	 */
	public static ChartFactory getInstance() {
		if (instance == null) {
			instance = new ChartFactory();
		}
		return instance;
	}
	
	/**
	 * Returns an interactive pie chart displaying sections derived from keyword ontologies.
	 * @param data Chart data
	 * @param chartType Chart type
	 * @return an ontology pie chart
	 */
	public static OntologyChart createOntologyChart(ChartData data, ChartType chartType) {
		return new OntologyChart(data, chartType);
	}
	
	/**
	 * Returns an interactive pie chart displaying sections derived from taxonomic information.
	 * @param data Chart data
	 * @param chartType Chart type
	 * @return a taxonomy pie chart
	 */
	public static TaxonomyChart createTaxonomyChart(ChartData data, ChartType chartType) {
		return new TaxonomyChart(data, chartType);
	}
	
	/**
	 * Returns a bar chart displaying the top number of proteins.
	 * @param data Chart data
	 * @param chartType Chart type
	 * @return a top bar chart
	 */
	public static TopBarChart createTopBarChart(ChartData data, ChartType chartType) {
		return new TopBarChart(data, chartType);
	}

	/**
	 * Returns a total ion current histogram of identified and non-identified spectra.
	 * @param data Chart data
	 * @param chartType Chart type
	 * @return a histogram chart
	 */
	public static Chart createHistogramChart(ChartData data, ChartType chartType) {
		return new HistogramChart(data, chartType);
	}
}
