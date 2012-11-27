package de.mpa.fastaLoader;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import com.compomics.util.protein.Header;

import de.mpa.io.fasta.FastaLoader;

public class TestFastaHeader extends TestCase {
	
	private File file;

	@Before
	public void setUp(){
		file = new File(getClass().getClassLoader().getResource("metadb_test.fasta").getPath());
	}
	
	@Test 
	public void testFasta() throws IOException {
		// Initialize the random access file instance
		RandomAccessFile raf = new RandomAccessFile(file, "r");				
		
		// Get the first position at the beginning of the file
		Long pos = raf.getFilePointer();
		
		// Iterate FASTA file line by line
		String line;
		int count = 1;
		while ((line = raf.readLine()) != null) {
			// Check for header
			if (line.startsWith(">")) {
				// Parse header
				Header header = Header.parseFromFASTA(line);
				if(count == 1) {
					assertEquals("00000002", header.getAccession());
					assertEquals("001AD21_227824466_FWD_prot", header.getDescription());
				} else {
					assertNull(header.getAccession());
					assertNull(header.getDescription());
				}
				count++;
			}
		}
	}
	
}
