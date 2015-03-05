package de.mpa.blast;

import java.io.IOException;
import java.util.ArrayList;
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

/**
 * This class test to run and query a BLAST
 * @author R. Heyer and S. Dorl
 */
public class RunMultipleQueryBlastTest extends TestCase{
	
	/**
	 * The BLAST result object.
	 */
	private BlastResult blastRes;
	
	/**
	 * input DbEntry list
	 */
	private List<DbEntry> entryList = new ArrayList<DbEntry>();

	@Before
	public void setUp() {

		DbEntry dbEntry = new DbEntry("FIRST", "sp|KEY?|001R_FRG3G Putative transcription factor 001R OS=Frog virus 3 (isolate Goorha) GN=FV3-001R PE=4 SV=1", DB_Type.UNIPROTSPROT, null);
		dbEntry.setSequence(	"MAFSAEDVLKEYDRRRRMEALLLSLYYPNDRKLLDYKEWSPPRVQVECPKAPVEWNNPPS" +
				"EKGLIVGHFSGIKYKGEKAQASEVDVNKMCCWVSKFKDAMRRYQGIQTCKIPGKVLSDLD" +
				"AKIKAYNLTVEGVEGFVRYSRVTKQHVAAFLKELRHSKQYENVNLIHYILTDKRVDIQHL" +
				"EKDLVKDFKALVESAHRMRQGHMINVKYILYQLLKKHGHGPDGPDILTVKTGSKGVLYDD" +
				"SFRKIYTDLGWKFTPL");
		entryList.add(dbEntry);
		
		dbEntry = new DbEntry("SECOND", "sp|KEY?|001R_FRG3G Putative transcription factor 001R OS=Frog virus 3 (isolate Goorha) GN=FV3-001R PE=4 SV=1", DB_Type.UNIPROTSPROT, null);
		dbEntry.setSequence(	"MAFSAEDVLKEYDRRRRMEALLLSLYYPNDRKLLDYKEWSPPRVQVECPKAPVEWNNPPS" +
				"EKGLIVGHFSGIKYKGEKAQASEVDVNKMCCWVSKFKDAMRRYQGIQTCKIPGKVLSDLD" +
				"AKIKAYNLTVEGVEGFVRYSRVTKQHVSAFLKELRHSKQYENVNLIHYILTDKRVDIQHL" +
				"EKDLVKDFKALVESAHRMRQGHMINVKYILYQLLKKHGHGPDGPDILTVKTGSKGVLYDD" +
				"SFRKIYTDLGWKFTPL");
		entryList.add(dbEntry);
		
	}

	@Test
	public void testBlast(){
		
		// Execute BLAST
		RunMultiBlast blaster = new RunMultiBlast(Constants.BLAST_FILE, Constants.BLAST_UNIPROT_DB, Constants.BLAST_EVALUE, entryList);
		try {
			blaster.blast();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		blastRes = blaster.getBlastResultMap().get("FIRST");
//				for (Entry<String, BlastHit> entry : blastRes.getBlastHitsMap().entrySet()) {
//					System.out.println("key " + entry.getKey());
//					System.out.println("hit name " + entry.getValue().getName());
//					System.out.println("hit accesion " + entry.getValue().getAccession());
//				}

		// Check Blast hits
		Map<String, BlastHit> blastHitsMap = blastRes.getBlastHitsMap();
		assertEquals(3, blastHitsMap.size());
		assertEquals(533.0, blastHitsMap.get("Q6GZX4").getScore());
		assertEquals(0.0, blastHitsMap.get("Q6GZX4").geteValue());
	}
}
