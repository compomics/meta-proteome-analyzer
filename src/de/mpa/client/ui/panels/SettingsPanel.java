package de.mpa.client.ui.panels;

import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Font;
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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXTitledPanel;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.DbSearchSettings;
import de.mpa.client.SearchSettings;
import de.mpa.client.SpecSimSettings;
import de.mpa.client.model.AbstractExperiment;
import de.mpa.client.model.AbstractProject;
import de.mpa.client.model.DatabaseExperiment;
import de.mpa.client.model.FileExperiment;
import de.mpa.client.ui.CheckBoxTreeTable;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.ConfirmFileChooser;
import de.mpa.client.ui.MultiExtensionFileFilter;
import de.mpa.client.ui.PanelConfig;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.db.storager.MascotStorager;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;
import de.mpa.io.MascotGenericFileReader.LoadMode;
import de.mpa.io.fasta.FastaLoader;
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
	 * Spectral library settings panel.
	 */
	private SpectralLibrarySettingsPanel specLibPnl;

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
		this.initComponents();
	}

	/**
	 * Initialize the UI components.
	 * @throws IOException 
	 */
	private void initComponents() {

		Set<AWTKeyStroke> forwardKeys = getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
		Set<AWTKeyStroke> newForwardKeys = new HashSet<AWTKeyStroke>(forwardKeys);
		newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
		setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, newForwardKeys);
		this.setLayout(new BorderLayout());

		// database search settings panel
		JPanel settingsPnl = new JPanel(new FormLayout("p", "f:p:g, 5dlu, p"));

		databasePnl = new DatabaseSearchSettingsPanel();
		this.specLibPnl = databasePnl.getSpectralLibrarySettingsPanel();

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
		batchBtn = new JButton("Search Files", IconConstants.SAVE_FILE_ICON);
		batchBtn.setRolloverIcon(IconConstants.SAVE_FILE_ROLLOVER_ICON);
		batchBtn.setPressedIcon(IconConstants.SAVE_FILE_PRESSED_ICON);
		batchBtn.setEnabled(true);
		batchBtn.setToolTipText("Press button to select and process several .mgf or .dat files" +
				" and store each file in a separate experiment.");
		batchBtn.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent evt) {
				final JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new MultiExtensionFileFilter(
						"All supported formats (*.mgf, *.dat)",
						Constants.MGF_FILE_FILTER,
						Constants.DAT_FILE_FILTER));
				fc.setAcceptAllFileFilterUsed(false);
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fc.setMultiSelectionEnabled(true);
				int result = fc.showOpenDialog(ClientFrame.getInstance());
				if (result == JFileChooser.APPROVE_OPTION) {
					File[] selectedFiles = fc.getSelectedFiles();
					// reset progress
					Client client = Client.getInstance();
					// appear busy
					firePropertyChange("progress", null, 0);
					batchBtn.setEnabled(false);
//					quickBtn.setEnabled(false);
					searchBtn.setEnabled(false);
					// Disable further actions until loading is finished
					ClientFrame.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					// Get selected experiment
					AbstractExperiment selExp = ClientFrame.getInstance().getProjectPanel().getSelectedExperiment();
					client.firePropertyChange("new message", null, "Creating experiment:");
					LinkedHashMap<Long, File> expFileMap = new LinkedHashMap<Long, File>();
					for (File selectedFile : selectedFiles) {
						// Create experiment map 
						// pre-create experiment using current project as parent
						AbstractProject selProject = ClientFrame.getInstance().getProjectPanel().getSelectedProject();
						AbstractExperiment experiment;
						if (Client.isViewer()) {
							experiment = new FileExperiment();
						} else {
							experiment = new DatabaseExperiment();
						}
						// Create Experiment
						experiment.setProject(selProject);
						experiment.persist(selExp.getTitle() +"_"+ selectedFile.getName(), new TreeMap<String, String>() );
						ClientFrame.getInstance().getProjectPanel().refreshProjectTable();
						try {
							client.getConnection().commit();
						} catch (SQLException e) {
							e.printStackTrace();
						}
						Long expId = experiment.getID();
						expFileMap.put(expId, selectedFile);
					}
					// Start searches in a worker
					new BatchProcessWorker(expFileMap).execute();
					
					
					// Delete old experiment name
//					selExp.delete();
					
					//	new QuickSearchWorker(fc.getSelectedFile()).execute();
				}
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
		searchBtn = new JButton("Search selected spectra", IconConstants.SEARCH_ICON);
		searchBtn.setRolloverIcon(IconConstants.SEARCH_ROLLOVER_ICON);
		searchBtn.setPressedIcon(IconConstants.SEARCH_PRESSED_ICON);
		searchBtn.setFont(searchBtn.getFont().deriveFont(Font.BOLD, searchBtn.getFont().getSize2D()*1.25f));
		searchBtn.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent evt) {
				new ProcessWorker().execute();
			}
		});
		searchBtn.setEnabled(true);

