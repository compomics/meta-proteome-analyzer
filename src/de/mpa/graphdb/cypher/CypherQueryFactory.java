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
	 * 
	 * @param pattern
	 * @return
	 */
	public static CypherQuery getProteinsByDescriptionPattern(String pattern) {
		return getProteinsByDescriptionPattern(pattern, false);
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
		matches.add(new CypherMatch("taxa", RelationType.BELONGS_TO, "rel", DirectionType.IN));
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
		matches.add(new CypherMatch("taxa", RelationType.BELONGS_TO, "rel", DirectionType.IN));
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

}
