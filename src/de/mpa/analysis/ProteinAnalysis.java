package de.mpa.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.mpa.algorithms.quantification.QuantMethod;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.util.Formatter;

/**
 * Helper class containing various protein-specific calculations.
 */
public class ProteinAnalysis {

	/**
	 * Calculates the molecular weight of a protein.
	 * @param The protein hit whose weight shall be calculated.
	 */
	public static double calculateMolecularWeight(ProteinHit proteinHit) {
		// Get the masses with the amino acid masses.
		Map<String, Double> masses = Masses.getInstance();

		// Start with the N-terminal mass
		double molWeight = Masses.N_term;

		// Get the protein sequence.
		String sequence = proteinHit.getSequence();

		// Iterate the protein sequence and add the molecular masses.
		for (char letter : sequence.toCharArray()) {
//			// Skip the wildcard amino acid.
//			if (letter != '*') {
//				molWeight += masses.get(String.valueOf(letter));
//			}
			Double aaWeight = masses.get(String.valueOf(letter));
			if (aaWeight != null) {
				molWeight += aaWeight;
			} else {
				System.out.println(letter);
			}
		}

		// Add the C-terminal mass.
		molWeight += Masses.C_term;

		// Get the weight in kDa
		molWeight = Formatter.roundDouble((molWeight / 1000.0), 3);

		return molWeight;
	}

	/**
	 * Calculates the sequence coverage of a protein hit with respect to its containing peptides. 
	 * Multiple occurences of a single peptide will be counted as a single occurence.
	 * @param proteinHit The protein hit whose coverage shall be calculated.
	 */
	public static double calculateSequenceCoverage(ProteinHit proteinHit) {
		return calculateSequenceCoverage(proteinHit, true);
	}

	/**
	 * Calculates the sequence coverage of a protein hit with respect to its containing peptides.
	 * @param proteinHit The protein hit whose coverage shall be calculated.
	 * @param hitsCoveredOnlyOnce Flag determining whether peptides are counted only once in a protein with repeats.
	 */
	public static double calculateSequenceCoverage(ProteinHit proteinHit, boolean hitsCoveredOnlyOnce) {
		// The Protein sequence.
		String sequence = proteinHit.getSequence();
		boolean[] foundAA = new boolean[sequence.length()];
		List<PeptideHit> peptides = proteinHit.getPeptideHitList();
		String pSequence;
		// Iterate the peptides in the protein.
		for (PeptideHit peptideHit : peptides) {
			// replacement of PTMs for the calculation of the sequence coverage
			//			System.out.println(peptideHit.getSequence());
			pSequence = peptideHit.getSequence().replaceAll("[^A-Z]", "");
			//			System.out.println(pSequence);
			peptideHit.setSequence(pSequence);
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
		double coverage = ((double) nCoveredAA / (double) sequence.length());

		return Formatter.roundDouble(coverage, 6);
	}

	/**
	 * Calculates the isoelectric point of the specified protein.
	 * @param proteinHit The protein.
	 * @return The isoelectric point.
	 * //http://scansite.mit.edu/cgi-bin/calcpi pI
	 */
	public static double calculateIsoelectricPoint(ProteinHit proteinHit) {
		// Get pKas from amino acids
		char[] aa = proteinHit.getSequence().toCharArray();
		List<Double> pKaListAcidicNeg = new ArrayList<Double>();
		List<Double> pKaListBasicPos = new ArrayList<Double>();
		Double pKa;
		for (int i = 0; i < aa.length; i++) {
			if (i == 0){
				pKa = IsoelectricPoints.pKaNtermMap.get(aa[i]);
				if(pKa != null) {
					pKaListBasicPos.add(pKa);
				}
			}
			
			if (i == aa.length - 1) {
				pKa = IsoelectricPoints.pKaCtermMap.get(aa[i]);
				if (pKa != null) {
					pKaListAcidicNeg.add(pKa);
				}
			}

			pKa = IsoelectricPoints.pKaSideChainMap.get(aa[i]);
			if (pKa != null) {
				if ((aa[i] == 'D') || (aa[i] == 'E') || (aa[i] == 'C') || (aa[i] == 'Y')) 
				{
					pKaListAcidicNeg.add(pKa);
				}
				if ((aa[i] == 'H') || (aa[i] == 'K') || (aa[i] == 'R')) {
					pKaListBasicPos.add(pKa);
				}
			}
		}
			// calculate charge of protein
			double pHMin = 0.0;
			double pHMax = 14.0;
			double pH = 0.0;
			int loops = 2000;
			double epsilon = 0.0001;
			double netCharge = 1.0;
			int actualLoop = 1;

			// Search for min charge according to Newton
			while (((pHMax - pHMin) > epsilon) && (actualLoop < loops)) {
				// Iteration steps
				actualLoop++;
				// set pH
				pH = pHMin + (pHMax - pHMin) / 2;
				double chargeAcidicAA = 0.0;
				double chargeBasicAA  = 0.0;
				for (Double pKaValue : pKaListAcidicNeg) {
					// Acidic amino acids
//					double charge = -1 / ( Math.exp(pKaValue - pH)  + 1 );
					double charge = -1 / ( Math.pow(10,(pKaValue - pH))  + 1 );
					chargeAcidicAA += charge;
				}
				
				for (Double pKaValue : pKaListBasicPos) {
					// Basic amino acids
//					double charge = 1 / ( Math.exp( pH - pKaValue ) + 1);
					double charge = 1 / ( Math.pow(10, ( pH - pKaValue )) + 1);
					chargeBasicAA += charge;
				}
				
				// Calculate charge
				netCharge = chargeAcidicAA + chargeBasicAA;
				// Set new pH
				if (netCharge > 0.0) {
					pHMin = pH;
				} else {
					pHMax = pH;
				}
			}		
			return pH;
		}

		/**
		 * Calculates label-free quantification measures.
		 * @param qm The quantification method object.
		 * @param params Variable argument list of parameters.
		 * @return The result of the quantification calculation.
		 */
		public static double calculateLabelFree(QuantMethod qm, Object... params) {
			qm.calculate(params);
			return qm.getResult();
		}



	}
