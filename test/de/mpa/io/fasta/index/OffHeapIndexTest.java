package de.mpa.io.fasta.index;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.NavigableSet;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
import uk.ac.ebi.uniprot.dataservice.client.exception.ServiceException;

public class OffHeapIndexTest extends TestCase {
	
	private OffHeapIndex index;
	private File fastaFile;
	
	@Before
	public void setUp() {
		
		fastaFile = new File("S:\\OE\\MF1\\staff\\Thilo\\Projects\\MPA_Portable\\Databases\\uniprot_sprot_ecoli.fasta");
//		fastaFile = new File("test/de/mpa/resources/test.fasta");
		try {
			index = new OffHeapIndex(fastaFile, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testSpeciesIndex() throws ServiceException {
		NavigableSet<Object[]> speciesIndex = index.getSpeciesIndex();
		assertNotNull(speciesIndex);
		
		Iterator<Object[]> iterator = speciesIndex.iterator();
		while (iterator.hasNext()) {
			Object[] objects = (Object[]) iterator.next();
			System.out.println(objects[0] + " : " + objects[1]);
		}
		
		NavigableSet<Object[]> peptideIndex = index.getPeptideIndex();
		assertNotNull(peptideIndex);
		
		Iterator<Object[]> iterator2 = peptideIndex.iterator();
		while (iterator2.hasNext()) {
			Object[] objects = (Object[]) iterator2.next();
			System.out.println(objects[0] + " : " + objects[1]);
		}
	}
}
