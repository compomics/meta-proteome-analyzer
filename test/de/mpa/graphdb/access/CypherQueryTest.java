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
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.Node;

import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.graphdb.cypher.CypherQuery;
import de.mpa.graphdb.insert.GraphDatabaseHandler;
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
	private static GraphDatabaseHandler graphDbHandler;
	
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
	
	@Test
    public void testGetFirstNode() {
		// Getting the first inserted protein node.
        Iterator<Object> columnAs = cypherQuery.getFirstNode().columnAs("n");
        while (columnAs.hasNext()) {
            final Object value = columnAs.next();
            if (value instanceof Node) {
                Node n = (Node)value;
                TestCase.assertEquals("Q8CPN2", n.getProperty(ProteinProperty.IDENTIFIER.name()));
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
                TestCase.assertEquals("Q8CPN2", n.getProperty(ProteinProperty.IDENTIFIER.name()));
                TestCase.assertEquals("ODPB_STAES Pyruvate dehydrogenase E1 component subunit beta", n.getProperty(ProteinProperty.DESCRIPTION.name()));
            }
        }
    }
	
	@Test
    public void testGetProteinBySequence() {
		String sequence = "MAQMTMVQAINDALKSELKRDEDVLVFGEDVGVNGGVFRVTEGLQKEFGEDRVFDTPLAESGIGGLALGLAVTGFRPVMEIQFLGFVYEVFDEVAGQIARTRFRSGGTKPAPVTIRTPFGGGVHTPELHADNLEGILAQSPGLKVVIPSGPYDAKGLLISSIQSNDPVVYLEHMKLYRSFREEVPEEEYKIDIGKANVKKEGNDITLISYGAMVQESLKAAEELEKDGYSVEVIDLRTVQPIDIDTLVASVEKTGRAVVVQEAQRQAGVGAQVAAELAERAILSLEAPIARVAASDTIYPFTQAENVWLPNKKDIIEQAKATLEF";
        Iterator<Object> columnAs = cypherQuery.getProteinBySequence(sequence).columnAs("protein");
        while (columnAs.hasNext()) {
            final Object value = columnAs.next();
            if (value instanceof Node) {
                Node n = (Node)value;
                TestCase.assertEquals("Q8CPN2", n.getProperty(ProteinProperty.IDENTIFIER.name()));
                TestCase.assertEquals("ODPB_STAES Pyruvate dehydrogenase E1 component subunit beta", n.getProperty(ProteinProperty.DESCRIPTION.name()));
                
            }
        }
    }
	
	@Test
    public void testGetPeptidesForProtein() {
		// Test1 : One peptide only!
        Iterator<Object> columnAs = cypherQuery.getPeptidesForProtein("P64462").columnAs("peptide");
        while (columnAs.hasNext()) {
            final Object value = columnAs.next();
            if (value instanceof Node) {
                Node n = (Node)value;
                if(n.hasProperty(PeptideProperty.IDENTIFIER.toString())){
                	TestCase.assertEquals("LESLMTGPRK", n.getProperty(PeptideProperty.IDENTIFIER.toString()));
                }
            }
        }
    }
	
	@Test
	public void testGetAllProteins() {
		ExecutionResult allProteins = cypherQuery.getAllProteins();
		//Set<Node> nodes = CypherQuery.retrieveNodeSet(allProteins, "protein", null);
		CypherQuery.printResult("Returns all proteins", allProteins, "protein");
		//TestCase.assertEquals(22, nodes.size());		
	}
	
	@Test
	public void testGetAllSharedPeptides() {
		Set<Node> nodeSet = CypherQuery.retrieveNodeSet(cypherQuery.getAllSharedPeptides(), "aPeptide", PeptideProperty.IDENTIFIER);
		TestCase.assertEquals(4, nodeSet.size());
	}
	
	@Test
    public void testGetUniquePeptidesForProtein() {
		// Test1 : One peptide only!
        Iterator<Object> columnAs = cypherQuery.getUniquePeptidesForProtein("P64462").columnAs("peptide");
        while (columnAs.hasNext()) {
            final Object value = columnAs.next();
            if (value instanceof Node) {
                Node n = (Node)value;
                if(n.hasProperty(PeptideProperty.IDENTIFIER.toString())){
                	TestCase.assertEquals("LESLMTGPRK", n.getProperty(PeptideProperty.IDENTIFIER.toString()));
                }
            }
        }
    }
	
	
	@Test
    public void testGetPSMsForProtein() {
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
	public void testGetPSMsForPeptide() {
		// One PSM for one peptide
        Iterator<Object> columnAs = cypherQuery.getPSMsForPeptide("LESLMTGPRK").columnAs("psm");
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
        TestCase.assertEquals(1081653L, nodeList.get(0).getProperty(PsmProperty.SPECTRUMID.name()));
        TestCase.assertEquals(1, nodeList.size());
    }
	
	@Test
    public void testGetPSMsForEnzyme() {
		// Multiple PSMs for one protein and peptide
		Set<Node> nodeSet = CypherQuery.retrieveNodeSet(cypherQuery.getPSMsForEnzyme("4.1.1.15"), "psm", PsmProperty.SPECTRUMID);
        
		TestCase.assertEquals(1081661L, nodeSet.iterator().next().getProperty(PsmProperty.SPECTRUMID.name()));
		TestCase.assertEquals(4, nodeSet.size());
    }
	
	@Test
    public void testGetPSMsForPathway() {
		// Multiple PSMs for one protein and peptide
		Set<Node> nodeSet = CypherQuery.retrieveNodeSet(cypherQuery.getPSMsForPathway("K01580"), "psm", PsmProperty.SPECTRUMID);
        
		TestCase.assertEquals(1081661L, nodeSet.iterator().next().getProperty(PsmProperty.SPECTRUMID.name()));
		TestCase.assertEquals(4, nodeSet.size());
    }
	
	@Test
    public void testGetPSMsForMolecularFunction() {
		// Get PSMs for molecular function.
		Set<Node> psmNodes = CypherQuery.retrieveNodeSet(cypherQuery.getPSMsForMolecularFunction("Transferase"), "psm", PsmProperty.SPECTRUMID);
        TestCase.assertEquals(6, psmNodes.size());
    }
	
	@Test
    public void testGetPSMsForBiologicalProcess() {
		// Get PSMs for molecular function.
		Set<Node> psmNodes = CypherQuery.retrieveNodeSet(cypherQuery.getPSMsForBiologicalProcess("Methanogenesis"), "psm", PsmProperty.SPECTRUMID);
        TestCase.assertEquals(0, psmNodes.size());
    }
	
	@Test
    public void testGetPSMsForCellularComponent() {
		// Get PSMs for molecular function.
		Set<Node> psmNodes = CypherQuery.retrieveNodeSet(cypherQuery.getPSMsForCellularComponent("Cytoplasm"), "psm", PsmProperty.SPECTRUMID);
        TestCase.assertEquals(11, psmNodes.size());
    }
	
	
    @AfterClass
    public static void tearDownClass() throws IOException {
    	graphDb.shutDown();
    }
}
