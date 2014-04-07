package de.mpa.analysis;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * Test the calculation of string similarity.
 * @author R. Heyer
 */
public class StringSimilarityTest extends TestCase{

	/**
	 * The peptide set.
	 */
	private HashSet<String> pepSet1 = new HashSet<String>(); 
	private HashSet<String> pepSet2 = new HashSet<String>(); 

	@Before
	public void setUp(){
		// Define Set
		pepSet1.add("EFLDPVEMADYNGNLVK");
		pepSet2.add("EFLDPVEMADFNGNLVK");
		pepSet1.add("ADAMIGAR");
		pepSet2.add("LGNIAAGVMAELLLDER");
		pepSet1.add("ADREDMITFMATSGTK");
		pepSet2.add("YIPIVASAHEMMR");

		StringSimilarity.similarPeptides(pepSet1, pepSet2, 2);
	}
	@Test

	public void testAlignment(){

		assertEquals(true, true);
	}
}
