package de.mpa.analysis;

import junit.framework.TestCase;

import org.junit.Test;

import de.mpa.client.model.PeptideHit;
import de.mpa.client.model.ProteinHit;

public class ProteinAnalysisTest extends TestCase {

	private ProteinHit proteinHit;
	
	@Override
	public void setUp() throws Exception {
		// Initialize a test protein
		PeptideHit peptide1 = new PeptideHit("WVSQD", 0, 0); // start 5, end 10
		String sequence = "MERCGWVSQDPLYIAYHDNEWGVPETDSKKLFEMWVSQDICLEGQQAGLSWITVLKKRENYRACFHQFDPVKVAAMQEEDVERLVQDAGIIRHRGKIQAIIGNARAYLQMEQNGEPFVDFVWSFVNHQPQVTQATTLSEIPTSTSASDALSKALKKRGFKFVGTTICYSFMQACGLVNDHVVGCCCYPGNKP";
		proteinHit = new ProteinHit("P05100", "3MG1_ECOLI DNA-3-methyladenine glycosylase 1 OS=Escherichia coli (strain K12) GN=tag PE=1 SV=1", sequence, peptide1);
		PeptideHit peptide2 = new PeptideHit("PGNKP", 0,0); // start 182 -end 187 (last) AS
		proteinHit.addPeptideHit(peptide2);
		super.setUp();
	}
	
	@Test
	public void testCalculateSequenceCoverage(){
		// Search each peptide only once in the protein
		ProteinAnalysis protAnalysis = new ProteinAnalysis(proteinHit);
		ProteinHit protein1 = protAnalysis.getProteinHit();
		assertEquals(0.052, protein1.getCoverage(), 0.0001);
		
		// Search each peptide multiple times in the protein
		ProteinAnalysis protAnalysis2 = new ProteinAnalysis(proteinHit, false);
		ProteinHit protein2 = protAnalysis2.getProteinHit();
		assertEquals(0.078125, protein2.getCoverage());
	}
	
	@Test
	public void testCalculateMolecularWeight(){
		
	}
	
}
