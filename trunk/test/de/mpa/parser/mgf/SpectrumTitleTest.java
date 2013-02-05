package de.mpa.parser.mgf;

import junit.framework.TestCase;

public class SpectrumTitleTest extends TestCase {
	
	public void testReplace() {
		String titleBefore = "BGA04F12_2.2201.2201.2 File:\\\"BGA04F12_2.raw\\\", NativeID:\\\"controllerType=0 controllerNumber=1 scan=2201\\\"";
		
		String titleAfter = titleBefore.replace("\\\"", "\"");
		
		String titleExpected = "BGA04F12_2.2201.2201.2 File:\"BGA04F12_2.raw\", NativeID:\"controllerType=0 controllerNumber=1 scan=2201\"";
		
		assertEquals(titleExpected, titleAfter);
		
		
	}

}
