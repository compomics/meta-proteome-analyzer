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
    protected JFreeChart chart = null;
    
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
            process(data);
            setChart(data);
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
    	return chartTitle;
    }

    /**
     * Returns the chart object.
     *
     * @return JFreeChart the chart object
     */
    public final JFreeChart getChart() {
        return chart;
    }
    
    /**
     * Returns the chart type.
     * @return Chart type 
     */
	public ChartType getChartType() {
		return chartType;
	}
}


