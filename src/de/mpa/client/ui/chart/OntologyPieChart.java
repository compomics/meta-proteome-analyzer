package de.mpa.client.ui.chart;

import java.text.AttributedString;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.PieDataset;

public class OntologyPieChart extends Chart {
	
	private PieDataset pieDataset;
	
	public enum OntologyChartType implements ChartType {
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
		OntologyChartType pieChartType = (OntologyChartType) chartType;
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
		plot.setLabelGenerator(new PieSectionLabelGenerator() {
			@Override
			public String generateSectionLabel(PieDataset dataset, Comparable key) {
				Integer value = (Integer) dataset.getValue(key);
				double total = 0.0;
				for (int i = 0; i < dataset.getItemCount(); i++) {
					total += ((Integer) dataset.getValue(i)).doubleValue();
				}
				double relVal = value.doubleValue() / total;
				return key.toString() + "\n" + value + " (" + (Math.round(relVal * 1000.0) / 10.0) + "%)";
			}
			@Override
			public AttributedString generateAttributedSectionLabel(PieDataset dataset, Comparable key) {
				return null;	// unused
			}
		});
        plot.setStartAngle(324);
        plot.setCircular(true);
        plot.setForegroundAlpha(0.75f);
        plot.setBackgroundPaint(null);
        plot.setOutlineVisible(false);
	}
}
