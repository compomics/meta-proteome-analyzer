package de.mpa.client.model;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;

import com.thoughtworks.xstream.XStream;

import de.mpa.client.Constants;
import de.mpa.client.ui.ClientFrame;

/**
 * Project implementation for file-based projects.
 * 
 * @author A. Behne
 */
public class FileProject extends AbstractProject {
	
	/**
	 * Creates an empty file-based project.
	 */
	public FileProject() {
		super(null, null, null, null, null);
	}

	@Override
	public void persist(String title, Map<String, String> properties, Object... params) {
		try {
			this.title = title;
			this.creationDate = new Date();
			this.properties.putAll(properties);
		
			List<AbstractProject> projects = ClientFrame.getInstance().getProjectPanel().getProjects();
			projects.add(this);
			
			new XStream().toXML(projects, new BufferedOutputStream(new FileOutputStream(Constants.getProjectsFile())));
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
		
			List<AbstractProject> projects = ClientFrame.getInstance().getProjectPanel().getProjects();
			
			new XStream().toXML(projects, new BufferedOutputStream(new FileOutputStream(Constants.getProjectsFile())));
		} catch (Exception e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),
					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
		}
	}
	
	@Override
	public void delete() {
		try {
			List<AbstractProject> projects = ClientFrame.getInstance().getProjectPanel().getProjects();
			projects.remove(this);
			
			new XStream().toXML(projects, new BufferedOutputStream(new FileOutputStream(Constants.getProjectsFile())));
		} catch (Exception e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),
					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
		}
	}

}
