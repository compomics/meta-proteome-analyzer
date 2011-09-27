package de.mpa.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.TreeSet;

import javax.swing.JPanel;

import de.mpa.io.MascotGenericFile;

public class PlotPanel extends JPanel {
	
	private MascotGenericFile Spectrum;
	
	public void setSpectrum(MascotGenericFile spec) {
		this.Spectrum = spec;
//		Spectrum.
	}
	
	public MascotGenericFile getSpectrum() {
		return this.Spectrum;
	}

	@Override
	protected void paintComponent( Graphics g )
	{
//		Graphics2D g2 = (Graphics2D) g;
		super.paintComponent( g );
		g.setColor(new Color(255,255,255));
//		System.out.println(g.toString());
		g.fillRect(10, 10, 300, 200);
		if (this.Spectrum != null) {
			// grab peaks and draw stuff
			HashMap<Double, Double> peaks = this.Spectrum.getPeaks();
			TreeSet<Double> sortedPeaks = new TreeSet<Double>(peaks.keySet());
			double minX = sortedPeaks.first();
			double maxX = sortedPeaks.last();
			double maxY = this.Spectrum.getHighestIntensity();
			g.setColor(new Color(0,0,0));
			for (Double mz : sortedPeaks) {
				g.drawLine((int)((mz-minX)/(maxX-minX)*290+15), 205,
						   (int)((mz-minX)/(maxX-minX)*290+15), (int)(205-peaks.get(mz)/maxY*190));
			}
		}
	}
}