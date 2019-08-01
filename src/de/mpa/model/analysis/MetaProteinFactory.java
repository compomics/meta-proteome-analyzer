package de.mpa.model.analysis;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import de.mpa.client.Client;
import de.mpa.client.settings.ResultParameters;
import de.mpa.client.settings.ResultParameters.MetaProteinParameters;
import de.mpa.db.mysql.accessor.Taxonomy;
import de.mpa.model.dbsearch.DbSearchResult;
import de.mpa.model.dbsearch.MetaProteinHit;
import de.mpa.model.dbsearch.PeptideHit;
import de.mpa.model.dbsearch.PeptideSpectrumMatch;
import de.mpa.model.dbsearch.ProteinHit;
import de.mpa.model.dbsearch.UniProtEntryMPA;
import de.mpa.model.taxonomy.TaxonomyNode;
import de.mpa.model.taxonomy.TaxonomyUtils;
import de.mpa.model.taxonomy.TaxonomyUtils.TaxonomyDefinition;

/**
 * Factory class providing methods to merge meta-proteins and to determine
 * common properties (i.e. taxonomy).
 * 
 * @author R. Heyer, A. Behne
 */
public class MetaProteinFactory {

	// /**
	// * Enumeration holding rules for generating meta-proteins based on peptide
	// sharing.
	// * @author A. Behne
	// */
	// public enum DistPepRule {
	// NEVER("ignore, never merge") {
	// @Override
	// public boolean shouldCondense(Collection<String> pepSeqsA,
	// Collection<String> pepSeqsB, int levThreshold) {
	// // trivial case, do not condense meta-proteins
	// return false;
	// }
	// },
	// ALWAYS("ignore, always merge") {
	// @Override
	// public boolean shouldCondense(Collection<String> pepSeqsA,
	// Collection<String> pepSeqsB,int levThreshold) {
	// // trivial case, always condense meta-proteins
	// return true;
	// }
	// },
	// THRESHOLD("Do not merge, if they have similar peptides") {
	// @Override
	// public boolean shouldCondense(
	// Collection<String> pepSeqsA,
	// Collection<String> pepSeqsB, int levThreshold) {
	// // Merge meta-proteins if at least one overlapping peptide
	// // element exists (weak similarity criterion)
	// return !StringSimilarity.similarPeptides(pepSeqsA, pepSeqsB,
	// levThreshold);
	// }
	// };
	//
	// /**
	// * The name string.
	// */
	// private String name;
	//
	// /**
	// * Constructs a meta-protein generation rule using the specified name
	// string.
	// * @param name the name of the rule
	// */
	// private DistPepRule(String name) {
	// this.name = name;
	// }
	//
	// @Override
	// public String toString() {
	// return this.name;
	// }
	//
	// /**
	// * Returns whether meta-proteins represented by the two provided peptide
	// * sequence collections should be merged.
	// * @param pepSeqsA the peptide sequences of meta-protein A
	// * @param pepSeqsB the peptide sequences of meta-protein B
	// * @return <code>true</code> if the meta-proteins should be merged,
	// <code>false</code> otherwise
	// */
	// public abstract boolean shouldCondense(Collection<String> pepSeqsA,
	// Collection<String> pepSeqsB, int levThreshold);
	// }

