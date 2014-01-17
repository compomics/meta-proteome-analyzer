package de.mpa.algorithms;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import de.mpa.algorithms.similarity.NormalizedDotProduct;
import de.mpa.algorithms.similarity.Transformation;
import de.mpa.algorithms.similarity.VectorizationFactory;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;

public class NormalizedDotProductTest extends TestCase {

	/**
	 * The k highest peaks.
	 */
	int k = 20;
	
	/**
	 * Peak map of source spectrum.
	 */
	private Map<Double, Double> srcPeaks;
	
	/**
	 * Peak map of target spectrum.
	 */
	private Map<Double, Double> trgPeaks;

	/**
	 * Sets up necessary variables
	 */
	@Before
	public void setUp() {
		try {
			// Parse .mgf file
			File file = new File(getClass().getClassLoader().getResource("Test_BSA_10.mgf").getPath());
			MascotGenericFileReader mgfReader = new MascotGenericFileReader(file);

			// Get source spectrum and candidate spectrum
			MascotGenericFile source = mgfReader.getSpectrumFiles().get(0);
			MascotGenericFile target = mgfReader.getSpectrumFiles().get(6);

			// Get spectrum peaks of source spectrum and candidate spectrum
			srcPeaks = source.getHighestPeaks(k);
			trgPeaks = target.getHighestPeaks(k);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testNormalizedDotProduct() {
		// Set up comparator algorithm
		NormalizedDotProduct comparator = new NormalizedDotProduct(
				VectorizationFactory.createDirectBinning(1.0, 0.0),
				Transformation.NONE);
		// Calculate similarity
		comparator.prepare(srcPeaks);
		comparator.compareTo(trgPeaks);

		assertEquals(0.76, comparator.getSimilarity(), 0.01);

	}
}
