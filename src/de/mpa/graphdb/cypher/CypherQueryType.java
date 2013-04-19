package de.mpa.graphdb.cypher;

/**
 * Enumeration containing various (pre-defined) Cypher query types and
 * associated factory methods.
 * 
 * @author T. Muth, A. Behne
 */
public enum CypherQueryType {
	
	PROTEINS_BY_ENZYMES("Get all Proteins grouped by Enzymes") {
		@Override
		public CypherQuery getQuery() {
			return CypherQueryFactory.getProteinsByEnzymes();
		}
	},
	PROTEINS_BY_PEPTIDES("Get all Proteins grouped by Peptides") {
		@Override
		public CypherQuery getQuery() {
			return CypherQueryFactory.getProteinsByPeptides();
		}
	},
	PEPTIDES_BY_PROTEINS("Get all Peptides grouped by Proteins") {
		@Override
		public CypherQuery getQuery() {
			return CypherQueryFactory.getPeptidesByProteins();
		}
	}/*,
	CUSTOM {
		@Override
		public CypherQuery getQuery() {
			return null;
		}
	}*/;
	
	/**
	 * A descriptive string for the query type.
	 */
	private String description;
	
	/**
	 * Creates a Cypher query type enum member by the provided description.
	 * @param description the description string
	 */
	private CypherQueryType(String description) {
		this.description = description;
	}
	
	/**
	 * Returns the associated Cypher query.
	 * @return the associated Cypher query
	 */
	public abstract CypherQuery getQuery();
	
	/**
	 * Returns a descriptive string for the query type.
	 * @return the description string
	 */
	@Override
	public String toString() {
		return description;
	}
	
}
