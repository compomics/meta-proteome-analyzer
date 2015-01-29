package de.mpa.client.ui.panels;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Constants;
import de.mpa.client.DbSearchSettings;
import de.mpa.client.settings.CruxParameters;
import de.mpa.client.settings.InspectParameters;
import de.mpa.client.settings.MascotParameters;
import de.mpa.client.settings.OmssaParameters;
import de.mpa.client.settings.ParameterMap;
import de.mpa.client.settings.XTandemParameters;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.ComponentTitledBorder;
import de.mpa.client.ui.RolloverButtonUI;
import de.mpa.client.ui.dialogs.AdvancedSettingsDialog;
import de.mpa.client.ui.icons.IconConstants;

/**
 * Panel containing control components for database search-related settings.
 * 
 * @author T. Muth, A. Behne
 */
public class DatabaseSearchSettingsPanel extends JPanel {

	/**
	 * Combo box referencing FASTA files available for database searching.
	 */
	private JComboBox fastaFileCbx;
	
	/**
	 * Spinner for controlling precursor mass tolerance.
	 */
	private JSpinner precTolSpn;
	
	/**
	 * Spinner for controlling fragment ion tolerance.
	 */
	private JSpinner fragTolSpn;

	/**
	 * Spinner for controlling the amount of missed cleavages to account for.
	 */
	private JSpinner missClvSpn;
	
	/**
	 * Combobox for selecting the search strategy employed (Target-only or Target-Decoy).
	 */
	private JComboBox searchTypeCbx;

	/**
	 * Checkbox for the units of the precursor tolerance.
	 */
	private JComboBox precTolCbx;

	/**
	 * The package size spinner.
	 */
	private JSpinner packSpn;
	
	/**
	 * Parameter map containing advanced settings for the X!Tandem search engine.
	 */
	private ParameterMap xTandemParams = new XTandemParameters();
	
	/**
	 * Parameter map containing advanced settings for the OMSSA search engine.
	 */
	private ParameterMap omssaParams = new OmssaParameters();

	/**
	 * Parameter map containing advanced settings for the Crux search engine.
	 */
	private ParameterMap cruxParams = new CruxParameters();

	/**
	 * Parameter map containing advanced settings for the InsPecT search engine.
	 */
	private ParameterMap inspectParams = new InspectParameters();

	/**
	 * Parameter map containing advanced settings for uploading imported Mascot search engine results.
	 */
	private ParameterMap mascotParams = new MascotParameters();
	
	/**
	 * Checkbox for using X!Tandem search engine.
	 */
	private JCheckBox xTandemChk;

	/**
	 * Checkbox for using OMSSA search engine.
	 */
	private JCheckBox omssaChk;
	
	/**
	 * Checkbox for using Crux search engine.
	 */
	private JCheckBox cruxChk;
	
	/**
	 * Checkbox for using InsPect search engine.
	 */
	private JCheckBox inspectChk;
	
	/**
	 * Checkbox for using Mascot search engine.
	 */
	private JCheckBox mascotChk;

	/**
	 * The default database search panel constructor.
	 */
	public DatabaseSearchSettingsPanel() {
		initComponents();
	}

