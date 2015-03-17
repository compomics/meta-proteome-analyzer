package de.mpa.client.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;

import de.mpa.analysis.UniProtUtilities.TaxonomyRank;
import de.mpa.analysis.taxonomy.TaxonomyNode;
import de.mpa.analysis.taxonomy.TaxonomyUtils;
import de.mpa.client.Client;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.PeptideSpectrumMatch;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.model.dbsearch.ReducedUniProtEntry;
import de.mpa.client.ui.ClientFrame;
import de.mpa.db.ProjectManager;
import de.mpa.db.accessor.ExpProperty;
import de.mpa.db.accessor.ExperimentAccessor;
import de.mpa.db.accessor.PeptideAccessor;
import de.mpa.db.accessor.ProteinAccessor;
import de.mpa.db.accessor.SearchHit;
import de.mpa.db.accessor.Searchspectrum;
import de.mpa.db.accessor.Taxonomy;
import de.mpa.db.accessor.Uniprotentry;
import de.mpa.db.extractor.SearchHitExtractor;

/**
 * Implementation of the experiment interface for multiple fused database-linked experiments.
 * @author R. Heyer
 */
public class MultipleDatabaseExperiments extends AbstractExperiment{
	
	/**
	 * The search result object.
	 */
	private DbSearchResult searchResult;
	
	/**
	 * Taxonomy map containing all entries from taxonomy db table.
	 */
	private Map<Long, Taxonomy> taxonomyMap;
	
	/**
	 * The shared taxonomy node instance for undefined taxonomies.
	 */
	private TaxonomyNode unclassifiedNode;
	
	/**
	 * The list of all experiments, which should be fused
	 */
	private LinkedList<Long> experimentList;
	
	/**
	 * Constructor
	 * @param id
	 * @param title
	 * @param creationDate
	 * @param project
	 */
	public MultipleDatabaseExperiments(Long id, String title,
			Date creationDate, AbstractProject project) {
		super(id, title, creationDate, project);
		// init properties
		// init taxonomy map
		this.taxonomyMap = new HashMap<>();
	}
	
	/**
	 * Constructor
	 * @param experimentList
	 * @param title
	 * @param creationDate
	 * @param project
	 */
	public MultipleDatabaseExperiments(LinkedList<Long> experimentList, String title,
			Date creationDate, AbstractProject project) {
		super(0L, title, creationDate, project);
		this.experimentList = experimentList;
		// init taxonomy map
		this.taxonomyMap = new HashMap<>();
	}

	@Override
	public void persist(String title, Map<String, String> properties, Object... params) {
	}

	@Override
	public void update(String title, Map<String, String> properties,
			Object... params) {
		// TODO Auto-generated method stub
	}

