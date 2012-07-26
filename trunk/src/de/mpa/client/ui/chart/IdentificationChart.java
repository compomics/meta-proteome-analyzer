package de.mpa.client.ui.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;


public class IdentificationChart extends Chart {
	
	private CategoryDataset categoryDataset;
	
	public enum BarChartType implements ChartType {
		PROTEIN_LEVEL, PEPTIDE_LEVEL, PSM_LEVEL
	}
	
	/**
     * Constructs an IdentificationChart.
     * @param data
     */
    public IdentificationChart(Object data, BarChartType barPlotType) {
        super(data, barPlotType);
    }
    
    
	@Override
	public String getChartTitle() {
		return chartTitle;
	}
	
	@Override
	protected void process(Object data) {
		
		if(data instanceof IdentificationData) {
			IdentificationData iData = (IdentificationData) data;
			categoryDataset = iData.getCategoryDataset();
		}
	}

	@Override
	protected void setChart() {
		BarChartType barPlotType = (BarChartType) chartType;
		
		switch (barPlotType) {
		case PROTEIN_LEVEL:
			chartTitle = "Protein Bar Chart";
			chart = ChartFactory.createBarChart(chartTitle, "Search Engines", "No. Proteins", categoryDataset, PlotOrientation.VERTICAL, true, true, false);
			break;
		case PEPTIDE_LEVEL:
			chartTitle = "Peptide Bar Chart";
			chart = ChartFactory.createBarChart(chartTitle, "Search Engines", "No. Peptides", categoryDataset, PlotOrientation.VERTICAL, true, true, false);
			break;
		case PSM_LEVEL:
			chartTitle = "PSM Bar Chart";
			chart = ChartFactory.createBarChart(chartTitle, "Search Engines", "No. PSMs", categoryDataset, PlotOrientation.VERTICAL, true, true, false);
			break;
		default:
			break;
		}
		chart.setBackgroundPaint(null);
	}
}
