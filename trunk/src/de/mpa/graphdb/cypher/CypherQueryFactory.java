package de.mpa.graphdb.cypher;

import java.util.ArrayList;
import java.util.List;

import de.mpa.graphdb.edges.DirectionType;
import de.mpa.graphdb.edges.RelationType;
import de.mpa.graphdb.nodes.NodeType;
import de.mpa.graphdb.properties.ProteinProperty;

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
	 * TODO: API
	 * @return
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
	 * 
	 * @return
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
	 * 
	 * @return
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
	 * 
	 * @return
	 */
	public static CypherQuery getProteinsByUniquePeptides() {
		List<CypherStartNode> startNodes = new ArrayList<CypherStartNode>();
		startNodes.add(new CypherStartNode("proteins", NodeType.PROTEINS, ProteinProperty.IDENTIFIER, "*"));
		
		List<CypherMatch> matches = new ArrayList<CypherMatch>();
		matches.add(new CypherMatch("proteins", RelationType.HAS_PEPTIDE, "rel", DirectionType.OUT));
		matches.add(new CypherMatch("peptides", null, null, null));
		
		return null;
		
	}
}
