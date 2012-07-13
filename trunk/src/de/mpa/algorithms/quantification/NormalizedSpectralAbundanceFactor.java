package de.mpa.algorithms.quantification;

import java.util.Map;

import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.ProteinHit;

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
		return nSAF;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void calculate(Object... params) {
		Map<String,ProteinHit> proteinHitMap = (Map<String, ProteinHit>) params[0];
		ProteinHit proteinHitOfInterest = (ProteinHit) params[1];

		// Calculate the number of spectrum matches weighted by sequence length for all proteins
		double protSum = 0.0;
		for ( ProteinHit proteinHit : proteinHitMap.values()) {
			double pepSum = 0.0;
			for ( PeptideHit peptideHit : proteinHit.getPeptideHitList()) {
				pepSum += peptideHit.getSpectralCount();
			}
			protSum += pepSum / proteinHit.getSequence().length();
		}

		// Calculate the number of spectrum matches weighted by sequence length for protein of interest
		double protOfInterestSum = 0.0;
		for (PeptideHit peptideHitofInterest : proteinHitOfInterest.getPeptideHitList()) {
			protOfInterestSum += peptideHitofInterest.getSpectralCount();
		}
		protOfInterestSum /= proteinHitOfInterest.getSequence().length();

		// Calculate  NSAF
		nSAF = protOfInterestSum / protSum;
	}
}
