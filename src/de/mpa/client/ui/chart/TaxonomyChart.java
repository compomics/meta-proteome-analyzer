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
 * Chart implementation to create pie or bar charts of protein taxonomy data.
 * 
 * @author A. Behne
 */
public class TaxonomyChart extends Chart {
	
	/**
	 * The primary dataset. For non-pie charts adapter classes are used to convert between dataset types.
	 */
	private PieDataset pieDataset;
	
	/**
	 * Enumeration holding chart sub-types pertaining to taxonomic categories.
	 */
	public enum TaxonomyChartType implements ChartType {
		SUPERKINGDOM("Superkingdom", UniProtUtilities.TaxonomyRank.SUPERKINGDOM, 8),
		KINGDOM("Kingdom", UniProtUtilities.TaxonomyRank.KINGDOM, 7),
		PHYLUM("Phylum", UniProtUtilities.TaxonomyRank.PHYLUM, 6),
		CLASS("Class", UniProtUtilities.TaxonomyRank.CLASS, 5),
		ORDER("Order", UniProtUtilities.TaxonomyRank.ORDER, 4),
		FAMILY("Family", UniProtUtilities.TaxonomyRank.FAMILY, 3),
		GENUS("Genus", UniProtUtilities.TaxonomyRank.GENUS, 2),
		SPECIES("Species", UniProtUtilities.TaxonomyRank.SPECIES, 1),
		SUBSPECIES("Subspecies", UniProtUtilities.TaxonomyRank.SUBSPECIES, 0);

		private String title;
		private UniProtUtilities.TaxonomyRank rank;
		private int depth;

		TaxonomyChartType(String title, UniProtUtilities.TaxonomyRank rank, int depth) {
			this.title = title;
			this.rank = rank;
			this.depth = depth;
		}

		@Override
		public String toString() {
			return this.title;
		}

		@Override
		public String getTitle() {
			return this.title;
		}

		public UniProtUtilities.TaxonomyRank getRank() {
			return rank;
		}
		
		public int getDepth() {
			return this.depth;
		}

		public static boolean contains(String s) {
			for (TaxonomyChart.TaxonomyChartType chartType : values()) {
				if (chartType.name().toLowerCase().equals(s))
					return true;
			}
			return false;
		}
	}

	/**
     * Constructs an OntologyPieChart.
     *
     * @param data Input data.
     * @param chartType Chart type.
     */
    public TaxonomyChart(ChartData data, ChartType chartType) {
        super(data, chartType);
    }

	@Override
	protected void process(ChartData data) {
		if (data instanceof TaxonomyData) {
			TaxonomyData taxonomyData = (TaxonomyData) data;
			taxonomyData.setChartType(chartType);
			pieDataset = taxonomyData.getDataset();
		}
	}

	@Override
	protected void setChart(ChartData data) {
		TaxonomyChart.TaxonomyChartType pieChartType = (TaxonomyChart.TaxonomyChartType) this.chartType;
        this.chartTitle = pieChartType.toString();
		
		PiePlot3D piePlot = new PiePlot3DExt(this.pieDataset, 0.2);
        piePlot.setInsets(new RectangleInsets(0.0, 5.0, 5.0, 5.0));
        piePlot.setCircular(true);
        piePlot.setForegroundAlpha(0.75f);
        piePlot.setBackgroundPaint(null);
        piePlot.setOutlineVisible(false);
		piePlot.setLabelGenerator(new StandardPieSectionLabelGenerator(
				"{0}\n{1} ({2})", new DecimalFormat("0"), new DecimalFormat("0.00%")));
		
//		System.out.println(piePlot.getLabelPadding());
//		piePlot.setLabelPadding(new RectangleInsets(2.0, 0.0, 2.0, 0.0));
        
		Plot plot = piePlot;
		if (!((TaxonomyData) data).getShowAsPie()) {
			PieToCategoryPlot categoryPlot = new PieToCategoryPlot(piePlot);
			categoryPlot.setForegroundAlpha(0.875f);
			categoryPlot.getRangeAxis().setLabel(this.pieDataset.getGroup().getID());
			
			plot = categoryPlot;
		}
		
		// create and configure chart
        this.chart = new JFreeChart(this.chartTitle, JFreeChart.DEFAULT_TITLE_FONT,
                plot, false);
        ChartFactory.getChartTheme().apply(this.chart);
        this.chart.setBackgroundPaint(null);
	}
}
