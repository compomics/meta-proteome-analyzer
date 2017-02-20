package de.mpa.client.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Date;
import java.util.LinkedHashMap;
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
import de.mpa.io.GenericContainer;
import de.mpa.io.MascotGenericFileReader;
import de.mpa.main.Parameters;

/**
 * Implementation of the experiment interface for file-based experiments.
 * 
 * @author A. Behne
 */
public class FileExperiment implements ProjectExperiment {
	
	/**
	 * The experiment ID.
	 */
	private Long id;

	/**
	 * The experiment title.
	 */
	private String title;

	/**
	 * The experiment's creation date.
	 */
	private Date creationDate;
	
	/**
	 * The experiment properties.
	 */
	private Map<String, String> properties;
	
	/**
	 * The parent project.
	 */
	private FileProject project;
	
	/**
	 * The result file.
	 */
	private File resultFile = null;
	
	/**
	 * The spectrum files.
	 */
	private List<File> spectrumFiles;
	
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
	public FileExperiment(String title, Date creationDate, FileProject project) {
		this(null, title, creationDate, project);
	}
	
	/**
	 * Creates an experiment using the specified title, id, creation date and
	 * parent project.
	 * @param id the experiment ID
	 * @param title the experiment title
	 * @param creationDate the experiment's creation date
	 * @param project the parent project
	 */
	public FileExperiment(Long id, String title, Date creationDate, FileProject project) {
		this(id, title, creationDate, new LinkedHashMap<String, String>(), project);
	}
	
	/**
	 * Creates an experiment using the specified title, id, creation date,
	 * property map and parent project.
	 * @param id the experiment ID
	 * @param title the experiment title
	 * @param creationDate the experiment's creation date
	 * @param properties the experiment properties
	 * @param project the parent project
	 */
	public FileExperiment(Long id, String title, Date creationDate, Map<String, String> properties, FileProject project) {
		this.id = id;
		this.title = title;
		this.creationDate = creationDate;
		this.properties = properties;
		this.project = project;
	}
	
	/**
	 * Adds a property to the map of experiment properties.
	 * @param name the property name
	 * @param value the property value
	 */
	public void addProperty(String name, String value) {
		properties.put(name, value);
	}

	@Override
	public Long getID() {
		return id;
	}
	
	/**
	 * Sets the experiment ID.
	 * @param id the ID to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	
	@Override
	public String getTitle() {
		return title;
	}
	
	/**
	 * Sets the experiment title.
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	@Override
	public Date getCreationDate() {
		return creationDate;
	}
	
	/**
	 * Sets the creation date.
	 * @param creationDate the date to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	public Map<String, String> getProperties() {
		return properties;
	}
	
	/**
	 * Returns the parent project.
	 * @return the parent project
	 */
	public FileProject getProject() {
		return project;
	}
	
	/**
	 * Sets the parent project.
	 * @param project the project to set
	 */
	public void setProject(FileProject project) {
		if (project == null) {
			throw new IllegalArgumentException("Project must not be null.");
		}
		this.project = project;
	}
	
	/**
	 * Clears the search result object.
	 */
	public void clearSearchResult() {
		this.setSearchResult(null);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ProjectExperiment) {
			ProjectExperiment that = (ProjectExperiment) obj;
			if (this.getClass().equals(that.getClass())) {
				if ((this.getID() != null) && (that.getID() != null)) {
					return this.getID().equals(that.getID());
				} 
				return this.getCreationDate().equals(that.getCreationDate());
			}
		}
		return false;
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
	public List<File> getSpectrumFiles() {
		return spectrumFiles;
	}
	
	/**
	 * Sets the spectrum files.
	 * @param spectrumFiles the spectrum files to set
	 */
	public void setSpectrumFiles(List<File> spectrumFiles) {
		this.spectrumFiles = spectrumFiles;
	}
	
	public boolean hasSearchResult() {
		return (resultFile != null) && resultFile.exists();
	}


