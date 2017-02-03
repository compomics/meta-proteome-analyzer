package de.mpa.analysis.taxonomy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.mpa.analysis.UniProtUtilities;
import de.mpa.analysis.UniProtUtilities.TaxonomyRank;
import de.mpa.client.Client;
import de.mpa.client.model.SpectrumMatch;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.model.dbsearch.ProteinHitList;
import de.mpa.client.model.dbsearch.Tax;
import de.mpa.client.settings.ParameterMap;

/**
 * This class serves as utility class for various methods handling taxonomic
 * issues.
 * 
 * @author T. Muth, A. Behne
 *
 */
public class TaxonomyUtils {

	/**
	 * Enumeration holding taxonomy definition-related constants.
	 * 
	 * @author A. Behne
	 */
	public enum TaxonomyDefinition {
		COMMON_ANCESTOR("by common ancestor") {
			@Override
			public TaxonomyNode getCommonTaxonomyNode(TaxonomyNode nodeA, TaxonomyNode nodeB) {
				// Get root paths of both taxonomy nodes
				TaxonomyNode[] path1 = nodeA.getPath();
				TaxonomyNode[] path2 = nodeB.getPath();
				TaxonomyNode ancestor = null;

				// Find last common element starting from the root
				int len = Math.min(path1.length, path2.length);

				// Only root
				if (len == 0) {
					// is root
					ancestor = new TaxonomyNode(1, TaxonomyRank.NO_RANK, "root");
				}

				// Taxonomy is superkingdom or "unclassified" (taxID == "0")
				if (len == 1) {
					// Both are unclassified
					if (nodeA.getID() == 0 && nodeB.getID() == 0) {
						ancestor = new TaxonomyNode(1, TaxonomyRank.NO_RANK, "root");
					}
					// Just one is unclassified (Taxonomy ID is "0")
					else if (nodeA.getID() == 0) {
						ancestor = nodeB;
					}
					// Just one is unclassified (Taxonomy ID is "0")
					else if (nodeB.getID() == 0) {
						ancestor = nodeA;
						// Similar superkingdom
					} else if (path1[0].equals(path2[0])) {
						ancestor = path1[0];
						// Else different superkingdoms
					} else if (!path1[0].equals(path2[0])) {
						ancestor = new TaxonomyNode(1, TaxonomyRank.NO_RANK, "root");
					}
				}
				// Find common ancestor of both paths
				if (len > 1) {
					// check for different superkingdoms
					if (!path1[0].equals(path2[0])) {
						ancestor = new TaxonomyNode(1, TaxonomyRank.NO_RANK, "root");
					} else {
						ancestor = path1[0]; // initialize ancestor as root
						for (int i = 1; i < len; i++) {
							if (!path1[i].equals(path2[i])) {
								break;
							}
							ancestor = path1[i];
						}
					}
				}
				return ancestor;
			}
		},
		MOST_SPECIFIC("by most specific member") {
			@Override
			public TaxonomyNode getCommonTaxonomyNode(TaxonomyNode nodeA, TaxonomyNode nodeB) {
				// Get root paths of both taxonomy nodes
				TaxonomyNode[] path1 = nodeA.getPath();
				TaxonomyNode[] path2 = nodeB.getPath();
				// return node at the end of the longer one of either paths
				if (path1.length >= path2.length) {
					return nodeA;
				}
				return nodeB;
			}
		};

		/**
		 * The name string.
		 */
		private String name;

		/**
		 * Constructs a meta-protein generation rule using the specified name
		 * string.
		 * 
		 * @param name
		 *            the name of the rule
		 */
		private TaxonomyDefinition(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return this.name;
		}

		/**
		 * Returns the common taxonomy node of the specified pair of taxonomy
		 * nodes.
		 * 
		 * @param nodeA
		 *            the first taxonomy node
		 * @param nodeB
		 *            the second taxonomy node
		 * @return the common taxonomy node
		 */
		public abstract TaxonomyNode getCommonTaxonomyNode(TaxonomyNode nodeA, TaxonomyNode nodeB);
	}

	/**
	 * Private constructor as class contains only static helper methods.
	 */
	private TaxonomyUtils() {
	}

