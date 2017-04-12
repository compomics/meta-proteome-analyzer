package de.mpa.client.ui.panels;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Constants;
import de.mpa.client.DbSearchSettings;
import de.mpa.client.settings.MascotParameters;
import de.mpa.client.settings.OmssaParameters;
import de.mpa.client.settings.ParameterMap;
import de.mpa.client.settings.XTandemParameters;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.ComponentTitledBorder;
import de.mpa.client.ui.RolloverButtonUI;
import de.mpa.client.ui.dialogs.AdvancedSettingsDialog;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.main.Starter;
import de.mpa.util.PropertyLoader;

/**
 * Panel containing control components for database search-related settings.
 * 
 * @author T. Muth, A. Behne
 */
@SuppressWarnings("serial")
public class DatabaseSearchSettingsPanel extends JPanel {

	/**
	 * Combo box referencing FASTA files available for database searching.
	 */
	@SuppressWarnings("rawtypes")
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
	@SuppressWarnings("rawtypes")
	private JComboBox searchTypeCbx;

	/**
	 * Checkbox for the units of the precursor tolerance.
	 */
	@SuppressWarnings("rawtypes")
	private JComboBox precTolCbx;

	/**
	 * The package size spinner.
	 */
	private JSpinner packSpn;
	
	/**
	 * The additional protein hits check box
	 */
	private JCheckBox addProtChk;
	
	/**
	 * Parameter map containing advanced settings for the X!Tandem search engine.
	 */
	private final ParameterMap xTandemParams = new XTandemParameters();
	
	/**
	 * Parameter map containing advanced settings for the OMSSA search engine.
	 */
	private final ParameterMap omssaParams = new OmssaParameters();

//	/**
//	 * Parameter map containing advanced settings for the Crux search engine.
//	 */
//	private ParameterMap cruxParams = new CruxParameters();
//
//	/**
//	 * Parameter map containing advanced settings for the InsPecT search engine.
//	 */
//	private ParameterMap inspectParams = new InspectParameters();

	/**
	 * Parameter map containing advanced settings for uploading imported Mascot search engine results.
	 */
	private final ParameterMap mascotParams = new MascotParameters();
	
	/**
	 * Checkbox for using X!Tandem search engine.
	 */
	private JCheckBox xTandemChk;

	/**
	 * Checkbox for using OMSSA search engine.
	 */
	private JCheckBox omssaChk;
	
//	/**
//	 * Checkbox for using Crux search engine.
//	 */
//	private JCheckBox cruxChk;
//	
//	/**
//	 * Checkbox for using InsPect search engine.
//	 */
//	private JCheckBox inspectChk;
	
	/**
	 * Checkbox for using Mascot search engine.
	 */
	private JCheckBox mascotChk;

	/**
	 * The default database search panel constructor.
	 * @throws IOException 
	 */
	public DatabaseSearchSettingsPanel() {
        initComponents();
	}