	/**
	 * Enumeration holding rules for generating meta-proteins based on protein
	 * clusters.
	 * 
	 * @author A. Behne, R. Heyer
	 */
	public enum ClusterRule {
		NEVER("ignore, never merge") {
			@Override
			public boolean shouldCondense(MetaProteinHit rowMP, MetaProteinHit colMP) {
				// trivial case, never merge
				return false;
			}
		},
		ALWAYS("ignore, always merge") {
			@Override
			public boolean shouldCondense(MetaProteinHit rowMP, MetaProteinHit colMP) {
				// trivial case, always merge
				return true;
			}
		},
		// KO("KEGG orthology (KO)"){
		// @Override
		// public boolean shouldCondense(
		// MetaProteinHit rowMP,
		// MetaProteinHit colMP) {
		// List<String> koRow =
		// rowMP.getProteinHitList().get(0).getUniProtEntry().getKNumbers();
		// List<String> kocol =
		// colMP.getProteinHitList().get(0).getUniProtEntry().getKNumbers();
		// if (koRow != null && koRow.size() > 0 && kocol != null &&
		// kocol.size() > 0) {
		// if (koRow.get(0).equals(kocol.get(0))) {
		// return true;
		// }
		// }
		// return false;
		// }
		// },
		// eggNOG("evolutionary genealogy of genes"){
		// @Override
		// public boolean shouldCondense(
		// MetaProteinHit rowMP,
		// MetaProteinHit colMP) {
		// // trivial case, always merge
		// return true;
		// }},
		UNIREF100("in UniRef100"), 
		UNIREF90("in UniRef90 or higher"), 
		UNIREF50("in UniRef50 or higher"),;

		/**
		 * The name string.
		 */
		private final String name;

		/**
		 * Constructs a meta-protein generation rule using the specified name
		 * string.
		 * 
		 * @param name
		 *            the name of the rule
		 */
		ClusterRule(String name) {
			this.name = name;
		}

		/**
		 * Returns the non-trivial cluster rules.
		 * 
		 * @return the non-trivial cluster rules.
		 */
		public static MetaProteinFactory.ClusterRule[] getValues() {
			MetaProteinFactory.ClusterRule[] values = MetaProteinFactory.ClusterRule.values();
			return Arrays.copyOfRange(values, 2, values.length);
		}

		@Override
		public String toString() {
			return name;
		}

		/**
		 * Returns whether the provided meta-proteins should be merged.
		 * 
		 * @param mphA
		 *            meta-protein A
		 * @param mphB
		 *            meta-protein B
		 * @return <code>true</code> if the meta-proteins should be merged,
		 *         <code>false</code> otherwise
		 */
		public boolean shouldCondense(MetaProteinHit mphA, MetaProteinHit mphB) {
			UniProtEntryMPA upeA = mphA.getUniProtEntry();
			UniProtEntryMPA upeB = mphB.getUniProtEntry();
			if ((upeA == null) || upeA.getUniProtID().equals(-1L) || (upeA.getUniRefMPA() == null) || (upeB == null)
					|| upeB.getUniProtID().equals(-1L) || (upeB.getUniRefMPA() == null)) {
				return false;
			}
			String refA = null;
			String refB = null;
			switch (this) {
			case UNIREF100:
				refA = upeA.getUniRefMPA().getUniRef100();
				refB = upeB.getUniRefMPA().getUniRef100();
				if (refA.equals("EMPTY")) { refA = null; }
				if (refB.equals("EMPTY")) { refB = null; }
				break;
			case UNIREF90:
				refA = upeA.getUniRefMPA().getUniRef90();
				refB = upeB.getUniRefMPA().getUniRef90();
				if (refA.equals("EMPTY")) { refA = null; }
				if (refB.equals("EMPTY")) { refB = null; }
				break;
			case UNIREF50:
				refA = upeA.getUniRefMPA().getUniRef50();
				refB = upeB.getUniRefMPA().getUniRef50();
				if (refA.equals("EMPTY")) { refA = null; }
				if (refB.equals("EMPTY")) { refB = null; }
				break;
			case ALWAYS:
				return true;
			case NEVER:
				return false;
			default:
				return true;
			}
			return (refA != null) && (refB != null) && refA.equals(refB);
			
		}

	}
	
	
	/**
	 * Enumeration holding rules for generating meta-proteins based on peptide
	 * sharing.
	 * 
	 * @author A. Behne
	 */
	public enum PeptideRule {
		NEVER("ignore, never merge") {
			@Override
			public boolean shouldCondense(Collection<String> pepSeqsA, Collection<String> pepSeqsB) {
				// trivial case, do not condense meta-proteins
				return false;
			}
		},
		ALWAYS("ignore, always merge") {
			@Override
			public boolean shouldCondense(Collection<String> pepSeqsA, Collection<String> pepSeqsB) {
				// trivial case, always condense meta-proteins
				return true;
			}
		},
		SHARED_PEPTIDE("have at least one peptide in common") {
			@Override
			public boolean shouldCondense(Collection<String> pepSeqsA, Collection<String> pepSeqsB) {
				// Merge meta-proteins if at least one overlapping peptide
				// element exists (weak similarity criterion)
				boolean shouldCondense = false;
				for (String seqA : pepSeqsA) {
					for (String seqB : pepSeqsB) {
						if (seqA.equals(seqB)) {
							shouldCondense = true;
						} else {
							if (getMaximumDistance() > 0) {
								// a small levenshtein distance indicates
								// same metaprotein
								if (computeLevenshteinDistance(seqA, seqB) <= getMaximumDistance()) {
									shouldCondense = true;
								}
							}
						}
					}
				}
				return shouldCondense;
				// return !Collections.disjoint(pepSeqsA, pepSeqsB);
			}
		},
		SHARED_SUBSET("have all share a common peptide subset") {
			@Override
			public boolean shouldCondense(Collection<String> pepSeqsA, Collection<String> pepSeqsB) {
				// merge meta-proteins if one peptide set is a subset of the
				// other meta-protein or vice versa (strict similarity
				// criterion)
				List<String> seqsA = new ArrayList<>(pepSeqsA);
				List<String> seqsB = new ArrayList<>(pepSeqsB);
				// boolean notInB = false;
				// check whether peptides in first list have corresponding
				// elements in second list, stop at first mismatch
				Iterator<String> iterA = seqsA.iterator();
				while (iterA.hasNext()) {
					String seqA = iterA.next();
					Iterator<String> iterB = seqsB.iterator();
					while (iterB.hasNext()) {
						String seqB = iterB.next();
						if (seqA.equals(seqB) || ((getMaximumDistance() > 0)
								&& (computeLevenshteinDistance(seqA, seqB) <= getMaximumDistance()))) {
							// match found, remove from lists
							iterA.remove();
							iterB.remove();
							break;
						}
					}
				}
				return seqsA.isEmpty() || seqsB.isEmpty();
				// return (pepSeqsA.containsAll(pepSeqsB) ||
				// pepSeqsB.containsAll(pepSeqsA));
			}
		};

