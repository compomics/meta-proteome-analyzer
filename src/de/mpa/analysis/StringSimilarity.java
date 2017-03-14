package de.mpa.analysis;

import java.util.Collection;

/**
 * Class to calculate peptide similarity
 * @author R. Heyer
 *
 */
public class StringSimilarity {

	/**
	 * Prooves whether the peptide sets have similar peptides, but not equal peptides.
	 * @return True if similar peptides exist / False no similar peptides 
	 */
	/**
	 * Prooves whether the peptide sets have similar peptides, but not equal peptides.
	 * @param pepSeqsA. The first peptide set.
	 * @param pepSeqsB. The second peptide set.
	 * @param threshold. Threshold of the levensthein distance (smaller as threshold)
	 * @return True if similar peptides exist / False no similar peptides.
	 */
	public static boolean similarPeptides(Collection<String> pepSeqsA, Collection<String> pepSeqsB, int threshold){
		// Analysis the similarity based on Levensthin distance, but ignores misscleavages.
		for (String peptide1 : pepSeqsA) {
			if (peptide1.split("(?<=[RK])(?=[^P])").length <2) {
				for (String peptide2 : pepSeqsB) {
					if (peptide2.split("(?<=[RK])(?=[^P])").length <2) {
						int levDistance = calculateLevenshteinDistance(peptide1, peptide2);
						if(( levDistance > 0) && (levDistance <= threshold)){return true;}
					}
				}

			}
		}
		return false;
	}

	
	/**
	 * Calculate Levensthin Distance: CODE from
	 * http://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance
	 * @param s0 String 1. 
	 * @param s1 String 2.
	 * @return The similarity based on LevenshteinDistance
	 */
	public static int calculateLevenshteinDistance (String s0, String s1) {
		int len0 = s0.length()+1;
		int len1 = s1.length()+1;
	 
		// the array of distances
		int[] cost = new int[len0];
		int[] newcost = new int[len0];
	 
		// initial cost of skipping prefix in String s0
		for(int i=0;i<len0;i++) cost[i]=i;
	 
		// dynamicaly computing the array of distances
	 
		// transformation cost for each letter in s1
		for(int j=1;j<len1;j++) {
	 
			// initial cost of skipping prefix in String s1
			newcost[0]=j-1;
	 
			// transformation cost for each letter in s0
			for(int i=1;i<len0;i++) {
	 
				// matching current letters in both strings
				int match = (s0.charAt(i-1)==s1.charAt(j-1))?0:1;
	 
				// computing cost for each transformation
				int cost_replace = cost[i-1]+match;
				int cost_insert  = cost[i]+1;
				int cost_delete  = newcost[i-1]+1;
	 
				// keep minimum cost
				newcost[i] = Math.min(Math.min(cost_insert, cost_delete),cost_replace );
			}
	 
			// swap cost/newcost arrays
			int[] swap=cost; cost=newcost; newcost=swap;
		}
	 
		// the distance is the cost for transforming all letters in both strings
//		System.out.println(cost[len0-1]);
		return cost[len0-1];
		
	}
}
