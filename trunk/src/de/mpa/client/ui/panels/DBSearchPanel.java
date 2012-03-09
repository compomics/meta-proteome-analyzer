package de.mpa.client.ui.panels;

import java.awt.AWTKeyStroke;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.ui.Constants;

public class DBSearchPanel extends JPanel {

	private JComboBox fastaFileCbx;
	private JSpinner fragTolSpn;
	private JSpinner precTolSpn;
	private JSpinner missClvSpn;
	private JCheckBox xTandemChk;
	private JCheckBox omssaChk;
	private JCheckBox cruxChk;
	private JCheckBox inspectChk;
	private JComboBox searchTypeCbx;
	private JButton xTandemSetBtn;
	private JButton omssaSetBtn;
	private JButton cruxSetBtn;
	private JButton inspectSetBtn;

	public DBSearchPanel() {
		initComponents();
	}

	private void initComponents() {
		CellConstraints cc = new CellConstraints();
		
		Set<AWTKeyStroke> forwardKeys = getFocusTraversalKeys(
				KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
		Set<AWTKeyStroke> newForwardKeys = new HashSet<AWTKeyStroke>(forwardKeys);
		newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
		setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
				newForwardKeys);

		this.setLayout(new FormLayout("8dlu, p, 8dlu", "p, 5dlu, p, 5dlu, p, 8dlu"));

		// Protein Database Panel
		final JPanel protDatabasePnl = new JPanel();
		protDatabasePnl.setLayout(new FormLayout("5dlu, p, 15dlu, p:g, 5dlu",
				"5dlu, p, 5dlu"));
		protDatabasePnl.setBorder(BorderFactory
				.createTitledBorder("Protein Database"));

		// FASTA file ComboBox
		fastaFileCbx = new JComboBox(Constants.FASTA_DB);
		
		protDatabasePnl.add(new JLabel("FASTA File:"), cc.xy(2, 2));
		protDatabasePnl.add(fastaFileCbx, cc.xy(4, 2));

		
		// Parameters Panel
		final JPanel paramsPnl = new JPanel();
		paramsPnl.setLayout(new FormLayout(
				"5dlu, p, 5dlu, p:g, 5dlu, p, 2dlu, p, 5dlu",
				"5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));
		paramsPnl.setBorder(BorderFactory.createTitledBorder("Parameters"));

		// Precursor ion tolerance Spinner
		precTolSpn = new JSpinner(new SpinnerNumberModel(1.0, 0.0, 10.0, 0.1));
		precTolSpn.setEditor(new JSpinner.NumberEditor(precTolSpn, "0.0"));
		precTolSpn.setToolTipText("Precursor Ion Tolerance:");

		// Fragment ion tolerance Spinner
		fragTolSpn = new JSpinner(new SpinnerNumberModel(0.5, 0.0, 10.0, 0.1));
		fragTolSpn.setEditor(new JSpinner.NumberEditor(fragTolSpn, "0.0"));
		fragTolSpn.setToolTipText("Fragment Ion Tolerance:");

		// Missed cleavages Spinner
		missClvSpn = new JSpinner(new SpinnerNumberModel(2, 0, 10, 1));
		missClvSpn.setToolTipText("Maximum number of missed cleavages:");

		// Enzyme ComboBox
		JComboBox enzymeCbx = new JComboBox(Constants.ENZYMES);

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
		searchEngPnl.setLayout(new FormLayout("5dlu, f:p:g, 5dlu, p:g, 5dlu",
				"5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));
		searchEngPnl.setBorder(BorderFactory.createTitledBorder("Search Engines"));

		// X!Tandem
		xTandemChk = new JCheckBox("X!Tandem", true);
		xTandemChk.setIconTextGap(10);
		xTandemSetBtn = new JButton("Advanced Settings");
		xTandemChk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				xTandemSetBtn.setEnabled(xTandemChk.isSelected());
			}
		});
		// OMSSA
		omssaChk = new JCheckBox("OMSSA", true);
		omssaChk.setIconTextGap(10);
		omssaSetBtn = new JButton("Advanced Settings");
		omssaChk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				omssaSetBtn.setEnabled(omssaChk.isSelected());
			}
		});
		// Crux
		cruxChk = new JCheckBox("Crux", true);
		cruxChk.setIconTextGap(10);
		cruxSetBtn = new JButton("Advanced Settings");
		cruxChk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				cruxSetBtn.setEnabled(cruxChk.isSelected());
			}
		});
		// InsPecT CheckBox
		inspectChk = new JCheckBox("InsPecT", true);
		inspectChk.setIconTextGap(10);
		inspectSetBtn = new JButton("Advanced Settings");
		inspectChk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				inspectSetBtn.setEnabled(inspectChk.isSelected());
			}
		});
		
		// Search strategy ComboBox
		searchTypeCbx = new JComboBox(new String[] { "Target only",
													 "Target-decoy" });

		searchEngPnl.add(xTandemChk, cc.xy(2, 2));
		searchEngPnl.add(xTandemSetBtn, cc.xy(4, 2));
		searchEngPnl.add(omssaChk, cc.xy(2, 4));
		searchEngPnl.add(omssaSetBtn, cc.xy(4, 4));
		searchEngPnl.add(cruxChk, cc.xy(2, 6));
		searchEngPnl.add(cruxSetBtn, cc.xy(4, 6));
		searchEngPnl.add(inspectChk, cc.xy(2, 8));
		searchEngPnl.add(inspectSetBtn, cc.xy(4, 8));
		searchEngPnl.add(new JSeparator(), cc.xyw(2, 10, 3));
		searchEngPnl.add(new JLabel("Search strategy:"), cc.xy(2, 12));
		searchEngPnl.add(searchTypeCbx, cc.xy(4, 12));


		// Status Panel
		JPanel statusPnl = new JPanel();
		statusPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p:g, 5dlu",
				"5dlu, p, 5dlu"));
		statusPnl.setBorder(BorderFactory.createTitledBorder("Search Status"));

		this.add(protDatabasePnl, cc.xy(2, 1));
		this.add(paramsPnl, cc.xy(2, 3));
		this.add(searchEngPnl, cc.xy(2, 5));

	}
	
	/**
	 * Method to toggle the enabled state of the whole panel.
	 * Honors separate enabled state of specific sub-components on restore.
	 */
	public void setEnabled(boolean enabled) {
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
		if (!(parent instanceof JPanel)) {	// don't mess with JPanels
			parent.setEnabled(enabled);
		}
	}

