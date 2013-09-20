package de.mpa.client.ui.chart;

import java.text.AttributedString;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.RectangleInsets;

import de.mpa.analysis.UniprotAccessor.KeywordOntology;

public class OntologyPieChart extends Chart {
	
	private PieDataset pieDataset;
	
	public enum OntologyChartType implements ChartType {
		MOLECULAR_FUNCTION("Molecular Function", KeywordOntology.MOLECULAR_FUNCTION),
		BIOLOGICAL_PROCESS("Biological Process", KeywordOntology.BIOLOGICAL_PROCESS),
		CELLULAR_COMPONENT("Cellular Component", KeywordOntology.CELLULAR_COMPONENT);
		
		private String title;
		private KeywordOntology ontology;
		
		private OntologyChartType(String title, KeywordOntology ontology) {
			this.title = title;
			this.ontology = ontology;
		}
		@Override
		public String toString() {
			return this.title;
		}
		
		@Override
		public String getTitle() {
			return this.title;
		}
		
		public KeywordOntology getOntology() {
			return this.ontology;
		}
	}
		
	/**
     * Constructs an OntologyPieChart.
     *
     * @param data Input data.
     * @param chartType Chart type.
     */
    public OntologyPieChart(ChartData data, ChartType chartType) {
        super(data, chartType);
    }

	@Override
	protected void process(ChartData data) {
		if (data instanceof OntologyData) {
			OntologyData ontologyData = (OntologyData) data;
			ontologyData.setChartType(chartType);
			pieDataset = ontologyData.getDataset();
		}
	}

	@Override
	protected void setChart() {
		OntologyChartType pieChartType = (OntologyChartType) chartType;
		chartTitle = pieChartType.toString() + " Ontology";
		
		PiePlot3D plot = new PiePlot3DExt(pieDataset, 0.2);
        plot.setInsets(new RectangleInsets(0.0, 5.0, 5.0, 5.0));
        plot.setStartAngle(324);
        plot.setCircular(true);
        plot.setForegroundAlpha(0.75f);
        plot.setBackgroundPaint(null);
        plot.setOutlineVisible(false);
        
		plot.setLabelGenerator(new PieSectionLabelGenerator() {
			@Override
			public String generateSectionLabel(PieDataset dataset, Comparable key) {
				double value = Math.round(dataset.getValue(key).doubleValue());
				if (value <= 0.0) {
					return null;
				}
				double total = 0.0;
				for (int i = 0; i < dataset.getItemCount(); i++) {
					total += dataset.getValue(i).doubleValue();
				}
				double relVal = value / total;
				return key.toString() + "\n" + ((int) value) +
						" (" + (Math.round(relVal * 1000.0) / 10.0) + "%)";
			}
			@Override
			public AttributedString generateAttributedSectionLabel(PieDataset dataset, Comparable key) {
				return null;	// unused
			}
		});
		
        chart = new JFreeChart(chartTitle, JFreeChart.DEFAULT_TITLE_FONT,
                plot, false);
        ChartFactory.getChartTheme().apply(chart);
		
		chart.setBackgroundPaint(null);
	}
}
