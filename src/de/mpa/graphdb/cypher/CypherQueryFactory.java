package de.mpa.graphdb.cypher;

import java.util.ArrayList;
import java.util.List;

import de.mpa.graphdb.edges.DirectionType;
import de.mpa.graphdb.edges.RelationType;
import de.mpa.graphdb.nodes.NodeType;
import de.mpa.graphdb.properties.OntologyProperty;
import de.mpa.graphdb.properties.ProteinProperty;
import de.mpa.graphdb.properties.TaxonProperty;

/**
 * Factory class providing methods to generate various Cypher query objects.
 * @see CypherQuery
 * 
 * @author A. Behne, T. Muth
 */
public class CypherQueryFactory {
	
	/**
	 * Private constructor for the factory class.
	 */
	private CypherQueryFactory() {}
	
	/**
	 * Returns the proteins grouped by enzymes.
	 * @return Cypher query for proteins grouped by enzymes
	 */
	public static CypherQuery getProteinsByEnzymes() {
		List<CypherStartNode> startNodes = new ArrayList<CypherStartNode>();
		startNodes.add(new CypherStartNode("proteins", NodeType.PROTEINS, ProteinProperty.IDENTIFIER, "*"));
		
		List<CypherMatch> matches = new ArrayList<CypherMatch>();
		matches.add(new CypherMatch("proteins", RelationType.BELONGS_TO_ENZYME, null, DirectionType.OUT));
		for (int i = 4; i > 1; i--) {
			matches.add(new CypherMatch("E" + i, RelationType.IS_SUPERGROUP_OF, null, DirectionType.IN));
		}
		matches.add(new CypherMatch("E1", null, null, null));
		
		List<CypherCondition> conditions = null;
		
		List<Integer> returnIndices = new ArrayList<Integer>();
		for (int i = 4; i >= 0; i--) {
			returnIndices.add(i);
		}
		
		return new CypherQuery(startNodes, matches, conditions, returnIndices);
	}
	
	/**
	 * Returns peptides grouped by proteins.
	 * @return CypherQuery for peptides grouped by proteins
	 */
	public static CypherQuery getPeptidesByProteins() {
		
		List<CypherStartNode> startNodes = new ArrayList<CypherStartNode>();
		startNodes.add(new CypherStartNode("peptides", NodeType.PEPTIDES, ProteinProperty.IDENTIFIER, "*"));
		
		List<CypherMatch> matches = new ArrayList<CypherMatch>();
		matches.add(new CypherMatch("peptides", RelationType.HAS_PEPTIDE, null, DirectionType.IN));
		matches.add(new CypherMatch("proteins", null, null, null));
		
		List<CypherCondition> conditions = null;
		
		List<Integer> returnIndices = new ArrayList<Integer>();
		returnIndices.add(1);
		returnIndices.add(0);
		
		return new CypherQuery(startNodes, matches, conditions, returnIndices);
	}
	
	/**
	 * Returns proteins grouped by peptides.
	 * @return CypherQuery for proteins grouped by peptides
	 */
	public static CypherQuery getProteinsByPeptides() {
		
		List<CypherStartNode> startNodes = new ArrayList<CypherStartNode>();
		startNodes.add(new CypherStartNode("proteins", NodeType.PROTEINS, ProteinProperty.IDENTIFIER, "*"));
		
		List<CypherMatch> matches = new ArrayList<CypherMatch>();
		matches.add(new CypherMatch("proteins", RelationType.HAS_PEPTIDE, null, DirectionType.OUT));
		matches.add(new CypherMatch("peptides", null, null, null));
		
		List<CypherCondition> conditions = null;
		
		List<Integer> returnIndices = new ArrayList<Integer>();
		returnIndices.add(1);
		returnIndices.add(0);
		
		return new CypherQuery(startNodes, matches, conditions, returnIndices);
	}
	
