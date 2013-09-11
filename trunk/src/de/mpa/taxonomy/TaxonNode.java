package de.mpa.taxonomy;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * This class represents a NCBI taxonomy entry, complete with taxonomy ID as
 * well as rank and name specifiers.
 * 
 * @author R. Heyer and A. Behne
 */
public class TaxonNode extends DefaultMutableTreeNode {

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
	public TaxonNode(int taxId, String rank, String taxName){
		this.taxId = taxId;
		this.rank = rank;
		this.taxName = taxName;
	}
	
	/**
	 * Copy constructor.
	 * @param that The taxonomy node to copy from
	 */
	public TaxonNode(TaxonNode that) {
		this(that.getTaxId(), that.getRank(), that.getTaxName());
	}

	/**
	 * Returns the taxonomy ID.
	 * @return the taxonomy ID
	 */
	public int getTaxId() {
		return taxId;
	}

	/**
	 * Sets the taxonomy ID.
	 * @param taxId The taxonomy ID to set
	 */
	public void setTaxId(int taxId) {
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
	public String getTaxName() {
		return taxName;
	}
	
	/**
	 * Sets the taxonomy name.
	 * @param taxName the taxonomy name to set
	 */
	public void setTaxName(String taxName) {
		this.taxName = taxName;
	}

	@Override
	public String toString() {
		return "" + getTaxName() + " (" + getTaxId() + ") | " + getRank() ;
	}
}
