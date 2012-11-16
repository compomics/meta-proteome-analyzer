package de.mpa.algorithms;

import java.io.File;
import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import de.mpa.algorithms.similarity.Transformation;
import de.mpa.algorithms.similarity.Vectorization;
import de.mpa.algorithms.similarity.VectorizationFactory;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;

public class CrossCorrelationTest extends TestCase {
	
	MascotGenericFile spectrumA, spectrumB;
	
	@Before
	public void setUp() {
		try {
			File mgfFile = new File("test/de/mpa/resources/Test_30.mgf");
			MascotGenericFileReader mgfReader = new MascotGenericFileReader(mgfFile);
			List<MascotGenericFile> spectrumFiles = mgfReader.getSpectrumFiles();
			spectrumA = spectrumFiles.get(1);
			spectrumB = spectrumFiles.get(10);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testCrossCorrelation() {
		double binWidth = 1.0;
		Vectorization vect = VectorizationFactory.createDirectBinning(binWidth, 0.0);
		Transformation trafo = Transformation.SQRT;
		CrossCorrelation method = new CrossCorrelation(vect, trafo, binWidth, 75);
		method.prepare(spectrumA.getPeaks());
		method.compareTo(spectrumB.getPeaks());
		assertEquals(0.018876804386547614, method.getSimilarity(), 1e-6);
	}

}