//	/**
//	 * RunDBSearchWorker class extending SwingWorker.
//	 * 
//	 * @author Thilo Muth
//	 * 
//	 */
//	private class RunDbSearchWorker extends SwingWorker {
//
//		protected Object doInBackground() throws Exception {
//			DbSearchSettings settings = collectDBSearchSettings();
//			try {
//				List<File> chunkedFiles = client.packFiles(1000, clientFrame
//						.getFilePanel().getCheckBoxTree(), "test");
//				client.sendFiles(chunkedFiles);
//				client.runDbSearch(chunkedFiles, settings);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			return 0;
//		}
//
//		@Override
//		public void done() {
//			runDbSearchBtn.setEnabled(true);
//		}
//	}

//	private DbSearchSettings collectDBSearchSettings() {
//		DbSearchSettings settings = new DbSearchSettings();
//		settings.setFastaFile(fastaFileCbx.getSelectedItem().toString());
//		settings.setFragmentIonTol((Double) fragTolSpn.getValue());
//		settings.setPrecursorIonTol((Double) precTolSpn.getValue());
//		settings.setNumMissedCleavages((Integer) missClvSpn.getValue());
//		// TODO: Enzyme: settings.setEnzyme(value)
//		settings.setXTandem(xTandemChk.isSelected());
//		settings.setOmssa(omssaChk.isSelected());
//		settings.setCrux(cruxChk.isSelected());
//		settings.setInspect(inspectChk.isSelected());
//		if (searchTypeCbx.getSelectedIndex() == 0) {
//			settings.setDecoy(false);
//			System.out.println(settings.isDecoy());
//		} else if (searchTypeCbx.getSelectedIndex() == 1) {
//			settings.setDecoy(true);
//			System.out.println(settings.isDecoy());
//		}
//		return settings;
//	}

}
