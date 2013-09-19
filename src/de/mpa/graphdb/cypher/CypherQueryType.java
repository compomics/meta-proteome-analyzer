package de.mpa.graphdb.cypher;

import de.mpa.graphdb.edges.RelationType;

/**
 * Enumeration containing various (pre-defined) Cypher query types and
 * associated factory methods.
 * 
 * @author T. Muth, A. Behne
 */
public enum CypherQueryType {
	
	PEPTIDES_BY_PROTEINS("Get Peptides grouped by Proteins") {
		@Override
		public CypherQuery getQuery() {
			return CypherQueryFactory.getPeptidesByProteins();
		}
	},
	PROTEINS_BY_PEPTIDES("Get Proteins grouped by Peptides") {
		@Override
		public CypherQuery getQuery() {
			return CypherQueryFactory.getProteinsByPeptides();
		}
	},
	PROTEINS_BY_TAXA("Get Proteins grouped by Taxonomies") {
		@Override
		public CypherQuery getQuery() {
			return CypherQueryFactory.getTaxaByProteins();
		}
	},	
	PEPTIDES_BY_TAXA("Get Peptides grouped by Taxonomies") {
		@Override
		public CypherQuery getQuery() {
			return CypherQueryFactory.getTaxaByPeptides();
		}
	},
	BIOLOGICAL_PROCESSES_BY_PROTEINS("Get Biological Process Ontologies grouped by Proteins") {
		@Override
		public CypherQuery getQuery() {
			return CypherQueryFactory.getOntologiesByProteins(RelationType.INVOLVED_IN_BIOPROCESS);
		}
	},	
	MOLECULAR_FUNCTIONS_BY_PROTEINS("Get Molecular Function Ontologies grouped by Proteins") {
		@Override
		public CypherQuery getQuery() {
			return CypherQueryFactory.getOntologiesByProteins(RelationType.HAS_MOLECULAR_FUNCTION);
		}
	},	
	CELLULAR_COMPONENTS_BY_PROTEINS("Get Cellular Component Ontologies grouped by Proteins") {
		@Override
		public CypherQuery getQuery() {
			return CypherQueryFactory.getOntologiesByProteins(RelationType.BELONGS_TO_CELL_COMP);
		}
	},	
	PATHWAYS_BY_PROTEINS("Get Pathways grouped by Proteins") {
		@Override
		public CypherQuery getQuery() {
			return CypherQueryFactory.getPathwaysByProteins();
		}
	},
	PROTEINS_BY_ENZYMES("Get Proteins grouped by Enzymes") {
		@Override
		public CypherQuery getQuery() {
			return CypherQueryFactory.getProteinsByEnzymes();
		}
	},
	PROTEINS_ONLY_KERATIN("Get Proteins, only Keratin") {
		@Override
		public CypherQuery getQuery() {
			return CypherQueryFactory.getProteinsByDescriptionPattern("keratin");
		}
	},
	PROTEINS_EXCEPT_KERATIN("Get Proteins, exclude Keratin") {
		@Override
		public CypherQuery getQuery() {
			return CypherQueryFactory.getProteinsByDescriptionPattern("keratin", true);
		}
	},
	CUSTOM("Customized Query") {
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
