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
	 * The targeted variable.
	 */
	private String targetVar;
	
	/**
	 * The relation type.
	 */
	private RelationType relation;
	
	/**
	 * Variable specifying the relation.
	 */
	private String relationVar;
	
	/**
	 * The direction of the relation.
	 */
	private DirectionType direction;

	/**
	 * Constructs a Cypher match from the specified target node, relation and direction parameters.
	 * @param targetVar the targeted variable
	 * @param relation the relation type
	 * @param direction the relation direction
	 */
	public CypherMatch(String targetVar, RelationType relation, String relationVar, DirectionType direction) {
		this.targetVar = targetVar;
		this.relationVar = (relationVar == null) ? "" : relationVar;
		this.relation = relation;
		this.direction = direction;
	}

	/**
	 * Returns the targeted variable name.
	 * @return the targeted variable name
	 */
	public String getTargetVariableName() {
		return targetVar;
	}

	/**
	 * @return the relationVar
	 */
	public String getRelationVariableName() {
		return relationVar;
	}

	/**
	 * @return the relation
	 */
	public RelationType getRelation() {
		return relation;
	}

	/**
	 * @return the direction
	 */
	public DirectionType getDirection() {
		return direction;
	}

	@Override
	public String toString() {
		String match = "(" + getTargetVariableName() + ")";
		if (relation != null) {
			match += direction.getLeft() + "[" + relationVar + ":" + relation + "]" + direction.getRight();
		} else {
			if (direction != null) {
				match += direction.getLeft() + direction.getRight();
			}
		}
		return match;
	}

}
