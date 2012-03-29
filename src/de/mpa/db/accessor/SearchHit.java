package de.mpa.db.accessor;

public interface SearchHit {
	public String getSequence();
	public String getAccession();
	public Number getQvalue();
	public long getCharge();
	public long getFk_searchspectrumid();
	public long getFk_peptideid();
	public long getFk_proteinid();
}
