package de.mpa.taxonomy;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;


/**
 * Tests for NCBI taxonomy-related classes and methods.
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

	@Test 
	public void testNamesIndexFile(){
		try {
			NcbiTaxonomy ncbiTax = NcbiTaxonomy.getInstance();
			
			// Get taxon name from name map
			String name1 = ncbiTax.getTaxonName(1);
			String name2 = ncbiTax.getTaxonName(2);
			String name3 = ncbiTax.getTaxonName(174);
			assertEquals("root", name1);
			assertEquals("Bacteria", name2);
			assertEquals("Leptospira borgpetersenii", name3);
			

			// Get parent from node map
			int parentTaxID1 = ncbiTax.getParentTaxId(1);
			int parentTaxID2 = ncbiTax.getParentTaxId(2);
			int parentTaxID3 = ncbiTax.getParentTaxId(171);
			assertEquals(1, parentTaxID1);
			assertEquals(131567, parentTaxID2);
			assertEquals(170, parentTaxID3);

			// Get rank from node map
			String rank1 = ncbiTax.getRank(1);
			String rank2 = ncbiTax.getRank(2);
			String rank3 = ncbiTax.getRank(171);
			assertEquals("no rank", rank1);
			assertEquals("superkingdom", rank2);
			assertEquals("genus", rank3);

			// Test common taxID
			int comTaxID1 = ncbiTax.getCommonTaxonomyId(269797, 79929);
			int comTaxID2 = ncbiTax.getCommonTaxonomyId(1, 1);
			assertEquals(28890, comTaxID1);
			assertEquals(1, comTaxID2);
			
			// Test common taxon Node
			TaxonomyNode comTaxNode1 = ncbiTax.createCommonTaxonomyNode(269797, 79929);
			TaxonomyNode comTaxNode2 = ncbiTax.createCommonTaxonomyNode(1, 1);
			assertEquals(28890, comTaxNode1.getId());
			assertEquals("Euryarchaeota", comTaxNode1.getName());
			assertEquals("phylum", comTaxNode1.getRank());
			assertEquals(1, comTaxNode2.getId());
			assertEquals("root", comTaxNode2.getName());
			assertEquals("no rank", comTaxNode2.getRank());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}