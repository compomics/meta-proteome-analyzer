package de.mpa.db.neo4j.cypher;

/**
 * Enumeration holding various operators for use in conditional statements of Cypher queries.
 * @see CypherQuery
 * 
 * @author T. Muth, A. Behne
 */
public enum CypherOperatorType {

	/* Arithmetic operators */
	PLUS("+"),
	MINUS("-"),
	MULT("*"),
	DIV("/"),
	MOD("%"),
	/* Boolean operators */
	AND("AND"),
	OR("OR"),
	/* Relational operators */
	GREATER_THAN(">"),
	GREATER_EQUALS(">="),
	LESS_THAN("<"),
	LESS_EQUALS("<="),
	EQUALS("="),
	NOT_EQUALS("<>"),
	REGEXP("=~"),
	/* Miscellaneous operators */
	IS_NULL("IS NULL"),	// unary
	IN("IN");
	
	private final String val;
	
	CypherOperatorType(String val) {
		this.val = val;
	}
	
	@Override
	public String toString() {
		return this.val;
	}
	
}
