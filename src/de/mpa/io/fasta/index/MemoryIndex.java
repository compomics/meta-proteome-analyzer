package de.mpa.io.fasta.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.compomics.util.protein.Header;

/**
 * <b>MemoryIndex</b>
 * <p>
 * This class allows to index a FASTA database file using memory-based mappings.
 * In addition, query strings (e.g. peptide tags) can be searched against the generated index.
 * </p>
 * 
 * @author Thilo Muth
 */
public class MemoryIndex extends StandardIndex implements DatabaseIndex {
	
	/**
	 * Precursor mass-based indexing of peptide digests.
	 */
	private Map<String, HashSet<PeptideDigest>> peptideIndex = null;
	
	/**
	 * Constructs an in-memory index on the basis of peptide strings mapping to peptide digest objects.
	 * 
 	 * @param fastaFile 		the FASTA database file
	 * @param nMissedCleavages 	the number of missed cleavages
	 * @throws IOException
	 */
	public MemoryIndex(File fastaFile, int missedCleavages) throws IOException {
		super(fastaFile, missedCleavages);
		this.generateIndex();
	}
	
	/**
	 * Indexes the FASTA file by retrieving every entry and creating an off-heap collection (using MapDB) of tags mapping to protein accessions.
	 * @throws IOException 
	 */
	public void generateIndex() throws IOException {
		// Initialize maps.
		peptideIndex = new HashMap<>();
		
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
	 * @param accession Protein header
	 * @param sequence Protein sequence
	 * @param tagLength Length of the peptide tag
	 */
	private void addToIndex(String header, String sequence) {
		// Digest peptides with tryptic cleavage.		
		List<String> peptides = performTrypticCleavage(sequence, 6, 40);
		String accession = Header.parseFromFASTA(header).getAccession();
		
		// Iterate all digested peptides and add them to the index (including protein accessions).
		for (String peptideSequence : peptides) {
			
			HashSet<PeptideDigest> digests;
			if (peptideIndex.get(peptideSequence) != null) {
				digests = peptideIndex.get(peptideSequence);
				
				// Iterate the set of peptide digests to check whether one already contains the suspect peptide.
				boolean peptideFound = false;
				for (PeptideDigest peptideDigest : digests) {
					if (peptideDigest.getPeptide().equals(peptideSequence)) {
						
						peptideDigest.addProteinAccession(accession);
						peptideDigest.addProteinEntry(new ProteinEntry(header, sequence));
						peptideFound = true;
					}
				}
				// Peptide not contained in the list of digests yet --> add it.
				if (!peptideFound) {
					PeptideDigest digestedPeptide = new PeptideDigest(peptideSequence, accession);
					digestedPeptide.addProteinEntry(new ProteinEntry(header, sequence));
					digests.add(digestedPeptide);
				}
			} else {
				digests = new HashSet<>();
				PeptideDigest digestedPeptide = new PeptideDigest(peptideSequence, accession);
				digestedPeptide.addProteinEntry(new ProteinEntry(header, sequence));
				digests.add(digestedPeptide);
			}
			peptideIndex.put(peptideSequence, digests);
		}
		nProteins++;
	}
	
	/**
	 * Returns the peptide index (mapping from a single peptide sequence to multiple proteins).
	 * @return peptide index
	 */
	public Map<String, HashSet<PeptideDigest>> getPeptideIndex() {
		return peptideIndex;
	}
}
