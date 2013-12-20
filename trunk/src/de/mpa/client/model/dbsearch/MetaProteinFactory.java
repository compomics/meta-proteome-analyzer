package de.mpa.client.model.dbsearch;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;

import de.mpa.analysis.UniProtUtilities.TaxonomyRank;
import de.mpa.client.Client;
import de.mpa.client.settings.ParameterMap;
import de.mpa.taxonomy.TaxonomyUtils;

/**
 * Factory class providing methods to merge meta-proteins and to determine common properties (i.e. taxonomy).
 * 
 * @author R. Heyer, A. Behne
 */
public class MetaProteinFactory {
	
	/**
	 * Enumeration holding rules for generating meta-proteins based on protein clusters.
	 * @author R. Heyer
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
		UNIREF100("in UniRef100") {
			@Override
			public boolean shouldCondense(
					MetaProteinHit rowMP,
					MetaProteinHit colMP) {
				// TODO: implement UniRef lookup
				return true; 
			}
		},
		UNIREF90("in UniRef90 or higher") {
			public boolean shouldCondense(
					MetaProteinHit rowMP,
					MetaProteinHit colMP) {
				// TODO: implement UniRef lookup
				return true;
			}
		},
		UNIREF50("in UniRef50 or higher") {
			public boolean shouldCondense(
					MetaProteinHit rowMP,
					MetaProteinHit colMP) {
				// TODO: implement UniRef lookup
				return true;
			}
		};
		
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
		public abstract boolean shouldCondense(MetaProteinHit mphA, MetaProteinHit mphB);
		
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
				return !Collections.disjoint(pepSeqsA, pepSeqsB);
			}
		},
		PEPTIDE_SUBSET("have all peptides in common") {
			@Override
			public boolean shouldCondense(
					Collection<String> pepSeqsA,
					Collection<String> pepSeqsB) {
				// // Merge meta-proteins if one peptide set is a subset of the
				// other meta-protein or vice versa (strict similarity criterion)
				return (pepSeqsA.containsAll(pepSeqsB) || pepSeqsB.containsAll(pepSeqsA));
			}
		};

		/**
		 * The name string.
		 */
		private String name;

		/**
		 * Constructs a meta-protein generation rule using the specified name string.
		 * @param name the name of the rule
		 */
		private PeptideRule(String name) {
			this.name = name;
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
		SUPERKINGDOM("on superkingdom level or deeper", TaxonomyRank.SUPERKINGDOM),
		KINGDOM("on kingdom level or deeper", TaxonomyRank.KINGDOM),
		PHYLUM("on phylum level or deeper", TaxonomyRank.PHYLUM),
		CLASS("on class level or deeper", TaxonomyRank.CLASS),
		ORDER("on order level or deeper", TaxonomyRank.ORDER),
		FAMILY("on family level or deeper", TaxonomyRank.FAMILY),
		GENUS("on genus level or deeper", TaxonomyRank.GENUS), 
		SPECIES("on species level or deeper", TaxonomyRank.SPECIES), 
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
			ProteinHit phA = mphA.getProteinHits().get(0);
			ProteinHit phB = mphB.getProteinHits().get(0);
			// get taxonomy name for target rank
			String taxNameA = TaxonomyUtils.getTaxonNameByRank(phA.getTaxonomyNode(), this.rank);
			String taxNameB = TaxonomyUtils.getTaxonNameByRank(phB.getTaxonomyNode(), this.rank);
			return taxNameA.equals(taxNameB);
		}
	}
	
	/**
	 * Combines meta-proteins contained in the specified list of single-protein 
	 * meta-proteins on the basis of overlaps in their respective 
	 * @param metaProteins the list of meta-proteins to condense
	 * @param metaProtParams the meta-protein generation parameters
	 */
	public static void condenseMetaProteins(
			ProteinHitList metaProteins, ParameterMap metaProtParams) {
		// Extract parameters
		boolean distinguishIL = (Boolean) metaProtParams.get("distinguishIL").getValue();
		ClusterRule protClusterRule = (ClusterRule)((DefaultComboBoxModel) metaProtParams.get(
				"proteinClusterRule").getValue()).getSelectedItem();
		PeptideRule peptideRule = (PeptideRule) ((DefaultComboBoxModel) metaProtParams.get(
				"peptideRule").getValue()).getSelectedItem();
		TaxonomyRule taxonomyRule = (TaxonomyRule) ((DefaultComboBoxModel) metaProtParams.get(
				"taxonomyRule").getValue()).getSelectedItem();
		
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
				// Merge leucine and isoleucine into one if desired
				if (!distinguishIL) {
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
					if (!distinguishIL) {
						sequence = sequence.replaceAll("[IL]", "L");
					}
					colPepSeqs.add(sequence);
				}

				// Merge meta-proteins if rules apply
				if (protClusterRule.shouldCondense(rowMP, colMP)
						&& peptideRule.shouldCondense(rowPepSeqs, colPepSeqs)
						&& taxonomyRule.shouldCondense(rowMP, colMP)) {
					// Add all proteins of outer meta-protein to inner meta-protein
					colMP.addAll(rowMP.getProteinHits());
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
			if (((MetaProteinHit) mph).getProteinHits().size() > 1) {
				mph.setAccession("Meta-Protein " + metaIndex++);
			}
		}
		for (ProteinHit mph : metaProteins) {
			if (((MetaProteinHit) mph).getProteinHits().size() == 1) {
				mph.setAccession("Meta-Protein " + metaIndex++);
			}
		}
	}
	
}
