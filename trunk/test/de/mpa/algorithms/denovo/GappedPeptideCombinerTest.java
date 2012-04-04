package de.mpa.algorithms.denovo;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

public class GappedPeptideCombinerTest extends TestCase{
	
	@Test
	public void testCombiner(){
		GappedPeptide peptide41 = new GappedPeptide("E,D,L,L,A,Y,L,K");
		GappedPeptide peptide42 = new GappedPeptide("E,D,L,L,A,Y,L");
		GappedPeptide peptide43 = new GappedPeptide("E,D,L,L,A,M+16,E,K");		
		GappedPeptide peptide44 = new GappedPeptide("F,P,L,L,A,Y,G,<202.8120>");
		GappedPeptide peptide45 = new GappedPeptide("E,D,L,L,A,Y,L,Q");
		GappedPeptide peptide46 = new GappedPeptide("E,D,L,L,A,Y,G,<202.8120>");
		GappedPeptide peptide47 = new GappedPeptide("<146.8320>,P,L,L,A,Y,L,K");
		GappedPeptide peptide48 = new GappedPeptide("M+16,P,L,L,A,Y,G,<202.8120>");
		GappedPeptide peptide49 = new GappedPeptide("E,D,L,L,A,F,E,K");
		GappedPeptide peptide50 = new GappedPeptide("E,D,L,L,M+16,S,L,K");
		
		List<GappedPeptide> lGappedPeptides = new ArrayList<GappedPeptide>();
		lGappedPeptides.add(peptide41);
		lGappedPeptides.add(peptide42);
		lGappedPeptides.add(peptide43);
		lGappedPeptides.add(peptide44);
		lGappedPeptides.add(peptide45);
		lGappedPeptides.add(peptide46);
		lGappedPeptides.add(peptide47);
		lGappedPeptides.add(peptide48);
		lGappedPeptides.add(peptide49);
		lGappedPeptides.add(peptide50);
		GappedPeptideCombiner combiner = new GappedPeptideCombiner(lGappedPeptides, 0.5);	
		assertEquals("E,D,L,L,A,Y,<194.64698133333314>", combiner.getCombinedGappedPeptide().toString());
	}
	
}
