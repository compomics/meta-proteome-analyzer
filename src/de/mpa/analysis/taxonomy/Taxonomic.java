package de.mpa.analysis.taxonomy;

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
    TaxonomyNode getTaxonomyNode();
	
	/**
	 * Sets the taxonomy node.
	 * @param taxNode the taxonomy node to set
	 */
    void setTaxonomyNode(TaxonomyNode taxonNode);
	
	/**
	 * Returns the list of child taxonomic instances.
	 * @return the children
	 */
    List<? extends Taxonomic> getTaxonomicChildren();
	
}
