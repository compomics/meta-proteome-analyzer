package de.mpa.client.ui.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.PieDataset;

public class OntologyPieChart extends Chart {
	
	private PieDataset pieDataset;
	
	public enum PieChartType implements ChartType {
		MOLECULAR_FUNCTION, BIOLOGICAL_PROCESS, CELLULAR_COMPONENT
	}
		
	/**
     * Constructs an OntologyPieChart.
     *
     * @param data Input data.
     * @param chartType Chart type.
     */
    public OntologyPieChart(Object data, ChartType chartType) {
        super(data, chartType);
    }

	@Override
	protected void process(Object data) {
		if (data instanceof OntologyData) {
			OntologyData ontologyData = (OntologyData) data;
			ontologyData.setChartType(chartType);
			pieDataset = ontologyData.getDataset();
		}
	}

	@Override
	protected void setChart() {
		PieChartType pieChartType = (PieChartType) chartType;
		switch (pieChartType) {
		case BIOLOGICAL_PROCESS:
			chartTitle = "Biological Process Ontology";
			break;
		case MOLECULAR_FUNCTION:
			chartTitle = "Molecular Function Ontology";
			break;
		case CELLULAR_COMPONENT:
			chartTitle = "Cellular Component Ontology";
			break;
		default:
			chartTitle = "Biological Process Ontology";
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