		/**
		 * The name string.
		 */
		private final String name;

		/**
		 * The maximum allowed pairwise peptide sequence Levenshtein distance.
		 */
		// TODO: maybe implement Levenshtein distance evaluation in
		// PeptideHit#equals()
		// TODO: maybe encapsulate globals
		private int maxDistance;

		/**
		 * The flag denoting whether amino acids leucine and isoleucine shall be
		 * considered distinct.
		 */
		private boolean distinctIL;

		/**
		 * Constructs a meta-protein generation rule using the specified name
		 * string.
		 * 
		 * @param name
		 *            the name of the rule
		 */
		PeptideRule(String name) {
			this.name = name;
		}

		/**
		 * Returns the non-trivial peptide rules.
		 * 
		 * @return the non-trivial peptide rules.
		 */
		public static MetaProteinFactory.PeptideRule[] getValues() {
			MetaProteinFactory.PeptideRule[] values = MetaProteinFactory.PeptideRule.values();
			return Arrays.copyOfRange(values, 2, values.length);
		}

		@Override
		public String toString() {
			return name;
		}

		/**
		 * Returns whether meta-proteins represented by the two provided peptide
		 * sequence collections should be merged.
		 * 
		 * @param pepSeqsA
		 *            the peptide sequences of meta-protein A
		 * @param pepSeqsB
		 *            the peptide sequences of meta-protein B
		 * @return <code>true</code> if the meta-proteins should be merged,
		 *         <code>false</code> otherwise
		 */
		public abstract boolean shouldCondense(Collection<String> pepSeqsA, Collection<String> pepSeqsB);

		/**
		 * Returns the maximum allowed pairwise peptide sequence Levenshtein
		 * distance.
		 * 
		 * @return the distance
		 */
		public int getMaximumDistance() {
			return this.maxDistance;
		}

