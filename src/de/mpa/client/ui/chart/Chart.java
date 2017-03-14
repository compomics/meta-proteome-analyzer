package de.mpa.client.ui.chart;

import org.jfree.chart.JFreeChart;

/**
 * <b>Chart</b>
 * <p>
 * This class provides general functions of a statistics chart.
 * </p>
 *
 * @author T.Muth
 * @date 22-06-2012
 */
public abstract class Chart {

    /**
     * The chart instance.
     */
    protected JFreeChart chart;
    
    /**
     * Chart title.
     */
    protected String chartTitle = "";
    
    /**
     * Chart type.
     */
    protected ChartType chartType;
    
    /**
     * Creates an instance of the Chart object.
     * All inheritance classes call this constructor.
     *
     * @param data The processed data
     */
    public Chart(ChartData data, ChartType chartType) {
    	this.chartType = chartType;
    	
        if (data != null) {
            this.process(data);
            this.setChart(data);
        }
    }

	/**
     * Show the data in the chart.
     *
     * @param data the processed data
     */
    protected abstract void process(ChartData data);

    /**
     * Sets the chart object.
     */
    protected abstract void setChart(ChartData data);

    /**
     * Returns the chart title.
     *
     * @return the title of the chart
     */
    public String getChartTitle() {
    	return this.chartTitle;
    }

    /**
     * Returns the chart object.
     *
     * @return JFreeChart the chart object
     */
    public final JFreeChart getChart() {
        return this.chart;
    }
    
    /**
     * Returns the chart type.
     * @return Chart type 
     */
	public ChartType getChartType() {
		return this.chartType;
	}
}


