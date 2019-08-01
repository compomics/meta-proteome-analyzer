package de.mpa.model.taxonomy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.mpa.model.analysis.UniProtUtilities;


/**
 * This class represents a NCBI taxonomy entry, complete with taxonomy ID as
 * well as rank and name specifiers.
 * 
 * @author R. Heyer and A. Behne
 */
public class TaxonomyNode implements Comparable<Object>, Serializable {
	
	/**
	 * Serialization ID set to default == 1L;
	 */
	private static final long serialVersionUID = 1L; 
	
	/**
	 * The taxonomy ID.
	 */
	private int taxId;

	/**
	 * The taxonomic rank.
	 */
	private UniProtUtilities.TaxonomyRank taxRank;

	/**
	 * The taxonomy name.
	 */
	private String taxName;

	/**
	 * The parent taxonomy node.
	 */
	private TaxonomyNode parentNode;

	/**
	 * Constructs a NCBI taxonomy node.
	 * @param taxId The taxonomy ID
	 * @param rank The taxonomic rank
	 * @param description The taxonomy description
	 */
	public TaxonomyNode(int taxId, UniProtUtilities.TaxonomyRank rank, String description) {
		this(taxId, rank, description, null);
	}

	/**
	 * Constructs a NCBI taxonomy node.
	 * @param taxId The taxonomy ID
	 * @param rank The taxonomic rank
	 * @param taxName The taxonomy name
	 * @param parentNode Parent node
	 */
	public TaxonomyNode(int taxId, UniProtUtilities.TaxonomyRank rank, String taxName, TaxonomyNode parentNode) {
		this.taxId = taxId;
		this.taxRank = rank;
		this.taxName = taxName;
		this.parentNode = parentNode;
	}
	
	
//	/**
//	 * Constructs taxonomy node from DB-taxonomy
//	 * 
//	 * @param taxonomy
//	 */
//	public TaxonomyNode(Taxonomy taxonomy) {
//		this.taxId = (int) taxonomy.getTaxonomyid();
//		this.taxRank = UniProtUtilities.TAXONOMY_RANKS_MAP.get(taxonomy.getRank());
//		this.taxName = taxonomy.getDescription();
//		// no parent for now
////		this.parentNode = new TaxonomyNode(taxonomy.getParentid());
//	}

	/**
	 * Returns the taxonomy ID.
	 * @return the taxonomy ID
	 */
	public int getID() {
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
	public UniProtUtilities.TaxonomyRank getRank() {
		return taxRank;
	}

	/**
	 * Sets the taxonomic rank.
	 * @param rank The taxonomic rank to set
	 */
	public void setRank(UniProtUtilities.TaxonomyRank rank) {
		this.taxRank = rank;
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

	/**
	 * Returns the parent taxonomy node.
	 * @return the parent taxonomy node
	 */
	public TaxonomyNode getParentNode() {
		return parentNode;
	}

	/**
	 * Returns the parent taxonomy node of the specified rank.
	 * 
	 * If the currentnode is already above the specified rank: returns unknown node
	 * If this node or parents is Uncategorized, returns uncategorized node
	 * If we jump above the specified rank return unknown node
	 * If there is an inconsistency in the taxonomy tree return unknown node
	 * 
	 * @param rank the desired parent rank
	 * @return the parent taxonomy node of the desired rank.
	 */
	public TaxonomyNode getParentNode(UniProtUtilities.TaxonomyRank rank) {
		TaxonomyNode currentNode = this;
		TaxonomyNode parentNode = this;
		
		// Inconsistency in NCBI Tax: "Uncategorized" --> return "Unknown"
		if (this.getID() == 0) {
			return new TaxonomyNode(0, rank, "Unknown " + rank.name());
		}
		// Inconsistency in NCBI Tax: "cellular organisms" --> return "root" which is the parent node
		if (this.getID() == 131567) {
			return new TaxonomyNode(0, rank, "Unknown " + rank.name());
		}

		if (parentNode.getID() == 1) {
//			return this;
			// this "Unknown value needs to be checked in graphs I think
			return new TaxonomyNode(0, rank, "Unknown " + rank.name());
		}

		UniProtUtilities.TaxonomyRank parentRank = parentNode.getRank();
		while (!rank.equals(parentRank)) {
			parentNode = parentNode.getParentNode();
			if (parentNode.getID() == 1) {
//				System.err.println("Root reached, possibly unknown rank identifier " +
//						"\'" + rank + "\' for " + this.getRank() + " " + this.getName() + " (" + this.getId() + ")");
				
//				parentNode = this;
				parentNode = new TaxonomyNode(0, rank, "Unknown " + rank.name());
				break;
			}
			parentRank = parentNode.getRank();
		}
		return parentNode;
	}

	/**
	 * Sets the parent taxonomy node.
	 * @param parentNode the parent node to set
	 */
	public void setParentNode(TaxonomyNode parentNode) {
		this.parentNode = parentNode;
	}
	
	/**
	 * Returns whether this node is the taxonomic root.
	 * @return <code>true</code> if this node is the taxonomic root, <code>false</code> otherwise
	 */
	public boolean isRoot() {
		return (taxId == 1);
	}
	
	/**
	 * Returns the list of parent taxonomy nodes of this node up to the taxonomy
	 * root (but without "root" or "unclassified"). The last element of the list is this node.
	 * @return the taxonomy path
	 */
	public TaxonomyNode[] getPath() {
		List<TaxonomyNode> path = new ArrayList<TaxonomyNode>();
		TaxonomyNode parent = this;
		while (parent.getID() != 1) {
			path.add(parent);
			parent = parent.getParentNode();
		}
		Collections.reverse(path);
		return path.toArray(new TaxonomyNode[path.size()]);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TaxonomyNode) {
			TaxonomyNode that = (TaxonomyNode) obj;			
			return (getID() == that.getID());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return getID();
	}

	@Override
	public String toString() {
		return getName() + " (" + getRank() + ") [" + getID() + "]";
	}

	@Override
	public int compareTo(Object obj) {
		if (obj instanceof TaxonomyNode) {
			TaxonomyNode that = (TaxonomyNode) obj;			
			if (getID()> that.getID()) {
				return 1;
			}else if (getID()== that.getID()) {
				return 0;
			}else if (getID()< that.getID()) {
				return -1;
			}
		}
		return 0;
	}
}