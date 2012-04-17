package de.mpa.analysis;

import java.util.List;
import java.util.Map;

import no.uib.jsparklines.renderers.util.Util;
import de.mpa.algorithms.quantification.QuantMethod;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.util.Formatter;

public class ProteinAnalysis {
	/**
	 * Calculates the molecular weight of a protein.
	 * @param The protein hit whose weight shall be calculated.
	 */
	public static void calculateMolecularWeight(ProteinHit proteinHit) {
		// Get the masses with the amino acid masses.
		Map<String, Double> masses = Masses.getMap();
		
		// Start with the N-terminal mass
		double molWeight = Masses.N_term;
		
		// Get the protien sequence.
		String sequence = proteinHit.getSequence();
		
		// Iterate the protein sequence and add the molecular masses.
		for (char letter : sequence.toCharArray()) {
			// Skip the wildcard amino acid.
			if (letter != '*') {
				molWeight += masses.get(String.valueOf(letter));
			}
		}
	    
	    // Add the C-terminal mass.
	    molWeight += Masses.C_term;
	    
	    // Get the weight in kDa
	    molWeight = Util.roundDouble((molWeight / 1000.0), 3);
	    proteinHit.setMolWeight(molWeight);
	}
	
	
	
	/**
	 * Calculates the sequence coverage of a protein hit with respect to its containing peptides. 
	 * Multiple occurences of a single peptide will be counted as a single occurence.
	 * @param proteinHit The protein hit whose coverage shall be calculated.
	 */
	public static void calculateSequenceCoverage(ProteinHit proteinHit) {
		calculateSequenceCoverage(proteinHit, true);
	}
	
	/**
	 * Calculates the sequence coverage of a protein hit with respect to its containing peptides.
	 * @param proteinHit The protein hit whose coverage shall be calculated.
	 * @param hitsCoveredOnlyOnce Flag determining whether peptides are counted only once in a protein with repeats.
	 */
	public static void calculateSequenceCoverage(ProteinHit proteinHit, boolean hitsCoveredOnlyOnce) {
		// The Protein sequence.
		String sequence = proteinHit.getSequence();
		boolean[] foundAA = new boolean[sequence.length()];
		List<PeptideHit> peptides = proteinHit.getPeptideHitList();

		// Iterate the peptides in the protein.
		for (PeptideHit peptideHit : peptides) {
			// Indices for the pattern
			int startIndex = 0;
			int endIndex = 0;
			// The pattern == The peptide sequence
			String pattern = peptideHit.getSequence();

			// Iterate the protein sequence and check for pattern.
			while (sequence.indexOf(pattern, startIndex) != -1) {

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
				if (hitsCoveredOnlyOnce) {
					break;
				}
			}
			proteinHit.addPeptideHit(peptideHit);
		}

		// Number of covered amino acids.
		int nCoveredAA = 0;

		// Get the number of covered amino acids.
		for (boolean aa : foundAA) {
			if (aa) {
				nCoveredAA++;
			}
		}
		double coverage = ((double) nCoveredAA / (double) sequence.length()) * 100.0 ;
		proteinHit.setCoverage(Formatter.roundDouble(coverage, 4));
	}
	
	/**
	 * Calculates the emPAI
	 */
	public static void calculateLabelFree(QuantMethod qm, Object... params) {
		qm.calculate(params);
	}
	
	
	
}
