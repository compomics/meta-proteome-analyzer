package de.mpa.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents the set of proteins which may hold multiple peptides for each proteinhit (represented by the accession)
 * @author muth
 *
 */
public class ProteinHitSet{
	
	private Map<String, List<PeptideHit>> proteins = new HashMap<String, List<PeptideHit>>();
	private Map<String, ProteinHit> proteinHits = new HashMap<String, ProteinHit>();
	/**
	 * Adding a protein to the protein hit set.
	 * @param hit The ProteinHit
	 */
	public void addProtein(ProteinHit hit){
		if (proteins.containsKey(hit.getAccession())) {
				List<PeptideHit> peptides = proteins.get(hit.getAccession());
				peptides.add(hit.getPeptideHit());
				proteins.put(hit.getAccession(), peptides);
				
		} else {
			// Start new list
			List<PeptideHit> peptides = new ArrayList<PeptideHit>();
			peptides.add(hit.getPeptideHit());
			proteins.put(hit.getAccession(), peptides);
		}
		// Set the accession-to-proteinhit map
		if(!hit.getAccession().equals("")){
			proteinHits.put(hit.getAccession(), hit);
		}
	}

	/**
	 * Returns all proteins with their corresponding peptides from the protein hit set.
	 * @return the proteins
	 */
	public Map<String, List<PeptideHit>> getPeptideHits() {
		return proteins;
	}
	
	/**
	 * Returns the protein hit for a particular accession.
	 * @param accession
	 * @return
	 */
	public ProteinHit getProteinHit(String accession){
		return proteinHits.get(accession);
	}
	
	/**
	 * Returns the peptide hits for a particular protein hit.
	 * @param hit
	 * @return
	 */
	public List<PeptideHit> getPeptideHits(String accession){
		return proteins.get(accession);
	}
}
