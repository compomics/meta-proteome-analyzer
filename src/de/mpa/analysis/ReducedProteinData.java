package de.mpa.analysis;

import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;

public class ReducedProteinData {

	private UniProtEntry uniProtEntry;
	private String uniRef100EntryId;
	private String uniRef90EntryId;
	private String uniRef50EntryId;
	
	public ReducedProteinData(UniProtEntry uniProtEntry, String uniRef100EntryId, String uniRef90EntryId, String uniRef50EntryId) {
		this.uniProtEntry = uniProtEntry;
		this.uniRef100EntryId = uniRef100EntryId;
		this.uniRef90EntryId = uniRef90EntryId;
		this.uniRef50EntryId = uniRef50EntryId;
	}
	
	public ReducedProteinData(UniProtEntry uniProtEntry) {
		this(uniProtEntry, null, null, null);
	}
	
	public UniProtEntry getUniProtEntry() {
		return uniProtEntry;
	}
	
	public String getUniRef100EntryId() {
		return uniRef100EntryId;
	}
	
	public String getUniRef90EntryId() {
		return uniRef90EntryId;
	}
	
	public String getUniRef50EntryId() {
		return uniRef50EntryId;
	}
}
