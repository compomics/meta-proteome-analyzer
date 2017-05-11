package de.mpa.io.fasta.index;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import junit.framework.TestCase;
import uk.ac.ebi.uniprot.dataservice.client.exception.ServiceException;

public class MemoryIndexTest extends TestCase {
	
	private MemoryIndex index;
	private File fastaFile;
	
	@Before
	public void setUp() {
		fastaFile = new File("test/de/mpa/resources/test.fasta");
		try {
			index = new MemoryIndex(fastaFile, 2);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testPeptideIndex() throws ServiceException {
		Map<String, HashSet<PeptideDigest>> peptideIndex = index.getPeptideIndex();
		assertNotNull(peptideIndex);
		assertEquals(227, peptideIndex.keySet().size());
		assertEquals(227, peptideIndex.values().size());
	}

}
