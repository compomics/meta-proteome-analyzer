package de.mpa.io.fasta.index;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
import uk.ac.ebi.uniprot.dataservice.client.exception.ServiceException;

public class OffHeapIndexTest extends TestCase {
	
	private OffHeapIndex index;
	private File fastaFile;
	
	@Before
	public void setUp() {
		fastaFile = new File("test/de/mpa/resources/yeast_ups.fasta");
		try {
			index = new OffHeapIndex(fastaFile, 2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testPeptideIndex() throws ServiceException {
		Map<String, Set<String>> peptideIndex = index.getPeptideIndex();
		assertNotNull(peptideIndex);
		assertEquals(726183, peptideIndex.keySet().size());
		assertEquals(726183, peptideIndex.values().size());
		
		Set<Entry<String, Set<String>>> entries = peptideIndex.entrySet();
		for (Entry<String, Set<String>> e : entries) {
			Set<String> values = e.getValue();
			if (values.size() > 1) {
				System.out.println(e.getKey());
				for (String accession : values) {
					System.out.print(accession + " ");
				}
				System.out.println();
			}
		}
	}

}