		/**
		 * Sets the maximum allowed pairwise peptide sequence Levenshtein
		 * distance.
		 * 
		 * @param maxDistance
		 *            the distance to set
		 */
		public void setMaximumDistance(int maxDistance) {
			this.maxDistance = maxDistance;
		}

		/**
		 * Returns the flag denoting whether amino acids leucine and isoleucine
		 * shall be considered distinct.
		 * 
		 * @return <code>true</code> if L and I are considered distinct,
		 *         <code>false</code> otherwise
		 */
		public boolean isDistinctIL() {
			return this.distinctIL;
		}

		/**
		 * Sets the flag denoting whether amino acids leucine and isoleucine
		 * shall be considered distinct.
		 * 
		 * @param distinctIL
		 *            <code>true</code> if L and I are considered distinct,
		 *            <code>false</code> otherwise
		 */
		public void setDistinctIL(boolean distinctIL) {
			this.distinctIL = distinctIL;
		}

	}

	/**
	 * Enumeration holding rules for generating meta-proteins based on common
	 * taxonomy.
	 * 
	 * @author A. Behne
	 */
	public enum TaxonomyRule {
		NEVER("ignore, never merge", null) {
			@Override
			public boolean shouldCondense(MetaProteinHit rowMP, MetaProteinHit colMP) {
				// trivial case, never merge
				return false;
			}
		},
		ALWAYS("ignore, always merge", null) {
			@Override
			public boolean shouldCondense(MetaProteinHit rowMP, MetaProteinHit colMP) {
				// trivial case, always merge
				return true;
			}
		},
		SUPERKINGDOM("on superkingdom level or lower", UniProtUtilities.TaxonomyRank.SUPERKINGDOM), 
		KINGDOM("on kingdom level or lower", UniProtUtilities.TaxonomyRank.KINGDOM), 
		PHYLUM("on phylum level or lower", UniProtUtilities.TaxonomyRank.PHYLUM), 
		CLASS("on class level or lower", UniProtUtilities.TaxonomyRank.CLASS), 
		ORDER("on order level or lower", UniProtUtilities.TaxonomyRank.ORDER), 
		FAMILY("on family level or lower", UniProtUtilities.TaxonomyRank.FAMILY), 
		GENUS("on genus level or lower", UniProtUtilities.TaxonomyRank.GENUS), 
		SPECIES("on species level or lower", UniProtUtilities.TaxonomyRank.SPECIES), 
		SUBSPECIES("on subspecies level", UniProtUtilities.TaxonomyRank.SUBSPECIES);

		/**
		 * The name string.
		 */
		private String name;

		/**
		 * The taxonomy rank.
		 */
		private UniProtUtilities.TaxonomyRank rank;

		/**
		 * Constructs a meta-protein generation rule using the specified name
		 * string and taxonomic rank.
		 *
		 * @param name
		 *            the name of the rule
		 * @param rank
		 *            the taxonomic rank
		 */
		TaxonomyRule(String name, UniProtUtilities.TaxonomyRank rank) {
			this.name = name;
			this.rank = rank;
		}

		/**
		 * Returns the non-trivial taxonomy rules.
		 * 
		 * @return the non-trivial taxonomy rules.
		 */
		public static MetaProteinFactory.TaxonomyRule[] getValues() {
			MetaProteinFactory.TaxonomyRule[] values = MetaProteinFactory.TaxonomyRule.values();
			return Arrays.copyOfRange(values, 2, values.length);
		}

		@Override
		public String toString() {
			return name;
		}

		/**
		 * Returns whether the provided meta-proteins should be merged on
		 * grounds of sharing the same common taxonomy defined by the specified
		 * taxonomy rank.
		 *
		 * @param mphA
		 *            meta-protein A
		 * @param mphB
		 *            meta-protein B
		 * @return <code>true</code> if the meta-proteins should be merged,
		 *         <code>false</code> otherwise
		 */
		public boolean shouldCondense(MetaProteinHit mphA, MetaProteinHit mphB) {
			// extract first protein from meta-proteins
			ProteinHit phA = mphA.getProteinHitList().get(0);
			ProteinHit phB = mphB.getProteinHitList().get(0);
			// get taxonomy name for target rank
			String taxNameA = TaxonomyUtils.getTaxonNameByRank(phA.getTaxonomyNode(), rank);
			String taxNameB = TaxonomyUtils.getTaxonNameByRank(phB.getTaxonomyNode(), rank);
			return taxNameA.equals(taxNameB);
		}
	}

