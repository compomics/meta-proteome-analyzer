package de.mpa.client.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import com.jgoodies.looks.HeaderStyle;
import com.jgoodies.looks.Options;

import de.mpa.analysis.UniProtUtilities;
import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.ExportFields;
import de.mpa.client.ui.dialogs.AddFastaDialog;
import de.mpa.client.ui.dialogs.AdvancedSettingsDialog;
import de.mpa.client.ui.dialogs.BlastDialog;
import de.mpa.client.ui.dialogs.ColorsDialog;
import de.mpa.client.ui.dialogs.ExportCombinedExpMetaproteins;
import de.mpa.client.ui.dialogs.ExportDialog;
import de.mpa.client.ui.dialogs.ExportSeparateExpMetaproteins;
import de.mpa.client.ui.dialogs.MetaproteinExportDialog;
import de.mpa.client.ui.dialogs.UpdateNcbiTaxDialog;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.db.DBDumper;
import de.mpa.db.accessor.ProteinAccessor;

/**
 * The main application frame's menu bar.
 * 
 * @author A. Behne
 */
@SuppressWarnings({ "serial", "deprecation" })
public class ClientFrameMenuBar extends JMenuBar {
	
	/**
	 * The client frame instance.
	 */
	private final ClientFrame clientFrame;
	
	/**
	 * Class containing all values for the export checkboxes.
	 */
	private final ExportFields exportFields;

	/**
	 * The 'Export' menu.
	 */
	private JMenu exportMenu;
	
	/**
	 * Constructs the client frame menu bar and initializes the components.
	 */
	public ClientFrameMenuBar() {
		clientFrame = ClientFrame.getInstance();
		Client.getInstance();
		this.exportFields = ExportFields.getInstance();
		this.initComponents();
	}
	
	/**
	 * Initializes the components.
	 */
	private void initComponents() {
		putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.SINGLE);

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
		