	/**
	 * Method to initialize the panel's components.
	 */
	private void initComponents() {
		
		this.setLayout(new FormLayout("7dlu, p:g, 5dlu, p, 7dlu",
									  "5dlu, p, 5dlu, f:p:g, 0dlu"));

		// Protein Database Panel
		final JPanel protDatabasePnl = new JPanel();
		protDatabasePnl.setLayout(new FormLayout("5dlu, p, 5dlu, p:g, 5dlu",
												 "0dlu, p, 5dlu"));
		protDatabasePnl.setBorder(new ComponentTitledBorder(new JLabel("Protein Database"), protDatabasePnl));

		// FASTA file ComboBox
		fastaFileCbx = new JComboBox<String>(Constants.FASTA_DB);
		
		protDatabasePnl.add(new JLabel("FASTA File:"), CC.xy(2, 2));
		protDatabasePnl.add(fastaFileCbx, CC.xy(4, 2));
		
		// General Settings Panel
		final JPanel paramsPnl = new JPanel();
		paramsPnl.setLayout(new FormLayout(
				"5dlu, p, 5dlu, p:g, 5dlu, p, 2dlu, p, 5dlu",
				"0dlu, p, 5dlu, p, 5dlu, p, 5dlu, 2px:g, 5dlu, p, 5dlu, 2px:g, 5dlu, p, 5dlu"));
		paramsPnl.setBorder(new ComponentTitledBorder(new JLabel("General Settings"), paramsPnl));

		// Precursor ion tolerance Spinner
		precTolSpn = new JSpinner(new SpinnerNumberModel(1.0, 0.0, null, 0.1));
		precTolSpn.setEditor(new JSpinner.NumberEditor(precTolSpn, "0.00"));
		precTolSpn.setToolTipText("The precursor mass tolerance.");
		precTolCbx = new JComboBox<String>(Constants.TOLERANCE_UNITS);

		// Fragment ion tolerance Spinner
		fragTolSpn = new JSpinner(new SpinnerNumberModel(0.5, 0.0, null, 0.1));
		fragTolSpn.setEditor(new JSpinner.NumberEditor(fragTolSpn, "0.00"));
		fragTolSpn.setToolTipText("The fragment mass tolerance.");
		
		// Missed cleavages Spinner
		missClvSpn = new JSpinner(new SpinnerNumberModel(2, 0, null, 1));
		missClvSpn.setToolTipText("The maximum number of missed cleavages.");
		
		// Search strategy ComboBox
		searchTypeCbx = new JComboBox<String>(new String[] { "Target-Decoy", "Target Only" });
		
		JPanel packPnl = new JPanel(new FormLayout("p, 2dlu, p:g, 2dlu, p", "p"));

		packSpn = new JSpinner(new SpinnerNumberModel(1000L, 1L, null, 100L));
		packSpn.setToolTipText("Number of spectra per transfer package"); 
		packSpn.setPreferredSize(new Dimension(packSpn.getPreferredSize().width*2,
											   packSpn.getPreferredSize().height));
		
		packPnl.add(new JLabel("Transfer"), CC.xy(1, 1));
		packPnl.add(packSpn, CC.xy(3, 1));
		packPnl.add(new JLabel("spectra per package"), CC.xy(5, 1));
		
		paramsPnl.add(new JLabel("Precursor Ion Tolerance:"), CC.xyw(2, 2, 3));
		paramsPnl.add(precTolSpn, CC.xy(6, 2));
		paramsPnl.add(precTolCbx, CC.xy(8, 2));
		paramsPnl.add(new JLabel("Fragment Ion Tolerance:"), CC.xyw(2, 4, 3));
		paramsPnl.add(fragTolSpn, CC.xy(6, 4));
		paramsPnl.add(new JLabel("Da"), CC.xy(8, 4));
		paramsPnl.add(new JLabel("Missed Cleavages (max):"), CC.xyw(2, 6, 3));
		paramsPnl.add(missClvSpn, CC.xy(6, 6));
		paramsPnl.add(new JSeparator(), CC.xyw(2, 8, 7));
		paramsPnl.add(new JLabel("Search Strategy:"), CC.xy(2, 10));
		paramsPnl.add(searchTypeCbx, CC.xyw(4, 10, 5));
		paramsPnl.add(new JSeparator(), CC.xyw(2, 12, 7));
		paramsPnl.add(packPnl, CC.xyw(2, 14, 7));
		
		// Search Engine Settings Panel
		final JPanel searchEngPnl = new JPanel();
		searchEngPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p:g, 5dlu",
				"0dlu, p:g, 5dlu, p:g, 5dlu, p:g, 5dlu, p:g, 5dlu, p:g, 5dlu, p:g, 5dlu"));
		searchEngPnl.setBorder(new ComponentTitledBorder(new JLabel("Search Engines"), searchEngPnl));

		xTandemChk = new JCheckBox("X!Tandem", true);
		xTandemChk.setIconTextGap(10);
		final JButton xTandemSetBtn = this.createSettingsButton();
		xTandemSetBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				showAdvancedSettings("X!Tandem Advanced Parameters", xTandemParams);
			}
		});
		xTandemChk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				xTandemSetBtn.setEnabled(xTandemChk.isSelected());
			}
		});
		
		omssaChk = new JCheckBox("OMSSA", true);
		omssaChk.setIconTextGap(10);
		final JButton omssaSetBtn = this.createSettingsButton();
		omssaSetBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				AdvancedSettingsDialog.showDialog(ClientFrame.getInstance(), "OMSSA Advanced Parameters", true, omssaParams);
			}
		});
		omssaChk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				omssaSetBtn.setEnabled(omssaChk.isSelected());
			}
		});
		
		cruxChk = new JCheckBox("Crux", true);
		cruxChk.setIconTextGap(10);
		final JButton cruxSetBtn = this.createSettingsButton();
		cruxSetBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				AdvancedSettingsDialog.showDialog(ClientFrame.getInstance(), "Crux Advanced Parameters", true, cruxParams);
			}
		});
		cruxChk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				cruxSetBtn.setEnabled(cruxChk.isSelected());
			}
		});
		
		inspectChk = new JCheckBox("InsPecT", true);
		inspectChk.setIconTextGap(10);
		final JButton inspectSetBtn = this.createSettingsButton();
		inspectSetBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				AdvancedSettingsDialog.showDialog(ClientFrame.getInstance(), "InsPecT Advanced Parameters", true, inspectParams);
			}
		});
		inspectChk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				inspectSetBtn.setEnabled(inspectChk.isSelected());
			}
		});
		
		mascotChk = new JCheckBox("Mascot", false);
		mascotChk.setIconTextGap(10);
		final JButton mascotSetBtn = this.createSettingsButton();
		mascotSetBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				AdvancedSettingsDialog.showDialog(
						ClientFrame.getInstance(), "Mascot Advanced Parameters", true, mascotParams);
			}
		});
		mascotChk.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				mascotSetBtn.setEnabled(mascotChk.isSelected());
			}
		});
		
		// Mascot functionality is initially disabled unless a .dat file is imported
		mascotChk.setEnabled(false);
		mascotSetBtn.setEnabled(false);
				
		searchEngPnl.add(xTandemChk, CC.xy(2, 2));
		searchEngPnl.add(xTandemSetBtn, CC.xy(4, 2));
		searchEngPnl.add(omssaChk, CC.xy(2, 4));
		searchEngPnl.add(omssaSetBtn, CC.xy(4, 4));
		searchEngPnl.add(cruxChk, CC.xy(2, 6));
		searchEngPnl.add(cruxSetBtn, CC.xy(4, 6));
		searchEngPnl.add(inspectChk, CC.xy(2, 8));
		searchEngPnl.add(inspectSetBtn, CC.xy(4, 8));
		searchEngPnl.add(mascotChk, CC.xy(2, 10));
		searchEngPnl.add(mascotSetBtn, CC.xy(4, 10));

		// add everything to main panel
		this.add(protDatabasePnl, CC.xy(2, 2));
		this.add(paramsPnl, CC.xy(2, 4));
		this.add(searchEngPnl, CC.xywh(4, 2, 1, 3));

	}
	
	/**
	 * Convenience method to return a settings button.
	 * @return a settings button
	 */
	private JButton createSettingsButton() {
		JButton button = new JButton(IconConstants.SETTINGS_SMALL_ICON);
		button.setRolloverIcon(IconConstants.SETTINGS_SMALL_ROLLOVER_ICON);
		button.setPressedIcon(IconConstants.SETTINGS_SMALL_PRESSED_ICON);
		button.setUI((RolloverButtonUI) RolloverButtonUI.createUI(button));
		return button;
	}
	
	/**
	 * Utility method to collect and consolidate all relevant database search settings.
	 * @return the database search settings object instance.
	 */
	public DbSearchSettings gatherDBSearchSettings() {
		DbSearchSettings dbSettings = new DbSearchSettings();
		dbSettings.setFastaFile(fastaFileCbx.getSelectedItem().toString());
		dbSettings.setFragmentIonTol((Double) fragTolSpn.getValue());
		dbSettings.setPrecursorIonTol((Double) precTolSpn.getValue());
		dbSettings.setPrecursorIonUnitPpm(precTolCbx.getSelectedIndex()==1);
		dbSettings.setNumMissedCleavages((Integer) missClvSpn.getValue());
		
		if (xTandemChk.isSelected()) {
			dbSettings.setXTandem(true);
			dbSettings.setXtandemParams(xTandemParams.toString());
		}
		
		if (omssaChk.isSelected()) {
			dbSettings.setOmssa(true);
			dbSettings.setOmssaParams(omssaParams.toString());
		}
		
		if (cruxChk.isSelected()) {
			dbSettings.setCrux(true);
			dbSettings.setCruxParams(cruxParams.toString());
		}
		
		if (inspectChk.isSelected()) {
			dbSettings.setInspect(true);
			dbSettings.setInspectParams(inspectParams.toString());
		}
		
		dbSettings.setMascot(mascotChk.isSelected());
		
		dbSettings.setDecoy(searchTypeCbx.getSelectedIndex() == 0);
		
		// Set the current experiment id for the database search settings.
		dbSettings.setExperimentid(ClientFrame.getInstance().getProjectPanel().getSelectedExperiment().getID());
		return dbSettings;
	}
	
	/**
	 * Utility method to display an advanced settings dialog.
	 * @param title
	 * @param params
	 */
	protected void showAdvancedSettings(String title, ParameterMap params) {
		gatherDBSearchSettings();
		AdvancedSettingsDialog.showDialog(ClientFrame.getInstance(), title, true, params);
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
		if (!(parent instanceof DatabaseSearchSettingsPanel)) { // don't mess with DBSearchPanels
			parent.setEnabled(enabled);
			Border border = parent.getBorder();
			if (border instanceof ComponentTitledBorder) {
				((ComponentTitledBorder) border).setEnabled(enabled);
			}
		}
	}

	/**
	 * Returns the precursor tolerance spinner.
	 * @return the precursor tolerance spinner
	 */
	public JSpinner getPrecursorToleranceSpinner() {
		return precTolSpn;
	}

	/**
	 * Returns the fragment tolerance spinner.
	 * @return the fragment tolerance spinner
	 */
	public JSpinner getFragmentToleranceSpinner() {
		return fragTolSpn;
	}

	/**
	 * Returns the missed cleavages spinner.
	 * @return the missed cleavages spinner
	 */
	public JSpinner getMissedCleavageSpinner() {
		return missClvSpn;
	}

	/**
	 * Returns the X!Tandem paramter map.
	 * @return ParameterMap for X!Tandem.
	 */
	public ParameterMap getXTandemParameterMap() {
		return xTandemParams;
	}
	
	/**
	 * Returns the OMSSA parameter map.
	 * @return ParameterMap for OMSSA.
	 */
	public ParameterMap getOmssaParameterMap() {
		return omssaParams;
	}
	
	/**
	 * Returns the Crux parameter map.
	 * @return ParameterMap for Crux.
	 */
	public ParameterMap getCruxParameterMap() {
		return cruxParams;
	}
	
	/**
	 * Returns the Inspect parameter map.
	 * @return ParameterMap for Inspect
	 */
	public ParameterMap getInspectParameterMap() {
		return inspectParams;
	}
	
	/**
	 * Returns the MASCOT parameter map.
	 * @return ParameterMap for MASCOT.
	 */
	public ParameterMap getMascotParameterMap() {
		return mascotParams;
	}
	
	/**
	 * Sets the enable state of the Mascot search engine selector.
	 * @param enabled
	 */
	public void setMascotEnabled(boolean enabled) {
		mascotChk.setEnabled(enabled);
		mascotChk.setSelected(enabled);
	}
	
	/**
	 * Returns the package size.
	 * @return the package size
	 */
	public long getPackageSize() {
		return ((Number) packSpn.getValue()).longValue();
	}


}
