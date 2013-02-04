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
import java.util.Set;
import java.util.zip.GZIPInputStream;

import junit.framework.TestCase;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;

import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.graphdb.insert.DataInserter;
import de.mpa.graphdb.properties.PeptideProperty;
import de.mpa.graphdb.properties.ProteinProperty;
import de.mpa.graphdb.properties.PsmProperty;
import de.mpa.graphdb.setup.GraphDatabase;

/**
 * Test class for starting up the graph database and accessing it 
 * by using Neo4j Cypher language: http://www.neo4j.org/learn/cypher
 * @author Thilo Muth
 *
 */

public class CypherQueryTest {
	private CypherQuery cypherQuery;
	private static GraphDatabase graphDb;
	private static DataInserter dataInserter;
	
	@BeforeClass
	public static void setUpClass() {
		// Proteins file.
		File file = new File("test/de/mpa/resources/DumpTest.mpa");
		// Object input stream.
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new BufferedInputStream(
					new GZIPInputStream(new FileInputStream(file))));
			DbSearchResult dbSearchResult = (DbSearchResult) ois.readObject();
			// Create a graph database.
			graphDb = new GraphDatabase("target/graphdb", true);
			GraphDatabaseService service = graphDb.getService();

			// Insert the data.
			dataInserter = new DataInserter(service);
			dataInserter.setData(dbSearchResult);
			dataInserter.insert();

			
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
		cypherQuery = dataInserter.getCypherQuery();
		TestCase.assertNotNull(cypherQuery);
	}
	
	@Test
    public void testFirstNode() {
		// Getting the first inserted protein node.
        Iterator<Object> columnAs = cypherQuery.getFirstNode().columnAs("n");
        while (columnAs.hasNext()) {
            final Object value = columnAs.next();
            if (value instanceof Node) {
                Node n = (Node)value;
                TestCase.assertEquals("Q8CPN2", n.getProperty(ProteinProperty.ACCESSION.name()));
                TestCase.assertEquals("ODPB_STAES Pyruvate dehydrogenase E1 component subunit beta", n.getProperty(ProteinProperty.DESCRIPTION.name()));
            }
        }
    }
	
	@Test
    public void testGetProteinByAccession() {
        Iterator<Object> columnAs = cypherQuery.getProteinByAccession("Q8CPN2").columnAs("protein");
        while (columnAs.hasNext()) {
            final Object value = columnAs.next();
            if (value instanceof Node) {
                Node n = (Node)value;
                TestCase.assertEquals("Q8CPN2", n.getProperty(ProteinProperty.ACCESSION.name()));
                TestCase.assertEquals("ODPB_STAES Pyruvate dehydrogenase E1 component subunit beta", n.getProperty(ProteinProperty.DESCRIPTION.name()));
            }
        }
    }
	
	@Test
    public void testPeptidesForProtein() {
		// Test1 : One peptide only!
        Iterator<Object> columnAs = cypherQuery.getPeptidesForProtein("P64462").columnAs("peptide");
        while (columnAs.hasNext()) {
            final Object value = columnAs.next();
            if (value instanceof Node) {
                Node n = (Node)value;
                if(n.hasProperty(PeptideProperty.SEQUENCE.name())){
                	TestCase.assertEquals("LESLMTGPRK", n.getProperty(PeptideProperty.SEQUENCE.name()));
                }
            }
        }
    }
	
	@Test
    public void testPSMsForProtein() {
		// Multiple PSMs for one protein and peptide
        Iterator<Object> columnAs = cypherQuery.getPSMsForProtein("P69908").columnAs("psm");
        List<Node> nodeList = new ArrayList<Node>();
        while (columnAs.hasNext()) {
            final Object value = columnAs.next();
            if (value instanceof Node) {
                Node n = (Node)value;
                if(n.hasProperty(PsmProperty.SPECTRUMID.name())){
                	nodeList.add(n);
                }
            }
        }
        TestCase.assertEquals(1081652L, nodeList.get(0).getProperty(PsmProperty.SPECTRUMID.name()));
        TestCase.assertEquals(4, nodeList.size());
    }
	
	@Test
    public void testPSMsForEnzyme() {
		// Multiple PSMs for one protein and peptide
		Set<Node> nodeSet = CypherQuery.retrieveNodeSet(cypherQuery.getPSMsForEnzymeNumber("4.1.1.15"), "psm", PsmProperty.SPECTRUMID);
        
		TestCase.assertEquals(1081661L, nodeSet.iterator().next().getProperty(PsmProperty.SPECTRUMID.name()));
		TestCase.assertEquals(4, nodeSet.size());
    }
	
	@Test
    public void testPSMsForPathway() {
		// Multiple PSMs for one protein and peptide
		Set<Node> nodeSet = CypherQuery.retrieveNodeSet(cypherQuery.getPSMsForPathway("K01580"), "psm", PsmProperty.SPECTRUMID);
        
		TestCase.assertEquals(1081661L, nodeSet.iterator().next().getProperty(PsmProperty.SPECTRUMID.name()));
		TestCase.assertEquals(4, nodeSet.size());
    }
	
    @AfterClass
    public static void tearDownClass() throws IOException {
    	graphDb.shutDown();
    }
}
