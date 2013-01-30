/**
 * 
 */
package de.mpa.graphdb.insert;

import org.neo4j.graphdb.GraphDatabaseService;

import com.tinkerpop.blueprints.IndexableGraph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.impls.neo4j.Neo4jGraph;

import de.mpa.graphdb.access.CypherQuery;
import de.mpa.graphdb.access.DataAccessor;

/**
 * AbstractDataInserter provides the minimum of needed fields and methods for graph database insertions.
 * @author Thilo Muth
 * @date 2013-01-09
 * @version 0.6.1
 *
 */
public abstract class AbstractDataInserter {
	
	/**
	 * Graph database service.
	 */
	protected GraphDatabaseService graphDb;
	
	/**
	 *  Data to be inserted in the graph.
	 */
	protected Object data;
	
	/**
	 * By default, the first operation on a TransactionalGraph will start a transaction automatically.
	 */
	protected TransactionalGraph graph;
	
	/**
	 *  An IndexableGraph is a graph that supports the manual indexing of its elements.
	 */
	protected IndexableGraph indexGraph;

	/**
	 * DataAccessor object.
	 */
	protected DataAccessor dataAccessor;
	
	/**
	 * CypherQuery object.
	 */
	protected CypherQuery cypherQuery;
	
	
	public AbstractDataInserter(GraphDatabaseService graphDb) {
		this.graphDb = graphDb;
		indexGraph = new Neo4jGraph(graphDb);
		graph = new Neo4jGraph(graphDb);
		dataAccessor = new DataAccessor(graphDb);
		cypherQuery = new CypherQuery(graphDb);
	}
	
	/**
	 * Returns the DataAccessor object.
	 * @return DataAccessor object
	 */
	public DataAccessor getDataAccessor() {
		return dataAccessor;
	}
	
	/**
	 * Returns the CypherQuery object.
	 * @return CypherQuery object
	 */
	public CypherQuery getCypherQuery() {
		return cypherQuery;
	}
}
