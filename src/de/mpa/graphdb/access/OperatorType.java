package de.mpa.graphdb.access;

/**
 * Enumeration holding various operators for use in conditional statements of Cypher queries.
 * @see CypherQuery
 * 
 * @author T. Muth, A. Behne
 */
public enum OperatorType {

	/* Boolean operators */
	AND,
	OR,
	NOT,		// unary
	/* Relational operators */
	GREATER_THAN,
	GREATER_EQUALS,
	LESS_THAN,
	LESS_EQUALS,
	EQUALS,
	NOT_EQUALS,
	REGEXP,
	/* Miscellaneous operators */
	HAS,		// unary
	IS_NULL,	// unary
	IN
	
}
