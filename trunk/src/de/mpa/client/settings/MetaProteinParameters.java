package de.mpa.client.settings;

import java.io.File;
import java.io.IOException;

import javax.swing.DefaultComboBoxModel;

import de.mpa.client.model.dbsearch.MetaProteinFactory.ClusterRule;
import de.mpa.client.model.dbsearch.MetaProteinFactory.PeptideRule;
import de.mpa.client.model.dbsearch.MetaProteinFactory.TaxonomyRule;

/**
 * Class for storing search result fetching-specific parameters.
 * 
 * @author A. Behne
 */
public class MetaProteinParameters extends ParameterMap {
	
	/**
	 * Constructs a parameter map initialized with default values.
	 */
	public MetaProteinParameters() {
		this.initDefaults();
	}

	@Override
	public void initDefaults() {
		this.put("distinguishIL", new Parameter("Consider Leucine and Isoleucine Distinct", true, "Meta-Protein Generation", "Consider peptides that differ only in Leucine/Isoleucine residues as distinct."));
		this.put("proteinClusterRule", new Parameter("Protein Cluster Rule", new DefaultComboBoxModel(ClusterRule.values()), "Meta-Protein Generation", "Choose the rule by which proteins are grouped into meta-proteins to the protein cluster."));
		this.put("peptideRule", new Parameter("Peptide Rule", new DefaultComboBoxModel(PeptideRule.values()), "Meta-Protein Generation", "Choose the rule by which proteins are grouped into meta-proteins according to shared peptides."));
		this.put("taxonomyRule", new Parameter("Taxonomy Rule", new DefaultComboBoxModel(TaxonomyRule.values()), "Meta-Protein Generation", "Choose the rule by which proteins are grouped into meta-proteins according to common taxonomy."));
		this.put("ProteinTaxonomy", new Parameter("Protein taxonomy based on most specific peptide taxonomy", true, "Taxonomy Definition", "TRUE: Protein taxonomy is the taxonomy of the most specific peptide. FALSE:Protein taxonomy of the common ancestor taxonomy of all peptides."));
		this.put("MetaproteinTaxonomy", new Parameter("Metaprotein taxonomy based on most specific protein taxonomy", true, "Taxonomy Definition", "TRUE: Metaprotein taxonomy is the taxonomy of the most specific protein. FALSE:Metaprotein taxonomy of the common ancestor taxonomy of all proteins."));
	}

	@Override
	public File toFile(String path) throws IOException {
		// not needed
		return null;
	}

}
