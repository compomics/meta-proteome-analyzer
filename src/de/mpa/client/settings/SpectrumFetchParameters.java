package de.mpa.client.settings;

import java.io.File;
import java.io.IOException;

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
		private final String name;

		/**
		 * Constructs an annotation type from the specified name.
		 * @param name the name
		 */
        AnnotationType(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return this.name;
		}
	}

	@Override
	public void initDefaults() {
//		this.put("library", new Parameter("Fetch from Spectral Library", false, "Database Fetching", "Choose whether spectra shall be fetched from the spectral library."));

        put("expID", new Parameter.NumberParameter(1, 1, null, "Experiment ID", "The database ID of the experiment to fetch spectra from.", "Database Fetching"));
        put("annotated", new Parameter.OptionParameter(SpectrumFetchParameters.AnnotationType.values(), 0, "Fetch Spectra", "Choose whether fetched spectra shall have annotations.", "Database Fetching"));
        put("saveToFile", new Parameter.BooleanParameter(false, "Save Peak Lists to Spectrum File", "Choose whether fetched spectrum file should contain peak lists.", "Database Fetching"));
	}

	@Override
	public File toFile(String path) throws IOException {
		// do nothing, not needed
		return null;
	}
}
