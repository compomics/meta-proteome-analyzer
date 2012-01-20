package de.mpa.algorithms;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;

public class SpectrumFeaturesTest extends TestCase{
	
	SpectrumFeatures specFeatures;
	@Before
	public void setUp() throws Exception {
		MascotGenericFileReader reader;
		try {
			reader = new MascotGenericFileReader(new File("test.mgf"));
			MascotGenericFile mgf = (MascotGenericFile) reader.getSpectrumFiles().get(0);
			specFeatures = new SpectrumFeatures(mgf, 0.5);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testGetY1PeakInt() {
		assertEquals(694.973365, specFeatures.getY1PeakInt());
		
	}

}
