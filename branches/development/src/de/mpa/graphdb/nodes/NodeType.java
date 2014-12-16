package de.mpa.graphdb.nodes;

/**
 * Enumeration containing all types of nodes inside the graph database.
 * 
 * @author T. Muth, A. Behne
 */
public enum NodeType {
	EXPERIMENTS("Experiments"),
	PROTEINS("Proteins"),
	PEPTIDES("Peptides"),
	PSMS("PSMs"),
	METAPROTEINS("Metaproteins"),
	ENZYMES("Enzymes"),
	PATHWAYS("Pathways"),
	ONTOLOGIES("Ontologies"),
	TAXA("Taxa");
	
	/**
	 * A descriptive string for the node type.
	 */
	private String description;

	/**
	 * Constructs a node type enum member from the specified description.
	 * @param description the description
	 */
	private NodeType(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		return description;
	}
}