	/**
	 * Computes the Levenshtein distance between the provided strings.
	 * <p>
	 * Code adapted from <a href=
	 * "http://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance#Java"
	 * >http://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/
	 * Levenshtein_distance#Java</a>
	 *
	 * @param s0
	 *            the first string
	 * @param s1
	 *            the second string
	 * @return the Levenshtein distance
	 */
	public static int computeLevenshteinDistance(String s0, String s1) {
		int len0 = s0.length() + 1;
		int len1 = s1.length() + 1;

		// the array of distances
		int[] cost = new int[len0];
		int[] newcost = new int[len0];

		// initial cost of skipping prefix in String s0
		for (int i = 0; i < len0; i++) {
			cost[i] = i;
		}

		/* dynamically compute the array of distances */

		// transformation cost for each letter in s1
		for (int j = 1; j < len1; j++) {

			// initial cost of skipping prefix in String s1
			newcost[0] = j - 1;

			// transformation cost for each letter in s0
			for (int i = 1; i < len0; i++) {

				// matching current letters in both strings
				int match = (s0.charAt(i - 1) == s1.charAt(j - 1)) ? 0 : 1;

				// computing cost for each transformation
				int cost_replace = cost[i - 1] + match;
				int cost_insert = cost[i] + 1;
				int cost_delete = newcost[i - 1] + 1;

				// keep minimum cost
				newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
			}

			// swap cost/newcost arrays
			int[] tmp = cost;
			cost = newcost;
			newcost = tmp;
		}

		// the distance is the cost for transforming all letters in both strings
		return cost[len0 - 1];
	}

	/**
	 * Creates meta-proteins from the specified database search result and
	 * creates the peptide, protein and meta-protein taxonomies.
	 * 
	 * @param result
	 *            the database search result
	 * @param params
	 *            the result parameter settings
	 */
	public static void determineTaxonomyAndCreateMetaProteins(DbSearchResult result, ResultParameters params) {
		// Create metaproteins for the new result object.
		Client client = Client.getInstance();
		HashMap<Long, TaxonomyNode> taxonomyNodeMap = result.getTaxonomyNodeMap();
		HashMap<Long, Taxonomy> taxonomyMap = result.getTaxonomyMap();
		
		// Apply FDR cut-off
		result.setFDR((Double) params.get("FDR").getValue());
		
		if (((MetaProteinParameters) params.get("metaProteinGeneration")).metaChk.isSelected()) {
			
			// Get various hit lists from result object
			ArrayList<MetaProteinHit> metaProteins = result.getAllMetaProteins();
			ArrayList<ProteinHit> proteinList = result.getAllProteinHits();
			// all distinct peptides
			ArrayList<PeptideHit> peptideSet = result.getAllPeptideHits(); 

			client.firePropertyChange("new message", null, "DETERMINING PEPTIDE TAXONOMY");
			client.firePropertyChange("resetall", -1L, (long) (peptideSet.size() + proteinList.size() + metaProteins.size()));
			client.firePropertyChange("resetcur", -1L, (long) peptideSet.size());
			// Define common peptide taxonomy for each peptide
			TaxonomyUtils.determinePeptideTaxonomy(peptideSet, TaxonomyDefinition.COMMON_ANCESTOR, taxonomyMap, taxonomyNodeMap);
			
			// WAS SET HARDCODED TO COMMON ANCESTOR??
//			TaxonomyUtils.determinePeptideTaxonomy(peptideSet, TaxonomyUtils.TaxonomyDefinition.COMMON_ANCESTOR);
			client.firePropertyChange("new message", null, "DETERMINING PEPTIDE TAXONOMY FINISHED");

			// Determine protein taxonomy
			client.firePropertyChange("new message", null, "DETERMINING PROTEIN TAXONOMY");
			client.firePropertyChange("resetcur", -1L, (long) proteinList.size()*2);
			// Define protein taxonomy by common tax ID of peptides
			TaxonomyDefinition taxDefProt = (TaxonomyUtils.TaxonomyDefinition) params.get("proteinTaxonomy").getValue();
			TaxonomyUtils.determineProteinTaxonomy(proteinList, taxDefProt, taxonomyMap, taxonomyNodeMap);

			client.firePropertyChange("new message", null, "DETERMINING PROTEIN TAXONOMY FINISHED");
			client.firePropertyChange("new message", null, "CONDENSING META-PROTEINS");
			client.firePropertyChange("resetcur", -1L, (long) metaProteins.size());
			// Combine proteins to metaproteins
			MetaProteinFactory.condenseMetaProteins(result, params);
			client.firePropertyChange("new message", null, "CONDENSING META-PROTEINS FINISHED");
		}
	}

