package de.mpa.client.model;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;

import com.thoughtworks.xstream.XStream;

import de.mpa.client.Constants;
import de.mpa.client.ui.ClientFrame;

/**
 * Project class for file-based projects.
 * 
 * @author T. Muth
 */
public class FileProject implements ProjectExperiment {
	
	/**
	 * The project ID.
	 */
	private Long id;
	
	/**
	 * The project title.
	 */
	private String title;
	
	/**
	 * The project's creation date.
	 */
	private Date creationDate;
	
	/**
	 * The experiment properties.
	 */
	private Map<String, String> properties;
	
	/**
	 * The project's list of child experiments.
	 */
	private List<FileExperiment> experiments;
	
	/**
	 * Creates an empty file-based project.
	 */
	public FileProject() {
		this(null, null, null, null, null);
	}
	
	/**
	 * Creates a project from the specified ID, title, creation date, properties
	 * and child experiments.
	 * @param id the project ID
	 * @param title the project title
	 * @param creationDate the project's creation date
	 * @param properties the project properties
	 * @param experiments the child experiments
	 */
	public FileProject(Long id, String title, Date creationDate, Map<String, String> properties, List<FileExperiment> experiments) {
		this.id = id;
		this.title = title;
		this.creationDate = creationDate;
		this.properties = (properties != null) ? properties : new LinkedHashMap<String, String>();
		this.experiments = (experiments != null) ? experiments : new ArrayList<FileExperiment>();
	}
	
	/**
	 * Returns the project ID.
	 * @return the project ID
	 */
	public Long getID() {
		return id;
	}
	
	/**
	 * Sets the project ID.
	 * @param id the ID to set
	 */
	public void setID(Long id) {
		this.id = id;
	}
	
	/**
	 * Returns the project properties.
	 * @return the project properties
	 */
	public Map<String, String> getProperties() {
		return properties;
	}
	
	/**
	 * Returns the list of child experiments of this project.
	 * @return the experiments
	 */
	public List<FileExperiment> getExperiments() {
		return experiments;
	}
	
	/** 
	 * Returns the project title.
	 * @return the project title
	 */
	public String getTitle() {
		return title;
	}

	/** 
	 * Sets the project title.
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Returns the project's creation date.
	 * @return the creation date
	 */
	public Date getCreationDate() {
		return creationDate;
	}
	
	/**
	 * Sets the project's creation date.
	 * @param creationDate the date to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public void persist(String title, Map<String, String> properties, Object... params) {
		try {
			this.setTitle(title);;
			this.setCreationDate(new Date());
			this.getProperties().putAll(properties);
			
			List<FileProject> projects = ClientFrame.getInstance().getProjectPanel().getProjects();
			projects.add(this);
			
			new XStream().toXML(projects, new BufferedOutputStream(new FileOutputStream(Constants.getProjectsFile())));
		} catch (Exception e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),
					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
		}
	}

	public void update(String title, Map<String, String> properties, Object... params) {
		try {
			this.setTitle(title);
			this.getProperties().clear();
			this.getProperties().putAll(properties);
		
			List<FileProject> projects = ClientFrame.getInstance().getProjectPanel().getProjects();
			
			new XStream().toXML(projects, new BufferedOutputStream(new FileOutputStream(Constants.getProjectsFile())));
		} catch (Exception e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),
					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
		}
	}
	
	public void delete() {
		try {
			List<FileProject> projects = ClientFrame.getInstance().getProjectPanel().getProjects();
			projects.remove(this);
			
			new XStream().toXML(projects, new BufferedOutputStream(new FileOutputStream(Constants.getProjectsFile())));
		} catch (Exception e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),
					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FileProject) {
			FileProject that = (FileProject) obj;
			if ((this.getID() != null) && (that.getID() != null)) {
				return this.getID().equals(that.getID());
			} else {
				return this.getCreationDate().equals(that.getCreationDate());
			}
		}
		return false;
	}

}
