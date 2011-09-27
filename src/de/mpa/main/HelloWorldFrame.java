package de.mpa.main;


import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;

import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;
import de.mpa.ui.PlotPanel;

public class HelloWorldFrame {
	public static void main(String[] args) {
		JFrame frame = new JFrame("Titel");
		frame.setBounds(new Rectangle(100,100,630,480));
		frame.setResizable(false);
		
		String pathName = "test/de/mpa/resources/";
		String fileName = "QSTAR/Test.mgf";
		
		PlotPanel p = new PlotPanel();
		frame.add(p);
		
		MascotGenericFileReader reader;
		try {
			reader = new MascotGenericFileReader(new File(pathName + fileName));
	    	List<MascotGenericFile> spectrumFiles = reader.getSpectrumFiles();
	    	p.setSpectrum(spectrumFiles.get(2));
	    	p.repaint();
		} catch (Exception e) {
            e.printStackTrace();
        }

		frame.setVisible(true);
		
		System.out.println("Hello World!");		
	}
}
