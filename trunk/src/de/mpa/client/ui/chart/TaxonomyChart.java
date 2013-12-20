package de.mpa.client.ui.chart;

import java.text.DecimalFormat;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.Plot;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.RectangleInsets;

import de.mpa.analysis.UniProtUtilities.TaxonomyRank;

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
		SUPERKINGDOM("Superkingdom", TaxonomyRank.SUPERKINGDOM),
		KINGDOM("Kingdom", TaxonomyRank.KINGDOM),
		PHYLUM("Phylum", TaxonomyRank.PHYLUM),
		CLASS("Class", TaxonomyRank.CLASS),
		ORDER("Order", TaxonomyRank.ORDER),
		FAMILY("Family", TaxonomyRank.FAMILY),
		GENUS("Genus", TaxonomyRank.GENUS),
		SPECIES("Species", TaxonomyRank.SPECIES);

		private String title;
		private TaxonomyRank rank;
		
		private TaxonomyChartType(String title, TaxonomyRank rank) {
			this.title = title;
			this.rank = rank;
		}
		
		@Override
		public String toString() {
			return this.title;
		}
		
		@Override
		public String getTitle() {
			return this.title;
		}
		
		public TaxonomyRank getRank() {
			return this.rank;
		}
		
		public static boolean contains(String s) {
			for (TaxonomyChartType chartType : values()) {
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
		TaxonomyChartType pieChartType = (TaxonomyChartType) chartType;
		chartTitle = pieChartType.toString();
		
		PiePlot3D piePlot = new PiePlot3DExt(pieDataset, 0.2);
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
			categoryPlot.getRangeAxis().setLabel(pieDataset.getGroup().getID());
			
			plot = categoryPlot;
		}
		
		// create and configure chart
        chart = new JFreeChart(chartTitle, JFreeChart.DEFAULT_TITLE_FONT,
                plot, false);
        ChartFactory.getChartTheme().apply(chart);
		chart.setBackgroundPaint(null);
	}
}
