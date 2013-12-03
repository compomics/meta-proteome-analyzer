package de.mpa.client.ui.chart;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;

/**
 * <p>Class to plot a histogram of the number of peaks of all intensity values.</p>
 *
 * @author T.Muth
 * @date 22-06-2012
 */
public class HistogramChart extends Chart {
	
	private HistogramDataset histDataset;
    
    public enum HistogramChartType implements ChartType {
    	TOTAL_ION_HIST("Total Ion Current", "Abs. Frequency");

    	private String xLabel;
    	private String yLabel;
    	
		private HistogramChartType(String xLabel, String yLabel) {
			this.xLabel = xLabel;
			this.yLabel = yLabel;
		}
		@Override
		public String toString() {
			return "TIC Histogram";
		}
		public String getXLabel() {
			return xLabel;
		}
		public String getYLabel() {
			return yLabel;
		}
		@Override
		public String getTitle() {
			return null;
		}
    }
    /**
     * Constructs a denovo score histogram
     *
     * @param resultData
     */
    public HistogramChart(ChartData data, ChartType chartType) {
        super(data, chartType);
    }

    @Override
    protected void process(ChartData data) {
    	if (data instanceof HistogramData) {
    		HistogramData totalIonData = (HistogramData) data;
			histDataset = (HistogramDataset) totalIonData.getDataset();
		}
    }

    @Override
    protected void setChart(ChartData data) {
    	HistogramChartType hct = (HistogramChartType) chartType;
        chart = ChartFactory.createHistogram(getChartTitle(),
                hct.getXLabel(),
                hct.getYLabel(),
                histDataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false);
        XYPlot plot = (XYPlot) chart.getPlot();
        
        plot.setBackgroundAlpha(0.0f);
        plot.setOutlineVisible(false);
        plot.setDomainZeroBaselineVisible(false);
        plot.setRangeZeroBaselineVisible(false);
        
        ValueAxis xAxis = plot.getDomainAxis();
//        xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		xAxis.setAutoRangeMinimumSize(1.0);
        
        LogarithmicAxis yAxis = new LogarithmicAxis(hct.getYLabel());
        yAxis.setLabelFont(plot.getRangeAxis().getLabelFont());
        yAxis.setTickLabelFont(plot.getRangeAxis().getTickLabelFont());
        yAxis.setAllowNegativesFlag(true);
		plot.setRangeAxis(yAxis);
        
        XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();
        renderer.setMargin(0.125);
        renderer.setShadowVisible(false);
    }

    @Override
    public String getChartTitle() {
        return null;
    }
}

