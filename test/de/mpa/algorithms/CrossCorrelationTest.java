package de.mpa.algorithms;

import java.io.File;
import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;

public class CrossCorrelationTest extends TestCase {
	
	MascotGenericFile spectrumA, spectrumB;
	int numTests = 1000;
	
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
		Vectorization vect = new Vectorization(Vectorization.DIRECT_BINNING, 1.0);
		Transformation trafo = new Transformation() { public double transform(double input) { return Math.sqrt(input); } };
		CrossCorrelation method = new CrossCorrelation(vect, trafo, 75);
		method.prepare(spectrumA.getPeaks());
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < numTests; i++) {
			method.compareTo(spectrumB.getPeaks());
			assertEquals(0.018876804386547614, method.getSimilarity(), 1e-6);
		}
		System.out.println("Took " + (System.currentTimeMillis()-startTime) + "ms");
	}

}
