package de.mpa.client.ui.inputpanel;

import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXTitledPanel;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.settings.DbSearchSettings;
import de.mpa.client.settings.SearchSettings;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.inputpanel.dialogs.SearchFileDialog;
import de.mpa.client.ui.projectpanel.ProjectPanel;
import de.mpa.client.ui.sharedelements.PanelConfig;
import de.mpa.client.ui.sharedelements.dialogs.ConfirmFileChooser;
import de.mpa.client.ui.sharedelements.icons.IconConstants;
import de.mpa.client.ui.sharedelements.tables.CheckBoxTreeTable;
import de.mpa.db.mysql.storager.MascotStorager;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;
import de.mpa.io.fasta.FastaLoader;
import de.mpa.model.MPAExperiment;
import de.mpa.model.MPAProject;
import de.mpa.util.PropertyLoader;

/**
 * Panel containing search engine settings and processing controls.
 * 
 * @author A. Behne
 */
@SuppressWarnings("serial")
public class SettingsPanel extends JPanel {

	/**
	 * Database search settings panel.
	 */
	private DatabaseSearchSettingsPanel databasePnl;

	/**
	 * The search button.
	 */
	private JButton searchBtn;

	/**
	 * The quick search button.
	 */
	//	private JButton quickBtn;


	/**
	 * The batch search button.
	 */
	private JButton batchBtn;

	/**
	 * Creates the settings panel containing controls for configuring and
	 * starting database searches.
	 * @throws IOException 
	 */
	public SettingsPanel() {
		initComponents();
	}

	/**
	 * Initialize the UI components.
	 * @throws IOException 
	 */
	private void initComponents() {

		Set<AWTKeyStroke> forwardKeys = this.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
		Set<AWTKeyStroke> newForwardKeys = new HashSet<AWTKeyStroke>(forwardKeys);
		newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
		this.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, newForwardKeys);
		setLayout(new BorderLayout());

		// database search settings panel
		JPanel settingsPnl = new JPanel(new FormLayout("p", "f:p:g, 5dlu, p"));

		this.databasePnl = new DatabaseSearchSettingsPanel();

		JPanel buttonPnl = new JPanel(new FormLayout("8dlu, p, 7dlu, p, 7dlu, p:g, 7dlu, p:g, 8dlu", "2dlu, p, 7dlu"));

		// create connect button
		//		final JButton connectBtn = new JButton(IconConstants.DISCONNECT_ICON);
		//		connectBtn.setRolloverIcon(IconConstants.DISCONNECT_ROLLOVER_ICON);
		//		connectBtn.setPressedIcon(IconConstants.CONNECT_PRESSED_ICON);
		//		connectBtn.setToolTipText("Connect to Server");
		//		final Client client = Client.getInstance();

		//		connectBtn.addActionListener(new ActionListener() {
		//			/** The flag denoting whether a connection has been established. */
		//			private boolean connected;
		//
		//			@Override
		//			public void actionPerformed(ActionEvent evt) {
		//				if (connected) {
		//					connectBtn.setIcon(IconConstants.DISCONNECT_ICON);
		//					connectBtn.setRolloverIcon(IconConstants.DISCONNECT_ROLLOVER_ICON);
		//					connectBtn.setPressedIcon(IconConstants.CONNECT_PRESSED_ICON);
		//					connectBtn.setToolTipText("Connect to Server");
		//					client.disconnectFromServer();
		//					connected = false;
		//				} else {
		//					try {
		//						connected = client.connectToServer();
		//					} catch (Exception e) {
		//						JXErrorPane.showDialog(e);
		//					}
		//					if (connected) {
		//						connectBtn.setIcon(IconConstants.CONNECT_ICON);
		//						connectBtn.setRolloverIcon(IconConstants.CONNECT_ROLLOVER_ICON);
		//						connectBtn.setPressedIcon(IconConstants.CONNECT_PRESSED_ICON);
		//						connectBtn.setToolTipText("Disconnect from Server");
		//					} else {
		////						JOptionPane.showMessageDialog(ClientFrame.getInstance(),
		//								"Could not connect to server, please check connection settings and try again.",
		//								"Connection Error", JOptionPane.ERROR_MESSAGE);
		//					}
		//				}
		//
		//			}
		//		});



