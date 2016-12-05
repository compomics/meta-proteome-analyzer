package de.mpa.client.model.dbsearch;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import de.mpa.analysis.UniProtUtilities;
import de.mpa.analysis.UniProtUtilities.TaxonomyRank;
import de.mpa.analysis.taxonomy.TaxonomyUtils;
import de.mpa.analysis.taxonomy.TaxonomyUtils.TaxonomyDefinition;
import de.mpa.client.Client;
import de.mpa.client.settings.ResultParameters;
import de.mpa.db.DBManager;
import de.mpa.db.accessor.Taxonomy;

/**
 * Factory class providing methods to merge meta-proteins and to determine common properties (i.e. taxonomy).
 * 
 * @author R. Heyer, A. Behne
 */
public class MetaProteinFactory {
	
//	/**
//	 * Enumeration holding rules for generating meta-proteins based on peptide sharing.
//	 * @author A. Behne
//	 */
//	public enum DistPepRule {
//		NEVER("ignore, never merge") {
//			@Override
//			public boolean shouldCondense(Collection<String> pepSeqsA, Collection<String> pepSeqsB, int levThreshold) {
//				// trivial case, do not condense meta-proteins
//				return false;
//			}
//		},
//		ALWAYS("ignore, always merge") {
//			@Override
//			public boolean shouldCondense(Collection<String> pepSeqsA, Collection<String> pepSeqsB,int levThreshold) {
//				// trivial case, always condense meta-proteins
//				return true;
//			}
//		},
//			THRESHOLD("Do not merge, if they have similar peptides") {
//			@Override
//			public boolean shouldCondense(
//					Collection<String> pepSeqsA,
//					Collection<String> pepSeqsB, int levThreshold) {
//				// Merge meta-proteins if at least one overlapping peptide
//				// element exists (weak similarity criterion)
//				return !StringSimilarity.similarPeptides(pepSeqsA, pepSeqsB, levThreshold);
//			}
//		};
//
//		/**
//		 * The name string.
//		 */
//		private String name;
//
//		/**
//		 * Constructs a meta-protein generation rule using the specified name string.
//		 * @param name the name of the rule
//		 */
//		private DistPepRule(String name) {
//			this.name = name;
//		}
//
//		@Override
//		public String toString() {
//			return this.name;
//		}
//
//		/**
//		 * Returns whether meta-proteins represented by the two provided peptide
//		 * sequence collections should be merged.
//		 * @param pepSeqsA the peptide sequences of meta-protein A
//		 * @param pepSeqsB the peptide sequences of meta-protein B
//		 * @return <code>true</code> if the meta-proteins should be merged, <code>false</code> otherwise
//		 */
//		public abstract boolean shouldCondense(Collection<String> pepSeqsA, Collection<String> pepSeqsB, int levThreshold);
//	}
	
	/**
	 * Enumeration holding rules for generating meta-proteins based on protein clusters.
	 * @author A. Behne, R. Heyer
	 */
	public enum ClusterRule {
		NEVER("ignore, never merge") {
			@Override
			public boolean shouldCondense(
					MetaProteinHit rowMP,
					MetaProteinHit colMP) {
				// trivial case, never merge
				return false;
			}
		},
		ALWAYS("ignore, always merge") {
			@Override
			public boolean shouldCondense(
					MetaProteinHit rowMP,
					MetaProteinHit colMP) {
				// trivial case, always merge
				return true;
			}
		},
//		KO("KEGG orthology (KO)"){
//			@Override
//			public boolean shouldCondense(
//					MetaProteinHit rowMP,
//					MetaProteinHit colMP) {
//				List<String> koRow  = rowMP.getProteinHitList().get(0).getUniProtEntry().getKNumbers();
//				List<String> kocol  = colMP.getProteinHitList().get(0).getUniProtEntry().getKNumbers();
//				if (koRow != null && koRow.size() > 0 && kocol != null && kocol.size() > 0) {
//					if (koRow.get(0).equals(kocol.get(0))) {
//						return true;
//					}
//				}
//				return false;
//			}
//		},
//		eggNOG("evolutionary genealogy of genes"){
//			@Override
//			public boolean shouldCondense(
//					MetaProteinHit rowMP,
//					MetaProteinHit colMP) {
//				// trivial case, always merge
//				return true;
//			}},
		UNIREF100("in UniRef100"),
		UNIREF90("in UniRef90 or higher"),
		UNIREF50("in UniRef50 or higher"),
		;
		
