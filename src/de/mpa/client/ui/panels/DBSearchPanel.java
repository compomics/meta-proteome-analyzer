package de.mpa.client.ui.panels;

import java.awt.Component;
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

import com.jgoodies.forms.layout.CellConstraints;
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
import de.mpa.client.ui.dialogs.AdvancedSettingsDialog;

/**
 * Panel containing control components for database search-related settings.
 * 
 * @author T. Muth, A. Behne
 */
public class DBSearchPanel extends JPanel {

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
	 * Checkbox for controlling whether the X!Tandem search engine shall be used.
	 */
	private JCheckBox xTandemChk;
	
	/**
	 * Button to show advanced settings for the X!Tandem search engine.
	 */
	private JButton xTandemSetBtn;
	
	/**
	 * Parameter map containing advanced settings for the X!Tandem search engine.
	 */
	private ParameterMap xTandemParams = new XTandemParameters();

	/**
	 * Checkbox for controlling whether the OMSSA search engine shall be used.
	 */
	private JCheckBox omssaChk;
	
	/**
	 * Button to show advanced settings for the OMSSA search engine.
	 */
	private JButton omssaSetBtn;
	
	/**
	 * Parameter map containing advanced settings for the OMSSA search engine.
	 */
	private ParameterMap omssaParams = new OmssaParameters();

	/**
	 * Checkbox for controlling whether the Crux search engine shall be used.
	 */
	private JCheckBox cruxChk;

	/**
	 * Button to show advanced settings for the Crux search engine.
	 */
	private JButton cruxSetBtn;
	
	/**
	 * Parameter map containing advanced settings for the Crux search engine.
	 */
	private ParameterMap cruxParams = new CruxParameters();

	/**
	 * Checkbox for controlling whether the InsPecT search engine shall be used.
	 */
	private JCheckBox inspectChk;

	/**
	 * Button to show advanced settings for the InsPecT search engine.
	 */
	private JButton inspectSetBtn;
	
	/**
	 * Parameter map containing advanced settings for the InsPecT search engine.
	 */
	private ParameterMap inspectParams = new InspectParameters();

	/**
	 * Checkbox for controlling whether imported Mascot search engine results shall be uploaded.
	 */
	private JCheckBox mascotChk;
	
	/**
	 * Button to show advanced settings for imported Mascot search engine results.
	 */
	private JButton mascotSetBtn;
	
	/**
	 * Parameter map containing advanced settings for uploading imported Mascot search engine results.
	 */
	private ParameterMap mascotParams = new MascotParameters();

	
	/**
	 * Checkbox for the units of the precursor tolerance
	 */
	private JComboBox precTolCbx;

	/**
	 * The default database search panel constructor.
	 */
	public DBSearchPanel() {
		initComponents();
	}

