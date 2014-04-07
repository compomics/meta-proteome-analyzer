package de.mpa.graphdb.cypher;

import java.util.List;

/**
 * Query class using the Cypher query language.
 * @author Thilo Muth
 *
 */
public class CypherQuery {
	
	/**
	 * The list of starting nodes of the query.
	 */
	private List<CypherStartNode> startNodes;

	/**
	 * The list of match statements of the query.
	 */
	private List<CypherMatch> matches;
	
	/**
	 * The list of conditionals of the query.
	 */
	private List<CypherCondition> conditions;
	
	/**
	 * The list of return values of the query as indices of the matches used.
	 */
	private List<Integer> returnIndices;
	
	/**
	 * Query statement.
	 */
	private String statement;
	
	/**
	 * Query state of being custom-built or not.
	 */
	private boolean custom;
	
	/**
	 * Title of the CypherQuery.
	 */
	private String title;
	
	/**
	 * CypherQuery constructor for a query made from direct statement, i.e. query is custom-built.
	 * @param statement
	 */
	public CypherQuery(String statement) {
		this.statement = statement;
		this.custom = true;
	}
	
	/**
	 * CypherQuery constructor for query made from defined start nodes, matches, conditions and return indices. 
	 * @param startNodes Cypher start nodes
	 * @param matches Cypher matches
	 * @param conditions Cypher matches
	 * @param returnIndices Cypher return indices
	 */
	public CypherQuery(List<CypherStartNode> startNodes, List<CypherMatch> matches, List<CypherCondition> conditions, List<Integer> returnIndices) {
		this.startNodes = startNodes;
		this.matches = matches;
		this.conditions = conditions;
		this.returnIndices = returnIndices;
	}

	/**
	 * Returns whether the query is valid in its current state.
	 * @return <code>true</code> if the query is valid, <code>false</code> otherwise
	 */
	public boolean isValid() {
		// Assume that the provided statement is valid. 
		if(statement != null) return true;		
		return (startNodes != null) && (returnIndices != null);
	}
	
	@Override
	public String toString() {
		
		// Returns direct statement
		if(statement != null) return statement;
		
		// Begin with START line
		String statement = "START ";
		boolean first = true;
		for (CypherStartNode startNode : startNodes) {
			if (!first) {
				statement += ", ";
			}
			statement += startNode;
			first = false;
		}
		// Add matches, if any were defined
		if ((matches != null) && (matches.size() > 1)) {
			statement += "\nMATCH ";
			for (CypherMatch match : matches) {
				statement += match;
			}
		}
		if (conditions != null) {
			statement += "\nWHERE ";
			for (CypherCondition cond : conditions) {
				statement += cond;
			}
		}
		statement += "\nRETURN ";
		first = true;
		for (Integer returnIndex : returnIndices) {
			if (!first) {
				statement += ", ";
			}
			statement += matches.get(returnIndex).getNodeIdentifier();
			first = false;
		}
		
		return statement;
	}

	/**
	 * Returns the list of start nodes.
	 * @return the start nodes
	 */
	public List<CypherStartNode> getStartNodes() {
		return startNodes;
	}
	
	/**
	 * Sets the list of start nodes.
	 * @param startNodes the list of start nodes to set
	 */
	public void setStartNodes(List<CypherStartNode> startNodes) {
		this.startNodes = startNodes;
	}

	/**
	 * Returns the list of matches.
	 * @return the matches
	 */
	public List<CypherMatch> getMatches() {
		return matches;
	}

	/**
	 * Sets the list of matches.
	 * @param the matches to set
	 */
	public void setMatches(List<CypherMatch> matches) {
		this.matches = matches;
	}
	
	/**
	 * Returns the list of return indices.
	 * @return the return indices
	 */
	public List<Integer> getReturnIndices() {
		return returnIndices;
	}

	/**
	 * Sets the list of return indices.
	 * @param returnIndices the return indices to set
	 */
	public void setReturnIndices(List<Integer> returnIndices) {
		this.returnIndices = returnIndices;
	}
	
	/**
	 * Returns the state of the CypherQuery (being custom-built or not).
	 * @return true if the CypherQuery is custom-built else false
	 */
	public boolean isCustom() {
		return custom;
	}
	
	/**
	 * Returns the title of the query.
	 * @return
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Sets the title of the query.
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
//	/**
//	 * Returns the unique peptides for a protein (specified by its accession).
//	 * @return Peptide node(s) ExecutionResult
//	 */
//	public ExecutionResult getUniquePeptidesForProtein(String accession) {
//		return engine.execute("START protein=node:" + NodeType.PROTEINS + "(" + ProteinProperty.IDENTIFIER + " = {accession}) " +
//				"MATCH (protein)-[:HAS_PEPTIDE]->(peptide) " +
//				"WITH peptide " +
//				"MATCH (peptide)<-[rel:HAS_PEPTIDE]-(protein2) " +
//				"WITH peptide, count(rel) as cn " + 
//				"WHERE cn = 1 " + 
//				"RETURN peptide", map("accession", accession));
//	}
//	
//	/**
//	 * Returns the shared peptides for a protein (specified by its accession).
//	 * @return Peptide node(s) ExecutionResult
//	 */
//	public ExecutionResult getSharedPeptidesForProtein(String accession) {
//		return engine.execute("START protein=node:proteins(IDENTIFIER = {accession}) " +
//				"MATCH (protein)-[:HAS_PEPTIDE]->(peptide) " +
//				"WITH peptide " +
//				"MATCH (peptide)<-[rel:HAS_PEPTIDE]-(protein2) " +
//				"WITH peptide, count(rel) as cn " + 
//				"WHERE cn > 1 " + 
//				"RETURN peptide", map("accession", accession));
//	}	
    
}