		// create Dump item
		JMenuItem dumpItem = new JMenuItem();
		dumpItem.setText("Dump Database");
		dumpItem.setIcon(IconConstants.GO_DB_ICON);
		dumpItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				new SwingWorker<Object, Object>() {
					@Override
					protected Object doInBackground() {
						JFileChooser chooser = new ConfirmFileChooser();
						chooser.setCurrentDirectory(new File(ClientFrameMenuBar.this.clientFrame.getLastSelectedFolder()));
						chooser.setFileFilter(new ExtensionFileFilter(".sql", false,
								"MYSQL Script File (*.sql)"));
						chooser.setAcceptAllFileFilterUsed(false);
						int returnVal = chooser.showSaveDialog(ClientFrameMenuBar.this.clientFrame);
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							File selFile = chooser.getSelectedFile();
							if (selFile != null) {
								String filePath = selFile.getPath();
								ClientFrameMenuBar.this.clientFrame.setLastSelectedFolder(selFile.getParent());
								if (!filePath.toLowerCase().endsWith(".sql")) {
									filePath += ".sql";
								}
								//									DBDumper.upgradeDatabase();
								DBDumper.dumpDatabase(filePath);
							}
						}
					return null;
					}
				}.execute();
			}
		});
		
		// create Restore item
		JMenuItem restoreItem = new JMenuItem();
		restoreItem.setText("Restore Database");
		restoreItem.setIcon(IconConstants.SET_DB_ICON);
		restoreItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new SwingWorker<Object, Object>() {
					@Override
					protected Object doInBackground() {
						JFileChooser chooser = new ConfirmFileChooser();
						chooser.setCurrentDirectory(new File(ClientFrameMenuBar.this.clientFrame.getLastSelectedFolder()));
						chooser.setFileFilter(new ExtensionFileFilter(".sql", false,
								"MYSQL Script File (*.sql)"));
						chooser.setAcceptAllFileFilterUsed(false);
						int returnVal = chooser.showOpenDialog(ClientFrameMenuBar.this.clientFrame);
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							File selFile = chooser.getSelectedFile();
							if (selFile != null) {
								ClientFrameMenuBar.this.clientFrame.setLastSelectedFolder(selFile.getParent());
								}
								try {
									String confirmCode = JOptionPane.showInputDialog(
									        ClientFrame.getInstance(), 
									        "Continuing this process will DELETE ALL DATA in the current database. \n Type \"DELETE THE DATABASE\" to proceed.", 
									        "Warning", 
									        JOptionPane.WARNING_MESSAGE
									    );
									if (confirmCode.equals("DELETE THE DATABASE")) {
										DBDumper.restoreDatabase(selFile.getPath());
									}
								} catch (SQLException | IOException e) {
									e.printStackTrace();
								}
						}
					return null;
					}
				}.execute();
			}
		});
		
		fileMenu.add(dumpItem);
		fileMenu.add(restoreItem);
		fileMenu.addSeparator();
		fileMenu.add(exitItem);

		/* create Settings menu */
		JMenu settingsMenu = new JMenu("Settings");
		
		// create Color Settings item
		JMenuItem colorsItem = new JMenuItem("Color Settings", IconConstants.COLOR_SETTINGS_ICON);
		colorsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				ColorsDialog.getInstance().setVisible(true);
			}
		});

		// create Connection Settings item
		JMenuItem connItem = new JMenuItem("Connection Settings", IconConstants.CONNECT_SMALL_ICON);
		connItem.setEnabled(!Client.isViewer());
		connItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				AdvancedSettingsDialog.showDialog(ClientFrameMenuBar.this.clientFrame, "Connection Settings", true, Client.getInstance().getConnectionParameters());
			}
		});

		settingsMenu.add(colorsItem);
		settingsMenu.addSeparator();
		settingsMenu.add(connItem);

		/* create Export menu */
		this.exportMenu = new JMenu("Export");
		
		JMenuItem mpaItem = new JMenuItem("MPA File...", IconConstants.MPA_SMALL_ICON);
		mpaItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				ClientFrameMenuBar.this.exportMPA();
			}
		});
		
		// Export CSV results
		JMenuItem csvItem = new JMenuItem("CSV File...", IconConstants.EXCEL_EXPORT_ICON);
		csvItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				ClientFrameMenuBar.this.exportCSV();
			}
		});
		
		// Export Multiple Combined results
		JMenuItem MCRItem = new JMenuItem("Multiple combined Results", IconConstants.EXCEL_EXPORT_ICON);
		MCRItem.setName("MCR");
		MCRItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				if (ClientFrame.getInstance().getProjectPanel().getSelectedProject() != null) {
                    ClientFrameMenuBar.this.exportMultipleCombined();
                }
			}
		});
		
		// Export Multiple Separate results
		JMenuItem MSRItem = new JMenuItem("Mutliple separate results", IconConstants.EXCEL_EXPORT_ICON);
		MSRItem.setName("MSR");
		MSRItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				if (ClientFrame.getInstance().getProjectPanel().getSelectedProject() != null) {
                    ClientFrameMenuBar.this.exportMultipleSeparate();
                }
			}
		});

		// Export graphML file
		JMenuItem graphmlItem = new JMenuItem("GraphML File...", IconConstants.GRAPH_ICON);
		graphmlItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				ClientFrameMenuBar.this.exportGraphML();
			}
		});

		this.exportMenu.add(mpaItem);
		this.exportMenu.addSeparator();
		this.exportMenu.add(csvItem);
		this.exportMenu.add(MCRItem);
		this.exportMenu.add(MSRItem);
		this.exportMenu.addSeparator();
		this.exportMenu.add(graphmlItem);

		setExportMenuEnabled(false);

		// Update Menu
		JMenu updateMenu = new JMenu();		
		updateMenu.setText("Update");

		// Find unreferenced UniProt entries in the database and try to fill them
		JMenuItem updateEmptyUniProtItem = new JMenuItem();
		updateEmptyUniProtItem.setText("Update empty UniProt Entries");
		updateEmptyUniProtItem.setIcon(new ImageIcon(this.getClass().getResource("/de/mpa/resources/icons/uniprot16.png")));
		updateEmptyUniProtItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new SwingWorker<Object, Object>() {
					@Override
					protected Object doInBackground() throws SQLException  {
						UniProtUtilities uniprotweb = new UniProtUtilities();
						// find all proteins in the database and pass them to BLAST
						// TODO: fix this method -> tries to find all proteins with upid = -1 (should only check swissprot and trembl)
						List<ProteinAccessor> proteins = ProteinAccessor.getAllProteinsWithoutUniProtEntry(Client.getInstance().getConnection());
						// update uniprot for each proteinaccessor
						for (ProteinAccessor prot : proteins) {
							prot.updateUniprotForProtein(Client.getInstance().getConnection());
						}
						Client.getInstance().firePropertyChange("new message", null, "UPDATING UNIPROT ENTRIES FINISHED");
						Client.getInstance().firePropertyChange("indeterminate", true,	false);
						return null;
					}	
				}.execute();
			}
		});
		
		
		// Find unreferenced UniProt entries in the database and try to BLAST them if necessary
		JMenuItem blastItem = new JMenuItem();
		blastItem.setText("BLAST unknown Hits");
		blastItem.setIcon(new ImageIcon(this.getClass().getResource("/de/mpa/resources/icons/blast16.png")));
		blastItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new BlastDialog(ClientFrameMenuBar.this.clientFrame, "Blast_Dialog");
			}
		});

		// Delete all blast Hits
		JMenuItem blastDeleteItem = new JMenuItem();
		blastDeleteItem.setText("Delete Blast Hits");
		blastDeleteItem.setIcon(new ImageIcon(this.getClass().getResource("/de/mpa/resources/icons/blast16.png")));
		blastDeleteItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String confirmCode = JOptionPane.showInputDialog(
				        ClientFrame.getInstance(), 
				        "Continuing this process will DELETE all BLAST results. \n Type \"DELETE\" to proceed.", 
				        "Warning", 
				        JOptionPane.WARNING_MESSAGE);
				if ("DELETE".equals(confirmCode)) {
					try {						
						UniProtUtilities.deleteblasthits();						
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		
		

		// Help Menu
		JMenuItem updateNcbiTaxItem = new JMenuItem();		
		updateNcbiTaxItem.setText("Update NCBI Taxonomy");
		updateNcbiTaxItem.setIcon(new ImageIcon(this.getClass().getResource("/de/mpa/resources/icons/ncbi16.png")));
		updateNcbiTaxItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new UpdateNcbiTaxDialog(ClientFrameMenuBar.this.clientFrame, "Update NCBI-Taxonomy Dialog");
			}
		});
		
		// Add further FASTA Menu
		JMenuItem addFastaDbItem = new JMenuItem();
		addFastaDbItem.setText("Add Fasta Database");
		addFastaDbItem.setIcon(new ImageIcon(this.getClass().getResource("/de/mpa/resources/icons/database_go16.png")));
		addFastaDbItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			new AddFastaDialog(ClientFrameMenuBar.this.clientFrame, "Add FASTA-Protein-Database");
			}
		});
		
		// Add items to update menu
		updateMenu.add(addFastaDbItem);
		updateMenu.add(updateNcbiTaxItem);
		updateMenu.addSeparator();
		updateMenu.add(updateEmptyUniProtItem);
		updateMenu.addSeparator();
		updateMenu.add(blastItem);
		updateMenu.add(blastDeleteItem);
		
		// Help Menu
		JMenu helpMenu = new JMenu();		
		helpMenu.setText("Help");

		// Help Contents
		JMenuItem helpContentsItem = new JMenuItem();
		helpContentsItem.setText("Help Contents");

		helpContentsItem.setIcon(new ImageIcon(this.getClass().getResource("/de/mpa/resources/icons/help.gif")));
		helpMenu.add(helpContentsItem);
		helpContentsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ClientFrameMenuBar.this.showHelp();
			}
		});
		helpMenu.addSeparator();

		// aboutItem
		JMenuItem aboutItem = new JMenuItem();
		aboutItem.setText("About");		
		aboutItem.setIcon(new ImageIcon(this.getClass().getResource("/de/mpa/resources/icons/about.gif")));
		aboutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ClientFrameMenuBar.this.showAbout();
			}
		});
		helpMenu.add(aboutItem);

		add(fileMenu);
		add(settingsMenu);
		add(this.exportMenu);
		// Add only for the client and not for the viewer
		if (!Client.isViewer()) {
			add(updateMenu);
		}
		add(helpMenu);
	}
	
	/**
	 * Method to open the metaprotein export dialog
	 * @throws SQLException 
	 */
	protected void openMetaproteinExportDialog() throws SQLException {
		new MetaproteinExportDialog(this.clientFrame, "User Export Dialog");
	}
	
	/**
	 * This method opens the export dialog.
	 */
    private void exportMultipleCombined() {
    	new ExportCombinedExpMetaproteins(this.clientFrame, "User Export Dialog");
    }
    
	/**
	 * This method opens the export dialog.
	 */
    private void exportMultipleSeparate() {
    	new ExportSeparateExpMetaproteins(this.clientFrame, "User Export Dialog");
    }

	/**
	 * Executed when the save project button is triggered. Via a file chooser the user can select the destination of the project (MPA) file.
	 */
	private void exportMPA() {
		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				JFileChooser chooser = new ConfirmFileChooser();
				chooser.setCurrentDirectory(new File(ClientFrameMenuBar.this.clientFrame.getLastSelectedFolder()));
				chooser.setFileFilter(Constants.MPA_FILE_FILTER);
				chooser.setAcceptAllFileFilterUsed(false);
				int returnVal = chooser.showSaveDialog(ClientFrameMenuBar.this.clientFrame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File selFile = chooser.getSelectedFile();
					if (selFile != null) {
						String filePath = selFile.getPath();
						ClientFrameMenuBar.this.clientFrame.setLastSelectedFolder(selFile.getParent());
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
    	new ExportDialog(this.clientFrame, "Results Export", true, this.exportFields);
//    	AdvancedSettingsDialog.showDialog(clientFrame, "Export Results to CSV", true, new ResultExportParameters());
    }
    
    /**
     * Executed when the graphML menu item is triggered. Via a file chooser the user can select the destination of the GraphML file.
     */
	private void exportGraphML() {
		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				JFileChooser chooser = new ConfirmFileChooser();
				chooser.setFileFilter(Constants.GRAPHML_FILE_FILTER);
				chooser.setAcceptAllFileFilterUsed(false);
				int returnVal = chooser.showSaveDialog(ClientFrameMenuBar.this.clientFrame);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File selFile = chooser.getSelectedFile();
					if (selFile != null) {
						String filePath = selFile.getPath();
						if (!filePath.toLowerCase().endsWith(".graphml")) {
							filePath += ".graphml";
						}
						Client.getInstance().getGraphDatabaseHandler().exportGraph(new File(filePath));
					}
				}
				return null;
			}
		}.execute();
	}
	
	/**
	 * This method is being executed when the help menu item is selected.
	 */
	private void showHelp() {
		new HtmlFrame(this.clientFrame, this.getClass().getResource("/de/mpa/resources/html/help.html"), "Help");
	}

	/**
	 * The method that builds the about dialog.
	 */
	private void showAbout() {
		StringBuffer tMsg = new StringBuffer();
		tMsg.append("Product Version: " + Constants.APPTITLE + " " + Constants.VER_NUMBER);
		tMsg.append("\n\n");
		tMsg.append("This software is developed by Alexander Behne, Robert Heyer, Fabian Kohrs and Thilo Muth \nat the Otto von Guericke University Magdeburg and the Max Planck Institute for Dynamics of Complex \nTechnical Systems in Magdeburg (Germany).");
		tMsg.append("\n\n");
		tMsg.append("The latest version is available at http://meta-proteome-analyzer.googlecode.com");
		tMsg.append("\n\n");
		tMsg.append("If any questions arise, contact the corresponding author: ");
		tMsg.append("\n");
		tMsg.append("muth@mpi-magdeburg.mpg.de");
		tMsg.append("\n\n");
		JOptionPane.showMessageDialog(this, tMsg,
				"About " + Constants.APPTITLE, JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Sets the enable state of the 'Export' menu.
	 * @param enabled <code>true</code> if enabled, <code>false</code> otherwise
	 */
	public void setExportMenuEnabled(boolean enabled) {
		for (Component comp : this.exportMenu.getMenuComponents()) {
			comp.setEnabled(enabled);
			// export is always enabled
			if (comp.getName() == "MSR") {comp.setEnabled(true);}
			if (comp.getName() == "MCR") {comp.setEnabled(true);}
						
		}
	}	 
}
