package de.mpa.client.model.dbsearch;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;

import de.mpa.analysis.UniProtUtilities.TaxonomyRank;
import de.mpa.analysis.taxonomy.TaxonomyUtils;
import de.mpa.client.Client;
import de.mpa.client.settings.ParameterMap;

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
		UNIREF100("in UniRef100"),
		UNIREF90("in UniRef90 or higher"),
		UNIREF50("in UniRef50 or higher");
		
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
		public boolean shouldCondense(MetaProteinHit mphA, MetaProteinHit mphB) {
			ReducedUniProtEntry upeA = mphA.getProteinHitList().get(0).getUniProtEntry();
			ReducedUniProtEntry upeB = mphB.getProteinHitList().get(0).getUniProtEntry();
			if ((upeA == null) || (upeB == null)) {
				return false;
			}
			String refA = null;
			String refB = null;
			switch (this) {
				case UNIREF100:
					refA = upeA.getUniRef100id();
					refB = upeB.getUniRef100id();
					break;
				case UNIREF90:
					refA = upeA.getUniRef90id();
					refB = upeB.getUniRef90id();
					System.out.println("" + refA + " " + refB + " " + ((refA != null) && refA.equals(refB)));
					break;
				case UNIREF50:
					refA = upeA.getUniRef50id();
					refB = upeB.getUniRef50id();
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
			ProteinHit phA = mphA.getProteinHitList().get(0);
			ProteinHit phB = mphB.getProteinHitList().get(0);
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
					colMP.addAll(rowMP.getProteinHitList());
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
		}
		for (ProteinHit mph : metaProteins) {
			if (((MetaProteinHit) mph).getProteinHitList().size() == 1) {
				mph.setAccession("Meta-Protein " + metaIndex++);
			}
		}
	}

	/**
	 * Creates meta-proteins from the specified database search result and
	 * creates the peptide, protein and meta-protein taxonomies.
	 * @param result the database search result
	 * @param params the result parameter settings
	 */
	public static void determineTaxonomyAndCreateMetaProteins(DbSearchResult result, ParameterMap params) {
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
		TaxonomyUtils.determinePeptideTaxonomy(result.getMetaProteins().getPeptideSet(), params);
		
		client.firePropertyChange("new message", null, "DETERMINING PEPTIDE TAXONOMY FINISHED");
		
		// Apply FDR cut-off
		result.setFDR(((Double[]) params.get("FDR").getValue())[0]);

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

		client.firePropertyChange("new message", null, "DETERMINING META-PROTEIN TAXONOMY");
		client.firePropertyChange("resetcur", -1L, (long) result.getMetaProteins().size());
		
		// Determine meta-protein taxonomy
		TaxonomyUtils.determineMetaProteinTaxonomy(result.getMetaProteins(), params);
		
		client.firePropertyChange("new message", null, "DETERMINING META-PROTEIN TAXONOMY FINISHED");
	}
	
	/**
	 * Recreates meta-proteins from the specified database search result and
	 * recreates the protein and meta-protein taxonomies.
	 * @param result the database search result
	 * @param params the result parameter settings
	 */
	public static void redetermineTaxonomyAndRecreateMetaProteins(DbSearchResult result, ParameterMap params) {
		// Create metaproteins for the new result object.
		Client client = Client.getInstance();

		// Undo FDR filtering
		result.setFDR(1.0);
		
		List<ProteinHit> proteins = result.getProteinHitList();

		// Determine protein taxonomy
		client.firePropertyChange("new message", null, "DETERMINING PROTEIN TAXONOMY");
		client.firePropertyChange("resetcur", -1L, (long) proteins.size());

		// Define protein taxonomy by common tax ID of peptides
		TaxonomyUtils.determineProteinTaxonomy(proteins, params);

		client.firePropertyChange("new message", null, "DETERMINING PROTEIN TAXONOMY FINISHED");

		// Reset meta-proteins
		result.clearVisibleMetaProteins();
		ProteinHitList metaProteins = result.getMetaProteins();
		metaProteins.clear();
		int i = 0;
		for (ProteinHit protein : proteins) {
			// create new meta-protein containing single protein
			MetaProteinHit mph = new MetaProteinHit("Meta-Protein " + (++i), protein);
			// link protein to meta-protein
			protein.setMetaProteinHit(mph);
			// add meta-protein to list
			metaProteins.add(mph);
		}

		// Apply FDR cut-off
		Double fdr = ((Double[]) params.get("FDR").getValue())[0];
		result.setFDR(fdr);
		
		// Re-retrieve filtered meta-protein list
		metaProteins = result.getMetaProteins();
		
		client.firePropertyChange("new message", null, "CONDENSING META-PROTEINS");
		client.firePropertyChange("resetcur", -1L, (long) metaProteins.size());
		
		// Combine proteins to meta-proteins
		MetaProteinFactory.condenseMetaProteins(metaProteins, params);

		client.firePropertyChange("new message", null, "CONDENSING META-PROTEINS FINISHED");

		client.firePropertyChange("new message", null, "DETERMINING META-PROTEIN TAXONOMY");
		client.firePropertyChange("resetcur", -1L, (long) metaProteins.size());

		// Determine meta-protein taxonomy
		TaxonomyUtils.determineMetaProteinTaxonomy(metaProteins, params);

		client.firePropertyChange("new message", null, "DETERMINING META-PROTEIN TAXONOMY FINISHED");
	}
	
}
