package de.mpa.fastaLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import de.mpa.io.fasta.FastaLoader;


/**
 * Class to compare different fasta db's
 * @author R. Heyer
 */
public class CompareFastaDBTest extends TestCase{
	String path 		= "Z:\\bpt\\bptprot\\MetaProteomeAnalyzer\\databases\\";
	private long time_start;

	@Before
	public void setUp(){
		// Protip: use System.currentTimeMillis() and calculate deltas to determine how much time has passed
		time_start = new Date().getTime(); 
		System.out.println("Time :" + (time_start / 1000 / 60) + " min");
		writeUniProtIndexFile();
		
		time_start= new Date().getTime(); 
		System.out.println("Time :" + (time_start / 1000 / 60) + "min");
//		writeSwissProtIndexFile();
		 
		time_start= new Date().getTime(); 
		System.out.println("Time :" + (time_start / 1000 / 60) + "min");
//		writeNCBInrIndexFile();
		time_start= new Date().getTime(); 
		System.out.println("Time :" + (time_start / 1000 / 60) + "min");
	}

	@Test
	public void testIrgendwas(){
		assertEquals(true, true);
	}


	/**
	 * Method to write UniProt index Files
	 */
	private void writeUniProtIndexFile() {
		// Load Fasta DBs and write index Files
		FastaLoader uniProtFastaLoader = FastaLoader.getInstance();
		uniProtFastaLoader.setFastaFile(new File(path +  "uniprot_sprot.fasta"));
		try {
			System.out.println("Load UniProt...");
			uniProtFastaLoader.loadFastaFile();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("UniProt finished");

		try {
			uniProtFastaLoader.writeIndexFile();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * Method to write Swissprot index Files
	 */
	private void writeSwissProtIndexFile() {
		// Load Fasta DBs and write index Files
		FastaLoader uniProtFastaLoader = FastaLoader.getInstance();
		uniProtFastaLoader.setFastaFile(new File(path + "swissprot.fasta"));
		try {
			System.out.println("Load SwissProt...");
			uniProtFastaLoader.loadFastaFile();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("SwissProt finished");
				try {
			uniProtFastaLoader.writeIndexFile();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * Method to write NCBInr index Files
	 */
	private void writeNCBInrIndexFile() {
		// Load Fasta DBs and write index Files
		FastaLoader uniProtFastaLoader = FastaLoader.getInstance();
		uniProtFastaLoader.setFastaFile(new File(path + "NCBInr.fasta"));
		try {
			System.out.println("Load NCBInr...");
			uniProtFastaLoader.loadFastaFile();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		System.out.println("NCBInr finished");
				try {
			uniProtFastaLoader.writeIndexFile();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}
