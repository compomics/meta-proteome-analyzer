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
     * Histogram chart object.
     */
    protected JFreeChart chart = null;
    
    /**
     * Chart title.
     */
    protected String chartTitle = "";
    
    /**
     * Creates an instance of the Chart object.
     * All inheritance classes call this constructor.
     *
     * @param data The processed data
     */
    public Chart(Object data) {
        if (data != null) {
            process(data);
            setChart();
        }
    }

    /**
     * Show the data in the chart.
     *
     * @param data the processed data
     */
    protected abstract void process(Object data);

    /**
     * Sets the chart object.
     */
    protected abstract void setChart();

    /**
     * Returns the chart title.
     *
     * @return the title of the chart
     */
    public abstract String getChartTitle();

    /**
     * Returns the chart object.
     *
     * @return JFreeChart the chart object
     */
    public final JFreeChart getChart() {
        return chart;
    }
}


