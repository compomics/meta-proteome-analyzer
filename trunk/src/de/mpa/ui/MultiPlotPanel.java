package de.mpa.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import javax.swing.JPanel;

import de.mpa.io.MascotGenericFile;
import de.mpa.io.Peak;

public class MultiPlotPanel extends JPanel {

	private ArrayList<MascotGenericFile> spectra;
	private int k;
	
	private int padX = 5, padY = 5;
	private Color bgColor = Color.WHITE;
	private ArrayList<Color> lineColors;
		
	
	public ArrayList<MascotGenericFile> getSpectra() {
		return spectra;
	}
	public void setSpectra(ArrayList<MascotGenericFile> spectra) {
		this.spectra = spectra;
	}

	public int getK() {
		return k;
	}
	public void setK(int k) {
		this.k = k;
	}

	public int getPadX() {
		return padX;
	}
	public void setPadX(int padX) {
		this.padX = padX;
	}

	public int getPadY() {
		return padY;
	}
	public void setPadY(int padY) {
		this.padY = padY;
	}

	public Color getBgColor() {
		return bgColor;
	}
	public void setBgColor(Color bgColor) {
		this.bgColor = bgColor;
	}

	public ArrayList<Color> getLineColors() {
		return lineColors;
	}
	public void setLineColors(ArrayList<Color> lineColors) {
		this.lineColors = lineColors;
	}
	

	@Override
	protected void paintComponent( Graphics g )
	{
		super.paintComponent( g );

		g.setColor(bgColor);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		// determine axis limits
		double minX = Double.MAX_VALUE, maxX = 0, maxY = 0;
		if (this.spectra != null) {
			for (MascotGenericFile spectrum : this.spectra) {
				if (spectrum != null) {
					ArrayList<Peak> peaks = spectrum.getPeakList();
					// assuming masses are ordered; first in list = min, last = max
					minX = Math.min(minX, peaks.get(0).getMz());
					maxX = Math.max(maxX, peaks.get(peaks.size()-1).getMz());
					maxY = Math.max(maxY, spectrum.getHighestIntensity());
				}
			}
		}

		// plot spectra
		if (this.spectra != null) {
			int i = 0;
			for (MascotGenericFile spectrum : this.spectra) {
				if ((spectrum != null) && (lineColors.get(i) != null)) {
					ArrayList<Peak> peaks = spectrum.getPeakList();
					ArrayList<Peak> hPeak = spectrum.getHighestPeaks(this.k);
					for (Peak peak : peaks) {
						// highlight k highest peaks by brightening all others
						if (hPeak.contains(peak)) {
							g.setColor(lineColors.get(i));
						} else {
							g.setColor(new Color((lineColors.get(i).getRed()  +7*255)/8,
												 (lineColors.get(i).getGreen()+7*255)/8,
												 (lineColors.get(i).getBlue() +7*255)/8));
						}
						g.drawLine((int)((peak.getMz()-minX)/(maxX-minX)*(getWidth()-2*padX)+padX),
									    (getHeight()/2),
								   (int)((peak.getMz()-minX)/(maxX-minX)*(getWidth()-2*padX)+padX),
								   (int)(getHeight()/2+((i%2)*2-1)*peak.getIntensity()/maxY*(getHeight()/2-padY)));
					}				//					^^^^^^^^^
				}					// even/odd index => plot on top/bottom half
				i++;
			}
		}
	}
	
}