		/**
		 * The name string.
		 */
		private String name;

		/**
		 * Constructs a meta-protein generation rule using the specified name string.
		 * @param name the name of the rule
		 */
		private ClusterRule(String name) {
			this.name = name;
		}

		/**
		 * Returns the non-trivial cluster rules.
		 * @return the non-trivial cluster rules.
		 */
		public static ClusterRule[] getValues() {
			ClusterRule[] values = values();
			return Arrays.copyOfRange(values, 2, values.length);
		}

		@Override
		public String toString() {
			return this.name;
		}

		/**
		 * Returns whether the provided meta-proteins should be merged.
		 * @param mphA meta-protein A
		 * @param mphB meta-protein B
		 * @return <code>true</code> if the meta-proteins should be merged, <code>false</code> otherwise
		 */
		@SuppressWarnings("incomplete-switch")
		public boolean shouldCondense(MetaProteinHit mphA, MetaProteinHit mphB) {
			UniProtEntryMPA upeA = mphA.getProteinHitList().get(0).getUniProtEntry();
			UniProtEntryMPA upeB = mphB.getProteinHitList().get(0).getUniProtEntry();
			if ((upeA == null) || upeA.getUniProtID().equals(-1L)  || (upeA.getUniRefMPA() == null)|| (upeB == null) || upeB.getUniProtID().equals(-1L)|| (upeB.getUniRefMPA() == null)) {
				return false;
			}
			String refA = null;
			String refB = null;
			switch (this) {
				case UNIREF100:
					refA = upeA.getUniRefMPA().getUniRef100();
					refB = upeB.getUniRefMPA().getUniRef100();
					break;
				case UNIREF90:
					refA = upeA.getUniRefMPA().getUniRef90();
					refB = upeB.getUniRefMPA().getUniRef90();
					break;
				case UNIREF50:
					refA = upeA.getUniRefMPA().getUniRef50();
					refB = upeB.getUniRefMPA().getUniRef50();
					break;
			}
			return (refA != null) && refA.equals(refB);
		}
		
	}

	/**
	 * Enumeration holding rules for generating meta-proteins based on peptide sharing.
	 * @author A. Behne
	 */
	public enum PeptideRule {
		NEVER("ignore, never merge") {
			@Override
			public boolean shouldCondense(
					Collection<String> pepSeqsA,
					Collection<String> pepSeqsB) {
				// trivial case, do not condense meta-proteins
				return false;
			}
		},
		ALWAYS("ignore, always merge") {
			@Override
			public boolean shouldCondense(
					Collection<String> pepSeqsA,
					Collection<String> pepSeqsB) {
				// trivial case, always condense meta-proteins
				return true;
			}
		},
		SHARED_PEPTIDE("have at least one peptide in common") {
			@Override
			public boolean shouldCondense(
					Collection<String> pepSeqsA,
					Collection<String> pepSeqsB) {
				// Merge meta-proteins if at least one overlapping peptide
				// element exists (weak similarity criterion)
				boolean shouldCondense = false;
				for (String seqA : pepSeqsA) {
					for (String seqB : pepSeqsB) {
						if (seqA.equals(seqB)) {
							shouldCondense = true;
						} else {
							if (this.getMaximumDistance() > 0) {
								// a small levenshtein distance indicates different metaproteins 
								if (MetaProteinFactory.computeLevenshteinDistance(seqA, seqB) <= this.getMaximumDistance()) {
									return false;
								}
							}
						}
					}
				}
				return shouldCondense;
//				return !Collections.disjoint(pepSeqsA, pepSeqsB);
			}
		},
		SHARED_SUBSET("have all share a common peptide subset") {
			@Override
			public boolean shouldCondense(
					Collection<String> pepSeqsA,
					Collection<String> pepSeqsB) {
				// merge meta-proteins if one peptide set is a subset of the
				// other meta-protein or vice versa (strict similarity criterion)
				List<String> seqsA = new ArrayList<>(pepSeqsA);
				List<String> seqsB = new ArrayList<>(pepSeqsB);
				//boolean notInB = false;
				// check whether peptides in first list have corresponding
				// elements in second list, stop at first mismatch
				Iterator<String> iterA = seqsA.iterator();
				while (iterA.hasNext()) {
					String seqA = (String) iterA.next();
					Iterator<String> iterB = seqsB.iterator();
					while (iterB.hasNext()) {
						String seqB = (String) iterB.next();
						if (seqA.equals(seqB) || ((this.getMaximumDistance() > 0) 
								&& (MetaProteinFactory.computeLevenshteinDistance(seqA, seqB) <= this.getMaximumDistance()))) {
							// match found, remove from lists
							iterA.remove();
							iterB.remove();
							break;
						} 
					}
				}
				return seqsA.isEmpty() || seqsB.isEmpty();
//				return (pepSeqsA.containsAll(pepSeqsB) || pepSeqsB.containsAll(pepSeqsA));
			}
		};