	/**
	 * Method to initialize the panel's components.
	 * @throws IOException 
	 */
	private void initComponents() {

        setLayout(new FormLayout("7dlu, p:g, 5dlu, p, 7dlu",
									  "5dlu, p, 5dlu, f:p:g, 0dlu"));

		// Protein Database Panel
		JPanel protDatabasePnl = new JPanel();
		protDatabasePnl.setLayout(new FormLayout("5dlu, p, 5dlu, p:g, 5dlu",
												 "0dlu, p, 5dlu"));
		protDatabasePnl.setBorder(new ComponentTitledBorder(new JLabel("Protein Database"), protDatabasePnl));

		// Load the resources settings via input stream.
		Properties prop = new Properties();
		InputStream input = null;
		try {
			if (Starter.isJarExport()) {
				input = new FileInputStream(PropertyLoader.getProperty("base_path") + PropertyLoader.getProperty("path.fasta") + File.separator + PropertyLoader.getProperty("file.fastalist"));
//				input = new FileInputStream(Constants.CONFIGURATION_PATH_JAR + File.separator + "client-settings.txt");
			} else {
				System.out.println("Turn on Jar-Export / Deprecated 'feature'");
//				input = this.getClass().getResourceAsStream(Constants.CONFIGURATION_PATH + "client-settings.txt");
			}
			prop.load(input);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String[] items = prop.get("files.fasta").toString().split(",");
//		String[] items = PropertyLoader.getProperty(PropertyLoader.FILES_FASTA).split(",");
		
		// FASTA file ComboBox
        this.fastaFileCbx = new JComboBox<String>(items);
		
		
		protDatabasePnl.add(new JLabel("FASTA File:"), CC.xy(2, 2));
		protDatabasePnl.add(this.fastaFileCbx, CC.xy(4, 2));
		
		// General Settings Panel 
		JPanel paramsPnl = new JPanel();
		paramsPnl.setLayout(new FormLayout(
				"5dlu, p, 5dlu, p:g, 5dlu, p, 2dlu, p, 5dlu",
				"0dlu, p, 5dlu, p, 5dlu, p, 5dlu, 2px, 5dlu, p, 5dlu, 2px, 5dlu, p, 5dlu"));
		paramsPnl.setBorder(new ComponentTitledBorder(new JLabel("General Settings"), paramsPnl));

		// Precursor ion tolerance Spinner
        this.precTolSpn = new JSpinner(new SpinnerNumberModel(10.0, 0.0, null, 0.1));
        this.precTolSpn.setEditor(new NumberEditor(this.precTolSpn, "0.00"));
        this.precTolSpn.setToolTipText("The precursor mass tolerance.");
        this.precTolCbx = new JComboBox<String>(Constants.TOLERANCE_UNITS);
        this.precTolCbx.setSelectedItem(Constants.TOLERANCE_UNITS[1]); // select ppm

		// Fragment ion tolerance Spinner
        this.fragTolSpn = new JSpinner(new SpinnerNumberModel(0.5, 0.0, null, 0.1));
        this.fragTolSpn.setEditor(new NumberEditor(this.fragTolSpn, "0.00"));
        this.fragTolSpn.setToolTipText("The fragment mass tolerance.");
		
		// Missed cleavages Spinner
        this.missClvSpn = new JSpinner(new SpinnerNumberModel(1, 0, null, 1));
        this.missClvSpn.setToolTipText("The maximum number of missed cleavages.");
		
		// Search strategy ComboBox
        this.searchTypeCbx = new JComboBox<String>(new String[] { "Target-Decoy", "Target Only" });
		
		JPanel packPnl = new JPanel(new FormLayout("p, 2dlu, p:g, 2dlu, p", "p, 2dlu, p"));

        this.packSpn = new JSpinner(new SpinnerNumberModel(1000L, 1L, null, 100L));
        this.packSpn.setToolTipText("Number of spectra per transfer package");
        this.packSpn.setPreferredSize(new Dimension(this.packSpn.getPreferredSize().width*2,
                this.packSpn.getPreferredSize().height));
		
		packPnl.add(new JLabel("Transfer"), CC.xy(1, 1));
		packPnl.add(this.packSpn, CC.xy(3, 1));
		packPnl.add(new JLabel("spectra per package"), CC.xy(5, 1));

        this.addProtChk = new JCheckBox("Search peptide FASTA for additonal hits.", true);
		packPnl.add(this.addProtChk, CC.xyw(1, 3, 5));
		
		paramsPnl.add(new JLabel("Precursor Ion Tolerance:"), CC.xyw(2, 2, 3));
		paramsPnl.add(this.precTolSpn, CC.xy(6, 2));
		paramsPnl.add(this.precTolCbx, CC.xy(8, 2));
		paramsPnl.add(new JLabel("Fragment Ion Tolerance:"), CC.xyw(2, 4, 3));
		paramsPnl.add(this.fragTolSpn, CC.xy(6, 4));
		paramsPnl.add(new JLabel("Da"), CC.xy(8, 4));
		paramsPnl.add(new JLabel("Missed Cleavages (max):"), CC.xyw(2, 6, 3));
		paramsPnl.add(this.missClvSpn, CC.xy(6, 6));
		paramsPnl.add(new JSeparator(), CC.xyw(2, 8, 7));
		paramsPnl.add(new JLabel("Search Strategy:"), CC.xy(2, 10));
		paramsPnl.add(this.searchTypeCbx, CC.xyw(4, 10, 5));
		paramsPnl.add(new JSeparator(), CC.xyw(2, 12, 7));
		paramsPnl.add(packPnl, CC.xyw(2, 14, 7));
		
		// Search Engine Settings Panel
		JPanel searchEngPnl = new JPanel();
		searchEngPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p:g, 5dlu",
				"0dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));
		searchEngPnl.setBorder(new ComponentTitledBorder(new JLabel("Search Engines"), searchEngPnl));

        this.xTandemChk = new JCheckBox("X!Tandem", true);
        this.xTandemChk.setIconTextGap(10);
		JButton xTandemSetBtn = createSettingsButton();
		xTandemSetBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
                DatabaseSearchSettingsPanel.this.showAdvancedSettings("X!Tandem Advanced Parameters", DatabaseSearchSettingsPanel.this.xTandemParams);
			}
		});
        this.xTandemChk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				xTandemSetBtn.setEnabled(DatabaseSearchSettingsPanel.this.xTandemChk.isSelected());
			}
		});

        this.omssaChk = new JCheckBox("OMSSA", true);
        this.omssaChk.setIconTextGap(10);
		JButton omssaSetBtn = createSettingsButton();
		omssaSetBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				AdvancedSettingsDialog.showDialog(ClientFrame.getInstance(), "OMSSA Advanced Parameters", true, DatabaseSearchSettingsPanel.this.omssaParams);
			}
		});
        this.omssaChk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				omssaSetBtn.setEnabled(DatabaseSearchSettingsPanel.this.omssaChk.isSelected());
			}
		});
		
