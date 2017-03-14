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
	private static ChartFactory instance;
	
	/**
	 * Non-visible constructor.
	 */
	private ChartFactory() {}
	
	/**
	 * Returns a singleton object of the ChartFactory.
	 * @return ChartFactory instance.
	 */
	public static ChartFactory getInstance() {
		if (ChartFactory.instance == null) {
            ChartFactory.instance = new ChartFactory();
		}
		return ChartFactory.instance;
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
}
