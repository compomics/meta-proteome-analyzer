package de.mpa.parser.mascot.xml;

/* Name:				RobbiesTestMain
 * letzte Änderung:		27.10.2011
 * Author:				Robbie
 * Beschreibung:		Zum aufrufen von neuen Files um zu checken ob sie fuktionieren
 */
//import******************************************************************************


import java.io.File;
public class TestMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fil = "C:\\Documents and Settings\\heyer\\workspace\\MetaProteomeAnalyzer\\test\\de\\mpa\\resources\\ESI\\xml\\Spot24.xml";
		MascotXMLParser parser = new MascotXMLParser();
		MascotRecord record = parser.parse(new File(fil));
		
	}

}

