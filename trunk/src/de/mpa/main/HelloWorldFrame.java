package de.mpa.main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;
import de.mpa.ui.MgfFilter;
import de.mpa.ui.PlotPanel;

public class HelloWorldFrame {
	
	static void addComponent( Container cont,
							  GridBagLayout gbl,
							  Component c,
							  int x, int y,
							  int width, int height,
							  double weightx, double weighty,
							  int fill, Insets insets)
	{
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
		String fileName = "QSTAR/Test.mgf";
		
		JFrame frame = new JFrame("Spectrotron 2000");
	    frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setLocation(100, 100);

	    Container c = frame.getContentPane();

	    GridBagLayout gbl = new GridBagLayout();
	    c.setLayout( gbl );
	    
	    final JFileChooser fc = new JFileChooser(pathName);
	    fc.setFileFilter(new MgfFilter());
	    fc.setAcceptAllFileFilterUsed(false);
	    
	    final JButton bLoadA = new JButton("Load");
	    
	    final JTextField tFileA = new JTextField();
	    
	    JButton bBrowA = new JButton("Browse...");
	    bBrowA.addActionListener(new ActionListener() {
	    	@Override public void actionPerformed( ActionEvent e ) {
	    		int res = fc.showOpenDialog((Component) e.getSource());
	    		if (res == JFileChooser.APPROVE_OPTION) {
	    			tFileA.setText(fc.getSelectedFile().getPath());
	    		}
	    	}
	    } );
	    

		final PlotPanel p = new PlotPanel();
		p.setPreferredSize(new Dimension(300, 200));
		p.setName("P1");
		
		final List<MascotGenericFile> spectrumFilesA;		
    	Vector<String> spectrumNames = new Vector<String>();
		final JComboBox<String> cA = new JComboBox<String>(spectrumNames);
		try {
			MascotGenericFileReader reader = new MascotGenericFileReader(new File(pathName + fileName));
	    	spectrumFilesA = reader.getSpectrumFiles();
	    	for (MascotGenericFile mgf : spectrumFilesA) {
				spectrumNames.add(mgf.getFilename());
			}
	    	p.setSpectrum(spectrumFilesA.get(0));
	    	
	    	cA.setSelectedIndex(0);
			
	    	cA.addActionListener( new ActionListener() {
	    		@Override public void actionPerformed( ActionEvent e ) {
	    			JComboBox<String> selectedChoice = (JComboBox<String>) e.getSource();
	    			int index = selectedChoice.getSelectedIndex();
	    			if (index != -1) {
	    				p.setSpectrum(spectrumFilesA.get(index));
	    				p.repaint();
	    			}
	    		}
	    	} );
		} catch (Exception e) {
            e.printStackTrace();
        }
	    
	    final JButton bLoadB = new JButton("Load");
	    
	    final JTextField tFileB = new JTextField();
	    
	    JButton bBrowB = new JButton("Browse...");
	    bBrowB.addActionListener(new ActionListener() {
	    	@Override public void actionPerformed( ActionEvent e ) {
	    		int res = fc.showOpenDialog((Component) e.getSource());
	    		if (res == JFileChooser.APPROVE_OPTION) {
	    			tFileB.setText(fc.getSelectedFile().getPath());
	    		}
	    	}
	    } );
		
		final PlotPanel q = new PlotPanel();
		q.setPreferredSize(new Dimension(300, 200));
		q.setName("P2");

		final List<MascotGenericFile> spectrumFilesB;
    	spectrumNames = new Vector<String>();
		final JComboBox<String> cB = new JComboBox<String>(spectrumNames);
		try {
			MascotGenericFileReader reader = new MascotGenericFileReader(new File(pathName + "MALDI/1A1.mgf"));
			spectrumFilesB = reader.getSpectrumFiles();
	    	for (MascotGenericFile mgf : spectrumFilesB) {
				spectrumNames.add(mgf.getFilename());
			}
	    	q.setSpectrum(spectrumFilesB.get(0));
	    	
	    	cB.setSelectedIndex(0);
			
			cB.addActionListener( new ActionListener() {
				  @Override public void actionPerformed( ActionEvent e )
				  {
				    JComboBox<String> selectedChoice = (JComboBox<String>) e.getSource();
	    			int index = selectedChoice.getSelectedIndex();
	    			if (index != -1) {
	    				q.setSpectrum(spectrumFilesB.get(index));
	    				q.repaint();
	    			}
				  }
				} );
		} catch (Exception e) {
            e.printStackTrace();
        }
	    
	    ActionListener alLoad = new ActionListener() {
	    	@Override public void actionPerformed( ActionEvent e ) {
	    		File file = new File(tFileA.getText());
	    		if ( file.exists() == true) {
	    			try {
	    				PlotPanel s = new PlotPanel();
						MascotGenericFileReader reader = new MascotGenericFileReader(file);
						List<MascotGenericFile> spectrumFiles = reader.getSpectrumFiles();
						JComboBox<String> c = new JComboBox<String>();
				    	if ((e.getSource().equals(bLoadA) == true) ||
				    		(e.getSource().equals(tFileA) == true)) {
				    		System.out.println("hmmm");
				    		c = cA;		s = p;
				    	} else if ((e.getSource().equals(bLoadB) == true) ||
					    		   (e.getSource().equals(tFileB) == true)) {
				    		System.out.println("yay");
				    		c = cB;		s = q;
				    	}
				    	c.removeAllItems();
				    	for (MascotGenericFile mgf : spectrumFiles) {
				    		c.addItem(mgf.getFilename());
						}
				    	c.setSelectedIndex(0);
				    	s.setSpectrum(spectrumFiles.get(0));
				    	s.repaint();
	    			} catch (Exception x) {
	    	            x.printStackTrace();
	    	        }
	    		} else {
	    			System.out.println("File not found!");
	    		}
	    	}
	    };

	    bLoadA.addActionListener(alLoad);
	    tFileA.addActionListener(alLoad);
	    bLoadB.addActionListener(alLoad);
	    tFileB.addActionListener(alLoad);

		addComponent( c, gbl, bLoadA,  0,  0, 1, 1,  0 ,  0 , GridBagConstraints.NONE, 		 new Insets(5,5,5,5));
		addComponent( c, gbl, tFileA, -1,  0, 1, 1, 1.0,  0 , GridBagConstraints.HORIZONTAL, new Insets(5,0,5,5));
		addComponent( c, gbl, bBrowA, -1,  0, 1, 1,  0 ,  0 , GridBagConstraints.NONE, 		 new Insets(5,0,5,5));
		addComponent( c, gbl,    p  ,  0, -1, 3, 3, 1.0, 1.0, GridBagConstraints.BOTH, 		 new Insets(0,5,5,5));
		addComponent( c, gbl,   cA  ,  0, -1, 3, 1, 1.0,  0 , GridBagConstraints.HORIZONTAL, new Insets(0,5,5,5));

		addComponent( c, gbl, bLoadB,  4,  0, 1, 1,  0 ,  0 , GridBagConstraints.NONE, 		 new Insets(5,0,5,5));
		addComponent( c, gbl, tFileB, -1,  0, 1, 1, 1.0,  0 , GridBagConstraints.HORIZONTAL, new Insets(5,0,5,5));
		addComponent( c, gbl, bBrowB, -1,  0, 1, 1,  0 ,  0 , GridBagConstraints.NONE, 		 new Insets(5,0,5,5));
		addComponent( c, gbl,    q  ,  4, -1, 3, 3, 1.0, 1.0, GridBagConstraints.BOTH, 		 new Insets(0,0,5,5));
		addComponent( c, gbl,   cB  ,  4, -1, 3, 1, 1.0,  0 , GridBagConstraints.HORIZONTAL, new Insets(0,0,5,5));

		frame.pack();
		frame.setVisible(true);
		
		System.out.println("Hello World!");		
	}
}