	/**
	 * Combines meta-proteins contained in the specified list of single-protein
	 * meta-proteins on the basis of overlaps in their respective
	 * 
	 * @param metaProteins
	 *            the list of meta-proteins to condense
	 * @param params
	 *            the result processing parameters
	 */
	private static void condenseMetaProteins(DbSearchResult result, ResultParameters params) {

		ArrayList<MetaProteinHit> metaProteins = result.getAllMetaProteins();
		HashMap<Long, TaxonomyNode> taxonomyNodeMap = result.getTaxonomyNodeMap();
		HashMap<Long, Taxonomy> taxonomyMap = result.getTaxonomyMap();
		
		// Extract parameters
		ClusterRule cR = (MetaProteinFactory.ClusterRule) params.get("clusterRule").getValue();
		PeptideRule pR = (MetaProteinFactory.PeptideRule) params.get("peptideRule").getValue();
		TaxonomyRule tR = (MetaProteinFactory.TaxonomyRule) params.get("taxonomyRule").getValue();

		// THE CURRENT METAPROTEIN GENERATION IS VERY EFFICIENT AND WORKS LIKE THIS:
		
		// STACK1 with all MPs
		// STACK2 with new MPs which are merged 
		// 1. Take ONE away from STACK1
		// PEPTIDE RULE RETRIEVAL SAVING MEM
		// SHOULD MERGE CODE 
		// 2. Check against all STACK2
		// 2.1 found one to merge with
		// take this one out of stack
		// MERGE 2 CODE
		// break to 1
		// 2.2 not found next
		// If ONE remains
		// 3. Check against all STACK1
		// 3.1 found one to merge with
		// take this one out of stack
		// MERGE 2 CODE
		// break to 1
		// 3.2 not found next
		// If ONE remains
		// put single prot MP into STACK1
		// FINISHING: STACK1 is empty and all Metarpoteins from STACK2 (the merged ones) are added 
		
		
//		Stack<MetaProteinHit> STACK1 = new Stack<MetaProteinHit>();
//		for (MetaProteinHit p : metaProteins) {
//			STACK1.push(p);
//		}
//		Stack<MetaProteinHit> STACK2 = new Stack<MetaProteinHit>();
//		// 1.
//		while (!STACK1.isEmpty()) {
//			MetaProteinHit currentMetaprotein = STACK1.pop();
//			Client.getInstance().firePropertyChange("progressmade", false, true);
//			Stack<MetaProteinHit> STACK3 = new Stack<MetaProteinHit>();
//			// compare to STACK2 segment
////			for (MetaProteinHit mp_from_stack2 : STACK2) {
//			while (!STACK2.isEmpty()) {
//				MetaProteinHit mp_from_stack2 = STACK2.pop();
//				boolean should_merge = makeComparison(mp_from_stack2, currentMetaprotein, cR, pR, tR);
//				if (should_merge) {
//					mergeTwoMetaproteins(mp_from_stack2, currentMetaprotein, taxonomyMap, taxonomyNodeMap, params);
//					currentMetaprotein = null;
//					break;
//				} else {
//					STACK3.push(mp_from_stack2);
//				}
//			}
//			while (!STACK2.isEmpty()) {
//				STACK3.push(STACK2.pop());
//			}
//			STACK2 = STACK3;
//			// compare to STACK1 segment
//			STACK3 = new Stack<MetaProteinHit>();
//			while (!STACK1.isEmpty() && (currentMetaprotein != null))  {
//				MetaProteinHit mp_from_stack1 = STACK1.pop();
//				boolean should_merge = makeComparison(currentMetaprotein, mp_from_stack1, cR, pR, tR);
//				if (should_merge) {
//					mergeTwoMetaproteins(currentMetaprotein, mp_from_stack1, taxonomyMap, taxonomyNodeMap, params);
//					break;
//				} else {
//					STACK3.push(mp_from_stack1);
//				}
//			}
//			while (!STACK1.isEmpty()) {
//				STACK3.push(STACK1.pop());
//			}
//			STACK1 = STACK3;
//			if (currentMetaprotein != null) {
//				STACK2.add(currentMetaprotein);
//			}
//		}
//		
//		metaProteins.addAll(STACK2);
		
		ArrayList<MetaProteinHit> STACK1 = metaProteins;
		ArrayList<MetaProteinHit> STACK2 = new ArrayList<MetaProteinHit>();
		// 1.
		while (!STACK1.isEmpty()) {
			MetaProteinHit currentMetaprotein = STACK1.get(0);
			STACK1.remove(0);
			Client.getInstance().firePropertyChange("progressmade", false, true);
			
			// compare to STACK2 segment
			for (MetaProteinHit mp_from_stack2 : STACK2) {
				boolean should_merge = makeComparison(mp_from_stack2, currentMetaprotein, cR, pR, tR);
				if (should_merge) {
					mergeTwoMetaproteins(mp_from_stack2, currentMetaprotein, taxonomyMap, taxonomyNodeMap, params, result);
					currentMetaprotein = null;
					break;
				}
			}
			// compare to STACK1 segment
			int index = 0;
			while ((index != metaProteins.size()) && (currentMetaprotein != null))  {
				MetaProteinHit mp_from_stack1 = metaProteins.get(index);
				boolean should_merge = makeComparison(currentMetaprotein, mp_from_stack1, cR, pR, tR);
				if (should_merge) {
					mergeTwoMetaproteins(currentMetaprotein, mp_from_stack1, taxonomyMap, taxonomyNodeMap, params, result);
					STACK1.remove(index);
					break;
				}
				index++;
			}
			if (currentMetaprotein != null) {
				STACK2.add(currentMetaprotein);
			}
		}
		
		metaProteins.addAll(STACK2);
			
		// Re-number condensed meta-proteins
		int metaIndex = 1;
		for (MetaProteinHit mph : metaProteins) {
			if (((MetaProteinHit) mph).getProteinHitList().size() > 1) {
				mph.setAccession("Meta-Protein " + metaIndex++);
			}
			// Set description by taking the description of the first protein
			// hit.
			mph.setDescription(((MetaProteinHit) mph).getProteinHitList().get(0).getDescription());
		}
		for (MetaProteinHit mph : metaProteins) {
			if (((MetaProteinHit) mph).getProteinHitList().size() == 1) {
				mph.setAccession("Meta-Protein " + metaIndex++);
			}
			// set the correct metaproteinhit to all proteinhits
			for (ProteinHit ph : mph.getProteinHitList()) {
				ph.setMetaProteinHit(mph);
			}
		}

		// after setting metaproteins like this visibility becomes pointless ...
		result.setMetaProteins(metaProteins);
		// re-set all proteinhits to their respective metaproteins
		for (MetaProteinHit mph : result.getAllMetaProteins()) {
			for (ProteinHit ph : mph.getProteinHitList()) {
				ph.setMetaProteinHit(mph);
			}
		}
	}
			
