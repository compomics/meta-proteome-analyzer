package de.mpa.algorithms;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import junit.framework.TestCase;

import org.junit.Test;

import de.mpa.algorithms.similarity.Transformation;
import de.mpa.algorithms.similarity.VectorizationFactory;
import de.mpa.db.DBManager;
import de.mpa.db.extractor.SpectrumExtractor;
import de.mpa.interfaces.SpectrumComparator;
import de.mpa.io.MascotGenericFile;

/**
 * Testbed class for spectral similarity calculations.
 * 
 * @author A. Behne
 */
public class SpectralSimilarityTest extends TestCase {

	/**
	 * Compares two spectra fetched from the remote database.
	 * @throws SQLException if a database error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Test
	public void testSpectralSimilarity() throws SQLException, IOException {
		Connection conn = DBManager.getInstance().getConnection();

		MascotGenericFile mgfA = SpectrumExtractor.getMascotGenericFile(157140, conn);
		MascotGenericFile mgfB = SpectrumExtractor.getMascotGenericFile(227424, conn);
		
		SpectrumComparator measure = new NormalizedDotProduct(VectorizationFactory.createDirectBinning(1.0, 0.0), Transformation.SQRT);
//		SpectrumComparator measure = new CrossCorrelation(VectorizationFactory.createDirectBinning(1.0, 0.0), Transformation.SQRT, 1.0, 75);
		
		measure.prepare(mgfA.getPeaks());
		measure.compareTo(mgfB.getPeaks());
		
		assertEquals(0.2962, measure.getSimilarity(), 1e-4);
	}
	
}
