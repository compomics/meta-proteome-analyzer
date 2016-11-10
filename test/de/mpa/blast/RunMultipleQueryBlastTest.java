package de.mpa.blast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import de.mpa.client.Constants;
import de.mpa.client.blast.BlastHit;
import de.mpa.client.blast.BlastResult;
import de.mpa.client.blast.DbEntry;
import de.mpa.client.blast.DbEntry.DB_Type;
import de.mpa.client.blast.RunMultiBlast;
import de.mpa.io.fasta.DigFASTAEntry;
import de.mpa.io.fasta.DigFASTAEntry.Type;

/**
 * This class test to run and query a BLAST
 * @author R. Heyer and S. Dorl
 */
public class RunMultipleQueryBlastTest extends TestCase{
	
	
	/**
	 * input DbEntry list
	 */
	private ArrayList<DigFASTAEntry> entryList = new ArrayList<DigFASTAEntry>();

	@Before
	public void setUp() {

		DigFASTAEntry dbEntry = new DigFASTAEntry("Q6GZX4", " sp|KEY?|001R_FRG3G Putative transcription factor", " Putative transcription factor",
					"MAFSAEDVLKEYDRRRRMEALLLSLYYPNDRKLLDYKEWSPPRVQVECPKAPVEWNNPPS" +
						"EKGLIVGHFSGIKYKGEKAQASEVDVNKMCCWVSKFKDAMRRYQGIQTCKIPGKVLSDLD" +
						"AKIKAYNLTVEGVEGFVRYSRVTKQHVAAFLKELRHSKQYENVNLIHYILTDKRVDIQHL" +
						"EKDLVKDFKALVESAHRMRQGHMINVKYILYQLLKKHGHGPDGPDILTVKTGSKGVLYDD" +
						"SFRKIYTDLGWKFTPL", Type.Database.UNIPROTSPROT,null);
		entryList.add(dbEntry);

		dbEntry = new DigFASTAEntry("P6GZX4", " sp|KEY?|001R_FRG3G Putative transcription factor", " Putative transcription factor",
				"AFSAEDVLKEYDRRRRMEALLLSLYYPNDRKLLDYKEWSPPRVQVECPKAPVEWNNPPS" +
				"EKGLIVGHFSGIKYKGEKAQASEVDVNKMCCWVSKFKDAMRRYQGIQTCKIPGKVLSDLD" +
				"AKIKAYNLTVEGVEGFVRYSRVTKQHVSAFLKELRHSKQYENVNLIHYILTDKRVDIQHL" +
				"EKDLVKDFKALVESAHRMRQGHMINVKYILYQLLKKHGHGPDGPDILTVKTGSKGVLYDD" +
				"SFRKIYTDLGWKFTPL", Type.Database.UNIPROTSPROT,null);
		
		entryList.add(dbEntry);
		
	}

	@Test
	public void testBlast(){
		
		HashMap<String, BlastResult> blastRes = null; 
		// Execute BLAST
		try {
			blastRes = RunMultiBlast.performBLAST(entryList, Constants.BLAST_FILE, Constants.BLAST_UNIPROT_DB, Constants.BLAST_EVALUE);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Check Blast hits
		assertEquals(2, blastRes.size());
		assertEquals(533.0, blastRes.get("Q6GZX4").getBestBitScoreBlastHit().getScore());
		assertEquals(0.0, blastRes.get("Q6GZX4").getBestEValueBlastHit().geteValue());
	}
}
