package de.mpa.client.settings;

import java.io.File;
import java.io.IOException;

import javax.swing.DefaultComboBoxModel;

import de.mpa.analysis.taxonomy.TaxonomyUtils.TaxonomyDefinition;
import de.mpa.client.model.dbsearch.MetaProteinFactory.ClusterRule;
import de.mpa.client.model.dbsearch.MetaProteinFactory.DistPepRule;
import de.mpa.client.model.dbsearch.MetaProteinFactory.PeptideRule;
import de.mpa.client.model.dbsearch.MetaProteinFactory.TaxonomyRule;

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
		// FDR cutoff
		this.put("FDR", new Parameter("Maximum peptide-spectrum match false discovery rate", new Double[] { 0.05, 0.0, 0.1 }, "Scoring", "Specify maximum false discovery rate for peptide-spectrum matches (i.e. the maximum q-value)"));
		
		// taxonomy definition rules
		DefaultComboBoxModel pepToProtTaxMdl = new DefaultComboBoxModel(TaxonomyDefinition.values());
		pepToProtTaxMdl.setSelectedItem(TaxonomyDefinition.COMMON_ANCESTOR);
		this.put("proteinTaxonomy", new Parameter("Peptide-to-Protein Taxonomy", pepToProtTaxMdl, "Taxonomy Definition", "Choose the rule by which proteins receive their common taxonomy from their peptides"));

		DefaultComboBoxModel protToMetaTaxMdl = new DefaultComboBoxModel(TaxonomyDefinition.values());
		protToMetaTaxMdl.setSelectedItem(TaxonomyDefinition.COMMON_ANCESTOR);
		this.put("metaProteinTaxonomy", new Parameter("Protein-to-Meta-Protein Taxonomy", protToMetaTaxMdl, "Taxonomy Definition", "Choose the rule by which meta-proteins receive their common taxonomy from their proteins"));
		
		// meta-protein generation rules
		this.put("distinguishIL", new Parameter("Consider Leucine and Isoleucine Distinct", true, "Meta-Protein Generation", "Consider peptides that differ only in Leucine/Isoleucine residues as distinct"));
		
		this.put("levenstheinDistance", new Parameter("Peptide Levensthein Distance", new Integer[] { 0, 0, 10 }, "Meta-Protein Generation", "Threshold for peptide similarity based on Levensthein distance"));
		
		DefaultComboBoxModel clusterMdl = new DefaultComboBoxModel(ClusterRule.values());
		clusterMdl.setSelectedItem(ClusterRule.ALWAYS);
		this.put("proteinClusterRule", new Parameter("Protein Cluster Rule", clusterMdl, "Meta-Protein Generation", "Choose the rule by which proteins are grouped into meta-proteins to the protein cluster"));
		
		DefaultComboBoxModel peptideMdl = new DefaultComboBoxModel(PeptideRule.values());
		peptideMdl.setSelectedItem(PeptideRule.PEPTIDE_SUBSET);
		this.put("peptideRule", new Parameter("Peptide Rule", peptideMdl, "Meta-Protein Generation", "Choose the rule by which proteins are grouped into meta-proteins according to shared peptides"));

		DefaultComboBoxModel levenstheinMdl = new DefaultComboBoxModel(DistPepRule.values());
		levenstheinMdl.setSelectedItem(DistPepRule.ALWAYS);
		this.put("distPepRule", new Parameter("Distinct Peptides", levenstheinMdl, "Meta-Protein Generation", "Exclude if peptides are similar by Levensthein Distance"));
		
		
		DefaultComboBoxModel taxonomyMdl = new DefaultComboBoxModel(TaxonomyRule.values());
		taxonomyMdl.setSelectedItem(TaxonomyRule.ALWAYS);
		this.put("taxonomyRule", new Parameter("Taxonomy Rule", taxonomyMdl, "Meta-Protein Generation", "Choose the rule by which proteins are grouped into meta-proteins according to common taxonomy"));
	
	}

	@Override
	public File toFile(String path) throws IOException {
		// not needed
		return null;
	}
}
