package de.mpa.db.neo4j.cypher;

import de.mpa.db.neo4j.edges.DirectionType;
import de.mpa.db.neo4j.edges.RelationType;

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
	private final String nodeId;
	
	/**
	 * The relation type.
	 */
	private final RelationType relation;
	
	/**
	 * The relation identifier.
	 */
	private final String relationId;
	
	/**
	 * The direction of the relation.
	 */
	private final DirectionType direction;

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
		return this.nodeId;
	}

	/**
	 * Returns the relation identifier.
	 * @return the relation identifier
	 */
	public String getRelationIdentifier() {
		return this.relationId;
	}

	/**
	 * Returns the relation.
	 * @return the relation
	 */
	public RelationType getRelation() {
		return this.relation;
	}

	/**
	 * Returns the direction.
	 * @return the direction
	 */
	public DirectionType getDirection() {
		return this.direction;
	}

	@Override
	public String toString() {
		String match = "(" + this.getNodeIdentifier() + ")";
		if (this.relation != null) {
			match += this.direction.getLeft() + "[" + this.relationId + ":" + this.relation + "]" + this.direction.getRight();
		} else {
			if (this.direction != null) {
				match += this.direction.getLeft() + this.direction.getRight();
			}
		}
		return match;
	}

}
