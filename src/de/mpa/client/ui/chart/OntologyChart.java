package de.mpa.client.ui.chart;

import java.text.DecimalFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.Plot;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.RectangleInsets;

import de.mpa.analysis.UniProtUtilities.KeywordCategory;

/**
 * Chart implementation to create pie or bar charts of protein ontology data.
 * 
 * @author A. Behne
 */
public class OntologyChart extends Chart {
	
	/**
	 * The primary dataset. For non-pie charts adapter classes are used to convert between dataset types.
	 */
	private PieDataset pieDataset;
	
	/**
	 * Enumeration holding chart sub-types pertaining to keyword ontologies.
	 */
	public enum OntologyChartType implements ChartType {

		BIOLOGICAL_PROCESS(KeywordCategory.BIOLOGICAL_PROCESS),
		CELLULAR_COMPONENT(KeywordCategory.CELLULAR_COMPONENT),
		MOLECULAR_FUNCTION(KeywordCategory.MOLECULAR_FUNCTION),
		CODING_SEQUNCE_DIVERSITY(KeywordCategory.CODING_SEQUNCE_DIVERSITY),
		DEVELOPMENTAL_STAGE(KeywordCategory.DEVELOPMENTAL_STAGE),
		DISEASE(KeywordCategory.DISEASE),
		DOMAIN(KeywordCategory.DOMAIN),
		LIGAND(KeywordCategory.LIGAND),
		PTM(KeywordCategory.PTM),
		TECHNICAL_TERM(KeywordCategory.TECHNICAL_TERM);
		
		private String title;
		private KeywordCategory ontology;
		
		private OntologyChartType(KeywordCategory ontology) {
			this.title = ontology.toString();
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
		
		public KeywordCategory getOntology() {
			return this.ontology;
		}
	}
		
	/**
     * Constructs an ontology chart from the specified data container and chart sub-type identifier.
     * @param data the chart data container
     * @param chartType the chart sub-type identifier
     */
    public OntologyChart(ChartData data, ChartType chartType) {
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
	protected void setChart(ChartData data) {
		OntologyChartType pieChartType = (OntologyChartType) chartType;
		chartTitle = pieChartType.toString() + " Ontology";
		
		final PiePlot3D piePlot = new PiePlot3DExt(pieDataset, 0.2);
        piePlot.setInsets(new RectangleInsets(0.0, 5.0, 5.0, 5.0));
        piePlot.setCircular(true);
        piePlot.setForegroundAlpha(0.75f);
        piePlot.setBackgroundPaint(null);
        piePlot.setOutlineVisible(false);
		piePlot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}\n{1} ({2})", new DecimalFormat("0"), new DecimalFormat("0.00%")));
		
		Plot plot = piePlot;
		if (!((OntologyData) data).getShowAsPie()) {
			PieToCategoryPlot categoryPlot = new PieToCategoryPlot(piePlot);
			categoryPlot.setForegroundAlpha(0.75f);
			categoryPlot.getRangeAxis().setLabel(pieDataset.getGroup().getID());
			plot = categoryPlot;
		}

        // create and configure chart
        chart = new JFreeChart(chartTitle, JFreeChart.DEFAULT_TITLE_FONT, plot, false);
        ChartFactory.getChartTheme().apply(chart);
		chart.setBackgroundPaint(null);
	}
	
}
