package de.mpa.algorithms;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import de.mpa.db.DBManager;
import de.mpa.db.extractor.SpectrumExtractor;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;
import de.mpa.io.Peak;

public class NormalizedDotProductTest extends TestCase{
	
   private Connection conn;

	public NormalizedDotProductTest() {
		try {
			DBManager dbManager = new DBManager();
			conn = dbManager.getConnection();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
   
   public void testCompare() throws IOException, SQLException{
	   SpectrumExtractor specExtractor = new SpectrumExtractor(conn);	   
	   File file = new File(getClass().getClassLoader().getResource("MALDI/1A1.mgf").getPath());
	   MascotGenericFileReader mgfReader = new MascotGenericFileReader(file);
	   List<MascotGenericFile> spectra = mgfReader.getSpectrumFiles();
	   
	   // The k-highest peaks
	   int k = 20;	   
	   
	   for (MascotGenericFile mgf : spectra) {
		   // Get the library spectra
		   List<LibrarySpectrum> libSpectra = specExtractor.getLibrarySpectra(mgf.getPrecursorMZ(), 0.5);
		   for (LibrarySpectrum libSpectrum : libSpectra) {
			MascotGenericFile libMGF = libSpectrum.getSpectrumFile();
			
			// Highest peaks from the library spectrum
			ArrayList<Peak> highestLibPeaks = libMGF.getHighestPeaks(k);
			
			// Highest peaks from the target (test) spectrum
			ArrayList<Peak> highestSpectrumPeaks = mgf.getHighestPeaks(k);
			
			NormalizedDotProduct method = new NormalizedDotProduct(0.5);
			method.compare(highestLibPeaks, highestSpectrumPeaks);
			System.out.println("Similarity of " + mgf.getFilename() + " to " + libMGF.getFilename() + " : " + method.getSimilarity());
		}
	   }
	   
   }
}