	/**
	 * Method to initialize the panel's components.
	 */
	private void initComponents() {
		
		CellConstraints cc = new CellConstraints();

		this.setLayout(new FormLayout("7dlu, p:g, 7dlu",
									  "5dlu, p, 5dlu, f:p:g, 5dlu, f:p, 7dlu"));

		// Protein Database Panel
		final JPanel protDatabasePnl = new JPanel();
		protDatabasePnl.setLayout(new FormLayout("5dlu, p, 5dlu, p:g, 5dlu",
												 "0dlu, p, 5dlu"));
		protDatabasePnl.setBorder(new ComponentTitledBorder(new JLabel("Protein Database"), protDatabasePnl));

		// FASTA file ComboBox
		fastaFileCbx = new JComboBox(Constants.FASTA_DB);
		
		protDatabasePnl.add(new JLabel("FASTA File:"), cc.xy(2, 2));
		protDatabasePnl.add(fastaFileCbx, cc.xy(4, 2));
		
		// General Settings Panel
		final JPanel paramsPnl = new JPanel();
		paramsPnl.setLayout(new FormLayout(
				"5dlu, p, 5dlu, p:g, 5dlu, p, 2dlu, p, 5dlu",
				"0dlu, p, 5dlu, p, 5dlu, p, 5dlu, 2px:g, 5dlu, p, 5dlu"));
		paramsPnl.setBorder(new ComponentTitledBorder(new JLabel("General Settings"), paramsPnl));

		// Precursor ion tolerance Spinner
		precTolSpn = new JSpinner(new SpinnerNumberModel(1.0, 0.0, null, 0.1));
		precTolSpn.setEditor(new JSpinner.NumberEditor(precTolSpn, "0.00"));
		precTolSpn.setToolTipText("The precursor mass tolerance.");
		precTolCbx = new JComboBox(Constants.TOLERANCE_UNITS);

		// Fragment ion tolerance Spinner
		fragTolSpn = new JSpinner(new SpinnerNumberModel(0.5, 0.0, null, 0.1));
		fragTolSpn.setEditor(new JSpinner.NumberEditor(fragTolSpn, "0.00"));
		fragTolSpn.setToolTipText("The fragment mass tolerance.");
		
		// Missed cleavages Spinner
		missClvSpn = new JSpinner(new SpinnerNumberModel(2, 0, null, 1));
		missClvSpn.setToolTipText("The maximum number of missed cleavages.");
		
		// Search strategy ComboBox
		searchTypeCbx = new JComboBox(new String[] { "Target-Decoy", "Target Only" });
		
		paramsPnl.add(new JLabel("Precursor Ion Tolerance:"), cc.xyw(2, 2, 3));
		paramsPnl.add(precTolSpn, cc.xy(6, 2));
		paramsPnl.add(precTolCbx, cc.xy(8, 2));
		paramsPnl.add(new JLabel("Fragment Ion Tolerance:"), cc.xyw(2, 4, 3));
		paramsPnl.add(fragTolSpn, cc.xy(6, 4));
		paramsPnl.add(new JLabel("Da"), cc.xy(8, 4));
		paramsPnl.add(new JLabel("Missed Cleavages (max):"), cc.xyw(2, 6, 3));
		paramsPnl.add(missClvSpn, cc.xy(6, 6));
		paramsPnl.add(new JSeparator(), cc.xyw(2, 8, 7));
		paramsPnl.add(new JLabel("Search Strategy:"), cc.xy(2, 10));
		paramsPnl.add(searchTypeCbx, cc.xyw(4, 10, 5));
		
		// Search Engine Settings Panel
		final JPanel searchEngPnl = new JPanel();
		searchEngPnl.setLayout(new FormLayout("5dlu, p, 10dlu, p:g, 5dlu",
				"0dlu, p:g, 5dlu, p:g, 5dlu, p:g, 5dlu, p:g, 5dlu, p:g, 5dlu"));
		searchEngPnl.setBorder(new ComponentTitledBorder(new JLabel("Search Engine Settings"), searchEngPnl));

		// X!Tandem
		xTandemChk = new JCheckBox("X!Tandem", true);
		xTandemChk.setIconTextGap(10);
		xTandemSetBtn = new JButton("Advanced Settings");
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
		
		// OMSSA
		omssaChk = new JCheckBox("OMSSA", true);
		omssaChk.setIconTextGap(10);
		omssaSetBtn = new JButton("Advanced Settings");
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
		
		// Crux
		cruxChk = new JCheckBox("Crux", true);
		cruxChk.setIconTextGap(10);
		cruxSetBtn = new JButton("Advanced Settings");
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
		
		// InsPecT
		inspectChk = new JCheckBox("InsPecT", true);
		inspectChk.setIconTextGap(10);
		inspectSetBtn = new JButton("Advanced Settings");
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
		
		// Mascot
		mascotChk = new JCheckBox("Mascot", false);
		mascotChk.setEnabled(false);
		mascotChk.setIconTextGap(10);
		mascotSetBtn = new JButton("Advanced Settings");
		mascotSetBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				AdvancedSettingsDialog.showDialog(ClientFrame.getInstance(), "Mascot Advanced Parameters", true, mascotParams);
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

		searchEngPnl.add(xTandemChk, cc.xy(2, 2));
		searchEngPnl.add(xTandemSetBtn, cc.xy(4, 2));
		searchEngPnl.add(omssaChk, cc.xy(2, 4));
		searchEngPnl.add(omssaSetBtn, cc.xy(4, 4));
		searchEngPnl.add(cruxChk, cc.xy(2, 6));
		searchEngPnl.add(cruxSetBtn, cc.xy(4, 6));
		searchEngPnl.add(inspectChk, cc.xy(2, 8));
		searchEngPnl.add(inspectSetBtn, cc.xy(4, 8));
		searchEngPnl.add(mascotChk, cc.xy(2, 10));
		searchEngPnl.add(mascotSetBtn, cc.xy(4, 10));

		// add everything to main panel
		this.add(protDatabasePnl, cc.xy(2, 2));
		this.add(paramsPnl, cc.xy(2, 4));
		this.add(searchEngPnl, cc.xy(2, 6));

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
		dbSettings.setExperimentid(ClientFrame.getInstance().getProjectPanel().getCurrentExperimentId());
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
	 * Method to toggle the enabled state of the whole panel.
	 * Honors separate enabled state of specific sub-components on restore.
	 */
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		setChildrenEnabled(this, enabled);
		if (enabled) {
			xTandemSetBtn.setEnabled(xTandemChk.isSelected());
			omssaSetBtn.setEnabled(omssaChk.isSelected());
			cruxSetBtn.setEnabled(cruxChk.isSelected());
			inspectSetBtn.setEnabled(inspectChk.isSelected());
			mascotSetBtn.setEnabled(mascotChk.isSelected());
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
		if (!(parent instanceof DBSearchPanel)) { // don't mess with DBSearchPanels
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
	 * Gets the status of the mascot check box.
	 * @return the mascot checkbox.
	 */
	public JCheckBox getMascotChk() {
		return mascotChk;
	}

}