	/**
	 * This method creates a taxonomy node which contains all ancestor taxonomy
	 * nodes up to the root node.
	 * 
	 * @param currentID
	 *            Taxonomy ID
	 * @param taxonomyMap
	 *            Taxonomy Map containing taxonomy DB accessor objects.
	 * @return TaxonomyNode Taxonomy node in the end state.
	 */
	public static TaxonomyNode createTaxonomyNode(long currentID, Map<Long, Tax> taxonomyMap) {

		Tax current = taxonomyMap.get(currentID);
		if (current == null) return null;
		Map<String, TaxonomyRank> targetRanks = UniProtUtilities.TAXONOMY_RANKS_MAP;

		// Check for rank being contained in the main categories (from
		// superkingdom to species)
		TaxonomyRank taxonomyRank = targetRanks.get(current.getRank());
		if (taxonomyRank == null) {
			// TODO: Check whether the general category "species" holds true for
			// all available ranks.
			taxonomyRank = TaxonomyRank.SPECIES;
		}

		// Create leaf node
		TaxonomyNode leafNode = new TaxonomyNode((int) current.getTaxonomyid(), taxonomyRank, current.getDescription());

		// Iterate up taxonomic hierarchy and create parent nodes
		TaxonomyNode currentNode = leafNode;
		boolean reachedRoot = false;
		while (!reachedRoot) {
			long parentID = current.getParentid();
			current = taxonomyMap.get(parentID);
			if (current != null) {
				// Check whether we have reached the root already
				reachedRoot = (current.getParentid() == 0L);
				// Check whether parent rank is in targeted ranks
				TaxonomyRank parentRank = targetRanks.get(current.getRank());
				if (parentRank != null) {
					// Create and configure parent node
					TaxonomyNode parentNode = new TaxonomyNode((int) current.getTaxonomyid(), parentRank,
							current.getDescription());
					currentNode.setParentNode(parentNode);
					// TODO: consider subspecies distinction in database, so far
					// all subspecies are labeled species there (Nov. 2013)
					if (parentRank == TaxonomyRank.SPECIES) {
						currentNode.setRank(TaxonomyRank.SUBSPECIES);
					}
					currentNode = parentNode;
				}
			} else {
				System.err.println("Unknown parent ID: " + parentID);
				break;
			}
		}
		return leafNode;
	}

	/**
	 * Method to go through a peptide set and define for each peptide hit the
	 * common taxonomy of the subsequent proteins.
	 * 
	 * @param peptideSet
	 *            the peptide set
	 */
	public static void determinePeptideTaxonomy(Set<PeptideHit> peptideSet, TaxonomyDefinition definition) {

		// Map with taxonomy entries used to merge redundant nodes
		Map<Integer, TaxonomyNode> nodeMap = new HashMap<Integer, TaxonomyNode>();
		// Insert root node
		nodeMap.put(1, new TaxonomyNode(1, TaxonomyRank.NO_RANK, "root"));

		// Iterate peptides and gather common taxonomy
		for (PeptideHit peptideHit : peptideSet) {

			// Gather protein taxonomy nodes
			List<TaxonomyNode> taxonNodes = new ArrayList<TaxonomyNode>();

			for (ProteinHit proteinHit : peptideHit.getProteinHits()) {
				taxonNodes.add(proteinHit.getTaxonomyNode());
			}

			// Find common ancestor node
			TaxonomyNode ancestor = taxonNodes.get(0);
			for (int i = 0; i < taxonNodes.size(); i++) {
				ancestor = definition.getCommonTaxonomyNode(ancestor, taxonNodes.get(i));
			}

			// Gets the parent node of the taxon node
			TaxonomyNode child = ancestor;
			TaxonomyNode parent = nodeMap.get(ancestor.getID());
			if (parent == null) {
				parent = child.getParentNode();
				// iterate up the taxonomy hierarchy until a mapped node is
				// found (which may be the root)
				while (true) {
					// retrieve parent node from map
					TaxonomyNode temp = nodeMap.get(parent.getID());

					if (temp == null) {
						// add child's parent node to map
						child.setParentNode(parent);
						nodeMap.put(parent.getID(), parent);
						child = parent;
						parent = parent.getParentNode();
					} else {
						// replace child's parent node with mapped parent and
						// break out of loop
						child.setParentNode(temp);
						break;
					}
				}
			} else {
				ancestor = parent;
			}

			// set peptide hit taxon node to ancestor
			peptideHit.setTaxonomyNode(ancestor);

			// possible TODO: determine spectrum taxonomy instead of inheriting
			// directly from peptide
			for (SpectrumMatch match : peptideHit.getSpectrumMatches()) {
				match.setTaxonomyNode(ancestor);
			}

			// fire progress notification
			Client.getInstance().firePropertyChange("progressmade", false, true);
		}
	}

