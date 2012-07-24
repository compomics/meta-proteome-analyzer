package de.mpa.client.ui.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.PieDataset;

public class OntologyPieChart extends Chart {
	
	 private PieDataset pieDataset;

	/**
     * Constructs an OntologyPieChart.
     *
     * @param data
     */
    public OntologyPieChart(Object data) {
        super(data);
    }

	@Override
	public String getChartTitle() {
		return chartTitle;
	}

	@Override
	protected void process(Object data) {
		if (data instanceof OntologyData) {
			OntologyData ontologyData = (OntologyData) data;
			pieDataset = ontologyData.getPieDataset();
		}
	}

	@Override
	protected void setChart() {
		chartTitle = "Ontology Pie Chart";
		chart = ChartFactory.createPieChart3D(chartTitle, pieDataset, false, false, false);
		chart.setBackgroundPaint(null);
		
		PiePlot3D plot = (PiePlot3D) chart.getPlot();
        plot.setStartAngle(324);
        plot.setCircular(true);
        plot.setForegroundAlpha(0.75f);
        plot.setBackgroundPaint(null);
		
	}

}
