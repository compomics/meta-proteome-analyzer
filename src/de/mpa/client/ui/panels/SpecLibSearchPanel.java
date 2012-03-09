package de.mpa.client.ui.panels;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.algorithms.CrossCorrelation;
import de.mpa.algorithms.NormalizedDotProduct;
import de.mpa.algorithms.Trafo;
import de.mpa.client.SpecSimSettings;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.SpectrumTree;
import de.mpa.interfaces.SpectrumComparator;
import de.mpa.io.MascotGenericFile;
import de.mpa.ui.PlotPanel2;

public class SpecLibSearchPanel extends JPanel {

	/**
	 * The parent frame.
	 */
	private ClientFrame clientFrame;

	/**
	 * A sub-panel containing database-specific parameters.
	 */
	private JPanel paramDbPnl;
	
	/**
	 * The spinner to define the precursor ion mass tolerance window.
	 */
	private JSpinner tolMzSpn;
	
	/**
	 * The check box to define whether only annotated spectra shall be considered in search queries.
	 */
	private JCheckBox annotChk;
	
	/**
	 * The spinner to define which experiment's spectra shall be searched against.
	 * A value of 0 denotes no limitation by experiment id.
	 */
	private JSpinner expIdSpn;

	/**
	 * A sub-panel containing scoring-specific parameters.
	 */
	private JPanel paramScPnl;

	/**
	 * The spinner to define the bin width used in vectorization of spectra.
	 */
	private JSpinner binWidthSpn;
	
	/**
	 * The spinner to define the bin center's shift along the m/z axis used in binning. 
	 */
	private JSpinner binShiftSpn;

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
	private JPanel proMErrorPnl;
	
	/**
	 * The spinner to define mass error windows used in profiling.
	 */
	private JSpinner proMErrorSpn;

	/**
	 * A sub-panel containing peak picking-specific parameters.
	 */
	private JPanel pickPnl;
	
	/**
	 * The spinner to define the amount of most intensive peaks to be used for spectral comparison.
	 */
	private JSpinner pickSpn;
	
	/**
	 * The combo box containing available input transformation methods.
	 */
	private JComboBox trafoCbx;

	/**
	 * A sub-panel containing similarity measure-specific parameters.
	 */
	private JPanel scorSubPnl;

	/**
	 * The combo box containing available spectral similarity scoring algorithms.
	 */
	private JComboBox measureCbx;

	/**
	 * Left-hand side label of cross-correlation spinner component.
	 */
	private JLabel xCorrOffLbl;
	
	/**
	 * The spinner to define the amount of neighboring bins that are evaluated when using cross-correlation.
	 */
	private JSpinner xCorrOffSpn;

	/**
	 * Right-hand side label of cross-correlation spinner component.
	 */
	private JLabel xCorrOffLbl2;
	
	/**
	 * The spinner to define the minimum score threshold above which a candidate spectrum is accepted as a positive hit.
	 */
	private JSpinner threshScSpn;

	/**
	 * The plot panel displaying original and transformed spectra simultaneously.
	 */
	private PlotPanel2 prePlotPnl;

