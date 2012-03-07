package de.mpa.client.ui.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Client;
import de.mpa.client.DbSearchSettings;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.Constants;

public class DBSearchPanel extends JPanel {

	private ClientFrame clientFrame;
	private Client client;
	
	private JComboBox fastaFileCbx;
	private JSpinner fragTolSpn;
	private JSpinner precTolSpn;
	private JSpinner missClvSpn;
	private JCheckBox xTandemChk;
	private JCheckBox omssaChk;
	private JCheckBox cruxChk;
	private JCheckBox inspectChk;
	private JComboBox searchTypeCbx;
	private JButton runDbSearchBtn;

	public DBSearchPanel(ClientFrame clientFrame) {
		this.clientFrame = clientFrame;
		this.client = clientFrame.getClient();
		initComponents();
	}

	private void initComponents() {
		CellConstraints cc = new CellConstraints();

		this.setLayout(new FormLayout("5dlu, p, 10dlu, p",
				"5dlu, f:p, 5dlu, p, 5dlu, t:p, 5dlu"));	

		// Protein Database Panel
		final JPanel protDatabasePnl = new JPanel();
		protDatabasePnl.setLayout(new FormLayout("5dlu, p, 15dlu, p:g, 5dlu", "5dlu, p, 5dlu"));		
		protDatabasePnl.setBorder(BorderFactory.createTitledBorder("Protein Database"));	

		// FASTA file Label
		final JLabel fastaFileLbl = new JLabel("FASTA File:");
		protDatabasePnl.add(fastaFileLbl, cc.xy(2, 2));

		// FASTA file ComboBox
		fastaFileCbx = new JComboBox(Constants.FASTA_DB);
		protDatabasePnl.add(fastaFileCbx, cc.xy(4, 2));

		// Parameters Panel
		final JPanel paramsPnl = new JPanel();
		paramsPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p:g, 5dlu, p, 2dlu, p, 5dlu", "5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));		
		paramsPnl.setBorder(BorderFactory.createTitledBorder("Parameters"));	

		// Precursor ion tolerance Label
		final JLabel precTolLbl = new JLabel("Precursor Ion Tolerance:");
		paramsPnl.add(precTolLbl, cc.xyw(2, 2, 3));

		// Precursor ion tolerance Spinner
		precTolSpn = new JSpinner(new SpinnerNumberModel(1.0, 0.0, 10.0, 0.1));
		precTolSpn.setEditor(new JSpinner.NumberEditor(precTolSpn, "0.0"));
		precTolSpn.setToolTipText("Precursor Ion Tolerance:");	    
		paramsPnl.add(precTolSpn, cc.xy(6, 2));
		paramsPnl.add(new JLabel("Da"), cc.xy(8,2));

		// Fragment ion tolerance Label
		final JLabel fragTolLbl = new JLabel("Fragment Ion Tolerance:");
		paramsPnl.add(fragTolLbl, cc.xyw(2, 4, 3));

		// Fragment ion tolerance Spinner
		fragTolSpn = new JSpinner(new SpinnerNumberModel(0.5, 0.0, 10.0, 0.1));
		fragTolSpn.setEditor(new JSpinner.NumberEditor(fragTolSpn, "0.0"));
		fragTolSpn.setToolTipText("Fragment Ion Tolerance:");	    
		paramsPnl.add(fragTolSpn, cc.xy(6, 4));
		paramsPnl.add(new JLabel("Da"), cc.xy(8,4));

		// Missed cleavages Label
		final JLabel missClvLbl = new JLabel("Missed Cleavages (max):");
		paramsPnl.add(missClvLbl, cc.xyw(2, 6, 3));

		// Missed cleavages Spinner
		missClvSpn = new JSpinner(new SpinnerNumberModel(2, 0, 10, 1));
		//missClvSpn.setEditor(new JSpinner.NumberEditor(fragTolSpn, "0"));
		missClvSpn.setToolTipText("Maximum number of missed cleavages:");	    
		paramsPnl.add(missClvSpn, cc.xy(6, 6));

		// Enzyme Label
		final JLabel enzymeLbl = new JLabel("Enzyme (Protease):");
		paramsPnl.add(enzymeLbl, cc.xy(2, 8));

		// Enzyme ComboBox
		JComboBox enzymeCbx = new JComboBox(Constants.ENZYMES);
		paramsPnl.add(enzymeCbx, cc.xyw(4, 8, 5));

		// Search Engine Panel
		final JPanel searchEngPnl = new JPanel();
		searchEngPnl.setLayout(new FormLayout("5dlu, p, 10dlu, p, 10dlu, p, 5dlu", "5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));		
		searchEngPnl.setBorder(BorderFactory.createTitledBorder("Search Engines"));	

		// X!Tandem Label
		final JLabel xTandemLbl = new JLabel("X!Tandem:");
		searchEngPnl.add(xTandemLbl, cc.xy(2, 2));

		// X!Tandem CheckBox
		xTandemChk = new JCheckBox();
		xTandemChk.setSelected(true);
		searchEngPnl.add(xTandemChk, cc.xy(4, 2));
		// TODO: Add action listener
		JButton xTandemSetBtn = new JButton("Advanced Settings");
		xTandemSetBtn.setEnabled(false);
		searchEngPnl.add(xTandemSetBtn, cc.xy(6, 2));

		// OMSSA Label
		final JLabel omssaLbl = new JLabel("OMSSA:");
		searchEngPnl.add(omssaLbl, cc.xy(2, 4));

		// OMSSA CheckBox
		omssaChk = new JCheckBox();
		omssaChk.setSelected(true);
		searchEngPnl.add(omssaChk, cc.xy(4, 4));
		// TODO: Add action listener
		JButton omssaSetBtn = new JButton("Advanced Settings");
		omssaSetBtn.setEnabled(false);
		searchEngPnl.add(omssaSetBtn, cc.xy(6, 4));

		// Crux Label
		final JLabel cruxLbl = new JLabel("Crux:");
		searchEngPnl.add(cruxLbl, cc.xy(2, 6));

		// InsPecT Label
		final JLabel inspectLbl = new JLabel("InsPecT:");
		searchEngPnl.add(inspectLbl, cc.xy(2, 8));

		// Crux CheckBox
		cruxChk = new JCheckBox();
		cruxChk.setSelected(true);
		searchEngPnl.add(cruxChk, cc.xy(4, 6));
		// TODO: Add action listener
		JButton cruxSetBtn = new JButton("Advanced Settings");
		cruxSetBtn.setEnabled(false);
		searchEngPnl.add(cruxSetBtn, cc.xy(6, 6));

		// InsPecT CheckBox
		inspectChk = new JCheckBox();
		inspectChk.setSelected(true);
		searchEngPnl.add(inspectChk, cc.xy(4, 8));
		// TODO: Add action listener
		JButton inspectSetBtn = new JButton("Advanced Settings");
		inspectSetBtn.setEnabled(false);
		searchEngPnl.add(inspectSetBtn, cc.xy(6, 8));
		// Search Start Panel
		final JPanel runPnl = new JPanel();
		runPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p, 10dlu, p", "5dlu, p, 5dlu"));		
		runPnl.setBorder(BorderFactory.createTitledBorder("Search Start"));

		// Search type Label
		final JLabel searchTypeLbl = new JLabel("Type:");
		runPnl.add(searchTypeLbl, cc.xy(2, 2));

		// Search type ComboBox
		searchTypeCbx = new JComboBox(new String[] {"Target only", "Target-decoy"});
		runPnl.add(searchTypeCbx, cc.xy(4, 2));

		// Search Run Button
		runDbSearchBtn = new JButton("Run");	    
		runDbSearchBtn.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				runDbSearchBtn.setEnabled(false);
				RunDbSearchWorker worker = new RunDbSearchWorker();
//				worker.addPropertyChangeListener(new PropertyChangeListener() {
//					@Override
//					public void propertyChange(PropertyChangeEvent evt) {
//						if ("progress" == evt.getPropertyName()) {
//							int progress = (Integer) evt.getNewValue();
//							specLibPnl.setProgress(progress);
//						} 
//
//					}
//				});
				worker.execute();
			}
		});	    
		runPnl.add(runDbSearchBtn, cc.xy(6, 2));

		// Status Panel
		JPanel statusPnl = new JPanel();
		statusPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p:g, 5dlu",
				"5dlu, p, 5dlu"));		
		statusPnl.setBorder(BorderFactory.createTitledBorder("Search Status"));

		// Progress Label
		final JLabel progressLbl = new JLabel("Progress:");
		statusPnl.add(progressLbl, cc.xy(2, 2));

		// Progress bar
		JProgressBar searchPrg = new JProgressBar(0, 100);
		searchPrg.setStringPainted(true);
		searchPrg.setValue(0);
		statusPnl.add(searchPrg, cc.xy(4, 2));

		// Status details Panel
		final JPanel statusDetailsPnl = new JPanel();
		statusDetailsPnl.setLayout(new FormLayout("5dlu, p, 10dlu, p, 5dlu", "5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));		
		statusDetailsPnl.setBorder(BorderFactory.createTitledBorder("Search Details"));	

		// X!Tandem status Label
		final JLabel xtandemStatLbl = new JLabel("X!Tandem:");
		statusDetailsPnl.add(xtandemStatLbl, cc.xy(2, 2));

		// X!Tandem status TextField
		JTextField xtandemStatTtf = new JTextField(15);
		xtandemStatTtf.setEditable(false);
		xtandemStatTtf.setEnabled(false);
		statusDetailsPnl.add(xtandemStatTtf, cc.xy(4, 2));

		// OMSSA status Label
		final JLabel omssaStatLbl = new JLabel("OMSSA:");
		statusDetailsPnl.add(omssaStatLbl, cc.xy(2, 4));

		// OMSSA status TextField
		JTextField omssaStatTtf = new JTextField(15);
		omssaStatTtf.setEditable(false);
		omssaStatTtf.setEnabled(false);
		statusDetailsPnl.add(omssaStatTtf, cc.xy(4, 4));

		// Crux status Label
		final JLabel cruxStatLbl = new JLabel("Crux:");
		statusDetailsPnl.add(cruxStatLbl, cc.xy(2, 6));

		// Crux status TextField
		JTextField cruxStatTtf = new JTextField(15);
		cruxStatTtf.setEditable(false);
		cruxStatTtf.setEnabled(false);
		statusDetailsPnl.add(cruxStatTtf, cc.xy(4, 6));

		// InsPecT status Label
		final JLabel inspectStatLbl = new JLabel("InsPecT:");
		statusDetailsPnl.add(inspectStatLbl, cc.xy(2, 8));

		// InsPecT status TextField
		JTextField inspectStatTtf = new JTextField(15);
		inspectStatTtf.setEditable(false);
		inspectStatTtf.setEnabled(false);
		statusDetailsPnl.add(inspectStatTtf, cc.xy(4, 8));

		this.add(protDatabasePnl, cc.xy(2, 2));	    
		this.add(statusPnl, cc.xy(4, 2));
		this.add(paramsPnl, cc.xy(2, 4));
		this.add(statusDetailsPnl, cc.xy(4, 4));
		this.add(searchEngPnl, cc.xy(2, 6));
		this.add(runPnl, cc.xy(4, 6));
		
	}
	
	/**
	 * RunDBSearchWorker class extending SwingWorker.
	 * @author Thilo Muth
	 *
	 */
	private class RunDbSearchWorker	extends SwingWorker {

		protected Object doInBackground() throws Exception {
			DbSearchSettings settings = collectDBSearchSettings();
			try {
				List<File> chunkedFiles = client.packFiles(1000, clientFrame.getFilePanel().getCheckBoxTree(), "test");
				client.sendFiles(chunkedFiles);
				client.runDbSearch(chunkedFiles, settings);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 0;
		}

		@Override
		public void done() {
			runDbSearchBtn.setEnabled(true);
		}
	}

	private DbSearchSettings collectDBSearchSettings() {
		DbSearchSettings settings = new DbSearchSettings();
		settings.setFastaFile(fastaFileCbx.getSelectedItem().toString());
		settings.setFragmentIonTol((Double) fragTolSpn.getValue());
		settings.setPrecursorIonTol((Double) precTolSpn.getValue());
		settings.setNumMissedCleavages((Integer) missClvSpn.getValue());
		//TODO: Enzyme: settings.setEnzyme(value)
		settings.setXTandem(xTandemChk.isSelected());
		settings.setOmssa(omssaChk.isSelected());
		settings.setCrux(cruxChk.isSelected());
		settings.setInspect(inspectChk.isSelected());
		if (searchTypeCbx.getSelectedIndex() == 0) {
			settings.setDecoy(false);
			System.out.println(settings.isDecoy());
		}
		else if (searchTypeCbx.getSelectedIndex() == 1) {
			settings.setDecoy(true);
			System.out.println(settings.isDecoy());
		}
		return settings;
	}

	
}
