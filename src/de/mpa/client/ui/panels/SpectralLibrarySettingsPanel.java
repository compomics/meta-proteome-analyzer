package de.mpa.client.ui.panels;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.SpecSimSettings;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.ComponentTitledBorder;
import de.mpa.client.ui.DisableComboBox;

@SuppressWarnings("serial")
public class SpectralLibrarySettingsPanel extends JPanel {

	/**
	 * The spinner to define the precursor ion mass tolerance window.
	 */
	private JSpinner tolMzSpn;

	/**
	 * The check box to define whether only annotated spectra shall be
	 * considered in search queries.
	 */
	private JCheckBox annotChk;

	/**
	 * The spinner to define which experiment's spectra shall be searched
	 * against. A value of 0 denotes no limitation by experiment id.
	 */
	private JSpinner expIdSpn;

	/**
	 * The combo box containing available vectorization methods.
	 */
	@SuppressWarnings("rawtypes")
	private JComboBox vectMethodCbx;

	/**
	 * The spinner to define the bin width used in vectorization of spectra.
	 */
	private JSpinner binWidthSpn;
	
	/**
	 * The spinner to define the bin center's shift along the m/z axis used in
	 * binning.
	 */
	private JSpinner binShiftSpn;

	/**
	 * A sub-panel containing profiling-specific parameters.
	 */
	private JPanel profilingPnl;

	/**
	 * The combo box containing available profiling shape types.
	 */
	@SuppressWarnings("rawtypes")
	private JComboBox proMethodCbx;

	/**
	 * The spinner to define peak base width windows used in profiling.
	 */
	private JSpinner proBaseWidthSpn;

	/**
	 * The check box for determining whether only a certain number of most
	 * intensive peaks shall be considered in spectral comparison.
	 */
	private JCheckBox pickChk;

	/**
	 * The spinner to define the amount of most intensive peaks to be used for
	 * spectral comparison.
	 */
	private JSpinner pickSpn;

	/**
	 * The combo box containing available input transformation methods.
	 */
	@SuppressWarnings("rawtypes")
	private JComboBox trafoCbx;

	/**
	 * The combo box containing available spectral similarity scoring
	 * algorithms.
	 */
	private DisableComboBox measureCbx;

	/**
	 * The spinner to define the amount of neighboring bins that are evaluated
	 * when using cross-correlation.
	 */
	private JSpinner xCorrOffSpn;

	/**
	 * The spinner to define the minimum score threshold above which a candidate
	 * spectrum is accepted as a positive hit.
	 */
	private JSpinner threshScSpn;

	/**
	 * Constructs a panel containing controls for spectral library search settings.
	 */
	public SpectralLibrarySettingsPanel() {
        this.initComponents();
	}

