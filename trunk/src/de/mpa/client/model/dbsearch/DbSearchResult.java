package de.mpa.client.model.dbsearch;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * This class represents the set of proteins which may hold multiple peptides for each proteinhit (represented by the accession)
 * @author T. Muth, R. Heyer
 *
 */
/**
 * @author muth
 *
 */
public class DbSearchResult {
	
	/**
	 * The project title.
	 */
	private String projectTitle;
	
	/**
	 * The experiment title.
	 */
	private String experimentTitle;
	
	/**
	 * The fastaDB.
	 */
	private String fastaDB;
	
	/**
	 *  The search date.
	 */
	private Date searchDate;
	
	/**
	 * The list of search engines.
	 */
	private List<String> searchEngines;
	
	/**
	 * The number of retrieved protein hits from the searches.
	 */
	private Map<String, ProteinHit> proteinHits = new HashMap<String, ProteinHit>();
	
	/**
	 * Constructors the project title, the experiment title and the FASTA database.
	 * @param projectTitle The project title.
	 * @param experimentTitle The experiment title.
	 * @param fastaDB The FASTA database.
	 */
	public DbSearchResult(String projectTitle, String experimentTitle, String fastaDB) {
		this.projectTitle = projectTitle;
		this.experimentTitle = experimentTitle;
		this.fastaDB = fastaDB;
	}

	/**
	 * Adding a protein to the protein hit set.
	 * @param proteinHit The ProteinHit
	 */
	public void addProtein(ProteinHit proteinHit) {

		String accession = proteinHit.getAccession();
		
		// Check if protein hit is already in the protein hit set.
		if (proteinHits.containsKey(accession)) {
				// Current protein hit
				ProteinHit currentProteinHit = proteinHits.get(accession);
				
				// Get the first - and only - peptide hit.
				PeptideHit peptideHit = proteinHit.getSinglePeptideHit();
				
				// Get the current peptide hits.
				TreeMap<String, PeptideHit> currentPeptideHits = currentProteinHit.getPeptideHits();
				
				// Check if peptide hit is not already in the protein hit
				if(!currentPeptideHits.containsKey(peptideHit.getSequence())){
					// Add the peptide hit to the protein hit.
					currentPeptideHits.put(peptideHit.getSequence(), peptideHit);
					currentProteinHit.setPeptideHits(currentPeptideHits);
				} else { // If peptide hit is already in the database check the actual PSM.
					// Returns the single PSM.
					PeptideSpectrumMatch psm = peptideHit.getSinglePeptideSpectrumMatch();
					
					PeptideHit currentPeptideHit = currentPeptideHits.get(peptideHit.getSequence());
					
					TreeMap<Long, PeptideSpectrumMatch> currentPsms = currentPeptideHit.getPeptideSpectrumMatches();
					
					// Current PSM 
					if(!currentPsms.containsKey(psm.getSpectrumId())){
						currentPsms.put(psm.getSpectrumId(), psm);
						currentPeptideHit.setPeptideSpectrumMatches(currentPsms);
						currentPeptideHits.put(currentPeptideHit.getSequence(), currentPeptideHit);
						currentProteinHit.setPeptideHits(currentPeptideHits);
					} else {
						PeptideSpectrumMatch currentPsm = currentPsms.get(psm.getSpectrumId());
						currentPsm.addSearchEngineHit(psm.getFirstSearchEngineHit());
						currentPsms.put(psm.getSpectrumId(), currentPsm);
						currentPeptideHit.setPeptideSpectrumMatches(currentPsms);
						currentPeptideHits.put(currentPeptideHit.getSequence(), currentPeptideHit);
						currentProteinHit.setPeptideHits(currentPeptideHits);
					}
				}
				proteinHits.put(accession, currentProteinHit);
				
		} else {
			proteinHits.put(accession, proteinHit);
		}
	}
	
	/**
	 * Returns the protein hit for a particular accession.
	 * @param accession
	 * @return
	 */
	public ProteinHit getProteinHit(String accession){
		return proteinHits.get(accession);
	}
	
	/**
	 * Returns the protein hits.
	 * @return The protein hits. 
	 */
	public Map<String, ProteinHit> getProteinHits(){
		return proteinHits;
	}
	
	/**
	 * Returns the project title. 
	 * @return The project title.
	 */
	public String getProjectTitle() {
		return projectTitle;
	}
	
	/**
	 * Returns the experiment title;
	 * @return The experiment title.
	 */
	public String getExperimentTitle() {
		return experimentTitle;
	}
	
	/**
	 * The FASTA database.
	 * @return The FASTA database.
	 */
	public String getFastaDB() {
		return fastaDB;
	}
	
	/**
	 * The search date.
	 * @return The search date.
	 */
	public Date getSearchDate() {
		return searchDate;
	}
	
	
	/**
	 * Sets the search date
	 * @param searchDate The search date.
	 */
	public void setSearchDate(Date searchDate) {
		this.searchDate = searchDate;
	}

	/**
	 * The list of search engines.
	 * @return The list of search engines.
	 */
	public List<String> getSearchEngines() {
		return searchEngines;
	}

	/**
	 * Sets the list of search engines
	 * @param searchEngines The list of search engines.
	 */
	public void setSearchEngines(List<String> searchEngines) {
		this.searchEngines = searchEngines;
	}
	
	
}
