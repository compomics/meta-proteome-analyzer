package de.mpa.client.model.dbsearch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import de.mpa.client.Client;
import de.mpa.taxonomy.TaxonomyNode;
import de.mpa.taxonomy.TaxonomyUtils;

/**
 * Factory class providing methods to merge meta-proteins and to determine common properties (i.e. taxonomy).
 * 
 * @author R. Heyer, A. Behne
 */
public class MetaProteinFactory {
	
	/**
	 * Enumeration holding rules for generating meta-proteins.
	 * @author A. Behne
	 */
	// TODO: implement different rules
	public enum MetaProteinRule {
		SHARED_PEPTIDE("have at least one peptide in common"),
		PEPTIDE_SUBSET("have all peptides in common"),
		BY_TAXONOMY("by peptide taxonomy");
		
		private String name;

		/**
		 * Constructs a meta-protein generation rule using the specified name string.
		 * @param name the name of the rule
		 */
		private MetaProteinRule(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return this.name;
		}
	}

	/**
	 * Combines meta-proteins contained in the specified list of single-protein 
	 * meta-proteins on the basis of overlaps in their respective 
	 * @param metaProteins the list of meta-proteins to condense
	 * @param mergeIL <code>true</code> if isoleucine and leucine shall be considered
	 *  indistinguishable, <code>false</code> otherwise
	 */
	public static void condenseMetaProteins(ProteinHitList metaProteins, boolean mergeIL) {
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
				if (mergeIL) {
					// FIXME: Replacing I or L by IL does not make sense... this should be handled via the PeptideHit equals() method
					sequence = sequence.replaceAll("[IL]", "L");
				}
				rowPepSeqs.add(sequence);
			}
			
			// Nested iteration of the same meta-protein list, stop when outer and inner iteration element 
			// are identical, iterate backwards from the end of the list
			ListIterator<ProteinHit> colIter = metaProteins.listIterator(metaProteins.size());
			// TODO: check for errors in meta-protein generation
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
					if (mergeIL) {
						sequence = sequence.replaceAll("[IL]", "L");
					}
					colPepSeqs.add(sequence);
				}
				
				// Merge meta-proteins if at least one overlapping element exists (weak similarity criterion)
				if (!Collections.disjoint(colPepSeqs, rowPepSeqs)) {
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
		
	}
	
	/**
	 * Sets the taxonomy of meta-proteins contained in the specified list to the
	 * common taxonomy based on their child protein taxonomies.
	 * @param metaProteins the list of meta-proteins for which common protein
	 *  taxonomies shall be determined
	 */
	// TODO: this method is very similar to TaxonomyUtils#determineProteinTaxonomy, maybe merge somehow
	public static void determineMetaProteinTaxonomy(ProteinHitList metaProteins) {
		
		// Iterate meta-proteins
		for (ProteinHit ph : metaProteins) {
			MetaProteinHit mph = (MetaProteinHit) ph;
			
			// Gather taxonomy nodes of child proteins
			List<TaxonomyNode> taxonNodes = new ArrayList<TaxonomyNode>();
			for (ProteinHit proteinHit : mph.getProteinHits()) {
				taxonNodes.add(proteinHit.getTaxonomyNode());
			}
			// Find common ancestor node
			TaxonomyNode ancestor = taxonNodes.get(0);
			for (int i = 0; i < taxonNodes.size(); i++) {
				ancestor = TaxonomyUtils.getCommonTaxonomyNode(ancestor, taxonNodes.get(i));
			}
			// Set common taxon node of meta-protein
			mph.setTaxonomyNode(ancestor);
			
			// Fire progress notification
			Client.getInstance().firePropertyChange("progressmade", false, true);
		}
		
	}
	
}
