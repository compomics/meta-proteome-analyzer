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
import org.junit.BeforeClass;
import org.junit.Test;

import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.graphdb.insert.GraphDatabaseHandler;
import de.mpa.graphdb.nodes.Enzyme;
import de.mpa.graphdb.nodes.Ontology;
import de.mpa.graphdb.nodes.Pathway;
import de.mpa.graphdb.nodes.Peptide;
import de.mpa.graphdb.nodes.PeptideSpectrumMatch;
import de.mpa.graphdb.nodes.Protein;
import de.mpa.graphdb.nodes.Taxon;
import de.mpa.graphdb.properties.EnzymeProperty;
import de.mpa.graphdb.properties.OntologyProperty;
import de.mpa.graphdb.properties.PathwayProperty;
import de.mpa.graphdb.properties.PeptideProperty;
import de.mpa.graphdb.properties.ProteinProperty;
import de.mpa.graphdb.properties.PsmProperty;
import de.mpa.graphdb.properties.TaxonProperty;
import de.mpa.graphdb.setup.GraphDatabase;

/**
 * Test class for starting up the graph database and accessing it 
 * by using Tinkerpop Blueprints framework: https://github.com/tinkerpop/blueprints/wiki
 * @author Thilo Muth
 *
 */
public class DataAccessorTest extends TestCase {
	
	private DbSearchResult dbSearchResult;
	private GraphDatabase graphDb;
	private DataAccessor dataAccessor;
	
	@BeforeClass
	public void setUp() {
		// Proteins file.
		File file = new File(getClass().getClassLoader().getResource("DumpTest.mpa").getPath());
		
		// Object input stream.
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(file))));
			dbSearchResult = (DbSearchResult) ois.readObject();
			// Create a graph database.
			graphDb = new GraphDatabase("target/graphdb", true);
			
			// Insert the data.
			GraphDatabaseHandler graphDbHandler = new GraphDatabaseHandler(graphDb);
			graphDbHandler.setData(dbSearchResult);
			
			//dataAccessor = graphDbHandler.getDataAccessor();
			assertNotNull(dataAccessor);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testProteins() {
		// Protein
		Protein prot1 = dataAccessor.getSingleProtein(ProteinProperty.IDENTIFIER, "P64462");
		
		// Test protein specification.
		assertEquals("P64462", prot1.getIdentifier());
		assertEquals("MHVTLVEINVHEDKVDEFIEVFRQNHLGSVQEEGNLRFDVLQDPEVNSRFYIYEAYKDEDAVAFHKTTPHYKTCVAKLESLMTGPRKKRLFNGLMP", prot1.getSpecies());
		assertEquals("LSRG_ECO57 Autoinducer 2-degrading protein lsrG", prot1.getDescription());
		
		// Test linked peptide(s)
		for(Peptide peptide: prot1.getPeptides()) {
			assertEquals("LESLMTGPRK", peptide.toString());
		}
	}
	
	@Test
	public void testSpecies() {
		// Get single species.
		Taxon taxon = dataAccessor.getTaxon(TaxonProperty.IDENTIFIER, "Escherichia coli (species)");
		assertEquals("Escherichia coli (species)", taxon.getIdentifier());

		// Test connected proteins		
		Iterator<Protein> iter = taxon.getProteins().iterator();
		List<Protein> proteins = new ArrayList<Protein>();
		
		while(iter.hasNext()) {
			proteins.add(iter.next());
		}
		assertEquals("P0A954", proteins.get(0).getIdentifier());
		assertEquals(14, proteins.size());
	}
	
	@Test
	public void testPeptides() {
		Peptide peptide = dataAccessor.getSinglePeptide(PeptideProperty.IDENTIFIER, "LESLMTGPRK");
		assertEquals("LESLMTGPRK", peptide.toString());
	}
	
	@Test
	public void testPSMs() {
		PeptideSpectrumMatch psm = dataAccessor.getSinglePSM(PsmProperty.SPECTRUMID, 1081621L);
		assertEquals((Long) 1081621L, psm.getSpectrumID());
	}
	
	@Test
	public void testEnzymes() {
		Enzyme enzyme = dataAccessor.getSingleEnzyme(EnzymeProperty.IDENTIFIER, "4.1.1.15");
		assertEquals("4.1.1.15", enzyme.getIdentifier());
		
		// Test connected proteins		
		Iterator<Protein> iter = enzyme.getProteins().iterator();
		List<Protein> proteins = new ArrayList<Protein>();
		
		while(iter.hasNext()) {
			proteins.add(iter.next());
		}
		assertEquals("Q8FHG5", proteins.get(0).getIdentifier());
		assertEquals(6, proteins.size());
		
	}
	
	@Test
	public void testPathways() {
		Pathway pathway = dataAccessor.getSinglePathway(PathwayProperty.IDENTIFIER, "K02358");
		assertEquals("K02358", pathway.getIdentifier());
		
		// Test connected proteins		
		Iterator<Protein> iter = pathway.getProteins().iterator();
		List<Protein> proteins = new ArrayList<Protein>();
		
		while(iter.hasNext()) {
			proteins.add(iter.next());
		}
		assertEquals("A7ZSL4", proteins.get(0).getIdentifier());
		assertEquals(4, proteins.size());
	}
	
	@Test
	public void testOntologies() {
		Ontology ontology = dataAccessor.getSingleOntology(OntologyProperty.IDENTIFIER, "Protein biosynthesis");
		assertEquals("Protein biosynthesis", ontology.getIdentifier());
		
		// Test connected proteins		
		Iterator<Protein> iter = ontology.getBiologicalProcessProteins().iterator();
		List<Protein> proteins = new ArrayList<Protein>();
		
		while(iter.hasNext()) {
			proteins.add(iter.next());
		}
		assertEquals("A7ZSL4", proteins.get(0).getIdentifier());
		assertEquals(4, proteins.size());
	}

	
	@AfterClass
	public void tearDown() throws Exception {
		graphDb.shutDown();
	}
}
