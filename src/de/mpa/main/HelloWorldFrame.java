package de.mpa.main;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.compomics.util.interfaces.SpectrumFile;

import de.mpa.algorithms.NormalizedDotProduct;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;
import de.mpa.io.Peak;
import de.mpa.ui.MgfFilter;
import de.mpa.ui.MultiPlotPanel;
import de.mpa.ui.PlotPanel2;

public class HelloWorldFrame {
	
	private static int k = 5;
	
	private static void addComponent( Container cont,
							  GridBagLayout gbl,
							  Component c,
							  int x, int y,
							  int width, int height,
							  double weightx, double weighty,
							  int fill, Insets insets) {
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = x;			gbc.gridy = y;
		gbc.gridwidth = width;	gbc.gridheight = height;
		gbc.weightx = weightx;	gbc.weighty = weighty;
		gbc.fill = fill;
		gbc.insets = insets;
		gbl.setConstraints( c, gbc );
		cont.add( c );
	}
	
	private static void updateScore(int k, PlotPanel2 pPlotA, PlotPanel2 pPlotB, MultiPlotPanel mPlot, JLabel lScore) {
		
//		ArrayList<MascotGenericFile> spectra = new ArrayList<MascotGenericFile>();
//		if (pPlotA.getSpectrum() != null) {
//			spectra.add(pPlotA.getSpectrum());
//		}
//		if (pPlotB.getSpectrum() != null) {
//			spectra.add(pPlotB.getSpectrum());
//		}
		
		
		ArrayList<MascotGenericFile> spectra = new ArrayList<MascotGenericFile>();
		
		MascotGenericFile specA = (MascotGenericFile) pPlotA.getSpectrumFile();
		if (specA != null) {
			spectra.add(specA);
		}
		MascotGenericFile specB = (MascotGenericFile) pPlotB.getSpectrumFile();
		if (specB != null) {
			spectra.add(specB);
		}
		mPlot.setSpectra(spectra);
		mPlot.setK(k);
		mPlot.repaint();
		
		if (spectra.size() == 2) {
			if (specA.getPeakList().size() < k) {
				k = specA.getPeakList().size();
			}
			if (specB.getPeakList().size() < k) {
				k = specB.getPeakList().size();
			}

			ArrayList<Peak> highestPeaksA = spectra.get(0).getHighestPeaks(k);
			ArrayList<Peak> highestPeaksB = spectra.get(1).getHighestPeaks(k);
			
			double deltaMz = 0.5;
			NormalizedDotProduct method = new NormalizedDotProduct(deltaMz);
			method.compare(highestPeaksA, highestPeaksB);
//			int numBins = 1000;
//			CrossCorrelation method = new CrossCorrelation(numBins);
//			method.compare(spectra.get(0).getPeakList(), spectra.get(1).getPeakList());
								
			DecimalFormat df = new DecimalFormat("0.00");
			df.setGroupingUsed(false);
			lScore.setText(df.format(method.getSimilarity()*100) + "%");
		}
		
	}

