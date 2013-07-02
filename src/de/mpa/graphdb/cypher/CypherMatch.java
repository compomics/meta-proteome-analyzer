package de.mpa.graphdb.cypher;

import de.mpa.graphdb.edges.DirectionType;
import de.mpa.graphdb.edges.RelationType;

/**
 * Class representing (part of) a match statement for use in Cypher queries.
 * @see CypherQuery
 * 
 * @author T. Muth, A. Behne
 */
public class CypherMatch {

	/**
	 * The node identifier.
	 */
	private String nodeId;
	
	/**
	 * The relation type.
	 */
	private RelationType relation;
	
	/**
	 * The relation identifier.
	 */
	private String relationId;
	
	/**
	 * The direction of the relation.
	 */
	private DirectionType direction;

	/**
	 * Constructs a Cypher match from the specified target node, relation and direction parameters.
	 * @param nodeId the node identifier
	 * @param relation the relation type
	 * @param relationId the relation identifier
	 * @param direction the relation direction
	 */
	public CypherMatch(String nodeId, RelationType relation, String relationId, DirectionType direction) {
		this.nodeId = nodeId;
		this.relationId = (relationId == null) ? "" : relationId;
		this.relation = relation;
		this.direction = direction;
	}

	/**
	 * Returns the node identifier.
	 * @return the node identifier
	 */
	public String getNodeIdentifier() {
		return nodeId;
	}

	/**
	 * Returns the relation identifier.
	 * @return the relation identifier
	 */
	public String getRelationIdentifier() {
		return relationId;
	}

	/**
	 * Returns the relation.
	 * @return the relation
	 */
	public RelationType getRelation() {
		return relation;
	}

	/**
	 * Returns the direction.
	 * @return the direction
	 */
	public DirectionType getDirection() {
		return direction;
	}

	@Override
	public String toString() {
		String match = "(" + getNodeIdentifier() + ")";
		if (relation != null) {
			match += direction.getLeft() + "[" + relationId + ":" + relation + "]" + direction.getRight();
		} else {
			if (direction != null) {
				match += direction.getLeft() + direction.getRight();
			}
		}
		return match;
	}

}
