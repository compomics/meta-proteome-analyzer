package de.mpa.client.ui.panels;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import com.compomics.util.gui.spectrum.SpectrumPanel;
import com.compomics.util.interfaces.SpectrumFile;

@SuppressWarnings("serial")
public class PlotPanel2 extends SpectrumPanel {
	
	private SpectrumFile iSpecFile;

	public PlotPanel2(SpectrumFile aSpecFile) {
		super(aSpecFile);
        showResolution = false;
	}

	@Override
	public void setSpectrumFile(SpectrumFile aSpecFile) {
        iSpecFile = aSpecFile;
		super.setSpectrumFile(aSpecFile);
	}

	@SuppressWarnings("unchecked")
	public void setSpectrumFile(SpectrumFile aSpecFile, Color lineCol) {
		
		if (aSpecFile == null) {
            this.clearSpectrumFile();
		} else {
            iSpecFile = aSpecFile;

            this.iXAxisData = new ArrayList<double[]>();
            this.iYAxisData = new ArrayList<double[]>();

            this.iDataPointAndLineColor = new ArrayList<Color>();
            this.iDataPointAndLineColor.add(lineCol);
            this.iAreaUnderCurveColor = new ArrayList<Color>();
            this.iAreaUnderCurveColor.add(Color.PINK);

			@SuppressWarnings("rawtypes")
			HashMap peaks = aSpecFile.getPeaks();

            this.iXAxisData.add(new double[peaks.size()]);
            this.iYAxisData.add(new double[peaks.size()]);

            this.iFilename = aSpecFile.getFilename();

//			// Maximum intensity of the peaks.
//			double maxInt = 0.0;

			// TreeSets are sorted.
			TreeSet<Double> masses = new TreeSet<Double>(peaks.keySet());
			@SuppressWarnings("rawtypes")
			Iterator iter = masses.iterator();

			int count = 0;

			while (iter.hasNext()) {
				Double key = (Double) iter.next();
                this.iXAxisData.get(0)[count] = key.doubleValue();
                this.iYAxisData.get(0)[count] = ((Double) peaks.get(key)).doubleValue();
				count++;
			}

            rescale(0.0, this.getMaxXAxisValue()*1.05);

            iPrecursorMZ = aSpecFile.getPrecursorMZ();
			int liTemp = aSpecFile.getCharge();

			if (liTemp == 0) {
                this.iPrecursorCharge = "?";
			} else {
                this.iPrecursorCharge = Integer.toString(liTemp);
                this.iPrecursorCharge += (liTemp > 0 ? "+" : "-");
			}
		}
	}
	
	public void clearSpectrumFile() {

        this.dataSetCounter = 0;
        iSpecFile = null;
        this.iXAxisData = new ArrayList<double[]>();
        this.iYAxisData = new ArrayList<double[]>();
        this.iXAxisData.add(new double[] {0.0});
        this.iYAxisData.add(new double[] {90.909});
        this.iDataPointAndLineColor = new ArrayList<Color>();
        this.iDataPointAndLineColor.add(Color.BLACK);
        this.iAreaUnderCurveColor = new ArrayList<Color>();
        this.iAreaUnderCurveColor.add(Color.PINK);
        this.iFilename = "no file selected";
        rescale(0.0, 999.0);
        iPrecursorMZ = 0.0;

        this.iPrecursorCharge = "?";
				
	}

	public SpectrumFile getSpectrumFile() {
		return this.iSpecFile;
	}
	
	@Override
	protected void addListeners() { }

}
