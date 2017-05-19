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
import javax.swing.JSpinner.NumberEditor;
import javax.swing.SpinnerNumberModel;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.settings.Parameter.ButtonParameter;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.inputpanel.DatabaseSearchSettingsPanel;

/**
 * Class for storing Mascot search engine-specific settings.
 * 
 * @author T. Muth, F. Kohrs, A. Behne
 */
@SuppressWarnings("serial")
public class MascotParameters extends ParameterMap {
	
	/**
	 * Constructs a parameter map initialized with default values.
	 */
	public MascotParameters() {
        this.initDefaults();
	}

	@Override
	public void initDefaults() {
		
		int defaultIonScore = 15;
		double defaultFDR = 0.05;
		
		/* Hidden parameters */
        put("filterType", new Parameter.BooleanParameter(true, null, null, "General"));
        put("ionScore", new Parameter.NumberParameter(defaultIonScore, 0, null, null, null, "General"));
		this.put("fdrScore", new Parameter.NumberParameter(defaultFDR, 0.0, 1.0, null, null, "General"));

		/* Editable parameters */
        put("filter", new MascotParameters.FilteringParameters(defaultIonScore, defaultFDR, "Filtering"));

		/* editable parameter for sequence querying*/
		this.put("useFasta", new Parameter.BooleanParameter(true, "Get Input sequence from FASTA", null,  "FASTA"));

		/* Non-editable parameters */
		this.put("precTol", new Parameter.NumberParameter(0.0, null, null, false, "Precursor Ion Tolerance", "The precursor mass tolerance.", "Search Settings"));
		this.put("fragTol", new Parameter.NumberParameter(0.0, null, null, false, "Fragment Ion Tolerance", "The fragment mass tolerance.", "Search Settings"));
		this.put("missClv", new Parameter.NumberParameter(0, null, null, false, "Missed Cleavages (max)", "The maximum number of missed cleavages.", "Search Settings"));

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
		((MascotParameters.FilteringParameters) get("filter")).setDecoy(decoy);
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
		private final int defaultIonScore;
		/** The default FDR score. */
		private final double defaultFdrScore;

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
		private boolean decoy;

		/**
		 * Creates an instance of Mascot filtering parameters using the
		 * specified default ion score and maximum FDR value.
		 * @param ionScore the ion score
		 * @param fdrScore the FDR value
		 * @param section the section identifier of the parameter
		 */
		public FilteringParameters(int ionScore, double fdrScore, String section) {
			super(null, null, null, section);
            defaultIonScore = ionScore;
            defaultFdrScore = fdrScore;
		}

		/**
		 * Sets whether decoy spectra are evaluated in the loaded Mascot file
		 * and therefore whether FDR filtering is possible.
		 * @param decoy <code>true</code> if FDR filtering is possible,
		 *  <code>false</code> otherwise
		 */
		public void setDecoy(boolean decoy) {
			this.decoy = decoy;
			if (this.fdrScoreRbn != null) {
                this.fdrScoreRbn.setEnabled(decoy);
			}
		}

		@Override
		public JComponent createLeftComponent() {
			if (this.panel == null) {
				// lazily instantiate panel
                this.panel = createPanel();
			}
			return this.panel;
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
            this.ionScoreRbn = new JRadioButton("Peptide Ion Score", true);
            this.ionScoreRbn.setToolTipText("Peptide Ion Score Threshold");
            this.ionScoreRbn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent evt) {
                    MascotParameters.FilteringParameters.this.ionScoreSpn.setEnabled(true);
                    MascotParameters.FilteringParameters.this.fdrScoreSpn.setEnabled(false);
				}
			});
			bg.add(this.ionScoreRbn);

			int ionScore = (Integer) get("ionScore").getValue();
            this.ionScoreSpn = new JSpinner(new SpinnerNumberModel(ionScore, 0, null, 1));
            this.ionScoreSpn.setToolTipText("Peptide Ion Score Threshold");

			// FDR score controls
            this.fdrScoreRbn = new JRadioButton("False Discovery Rate", false);
            this.fdrScoreRbn.setToolTipText("Maximum False Discovery Rate");
            this.fdrScoreRbn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent evt) {
                    MascotParameters.FilteringParameters.this.ionScoreSpn.setEnabled(false);
                    MascotParameters.FilteringParameters.this.fdrScoreSpn.setEnabled(true);
				}
			});
			bg.add(this.fdrScoreRbn);
            this.fdrScoreRbn.setEnabled(true);

			double fdrScore = (Double) get("fdrScore").getValue();
            this.fdrScoreSpn = new JSpinner(new SpinnerNumberModel(fdrScore, 0.0, 1.0, 0.01));
            this.fdrScoreSpn.setEditor(new NumberEditor(this.fdrScoreSpn, "0.00"));
            this.fdrScoreSpn.setToolTipText("Maximum False Discovery Rate");
            this.fdrScoreSpn.setEnabled(false);

			// add components to panel
			panel.add(this.ionScoreRbn, CC.xy(1, 1));
			panel.add(this.ionScoreSpn, CC.xy(3, 1));
			panel.add(this.fdrScoreRbn, CC.xy(1, 3));
			panel.add(this.fdrScoreSpn, CC.xy(3, 3));

			return panel;
		}

		@Override
		public boolean applyChanges() {
			boolean changed = false;

			Object oldType = get("filterType").getValue();
			Boolean type = this.ionScoreRbn.isSelected();
			MascotParameters.this.setValue("filterType", type);
			changed |= !oldType.equals(type);

			Object oldValue = get("ionScore").getValue();
			Integer value = (Integer) this.ionScoreSpn.getValue();
			MascotParameters.this.setValue("ionScore", value);
			if (type == MascotParameters.FilteringParameters.ION_SCORE) {
				changed |= !oldValue.equals(value);
			}

			oldValue = get("fdrScore").getValue();
			Double value2 = (Double) this.fdrScoreSpn.getValue();
			MascotParameters.this.setValue("fdrScore", value2);
			if (type == MascotParameters.FilteringParameters.FDR_SCORE) {
				changed |= !oldValue.equals(value2);
			}
			
			return changed;
		}
		
		@Override
		public void restoreDefaults() {
            this.ionScoreRbn.doClick();
            this.ionScoreSpn.setValue(this.defaultIonScore);
            this.fdrScoreSpn.setValue(this.defaultFdrScore);

            applyChanges();
		}
		
	}

}
