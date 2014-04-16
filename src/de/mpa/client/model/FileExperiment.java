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

import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.ui.ClientFrame;

/**
 * Implementation of the experiment interface for file-based experiments.
 * 
 * @author A. Behne
 */
public class FileExperiment extends AbstractExperiment {
	
	/**
	 * The result file.
	 */
	private File resultFile;
	
	/**
	 * The spectrum file.
	 */
	private File spectrumFile;
	
	/**
	 * The search result object.
	 */
	private DbSearchResult searchResult;
	
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
		if ((title == null) && (resultFile != null)) {
			String filename = resultFile.getName();
			title = filename.substring(0, filename.lastIndexOf('.'));
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
		if (searchResult == null) {
			Client client = Client.getInstance();
			client.firePropertyChange("new message", null, "READING RESULTS FILE");
			client.firePropertyChange("resetall", 0L, 100L);
			client.firePropertyChange("indeterminate", false, true);
			
			try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(
					new GZIPInputStream(new FileInputStream(resultFile))))) {
				searchResult = (DbSearchResult) ois.readObject();
				
				ClientFrame.getInstance().getGraphDatabaseResultPanel().setResultsButtonEnabled(true);
				client.firePropertyChange("new message", null, "READING RESULTS FILE FINISHED");
				
			} catch (Exception e) {
				JXErrorPane.showDialog(ClientFrame.getInstance(),
						new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
				
				client.firePropertyChange("new message", null, "READING RESULTS FILE FAILED");
			}
			client.firePropertyChange("indeterminate", true, false);
		}
		return searchResult;
	}
	
	@Override
	public void setSearchResult(DbSearchResult searchResult) {
		this.searchResult = searchResult;
	}

	@Override
	public void persist(String title, Map<String, String> properties, Object... params) {
		try {
			this.title = title;
			this.creationDate = new Date();
			this.properties.putAll(properties);
			this.project.getExperiments().add(this);
		
			this.serialize();
		} catch (Exception e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),
					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
		}
	}

	@Override
	public void update(String title, Map<String, String> properties, Object... params) {
		try {
			this.title = title;
			this.properties.clear();
			this.properties.putAll(properties);
		
			this.serialize();
		} catch (Exception e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),
					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
		}
	}
	
	@Override
	public void delete() {
		try {
			this.project.getExperiments().remove(this);
			
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
	private void serialize() throws Exception {
		// temporarily remove result object reference to avoid it being serialized with the project structure
		DbSearchResult searchResult = this.searchResult;
		this.searchResult = null;
		
		List<AbstractProject> projects = ClientFrame.getInstance().getProjectPanel().getProjects();
		new XStream().toXML(projects, new BufferedOutputStream(new FileOutputStream(Constants.getProjectsFile())));
		
		// restore search result reference
		this.searchResult = searchResult;
	}

}
