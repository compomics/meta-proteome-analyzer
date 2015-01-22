package de.mpa.io.fasta;

import gnu.trove.map.TObjectLongMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import com.compomics.util.protein.Protein;

public class FastaLoaderTest extends TestCase {
	
	private FastaLoader fastaLoader;
	private String filePath = "test/de/mpa/resources/fasta/test.fasta";
	private String filePath2 = "test/de/mpa/resources/fasta/metadb_test.fasta";
	
	@Before
	public void setUp() throws FileNotFoundException {
		fastaLoader = FastaLoader.getInstance();		
		fastaLoader.setFastaFile(new File(filePath));
		fastaLoader.loadFastaFile();
	}
	
	@Test
	public void testGetNumberOfEntries() throws IOException, ClassNotFoundException {
		assertEquals(76, fastaLoader.getNumberOfEntries());
		fastaLoader.setFastaFile(new File(filePath2));
		fastaLoader.loadFastaFile();
		assertEquals(2, fastaLoader.getNumberOfEntries());
	}
	
	@Test 
	public void testWriteAndReadIndexFile(){
		try {
			fastaLoader.writeIndexFile();
			fastaLoader.readIndexFile();
			TObjectLongMap<String> indexMap = fastaLoader.getIndexMap();
			assertNotNull(indexMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetProteinFromFasta() throws IOException, ClassNotFoundException {
		FastaLoader fastaLoader = FastaLoader.getInstance();
		fastaLoader.setFastaFile(new File(filePath));
		fastaLoader.setIndexFile(new File(filePath + ".fb"));
		fastaLoader.readIndexFile();
		Protein protein = fastaLoader.getProteinFromFasta("Q197F8");
		assertEquals("002R_IIV3 Uncharacterized protein 002R OS=Invertebrate iridescent virus 3 GN=IIV3-002R PE=4 SV=1", protein.getHeader().getDescription());

	}

}
