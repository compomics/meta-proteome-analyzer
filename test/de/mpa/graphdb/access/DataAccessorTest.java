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
import org.neo4j.graphdb.GraphDatabaseService;

import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.graphdb.insert.DataInserter;
import de.mpa.graphdb.properties.EnzymeProperty;
import de.mpa.graphdb.properties.OntologyProperty;
import de.mpa.graphdb.properties.PathwayProperty;
import de.mpa.graphdb.properties.PeptideProperty;
import de.mpa.graphdb.properties.ProteinProperty;
import de.mpa.graphdb.properties.PsmProperty;
import de.mpa.graphdb.properties.SpeciesProperty;
import de.mpa.graphdb.setup.GraphDatabase;
import de.mpa.graphdb.vertices.Enzyme;
import de.mpa.graphdb.vertices.Ontology;
import de.mpa.graphdb.vertices.Pathway;
import de.mpa.graphdb.vertices.Peptide;
import de.mpa.graphdb.vertices.PeptideSpectrumMatch;
import de.mpa.graphdb.vertices.Protein;
import de.mpa.graphdb.vertices.Species;

/**
 * Test class for starting up the graph database and accessing it.
 * @author Thilo Muth
 *
 */
public class DataAccessorTest extends TestCase {
	
	private DbSearchResult dbSearchResult;
	private GraphDatabase graphDb;
	private GraphDatabaseService service;
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
			assertNotNull(graphDb);
			service =  graphDb.getService();
			assertNotNull(service);
			
			// Insert the data.
			DataInserter dataInserter = new DataInserter(service);
			dataInserter.setData(dbSearchResult);
			dataInserter.insert();
			
			dataAccessor = dataInserter.getDataAccessor();
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
		Protein prot1 = dataAccessor.getSingleProtein(ProteinProperty.ACCESSION, "P64462");
		
		// Test protein specification.
		assertEquals("P64462", prot1.getAccession());
		assertEquals("MHVTLVEINVHEDKVDEFIEVFRQNHLGSVQEEGNLRFDVLQDPEVNSRFYIYEAYKDEDAVAFHKTTPHYKTCVAKLESLMTGPRKKRLFNGLMP", prot1.getSequence());
		assertEquals("LSRG_ECO57 Autoinducer 2-degrading protein lsrG", prot1.getDescription());
		
		// Test linked peptide(s)
		for(Peptide peptide: prot1.getPeptides()) {
			assertEquals("LESLMTGPRK", peptide.getSequence());
		}
	}
	
	@Test
	public void testSpecies() {
		// Get single species.
		Species species = dataAccessor.getSingleSpecies(SpeciesProperty.NAME, "Escherichia coli (species)");
		assertEquals("Escherichia coli (species)", species.getName());

		// Test connected proteins		
		Iterator<Protein> iter = species.getProteins().iterator();
		List<Protein> proteins = new ArrayList<Protein>();
		
		while(iter.hasNext()) {
			proteins.add(iter.next());
		}
		assertEquals("P0A954", proteins.get(0).getAccession());
		assertEquals(14, proteins.size());
	}
	
	@Test
	public void testPeptides() {
		Peptide peptide = dataAccessor.getSinglePeptide(PeptideProperty.SEQUENCE, "LESLMTGPRK");
		assertEquals("LESLMTGPRK", peptide.getSequence());
	}
	
	@Test
	public void testPSMs() {
		PeptideSpectrumMatch psm = dataAccessor.getSinglePSM(PsmProperty.SPECTRUMID, 1081621L);
		assertEquals((Long) 1081621L, psm.getSpectrumID());
		assertEquals((Integer) 1, psm.getVotes());
	}
	
	@Test
	public void testEnzymes() {
		Enzyme enzyme = dataAccessor.getSingleEnzyme(EnzymeProperty.ECNUMBER, "4.1.1.15");
		assertEquals("4.1.1.15", enzyme.getECNumber());
		
		// Test connected proteins		
		Iterator<Protein> iter = enzyme.getProteins().iterator();
		List<Protein> proteins = new ArrayList<Protein>();
		
		while(iter.hasNext()) {
			proteins.add(iter.next());
		}
		assertEquals("Q8FHG5", proteins.get(0).getAccession());
		assertEquals(6, proteins.size());
		
	}
	
	@Test
	public void testPathways() {
		Pathway pathway = dataAccessor.getSinglePathway(PathwayProperty.KONUMBER, "K02358");
		assertEquals("K02358", pathway.getKONumber());
		
		// Test connected proteins		
		Iterator<Protein> iter = pathway.getProteins().iterator();
		List<Protein> proteins = new ArrayList<Protein>();
		
		while(iter.hasNext()) {
			proteins.add(iter.next());
		}
		assertEquals("A7ZSL4", proteins.get(0).getAccession());
		assertEquals(4, proteins.size());
	}
	
	@Test
	public void testOntologies() {
		Ontology ontology = dataAccessor.getSingleOntology(OntologyProperty.KEYWORD, "Protein biosynthesis");
		assertEquals("Protein biosynthesis", ontology.getKeyword());
		
		// Test connected proteins		
		Iterator<Protein> iter = ontology.getBiologicalProcessProteins().iterator();
		List<Protein> proteins = new ArrayList<Protein>();
		
		while(iter.hasNext()) {
			proteins.add(iter.next());
		}
		assertEquals("A7ZSL4", proteins.get(0).getAccession());
		assertEquals(4, proteins.size());
	}

	
	@AfterClass
	public void tearDown() throws Exception {
		graphDb.shutDown();
	}
}
