package de.mpa.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.tree.TreePath;

import org.jdesktop.swingx.treetable.TreeTableModel;
import org.jdesktop.swingx.treetable.TreeTableNode;

import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.ui.sharedelements.tables.CheckBoxTreeTable;
import de.mpa.client.ui.sharedelements.tables.ProteinTreeTables;
import de.mpa.db.mysql.accessor.SearchHit;
import de.mpa.model.analysis.UniProtUtilities;
import de.mpa.model.analysis.UniProtUtilities.TaxonomyRank;
import de.mpa.model.dbsearch.DbSearchResult;
import de.mpa.model.dbsearch.MetaProteinHit;
import de.mpa.model.dbsearch.PeptideHit;
import de.mpa.model.dbsearch.PeptideSpectrumMatch;
import de.mpa.model.dbsearch.ProteinHit;
import de.mpa.model.taxonomy.TaxonomyNode;

/**
 * This class holds export modes for meta-proteins, proteins, peptides, PSMs and
 * taxonomy results.
 * 
 * @author T.Muth, R.Heyer
 *
 */
public class ResultExporter {

	public enum ExportHeaderType {
		METAPROTEINS, METAPROTEINTAXONOMY, PROTEINTAXONOMY, PROTEINS, PEPTIDES, PSMS, SPECTRA
	}

	public static <K, V extends Comparable<V>> Map<K, V> sortByValues(final Map<K, V> map) {
		Comparator<K> valueComparator = new Comparator<K>() {
			public int compare(K k1, K k2) {
				int compare = map.get(k1).compareTo(map.get(k2));
				if (compare == 0)
					return 1;
				else
					return compare * -1;
			}
		};

		Map<K, V> sortedByValues = new TreeMap<K, V>(valueComparator);
		sortedByValues.putAll(map);
		return sortedByValues;
	}