		// Button for batch search
		this.batchBtn = new JButton("Search Files", IconConstants.SAVE_FILE_ICON);
		this.batchBtn.setRolloverIcon(IconConstants.SAVE_FILE_ROLLOVER_ICON);
		this.batchBtn.setPressedIcon(IconConstants.SAVE_FILE_PRESSED_ICON);
		this.batchBtn.setEnabled(true);
		this.batchBtn.setToolTipText("Button to select and process several .mgf or .dat files" +
				" and store each file in a separate experiment.");
		this.batchBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				new SearchFileDialog(ClientFrame.getInstance(), "Search File selection Dialog", SettingsPanel.this);
			}
		});

		//		// button for quick searches
		//		quickBtn = new JButton("Quick Search", IconConstants.LIGHTNING_ICON);
		//		quickBtn.setRolloverIcon(IconConstants.LIGHTNING_ROLLOVER_ICON);
		//		quickBtn.setPressedIcon(IconConstants.LIGHTNING_PRESSED_ICON);
		//		quickBtn.addActionListener(new ActionListener() {
		//			public void actionPerformed(ActionEvent evt) {
		//				final JFileChooser fc = new JFileChooser();
		//				fc.setFileFilter(Constants.MGF_FILE_FILTER);
		//				fc.setAcceptAllFileFilterUsed(false);
		//				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		//				// changed to multiple selection
		//				fc.setMultiSelectionEnabled(true);
		//				int result = fc.showOpenDialog(ClientFrame.getInstance());
		//				if (result == JFileChooser.APPROVE_OPTION) {
		//					// QuickSearchWorker now accepts multiple files 
		//					new QuickSearchWorker(fc.getSelectedFiles()).execute();
		//				}
		//			}
		//		});
		//		quickBtn.setEnabled(true);

		// create process button
		this.searchBtn = new JButton("Search selected spectra", IconConstants.SEARCH_ICON);
		this.searchBtn.setRolloverIcon(IconConstants.SEARCH_ROLLOVER_ICON);
		this.searchBtn.setPressedIcon(IconConstants.SEARCH_PRESSED_ICON);
		//        this.searchBtn.setFont(this.searchBtn.getFont().deriveFont(Font.BOLD, this.searchBtn.getFont().getSize2D()*1.25f));
		this.searchBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				new ProcessWorker().execute();
			}
		});
		SettingsPanel.this.searchBtn.setEnabled(false);

		//		buttonPnl.add(connectBtn, CC.xy(2, 2));
		buttonPnl.add(this.batchBtn, CC.xy(4, 2));
		//		buttonPnl.add(quickBtn, CC.xy(6, 2));
		buttonPnl.add(this.searchBtn, CC.xy(8, 2));

		settingsPnl.add(this.databasePnl, CC.xy(1, 1));
		settingsPnl.add(buttonPnl, CC.xy(1, 3));

		JXTitledPanel dbTtlPnl = PanelConfig.createTitledPanel("Search Settings", settingsPnl);
		add(dbTtlPnl, BorderLayout.CENTER);
	}

	public void performBatchSearch(File[] selectedFiles, boolean singleExperiment, String mascotFile) throws SQLException {
		// reset progress
		Client client = Client.getInstance();
		// appear busy
		SettingsPanel.this.firePropertyChange("progress", null, 0);
		SettingsPanel.this.batchBtn.setEnabled(false);
		SettingsPanel.this.searchBtn.setEnabled(false);
		// Disable further actions until loading is finished
		ClientFrame.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		// Get selected experiment
		MPAExperiment selExp = ClientFrame.getInstance().getProjectPanel().getSelectedExperiment();
		client.firePropertyChange("new message", null, "Creating experiment:");
		HashMap<Long, ArrayList<File>> expFileMap = new HashMap<Long, ArrayList<File>>();
		for (File selectedFile : selectedFiles) {
			// Create experiment map 
			// pre-create experiment using current project as parent
			MPAProject selProject = ClientFrame.getInstance().getProjectPanel().getSelectedProject();
			MPAExperiment experiment;
//			if (Client.isViewer()) {
//				experiment = new FileExperiment();
//			} else {
			experiment = new MPAExperiment(selectedFile.getName(), selProject);
//			}
			// Create Experiments if necessary
			if (singleExperiment) {
				if (expFileMap.containsKey(selExp.getID())) {
					expFileMap.get(selExp.getID()).add(selectedFile);
				} else {
					ArrayList<File> filelist = new ArrayList<File>();
					filelist.add(selectedFile);
					expFileMap.put(selExp.getID(), filelist);
				}
			} else {
				experiment.setProject(selProject);
				experiment.persist(selectedFile.getName(), new TreeMap<String, String>(), selProject);
				ClientFrame.getInstance().getProjectPanel().refreshProjectTable();
				try {
					client.getConnection().commit();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				Long expId = experiment.getID();
				if (expFileMap.containsKey(expId)) {
					expFileMap.get(expId).add(selectedFile);
				} else {
					ArrayList<File> filelist = new ArrayList<File>();
					filelist.add(selectedFile);
					expFileMap.put(expId, filelist);
				}
			}
		}
		// Start searches in a worker
		new BatchProcessWorker(expFileMap, mascotFile).execute();
	}

	/**
	 * allows external activation of the search button (intended user: FilePanel)
	 */
	public void enableSearchBtn() {
		this.searchBtn.setEnabled(true);
	}

	/**
	 * allows external deactivation of the search button (intended user: FilePanel)
	 */
	public void disableSearchBtn() {
		this.searchBtn.setEnabled(false);
	}

	/**
	 * Worker class for packing/sending input files and dispatching search requests to the server instance.
	 * 
	 * @author Thilo Muth, Alex Behne
	 */
	private class ProcessWorker extends SwingWorker {
		protected Object doInBackground() {
			ProjectPanel projectPanel = ClientFrame.getInstance().getProjectPanel();
			long experimentID = projectPanel.getSelectedExperiment().getID();
			if (experimentID != 0L) {
				CheckBoxTreeTable checkBoxTree = ClientFrame.getInstance().getFilePanel().getSpectrumTable();
				// reset progress
				Client client = Client.getInstance();
				client.firePropertyChange("resetall", 0L, (long) (checkBoxTree.getCheckBoxTreeSelectionModel()).getSelectionCount());
				// appear busy
				this.firePropertyChange("progress", null, 0);
				SettingsPanel.this.searchBtn.setEnabled(false);
				ClientFrame.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				try {
					// Pack and send files.
					client.firePropertyChange("new message", null, "PACKING AND SENDING FILES");
					long packSize = SettingsPanel.this.databasePnl.getPackageSize();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
					List<String> filenames = null;
					// Collect search settings.
					DbSearchSettings dbss = (SettingsPanel.this.databasePnl.isEnabled()) ? SettingsPanel.this.databasePnl.gatherDBSearchSettings() : null;
					SearchSettings settings = new SearchSettings(dbss, null, experimentID);
					// FIXME: Please change that and get files from file tree.
					if (dbss.isMascot()) {		
						// Get Instance of a fastaLoader
						FastaLoader fastaLoader = FastaLoader.getInstance();
						String fastaFilePath = SettingsPanel.this.getFastaPath();
						if (fastaFilePath != null && !fastaFilePath.isEmpty()) {
							fastaLoader.setFastaFile(new File(fastaFilePath));
							//							try {
							//								fastaLoader.loadFastaFile();
							//							} catch (FileNotFoundException e) {
							//								e.printStackTrace();
							//							}
						} else {
							fastaLoader = null;
						}
						List<File> datFiles = ClientFrame.getInstance().getFilePanel().getSelectedMascotFiles();
						client.firePropertyChange("resetall", 0, datFiles.size());
						int i = 0;
						for (File datFile : datFiles) {
							client.firePropertyChange("new message", null, "STORING MASCOT FILE " + ++i + "/" + datFiles.size());
							// store mascot results
							MascotStorager storager = new MascotStorager(Client.getInstance().getDatabaseConnection(), datFile, settings, SettingsPanel.this.databasePnl.getMascotParameterMap(), fastaLoader);
							storager.run();
							client.firePropertyChange("new message", null, "FINISHED STORING MASCOT FILE " + i + "/" + datFiles.size());
						}
					}
					if (dbss.isXTandem() || dbss.isOmssa() || dbss.isCrux() || dbss.isInspect()) {
						filenames = client.packAndSend(packSize, checkBoxTree, projectPanel.getSelectedExperiment().getTitle() + "_" + sdf.format(new Date()) + "_");
					}

					client.firePropertyChange("new message", null, "SEARCHES RUNNING");

					// dispatch search request
					client.firePropertyChange("indeterminate", false, true);
					client.runSearches(filenames, settings);
					client.firePropertyChange("indeterminate", true, false);

				} catch (Exception e) {
					e.printStackTrace();
				}
				return 0;
			} else {
				JOptionPane.showMessageDialog(ClientFrame.getInstance(), "No experiment selected.", "Error", JOptionPane.ERROR_MESSAGE);
				return 404;
			}
		}

		@Override
		public void done() {
			ClientFrame.getInstance().setCursor(null);
			SettingsPanel.this.searchBtn.setEnabled(true);
		}
	}


	/**
	 * Worker class for packing/sending input files and dispatching search requests to the server instance.
	 * 
	 * @author Thilo Muth, Alex Behne, R. Heyer
	 */
	private class BatchProcessWorker extends SwingWorker {

		/**
		 * Map with experiments and the associated files.
		 */
		private final HashMap<Long, ArrayList<File>> expFileMap;

		/**
		 * The FASTA used to match mascot results to it
		 */
		private final String mascotFastaFile;

		/**
		 * Constructor for a batch with a map of experiments and files
		 * @param expFileMap
		 */
		public BatchProcessWorker(HashMap<Long, ArrayList<File>> expFileMap, String mascotFile) {
			this.expFileMap = expFileMap;
			this.mascotFastaFile = mascotFile;
		}

		protected Object doInBackground() {
			// count datfiles and mgffiles
			int datcount = 0;
			int mgfcount = 0;
			for (Map.Entry<Long, ArrayList<File>> fileEntry : this.expFileMap.entrySet()) {
				for (File expfile : fileEntry.getValue()) {
					if (expfile.getName().contains(".mgf")) {
						mgfcount++;
					} else if (expfile.getName().contains(".dat")) {
						datcount++;
					}
				}
			}
			
			Client client = Client.getInstance();
			SettingsPanel.this.searchBtn.setEnabled(false);
			SettingsPanel.this.batchBtn.setEnabled(false);
			ClientFrame.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			try {
				// Pack and send files.
				client.firePropertyChange("new message", null, "PACKING AND SENDING FILES");
				long packSize = SettingsPanel.this.databasePnl.getPackageSize();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				// Collect search settings.
				DbSearchSettings dbss = (SettingsPanel.this.databasePnl.isEnabled()) ? SettingsPanel.this.databasePnl.gatherDBSearchSettings() : null;
				SearchSettings settings = new SearchSettings(dbss, null, 1L);
				if (dbss.isMascot()) {
					// Get Instance of a fastaLoader
					FastaLoader fastaLoader = FastaLoader.getInstance();
					String fastaFilePath = Constants.FASTA_PATHS + Constants.SEP + this.mascotFastaFile + ".fasta";
					//					String fastaFilePath = SettingsPanel.this.getFastaPath();
					if (fastaFilePath != null && !fastaFilePath.isEmpty()) {
						// we only need to point to the correct file, the rest is now done during parsing
						fastaLoader.setFastaFile(new File(fastaFilePath));
					} else {
						fastaLoader = null;
					}
					client.firePropertyChange("resetall", 0, datcount);
					int i = 0; // Flag for number of dat-files
					for (Map.Entry<Long, ArrayList<File>> datFileEntry : this.expFileMap.entrySet()) {
						for (File expfile : datFileEntry.getValue()) {
							if (expfile.getName().contains(".dat")) {
								i++;
								client.firePropertyChange("new message", null, "STORING MASCOT FILE " + i + "/" + datcount);
								// Store Mascot results
								settings.setExpID(datFileEntry.getKey());
								MascotStorager storager = new MascotStorager(Client.getInstance().getDatabaseConnection(),
										expfile, settings, databasePnl.getMascotParameterMap(), fastaLoader);
								storager.run();
								client.firePropertyChange("progressmade", true, false);
								client.firePropertyChange("new message", null, "FINISHED STORING MASCOT FILE " + i + "/" + datcount);
							}
						}
					}
				}
				// process mgf-files
				int i = 0;
				client.firePropertyChange("resetall", 0L, mgfcount);
				client.firePropertyChange("resetcur", 0L, mgfcount);
				for (Map.Entry<Long, ArrayList<File>> fileEntry : this.expFileMap.entrySet()) {
					for (File expfile : fileEntry.getValue()) { 
						if (expfile.getName().contains(".mgf")) {
							i++;
							List<String> filenames = new ArrayList<String>();
							settings.setExpID(fileEntry.getKey());
							String mgffilename = expfile.getName();
							FileOutputStream fos = null;
//							client.firePropertyChange("indeterminate", false, true);
//							client.firePropertyChange("new message", null, "READING SPECTRUM FILE");
							MascotGenericFileReader reader = new MascotGenericFileReader(expfile, MascotGenericFileReader.LoadMode.SURVEY);
//							client.firePropertyChange("indeterminate", true, false);
//							client.firePropertyChange("new message", null, "READING SPECTRUM FILE FINISHED");
							List<Long> positions = reader.getSpectrumPositions(false);
							long numSpectra = 0L;
//							long maxSpectra = (long) positions.size();
							long packageSize = databasePnl.getPackageSize();
//							client.firePropertyChange("resetall", 0L, maxSpectra);
//							client.firePropertyChange("new message", null, "PACKING AND SENDING FILES");
							// iterate over all spectra 
							File batchFile = null;
							for (int j = 0; j < positions.size(); j++) {
								if ((numSpectra % packageSize) == 0) {
									if (fos != null) {
										fos.close();
										client.uploadFile(batchFile.getName(), client.getBytesFromFile(batchFile));
										batchFile.delete();
									}
									batchFile = new File(PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + "/data/output/xtandem/" + mgffilename + "_quick_batch_" + (numSpectra/packageSize) + ".mgf");
									filenames.add(batchFile.getName());
									fos = new FileOutputStream(batchFile);
//									long remaining = maxSpectra - numSpectra;
									
								}

								MascotGenericFile mgf = reader.loadSpectrum((int) numSpectra);
								mgf.writeToStream(fos);
								fos.flush();
								numSpectra++;
							}
							fos.close();
							client.uploadFile(batchFile.getName(), client.getBytesFromFile(batchFile));
							batchFile.delete();
//							client.firePropertyChange("new message", null, "PACKING AND SENDING FILES FINISHED");
							client.firePropertyChange("new message", null, "SEARCH " + i + "/" + mgfcount + " RUNNING");
							settings.setCurrentMgfNumber(i);
							settings.setMgfCount(mgfcount);
							// dispatch search request
//							client.firePropertyChange("indeterminate", false, true);
							client.runSearches(filenames, settings);
//							client.firePropertyChange("indeterminate", true, false);
							client.firePropertyChange("new message", null, "FINISHED SEARCH " + i + "/" + mgfcount);
							client.firePropertyChange("progressmade", true, false);
						}
					}
				}
				// restart the client frame File:
				ClientFrame.getInstance().restart();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 0;
		}

		@Override
		public void done() {
			ClientFrame.getInstance().setCursor(null);
			searchBtn.setEnabled(true);
			batchBtn.setEnabled(true);
		}
	}

	/**
	 * Convenience worker to process a file without loading its contents into the tree table first.
	 * @author A. Behne
	 */
	@Deprecated
	private class QuickSearchWorker extends SwingWorker<Object, Object> {

		/**
		 * The spectrum file.
		 */
		private File[] files;

		/**
		 * Constructs a quick search worker using the specified file.
		 * @param file the spectrum file
		 */
		public QuickSearchWorker(File[] files) {
			this.files = files;
		}

		@Override
		protected Object doInBackground() throws Exception {
			Client client = Client.getInstance();
			List<String> filenames = new ArrayList<String>();
			for (File file : this.files) {
				String mgffilename = file.getName();
				FileOutputStream fos = null;
				client.firePropertyChange("indeterminate", false, true);
				client.firePropertyChange("new message", null, "READING SPECTRUM FILE");
				MascotGenericFileReader reader = new MascotGenericFileReader(file, MascotGenericFileReader.LoadMode.SURVEY);
				client.firePropertyChange("indeterminate", true, false);
				client.firePropertyChange("new message", null, "READING SPECTRUM FILE FINISHED");
				List<Long> positions = reader.getSpectrumPositions(false);
				long numSpectra = 0L;
				long maxSpectra = (long) positions.size();
				long packageSize = SettingsPanel.this.databasePnl.getPackageSize();
				client.firePropertyChange("resetall", 0L, maxSpectra);
				client.firePropertyChange("new message", null, "PACKING AND SENDING FILES");
				// iterate over all spectra
				File batchFile = null;
				for (int j = 0; j < positions.size(); j++) {

					if ((numSpectra % packageSize) == 0) {
						if (fos != null) {
							fos.close();
							client.uploadFile(batchFile.getName(), client.getBytesFromFile(batchFile));
							batchFile.delete();
						}
						batchFile = new File(PropertyLoader.getProperty(PropertyLoader.BASE_PATH) + "/data/output/xtandem/" + mgffilename + "_quick_batch_" + (numSpectra/packageSize) + ".mgf");
						filenames.add(batchFile.getName());
						fos = new FileOutputStream(batchFile);
						long remaining = maxSpectra - numSpectra;
						this.firePropertyChange("resetcur", 0L, (remaining > packageSize) ? packageSize : remaining);
					}

					MascotGenericFile mgf = reader.loadSpectrum((int) numSpectra);
					mgf.writeToStream(fos);
					fos.flush();
					this.firePropertyChange("progressmade", 0L, ++numSpectra);
				}
				fos.close();
				client.uploadFile(batchFile.getName(), client.getBytesFromFile(batchFile));
				batchFile.delete();
				client.firePropertyChange("new message", null, "PACKING AND SENDING FILES FINISHED");
			}

			// collect search settings
			DbSearchSettings dbss = (SettingsPanel.this.databasePnl.isEnabled()) ? SettingsPanel.this.databasePnl.gatherDBSearchSettings() : null;
			SearchSettings settings = new SearchSettings(dbss, null, ClientFrame.getInstance().getProjectPanel().getSelectedExperiment().getID());
			client.firePropertyChange("new message", null, "SEARCHES RUNNING");
			// dispatch search request		
			client.firePropertyChange("indeterminate", false, true);
			client.runSearches(filenames, settings);				
			client.firePropertyChange("indeterminate", true, false);
			return null;
		}
	}

	/**
	 * Returns the database search settings panel.
	 * @return the database search settings panel
	 */
	public DatabaseSearchSettingsPanel getDatabaseSearchSettingsPanel() {
		return this.databasePnl;
	}

	/**
	 * Convienience Method to fetch the fasta path/
	 * @return
	 */
	private String getFastaPath() {
		String fastaFilePath = null;
		// Get Fasta path to store the amino acid sequence
		if (((Boolean) this.databasePnl.getMascotParameterMap().get("useFasta").getValue()).booleanValue()) {
			JFileChooser chooser = new ConfirmFileChooser();
			chooser.setFileFilter(Constants.FASTA_FILE_FILTER);
			int returnVal = chooser.showOpenDialog(ClientFrame.getInstance());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File selFile = chooser.getSelectedFile();
				if (selFile != null) {
					fastaFilePath = selFile.getPath();
				}
			}
		}
		return fastaFilePath;
	}

}