//		cruxChk = new JCheckBox("Crux", false);
//		cruxChk.setIconTextGap(10);
//		final JButton cruxSetBtn = this.createSettingsButton();
//		cruxSetBtn.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent evt) {
//				AdvancedSettingsDialog.showDialog(ClientFrame.getInstance(), "Crux Advanced Parameters", true, cruxParams);
//			}
//		});
//		cruxChk.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent evt) {
//				cruxSetBtn.setEnabled(cruxChk.isSelected());
//			}
//		});
//		
//		inspectChk = new JCheckBox("InsPecT", false);
//		inspectChk.setIconTextGap(10);
//		final JButton inspectSetBtn = this.createSettingsButton();
//		inspectSetBtn.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent evt) {
//				AdvancedSettingsDialog.showDialog(ClientFrame.getInstance(), "InsPecT Advanced Parameters", true, inspectParams);
//			}
//		});
//		inspectChk.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent evt) {
//				inspectSetBtn.setEnabled(inspectChk.isSelected());
//			}
//		});

        this.mascotChk = new JCheckBox("Mascot", true);
        this.mascotChk.setIconTextGap(10);
		JButton mascotSetBtn = createSettingsButton();
		mascotSetBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				AdvancedSettingsDialog.showDialog(
						ClientFrame.getInstance(), "Mascot Advanced Parameters", true, DatabaseSearchSettingsPanel.this.mascotParams);
			}
		});
        this.mascotChk.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				mascotSetBtn.setEnabled(DatabaseSearchSettingsPanel.this.mascotChk.isSelected());
			}
		});
		
		// Spectral Library
		JCheckBox specLibChk = new JCheckBox("SpecLib", false);
		specLibChk.setIconTextGap(10);
		JButton specLibSetBtn = createSettingsButton();
		specLibSetBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				AdvancedSettingsDialog.showDialog(
						ClientFrame.getInstance(), "Spectral Library Advanced Parameters", true, null);
			}
		});

		searchEngPnl.add(this.xTandemChk, CC.xy(2, 2));
		searchEngPnl.add(xTandemSetBtn, CC.xy(4, 2));
		searchEngPnl.add(this.omssaChk, CC.xy(2, 4));
		searchEngPnl.add(omssaSetBtn, CC.xy(4, 4));
//		searchEngPnl.add(cruxChk, CC.xy(2, 6));
//		searchEngPnl.add(cruxSetBtn, CC.xy(4, 6));
//		searchEngPnl.add(inspectChk, CC.xy(2, 8));
//		searchEngPnl.add(inspectSetBtn, CC.xy(4, 8));
		searchEngPnl.add(this.mascotChk, CC.xy(2, 6));
		searchEngPnl.add(mascotSetBtn, CC.xy(4, 6));