	private static boolean makeComparison(MetaProteinHit mp1, MetaProteinHit mp2, ClusterRule cR, PeptideRule pR, TaxonomyRule tR) {
		// merge peptides if we consider I and L the same
		if (!pR.isDistinctIL()) {
			// first we change all I to L
			// protein 1
			for (PeptideHit pep : mp1.getPeptides()) {
				// this method also updates the proteins
				pep.replaceIsoleucine();
			}
			// protein 2
			for (PeptideHit pep : mp2.getPeptides()) {
				pep.replaceIsoleucine();
				// while changing these we check inside protein 1 for this specific peptide
				PeptideHit pep2 = mp1.getPeptideHit(pep.getSequence());
				if (pep2 != null) {
					// transfer all PSMs
					for (PeptideSpectrumMatch psm : pep.getPeptideSpectrumMatches()) {
						pep2.mergePeptideSpectrumMatch(psm);
					}
					// replace the peptide with new merged one
					for (ProteinHit other_ph : pep.getProteinHits()) {
						pep2.addProteinHit(other_ph);
						other_ph.replacePeptide(pep2);
					}
					//						pep = pep2;
				}
			}
		}
		
		ArrayList<String> mp2_pepstrings = new ArrayList<String>();
		ArrayList<String> mp1_pepstrings = new ArrayList<String>();
		if (pR.equals(PeptideRule.SHARED_PEPTIDE) || pR.equals(PeptideRule.SHARED_SUBSET)) {
			ArrayList<PeptideHit> peptides1 = mp1.getPeptides();
			for (PeptideHit peptideHit : peptides1) {
				mp1_pepstrings.add(peptideHit.getSequence());
			}
			ArrayList<PeptideHit> peptides2 = mp2.getPeptides();
			for (PeptideHit peptideHit : peptides2) {
				mp2_pepstrings.add(peptideHit.getSequence());
			}
		}
		if (cR.shouldCondense(mp1, mp2)
				&& pR.shouldCondense(mp1_pepstrings, mp2_pepstrings)
				&& tR.shouldCondense(mp1, mp2)) {
			return true;
		} else {
			return false;
		}
	}
	
