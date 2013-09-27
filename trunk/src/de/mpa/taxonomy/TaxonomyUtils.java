package de.mpa.taxonomy;

import java.util.Map;

import de.mpa.db.accessor.Taxonomy;

/**
 * This class models a taxonomic entry derived from the database.
 * 
 * @author T. Muth
 *
 */
public class TaxonomyUtils {

	/**
	 * Default constructor for the taxonomy entry, featuring IDs, rank and description.
	 */
	private TaxonomyUtils() {
	}
	
	/**
	 * This method creates a taxonomy node which contains all ancestor taxonomy nodes up to the root node.
	 * @param taxID Taxonomy ID
	 * @param taxonomyMap Taxonomy Map containing taxonomy DB accessor objects.
	 * @return TaxonomyNode Taxonomy node in the end state.
	 */
	public static TaxonomyNode createTaxonomyNode(long taxID, Map<Long, Taxonomy> taxonomyMap) {
		boolean reachedRoot = false;
		Taxonomy current = taxonomyMap.get(taxID);
		TaxonomyNode currentNode = new TaxonomyNode((int) current.getTaxonomyid(), current.getRank(), current.getDescription());
		TaxonomyNode leafNode = currentNode;
		while (!reachedRoot) {
			// Start
			current = taxonomyMap.get(taxID);
			long parentID = current.getParentid();
			Taxonomy ancestor = taxonomyMap.get(parentID);
			 ancestor.getParentid();
			if (ancestor.getParentid() == 0L) {
				reachedRoot = true;
			}
			if (ancestor != null) {
				TaxonomyNode parentNode = new TaxonomyNode(	(int) ancestor.getTaxonomyid(), ancestor.getRank(), ancestor.getDescription());
				currentNode.setParentNode(parentNode);
				currentNode = parentNode;
			}
			taxID = parentID;
		}
		return leafNode;
	}
	
	
	/**
	 * This method created an uncategorized taxonomy node for protein with UniProt entries. 
	 * @return Uncategorized taxonomy node.
	 */
	public static TaxonomyNode createUncatogorizedTaxonomyNode() {
		TaxonomyNode rootNode = new TaxonomyNode(1, "no rank", "root");
		TaxonomyNode uncategorizedNode = new TaxonomyNode(0, "no rank", "uncategorized", rootNode);
		return uncategorizedNode;
	}
}	

