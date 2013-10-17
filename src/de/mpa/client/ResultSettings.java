package de.mpa.client;

/**
 * This class holds the settings for the ResultSettingsDialog
 * FIXME: Use these settings in a meaningful way.
 * @author R. Heyer
 *
 */
public class ResultSettings {

	/**
	 * The singleton instance of the result settings
	 */
	private static ResultSettings resultSettings;

	/**
	 * Parameters for metaprotein settings.
	 */
	public boolean  leuDistIso; 
	public boolean  alignment;
	public enum MetaproteinTyp {ONE_SHARED_PEPTIDE, SHARED_PEPTIDESET, NO_METAPROTEINS}
	public MetaproteinTyp metaproteinTyp;
	
	
	/**
	 * Default constructor with standard settings
	 */
	private ResultSettings() {
		// Define default metaprotein settings
		leuDistIso = true; 
		alignment = false;
		metaproteinTyp = MetaproteinTyp.ONE_SHARED_PEPTIDE;
	}
	
	
	/**
	 * If is not existing, create a new instance.
	 * @return The exportFields.
	 */
	public static ResultSettings getInstance() {
		if (resultSettings == null) {
			resultSettings = new ResultSettings();
		}
			return resultSettings;
	}
	
}
