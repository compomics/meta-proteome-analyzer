package de.mpa.algorithms;

import java.io.File;
import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import de.mpa.algorithms.similarity.EuclideanDistance;
import de.mpa.algorithms.similarity.Transformation;
import de.mpa.algorithms.similarity.Vectorization;
import de.mpa.algorithms.similarity.VectorizationFactory;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;

public class EuclideanDistanceTest extends TestCase {
	
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
		Vectorization vect = VectorizationFactory.createDirectBinning(1.0, 0.0);
		Transformation trafo = Transformation.SQRT;
		
		EuclideanDistance euclidDist = new EuclideanDistance(vect, trafo);
		euclidDist.prepare(spectrumA.getPeaks());
		euclidDist.compareTo(spectrumB.getPeaks());
		assertEquals(43.1758, euclidDist.getSimilarity(), 1e-4);
	}

}
