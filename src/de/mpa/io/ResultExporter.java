package de.mpa.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.tree.TreePath;

import org.jdesktop.swingx.treetable.TreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;

import de.mpa.client.Constants;
import de.mpa.client.model.SpectrumMatch;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.MetaProteinHit;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.PeptideSpectrumMatch;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.model.dbsearch.ProteinHitList;
import de.mpa.client.ui.CheckBoxTreeTable;
import de.mpa.client.ui.ProteinTreeTables;
import de.mpa.db.accessor.SearchHit;

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
				if (hasFeature[11]) writer.append(proteinHit.getSequence() + Constants.TSV_FILE_SEPARATOR);
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
					if (hasFeature[9]) writer.append(peptideHit.getTaxonomyNode().getID()+ Constants.TSV_FILE_SEPARATOR);
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
					List<SearchHit> searchHits = psm.getSearchHits();
					for (SearchHit searchHit : searchHits) {
						if (hasFeature[0]) writer.append(++smCount + Constants.TSV_FILE_SEPARATOR);
						if (hasFeature[1]) writer.append(ph.getAccession() + Constants.TSV_FILE_SEPARATOR);
						if (hasFeature[2]) writer.append(peptideHit.getSequence() + Constants.TSV_FILE_SEPARATOR);
						if (hasFeature[3]) writer.append(psm.getTitle() + Constants.TSV_FILE_SEPARATOR);
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
	public void exportTaxonomy(String filePath, DbSearchResult result, List<ExportHeader> exportHeaders) throws IOException {
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
		
		this.writeTaxonomyResult(writer, hasFeature);
		writer.flush();
		writer.close();
	}
	
	/**
	 * Writes the result of the taxonomy (supporting Krona Plot).
	 * @param writer
	 * @param hasFeature
	 * @throws IOException
	 */
	private void writeTaxonomyResult(BufferedWriter writer, boolean[] hasFeature) throws IOException {
		Map<String, Integer> rankMap = new LinkedHashMap<String, Integer>();
		rankMap.put("No rank", 0);
		rankMap.put("Superkingdom", 1);
		rankMap.put("Kingdom", 2);
		rankMap.put("Phylum", 3);
		rankMap.put("Class", 4);
		rankMap.put("Order", 5);
		rankMap.put("Family", 6);
		rankMap.put("Genus", 7);
		rankMap.put("Species", 8);
		rankMap.put("Subspecies", 9);
		List<String> ranks = new ArrayList<String>(rankMap.keySet());
		ranks.remove(0);
		
		CheckBoxTreeTable treeTbl = ProteinTreeTables.TAXONOMY.getTreeTable();
		TreeTableModel tableModel = treeTbl.getTreeTableModel();
		
		// Store expansion state and expand whole tree.
		Enumeration<?> expanded = treeTbl.getExpandedDescendants(new TreePath(tableModel.getRoot()));
		treeTbl.expandAll();

		int rowCount = treeTbl.getRowCount();
		int colCount = tableModel.getColumnCount();
		// Write table contents.
		for (int row = 0; row < rowCount; row++) {
			TreePath treePath = treeTbl.getPathForRow(row);
			TreeTableNode tableNode = (TreeTableNode) treePath.getLastPathComponent();
			// Export only nodes within the tree => no leaves representing proteins.
			if (!tableNode.isLeaf()) {
				boolean rankFound = false;
				boolean formatFlag = false;
				for (int col = 0; col < colCount; col++) {
					Object value = tableNode.getValueAt(col);					
					if (value != null) {
						if (treeTbl.getColumnName(col).equals("Description")) {
							if (rankMap.get(value.toString()) != null) {
								int pos = rankMap.get(value.toString());
								for (int c = 0; c <= rankMap.size(); c++) {
								if (c == pos && hasFeature[pos]) {
										TreeTableNode parent = tableNode;
										boolean stepUp = true;
										rankFound = true;
										String[] array = new String[10];
										
										while (stepUp) {
											Object parentValue = parent.getValueAt(col);
											if (rankMap.get(parentValue.toString()) != null) {
												int realPos = rankMap.get(parentValue.toString());
												array[realPos] = parent.getValueAt(col - 1).toString();
											}
											
											if (parent.getUserObject().toString().contains("Superkingdom") || parent.getUserObject().toString().contains("No rank")) {
												stepUp = false;		
												c = pos;
												for (int i = 0; i < array.length; i++) {
													String taxon = array[i];
													if (taxon == null) {
														if (i == 0) taxon = ""; 
														else taxon = "Unknown " + ranks.get(i - 1);
													}
													if (hasFeature[i])
														writer.append(taxon + Constants.TSV_FILE_SEPARATOR);
												}
											} else {
												parent = parent.getParent();
											}
										} 
									} 
								}
							}
						}
						if (rankFound) {
							formatFlag = true;
							if (tableModel.getColumnName(col).equals("PepC") && hasFeature[10]) {
								writer.append(value.toString() + Constants.TSV_FILE_SEPARATOR);
								
							}
							if (tableModel.getColumnName(col).equals("SpC")) {
								if (hasFeature[11]) {
									writer.append(value.toString() + Constants.TSV_FILE_SEPARATOR);
								}
								
								Set<Long> specificSpectrumIDs = new HashSet<Long>();
								if (hasFeature[12]) {
									Set<Long> unspecificSpectrumIDs = new HashSet<Long>();
									for (int i = 0; i < tableNode.getChildCount(); i++) {
										TreeTableNode childNode = tableNode.getChildAt(i);
										// Get all specific spectrum IDs from the children (except leaves!)
										if (!childNode.isLeaf()) {
											specificSpectrumIDs.addAll(getSpectrumIDsRecursively(childNode));
										} 
										if (childNode.isLeaf()) {
											unspecificSpectrumIDs.addAll(getSpectrumIDsRecursively(childNode));
										}
									}
									// Get the asymmetric set difference of the two sets.
									unspecificSpectrumIDs.removeAll(specificSpectrumIDs);
									writer.append(unspecificSpectrumIDs.size() + Constants.TSV_FILE_SEPARATOR);
								}
							}
						}
					}
				}
				if (formatFlag) writer.newLine();
			}
		}
		
		// Restore original tree state.
		treeTbl.collapseAll();
		while (expanded.hasMoreElements()) {
			treeTbl.expandPath((TreePath) expanded.nextElement());
		}
	}
	
	/**
	 * Retrieve all spectrum IDs from the protein hits a tree table node recursively.
	 * @param tableNode the tree table node
	 * @return the set of spectrum IDs
	 */
	private Set<Long> getSpectrumIDsRecursively(TreeTableNode tableNode) {
		Set<Long> spectrumIDs = new LinkedHashSet<Long>();
		
		if (tableNode != null) {
			if (tableNode.isLeaf()) {
				ProteinHit hit = (ProteinHit) tableNode.getUserObject();
				spectrumIDs.addAll(hit.getSpectrumIDs());
			} else {
				Enumeration<? extends TreeTableNode> children = tableNode.children();
				while (children.hasMoreElements()) {
					TreeTableNode child = children.nextElement();
					spectrumIDs.addAll(this.getSpectrumIDsRecursively((TreeTableNode) child));
				}
			}
		}
		return spectrumIDs;
	}

}
