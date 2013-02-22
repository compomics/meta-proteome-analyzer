package de.mpa.parser.mgf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;
import de.mpa.io.MascotGenericFileReader.LoadMode;

public class SpectrumQualityTest extends TestCase {
	
	private MascotGenericFileReader reader;

	
	@Before
	public void setUp() {
		String filePath = "/home/muth/1_2_8.mgf";
		try {
			reader = new MascotGenericFileReader(new File(filePath), LoadMode.NONE);
		} catch (IOException e) {
			fail();
		}
	}
	
	@Test
	public void testLoadSpectra() throws IOException {
		BufferedWriter bWriter = new BufferedWriter(new FileWriter("/home/muth/" + reader.getFilename() + ".txt"));
		reader.load();
		List<MascotGenericFile> spectrumFiles = reader.getSpectrumFiles();
		int c1 = 0, c2 = 0, c3 = 0, c4 = 0, c5 = 0, c6 = 0;
		for (MascotGenericFile mgf : spectrumFiles) {
			int size = mgf.getPeaks().size();
			bWriter.write(Double.valueOf(mgf.getTotalIntensity()).toString());
			bWriter.newLine();
			
			if(size < 6) {
				c1++;
			} else if(size > 5 && size <11) {
				c2++;
			} else if(size > 10 && size < 21) {
				c3++;
			} else if(size > 20 && size < 51) {
				c4++;
			} else if(size > 50 && size < 101) {
				c5++;
			} else if (size > 100) {
				c6++;
			} 
		}
		bWriter.flush();
		bWriter.close();
		System.out.println(reader.getFilename());
		System.out.println("total spectra: " + spectrumFiles.size());
		System.out.println("0-5 peaks : " + c1);
		System.out.println("6-10 peaks : " + c2);
		System.out.println("11-20 peaks : " + c3);
		System.out.println("21-50 peaks : " + c4);
		System.out.println("51-100 peaks : " + c5);
		System.out.println("> 100 peaks : " + c6);
	}
	
}

