package de.mpa.client.ui.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;


public class IdentificationChart extends Chart {
	
	private CategoryDataset categoryDataset;
	
	public enum BarChartType implements ChartType {
		PROTEIN_LEVEL("Protein"),
		PEPTIDE_LEVEL("Peptide"),
		PSM_LEVEL("PSM");

		private final String title;
		
		BarChartType(String title) {
			this.title = title;
		}
		@Override
		public String toString() {
			return this.title;
		}
		
		@Override
		public String getTitle() {
			return this.title;
		}
	}
	
	/**
     * Constructs an IdentificationChart.
     * @param data
     */
    public IdentificationChart(ChartData data, IdentificationChart.BarChartType barPlotType) {
        super(data, barPlotType);
    }

	@Override
	protected void process(ChartData data) {

		if(data instanceof IdentificationData) {
			IdentificationData iData = (IdentificationData) data;
			categoryDataset = iData.getDataset();
		}
	}

	@Override
	protected void setChart(ChartData data) {
		IdentificationChart.BarChartType barPlotType = (IdentificationChart.BarChartType) this.chartType;
		String title = barPlotType.toString();
        this.chartTitle = title + " Bar Chart";
        this.chart = ChartFactory.createBarChart(this.chartTitle, "Search Engines", "No. " + title + "s", this.categoryDataset, PlotOrientation.VERTICAL, true, true, false);
        this.chart.setBackgroundPaint(null);
	}
}
