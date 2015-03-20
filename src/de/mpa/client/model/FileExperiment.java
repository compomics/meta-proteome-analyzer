package de.mpa.client.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;

import com.thoughtworks.xstream.XStream;

import de.mpa.analysis.UniProtUtilities.TaxonomyRank;
import de.mpa.analysis.taxonomy.TaxonomyNode;
import de.mpa.analysis.taxonomy.TaxonomyUtils;
import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.PeptideSpectrumMatch;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.model.dbsearch.ReducedUniProtEntry;
import de.mpa.client.ui.ClientFrame;
import de.mpa.io.GeneralParser;
import de.mpa.main.Parameters;

/**
 * Implementation of the experiment interface for file-based experiments.
 * 
 * @author A. Behne
 */
public class FileExperiment extends AbstractExperiment {
	
	/**
	 * The result file.
	 */
	private File resultFile = null;
	
	/**
	 * The spectrum file.
	 */
	private File spectrumFile;
	
	/**
	 * The search result object.
	 */
	private DbSearchResult searchResult;
	
	/**
	 * The shared taxonomy node instance for undefined taxonomies.
	 */
	private TaxonomyNode unclassifiedNode;
	

	/**
	 * Creates an empty file-based experiment.
	 */
	public FileExperiment() {
		this(null, null, null);
	}

	/**
	 * Creates a file-based experiment using the specified database accessor
	 * object, experiment properties and parent project.
	 * @param title
	 * @param creationDate
	 * @param project
	 */
	public FileExperiment(String title, Date creationDate, AbstractProject project) {
		super(null, title, creationDate, project);
	}
	
	/**
	 * Sets the result file.
	 * @param resultFile the result file to set
	 */
	public void setResultFile(File resultFile) {
		this.resultFile = resultFile;
		if ((this.getTitle() == null) && (resultFile != null)) {
			String filename = resultFile.getName();
			this.setTitle(filename.substring(0, filename.lastIndexOf('.')));
		}
	}
	
	/**
	 * Returns the spectrum file.
	 * @return the spectrum file
	 */
	public File getSpectrumFile() {
		return spectrumFile;
	}
	
	/**
	 * Sets the spectrum file.
	 * @param spectrumFile the spectrum file to set
	 */
	public void setSpectrumFile(File spectrumFile) {
		this.spectrumFile = spectrumFile;
	}
	
	@Override
	public boolean hasSearchResult() {
		return (resultFile != null) && resultFile.exists();
	}