	public static void exportChordData(String filePath, DbSearchResult result, String taxLevel, String keywordCategory,
			int matrixSize, List<ExportHeader> exportHeaders) throws IOException {

		// System.out.println(taxLevel);
		// System.out.println(keywordCategory);
		// System.out.println(matrixSize);

		HashSet<String> taxOnomies = new HashSet<>();
		HashSet<String> keywords = new HashSet<>();

		HashMap<String, HashMap<String, Integer>> matrix = new HashMap<>();

		// loop metaprotein hits
		for (MetaProteinHit metaHit : result.getAllMetaProteins()) {

			TaxonomyNode taxNode = null;

			// check if the right tax level
			if (metaHit.getTaxonomyNode().getRank().name().toUpperCase().indexOf(taxLevel.toUpperCase()) >= 0) {
				taxNode = metaHit.getTaxonomyNode();
			} else {
				taxNode = metaHit.getTaxonomyNode()
						.getParentNode(UniProtUtilities.TAXONOMY_RANKS_MAP.get(taxLevel.toLowerCase()));
			}

			String taxName = " " + taxNode.getName();

			taxOnomies.add(taxName);

			HashMap<String, Integer> keywordsCol = null;

			if (matrix.containsKey(taxName)) {
				keywordsCol = matrix.get(taxName);
			} else {
				keywordsCol = new HashMap<>();
			}

			Integer spectralCount = 0;

			for (ProteinHit protHit : metaHit.getProteinHitList()) {
				for (String keyword : protHit.getUniProtEntry().getKeywords()) {
					spectralCount += protHit.getSpectralCount();
				}
			}

			for (String keyword : metaHit.getUniProtEntry().getKeywords()) {
				// System.out.println("keyword: " + keyword + " Length: " +
				// keyword.trim().length());

				if (keyword.trim().length() > 0 && UniProtUtilities.ONTOLOGY_MAP.get(keyword) != null) {
					if (UniProtUtilities.ONTOLOGY_MAP.get(keyword).getCategory().toString().toUpperCase()
							.indexOf(keywordCategory.toUpperCase()) >= 0) {
						if (keywordsCol.containsKey(keyword)) {
							keywordsCol.put(keyword, keywordsCol.get(keyword) + spectralCount);
						} else {
							keywords.add(keyword);
							keywordsCol.put(keyword, spectralCount);
						}
					}
				}
			}

			matrix.put(taxName, keywordsCol);
		}

		String[] functionsArray = new String[keywords.size()];
		String[] taxonomiesArray = new String[matrix.keySet().size()];

		int indexFunctions = 0;
		int indexTaxonomies = 0;

		Map<String, Integer> sumFunctions = new TreeMap<>();
		Map<String, Integer> sumTaxonomies = new TreeMap<>();

		for (String keyword : keywords) {
			functionsArray[indexFunctions] = keyword;
			sumFunctions.put(keyword, 0);
			indexFunctions++;
		}

		for (String tax : taxOnomies) {
			taxonomiesArray[indexTaxonomies] = tax;
			sumTaxonomies.put(tax, 0);
			indexTaxonomies++;
		}

		int[][] matrixArray = new int[taxonomiesArray.length][functionsArray.length];

		for (int i = 0; i < taxonomiesArray.length; i++) {
			for (int j = 0; j < functionsArray.length; j++) {
				if (!matrix.get(taxonomiesArray[i]).containsKey(functionsArray[j])
						&& matrix.get(taxonomiesArray[i]).get(functionsArray[j]) == null) {
					matrixArray[i][j] = 0;
				} else {

					sumTaxonomies.put(taxonomiesArray[i], sumTaxonomies.get(taxonomiesArray[i])
							+ matrix.get(taxonomiesArray[i]).get(functionsArray[j]));

					sumFunctions.put(functionsArray[j], sumFunctions.get(functionsArray[j])
							+ matrix.get(taxonomiesArray[i]).get(functionsArray[j]));

					matrixArray[i][j] = matrix.get(taxonomiesArray[i]).get(functionsArray[j]);
				}

			}
		}

		Map<String, Integer> sortedSumFunctions = sortByValues(sumFunctions);
		Map<String, Integer> sortedSumTaxonomies = sortByValues(sumTaxonomies);

		Set<String> topTax = new HashSet<>();
		Set<String> topFunc = new HashSet<>();

		int topCounterTax = 0;
		int topCounterFunc = 0;

		indexFunctions = 0;
		indexTaxonomies = 0;

		for (Map.Entry<String, Integer> taxEntry : sortedSumTaxonomies.entrySet()) {
			taxonomiesArray[indexTaxonomies] = taxEntry.getKey();
			indexTaxonomies++;
			if (topCounterTax < matrixSize) {
				topTax.add(taxEntry.getKey());
				topCounterTax++;
			}
			indexFunctions = 0;
			for (Map.Entry<String, Integer> funcEntry : sortedSumFunctions.entrySet()) {
				functionsArray[indexFunctions] = funcEntry.getKey();
				indexFunctions++;
				if (topCounterFunc < matrixSize) {
					topFunc.add(funcEntry.getKey());
					topCounterFunc++;
				}
			}
		}

		for (int i = 0; i < taxonomiesArray.length; i++) {
			for (int j = 0; j < functionsArray.length; j++) {
				if (!matrix.get(taxonomiesArray[i]).containsKey(functionsArray[j])
						&& matrix.get(taxonomiesArray[i]).get(functionsArray[j]) == null) {
					matrixArray[i][j] = 0;
				} else {
					matrixArray[i][j] = matrix.get(taxonomiesArray[i]).get(functionsArray[j]);
				}
			}
		}

		int taxMatrixSize = matrixSize;
		int funcMatrixSize = matrixSize;

		if (matrixSize >= taxonomiesArray.length) {
			taxMatrixSize = taxonomiesArray.length;
		}
		if (matrixSize >= functionsArray.length) {
			funcMatrixSize = functionsArray.length;
		}
		for (int j = 0; j < functionsArray.length; j++) {
			for (int i = taxMatrixSize + 1; i < taxonomiesArray.length; i++) {
				matrixArray[taxMatrixSize][j] = matrixArray[taxMatrixSize][j] + matrixArray[i][j];
			}
		}
		for (int i = 0; i < taxonomiesArray.length; i++) {
			for (int j = funcMatrixSize + 1; j < functionsArray.length; j++) {
				matrixArray[i][funcMatrixSize] = matrixArray[i][funcMatrixSize] + matrixArray[i][j];
			}
		}

		String otherFunc = "other Functions";
		String otherTax = " other Tax";

		String[] functionsTopArray = new String[funcMatrixSize + 1];
		String[] taxonomiesTopArray = new String[taxMatrixSize + 1];

		int[][] matrixTopArray = new int[taxMatrixSize + 1][funcMatrixSize + 1];

		if (functionsArray.length > 0 && taxonomiesArray.length > 0) {
			for (int i = 0; i < taxMatrixSize; i++) {
				taxonomiesTopArray[i] = taxonomiesArray[i];
			}

			for (int i = 0; i < funcMatrixSize; i++) {
				functionsTopArray[i] = functionsArray[i];
			}

			taxonomiesTopArray[taxonomiesTopArray.length - 1] = otherTax;

			functionsTopArray[functionsTopArray.length - 1] = otherFunc;

			for (int i = 0; i < matrixTopArray.length && i < matrixArray.length; i++) {
				for (int j = 0; j < matrixTopArray[0].length && j < matrixArray[0].length; j++) {
					matrixTopArray[i][j] = matrixArray[i][j];
				}
			}
		}

		// System.out.println();
		// System.out.print("0,");
		for (int j = 0; j < functionsArray.length; j++) {
			if (j == functionsArray.length - 1) {
				// System.out.print(functionsArray[j]);
			} else {
				// System.out.print(functionsArray[j] + ",");
			}
		}

		// System.out.print("\n");

		for (int i = 0; i < taxonomiesArray.length; i++) {
			// System.out.print(taxonomiesArray[i] + ",");
			for (int j = 0; j < functionsArray.length; j++) {
				if (j == functionsArray.length - 1) {
					// System.out.print(matrixArray[i][j]);
				} else {
					// System.out.print(matrixArray[i][j] + ",");
				}
			}
			// System.out.print("\n");
		}

		// System.out.println();

		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath)));

		boolean isOnServer = false;

		String serverFilePath = "/raid/MPA/Wordpress-Upload/chord/index/";

		File uploadFolder = new File(serverFilePath);
		if (uploadFolder.exists()) {
			isOnServer = true;
			long lastDate = System.currentTimeMillis() - 604800000;
			for (File csvFile : uploadFolder.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					if (pathname.getName().contains(".csv"))
						return true;
					return false;
				}
			})) {
				if (csvFile.getName().contains(".csv") && csvFile.lastModified() < lastDate
						&& !csvFile.getName().contains("ChordSample.csv"))
					csvFile.delete();
			}
		}

		BufferedWriter writerServer = null;
		if (isOnServer) {
			if (new File(uploadFolder.getAbsolutePath() + "/" + new File(filePath).getName()).exists()) {
				new File(uploadFolder.getAbsolutePath() + "/" + new File(filePath).getName()).delete();
			}
			writerServer = new BufferedWriter(
					new FileWriter(new File(uploadFolder.getAbsolutePath() + "/" + new File(filePath).getName())));
		}

		writer.append("0");
		writer.append(Constants.CSV_FILE_SEPARATOR);
		if (isOnServer) {
			writerServer.append("0");
			writerServer.append(Constants.CSV_FILE_SEPARATOR);
		}
		// System.out.print("0,");
		for (int j = 0; j < functionsTopArray.length; j++) {
			if (j == functionsTopArray.length - 1) {
				// System.out.print(functionsTopArray[j]);
				writer.append(functionsTopArray[j]);
				if (isOnServer) {
					writerServer.append(functionsTopArray[j]);
				}
			} else {
				// System.out.print(functionsTopArray[j] + ",");
				writer.append(functionsTopArray[j]);
				writer.append(Constants.CSV_FILE_SEPARATOR);
				if (isOnServer) {
					writerServer.append(functionsTopArray[j]);
					writerServer.append(Constants.CSV_FILE_SEPARATOR);
				}
			}
		}

		// System.out.print("\n");
		writer.newLine();

		if (isOnServer) {
			writerServer.newLine();
		}
		for (int i = 0; i < taxonomiesTopArray.length; i++) {
			writer.append(taxonomiesTopArray[i]);
			writer.append(Constants.CSV_FILE_SEPARATOR);
			if (isOnServer) {
				writerServer.append(taxonomiesTopArray[i]);
				writerServer.append(Constants.CSV_FILE_SEPARATOR);
			}
			// System.out.print(taxonomiesTopArray[i] + ",");
			for (int j = 0; j < functionsTopArray.length; j++) {
				if (j == functionsTopArray.length - 1) {
					writer.append(matrixTopArray[i][j] + "");
					// System.out.print(matrixTopArray[i][j]);
					if (isOnServer) {
						writerServer.append(matrixTopArray[i][j] + "");
					}
				} else {
					writer.append(matrixTopArray[i][j] + "");
					writer.append(Constants.CSV_FILE_SEPARATOR);
					// System.out.print(matrixTopArray[i][j] + ",");
					if (isOnServer) {
						writerServer.append(matrixTopArray[i][j] + "");
						writerServer.append(Constants.CSV_FILE_SEPARATOR);
					}
				}
			}
			writer.newLine();
			// System.out.print("\n");
			if (isOnServer) {
				writerServer.newLine();
			}
		}
		writer.flush();
		writer.close();
		if (isOnServer) {
			writerServer.flush();
			writerServer.close();
		}

		// StringBuilder csv = new StringBuilder();
		// ArrayList<String> header = new ArrayList<>();
		// System.out.println();
		// csv.append("0,");
		//
		// for (String keyword : keywords) {
		// header.add(keyword);
		// csv.append(keyword + ",");
		// }
		//
		// csv.deleteCharAt(csv.length() - 1);
		// csv.append("\n");
		//
		// for (String tax : taxOnomies) {
		//
		// csv.append(tax + ",");
		//
		// for (String keyword : header) {
		//
		// if (!matrix.get(tax).containsKey(keyword) && matrix.get(tax).get(keyword) ==
		// null) {
		// csv.append("0,");
		// } else {
		// csv.append(matrix.get(tax).get(keyword).toString() + ",");
		// }
		//
		// }
		// csv.deleteCharAt(csv.length() - 1);
		// csv.append("\n");
		//
		// }
		// System.out.println(csv.toString());

		// System.out.println("\n\n\n\n");
		// System.out.println(sumTaxonomies);
		// System.out.println(sortedSumTaxonomies);
		// System.out.println(topTax);
		// System.out.println(sumFunctions);
		// System.out.println(sortedSumFunctions);
		// System.out.println(topFunc);
		//
		// System.out.println(matrix);

	}

	/**
	 * This method exports the meta-protein results.
	 * 
	 * @param filePath
	 *            The path string pointing to the target file.
	 * @param result
	 *            The database search result object.
	 * @param exportHeaders
	 *            The exported headers.
	 * @throws IOException
	 */
	public static void exportMetaProteins(String filePath, DbSearchResult result, List<ExportHeader> exportHeaders)
			throws IOException {
		// Init the buffered writer.
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath)));
		boolean hasFeature[] = new boolean[21];
		// Meta-protein header
		for (ExportHeader exportHeader : exportHeaders) {
			if (exportHeader.getType() == ResultExporter.ExportHeaderType.METAPROTEINS) {
				hasFeature[exportHeader.getId() - 1] = true;
				writer.append(exportHeader.getName() + Constants.TSV_FILE_SEPARATOR);
			}
		}
		writer.newLine();

		// Filling the format with data
		int metaProtCount = 0;
		for (MetaProteinHit metaProtein : result.getAllMetaProteins()) {
			if (metaProtein.isVisible() && metaProtein.isSelected()) {
				if (hasFeature[0])
					writer.append(++metaProtCount + Constants.TSV_FILE_SEPARATOR);
				if (hasFeature[1])
					writer.append(metaProtein.getAccession() + Constants.TSV_FILE_SEPARATOR);
				if (hasFeature[2])
					writer.append(metaProtein.getDescription() + Constants.TSV_FILE_SEPARATOR);
				TaxonomyNode taxNode = metaProtein.getTaxonomyNode();
				if (hasFeature[3])
					writer.append(taxNode.getName() + Constants.TSV_FILE_SEPARATOR);
				// TaxTree
				TaxonomyNode spNode = taxNode.getParentNode(TaxonomyRank.SUPERKINGDOM);
				if (spNode != null && spNode.getRank() != TaxonomyRank.SUPERKINGDOM) {
					if (hasFeature[4])
						writer.append(spNode.getName() + Constants.TSV_FILE_SEPARATOR);
				} else {
					if (hasFeature[4])
						writer.append("Unknown" + Constants.TSV_FILE_SEPARATOR);
				}
				TaxonomyNode kingNode = taxNode.getParentNode(TaxonomyRank.KINGDOM);
				if (kingNode != null && kingNode.getRank() != TaxonomyRank.KINGDOM) {
					if (hasFeature[5])
						writer.append(kingNode.getName() + Constants.TSV_FILE_SEPARATOR);
				} else {
					if (hasFeature[5])
						writer.append("Unknown" + Constants.TSV_FILE_SEPARATOR);
				}
				TaxonomyNode phNode = taxNode.getParentNode(TaxonomyRank.PHYLUM);
				if (phNode != null && phNode.getRank() != TaxonomyRank.PHYLUM) {
					if (hasFeature[6])
						writer.append(phNode.getName() + Constants.TSV_FILE_SEPARATOR);
				} else {
					if (hasFeature[6])
						writer.append("Unknown" + Constants.TSV_FILE_SEPARATOR);
				}
				TaxonomyNode classNode = taxNode.getParentNode(TaxonomyRank.CLASS);
				if (classNode != null && classNode.getRank() != TaxonomyRank.CLASS) {
					if (hasFeature[7])
						writer.append(classNode.getName() + Constants.TSV_FILE_SEPARATOR);
				} else {
					if (hasFeature[7])
						writer.append("Unknown" + Constants.TSV_FILE_SEPARATOR);
				}
				TaxonomyNode orderNode = taxNode.getParentNode(TaxonomyRank.ORDER);
				if (orderNode != null && orderNode.getRank() != TaxonomyRank.ORDER) {
					if (hasFeature[8])
						writer.append(orderNode.getName() + Constants.TSV_FILE_SEPARATOR);
				} else {
					if (hasFeature[8])
						writer.append("Unknown" + Constants.TSV_FILE_SEPARATOR);
				}
				TaxonomyNode famNode = taxNode.getParentNode(TaxonomyRank.FAMILY);
				if (famNode != null && famNode.getRank() != TaxonomyRank.FAMILY) {
					if (hasFeature[9])
						writer.append(famNode.getName() + Constants.TSV_FILE_SEPARATOR);
				} else {
					if (hasFeature[9])
						writer.append("Unknown" + Constants.TSV_FILE_SEPARATOR);
				}
				TaxonomyNode genusNode = taxNode.getParentNode(TaxonomyRank.GENUS);
				if (genusNode != null && genusNode.getRank() != TaxonomyRank.GENUS) {
					if (hasFeature[10])
						writer.append(genusNode.getName() + Constants.TSV_FILE_SEPARATOR);
				} else {
					if (hasFeature[10])
						writer.append("Unknown" + Constants.TSV_FILE_SEPARATOR);
				}
				TaxonomyNode specNode = taxNode.getParentNode(TaxonomyRank.SPECIES);
				if (specNode != null && specNode.getRank() != TaxonomyRank.SPECIES) {
					if (hasFeature[11])
						writer.append(specNode.getName() + Constants.TSV_FILE_SEPARATOR);
				} else {
					if (hasFeature[11])
						writer.append("Unknown" + Constants.TSV_FILE_SEPARATOR);
				}
				String uniref100 = "UNKNOWN";
				String uniref90 = "UNKNOWN";
				String uniref50 = "UNKNOWN";
				if (metaProtein.getUniProtEntry() != null && metaProtein.getUniProtEntry().getUniRefMPA() != null
						&& metaProtein.getUniProtEntry().getUniRefMPA().getUniRef100() != null) {
					uniref100 = metaProtein.getUniProtEntry().getUniRefMPA().getUniRef100();
				} else {
					uniref100 = "unknown";
				}
				if (metaProtein.getUniProtEntry() != null && metaProtein.getUniProtEntry().getUniRefMPA() != null
						&& metaProtein.getUniProtEntry().getUniRefMPA().getUniRef90() != null) {
					uniref90 = metaProtein.getUniProtEntry().getUniRefMPA().getUniRef90();
				} else {
					uniref90 = "unknown";
				}
				if (metaProtein.getUniProtEntry() != null && metaProtein.getUniProtEntry().getUniRefMPA() != null
						&& metaProtein.getUniProtEntry().getUniRefMPA().getUniRef50() != null) {
					uniref50 = metaProtein.getUniProtEntry().getUniRefMPA().getUniRef50();
				} else {
					uniref50 = "unknown";
				}
				if (hasFeature[12])
					writer.append(uniref100 + Constants.TSV_FILE_SEPARATOR);
				if (hasFeature[13])
					writer.append(uniref90 + Constants.TSV_FILE_SEPARATOR);
				if (hasFeature[14])
					writer.append(uniref50 + Constants.TSV_FILE_SEPARATOR);
				// Get KOs
				String kOs = "";
				if (metaProtein.getUniProtEntry() != null && metaProtein.getUniProtEntry().getKonumbers() != null) {
					List<String> kNumbers = metaProtein.getUniProtEntry().getKonumbers();
					for (String ko : kNumbers) {
						kOs += ko.replace(";", "") + "|";
					}
					if (kOs.equals("")) {
						kOs = "UNKNOWN";
					}
				}
				if (hasFeature[15])
					writer.append(kOs + Constants.TSV_FILE_SEPARATOR);
				// Get ECs
				String ECs = "";
				if (metaProtein.getUniProtEntry() != null && metaProtein.getUniProtEntry().getEcnumbers() != null
						&& (!metaProtein.getUniProtEntry().getEcnumbers().isEmpty())) {
					List<String> ECNumbers = metaProtein.getUniProtEntry().getEcnumbers();
					for (String ec : ECNumbers) {

						ECs += ec.replace(";", "") + "|";
					}
				} else {
					ECs = "UNKNOWN";
				}
				if (hasFeature[8])
					writer.append(ECs + Constants.TSV_FILE_SEPARATOR);

				if (hasFeature[16])
					writer.append(metaProtein.getPeptides().size() + Constants.TSV_FILE_SEPARATOR);
				if (hasFeature[17])
					// TODO: get real spectral count
					writer.append(metaProtein.getPSMS().size() + Constants.TSV_FILE_SEPARATOR);
				if (hasFeature[18]) {
					ArrayList<ProteinHit> proteinHits = metaProtein.getProteinHitList();
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
				if (hasFeature[19]) {
					ArrayList<PeptideHit> peptideSet = metaProtein.getPeptides();
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
		}
		writer.close();
	}

	/**
	 * This method exports the meta-protein results.
	 * 
	 * @param filePath
	 *            The path string pointing to the target file.
	 * @param result
	 *            The database search result object.
	 * @param exportHeaders
	 *            The exported headers.
	 * @throws IOException
	 */
	public static void exportMetaProteinTaxonomy(String filePath, DbSearchResult result,
			List<ExportHeader> exportHeaders) throws IOException {
		// Init the buffered writer.
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath)));
		boolean hasFeature[] = new boolean[2];

		// Taxonomy header information: Always write fixed header.
		int counter = 0;
		for (ExportHeader exportHeader : exportHeaders) {
			if (exportHeader.getType() == ResultExporter.ExportHeaderType.PROTEINTAXONOMY && counter < 10) {
				writer.append(exportHeader.getName() + Constants.TSV_FILE_SEPARATOR);
				counter++;
			}
		}
		// Meta-protein header
		for (ExportHeader exportHeader : exportHeaders) {
			if (exportHeader.getType() == ResultExporter.ExportHeaderType.METAPROTEINTAXONOMY) {
				hasFeature[exportHeader.getId() - 1] = true;
				writer.append(exportHeader.getName() + Constants.TSV_FILE_SEPARATOR);
			}
		}
		writer.newLine();

		Map<TaxonomyNode, HashSet<PeptideSpectrumMatch>> taxonomySpectra = new HashMap<TaxonomyNode, HashSet<PeptideSpectrumMatch>>();
		Map<TaxonomyNode, ArrayList<PeptideHit>> taxonomyPeptides = new HashMap<TaxonomyNode, ArrayList<PeptideHit>>();

		// Collect peptides and spectra from taxonomy hits.
		for (MetaProteinHit metaProtein : result.getAllMetaProteins()) {
			if (metaProtein.isVisible() && metaProtein.isSelected()) {
				HashSet<PeptideSpectrumMatch> spectrumIDs = metaProtein.getPSMS();
				ArrayList<PeptideHit> peptideSet = metaProtein.getPeptides();
				if (taxonomySpectra.get(metaProtein.getTaxonomyNode()) != null) {
					Set<PeptideSpectrumMatch> spectraSet = taxonomySpectra.get(metaProtein.getTaxonomyNode());
					spectrumIDs.addAll(spectraSet);
				}
				if (taxonomyPeptides.get(metaProtein.getTaxonomyNode()) != null) {
					ArrayList<PeptideHit> peptideSet2 = taxonomyPeptides.get(metaProtein.getTaxonomyNode());
					peptideSet.addAll(peptideSet2);
				}
				taxonomySpectra.put(metaProtein.getTaxonomyNode(), spectrumIDs);
				taxonomyPeptides.put(metaProtein.getTaxonomyNode(), peptideSet);
			}
		}

		Map<TaxonomyRank, Integer> rankMap = new LinkedHashMap<TaxonomyRank, Integer>();
		rankMap.put(TaxonomyRank.ROOT, 0);
		rankMap.put(TaxonomyRank.SUPERKINGDOM, 1);
		rankMap.put(TaxonomyRank.KINGDOM, 2);
		rankMap.put(TaxonomyRank.PHYLUM, 3);
		rankMap.put(TaxonomyRank.CLASS, 4);
		rankMap.put(TaxonomyRank.ORDER, 5);
		rankMap.put(TaxonomyRank.FAMILY, 6);
		rankMap.put(TaxonomyRank.GENUS, 7);
		rankMap.put(TaxonomyRank.SPECIES, 8);
		rankMap.put(TaxonomyRank.SUBSPECIES, 9);
		List<TaxonomyRank> ranks = new ArrayList<TaxonomyRank>(rankMap.keySet());
		ranks.remove(0);

		Set<Entry<TaxonomyNode, HashSet<PeptideSpectrumMatch>>> entrySet = taxonomySpectra.entrySet();
		for (Entry<TaxonomyNode, HashSet<PeptideSpectrumMatch>> entry : entrySet) {
			String[] taxArray = new String[10];
			ArrayList<PeptideHit> peptides = taxonomyPeptides.get(entry.getKey());
			TaxonomyNode[] path = entry.getKey().getPath();

			// Get the real position of the taxonomic rank.
			for (TaxonomyNode taxonomyNode : path) {
				if (rankMap.get(taxonomyNode.getRank()) != null) {
					int realPos = rankMap.get(taxonomyNode.getRank());
					taxArray[realPos] = taxonomyNode.getName();
				}
			}
			for (int i = 0; i < taxArray.length; i++) {
				String taxon = taxArray[i];
				if (taxon == null) {
					if (i == 0)
						taxon = "";
					else
						taxon = "Unknown " + ranks.get(i - 1).toString();
				}
				writer.append(taxon + Constants.TSV_FILE_SEPARATOR);
			}
			if (hasFeature[0])
				writer.append(peptides.size() + Constants.TSV_FILE_SEPARATOR);
			if (hasFeature[1])
				writer.append(entry.getValue().size() + Constants.TSV_FILE_SEPARATOR);
			writer.newLine();
		}
		writer.flush();
		writer.close();
	}

	/**
	 * This method exports the protein results.
	 * 
	 * @param filePath
	 *            The path string pointing to the target file.
	 * @param result
	 *            The database search result object.
	 * @param exportHeaders
	 *            The exported headers.
	 * @throws IOException
	 */
	public static void exportProteins(String filePath, DbSearchResult result, List<ExportHeader> exportHeaders)
			throws IOException {
		// Init the buffered writer.
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath)));
		boolean hasFeature[] = new boolean[13];

		// Protein header
		for (ExportHeader exportHeader : exportHeaders) {
			if (exportHeader.getType() == ResultExporter.ExportHeaderType.PROTEINS) {
				hasFeature[exportHeader.getId() - 1] = true;
				writer.append(exportHeader.getName() + Constants.TSV_FILE_SEPARATOR);
			}
		}
		writer.newLine();

		// Filling the format with data
		int protCount = 0;
		for (ProteinHit proteinHit : result.getAllProteinHits()) {
			if (proteinHit.isVisible() && proteinHit.isSelected()) {
				// Export not metagenomic hits
				// if(!proteinHit.getAccession().matches("^\\d*$")) {
				if (hasFeature[0])
					writer.append(++protCount + Constants.TSV_FILE_SEPARATOR);
				if (hasFeature[1])
					writer.append(proteinHit.getAccession() + Constants.TSV_FILE_SEPARATOR);
				if (hasFeature[2])
					writer.append(proteinHit.getDescription() + Constants.TSV_FILE_SEPARATOR);
				if (hasFeature[3])
					writer.append(proteinHit.getTaxonomyNode().getName() + Constants.TSV_FILE_SEPARATOR);
				if (hasFeature[4])
					writer.append(proteinHit.getCoverage() * 100 + Constants.TSV_FILE_SEPARATOR);
				if (hasFeature[5])
					writer.append(proteinHit.getPeptideCount() + Constants.TSV_FILE_SEPARATOR);
				if (hasFeature[6])
					writer.append(proteinHit.getNSAF() + Constants.TSV_FILE_SEPARATOR);
				if (hasFeature[7])
					writer.append(proteinHit.getEmPAI() + Constants.TSV_FILE_SEPARATOR);
				if (hasFeature[8])
					writer.append(proteinHit.getSpectralCount() + Constants.TSV_FILE_SEPARATOR);
				if (hasFeature[9])
					writer.append((Math.round(proteinHit.getIsoelectricPoint() * 100.0) / 100.0)
							+ Constants.TSV_FILE_SEPARATOR);
				if (hasFeature[10])
					writer.append((Math.round(proteinHit.getMolecularWeight() * 100.0) / 100.0)
							+ Constants.TSV_FILE_SEPARATOR);
				if (hasFeature[11])
					writer.append(proteinHit.getSequence() + Constants.TSV_FILE_SEPARATOR);
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
	 * 
	 * @param filePath
	 *            The path string pointing to the target file.
	 * @param result
	 *            The database search result object.
	 * @param exportHeaders
	 *            The exported headers.
	 * @throws IOException
	 */
	public static void exportPeptides(String filePath, DbSearchResult result, List<ExportHeader> exportHeaders)
			throws IOException {
		// Init the buffered writer.
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath)));
		boolean hasFeature[] = new boolean[10];

		// Peptide header
		for (ExportHeader exportHeader : exportHeaders) {
			if (exportHeader.getType() == ResultExporter.ExportHeaderType.PEPTIDES) {
				hasFeature[exportHeader.getId() - 1] = true;
				// Holy Shit
				if (exportHeader.getId() < 4 || exportHeader.getId() > 5) {
					writer.append(exportHeader.getName() + Constants.TSV_FILE_SEPARATOR);
				}
			}
		}
		writer.newLine();

		boolean uniquePeptidesOnly = hasFeature[3];
		boolean sharedPeptidesOnly = hasFeature[4];

		// The set is used to ensure that the same peptides are exported only
		// once.
		Set<PeptideHit> peptideSet = new HashSet<PeptideHit>();

		// Get the peptide hits.
		int pepCount = 0;
		ArrayList<ProteinHit> proteinHitList = result.getAllProteinHits();
		for (ProteinHit proteinHit : proteinHitList) {
			for (PeptideHit peptideHit : proteinHit.getPeptideHitList()) {
				if (peptideHit.isVisible() && peptideHit.isSelected() && !peptideSet.contains(peptideHit)) {
					if (uniquePeptidesOnly) {
						if (peptideHit.getProteinCount() > 1) {
							continue;
						}
					} else if (sharedPeptidesOnly) {
						if (peptideHit.getProteinCount() == 1) {
							continue;
						}
					}

					if (hasFeature[0])
						writer.append(++pepCount + Constants.TSV_FILE_SEPARATOR);
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
					if (hasFeature[2])
						writer.append(peptideHit.getSequence() + Constants.TSV_FILE_SEPARATOR);
					if (hasFeature[5])
						writer.append(peptideHit.getProteinCount() + Constants.TSV_FILE_SEPARATOR);
					if (hasFeature[6])
						writer.append(peptideHit.getSpectralCount() + Constants.TSV_FILE_SEPARATOR);
					if (hasFeature[7]) {
						String name = peptideHit.getTaxonomyNode().getName();
						if (name.equals("root")) {
							name = "Unclassified";
						}
						writer.append(name + Constants.TSV_FILE_SEPARATOR);
					}
					if (hasFeature[8])
						writer.append(peptideHit.getTaxonomyNode().getRank() + Constants.TSV_FILE_SEPARATOR);
					if (hasFeature[9])
						writer.append(peptideHit.getTaxonomyNode().getID() + Constants.TSV_FILE_SEPARATOR);
					writer.newLine();

					// Add peptide to set.
					peptideSet.add(peptideHit);
				}
			}
			writer.flush();
		}
		writer.close();
	}

	/**
	 * This method exports the PSM results.
	 * 
	 * @param filePath
	 *            The path string pointing to the target file.
	 * @param result
	 *            The database search result object.
	 * @param exportHeaders
	 *            The exported headers.
	 * @throws IOException
	 * @throws SQLException
	 */
	public static void exportPSMs(String filePath, DbSearchResult result, List<ExportHeader> exportHeaders)
			throws IOException, SQLException {
		// Init the buffered writer.
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath)));

		boolean hasFeature[] = new boolean[8];

		// Protein header
		for (ExportHeader exportHeader : exportHeaders) {
			if (exportHeader.getType() == ResultExporter.ExportHeaderType.PSMS) {
				hasFeature[exportHeader.getId() - 1] = true;
				writer.append(exportHeader.getName() + Constants.TSV_FILE_SEPARATOR);
			}
		}
		writer.newLine();

		// Get the spectrum hits.
		int smCount = 0;

		ArrayList<PeptideSpectrumMatch> spectrumMatches = result.getAllPSMS();

		for (PeptideSpectrumMatch sm : spectrumMatches) {
			if (sm.isVisible() && sm.isSelected()) {
				MascotGenericFile mgf = Client.getInstance().getSpectrumBySpectrumID(sm.getSpectrumID());
				sm.setTitle(mgf.getTitle());
				PeptideSpectrumMatch psm = (PeptideSpectrumMatch) sm;

				PeptideHit peptideHit = sm.getPeptideHit();
				List<ProteinHit> proteinHits = peptideHit.getProteinHits();
				for (ProteinHit ph : proteinHits) {
					List<SearchHit> searchHits = psm.getSearchHits();
					for (SearchHit searchHit : searchHits) {
						if (hasFeature[0])
							writer.append(++smCount + Constants.TSV_FILE_SEPARATOR);
						if (hasFeature[1])
							writer.append(ph.getAccession() + Constants.TSV_FILE_SEPARATOR);
						if (hasFeature[2])
							writer.append(peptideHit.getSequence() + Constants.TSV_FILE_SEPARATOR);
						if (hasFeature[3])
							writer.append(psm.getTitle() + Constants.TSV_FILE_SEPARATOR);
						if (hasFeature[4])
							writer.append(psm.getCharge() + Constants.TSV_FILE_SEPARATOR);
						if (hasFeature[5])
							writer.append(searchHit.getType().toString() + Constants.TSV_FILE_SEPARATOR);
						if (hasFeature[6])
							writer.append(searchHit.getQvalue().doubleValue() + Constants.TSV_FILE_SEPARATOR);
						if (hasFeature[7])
							writer.append(searchHit.getScore() + Constants.TSV_FILE_SEPARATOR);
						writer.newLine();
					}
				}
			}
		}
		writer.flush();
		writer.close();
	}

	/**
	 * This method exports the identified spectra.
	 * 
	 * @param filePath
	 *            The path string pointing to the target file.
	 * @param result
	 *            The database search result object.
	 * @param exportHeaders
	 *            The exported headers.
	 * @throws IOException
	 * @throws SQLException
	 */
	public static void exportIdentifiedSpectra(String filePath, DbSearchResult result, List<ExportHeader> exportHeaders)
			throws IOException, SQLException {
		// Init the buffered writer.
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath)));

		boolean hasFeature[] = new boolean[5];

		// Protein header
		for (ExportHeader exportHeader : exportHeaders) {
			if (exportHeader.getType() == ResultExporter.ExportHeaderType.SPECTRA) {
				hasFeature[exportHeader.getId() - 1] = true;
				writer.append(exportHeader.getName() + Constants.TSV_FILE_SEPARATOR);
			}
		}
		writer.newLine();

		// Number of identified spectra
		int nIdentifiedSpectra = 0;

		Map<String, List<PeptideSpectrumMatch>> spectraToPSMs = new HashMap<String, List<PeptideSpectrumMatch>>();

		for (ProteinHit ph : result.getAllProteinHits()) {
			List<PeptideHit> peptideHits = ph.getPeptideHitList();
			for (PeptideHit peptideHit : peptideHits) {

				for (PeptideSpectrumMatch sm : peptideHit.getPeptideSpectrumMatches()) {
					if (sm.isVisible() && sm.isSelected()) {
						// if (!Client.isViewer()) {
						MascotGenericFile mgf = Client.getInstance().getSpectrumBySpectrumID(sm.getSpectrumID());
						sm.setTitle(mgf.getTitle());
						// }
						List<PeptideSpectrumMatch> currentPSMs = null;
						if (spectraToPSMs.get(sm.getTitle()) != null) {
							currentPSMs = spectraToPSMs.get(sm.getTitle());
						} else {
							currentPSMs = new ArrayList<PeptideSpectrumMatch>();
						}
						currentPSMs.add(sm);
						spectraToPSMs.put(sm.getTitle(), currentPSMs);
					}
				}
			}
		}

		Set<Entry<String, List<PeptideSpectrumMatch>>> entrySet = spectraToPSMs.entrySet();
		for (Entry<String, List<PeptideSpectrumMatch>> entry : entrySet) {
			if (hasFeature[0])
				writer.append(++nIdentifiedSpectra + Constants.TSV_FILE_SEPARATOR);
			List<PeptideSpectrumMatch> psms = entry.getValue();
			if (hasFeature[1])
				writer.append(psms.get(0).getSpectrumID() + Constants.TSV_FILE_SEPARATOR);
			if (hasFeature[2])
				writer.append(psms.get(0).getTitle() + Constants.TSV_FILE_SEPARATOR);

			Set<PeptideHit> allPeptides = new HashSet<PeptideHit>();

			Set<ProteinHit> allProteins = new HashSet<ProteinHit>();
			for (PeptideSpectrumMatch sm : psms) {
				PeptideHit peptideHit = sm.getPeptideHit();
				allPeptides.add(peptideHit);
				allProteins.addAll(peptideHit.getProteinHits());
			}

			List<PeptideHit> peptideHits = new ArrayList<PeptideHit>(allPeptides);
			List<ProteinHit> proteinHits = new ArrayList<ProteinHit>(allProteins);
			if (hasFeature[3]) {
				for (int i = 0; i < peptideHits.size(); i++) {
					PeptideHit peptideHit = peptideHits.get(i);
					if (i == peptideHits.size() - 1) {
						writer.append(peptideHit.getSequence() + Constants.TSV_FILE_SEPARATOR);
					} else {
						writer.append(peptideHit.getSequence() + ";");
					}
				}
			}

			if (hasFeature[4]) {
				for (int i = 0; i < proteinHits.size(); i++) {
					ProteinHit proteinHit = proteinHits.get(i);
					if (i == proteinHits.size() - 1) {
						writer.append(proteinHit.getAccession() + Constants.TSV_FILE_SEPARATOR);
					} else {
						writer.append(proteinHit.getAccession() + ";");
					}
				}

			}
			writer.newLine();
			writer.flush();
		}
		writer.close();
	}

	/**
	 * This method exports the taxonomic results.
	 * 
	 * @param filePath
	 *            The path string pointing to the target file.
	 * @param result
	 *            The database search result object.
	 * @param exportHeaders
	 *            The exported headers.
	 * @throws IOException
	 */
	public static void exportProteinTaxonomy(String filePath, DbSearchResult result, List<ExportHeader> exportHeaders)
			throws IOException {
		// Init the buffered writer.
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath)));
		boolean hasFeature[] = new boolean[13];

		// Peptide header
		for (ExportHeader exportHeader : exportHeaders) {
			if (exportHeader.getType() == ResultExporter.ExportHeaderType.PROTEINTAXONOMY) {
				hasFeature[exportHeader.getId() - 1] = true;
				writer.append(exportHeader.getName() + Constants.TSV_FILE_SEPARATOR);
			}
		}
		writer.newLine();

		ResultExporter.writeProteinTaxonomy(writer, hasFeature);
		writer.flush();
		writer.close();
	}

	/**
	 * Writes the result of the taxonomy (supporting Krona Plot).
	 * 
	 * @param writer
	 * @param hasFeature
	 * @throws IOException
	 */
	private static void writeProteinTaxonomy(BufferedWriter writer, boolean[] hasFeature) throws IOException {
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
			// Export only nodes within the tree => no leaves representing
			// proteins.
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

											if (parent.getUserObject().toString().contains("Superkingdom")
													|| parent.getUserObject().toString().contains("No rank")) {
												stepUp = false;
												c = pos;
												for (int i = 0; i < array.length; i++) {
													String taxon = array[i];
													if (taxon == null) {
														if (i == 0)
															taxon = "";
														else
															taxon = "Unknown " + ranks.get(i - 1);
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
								writer.append(value + Constants.TSV_FILE_SEPARATOR);

							}
							if (tableModel.getColumnName(col).equals("SpC")) {
								if (hasFeature[11]) {
									writer.append(value + Constants.TSV_FILE_SEPARATOR);
								}

								Set<Long> specificSpectrumIDs = new HashSet<Long>();
								if (hasFeature[12]) {
									Set<Long> unspecificSpectrumIDs = new HashSet<Long>();
									for (int i = 0; i < tableNode.getChildCount(); i++) {
										TreeTableNode childNode = tableNode.getChildAt(i);
										// Get all specific spectrum IDs from
										// the children (except leaves!)
										if (!childNode.isLeaf()) {
											specificSpectrumIDs
													.addAll(ResultExporter.getSpectrumIDsRecursively(childNode));
										}
										if (childNode.isLeaf()) {
											unspecificSpectrumIDs
													.addAll(ResultExporter.getSpectrumIDsRecursively(childNode));
										}
									}
									// Get the asymmetric set difference of the
									// two sets.
									unspecificSpectrumIDs.removeAll(specificSpectrumIDs);
									writer.append(unspecificSpectrumIDs.size() + Constants.TSV_FILE_SEPARATOR);
								}
							}
						}
					}
				}
				if (formatFlag)
					writer.newLine();
			}
		}

		// Restore original tree state.
		treeTbl.collapseAll();
		while (expanded.hasMoreElements()) {
			treeTbl.expandPath((TreePath) expanded.nextElement());
		}
	}

	/**
	 * Retrieve all spectrum IDs from the protein hits a tree table node
	 * recursively.
	 * 
	 * @param tableNode
	 *            the tree table node
	 * @return the set of spectrum IDs
	 */
	private static Set<Long> getSpectrumIDsRecursively(TreeTableNode tableNode) {
		Set<Long> spectrumIDs = new LinkedHashSet<Long>();

		if (tableNode != null) {
			if (tableNode.isLeaf()) {
				ProteinHit hit = (ProteinHit) tableNode.getUserObject();
				for (PeptideHit pep : hit.getPeptideHitList()) {
					for (PeptideSpectrumMatch psm : pep.getPeptideSpectrumMatches()) {
						spectrumIDs.add(psm.getSpectrumID());
					}
				}

			} else {
				Enumeration<? extends TreeTableNode> children = tableNode.children();
				while (children.hasMoreElements()) {
					TreeTableNode child = children.nextElement();
					spectrumIDs.addAll(ResultExporter.getSpectrumIDsRecursively(child));
				}
			}
		}
		return spectrumIDs;
	}
}