	public static void main(String[] args) {
		
		try {
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
		}
		catch( Exception e ) { e.printStackTrace(); }
		
		String pathName = "test/de/mpa/resources/";
		
		final ArrayList<MascotGenericFile> spectrumFilesA = new ArrayList<MascotGenericFile>();
		final ArrayList<MascotGenericFile> spectrumFilesB = new ArrayList<MascotGenericFile>();
		
    	// here be gui elements
		JFrame frame = new JFrame("Spectrotron 2000");
	    	frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
	    	frame.setLocation(100, 100);
	    	frame.setPreferredSize(new Dimension(1280, 480));

	    Container c = frame.getContentPane();
	    GridBagLayout gbl = new GridBagLayout();
	    	c.setLayout( gbl );
	    
	    // file chooser for .mgf files
	    final JFileChooser fc = new JFileChooser(pathName);
	    	fc.setFileFilter(new MgfFilter());
	    	fc.setAcceptAllFileFilterUsed(false);
	    
	    // buttons and textfield for file browsing and loading
	    final JButton 	 bLoadA = new JButton("Load");
	    final JTextField tFileA = new JTextField();
	    final JButton    bBrowA = new JButton("Browse...");

	    // left-hand side plot panel
	    final PlotPanel2 pPlotA = new PlotPanel2(null);
			pPlotA.setPreferredSize(new Dimension(300, 200));
		
		// combo box to select from list of loaded spectra
		final JComboBox  cBoxA = new JComboBox();

	    // same as above, for right-hand side
	    final JButton    bLoadB = new JButton("Load");
	    final JTextField tFileB = new JTextField();
	    final JButton    bBrowB = new JButton("Browse...");
		
		final PlotPanel2 pPlotB = new PlotPanel2(null);
			pPlotB.setPreferredSize(new Dimension(300, 200));
		
		final JComboBox cBoxB = new JComboBox();
		
		// plot panel to incorporate multiple graphs, highlighting picked peaks
		final MultiPlotPanel mPlot = new MultiPlotPanel();
			mPlot.setPreferredSize(new Dimension(300, 200));
			mPlot.setBorder(tFileA.getBorder());
			ArrayList<Color> colors = new ArrayList<Color>();
			colors.add(Color.BLUE);
			colors.add(Color.RED);
			mPlot.setLineColors(colors);
			mPlot.setK(k);

		// combo box to select pre-set methods for picking parameter k
		final JComboBox cBoxK = new JComboBox(new String[] {
				"custom",
				"pMZ/50",
				"all"
		});
		final JLabel     lK0 = new JLabel("k = ", SwingConstants.LEFT);
		// spinner to edit parameter k by hand
		final JSpinner spinK = new JSpinner( new SpinnerNumberModel(k, 1, 10, 1));

		// labels for score display
		final JLabel    lS0 = new JLabel("s =", SwingConstants.LEFT);
			lS0.setVerticalAlignment(SwingConstants.TOP);
		final JLabel lScore = new JLabel("Hello World!", SwingConstants.RIGHT);
		 	lScore.setVerticalAlignment(SwingConstants.TOP);
		
		// gui listeners for various elements
	    ActionListener alBrow = new ActionListener() {
	    	@Override public void actionPerformed( ActionEvent e ) {
	    		int res = fc.showOpenDialog((Component) e.getSource());
	    		if (res == JFileChooser.APPROVE_OPTION) {
	    			if (e.getSource() == bBrowA) {
	    				tFileA.setText(fc.getSelectedFile().getPath());
	    			} else {
	    				tFileB.setText(fc.getSelectedFile().getPath());
	    			}
	    		}
	    	}
	    };
	    bBrowA.addActionListener(alBrow);
	    bBrowB.addActionListener(alBrow);
			
		ActionListener alCBox = new ActionListener() {
			@Override public void actionPerformed( ActionEvent e ) {
				JComboBox selectedChoice = (JComboBox) e.getSource();
				int index = selectedChoice.getSelectedIndex();
				
				// determine maximum number of peaks to pick for vectorization
				int maxK = Integer.MAX_VALUE;				
				
				if (spectrumFilesA.size() > 0) {
					maxK = Math.min(maxK, spectrumFilesA.get(index).getPeakList().size());
				}
				if (spectrumFilesB.size() > 0) {
					maxK = Math.min(maxK, spectrumFilesB.get(index).getPeakList().size());
				}
				if ((k > maxK)|| (cBoxK.getSelectedIndex() == 2)) {
					k = maxK;
					spinK.setValue(k);
				}
				// update spinner maximum
				((SpinnerNumberModel)spinK.getModel()).setMaximum(maxK);
				
				
				// determine which combo box triggered the event and update plots
				if (selectedChoice == cBoxA) {
					if ((index != -1) && (spectrumFilesA != null)) {
//						pPlotA.setSpectrum(spectrumFilesA.get(index));
						pPlotA.setSpectrumFile(spectrumFilesA.get(index),Color.BLUE);
						pPlotA.repaint();
					}
				} else {
					if ((index != -1) && (spectrumFilesB != null)) {
//						pPlotB.setSpectrum(spectrumFilesB.get(index));
						pPlotB.setSpectrumFile(spectrumFilesB.get(index),Color.RED);
						pPlotB.repaint();
					}
				}
				
				// try recalculating similarity score
				updateScore(k, pPlotA, pPlotB, mPlot, lScore);
			}
		};
		cBoxA.addActionListener(alCBox);
		cBoxB.addActionListener(alCBox);

	    ActionListener alLoad = new ActionListener() {
	    	@Override public void actionPerformed( ActionEvent e ) {
	    		File file = new File("");
				JComboBox cBox = new JComboBox();
				ArrayList<MascotGenericFile> spectrumFiles = new ArrayList<MascotGenericFile>();
		    	if ((e.getSource() == bLoadA) ||
		    		(e.getSource() == tFileA)) {
		    		file = new File(tFileA.getText());
		    		cBox = cBoxA;
		    		spectrumFiles = spectrumFilesA;
		    	} else {
		    		file = new File(tFileB.getText());
		    		cBox = cBoxB;
		    		spectrumFiles = spectrumFilesB;
		    	}
	    		
	    		if ( file.exists() ) {
	    			try {
						MascotGenericFileReader reader = new MascotGenericFileReader(file);
			    		if (!spectrumFiles.isEmpty()) {
			    			spectrumFiles.clear();
			    		}
			    		spectrumFiles.addAll(new ArrayList<MascotGenericFile>(reader.getSpectrumFiles()));
				    	
				    	cBox.removeAllItems();
				    	for (MascotGenericFile mgf : spectrumFiles) {
				    		cBox.addItem(mgf.getFilename());
						}
				    	cBox.setSelectedIndex(0);
	    			} catch (Exception x) {
	    	            x.printStackTrace();
	    	        }
	    		} else if (!file.getPath().isEmpty()) {
	    			System.out.println("File not found!");
	    		}
	    	}
	    };
	    bLoadA.addActionListener(alLoad);
	    tFileA.addActionListener(alLoad);
	    bLoadB.addActionListener(alLoad);
	    tFileB.addActionListener(alLoad);
	    
	    ActionListener alCBoxK = new ActionListener() {
			@Override public void actionPerformed( ActionEvent e ) {
				int index = ((JComboBox)e.getSource()).getSelectedIndex();
				SpectrumFile specA = pPlotA.getSpectrumFile();
				switch (index) {
				case 1:
					if (specA != null) {
						k = (int)Math.round(specA.getPrecursorMZ()/50.0);
						spinK.setValue(k);
					} else {
						cBoxK.setSelectedIndex(0);
					}
					break;
				case 2:
					k = Integer.parseInt(((SpinnerNumberModel)spinK.getModel()).getMaximum().toString());
					spinK.setValue(k);
					break;
				default:
					break;
				}
				updateScore(k, pPlotA, pPlotB, mPlot, lScore);
			}
		};
		cBoxK.addActionListener(alCBoxK);
	    
	    ChangeListener clSpinK = new ChangeListener() {
			@Override public void stateChanged(ChangeEvent e) {
				int val = (Integer) spinK.getValue();
				SpinnerNumberModel snm = (SpinnerNumberModel) spinK.getModel();
				if (snm.getMaximum().compareTo(val) == 0) {
					cBoxK.setSelectedIndex(2);
				} else {
					cBoxK.setSelectedIndex(0);
				}
				k = val;
				updateScore(val, pPlotA, pPlotB, mPlot, lScore);
			}
		};
		spinK.addChangeListener(clSpinK);
	    
	    // add stuff to frame and fire her up!
		addComponent( c, gbl, bLoadA,  0,  0, 1, 1,  0 ,  0 , GridBagConstraints.NONE, 		 new Insets(5,5,5,5));
		addComponent( c, gbl, tFileA, -1,  0, 1, 1, 1.0,  0 , GridBagConstraints.HORIZONTAL, new Insets(5,0,5,5));
		addComponent( c, gbl, bBrowA, -1,  0, 2, 1,  0 ,  0 , GridBagConstraints.HORIZONTAL, new Insets(5,0,5,5));
		addComponent( c, gbl, pPlotA,  0, -1, 4, 3, 1.0, 1.0, GridBagConstraints.BOTH, 		 new Insets(0,5,5,5));
		addComponent( c, gbl,  cBoxA,  0, -1, 4, 1, 1.0,  0 , GridBagConstraints.HORIZONTAL, new Insets(0,5,5,5));

		addComponent( c, gbl, bLoadB,  5,  0, 1, 1,  0 ,  0 , GridBagConstraints.NONE, 		 new Insets(5,0,5,5));
		addComponent( c, gbl, tFileB, -1,  0, 1, 1, 1.0,  0 , GridBagConstraints.HORIZONTAL, new Insets(5,0,5,5));
		addComponent( c, gbl, bBrowB, -1,  0, 2, 1,  0 ,  0 , GridBagConstraints.HORIZONTAL, new Insets(5,0,5,5));
		addComponent( c, gbl, pPlotB,  5, -1, 4, 3, 1.0, 1.0, GridBagConstraints.BOTH, 		 new Insets(0,0,5,5));
		addComponent( c, gbl,  cBoxB,  5, -1, 4, 1, 1.0,  0 , GridBagConstraints.HORIZONTAL, new Insets(0,0,5,5));
		
		addComponent( c, gbl,  mPlot,  0, -1, 2, 3, 1.0, 1.0, GridBagConstraints.BOTH, 		 new Insets(0,5,5,5));

		addComponent( c, gbl,  cBoxK,  2,  5, 2, 1,  0 ,  0 , GridBagConstraints.HORIZONTAL, new Insets(0,0,5,5));
		addComponent( c, gbl,    lK0,  2,  6, 1, 1,  0 ,  0 , GridBagConstraints.HORIZONTAL, new Insets(0,0,5,0));
		addComponent( c, gbl,  spinK,  3,  6, 1, 1,  0 ,  0 , GridBagConstraints.HORIZONTAL, new Insets(0,0,5,5));
		addComponent( c, gbl,    lS0,  2,  7, 1, 1,  0 ,  0 , GridBagConstraints.BOTH, 		 new Insets(0,0,5,0));
		addComponent( c, gbl, lScore,  3,  7, 1, 1,  0 ,  0 , GridBagConstraints.BOTH,		 new Insets(0,0,5,5));

		frame.pack();
		frame.setVisible(true);
		
	}
}