	@Override
	public DbSearchResult getSearchResult() {
		Client client = Client.getInstance();
		if (resultFile != null) {
			client.firePropertyChange("new message", null, "READING RESULTS FILE");
			client.firePropertyChange("resetall", 0L, 100L);
			client.firePropertyChange("indeterminate", false, true);
			
			try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(resultFile))))) {
				searchResult = (DbSearchResult) ois.readObject();
				
				ClientFrame.getInstance().getGraphDatabaseResultPanel().setResultsButtonEnabled(true);
				client.firePropertyChange("new message", null, "READING RESULTS FILE FINISHED");
				
			} catch (Exception e) {
				JXErrorPane.showDialog(ClientFrame.getInstance(),
						new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
				
				client.firePropertyChange("new message", null, "READING RESULTS FILE FAILED");
			}
			client.firePropertyChange("indeterminate", true, false);
		} else {
			try {
				// Initialize the result object
				DbSearchResult searchResult = new DbSearchResult(this.getProject().getTitle(), this.getTitle(), null);
				List<SearchHit> searchHits = GeneralParser.SearchHits;
				long maxProgress = searchHits.size();
				client.firePropertyChange("new message", null, "BUILDING RESULTS OBJECT");
				client.firePropertyChange("indeterminate", true, false);
				client.firePropertyChange("resetall", 0L, maxProgress);
				client.firePropertyChange("resetcur", 0L, maxProgress);
				
				// Add search hits to result object.
				for (SearchHit searchHit : searchHits) {
					addProteinSearchHit(searchResult, searchHit, this.getID());
					client.firePropertyChange("progressmade", true, false);
				}
				
				// Determine total spectral count.
				searchResult.setTotalSpectrumCount(GeneralParser.SpectrumTitle2IdMap.size());

				client.firePropertyChange("new message", null, "BUILDING RESULTS OBJECT FINISHED");
				this.searchResult = searchResult;
			} catch (Exception e) {
				JXErrorPane.showDialog(ClientFrame.getInstance(),
						new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
			}
		}
		return searchResult;
	}
	
	/**
	 * This method converts a search hit into a protein hit and adds it to the current protein hit set.
	 * @param result the database search result
	 * @param hit the search hit implementation
	 * @param experimentID the experiment ID
	 */
	private void addProteinSearchHit(DbSearchResult result, SearchHit hit, long experimentID) throws Exception {
		// wrap the search hit in a new PSM
		PeptideSpectrumMatch psm = new PeptideSpectrumMatch(hit.getSpectrumId(), hit);
		
		// wrap the PSM in a new peptide
		PeptideHit peptideHit = new PeptideHit(hit.getPeptideSequence(), psm);
		
		// Retrieve UniProt meta-data
		ReducedUniProtEntry uniProtEntry = null;
		TaxonomyNode taxonomyNode = null;

		if (GeneralParser.UniprotQueryProteins.get(hit.getAccession()) != null) {
			uniProtEntry = GeneralParser.UniprotQueryProteins.get(hit.getAccession());
			
			// retrieve taxonomy branch
			taxonomyNode = TaxonomyUtils.createTaxonomyNode(uniProtEntry.getTaxID(), Parameters.getInstance().getTaxonomyMap());
		} else {
			// create dummy UniProt entry
			uniProtEntry = new ReducedUniProtEntry(1, "", "", "", null, null, null);
			
			// mark taxonomy as 'unclassified'
			if (unclassifiedNode == null) {
				TaxonomyNode rootNode = new TaxonomyNode(1, TaxonomyRank.NO_RANK, "root"); 
				unclassifiedNode = new TaxonomyNode(0, TaxonomyRank.NO_RANK, "Unclassified", rootNode);
			}
			taxonomyNode = unclassifiedNode;
		}
		
		// create a new protein hit and add it to the result
		result.addProtein(new ProteinHit(hit.getAccession(), hit.getProteinDescription(), hit.getProteinSequence(), peptideHit, uniProtEntry, taxonomyNode, experimentID));
	}

	@Override
	public void clearSearchResult() {
		searchResult = null;
	}
	
	@Override
	public void setSearchResult(DbSearchResult searchResult) {
		this.searchResult = searchResult;
	}


	@Override
	public void persist(String title, Map<String, String> properties, Object... params) {
		try {
			this.setTitle(title);
			this.setCreationDate(new Date());
			this.getProperties().putAll(properties);
			
			AbstractProject project = this.getProject();
			List<AbstractExperiment> experiments = project.getExperiments();
			experiments.add(this);
		
			this.serialize();
		} catch (Exception e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),
					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
		}
	}

	@Override
	public void update(String title, Map<String, String> properties, Object... params) {
		try {
			this.setTitle(title);
			this.getProperties().clear();
			this.getProperties().putAll(properties);
		
			this.serialize();
		} catch (Exception e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),
					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
		}
	}
	
	@Override
	public void delete() {
		try {
			AbstractProject project = this.getProject();
			List<AbstractExperiment> experiments = project.getExperiments();
			experiments.remove(this);
			
			this.serialize();
		} catch (Exception e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),
					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
		}
	}

	/**
	 * Serializes the list of projects to reflect addition/modification/removal of experiments.
	 * @throws Exception if the projects file could not be retrieved or could not be written to
	 */
	public void serialize() throws Exception {
		// temporarily remove result object reference to avoid it being serialized with the project structure
		DbSearchResult searchResult = this.searchResult;
		this.searchResult = null;
		
		List<AbstractProject> projects = ClientFrame.getInstance().getProjectPanel().getProjects();
		new XStream().toXML(projects, new BufferedOutputStream(new FileOutputStream(Constants.getProjectsFile())));
		
		// restore search result reference
		this.searchResult = searchResult;
	}
}
