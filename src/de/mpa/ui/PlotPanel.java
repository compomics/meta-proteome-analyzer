package de.mpa.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.TreeSet;

import javax.swing.JPanel;

import de.mpa.io.MascotGenericFile;

public class PlotPanel extends JPanel {
	
	private MascotGenericFile spectrum;
	private int padX = 5, padY = 5;
	private Color lineColor = Color.BLACK, bgColor = Color.WHITE;


	public MascotGenericFile getSpectrum() {
		return spectrum;
	}
	public void setSpectrum(MascotGenericFile spectrum) {
		this.spectrum = spectrum;
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

	public Color getLineColor() {
		return lineColor;
	}
	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}

	public Color getBackgroundColor() {
		return bgColor;
	}
	public void setBackgroundColor(Color bgColor) {
		this.bgColor = bgColor;
	}
	

	@Override
	protected void paintComponent( Graphics g )
	{
//		Graphics2D g2 = (Graphics2D) g;
		super.paintComponent( g );

		g.setColor(bgColor);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		if (this.spectrum != null) {
			// grab peaks and draw stuff
			HashMap<Double, Double> peaks = this.spectrum.getPeaks();
			TreeSet<Double> sortedPeaks = new TreeSet<Double>(peaks.keySet());
			double minX = sortedPeaks.first();
			double maxX = sortedPeaks.last();
			double maxY = this.spectrum.getHighestIntensity();
			g.setColor(lineColor);
			for (Double mz : sortedPeaks) {
				g.drawLine((int)((mz-minX)/(maxX-minX)*(getWidth()-2*padX)+padX),
							    (getHeight()-padY),
						   (int)((mz-minX)/(maxX-minX)*(getWidth()-2*padX)+padX),
						   (int)(getHeight()-padY-peaks.get(mz)/maxY*(getHeight()-2*padY)));
			}
		}
	}
}