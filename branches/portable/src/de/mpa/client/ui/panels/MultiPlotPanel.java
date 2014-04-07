package de.mpa.client.ui.panels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import de.mpa.io.MascotGenericFile;

public class MultiPlotPanel extends JPanel {

	// list of spectra, typically contains two elements
	private ArrayList<MascotGenericFile> spectra;
	
	// max amount of highest peaks to highlight 
	private int k;
	
	// flag to determine whether upper and lower part should be normalized separately
	private boolean normalizeSeparately = false;

	// padding
	private int padX = 5, padY = 5;
	// background color
	private Color bgColor = Color.WHITE;
	// list of line colors
	private ArrayList<Color> lineColors;
	
	public MultiPlotPanel() {
		initComponents();
	}
	
	private void initComponents() {
		// generate default colors
		lineColors = new ArrayList<Color>();
		lineColors.add(Color.RED);
		lineColors.add(Color.BLUE);
		
		// generate context menu
		final JPopupMenu mPlotPopup = new JPopupMenu();
		final JCheckBoxMenuItem normSepItem = new JCheckBoxMenuItem("Normalize separately", false);
		normSepItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				repaint(normSepItem.getState());
			}
		});
		final JCheckBoxMenuItem highlightItem = new JCheckBoxMenuItem("Highlight only " + Math.abs(k) + " most intensive peaks", true);
		highlightItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				k = -k;
				repaint();
			}
		});
	    mPlotPopup.add(normSepItem);
	    mPlotPopup.add(highlightItem);
	    
		this.addMouseListener(new MouseAdapter() {
		    public void mousePressed(MouseEvent e) {
		        maybeShowPopup(e);
		    }

		    public void mouseReleased(MouseEvent e) {
		        maybeShowPopup(e);
		    }
			private void maybeShowPopup(MouseEvent e) {
		        if (e.isPopupTrigger()) {
		            mPlotPopup.show(e.getComponent(),
		                       e.getX(), e.getY());
		        }
		    }
		});
	}

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

	public boolean isNormalizedSeparately() {
		return normalizeSeparately;
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
	
	public void setFirstSpectrum(MascotGenericFile spec) {
		if (this.spectra != null) {
			this.spectra.set(0, spec);
		} else {
			this.spectra = new ArrayList<MascotGenericFile>();
			this.spectra.add(spec);
		}
	}
	
	public void setSecondSpectrum(MascotGenericFile spec) {
		if (this.spectra != null) {
			if (this.spectra.size() >= 2) {
				this.spectra.set(1, spec);
			} else {
				this.spectra.add(spec);
			}
		} else {
			this.spectra = new ArrayList<MascotGenericFile>();
			this.spectra.add(null);
			this.spectra.add(spec);
		}
	}
	
	/**
	 * Repaints the plot panel with the option to normalize the upper and lower spectra separately.
	 * @param normalizeSeparately boolean flag to determine normalization behavior.
	 */
	public void repaint(boolean normalizeSeparately) {
		this.normalizeSeparately = normalizeSeparately;
		repaint();
	}

	@Override
	protected void paintComponent( Graphics g )
	{
		super.paintComponent( g );

		g.setColor(bgColor);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		// determine axis limits
		double minX = Double.MAX_VALUE, maxX = 0, maxY = 0;
		int k = this.k;
		if (this.spectra != null) {
			for (MascotGenericFile spectrum : this.spectra) {
				if (spectrum != null) {
//					ArrayList<Peak> peaks = spectrum.getPeakList();
					TreeMap<Double, Double> peaks = new TreeMap<Double, Double>(spectrum.getPeaks());
					// adjust k downwards if needed
					if (k > 0) {
						k = Math.min(k, peaks.size());
					}
					// assuming masses are ordered; first in list = min, last = max
//					minX = Math.min(minX, peaks.get(0).getMz());
//					maxX = Math.max(maxX, peaks.get(peaks.size()-1).getMz());
					minX = Math.min(minX, peaks.firstKey());
					maxX = Math.max(maxX, peaks.lastKey());
					if (!normalizeSeparately) {
						maxY = Math.max(maxY, spectrum.getHighestIntensity());
					}
				}
			}
		}

		// plot spectra
		if (this.spectra != null) {
			int i = 0;
			for (MascotGenericFile spectrum : this.spectra) {
				if ((spectrum != null) && (lineColors.get(i) != null)) {
					if (normalizeSeparately) {
						maxY = spectrum.getHighestIntensity();
					}
//					ArrayList<Peak> peaks = spectrum.getPeakList();
					HashMap<Double, Double> peaks = new HashMap<Double, Double>(spectrum.getPeaks());
//					ArrayList<Peak> hPeak;
					HashMap<Double, Double> hPeaks;
					if (k > 0) {
						hPeaks = spectrum.getHighestPeaks(k);
					} else {
						hPeaks = peaks;
					}
//					for (Peak peak : peaks) {
					for (Double mz : peaks.keySet()) {
						// skip peak if it's part of the list of highlighted peaks
//						if (hPeak.contains(peak)) {
						if (hPeaks.containsKey(mz)) {
							continue;
						}
						// highlight k highest peaks by brightening all others
						g.setColor(new Color((lineColors.get(i).getRed()  +7*255)/8,
								 (lineColors.get(i).getGreen()+7*255)/8,
								 (lineColors.get(i).getBlue() +7*255)/8));
//						g.drawLine((int)((peak.getMz()-minX)/(maxX-minX)*(getWidth()-2*padX)+padX),
//									    (getHeight()/2),
//								   (int)((peak.getMz()-minX)/(maxX-minX)*(getWidth()-2*padX)+padX),
//								   (int)(getHeight()/2+((i%2)*2-1)*peak.getIntensity()/maxY*(getHeight()/2-padY)));
						g.drawLine((int)((mz-minX)/(maxX-minX)*(getWidth()-2*padX)+padX),
							    		(getHeight()/2),
							       (int)((mz-minX)/(maxX-minX)*(getWidth()-2*padX)+padX),
							       (int)(getHeight()/2+((i%2)*2-1)*peaks.get(mz)/maxY*(getHeight()/2-padY)));
					}
//					for (Peak peak : hPeak) {
					for (Double mz : hPeaks.keySet()) {
						// plot highlighted peaks on top of other peaks
						g.setColor(lineColors.get(i));
//						g.drawLine((int)((peak.getMz()-minX)/(maxX-minX)*(getWidth()-2*padX)+padX),
//							    	    (getHeight()/2),
//							       (int)((peak.getMz()-minX)/(maxX-minX)*(getWidth()-2*padX)+padX),
//							       (int)(getHeight()/2+((i%2)*2-1)*peak.getIntensity()/maxY*(getHeight()/2-padY)));
						g.drawLine((int)((mz-minX)/(maxX-minX)*(getWidth()-2*padX)+padX),
					    	    		(getHeight()/2),
					    	       (int)((mz-minX)/(maxX-minX)*(getWidth()-2*padX)+padX),
					    	       (int)(getHeight()/2+((i%2)*2-1)*peaks.get(mz)/maxY*(getHeight()/2-padY)));
					}
				}
				i++;
			}
		}
	}
	
}