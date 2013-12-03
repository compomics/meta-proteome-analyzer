package de.mpa.client.ui.chart;

import java.awt.Color;
import java.awt.Paint;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.data.category.CategoryDataset;

import de.mpa.util.ColorUtils;

public class TopBarChart extends Chart {
	
	public enum TopBarChartType implements ChartType {
		PROTEINS("Proteins"),
		SPECIES("Species"),
		ENZYMES("Enzymes");

		private String title;
		
		private TopBarChartType(String title) {
			this.title = title;
		}
		@Override
		public String toString() {
			return "Top 10 " + title;
		}
		@Override
		
		public String getTitle() {
			return title;
		}
	}

	private CategoryDataset categoryDataset;
		
	/**
     * Constructs the TopBarChart.
     *
     * @param data Input data.
     * @param chartType Chart type.
     */
    public TopBarChart(ChartData data, ChartType chartType) {
        super(data, chartType);
    }

	@Override
	protected void process(ChartData data) {
		if(data instanceof TopData) {
			TopData topData = (TopData) data;
			categoryDataset = topData.getDataset();
		}
	}

	@Override
	protected void setChart(ChartData data) {
		TopBarChartType topBarChartType = (TopBarChartType) chartType;
		chartTitle = topBarChartType.toString();
		String xLabel = chartTitle.trim().substring(chartTitle.lastIndexOf(' ') + 1);
		chart = ChartFactory.createBarChart3D(chartTitle, xLabel, "No. Spectra", categoryDataset, PlotOrientation.VERTICAL, false, true, false);
		
//    	chart.setTextAntiAlias(true);
//    	
//		// set background color
//		chart.getPlot().setBackgroundPaint(Color.WHITE);
//		chart.setBackgroundPaint(Color.WHITE);
		
		BarRenderer3D barRenderer3d = new BarRenderer3D() {
			public Paint getItemPaint(int row, int column) {
				Color[] colors = ColorUtils.getRainbowGradient(10);
				return colors[column];
	        }
		};
//		barRenderer3d.setDrawBarOutline(false);
//		barRenderer3d.setShadowVisible(false);
        
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setRenderer(0, barRenderer3d);
		plot.setForegroundAlpha(0.75f);
        
        // Rotate the label axis up 45 degrees
        CategoryAxis labelAxis = (CategoryAxis)plot.getDomainAxis();
        labelAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        
        // Set the range axis to display integers only...
        NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        
	}
	
}
