package de.mpa.taxonomy;


/**
 * This class represents a NCBI taxonomy entry, complete with taxonomy ID as
 * well as rank and name specifiers.
 * 
 * @author R. Heyer and A. Behne
 */
public class TaxonomyNode {

	/**
	 * The taxonomy ID.
	 */
	private int taxId;

	/**
	 * The taxonomic rank
	 */
	private String rank;

	/**
	 * The taxonomy name.
	 */
	private String taxName;

	/**
	 * Constructs a NCBI taxonomy node.
	 * @param taxId The taxonomy ID
	 * @param rank The taxonomic rank
	 * @param taxName The taxonomy name
	 */
	public TaxonomyNode(int taxId, String rank, String taxName){
		this.taxId = taxId;
		this.rank = rank;
		this.taxName = taxName;
	}

	/**
	 * Returns the taxonomy ID.
	 * @return the taxonomy ID
	 */
	public int getId() {
		return taxId;
	}

	/**
	 * Sets the taxonomy ID.
	 * @param taxId The taxonomy ID to set
	 */
	public void setId(int taxId) {
		this.taxId = taxId;
	}

	/**
	 * Returns the taxonomic rank.
	 * @return the taxonomic rank
	 */
	public String getRank() {
		return rank;
	}

	/**
	 * Sets the taxonomic rank.
	 * @param rank The taxonomic rank to set
	 */
	public void setRank(String rank) {
		this.rank = rank;
	}

	/**
	 * Returns the taxonomy name.
	 * @return the taxonomy name
	 */
	public String getName() {
		return taxName;
	}
	
	/**
	 * Sets the taxonomy name.
	 * @param taxName the taxonomy name to set
	 */
	public void setName(String taxName) {
		this.taxName = taxName;
	}

	@Override
	public String toString() {
		return "" + getName() + " (" + getId() + ") | " + getRank() ;
	}
}
