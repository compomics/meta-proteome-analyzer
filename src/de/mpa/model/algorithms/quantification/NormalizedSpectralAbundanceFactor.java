package de.mpa.model.algorithms.quantification;

import java.util.ArrayList;

import de.mpa.model.dbsearch.PeptideHit;
import de.mpa.model.dbsearch.ProteinHit;

/**
 * Class representing Normalized Spectral Abundance Factor quantification measure calculation.
 * 
 * @author heyer et al
 *
 */
public class NormalizedSpectralAbundanceFactor implements QuantMethod {

	// The NSAF
	private double nSAF;

	@Override
	public double getResult() {
		return this.nSAF;
	}

	@Override
	public void calculate(Object... params) {
		ArrayList<ProteinHit> proteinHitMap = (ArrayList<ProteinHit>) params[0];
		ProteinHit proteinHitOfInterest = (ProteinHit) params[1];

		// Calculate the number of spectrum matches weighted by sequence length for all proteins
		double protSum = 0.0;
		for (ProteinHit proteinHit : proteinHitMap) {
			double pepSum = 0.0;
			for ( PeptideHit peptideHit : proteinHit.getPeptideHitList()) {
				pepSum += peptideHit.getSpectralCount();
			}
			// Check for missing protein sequence 
				if (proteinHit.getSequence() != null && proteinHit.getSequence().length() > 0 ) {
					protSum += pepSum / proteinHit.getSequence().length();
				}
				

		}

		// Calculate the number of spectrum matches weighted by sequence length for protein of interest
		double protOfInterestSum = 0.0;
		for (PeptideHit peptideHitofInterest : proteinHitOfInterest.getPeptideHitList()) {
			protOfInterestSum += peptideHitofInterest.getSpectralCount();
		}
		
		// Check for no protein sequence 
		if (proteinHitOfInterest.getSequence() != null && proteinHitOfInterest.getSequence().length() > 0 ) {
			protOfInterestSum /= proteinHitOfInterest.getSequence().length();
			// Calculate  NSAF
            this.nSAF = protOfInterestSum / protSum;
		}else{
            this.nSAF = 0.0;
		}
			
		
			
		
		
	}
}
