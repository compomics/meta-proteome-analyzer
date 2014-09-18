package de.mpa.client.settings;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.analysis.taxonomy.TaxonomyUtils.TaxonomyDefinition;
import de.mpa.client.model.dbsearch.MetaProteinFactory.ClusterRule;
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
		this.put("FDR", new Parameter("Maximum peptide-spectrum match FDR", new Double[] { 0.05, 0.0, 0.1 }, "Scoring", "Specify maximum false discovery rate for peptide-spectrum matches (i.e. the maximum q-value)"));
		
		// taxonomy definition rules
		DefaultComboBoxModel<TaxonomyDefinition> pepToProtTaxMdl = new DefaultComboBoxModel<>(TaxonomyDefinition.values());
		pepToProtTaxMdl.setSelectedItem(TaxonomyDefinition.COMMON_ANCESTOR);
		this.put("proteinTaxonomy", new Parameter("Peptide-to-Protein Taxonomy", pepToProtTaxMdl, "Taxonomy Definition", "Choose the rule by which proteins receive their common taxonomy from their peptides"));

		DefaultComboBoxModel<TaxonomyDefinition> protToMetaTaxMdl = new DefaultComboBoxModel<>(TaxonomyDefinition.values());
		protToMetaTaxMdl.setSelectedItem(TaxonomyDefinition.COMMON_ANCESTOR);
		this.put("metaProteinTaxonomy", new Parameter("Protein-to-Meta-Protein Taxonomy", protToMetaTaxMdl, "Taxonomy Definition", "Choose the rule by which meta-proteins receive their common taxonomy from their proteins"));
		
		// meta-protein generation rules
		JPanel metaPnl = new MetaProteinRulesPanel();
		
		this.put("metaProteinGeneration", new Parameter("Meta-Protein Generation", metaPnl, "Meta-Protein Generation", null));
	}

	@Override
	public File toFile(String path) throws IOException {
		// not needed
		return null;
	}
	
	/**
	 * Returns the peptide rule.
	 * @return the peptide rule
	 */
	public PeptideRule getPeptideRule() {
		return(((MetaProteinRulesPanel) this.get("metaProteinGeneration").getValue()).getPeptideRule());
	}

	/**
	 * Returns the protein cluster rule.
	 * @return the protein cluster rule
	 */
	public ClusterRule getClusterRule() {
		return(((MetaProteinRulesPanel) this.get("metaProteinGeneration").getValue()).getClusterRule());
	}

	/**
	 * Returns the protein taxonomy rule.
	 * @return the protein taxonomy rule
	 */
	public TaxonomyRule getTaxonomyRule() {
		return(((MetaProteinRulesPanel) this.get("metaProteinGeneration").getValue()).getTaxonomyRule());
	}

	/**
	 * Convenience class for storing meta-protein generation-related parameters
	 * and for providing controls to manipulate them.
	 * @author A. Behne
	 */
	public class MetaProteinRulesPanel extends JPanel {

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
		 * This flag determines whether changes have been made by the user.
		 */
		private boolean changed;

		/**
		 * Constructs a meta-protein rule selection panel.
		 */
		public MetaProteinRulesPanel() {
			super();
			this.initComponents();
		}

		/**
		 * Creates and lays out the panel's components.
		 */
		private void initComponents() {
			// define top-level layout
			this.setLayout(new FormLayout("p, 5dlu, p:g, 1px", "p, 5dlu, f:p:g"));
			
			// create checkbox governing whether meta-proteins shall be created at all
			metaChk = new JCheckBox("Generate Meta-Proteins", true);
			
			// create panel containing detailed rule controls
			final JPanel rulesPnl = new JPanel(new FormLayout("p, 5dlu, p:g", "p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p"));
			
			peptideChk = new JCheckBox("Peptide Rule", true);
			peptideCbx = new JComboBox<>(PeptideRule.getValues());
			leucineChk = new JCheckBox("Consider Leucine and Isoleucine Distinct", false);
			
			JPanel distPnl = new JPanel(new FormLayout("p, 5dlu, 0px:g", "p"));
			final JLabel distLbl = new JLabel("Max. Peptide Sequence Distance");
			distSpn = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
			
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
			
			this.add(metaChk, CC.xy(1, 1));
			this.add(new JSeparator(), CC.xy(3, 1));
			this.add(rulesPnl, CC.xyw(1, 3, 4));
			
			// initially disable rules
			clusterChk.doClick();
			taxonomyChk.doClick();
			changed = false;
			
			// register listeners
			metaChk.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent evt) {
					rulesPnl.setEnabled(metaChk.isSelected());
					changed = true;
				}
			});
			
			rulesPnl.addPropertyChangeListener("enabled", new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					Boolean enabled = (Boolean) evt.getNewValue();
					peptideChk.setEnabled(enabled);
					clusterChk.setEnabled(enabled);
					taxonomyChk.setEnabled(enabled);
					changed = true;
				}
			});
			
			peptideCbx.addItemListener(new ComboBoxItemListener());
			clusterCbx.addItemListener(new ComboBoxItemListener());
			taxonomyCbx.addItemListener(new ComboBoxItemListener());
			
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
					changed = true;
				}
			});
			
			peptideChk.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent evt) {
					boolean selected = peptideChk.isSelected();
					peptideCbx.setEnabled(selected);
					leucineChk.setEnabled(selected);
					distLbl.setEnabled(selected);
					distSpn.setEnabled(selected);
					changed = true;
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
					changed = true;
				}
			});
			
			clusterChk.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent evt) {
					boolean selected = clusterChk.isSelected();
					clusterCbx.setEnabled(selected);
					changed = true;
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
					changed = true;
				}
			});
			
			taxonomyChk.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent evt) {
					boolean selected = taxonomyChk.isSelected();
					taxonomyCbx.setEnabled(selected);
					changed = true;
				}
			});
			
			leucineChk.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					changed = true;
				}
			});

		}
		
		/**
		 * Returns the currently selected peptide rule.
		 * @return the peptide rule
		 */
		public PeptideRule getPeptideRule() {
			if (peptideChk.isEnabled()) {
				if (peptideChk.isSelected()) {
					PeptideRule.maxDistance = (Integer) distSpn.getValue();
					PeptideRule.distinctIL = leucineChk.isSelected();
					return (PeptideRule) peptideCbx.getSelectedItem();
				} else {
					return PeptideRule.ALWAYS;
				}
			}
			return PeptideRule.NEVER;
		}
		
		/**
		 * Returns the currently selected protein cluster rule.
		 * @return the protein cluster rule
		 */
		public ClusterRule getClusterRule() {
			if (clusterChk.isEnabled()) {
				if (clusterChk.isSelected()) {
					return (ClusterRule) clusterCbx.getSelectedItem();
				} else {
					return ClusterRule.ALWAYS;
				}
			}
			return ClusterRule.NEVER;
		}
		
		/**
		 * Returns the currently selected protein taxonomy rule.
		 * @return the protein taxonomy rule
		 */
		public TaxonomyRule getTaxonomyRule() {
			if (taxonomyChk.isEnabled()) {
				if (taxonomyChk.isSelected()) {
					return (TaxonomyRule) taxonomyCbx.getSelectedItem();
				} else {
					return TaxonomyRule.ALWAYS;
				}
			}
			return TaxonomyRule.NEVER;
		}
		
		/**
		 * Method to check whether meta-proteins should be generated at all. 
		 * @return the flag for generating meta-proteins
		 */
		public boolean hasChanged() {
			return changed;
		}
		
		/**
		 * Method to set the changed flag externally. 
		 */
		public void setChanged(boolean changed) {
			this.changed = changed;
		}

		class ComboBoxItemListener implements ItemListener {
			// This method is called only if a new item has been selected.
			public void itemStateChanged(ItemEvent evt) {
				changed = true;
			}
		}
	}
	

}
