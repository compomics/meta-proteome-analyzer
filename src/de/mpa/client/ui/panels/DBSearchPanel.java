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

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.DbSearchSettings;
import de.mpa.client.settings.CruxParameters;
import de.mpa.client.settings.InspectParameters;
import de.mpa.client.settings.OmssaParameters;
import de.mpa.client.settings.XTandemParameters;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.ComponentTitledBorder;
import de.mpa.client.ui.Constants;
import de.mpa.client.ui.dialogs.AdvancedSettingsDialog;

public class DBSearchPanel extends JPanel {

	private JComboBox fastaFileCbx;
	private JSpinner fragTolSpn;
	private JSpinner precTolSpn;
	private JSpinner missClvSpn;
	private JCheckBox xTandemChk;
	private JCheckBox omssaChk;
	private JCheckBox cruxChk;
	private JCheckBox inspectChk;
	private JButton xTandemSetBtn;
	private JButton omssaSetBtn;
	private JButton cruxSetBtn;
	private JButton inspectSetBtn;
	private JComboBox searchTypeCbx;
	private ClientFrame clientFrame;
	
	/**
	 * The default database search panel constructor.
	 */
	public DBSearchPanel(ClientFrame clientFrame) {
		this.clientFrame = clientFrame;
		initComponents();
	}

