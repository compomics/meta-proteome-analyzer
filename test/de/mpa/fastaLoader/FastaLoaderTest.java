package de.mpa.fastaLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.compomics.util.protein.Protein;

import de.mpa.io.fasta.FastaLoader;
import junit.framework.TestCase;
import uk.ac.ebi.uniprot.dataservice.client.exception.ServiceException;

public class FastaLoaderTest extends TestCase {

	private FastaLoader fastaLoader;
	private File file;
	
	@Before
	public void setUp() {
		file = new File("test/de/mpa/resources/test.fasta");
		fastaLoader = FastaLoader.getInstance();
		fastaLoader.setFastaFile(file);
		try {
			fastaLoader.loadFastaFile();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetProteinFromFasta() throws ServiceException {
		File indexFile = new File(file.getAbsolutePath() + ".fb");

		try {
			if (indexFile.exists()) {
				fastaLoader.setIndexFile(indexFile);
				fastaLoader.readIndexFile();
			}
			
			Protein proteinFirst = fastaLoader.getProteinFromFasta("Q8U4R3");
			
			// ProteinFirst
			assertEquals("1A1D_PYRFU Putative 1-aminocyclopropane-1-carboxylate deaminase OS=Pyrococcus furiosus (strain ATCC 43587 / DSM 3638 / JCM 8422 / Vc1) GN=PF0010 PE=3 SV=2", proteinFirst.getHeader().getDescription());
			
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
