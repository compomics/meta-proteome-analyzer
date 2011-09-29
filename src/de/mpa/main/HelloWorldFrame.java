package de.mpa.main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;
import de.mpa.ui.MgfFilter;
import de.mpa.ui.MultiPlotPanel;
import de.mpa.ui.PlotPanel;

public class HelloWorldFrame {
	
	static void addComponent( Container cont,
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

	public static void main(String[] args) {
		
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
	    
	    final JFileChooser fc = new JFileChooser(pathName);
	    	fc.setFileFilter(new MgfFilter());
	    	fc.setAcceptAllFileFilterUsed(false);
	    
	    final JButton 	 bLoadA = new JButton("Load");
	    final JTextField tFileA = new JTextField();
	    final JButton    bBrowA = new JButton("Browse...");

		final PlotPanel  pPlotA = new PlotPanel();
			pPlotA.setPreferredSize(new Dimension(300, 200));
		
		final JComboBox<String> cBoxA = new JComboBox<String>();

	    
	    final JButton    bLoadB = new JButton("Load");
	    final JTextField tFileB = new JTextField();
	    final JButton    bBrowB = new JButton("Browse...");
		
		final PlotPanel  pPlotB = new PlotPanel();
			pPlotB.setPreferredSize(new Dimension(300, 200));
		
		final JComboBox<String> cBoxB = new JComboBox<String>();
		
		final MultiPlotPanel mPlot = new MultiPlotPanel();
			mPlot.setPreferredSize(new Dimension(300, 200));
			ArrayList<Color> colors = new ArrayList<Color>();
			colors.add(new Color(0,0,255,128));		// translucent blue
			colors.add(new Color(255,0,0,128));		// translucent red
			mPlot.setLineColors(colors);
		
		// here be gui listeners
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
				JComboBox<String> selectedChoice = (JComboBox<String>) e.getSource();
				int index = selectedChoice.getSelectedIndex();
				if (selectedChoice == cBoxA) {
					if ((index != -1) && (spectrumFilesA != null)) {
						pPlotA.setSpectrum(spectrumFilesA.get(index));
						pPlotA.repaint();
					}
				} else {
					if ((index != -1) && (spectrumFilesB != null)) {
						pPlotB.setSpectrum(spectrumFilesB.get(index));
						pPlotB.repaint();
					}
				}
				ArrayList<MascotGenericFile> spectra = new ArrayList<MascotGenericFile>();
				if (pPlotA.getSpectrum() != null) {
					spectra.add(pPlotA.getSpectrum());
				}
				if (pPlotB.getSpectrum() != null) {
					spectra.add(pPlotB.getSpectrum());
				}
				mPlot.setSpectra(spectra);
				mPlot.repaint();
			}
		};
		cBoxA.addActionListener(alCBox);
		cBoxB.addActionListener(alCBox);
	    
	    ActionListener alLoad = new ActionListener() {
	    	@Override public void actionPerformed( ActionEvent e ) {
	    		File file = new File("");
				JComboBox<String> cBox = new JComboBox<String>();
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
	    		
	    		if ( file.exists() == true) {
	    			try {
						MascotGenericFileReader reader = new MascotGenericFileReader(file);
			    		if (spectrumFiles.isEmpty() == false) {
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
	    		} else if (file.getPath().isEmpty() == false) {
	    			System.out.println("File not found!");
	    		}
	    	}
	    };
	    bLoadA.addActionListener(alLoad);
	    tFileA.addActionListener(alLoad);
	    bLoadB.addActionListener(alLoad);
	    tFileB.addActionListener(alLoad);
	    
	    // add stuff to frame and fire her up!
		addComponent( c, gbl, bLoadA,  0,  0, 1, 1,  0 ,  0 , GridBagConstraints.NONE, 		 new Insets(5,5,5,5));
		addComponent( c, gbl, tFileA, -1,  0, 1, 1, 1.0,  0 , GridBagConstraints.HORIZONTAL, new Insets(5,0,5,5));
		addComponent( c, gbl, bBrowA, -1,  0, 1, 1,  0 ,  0 , GridBagConstraints.NONE, 		 new Insets(5,0,5,5));
		addComponent( c, gbl, pPlotA,  0, -1, 3, 3, 1.0, 1.0, GridBagConstraints.BOTH, 		 new Insets(0,5,5,5));
		addComponent( c, gbl,  cBoxA,  0, -1, 3, 1, 1.0,  0 , GridBagConstraints.HORIZONTAL, new Insets(0,5,5,5));

		addComponent( c, gbl, bLoadB,  4,  0, 1, 1,  0 ,  0 , GridBagConstraints.NONE, 		 new Insets(5,0,5,5));
		addComponent( c, gbl, tFileB, -1,  0, 1, 1, 1.0,  0 , GridBagConstraints.HORIZONTAL, new Insets(5,0,5,5));
		addComponent( c, gbl, bBrowB, -1,  0, 1, 1,  0 ,  0 , GridBagConstraints.NONE, 		 new Insets(5,0,5,5));
		addComponent( c, gbl, pPlotB,  4, -1, 3, 3, 1.0, 1.0, GridBagConstraints.BOTH, 		 new Insets(0,0,5,5));
		addComponent( c, gbl,  cBoxB,  4, -1, 3, 1, 1.0,  0 , GridBagConstraints.HORIZONTAL, new Insets(0,0,5,5));
		
		addComponent( c, gbl, mPlot,  0, -1, 3, 3, 1.0, 1.0, GridBagConstraints.BOTH, 		 new Insets(0,5,5,5));

		frame.pack();
		frame.setVisible(true);
		
		// obligatory phrase
		System.out.println("Hello World!");		
	}
}
