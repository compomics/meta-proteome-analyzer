package de.mpa.client.settings;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.settings.Parameter.BooleanParameter;
import de.mpa.client.settings.Parameter.ButtonParameter;
import de.mpa.client.settings.Parameter.NumberParameter;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.panels.DatabaseSearchSettingsPanel;

/**
 * Class for storing Mascot search engine-specific settings.
 * 
 * @author T. Muth, F. Kohrs, A. Behne
 */
public class MascotParameters extends ParameterMap {
	
	/**
	 * Constructs a parameter map initialized with default values.
	 */
	public MascotParameters() {
		initDefaults();
	}

	@Override
	public void initDefaults() {
		
		int defaultIonScore = 15;
		double defaultFDR = 0.05;
		
		/* Hidden parameters */
		this.put("filterType", new BooleanParameter(true, null, null, "General"));
		this.put("ionScore", new NumberParameter(defaultIonScore, 0, null, null, null, "General"));
		this.put("fdrScore", new NumberParameter(defaultFDR, 0.0, 1.0, null, null, "General"));
		
		/* Editable parameters */
		this.put("filter", new FilteringParameters(defaultIonScore, defaultFDR, "Filtering"));
	
		/* editable parameter for sequence querying*/
		this.put("useFasta", new BooleanParameter(true, "Get Input sequence from FASTA", null,  "FASTA"));
		
		/* Non-editable parameters */
		this.put("precTol", new NumberParameter(0.0, null, null, false, "Precursor Ion Tolerance", "The precursor mass tolerance.", "Search Settings"));
		this.put("fragTol", new NumberParameter(0.0, null, null, false, "Fragment Ion Tolerance", "The fragment mass tolerance.", "Search Settings"));
		this.put("missClv", new NumberParameter(0, null, null, false, "Missed Cleavages (max)", "The maximum number of missed cleavages.", "Search Settings"));
		
		Action copyAction = new AbstractAction("Copy to General Settings") {
			@Override
			public void actionPerformed(ActionEvent evt) {
				DatabaseSearchSettingsPanel dbSearchPanel =
						ClientFrame.getInstance().getFilePanel().getSettingsPanel().getDatabaseSearchSettingsPanel();
				
				dbSearchPanel.getPrecursorToleranceSpinner().setValue(MascotParameters.this.get("precTol").getValue());
				dbSearchPanel.getFragmentToleranceSpinner().setValue(MascotParameters.this.get("fragTol").getValue());
				dbSearchPanel.getMissedCleavageSpinner().setValue(MascotParameters.this.get("missClv").getValue());
			}
		};
		
		this.put("copyBtn", new ButtonParameter(copyAction, "Search Settings"));
	}
	
	/**
	 * Sets whether decoy spectra are evaluated in the loaded Mascot file
	 * and therefore whether FDR filtering is possible.
	 * @param decoy <code>true</code> if FDR filtering is possible,
	 *  <code>false</code> otherwise
	 */
	public void setDecoy(boolean decoy) {
		((FilteringParameters) this.get("filter")).setDecoy(decoy);
	}
	
	@Override
	public File toFile(String path) throws IOException {
		return null;
	}
	
	/**
	 * Parameter object for holding Mascot result filtering options.
	 * @author A. Behne
	 */
	public class FilteringParameters extends Parameter {
		
		/** Type flag for querying sequence from the FASTA-FILE. */
		public static final boolean UseFASTA = true;
		
		/** Type flag for ion score. */
		public static final boolean ION_SCORE = true;
		/** Type flag for FDR score. */
		public static final boolean FDR_SCORE = false;
		
		/** The default ion score. */
		private int defaultIonScore;
		/** The default FDR score. */
		private double defaultFdrScore;
		
		/** The main panel. */
		private JPanel panel;

		/** The ion score radio button. */
		private JRadioButton ionScoreRbn;
		/** The ion score spinner. */
		private JSpinner ionScoreSpn;
		/** The FDR score radio button. */
		private JRadioButton fdrScoreRbn;
		/** The FDR score spinner. */
		private JSpinner fdrScoreSpn;

		/** Flag indicating whether FDR filtering is available. */
		private boolean decoy = false;

