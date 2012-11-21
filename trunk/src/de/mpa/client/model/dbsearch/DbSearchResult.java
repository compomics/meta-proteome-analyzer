package de.mpa.client.model.dbsearch;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents the set of proteins which may hold multiple peptides
 * for each protein hit (identified by its accession number).
 * 
 * @author T. Muth, R. Heyer
 */
public class DbSearchResult implements Serializable {

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
	 * The search date.
	 */
	private Date searchDate;

	/**
	 * The list of search engines.
	 */
	private List<String> searchEngines;

	/**
	 * The number of retrieved protein hits from the searches.
	 */
	private Map<String, ProteinHit> proteinHits = new LinkedHashMap<String, ProteinHit>();

	/**
	 * The total amount of spectra queried.
	 */
	private int totalSpectra;
	
	/**
	 * The amount of spectra with peptide associations.
	 */
	private int identifiedSpectra;
	
	private int totalPeptides;

	private int uniquePeptides;

	/**
	 * Constructs a result object from the specified project title, experiment
	 * title and FASTA database name.
	 * @param projectTitle The project title.
	 * @param experimentTitle The experiment title.
	 * @param fastaDB The FASTA database.
	 */
	public DbSearchResult(String projectTitle, String experimentTitle,
			String fastaDB) {
		this.projectTitle = projectTitle;
		this.experimentTitle = experimentTitle;
		this.fastaDB = fastaDB;
		// TODO: infer search date from database
		this.searchDate = new Date();
	}

	/**
	 * Adds a protein hit to the protein hit set.
	 * @param proteinHit The {@link ProteinHit} to add
	 */
	public void addProtein(ProteinHit proteinHit) {
		String accession = proteinHit.getAccession();

		// Get the first - and only - peptide hit
		PeptideHit peptideHit = proteinHit.getSinglePeptideHit();

		// Find current protein hit, will be null if it's a new protein
		ProteinHit currentProteinHit = proteinHits.get(accession);
		// Find current peptide hit, ideally inside current protein hit
		PeptideHit currentPeptideHit = 
			findExistingPeptide(peptideHit.getSequence(), currentProteinHit);

		// Check if protein hit is already in the protein hit set.
		if (currentProteinHit != null) {
			// Check whether peptide hit match has been found
			if (currentPeptideHit != null) {
				// Peptide hit is already stored somewhere in the result object,
				// therefore inspect new PSM
				PeptideSpectrumMatch match = 
					(PeptideSpectrumMatch) peptideHit.getSingleSpectrumMatch();

				PeptideSpectrumMatch currentMatch = 
					(PeptideSpectrumMatch) currentPeptideHit.getSpectrumMatch(
							match.getSearchSpectrumID());
				if (currentMatch != null) {
					currentMatch.addSearchEngineHit(match.getFirstSearchHit());
				} else {
					currentMatch = match;
				}
				currentPeptideHit.replaceSpectrumMatch(currentMatch);
			} else {
				// No match found, peptide hit is new
				currentPeptideHit = peptideHit;
			}
			currentProteinHit.addPeptideHit(currentPeptideHit);
		} else {
			currentProteinHit = proteinHit;
			// Check whether peptide hit match has been found
			if (currentPeptideHit != null) {
				// Link new PSM to found peptide
				currentPeptideHit.addSpectrumMatch(peptideHit
						.getSingleSpectrumMatch());
				// Link found peptide to new Protein by replacing hit map
				Map<String, PeptideHit> newPeptideHits = new LinkedHashMap<String, PeptideHit>();
				currentProteinHit.setPeptideHits(newPeptideHits);
				currentProteinHit.addPeptideHit(currentPeptideHit);
			} else {
				// No match found, both protein and peptide hits are new
				currentPeptideHit = peptideHit;
			}
		}
		// Link parent protein hit to peptide hit
		currentPeptideHit.addProteinHit(currentProteinHit);

		proteinHits.put(accession, currentProteinHit);

	}

	/**
	 * Searches all lists of peptide hits and returns the first matching
	 * occurrence of a peptide hit identified by the provided sequence.
	 * 
	 * @param sequence
	 *            the peptide sequence identifier
	 * @param first
	 *            a protein hit reference which will be searched first and
	 *            skipped later on when iterating the list of other stored
	 *            protein hits
	 * @return the first matching occurrence of the desired peptide hit or
	 *         <code>null</code> if the hit is not stored yet
	 */
	private PeptideHit findExistingPeptide(String sequence, ProteinHit first) {
		PeptideHit peptideHit = null;
		// Check provided protein hit (most likely candidate), if applicable
		if (first != null) {
			peptideHit = first.getPeptideHits().get(sequence);
			if (peptideHit != null) {
				return peptideHit;
			}
		}
		// Iterate already stored peptide hits and look for possible matches
		for (ProteinHit proteinHit : proteinHits.values()) {
			// TODO: is skipping necessary given the total number of peptides exceeds the number of peptides associated with a single protein by far? In that case the number of comparisons for skipping might be much larger than the number of extra peptide reference comparisons...
			if (proteinHit == first) {
				continue;
			}
			peptideHit = proteinHit.getPeptideHits().get(sequence);
			if (peptideHit != null) {
				// A match has been found, abort loop
				return peptideHit;
			}
		}
		return peptideHit;
	}

