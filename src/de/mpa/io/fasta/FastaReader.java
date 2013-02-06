package de.mpa.io.fasta;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class to create the Uniprot maps for peptides and accession
 * @author R. Heyer
 */
public class FastaReader {

	/**
	 * The file of the fasta database.
	 */
	private File file;

	/**
	 * The map with identifier as key and sequence as value for test purposes.
	 */
	private Map<String,String> proteinMap = new TreeMap<String,String>();

	/**
	 * The length of the protein
	 */
	private int proteinLength;

	/**
	 * Object to digest a protein to peptides.
	 */
	private Digester digester;

	/**
	 * List of peptides after regex.
	 */
	private List<String> peptides = new ArrayList<String>();

	/**
	 * Number of peptides after RegEX.
	 */
	private int peptideCount;

	/**
	 * List of peptides after compomics trypsin
	 */
	private List<String> compPeptides;

	/**
	 * Number of peptides after compomics
	 */
	private int compPeptidesCount;

	/**
	 * Constructor with file.	
	 * @param file
	 */
	public FastaReader(File file){
		this.file = file;
	}

	/**
	 * Method to read the fasta and get the entries
	 * @param file
	 * @return 
	 * @throws IOException 
	 */
	public Map<String, String> readFasta() throws IOException{

	
		// The file reader
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);

		// Go through the fasta file
		String line = br.readLine(); 
		StringBuilder proteinSeq = new StringBuilder();		// The amino Acid sequence of the protein
		proteinSeq.setLength(0);
		long index = 0;						// Index to show number of proteins
		String identifier = null;			// Accession of the protein 

		// Iterate through whole document
		while (line!= null) {
			// Begin of protein entry
			if (line.startsWith(">")) {
				index++; 
				identifier = line.substring(line.indexOf('|') + 1,line.indexOf('|', line.indexOf('|') + 1));
				proteinSeq.setLength(0);
			}else{
				// StringBuilder append amino acids to protein sequences
				proteinSeq.append(line);
			}

			// Read next line
			line =  br.readLine();
			// Check whether it was the end of the protein
			if ((line == null ) || (line.startsWith(">"))) {
				proteinMap.put(identifier, proteinSeq.toString());
			}
		}
		return proteinMap;
	}

	/**
	 * Method to read the fasta and get the entries
	 * @param file
	 * @throws IOException 
	 */
	public  void calculateNSAFDominator() throws IOException{

		// NSAF denominator
		double denominator = 0.0;
		
		// The file reader
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);

		digester = new Digester();
		// Go through the fasta file
		String line = br.readLine(); 
		StringBuilder proteinSeq = new StringBuilder();		// The amino Acid sequence of the protein
		proteinSeq.setLength(0);
		long index = 0;						// Index to show number of proteins
		String identifier = null;			// Accession of the protein 

		// Iterate through whole document
		while (line!= null) {
			// Begin of protein entry
			if (line.startsWith(">")) {
				index++; 
				identifier = line.substring(line.indexOf('|') + 1,line.indexOf('|', line.indexOf('|') + 1));
				proteinSeq.setLength(0);
			}else{
				// StringBuilder append amino acids to protein sequences
				proteinSeq.append(line);
			}

			// Read next line
			line =  br.readLine();
			// Check whether it was the end of the protein
			if ((line == null ) || (line.startsWith(">"))) {
				proteinLength = proteinSeq.length();
				// RegEX
				String seq = proteinSeq.toString();

				// Compomics
				compPeptides = digester.digestCompomics("Trypsin", "RK", "", "", "P", seq, 1);
				compPeptidesCount =  compPeptides.size();
				// Calculate value for NSAF
				denominator = denominator +  1.0 * compPeptidesCount/ seq.length();
				System.out.println(index);
			}
		}

		System.out.println("finished: " + index + "protein Entries");
		System.out.println("NSAF  denominator: " + denominator);
		
	}
}