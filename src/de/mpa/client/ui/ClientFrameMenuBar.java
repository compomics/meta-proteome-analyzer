package de.mpa.client.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;

import com.jgoodies.looks.HeaderStyle;
import com.jgoodies.looks.Options;

import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.ExportFields;
import de.mpa.client.model.FileExperiment;
import de.mpa.client.model.FileProject;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.ui.dialogs.ColorsDialog;
import de.mpa.client.ui.dialogs.ExportDialog;
import de.mpa.client.ui.dialogs.GeneralDialog;
import de.mpa.client.ui.dialogs.GeneralDialog.DialogType;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.client.ui.panels.ProjectPanel;
import de.mpa.io.GenericContainer;
import de.mpa.io.parser.mzid.MzidParser;
import de.mpa.task.instances.UniProtTask;

/**
 * The main application frame's menu bar.
 * 
 * @author Alexander Behne, Thilo Muth
 */
public class ClientFrameMenuBar extends JMenuBar {
	
	/**
	 * The client frame instance.
	 */
	private ClientFrame clientFrame;
	
	/**
	 * Class containing all values for the export checkboxes.
	 */
	private ExportFields exportFields;

	/**
	 * The 'Export' menu.
	 */
	private JMenu exportMenu;
	
	/**
	 * The 'Import' menu.
	 */
	private JMenu importMenu;

	private JMenuItem updateUniProtItem;

	private JMenuItem importExperimentItem;

	private JMenuItem importMzIdentMLItem;
	
	/**
	 * Constructs the client frame menu bar and initializes the components.
	 * @param clientFrame The client frame. 
	 */
	public ClientFrameMenuBar() {
		this.clientFrame = ClientFrame.getInstance();
		Client.getInstance();
		exportFields = ExportFields.getInstance();
		initComponents();
	}
	
	/**
	 * Initializes the components.
	 */
	private void initComponents() {
		this.putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.SINGLE);