	/**
	 * Returns proteins grouped by meta-proteins
	 * @return CypherQuery for proteins grouped by meta-proteins
	 */
	public static CypherQuery getProteinsByMetaproteins() {
		
		List<CypherStartNode> startNodes = new ArrayList<CypherStartNode>();
		startNodes.add(new CypherStartNode("metaproteins", NodeType.PROTEINS, ProteinProperty.IDENTIFIER, "*"));
		
		List<CypherMatch> matches = new ArrayList<CypherMatch>();
		matches.add(new CypherMatch("metaproteins", RelationType.IS_METAPROTEIN_OF, null, DirectionType.OUT));
		matches.add(new CypherMatch("proteins", RelationType.HAS_PEPTIDE, null, DirectionType.OUT));
		matches.add(new CypherMatch("peptides", null, null, null));
		
		List<CypherCondition> conditions = null;
		
		List<Integer> returnIndices = new ArrayList<Integer>();
		returnIndices.add(0);
		returnIndices.add(1);
		returnIndices.add(2);
		
		return new CypherQuery(startNodes, matches, conditions, returnIndices);
	}

	/**
	 * Returns the proteins by a certain description pattern.
	 * @param pattern
	 * @return
	 */
	public static CypherQuery getProteinsByDescriptionPattern(String pattern) {
		return CypherQueryFactory.getProteinsByDescriptionPattern(pattern, false);
	}
	
	/**
	 * 
	 * @param pattern
	 * @param not
	 * @return
	 */
	public static CypherQuery getProteinsByDescriptionPattern(String pattern, boolean not) {
		
		List<CypherStartNode> startNodes = new ArrayList<CypherStartNode>();
		startNodes.add(new CypherStartNode("proteins", NodeType.PROTEINS, ProteinProperty.IDENTIFIER, "*"));
		
		List<CypherMatch> matches = new ArrayList<CypherMatch>();
		matches.add(new CypherMatch("proteins", null, null, null));
		
		List<CypherCondition> conditions = new ArrayList<CypherCondition>();
		if (not) {
			pattern = "\'(?i)^((?!" + pattern + ").)*$\'";
		} else {
			pattern = "\'(?i).*" + pattern + ".*\'";
		}
		conditions.add(new CypherCondition("proteins." + ProteinProperty.DESCRIPTION, pattern, CypherOperatorType.REGEXP));
//		List<CypherCondition> conditions = null;
		
		List<Integer> returnIndices = new ArrayList<Integer>();
		returnIndices.add(0);
		
		return new CypherQuery(startNodes, matches, conditions, returnIndices);
	}
	
	/**
	 * Returns taxa grouped by proteins.
	 * @return CypherQuery for taxa grouped by proteins.
	 */
	public static CypherQuery getProteinsByTaxonomies() {
		List<CypherStartNode> startNodes = new ArrayList<CypherStartNode>();
		startNodes.add(new CypherStartNode("taxa", NodeType.TAXA, TaxonProperty.IDENTIFIER, "*"));
		
		List<CypherMatch> matches = new ArrayList<CypherMatch>();
		matches.add(new CypherMatch("taxa", RelationType.BELONGS_TO_TAXONOMY, "rel", DirectionType.IN));
		matches.add(new CypherMatch("proteins", null, null, null));
		
		List<CypherCondition> conditions = null;
		
		List<Integer> returnIndices = new ArrayList<Integer>();
		returnIndices.add(0);
		returnIndices.add(1);
		
		return new CypherQuery(startNodes, matches, conditions, returnIndices);
	}
	
	/**
	 * Returns taxa grouped by peptides.
	 * @return CypherQuery for taxa grouped by peptides.
	 */
	public static CypherQuery getPeptidesByTaxonomies() {
		List<CypherStartNode> startNodes = new ArrayList<CypherStartNode>();
		startNodes.add(new CypherStartNode("taxa", NodeType.TAXA, TaxonProperty.IDENTIFIER, "*"));
		
		List<CypherMatch> matches = new ArrayList<CypherMatch>();
		matches.add(new CypherMatch("taxa", RelationType.BELONGS_TO_TAXONOMY, "rel", DirectionType.IN));
		matches.add(new CypherMatch("proteins", RelationType.HAS_PEPTIDE, null, DirectionType.BOTH));
		matches.add(new CypherMatch("peptides", null, null, null));
		
		List<CypherCondition> conditions = null;
		
		List<Integer> returnIndices = new ArrayList<Integer>();
		returnIndices.add(0);
		returnIndices.add(2);
		
		return new CypherQuery(startNodes, matches, conditions, returnIndices);
	}
	
