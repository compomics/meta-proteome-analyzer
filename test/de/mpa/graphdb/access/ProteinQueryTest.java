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

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.graphdb.insert.GraphDatabaseHandler;
import de.mpa.graphdb.properties.ProteinProperty;
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
			GraphDatabaseService service = graphDb.getService();

			// Insert the data.
			graphDbHandler = new GraphDatabaseHandler(service);
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
		cypherQuery = graphDbHandler.getCypherQuery();
		TestCase.assertNotNull(cypherQuery);
	}
	
	@Test
    public void testGetProteinsForPeptide() {
		// Unique peptide test: One peptide occuring in one protein.
        Iterator<Object> columnAs = cypherQuery.getProteinsForPeptide("ELLETEFFDPAR").columnAs("protein");
        while (columnAs.hasNext()) {
            final Object value = columnAs.next();
            if (value instanceof Node) {
                Node n = (Node)value;
                TestCase.assertEquals("O27233", n.getProperty(ProteinProperty.IDENTIFIER.name()));
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
        TestCase.assertEquals("G9BY94", nodes.get(0).getProperty(ProteinProperty.IDENTIFIER.name()));
        TestCase.assertEquals("A3CT50", nodes.get(1).getProperty(ProteinProperty.IDENTIFIER.name()));
    }
	
	@Test
    public void testGetProteinsForSpecies() {
		// Protein
        Iterator<Object> columnAs = cypherQuery.getProteinsForSpecies("Methanoculleus marisnigri JR1 (no rank)").columnAs("protein");
        List<Node> nodes = new ArrayList<Node>();
        while (columnAs.hasNext()) {
            final Object value = columnAs.next();
            if (value instanceof Node) {
                Node n = (Node)value;
                nodes.add(n);
            }
        }
        TestCase.assertEquals(7, nodes.size());
    }
	
	@Test
    public void testGetProteinsForEnzyme() {
		// Protein
        Iterator<Object> columnAs = cypherQuery.getProteinsForEnzyme("2.1.1.249").columnAs("protein");
        List<Node> nodes = new ArrayList<Node>();
        while (columnAs.hasNext()) {
            final Object value = columnAs.next();
            if (value instanceof Node) {
                Node n = (Node)value;
                nodes.add(n);
            }
        }
        TestCase.assertEquals(3, nodes.size());
        TestCase.assertEquals("Q8TN68", nodes.get(0).getProperty(ProteinProperty.IDENTIFIER.name()));
    }
	
	@Test
    public void testGetProteinsForPathway() {
		// Pathway: Photosynthesis
        Iterator<Object> columnAs = cypherQuery.getProteinsForPathway("K02117").columnAs("protein");
        List<Node> nodes = new ArrayList<Node>();
        while (columnAs.hasNext()) {
            final Object value = columnAs.next();
            if (value instanceof Node) {
                Node n = (Node)value;
                nodes.add(n);
            }
        }
        TestCase.assertEquals(5, nodes.size());
        TestCase.assertEquals("Q60186", nodes.get(0).getProperty(ProteinProperty.IDENTIFIER.name()));
    }
	
	@Test
    public void testGetProteinsForMolecularFunction() {
		// Protein
        Iterator<Object> columnAs = cypherQuery.getProteinsForMolecularFunction("Transferase").columnAs("protein");
        List<Node> nodes = new ArrayList<Node>();
        while (columnAs.hasNext()) {
            final Object value = columnAs.next();
            if (value instanceof Node) {
                Node n = (Node)value;
                nodes.add(n);
            }
        }
        TestCase.assertEquals(50, nodes.size());
    }
	
	@Test
    public void testGetProteinsForBiologicalProcess() {
		// Protein
        Iterator<Object> columnAs = cypherQuery.getProteinsForBiologicalProcess("Methanogenesis").columnAs("protein");
        List<Node> nodes = new ArrayList<Node>();
        while (columnAs.hasNext()) {
            final Object value = columnAs.next();
            if (value instanceof Node) {
                Node n = (Node)value;
                nodes.add(n);
            }
        }
        TestCase.assertEquals(36, nodes.size());
        TestCase.assertEquals("Q9C4Z4", nodes.get(0).getProperty(ProteinProperty.IDENTIFIER.name()));
    }
	
	@Test
    public void testGetProteinsForCellularComponent() {
		// Protein
        Iterator<Object> columnAs = cypherQuery.getProteinsForCellularComponent("Cytoplasm").columnAs("protein");
        List<Node> nodes = new ArrayList<Node>();
        while (columnAs.hasNext()) {
            final Object value = columnAs.next();
            if (value instanceof Node) {
                Node n = (Node)value;
                nodes.add(n);
            }
        }
        TestCase.assertEquals(11, nodes.size());
    }
	
    @AfterClass
    public static void tearDownClass() throws IOException {
    	graphDb.shutDown();
    }
	

}
