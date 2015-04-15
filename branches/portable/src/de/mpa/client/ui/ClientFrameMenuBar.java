package de.mpa.client.ui;

import java.awt.Component;
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

/**
 * The main application frame's menu bar.
 * 
 * @author A. Behne
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
		JMenuItem colorsItem = new JMenuItem("Color Settings", IconConstants.COLOR_SETTINGS_ICON);
		colorsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				ColorsDialog.getInstance().setVisible(true);
			}
		});

		settingsMenu.add(colorsItem);

		/* create Export menu */
		exportMenu = new JMenu("Export");
		
		JMenuItem mpaItem = new JMenuItem("MPA File...", IconConstants.MPA_SMALL_ICON);
		mpaItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				exportMPA();
			}
		});
		
		// Export CSV results
		JMenuItem csvItem = new JMenuItem("CSV File...", IconConstants.EXCEL_EXPORT_ICON);
		csvItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				exportCSV();
			}
		});

		// Export graphML file
		JMenuItem graphmlItem = new JMenuItem("GraphML File...", IconConstants.GRAPH_ICON);
		graphmlItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				exportGraphML();
			}
		});

		exportMenu.add(mpaItem);
		exportMenu.add(csvItem);	
		exportMenu.add(graphmlItem);
		
		this.setExportMenuEnabled(false);
		
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
		this.add(exportMenu);
		this.add(helpMenu);
	}
	
	/**
	 * Executed when the save project button is triggered. Via a file chooser the user can select the destination of the project (MPA) file.
	 */
	private void exportMPA() {
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
    	new ExportDialog(clientFrame, "Results Export", true, exportFields);
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
		tMsg.append("This software is developed by Alexander Behne, Robert Heyer and Thilo Muth \nat the Max Planck Institute for Dynamics of Complex \nTechnical Systems in Magdeburg (Germany).");
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
		for (Component comp : exportMenu.getMenuComponents()) {
			comp.setEnabled(enabled);
		}
	}
    
}