	/**
	 * Class constructor
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
		
		this.setLayout(new FormLayout("8dlu, p, 8dlu",					// col
									  "p, 5dlu, f:p:g, 8dlu, 0dlu"));	// row

		// spectral library search parameters
		paramDbPnl = new JPanel();
		paramDbPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu",		// col
										   "p, 5dlu, p, 5dlu"));	// row	
		paramDbPnl.setBorder(BorderFactory.createTitledBorder("Search parameters"));

		JPanel topPnl = new JPanel();
		topPnl.setLayout(new BoxLayout(topPnl, BoxLayout.X_AXIS));
		topPnl.add(new JLabel("Precursor mass tolerance: "));
		tolMzSpn = new JSpinner(new SpinnerNumberModel(10.0, 0.0, null, 0.1));
		tolMzSpn.setPreferredSize(new Dimension((int) (tolMzSpn.getPreferredSize().width*1.75),
												tolMzSpn.getPreferredSize().height));
		tolMzSpn.setEditor(new JSpinner.NumberEditor(tolMzSpn, "0.00"));
		tolMzSpn.setToolTipText("Precursor mass tolerance");
		topPnl.add(tolMzSpn);
		topPnl.add(new JLabel(" Da"));

		annotChk = new JCheckBox("Search only among annotated spectra", true);

//		JPanel bottomPnl = new JPanel();
//		bottomPnl.setLayout(new BoxLayout(bottomPnl, BoxLayout.X_AXIS));
//		expIdSpn = new JSpinner(new SpinnerNumberModel(1L, 0L, null, 1L));
//		expIdSpn.setEnabled(false);
//
//		final JCheckBox expIdChk = new JCheckBox("Search only from experiment ID: ", false);
//		expIdChk.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				expIdSpn.setEnabled(((JCheckBox)e.getSource()).isSelected());
//			}
//		});
//		bottomPnl.add(expIdChk);
//		bottomPnl.add(expIdSpn);
		
		paramDbPnl.add(topPnl, cc.xy(2,1));
		paramDbPnl.add(annotChk, cc.xy(2,3));
//		paramDbPnl.add(bottomPnl, cc.xy(2,5));
		
		// spectrum previewing
		JPanel previewPnl = new JPanel();
		previewPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu",
											"f:p:g, 5dlu"));
		previewPnl.setBorder(BorderFactory.createTitledBorder("Preview input"));
		prePlotPnl = new PlotPanel2(null);
		prePlotPnl.clearSpectrumFile();
//		prePlotPnl.setMiniature(true);
//		prePlotPnl.setMaxPadding(25);
		prePlotPnl.setPreferredSize(new Dimension(500, 300));
		previewPnl.add(prePlotPnl, cc.xy(2,1));
		
		RefreshPlotListener refreshPlotListener = new RefreshPlotListener();

		// similarity scoring parameters
		paramScPnl = new JPanel();
		paramScPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu",
											"p, 5dlu, p, 5dlu, p:g, 5dlu, p:g, 5dlu, p, 5dlu"));
		paramScPnl.setBorder(BorderFactory.createTitledBorder("Scoring parameters"));

		// sub-panel for binning parameters
		final JPanel binningPnl = new JPanel();
		binningPnl.setLayout(new FormLayout("p:g",
		   									"p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p"));
		
		// sub-sub-panel for binning method
		JPanel binMethodPnl = new JPanel();
		binMethodPnl.setLayout(new FormLayout("p, 5dlu, p:g", "p"));
		
		final JComboBox binningCbx = new JComboBox(new Object[] { "direct binning", "profiling" });

		binMethodPnl.add(new JLabel("Binning method"), cc.xy(1,1));
		binMethodPnl.add(binningCbx, cc.xy(3,1));

		// sub-sub-panel for general binning settings
		JPanel genBinSetPnl = new JPanel();
		genBinSetPnl.setLayout(new FormLayout("5dlu:g, p, 5dlu, p, 2dlu, p",
				 			   				  "p, 5dlu, p"));
		
		binWidthSpn = new JSpinner(new SpinnerNumberModel(1.0, 0.0, null, 0.1));
		binWidthSpn.setEditor(new JSpinner.NumberEditor(binWidthSpn, "0.00"));
		binWidthSpn.addChangeListener(refreshPlotListener);
		binShiftSpn = new JSpinner(new SpinnerNumberModel(0.0, null, null, 0.1));
		binShiftSpn.setEditor(new JSpinner.NumberEditor(binShiftSpn, "0.00"));
		binShiftSpn.addChangeListener(refreshPlotListener);
		
		genBinSetPnl.add(new JLabel("Bin width"), cc.xy(2,1));
		genBinSetPnl.add(binWidthSpn, cc.xy(4,1));
		genBinSetPnl.add(new JLabel("Da"), cc.xy(6,1));
		genBinSetPnl.add(new JLabel("Bin shift"), cc.xy(2,3));
		genBinSetPnl.add(binShiftSpn, cc.xy(4,3));
		genBinSetPnl.add(new JLabel("Da"), cc.xy(6,3));
		
		// sub-sub-panels for profiling settings
		proMethodPnl = new JPanel();
		proMethodPnl.setLayout(new FormLayout("p, 5dlu, p:g", "p"));
		
		proMethodCbx = new JComboBox(new Object[] { "linear", "gaussian" });

		proMethodPnl.add(new JLabel("Profile type"), cc.xy(1,1));
		proMethodPnl.add(proMethodCbx, cc.xy(3,1));
		proMethodPnl.setEnabled(false);
		for (Component comp : proMethodPnl.getComponents()) { comp.setEnabled(false); }
		
		proMErrorPnl = new JPanel();
		proMErrorPnl.setLayout(new FormLayout("5dlu:g, p, 5dlu, p, 2dlu, p", "p"));
		
		proMErrorSpn = new JSpinner(new SpinnerNumberModel(1.0, 0.0, null, 0.1));
		proMErrorSpn.setEditor(new JSpinner.NumberEditor(proMErrorSpn, "0.00"));
		
		proMErrorPnl.add(new JLabel("Mass error window"), cc.xy(2,1));
		proMErrorPnl.add(proMErrorSpn, cc.xy(4,1));
		proMErrorPnl.add(new JLabel("Da"), cc.xy(6,1));
		proMErrorPnl.setEnabled(false);
		for (Component comp : proMErrorPnl.getComponents()) { comp.setEnabled(false); }
		
		// sub-sub panel for peak picking
		pickPnl = new JPanel();
		pickPnl.setLayout(new BoxLayout(pickPnl, BoxLayout.X_AXIS));
		
		JCheckBox pickChk = new JCheckBox("Pick only ");
		pickChk.addActionListener(refreshPlotListener);
		
		pickSpn = new JSpinner(new SpinnerNumberModel(20, 1, null, 1));
		pickSpn.setEnabled(false);
		pickSpn.addChangeListener(refreshPlotListener);
		
		pickPnl.add(pickChk);
		pickPnl.add(pickSpn);
		pickPnl.add(new JLabel(" most intensive peaks"));
		pickPnl.setEnabled(false);
		
		// add action listener to peak picking checkbox to synch enable state with spinner
		pickChk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean selected = ((JCheckBox) e.getSource()).isSelected();
				pickPnl.setEnabled(selected);
				pickSpn.setEnabled(selected);
			}
		});
		
		binningPnl.add(binMethodPnl, cc.xy(1,1));
		binningPnl.add(genBinSetPnl, cc.xy(1,3));
		binningPnl.add(proMethodPnl, cc.xy(1,5));
		binningPnl.add(proMErrorPnl, cc.xy(1,7));
		binningPnl.add(pickPnl, cc.xy(1,9));

		// add action listener to binning method combobox to disable/enable profiling-specific elements
		binningCbx.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				if (binningCbx.getSelectedIndex() == 0) {
					proMethodPnl.setEnabled(false);
					setChildrenEnabled(proMethodPnl, false);
					proMErrorPnl.setEnabled(false);
					setChildrenEnabled(proMErrorPnl, false);
				} else {
					proMethodPnl.setEnabled(true);
					setChildrenEnabled(proMethodPnl, true);
					proMErrorPnl.setEnabled(true);
					setChildrenEnabled(proMErrorPnl, true);
				}
			}
		});
		
		// sub-panel for data transformation settings
		JPanel trafoPnl = new JPanel();
		trafoPnl.setLayout(new FormLayout("p, 5dlu, p:g", "p"));
		
		trafoCbx = new JComboBox(new Object[] { "none", "square root", "logarithmic" });
		trafoCbx.setSelectedIndex(1);
		trafoCbx.addActionListener(refreshPlotListener);
		
		trafoPnl.add(new JLabel("Input transformation"), cc.xy(1,1));
		trafoPnl.add(trafoCbx, cc.xy(3,1));
		
		// sub-panel for similarity scoring parameters
		JPanel scoringPnl = new JPanel();
		scoringPnl.setLayout(new FormLayout("p, 5dlu, p:g",
											"p, 5dlu, p"));
		
		measureCbx = new JComboBox(new Object[] { "Dot product", "Cross-correlation" });
		measureCbx.addActionListener(refreshPlotListener);
		
		// sub-sub-panel for further scoring parameters
		scorSubPnl = new JPanel();
		scorSubPnl.setLayout(new FormLayout("5dlu:g, r:p, 2dlu, p, 2dlu, p",
											"p, 5dlu, p"));

		xCorrOffLbl = new JLabel("Correlation offsets  \u00b1");
		xCorrOffLbl.setEnabled(false);
		xCorrOffSpn = new JSpinner(new SpinnerNumberModel(75, 1, null, 1));
		xCorrOffSpn.setEnabled(false);
		xCorrOffSpn.addChangeListener(refreshPlotListener);
		xCorrOffLbl2 = new JLabel("Bins");
		xCorrOffLbl2.setEnabled(false);
		
		threshScSpn = new JSpinner(new SpinnerNumberModel(0.5, null, null, 0.1));
		threshScSpn.setPreferredSize(new Dimension((int) (threshScSpn.getPreferredSize().width*1.25),
														  threshScSpn.getPreferredSize().height));
		threshScSpn.setEditor(new JSpinner.NumberEditor(threshScSpn, "0.0"));

		scorSubPnl.add(xCorrOffLbl, cc.xy(2,1));
		scorSubPnl.add(xCorrOffSpn, cc.xy(4,1));
		scorSubPnl.add(xCorrOffLbl2, cc.xy(6,1));
		scorSubPnl.add(new JLabel("Score Threshold  \u2265"), cc.xy(2,3));
		scorSubPnl.add(threshScSpn, cc.xy(4,3));
		scorSubPnl.setEnabled(false);

		// add action listener to similarity measure combo box to disable/enable xcorr-specific elements
		measureCbx.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (measureCbx.getSelectedIndex() == 0) {
					xCorrOffLbl.setEnabled(false);
					xCorrOffSpn.setEnabled(false);
					xCorrOffLbl2.setEnabled(false);
				} else {
					xCorrOffLbl.setEnabled(true);
					xCorrOffSpn.setEnabled(true);
					xCorrOffLbl2.setEnabled(true);
				}
			}
		});
		
		scoringPnl.add(new JLabel("Similarity measure"), cc.xy(1,1));
		scoringPnl.add(measureCbx, cc.xy(3,1));
		scoringPnl.add(scorSubPnl, cc.xyw(1,3,3));
		
		paramScPnl.add(binningPnl, cc.xy(2,1));
		paramScPnl.add(new JSeparator(), cc.xy(2,3));
		paramScPnl.add(trafoPnl, cc.xy(2,5));
		paramScPnl.add(new JSeparator(), cc.xy(2,7));
		paramScPnl.add(scoringPnl, cc.xy(2,9));
		
		// add everything to parent panel
		this.add(paramDbPnl, cc.xy(2,1));
//		this.add(previewPnl, cc.xywh(4,1,1,5));
		this.add(paramScPnl, cc.xy(2,3));
		
	}

	/**
	 * Method to gather spectral similarity search settings.
	 * @return The SpecSimSettings object containing values from various GUI elements.
	 */
	public SpecSimSettings gatherSpecSimSettings() {
		
		Trafo trafo = null;
		switch (trafoCbx.getSelectedIndex()) {
		case 0:
			trafo = new Trafo() { public double transform(double input) { return input; } };
			break;
		case 1:
			trafo = new Trafo() { public double transform(double input) { return Math.sqrt(input); } };
			break;
		case 2:
			trafo = new Trafo() { public double transform(double input) { return Math.log(input); } };
			break;
		}
		
		SpectrumComparator method = null;
		switch (measureCbx.getSelectedIndex()) {
		case 0:
			method = new NormalizedDotProduct((Double) binWidthSpn.getValue(),
					   						  (Double) binShiftSpn.getValue(),
					   						  trafo);
			break;
		case 1:
			method = new CrossCorrelation((Double) binWidthSpn.getValue(),
						  				  (Double) binShiftSpn.getValue(),
										  (Integer) xCorrOffSpn.getValue(),
						  				  trafo);
			break;
		}
		
		int pickCount = 0;
		if (pickSpn.isEnabled()) { pickCount = (Integer) pickSpn.getValue(); }
		
		return new SpecSimSettings((Double) tolMzSpn.getValue(),
								   annotChk.isSelected(),
								   (Long) expIdSpn.getValue(),
								   proMethodCbx.getSelectedIndex(),
								   (Double) proMErrorSpn.getValue(),
								   pickCount,
								   method,
								   (Double) threshScSpn.getValue());
	}
	
