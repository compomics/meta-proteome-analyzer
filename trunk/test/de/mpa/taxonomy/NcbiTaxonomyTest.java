package de.mpa.taxonomy;

import junit.framework.TestCase;

import org.junit.Before;


/**
 * Tests for NCBI taxonomy-related classes and methods.
 * TODO: Use this class for creation of the taxonomy index file (if needed at all).
 * @author R. Heyer, A. Behne
 */
public class NcbiTaxonomyTest extends TestCase {

	@Before
	public void setUp(){
		// Path of the taxonomy dump folder
		NcbiTaxonomy ncbiTax = NcbiTaxonomy.getInstance();
		try {
//			System.out.print("Creating Index File... ");
//			long startTime = System.currentTimeMillis();
			ncbiTax.createIndexFile();
//			System.out.flush();
//			System.out.println("done! (" + (System.currentTimeMillis() - startTime) + " ms)");

//			System.out.print("Reading Index File... ");
//			startTime = System.currentTimeMillis();
			ncbiTax.readIndexFile();
//			System.out.flush();
//			System.out.println("done! (" + (System.currentTimeMillis() - startTime) + " ms)");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}