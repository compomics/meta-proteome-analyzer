package de.mpa.client.model.specsim;

import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.Map;

import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.ProteinHit;

public class SpecSimResult {

	/**
	 * The number of retrieved protein hits from the searches.
	 */
	private Map<String, ProteinHit> proteinHits = new LinkedHashMap<String, ProteinHit>();
	private BufferedImage scoreMatrixImage;

	/**
	 * Adding a protein to the protein hit set.
	 * 
	 * @param proteinHit The ProteinHit
	 */
	public void addProtein(ProteinHit newProteinHit) {
		String accession = newProteinHit.getAccession();
		ProteinHit currentProteinHit = proteinHits.get(accession);
		if (currentProteinHit == null) {
			// protein hit does not exist yet in map, therefore append it
			currentProteinHit = newProteinHit;
		} else {
			// possibly append peptide hit to existing protein hit
			PeptideHit currentPeptideHit = currentProteinHit.getPeptideHits().get(
					newProteinHit.getSinglePeptideHit().getSequence());
			if (currentPeptideHit == null) {
				// append new peptide hit
				currentPeptideHit = newProteinHit.getSinglePeptideHit();
			} else {
				// append SSM to existing peptide hit
				currentPeptideHit.addSpectrumMatch(currentPeptideHit.getSequence(),	newProteinHit.getSinglePeptideHit().getSingleSpectrumMatch());
			}
			currentProteinHit.addPeptideHit(currentPeptideHit);
		}
		proteinHits.put(accession, currentProteinHit);
	}

	/**
	 * Returns the protein hit for a particular accession.
	 * 
	 * @param accession The accession string.
	 * @return
	 */
	public ProteinHit getProteinHit(String accession) {
		return proteinHits.get(accession);
	}
	
	/**
	 * Returns <code>true</code> if this result object contains no protein hits.
	 * 
	 * @return <code>true</code> if this result object contains no protein hits.
	 */
	public boolean isEmpty() {
		return proteinHits.isEmpty();
	}

	/**
	 * Returns the protein hits.
	 * 
	 * @return The protein hits.
	 */
	public Map<String, ProteinHit> getProteinHits() {
		return proteinHits;
	}

	/**
	 * @return the scoreMatrixImage
	 */
	public BufferedImage getScoreMatrixImage() {
		return scoreMatrixImage;
	}

	public void setScoreMatrixImage(BufferedImage scoreMatrixImage) {
		this.scoreMatrixImage = scoreMatrixImage;
	}

}
