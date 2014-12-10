package de.mpa.io.parser;

import junit.framework.TestCase;

import org.junit.Test;

import de.mpa.io.parser.ec.ECNode;
import de.mpa.io.parser.ec.ECReader;

/**
 * Test class for E.C. configuration file parsing.
 * @author XMS
 */
public class ECReaderTest extends TestCase {

	@Test
	public void testEnzymeParsing() {
		ECNode root = ECReader.readEnzymeClasses("src/conf/enzclass.txt");
		ECReader.readEnzymes(root, "src/conf/enzyme.dat");
		
		assertEquals("1.-.-.-", ((ECNode) root.getChildAt(0)).getIdentifier());
		assertEquals("Alcohol dehydrogenase.", ((ECNode) root.getFirstLeaf()).getDescription());
		assertEquals("6.6.1.2", ((ECNode) root.getLastLeaf()).getIdentifier());
		assertEquals("Cobaltochelatase.", ((ECNode) root.getLastLeaf()).getDescription());
	}
	
}
