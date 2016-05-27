package de.mpa.client.settings;

import java.io.File;
import java.io.IOException;

import de.mpa.client.settings.Parameter.BooleanParameter;
import de.mpa.client.settings.Parameter.NumberParameter;
import de.mpa.client.settings.Parameter.OptionParameter;

/**
 * Parameter settings for spectrum database retrieval in File Panel.
 * 
 * @author A. Behne
 */
@SuppressWarnings("serial")
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
//		this.put("library", new Parameter("Fetch from Spectral Library", false, "Database Fetching", "Choose whether spectra shall be fetched from the spectral library."));
		
		this.put("expID", new NumberParameter(1, 1, null, "Experiment ID", "The database ID of the experiment to fetch spectra from.", "Database Fetching"));
		this.put("annotated", new OptionParameter(AnnotationType.values(), 0, "Fetch Spectra", "Choose whether fetched spectra shall have annotations.", "Database Fetching"));
		this.put("saveToFile", new BooleanParameter(false, "Save Peak Lists to Spectrum File", "Choose whether fetched spectrum file should contain peak lists.", "Database Fetching"));
	}

	@Override
	public File toFile(String path) throws IOException {
		// do nothing, not needed
		return null;
	}
}
