package de.mpa.client.ui.chart;

import java.text.AttributedString;
import java.util.HashMap;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.RectangleInsets;

import de.mpa.analysis.UniprotAccessor.KeywordOntology;
import de.mpa.client.model.dbsearch.ProteinHitList;

public class OntologyPieChart extends Chart {
	
	private PieDataset pieDataset;
	
	public enum OntologyChartType implements ChartType {

		BIOLOGICAL_PROCESS(KeywordOntology.BIOLOGICAL_PROCESS),
		CELLULAR_COMPONENT(KeywordOntology.CELLULAR_COMPONENT),
		CODING_SEQUNCE_DIVERSITY(KeywordOntology.CODING_SEQUNCE_DIVERSITY),
		DEVELOPMENTAL_STAGE(KeywordOntology.DEVELOPMENTAL_STAGE),
		DISEASE(KeywordOntology.DISEASE),
		DOMAIN(KeywordOntology.DOMAIN),
		LIGAND(KeywordOntology.LIGAND),
		MOLECULAR_FUNCTION(KeywordOntology.MOLECULAR_FUNCTION),
		PTM(KeywordOntology.PTM),
		TECHNICAL_TERM(KeywordOntology.TECHNICAL_TERM);
		
		private String title;
		private KeywordOntology ontology;
		private Map<String, ProteinHitList> occMap;
		
		private OntologyChartType(KeywordOntology ontology) {
			this.title = ontology.toString();
			this.ontology = ontology;
			this.occMap = new HashMap<String, ProteinHitList>();
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
		
		public Map<String, ProteinHitList> getOccurrenceMap() {
			return this.occMap;
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
