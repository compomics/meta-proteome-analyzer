package de.mpa.io;

import java.io.File;
import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import de.mpa.io.MascotGenericFileReader.LoadMode;

public class MascotGenericFileReaderTest extends TestCase {
	
	private MascotGenericFileReader reader;

	@Before
	public void setUp() {
		String filePath = "test/de/mpa/resources/Test_100.mgf";
		try {
			reader = new MascotGenericFileReader(new File(filePath), LoadMode.NONE);
		} catch (IOException e) {
			fail();
		}
	}
	
	@Test
	public void testSurveySpectra() throws IOException {
		reader.survey();
		List<Long> spectrumPositions = reader.getSpectrumPositions();
		
		assertEquals(100, spectrumPositions.size());
	}
	
	@Test
	public void testLoadSpectra() throws IOException {
		reader.load();
		List<MascotGenericFile> spectrumFiles = reader.getSpectrumFiles();
		assertEquals(100, spectrumFiles.size());

	}
}
