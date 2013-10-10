package de.mpa.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import de.mpa.client.Client;
import de.mpa.client.model.dbsearch.MetaProteinHit;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.model.dbsearch.ProteinHitList;
import de.mpa.taxonomy.TaxonomyNode;
import de.mpa.taxonomy.TaxonomyUtils;

/**
 * This class connects Proteinhits to metaproteins
 * @author R. Heyer
 */
public class MetaProteinFactory {

	/**
	 * Combine protein hits to metaproteins if they share all proteins.
	 * @param metaProteinList
	 * @param IequalsL. Flag whether isoleucin or leucin are distinguishable
	 * @throws Exception 
	 */
	public static void combineProteins2MetaProteins(ProteinHitList metaProteinList, boolean IdistL) throws Exception {
		
		// Go through metaproteinlist by 2 iterators (only through diagonal matrix) and check for equal peptides
		Iterator<ProteinHit> rowIter = metaProteinList.iterator();
		while (rowIter.hasNext()) {
			MetaProteinHit rowMP = (MetaProteinHit) rowIter.next();
			Set<PeptideHit> rowPS = rowMP.getPeptideSet();
			Set<String> rowPepSeq = new HashSet<String>() ;
			// Get peptide set with accession to check for similarity	
			for (PeptideHit peptideHit : rowPS) {
					String sequence = peptideHit.getSequence();
					// Similarity of leucin and isoleucin
					if (!IdistL) {
						//FIXME: Replacing I or L by IL does not make sense... and this should be handled via the PeptideHit equals() method
						sequence = sequence.replaceAll("[IL]", "IL");
					}
					rowPepSeq.add(sequence);
				}
			ListIterator<ProteinHit> colIter = metaProteinList.listIterator(metaProteinList.size());
			// Start to iterate beginning from the end to improve cpu time.
			//TODO Check for mistakes of Metaprotein generation
			while (colIter.hasPrevious()) {
				MetaProteinHit colMP = (MetaProteinHit) colIter.previous();
				// Check by accession of metaproteins
				if (rowMP == colMP) {
					break;
				}
				Set<PeptideHit> colPS = colMP.getPeptideSet();
				//								if (colPS.containsAll(rowPS) || rowPS.containsAll(colPS)) {
				
				// Sets for comparison of both sets
				Set<String> colPepSeq = new HashSet<String>() ;
				// Get peptide set with accession to check for similarity	
				for (PeptideHit peptideHit : colPS) {
						String sequence = peptideHit.getSequence();
						// Similarity of leucin and isoleucin
						if (!IdistL) {
							sequence = sequence.replaceAll("[IL]", "IL");
						}
						colPepSeq.add(sequence);
					}
				
				if (!Collections.disjoint(colPepSeq, rowPepSeq)) {
					colMP.addAll(rowMP.getProteinHits());
					rowIter.remove();
					Client.getInstance().firePropertyChange("progressmade", false, true);
					break;
				}
			}
		}
		
		// Rename Metaproteins and define common taxID
		int metaIndex = 1;
		for (ProteinHit metaProteinHit : metaProteinList) {
			// rename meta-protein
			metaProteinHit.setAccession("Meta-Protein " + metaIndex++);
			// gather protein taxonomy nodes
			List<TaxonomyNode> taxonNodes = new ArrayList<TaxonomyNode>();
			for (ProteinHit proteinHit : ((MetaProteinHit) metaProteinHit).getProteinHits()) {
				taxonNodes.add(proteinHit.getTaxonomyNode());
			}
			// find common ancestor node
			TaxonomyNode ancestor = taxonNodes.get(0);
			for (int i = 0; i < taxonNodes.size(); i++) {
				ancestor = TaxonomyUtils.getCommonTaxonomyNode(ancestor, taxonNodes.get(i));
			}
			// set peptide hit taxon node to ancestor
			metaProteinHit.setTaxonomyNode(ancestor);
			// fire progress notification
			Client.getInstance().firePropertyChange("progressmade", false, true);
		}
		
	}
}
