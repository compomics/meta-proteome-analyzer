package de.mpa.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Set;

import de.mpa.client.model.SpectrumMatch;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.ProteinHit;

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

			// Protein format.
			writer.newLine();
			writer.append(getProteinHeader());
			writer.newLine();

			int count = 1;
			for (Entry entry : expResult.getProteinHits().entrySet()){

				// Get the protein hit.
				ProteinHit proteinHit = (ProteinHit) entry.getValue();
				writer.append(count + SEP);
				writer.append(proteinHit.getAccession() + SEP);
				writer.append(proteinHit.getDescription() + SEP);
				writer.append(proteinHit.getCoverage() + SEP);
				writer.append(proteinHit.getMolecularWeight() + SEP);
				writer.append((Math.round(proteinHit.getPI() * 100) / 100) + SEP);
				writer.append(proteinHit.getPeptideCount() + SEP);
				writer.append(proteinHit.getSpectralCount() + SEP);
				writer.append(proteinHit.getEmPAI() + SEP);
				writer.append((Math.round(proteinHit.getNSAF() * 100) / 100) + SEP);

				// Get the peptide hits.
				Set<Entry<String, PeptideHit>> entrySet = proteinHit.getPeptideHits().entrySet();
				int pepCount = 1;
				for (Entry<String, PeptideHit> peptideEntry : entrySet) {
					if(pepCount < entrySet.size()){
						writer.append(pepCount + ": " + peptideEntry.getValue().getSequence() + ",");
					} else {
						writer.append(pepCount + ": " + peptideEntry.getValue().getSequence());
					}
					pepCount++;
				}
				writer.newLine();
				count++;
				writer.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			writer.close();
		}
	}

	/**
	 * This method exports the peptide results.
	 * @param filePath
	 * @param expResult
	 * @throws IOException
	 */
	public static void exportPeptides(String filePath, DbSearchResult expResult) throws IOException{
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

			// Peptide format.
			writer.newLine();
			writer.append(getPeptideHeader());
			writer.newLine();

			int count = 1;
			// Protein level
			for (Entry entry : expResult.getProteinHits().entrySet()){
				ProteinHit proteinHit = (ProteinHit) entry.getValue();
				// Peptide level
				for (PeptideHit peptideHit : proteinHit.getPeptideHitList()) {

					writer.append(count++ + SEP);
					writer.append(peptideHit.getSequence() + SEP);
					writer.append(peptideHit.getSpectrumMatches().size() + SEP);
					writer.append(proteinHit.getAccession() + SEP);
					writer.append(proteinHit.getDescription() + SEP);
					writer.append(proteinHit.getMolecularWeight() + SEP);
					writer.append((Math.round(proteinHit.getPI() * 100.0) / 100.0) + SEP);
					writer.newLine();
				}
				writer.flush();}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			writer.close();
		}
	}

	/**
	 * This method exports the psm results.
	 * @param path
	 * @param dbSearchResult
	 */
	public static void exportPSMs(String filePath, DbSearchResult expResult) throws IOException {
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

			// psm format.
			writer.newLine();
			writer.append(getPSMHeader());
			writer.newLine();

			int count = 1;
			// Protein level
			for (Entry entry : expResult.getProteinHits().entrySet()){
				ProteinHit proteinHit = (ProteinHit) entry.getValue();
				// Peptide level
				for (PeptideHit peptideHit : proteinHit.getPeptideHitList()) {
					// Peptide Spectrum Match Level
					for (SpectrumMatch sm : peptideHit.getSpectrumMatches()) {
						writer.append(count++ + SEP);
						writer.append(sm.getSearchSpectrumID() + SEP);
						writer.append(peptideHit.getSequence() + SEP);
						writer.append(proteinHit.getAccession() + SEP);
						writer.append(proteinHit.getDescription() + SEP);
						writer.append(proteinHit.getMolecularWeight() + SEP);
						writer.append((Math.round(proteinHit.getPI() * 100.0) / 100.0) + SEP);
						writer.newLine();
					}
				}
				writer.flush();}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			writer.close();
		}
	}

	/**
	 * Returns the protein header string.
	 * @return The protein header string.
	 */
	private static String getProteinHeader(){
		return  "No." + SEP + 
				"Accession" + SEP + 
				"Description" + SEP + 
				"Sequence Coverage [%]" + SEP +
				"MW [kDa]" + SEP + 
				"pI" + SEP +
				"No. Peptides" + SEP + 
				"Spectral Count" + SEP + 
				"emPAI" + SEP +
				"NSAF x 100" + SEP + 
				"Peptides";
	}

	/**
	 * Returns the peptide header string
	 * @return The peptide header string
	 */
	private static String getPeptideHeader() {
		return  "No." + SEP + 
				"Peptide Sequence" + SEP + 
				"Peptide Spectrum Matches" + SEP + 
				"Protein Accession" + SEP + 
				"Protein Description" + SEP + 
				"Protein MW [kDa]" + SEP + 
				"Protein pI" + SEP; 
	}

	/**
	 * Returns the psm header string
	 * @return The psm header string
	 */
	private static String getPSMHeader() {
		return  "No." + SEP + 
				"Search Spectrum ID" + SEP + 
				"Peptide Sequence" + SEP + 
				"Protein Accession" + SEP + 
				"Protein Description" + SEP + 
				"Protein MW [kDa]" + SEP + 
				"Protein pI" + SEP; 
	}

}
