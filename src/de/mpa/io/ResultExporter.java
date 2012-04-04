package de.mpa.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.Map.Entry;

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
	 * @throws IOException
	 * 
	 */
	public static void exportProteins(String filePath, DbSearchResult expResult) throws IOException{
		
		// Init the buffered writer.
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath)));
		
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
			writer.append(proteinHit.getMolWeight() + SEP);
			writer.append(proteinHit.getPeptideCount() + SEP);
			writer.append(proteinHit.getCoverage() + SEP);
			writer.append(proteinHit.getSpecCount() + SEP);
			writer.append(proteinHit.getSpecCount() + SEP);
			
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
		}
		writer.flush();
		writer.close();
		
	}
	
	/**
	 * Returns the protein header string.
	 * @return The protein header string.
	 */
	private static String getProteinHeader(){
		return "No." + SEP + "Accession" + SEP + "Description" + SEP + "MW [kDa]" + SEP + "No. Peptides" + SEP + "Sequence Coverage [%]" + SEP + "Spectral Count" + SEP + "NSAF" + SEP + "Peptides";
	}
	
	
	
}
