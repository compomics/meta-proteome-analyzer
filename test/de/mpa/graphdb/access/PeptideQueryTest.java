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
import org.neo4j.graphdb.Node;

import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.graphdb.cypher.CypherQuery;
import de.mpa.graphdb.insert.GraphDatabaseHandler;
import de.mpa.graphdb.properties.PeptideProperty;
import de.mpa.graphdb.setup.GraphDatabase;

public class PeptideQueryTest {
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
	
	@Test
    public void testGetPeptidesForSpecies() {
		// Retrieve all peptides for species.
        Iterator<Object> columnAs = cypherQuery.getPeptidesForSpecies("Methanoculleus marisnigri JR1 (no rank)").columnAs("peptide");
        List<Node> nodes = new ArrayList<Node>();
        while (columnAs.hasNext()) {
            final Object value = columnAs.next();
            if (value instanceof Node) {
                Node n = (Node)value;
                nodes.add(n);
            }
        }
        TestCase.assertEquals(10, nodes.size());
    }
	
	@Test 
	public void testGetAllUniquePeptides() {
		// Retrieve all unique peptides from the dataset = 201 Unique peptides
        Iterator<Object> columnAs = cypherQuery.getAllUniquePeptides().columnAs("peptide");
        List<Node> nodes = new ArrayList<Node>();
        while (columnAs.hasNext()) {
            final Object value = columnAs.next();
            if (value instanceof Node) {
                Node n = (Node)value;
                nodes.add(n);
            }
        }
        TestCase.assertEquals(201, nodes.size());
    }
	
	@Test 
	public void testGetAllSharedPeptides() {
		// Retrieve all unique peptides from the dataset = 201 Unique peptides
        Iterator<Object> columnAs = cypherQuery.getAllSharedPeptides().columnAs("peptide");
        List<Node> nodes = new ArrayList<Node>();
        while (columnAs.hasNext()) {
            final Object value = columnAs.next();
            if (value instanceof Node) {
                Node n = (Node)value;
                nodes.add(n);
            }
        }
        TestCase.assertEquals(21, nodes.size());
	}
	
	@Test
    public void testGetPeptidesForProtein() {
		// Protein contains three peptides.
		Set<Node> peptideNodes = CypherQuery.retrieveNodeSet(cypherQuery.getPeptidesForProtein("P07955"), "peptide", PeptideProperty.SEQUENCE);
		TestCase.assertEquals(3, peptideNodes.size());
    }
	
	@Test
    public void testGetUniquePeptidesForProtein() {
		// Protein contains two unique peptides.
		Set<Node> peptideNodes = CypherQuery.retrieveNodeSet(cypherQuery.getUniquePeptidesForProtein("P07955"), "peptide", PeptideProperty.SEQUENCE);
		TestCase.assertEquals(2, peptideNodes.size());
    }
	
	@Test
    public void testGetSharedPeptidesForProtein() {
		// Protein contains two unique peptides.
		Set<Node> peptideNodes = CypherQuery.retrieveNodeSet(cypherQuery.getSharedPeptidesForProtein("P07955"), "peptide", PeptideProperty.SEQUENCE);
		TestCase.assertEquals(1, peptideNodes.size());
    }
	
	@Test
    public void testGetPeptidesForEnzyme() {
		// Retrieve all peptides for enzyme.
        Iterator<Object> columnAs = cypherQuery.getPeptidesForEnzyme("2.1.1.249").columnAs("peptide");
        List<Node> nodes = new ArrayList<Node>();
        while (columnAs.hasNext()) {
            final Object value = columnAs.next();
            if (value instanceof Node) {
                Node n = (Node)value;
                nodes.add(n);
            }
        }
        TestCase.assertEquals(3, nodes.size());
        TestCase.assertEquals("LDGDQGNSGVGIPSSR", nodes.get(0).getProperty(PeptideProperty.SEQUENCE.name()));
        TestCase.assertEquals("AVSVEQGMEVPVTHDIGTIR", nodes.get(1).getProperty(PeptideProperty.SEQUENCE.name()));
        TestCase.assertEquals("AVSVEQGMEVPVTHDIGTLR", nodes.get(2).getProperty(PeptideProperty.SEQUENCE.name()));
    }
	
	@Test
    public void testGetPeptidesForMolecularFunction() {
		// Get peptides for molecular function.
		Set<Node> peptideNodes = CypherQuery.retrieveNodeSet(cypherQuery.getPeptidesForMolecularFunction("Transferase"), "peptide", PeptideProperty.SEQUENCE);
        TestCase.assertEquals(62, peptideNodes.size());
    }
	
	@Test
    public void testGetPeptidesForBiologicalProcess() {
		// Get peptides for molecular function.
		Set<Node> peptideNodes = CypherQuery.retrieveNodeSet(cypherQuery.getPeptidesForBiologicalProcess("Methanogenesis"), "peptide", PeptideProperty.SEQUENCE);
        TestCase.assertEquals(54, peptideNodes.size());
    }
	
	@Test
    public void testGetPeptidesForCellularComponent() {
		// Get peptides for molecular function.
		Set<Node> peptideNodes = CypherQuery.retrieveNodeSet(cypherQuery.getPeptidesForCellularComponent("Cytoplasm"), "peptide", PeptideProperty.SEQUENCE);
        TestCase.assertEquals(12, peptideNodes.size());
    }
	
	@Test
    public void testGetProteinsForPathway() {
		// Get all peptides for pathway.
		Set<Node> peptideNodes = CypherQuery.retrieveNodeSet(cypherQuery.getPeptidesForPathway("K02117"), "peptide", PeptideProperty.SEQUENCE);
		TestCase.assertEquals(8, peptideNodes.size());
    }

	@AfterClass
	public static void tearDownClass() throws IOException {
		graphDb.shutDown();
	}
}
