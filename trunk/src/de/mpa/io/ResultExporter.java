package de.mpa.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import de.mpa.client.model.SpectrumMatch;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.PeptideSpectrumMatch;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.db.accessor.SearchHit;

/**
 * This class holds basic export functionalities. 
 * @author R.Heyer, T.Muth
 *
 */
public class ResultExporter {

	/**
	 * TSV format separator.
	 */
	private static final String SEP = "\t";

	/**
	 * This method exports the protein results.
	 * @param filePath
	 * @param expResult
	 * @throws IOException
	 */
	public static void exportProteins(String filePath, DbSearchResult expResult) throws IOException{
		// Init the buffered writer.
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath)));
		try {
			// Header format.
			writer.append("Project: " + SEP + expResult.getProjectTitle());
			writer.newLine();
			writer.append("Experiment: " + SEP + expResult.getExperimentTitle());
			writer.newLine();
			writer.append("FASTA Database: " + SEP + expResult.getFastaDB());
			writer.newLine();
			writer.append("Search Engines: " + SEP);

			StringBuilder sb = new StringBuilder();
			for (String searchEngine : expResult.getSearchEngines()){
				sb.append(searchEngine);
				sb.append(",");
			}
			sb.deleteCharAt(sb.length()-1);
			writer.append(sb);
			writer.newLine();
			writer.append("Search Date: " + SEP + expResult.getSearchDate().toString());
			writer.newLine();

			// Protein format
			writer.newLine();
			writer.append(SEP + SEP + getProteinHeader());
			
			// Peptide format
			writer.newLine();
			for (int i = 0; i < 14; i++) { 	// i = number of header columns
				writer.append(SEP);			//TODO: make this column count dynamic
			}
			writer.append(getPeptideHeader());
			
			// PSM format
			writer.newLine();
			for (int i = 0; i < 18; i++) { 	// i = number of header columns
				writer.append(SEP);			//TODO: make this column count dynamic
			}
			writer.append(getPSMHeader());
			writer.newLine();
			
			// Filling the format with data
			int protCount = 0;
			for (Entry<String, ProteinHit> entry : expResult.getProteinHits().entrySet()){
				if ((entry.getValue()).isSelected()) {

					// Get the protein hit.
					ProteinHit proteinHit = (ProteinHit) entry.getValue();
					writer.append(SEP + SEP + ++protCount + SEP);
					writer.append(proteinHit.getAccession() + SEP);
					writer.append(proteinHit.getDescription() + SEP);
					writer.append(proteinHit.getSpecies() + SEP);
					writer.append(proteinHit.getCoverage() + SEP);
					writer.append(proteinHit.getMolecularWeight() + SEP);
					writer.append((Math.round(proteinHit.getPI() * 100.0) / 100.0) + SEP);
					writer.append(proteinHit.getPeptideCount() + SEP);
					writer.append(proteinHit.getSpectralCount() + SEP);
					writer.append(proteinHit.getEmPAI() + SEP);
					writer.append((Math.round(proteinHit.getNSAF() * 100.0) / 100.0) + SEP);
					writer.append(expResult.getProjectTitle() + '/' + expResult.getExperimentTitle());

					// Get the peptide hits.
					int pepCount = 0;
					for (PeptideHit peptideHit : proteinHit.getPeptideHits().values()) {
						if (peptideHit.isSelected()) {
							writer.newLine();
							for (int i = 0; i < 14; i++) { 	// i = number of header columns
															//TODO: make this column count dynamic
								writer.append(SEP);
							}
							writer.append("" + ++pepCount + SEP);
							writer.append(peptideHit.getSequence() + SEP);
							writer.append(peptideHit.getSpectralCount() + SEP);
							writer.append(expResult.getProjectTitle() + '/' + expResult.getExperimentTitle() + '/' + proteinHit.getAccession());
						
							// Get the spectrum hits.
							int smCount = 0;
							for (SpectrumMatch sm : peptideHit.getSpectrumMatches()) {
								PeptideSpectrumMatch psm = (PeptideSpectrumMatch) sm;
								List<SearchHit> searchHits = psm.getSearchHits();
								double[] qValues = { 0.0, 0.0, 0.0, 0.0 };
								for (SearchHit searchHit : searchHits) {
									switch (searchHit.getType()) {
									case XTANDEM:
										qValues[0] = 1.0 - searchHit.getQvalue().doubleValue(); break;
									case OMSSA:
										qValues[1] = 1.0 - searchHit.getQvalue().doubleValue(); break;
									case CRUX:
										qValues[2] = 1.0 - searchHit.getQvalue().doubleValue(); break;
									case INSPECT:
										qValues[3] = 1.0 - searchHit.getQvalue().doubleValue(); break;
									}
								}
								writer.newLine();
								for (int i = 0; i < 18; i++) { 	// i = number of header columns
																//TODO: make this column count dynamic
									writer.append(SEP);
								}
								writer.append("" + ++smCount + SEP);
								writer.append(psm.getCharge() + SEP);
								writer.append(psm.getVotes() + SEP);
								writer.append(expResult.getProjectTitle() + '/'
										+ expResult.getExperimentTitle() + '/' 
										+ proteinHit.getAccession() + '/'
										+ sm.getSearchSpectrumID() + SEP);
								writer.append("" + qValues[0] + SEP);
								writer.append("" + qValues[1] + SEP);
								writer.append("" + qValues[2] + SEP);
								writer.append("" + qValues[3] + SEP);
							}
						}
					}
					writer.newLine();
					writer.flush();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			writer.close();
		}
	}

//	/**
//	 * This method exports the peptide results.
//	 * @param filePath
//	 * @param expResult
//	 * @throws IOException
//	 */
//	public static void exportPeptides(String filePath, DbSearchResult expResult) throws IOException{
//		// Init the buffered writer.
//		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath)));
//		try {
//			// Header format.
//			writer.append("Project: " + SEP + expResult.getProjectTitle());
//			writer.newLine();
//			writer.append("Experiment: " + SEP + expResult.getExperimentTitle());
//			writer.newLine();
//			writer.append("FASTA Database: " + SEP + expResult.getFastaDB());
//			writer.newLine();
//			writer.append("Search Engines: " + SEP);
//
//			StringBuilder sb = new StringBuilder();
//			for (String searchEngine : expResult.getSearchEngines()){
//				sb.append(searchEngine);
//				sb.append(",");
//			}
//			sb.deleteCharAt(sb.length()-1);
//			writer.append(sb);
//			writer.newLine();
//			writer.append("Search Date: " + SEP + expResult.getSearchDate().toString());
//			writer.newLine();
//
//			// Peptide format.
//			writer.newLine();
//			writer.append(getPeptideHeader());
//			writer.newLine();
//
//			int count = 1;
//			// Protein level
//			for (Entry entry : expResult.getProteinHits().entrySet()){
//				ProteinHit proteinHit = (ProteinHit) entry.getValue();
//				// Peptide level
//				for (PeptideHit peptideHit : proteinHit.getPeptideHitList()) {
//
//					writer.append(count++ + SEP);
//					writer.append(peptideHit.getSequence() + SEP);
//					writer.append(peptideHit.getSpectralCount() + SEP);
//					writer.append(proteinHit.getAccession() + SEP);
//					writer.append(proteinHit.getDescription() + SEP);
//					writer.append(proteinHit.getMolecularWeight() + SEP);
//					writer.append((Math.round(proteinHit.getPI() * 100.0) / 100.0) + SEP);
//					writer.newLine();
//				}
//				writer.flush();}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			writer.close();
//		}
//	}
//
//	/**
//	 * This method exports the psm results.
//	 * @param path
//	 * @param dbSearchResult
//	 */
//	public static void exportPSMs(String filePath, DbSearchResult expResult) throws IOException {
//		// Init the buffered writer.
//		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath)));
//		try {
//			// Header format.
//			writer.append("Project: " + SEP + expResult.getProjectTitle());
//			writer.newLine();
//			writer.append("Experiment: " + SEP + expResult.getExperimentTitle());
//			writer.newLine();
//			writer.append("FASTA Database: " + SEP + expResult.getFastaDB());
//			writer.newLine();
//			writer.append("Search Engines: " + SEP);
//
//			StringBuilder sb = new StringBuilder();
//			for (String searchEngine : expResult.getSearchEngines()){
//				sb.append(searchEngine);
//				sb.append(",");
//			}
//			sb.deleteCharAt(sb.length()-1);
//			writer.append(sb);
//			writer.newLine();
//			writer.append("Search Date: " + SEP + expResult.getSearchDate().toString());
//			writer.newLine();
//
//			// psm format.
//			writer.newLine();
//			writer.append(getPSMHeader());
//			writer.newLine();
//
//			int count = 1;
//			// Protein level
//			for (Entry entry : expResult.getProteinHits().entrySet()){
//				ProteinHit proteinHit = (ProteinHit) entry.getValue();
//				// Peptide level
//				for (PeptideHit peptideHit : proteinHit.getPeptideHitList()) {
//					// Peptide Spectrum Match Level
//					for (SpectrumMatch sm : peptideHit.getSpectrumMatches()) {
//						writer.append(count++ + SEP);
//						writer.append(sm.getSearchSpectrumID() + SEP);
//						writer.append(peptideHit.getSequence() + SEP);
//						writer.append(proteinHit.getAccession() + SEP);
//						writer.append(proteinHit.getDescription() + SEP);
//						writer.append(proteinHit.getMolecularWeight() + SEP);
//						writer.append((Math.round(proteinHit.getPI() * 100.0) / 100.0) + SEP);
//						writer.newLine();
//					}
//				}
//				writer.flush();}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			writer.close();
//		}
//	}

	/**
	 * Returns the protein header string.
	 * @return The protein header string.
	 */
	private static String getProteinHeader(){
		return  "#" + SEP + 
				"Accession" + SEP + 
				"Description" + SEP + 
				"Species" + SEP +
				"Sequence Coverage [%]" + SEP +
				"MW [kDa]" + SEP + 
				"pI" + SEP +
				"Peptide Count" + SEP + 
				"Spectral Count" + SEP + 
				"emPAI" + SEP +
				"NSAF x 100" + SEP +
				"Tag";
	}

	/**
	 * Returns the peptide header string
	 * @return The peptide header string
	 */
	private static String getPeptideHeader() {
		return  "#" + SEP + 
				"Sequence" + SEP + 
				"Spectral Count" + SEP + 
				"Tag"; 
	}

	/**
	 * Returns the psm header string
	 * @return The psm header string
	 */
	private static String getPSMHeader() {
		return  "#" + SEP + 
				"Charge" + SEP + 
				"Votes" + SEP + 
				"Tag" + SEP + 
				"X" + SEP + 
				"O" + SEP + 
				"C" + SEP + 
				"I"; 
	}
}
