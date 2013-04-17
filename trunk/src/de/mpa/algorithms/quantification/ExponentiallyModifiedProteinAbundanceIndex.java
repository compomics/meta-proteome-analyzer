package de.mpa.algorithms.quantification;

import java.util.ArrayList;

import com.compomics.util.experiment.biology.Enzyme;

import de.mpa.client.model.dbsearch.ProteinHit;

/**
 * Class to calculate the Exponentially Modified Protein Abundance Index.
 * @author heyer and kohrs
 *
 */
public class ExponentiallyModifiedProteinAbundanceIndex implements QuantMethod {

	private double emPAI = 0.0;

	@Override
	public void calculate(Object... obj) {
		
		ProteinHit proteinHit = (ProteinHit) obj[0];
		// Constructur enzyme
		// 1. id, the enzyme id which should be OMSSA compatible
		// 2. name, the name of the enzym
		// 3. aminoAcidBefore, the amino-acids which can be found before the cleavage
		// 4. restrictionBefore, amino-acids which should not be found before the cleavage
		// 5. aminoAcidAfter, the amino-acids which should be found after the cleavage
		// 6. restrictionAfter, the amino-acids which should not be found after the cleavage
		Enzyme trypsin = new Enzyme(1, "Trypsin", "RK" , "", "", "P");

		// Call digest
		// @param sequence              the protein sequence
		// @param nMissedCleavages      the allowed number of missed cleavages
		// @param nMin                  the minimal size for a peptide
		// @param nMax                  the maximal size for a peptide
		ArrayList<String> insilicoPeptides = new ArrayList<String>();
		//TODO: # missed cleavages has to be taken from search parameters to avoid misscalculations 
		//		or remove misscleaved peptides from calculation.
		if (proteinHit.getSequence() != null && proteinHit.getSequence().length() >0) {
			insilicoPeptides= trypsin.digest(proteinHit.getSequence(), 0, 4, 1000);
			double pAI;
			double peptideObserved = proteinHit.getPeptideCount();
			//TODO Control PeptideCount to misscleavages
			double peptideObservable = insilicoPeptides.size();
			// Calculates the PAI
			pAI = peptideObserved / peptideObservable;

			// Calculates the emPAI
			emPAI = Math.pow(10, pAI) - 1;
		}else{
			emPAI = 0.0;
		}
		
		
		
	}

	@Override
	public double getResult() {
		return emPAI;
	}
}
