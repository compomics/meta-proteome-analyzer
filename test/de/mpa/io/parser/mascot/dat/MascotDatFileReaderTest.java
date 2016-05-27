package de.mpa.io.parser.mascot.dat;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.junit.BeforeClass;
import org.junit.Test;

public class MascotDatFileReaderTest {
	
	private static MascotDatFileReader mascotDatFileReader;

	@BeforeClass
	public static void setUpClass() throws IOException {
//		File file = new File("E:\\Publikationsvorhaben1_BGA04\\DAT_Orbi\\KW06_12_001.dat");
		File file = new File("\\home\\robert\\Schreibtisch\\F030359_MetaGent1c.dat");
		
		
//		InputStreamReader r = new InputStreamReader(new FileInputStream(file));
		mascotDatFileReader = new MascotDatFileReader(file);
//		File file2 = new File("E:\\Publikationsvorhaben1_BGA04\\DAT_Orbi\\IT_DAT_4Testing.dat");
//		InputStreamReader r2 = new InputStreamReader(new FileInputStream(file2));
//		mascotDatFileReader = new MascotDatFileReader(file2);
		
		RandomAccessFile raf = new RandomAccessFile(file, "r");
		raf.seek(24270);
		System.out.println(raf.readLine());
		raf.close();
	}
	
	@Test
	public void testSurvey() throws IOException {
		mascotDatFileReader.survey();
	}
	
	@Test
	public void testLoadSpectrum() throws IOException {
		mascotDatFileReader.loadSpectrum(1);
	}
	

}
