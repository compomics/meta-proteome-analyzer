package de.mpa.graphdb.access;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.zip.GZIPInputStream;

import junit.framework.TestCase;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.graphdb.cypher.CypherQuery;
import de.mpa.graphdb.insert.GraphDatabaseHandler;
import de.mpa.graphdb.setup.GraphDatabase;

/**
 * Another test class for starting up the graph database and accessing it 
 * by using Neo4j Cypher language: http://www.neo4j.org/learn/cypher
 * Here, the different protein queries are tested.
 * @author Thilo Muth
 *
 */

public class ProteinQueryTest {
	private CypherQuery cypherQuery;
	private static GraphDatabase graphDb;
	private static GraphDatabaseHandler graphDbHandler;
	
	@BeforeClass
	public static void setUpClass() {
		// Proteins file.
		File file = new File("test/de/mpa/resources/BGA04F14.mpa");

		// Object input stream.
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(file))));
			DbSearchResult dbSearchResult = (DbSearchResult) ois.readObject();
			// Create a graph database.
			graphDb = new GraphDatabase("target/graphdb", true);

			// Insert the data.
			graphDbHandler = new GraphDatabaseHandler(graphDb);
			graphDbHandler.setData(dbSearchResult);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Before
	public void setUp() {
		cypherQuery = new CypherQuery(graphDbHandler.getGraphDatabaseService());
		TestCase.assertNotNull(cypherQuery);
	}	
	
    @AfterClass
    public static void tearDownClass() throws IOException {
    	graphDb.shutDown();
    }
	

}
