package de.mpa.parser.mascot.xml;

import java.io.File;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class MascotXMLParserTest extends TestCase {
	
	private MascotXMLParser parser;

	@Before
	public void setUp() {
		File file = new File(getClass().getClassLoader().getResource("ESI/xml/Spot24.xml").getPath());
		parser = new MascotXMLParser(file);
	}
	
	@Test
	public void testParse() {
		// MascotRecord
		MascotRecord record = parser.parse();
		
		// File name test
		assertEquals("Spot24.xml", record.getXmlFilename());
		
		// Number of queries
		assertEquals(1001, record.getNumQueries());
		
		// MGF file 
		assertEquals("23925373020409089.mgf", record.getMascotFilename());
	}
	
	@Test
	public void testParse2(){
		
		File bande1XML = new File(getClass().getClassLoader().getResource("ESI/xml/Bande1.xml").getPath());
		
		MascotXMLParser parser = new MascotXMLParser(bande1XML);
		
		// MascotRecord 
		MascotRecord record = parser.parse();
		
		// File name test
		assertEquals("Bande1.xml", record.getXmlFilename());
		
		// Number of queries
		System.out.println(record.getNumQueries());
		
		// Peptide map
		System.out.println(record.getPepMap());
		
	}
	


}

