package de.mpa.graphdb.access;

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
	 * The direction of the relation.
	 */
	private DirectionType direction;

	/**
	 * Constructs a Cypher match from the specified target node, relation and direction parameters.
	 * @param targetVar the targeted variable
	 * @param relation the relation type
	 * @param direction the relation direction
	 */
	public CypherMatch(String targetVar, RelationType relation, DirectionType direction) {
		this.targetVar = targetVar;
		this.relation = relation;
		this.direction = direction;
	}

	@Override
	public String toString() {
		String match = "(" + getTargetVar() + ")";
		if (relation != null) {
			match += direction.getLeft() + "[:" + relation + "]" + direction.getRight();
		} else {
			if (direction != null) {
				match += direction.getLeft() + direction.getRight();
			}
		}
		return match;
	}

	/**
	 * Returns the targeted variable name.
	 * @return the targeted variable name
	 */
	public String getTargetVar() {
		return targetVar;
	}

}
