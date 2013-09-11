package de.mpa.parser.mgf;

import java.io.File;
import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;
import de.mpa.io.MascotGenericFileReader.LoadMode;

public class MascotGenericFileReaderTest extends TestCase {
	
	private MascotGenericFileReader reader;
	
//	@Test
//	public void testTrimMGF() throws IOException {
//		String filename = "test/de/mpa/resources/Test1000Ecoli";
//		File file = new File(filename + ".mgf");
//		reader = new MascotGenericFileReader(file, LoadMode.SURVEY);
//		List<Long> positions = reader.getSpectrumPositions(false);
//		
//		assertEquals(1000, positions.size());
//		
//		File outFile = new File(filename + "_abridged.mgf");
//		FileOutputStream fos = new FileOutputStream(outFile);
//		
//		int index = 0;
//		int total = 0;
//		double rt = 0.0;
//		for (int j = 0; j < positions.size(); j++) {
//			long startPos = positions.get(j);
//			long endPos = (j == (positions.size() -1)) ? file.length() : positions.get(j + 1);
//			
//			MascotGenericFile spectrum = reader.loadNthSpectrum(index, startPos, endPos);
//			
//			if (index < 1000) {
//				spectrum.writeToStream(fos);
//				total++;
//			} else {
//				String title = spectrum.getTitle();
//				double rtNew = Double.parseDouble(title.split(" ")[4]);
//				if (rtNew < rt) {
//					index = -1;
//					rt = 0.0;
//				} else {
//					rt = rtNew;
//				}
//			}
//			index++;
//		}
//	}
	
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
		
		assertEquals(79.299714, spectrumFiles.get(0).getHighestIntensity(), 0.01);
		assertEquals(103.3638582, spectrumFiles.get(0).getTotalIntensity(), 0.01);
		
		assertEquals(832.62274, spectrumFiles.get(99).getHighestIntensity(), 0.01);
		assertEquals(6371.3626816, spectrumFiles.get(99).getTotalIntensity(), 0.01);
	}
	
}
