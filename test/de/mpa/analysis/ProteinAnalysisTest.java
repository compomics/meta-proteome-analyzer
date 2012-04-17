package de.mpa.analysis;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.junit.Test;

import com.compomics.util.experiment.biology.Enzyme;

import de.mpa.algorithms.quantification.EmPAIAlgorithm;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.ProteinHit;

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
		ProteinAnalysis.calculateSequenceCoverage(proteinHit);
		assertEquals(5.2, proteinHit.getCoverage(), 0.1);
		
		// Search each peptide multiple times in the protein
		ProteinAnalysis.calculateSequenceCoverage(proteinHit, false);
		assertEquals(7.8125, proteinHit.getCoverage(),0.1);
	}
	
	@Test
	public void testCalculateMolecularWeight(){
		//TODO: DO THIS THILO
		
	}
	
	@Test
	public void testEnzyme(){
		// Constructur enzyme
		// 1. id, the enzyme id which should be OMSSA compatible
		// 2. name, the name of the enzym
		// 3. aminoAcidBefore, the amino-acids which can be found before the cleavage
		// 4. restrictionBefore, amino-acids which should not be found before the cleavage
		// 5. aminoAcidAfter, the amino-acids which should be found after the cleavage
		// 6. restrictionAfter, the amino-acids which should not be found after the cleavage
		Enzyme trypsin = new Enzyme( 1,  "Trypsin",  "RK" ,  "",  "",  "P");

		// Call digest
		// @param sequence              the protein sequence
		// @param nMissedCleavages      the allowed number of missed cleavages
		// @param nMin                  the minimal size for a peptide
		// @param nMax                  the maximal size for a peptide
		ArrayList<String> insilicoPeptides = new ArrayList<String>();
		insilicoPeptides= trypsin.digest("AAAARPRBBBBKCCCC", 0, 1, 10000);
		
		assertEquals(insilicoPeptides.get(1).toString(), "BBBBK");
	}
	
	
	@Test
	public void testEmPAI(){
		ProteinAnalysis.calculateLabelFree(new EmPAIAlgorithm(),proteinHit);
		assertEquals(0.66810	, proteinHit.getEmPAI(), 0.01);
	}
	
}