		/**
		 * The name string.
		 */
		private String name;
		
		/**
		 * The maximum allowed pairwise peptide sequence Levenshtein distance.
		 */
		// TODO: maybe implement Levenshtein distance evaluation in PeptideHit#equals()
		// TODO: maybe encapsulate globals
		private int maxDistance;
		
		/**
		 * The flag denoting whether amino acids leucine and isoleucine shall be
		 * considered distinct.
		 */
		private boolean distinctIL;

		/**
		 * Constructs a meta-protein generation rule using the specified name string.
		 * @param name the name of the rule
		 */
		private PeptideRule(String name) {
			this.name = name;
		}

		/**
		 * Returns the non-trivial peptide rules.
		 * @return the non-trivial peptide rules.
		 */
		public static PeptideRule[] getValues() {
			PeptideRule[] values = values();
			return Arrays.copyOfRange(values, 2, values.length);
		}

		@Override
		public String toString() {
			return this.name;
		}

		/**
		 * Returns whether meta-proteins represented by the two provided peptide
		 * sequence collections should be merged.
		 * @param pepSeqsA the peptide sequences of meta-protein A
		 * @param pepSeqsB the peptide sequences of meta-protein B
		 * @return <code>true</code> if the meta-proteins should be merged, <code>false</code> otherwise
		 */
		public abstract boolean shouldCondense(Collection<String> pepSeqsA, Collection<String> pepSeqsB);

		/**
		 * Returns the maximum allowed pairwise peptide sequence Levenshtein distance.
		 * @return the distance
		 */
		public int getMaximumDistance() {
			return maxDistance;
		}

		/**
		 * Sets the maximum allowed pairwise peptide sequence Levenshtein distance.
		 * @param maxDistance the distance to set
		 */
		public void setMaximumDistance(int maxDistance) {
			this.maxDistance = maxDistance;
		}

		/**
		 * Returns the flag denoting whether amino acids leucine and isoleucine
		 * shall be considered distinct.
		 * @return <code>true</code> if L and I are considered distinct,
		 *  <code>false</code> otherwise
		 */
		public boolean isDistinctIL() {
			return distinctIL;
		}

		/**
		 * Sets the flag denoting whether amino acids leucine and isoleucine
		 * shall be considered distinct.
		 * @param distinctIL <code>true</code> if L and I are considered distinct,
		 *  <code>false</code> otherwise
		 */
		public void setDistinctIL(boolean distinctIL) {
			this.distinctIL = distinctIL;
		}
		
	}

	/**
	 * Enumeration holding rules for generating meta-proteins based on common taxonomy.
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
		SUPERKINGDOM("on superkingdom level or lower", TaxonomyRank.SUPERKINGDOM),
		KINGDOM("on kingdom level or lower", TaxonomyRank.KINGDOM),
		PHYLUM("on phylum level or lower", TaxonomyRank.PHYLUM),
		CLASS("on class level or lower", TaxonomyRank.CLASS),
		ORDER("on order level or lower", TaxonomyRank.ORDER),
		FAMILY("on family level or lower", TaxonomyRank.FAMILY),
		GENUS("on genus level or lower", TaxonomyRank.GENUS), 
		SPECIES("on species level or lower", TaxonomyRank.SPECIES), 
		SUBSPECIES("on subspecies level", TaxonomyRank.SUBSPECIES);

		/**
		 * The name string.
		 */
		private String name;
		
		/**
		 * The taxonomy rank.
		 */
		private TaxonomyRank rank;

		/**
		 * Constructs a meta-protein generation rule using the specified name
		 * string and taxonomic rank.
		 * 
		 * @param name the name of the rule
		 * @param rank the taxonomic rank
		 */
		private TaxonomyRule(String name, TaxonomyRank rank) {
			this.name = name;
			this.rank = rank;
		}

