package de.mpa.main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;

public class Test {
	public static void main(String[] args) {
		try {
			MascotGenericFileReader reader = new MascotGenericFileReader(new File("test.mgf"));
			List<MascotGenericFile> spectrumFiles = reader.getSpectrumFiles();
			
			for (MascotGenericFile file : spectrumFiles) {
				System.out.println("Filename: " + file.getFilename());
				System.out.println("Highest intensity: " + file.getHighestIntensity());
				System.out.println("Total intensity: " + file.getTotalIntensity());
			}
			
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
}