		/**
		 * Creates an instance of Mascot filtering parameters using the
		 * specified default ion score and maximum FDR value.
		 * @param ionScore the ion score
		 * @param fdrScore the FDR value
		 * @param section the section identifier of the parameter
		 */
		public FilteringParameters(int ionScore, double fdrScore, String section) {
			super(null, null, null, section);
			this.defaultIonScore = ionScore;
			this.defaultFdrScore = fdrScore;
		}
		
		/**
		 * Sets whether decoy spectra are evaluated in the loaded Mascot file
		 * and therefore whether FDR filtering is possible.
		 * @param decoy <code>true</code> if FDR filtering is possible,
		 *  <code>false</code> otherwise
		 */
		public void setDecoy(boolean decoy) {
			this.decoy = decoy;
			if (fdrScoreRbn != null) {
				fdrScoreRbn.setEnabled(decoy);
			}
		}
		
		@Override
		public JComponent createLeftComponent() {
			if (panel == null) {
				// lazily instantiate panel
				panel = this.createPanel();
			}
			return panel;
		}

		/**
		 * Creates and initializes the main panel.
		 * @return the created panel
		 */
		private JPanel createPanel() {
			JPanel panel = new JPanel(new FormLayout(
					"l:p, 5dlu, p:g",
					"f:p:g, 3dlu, f:p:g"));
			
			ButtonGroup bg = new ButtonGroup();
			
			
			
			
			
			
			// ion score controls
			ionScoreRbn = new JRadioButton("Peptide Ion Score", true);
			ionScoreRbn.setToolTipText("Peptide Ion Score Threshold");
			ionScoreRbn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent evt) {
					ionScoreSpn.setEnabled(true);
					fdrScoreSpn.setEnabled(false);
				}
			});
			bg.add(ionScoreRbn);
			
			int ionScore = (Integer) MascotParameters.this.get("ionScore").getValue();
			ionScoreSpn = new JSpinner(new SpinnerNumberModel(ionScore, 0, null, 1));
			ionScoreSpn.setToolTipText("Peptide Ion Score Threshold");
			
			// FDR score controls
			fdrScoreRbn = new JRadioButton("False Discovery Rate", false);
			fdrScoreRbn.setToolTipText("Maximum False Discovery Rate");
			fdrScoreRbn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent evt) {
					ionScoreSpn.setEnabled(false);
					fdrScoreSpn.setEnabled(true);
				}
			});
			bg.add(fdrScoreRbn);
			fdrScoreRbn.setEnabled(true);

			double fdrScore = (Double) MascotParameters.this.get("fdrScore").getValue();
			fdrScoreSpn = new JSpinner(new SpinnerNumberModel(fdrScore, 0.0, 1.0, 0.01));
			fdrScoreSpn.setEditor(new JSpinner.NumberEditor(fdrScoreSpn, "0.00"));
			fdrScoreSpn.setToolTipText("Maximum False Discovery Rate");
			fdrScoreSpn.setEnabled(false);

			// add components to panel
			panel.add(ionScoreRbn, CC.xy(1, 1));
			panel.add(ionScoreSpn, CC.xy(3, 1));
			panel.add(fdrScoreRbn, CC.xy(1, 3));
			panel.add(fdrScoreSpn, CC.xy(3, 3));
			
			return panel;
		}
		
		@Override
		public boolean applyChanges() {
			boolean changed = false;
			
			Object oldType = MascotParameters.this.get("filterType").getValue();
			Boolean type = ionScoreRbn.isSelected();
			MascotParameters.this.setValue("filterType", type);
			changed |= !oldType.equals(type);

			Object oldValue = MascotParameters.this.get("ionScore").getValue();
			Integer value = (Integer) ionScoreSpn.getValue();
			MascotParameters.this.setValue("ionScore", value);
			if (type == ION_SCORE) {
				changed |= !oldValue.equals(value);
			}

			oldValue = MascotParameters.this.get("fdrScore").getValue();
			Double value2 = (Double) fdrScoreSpn.getValue();
			MascotParameters.this.setValue("fdrScore", value2);
			if (type == FDR_SCORE) {
				changed |= !oldValue.equals(value2);
			}
			
			return changed;
		}
		
		@Override
		public void restoreDefaults() {
			ionScoreRbn.doClick();
			ionScoreSpn.setValue(defaultIonScore);
			fdrScoreSpn.setValue(defaultFdrScore);
			
			this.applyChanges();
		}
		
	}

}
