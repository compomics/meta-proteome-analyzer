package de.mpa.model.dbsearch;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.mpa.client.ui.sharedelements.chart.ChartType;
import de.mpa.db.mysql.accessor.SearchHit;
import de.mpa.model.taxonomy.Taxonomic;
import de.mpa.model.taxonomy.TaxonomyNode;

public class PeptideSpectrumMatch implements Serializable, Comparable<PeptideSpectrumMatch>, Taxonomic, Hit {
	
	/*
	 * FIELDS
	 */
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Flag denoting whether this match is selected for export.
	 */
	private boolean selected = true;
	
	/**
	 * The SPECTRUM ID
	 */
	private long spectrumID;
	
	/**
	 * 
	 */
	private PeptideHit peptideHit;
	
	/**
	 * The spectrum title.
	 */
	private String title;
	
	/**
	 * The database IDs of the experiments which contain the protein hit.
	 */
	private final HashSet<Long> experimentIDs;
	
	/**
	 * The ion charge.
	 */
	private int charge;
	
	/**
	 * The search engine hits.
	 */
	private ArrayList<SearchHit> searchHits;
	
	/**
	 * The visible search engine hits.
	 */
	private ArrayList<SearchHit> visSearchHits;
	
	/**
	 * Taxonomy of this Peptide
	 */
	private TaxonomyNode taxonomyNode;
	
	/*
	 * CONSTRUCTOR
	 */
	
	/**
	 * Constructor for the PeptideSpectrumMatch from a single Searchhit + Spectrum.
	 * The PeptideHit NEEDS to be provided later ...
	 * 
	 * @param spectrumid
	 * @param searchHits
	 */
	public PeptideSpectrumMatch(long spectrumid, SearchHit searchHit) {
		this.experimentIDs = new HashSet<Long>();
		this.spectrumID = spectrumid;
		this.charge = (int) searchHit.getCharge();
		this.searchHits = new ArrayList<SearchHit>();
		this.searchHits.add(searchHit);
		this.visSearchHits = new ArrayList<SearchHit>();
		this.visSearchHits.add(searchHit);
	}
	
	/*
	 * METHODS
	 * 
	 * FDR
	 */
	
	/**
	 * Sets the false discovery rate
	 * @param fdr
	 */
	@Override
	public void setFDR(double fdr) {
        visSearchHits = new ArrayList<SearchHit>();
		for (SearchHit hit : searchHits) {
			if (hit.getQvalue().doubleValue() <= fdr) {
                visSearchHits.add(hit);
			}
		}
	}
	
	@Override
	public boolean isVisible() {
		return !visSearchHits.isEmpty();
	}
	
	/*
	 * METHODS
	 * 
	 * PEPTIDE
	 */
	
	public PeptideHit getPeptideHit() {
		return peptideHit;
	}

	public void setPeptideHit(PeptideHit peptideHit) {
		this.peptideHit = peptideHit;
	}
	
	/*
	 * METHODS
	 * 
	 * SPECTRUM
	 */
	
	/**
	 * Returns the spectrum ID.
	 * @return the spectrum ID
	 */
	public long getSpectrumID() {
		return this.spectrumID;
	}
	
	/**
	 * Gets the spectrum title.
	 * @return The spectrum title.
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * Sets the spectrum title.
	 * @param The spectrum title.
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/*
	 * METHODS
	 * 
	 * SEARCHHITS
	 */
	
	/**
	 * Returns the search hit associated with the specified search engine type.
	 * @param type the search engine type
	 * @return the search hit or <code>null</code> if no such hit exists
	 */
	public SearchHit getSearchHit(SearchEngineType type) {
		for (SearchHit sh : searchHits) {
			 if (sh.getType().equals(type)) {
				 return sh;
			 }
		}
		return null;
	}
	
	/**
	 * Returns the list of search hits.
	 * @return The list of search hits.
	 */
	public ArrayList<SearchHit> getSearchHits() {
		return this.searchHits;
	}
	
	/**
	 * Returns the list of search hits.
	 * @return The list of search hits.
	 */
	public ArrayList<SearchHit> getVisSearchHits() {
		return this.visSearchHits;
	}
	
	/**
	 * Adds a search engine hit to the PSM. Checks for redundancy.
	 * @param hit Another search engine hit to be added.
	 */
	public void addSearchHit(SearchHit hit) {
		this.searchHits.add(hit);
		this.visSearchHits.add(hit);
	}
	
	/**
	 * Returns the votes.
	 * @return The number of votes for the search engine hits.
	 */
	public int getVotes() {
		return this.visSearchHits.size();
	}
	
	
	/*
	 * METHODS
	 * 
	 * GENERAL
	 */
	
	/**
	 * Returns the PSM charge.
	 * @return The PSM charge.
	 */
	public int getCharge() {
		return this.charge;
	}
	
	/**
	 * Sets a new charge for this PSM
	 * @param c  The charge provided 
	 */
	public void setCharge(int c) {
        charge = c;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		return false;
	}
	
	/**
	 * Gets the experiments IDs in which the spectrum was identified
	 * @return experiment IDs.
	 */
	public Set<Long> getExperimentIDs() {
		return this.experimentIDs;
	}
	
	/**
	 * Adds the IDs of the experiments which contain this spectrum.
	 */
	public void addExperimentIDs(Set<Long> experimentIDs) {
		this.experimentIDs.addAll(experimentIDs);
	}
	
	/**
	 * Adds a single ID of the experiment which contains this spectrum.
	 */
	public void addExperimentID(Long experimentID) {
        experimentIDs.add(experimentID);
	}

	@Override
	public boolean isSelected() {
		return this.selected;
	}
	
	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	@Override
	public int compareTo(PeptideSpectrumMatch that) {
		long delta = getSpectrumID() - that.getSpectrumID();
		return (delta < 0L) ? -1 : (delta > 0L) ? 1 : 0;
	}
	

	@Override
	public Set<Object> getProperties(ChartType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TaxonomyNode getTaxonomyNode() {
		return this.taxonomyNode;
	}

	@Override
	public void setTaxonomyNode(TaxonomyNode taxonNode) {
		this.taxonomyNode = taxonNode;
	}

	@Override
	public List<? extends Taxonomic> getTaxonomicChildren() {
		return null;
	}

}
