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
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.SpecSimSettings;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.ComponentTitledBorder;
import de.mpa.client.ui.DisableComboBox;

public class SpecLibSearchPanel extends JPanel {

	/**
	 * The parent frame.
	 */
	private ClientFrame clientFrame;

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
	private JComboBox vectMethodCbx;

	/**
	 * The spinner to define the bin width used in vectorization of spectra.
	 */
	private JSpinner binWidthSpn;
	
	/**
	 * Left-hand side label of bin shift spinner component.
	 */
	private JLabel binShiftLbl;
	
	/**
	 * The spinner to define the bin center's shift along the m/z axis used in
	 * binning.
	 */
	private JSpinner binShiftSpn;

	/**
	 * Right-hand side label of bin shift spinner component.
	 */
	private JLabel binShiftLbl2;

	/**
	 * A sub-panel containing profiling-specific parameters.
	 */
	private JPanel proMethodPnl;

	/**
	 * The combo box containing available profiling shape types.
	 */
	private JComboBox proMethodCbx;

	/**
	 * A sub-panel containing profiling-specific parameters.
	 */
	private JPanel proBaseWidthPnl;

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
	private JComboBox trafoCbx;

	/**
	 * The combo box containing available spectral similarity scoring
	 * algorithms.
	 */
	private DisableComboBox measureCbx;

	/**
	 * Left-hand side label of cross-correlation spinner component.
	 */
	private JLabel xCorrOffLbl;

	/**
	 * The spinner to define the amount of neighboring bins that are evaluated
	 * when using cross-correlation.
	 */
	private JSpinner xCorrOffSpn;

	/**
	 * Right-hand side label of cross-correlation spinner component.
	 */
	private JLabel xCorrOffLbl2;

	/**
	 * The spinner to define the minimum score threshold above which a candidate
	 * spectrum is accepted as a positive hit.
	 */
	private JSpinner threshScSpn;

	/**
	 * Class constructor
	 * 
	 * @param clientFrame
	 */
	public SpecLibSearchPanel(ClientFrame clientFrame) {
		this.clientFrame = clientFrame;
		initComponents();
	}

	/**
	 * Method to initialize the panel's components
	 */
	private void initComponents() {

		CellConstraints cc = new CellConstraints();

		this.setLayout(new FormLayout("7dlu, p:g, 7dlu", // col
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
		tolMzSpn = new JSpinner(new SpinnerNumberModel(10.0, 0.0, null, 0.1));
		tolMzSpn.setPreferredSize(new Dimension((int) (tolMzSpn
				.getPreferredSize().width * 1.75),
				tolMzSpn.getPreferredSize().height));
		tolMzSpn.setEditor(new JSpinner.NumberEditor(tolMzSpn, "0.00"));
		tolMzSpn.setToolTipText("The precursor mass tolerance.");
		topPnl.add(tolMzSpn);
		topPnl.add(new JLabel(" Da"));

		annotChk = new JCheckBox("Search only among annotated spectra", true);

		JPanel bottomPnl = new JPanel(new FormLayout("r:p", "p"));
		JButton advBtn = new JButton("Advanced Settings");
		bottomPnl.add(advBtn, cc.xy(1, 1));

		final JPanel advPnl = new JPanel(new FormLayout("5dlu, p, 5dlu",
				"0dlu, p, 5dlu, p, 5dlu"));
		advPnl.setBorder(BorderFactory.createTitledBorder("Advanced Settings"));

		JPanel expIdPnl = new JPanel();
		expIdPnl.setLayout(new BoxLayout(expIdPnl, BoxLayout.X_AXIS));
		expIdSpn = new JSpinner(new SpinnerNumberModel(1L, 0L, null, 1L));
		expIdSpn.setPreferredSize(new Dimension((int)(expIdSpn.getPreferredSize().width * 1.5),
				expIdSpn.getPreferredSize().height));
		expIdSpn.setEnabled(false);

		final JCheckBox expIdChk = new JCheckBox(
				"Search only from experiment ID: ", false);
		expIdChk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				expIdSpn.setEnabled(((JCheckBox) e.getSource()).isSelected());
			}
		});
		expIdPnl.add(expIdChk);
		expIdPnl.add(expIdSpn);

		advPnl.add(annotChk, cc.xy(2, 2));
		advPnl.add(expIdPnl, cc.xy(2, 4));

		advBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean annotatedOnly = annotChk.isSelected();
				boolean expIdWanted = expIdChk.isSelected();
				Long expID = (Long) expIdSpn.getValue();
				int res = JOptionPane.showConfirmDialog(clientFrame, advPnl,
						"Advanced Settings", JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE);
				if (res != JOptionPane.OK_OPTION) {
					annotChk.setSelected(annotatedOnly);
					expIdChk.setSelected(expIdWanted);
					expIdSpn.setEnabled(expIdWanted);
					expIdSpn.setValue(expID);
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
		final JPanel vectPnl = new JPanel();
		vectPnl.setLayout(new FormLayout("p:g",
				"p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p"));

		// sub-sub-panel for vectorization method
		JPanel vectMethodPnl = new JPanel();
		vectMethodPnl.setLayout(new FormLayout("p, 5dlu, p:g", "p"));

		vectMethodCbx = new JComboBox(
				new Object[] { "Peak matching", "Direct binning", "Profiling" });
		vectMethodCbx.setSelectedIndex(1);

		vectMethodPnl.add(new JLabel("Vectorization:"), cc.xy(1, 1));
		vectMethodPnl.add(vectMethodCbx, cc.xy(3, 1));

		// sub-sub-panel for general vectorization settings
		JPanel genVectSetPnl = new JPanel();
		genVectSetPnl.setLayout(new FormLayout("5dlu:g, p, 5dlu, p, 2dlu, p",
				"p, 5dlu, p"));

		binWidthSpn = new JSpinner(new SpinnerNumberModel(1.0, Double.MIN_VALUE, null, 0.1));
		binWidthSpn.setEditor(new JSpinner.NumberEditor(binWidthSpn, "0.00"));
		
		binShiftLbl = new JLabel("Bin Shift:");
		binShiftSpn = new JSpinner(new SpinnerNumberModel(0.0, null, null, 0.1));
		binShiftSpn.setEditor(new JSpinner.NumberEditor(binShiftSpn, "0.00"));
		binShiftLbl2 = new JLabel("Da");

		genVectSetPnl.add(new JLabel("Bin Width:"), cc.xy(2, 1));
		genVectSetPnl.add(binWidthSpn, cc.xy(4, 1));
		genVectSetPnl.add(new JLabel("Da"), cc.xy(6, 1));
		genVectSetPnl.add(binShiftLbl, cc.xy(2, 3));
		genVectSetPnl.add(binShiftSpn, cc.xy(4, 3));
		genVectSetPnl.add(binShiftLbl2, cc.xy(6, 3));

		// sub-sub-panels for profiling settings
		proMethodPnl = new JPanel();
		proMethodPnl.setLayout(new FormLayout("p, 5dlu, p:g", "p"));

		proMethodCbx = new JComboBox(new Object[] { "Piecewise linear", "Gaussian function" });

		proMethodPnl.add(new JLabel("Profile Shape:"), cc.xy(1, 1));
		proMethodPnl.add(proMethodCbx, cc.xy(3, 1));
		proMethodPnl.setEnabled(false);
		for (Component comp : proMethodPnl.getComponents()) {
			comp.setEnabled(false);
		}

		proBaseWidthPnl = new JPanel();
		proBaseWidthPnl.setLayout(new FormLayout("5dlu:g, p, 5dlu, p, 2dlu, p",
				"p"));

		proBaseWidthSpn = new JSpinner(new SpinnerNumberModel(0.5, Double.MIN_VALUE, null, 0.1));
		proBaseWidthSpn.setEditor(new JSpinner.NumberEditor(proBaseWidthSpn, "0.00"));

		proBaseWidthPnl.add(new JLabel("Peak Base Width:"), cc.xy(2, 1));
		proBaseWidthPnl.add(proBaseWidthSpn, cc.xy(4, 1));
		proBaseWidthPnl.add(new JLabel("Da"), cc.xy(6, 1));
		proBaseWidthPnl.setEnabled(false);
		for (Component comp : proBaseWidthPnl.getComponents()) {
			comp.setEnabled(false);
		}

		// sub-sub panel for peak picking
		final JPanel pickPnl = new JPanel();
		pickPnl.setLayout(new BoxLayout(pickPnl, BoxLayout.X_AXIS));

		pickChk = new JCheckBox("Pick only ");

		pickSpn = new JSpinner(new SpinnerNumberModel(20, 1, null, 1));
		pickSpn.setEnabled(false);

		pickPnl.add(pickChk);
		pickPnl.add(pickSpn);
		pickPnl.add(new JLabel(" most intensive peaks"));
		pickPnl.setEnabled(false);

		// add action listener to peak picking checkbox to synch enable state
		// with spinner
		pickChk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean selected = ((JCheckBox) e.getSource()).isSelected();
				pickPnl.setEnabled(selected);
				pickSpn.setEnabled(selected);
			}
		});

		vectPnl.add(vectMethodPnl, cc.xy(1, 1));
		vectPnl.add(genVectSetPnl, cc.xy(1, 3));
		vectPnl.add(proMethodPnl, cc.xy(1, 5));
		vectPnl.add(proBaseWidthPnl, cc.xy(1, 7));
		vectPnl.add(pickPnl, cc.xy(1, 9));

		// add action listener to vectorization method combobox to disable/enable
		// profiling-specific elements
		vectMethodCbx.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				boolean[] flags = { false, false, false };
				measureCbx.setItemEnabledAt(3, true);
				switch (vectMethodCbx.getSelectedIndex()) {
				case 0:		// peak matching
					// disable cross-correlation
					// TODO: make cross-correlation work with peak matching, if possible
					if (measureCbx.getSelectedIndex() == 3)
						measureCbx.setSelectedIndex(1);
					measureCbx.setItemEnabledAt(3, false);
					break;
				case 1:		// direct binning
					flags[0] = true;
					break;
				case 2:		// profiling
					flags = new boolean[] { true, true, true };
					break;
				}
				binShiftLbl.setEnabled(flags[0]);
				binShiftSpn.setEnabled(flags[0]);
				binShiftLbl2.setEnabled(flags[0]);
				proMethodPnl.setEnabled(flags[1]);
				setChildrenEnabled(proMethodPnl, flags[1]);
				proBaseWidthPnl.setEnabled(flags[2]);
				setChildrenEnabled(proBaseWidthPnl, flags[2]);
			}
		});

		// sub-panel for data transformation settings
		JPanel trafoPnl = new JPanel();
		trafoPnl.setLayout(new FormLayout("p, 5dlu, p:g", "p"));

		trafoCbx = new JComboBox(new Object[] { "None", "Square root",
				"Logarithmic" });
		trafoCbx.setSelectedIndex(1);

		trafoPnl.add(new JLabel("Transformation:"), cc.xy(1, 1));
		trafoPnl.add(trafoCbx, cc.xy(3, 1));

		// sub-panel for similarity scoring parameters
		JPanel scoringPnl = new JPanel();
		scoringPnl.setLayout(new FormLayout("p, 5dlu, p:g", "p, 5dlu, p"));

		measureCbx = new DisableComboBox(new Object[] {
				"Euclidean distance",
				"Cosine correlation",
				"Pearson's correlation",
				"Cross-correlation" });
		measureCbx.setSelectedIndex(1);

		// sub-sub-panel for further scoring parameters
		JPanel scorSubPnl = new JPanel();
		scorSubPnl.setLayout(new FormLayout("5dlu:g, r:p, 2dlu, p, 2dlu, p",
				"p, 5dlu, p"));

		xCorrOffLbl = new JLabel("Correlation Offsets  \u00b1"); // +/- as unicode
		xCorrOffLbl.setEnabled(false);
		xCorrOffSpn = new JSpinner(new SpinnerNumberModel(75, 1, null, 1));
		xCorrOffSpn.setEnabled(false);
		xCorrOffLbl2 = new JLabel("Bins");
		xCorrOffLbl2.setEnabled(false);

		threshScSpn = new JSpinner(new SpinnerNumberModel(0.5, null, null, 0.1));
		threshScSpn.setPreferredSize(new Dimension((int) (threshScSpn
				.getPreferredSize().width * 1.25), threshScSpn
				.getPreferredSize().height));
		threshScSpn.setEditor(new JSpinner.NumberEditor(threshScSpn, "0.00"));

		scorSubPnl.add(xCorrOffLbl, cc.xy(2, 1));
		scorSubPnl.add(xCorrOffSpn, cc.xy(4, 1));
		scorSubPnl.add(xCorrOffLbl2, cc.xy(6, 1));
		scorSubPnl.add(new JLabel("Score Threshold  \u2265"), cc.xy(2, 3)); // >= as unicode
		scorSubPnl.add(threshScSpn, cc.xy(4, 3));
		scorSubPnl.setEnabled(false);

		// add action listener to similarity measure combo box to disable/enable
		// xcorr-specific elements
		measureCbx.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switch (measureCbx.getSelectedIndex()) {
				case 0:		// euclidean distance
				case 1:		// cosine correlation
				case 2:		// pearson's correlation
					xCorrOffLbl.setEnabled(false);
					xCorrOffSpn.setEnabled(false);
					xCorrOffLbl2.setEnabled(false);
					break;
				case 3:		// cross-correlation
					xCorrOffLbl.setEnabled(true);
					xCorrOffSpn.setEnabled(true);
					xCorrOffLbl2.setEnabled(true);
					break;
				}
			}
		});

		scoringPnl.add(new JLabel("Measure:"), cc.xy(1, 1));
		scoringPnl.add(measureCbx, cc.xy(3, 1));
		scoringPnl.add(scorSubPnl, cc.xyw(1, 3, 3));

		paramScPnl.add(vectPnl, cc.xy(2, 2));
		paramScPnl.add(new JSeparator(), cc.xy(2, 4));
		paramScPnl.add(trafoPnl, cc.xy(2, 6));
		paramScPnl.add(new JSeparator(), cc.xy(2, 8));
		paramScPnl.add(scoringPnl, cc.xy(2, 10));

		// add everything to parent panel
		this.add(paramDbPnl, cc.xy(2, 2));
		// this.add(previewPnl, cc.xywh(4,2,1,5));
		this.add(paramScPnl, cc.xy(2, 4));

	}

	/**
	 * Method to gather spectral similarity search settings.
	 * 
	 * @return The SpecSimSettings object containing values from various GUI
	 *         elements.
	 */
	public SpecSimSettings gatherSpecSimSettings() {
		SpecSimSettings settings = new SpecSimSettings();
		
		// Get the target experiment ID.
		long expID = (expIdSpn.isEnabled()) ? (Long) expIdSpn.getValue() : 0L;
		
		int pickCount = 0;
		if (pickSpn.isEnabled()) {
			pickCount = (Integer) pickSpn.getValue();
		}
		settings.setExperimentID(expID);
		settings.setAnnotatedOnly(annotChk.isSelected());
		settings.setTolMz((Double) tolMzSpn.getValue());
		settings.setPickCount(pickCount);
		settings.setThreshScore((Double) threshScSpn.getValue());
		settings.setCompIndex(measureCbx.getSelectedIndex());
		settings.setTrafoIndex(trafoCbx.getSelectedIndex());
		settings.setVectIndex(vectMethodCbx.getSelectedIndex());
		settings.setBinWidth((Double) binWidthSpn.getValue());
		settings.setBinShift((Double) binShiftSpn.getValue());
		settings.setProfileIndex(proMethodCbx.getSelectedIndex());
		settings.setBaseWidth((Double) proBaseWidthSpn.getValue());
		settings.setXCorrOffset((Integer) xCorrOffSpn.getValue());
		return settings;
	}
	
	/**
	 * Method to toggle the enabled state of the whole panel. Honors separate
	 * enabled state of specific sub-components on restore.
	 */
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		setChildrenEnabled(this, enabled);
		if (enabled) {	// restore proper enabled state by enforcing listener events
			vectMethodCbx.setSelectedIndex(vectMethodCbx.getSelectedIndex());
			pickSpn.setEnabled(pickChk.isSelected());
			measureCbx.setSelectedIndex(measureCbx.getSelectedIndex());
		}
	}

	/**
	 * Method to recursively iterate a component's children and set their
	 * enabled state.
	 * 
	 * @param parent
	 * @param enabled
	 */
	public void setChildrenEnabled(JComponent parent, boolean enabled) {
		for (Component child : parent.getComponents()) {
			if (child instanceof JComponent) {
				setChildrenEnabled((JComponent) child, enabled);
			}
		}
		if (!(parent instanceof SpecLibSearchPanel)) { // don't mess with SpecLibSearchPanels
			parent.setEnabled(enabled);
			Border border = parent.getBorder();
			if (border instanceof ComponentTitledBorder) {
				((ComponentTitledBorder) border).setEnabled(enabled);
			}
		}
	}

	/**
	 * Method to return the experiment id spinner value.
	 * 
	 * @return The experiment id.
	 */
	public long getExperimentID() {
		return (Long) expIdSpn.getValue();
	}

}