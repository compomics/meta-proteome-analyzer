package de.mpa.client.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
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
import de.mpa.client.ui.dialogs.GeneralDialog.Operation;
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
 * Implementation of the experiment interface for database-linked experiments.
 * 
 * @author A. Behne
 */
public class DatabaseExperiment extends AbstractExperiment {
	
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
	 * Creates an empty database-linked experiment.
	 */
	public DatabaseExperiment() {
		this(null, null, null, null, null);
	}
	
	/**
	 * Creates a database-linked experiment using the specified database
	 * accessor object, experiment properties and parent project.
	 * @param experimentAcc the database accessor wrapping the experiment
	 * @param properties the experiment property accessor objects
	 * @param project the parent project
	 */
	public DatabaseExperiment(ExperimentAccessor experimentAcc, List<ExpProperty> properties, AbstractProject project) {
		this(experimentAcc.getExperimentid(), experimentAcc.getTitle(),
				experimentAcc.getCreationdate(), properties, project);
	}

	/**
	 * Creates a database-linked experiment using the specified title, database
	 * id, experiment properties and parent project.
	 * @param title the experiment title
	 * @param id the experiment's database id
	 * @param creationDate the project's creation date
	 * @param properties the experiment property accessor objects
	 * @param project the parent project
	 */
	public DatabaseExperiment(Long id, String title, Date creationDate, List<ExpProperty> properties, AbstractProject project) {
		super(id, title, creationDate, project);
		
		// init properties
		if (properties != null) {
			for (ExpProperty property : properties) {
				this.addProperty(property.getName(), property.getValue());
			}
		}
		
		// init taxonomy map
		this.taxonomyMap = new HashMap<>();
	}
	
	@Override
	public boolean hasSearchResult() {
		try {
			return Searchspectrum.hasSearchSpectra(
					this.getID(), Client.getInstance().getConnection());
		} catch (SQLException e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),
					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
		};
		return false;
	}
	
	@Override
	public DbSearchResult getSearchResult() {
		if (searchResult == null) {
			Client client = Client.getInstance();
			try {
				// initialize the result object
				DbSearchResult searchResult = new DbSearchResult(project.getTitle(), title, null);

				// set up progress monitoring
				client.firePropertyChange("new message", null, "QUERYING DB SEARCH HITS");
				client.firePropertyChange("resetall", 0L, 100L);
				client.firePropertyChange("indeterminate", false, true);
				
				// initialize database connection
				Connection conn = client.getConnection();

				// gather search hits from remote database
				List<SearchHit> searchHits = SearchHitExtractor.findSearchHitsFromExperimentID(id, conn);

				long maxProgress = searchHits.size();
				client.firePropertyChange("new message", null, "BUILDING RESULTS OBJECT");
				client.firePropertyChange("indeterminate", true, false);
				client.firePropertyChange("resetall", 0L, maxProgress);
				client.firePropertyChange("resetcur", 0L, maxProgress);
				
				
				// add search hits to result object
				for (SearchHit searchHit : searchHits) {
					this.addProteinSearchHit(searchResult, searchHit, id, conn);

					client.firePropertyChange("progressmade", true, false);
				}
				
				// determine total spectral count
				searchResult.setTotalSpectrumCount(Searchspectrum.getSpectralCountFromExperimentID(id, conn));

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
	public void setSearchResult(DbSearchResult searchResult) {
		this.searchResult = searchResult;
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

	@Override
	public void persist(String title, Map<String, String> properties, Object... params) {
		try {
			this.title = title;
			this.properties.putAll(properties);
			
			ProjectManager manager = ProjectManager.getInstance();
	
			// create new experiment in the remote database
			ExperimentAccessor experimentAcc = manager.createNewExperiment(this.project.id, this.title);
			this.id = experimentAcc.getExperimentid();
			this.creationDate = experimentAcc.getCreationdate();
			
			// store experiment properties in the remote database
			manager.addExperimentProperties(this.id, this.properties);
			
		} catch (SQLException e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),
					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void update(String title, Map<String, String> properties, Object... params) {
		try {
			this.title = title;
			this.properties.clear();
			this.properties.putAll(properties);

			ProjectManager manager = ProjectManager.getInstance();

			// modify the experiment name
			manager.modifyExperimentName(this.id, this.title);

			// modify the experiment properties
			manager.modifyExperimentProperties(this.id, this.properties, (List<Operation>) params[0]);

		} catch (SQLException e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),
					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
		}
	}
	
	@Override
	public void delete() {
		try {
			ProjectManager manager = ProjectManager.getInstance();
			
			// remove experiment and all its properties
			manager.deleteExperiment(this.id);
			
		} catch (SQLException e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),
					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
		}
	}
	
}
