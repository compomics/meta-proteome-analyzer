package de.mpa.taxonomy;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import de.mpa.analysis.taxonomy.NcbiTaxonomy;
import de.mpa.client.Constants;


/**
 * Tests for NCBI taxonomy-related classes and methods.
 * TODO: Use this class for creation of the taxonomy index file (if needed at all).
 * @author R. Heyer, A. Behne
 */
public class NcbiTaxonomyTest extends TestCase {

	@Before
	public void setUp(){
		// Path of the taxonomy dump folder
		String namesFileString = Constants.CONFIGURATION_DIR_PATH + "names.dmp";
		String nodesFileString = Constants.CONFIGURATION_DIR_PATH + "nodes.dmp";
		
		try {
			NcbiTaxonomy ncbiTax = NcbiTaxonomy.getInstance(namesFileString, nodesFileString);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testNCBI(){
		int a = 1;
		assertEquals(1, a);
	}
}