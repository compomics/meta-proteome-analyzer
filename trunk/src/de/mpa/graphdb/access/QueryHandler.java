package de.mpa.graphdb.access;

import java.util.Iterator;

import de.mpa.client.ui.dialogs.PredefinedQueries;
import de.mpa.graphdb.insert.GraphDatabaseHandler;

public class QueryHandler {
	
	
	public static Iterator<Object> executePredefinedQuery(GraphDatabaseHandler graphDatabaseHandler, String predefinedQuery, String param){
		// Get the CYPHER query.
		CypherQuery cypherQuery = graphDatabaseHandler.getCypherQuery();
		Iterator<Object> resultObjects = null;
		if(predefinedQuery.equals(PredefinedQueries.GETALLUNIQUEPEPTIDES)){
			resultObjects = cypherQuery.getAllUniquePeptides().columnAs("peptide");
		} else if(predefinedQuery.equals(PredefinedQueries.GETALLSHAREDPEPTIDES)) {
			resultObjects = cypherQuery.getAllSharedPeptides().columnAs("peptide");
		} else if (predefinedQuery.equals(PredefinedQueries.GETPEPTIDESFORPROTEIN)) {
			resultObjects = cypherQuery.getPeptidesForProtein(param).columnAs("peptide");
		} else if (predefinedQuery.equals(PredefinedQueries.GETPEPTIDESFORSPECIES)) {
			resultObjects = cypherQuery.getPeptidesForSpecies(param).columnAs("peptide");
		} else if (predefinedQuery.equals(PredefinedQueries.GETPROTEINSFORENZYME)) {
			resultObjects = cypherQuery.getProteinsForEnzyme(param).columnAs("protein");
		} else if (predefinedQuery.equals(PredefinedQueries.GETPROTEINSFORSPECIES)) {
			resultObjects = cypherQuery.getProteinsForSpecies(param).columnAs("protein");
		}
		return resultObjects;
	}
}
