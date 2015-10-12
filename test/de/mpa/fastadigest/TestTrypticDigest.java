package de.mpa.fastadigest;

import java.util.List;

import org.junit.*;
import static org.junit.Assert.*;

/**
 * Test the resulting peptide sequences from in-silico digestion of ubiquitin with trypsin
 */
public class TestTrypticDigest {

	String header;
	String sequence;
	
	@Before
	public void setup() {
		
		// Ubiquitin fasta entry 
		header = ">sp|P0CG47|UBB_HUMAN Polyubiquitin-B OS=Homo sapiens GN=UBB PE=1 SV=1";
		sequence = 	"MQIFVKTLTGKTITLEVEPSDTIENVKAKIQDKEGIPPDQQRLIFAGKQLEDGRTLSDYN" +
					"IQKESTLHLVLRLRGGMQIFVKTLTGKTITLEVEPSDTIENVKAKIQDKEGIPPDQQRLI" +
					"FAGKQLEDGRTLSDYNIQKESTLHLVLRLRGGMQIFVKTLTGKTITLEVEPSDTIENVKA" +
					"KIQDKEGIPPDQQRLIFAGKQLEDGRTLSDYNIQKESTLHLVLRLRGGC";
	}
	
	@Test
	public void testTrypticDigestWithoutMissedCleavages() {
		
		List<String> result = PeptideDigester.trypticCleave(sequence, 0, 5, 20);
		assertEquals("24 peptides found for Trypsin and Ubiquitin", 24, result.size());
		
	}
	
	@Test
	public void testTrypticDigestWithMissedCleavages() {
		
		List<String> result = PeptideDigester.trypticCleave(sequence, 1, 5, 20);
		assertEquals("54 peptides found for Trypsin and Ubiquitin with up to one missed cleavage", 54, result.size());
		
	}
	
}
