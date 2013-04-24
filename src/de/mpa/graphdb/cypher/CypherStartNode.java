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
	private String varName;

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
	 * the specified variable name, index name, node property and property
	 * value.
	 * @param varName the variable name for the node(s)
	 * @param index the index name
	 * @param property the property type
	 * @param value the property value
	 */
	public CypherStartNode(String varName, NodeType index,
			ElementProperty property, Object value) {
		super();
		this.varName = varName;
		this.index = index;
		this.property = property;
		this.value = value;
	}
	
	/**
	 * Returns the variable name
	 * @return the variable name
	 */
	public String getVariableName() {
		return varName;
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
			return varName + " = node:" + index + "(\"" + property + ":" + value + "\")";
		}
		return varName + " = node(*)";
	}
}