		/**
		 * Returns the non-trivial taxonomy rules.
		 * @return the non-trivial taxonomy rules.
		 */
		public static TaxonomyRule[] getValues() {
			TaxonomyRule[] values = values();
			return Arrays.copyOfRange(values, 2, values.length);
		}

		@Override
		public String toString() {
			return this.name;
		}

		/**
		 * Returns whether the provided meta-proteins should be merged on
		 * grounds of sharing the same common taxonomy defined by the specified
		 * taxonomy rank.
		 * 
		 * @param mphA meta-protein A
		 * @param mphB meta-protein B
		 * @return <code>true</code> if the meta-proteins should be merged,
		 *         <code>false</code> otherwise
		 */
		public boolean shouldCondense(MetaProteinHit mphA, MetaProteinHit mphB) {
			// extract first protein from meta-proteins
			ProteinHit phA = mphA.getProteinHitList().get(0);
			ProteinHit phB = mphB.getProteinHitList().get(0);
			// get taxonomy name for target rank
			String taxNameA = TaxonomyUtils.getTaxonNameByRank(phA.getTaxonomyNode(), this.rank);
			String taxNameB = TaxonomyUtils.getTaxonNameByRank(phB.getTaxonomyNode(), this.rank);
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
	 * @param s0 the first string
	 * @param s1 the second string
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
	 * @param result the database search result
	 * @param params the result parameter settings
	 */
	public static void determineTaxonomyAndCreateMetaProteins(DbSearchResult result, ResultParameters params) {
		// Create metaproteins for the new result object.
		Client client = Client.getInstance();
		
		// Get various hit lists from result object
		ProteinHitList metaProteins = result.getMetaProteins();
		List<ProteinHit> proteinList = result.getProteinHitList();
		Set<PeptideHit> peptideSet = ((ProteinHitList) proteinList).getPeptideSet();	// all distinct peptides

		client.firePropertyChange("new message", null, "DETERMINING PEPTIDE TAXONOMY");
		client.firePropertyChange("resetall", -1L, (long) (peptideSet.size() + proteinList.size() + metaProteins.size()));
		client.firePropertyChange("resetcur", -1L, (long) peptideSet.size());

		// Define common peptide taxonomy for each peptide
		TaxonomyUtils.determinePeptideTaxonomy(result.getMetaProteins().getPeptideSet(), TaxonomyDefinition.COMMON_ANCESTOR);
		
		client.firePropertyChange("new message", null, "DETERMINING PEPTIDE TAXONOMY FINISHED");
		
		// Apply FDR cut-off
		result.setFDR((Double) params.get("FDR").getValue());

		// Determine protein taxonomy
		client.firePropertyChange("new message", null, "DETERMINING PROTEIN TAXONOMY");
		client.firePropertyChange("resetcur", -1L, (long) result.getProteinHitList().size());

		// Define protein taxonomy by common tax ID of peptides
		TaxonomyUtils.determineProteinTaxonomy(result.getProteinHitList(), params);
		
		client.firePropertyChange("new message", null, "DETERMINING PROTEIN TAXONOMY FINISHED");

		client.firePropertyChange("new message", null, "CONDENSING META-PROTEINS");
		client.firePropertyChange("resetcur", -1L, (long) result.getMetaProteins().size());

		// Combine proteins to metaproteins
		MetaProteinFactory.condenseMetaProteins(result.getMetaProteins(), params);
		
		client.firePropertyChange("new message", null, "CONDENSING META-PROTEINS FINISHED");

	}
	
	/**
		 * Combines meta-proteins contained in the specified list of single-protein 
		 * meta-proteins on the basis of overlaps in their respective 
		 * @param metaProteins the list of meta-proteins to condense
		 * @param params the result processing parameters
		 */
		private static void condenseMetaProteins(ProteinHitList metaProteins, ResultParameters params) {
			
			// taxonomy map for common ancestor retrieval
			Map<Long, Taxonomy> taxonomyMap = null;
			try {
				taxonomyMap = Taxonomy.retrieveTaxonomyMap(DBManager.getInstance().getConnection());
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			// Extract parameters
			ClusterRule clusterRule = (ClusterRule) params.get("clusterRule").getValue();
			PeptideRule peptideRule = (PeptideRule) params.get("peptideRule").getValue();
			TaxonomyRule taxonomyRule = (TaxonomyRule) params.get("taxonomyRule").getValue();
			
			// Decide whether leucine and isoleucine should be considered distinct or not
			boolean distinctIL = peptideRule.isDistinctIL();
			
			// Iterate (initially single-protein) meta-proteins
			Iterator<ProteinHit> rowIter = metaProteins.iterator();
			
			while (rowIter.hasNext()) {
				MetaProteinHit rowMP = (MetaProteinHit) rowIter.next();
	
				// Get set of peptide sequences
				Set<PeptideHit> rowPeps = rowMP.getPeptideSet();
				Set<String> rowPepSeqs = new HashSet<String>() ;
				for (PeptideHit peptideHit : rowPeps) {
					// Make copy of sequence string to avoid overwriting the original sequence
					String sequence = new String(peptideHit.getSequence());
					// Merge leucine and isoleucine if not considered distinct
					if (!distinctIL) {
						sequence = sequence.replaceAll("[IL]", "L");
					}
					rowPepSeqs.add(sequence);
				}

				// Nested iteration of the same meta-protein list, stop when outer and inner iteration element 
				// are identical, iterate backwards from the end of the list
				ListIterator<ProteinHit> colIter = metaProteins.listIterator(metaProteins.size());
				while (colIter.hasPrevious()) {
					MetaProteinHit colMP = (MetaProteinHit) colIter.previous();
					// Check termination condition
					if (rowMP == colMP) {
						break;
					}

					// Get set of peptide sequences (of inner iteration element)
					Set<PeptideHit> colPeps = colMP.getPeptideSet();
					Set<String> colPepSeqs = new HashSet<String>() ;
					for (PeptideHit peptideHit : colPeps) {
						// Make copy of sequence string to avoid overwriting the original sequence
						String sequence = new String(peptideHit.getSequence());
						// Merge leucine and isoleucine into one if desired
						if (!distinctIL) {
							sequence = sequence.replaceAll("[IL]", "L");
						}
						colPepSeqs.add(sequence);
					}
					
					// Merge meta-proteins if rules apply
					if (clusterRule.shouldCondense(rowMP, colMP)
							&& peptideRule.shouldCondense(rowPepSeqs, colPepSeqs)
							&& taxonomyRule.shouldCondense(rowMP, colMP)) {
						
						// Add all proteins of outer meta-protein to inner meta-protein
						colMP.addAll(rowMP.getProteinHitList());
						
						// Calculate common ancestor uniprot entry
						ArrayList<UniProtEntryMPA> uniprotList = new ArrayList<UniProtEntryMPA>();
						
						if (colMP.getUniProtEntry() != null && !colMP.getUniProtEntry().getUniProtID().equals(-1L)) {
							uniprotList.add(colMP.getUniProtEntry());
						}
						if (rowMP.getUniProtEntry() != null && !rowMP.getUniProtEntry().getUniProtID().equals(-1L)) {
							uniprotList.add(rowMP.getUniProtEntry());
						} 
						UniProtEntryMPA commonUniprotEntry;
						if (uniprotList.size() > 1) {
							commonUniprotEntry = UniProtUtilities.getCommonUniprotEntry(uniprotList, taxonomyMap, (TaxonomyDefinition) params.get("metaProteinTaxonomy").getValue());
						} else if (uniprotList.size() == 1){
							commonUniprotEntry = uniprotList.get(0);
						} else {
							commonUniprotEntry = null;
						}
						colMP.setUniprotEntry(commonUniprotEntry);
						if (commonUniprotEntry != null) {
							colMP.setTaxonomyNode(commonUniprotEntry.getTaxonomyNode());
						}
						
						// Remove emptied outer meta-protein from list
						rowIter.remove();
						// Abort inner iteration as outer element has been removed and 
						// therefore cannot be merged into another meta-protein again
						break;
					}
				}
				
				// Fire progress notification
				Client.getInstance().firePropertyChange("progressmade", false, true);
			}
	
			// Re-number condensed meta-proteins
			int metaIndex = 1;
			for (ProteinHit mph : metaProteins) {
				
				if (((MetaProteinHit) mph).getProteinHitList().size() > 1) {
					mph.setAccession("Meta-Protein " + metaIndex++);
				}
				
				// Set description by taking the description of the first protein hit.
				mph.setDescription(((MetaProteinHit) mph).getProteinHitList().get(0).getDescription());
			}
			
			for (ProteinHit mph : metaProteins) {
				if (((MetaProteinHit) mph).getProteinHitList().size() == 1) {
					mph.setAccession("Meta-Protein " + metaIndex++);
				}
			}
		}
	
}
