package de.mpa.fastaLoader;

import java.io.File;
import java.util.HashMap;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import de.mpa.io.fasta.FastaLoader;

public class TestFastaLoader extends TestCase {

	private FastaLoader fastaLoader;
	private File file;
	
	@Before
	public void setUp(){
		file = new File(getClass().getClassLoader().getResource("Fasta/test.fasta").getPath());
		fastaLoader = FastaLoader.getInstance();
		fastaLoader.setFastaFile(file);
	}
	
	@Test 
	public void testLoadFastaFile(){
		try {
			fastaLoader.loadFastaFile();
			HashMap<String, Long> indexMap1 = fastaLoader.getIndexMap();
			fastaLoader.writeIndexFile();
			fastaLoader.readIndexFile();
			HashMap<String, Long> indexMap2 = fastaLoader.getIndexMap();
			assertEquals(indexMap1, indexMap2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
