package de.mpa.graphdb.access;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;

import de.mpa.graphdb.edges.DirectionType;
import de.mpa.graphdb.edges.RelationType;
import de.mpa.graphdb.nodes.NodeType;
import de.mpa.graphdb.properties.ProteinProperty;

public class CypherQueryFactory {
	
	/**
	 * Execution engine derived from the GraphDatabaseService.
	 */
	private static ExecutionEngine engine;
	
	/**
	 * Private constructor for the factory class.
	 */
	private CypherQueryFactory() {}
	
	/**
	 * 
	 * @param graphDb the graph database service reference
	 * @return
	 */
	public static ExecutionResult getProteinsByEnzymes(GraphDatabaseService graphDb) {
		
		initExecutionEngine(graphDb);
		
		List<CypherStartNode> startNodes = new ArrayList<CypherStartNode>();
		startNodes.add(new CypherStartNode("protein", NodeType.PROTEINS, ProteinProperty.IDENTIFIER, "*"));
		
		List<CypherMatch> matches = new ArrayList<CypherMatch>();
		matches.add(new CypherMatch("protein", RelationType.BELONGS_TO_ENZYME, DirectionType.OUT));
		for (int i = 4; i > 1; i--) {
			matches.add(new CypherMatch("E" + i, RelationType.IS_SUPERGROUP_OF, DirectionType.IN));
		}
		matches.add(new CypherMatch("E1", null, null));
		
		List<CypherCondition> conditions = null;
		
		List<Integer> returnIndices = new ArrayList<Integer>();
		for (int i = 4; i >= 0; i--) {
			returnIndices.add(i);
		}
		
		CypherQuery cypherQuery = new CypherQuery(startNodes, matches, conditions, returnIndices);
		
		if (cypherQuery.isValid()) {
			return engine.execute(cypherQuery.toString());
		}
		
		// TODO: maybe throw error/warning/exception here
		return null;
	}
	
	/**
	 * 
	 * @param graphDb the graph database service reference
	 * @return
	 */
	public static ExecutionResult getPeptidesByProteins(GraphDatabaseService graphDb) {
		
		initExecutionEngine(graphDb);
		
		List<CypherStartNode> startNodes = new ArrayList<CypherStartNode>();
		startNodes.add(new CypherStartNode("protein", NodeType.PROTEINS, ProteinProperty.IDENTIFIER, "*"));
		
		List<CypherMatch> matches = new ArrayList<CypherMatch>();
		matches.add(new CypherMatch("protein", RelationType.HAS_PEPTIDE, DirectionType.OUT));
		matches.add(new CypherMatch("peptide", null, null));
		
		List<CypherCondition> conditions = null;
		
		List<Integer> returnIndices = new ArrayList<Integer>();
		returnIndices.add(0);
		returnIndices.add(1);
		
		CypherQuery cypherQuery = new CypherQuery(startNodes, matches, conditions, returnIndices);
		
		if (cypherQuery.isValid()) {
			return engine.execute(cypherQuery.toString());
		}
		
		// TODO: maybe throw error/warning/exception here
		return null;
	}
	
	/**
	 * 
	 * @param graphDb the graph database service reference
	 * @return
	 */
	public static ExecutionResult getProteinsByPeptides(GraphDatabaseService graphDb) {
		
		initExecutionEngine(graphDb);
		
		List<CypherStartNode> startNodes = new ArrayList<CypherStartNode>();
		startNodes.add(new CypherStartNode("protein", NodeType.PROTEINS, ProteinProperty.IDENTIFIER, "*"));
		
		List<CypherMatch> matches = new ArrayList<CypherMatch>();
		matches.add(new CypherMatch("protein", RelationType.HAS_PEPTIDE, DirectionType.OUT));
		matches.add(new CypherMatch("peptide", null, null));
		
		List<CypherCondition> conditions = null;
		
		List<Integer> returnIndices = new ArrayList<Integer>();
		returnIndices.add(1);
		returnIndices.add(0);
		
		CypherQuery cypherQuery = new CypherQuery(startNodes, matches, conditions, returnIndices);
		
		if (cypherQuery.isValid()) {
			return engine.execute(cypherQuery.toString());
		}
		
		// TODO: maybe throw error/warning/exception here
		return null;
	}

	private static void initExecutionEngine(GraphDatabaseService graphDb) {
		if (engine == null) {
			engine = new ExecutionEngine(graphDb);
		}
	}
}
