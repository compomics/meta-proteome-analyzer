/**
 * 
 */
package de.mpa.graphdb.insert;

import org.neo4j.graphdb.GraphDatabaseService;

import de.mpa.graphdb.access.DataAccessor;
import de.mpa.graphdb.cypher.CypherQuery;

/**
 * AbstractDataInserter provides the minimum of needed fields and methods for graph database inserting.
 * @author Thilo Muth
 * @date 2013-01-09
 * @version 0.6.1
 *
 */
public abstract class AbstractGraphDatabaseHandler {
	

	// FIXME Remove class!


	/**
	 * DataAccessor object.
	 */
	protected DataAccessor dataAccessor;
	
	/**
	 * CypherQuery object.
	 */
	protected CypherQuery cypherQuery;
	
	
	public AbstractGraphDatabaseHandler(GraphDatabaseService graphDb) {

		dataAccessor = new DataAccessor(graphDb);
		cypherQuery = new CypherQuery(graphDb);
	}

}
