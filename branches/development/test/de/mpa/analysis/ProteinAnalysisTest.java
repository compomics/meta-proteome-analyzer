package de.mpa.analysis;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import com.compomics.util.experiment.biology.Enzyme;

import de.mpa.algorithms.quantification.ExponentiallyModifiedProteinAbundanceIndex;
import de.mpa.algorithms.quantification.NormalizedSpectralAbundanceFactor;
import de.mpa.client.model.SpectrumMatch;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.ProteinHit;

public class ProteinAnalysisTest extends TestCase {
	// Declaration of variables
	private ProteinHit proteinHit;
	private DbSearchResult dbSearchResult;
	private ProteinHit proteinNsaf12;
	private ProteinHit proteinNsaf3;

	@Override
	public void setUp() throws Exception {

		// Initialize a test peptide
		PeptideHit peptide1 = new PeptideHit("WVSQD", 0, 0); // start 5, end 10
		String sequence = "CVVMEKMERCGWVSQDPLYIAYHDNEWGVPETDSKKLFEMWVSQDICLEGQQAGLSWITVLKKRENYRACFHQFDPVKVAAMQEEDVERLVQDAGIIRHRGKIQAIIGNARAYLQMEQNGEPFVDFVWSFVNHQPQVTQATTLSEIPTSTSASDALSKALKKRGFKFVGTTICYSFMQACGLVNDHVVGCCCYPGNKP";
		// Initialize a test Protein
		proteinHit = new ProteinHit("P05100", "3MG1_ECOLI DNA-3-methyladenine glycosylase 1 OS=Escherichia coli (strain K12) GN=tag PE=1 SV=1", sequence, peptide1);
		PeptideHit peptide2 = new PeptideHit("PGNKP", 0,0); // start 182 -end 187 (last) AS
		proteinHit.addPeptideHit(peptide2);
		// Test with PTMs 
		PeptideHit peptideHit = new PeptideHit("C+57V$VM+16EK", 1, 20);
		proteinHit.addPeptideHit(peptideHit);
		
		// Initialize DBResultSet to test the NSAF
		SpectrumMatch specMatch1 = new SpectrumMatch();
		PeptideHit peptideNsaf1 = new PeptideHit("AAAAK", specMatch1);
		SpectrumMatch specMatch2 = new SpectrumMatch();
		PeptideHit peptideNsaf2 = new PeptideHit("CCCCK", specMatch2);
		proteinNsaf12 = new ProteinHit("Fabi", "Fabis Keratin- 15 AS long", "AAAAKCCCCKFFFFF", peptideNsaf1);
		proteinNsaf12.addPeptideHit(peptideNsaf2);
		SpectrumMatch specMatch3 = new SpectrumMatch();
		PeptideHit peptideNsaf3 = new PeptideHit("DDDDDK", specMatch3);
		proteinNsaf3 = new ProteinHit("Robert", "Roberts Keratin- 18 AS long", "DDDDDKMMMMMMMMMMMM", peptideNsaf3);
		dbSearchResult = new DbSearchResult("Project 1", "Experiment 1","Fab1Pr0t");
		dbSearchResult.addProtein(proteinNsaf12);
		dbSearchResult.addProtein(proteinNsaf3);
	}

	@Test
	public void testCalculateMolecularWeight(){
		ProteinAnalysis.calculateMolecularWeight(proteinHit);
		assertEquals(22.405, proteinHit.getMolecularWeight(), 0.1);
	}	

	@Test //pSequence Coverage
	public void testCalculateSequenceCoverage(){

		// Search each peptide only once in the protein
		double coverage = ProteinAnalysis.calculateSequenceCoverage(proteinHit);
		assertEquals(0.0808, coverage, 0.01);

		// Search each peptide multiple times in the protein
		coverage = ProteinAnalysis.calculateSequenceCoverage(proteinHit, false);
		assertEquals(0.1061, coverage, 0.001);
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
		List<String> insilicoPeptides = new ArrayList<String>();
		insilicoPeptides= trypsin.digest("AAAARPRBBBBKCCCC", 0, 1, 10000);
		assertEquals(insilicoPeptides.get(0), "CCCC");
		assertEquals(insilicoPeptides.get(1), "BBBBK");
		assertEquals(insilicoPeptides.get(2), "AAAARPR");
	}

	@Test
	public void testPI(){
		proteinHit.setSequence("HKR");
		double pI = ProteinAnalysis.calculateIsoelectricPoint(proteinHit);
		assertEquals(11, pI, 0.1);

		proteinHit.setSequence("AVLIPMFW");
		pI = ProteinAnalysis.calculateIsoelectricPoint(proteinHit);
		assertEquals(5.57, pI, 5.57);

		proteinHit.setSequence("QNYCTSG");
		pI = ProteinAnalysis.calculateIsoelectricPoint(proteinHit);
		assertEquals(5.52, pI, 0.1);
		
		//http://scansite.mit.edu/cgi-bin/calcpi pI
		proteinHit.setSequence("ED");
		pI = ProteinAnalysis.calculateIsoelectricPoint(proteinHit);
		assertEquals(4.03, pI, 0.1);
	}

	@Test
	public void testEmPAI(){
		double  emPAI = ProteinAnalysis.calculateLabelFree(new ExponentiallyModifiedProteinAbundanceIndex(),proteinHit);
		assertEquals(0.995, emPAI, 0.01);
	}

	@Test
	public void testNSAF() {
		double nsaf = ProteinAnalysis.calculateLabelFree(new NormalizedSpectralAbundanceFactor(), dbSearchResult.getProteinHits(), proteinNsaf12);
		assertEquals(0.70585, nsaf, 0.1);

		nsaf = ProteinAnalysis.calculateLabelFree(new NormalizedSpectralAbundanceFactor(), dbSearchResult.getProteinHits(), proteinNsaf3);
		assertEquals(0.294117647, nsaf, 0.1);
	}
}
