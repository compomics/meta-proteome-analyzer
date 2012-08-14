package de.mpa.fastaLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import com.compomics.util.protein.Protein;

import de.mpa.io.fasta.FastaLoader;

public class TestFastaLoader extends TestCase {

	private FastaLoader fastaLoader;
	private File file;
	
	@Before
	public void setUp(){
		file = new File(getClass().getClassLoader().getResource("Fasta/test.fasta").getPath());
		fastaLoader = FastaLoader.getInstance();
		fastaLoader.setFastaFile(file);
		try {
			fastaLoader.loadFastaFile();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Test 
	public void testLoadFastaFile(){
		try {
			Map<String, Long> indexMap1 = fastaLoader.getIndexMap();
			fastaLoader.writeIndexFile();
			fastaLoader.readIndexFile();
			Map<String, Long> indexMap2 = fastaLoader.getIndexMap();
			assertEquals(indexMap1, indexMap2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testgetProteinFromFasta(){
		try {
			Protein proteinFirst = fastaLoader.getProteinFromFasta("P05100");
			Protein proteinMiddle = fastaLoader.getProteinFromFasta("B1XDP2");
			Protein proteinLast = fastaLoader.getProteinFromFasta("A7ZLN7");
	
			// ProteinFirst
			assertEquals("3MG1_ECOLI DNA-3-methyladenine glycosylase 1 OS=Escherichia coli (strain K12) GN=tag PE=1 SV=1", proteinFirst.getHeader().getDescription());
			assertEquals("MERCGWVSQDPLYIAYHDNEWGVPETDSKKLFEMICLEGQQAGLSWITVLKKRENYRACFHQFDPVKVAAMQEEDVERLVQDAGIIRHRGKIQAIIGNARAYLQMEQNGEPFVDFVWSFVNHQPQVTQATTLSEIPTSTSASDALSKALKKRGFKFVGTTICYSFMQACGLVNDHVVGCCCYPGNKP", proteinFirst.getSequence().getSequence());
			
			// ProteinMiddle
			assertEquals("AAS_ECODH Bifunctional protein aas OS=Escherichia coli (strain K12 / DH10B) GN=aas PE=3 SV=1", proteinMiddle.getHeader().getDescription());
			assertEquals("MLFSFFRNLCRVLYRVRVTGDTQALKGERVLITPNHVSFIDGILLGLFLPVRPVFAVYTSISQQWYMRWLKSFIDFVPLDPTQPMAIKHLVRLVEQGRPVVIFPEGRITTTGSLMKIYDGAGFVAAKSGATVIPVRIEGAELTHFSRLKGLVKRRLFPQITLHILPPTQVAMPDAPRARDRRKIAGEMLHQIMMEARMAVRPRETLYESLLSAMYRFGAGKKCVEDVNFTPDSYRKLLTKTLFVGRILEKYSVEGERIGLMLPNAGISAAVIFGAIARRRMPAMMNYTAGVKGLTSAITAAEIKTIFTSRQFLDKGKLWHLPEQLTQVRWVYLEDLKADVTTADKVWIFAHLLMPRLAQVKQQPEEEALILFTSGSEGHPKGVVHSHKSILANVEQIKTIADFTTNDRFMSALPLFHSFGLTVGLFTPLLTGAEVFLYPSPLHYRIVPELVYDRSCTVLFGTSTFLGHYARFANPYDFYRLRYVVAGAEKLQESTKQLWQDKFGLRILEGYGVTECAPVVSINVPMAAKPGTVGRILPGMDARLLSVPGIEEGGRLQLKGPNIMNGYLRVEKPGVLEVPTAENVRGEMERGWYDTGDIVRFDEQGFVQIQGRAKRFAKIAGEMVSLEMVEQLALGVSPDKVHATAIKSDASKGEALVLFTTDNELTRDKLQQYAREHGVPELAVPRDIRYLKQMPLLGSGKPDFVTLKSWVDEAEQHDE", proteinMiddle.getSequence().getSequence());
			
			// ProteinLast
			assertEquals("ABDH_ECO24 Gamma-aminobutyraldehyde dehydrogenase OS=Escherichia coli O139:H28 (strain E24377A / ETEC) GN=prr PE=3 SV=1", proteinLast.getHeader().getDescription());
			assertEquals("MQHKLLINGELVSGEGEKQPVYNPATGDVLLEIAEASAEQVDAAVRAADAAFAEWGQTTPKVRAECLLKLADVIEENGQVFAELESRNCGKPLHSAFNDEIPAIVDVFRFFAGAARCLNGLAAGEYLEGHTSMIRRDPLGVVASIAPWNYPLMMAAWKLAPALAAGNCVVLKPSEITPLTALKLAELAKDIFPAGVINVLFGRGKTVGDPLTGHPKVRMVSLTGSIATGEHIISHTASSIKRTHMELGGKAPVIVFDDADIEAVVEGVRTFGYYNAGQDCTAACRIYAQKGIYDTLVEKLGAAVATLKSGAPDDESTELGPLSSLAHLERVSKAVEEAKATGHIKVITGGEKRKGNGYYYAPTLLAGALQDDAIVQKEVFGPVVSVTPFDNEEQVVNWANDSQYGLASSVWTKDVGRAHRVSARLQYGCTWVNTHFMLVSEMPHGGQKLSGYGKDMSLYGLEDYTVVRHVMVKH", proteinLast.getSequence().getSequence());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
}
