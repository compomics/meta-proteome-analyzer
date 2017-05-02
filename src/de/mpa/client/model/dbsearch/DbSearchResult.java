package de.mpa.client.model.dbsearch;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.mpa.client.model.SearchHit;
import de.mpa.client.model.SpectrumMatch;

/**
 * This class represents the set of proteins which may hold multiple peptides
 * for each protein hit (identified by its accession number).
 * 
 * @author T. Muth, R. Heyer
 */
public class DbSearchResult implements Serializable {
	
	/**
	 * Serialization ID set to default == 1L;
	 */
	private static final long serialVersionUID = 1L; 
	
	/**
	 * Flag indicating whether this result object has not been subject to
	 * further processing.
	 */
	private boolean raw = true;
	
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
	 * The list of meta-proteins.
	 */
	private ProteinHitList metaProteins = new ProteinHitList();

	/**
	 * The list of visible meta-proteins.
	 */
	private ProteinHitList visMetaProteins;
	
	/**
	 * The total amount of spectra.
	 */
	private int totalSpectra;
	
	/**
	 * List of spectrum file paths. 
	 */
	private List<String> spectrumFilePaths;

	/**
	 * Constructs a result object from the specified project title, experiment
	 * title and FASTA database name.
	 * @param projectTitle The project title.
	 * @param experimentTitle The experiment title.
	 * @param fastaDB The FASTA database.
	 */
	public DbSearchResult(String projectTitle, String experimentTitle,	String fastaDB) {
		this.projectTitle = projectTitle;
		this.experimentTitle = experimentTitle;
		this.fastaDB = fastaDB;
		// TODO: infer search date from database
		this.searchDate = new Date();
	}

	/**
	 * Adds a protein hit to the result object.
	 * @param proteinHit the {@link ProteinHit} to add
	 */
	public void addProtein(ProteinHit proteinHit) {
		// extract elements
		PeptideHit peptideHit = proteinHit.getSinglePeptideHit();
		SpectrumMatch spectrumMatch = peptideHit.getSingleSpectrumMatch();
		SearchHit searchHit = spectrumMatch.getSearchHits().get(0);
		Set<Long> experimentIDs = proteinHit.getExperimentIDs();

		// check for existing elements
		SpectrumMatch currentSpectrumMatch =
				this.getSpectrumMatch(spectrumMatch.getSpectrumID());
		if (currentSpectrumMatch != null) {
			currentSpectrumMatch.addExperimentIDs(experimentIDs);
			spectrumMatch = currentSpectrumMatch;
		}
		
		PeptideHit currentPeptideHit =
				this.getPeptideHit(peptideHit.getSequence());
		if (currentPeptideHit != null) {
			currentPeptideHit.addExperimentIDs(experimentIDs);
			peptideHit = currentPeptideHit;
		}
		
		ProteinHit currentProteinHit = this.getProteinHit(proteinHit.getAccession());
		if (currentProteinHit != null) {
			currentProteinHit.addExperimentIDs(experimentIDs);
			proteinHit = currentProteinHit;
		} else {
			// wrap new protein in meta-protein
			MetaProteinHit mph = new MetaProteinHit(
					"Meta-Protein " + proteinHit.getAccession(), proteinHit);
			proteinHit.setMetaProteinHit(mph);
			this.metaProteins.add(mph);
		}
		
		// add elements, possibly replacing them with existing ones
		proteinHit.addPeptideHit(peptideHit);
		peptideHit.addSpectrumMatch(spectrumMatch);
		spectrumMatch.addSearchHit(searchHit);
	}
	
	/**
	 * Returns whether this result object has not been processed yet.
	 * @return <code>true</code> if this is an unprocessed result, <code>false</code> otherwise
	 */
	public boolean isRaw() {
		return raw;
	}

	/**
	 * Sets the state flag denoting whether this result object has been
	 * processed to the specified value.
	 * @param raw <code>true</code> if this result has not been processed, <code>false</code> otherwise
	 */
	public void setRaw(boolean raw) {
		this.raw = raw;
	}

	/**
	 * Returns whether this result object contains no protein hits.
	 * @return <code>true</code> if empty, <code>false</code> otherwise.
	 */
	public boolean isEmpty() {
		if (visMetaProteins == null) {
			return this.metaProteins.isEmpty();
		} 
		return this.visMetaProteins.isEmpty();
	}

	/**
	 * Returns the list of metaproteins containing grouped protein hits.
	 * @return the list of metaproteins.
	 */
	public ProteinHitList getMetaProteins() {
		return (visMetaProteins == null) ? this.metaProteins : this.visMetaProteins;
	}
	
	/**
	 * Resets the mapping of visible meta-proteins.
	 */
	public void clearVisibleMetaProteins() {
		this.visMetaProteins = null;
	}

	/**
	 * Returns the list of protein hits.
	 * 
	 * @return the list of protein hits.
	 */
	public List<ProteinHit> getProteinHitList() {
		ProteinHitList metaProteins =
				(visMetaProteins == null) ? this.metaProteins : this.visMetaProteins;
		ProteinHitList proteinHits = new ProteinHitList();
		for (ProteinHit mph : metaProteins) {
			proteinHits.addAll(((MetaProteinHit) mph).getProteinHitList());
		}
		return proteinHits;
	}

