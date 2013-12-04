package de.mpa.client.settings;

import java.io.File;
import java.io.IOException;

import javax.swing.DefaultComboBoxModel;

import de.mpa.client.model.dbsearch.MetaProteinFactory.MetaProteinRule;

/**
 * Class for storing search result fetching-specific parameters.
 * 
 * @author A. Behne
 */
public class ResultParameters extends ParameterMap {
	
	/**
	 * Constructs a parameter map initialized with default values.
	 */
	public ResultParameters() {
		this.initDefaults();
	}

	@Override
	public void initDefaults() {
		this.put("mergeIL", new Parameter("Consider Leucine and Isoleucine Distinct", true, "Peptides", "Consider peptides that differ only in Leucine/Isoleucine residues as distinct."));
		this.put("metaRule", new Parameter("Rule", new DefaultComboBoxModel(MetaProteinRule.values()), "Meta-Protein Generation", "Choose the rule by which proteins are grouped into meta-proteins."));
	}

	@Override
	public File toFile(String path) throws IOException {
		// not needed
		return null;
	}

}
