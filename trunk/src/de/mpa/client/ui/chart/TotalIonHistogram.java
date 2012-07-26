package de.mpa.client.ui.chart;

import java.util.ArrayList;
import java.util.List;

import no.uib.jsparklines.renderers.JSparklines3dTableCellRenderer.PlotType;

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
    
    public enum HistChartType implements ChartType {
    	TOTAL_ION_HIST
    }
    /**
     * Constructs a denovo score histogram
     *
     * @param resultData
     */
    public TotalIonHistogram(Object data, ChartType chartType) {
        super(data, chartType);
    }

    @Override
    protected void process(Object data) {
    	// List of all total intensities
        List<Double> ticList = new ArrayList<Double>();
        
		if (data instanceof SpectrumData) {
			SpectrumData spectrumData = (SpectrumData) data;
			filename = spectrumData.getFilename();

			List<MascotGenericFile> spectra = spectrumData.getSpectra();
			for (MascotGenericFile mgf : spectra) {
				ticList.add(mgf.getTotalIntensity());
			}
		} else if (data instanceof List<?>) {
			filename = "default";
			ticList = (List<Double>) data;
		}
              
        // Set data.
        dataArray = new double[ticList.size()];
        for (int i = 0; i < ticList.size(); i++) {
            dataArray[i] = ticList.get(i);
        }
    }

    @Override
    protected void setChart() {
        HistogramDataset dataset = new HistogramDataset();
        dataset.setType(HistogramType.RELATIVE_FREQUENCY);
        
        // Dynamically set the bin size 
        // TODO: use a better formula for the bin size...
        dataset.addSeries(filename, dataArray, 40);
        chart = ChartFactory.createHistogram(getChartTitle(),
                "Total Ion Count",
                "Rel. Frequency",
                dataset,
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
        return "";
    }
}

