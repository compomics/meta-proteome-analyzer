package de.mpa.client.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import com.jgoodies.looks.HeaderStyle;
import com.jgoodies.looks.Options;

import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.ExportFields;
import de.mpa.client.ui.dialogs.ColorsDialog;
import de.mpa.client.ui.dialogs.ExportDialog;
import de.mpa.client.ui.icons.IconConstants;

public class ClientFrameMenuBar extends JMenuBar {
	/**
	 * Client frame instance.
	 */
	private ClientFrame clientFrame;
	
	/**
	 * Client instance.
	 */
	private Client client;
	private JMenuItem exportCSVResultsItem;
	private JMenuItem saveProjectItem;
	private JMenuItem exportGraphMLItem;

	
	/**
	 * Class containing all values for the export checkboxes.
	 */
	private ExportFields exportFields;
	
	/**
	 * Constructs the client frame menu bar and initializes the components.
	 * @param clientFrame The client frame. 
	 */
	public ClientFrameMenuBar() {
		this.clientFrame = ClientFrame.getInstance();
		this.client = Client.getInstance();
		exportFields = ExportFields.getInstance();
		initComponents();
	}
	
	/**
	 * Initializes the components.
	 */
	private void initComponents() {
		this.putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.SINGLE);

		// File Menu
		JMenu fileMenu = new JMenu();
		fileMenu.setText("File");
		JMenuItem newProjectItem = new JMenuItem();
		newProjectItem.setText("New Project");
		newProjectItem.setIcon(IconConstants.ADD_FOLDER_ICON);
		newProjectItem.setEnabled(false);

		JMenuItem openProjectItem = new JMenuItem();
		openProjectItem.setText("Open Project");
		openProjectItem.setIcon(IconConstants.VIEW_FOLDER_ICON);
		openProjectItem.setEnabled(false);
		
		saveProjectItem = new JMenuItem();
		saveProjectItem.setText("Save Project");
		saveProjectItem.setIcon(IconConstants.SAVE_ICON);
		saveProjectItem.setEnabled(false);
		saveProjectItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				saveProjectButtonTriggered();
			}
		});

		// exitItem
		JMenuItem exitItem = new JMenuItem();
		exitItem.setText("Exit");
		exitItem.setIcon(IconConstants.EXIT_ICON);
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Client.exit();
			}
		});
		
		fileMenu.add(newProjectItem);
		fileMenu.add(openProjectItem);
		fileMenu.add(saveProjectItem);
		fileMenu.addSeparator();
		fileMenu.add(exitItem);

		// Settings Menu
		JMenu settingsMenu = new JMenu("Settings");
		
		// Color settings item
		JMenuItem colorsItem = new JMenuItem("Color Settings", IconConstants.COLOR_SETTINGS_ICON);
		colorsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ColorsDialog.getInstance().setVisible(true);
			}
		});

		// Export menu
		JMenu exportMenu = new JMenu();
		exportMenu.setText("Export");
		// Export CSV results
		exportCSVResultsItem = new JMenuItem();
		exportCSVResultsItem.setText("CSV Results");
		exportCSVResultsItem.setIcon(IconConstants.EXCEL_EXPORT_ICON);
		exportCSVResultsItem.setEnabled(false);
		exportCSVResultsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				showExportDialog();
			}
		});
		exportMenu.add(exportCSVResultsItem);	

		exportMenu.addSeparator();

		// Export graphML file
		exportGraphMLItem = new JMenuItem();
		exportGraphMLItem.setText("GraphML File");
		exportGraphMLItem.setIcon(IconConstants.GRAPH_ICON);
		exportGraphMLItem.setEnabled(false);
		exportGraphMLItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				saveGraphMLItemTriggered();
			}
		});
		exportMenu.add(exportGraphMLItem);	
		
		
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
				helpTriggered();
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
		this.add(exportMenu);
		this.add(helpMenu);
	}
	
	/**
	 * This method is being executed when the help menu item is selected.
	 */
	private void helpTriggered() {
		new HtmlFrame(clientFrame, getClass().getResource("/de/mpa/resources/html/help.html"), "Help");
	}
	

	/**
	 * The method that builds the about dialog.
	 */
	private void showAbout() {
		StringBuffer tMsg = new StringBuffer();
		tMsg.append("Product Version: " + Constants.APPTITLE + " " + Constants.VER_NUMBER);
		tMsg.append("\n");
		tMsg.append("\n");
		tMsg.append("This software is developed by Alexander Behne, Robert Heyer and Thilo Muth \nat the Max Planck Institute for Dynamics of Complex \nTechnical Systems in Magdeburg (Germany).");
		tMsg.append("\n");
		tMsg.append("\n");
		tMsg.append("The latest version is available at http://meta-proteome-analyzer.googlecode.com");
		tMsg.append("\n");
		tMsg.append("\n");
		tMsg.append("If any questions arise, contact the corresponding author: ");
		tMsg.append("\n");
		tMsg.append("muth@mpi-magdeburg.mpg.de");
		tMsg.append("\n");
		tMsg.append("\n");
		tMsg.append("");
		tMsg.append("");
		JOptionPane.showMessageDialog(this, tMsg,
				"About " + Constants.APPTITLE, JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * This method opens the export dialog.
	 */
    private void showExportDialog() {
    	new ExportDialog(clientFrame, "Results Export", true, exportFields);
    }
    
    /**
     * Executed when the save project button is triggered. Via a file chooser the user can select the destination of the project (MPA) file.
     */
	private void saveProjectButtonTriggered() {
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
     * Executed when the graphML menu item is triggered. Via a file chooser the user can select the destination of the GraphML file.
     */
	private void saveGraphMLItemTriggered() {
		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				JFileChooser chooser = new ConfirmFileChooser();
				chooser.setFileFilter(Constants.GRAPHML_FILE_FILTER);
				chooser.setAcceptAllFileFilterUsed(false);
				int returnVal = chooser.showSaveDialog(clientFrame);
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
     * Enables the export CSV results function.
     * @param enabled The state of the CSV result export menu item
     */
	public void setExportCSVResultsEnabled(boolean enabled) {
    	exportCSVResultsItem.setEnabled(enabled);
    }
	
    /**
     * Enables the export GraphML function.
     * @param enabled The state of the GraphML export menu item
     */
	public void setExportGraphMLEnabled(boolean enabled) {
    	exportGraphMLItem.setEnabled(enabled);
    }
    
    /**
     * Enables the save project function.
     * @param enabled The state of the save project menu item
     */
	public void setSaveProjectEnabled(boolean enabled) {
    	saveProjectItem.setEnabled(enabled);
    }
}
