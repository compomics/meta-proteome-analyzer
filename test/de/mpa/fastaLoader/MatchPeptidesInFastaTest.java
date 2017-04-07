package de.mpa.fastaLoader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.mpa.io.fasta.Database;
import de.mpa.io.fasta.Entry;
import de.mpa.io.fasta.FastaUtilities;
import junit.framework.TestCase;

public class MatchPeptidesInFastaTest extends TestCase {
	
	private Database fastaDb;
	private Set<String> peptides;
	
	@Before
	public void setUp() throws IOException{
		fastaDb = FastaUtilities.read("E://dbstuff//uniprot_sprot.fasta");
		
		final BufferedReader reader = new BufferedReader(new FileReader("E://dbstuff//TrEMBL_Gent16a_Peptides_FDR5.csv"));
		String nextLine = null;
		peptides = new HashSet<String>();
	
		while ((nextLine = reader.readLine()) != null) {
			peptides.add(nextLine);
		}
		reader.close();
	}
	
	@Test
	public void testgetProteinFromFasta() {
		int matches = 0;
		System.out.println("total peptides: " + peptides.size());
	
		for (String string : peptides) {
			boolean found = false;
			for (Entry e : fastaDb.getEntries()) {
				if (!found && e.getSequenceAsString().contains(string)) {
					matches++;
					break;
				}
			}
			
			if (matches % 500 == 0) {
				System.out.println(matches);
			}
		}
		
		System.out.println(matches);
	}
	
}
