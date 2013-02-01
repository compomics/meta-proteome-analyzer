package de.mpa.client.ui.panels;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.Map.Entry;
import java.util.TreeMap;

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
		super.paintComponent( g );
		
		FontMetrics fm = g.getFontMetrics();
		
		g.setColor(bgColor);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		g.setColor(Color.BLACK);
		g.drawLine(10*padX, this.getHeight()-2*padY, 10*padX, padY);
		g.drawLine(10*padX, this.getHeight()-2*padY, this.getWidth()-padX, this.getHeight()-2*padY);
				
		if (this.spectrum != null) {
			// grab peaks and draw stuff
			TreeMap<Double, Double> peaks = new TreeMap<Double, Double>(this.spectrum.getPeaks());
			double minX = peaks.firstKey();
			double maxX = peaks.lastKey();
			double maxY = this.spectrum.getHighestIntensity();
			
			double pow  = Math.floor(Math.log10(maxY));
			for (int i = 0; i < maxY; i += Math.pow(10, pow)) {
				int posY = (int)(getHeight()-3*padY-i/maxY*(getHeight()-4*padY));
				String s = Integer.toString((int)i);
				g.drawLine( 9*padX, posY, 10*padX, posY);
				g.drawString(s, 8*padX-fm.stringWidth(s), posY+fm.getAscent()/2);
			}
			
			g.setColor(lineColor);
			for (Entry<Double, Double> peak : peaks.entrySet()) {
				g.drawLine((int)((peak.getKey()-minX)/(maxX-minX)*(getWidth()-12*padX)+11*padX),
						   		(getHeight()-3*padY),
						   (int)((peak.getKey()-minX)/(maxX-minX)*(getWidth()-12*padX)+11*padX),
						   (int)(getHeight()-3*padY-peak.getValue()/maxY*(getHeight()-4*padY)));
//				String s = Double.toString(Math.round(peak.getValue()*100.0)/100.0);
//				g.drawString(s,
//							 (int)((peak.getMz()-minX)/(maxX-minX)*(getWidth()-4*padX)+3*padX-g.getFontMetrics().stringWidth(s)/2),
//							 (int)(getHeight()-3*padY-peak.getIntensity()/maxY*(getHeight()-4*padY))-5);				
			}
		}
	}
	
	
//	@Override
//	protected void paintComponent( Graphics g )
//	{
//		Graphics2D g2 = (Graphics2D) g;
//		super.paintComponent( g );
//
//		g2.rotate(-Math.PI/2,this.getHeight()/2.0,this.getHeight()/2.0);
//		
//		g.setColor(bgColor);
//		g.fillRect(0, 0, this.getHeight(), this.getWidth());
//		
//		g.setColor(Color.BLACK);
//		g.drawRect(0, 0, this.getHeight(), this.getWidth()-1);
//		
//		if (this.spectrum != null) {
//			ArrayList<Peak> peaks = this.spectrum.getPeakList();
//			double minX = peaks.get(0).getMz();
//			double maxX = peaks.get(peaks.size()-1).getMz();
//			double maxY = this.spectrum.getHighestIntensity();
//			g.setColor(lineColor);
//			for (Peak peak : peaks) {
//				g.drawLine(		(padY),
//					       (int)((peak.getMz()-minX)/(maxX-minX)*(getWidth()-2*padX)+padX),
//					       (int)(padY+peak.getIntensity()/maxY*(getHeight()-2*padY)),
//						   (int)((peak.getMz()-minX)/(maxX-minX)*(getWidth()-2*padX)+padX));
//				g.drawString(Double.toString(Math.round(peak.getIntensity()*100.0)/100.0),
//							 (int)(padY+peak.getIntensity()/maxY*(getHeight()-2*padY))+5,
//							 (int)((peak.getMz()-minX)/(maxX-minX)*(getWidth()-2*padX)+padX+5));
//			}
//		}
//	}

}