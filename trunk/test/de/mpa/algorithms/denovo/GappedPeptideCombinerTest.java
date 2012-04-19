package de.mpa.algorithms.denovo;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

public class GappedPeptideCombinerTest extends TestCase{
	
	private List<GappedPeptide> gappedPeptides;
	
	@Before
	public void setUp() {
		gappedPeptides = new ArrayList<GappedPeptide>();
		gappedPeptides.add(new GappedPeptide("E,D,L,L,A,Y,L,K"));
		gappedPeptides.add(new GappedPeptide("E,D,L,L,A,Y,L"));
		gappedPeptides.add(new GappedPeptide("E,D,L,L,A,M+16,E,K"));
		gappedPeptides.add(new GappedPeptide("F,P,L,L,A,Y,G,<202.8120>"));
		gappedPeptides.add(new GappedPeptide("E,D,L,L,A,Y,L,Q"));
		gappedPeptides.add(new GappedPeptide("E,D,L,L,A,Y,G,<202.8120>"));
		gappedPeptides.add(new GappedPeptide("<146.8320>,P,L,L,A,Y,L,K"));
		gappedPeptides.add(new GappedPeptide("M+16,P,L,L,A,Y,G,<202.8120>"));
		gappedPeptides.add(new GappedPeptide("E,D,L,L,A,F,E,K"));
		gappedPeptides.add(new GappedPeptide("E,D,L,L,M+16,S,L,K"));
	}
	
	@Test
	public void testCombiner(){		
		GappedPeptideCombiner combiner = new GappedPeptideCombiner(gappedPeptides, 0.5);	
		assertEquals("E,D,L,L,A,Y,<194.64698133333314>", combiner.getCombinedGappedPeptide().toString());
	}
}