	/**
	 * Method to initialize the panel's components
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initComponents() {

		CellConstraints cc = new CellConstraints();

        setLayout(new FormLayout("7dlu, p:g, 7dlu", // col
				"5dlu, p, 5dlu, f:p:g, 7dlu")); // row

		// spectral library search parameters
		JPanel paramDbPnl = new JPanel();
		paramDbPnl.setLayout(new FormLayout("5dlu, r:p:g, 5dlu", // col
				"0dlu, p, 5dlu, p, 5dlu")); // row
		// paramDbPnl.setBorder(BorderFactory.createTitledBorder("Search parameters"));
		paramDbPnl.setBorder(new ComponentTitledBorder(new JLabel(
				"Search parameters"), paramDbPnl));

		JPanel topPnl = new JPanel();
		topPnl.setLayout(new BoxLayout(topPnl, BoxLayout.X_AXIS));
		topPnl.add(new JLabel("Precursor Mass Tolerance: "));
        this.tolMzSpn = new JSpinner(new SpinnerNumberModel(10.0, 0.0, null, 0.1));
        this.tolMzSpn.setPreferredSize(new Dimension((int) (this.tolMzSpn
				.getPreferredSize().width * 1.75),
                this.tolMzSpn.getPreferredSize().height));
        this.tolMzSpn.setEditor(new NumberEditor(this.tolMzSpn, "0.00"));
        this.tolMzSpn.setToolTipText("The precursor mass tolerance.");
		topPnl.add(this.tolMzSpn);
		topPnl.add(new JLabel(" Da"));

        this.annotChk = new JCheckBox("Search only among annotated spectra", true);

		JPanel bottomPnl = new JPanel(new FormLayout("r:p", "p"));
		JButton advBtn = new JButton("Advanced Settings");
		bottomPnl.add(advBtn, cc.xy(1, 1));

		JPanel advPnl = new JPanel(new FormLayout("5dlu, p, 5dlu",
				"0dlu, p, 5dlu, p, 5dlu"));
		advPnl.setBorder(BorderFactory.createTitledBorder("Advanced Settings"));

		JPanel expIdPnl = new JPanel();
		expIdPnl.setLayout(new BoxLayout(expIdPnl, BoxLayout.X_AXIS));
        this.expIdSpn = new JSpinner(new SpinnerNumberModel(1L, 0L, null, 1L));
        this.expIdSpn.setPreferredSize(new Dimension((int)(this.expIdSpn.getPreferredSize().width * 1.5),
                this.expIdSpn.getPreferredSize().height));
        this.expIdSpn.setEnabled(false);

		JCheckBox expIdChk = new JCheckBox(
				"Search only from experiment ID: ", false);
		expIdChk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
                SpectralLibrarySettingsPanel.this.expIdSpn.setEnabled(((JCheckBox) e.getSource()).isSelected());
			}
		});
		expIdPnl.add(expIdChk);
		expIdPnl.add(this.expIdSpn);

		advPnl.add(this.annotChk, cc.xy(2, 2));
		advPnl.add(expIdPnl, cc.xy(2, 4));

		advBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean annotatedOnly = SpectralLibrarySettingsPanel.this.annotChk.isSelected();
				boolean expIdWanted = expIdChk.isSelected();
				Long expID = (Long) SpectralLibrarySettingsPanel.this.expIdSpn.getValue();
				int res = JOptionPane.showConfirmDialog(ClientFrame.getInstance(), advPnl,
						"Advanced Settings", JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE);
				if (res != JOptionPane.OK_OPTION) {
                    SpectralLibrarySettingsPanel.this.annotChk.setSelected(annotatedOnly);
					expIdChk.setSelected(expIdWanted);
                    SpectralLibrarySettingsPanel.this.expIdSpn.setEnabled(expIdWanted);
                    SpectralLibrarySettingsPanel.this.expIdSpn.setValue(expID);
				}
			}
		});

		paramDbPnl.add(topPnl, cc.xy(2, 2));
		paramDbPnl.add(bottomPnl, cc.xy(2, 4));

		// similarity scoring parameters
		JPanel paramScPnl = new JPanel();
		paramScPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu",
				"0dlu, p, 5dlu, p:g, 5dlu, p, 5dlu, p:g, 5dlu, p, 5dlu"));
		// paramScPnl.setBorder(BorderFactory.createTitledBorder("Scoring parameters"));
		paramScPnl.setBorder(new ComponentTitledBorder(new JLabel(
				"Scoring parameters"), paramScPnl));

		// sub-panel for vectorization parameters
		JPanel vectPnl = new JPanel();
		vectPnl.setLayout(new FormLayout("p:g", "p, 5dlu, p, 5dlu, p, 5dlu, p"));

		// sub-sub-panel for vectorization method
		JPanel vectMethodPnl = new JPanel();
		vectMethodPnl.setLayout(new FormLayout("p, 5dlu, p:g", "p"));

        this.vectMethodCbx = new JComboBox(new String[] { "Peak matching", "Direct binning", "Profiling" });
        this.vectMethodCbx.setSelectedIndex(1);

		vectMethodPnl.add(new JLabel("Vectorization:"), cc.xy(1, 1));
		vectMethodPnl.add(this.vectMethodCbx, cc.xy(3, 1));

		// sub-sub-panel for general vectorization settings
		JPanel genVectSetPnl = new JPanel();
		genVectSetPnl.setLayout(new FormLayout("5dlu:g, p, 5dlu, p, 2dlu, p",
				"p, 5dlu, p"));

        this.binWidthSpn = new JSpinner(new SpinnerNumberModel(1.0, Double.MIN_VALUE, null, 0.1));
        this.binWidthSpn.setEditor(new NumberEditor(this.binWidthSpn, "0.00"));
		
		JLabel binShiftLbl = new JLabel("Bin Shift:");
        this.binShiftSpn = new JSpinner(new SpinnerNumberModel(0.0, null, null, 0.1));
        this.binShiftSpn.setEditor(new NumberEditor(this.binShiftSpn, "0.00"));
		JLabel binShiftLbl2 = new JLabel("Da");
		
		genVectSetPnl.add(new JLabel("Bin Width:"), cc.xy(2, 1));
		genVectSetPnl.add(this.binWidthSpn, cc.xy(4, 1));
		genVectSetPnl.add(new JLabel("Da"), cc.xy(6, 1));
		genVectSetPnl.add(binShiftLbl, cc.xy(2, 3));
		genVectSetPnl.add(this.binShiftSpn, cc.xy(4, 3));
		genVectSetPnl.add(binShiftLbl2, cc.xy(6, 3));

		// sub-sub-panels for profiling settings
        this.profilingPnl = new JPanel(new FormLayout("p, 5dlu, 0px:g, 5dlu, p, 2dlu, p", "p, 5dlu, p"));

        this.proMethodCbx = new JComboBox(new String[] { "Piecewise linear", "Gaussian function" });

        this.profilingPnl.add(new JLabel("Profile Shape:"), cc.xy(1, 1));
        this.profilingPnl.add(this.proMethodCbx, cc.xyw(3, 1, 5));

        this.proBaseWidthSpn = new JSpinner(new SpinnerNumberModel(0.5, Double.MIN_VALUE, null, 0.1));
        this.proBaseWidthSpn.setEditor(new NumberEditor(this.proBaseWidthSpn, "0.00"));

        this.profilingPnl.add(new JLabel("Peak Base Width:", SwingConstants.RIGHT), cc.xyw(1, 3, 3));
        this.profilingPnl.add(this.proBaseWidthSpn, cc.xy(5, 3));
        this.profilingPnl.add(new JLabel("Da"), cc.xy(7, 3));
		
		for (Component comp : this.profilingPnl.getComponents()) {
			comp.setEnabled(false);
		}

		// sub-sub panel for peak picking
		JPanel pickPnl = new JPanel();
		pickPnl.setLayout(new BoxLayout(pickPnl, BoxLayout.X_AXIS));

        this.pickChk = new JCheckBox("Pick only ");

        this.pickSpn = new JSpinner(new SpinnerNumberModel(20, 1, null, 1));
        this.pickSpn.setEnabled(false);

		pickPnl.add(this.pickChk);
		pickPnl.add(this.pickSpn);
		pickPnl.add(new JLabel(" most intensive peaks"));
		pickPnl.setEnabled(false);

		// add action listener to peak picking checkbox to synch enable state
		// with spinner
        this.pickChk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean selected = ((JCheckBox) e.getSource()).isSelected();
				pickPnl.setEnabled(selected);
                SpectralLibrarySettingsPanel.this.pickSpn.setEnabled(selected);
			}
		});

		vectPnl.add(vectMethodPnl, cc.xy(1, 1));
		vectPnl.add(genVectSetPnl, cc.xy(1, 3));
		vectPnl.add(this.profilingPnl, cc.xy(1, 5));
		vectPnl.add(pickPnl, cc.xy(1, 7));

		// add action listener to vectorization method combobox to disable/enable
		// method-specific elements
        this.vectMethodCbx.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				/** Boolean flags, { bin shift, profiling } */
				boolean[] enabled = null;
                SpectralLibrarySettingsPanel.this.measureCbx.setItemEnabledAt(3, true);
				switch (SpectralLibrarySettingsPanel.this.vectMethodCbx.getSelectedIndex()) {
				case 0:		// peak matching
					// disable cross-correlation
					// TODO: make cross-correlation work with peak matching, if possible
					if (SpectralLibrarySettingsPanel.this.measureCbx.getSelectedIndex() == 3) {
                        SpectralLibrarySettingsPanel.this.measureCbx.setSelectedIndex(1);
					}
                    SpectralLibrarySettingsPanel.this.measureCbx.setItemEnabledAt(3, false);
					enabled = new boolean[] { false, false };
					break;
				case 1:		// direct binning
					enabled = new boolean[] { true, false };
					break;
				case 2:		// profiling
					enabled = new boolean[] { true, true };
					break;
				}
				binShiftLbl.setEnabled(enabled[0]);
                SpectralLibrarySettingsPanel.this.binShiftSpn.setEnabled(enabled[0]);
				binShiftLbl2.setEnabled(enabled[0]);
                SpectralLibrarySettingsPanel.this.profilingPnl.setEnabled(enabled[1]);
                SpectralLibrarySettingsPanel.this.setChildrenEnabled(SpectralLibrarySettingsPanel.this.profilingPnl, enabled[1]);
			}
		});

		// sub-panel for data transformation settings
		JPanel trafoPnl = new JPanel();
		trafoPnl.setLayout(new FormLayout("p, 5dlu, p:g", "p"));

        this.trafoCbx = new JComboBox(new String[] { "None", "Square root",
				"Logarithmic" });
        this.trafoCbx.setSelectedIndex(1);

		trafoPnl.add(new JLabel("Transformation:"), cc.xy(1, 1));
		trafoPnl.add(this.trafoCbx, cc.xy(3, 1));

		// sub-panel for similarity scoring parameters
		JPanel scoringPnl = new JPanel();
		scoringPnl.setLayout(new FormLayout("p, 5dlu, p:g", "p, 5dlu, p"));

        this.measureCbx = new DisableComboBox(new Object[] {
				"Euclidean distance",
				"Cosine correlation",
				"Pearson's correlation",
				"Cross-correlation" });
        this.measureCbx.setSelectedIndex(1);

		// sub-sub-panel for further scoring parameters
		JPanel scorSubPnl = new JPanel();
		scorSubPnl.setLayout(new FormLayout("5dlu:g, r:p, 2dlu, p, 2dlu, p",
				"p, 5dlu, p"));

		JLabel xCorrOffLbl = new JLabel("Correlation Offsets  \u00b1"); // +/- as unicode
		xCorrOffLbl.setEnabled(false);
        this.xCorrOffSpn = new JSpinner(new SpinnerNumberModel(75, 1, null, 1));
        this.xCorrOffSpn.setEnabled(false);
		JLabel xCorrOffLbl2 = new JLabel("Bins");
		xCorrOffLbl2.setEnabled(false);

        this.threshScSpn = new JSpinner(new SpinnerNumberModel(0.5, null, null, 0.1));
        this.threshScSpn.setPreferredSize(new Dimension((int) (this.threshScSpn
				.getPreferredSize().width * 1.25), this.threshScSpn
				.getPreferredSize().height));
        this.threshScSpn.setEditor(new NumberEditor(this.threshScSpn, "0.00"));

		scorSubPnl.add(xCorrOffLbl, cc.xy(2, 1));
		scorSubPnl.add(this.xCorrOffSpn, cc.xy(4, 1));
		scorSubPnl.add(xCorrOffLbl2, cc.xy(6, 1));
		scorSubPnl.add(new JLabel("Score Threshold  \u2265"), cc.xy(2, 3)); // >= as unicode
		scorSubPnl.add(this.threshScSpn, cc.xy(4, 3));
		scorSubPnl.setEnabled(false);

		// add action listener to similarity measure combo box to disable/enable
		// xcorr-specific elements
        this.measureCbx.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switch (SpectralLibrarySettingsPanel.this.measureCbx.getSelectedIndex()) {
				case 0:		// euclidean distance
				case 1:		// cosine correlation
				case 2:		// pearson's correlation
					xCorrOffLbl.setEnabled(false);
                    SpectralLibrarySettingsPanel.this.xCorrOffSpn.setEnabled(false);
					xCorrOffLbl2.setEnabled(false);
					break;
				case 3:		// cross-correlation
					xCorrOffLbl.setEnabled(true);
                    SpectralLibrarySettingsPanel.this.xCorrOffSpn.setEnabled(true);
					xCorrOffLbl2.setEnabled(true);
					break;
				}
			}
		});

		scoringPnl.add(new JLabel("Measure:"), cc.xy(1, 1));
		scoringPnl.add(this.measureCbx, cc.xy(3, 1));
		scoringPnl.add(scorSubPnl, cc.xyw(1, 3, 3));

		paramScPnl.add(vectPnl, cc.xy(2, 2));
		paramScPnl.add(new JSeparator(), cc.xy(2, 4));
		paramScPnl.add(trafoPnl, cc.xy(2, 6));
		paramScPnl.add(new JSeparator(), cc.xy(2, 8));
		paramScPnl.add(scoringPnl, cc.xy(2, 10));

		// add everything to parent panel
        add(paramDbPnl, cc.xy(2, 2));
		// this.add(previewPnl, cc.xywh(4,2,1,5));
        add(paramScPnl, cc.xy(2, 4));

	}

	/**
	 * Method to gather spectral similarity search settings.
	 * @return The SpecSimSettings object containing values from various GUI
	 *         elements.
	 */
	public SpecSimSettings gatherSpecSimSettings() {
		SpecSimSettings settings = new SpecSimSettings();
		settings.setExperimentID((this.expIdSpn.isEnabled()) ? (Long) this.expIdSpn.getValue() : 0L);
		settings.setAnnotatedOnly(this.annotChk.isSelected());
		settings.setTolMz((Double) this.tolMzSpn.getValue());
		settings.setPickCount(this.pickSpn.isEnabled() ? (Integer) this.pickSpn.getValue() : 0);
		settings.setThreshScore((Double) this.threshScSpn.getValue());
		settings.setCompIndex(this.measureCbx.getSelectedIndex());
		settings.setTrafoIndex(this.trafoCbx.getSelectedIndex());
		settings.setVectIndex(this.vectMethodCbx.getSelectedIndex());
		settings.setBinWidth((Double) this.binWidthSpn.getValue());
		settings.setBinShift((Double) this.binShiftSpn.getValue());
		settings.setProfileIndex(this.proMethodCbx.getSelectedIndex());
		settings.setBaseWidth((Double) this.proBaseWidthSpn.getValue());
		settings.setXCorrOffset((Integer) this.xCorrOffSpn.getValue());
		return settings;
	}
	
	/**
	 * Method to toggle the enabled state of the whole panel. Honors separate
	 * enabled state of specific sub-components on restore.
	 */
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
        this.setChildrenEnabled(this, enabled);
		if (enabled) {	// restore proper enabled state by enforcing listener events
            this.vectMethodCbx.setSelectedIndex(this.vectMethodCbx.getSelectedIndex());
            this.pickSpn.setEnabled(this.pickChk.isSelected());
            this.measureCbx.setSelectedIndex(this.measureCbx.getSelectedIndex());
		}
	}

	/**
	 * Method to recursively iterate a component's children and set their
	 * enabled state.
	 * @param parent the parent component
	 * @param enabled <code>true if the sub-components should be enabled,
	 * 				  <code>false</code> otherwise
	 */
	private void setChildrenEnabled(JComponent parent, boolean enabled) {
		for (Component child : parent.getComponents()) {
			if (child instanceof JComponent) {
                this.setChildrenEnabled((JComponent) child, enabled);
			}
		}
		if (!(parent instanceof SpectralLibrarySettingsPanel)) { // don't mess with SpecLibSearchPanels
			parent.setEnabled(enabled);
			Border border = parent.getBorder();
			if (border instanceof ComponentTitledBorder) {
				((ComponentTitledBorder) border).setEnabled(enabled);
			}
		}
	}

}