	/**
	 * Method to initialize the panel's components
	 */
	private void initComponents() {
		
		CellConstraints cc = new CellConstraints();

		this.setLayout(new FormLayout("7dlu, p:g, 7dlu",
									  "5dlu, p, 5dlu, f:p:g, 5dlu, f:p:g, 7dlu"));

		// Protein Database Panel
		final JPanel protDatabasePnl = new JPanel();
		protDatabasePnl.setLayout(new FormLayout("5dlu, p, 15dlu, p:g, 5dlu",
												 "0dlu, p, 5dlu"));
		protDatabasePnl.setBorder(new ComponentTitledBorder(new JLabel("Protein Database"), protDatabasePnl));

		// FASTA file ComboBox
		fastaFileCbx = new JComboBox(Constants.FASTA_DB);
		
		protDatabasePnl.add(new JLabel("FASTA File:"), cc.xy(2, 2));
		protDatabasePnl.add(fastaFileCbx, cc.xy(4, 2));
		
		// Parameters Panel
		final JPanel paramsPnl = new JPanel();
		paramsPnl.setLayout(new FormLayout(
				"5dlu, p, 5dlu, p:g, 5dlu, p, 2dlu, p, 5dlu",
				"0dlu, p, 5dlu, p, 5dlu, p, 5dlu, p:g, 5dlu, p, 5dlu"));
		paramsPnl.setBorder(new ComponentTitledBorder(new JLabel("Parameters"), paramsPnl));

		// Precursor ion tolerance Spinner
		precTolSpn = new JSpinner(new SpinnerNumberModel(1.0, 0.0, 10.0, 0.1));
		precTolSpn.setEditor(new JSpinner.NumberEditor(precTolSpn, "0.0"));
		precTolSpn.setToolTipText("The precursor mass tolerance.");

		// Fragment ion tolerance Spinner
		fragTolSpn = new JSpinner(new SpinnerNumberModel(0.5, 0.0, 10.0, 0.1));
		fragTolSpn.setEditor(new JSpinner.NumberEditor(fragTolSpn, "0.0"));
		fragTolSpn.setToolTipText("The fragment mass tolerance.");

		// Missed cleavages Spinner
		missClvSpn = new JSpinner(new SpinnerNumberModel(2, 0, 10, 1));
		missClvSpn.setToolTipText("The maximum number of missed cleavages.");

		// Enzyme ComboBox
		JComboBox enzymeCbx = new JComboBox(Constants.DB_ENZYMES);
		paramsPnl.add(new JLabel("Precursor Ion Tolerance:"), cc.xyw(2, 2, 3));
		paramsPnl.add(precTolSpn, cc.xy(6, 2));
		paramsPnl.add(new JLabel("Da"), cc.xy(8, 2));
		paramsPnl.add(new JLabel("Fragment Ion Tolerance:"), cc.xyw(2, 4, 3));
		paramsPnl.add(fragTolSpn, cc.xy(6, 4));
		paramsPnl.add(new JLabel("Da"), cc.xy(8, 4));
		paramsPnl.add(new JLabel("Missed Cleavages (max):"), cc.xyw(2, 6, 3));
		paramsPnl.add(missClvSpn, cc.xy(6, 6));
		paramsPnl.add(new JSeparator(), cc.xyw(2, 8, 7));
		paramsPnl.add(new JLabel("Enzyme (Protease):"), cc.xy(2, 10));
		paramsPnl.add(enzymeCbx, cc.xyw(4, 10, 5));
		
		// Search Engine Panel
		final JPanel searchEngPnl = new JPanel();
		searchEngPnl.setLayout(new FormLayout("5dlu, f:p:g, 5dlu, p:g, 5dlu", "0dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p:g, 5dlu"));
		searchEngPnl.setBorder(new ComponentTitledBorder(new JLabel("Search Engines"), searchEngPnl));

		// X!Tandem
		xTandemChk = new JCheckBox("X!Tandem", true);
		xTandemChk.setIconTextGap(10);
		
		// Load default X!Tandem settings
		xTandemSetBtn = new JButton("Advanced Settings");
		xTandemSetBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new AdvancedSettingsDialog(clientFrame, "X!Tandem Advanced Parameters", true, new XTandemParameters());
			}
		});
		
		xTandemSetBtn.setEnabled(xTandemChk.isSelected());
		
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
			public void actionPerformed(ActionEvent arg0) {
				new AdvancedSettingsDialog(clientFrame, "OMSSA Advanced Parameters", true, new OmssaParameters());
			}
		});
		
		omssaSetBtn.setEnabled(omssaChk.isSelected());
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
			public void actionPerformed(ActionEvent arg0) {
				new AdvancedSettingsDialog(clientFrame, "Crux Advanced Parameters", true, new CruxParameters());
			}
		});
		
		cruxSetBtn.setEnabled(cruxChk.isSelected());
		cruxChk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				cruxSetBtn.setEnabled(cruxChk.isSelected());
			}
		});
		
		// InsPecT CheckBox
		inspectChk = new JCheckBox("InsPecT", true);
		inspectChk.setIconTextGap(10);
		inspectSetBtn = new JButton("Advanced Settings");
		inspectSetBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new AdvancedSettingsDialog(clientFrame, "InsPect Advanced Parameters", true, new InspectParameters());
			}
		});
		
		inspectSetBtn.setEnabled(inspectChk.isSelected());
		inspectChk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				inspectSetBtn.setEnabled(inspectChk.isSelected());
			}
		});
		
		// Search strategy ComboBox
		searchTypeCbx = new JComboBox(new String[] {"Target-Decoy", "Target Only"});

		searchEngPnl.add(xTandemChk, cc.xy(2, 2));
		searchEngPnl.add(xTandemSetBtn, cc.xy(4, 2));
		searchEngPnl.add(omssaChk, cc.xy(2, 4));
		searchEngPnl.add(omssaSetBtn, cc.xy(4, 4));
		searchEngPnl.add(cruxChk, cc.xy(2, 6));
		searchEngPnl.add(cruxSetBtn, cc.xy(4, 6));
		searchEngPnl.add(inspectChk, cc.xy(2, 8));
		searchEngPnl.add(inspectSetBtn, cc.xy(4, 8));
		searchEngPnl.add(new JSeparator(), cc.xyw(2, 10, 3));
		searchEngPnl.add(new JLabel("Search Strategy:"), cc.xy(2, 12));
		searchEngPnl.add(searchTypeCbx, cc.xy(4, 12));

		// add everything to main panel
		this.add(protDatabasePnl, cc.xy(2, 2));
		this.add(paramsPnl, cc.xy(2, 4));
		this.add(searchEngPnl, cc.xy(2, 6));

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
	 * This method collects the database search settings.
	 * @return The database search settings.
	 */
	public DbSearchSettings collectDBSearchSettings() {
		DbSearchSettings dbSettings = new DbSearchSettings();
		dbSettings.setFastaFile(fastaFileCbx.getSelectedItem().toString());
		dbSettings.setFragmentIonTol((Double) fragTolSpn.getValue());
		dbSettings.setPrecursorIonTol((Double) precTolSpn.getValue());
		dbSettings.setNumMissedCleavages((Integer) missClvSpn.getValue());
		// TODO: Enzyme: settings.setEnzyme(value)
		dbSettings.setXTandem(xTandemChk.isSelected());
		dbSettings.setOmssa(omssaChk.isSelected());
		dbSettings.setCrux(cruxChk.isSelected());
		dbSettings.setInspect(inspectChk.isSelected());
		dbSettings.setDecoy(searchTypeCbx.getSelectedIndex() == 0);
		
		// Set the current experiment id for the database search settings.
		dbSettings.setExperimentid(clientFrame.getProjectPanel().getCurrentExperimentId());
		return dbSettings;
	}
		
	
	
	
}