	/**
	 * Returns pathways grouped by proteins.
	 * @return CypherQuery for pathways grouped by proteins.
	 */
	public static CypherQuery getProteinsByPathways() {
		List<CypherStartNode> startNodes = new ArrayList<CypherStartNode>();
		startNodes.add(new CypherStartNode("pathways", NodeType.PATHWAYS, TaxonProperty.IDENTIFIER, "*"));
		
		List<CypherMatch> matches = new ArrayList<CypherMatch>();
		matches.add(new CypherMatch("pathways", RelationType.BELONGS_TO_PATHWAY, "rel", DirectionType.IN));
		matches.add(new CypherMatch("proteins", null, null, null));
		
		List<CypherCondition> conditions = null;
		
		List<Integer> returnIndices = new ArrayList<Integer>();
		returnIndices.add(0);
		returnIndices.add(1);
		
		return new CypherQuery(startNodes, matches, conditions, returnIndices);
	}
	
	/**
	 * Returns ontologies grouped by proteins.
	 * @param type RelationType for specific ontology
	 * @return CypherQuery for molecular function ontologies grouped by proteins.
	 */
	public static CypherQuery getProteinsByOntologies(RelationType type) {
		List<CypherStartNode> startNodes = new ArrayList<CypherStartNode>();
		startNodes.add(new CypherStartNode("ontologies", NodeType.ONTOLOGIES, OntologyProperty.IDENTIFIER, "*"));
		
		List<CypherMatch> matches = new ArrayList<CypherMatch>();
		matches.add(new CypherMatch("ontologies", type, "rel", DirectionType.IN));
		matches.add(new CypherMatch("proteins", null, null, null));
		
		List<CypherCondition> conditions = null;		
		List<Integer> returnIndices = new ArrayList<Integer>();
		returnIndices.add(0);
		returnIndices.add(1);		
		return new CypherQuery(startNodes, matches, conditions, returnIndices);
	}
	
	/**
	 * Returns the meta-proteins with counts by experiment.
	 * @return CypherQuery instance.
	 */
	public static CypherQuery getMetaProteinsWithCountsByExperiments(String countIdentifier) {
		return new CypherQuery("START metaproteins = node:Proteins(\"Identifier:*\")" + 
							   "MATCH (experiments)<-[:BELONGS_TO_EXPERIMENT]-(metaproteins)-[:IS_METAPROTEIN_OF]->(proteins)-[:HAS_PEPTIDE]->(peptides)<-[:IS_MATCH_IN]-(psms)-[:BELONGS_TO_EXPERIMENT]->(experiments)" +
							   "RETURN experiments, count(distinct " + countIdentifier + "), metaproteins");
	}
	
	/**
	 * Returns the proteins with counts by experiment.
	 * @return CypherQuery instance.
	 */
	public static CypherQuery getProteinsWithCountsByExperiments(String countIdentifier) {
		return new CypherQuery("START proteins = node:Proteins(\"Identifier:*\")" + 
							   "MATCH (experiments)<-[:BELONGS_TO_EXPERIMENT]-(proteins)-[:HAS_PEPTIDE]->(peptides)<-[:IS_MATCH_IN]-(psms)-[:BELONGS_TO_EXPERIMENT]->(experiments)" +
							   "RETURN experiments, count(distinct " + countIdentifier + "), proteins");
	}
	
	/**
	 * Returns the peptides with counts by experiment.
	 * @return CypherQuery instance.
	 */
	public static CypherQuery getPeptidesWithCountsByExperiments(String countIdentifier) {
		return new CypherQuery("START proteins = node:Proteins(\"Identifier:*\")" + 
							   "MATCH (experiments)<-[:BELONGS_TO_EXPERIMENT]-(proteins)-[:HAS_PEPTIDE]->(peptides)<-[:IS_MATCH_IN]-(psms)-[:BELONGS_TO_EXPERIMENT]->(experiments)" +
							   "RETURN experiments, count(distinct " + countIdentifier + "), peptides");
	}
	
