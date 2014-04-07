package de.mpa.graphdb.cypher;

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
