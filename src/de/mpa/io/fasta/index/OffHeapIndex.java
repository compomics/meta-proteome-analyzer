package de.mpa.io.fasta.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import com.compomics.util.protein.Header;

/**
 * The OffHeapIndex constructs a file-based mapping database and stores
 * peptide string entries together with its respective protein accessions. 
 * 
 * @author Thilo Muth
 *
 */
public class OffHeapIndex extends StandardIndex implements DatabaseIndex {
	
	/**
	 * The DB instance.
	 */
	private DB mapDb;
	
	/**
	 * The peptide string index is based on lock-free concurrent B-Linked-Tree.
	 */
	private Map<String, Set<String>> peptideIndex;
	
	/**
	 * Constructs an off-heap index on the basis of peptide strings mapping to sets of protein accessions.
	 * 
 	 * @param fastaFile 		the FASTA database file
	 * @param nMissedCleavages 	the number of missed cleavages
	 * @throws IOException
	 */
	public OffHeapIndex(File fastaFile, int missedCleavages) throws IOException {
		super(fastaFile, missedCleavages);
		this.setupDatabase();
	}
	
	/**
	 * This method contructs the file-based database, which holds peptide strings as keys and a string array of proteins accessions as values. 
	 * @throws IOException 
	 */
	private void setupDatabase() throws IOException {
		File file = new File(fastaFile.getAbsolutePath() + ".db");
		boolean dbExists = file.exists();
		mapDb = DBMaker.newFileDB(file).transactionDisable().closeOnJvmShutdown().mmapFileEnableIfSupported().make();
		peptideIndex = mapDb.createTreeMap("peptideToProteins").makeOrGet();
		
		// If database does not exist, create a new index.
		if(!dbExists) {
			generateIndex();
		}
	}
	
	/**
	 * Indexes the FASTA file by retrieving every entry and creating an off-heap collection (using MapDB) of tags mapping to protein accessions.
	 * @throws IOException 
	 */
	public void generateIndex() throws IOException {
		final BufferedReader reader = new BufferedReader(new FileReader(fastaFile));
		String nextLine = null;
		boolean firstline = true;
		String header = null;
		StringBuffer stringBf = new StringBuffer();
		while ((nextLine = reader.readLine()) != null) {
			if (!nextLine.isEmpty() && nextLine.charAt(0) == '>') {
				if (firstline) {
					header = nextLine.trim();
					firstline = false;
				} else {			
					addToIndex(header, stringBf.toString());
					stringBf = new StringBuffer();
					header = nextLine.trim();
				}
			} else {
				stringBf.append(nextLine.trim());
			}
		}
		addToIndex(header, stringBf.toString());
		reader.close();
	}
	
	/**
	 * Adds respective tags and the protein to the index.
	 * @param accession	Protein	header
	 * @param sequence	Protein	sequence
	 */
	private void addToIndex(String header, String sequence) {
		// Digest peptides with tryptic cleavage.		
		List<String> peptides = performTrypticCleavage(sequence, 6, 40);
		String accession = Header.parseFromFASTA(header).getAccession();
		
		// Iterate all digested peptides and add them to the index (including protein accessions).
		for (String peptideSequence : peptides) {
			Set<String> proteins = getProteins(peptideSequence);
			proteins.add(accession);
			peptideIndex.put(peptideSequence, proteins);
		}
		nProteins++;
	}
	
	/**
	 * This method retrieves the proteins for each peptide.
	 * 
	 * @param peptideSequence	The peptide sequence
	 * @return Set of protein accessions
	 */
	public Set<String> getProteins(String peptideSequence) {
		if (peptideIndex.isEmpty()) {
			return new HashSet<>();
		} else {
			if (!peptideIndex.containsKey(peptideSequence)) {
				return new HashSet<>();
			}
			return new HashSet<>(this.peptideIndex.get(peptideSequence));
		}
	}
	
	/**
	 * Returns the peptide index (based on a tree map). 
	 * @return The peptide index
	 */
	public Map<String, Set<String>> getPeptideIndex() {
		return peptideIndex;
	}
}