	/**
	 * Returns the biological processes with counts by experiment.
	 * @return CypherQuery instance.
	 */
	public static CypherQuery getBiologicalProcessesWithCountsByExperiments(String countIdentifier) {
		return new CypherQuery("START ontologies = node:Ontologies(\"Identifier:*\")" + 
							   "MATCH (ontologies)<-[:INVOLVED_IN_BIOPROCESS]-(proteins)-[:HAS_PEPTIDE]->(peptides)<-[:IS_MATCH_IN]-(psms)-[:BELONGS_TO_EXPERIMENT]->(experiments), (experiments)<-[:BELONGS_TO_EXPERIMENT]-(proteins)" +							  
							   "RETURN experiments, count(distinct " + countIdentifier + "), ontologies");
	}
	
	/**
	 * Returns the molecular functions with counts by experiment.
	 * @return CypherQuery instance.
	 */
	public static CypherQuery getMolecularFunctionsWithCountsByExperiments(String countIdentifier) {
		return new CypherQuery("START ontologies = node:Ontologies(\"Identifier:*\")" + 
							   "MATCH (ontologies)<-[:HAS_MOLECULAR_FUNCTION]-(proteins)-[:HAS_PEPTIDE]->(peptides)<-[:IS_MATCH_IN]-(psms)-[:BELONGS_TO_EXPERIMENT]->(experiments), (experiments)<-[:BELONGS_TO_EXPERIMENT]-(proteins)" +							  
							   "RETURN experiments, count(distinct " + countIdentifier + "), ontologies");
	}
	
	/**
	 * Returns the cellular components with counts by experiment.
	 * @return CypherQuery instance.
	 */
	public static CypherQuery getCellularComponentsWithCountsByExperiments(String countIdentifier) {
		return new CypherQuery("START ontologies = node:Ontologies(\"Identifier:*\")" + 
							   "MATCH (ontologies)<-[:BELONGS_TO_CELL_COMP]-(proteins)-[:HAS_PEPTIDE]->(peptides)<-[:IS_MATCH_IN]-(psms)-[:BELONGS_TO_EXPERIMENT]->(experiments), (experiments)<-[:BELONGS_TO_EXPERIMENT]-(proteins)" +							  
							   "RETURN experiments, count(distinct " + countIdentifier + "), ontologies");
	}	
	
	/**
	 * Returns the taxonomy with counts by experiment.
	 * @param rank Taxonomic rank.
	 * @param depth Depth of the taxonomy (e.g. lowest level == 1 --> species).
	 * @return CypherQuery instance.
	 */
	public static CypherQuery getTaxonomyWithCountsByExperiments(String countIdentifier, String rank) {
		return new CypherQuery("START parent = node:Taxa(\"Identifier:*\")" + 
							   "MATCH (parent)-[:IS_ANCESTOR_OF*]->(taxa)<-[:BELONGS_TO_TAXONOMY]-(proteins)-[:HAS_PEPTIDE]->(peptides)<-[:IS_MATCH_IN]-(psms)-[:BELONGS_TO_EXPERIMENT]->(experiments), (experiments)<-[:BELONGS_TO_EXPERIMENT]-(proteins)" +
							   "WHERE (parent.Rank = '" + rank + "')" + 
							   "RETURN experiments, count(distinct " + countIdentifier + "), parent");
	}
	
	/**
	 * Returns the subspecies taxonomy with counts by experiment.
	 * @return CypherQuery instance.
	 */
	public static CypherQuery getSubspeciesWithCountsByExperiments(String countIdentifier) {
		return new CypherQuery("START taxa = node:Taxa(\"Identifier:*\")" + 
							   "MATCH (taxa)<-[:BELONGS_TO_TAXONOMY]-(proteins)-[:HAS_PEPTIDE]->(peptides)<-[:IS_MATCH_IN]-(psms)-[:BELONGS_TO_EXPERIMENT]->(experiments), (experiments)<-[:BELONGS_TO_EXPERIMENT]-(proteins)" +
							   "WHERE (taxa.Rank = 'Subspecies')" + 
							   "RETURN experiments, count(distinct " + countIdentifier + "), taxa");
	}


}
