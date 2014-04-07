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

		private String title;
		
		private BarChartType(String title) {
			this.title = title;
		}
		@Override
		public String toString() {
			return title;
		}
		
		@Override
		public String getTitle() {
			return title;
		}
	}
	
	/**
     * Constructs an IdentificationChart.
     * @param data
     */
    public IdentificationChart(ChartData data, BarChartType barPlotType) {
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
		BarChartType barPlotType = (BarChartType) chartType;
		String title = barPlotType.toString();
		chartTitle = title + " Bar Chart";
		chart = ChartFactory.createBarChart(chartTitle, "Search Engines", "No. " + title + "s", categoryDataset, PlotOrientation.VERTICAL, true, true, false);
		chart.setBackgroundPaint(null);
	}
}
