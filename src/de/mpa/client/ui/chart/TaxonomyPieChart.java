package de.mpa.client.ui.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.PieDataset;

public class TaxonomyPieChart extends Chart {
	private PieDataset pieDataset;
	
	public enum TaxonomyChartType implements ChartType {
		KINGDOM, PHYLUM, CLASS, SPECIES
	}
		
	/**
     * Constructs an OntologyPieChart.
     *
     * @param data Input data.
     * @param chartType Chart type.
     */
    public TaxonomyPieChart(Object data, ChartType chartType) {
        super(data, chartType);
    }

	@Override
	protected void process(Object data) {
		if (data instanceof TaxonomyData) {
			TaxonomyData taxonomyData = (TaxonomyData) data;
			taxonomyData.setChartType(chartType);
			pieDataset = taxonomyData.getDataset();
		}
	}

	@Override
	protected void setChart() {
		TaxonomyChartType pieChartType = (TaxonomyChartType) chartType;
		switch (pieChartType) {
		case KINGDOM:
			chartTitle = "Kingdom Taxonomy";
			break;
		case PHYLUM:
			chartTitle = "Phylum Taxonomy";
			break;
		case CLASS:
			chartTitle = "Class Taxonomy";
			break;
		case SPECIES:
			chartTitle = "Species Taxonomy";
			break;
		default:
			chartTitle = "Class Taxonomy";
			break;
		}
		chart = ChartFactory.createPieChart3D(chartTitle, pieDataset, false, false, false);
		chart.setBackgroundPaint(null);
		
		PiePlot3D plot = (PiePlot3D) chart.getPlot();
        plot.setStartAngle(324);
        plot.setCircular(true);
        plot.setForegroundAlpha(0.75f);
        plot.setBackgroundPaint(null);
	}
}
