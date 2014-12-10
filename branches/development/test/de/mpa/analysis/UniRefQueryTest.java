package de.mpa.analysis;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

/**
 * Test the UniRef querying
 * @author robert
 *
 */
public class UniRefQueryTest extends TestCase {

	private ReducedProteinData uniRefs;
	
	@Before
	public void setUp() {

		// Create entry retrival service
		uniRefs = UniProtUtilities.getUniRefByUniProtAcc("A4IP67");

		System.out.println(	"UNIREF100: " + uniRefs.getUniRef100EntryId());
		System.out.println(	"UNIREF90: " + uniRefs.getUniRef90EntryId());
		System.out.println(	"UNIREF50: " + uniRefs.getUniRef50EntryId());
	}

	@Test // Test keywords
	public void testdefault() {
		assertEquals("UniRef100_A4IP67", uniRefs.getUniRef100EntryId());
		assertEquals("UniRef50_P22842", uniRefs.getUniRef50EntryId());
		assertEquals("UniRef90_Q5KYS6", uniRefs.getUniRef90EntryId()); // Error is done by UniProt
	}
}