//		searchEngPnl.add(specLibChk, CC.xy(2, 12));
//		searchEngPnl.add(specLibSetBtn, CC.xy(4, 12));

		// add everything to main panel
        add(protDatabasePnl, CC.xy(2, 2));
        add(paramsPnl, CC.xy(2, 4));
        add(searchEngPnl, CC.xywh(4, 2, 1, 3));

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
		button.setPreferredSize(new Dimension(22, 22));
		return button;
	}
	
	/**
	 * Utility method to collect and consolidate all relevant database search settings.
	 * @return the database search settings object instance.
	 */
	public DbSearchSettings gatherDBSearchSettings() {
		DbSearchSettings dbSettings = new DbSearchSettings();
		dbSettings.setFastaFile(this.fastaFileCbx.getSelectedItem().toString());
		dbSettings.setFragmentIonTol((Double) this.fragTolSpn.getValue());
		dbSettings.setPrecursorIonTol((Double) this.precTolSpn.getValue());
		dbSettings.setPrecursorIonUnitPpm(this.precTolCbx.getSelectedIndex()==1);
		dbSettings.setNumMissedCleavages((Integer) this.missClvSpn.getValue());
		dbSettings.setpepFASTA(this.addProtChk.isSelected());
		
		if (this.xTandemChk.isSelected()) {
			dbSettings.setXTandem(true);
			dbSettings.setXtandemParams(this.xTandemParams.toString());
		}
		
		if (this.omssaChk.isSelected()) {
			dbSettings.setOmssa(true);
			dbSettings.setOmssaParams(this.omssaParams.toString());
		}
		
//		if (cruxChk.isSelected()) {
//			dbSettings.setCrux(true);
//			dbSettings.setCruxParams(cruxParams.toString());
//		}
//		
//		if (inspectChk.isSelected()) {
//			dbSettings.setInspect(true);
//			dbSettings.setInspectParams(inspectParams.toString());
//		}
		
		dbSettings.setMascot(this.mascotChk.isSelected());
		
		dbSettings.setDecoy(this.searchTypeCbx.getSelectedIndex() == 0);
		
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
        this.gatherDBSearchSettings();
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
                this.setChildrenEnabled((JComponent) child, enabled);
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
		return this.precTolSpn;
	}

	/**
	 * Returns the fragment tolerance spinner.
	 * @return the fragment tolerance spinner
	 */
	public JSpinner getFragmentToleranceSpinner() {
		return this.fragTolSpn;
	}

	/**
	 * Returns the missed cleavages spinner.
	 * @return the missed cleavages spinner
	 */
	public JSpinner getMissedCleavageSpinner() {
		return this.missClvSpn;
	}

	/**
	 * Returns the X!Tandem paramter map.
	 * @return ParameterMap for X!Tandem.
	 */
	public ParameterMap getXTandemParameterMap() {
		return this.xTandemParams;
	}
	
	/**
	 * Returns the OMSSA parameter map.
	 * @return ParameterMap for OMSSA.
	 */
	public ParameterMap getOmssaParameterMap() {
		return this.omssaParams;
	}
	
//	/**
//	 * Returns the Crux parameter map.
//	 * @return ParameterMap for Crux.
//	 */
//	public ParameterMap getCruxParameterMap() {
//		return cruxParams;
//	}
//	
//	/**
//	 * Returns the Inspect parameter map.
//	 * @return ParameterMap for Inspect
//	 */
//	public ParameterMap getInspectParameterMap() {
//		return inspectParams;
//	}
	
	/**
	 * Returns the MASCOT parameter map.
	 * @return ParameterMap for MASCOT.
	 */
	public ParameterMap getMascotParameterMap() {
		return this.mascotParams;
	}
	
	/**
	 * Sets the enable state of the Mascot search engine selector.
	 * @param enabled
	 */
	public void setMascotEnabled(boolean enabled) {
        this.mascotChk.setEnabled(enabled);
        this.mascotChk.setSelected(enabled);
	}
	
	/**
	 * Returns the package size.
	 * @return the package size
	 */
	public long getPackageSize() {
		return ((Number) this.packSpn.getValue()).longValue();
	}
	
	/**
	 * Returns peptide FASTA flag.
	 * @return the peptide FASTA flag
	 */
	public boolean getpepHitsUse() {
		return this.addProtChk.isSelected();
	}
}