		/* create File Menu */
		JMenu fileMenu = new JMenu();
		fileMenu.setText("File");
		

		
		// create Exit item
		JMenuItem exitItem = new JMenuItem();
		exitItem.setText("Exit");
		exitItem.setIcon(IconConstants.EXIT_ICON);
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				Client.exit();
			}
		});
		
		fileMenu.add(exitItem);

		/* create Settings menu */
		JMenu settingsMenu = new JMenu("Settings");
		
		// create Color Settings item
		JMenuItem colorSettingsItem = new JMenuItem("Color Settings", IconConstants.COLOR_SETTINGS_ICON);
		colorSettingsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				ColorsDialog.getInstance().setVisible(true);
			}
		});

		settingsMenu.add(colorSettingsItem);
		
		importMenu = new JMenu("Import");
		
		// create Import experiment item
		importExperimentItem = new JMenuItem();
		importExperimentItem.setText("MPA Experiment...");
		importExperimentItem.setIcon(IconConstants.IMPORT_ICON);
		importExperimentItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				importExperiment();
			}
		});
		
		importExperimentItem.setEnabled(false);
		importMenu.add(importExperimentItem);
		
		// create Import experiment item
		importMzIdentMLItem = new JMenuItem();
		importMzIdentMLItem.setText("MzIdentML File...");
		importMzIdentMLItem.setIcon(IconConstants.SPECTRUM_ICON);
		importMzIdentMLItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				importMzIdentMLFile();
			}
		});
		importMzIdentMLItem.setEnabled(false);
		importMenu.add(importMzIdentMLItem);
		
		/* create Export menu */
		exportMenu = new JMenu("Export");
		
		JMenuItem exportExperimentItem = new JMenuItem("Export MPA Experiment", IconConstants.MPA_SMALL_ICON);
		exportExperimentItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				exportExperiment();
			}
		});
		
		// Export CSV results
		JMenuItem csvItem = new JMenuItem("Export CSV File", IconConstants.EXCEL_EXPORT_ICON);
		csvItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				exportCSV();
			}
		});

		exportMenu.add(exportExperimentItem);
		exportMenu.add(csvItem);	
		
		// Update Menu
		JMenu updateMenu = new JMenu();		
		updateMenu.setText("Update");
		
		// Find unreferenced UniProt entries in the database and try to fill them
		updateUniProtItem = new JMenuItem();
		updateUniProtItem.setText("Update UniProt Entries");
		updateUniProtItem.setIcon(new ImageIcon(this.getClass().getResource("/de/mpa/resources/icons/uniprot16.png")));
		updateUniProtItem.setEnabled(true);
		updateUniProtItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new SwingWorker<Object, Object>() {
					@Override
					protected Object doInBackground()  {
						
						Client client = Client.getInstance();
						FileExperiment selectedExperiment = ClientFrame.getInstance().getProjectPanel().getSelectedExperiment();
						DbSearchResult dbSearchResult = client.getDatabaseSearchResult();
						Set<String> accessions = dbSearchResult.getProteinHits().keySet();
						for (String accession : accessions) {
							GenericContainer.UniprotQueryProteins.put(accession, null);
						}
						
						// Start the UniProt entry retrieval task.
						UniProtTask uniProtTask = new UniProtTask();
						uniProtTask.run();
						
						client.firePropertyChange("new message", null, "UPDATING UNIPROT ENTRIES FINISHED");
						client.firePropertyChange("indeterminate", true,	false);
						
						// Updating the result set
						for (String accession : accessions) {
							selectedExperiment.updateProteinSearchHit(dbSearchResult, accession);
						}
						
						// Finally dump the new result object.
						try {
							if (selectedExperiment.hasSearchResult()) {
								client.dumpDatabaseSearchResult(dbSearchResult, selectedExperiment.getResultFile().getAbsolutePath());
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
						return null;
					}	
				}.execute();
			}
		});
		updateMenu.add(updateUniProtItem);
		this.setMenuItemsEnabled(false);
		
		// Help Menu
		JMenu helpMenu = new JMenu();		
		helpMenu.setText("Help");

		// Help Contents
		JMenuItem helpContentsItem = new JMenuItem();
		helpContentsItem.setText("Help Contents");

		helpContentsItem.setIcon(new ImageIcon(getClass().getResource("/de/mpa/resources/icons/help.gif")));
		helpMenu.add(helpContentsItem);
		helpContentsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showHelp();
			}
		});
		helpMenu.addSeparator();

		// aboutItem
		JMenuItem aboutItem = new JMenuItem();
		aboutItem.setText("About");
		aboutItem.setIcon(new ImageIcon(getClass().getResource("/de/mpa/resources/icons/about.gif")));
		aboutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showAbout();
			}
		});
		helpMenu.add(aboutItem);
		
		this.add(fileMenu);
		this.add(settingsMenu);
		this.add(importMenu);
		this.add(exportMenu);
		this.add(updateMenu);
		this.add(helpMenu);
	}
	
	/**
	 * This method is executed when the import MzIdentML menu item is selected. 
	 * The user can select the destination of the imported MzIdentML file.
	 */
	private void importMzIdentMLFile() {
		SwingWorker<String, Void> swingWorker = new SwingWorker<String, Void>() {
			@Override
			protected String doInBackground() throws Exception {
				String selectedFilePath = null;
				JFileChooser chooser = new JFileChooser(new File(Constants.PROJECTS_PATH));
				chooser.setFileFilter(Constants.MZID_FILE_FILTER);
				chooser.setMultiSelectionEnabled(false);
				chooser.setApproveButtonText("Open");
				if (chooser.showOpenDialog(ClientFrame.getInstance()) == JFileChooser.APPROVE_OPTION) {
					File selectedFile = chooser.getSelectedFile();
					// Check whether user has approved the file.
					if (selectedFile != null) {
						if (selectedFile.canRead() && selectedFile.exists()) {
							selectedFilePath = selectedFile.toString();
							
							// Get the project panel.
							ProjectPanel projectPanel = ClientFrame.getInstance().getProjectPanel();
							
							// Get the list of existing experiments.							
							FileProject selectedProject = projectPanel.getSelectedProject();
							
							// Step 1: add new experiment to the list.
							FileExperiment experiment;
							experiment = new FileExperiment();
							experiment.setTitle(selectedFile.getName().substring(0, selectedFile.getName().indexOf(".mzid")));
							experiment.setProject(selectedProject);
							experiment.setCreationDate(new Date());
							// Show experiment creation dialog
							GeneralDialog dialog = new GeneralDialog(DialogType.NEW_EXPERIMENT, experiment);
							int res = dialog.showDialog();
							
							// Check if new experiment has been created (setting a title is mandatory).
							if (res == GeneralDialog.RESULT_SAVED) {
								projectPanel.refreshExperimentTable(selectedProject);
								projectPanel.setSelectedExperiment(experiment);
							}
							// Step 2: create project specific folder.
							String experimentPath = Constants.PROJECTS_PATH + File.separator + experiment.getTitle();
							GenericContainer.CurrentExperimentPath = experimentPath;
							File experimentDir = new File(experimentPath);
							if (!experimentDir.exists()) {
								experimentDir.mkdir();
							}
							File resultFile = new File(experimentPath + File.separator + experiment.getTitle() + ".mpa");
							MzidParser parser = new MzidParser(selectedFile);
							parser.parse();
							
							// Store the new experiment file.
							DbSearchResult dbSearchResult = experiment.getSearchResult();
							experiment.setResultFile(resultFile);
							experiment.serialize();
							Client.getInstance().dumpDatabaseSearchResult(dbSearchResult, resultFile.getAbsolutePath());
							
							projectPanel.refreshExperimentTable(selectedProject);
							projectPanel.setSelectedExperiment(experiment);
							projectPanel.setResultsButtonState(true);
						} else {
							JOptionPane.showMessageDialog(ClientFrame.getInstance(),
									"The file does not exist or is not readable.",
									"Selection Error", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
				return selectedFilePath;
			}
		};
		swingWorker.execute();
	}
	
	/**
	 * This method is executed when the import experiment button is triggered. 
	 * The user can select the destination of the imported (MPA) experiment file.
	 */
	private void importExperiment() {
		SwingWorker<String, Void> swingWorker = new SwingWorker<String, Void>() {
			@Override
			protected String doInBackground() throws Exception {
				String selectedFilePath = null;
				JFileChooser chooser = new JFileChooser(new File(Constants.PROJECTS_PATH));
				chooser.setFileFilter(Constants.MPA_FILE_FILTER);
				chooser.setMultiSelectionEnabled(false);
				chooser.setApproveButtonText("Open");
				if (chooser.showOpenDialog(ClientFrame.getInstance()) == JFileChooser.APPROVE_OPTION) {
					File selectedFile = chooser.getSelectedFile();
					// Check whether user has approved the file.
					if (selectedFile != null) {
						if (selectedFile.canRead() && selectedFile.exists()) {
							selectedFilePath = selectedFile.toString();
							
							// Get the project panel.
							ProjectPanel projectPanel = ClientFrame.getInstance().getProjectPanel();
							
							// Get the list of existing experiments.							
							FileProject selectedProject = projectPanel.getSelectedProject();
							
							// Step 1: add new experiment to the list.
							FileExperiment experiment;
							experiment = new FileExperiment();
							experiment.setTitle(selectedFile.getName().substring(0, selectedFile.getName().indexOf(".mpa")));
							experiment.setProject(selectedProject);
							experiment.setCreationDate(new Date());
							// Show experiment creation dialog
							GeneralDialog dialog = new GeneralDialog(DialogType.NEW_EXPERIMENT, experiment);
							int res = dialog.showDialog();
							
							// Check if new experiment has been created (setting a title is mandatory).
							if (res == GeneralDialog.RESULT_SAVED) {
								projectPanel.refreshExperimentTable(selectedProject);
								projectPanel.setSelectedExperiment(experiment);
							}
							// Step 2: create project specific folder.
							String experimentPath = Constants.PROJECTS_PATH + File.separator + experiment.getTitle();
							GenericContainer.CurrentExperimentPath = experimentPath;
							File experimentDir = new File(experimentPath);
							if (!experimentDir.exists()) {
								experimentDir.mkdir();
							}
							
							// Step 3: move result file and include it within the experiment.
							File createdFile = new File(experimentPath + File.separator + experiment.getTitle() + ".mpa");
							if (!createdFile.exists()) {
								Files.copy(selectedFile.toPath(), createdFile.toPath());
							}
							
							// Step 4: update the spectrum file path structure.
							DbSearchResult dbSearchResult = null;
							List<String> spectrumFilePaths = null;
							
							try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new GZIPInputStream(new FileInputStream(createdFile))))) {
								dbSearchResult = (DbSearchResult) ois.readObject();
								spectrumFilePaths = dbSearchResult.getSpectrumFilePaths();
							} catch (Exception e) {
								JXErrorPane.showDialog(ClientFrame.getInstance(), new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
							}
							
							List<String> newSpectrumFilePaths = new ArrayList<>();
							// Iterate over all spectrum file path and copy spectrum files to their new destination.
							for (String spectrumFilePath : spectrumFilePaths) {
								File spectrumFile = new File(spectrumFilePath);
								
								if (!spectrumFile.exists()) {
									spectrumFile = new File(selectedFile.getParentFile().getAbsolutePath() + File.separator + spectrumFile.getName());
								}
								File createdSpectrumFile = new File(experimentPath + File.separator + spectrumFile.getName());
								
								// Check whether spectrum file is not already existing
								if (spectrumFile.exists() && !createdSpectrumFile.exists()) {
									Files.copy(spectrumFile.toPath(), createdSpectrumFile.toPath());
								}
								
								if (createdSpectrumFile.exists()) {
									newSpectrumFilePaths.add(createdSpectrumFile.getAbsolutePath());
								}
							}
							if (!newSpectrumFilePaths.isEmpty()) {
								experiment.setSpectrumFilePaths(newSpectrumFilePaths);
							} 
							
							experiment.setResultFile(createdFile);
							experiment.serialize();
							projectPanel.refreshExperimentTable(selectedProject);
							projectPanel.setSelectedExperiment(experiment);
							projectPanel.setResultsButtonState(true);
						} else {
							JOptionPane.showMessageDialog(ClientFrame.getInstance(),
									"The file does not exist or is not readable.",
									"Selection Error", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
				return selectedFilePath;
			}
		};
		swingWorker.execute();
	}
		
	/**
	 * This method is executed when the export experiment button is triggered. 
	 * The user can select the destination of the exported (MPA) experiment file.
	 */
	private void exportExperiment() {
		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				JFileChooser chooser = new ConfirmFileChooser();
				chooser.setCurrentDirectory(new File(clientFrame.getLastSelectedFolder()));
				chooser.setFileFilter(Constants.MPA_FILE_FILTER);
				chooser.setAcceptAllFileFilterUsed(false);
				int returnVal = chooser.showSaveDialog(clientFrame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File selFile = chooser.getSelectedFile();
					if (selFile != null) {
						String filePath = selFile.getPath();
						clientFrame.setLastSelectedFolder(selFile.getParent());
						if (!filePath.toLowerCase().endsWith(".mpa")) {
							filePath += ".mpa";
						}
						Client.getInstance().exportDatabaseSearchResult(filePath);
					}
				}
				return null;
			}
		}.execute();
	}

	/**
	 * This method opens the export dialog.
	 */
    private void exportCSV() {
    	new ExportDialog(clientFrame, "Results Export", true, true, exportFields);
    }
    
	/**
	 * This method is being executed when the help menu item is selected.
	 */
	private void showHelp() {
		new HtmlFrame(clientFrame, getClass().getResource("/de/mpa/resources/html/help.html"), "Help");
	}

	/**
	 * The method that builds the about dialog.
	 */
	private void showAbout() {
		StringBuffer tMsg = new StringBuffer();
		tMsg.append("Product Version: " + Constants.APPTITLE + " " + Constants.VER_NUMBER);
		tMsg.append("\n\n");
		tMsg.append("This software is developed by Thilo Muth at the Robert Koch Institute in Berlin (Germany).");
		tMsg.append("\n\n");
		tMsg.append("The latest version is available here:\nhttp://github.com/compomics/meta-proteome-analyzer");
		tMsg.append("\n\n");
		tMsg.append("If any questions arise, contact the corresponding author: ");
		tMsg.append("\n");
		tMsg.append("mutht@rki.de");
		tMsg.append("\n\n");
		JOptionPane.showMessageDialog(this, tMsg,
				"About " + Constants.APPTITLE, JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Sets the enabled state of the different menu items.
	 * @param enabled <code>true</code> if enabled, <code>false</code> otherwise
	 */
	public void setMenuItemsEnabled(boolean enabled) {
		for (Component comp : exportMenu.getMenuComponents()) {
			comp.setEnabled(enabled);
		}
		updateUniProtItem.setEnabled(enabled);
	}
	
	/**
	 * Sets the enabled state of the import experiment menu item.
	 * @param enabled <code>true</code> if enabled, <code>false</code> otherwise
	 */
	public void setImportExperimentMenuItemEnabled(boolean enabled) {
		importExperimentItem.setEnabled(enabled);
		importMzIdentMLItem.setEnabled(enabled);
	}
}
