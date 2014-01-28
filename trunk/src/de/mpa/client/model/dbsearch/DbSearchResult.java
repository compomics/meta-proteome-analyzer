package de.mpa.client.model.dbsearch;

import gnu.trove.map.hash.TLongDoubleHashMap;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	 * TODO: API
	 */
	private ProteinHitList visMetaProteins;
	
	/**
	 * Map containing search spectrum id-to-TIC pairs
	 */
	private TLongDoubleHashMap ticMap;
	
	/**
	 * The total amount of spectra.
	 */
	private int totalSpectra;

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
	 * Adds a protein hit to the protein hit set.
	 * @param proteinHit The {@link ProteinHit} to add
	 * @throws Exception 
	 */
	public void addProtein(ProteinHit proteinHit) throws Exception {
		String accession = proteinHit.getAccession();

		// Get the first - and only - peptide hit
		PeptideHit peptideHit = proteinHit.getSinglePeptideHit();

		// Find current protein hit, will be null if it's a new protein
//		ProteinHit currentProteinHit = proteinHits.get(accession);
		ProteinHit currentProteinHit = this.getProteinHit(accession);
		// Find current peptide hit, ideally inside current protein hit
		PeptideHit currentPeptideHit = 
			this.findExistingPeptide(peptideHit.getSequence(), currentProteinHit);

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
				
				// Link spectrum match to peptide hit
				currentMatch.addPeptideHit(currentPeptideHit);
			} else {
				// No match found, peptide hit is new
				currentPeptideHit = peptideHit;
			}
			currentProteinHit.addPeptideHit(currentPeptideHit);
			currentProteinHit.addExperimentIDs(proteinHit.getExperimentIDs());
		} else {
			// A new protein is to be added
			currentProteinHit = proteinHit;

			MetaProteinHit mph = new MetaProteinHit("Meta-Protein", currentProteinHit);
			currentProteinHit.setMetaProteinHit(mph);
			this.metaProteins.add(mph);
			
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
				
				// Link found peptide to new Protein by replacing hit map
				Map<String, PeptideHit> newPeptideHits = new LinkedHashMap<String, PeptideHit>();
				currentProteinHit.setPeptideHits(newPeptideHits);
				currentProteinHit.addPeptideHit(currentPeptideHit);

				// Link spectrum match to peptide hit
				currentMatch.addPeptideHit(currentPeptideHit);
			} else {
				// No match found, both protein and peptide hits are new
				currentPeptideHit = peptideHit;
			}
		}
		// Link parent protein hit to peptide hit
		currentPeptideHit.addProteinHit(currentProteinHit);

//		proteinHits.put(accession, currentProteinHit);
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
		for (ProteinHit proteinHit : this.getProteinHitList()) {
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
	 * @return TODO: API
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
//		return proteinHits.get(accession);
	}

	/**
	 * Returns whether this result object contains no protein hits.
	 * @return <code>true</code> if empty, <code>false</code> otherwise.
	 */
	public boolean isEmpty() {
		if (visMetaProteins == null) {
			return this.metaProteins.isEmpty();
		} else {
			return this.visMetaProteins.isEmpty();
		}
	}

	/**
	 * Returns the list of metaproteins containing grouped protein hits.
	 * @return the list of metaproteins.
	 */
	public ProteinHitList getMetaProteins() {
		return (visMetaProteins == null) ? this.metaProteins : this.visMetaProteins;
	}
	
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
	 * 
	 * @return The map of protein hits.
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
	 * Returns the total amount of queried spectra.
	 * @return The total spectral count.
	 */
	public int getTotalSpectrumCount() {
		return totalSpectra;
	}
	
	/**
	  Sets the total amount of queried spectra.
	 * @param totalSpectra The total spectral count.
	 */
	public void setTotalSpectrumCount(int totalSpectra) {
		this.totalSpectra = totalSpectra;
	}

	/**
	 * Returns the total ion current map.
	 * @return the total ion current map
	 */
	public TLongDoubleHashMap getTotalIonCurrentMap() {
		return ticMap;
	}
	
	/**
	 * Sets the total ion current map.
	 * @param ticMap the total ion current map to set.
	 */
	public void setTotalIonCurrentMap(TLongDoubleHashMap ticMap) {
		this.ticMap = ticMap;
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
		Set<PeptideHit> peptideSet = this.getMetaProteins().getPeptideSet();
		for (PeptideHit peptideHit : peptideSet) {
			if (!peptideHit.getSequence().matches("^[A-Z]*$")) {
				modifiedPeptides++;
			}
		}
		return modifiedPeptides;
	}
	
	/**
	 * TODO: API
	 * @param fdr
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
	
	@Override
	public boolean equals(Object obj) {
		boolean result = (obj instanceof DbSearchResult);
		if (result) {
			DbSearchResult that = (DbSearchResult) obj;
			result = this.getProjectTitle().equals(that.getProjectTitle())
					&& this.getExperimentTitle().equals(that.getExperimentTitle());
			// TODO: maybe move MetaProteinParams to DbSearchResult class and use them in equals comparison
		}
		return result;
	}
	
}