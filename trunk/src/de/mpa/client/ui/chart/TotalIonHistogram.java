package de.mpa.client.ui.chart;

import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;

import de.mpa.io.MascotGenericFile;

/**
 * <p>Class to plot a histogram of the number of peaks of all intensity values.</p>
 *
 * @author T.Muth
 * @date 22-06-2012
 */
public class TotalIonHistogram extends Chart {
    private double[] dataArray;
    private String filename;

    /**
     * Constructs a denovo score histogram
     *
     * @param resultData
     */
    public TotalIonHistogram(Object data) {
        super(data);
    }

    @Override
    protected void process(Object data) {
    	// List of all total intensities
        List<Double> ticList = new ArrayList<Double>();
        
    	if(data instanceof SpectrumData){
    		SpectrumData spectrumData = (SpectrumData) data;
    		filename = spectrumData.getFilename();
    		
    		List<MascotGenericFile> spectra = spectrumData.getSpectra();
    		for (MascotGenericFile mgf : spectra) {
				ticList.add(mgf.getTotalIntensity());
			}
    	}
              
        // Set data.
        dataArray = new double[ticList.size()];
        for (int i = 0; i < ticList.size(); i++) {
            dataArray[i] = ticList.get(i);
        }
        setChart();
    }

    @Override
    protected void setChart() {
        HistogramDataset dataset = new HistogramDataset();
        dataset.setType(HistogramType.FREQUENCY);
        
        // Dynamically set the bin size 
        // TODO: use a better formula for the bin size...
        int binSize = (int) Math.log10(dataArray.length) * 10;
        
        dataset.addSeries(filename, dataArray, binSize);
        chart = ChartFactory.createHistogram(getChartTitle(),
                "Total Ion Count",
                "Frequency",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false);

        
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundAlpha(0f);
        plot.setDomainGridlinesVisible(false);
        plot.setRangeGridlinesVisible(false);
        plot.setOutlineVisible(false);
        XYBarRenderer renderer = new XYBarRenderer();
        renderer.setShadowVisible(false);
        plot.setRenderer(renderer);
    }

    @Override
    public String getChartTitle() {
        return "";
    }
}

