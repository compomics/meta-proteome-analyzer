package de.mpa.analysis.taxonomy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ComboBoxModel;

import de.mpa.analysis.UniProtUtilities;
import de.mpa.analysis.UniProtUtilities.TaxonomyRank;
import de.mpa.client.Client;
import de.mpa.client.model.SpectrumMatch;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.model.dbsearch.ProteinHitList;
import de.mpa.client.settings.ParameterMap;
import de.mpa.db.accessor.Taxonomy;

/**
 * This class serves as utility class for various methods handling taxonomic issues.
 * 
 * @author T. Muth, A. Behne
 *
 */
public class TaxonomyUtils {
	
	/**
	 * Enumeration holding taxonomy definition-related constants.
	 * @author A. Behne
	 */
	public enum TaxonomyDefinition {
		COMMON_ANCESTOR("by common ancestor") {
			@Override
			public TaxonomyNode getCommonTaxonomyNode(
					TaxonomyNode nodeA, TaxonomyNode nodeB) {
				// Get root paths of both taxonomy nodes
				TaxonomyNode[] path1 = nodeA.getPath();
				TaxonomyNode[] path2 = nodeB.getPath();
				TaxonomyNode ancestor;
			
				// Find last common element starting from the root
				int len = Math.min(path1.length, path2.length);
				if (len > 1) {
					ancestor = path1[0];	// initialize ancestor as root
					for (int i = 1; i < len; i++) {
						if (!path1[i].equals(path2[i])) {
							break;
						} 
						ancestor = path1[i];
					}
				} else {
					ancestor = nodeA;
				}
				return ancestor;
			}
		},
		MOST_SPECIFIC("by most specific member") {
			@Override
			public TaxonomyNode getCommonTaxonomyNode(
					TaxonomyNode nodeA, TaxonomyNode nodeB) {
				// Get root paths of both taxonomy nodes
				TaxonomyNode[] path1 = nodeA.getPath();
				TaxonomyNode[] path2 = nodeB.getPath();
				// return node at the end of the longer one of either paths
				if (path1.length >= path2.length) {
					return nodeA;
				} else {
					return nodeB;
				}
			}
		};
		
		/**
		 * The name string.
		 */
		private String name;

		/**
		 * Constructs a meta-protein generation rule using the specified name string.
		 * @param name the name of the rule
		 */
		private TaxonomyDefinition(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return this.name;
		}

		/**
		 * Returns the common taxonomy node of the specified pair of taxonomy nodes.
		 * @param nodeA the first taxonomy node
		 * @param nodeB the second taxonomy node
		 * @return the common taxonomy node
		 */
		public abstract TaxonomyNode getCommonTaxonomyNode(
				TaxonomyNode nodeA, TaxonomyNode nodeB);
	}

	/**
	 * Private constructor as class contains only static helper methods.
	 */
	private TaxonomyUtils() {}

