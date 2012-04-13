package de.mpa.client.model.specsim;

import java.util.HashMap;
import java.util.Map;

import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.ProteinHit;

public class SpecSimResult {

	/**
	 * The number of retrieved protein hits from the searches.
	 */
	private Map<String, ProteinHit> proteinHits = new HashMap<String, ProteinHit>();

	/**
	 * Adding a protein to the protein hit set.
	 * 
	 * @param proteinHit
	 *            The ProteinHit
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
				currentProteinHit.addPeptideHit(newProteinHit.getSinglePeptideHit());
			} else {
				// append SSM to existing peptide hit
				currentProteinHit.getSinglePeptideHit().addSpectrumMatch(
						newProteinHit.getSinglePeptideHit().getSingleSpectrumMatch());
			}
		}
		proteinHits.put(accession, currentProteinHit);
	}

	/**
	 * Returns the protein hit for a particular accession.
	 * 
	 * @param accession
	 * @return
	 */
	public ProteinHit getProteinHit(String accession) {
		return proteinHits.get(accession);
	}

	/**
	 * Returns the protein hits.
	 * 
	 * @return The protein hits.
	 */
	public Map<String, ProteinHit> getProteinHits() {
		return proteinHits;
	}

}