	/**
	 * Method to repaint the panel's plot component.
	 * Draws normalized original (black) and transformed (red) versions of the supplied spectrum.
	 * @param spectrumFile The spectrum file to be plotted.
	 */
	public void refreshPlot(MascotGenericFile spectrumFile) {
		
		prePlotPnl.clearSpectrumFile();
		
		HashMap<Double, Double> basePeaks = new HashMap<Double, Double>(spectrumFile.getPeaks());
		// normalize spectrum file
		double maxInten = 0.0;
		for (double inten : basePeaks.values()) { maxInten = (inten > maxInten) ? inten : maxInten; }
		maxInten /= 100.0;
		for (Double mz : basePeaks.keySet()) { basePeaks.put(mz, basePeaks.get(mz)/maxInten); }
		
		// transform spectrum file
		SpecSimSettings specSet = gatherSpecSimSettings();
		specSet.getSpecComparator().prepare(spectrumFile.getHighestPeaks(specSet.getPickCount()));
		HashMap<Double, Double> transPeaks = (HashMap<Double, Double>) specSet.getSpecComparator().getSourcePeaks();
		// normalize transformed spectrum
		maxInten = 0.0;
		for (double inten : transPeaks.values()) { maxInten = (inten > maxInten) ? inten : maxInten; }
		maxInten /= 100.0;
		for (Double mz : transPeaks.keySet()) { transPeaks.put(mz, transPeaks.get(mz)/maxInten); }
		
		// add spectra to plot panel and paint them
		prePlotPnl.setSpectrumFile(new MascotGenericFile(null, null,  basePeaks, 			0.0				  , 			0			));
		prePlotPnl.setSpectrumFile(new MascotGenericFile(null, null, transPeaks, spectrumFile.getPrecursorMZ(), spectrumFile.getCharge()));
		prePlotPnl.repaint();
	}
	
