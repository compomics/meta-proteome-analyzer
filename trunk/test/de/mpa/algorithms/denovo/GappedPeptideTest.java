package de.mpa.algorithms.denovo;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class GappedPeptideTest extends TestCase {
	
	GappedPeptide peptide1, peptide2, peptide3;
	
	@Before
	public void setUp() {
		peptide1 = new GappedPeptide("E,D,L,L,A,Y,L,K");
		peptide2 = new GappedPeptide("<218.1>,D,Q,L,D,N,A,P,E,E,K");
		peptide3 = new GappedPeptide("<218.1>,D,Q,L,D,N,A,P,E,<217.8>");
		
	}
	
	@Test
	public void testGetGappedSequence() {
		assertEquals("EDLLAYLK", peptide1.getGappedSequence());
		assertEquals("<218.1>DQLDNAPEEK", peptide2.getGappedSequence());
		assertEquals("<218.1>DQLDNAPE<217.8>", peptide3.getGappedSequence());
	}
	
	@Test
	public void testGetFormattedSequence() {
		assertEquals("EDLLAYLK", peptide1.getFormattedSequence());
		assertEquals("[DC|MS|FA]DQLDNAPEEK", peptide2.getFormattedSequence());
		assertEquals("[DC|MS|FA]DQLDNAPE[DC|MS|FA]", peptide3.getFormattedSequence());
	}
}
