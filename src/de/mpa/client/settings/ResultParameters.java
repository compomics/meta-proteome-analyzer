package de.mpa.client.settings;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.analysis.MetaProteinFactory.ClusterRule;
import de.mpa.analysis.MetaProteinFactory.PeptideRule;
import de.mpa.analysis.MetaProteinFactory.TaxonomyRule;
import de.mpa.analysis.taxonomy.TaxonomyUtils.TaxonomyDefinition;
import de.mpa.client.settings.Parameter.NumberParameter;
import de.mpa.client.settings.Parameter.OptionParameter;

/**
 * Class for storing search result fetching-specific parameters.
 * 
 * @author A. Behne
 */
@SuppressWarnings("serial")
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
		this.put("FDR", new NumberParameter(0.05, 0.0, 0.1, "Maximum peptide-spectrum match FDR", "Specify maximum false discovery rate for peptide-spectrum matches (i.e. the maximum q-value).", "Scoring"));
		
		// taxonomy definition rules
		this.put("proteinTaxonomy", new OptionParameter(TaxonomyDefinition.values(), 0, "Peptide-to-Protein Taxonomy", "Choose the rule by which proteins receive their common taxonomy from their peptides", "Taxonomy Definition"));
		this.put("metaProteinTaxonomy", new OptionParameter(TaxonomyDefinition.values(), 0, "Protein-to-Meta-Protein Taxonomy", "Choose the rule by which meta-proteins receive their common taxonomy from their proteins", "Taxonomy Definition"));
		
		/* meta-protein generation rules */
		// hidden parameters
		this.put("peptideRule", new OptionParameter(PeptideRule.getValues(), 0, null, null, "General"));
		this.put("clusterRule", new OptionParameter(ClusterRule.getValues(), 0, null, null, "General"));
		this.put("taxonomyRule", new OptionParameter(TaxonomyRule.getValues(), 0, null, null, "General"));
		// visible component
		MetaProteinParameters metaParams = new MetaProteinParameters("Meta-Protein Generation");
		this.put("metaProteinGeneration", metaParams);
		metaParams.createLeftComponent();
		metaParams.applyChanges();
	}

	@Override
	public File toFile(String path) throws IOException {
		// not needed
		return null;
	}
	
	/**
	 * Parameter implementation holding meta-protein generation-related options.
	 * @author A. Behne
	 */
	public class MetaProteinParameters extends Parameter {
		
		/**
		 * The main panel.
		 */
		private JPanel panel;

		/**
		 * The checkbox governing whether the peptide rule shall be applied.
		 */
		private JCheckBox peptideChk;
		
		/**
		 * The combo box containing peptide rules.
		 */
		private JComboBox<PeptideRule> peptideCbx;
		
		/**
		 * The checkbox governing whether amino acids leucine and isoleucine
		 * shall be considered distinct.
		 */
		private JCheckBox leucineChk;
		
		/**
		 * The spinner containing the maximum allowed pairwise peptide sequence
		 * distance.
		 */
		private JSpinner distSpn;

		/**
		 * The checkbox governing whether the protein cluster rule shall be applied.
		 */
		private JCheckBox clusterChk;
		
		/**
		 * The combo box containing protein cluster rules.
		 */
		private JComboBox<ClusterRule> clusterCbx;

		/**
		 * The checkbox governing whether the protein taxonomy rule shall be applied.
		 */
		private JCheckBox taxonomyChk;
		
		/**
		 * The combo box containing protein taxonomy rules.
		 */
		private JComboBox<TaxonomyRule> taxonomyCbx;
		
		/**
		 * The check box for checking whether meta-proteins should be generated.
		 */
		private JCheckBox metaChk;
		
		/**
		 * Creates a Meta-Protein generation parameter instance using the
		 * specified section identifier.
		 * @param section the section identifier
		 */
		// TODO: rewrite using nested parameter components (i.e. BooleanParameter and OptionParameter instances)
		public MetaProteinParameters(String section) {
			super(null, null, null, section);
		}

		@Override
		public JComponent createLeftComponent() {
			if (panel == null) {
				// lazily instantiate control panel
				panel = this.createPanel();
			}
			return panel;
		}
		
		/**
		 * Creates and initializes the main panel.
		 * @return the created panel
		 */
		private JPanel createPanel() {
			JPanel panel = new JPanel(new FormLayout("p, 5dlu, p:g, 1px", "p, 5dlu, f:p:g"));
			
			// create checkbox governing whether meta-proteins shall be created at all
			metaChk = new JCheckBox("Generate Meta-Proteins", true);
			
			// create panel containing detailed rule controls
			final JPanel rulesPnl = new JPanel(new FormLayout("p, 5dlu, p:g", "p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p"));
			
			peptideChk = new JCheckBox("Peptide Rule", true);
			peptideCbx = new JComboBox<>(PeptideRule.getValues());
			
			leucineChk = new JCheckBox("Consider Leucine and Isoleucine Distinct", false);
			leucineChk.setToolTipText("If checked amino acids Leucine and Isoleucine"
					+ " are considered distinct in peptide sequences.");
			
			JPanel distPnl = new JPanel(new FormLayout("p, 5dlu, 0px:g", "p"));
			final JLabel distLbl = new JLabel("Max. Peptide Sequence Distance");
			String distToolTip = "In single shared peptide mode this will result in a negative control and split of metaproteins.";
			distLbl.setToolTipText(distToolTip);
			distSpn = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
			distSpn.setToolTipText(distToolTip);
			
			distPnl.add(distLbl, CC.xy(1, 1));
			distPnl.add(distSpn, CC.xy(3, 1));
			
			clusterChk = new JCheckBox("Cluster Rule", true);
			clusterCbx = new JComboBox<>(ClusterRule.getValues());
			clusterCbx.setEnabled(false);
			taxonomyChk = new JCheckBox("Taxonomy Rule", true);
			taxonomyCbx = new JComboBox<>(TaxonomyRule.getValues());
			taxonomyCbx.setEnabled(false);
			
			rulesPnl.add(peptideChk, CC.xy(1, 1));
			rulesPnl.add(peptideCbx, CC.xy(3, 1));
			rulesPnl.add(leucineChk, CC.xy(3, 3));
			rulesPnl.add(distPnl, CC.xy(3, 5));
			rulesPnl.add(clusterChk, CC.xy(1, 7));
			rulesPnl.add(clusterCbx, CC.xy(3, 7));
			rulesPnl.add(taxonomyChk, CC.xy(1, 9));
			rulesPnl.add(taxonomyCbx, CC.xy(3, 9));
			
			panel.add(metaChk, CC.xy(1, 1));
			panel.add(new JSeparator(), CC.xy(3, 1));
			panel.add(rulesPnl, CC.xyw(1, 3, 4));
			
			// initially disable rules
			clusterChk.doClick();
			taxonomyChk.doClick();
			
			// register listeners
			metaChk.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent evt) {
					rulesPnl.setEnabled(metaChk.isSelected());
				}
			});
			
			rulesPnl.addPropertyChangeListener("enabled", new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					Boolean enabled = (Boolean) evt.getNewValue();
					peptideChk.setEnabled(enabled);
					clusterChk.setEnabled(enabled);
					taxonomyChk.setEnabled(enabled);
				}
			});
			
			peptideChk.addPropertyChangeListener("enabled", new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					Boolean enabled = (Boolean) evt.getNewValue();
					if (enabled) {
						enabled = peptideChk.isSelected();
					}
					peptideCbx.setEnabled(enabled);
					leucineChk.setEnabled(enabled);
					distLbl.setEnabled(enabled);
					distSpn.setEnabled(enabled);
				}
			});
			
			peptideChk.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent evt) {
					boolean selected = peptideChk.isSelected();
					peptideCbx.setEnabled(selected);
					leucineChk.setEnabled(selected);
					distLbl.setEnabled(selected);
					distSpn.setEnabled(selected);
				}
			});
			
			clusterChk.addPropertyChangeListener("enabled", new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					Boolean enabled = (Boolean) evt.getNewValue();
					if (enabled) {
						enabled = clusterChk.isSelected();
					}
					clusterCbx.setEnabled(enabled);
				}
			});
			
			clusterChk.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent evt) {
					boolean selected = clusterChk.isSelected();
					clusterCbx.setEnabled(selected);
				}
			});
			
			taxonomyChk.addPropertyChangeListener("enabled", new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					Boolean enabled = (Boolean) evt.getNewValue();
					if (enabled) {
						enabled = taxonomyChk.isSelected();
					}
					taxonomyCbx.setEnabled(enabled);
				}
			});
			
			taxonomyChk.addItemListener(new ItemListener() {
				@Override
				public void itemStateChanged(ItemEvent evt) {
					boolean selected = taxonomyChk.isSelected();
					taxonomyCbx.setEnabled(selected);
				}
			});
			
			return panel;
		}

		@Override
		public boolean applyChanges() {
			boolean changed = false;

			Object oldRule = ResultParameters.this.get("peptideRule").getValue();
			PeptideRule peptideRule = PeptideRule.NEVER;
			if (peptideChk.isEnabled()) {
				if (peptideChk.isSelected()) {
					peptideRule = (PeptideRule) peptideCbx.getSelectedItem();
					peptideRule.setMaximumDistance((Integer) distSpn.getValue());
					peptideRule.setDistinctIL(leucineChk.isSelected());
				} else {
					peptideRule = PeptideRule.ALWAYS;
				}
			}
			ResultParameters.this.setValue("peptideRule", peptideRule);
			changed |= !peptideRule.equals(oldRule);

			oldRule = ResultParameters.this.get("clusterRule").getValue();
			ClusterRule clusterRule = ClusterRule.NEVER;
			if (clusterChk.isEnabled()) {
				if (clusterChk.isSelected()) {
					clusterRule = (ClusterRule) clusterCbx.getSelectedItem();
				} else {
					clusterRule = ClusterRule.ALWAYS;
				}
			}
			ResultParameters.this.setValue("clusterRule", clusterRule);
			changed |= !clusterRule.equals(oldRule);

			oldRule = ResultParameters.this.get("taxonomyRule").getValue();
			TaxonomyRule taxonomyRule = TaxonomyRule.NEVER;
			if (taxonomyChk.isEnabled()) {
				if (taxonomyChk.isSelected()) {
					taxonomyRule = (TaxonomyRule) taxonomyCbx.getSelectedItem();
				} else {
					taxonomyRule = TaxonomyRule.ALWAYS;
				}
			}
			ResultParameters.this.setValue("taxonomyRule", taxonomyRule);
			changed |= !taxonomyRule.equals(oldRule);
			
			return changed;
		}
		
		@Override
		public void restoreDefaults() {
			metaChk.setSelected(true);
			
			peptideChk.setSelected(true);
			peptideCbx.setSelectedIndex(0);
			leucineChk.setSelected(false);
			distSpn.setValue(0);
			
			clusterChk.setSelected(false);
			clusterCbx.setSelectedIndex(0);
			
			taxonomyChk.setSelected(false);
			taxonomyCbx.setSelectedIndex(0);
			
			this.applyChanges();
		}
		
	}

}
