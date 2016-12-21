package de.mpa.fastadigest;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

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
	public void testreadingpepfile() {
		String inSequence = "MAFSAEDVLK";
		String inFile = "/scratch/metaprot/data/fasta/TestDB.pep";
		PeptideDigester digester = new PeptideDigester();
		
		@SuppressWarnings("static-access")
		HashSet<String> accessionSet = digester.fetchProteinsFromPeptideSequence(inSequence, inFile);
		
		System.out.println(accessionSet.size());
		System.out.println(accessionSet.toString());

	}	
	@Test
	public void testDBcreation() {
		
		PeptideDigester digester = new PeptideDigester();
		String [] dbFiles = {"/scratch/metaprot/data/fasta/DatabaseKay.fasta"};
		String outFile = "/scratch/metaprot/data/fasta/DatabaseKay.pep";
		
		digester.createPeptidDB(dbFiles, outFile, 1, 1, 50);		
				
	}
	
	@Test
	public void testMyIgnorance() {
		String AllAminoAcids = "GPAVLIMCFYWHKRQNEDSTJOX*";
		String pep = "VQ";
		
		List<String> FileExtensions = new ArrayList<String>(); 
		for(char AminoAcid1 : AllAminoAcids.toCharArray()) {		
			for(char AminoAcid2 : AllAminoAcids.toCharArray()) {				
				FileExtensions.add(Character.toString(AminoAcid1)+Character.toString(AminoAcid2));				
			}
		}
		int AA;
		if (pep.length() != 1) {
			AA = FileExtensions.indexOf(pep.subSequence(0, 2));
		}
		else {									
			AA = FileExtensions.indexOf(pep.subSequence(0, 2));									
		}
		System.out.print(AA);	
				
		//for(String c : FileExtensions) { 
		//	int index = FileExtensions.indexOf(c);
		//	System.out.println(c);
		//	System.out.println(index);
		//}
		
	}
	@Test
	public void testTrypticDigestWithoutMissedCleavages() {
		
		List<String> result = PeptideDigester.trypticCleave(sequence, 0, 5, 20);
		assertEquals("24 peptides found for Trypsin and Ubiquitin", 24, result.size());

		
		
	}
	
	@Test
	public void testTrypticDigestWithMissedCleavages() {
		
		List<String> result = PeptideDigester.trypticCleave(sequence, 0, 5, 20);
		assertEquals("54 peptides found for Trypsin and Ubiquitin with up to one missed cleavage", 54, result.size());
		
	}
	
}
