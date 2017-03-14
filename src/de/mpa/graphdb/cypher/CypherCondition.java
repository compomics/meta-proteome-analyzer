package de.mpa.graphdb.cypher;


/**
 * Class representing (part of) a conditional statement for use in Cypher queries.
 * @see CypherQuery
 * 
 * @author T. Muth, A. Behne
 */
public class CypherCondition {

	/**
	 * Left-hand side of the conditional.
	 */
	private final Object leftTerm;
	
	/**
	 * Right-hand side of the conditional.
	 */
	private final Object rightTerm;
	
	/**
	 * The conditional operator.
	 */
	private final CypherOperatorType operator;
	
	/**
	 * Constructs a singular value for use in conditional statements of Cypher queries.
	 * @param value the singular value
	 */
	public CypherCondition(Object value) {
		this(value, null, null);
	}
	
	/**
	 * Constructs a representation of a conditional statement for use in Cypher queries. 
	 * Use <code>CypherCondition</code>s for left/right terms for nesting conditions. 
	 * @param leftTerm the left-hand side of the conditional
	 * @param rightTerm the right-hand side of the conditional (can be <code>null</code> for singular values)
	 * @param operator the conditional operator (can be <code>null</code> for singular values)
	 */
	public CypherCondition(Object leftTerm, Object rightTerm, CypherOperatorType operator) {
		this.leftTerm = leftTerm;
		this.rightTerm = rightTerm;
		this.operator = operator;
	}
	
	@Override
	public String toString() {
		if (this.rightTerm != null) {
			return "(" + this.leftTerm + " " + this.operator + " " + this.rightTerm + ")";
		}
		return this.leftTerm.toString();
	}
}
