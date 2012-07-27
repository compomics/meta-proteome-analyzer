package de.mpa.client.ui.chart;

import java.awt.Color;
import java.awt.Paint;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.data.category.CategoryDataset;

import de.mpa.util.ColorUtils;

public class TopBarChart extends Chart {
	
	public enum TopBarChartType implements ChartType {
		PROTEINS, SPECIES, ENZYMES
	}

	private CategoryDataset categoryDataset;
		
	/**
     * Constructs the TopBarChart.
     *
     * @param data Input data.
     * @param chartType Chart type.
     */
    public TopBarChart(Object data, ChartType chartType) {
        super(data, chartType);
    }

	@Override
	protected void process(Object data) {
		if(data instanceof TopData) {
			TopData topData = (TopData) data;
			categoryDataset = topData.getDataset();
		}
	}

	@Override
	protected void setChart() {
		TopBarChartType topBarChartType = (TopBarChartType) chartType;
		
		switch (topBarChartType) {
		case PROTEINS:
			chartTitle = "Top10 Proteins Chart";
			chart = ChartFactory.createBarChart3D(chartTitle, "Proteins", "No. Spectra", categoryDataset, PlotOrientation.VERTICAL, false, true, false);
			break;
		case SPECIES:
			chartTitle = "Top10 Species Chart";
			chart = ChartFactory.createBarChart3D(chartTitle, "Species", "No. Spectra", categoryDataset, PlotOrientation.VERTICAL, false, true, false);
			break;
		case ENZYMES:
			chartTitle = "Top10 Enzymes Chart";
			chart = ChartFactory.createBarChart3D(chartTitle, "Enzymes", "No. Spectra", categoryDataset, PlotOrientation.VERTICAL, false, true, false);
			break;
		default:
			break;
		}
		
    	chart.setTextAntiAlias(true);
		// set background color
		chart.getPlot().setBackgroundPaint(Color.WHITE);
		chart.setBackgroundPaint(Color.WHITE);
		CustomBarRenderer3D barRenderer3d = new CustomBarRenderer3D();
		barRenderer3d.setDrawBarOutline(false);    
		barRenderer3d.setShadowVisible(false);
		//barRenderer3d.setSeriesPaint(0, new Color(100, 150, 255));
        chart.getCategoryPlot().setRenderer(0, barRenderer3d);
         
		 // remove space before/after the domain axis
//        chart.getCategoryPlot().getDomainAxis().setUpperMargin(0);
//        chart.getCategoryPlot().getDomainAxis().setLowerMargin(0);
	}
	
	private class CustomBarRenderer3D extends BarRenderer3D {

        public CustomBarRenderer3D() {
        }

        public Paint getItemPaint(int row, int column) {
        	Color[] colors = ColorUtils.getRainbowGradient(10);
            	return colors[column];
        }
    }
}
