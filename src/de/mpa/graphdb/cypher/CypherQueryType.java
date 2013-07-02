package de.mpa.graphdb.cypher;

/**
 * Enumeration containing various (pre-defined) Cypher query types and
 * associated factory methods.
 * 
 * @author T. Muth, A. Behne
 */
public enum CypherQueryType {
	
	PEPTIDES_BY_PROTEINS("Get all Peptides grouped by Proteins") {
		@Override
		public CypherQuery getQuery() {
			return CypherQueryFactory.getPeptidesByProteins();
		}
	},
	PROTEINS_BY_PEPTIDES("Get all Proteins grouped by Peptides") {
		@Override
		public CypherQuery getQuery() {
			return CypherQueryFactory.getProteinsByPeptides();
		}
	},
	PROTEINS_BY_ENZYMES("Get all Proteins grouped by Enzymes") {
		@Override
		public CypherQuery getQuery() {
			return CypherQueryFactory.getProteinsByEnzymes();
		}
	},
	PROTEINS_ONLY_KERATIN("Get all Proteins, only Keratin") {
		@Override
		public CypherQuery getQuery() {
			return CypherQueryFactory.getProteinsByDescriptionPattern("keratin");
		}
	},
	PROTEINS_EXCEPT_KERATIN("Get all Proteins, exclude Keratin") {
		@Override
		public CypherQuery getQuery() {
			return CypherQueryFactory.getProteinsByDescriptionPattern("keratin", true);
		}
	},
	CUSTOM("Custom Query") {
		@Override
		public CypherQuery getQuery() {
			return null;
		}
	};
	
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
