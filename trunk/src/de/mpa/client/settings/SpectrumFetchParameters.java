package de.mpa.client.settings;

import java.io.File;
import java.io.IOException;

import javax.swing.DefaultComboBoxModel;

/**
 * Parameter settings for spectrum database retrieval in File Panel.
 * 
 * @author A. Behne
 */
public class SpectrumFetchParameters extends ParameterMap {
	
	/**
	 * Enumeration holding annotation-related spectrum fetching parameters.
	 * @author A. Behne
	 */
	public enum AnnotationType {
		WITH_ANNOTATIONS("with annotations"),
		WITHOUT_ANNOTATIONS("without annotations"),
		IGNORE_ANNOTATIONS("ignore annotations");
		
		/** The name. */
		private String name;

		/**
		 * Constructs an annotation type from the specified name.
		 * @param name the name
		 */
		private AnnotationType(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}

	@Override
	public void initDefaults() {
		this.put("expID", new Parameter("Experiment ID", new Integer[] { 1, 1, Integer.MAX_VALUE, 1 }, "Database Fetching", "The database ID of the experiment to fetch spectra from."));
		this.put("annotated", new Parameter("Fetch Spectra", new DefaultComboBoxModel<AnnotationType>(AnnotationType.values()), "Database Fetching", "Choose whether fetched spectra shall have annotations."));
		this.put("library", new Parameter("Fetch from Spectral Library", false, "Database Fetching", "Choose whether spectra shall be fetched from the spectral library."));
		this.put("saveToFile", new Parameter("Save Peak Lists to Spectrum File", false, "Database Fetching", "Choose whether fetched spectrum file should contain peak lists."));
	}

	@Override
	public File toFile(String path) throws IOException {
		// do nothing, not needed
		return null;
	}

}
