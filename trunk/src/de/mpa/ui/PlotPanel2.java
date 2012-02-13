package de.mpa.ui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import com.compomics.util.gui.spectrum.SpectrumPanel;
import com.compomics.util.interfaces.SpectrumFile;

public class PlotPanel2 extends SpectrumPanel {
	
	private SpectrumFile iSpecFile;

	public PlotPanel2(SpectrumFile aSpecFile) {
		super(aSpecFile);
		this.showResolution = false;
	}

	@ Override
	public void setSpectrumFile(SpectrumFile aSpecFile) {
		this.iSpecFile = aSpecFile;
		super.setSpectrumFile(aSpecFile);
	}

	public void setSpectrumFile(SpectrumFile aSpecFile, Color lineCol) {
		
		if (aSpecFile == null) {
			clearSpectrumFile();
		} else {
			this.iSpecFile = aSpecFile;

			iXAxisData = new ArrayList<double[]>();
			iYAxisData = new ArrayList<double[]>();

			iDataPointAndLineColor = new ArrayList<Color>();
			iDataPointAndLineColor.add(lineCol);
			iAreaUnderCurveColor = new ArrayList<Color>();
			iAreaUnderCurveColor.add(Color.PINK);

			HashMap peaks = aSpecFile.getPeaks();

			iXAxisData.add(new double[peaks.size()]);
			iYAxisData.add(new double[peaks.size()]);

			iFilename = aSpecFile.getFilename();

			// Maximum intensity of the peaks.
			double maxInt = 0.0;

			// TreeSets are sorted.
			TreeSet masses = new TreeSet(peaks.keySet());
			Iterator iter = masses.iterator();

			int count = 0;

			while (iter.hasNext()) {
				Double key = (Double) iter.next();
				iXAxisData.get(0)[count] = key.doubleValue();
				iYAxisData.get(0)[count] = ((Double) peaks.get(key)).doubleValue();
				count++;
			}

			this.rescale(0.0, getMaxXAxisValue()*1.05);

			this.iPrecursorMZ = aSpecFile.getPrecursorMZ();
			int liTemp = aSpecFile.getCharge();

			if (liTemp == 0) {
				iPrecursorCharge = "?";
			} else {
				iPrecursorCharge = Integer.toString(liTemp);
				iPrecursorCharge += (liTemp > 0 ? "+" : "-");
			}
		}
	}
	
	public void clearSpectrumFile() {

		dataSetCounter = 0;
		this.iSpecFile = null;
		iXAxisData = new ArrayList<double[]>();
		iYAxisData = new ArrayList<double[]>();
		iXAxisData.add(new double[] {0.0});
		iYAxisData.add(new double[] {90.909});
		iDataPointAndLineColor = new ArrayList<Color>();
		iDataPointAndLineColor.add(Color.BLACK);
		iAreaUnderCurveColor = new ArrayList<Color>();
		iAreaUnderCurveColor.add(Color.PINK);
		iFilename = "no file selected";
		this.rescale(0.0, 999.0);
		this.iPrecursorMZ = 0.0;
	
		iPrecursorCharge = "?";
				
	}

	public SpectrumFile getSpectrumFile() {
		return iSpecFile;
	}
	
	@Override
	protected void addListeners() { }

}
