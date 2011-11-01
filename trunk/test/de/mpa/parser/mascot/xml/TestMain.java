package de.mpa.parser.mascot.xml;

/* Name:				RobbiesTestMain
 * letzte Änderung:		27.10.2011
 * Author:				Robbie
 * Beschreibung:		Zum aufrufen von neuen Files um zu checken ob sie fuktionieren
 */
//import******************************************************************************


import java.io.File;

import junit.framework.TestCase;
public class TestMain extends TestCase {

	/**
	 * @param args
	 */
	public void testParse() {
		String file = "C:\\Documents and Settings\\heyer\\workspace\\MetaProteomeAnalyzer\\test\\de\\mpa\\resources\\ESI\\xml\\Spot24.xml";
		MascotXMLParser parser = new MascotXMLParser(new File(file));
		System.out.println(parser.parse().getXmlFilename());
		
	}

}

