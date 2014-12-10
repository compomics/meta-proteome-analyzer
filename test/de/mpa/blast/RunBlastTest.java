package de.mpa.blast;

import java.util.Map;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import de.mpa.client.Constants;
import de.mpa.client.blast.BlastHit;
import de.mpa.client.blast.BlastResult;
import de.mpa.client.blast.DbEntry;
import de.mpa.client.blast.DbEntry.DB_Type;
import de.mpa.client.blast.RunBlast;

/**
 * This class test to run and query a BLAST
 * @author R. Heyer
 */
public class RunBlastTest extends TestCase{

	/**
	 * The FASTAfile, which should be BLASTed.
	 *//**
	 * File for a dummy fasta for each BLAST query
	 */
	public static final String BLAST_DUMMY_FASTA_FILE = System.getProperty("user.dir")  + System.getProperty("file.separator") + "out" + System.getProperty("file.separator") + "Test_dummy.fasta";

	/**
	 * The BLAST result object.
	 */
	private BlastResult blastRes;

	@Before
	public void setUp() {
		// Entry for BLASt RUN
		DbEntry dbEntry = new DbEntry("Q6GZX4", "sp|Q6GZX4|001R_FRG3G Putative transcription factor 001R OS=Frog virus 3 (isolate Goorha) GN=FV3-001R PE=4 SV=1", DB_Type.UNIPROTSPROT, null);
		dbEntry.setSequence(	"MAFSAEDVLKEYDRRRRMEALLLSLYYPNDRKLLDYKEWSPPRVQVECPKAPVEWNNPPS" +
				"EKGLIVGHFSGIKYKGEKAQASEVDVNKMCCWVSKFKDAMRRYQGIQTCKIPGKVLSDLD" +
				"AKIKAYNLTVEGVEGFVRYSRVTKQHVAAFLKELRHSKQYENVNLIHYILTDKRVDIQHL" +
				"EKDLVKDFKALVESAHRMRQGHMINVKYILYQLLKKHGHGPDGPDILTVKTGSKGVLYDD" +
				"SFRKIYTDLGWKFTPL");

		// Execute BLAST
		blastRes = RunBlast.blast(Constants.BLAST_FILE, Constants.BLAST_UNIPROT_DB, Constants.BLAST_EVALUE, dbEntry);
	}

	@Test
	public void testBlast(){
		//		for (Entry<String, BlastHit> entry : blastRes.getBlastHitsMap().entrySet()) {
		//			System.out.println(entry.getKey());
		//			System.out.println(entry.getValue().getName());
		//			System.out.println(entry.getValue().getAccession());
		//		}

		// Check Blast hits
		Map<String, BlastHit> blastHitsMap = blastRes.getBlastHitsMap();
		assertEquals(3, blastHitsMap.size());
		assertEquals("001R_FRG3G Putative transcription factor 001R OS=Frog virus 3 (isolate Goorha) GN=FV3-001R PE=4 SV=1", blastHitsMap.get("Q6GZX4").getName());
		assertEquals(256, blastHitsMap.get("Q6GZX4").getLength());
		assertEquals(533.102, blastHitsMap.get("Q6GZX4").getScore());// Bit Score
		assertEquals(0.0, blastHitsMap.get("Q6GZX4").geteValue());
		assertEquals("256", blastHitsMap.get("Q6GZX4").getIdentities());
		assertEquals("256", blastHitsMap.get("Q6GZX4").getPositives());
		assertEquals("MAFSAEDVLKEYDRRRRMEALLLSLYYPNDRKLLDYKEWSPPRVQVECPKAPVEWNNPPSEKGLIVGHFSGIKYKGEKAQASEVDVNKMCCWVSKFKDAMRRYQGIQTCKIPGKVLSDLDAKIKAYNLTVEGVEGFVRYSRVTKQHVAAFLKELRHSKQYENVNLIHYILTDKRVDIQHLEKDLVKDFKALVESAHRMRQGHMINVKYILYQLLKKHGHGPDGPDILTVKTGSKGVLYDDSFRKIYTDLGWKFTPL", blastHitsMap.get("Q6GZX4").getQuery());
		assertEquals("MAFSAEDVLKEYDRRRRMEALLLSLYYPNDRKLLDYKEWSPPRVQVECPKAPVEWNNPPSEKGLIVGHFSGIKYKGEKAQASEVDVNKMCCWVSKFKDAMRRYQGIQTCKIPGKVLSDLDAKIKAYNLTVEGVEGFVRYSRVTKQHVAAFLKELRHSKQYENVNLIHYILTDKRVDIQHLEKDLVKDFKALVESAHRMRQGHMINVKYILYQLLKKHGHGPDGPDILTVKTGSKGVLYDDSFRKIYTDLGWKFTPL", blastHitsMap.get("Q6GZX4").getSbjct());
		assertEquals("001R_FRG3G Putative transcription factor 001R OS=Frog virus 3 (isolate Goorha) GN=FV3-001R PE=4 SV=1", blastHitsMap.get("Q6GZX4").getName());
	}
}
