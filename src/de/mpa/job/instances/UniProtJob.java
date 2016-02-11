package de.mpa.job.instances;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import de.mpa.analysis.ReducedProteinData;
import de.mpa.analysis.UniProtUtilities;
import de.mpa.client.model.dbsearch.ReducedUniProtEntry;
import de.mpa.io.GenericContainer;
import de.mpa.job.Job;
import de.mpa.job.JobStatus;
import de.mpa.util.Formatter;
import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseCrossReference;
import uk.ac.ebi.kraken.interfaces.uniprot.DatabaseType;
import uk.ac.ebi.kraken.interfaces.uniprot.Keyword;
import uk.ac.ebi.kraken.interfaces.uniprot.SecondaryUniProtAccession;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;

public class UniProtJob extends Job {
	/**
	 * Constructs a job for the UniProt querying and storing.
	 */
	public UniProtJob() {
		// Set the description
		setDescription("UNIPROT RESULTS FETCHING");
	}
	
	@Override
	public void run() {
		try {
			setStatus(JobStatus.RUNNING);
			log = Logger.getLogger(getClass());
			client.firePropertyChange("new message", null, this.getDescription() + " " + this.getStatus());
			queryAndStoreUniprotEntries(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		setStatus(JobStatus.FINISHED);
		client.firePropertyChange("new message", null, this.getDescription() + " " + this.getStatus());
	}
	
	/**
	 * Method to query and store the UniProt entries. 
	 * Retrieval is based on the UniProt JAPI.
	 * @throws Exception 
	 * @throws SQLException
	 */
	private void queryAndStoreUniprotEntries(boolean doUniRefRetrieval) throws Exception {
		Map<String, ReducedProteinData> proteinDataMap = null;
		
		// Retrieve the UniProt entries.
		Map<String, ReducedUniProtEntry> uniProtEntries = GenericContainer.UniprotQueryProteins;
		Set<String> keySet = uniProtEntries.keySet();
		List<String> accessions = new ArrayList<String>();
		int counter = 0;
		for (String string : keySet) {			
			// UniProt accession
			if (((string.length() == 6) && string.matches("[A-NR-Z][0-9][A-Z][A-Z0-9][A-Z0-9][0-9]|[OPQ][0-9][A-Z0-9][A-Z0-9][A-Z0-9][0-9]"))) {
				accessions.add(string);		
			}
		}
		log.info("Fetching " + accessions.size() + " UniProt entries...");
		if (accessions.size() > 0) {
			proteinDataMap = UniProtUtilities.retrieveProteinData(accessions, doUniRefRetrieval);
			Set<Entry<String, ReducedProteinData>> entrySet = proteinDataMap.entrySet();

			for (Entry<String, ReducedProteinData> e : entrySet) {
				String accession = e.getKey();
				ReducedProteinData proteinData = e.getValue();
				if (proteinData != null && proteinData.getUniProtEntry() != null) {
					UniProtEntry uniProtEntry = proteinData.getUniProtEntry();
					
					// Check for secondary protein accessions.
					if (!uniProtEntries.containsKey(e.getKey())) {
						List<SecondaryUniProtAccession> secondaryUniProtAccessions = uniProtEntry.getSecondaryUniProtAccessions();
						for (SecondaryUniProtAccession acc : secondaryUniProtAccessions) {
							if (uniProtEntries.containsKey(acc.getValue())) {
								accession = acc.getValue();
							}
						}
					} 
					
					// Get taxonomy id
					Long taxId = Long.valueOf(uniProtEntry.getNcbiTaxonomyIds().get(0).getValue());
					
					// Get EC Numbers.
					String ecNumbers = "";
					List<String> ecNumberList = uniProtEntry.getProteinDescription().getEcNumbers();
					if (ecNumberList.size() > 0) {
						for (String ecNumber : ecNumberList) {
							ecNumbers += ecNumber + ";";
						}
						ecNumbers = Formatter.removeLastChar(ecNumbers);
					}
					
					// Get ontology keywords.
					String keywords = "";
					List<Keyword> keywordsList = uniProtEntry.getKeywords();

					if (keywordsList.size() > 0) {
						for (Keyword kw : keywordsList) {
							keywords += kw.getValue() + ";";
						}
						keywords = Formatter.removeLastChar(keywords);
					}
					
					// Get KO numbers.
					String koNumbers = "";
					List<DatabaseCrossReference> xRefs = uniProtEntry.getDatabaseCrossReferences(DatabaseType.KO);
					if (xRefs.size() > 0) {
						for (DatabaseCrossReference xRef : xRefs) {
							koNumbers += xRef.getPrimaryId().getValue() + ";";
						}
						koNumbers = Formatter.removeLastChar(koNumbers);
					}
					
					ReducedUniProtEntry reducedUniProtEntry = new ReducedUniProtEntry(taxId, keywords, ecNumbers, koNumbers, proteinData.getUniRef100EntryId(), proteinData.getUniRef90EntryId(), proteinData.getUniRef50EntryId());
					uniProtEntries.put(accession, reducedUniProtEntry);
					counter++;
				}
			}
			log.info(counter + " UniProt entries have been retrieved.");
		}
	}
}