	/**
	 * Listener class various GUI elements of the panel to trigger refreshing the preview plot.
	 */
	private class RefreshPlotListener implements ActionListener, ChangeListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			grabSpectrumAndRefreshPlot();
		}
		@Override
		public void stateChanged(ChangeEvent arg0) {
			grabSpectrumAndRefreshPlot();
		}
		private void grabSpectrumAndRefreshPlot() {
			try {
				MascotGenericFile mgf = ((SpectrumTree) clientFrame.getFilePanel().getCheckBoxTree().getTree()).getSpectrumAt(((DefaultMutableTreeNode) clientFrame.getFilePanel().getCheckBoxTree().getModel().getRoot()).getFirstLeaf());
				if (mgf != null) {
					refreshPlot(mgf);
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 * Method to toggle the enabled state of the whole panel.
	 * Honors separate enabled state of specific sub-components on restore.
	 */
	public void setEnabled(boolean enabled) {
		setChildrenEnabled(this, enabled);
		if (enabled) {	// restore old enabled state
			setChildrenEnabled(proMethodPnl, proMethodPnl.isEnabled());
			setChildrenEnabled(proMErrorPnl, proMErrorPnl.isEnabled());
			pickSpn.setEnabled(pickPnl.isEnabled());
			xCorrOffSpn.setEnabled(scorSubPnl.isEnabled());
			xCorrOffLbl.setEnabled(scorSubPnl.isEnabled());
			xCorrOffLbl2.setEnabled(scorSubPnl.isEnabled());
		}
	}

	/**
	 * Method to recursively iterate a component's children and set their enabled state.
	 * @param parent
	 * @param enabled
	 */
	public void setChildrenEnabled(JComponent parent, boolean enabled) {
		for (Component child : parent.getComponents()) {
			if (child instanceof JComponent) {
				setChildrenEnabled((JComponent) child, enabled);
			}
		}
		if (!(parent instanceof JPanel)) {	// don't mess with JPanels
			parent.setEnabled(enabled);
		}
	}
	
	/**
	 * Method to return the experiment id spinner value.
	 * @return The experiment id.
	 */
	public long getExperimentID() {
		return (Long) expIdSpn.getValue();
	}

}