	/**
	 * This method creates a taxonomy node which contains all ancestor taxonomy nodes up to the root node.
	 * @param currentID Taxonomy ID
	 * @param taxonomyMap Taxonomy Map containing taxonomy DB accessor objects.
	 * @return TaxonomyNode Taxonomy node in the end state.
	 */
	public static TaxonomyNode createTaxonomyNode(long currentID, Map<Long, Taxonomy> taxonomyMap) {
		
		Taxonomy current = taxonomyMap.get(currentID);
		Map<String, TaxonomyRank> targetRanks = UniProtUtilities.TAXONOMY_RANKS_MAP;

		// Check for rank being contained in the main categories (from superkingdom to species)
		TaxonomyRank taxonomyRank = targetRanks.get(current.getRank());
		if (taxonomyRank == null) {
			// TODO: Check whether the general category "species" holds true for all available ranks.
			taxonomyRank = TaxonomyRank.SPECIES;
		}
		
		// Create leaf node
		TaxonomyNode leafNode = new TaxonomyNode(
				(int) current.getTaxonomyid(), taxonomyRank, current.getDescription());
		
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
					TaxonomyNode parentNode = new TaxonomyNode(
							(int) current.getTaxonomyid(), parentRank, current.getDescription());
					currentNode.setParentNode(parentNode);
					// TODO: consider subspecies distinction in database, so far all subspecies are labeled species there (Nov. 2013)
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
	
	public static void determinePeptideTaxonomy(Set<PeptideHit> peptideSet, ParameterMap params) {
		determinePeptideTaxonomy(peptideSet, TaxonomyDefinition.COMMON_ANCESTOR);
	}

	/**
	 * Method to go through a peptide set and define for each peptide hit the
	 * common taxonomy of the subsequent proteins.
	 * @param peptideSet the peptide set
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
			TaxonomyNode parent = nodeMap.get(ancestor.getId());
			if (parent == null) {
				parent = child.getParentNode();
				// iterate up the taxonomy hierarchy until a mapped node is found (which may be the root)
				while (true) {
					// retrieve parent node from map
					TaxonomyNode temp = nodeMap.get(parent.getId());

					if (temp == null) {
						// add child's parent node to map
						child.setParentNode(parent);
						nodeMap.put(parent.getId(), parent);
						child = parent;
						parent = parent.getParentNode();
					} else {
						// replace child's parent node with mapped parent and break out of loop
						child.setParentNode(temp);
						break;
					}
				}
			} else {
				ancestor = parent;
			}

			// set peptide hit taxon node to ancestor
			peptideHit.setTaxonomyNode(ancestor);

			// possible TODO: determine spectrum taxonomy instead of inheriting directly from peptide
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
	 * @param metaProteins the list of meta-proteins for which common protein
	 *  taxonomies shall be determined
	 * @param params the parameter map containing taxonomy definition rules
	 */
	public static void determineMetaProteinTaxonomy(
			ProteinHitList metaProteins, ParameterMap params) {
		TaxonomyUtils.determineProteinTaxonomy(metaProteins, params);
	}

	/**
	 * Sets the taxonomy of proteins contained in the specified list to the
	 * common taxonomy based on their peptide taxonomies.
	 * @param proteinList List of proteins hits.
	 * @param params the parameter map containing taxonomy definition rules
	 */
	public static void determineProteinTaxonomy(
			List<ProteinHit> proteinList, ParameterMap params) {
		ComboBoxModel model = (ComboBoxModel) params.get("proteinTaxonomy").getValue();
		TaxonomyUtils.determineTaxonomy(proteinList,
				(TaxonomyDefinition) model.getSelectedItem());
	}

	/**
	 * Sets the taxonomy of the elements of the specified taxonomic list to the
	 * common taxonomy based on their child taxonomies.
	 * 
	 * @param taxList the list of taxonomic instances
	 * @param definition the taxonomy definition
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
					System.out.println("asdfsdds");
				}
				taxonNodes.add(taxNode);
			}
			
			// find common taxonomy node
			TaxonomyNode ancestor = taxonNodes.get(0);
			if (ancestor == null) {
				System.out.println("asfds");
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
	 * @param proteinHit Protein hit
	 * @param taxRank The taxonomic rank
	 * @return The name of the taxonomy.
	 */
	public static String getTaxonNameByRank(TaxonomyNode taxNode, TaxonomyRank taxRank) {
		// Default value for taxonomy name.
		String taxName = "root";

		while (taxNode.getId() != 1) { // unequal to root
			if (taxNode.getRank() == taxRank) {
				taxName = taxNode.getName();
				break;
			}
			taxNode = taxNode.getParentNode();
		}
		return taxName; 
	}

	/**
	 * Method to check whether a taxonomy belongs to a certain group determined by a certain NCBI taxonomy number.
	 * @param taxNode. Taxonomy node.
	 * @param filterTaxId. NCBI taxonomy ID.
	 * @return belongs to ? true / false
	 */
	public static boolean belongsToGroup(TaxonomyNode taxNode, long filterTaxId) {
		// Does not belong to group.
		boolean belongsToGroup = false;

		// To care for same taxID and especially for root as filtering level.
		if (filterTaxId == taxNode.getId()) { 
			belongsToGroup = true;
		} else {
			// Get all parents of the taxonNode and check whether they are equal to the filter level.
			while (taxNode.getParentNode() != null || (taxNode.getId() != 1)) {
				// Get parent taxon node of protein entry.
				try {
					taxNode = taxNode.getParentNode();
				} catch (Exception e) {
					e.printStackTrace();
				}
				// Check for filter ID
				if (filterTaxId == taxNode.getId()) {
					belongsToGroup = true;
					break;
				}
			}
		}
		return belongsToGroup;
	}

	// TODO: Probably most of the legacy code from NcbiTaxonomy class can be removed (see below)...  

	//	/**
	//	 * Returns a parent taxonomy node of the specified child taxonomy node.
	//	 * @param childNode the child node
	//	 * @return a parent node
	//	 * @throws Exception if an I/O error occurs
	//	 */
	//	synchronized public TaxonomyNode getParentTaxonomyNode(TaxonomyNode childNode) throws Exception {
	//		return this.getParentTaxonomyNode(childNode, true);
	//	}

	//	/**
	//	 * Returns a parent taxonomy node of the specified child taxonomy node. The
	//	 * parent node's rank may be forced to conform to the list of known ranks.
	//	 * @param childNode the child node
	//	 * @param knownRanksOnly <code>true</code> if the parent node's rank shall be
	//	 *  one of those specified in the list of known ranks, <code>false</code> otherwise
	//	 * @return a parent node
	//	 * @throws Exception if an I/O error occurs
	//	 */
	//	synchronized public TaxonomyNode getParentTaxonomyNode(TaxonomyNode childNode, boolean knownRanksOnly) throws Exception {
	//
	//		// Get parent data
	//		int parentTaxId = this.getParentTaxId(childNode.getId());
	//		String rank = this.getRank(parentTaxId);
	//
	//		if (knownRanksOnly) {
	//			// As long as parent rank is not inside list of known ranks move up in taxonomic tree
	//			while ((parentTaxId != 1) && !ranks.contains(rank)) {
	//				parentTaxId = this.getParentTaxId(parentTaxId);
	//				rank = this.getRank(parentTaxId);
	//			}
	//		}
	//
	//		// Wrap parent data in taxonomy node
	//		return new TaxonomyNode(parentTaxId, rank, this.getTaxonName(parentTaxId));
	//	}
	//	/**
	//	 * Returns the name of the taxonomy node belonging to the specified taxonomy id.
	//	 * @param taxID the taxonomy id
	//	 * @return the taxonomy node name
	//	 * @throws Exception if an I/O error occurs
	//	 */
	//	synchronized public String getTaxonName(int taxID) throws Exception {
	//
	//		// Get mapping
	//		int pos = namesMap.get(taxID);
	//
	//		// Skip to mapped byte position in names file
	//		namesRaf.seek(pos);
	//
	//		// Read line and isolate second non-numeric value
	//		String line = namesRaf.readLine();
	//		line = line.substring(line.indexOf("\t|\t") + 3);
	//		line = line.substring(0, line.indexOf("\t"));
	//
	//		return line;
	//	}
	//
	//	/**
	//	 * Returns the parent taxonomy id of the taxonomy node belonging to the specified taxonomy id.
	//	 * @param taxId the taxonomy id
	//	 * @return the parent taxonomy id
	//	 * @throws Exception if an I/O error occurs
	//	 */
	//	synchronized public int getParentTaxId(int taxId) throws Exception {
	//
	//		// Get mapping
	//		int pos = nodesMap.get(taxId);
	//
	//		// Skip to mapped byte position in nodes file
	//		nodesRaf.seek(pos);
	//
	//		// Read line and isolate second numeric value
	//		String line = nodesRaf.readLine();
	//		line = line.substring(line.indexOf("\t|\t") + 3);
	//		line = line.substring(0, line.indexOf("\t"));
	//		return Integer.valueOf(line).intValue();
	//
	//	}
	//
	//
	//
	//	/**
	//	 * Returns the taxonomic rank identifier of the taxonomy node belonging to the specified taxonomy id.
	//	 * @param taxID the taxonomy id
	//	 * @return the taxonomic rank
	//	 * @throws Exception if an I/O error occurs
	//	 */
	//	synchronized public String getRank(int taxID) throws Exception {
	//
	//		// Get mapping
	//		int pos = nodesMap.get(taxID);
	//
	//		// Skip to mapped byte position in nodes file
	//		nodesRaf.seek(pos);
	//
	//		// Read line and isolate third non-numeric value
	//		String line = nodesRaf.readLine();
	//		line = line.substring(line.indexOf("\t|\t") + 3);
	//		line = line.substring(line.indexOf("\t|\t") + 3);
	//		line = line.substring(0, line.indexOf("\t"));
	//
	//		return line;
	//	}
	//
	//	/**
	//	 * This method creates a TaxonNode for a certain taxID.
	//	 * @param taxId the taxonomy id
	//	 * @return The taxonNode containing the taxID, rank, taxName
	//	 * @throws Exception if an I/O error occurs
	//	 */
	//	public TaxonomyNode createTaxonNode(int taxId) throws Exception {
	//		TaxonomyNode taxNode = null;
	//		taxNode = new TaxonomyNode(taxId,
	//				this.getRank(taxId),
	//				this.getTaxonName(taxId));
	//		return taxNode;
	//	}
	//	
	//	/**
	//	 * Creates and returns the common taxonomy node above taxonomy nodes belonging to the
	//	 * specified taxonomy IDs.
	//	 * @param taxId1 the first taxonomy ID
	//	 * @param taxId2 the second taxonomy ID
	//	 * @return the common taxonomy node
	//	 * @throws Exception 
	//	 */
	//	synchronized public TaxonomyNode createCommonTaxonomyNode(TaxonomyNode node1, Taxonomy node2) throws Exception {
	//		return this.createTaxonNode(this.getCommonTaxonomyId(taxId1, taxId2));
	//	}
	//
	//
	//
	//	/**
	//	 * Finds the taxonomy level (taxID) were the 2 taxonomy levels intersect.
	//	 * @param taxId1 NCBI taxonomy of the first entry
	//	 * @param taxId2 NCBI taxonomy of the second entry
	//	 * @return the NCBI taxonomy ID where both entries intersect (or 0 when something went wrong)
	//	 * @throws Exception 
	//	 */
	//	public static Taxonomy getCommonTaxonomy(int taxId1, int taxId2) throws Exception {
	//
	//		// List of taxonomy entries for the first taxonomy entry.
	//		List<Integer> taxList1 = new ArrayList<Integer>();
	//		taxList1.add(taxId1);
	//		while (taxId1 != 1) {	// 1 is the root node
	//			try {
	//				taxId1 = this.getParentTaxId(taxId1);
	//				taxList1.add(taxId1);
	//			} catch (Exception e) {
	//				e.printStackTrace();
	//			}
	//		}
	//
	//		// List of taxonomy entries for the second taxonomy entry.
	//		List<Integer> taxList2 = new ArrayList<Integer>();
	//		taxList2.add(taxId2);
	//		while (taxId2 != 1) {	// 1 is the root node
	//			try {
	//				taxId2 = this.getParentTaxId(taxId2);
	//				taxList2.add(taxId2);
	//			} catch (Exception e) {
	//				e.printStackTrace();
	//			}
	//		}
	//
	//		// Get common ancestor
	//		Integer taxId = 0;
	//		for (int i = 0; i < taxList1.size(); i++) {
	//			taxId = taxList1.get(i);
	//			if (taxList2.contains(taxId)) {
	//				break;
	//			}
	//		}
	//
	//		// Find ancestor of closest known rank type
	//		while (!ranks.contains(this.getRank(taxId)) && (taxId != 1)) {
	//			taxId = this.getParentTaxId(taxId);
	//		}
	//
	//		return taxId;
	//	}
}	

