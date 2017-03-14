package de.mpa.client.ui.chart;

import java.text.DecimalFormat;

import de.mpa.analysis.UniProtUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.Plot;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.RectangleInsets;

/**
 * Chart implementation to create pie or bar charts of protein ontology data.
 * 
 * @author A. Behne
 */
public class OntologyChart extends Chart {

	/**
	 * The primary dataset. For non-pie charts adapter classes are used to
	 * convert between dataset types.
	 */
	private PieDataset pieDataset;

	/**
	 * Enumeration holding chart sub-types pertaining to keyword ontologies.
	 */
	public enum OntologyChartType implements ChartType {
		BIOLOGICAL_PROCESS(UniProtUtilities.KeywordCategory.BIOLOGICAL_PROCESS), CELLULAR_COMPONENT(
				UniProtUtilities.KeywordCategory.CELLULAR_COMPONENT), MOLECULAR_FUNCTION(
				UniProtUtilities.KeywordCategory.MOLECULAR_FUNCTION), CODING_SEQUNCE_DIVERSITY(
				UniProtUtilities.KeywordCategory.CODING_SEQUNCE_DIVERSITY), DEVELOPMENTAL_STAGE(
				UniProtUtilities.KeywordCategory.DEVELOPMENTAL_STAGE), DISEASE(
				UniProtUtilities.KeywordCategory.DISEASE), DOMAIN(UniProtUtilities.KeywordCategory.DOMAIN), LIGAND(
				UniProtUtilities.KeywordCategory.LIGAND), PTM(UniProtUtilities.KeywordCategory.PTM), TECHNICAL_TERM(
				UniProtUtilities.KeywordCategory.TECHNICAL_TERM);

		private String title;
		private UniProtUtilities.KeywordCategory ontology;

		OntologyChartType(UniProtUtilities.KeywordCategory ontology) {
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

		public UniProtUtilities.KeywordCategory getOntology() {
			return ontology;
		}
	}

	/**
	 * Constructs an ontology chart from the specified data container and chart
	 * sub-type identifier.
	 * 
	 * @param data
	 *            the chart data container
	 * @param chartType
	 *            the chart sub-type identifier
	 */
	public OntologyChart(ChartData data, ChartType chartType) {
		super(data, chartType);
	}

	@Override
	protected void process(ChartData data) {
		if (data instanceof OntologyData) {
			OntologyData ontologyData = (OntologyData) data;
			ontologyData.setChartType(this.chartType);
			try {
                this.pieDataset = ontologyData.getDataset();
			} catch (NullPointerException e) {
				// ignore this error (user clicks too fast)
			}
		}
	}

	@Override
	protected void setChart(ChartData data) {
		OntologyChart.OntologyChartType pieChartType = (OntologyChart.OntologyChartType) this.chartType;
        this.chartTitle = pieChartType + " Ontology";

		PiePlot3D piePlot = new PiePlot3DExt(this.pieDataset, 0.2);
		piePlot.setInsets(new RectangleInsets(0.0, 5.0, 5.0, 5.0));
		piePlot.setCircular(true);
		piePlot.setForegroundAlpha(0.75f);
		piePlot.setBackgroundPaint(null);
		piePlot.setOutlineVisible(false);
		piePlot.setLabelGenerator(new StandardPieSectionLabelGenerator(
				"{0}\n{1} ({2})", new DecimalFormat("0"), new DecimalFormat(
						"0.00%")));

		Plot plot = piePlot;
		if (!((OntologyData) data).getShowAsPie()) {
			PieToCategoryPlot categoryPlot = new PieToCategoryPlot(piePlot);
			categoryPlot.setForegroundAlpha(0.75f);
			categoryPlot.getRangeAxis().setLabel(this.pieDataset.getGroup().getID());
			plot = categoryPlot;
		}

		// create and configure chart
        this.chart = new JFreeChart(this.chartTitle, JFreeChart.DEFAULT_TITLE_FONT, plot,
				false);
		ChartFactory.getChartTheme().apply(this.chart);
        this.chart.setBackgroundPaint(null);
	}

}
