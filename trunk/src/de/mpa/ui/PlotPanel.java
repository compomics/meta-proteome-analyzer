package de.mpa.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.TreeSet;

import javax.swing.JPanel;

import de.mpa.io.MascotGenericFile;

public class PlotPanel extends JPanel {
	
	private MascotGenericFile Spectrum;
	private int padX = 5, padY = 5;		// NOTE: add get()/set()
	
	public void setSpectrum(MascotGenericFile spec) {
		this.Spectrum = spec;
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
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		if (this.Spectrum != null) {
			// grab peaks and draw stuff
			HashMap<Double, Double> peaks = this.Spectrum.getPeaks();
			TreeSet<Double> sortedPeaks = new TreeSet<Double>(peaks.keySet());
			double minX = sortedPeaks.first();
			double maxX = sortedPeaks.last();
			double maxY = this.Spectrum.getHighestIntensity();
			g.setColor(new Color(0,0,0));
			for (Double mz : sortedPeaks) {
				g.drawLine((int)((mz-minX)/(maxX-minX)*(getWidth()-2*padX)+padX),
							    (getHeight()-padY),
						   (int)((mz-minX)/(maxX-minX)*(getWidth()-2*padX)+padX),
						   (int)(getHeight()-padY-peaks.get(mz)/maxY*(getHeight()-2*padY)));
			}
		}
	}
}