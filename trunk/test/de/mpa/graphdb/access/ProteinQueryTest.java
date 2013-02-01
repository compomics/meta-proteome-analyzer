package de.mpa.graphdb.access;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;

import junit.framework.TestCase;

import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.graphdb.insert.DataInserter;
import de.mpa.graphdb.properties.PeptideProperty;
import de.mpa.graphdb.properties.ProteinProperty;
import de.mpa.graphdb.setup.GraphDatabase;

/**
 * Another test class for starting up the graph database and accessing it 
 * by using Neo4j Cypher language: http://www.neo4j.org/learn/cypher
 * Here, the different protein queries are tested.
 * @author Thilo Muth
 *
 */

public class ProteinQueryTest extends TestCase {
	private DbSearchResult dbSearchResult;
	private GraphDatabase graphDb;
	private GraphDatabaseService service;
	private CypherQuery cypherQuery;
	
	@BeforeClass
	public void setUp() {
		// Proteins file.
		File file = new File(getClass().getClassLoader().getResource("BGA04F14.mpa").getPath());
		
		// Object input stream.
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(file))));
			dbSearchResult = (DbSearchResult) ois.readObject();
			// Create a graph database.
			graphDb = new GraphDatabase("target/graphdb", true);
			assertNotNull(graphDb);
			service =  graphDb.getService();
			assertNotNull(service);
			
			// Insert the data.
			DataInserter dataInserter = new DataInserter(service);
			dataInserter.setData(dbSearchResult);
			dataInserter.insert();
			
			cypherQuery = dataInserter.getCypherQuery();
			assertNotNull(cypherQuery);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Test
    public void testProteinsForPeptide() {
		// Unique peptide test: One peptide occuring in one protein.
        Iterator<Object> columnAs = cypherQuery.getProteinsForPeptide("ELLETEFFDPAR").columnAs("protein");
        while (columnAs.hasNext()) {
            final Object value = columnAs.next();
            if (value instanceof Node) {
                Node n = (Node)value;
                assertEquals("O27233", n.getProperty(ProteinProperty.ACCESSION.name()));
            }
        }
        
        // Shared peptide test: One peptide occuring in two proteins.
        Iterator<Object> columnAs2 = cypherQuery.getProteinsForPeptide("NGATGTIGTVVQSLVER").columnAs("protein");
        List<Node> nodes = new ArrayList<Node>();
        while (columnAs2.hasNext()) {
            final Object value = columnAs2.next();
            if (value instanceof Node) {
                Node n = (Node)value;
                nodes.add(n);
            }
        }
        assertEquals("G9BY94", nodes.get(0).getProperty(ProteinProperty.ACCESSION.name()));
        assertEquals("A3CT50", nodes.get(1).getProperty(ProteinProperty.ACCESSION.name()));
    }

}
