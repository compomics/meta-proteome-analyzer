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
		if(instance == null) {
			instance = new ChartFactory();
		}
		return instance;
	}
	
	/**
	 * Returns an OntologyPieChart.
	 * @param data Chart data
	 * @param chartType Chart type
	 * @return OntologyPieChart object.
	 */
	public static OntologyPieChart createOntologyPieChart(ChartData data, ChartType chartType) {
		return new OntologyPieChart(data, chartType);
	}
	
	/**
	 * Returns a TaxonomyPieChart.
	 * @param data Chart data
	 * @param chartType Chart type
	 * @return TaxonomyPieChart object.
	 */
	public static TaxonomyPieChart createTaxonomyPieChart(ChartData data, ChartType chartType) {
		return new TaxonomyPieChart(data, chartType);
	}
	
	/**
	 * Returns a TopBarChart.
	 * @param data Chart data
	 * @param chartType Chart type
	 * @return TopBarChart object.
	 */
	public static TopBarChart createTopBarChart(ChartData data, ChartType chartType) {
		return new TopBarChart(data, chartType);
	}
}
