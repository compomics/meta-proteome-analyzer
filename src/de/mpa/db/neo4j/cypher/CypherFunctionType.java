package de.mpa.db.neo4j.cypher;

public enum CypherFunctionType {

	/* Boolean functions */
	NOT,
	HAS,
	/* Aggregate functions */
	COUNT,		// unary
	SUM,		// unary
	AVG,		// unary
	MAX,		// unary
	MIN,		// unary
	/* Math functions */
	ABS,
	ROUND,
	SQRT,
	SIGN
	
}
