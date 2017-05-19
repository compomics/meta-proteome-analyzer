package de.mpa.model.analysis;

import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;

/**
 * Object containing reduced protein entry.
 * @author Irgendwer + Robert Heyer
 *
 */
public class ReducedProteinData {

	/**
	 * The UniProt entry... belongs to UniprotJapi
	 */
	private final UniProtEntry uniProtEntry;
	
	/**
	 * The UniRef100 entry
	 */
	private String uniRef100EntryId;
	
	/**
	 * The UniRef100 entry
	 */
	private String uniRef90EntryId;
	
	/**
	 * The UniRef100 entry
	 */
	private String uniRef50EntryId;
	
	public ReducedProteinData(UniProtEntry uniProtEntry, String uniRef100EntryId, String uniRef90EntryId, String uniRef50EntryId) {
		this.uniProtEntry = uniProtEntry;
		this.uniRef100EntryId = uniRef100EntryId;
		this.uniRef90EntryId = uniRef90EntryId;
		this.uniRef50EntryId = uniRef50EntryId;
	}
	
	/**
	 * Constructor for a reduced protein entry
	 * @param uniProtEntry
	 */
	public ReducedProteinData(UniProtEntry uniProtEntry) {
		this(uniProtEntry, null, null, null);
	}
	
	/**
	 * Get UniProt entry.
	 * @return. The UniProt entry
	 */
	public UniProtEntry getUniProtEntry() {
		return this.uniProtEntry;
	}
	
	/**
	 * Gets the UniRef 100 ID
	 * @return. The UniRef 100ID
	 */
	public String getUniRef100EntryId() {
		return this.uniRef100EntryId;
	}
	
	/**
	 * Gets the UniRef90 ID
	 * @return. The UniRef 90 ID
	 */
	public String getUniRef90EntryId() {
		return this.uniRef90EntryId;
	}
	
	/**
	 * Gets the UniRef50 ID
	 * @return. The UniRef 50 ID
	 */
	public String getUniRef50EntryId() {
		return this.uniRef50EntryId;
	}

	/**
	 * Sets the UniRef100 cluster ID.
	 * @param uniRef100EntryId
	 */
	public void setUniRef100EntryId(String uniRef100EntryId) {
		this.uniRef100EntryId = uniRef100EntryId;
	}

	/**
	 * Sets the UniRef 90 cluster ID
	 * @param uniRef90EntryId
	 */
	public void setUniRef90EntryId(String uniRef90EntryId) {
		this.uniRef90EntryId = uniRef90EntryId;
	}

	/**
	 * Sets the UniRef 50 cluster ID.
	 * @param uniRef50EntryId
	 */
	public void setUniRef50EntryId(String uniRef50EntryId) {
		this.uniRef50EntryId = uniRef50EntryId;
	}
}
