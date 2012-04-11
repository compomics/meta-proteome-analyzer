package de.mpa.analysis;

import java.util.List;
import java.util.Map;

import no.uib.jsparklines.renderers.util.Util;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.util.Formatter;

public class ProteinAnalysis {
	
	/**
	 * The Proteinhit instance.
	 */
	private ProteinHit proteinHit;
	
	/**
	 * This variable determines whether peptides are counted only once in a protein with repeats.
	 */
	private boolean hitsCoveredOnlyOnce;
	
	/**
	 * Default constructor for the ProteinAnalysis which does the calculation of the sequence coverage, the molecular weight and the pI.
	 * The peptides are counted only once by once in a protein with repeating peptide sequences.
	 * @param proteinHit The ProteinHit instance.
	 */
	public ProteinAnalysis(ProteinHit proteinHit) {
		this(proteinHit, true);
	}
	
	/**
	 * The ProteinAnalysis does the calculation of the sequence coverage, the molecular weight and the pI.
	 * @param proteinHit The ProteinHit instance.
	 * @param hitsCoveredOnlyOnce Flag if peptides are counted only once in a protein with repeats.
	 */
	public ProteinAnalysis(ProteinHit proteinHit, boolean hitsCoveredOnlyOnce) {
		this.proteinHit = proteinHit;
		this.hitsCoveredOnlyOnce = hitsCoveredOnlyOnce;
		calculateSequenceCoverage();
		calculateMolecularWeight();
	}
	
	/**
	 * Calculates the molecular weight 
	 */
	private void calculateMolecularWeight(){
		// Get the masses with the amino acid masses.
		Map<String, Double> masses = Masses.getMap();
		
		// Start with the N-terminal mass
		double molWeight = Masses.N_term;
		
		// Get the protien sequence.
		String sequence = proteinHit.getSequence();
		
		// Iterate the protein sequence and add the molecular masses.
	    for (int i = 0; i < sequence.length(); i++) {
	            char letter = sequence.charAt(i);
	            String aa = String.valueOf(letter);
	            // Skip the wildcard amino acid.
                if (letter != '*') {
                    molWeight += masses.get(aa);
                }
        }
	    
	    // Add the C-terminal mass.
	    molWeight += Masses.C_term;
	    
	    // Get the weight in kDa
	    molWeight = Util.roundDouble((molWeight / 1000.0), 3);
	    proteinHit.setMolWeight(molWeight);
	}
	
	/**
	 * Calculates the sequence coverage for a protein with the containing peptides.
	 */
	private void calculateSequenceCoverage(){
		// The Protein sequence.
		String sequence = proteinHit.getSequence();
		boolean [] foundAA = new boolean[sequence.length()];
		List<PeptideHit> peptides = proteinHit.getPeptideHitList();
		
		// Iterate the peptides in the protein.
		for (PeptideHit peptideHit : peptides) {
			// Indices for the pattern
			int startIndex = 0;
			int endIndex = 0;
			// The pattern == The peptide sequence
			String pattern = peptideHit.getSequence();
			
			// Iterate the protein sequence and check for pattern. 
			while(sequence.indexOf(pattern, startIndex) != -1){
				
				// Search for multiple hits
				startIndex = sequence.indexOf(pattern, startIndex);
				peptideHit.setStart(startIndex);
				endIndex = startIndex + pattern.length();
				peptideHit.setEnd(endIndex);
				
				// Set the found amino acid sites in the protein to true.
				for (int i = startIndex; i < endIndex; i++) {
					foundAA[i] = true;
				}
				startIndex++;
				
				// Search only once or not
				if(hitsCoveredOnlyOnce){
					break;
				}
			}
			proteinHit.addPeptideHit(peptideHit);
		}
		
		// Number of covered amino acids.
		int nCoveredAA = 0;
		
		// Get the number of covered amino acids.
		for (boolean aa : foundAA) {
			if(aa){
				nCoveredAA++;
			}
		}
		double coverage = ((double) nCoveredAA / (double) sequence.length()) * 100.0 ;
		proteinHit.setCoverage(Formatter.roundDouble(coverage, 2));
	}
	
	/**
	 * Returns the ProteinHit instance which contains all calculated values.
	 * @return The ProteinHit instance with the calculated values.
	 */
	public ProteinHit getProteinHit() {
		return proteinHit;
	}
	
	
}
