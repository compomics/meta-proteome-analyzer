package de.mpa.io.parser.xtandem;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import de.mpa.io.GenericContainer;
import junit.framework.TestCase;

public class XTandemProteinParserTest extends TestCase {
	private XTandemProteinParser parser;

	@Before
	public void setUp() {
		parser = new XTandemProteinParser(new File("test/de/mpa/resources/output/Test1426_target.xml"));
	}
	
	@Test 
	public void testParse() {
		parser.parse();
		assertEquals(2586, GenericContainer.ProteinAccs.size());
	}
}
