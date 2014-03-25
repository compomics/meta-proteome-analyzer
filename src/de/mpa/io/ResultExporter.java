package de.mpa.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import de.mpa.analysis.taxonomy.TaxonomyNode;
import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.model.SpectrumMatch;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.MetaProteinHit;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.PeptideSpectrumMatch;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.model.dbsearch.ProteinHitList;
import de.mpa.db.accessor.SearchHit;
import de.mpa.db.accessor.Searchspectrum;
import de.mpa.db.accessor.Spectrum;

/**
 * This class holds export modes for meta-proteins, proteins, peptides, PSMs and taxonomy results.
 * @author T.Muth, R.Heyer
 *
 */
public class ResultExporter {

	public enum ExportHeaderType {
		METAPROTEINS, TAXONOMY, PROTEINS, PEPTIDES, PSMS
	}
	
	/**
	 * This method exports the meta-protein results.
	 * @param filePath The path string pointing to the target file.
	 * @param result The database search result object.
	 * @param exportHeaders The exported headers.
	 * @throws IOException
	 */
	public static void exportMetaProteins(String filePath, DbSearchResult result, List<ExportHeader> exportHeaders) throws IOException {
		// Init the buffered writer.
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath)));
		boolean hasFeature[] = new boolean[10];
		
		// Meta-protein header
		for (ExportHeader exportHeader : exportHeaders) {
			if(exportHeader.getType() == ExportHeaderType.METAPROTEINS) {
				hasFeature[exportHeader.getId() - 1] = true;
				writer.append(exportHeader.getName() + Constants.TSV_FILE_SEPARATOR);
			}
		}
		writer.newLine();
		
		// Filling the format with data
		int metaProtCount = 0;
		for (ProteinHit metaProteinHit : result.getMetaProteins()) {
			MetaProteinHit metaProtein = (MetaProteinHit) metaProteinHit;
			if (hasFeature[0]) writer.append(++metaProtCount + Constants.TSV_FILE_SEPARATOR);
			if (hasFeature[1]) writer.append(metaProtein.getAccession() + Constants.TSV_FILE_SEPARATOR);
			if (hasFeature[2]) writer.append(metaProtein.getDescription() + Constants.TSV_FILE_SEPARATOR);
			if (hasFeature[3]) writer.append(metaProtein.getTaxonomyNode().getName() + Constants.TSV_FILE_SEPARATOR);
			if (hasFeature[4]) writer.append(metaProtein.getPeptideSet().size() + Constants.TSV_FILE_SEPARATOR);
			if (hasFeature[5]) writer.append(metaProtein.getMatchSet().size() + Constants.TSV_FILE_SEPARATOR);
			if (hasFeature[6]) {
				ProteinHitList proteinHits = metaProtein.getProteinHitList();
				int i = 0;
				for (ProteinHit proteinHit : proteinHits) {
					++i;
					writer.append(proteinHit.getAccession());
					// Append separator, except the last entry
					if ((i < proteinHits.size())) {
						writer.append(", ");
					}
				}
				writer.append(Constants.TSV_FILE_SEPARATOR);
			}
			if (hasFeature[7]) {
				Set<PeptideHit> peptideSet = metaProtein.getPeptideSet();
				int j = 0;
				for (PeptideHit peptideHit : peptideSet) {
					++j;
					writer.append(peptideHit.getSequence());
					// Append separator, except the last entry
					if ((j < peptideSet.size() - 1)) {
						writer.append(", ");
					}
				}
				writer.append(Constants.TSV_FILE_SEPARATOR);
			}
			writer.newLine();
			writer.flush();
		}
		writer.close();
	}
	
	
	/**
	 * This method exports the protein results.
	 * @param filePath The path string pointing to the target file.
	 * @param result The database search result object.
	 * @param exportHeaders The exported headers.
	 * @throws IOException
	 */
	public static void exportProteins(String filePath, DbSearchResult result, List<ExportHeader> exportHeaders) throws IOException{
		// Init the buffered writer.
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath)));
		boolean hasFeature[] = new boolean[13];		

		// Protein header
		for (ExportHeader exportHeader : exportHeaders) {
			if(exportHeader.getType() == ExportHeaderType.PROTEINS) {
				hasFeature[exportHeader.getId() - 1] = true;
				writer.append(exportHeader.getName() + Constants.TSV_FILE_SEPARATOR);
			}
		}
		writer.newLine();
		
		// Filling the format with data
		int protCount = 0;
		for (Entry<String, ProteinHit> entry : result.getProteinHits().entrySet()) {
			if ((entry.getValue()).isSelected()) {

				ProteinHit proteinHit = (ProteinHit) entry.getValue();
				// Export not metagenomic hits
				// if(!proteinHit.getAccession().matches("^\\d*$")) {
				
				if (hasFeature[0]) writer.append(++protCount + Constants.TSV_FILE_SEPARATOR);
				if (hasFeature[1]) writer.append(proteinHit.getAccession() + Constants.TSV_FILE_SEPARATOR);
				if (hasFeature[2]) writer.append(proteinHit.getDescription() + Constants.TSV_FILE_SEPARATOR);
				if (hasFeature[3]) writer.append(proteinHit.getSpecies() + Constants.TSV_FILE_SEPARATOR);
				if (hasFeature[4]) writer.append(proteinHit.getCoverage() * 100 + Constants.TSV_FILE_SEPARATOR);
				if (hasFeature[5]) writer.append(proteinHit.getPeptideCount() + Constants.TSV_FILE_SEPARATOR);
				if (hasFeature[6]) writer.append(proteinHit.getNSAF() + Constants.TSV_FILE_SEPARATOR);
				if (hasFeature[7]) writer.append(proteinHit.getEmPAI() + Constants.TSV_FILE_SEPARATOR);
				if (hasFeature[8]) writer.append(proteinHit.getSpectralCount() + Constants.TSV_FILE_SEPARATOR);
				if (hasFeature[9]) writer.append((Math.round(proteinHit.getIsoelectricPoint() * 100.0) / 100.0) + Constants.TSV_FILE_SEPARATOR);
				if (hasFeature[10]) writer.append((Math.round(proteinHit.getMolecularWeight() * 100.0) / 100.0) + Constants.TSV_FILE_SEPARATOR);
				if (hasFeature[11]) writer.append(proteinHit.getSequence());
				if (hasFeature[12]) {
					List<PeptideHit> peptideHitList = proteinHit.getPeptideHitList();
					for (int i = 0; i < peptideHitList.size(); i++) {
						writer.append(peptideHitList.get(i).getSequence());
						// Append separater, except the last entry
						if ((i < peptideHitList.size() - 1)) {
							writer.append(", ");
						}
					}
					
					
					
					writer.append(Constants.TSV_FILE_SEPARATOR);
				}
				writer.newLine();
				writer.flush();
			}
		}
		writer.close();
	}
	
	/**
	 * This method exports the peptide results.
	 * @param filePath The path string pointing to the target file.
	 * @param result The database search result object.
	 * @param exportHeaders The exported headers.
	 * @throws IOException
	 */
	public static void exportPeptides(String filePath, DbSearchResult result, List<ExportHeader> exportHeaders) throws IOException {
		// Init the buffered writer.
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath)));
		boolean hasFeature[] = new boolean[10];		

		// Peptide header
		for (ExportHeader exportHeader : exportHeaders) {
			if(exportHeader.getType() == ExportHeaderType.PEPTIDES) {
				hasFeature[exportHeader.getId() - 1] = true;
				if (exportHeader.getId() < 4 || exportHeader.getId() > 5){
					writer.append(exportHeader.getName() + Constants.TSV_FILE_SEPARATOR);
				}
			}
		}
		writer.newLine();
		
		boolean uniquePeptidesOnly = hasFeature[3];
		boolean sharedPeptidesOnly = hasFeature[4];
		
		// The set is used to ensure that the same peptides are exported only once. 
		Set<PeptideHit> peptideSet = new HashSet<PeptideHit>();
		
		// Get the peptide hits.
		int pepCount = 0;
		List<ProteinHit> proteinHitList = result.getProteinHitList();
		for (ProteinHit proteinHit : proteinHitList) {
			for (PeptideHit peptideHit : proteinHit.getPeptideHits().values()) {				
				if (peptideHit.isSelected() && !peptideSet.contains(peptideHit)) {
					if (uniquePeptidesOnly) {
						if(peptideHit.getProteinCount() > 1) {
							continue;
						}	
					} else if (sharedPeptidesOnly) {
						if(peptideHit.getProteinCount() == 1) {
							continue;
						}
					}
						
					if (hasFeature[0]) writer.append(++pepCount + Constants.TSV_FILE_SEPARATOR);
					if (hasFeature[1]) {
						List<ProteinHit> proteinHits = peptideHit.getProteinHits();
						for (int i = 0; i < proteinHits.size(); i++) {
							ProteinHit proteinHit2 = proteinHits.get(i);
							if (i == proteinHits.size() - 1) {
								writer.append(proteinHit2.getAccession() + Constants.TSV_FILE_SEPARATOR);
							} else {
								writer.append(proteinHit2.getAccession() + ";");
							}
						}
					}
					if (hasFeature[2]) writer.append(peptideHit.getSequence() + Constants.TSV_FILE_SEPARATOR);
					if (hasFeature[5]) writer.append(peptideHit.getProteinCount() + Constants.TSV_FILE_SEPARATOR);
					if (hasFeature[6]) writer.append(peptideHit.getSpectralCount() + Constants.TSV_FILE_SEPARATOR);
					if (hasFeature[7]) {
						String name = peptideHit.getTaxonomyNode().getName();
						if (name.equals("root")) {
							name = "Unclassified";
						}
						writer.append(name + Constants.TSV_FILE_SEPARATOR);
					}
					if (hasFeature[8]) writer.append(peptideHit.getTaxonomyNode().getRank() + Constants.TSV_FILE_SEPARATOR);
					if (hasFeature[9]) writer.append(peptideHit.getTaxonomyNode().getId()+ Constants.TSV_FILE_SEPARATOR);
					writer.newLine();
					
					// Add peptide to set.
					peptideSet.add(peptideHit);
				}
			}
		}
		writer.close();
	}
	
	/**
	 * This method exports the PSM results.
	 * @param filePath The path string pointing to the target file.
	 * @param result The database search result object.
	 * @param exportHeaders The exported headers.
	 * @throws IOException
	 * @throws SQLException 
	 */
	public static void exportPSMs(String filePath, DbSearchResult result, List<ExportHeader> exportHeaders) throws IOException, SQLException {
		// Init the buffered writer.
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath)));
		
		boolean hasFeature[] = new boolean[8];		

		// Protein header
		for (ExportHeader exportHeader : exportHeaders) {
			if(exportHeader.getType() == ExportHeaderType.PSMS) {
				hasFeature[exportHeader.getId() - 1] = true;
				writer.append(exportHeader.getName() + Constants.TSV_FILE_SEPARATOR);
			}
		}
		writer.newLine();
		
		// Get the spectrum hits.
		int smCount = 0;
		
		List<ProteinHit> proteinHits = result.getProteinHitList();
		for (ProteinHit ph : proteinHits) {
			List<PeptideHit> peptideHits = ph.getPeptideHitList();
			for (PeptideHit peptideHit : peptideHits) {
				for (SpectrumMatch sm : peptideHit.getSpectrumMatches()) {
					PeptideSpectrumMatch psm = (PeptideSpectrumMatch) sm;
					// Get the spectrum id.
					Connection conn = Client.getInstance().getDatabaseConnection();
					long spectrumid = Searchspectrum.findFromSearchSpectrumID(psm.getSearchSpectrumID(), conn).getFk_spectrumid();
					String spectrumTitle = Spectrum.findFromSpectrumID(spectrumid, conn).getTitle();
					List<SearchHit> searchHits = psm.getSearchHits();
					for (SearchHit searchHit : searchHits) {
						if (hasFeature[0]) writer.append(++smCount + Constants.TSV_FILE_SEPARATOR);
						if (hasFeature[1]) writer.append(ph.getAccession() + Constants.TSV_FILE_SEPARATOR);
						if (hasFeature[2]) writer.append(peptideHit.getSequence() + Constants.TSV_FILE_SEPARATOR);
						if (hasFeature[3]) writer.append(spectrumTitle + Constants.TSV_FILE_SEPARATOR);
						if (hasFeature[4]) writer.append(psm.getCharge() + Constants.TSV_FILE_SEPARATOR);
						if (hasFeature[5]) writer.append(searchHit.getType().toString() + Constants.TSV_FILE_SEPARATOR);
						if (hasFeature[6]) writer.append(searchHit.getQvalue().doubleValue() + Constants.TSV_FILE_SEPARATOR);
						if (hasFeature[7]) writer.append(searchHit.getScore() + Constants.TSV_FILE_SEPARATOR);
						writer.newLine();
					}			
					
				}
			}		
		}
		writer.close();
	}
	
	/**
	 * This method exports the taxonomic results.
	 * @param filePath The path string pointing to the target file.
	 * @param result The database search result object.
	 * @param exportHeaders The exported headers.
	 * @throws IOException
	 */
	public static void exportTaxonomy(String filePath, DbSearchResult result, List<ExportHeader> exportHeaders) throws IOException {
		// Init the buffered writer.
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath)));
		boolean hasFeature[] = new boolean[13];		

		// Peptide header
		for (ExportHeader exportHeader : exportHeaders) {
			if(exportHeader.getType() == ExportHeaderType.TAXONOMY) {
				hasFeature[exportHeader.getId() - 1] = true;
				writer.append(exportHeader.getName() + Constants.TSV_FILE_SEPARATOR);
			}
		}
		writer.newLine();
		
		
		// The set is used to ensure that the same peptides are exported only once. 
		Set<PeptideHit> peptideSet = new HashSet<PeptideHit>();
		
		Map<String, List<PeptideHit>> unclassifiedSpecificPeptideHits = new TreeMap<String, List<PeptideHit>>();
		Map<String, List<PeptideHit>> superkingdomSpecificPeptideHits = new TreeMap<String, List<PeptideHit>>();
		Map<String, List<PeptideHit>> kingdomSpecificPeptideHits = new TreeMap<String, List<PeptideHit>>();
		Map<String, List<PeptideHit>> phylumSpecificPeptideHits = new TreeMap<String, List<PeptideHit>>();
		Map<String, List<PeptideHit>> orderSpecificPeptideHits = new TreeMap<String, List<PeptideHit>>();
		Map<String, List<PeptideHit>> classSpecificPeptideHits = new TreeMap<String, List<PeptideHit>>();
		Map<String, List<PeptideHit>> familySpecificPeptideHits = new TreeMap<String, List<PeptideHit>>();		
		Map<String, List<PeptideHit>> genusSpecificPeptideHits = new TreeMap<String, List<PeptideHit>>();
		Map<String, List<PeptideHit>> speciesSpecificPeptideHits = new TreeMap<String, List<PeptideHit>>();
		
		// Get the peptide hits and assign them to the taxonomy.
		List<ProteinHit> proteinHitList = result.getProteinHitList();
		for (ProteinHit proteinHit : proteinHitList) {
			for (PeptideHit peptideHit : proteinHit.getPeptideHits().values()) {				
				if (peptideHit.isSelected() && !peptideSet.contains(peptideHit)) {
					TaxonomyNode taxNode = peptideHit.getTaxonomyNode();					
					List<PeptideHit> peptideHits = null;
					
					if(taxNode.isRoot()) {
						if(unclassifiedSpecificPeptideHits.get("Unclassified") == null) {
							peptideHits = new ArrayList<PeptideHit>();
						} else {
							peptideHits = unclassifiedSpecificPeptideHits.get("Unclassified");							
						}
						peptideHits.add(peptideHit);
						unclassifiedSpecificPeptideHits.put("Unclassified", peptideHits);
					} else {
						String taxonGroup = taxNode.getName();
						if (taxNode.getRank().equals("superkingdom")) {						
							if(superkingdomSpecificPeptideHits.get(taxonGroup) == null) {
								peptideHits = new ArrayList<PeptideHit>();
							} else {
								peptideHits = superkingdomSpecificPeptideHits.get(taxonGroup);							
							}
							peptideHits.add(peptideHit);
							superkingdomSpecificPeptideHits.put(taxonGroup, peptideHits);
						} else if (taxNode.getRank().equals("kingdom")) {						
							if(kingdomSpecificPeptideHits.get(taxonGroup) == null) {
								peptideHits = new ArrayList<PeptideHit>();
							} else {
								peptideHits = kingdomSpecificPeptideHits.get(taxonGroup);							
							}
							peptideHits.add(peptideHit);
							kingdomSpecificPeptideHits.put(taxonGroup, peptideHits);
						} else if (taxNode.getRank().equals("phylum")) {						
							if(phylumSpecificPeptideHits.get(taxonGroup) == null) {
								peptideHits = new ArrayList<PeptideHit>();
							} else {
								peptideHits = phylumSpecificPeptideHits.get(taxonGroup);							
							}
							peptideHits.add(peptideHit);
							phylumSpecificPeptideHits.put(taxonGroup, peptideHits);
						} else if (taxNode.getRank().equals("order")) {						
							if(orderSpecificPeptideHits.get(taxonGroup) == null) {
								peptideHits = new ArrayList<PeptideHit>();
							} else {
								peptideHits = orderSpecificPeptideHits.get(taxonGroup);							
							}
							peptideHits.add(peptideHit);
							orderSpecificPeptideHits.put(taxonGroup, peptideHits);
						} else if (taxNode.getRank().equals("class")) {						
							if(classSpecificPeptideHits.get(taxonGroup) == null) {
								peptideHits = new ArrayList<PeptideHit>();
							} else {
								peptideHits = classSpecificPeptideHits.get(taxonGroup);							
							}
							peptideHits.add(peptideHit);
							classSpecificPeptideHits.put(taxonGroup, peptideHits);
						} else if (taxNode.getRank().equals("family")) {						
							if(familySpecificPeptideHits.get(taxonGroup) == null) {
								peptideHits = new ArrayList<PeptideHit>();
							} else {
								peptideHits = familySpecificPeptideHits.get(taxonGroup);							
							}
							peptideHits.add(peptideHit);
							familySpecificPeptideHits.put(taxonGroup, peptideHits);
						} else if (taxNode.getRank().equals("genus")) {						
							if(genusSpecificPeptideHits.get(taxonGroup) == null) {
								peptideHits = new ArrayList<PeptideHit>();
							} else {
								peptideHits = genusSpecificPeptideHits.get(taxonGroup);							
							}
							peptideHits.add(peptideHit);
							genusSpecificPeptideHits.put(taxonGroup, peptideHits);
						} else if (taxNode.getRank().equals("species")) {						
							if(speciesSpecificPeptideHits.get(taxonGroup) == null) {
								peptideHits = new ArrayList<PeptideHit>();
							} else {
								peptideHits = speciesSpecificPeptideHits.get(taxonGroup);							
							}
							peptideHits.add(peptideHit);
							speciesSpecificPeptideHits.put(taxonGroup, peptideHits);
						}
					}
					peptideSet.add(peptideHit);
				}
			
			}	
		}
		
		// Write unclassified
		if (hasFeature[0]) {
			Set<Entry<String, List<PeptideHit>>> entrySet = unclassifiedSpecificPeptideHits.entrySet();
			for (Entry<String, List<PeptideHit>> entry : entrySet) {
				List<PeptideHit> peptideHits = entry.getValue();
				String seps = "";
				for(int i = 1; i <= 8; i++){
					if(hasFeature[i]) seps += Constants.TSV_FILE_SEPARATOR;
				}
				writer.append(entry.getKey() + seps + Constants.TSV_FILE_SEPARATOR + peptideHits.size() + Constants.TSV_FILE_SEPARATOR + getSpectralCount(hasFeature[10], peptideHits));		
				writer.newLine();
			}
		}
		
		// Write superkingdom
		if (hasFeature[1]) {
			writeTaxonomyResult(writer, hasFeature, superkingdomSpecificPeptideHits.entrySet());
		}
				
		// Write kingdom
		if (hasFeature[2]) {
			writeTaxonomyResult(writer, hasFeature, kingdomSpecificPeptideHits.entrySet());
		}
				
		// Write phylum
		if (hasFeature[3]) {
			writeTaxonomyResult(writer, hasFeature, phylumSpecificPeptideHits.entrySet());
		}
		
		// Write order
		if (hasFeature[4]) {
			writeTaxonomyResult(writer, hasFeature, orderSpecificPeptideHits.entrySet());
		}
				
		// Write class
		if (hasFeature[5]) {
			writeTaxonomyResult(writer, hasFeature, classSpecificPeptideHits.entrySet());
		}
		
		// Write family
		if (hasFeature[6]) {
			writeTaxonomyResult(writer, hasFeature, familySpecificPeptideHits.entrySet());
		}
				
		// Write genus
		if (hasFeature[7]) {
			writeTaxonomyResult(writer, hasFeature, genusSpecificPeptideHits.entrySet());
		}
				
		// Write species
		if (hasFeature[8]) {
			writeTaxonomyResult(writer, hasFeature, speciesSpecificPeptideHits.entrySet());
		}
		writer.close();
	}
	
	/**
	 * 
	 * @param writer
	 * @param hasFeature
	 * @param entrySet
	 * @throws IOException
	 */
	private static void writeTaxonomyResult(BufferedWriter writer, boolean[] hasFeature,	Set<Entry<String, List<PeptideHit>>> entrySet) throws IOException {
		for (Entry<String, List<PeptideHit>> entry : entrySet) {
			List<PeptideHit> peptideHits = entry.getValue();				
			writer.append(Constants.TSV_FILE_SEPARATOR + formatTaxonomyPath(peptideHits.get(0).getTaxonomyNode(), hasFeature) + peptideHits.size() + Constants.TSV_FILE_SEPARATOR + getSpectralCount(hasFeature[10], peptideHits));				
			writer.newLine();
		}
	}
	
	/**
	 * Returns the summed spectral count, if feature has been enabled.
	 * @param hasFeature Spectral count header feature.
	 * @param peptideHits List of corresponding peptide hits.
	 * @return Spectral count as string.
	 */
	private static String getSpectralCount(boolean hasFeature, List<PeptideHit> peptideHits) {
		if(hasFeature) {
			int specCount = 0;
			for (PeptideHit peptideHit : peptideHits) {
				specCount += peptideHit.getSpectralCount();
			}
			return Integer.toString(specCount);
		} else 
			return "";
	}
	
	/**
	 * This method returns a formatted output for the taxonomy path and the taxonomy node itself.
	 * @param taxNode The taxonomy node for a peptide.
	 * @return Formatted taxonomy path.
	 */
	private static String formatTaxonomyPath(TaxonomyNode taxNode, boolean[] hasFeature) {
		String formattedTaxonomy = "";
		String[] ranks = {"superkingdom", "kingdom", "phylum", "order", "class", "family", "genus"};
		
		Map<String, String> taxRankMap = new HashMap<String, String>();
		List<TaxonomyNode> path = removeDuplicatesFromPath(taxNode.getPath());
		
		int pathLength = path.size();
		for (int i = 0; i < pathLength; i++) {
			TaxonomyNode node = path.get(i);
			taxRankMap.put(node.getRank().toString().toLowerCase(), node.getName());	
		}
		
		// Add parent taxnode names.
		for (int i = 0; i < ranks.length; i++) {
			if(hasFeature[i+1]) {
				String rank = ranks[i];
				if(taxRankMap.get(rank) != null) {
					formattedTaxonomy += taxRankMap.get(rank) + Constants.TSV_FILE_SEPARATOR;
				} else {
					formattedTaxonomy += "" + Constants.TSV_FILE_SEPARATOR;
				}
			}
		}	
		
		// Add the taxnode name itself.
		formattedTaxonomy += taxNode.getName() + Constants.TSV_FILE_SEPARATOR;
		
		return formattedTaxonomy;
	}
	
	/**
	 * Helper method to remove duplicate entries from the taxonomy path.
	 * @param path Taxonomy path
	 * @return Non-redundant list of taxonomy nodes
	 */
	private static List<TaxonomyNode> removeDuplicatesFromPath(TaxonomyNode[] path) {
		Set<String> dupsRemovedPath = new HashSet<String>();
		List<TaxonomyNode> nodes = new ArrayList<TaxonomyNode>();
		for (int i = 0; i < path.length; i++) {		
			String nodeId = path[i].getRank() + path[i].getName();
			if(!dupsRemovedPath.contains(nodeId)) {
				dupsRemovedPath.add(nodeId);
				nodes.add(path[i]);
			}
		}
		return nodes;
	}
}
