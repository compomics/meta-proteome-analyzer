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
				double mass = key.doubleValue();
				double intensity = ((Double) peaks.get(key)).doubleValue();
				if (intensity > maxInt) {
					maxInt = intensity;
				}
				//            iXAxisData.get(dataSetCounter)[count] = mass;
				//            iYAxisData.get(dataSetCounter)[count] = intensity;
				iXAxisData.get(0)[count] = mass;
				iYAxisData.get(0)[count] = intensity;
				count++;
			}

			if (iXAxisStartAtZero) {
				this.rescale(0.0, getMaxXAxisValue());
			} else {
				this.rescale(getMinXAxisValue(), getMaxXAxisValue());
			}

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
		
		this.iSpecFile = null;

		iXAxisData = new ArrayList<double[]>();
		iYAxisData = new ArrayList<double[]>();

		iDataPointAndLineColor = new ArrayList<Color>();
		iAreaUnderCurveColor = new ArrayList<Color>();

		iFilename = "none";

		this.iPrecursorMZ = 0;
		
		iPrecursorCharge = "?";
		
	}

	public SpectrumFile getSpectrumFile() {
		return iSpecFile;
	}

}
