package de.mpa.client.ui;

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

@SuppressWarnings("serial")
public class MultiPlotPanel extends JPanel {

	// list of spectra, typically contains two elements
	private ArrayList<MascotGenericFile> spectra;
	
	// max amount of highest peaks to highlight 
	private int k;
	
	// flag to determine whether upper and lower part should be normalized separately
	private boolean normalizeSeparately;

	// padding
	private int padX = 5, padY = 5;
	// background color
	private Color bgColor = Color.WHITE;
	// list of line colors
	private ArrayList<Color> lineColors;
	
	public MultiPlotPanel() {
        this.initComponents();
	}
	
	private void initComponents() {
		// generate default colors
        this.lineColors = new ArrayList<Color>();
        this.lineColors.add(Color.RED);
        this.lineColors.add(Color.BLUE);
		
		// generate context menu
		JPopupMenu mPlotPopup = new JPopupMenu();
		JCheckBoxMenuItem normSepItem = new JCheckBoxMenuItem("Normalize separately", false);
		normSepItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
                MultiPlotPanel.this.repaint(normSepItem.getState());
			}
		});
		JCheckBoxMenuItem highlightItem = new JCheckBoxMenuItem("Highlight only " + Math.abs(this.k) + " most intensive peaks", true);
		highlightItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
                MultiPlotPanel.this.k = -MultiPlotPanel.this.k;
                MultiPlotPanel.this.repaint();
			}
		});
	    mPlotPopup.add(normSepItem);
	    mPlotPopup.add(highlightItem);

        addMouseListener(new MouseAdapter() {
		    public void mousePressed(MouseEvent e) {
                this.maybeShowPopup(e);
		    }

		    public void mouseReleased(MouseEvent e) {
                this.maybeShowPopup(e);
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
		return this.spectra;
	}
	public void setSpectra(ArrayList<MascotGenericFile> spectra) {
		this.spectra = spectra;
	}

	public int getK() {
		return this.k;
	}
	public void setK(int k) {
		this.k = k;
	}

	public boolean isNormalizedSeparately() {
		return this.normalizeSeparately;
	}

	public int getPadX() {
		return this.padX;
	}
	public void setPadX(int padX) {
		this.padX = padX;
	}

	public int getPadY() {
		return this.padY;
	}
	public void setPadY(int padY) {
		this.padY = padY;
	}

	public Color getBgColor() {
		return this.bgColor;
	}
	public void setBgColor(Color bgColor) {
		this.bgColor = bgColor;
	}

	public ArrayList<Color> getLineColors() {
		return this.lineColors;
	}
	public void setLineColors(ArrayList<Color> lineColors) {
		this.lineColors = lineColors;
	}
	
	public void setFirstSpectrum(MascotGenericFile spec) {
		if (spectra != null) {
            spectra.set(0, spec);
		} else {
            spectra = new ArrayList<MascotGenericFile>();
            spectra.add(spec);
		}
	}
	
	public void setSecondSpectrum(MascotGenericFile spec) {
		if (spectra != null) {
			if (spectra.size() >= 2) {
                spectra.set(1, spec);
			} else {
                spectra.add(spec);
			}
		} else {
            spectra = new ArrayList<MascotGenericFile>();
            spectra.add(null);
            spectra.add(spec);
		}
	}
	
	/**
	 * Repaints the plot panel with the option to normalize the upper and lower spectra separately.
	 * @param normalizeSeparately boolean flag to determine normalization behavior.
	 */
	public void repaint(boolean normalizeSeparately) {
		this.normalizeSeparately = normalizeSeparately;
        this.repaint();
	}

	@Override
	protected void paintComponent( Graphics g )
	{
		super.paintComponent( g );

		g.setColor(this.bgColor);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		// determine axis limits
		double minX = Double.MAX_VALUE, maxX = 0, maxY = 0;
		int k = this.k;
		if (spectra != null) {
			for (MascotGenericFile spectrum : spectra) {
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
					if (!this.normalizeSeparately) {
						maxY = Math.max(maxY, spectrum.getHighestIntensity());
					}
				}
			}
		}

		// plot spectra
		if (spectra != null) {
			int i = 0;
			for (MascotGenericFile spectrum : spectra) {
				if ((spectrum != null) && (this.lineColors.get(i) != null)) {
					if (this.normalizeSeparately) {
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
						g.setColor(new Color((this.lineColors.get(i).getRed()  +7*255)/8,
								 (this.lineColors.get(i).getGreen()+7*255)/8,
								 (this.lineColors.get(i).getBlue() +7*255)/8));
//						g.drawLine((int)((peak.getMz()-minX)/(maxX-minX)*(getWidth()-2*padX)+padX),
//									    (getHeight()/2),
//								   (int)((peak.getMz()-minX)/(maxX-minX)*(getWidth()-2*padX)+padX),
//								   (int)(getHeight()/2+((i%2)*2-1)*peak.getIntensity()/maxY*(getHeight()/2-padY)));
						g.drawLine((int)((mz-minX)/(maxX-minX)*(this.getWidth()-2* this.padX)+ this.padX),
							    		(this.getHeight()/2),
							       (int)((mz-minX)/(maxX-minX)*(this.getWidth()-2* this.padX)+ this.padX),
							       (int)(this.getHeight()/2+((i%2)*2-1)*peaks.get(mz)/maxY*(this.getHeight()/2- this.padY)));
					}
//					for (Peak peak : hPeak) {
					for (Double mz : hPeaks.keySet()) {
						// plot highlighted peaks on top of other peaks
						g.setColor(this.lineColors.get(i));
//						g.drawLine((int)((peak.getMz()-minX)/(maxX-minX)*(getWidth()-2*padX)+padX),
//							    	    (getHeight()/2),
//							       (int)((peak.getMz()-minX)/(maxX-minX)*(getWidth()-2*padX)+padX),
//							       (int)(getHeight()/2+((i%2)*2-1)*peak.getIntensity()/maxY*(getHeight()/2-padY)));
						g.drawLine((int)((mz-minX)/(maxX-minX)*(this.getWidth()-2* this.padX)+ this.padX),
					    	    		(this.getHeight()/2),
					    	       (int)((mz-minX)/(maxX-minX)*(this.getWidth()-2* this.padX)+ this.padX),
					    	       (int)(this.getHeight()/2+((i%2)*2-1)*peaks.get(mz)/maxY*(this.getHeight()/2- this.padY)));
					}
				}
				i++;
			}
		}
	}
	
}