	/**
	 * Returns the map of protein hits.
	 * @return the map of protein hits
	 */
	public Map<String, ProteinHit> getProteinHits() {
		ProteinHitList metaProteins =
				(visMetaProteins == null) ? this.metaProteins : this.visMetaProteins;
		Map<String, ProteinHit> proteinHits = new LinkedHashMap<String, ProteinHit>();
		for (ProteinHit mph : metaProteins) {
			proteinHits.putAll(((MetaProteinHit) mph).getProteinHits());
		}
		return proteinHits ;
	}

	/**
	 * Returns the protein hit for a particular accession.
	 * @param accession the protein accession
	 * @return the protein hit or <code>null</code> if no such hit exists
	 */
	public ProteinHit getProteinHit(String accession) {
		ProteinHitList metaProteins =
				(visMetaProteins == null) ? this.metaProteins : this.visMetaProteins;
		for (ProteinHit mph : metaProteins) {
			ProteinHit ph = ((MetaProteinHit) mph).getProteinHit(accession);
			if (ph != null) {
				return ph;
			}
		}
		return null;
	}
	
	/**
	 * Returns the peptide hit for a particular sequence.
	 * @param sequence the peptide sequence
	 * @return the peptide hit or <code>null</code> if no such hit exists
	 */
	public PeptideHit getPeptideHit(String sequence) {
		for (ProteinHit proteinHit : this.getProteinHits().values()) {
			PeptideHit peptideHit = proteinHit.getPeptideHit(sequence);
			if (peptideHit != null) {
				return peptideHit;
			}
		}
		return null;
	}
	
	/**
	 * Returns the spectrum match for a particular search spectrum ID.
	 * @param id the search spectrum database ID
	 * @return the spectrum match or <code>null</code> if no such match exists
	 */
	public SpectrumMatch getSpectrumMatch(long id) {
		for (ProteinHit proteinHit : this.getProteinHits().values()) {
			for (PeptideHit peptideHit : proteinHit.getPeptideHits().values()) {
				SpectrumMatch spectrumMatch = peptideHit.getSpectrumMatch(id);
				if (spectrumMatch != null) {
					return spectrumMatch;
				}
			}
		}
		return null;
	}

	/**
	 * Returns the project title.
	 * @return the project title
	 */
	public String getProjectTitle() {
		return projectTitle;
	}

	/**
	 * Returns the experiment title.
	 * @return the experiment title
	 */
	public String getExperimentTitle() {
		return experimentTitle;
	}

	/**
	 * Returns the FASTA database identifier.
	 * @return the FASTA database identifier.
	 */
	public String getFastaDB() {
		return fastaDB;
	}

	/**
	 * Returns the search date.
	 * @return the search date
	 */
	public Date getSearchDate() {
		return searchDate;
	}

	/**
	 * Sets the search date
	 * @param searchDate the search date to set
	 */
	public void setSearchDate(Date searchDate) {
		this.searchDate = searchDate;
	}

	/**
	 * Returns the total amount of queried spectra.
	 * @return The total spectral count.
	 */
	public int getTotalSpectrumCount() {
		return totalSpectra;
	}
	
	/**
	 * Sets the total amount of queried spectra.
	 * @param totalSpectra The total spectral count.
	 */
	public void setTotalSpectrumCount(int totalSpectra) {
		this.totalSpectra = totalSpectra;
	}
	
	/**
	 * Returns the amount of spectrum queries associated with peptides.
	 * @return the amount of identified spectra.
	 */
	public int getIdentifiedSpectrumCount() {
		return this.getMetaProteins().getMatchSet().size();
	}
	
	/**
	 * @return the totalPeptides
	 */
	public int getDistinctPeptideCount() {
		return this.getMetaProteins().getPeptideSet().size();
	}

	/**
	 * Returns the number of peptides with PTMs.
	 * @return the number of modified peptides
	 */
	public int getModifiedPeptideCount() {
		int modifiedPeptides = 0;
		for (PeptideHit peptideHit : this.getMetaProteins().getPeptideSet()) {
			if (!peptideHit.getSequence().matches("^[A-Z]*$")) {
				modifiedPeptides++;
			}
		}
		return modifiedPeptides;
	}
	
	/**
	 * Returns the number of peptides with exactly one parent protein.
	 * @return the number of unique peptides
	 */
	public int getUniquePeptideCount() {
		int uniquePeptides = 0;
		for (PeptideHit peptideHit : this.getMetaProteins().getPeptideSet()) {
			if (peptideHit.getProteinCount() == 1) {
				uniquePeptides++;
			}
		}
		return uniquePeptides;
	}
	
	/**
	 * Sets the FDR threshold for the visible metaproteins.
	 * @param fdr the FDR threshold.
	 */
	public void setFDR(double fdr) {
		this.visMetaProteins = new ProteinHitList();
		for (ProteinHit mph : metaProteins) {
			mph.setFDR(fdr);
			if (mph.isVisible()) {
				this.visMetaProteins.add(mph);
			}
		}
	}
	
	/**
	 * Returns a list of spectrum file paths (to the original MGF files). 
	 * @return spectrumFilePaths the list of spectrum file paths.
	 */
	public List<String> getSpectrumFilePaths() {
		return spectrumFilePaths;
	}
	
	/**
	 * Sets a list of spectrum file paths.
	 * @param spectrumFilePaths list of spectrum file paths
	 */
	public void setSpectrumFilePaths(List<String> spectrumFilePaths) {
		this.spectrumFilePaths = spectrumFilePaths;
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = (obj instanceof DbSearchResult);
		if (result) {
			DbSearchResult that = (DbSearchResult) obj;
			result = this.getProjectTitle().equals(that.getProjectTitle())
					&& this.getExperimentTitle().equals(that.getExperimentTitle());
		}
		return result;
	}
	
}