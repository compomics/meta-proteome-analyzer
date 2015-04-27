package de.mpa.fastaLoader;

import gnu.trove.map.TObjectLongMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import com.compomics.util.protein.Protein;

import de.mpa.client.Client;
import de.mpa.db.DBManager;
import de.mpa.io.fasta.FastaLoader;

public class FastaLoaderTest extends TestCase {

	private FastaLoader fastaLoader;
	private File file;
	
	@Before
	public void setUp(){
//		file = new File("test/de/mpa/resources/test.fasta");
//		fastaLoader = FastaLoader.getInstance();
//		fastaLoader.setFastaFile(file);
//		try {
//			fastaLoader.loadFastaFile();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
	}
	
//	@Test 
//	public void testLoadFastaFile(){
//		try {
//			TObjectLongMap<String> indexMap1 = fastaLoader.getIndexMap();
//			fastaLoader.writeIndexFile();
//			fastaLoader.readIndexFile();
//			TObjectLongMap<String> indexMap2 = fastaLoader.getIndexMap();
//			assertEquals(indexMap1, indexMap2);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	
	@Test
	public void testrepairSequence(){
		fastaLoader = FastaLoader.getInstance();
		fastaLoader.setFastaFile(new File("//scratch//metaprot//data//fasta//DB_WWTP19_03_2015_out.fasta"));
		try {
			fastaLoader.loadFastaFile();
//			fastaLoader.loadFastaFile();
			fastaLoader.repairSequences(DBManager.getInstance().getConnection());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	
//	@Test
//	public void testgetProteinFromFasta(){
//		try {
//			// TODO: IOException thrown for invalid handle!
//			Protein proteinFirst = fastaLoader.getProteinFromFasta("Q6GZX4");
//			Protein proteinMiddle = fastaLoader.getProteinFromFasta("B1XDP2"); // not included, should fail
//			Protein proteinLast = fastaLoader.getProteinFromFasta("Q197A7");
//	
//			// ProteinFirst
//			assertEquals("001R_FRG3G Putative transcription factor 001R OS=Frog virus 3 (isolate Goorha) GN=FV3-001R PE=4 SV=1", proteinFirst.getHeader().getDescription());
//			assertEquals("MAFSAEDVLKEYDRRRRMEALLLSLYYPNDRKLLDYKEWSPPRVQVECPKAPVEWNNPPSEKGLIVGHFSGIKYKGEKAQASEVDVNKMCCWVSKFKDAMRRYQGIQTCKIPGKVLSDLDAKIKAYNLTVEGVEGFVRYSRVTKQHVAAFLKELRHSKQYENVNLIHYILTDKRVDIQHLEKDLVKDFKALVESAHRMRQGHMINVKYILYQLLKKHGHGPDGPDILTVKTGSKGVLYDDSFRKIYTDLGWKFTPL", proteinFirst.getSequence().getSequence());
//			
//			// ProteinMiddle--> Should fail
//			assertEquals(null, proteinMiddle);
//			
//			// ProteinLast
//			assertEquals("053L_IIV3 Uncharacterized protein 053L OS=Invertebrate iridescent virus 3 GN=IIV3-053L PE=4 SV=1", proteinLast.getHeader().getDescription());
//			assertEquals("MEQYLQAFEFVEEMVVLPKYLSWELYHHLAVLLREKYPKTYKNKGYIFNIKVKSILDNRITPTGQIVLVVMFQSDLYVPQVGHVFTERIRVNSVDDRYQWITIEPLTVFLRSNIPYKPNTLVTVQICSIKMDNTLCFGTILD", proteinLast.getSequence().getSequence());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
}
