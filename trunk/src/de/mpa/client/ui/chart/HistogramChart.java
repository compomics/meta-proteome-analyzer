package de.mpa.client.ui.chart;

import org.jfree.chart.ChartFactory;
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
    
    public enum HistChartType implements ChartType {
    	TOTAL_ION_HIST;

		@Override
		public String getTitle() {
			return "";
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
//    	// List of all total intensities
//        List<Double> ticList = new ArrayList<Double>();
//        
//		if (data instanceof SpectrumData) {
//			SpectrumData spectrumData = (SpectrumData) data;
//			filename = spectrumData.getFilename();
//
//			List<MascotGenericFile> spectra = spectrumData.getSpectra();
//			for (MascotGenericFile mgf : spectra) {
//				ticList.add(mgf.getTotalIntensity());
//			}
//		} else if (data instanceof List<?>) {
//			filename = "default";
//			ticList = (List<Double>) data;
//		}
//              
//        // Set data.
//        dataArray = new double[ticList.size()];
//        for (int i = 0; i < ticList.size(); i++) {
//            dataArray[i] = ticList.get(i);
//        }
    }

    @Override
    protected void setChart() {
        chart = ChartFactory.createHistogram(getChartTitle(),
                "Total Ion Count",
                "Rel. Frequency",
                histDataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false);

        
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundAlpha(0f);
        plot.setDomainGridlinesVisible(false);
        plot.setRangeGridlinesVisible(false);
        //plot.setDomainAxis(new LogarithmicAxis("Total Ion Count"));
        plot.setOutlineVisible(false);
        XYBarRenderer renderer = new XYBarRenderer();
        renderer.setShadowVisible(false);
        plot.setRenderer(renderer);
    }

    @Override
    public String getChartTitle() {
        return null;
    }
}