	/**
	 * Sets the taxonomy of meta-proteins contained in the specified list to the
	 * common taxonomy based on their child protein taxonomies.
	 * 
	 * @param metaProteins
	 *            the list of meta-proteins for which common protein taxonomies
	 *            shall be determined
	 * @param params
	 *            the parameter map containing taxonomy definition rules
	 */
	public static void determineMetaProteinTaxonomy(ProteinHitList metaProteins, ParameterMap params) {
		TaxonomyUtils.determineTaxonomy(metaProteins,
				(TaxonomyDefinition) params.get("metaProteinTaxonomy").getValue());
	}

	/**
	 * Sets the taxonomy of proteins contained in the specified list to the
	 * common taxonomy based on their peptide taxonomies.
	 * 
	 * @param proteins
	 *            List of proteins hits.
	 * @param params
	 *            the parameter map containing taxonomy definition rules
	 */
	public static void determineProteinTaxonomy(List<ProteinHit> proteins, ParameterMap params) {
		TaxonomyUtils.determineTaxonomy(proteins, (TaxonomyDefinition) params.get("proteinTaxonomy").getValue());
	}

	/**
	 * Sets the taxonomy of the elements of the specified taxonomic list to the
	 * common taxonomy based on their child taxonomies.
	 * 
	 * @param taxList
	 *            the list of taxonomic instances
	 * @param definition
	 *            the taxonomy definition
	 */
	public static void determineTaxonomy(List<? extends Taxonomic> taxList, TaxonomyDefinition definition) {
		// iterate taxonomic list
		for (Taxonomic taxonomic : taxList) {
			// extract child taxonomy nodes
			List<TaxonomyNode> taxonNodes = new ArrayList<TaxonomyNode>();
			List<? extends Taxonomic> children = taxonomic.getTaxonomicChildren();
			for (Taxonomic childTax : children) {
				TaxonomyNode taxNode = childTax.getTaxonomyNode();
				if (taxNode == null) {
					System.err.println("ERROR: no taxonomic children found for " + childTax);
				}
				taxonNodes.add(taxNode);
			}

			// find common taxonomy node
			TaxonomyNode ancestor = taxonNodes.get(0);
			if (ancestor == null) {
				System.err.println("ERROR: no taxonomic ancestor found for " + taxonomic);
			}
			for (int i = 1; i < taxonNodes.size(); i++) {
				ancestor = definition.getCommonTaxonomyNode(ancestor, taxonNodes.get(i));
			}

			// set common taxonomy node
			taxonomic.setTaxonomyNode(ancestor);

			// fire progress notification
			Client.getInstance().firePropertyChange("progressmade", false, true);
		}
	}

	/**
	 * Gets the tax name by the rank from the NCBI taxonomy.
	 * 
	 * @param proteinHit
	 *            Protein hit
	 * @param taxRank
	 *            The taxonomic rank
	 * @return The name of the taxonomy.
	 */
	public static String getTaxonNameByRank(TaxonomyNode taxNode, TaxonomyRank taxRank) {
		// Default value for taxonomy name.
		String taxName = "root";

		while (taxNode.getID() != 1) { // unequal to root
			if (taxNode.getRank() == taxRank) {
				taxName = taxNode.getName();
				break;
			}
			taxNode = taxNode.getParentNode();
		}
		return taxName;
	}

	/**
	 * Method to check whether a taxonomy belongs to a certain group determined
	 * by a certain NCBI taxonomy number.
	 * 
	 * @param taxNode.
	 *            Taxonomy node.
	 * @param filterTaxId.
	 *            NCBI taxonomy ID.
	 * @return belongs to ? true / false
	 */
	public static boolean belongsToGroup(TaxonomyNode taxNode, long filterTaxId) {
		// Does not belong to group.
		boolean belongsToGroup = false;

		// To care for same taxID and especially for root as filtering level.
		if (filterTaxId == taxNode.getID()) {
			belongsToGroup = true;
		} else {
			// Get all parents of the taxonNode and check whether they are equal
			// to the filter level.
			while (taxNode.getParentNode() != null || (taxNode.getID() != 1)) {
				// Get parent taxon node of protein entry.
				try {
					taxNode = taxNode.getParentNode();
				} catch (Exception e) {
					e.printStackTrace();
				}
				// Check for filter ID
				if (filterTaxId == taxNode.getID()) {
					belongsToGroup = true;
					break;
				}
			}
		}
		return belongsToGroup;
	}

}
