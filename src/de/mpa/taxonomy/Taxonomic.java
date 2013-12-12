package de.mpa.taxonomy;

import java.util.List;

/**
 * Interface to enforce taxonomic getter and setter methods.
 * 
 * @author A. Behne, R. Heyer
 */
public interface Taxonomic {

	/**
	 * Returns the taxonomy node.
	 * @return the taxonomy node
	 */
	public TaxonomyNode getTaxonomyNode();
	
	/**
	 * Sets the taxonomy node.
	 * @param taxNode the taxonomy node to set
	 */
	public void setTaxonomyNode(TaxonomyNode taxonNode);
	
	/**
	 * Returns the list of child taxonomic instances.
	 * @return the children
	 */
	public List<? extends Taxonomic> getTaxonomicChildren();
	
}