//		buttonPnl.add(connectBtn, CC.xy(2, 2));
		buttonPnl.add(batchBtn, CC.xy(4, 2));
//		buttonPnl.add(quickBtn, CC.xy(6, 2));
		buttonPnl.add(searchBtn, CC.xy(8, 2));

		settingsPnl.add(databasePnl, CC.xy(1, 1));
		settingsPnl.add(buttonPnl, CC.xy(1, 3));

		JXTitledPanel dbTtlPnl = PanelConfig.createTitledPanel("Search Settings", settingsPnl);
		this.add(dbTtlPnl, BorderLayout.CENTER);
	}

	/**
	 * Worker class for packing/sending input files and dispatching search requests to the server instance.
	 * 
	 * @author Thilo Muth, Alex Behne
	 */
	@SuppressWarnings("rawtypes")
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
				firePropertyChange("progress", null, 0);
				searchBtn.setEnabled(false);
				ClientFrame.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				try {
					// Pack and send files.
					client.firePropertyChange("new message", null, "PACKING AND SENDING FILES");
					long packSize = databasePnl.getPackageSize();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
					List<String> filenames = null;
					// Collect search settings.
					DbSearchSettings dbss = (databasePnl.isEnabled()) ? databasePnl.gatherDBSearchSettings() : null;
					SpecSimSettings sss = (specLibPnl.isEnabled()) ? specLibPnl.gatherSpecSimSettings() : null;
					SearchSettings settings = new SearchSettings(dbss, sss, experimentID);
					// FIXME: Please change that and get files from file tree.
					if (dbss.isMascot()) {		
						// Get Instance of a fastaLoader
						FastaLoader fastaLoader = FastaLoader.getInstance();
						String fastaFilePath = getFastaPath();
						if (fastaFilePath != null && !fastaFilePath.isEmpty()) {

							fastaLoader.setFastaFile(new File(fastaFilePath));
//							try {
//								fastaLoader.loadFastaFile();
//							} catch (FileNotFoundException e) {
//								e.printStackTrace();
//							}
						}else{
							fastaLoader = null;
						}
						List<File> datFiles = ClientFrame.getInstance().getFilePanel().getSelectedMascotFiles();
						client.firePropertyChange("resetall", 0, datFiles.size());
						int i = 0;
						for (File datFile : datFiles) {
							client.firePropertyChange("new message", null, "STORING MASCOT FILE " + ++i + "/" + datFiles.size());
							// store mascot results
							MascotStorager storager = new MascotStorager(Client.getInstance().getDatabaseConnection(), datFile, settings, databasePnl.getMascotParameterMap(), fastaLoader);
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
			searchBtn.setEnabled(true);
		}
	}


	/**
	 * Worker class for packing/sending input files and dispatching search requests to the server instance.
	 * 
	 * @author Thilo Muth, Alex Behne, R. Heyer
	 */
	@SuppressWarnings("rawtypes")
	private class BatchProcessWorker extends SwingWorker {

		/**
		 * Map with experiments and the associated files.
		 */
		private LinkedHashMap<Long, File>expFileMap;

		/**
		 * Constructor for a batch with a map of experiments and files
		 * @param expFileMap
		 */
		public BatchProcessWorker(LinkedHashMap<Long, File> expFileMap) {
			this.expFileMap = expFileMap;
		}

		protected Object doInBackground() {
			Client client = Client.getInstance();
			searchBtn.setEnabled(false);
			ClientFrame.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			try {
				// Pack and send files.
				client.firePropertyChange("new message", null, "PACKING AND SENDING FILES");
				@SuppressWarnings("unused")
				long packSize = databasePnl.getPackageSize();
				@SuppressWarnings("unused")
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				// Collect search settings.
				DbSearchSettings dbss = (databasePnl.isEnabled()) ? databasePnl.gatherDBSearchSettings() : null;
				SpecSimSettings sss = (specLibPnl.isEnabled()) ? specLibPnl.gatherSpecSimSettings() : null;
				SearchSettings settings = new SearchSettings(dbss, sss, 1L);
				if (dbss.isMascot()) {
					// Get Instance of a fastaLoader
					FastaLoader fastaLoader = FastaLoader.getInstance();
					String fastaFilePath = getFastaPath();
					if (fastaFilePath != null && !fastaFilePath.isEmpty()) {
						// we only need to point to the correct file, the rest is now done during parsing
						fastaLoader.setFastaFile(new File(fastaFilePath));
					}else{
						fastaLoader = null;
					}
					client.firePropertyChange("resetall", 0, expFileMap.size());
					int i = 0; // Flag for number of dat-files
					for (Entry<Long, File> datFileEntry : expFileMap.entrySet()) {
						if (datFileEntry.getValue().getName().contains(".dat")) {
							client.firePropertyChange("new message", null, "STORING MASCOT FILE " + ++i + "/" + expFileMap.size());
							// Store Mascot results
							settings.setExpID(datFileEntry.getKey());

							MascotStorager storager = new MascotStorager(Client.getInstance().getDatabaseConnection(), 
									datFileEntry.getValue(), settings, databasePnl.getMascotParameterMap(), fastaLoader);
							storager.run();
							client.firePropertyChange("new message", null, "FINISHED STORING MASCOT FILE " + i + "/" + expFileMap.size());
						}
					}
				}
				
				// quicksearch code start
				
				List<String> filenames = new ArrayList<String>();
				for (Entry<Long, File> fileEntry : expFileMap.entrySet()) {
					if (fileEntry.getValue().getName().contains(".mgf")) {
						settings.setExpID(fileEntry.getKey());
						String mgffilename = fileEntry.getValue().getName();
						FileOutputStream fos = null;
						client.firePropertyChange("indeterminate", false, true);
						client.firePropertyChange("new message", null, "READING SPECTRUM FILE");
						MascotGenericFileReader reader = new MascotGenericFileReader(fileEntry.getValue(), LoadMode.SURVEY);
						client.firePropertyChange("indeterminate", true, false);
						client.firePropertyChange("new message", null, "READING SPECTRUM FILE FINISHED");
						List<Long> positions = reader.getSpectrumPositions(false);
						long numSpectra = 0L;
						long maxSpectra = (long) positions.size();
						long packageSize = databasePnl.getPackageSize();
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
								firePropertyChange("resetcur", 0L, (remaining > packageSize) ? packageSize : remaining);
							}

							MascotGenericFile mgf = reader.loadSpectrum((int) numSpectra);
							mgf.writeToStream(fos);
							fos.flush();
							firePropertyChange("progressmade", 0L, ++numSpectra);
						}
						fos.close();
						client.uploadFile(batchFile.getName(), client.getBytesFromFile(batchFile));
						batchFile.delete();
						client.firePropertyChange("new message", null, "PACKING AND SENDING FILES FINISHED");
						client.firePropertyChange("new message", null, "SEARCH FOR " + mgffilename +  " RUNNING");
						// dispatch search request		
						client.firePropertyChange("indeterminate", false, true);
						client.runSearches(filenames, settings);
						client.firePropertyChange("indeterminate", true, false);
					}
				}
				return null;
				
				
				// quicksearch code end
				
				
				
				// TODO: CODE for runnning mulitiple database search jobs .... not working yet
				// Start database searches
//				if (dbss.isXTandem() || dbss.isOmssa() || dbss.isCrux() || dbss.isInspect()) {
//					int i = 0;
//					for (Entry<Long, File> mgfSearch : expFileMap.entrySet()) {
//						client.firePropertyChange("new message", null, "Start Database Search " + ++i + "/" + expFileMap.size() +" " + mgfSearch.getValue().getAbsolutePath());
//						settings.setExpID(mgfSearch.getKey());
//						// pack and send the files to the server
//						List<String> mgfFileNames = new ArrayList<String>();
//						FileOutputStream fos = null;
//						client.firePropertyChange("indeterminate", false, true);
//						client.firePropertyChange("new message", null, "READING SPECTRUM FILE");
//						MascotGenericFileReader reader = new MascotGenericFileReader(mgfSearch.getValue(), LoadMode.SURVEY);
//						client.firePropertyChange("indeterminate", true, false);
//						client.firePropertyChange("new message", null, "READING SPECTRUM FILE FINISHED");
//						List<Long> positions = reader.getSpectrumPositions(false);
//						long numSpectra = 0L;
//						long maxSpectra = (long) positions.size();
//						long packageSize = databasePnl.getPackageSize();
//						client.firePropertyChange("resetall", 0L, maxSpectra);
//						client.firePropertyChange("new message", null, "PACKING AND SENDING FILES");
//						// iterate over all spectra
//						File batchFile = null;
//						for (int j = 0; j < positions.size(); j++) {
//
//							if ((numSpectra % packageSize) == 0) {
//								System.out.println("first if");
//								if (fos != null) {
//									System.out.println("2nd if");
//									fos.close();
//									client.uploadFile(batchFile.getName(), client.getBytesFromFile(batchFile));
//									batchFile.delete();
//								}
//								batchFile = new File("quick_batch" + (numSpectra/packageSize) + ".mgf");
//								mgfFileNames.add(batchFile.getName());
//								fos = new FileOutputStream(batchFile);
//								long remaining = maxSpectra - numSpectra;
//								firePropertyChange("resetcur", 0L, (remaining > packageSize) ? packageSize : remaining);
//							}
//
//							MascotGenericFile mgf = reader.loadSpectrum((int) numSpectra);
//							mgf.writeToStream(fos);
//							fos.flush();
//							firePropertyChange("progressmade", 0L, ++numSpectra);
//						}
//						fos.close();
//						client.uploadFile(batchFile.getName(), client.getBytesFromFile(batchFile));
//						batchFile.delete();
//						client.firePropertyChange("new message", null, "PACKING AND SENDING FILES FINISHED");
//						for (String str : mgfFileNames) {
//							System.out.println("RunSearches on Files>" +str +" " + settings.getExpID());							
//						}
//						//client.runSearches(mgfFileNames, settings);
//						
//						//////////////////
//						// Update expFileMap to the sended filename of the server
//						expFileMap.put(mgfSearch.getKey(), mgfSearch.getValue());
//					}
//				client.firePropertyChange("new message", null, "SEARCHES RUNNING");
//				// dispatch search request
//				//client.runSearches(expFileMap, settings);
//				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 0;
		}

		@Override
		public void done() {
			ClientFrame.getInstance().setCursor(null);
			searchBtn.setEnabled(true);
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
				MascotGenericFileReader reader = new MascotGenericFileReader(file, LoadMode.SURVEY);
				client.firePropertyChange("indeterminate", true, false);
				client.firePropertyChange("new message", null, "READING SPECTRUM FILE FINISHED");
				List<Long> positions = reader.getSpectrumPositions(false);
				long numSpectra = 0L;
				long maxSpectra = (long) positions.size();
				long packageSize = databasePnl.getPackageSize();
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
						firePropertyChange("resetcur", 0L, (remaining > packageSize) ? packageSize : remaining);
					}
	
					MascotGenericFile mgf = reader.loadSpectrum((int) numSpectra);
					mgf.writeToStream(fos);
					fos.flush();
					firePropertyChange("progressmade", 0L, ++numSpectra);
				}
				fos.close();
				client.uploadFile(batchFile.getName(), client.getBytesFromFile(batchFile));
				batchFile.delete();
				client.firePropertyChange("new message", null, "PACKING AND SENDING FILES FINISHED");
			}
			
			// collect search settings
			DbSearchSettings dbss = (databasePnl.isEnabled()) ? databasePnl.gatherDBSearchSettings() : null;
			SpecSimSettings sss = (specLibPnl.isEnabled()) ? specLibPnl.gatherSpecSimSettings() : null;
			SearchSettings settings = new SearchSettings(dbss, sss, ClientFrame.getInstance().getProjectPanel().getSelectedExperiment().getID());
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
		return databasePnl;
	}

	/**
	 * Convienience Method to fetch the fasta path/
	 * @return
	 */
	private String getFastaPath() {
		String fastaFilePath = null;
		// Get Fasta path to store the amino acid sequence
		if (((Boolean)databasePnl.getMascotParameterMap().get("useFasta").getValue()).booleanValue()) {
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