	public DbSearchResult getSearchResult() {
		if (searchResult != null) {
			return searchResult;
		}
		Client client = Client.getInstance();
		if (resultFile != null) {
			
			client.firePropertyChange("new message", null, "READING RESULTS FILE");
			client.firePropertyChange("resetall", 0L, 100L);
			client.firePropertyChange("indeterminate", false, true);
			
			try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(resultFile))))) {
				searchResult = (DbSearchResult) ois.readObject();
				
//				ClientFrame.getInstance().getGraphDatabaseResultPanel().setResultsButtonEnabled(true);
				client.firePropertyChange("new message", null, "READING RESULTS FILE FINISHED");
				
			} catch (Exception e) {
				JXErrorPane.showDialog(ClientFrame.getInstance(), new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
				
				client.firePropertyChange("new message", null, "READING RESULTS FILE FAILED");
			}
			client.firePropertyChange("indeterminate", true, false);
			
			List<File> spectrumFiles = this.getSpectrumFiles();
			for (File file : spectrumFiles) {
				String pathname = file.getAbsolutePath();
				// Check if reader already has the current experiment selected.
				if (GenericContainer.MGFReaders.get(pathname) == null || !file.getName().equals(GenericContainer.MGFReaders.get(pathname).getFilename())) {
					client.firePropertyChange("new message", null, "INDEXING SPECTRA");
					try {
						client.firePropertyChange("resetcur", -1L, file.length());
						MascotGenericFileReader mgfReader = new MascotGenericFileReader(new File(pathname));
						
						GenericContainer.SpectrumPosMap.put(pathname, mgfReader.getSpectrumPositions(false));
						client.firePropertyChange("indeterminate", true, false);
						client.firePropertyChange("new message", null, "INDEXING SPECTRA FINISHED");
						mgfReader.setSpectrumPositions(GenericContainer.SpectrumPosMap.get(pathname));
						GenericContainer.MGFReaders.put(pathname, mgfReader);
					} catch (IOException e) {
						e.printStackTrace();
						Client.getInstance().firePropertyChange("new message", null, "INDEXING SPECTRA FAILED");
					}
				}
			}
			
		} else {
			try {
				// Initialize the result object
				DbSearchResult searchResult = new DbSearchResult(this.getProject().getTitle(), this.getTitle(), null);
				List<SearchHit> searchHits = GenericContainer.SearchHits;
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
				searchResult.setTotalSpectrumCount(GenericContainer.numberTotalSpectra);
				this.searchResult = searchResult;
				
				// Empty the search hits from the GenericContainer
				GenericContainer.SearchHits.clear();
				
				client.firePropertyChange("new message", null, "BUILDING RESULTS OBJECT FINISHED");
			} catch (Exception e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
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

		if (GenericContainer.UniprotQueryProteins.get(hit.getAccession()) != null) {
			uniProtEntry = GenericContainer.UniprotQueryProteins.get(hit.getAccession());
			
			// retrieve taxonomy branch
			taxonomyNode = TaxonomyUtils.createTaxonomyNode(uniProtEntry.getTaxID(), Parameters.getInstance().getTaxonomyMap());
			if (taxonomyNode == null) {
				if (unclassifiedNode == null) {
					TaxonomyNode rootNode = new TaxonomyNode(1, TaxonomyRank.NO_RANK, "root"); 
					unclassifiedNode = new TaxonomyNode(0, TaxonomyRank.NO_RANK, "Unclassified", rootNode);
				}
				taxonomyNode = unclassifiedNode;
			}
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

	public void setSearchResult(DbSearchResult searchResult) {
		this.searchResult = searchResult;
	}


	@Override
	public void persist(String title, Map<String, String> properties, Object... params) {
		try {
			this.setTitle(title);
			this.setCreationDate(new Date());
			this.getProperties().putAll(properties);
			
			FileProject project = this.getProject();
			List<FileExperiment> experiments = project.getExperiments();
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
			FileProject project = this.getProject();
			List<FileExperiment> experiments = project.getExperiments();
			experiments.remove(this);
			this.serialize();
			
			// Delete the actual (*.mpa) result file.
			if (resultFile != null) {
				if (resultFile.exists() && resultFile.isFile()) {
					resultFile.delete();
				}
			}
			
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
		
		List<FileProject> projects = ClientFrame.getInstance().getProjectPanel().getProjects();
		new XStream().toXML(projects, new BufferedOutputStream(new FileOutputStream(Constants.getProjectsFile())));
		
		// restore search result reference
		this.searchResult = searchResult;
	}
}
