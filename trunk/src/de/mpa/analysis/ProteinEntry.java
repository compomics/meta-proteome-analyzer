package de.mpa.analysis;

import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import de.mpa.client.model.dbsearch.ProteinHit;

/**
 * <b>ProteinEntry<b>
 * <p>
 * This class holds information about the protein search hit and the corresponding UniProt information.
 * </p>
 * @author T.Muth
 *
 */
public class ProteinEntry {
	
	/**
	 * UniprotEntry object.
	 */
	private UniProtEntry uniprotEntry;
	
	/**
	 * ProteinHit object.
	 */
	private ProteinHit proteinHit;
	
	/**
	 * Constructs a ProteinEntry.
	 * @param uniprotEntry The UniProtEntry object.
	 * @param proteinHit The ProteinHit retrieved from searches.
	 */
	public ProteinEntry(UniProtEntry uniprotEntry, ProteinHit proteinHit) {
		this.uniprotEntry = uniprotEntry;
		this.proteinHit = proteinHit;
	}

	/**
	 * Returns the UniProtEntry object.
	 * @return UniProtEntry object
	 */
	public UniProtEntry getUniprotEntry() {
		return uniprotEntry;
	}
	
	/**
	 * Returns the ProteinHit object.
	 * @return ProteinHit object.
	 */
	public ProteinHit getProteinHit() {
		return proteinHit;
	}
	
}
