package de.mpa.algorithms;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;
import de.mpa.db.DBManager;
import de.mpa.db.extractor.SpectrumExtractor;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;

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
			HashMap<Double, Double> highestLibPeaks = libMGF.getHighestPeaks(k);
//			ArrayList<Peak> highestLibPeaks = libMGF.getHighestPeaksList(k);
			
			// Highest peaks from the target (test) spectrum
			HashMap<Double, Double> highestSpectrumPeaks = libMGF.getHighestPeaks(k);
//			ArrayList<Peak> highestSpectrumPeaks = mgf.getHighestPeaksList(k);
			
			NormalizedDotProduct comparator = new NormalizedDotProduct(
					new Vectorization(Vectorization.DIRECT_BINNING, 1.0),
					new Transformation() { public double transform(double input) { return input; } });
			comparator.prepare(highestLibPeaks);
			comparator.compareTo(highestSpectrumPeaks);
			System.out.println("Similarity of " + mgf.getFilename() + " to " + libMGF.getFilename() + " : " + comparator.getSimilarity());
		}
	   }
	   
   }
}