	/**
	 * Returns the protein hit for a particular accession.
	 * 
	 * @param accession
	 * @return
	 */
	public ProteinHit getProteinHit(String accession) {
		return proteinHits.get(accession);
	}

	/**
	 * Returns <code>true</code> if this result object contains no protein hits.
	 * 
	 * @return <code>true</code> if this result object contains no protein hits.
	 */
	public boolean isEmpty() {
		return proteinHits.isEmpty();
	}

	/**
	 * Returns the list of protein hits.
	 * 
	 * @return the list of protein hits.
	 */
	public List<ProteinHit> getProteinHitList() {
		return new ProteinHitList(proteinHits.values());
	}

	/**
	 * Returns the map of protein hits.
	 * 
	 * @return The map of protein hits.
	 */
	public Map<String, ProteinHit> getProteinHits() {
		return proteinHits;
	}

	/**
	 * Returns the project title.
	 * 
	 * @return The project title.
	 */
	public String getProjectTitle() {
		return projectTitle;
	}

	/**
	 * Returns the experiment title;
	 * 
	 * @return The experiment title.
	 */
	public String getExperimentTitle() {
		return experimentTitle;
	}

	/**
	 * The FASTA database.
	 * 
	 * @return The FASTA database.
	 */
	public String getFastaDB() {
		return fastaDB;
	}

	/**
	 * The search date.
	 * 
	 * @return The search date.
	 */
	public Date getSearchDate() {
		return searchDate;
	}

	/**
	 * Sets the search date
	 * 
	 * @param searchDate
	 *            The search date.
	 */
	public void setSearchDate(Date searchDate) {
		this.searchDate = searchDate;
	}

	/**
	 * The list of search engines.
	 * 
	 * @return The list of search engines.
	 */
	public List<String> getSearchEngines() {
		return searchEngines;
	}

	/**
	 * Sets the list of search engines
	 * 
	 * @param searchEngines
	 *            The list of search engines.
	 */
	public void setSearchEngines(List<String> searchEngines) {
		this.searchEngines = searchEngines;
	}

//	/**
//	 * Returns the {@link MascotGenericFile} associated with the specified search spectrum ID.
//	 * @param specID the search spectrum ID.
//	 * @return the spectrum file.
//	 * @throws SQLException
//	 */
//	public MascotGenericFile getMgfFromSearchSpectrumID(long specID)
//			throws SQLException {
//		for (ProteinHit protHit : this.getProteinHitList()) {
//			for (PeptideHit peptide : protHit.getPeptideHitList()) {
//				SpectrumMatch match = peptide.getSpectrumMatch(specID);
//				if (match != null) {
//					return match.getMgf();
//				}
//			}
//		}
//		// fall back to database query 
//		return Client.getInstance().getSpectrumFromSearchSpectrumID(specID);
//	}
	
	/**
	 * Returns the total amount of queried spectra.
	 * @return the total spectral count.
	 */
	public int getTotalSpectrumCount() {
		return totalSpectra;
	}
	
	/**
	 * Sets the total amount of queried spectra.
	 * @param totalSpectra the total spectral count.
	 */
	public void setTotalSpectrumCount(int totalSpectra) {
		this.totalSpectra = totalSpectra;
	}
	
	/**
	 * Returns the amount of spectrum queries associated with peptides.
	 * @return the amount of identified spectra.
	 */
	public int getIdentifiedSpectrumCount() {
		return identifiedSpectra;
	}
	
	/**
	 * Sets the amount of identified spectrum queries.
	 * @param identifiedSpectra
	 */
	public void setIdentifiedSpectrumCount(int identifiedSpectra) {
		this.identifiedSpectra = identifiedSpectra;
	}
	
	/**
	 * @return the totalPeptides
	 */
	public int getTotalPeptideCount() {
		return totalPeptides;
	}

	/**
	 * @param totalPeptides the totalPeptides to set
	 */
	public void setTotalPeptideCount(int totalPeptides) {
		this.totalPeptides = totalPeptides;
	}

	/**
	 * @return the uniquePeptides
	 */
	public int getUniquePeptideCount() {
		return uniquePeptides;
	}

	/**
	 * @param uniquePeptides the uniquePeptides to set
	 */
	public void setUniquePeptideCount(int uniquePeptides) {
		this.uniquePeptides = uniquePeptides;
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = (obj instanceof DbSearchResult);
		if (result) {
			DbSearchResult that = (DbSearchResult) obj;
			result = this.getProjectTitle().equals(that.getProjectTitle())
					&& this.getExperimentTitle().equals(
							that.getExperimentTitle());
		}
		return result;
	}

}
