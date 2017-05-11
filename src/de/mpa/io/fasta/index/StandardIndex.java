package de.mpa.io.fasta.index;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StandardIndex {
	
	/**
	 * FASTA protein database.
	 */
	protected final File fastaFile;
	
	/**
	 * Number of maximum allowed missed cleavages. 
	 */
	protected final int nMissedCleavages;
	
	/**
	 * Number of proteins which have been indexed.
	 */
	protected int nProteins = 0;
	
	/**
	 * StandardIndex constructor taking FASTA file and number of missed cleavages as input.
	 * @param fastaFile 		FASTA file instance
	 * @param missedCleavages	No. of maximum allowed missed cleavages
	 */
	public StandardIndex(File fastaFile, int missedCleavages) {
		this.fastaFile = fastaFile;
		this.nMissedCleavages = missedCleavages;
	}
	
	/**
	 * Perform tryptic cleavage on a protein sequence with a specified minimum and maximum length.
	 * @param proteinSequence 	The protein sequence
	 * @param minLength 		Minimum length
	 * @param maxLength			Maximum length
	 * @return List of tryptically digested peptide sequence strings.
	 */
	protected List<String> performTrypticCleavage(String proteinSequence, int minLength, int maxLength) {
		// Cleavage rules for tryptic digestion
		String[] cleavagesPeptides = proteinSequence.split("(?<=[RK])(?!=[P])");
		String first = cleavagesPeptides[0].substring(1);
		List<String> list = new ArrayList<>(Arrays.asList(cleavagesPeptides));
		
		// Add peptide with removed methionine.
		list.add(first);
		if (cleavagesPeptides.length > 1) {
			String second = cleavagesPeptides[1];
			list.add(second);
		}
		cleavagesPeptides = new String[list.size()];
		list.toArray(cleavagesPeptides);
		
	    Set<String> peptides = new HashSet<String>();
		// Iterate the digested peptides.
		for (int i = 0; i < cleavagesPeptides.length; i++) {
			if (cleavagesPeptides[i].length()>=minLength && cleavagesPeptides[i].length()<=maxLength) {
				peptides.add(cleavagesPeptides[i]);
			}
		}
		
		// Loop of missed cleavages (mc)
		for (int mc = 1; mc <= nMissedCleavages; mc++) {
			for (int i = 0; i < cleavagesPeptides.length-mc; i++) {
				// Build a concatenated sequences from neighbours
				StringBuilder builder = new StringBuilder(cleavagesPeptides[i]);
				
				for (int j = 1; j <= mc; j++) {
					builder.append(cleavagesPeptides[i+j]);
					String newSequence = builder.toString();
					if (newSequence.length()>=minLength &&	newSequence.length()<=maxLength) {
						peptides.add(newSequence);
					}
				}
			}
		}
		return new ArrayList<>(peptides);
	}
	
	/**
	 * Number of indexed proteins.
	 * @return Number of indexed proteins
	 */
	public int getNumberOfIndexedProteins() {
		return nProteins;
	}
}