	private static MetaProteinHit mergeTwoMetaproteins(MetaProteinHit merge_into_entry, MetaProteinHit entry_that_is_removed, HashMap<Long, Taxonomy> taxonomyMap, HashMap<Long, TaxonomyNode> taxonomyNodeMap, ResultParameters params, DbSearchResult result) {
		// merge proteinlists
		for (ProteinHit ph : entry_that_is_removed.getProteinHitList()) {
			merge_into_entry.addProteinHit(ph);
		}
		// Calculate common ancestor uniprot entry and merge
		ArrayList<UniProtEntryMPA> uniprotList = new ArrayList<UniProtEntryMPA>();
		if (entry_that_is_removed.getUniProtEntry() != null
				&& !entry_that_is_removed.getUniProtEntry().getUniProtID().equals(-1L)) {
			uniprotList.add(entry_that_is_removed.getUniProtEntry());
		}
		// for some reason it has uniprotid == -1 even if it should not 
		if (merge_into_entry.getUniProtEntry() != null
				&& !merge_into_entry.getUniProtEntry().getUniProtID().equals(-1L)) {
			uniprotList.add(merge_into_entry.getUniProtEntry());
		}
		UniProtEntryMPA commonUniprotEntry = null;
		if (uniprotList.size() > 1) {
			commonUniprotEntry = UniProtUtilities.getCommonUniprotEntry(uniprotList, taxonomyMap, taxonomyNodeMap,
					(TaxonomyUtils.TaxonomyDefinition) params.get("metaProteinTaxonomy")
					.getValue(), null);
		} else if (uniprotList.size() == 1) {
			commonUniprotEntry = uniprotList.get(0);
		} else {
			try {
				commonUniprotEntry = result.createEmptyUPEntry();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		merge_into_entry.setUniprotEntry(commonUniprotEntry);
		if (commonUniprotEntry != null) {
			merge_into_entry.setTaxonomyNode(commonUniprotEntry.getTaxonomyNode());
		}
		return merge_into_entry;
	}
}
