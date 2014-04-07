package de.mpa.graphdb.cypher;

import de.mpa.graphdb.nodes.NodeType;
import de.mpa.graphdb.properties.ElementProperty;

/**
 * Class representing a start node for use in Cypher queries.
 * @see CypherQuery
 * 
 * @author T. Muth, A. Behne
 */
public class CypherStartNode {
	
	/**
	 * The variable name for the (list of) node(s).
	 */
	private String identifier;

	/**
	 * The node type index.
	 */
	private NodeType index;
	
	/**
	 * The node property.
	 */
	private ElementProperty property;
	
	/**
	 * The property value.
	 */
	private Object value;
	
	/**
	 * Constructs a (list of) starting node(s) for use in Cypher queries from
	 * the specified identifier, index name, node property and property value.
	 * @param identifier the node identifier(s)
	 * @param index the index name
	 * @param property the property type
	 * @param value the property value
	 */
	public CypherStartNode(String identifier, NodeType index,
			ElementProperty property, Object value) {
		super();
		this.identifier = identifier;
		this.index = index;
		this.property = property;
		this.value = value;
	}
	
	/**
	 * Returns the identifier.
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * Returns the node index reference.
	 * @return the node index
	 */
	public NodeType getIndex() {
		return index;
	}

	@Override
	public String toString() {
		if (index != null) {
			return identifier + " = node:" + index + "(\"" + property + ":" + value + "\")";
		}
		return identifier + " = node(*)";
	}
}