	@Override
	public void delete() {
		try {
			ProjectManager manager = ProjectManager.getInstance();
			
			// remove experiment and all its properties
			for (Long expID : experimentList) {
				manager.deleteExperiment(expID);
			}
		} catch (SQLException e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),
					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
		}
	}

	@Override
	public boolean hasSearchResult() {
		try {
			for (long expID : experimentList) {
				return Searchspectrum.hasSearchSpectra(expID, Client.getInstance().getConnection());
			}
		} catch (SQLException e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
		};
		return false;
	}

	@Override
	public DbSearchResult getSearchResult() {
		if (searchResult == null) {
			Client client = Client.getInstance();
			try {
				// initialize the result object
				DbSearchResult searchResult = new DbSearchResult(this.getProject().getTitle(), "MultipleDBresult" +this.getTitle(), null);

				// set up progress monitoring
				client.firePropertyChange("new message", null, "QUERYING DB SEARCH HITS");
				client.firePropertyChange("resetall", 0L, 100L);
				client.firePropertyChange("indeterminate", false, true);
				
				// initialize database connection
				Connection conn = client.getConnection();

				// gather search hits from remote database
				List<SearchHit> searchHits = new LinkedList<SearchHit>() ;
				for (Long expID : experimentList) {
					searchHits.addAll(SearchHitExtractor.findSearchHitsFromExperimentID(expID, conn));
				}

				long maxProgress = searchHits.size();
				client.firePropertyChange("new message", null, "BUILDING RESULTS OBJECT");
				client.firePropertyChange("indeterminate", true, false);
				client.firePropertyChange("resetall", 0L, maxProgress);
				client.firePropertyChange("resetcur", 0L, maxProgress);
				
				
				// add search hits to result object
				for (SearchHit searchHit : searchHits) {
					this.addProteinSearchHit(searchResult, searchHit, this.getID(), conn);
					client.firePropertyChange("progressmade", true, false);
				}
				
				// determine total spectral count
				int spectralCount = 0;
					for (Long expID : experimentList) {
						spectralCount+=Searchspectrum.getSpectralCountFromExperimentID(expID, conn);
					}
					searchResult.setTotalSpectrumCount(spectralCount);

				client.firePropertyChange("new message", null, "BUILDING RESULTS OBJECT FINISHED");

				this.searchResult = searchResult;
			} catch (Exception e) {
				JXErrorPane.showDialog(ClientFrame.getInstance(),
						new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
			}
		}
		return searchResult;
	}

	@Override
	public void setSearchResult(DbSearchResult result) {
		// TODO Auto-generated method stub
	}
	
	/**
	 * This method converts a search hit into a protein hit and adds it to the current protein hit set.
	 * @param result the database search result
	 * @param hit the search hit implementation
	 * @param experimentID the experiment ID
	 */
	public void addProteinSearchHit(DbSearchResult result, SearchHit hit,
			long experimentID, Connection conn) throws Exception {

		// wrap the search hit in a new PSM
		PeptideSpectrumMatch psm = new PeptideSpectrumMatch(hit.getFk_searchspectrumid(), hit);
		
		// wrap the PSM in a new peptide
		PeptideAccessor peptide = PeptideAccessor.findFromID(hit.getFk_peptideid(), conn);
		PeptideHit peptideHit = new PeptideHit(peptide.getSequence(), psm);
		
		// retrieve the protein database entry
		long proteinID = hit.getFk_proteinid();
		ProteinAccessor protein = ProteinAccessor.findFromID(proteinID, conn);
		
		// retrieve UniProt meta-data
		Uniprotentry uniprotEntryAccessor = Uniprotentry.findFromProteinID(proteinID, conn);
		ReducedUniProtEntry uniprotEntry = null;
		TaxonomyNode taxonomyNode = null;
		// if meta-data exists...
		if (uniprotEntryAccessor != null) {
			// ... wrap it in a UniProt entry container class
			long taxID = uniprotEntryAccessor.getTaxid();
			uniprotEntry = new ReducedUniProtEntry(taxID,
					uniprotEntryAccessor.getKeywords(),
					uniprotEntryAccessor.getEcnumber(),
					uniprotEntryAccessor.getKonumber(),
					uniprotEntryAccessor.getUniref100(),
					uniprotEntryAccessor.getUniref90(),
					uniprotEntryAccessor.getUniref50());
			
			// retrieve taxonomy branch
			taxonomyNode = TaxonomyUtils.createTaxonomyNode(taxID, taxonomyMap, conn);
		} else {
			// create dummy UniProt entry
			uniprotEntry = new ReducedUniProtEntry(1, "", "", "", null, null, null);
			
			// mark taxonomy as 'unclassified'
			if (unclassifiedNode == null) {
				TaxonomyNode rootNode = new TaxonomyNode(1, TaxonomyRank.NO_RANK, "root"); 
				unclassifiedNode = new TaxonomyNode(0, TaxonomyRank.NO_RANK, "Unclassified", rootNode);
			}
			taxonomyNode = unclassifiedNode;
		}
		
		// create a new protein hit and add it to the result
		result.addProtein(new ProteinHit(
				protein.getAccession(), protein.getDescription(), protein.getSequence(),
				peptideHit, uniprotEntry, taxonomyNode, experimentID));
	}
}
