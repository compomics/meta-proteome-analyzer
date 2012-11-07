package de.mpa.taxonomy;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * This class holds a taxon, including the NCBI tax Id, the parent Id and the rank
 * @author R. Heyer and A. Behne
 */
public class TaxonNode extends DefaultMutableTreeNode {
	
	// The tax ID
	private int taxId;
	
	// The taxonomic rank
	private String rank;
	
	// The tax name
	private String taxName;
	
	/**
	 * Constructor to create a Taxon.
	 * @param taxId
	 * @param rank
	 */
	public TaxonNode(int taxId, String rank, String taxName){
		this.taxId 		= taxId;
		this.rank 		= rank;
		this.taxName 	= taxName;
	}

	/**
	 * Gets the taxId.
	 * @return taxId
	 */
	public int getTaxId() {
		return taxId;
	}

	/**
	 * Sets the taxId.
	 * @param taxId
	 */
	public void setTaxId(int taxId) {
		this.taxId = taxId;
	}

	/**
	 * Gets the taxonomic rank.
	 * @return rank
	 */
	public String getRank() {
		return rank;
	}

	/**
	 * Sets the taxonomic rank.
	 * @param rank
	 */
	public void setRank(String rank) {
		this.rank = rank;
	}

	/**
	 * Gets the taxonomic name.
	 * @return The taxName
	 */
	public String getTaxName() {
		return taxName;
	}
	
	/**
	 * Sets the taxonomic name.
	 * @param taxName
	 */
	public void setTaxName(String taxName) {
		this.taxName = taxName;
	}

	@Override
	public String toString() {
		return "" + getTaxName() + " (" + getTaxId() + ") | " + getRank() ;
	}
}
