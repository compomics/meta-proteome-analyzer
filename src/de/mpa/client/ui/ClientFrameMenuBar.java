package de.mpa.client.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.HeaderStyle;
import com.jgoodies.looks.Options;

import de.mpa.client.Client;
import de.mpa.client.settings.ServerConnectionSettings;
import de.mpa.db.DbConnectionSettings;
import de.mpa.io.ResultExporter;

public class ClientFrameMenuBar extends JMenuBar {
	
	private ClientFrame clientFrame;
	private Client client;
	private JTextField srvHostTtf;
	private JTextField srvPortTtf;
	private JTextField dbDriverTtf;
	private JTextField dbLocalUrlTtf;
	private JTextField dbRemoteUrlTtf;
	private JTextField dbPortTtf;
	private JTextField dbUserTtf;
	private JPasswordField dbPassTtf;
	private JLabel dbConnTestLbl;
	private JPanel dbPnl;
	private String lastSelectedFolder = System.getProperty("user.home");
	private JMenuItem exportPeptideItem;
	private JMenuItem exportPSMItem;
	private JMenuItem exportProteinsItem;
	private final int  PROTEIN = 0; 
	private final int PEPTIDE = 1; 
	private final int PSM = 2;
	private JMenuItem exportSelProteinsItem;
	private JMenuItem exportSelPeptideItem;
	private JMenuItem exportSelPSMItem; 
	
	/**
	 * Constructs the client frame menu bar and initializes the components.
	 * @param clientFrame The client frame. 
	 */
	public ClientFrameMenuBar() {
		this.clientFrame = ClientFrame.getInstance();
		this.client = Client.getInstance();
		initComponents();
	}
	
	/**
	 * Initializes the components.
	 */
	private void initComponents() {
		this.putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.SINGLE);

		// File Menu
		JMenu fileMenu = new JMenu();
		JMenuItem newProjectItem = new JMenuItem();
		newProjectItem.setText("New Project");
		newProjectItem.setIcon(new ImageIcon(getClass().getResource("/de/mpa/resources/icons/new_project.gif")));

		JMenuItem openProjectItem = new JMenuItem();
		openProjectItem.setText("Open Project");
		openProjectItem.setIcon(new ImageIcon(getClass().getResource("/de/mpa/resources/icons/open_project.gif")));
		JMenuItem exitItem = new JMenuItem();
		fileMenu.setText("File");

		// exitItem
		exitItem.setText("Exit");
		exitItem.setIcon(new ImageIcon(getClass().getResource("/de/mpa/resources/icons/exit.gif")));
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				client.exit();
			}
		});
		fileMenu.add(newProjectItem);
		fileMenu.add(openProjectItem);
		fileMenu.addSeparator();
		fileMenu.add(exitItem);
		this.add(fileMenu);

		// Settings Menu
		JMenu settingsMenu = new JMenu();

		settingsMenu.setText("Settings");

		JMenuItem databaseItem = new JMenuItem();
		// databaseItem
		databaseItem.setText("Database Connection");

		databaseItem.setIcon(new ImageIcon(getClass().getResource("/de/mpa/resources/icons/database.png")));
		
		dbPnl = constructDbSettingsPanel();

		// action listener for database settings
		databaseItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				displayDbSettings();
			}
		});

		settingsMenu.add(databaseItem);
		settingsMenu.addSeparator();

		// serverItem
		JMenuItem serverItem = new JMenuItem();
		serverItem.setText("Server Configuration");
		serverItem.setIcon(new ImageIcon(getClass().getResource("/de/mpa/resources/icons/server.png")));
		final JPanel srvPnl = constructServerSettingsPanel();
		serverItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ServerConnectionSettings oldSrvSettings = client.getServerSettings();
				int res = JOptionPane.showConfirmDialog(clientFrame, srvPnl, "Server Configuration",
						  JOptionPane.OK_CANCEL_OPTION,
						  JOptionPane.PLAIN_MESSAGE);
				if (res == JOptionPane.OK_OPTION) {
					client.setServerSettings(gatherServerSettings());
				} else {
					client.setServerSettings(oldSrvSettings);
				}
			}
		});

		settingsMenu.add(serverItem);
		this.add(settingsMenu);

		// Export menu
		JMenu exportMenu = new JMenu();
		exportMenu.setText("Export");
		// Export proteins
		exportProteinsItem = new JMenuItem();
		exportProteinsItem.setText("Protein Results");
		exportProteinsItem.setEnabled(false);
		exportProteinsItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				exportResults(PROTEIN);
			}
		});
		exportMenu.add(exportProteinsItem);
		
		//Export peptides
		exportPeptideItem = new JMenuItem();
		exportPeptideItem.setText("Peptide Results");
		exportPeptideItem.setEnabled(false);
		exportPeptideItem.addActionListener(new ActionListener() {
			

			@Override
			public void actionPerformed(ActionEvent evt) {
				exportResults(PEPTIDE);
			}
		});
		exportMenu.add(exportPeptideItem);
		// Export psm
		exportPSMItem = new JMenuItem();
		exportPSMItem.setText("PSM Results");
		exportPSMItem.setEnabled(false);
		exportPSMItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				exportResults(PSM);
			}
		});
		exportMenu.add(exportPSMItem);
		
		// Export selected proteins/ peptides and PSMs TODO
		// Export selected proteins
		exportSelProteinsItem = new JMenuItem();
		exportSelProteinsItem.setText("Selected Protein Results");
		exportSelProteinsItem.setEnabled(false);
		exportSelProteinsItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				exportResults(PROTEIN);
			}
		});
		exportMenu.add(exportSelProteinsItem);
		
		//Export selected peptides
		exportSelPeptideItem = new JMenuItem();
		exportSelPeptideItem.setText("Selected Peptide Results");
		exportSelPeptideItem.setEnabled(false);
		exportSelPeptideItem.addActionListener(new ActionListener() {
			

			@Override
			public void actionPerformed(ActionEvent evt) {
				exportResults(PEPTIDE);
			}
		});
		exportMenu.add(exportSelPeptideItem);
		
		// Export selected psm
		exportSelPSMItem = new JMenuItem();
		exportSelPSMItem.setText("Selected PSM Results");
		exportSelPSMItem.setEnabled(false);
		exportSelPSMItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				exportResults(PSM);
			}
		});
		exportMenu.add(exportSelPSMItem);
		
		
		
		// Add export menu to menubar.
		this.add(exportMenu);
		
		// Help Menu
		JMenu helpMenu = new JMenu();		
		helpMenu.setText("Help");

		// helpContentsItem
		JMenuItem helpContentsItem = new JMenuItem();
		helpContentsItem.setText("Help Contents");

		helpContentsItem.setIcon(new ImageIcon(getClass().getResource("/de/mpa/resources/icons/help.gif")));
		helpMenu.add(helpContentsItem);
		helpContentsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//TODO: helpTriggered();
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
		this.add(helpMenu);
	}

	public void displayDbSettings() {
		DbConnectionSettings oldDbSettings = client.getDbSettings();
		int res = JOptionPane.showConfirmDialog(clientFrame, dbPnl, "Database Settings", 
				  JOptionPane.OK_CANCEL_OPTION,
				  JOptionPane.PLAIN_MESSAGE);
		if (res == JOptionPane.OK_OPTION) {
			client.setDbSettings(gatherDbSettings());	// update settings
		} else {	// cancel option or window close option
			client.setDbSettings(oldDbSettings);	// revert to old settings
		}
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
	 * Method to gather database settings from GUI elements.
	 * @return
	 */
	private DbConnectionSettings gatherDbSettings() {

		DbConnectionSettings dbSettings = new DbConnectionSettings();
		
		dbSettings.setJdbcDriver(dbDriverTtf.getText());
		dbSettings.setUrlLocale(dbLocalUrlTtf.getText());
		dbSettings.setUrlRemote(dbRemoteUrlTtf.getText());
		dbSettings.setPort(dbPortTtf.getText());
		dbSettings.setUsername(dbUserTtf.getText());
		dbSettings.setPassword(new String(dbPassTtf.getPassword()));
		
		return dbSettings;
	}

	/**
	 * Method to construct database settings panel.
	 * @return
	 */
	private JPanel constructDbSettingsPanel() {
		
		final JPanel dbPnl = new JPanel();
		
		CellConstraints cc = new CellConstraints();
		
		dbPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p, 5dlu  ",
				"5dlu, p, 5dlu, t:p, 5dlu, p, 5dlu, t:p, 5dlu, p, 5dlu, t:p, 5dlu, t:p, 5dlu"));
		dbPnl.setBorder(new TitledBorder("Database"));
		dbPnl.add(new JLabel("JDBC driver"),cc.xy(2,2));
		dbPnl.add(new JLabel("URL locale"), cc.xy(2,4));
		dbPnl.add(new JLabel("URL remote"), cc.xy(2,6));
		dbPnl.add(new JLabel("Port"), cc.xy(2,8));
		dbPnl.add(new JLabel("Username"), cc.xy(2,10));
		dbPnl.add(new JLabel("Password"), cc.xy(2,12));

		final DbConnectionSettings dbSettings = client.getDbSettings();

		dbDriverTtf = new JTextField(dbSettings.getJdbcDriver());
		dbPnl.add(dbDriverTtf,cc.xy(4, 2));
		dbLocalUrlTtf = new JTextField(dbSettings.getUrlLocale());
		dbPnl.add(dbLocalUrlTtf,cc.xy(4, 4));
		dbRemoteUrlTtf = new JTextField(dbSettings.getUrlRemote());
		dbPnl.add(dbRemoteUrlTtf,cc.xy(4, 6));
		dbPortTtf = new JTextField(dbSettings.getPort());
		dbPnl.add(dbPortTtf,cc.xy(4, 8));
		dbUserTtf = new JTextField(dbSettings.getUsername());
		dbPnl.add(dbUserTtf,cc.xy(4, 10));
		dbPassTtf = new JPasswordField(dbSettings.getPassword());
		dbPnl.add(dbPassTtf,cc.xy(4, 12));
		dbConnTestLbl = new JLabel("");
		dbPnl.add(dbConnTestLbl,cc.xy(4, 14));
		JButton testConnBtn = new JButton("Test connection");
		// action listener for button "Test connection"
		testConnBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				DbConnectionSettings dbSettings = gatherDbSettings();
				client.setDbSettings(dbSettings);

				// method closes old connection
				try {
					client.closeDBConnection();				
				} catch (Exception e) {
					e.printStackTrace();
				}
				// try new connection				
				try {
					client.initDBConnection();
					dbConnTestLbl.setText("Connection OK");
					dbConnTestLbl.setForeground(Color.GREEN);
				} catch (Exception e) {
					dbConnTestLbl.setText("Connection failed");
					dbConnTestLbl.setForeground(Color.RED);
				}
			}
		});

		dbPnl.add(testConnBtn,cc.xy(2, 14));
		return dbPnl;
	}

	/**
	 * Method to gather server settings from GUI elements.
	 * @return
	 */
	private ServerConnectionSettings gatherServerSettings() {

		ServerConnectionSettings srvSettings = new ServerConnectionSettings();
		
		srvSettings.setHost(srvHostTtf.getText());
		srvSettings.setPort(srvPortTtf.getText());
		
		return srvSettings;
	}
	
	/**
	 * Method to construct server settings panel.
	 * @return
	 */
	private JPanel constructServerSettingsPanel() {

		JPanel srvPnl = new JPanel();
		
		CellConstraints cc = new CellConstraints();

		srvPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p:g, 5dlu",		// col
										"p, 3dlu, p, 3dlu, p, 5dlu"));	// row
		srvPnl.setBorder(new TitledBorder("Server Configuration"));

		ServerConnectionSettings srvSettings = client.getServerSettings();
		
		final JLabel hostLbl = new JLabel("Hostname:"); 
		srvHostTtf = new JTextField(8);
		srvHostTtf.setText(srvSettings.getHost());

		final JLabel portLbl = new JLabel("Port:");
		srvPortTtf = new JTextField(8);
		srvPortTtf.setText(srvSettings.getPort());

		srvPnl.add(hostLbl, cc.xy(2,1));
		srvPnl.add(srvHostTtf, cc.xy(4,1));
		srvPnl.add(portLbl, cc.xy(2,3));
		srvPnl.add(srvPortTtf, cc.xy(4,3));

		JButton startBtn = new JButton("Connect to server");	    
		startBtn.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				client.connect();
				clientFrame.setConnectedToServer(true);
				clientFrame.getSettingsPanel().getProcessButton().setEnabled(true);
				JOptionPane.showMessageDialog(clientFrame, "Web Service @" + srvHostTtf.getText() + ":" + srvPortTtf.getText() + " established.");
			}
		});

		srvPnl.add(startBtn, cc.xyw(2,5,3));
		return srvPnl;
	}
	
	/**
	 * Exports the results.
	 * @param evt The ActionEvent evt
	 */
    private void exportResults(int exportType) {
        JFileChooser chooser = new JFileChooser(lastSelectedFolder);
        chooser.setFileFilter(new CsvFileFilter());
        chooser.setMultiSelectionEnabled(false);
        chooser.setDialogTitle("Export Spectra File Details");
        File selectedFile;

        int returnVal = chooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {

            selectedFile = chooser.getSelectedFile();

            if (!selectedFile.getName().toLowerCase().endsWith(".csv")) {
                selectedFile = new File(selectedFile.getAbsolutePath() + ".csv");
            }

            while (selectedFile.exists()) {
                int option = JOptionPane.showConfirmDialog(this, "The  file " + chooser.getSelectedFile().getName()
                        + " already exists. Replace file?", "Replace File?", JOptionPane.YES_NO_CANCEL_OPTION);

                if (option == JOptionPane.NO_OPTION) {
                    chooser = new JFileChooser(lastSelectedFolder);
                    chooser.setFileFilter(new CsvFileFilter());
                    chooser.setMultiSelectionEnabled(false);
                    chooser.setDialogTitle("Export matchted fragment ions");
                    returnVal = chooser.showSaveDialog(this);

                    if (returnVal == JFileChooser.CANCEL_OPTION) {
                        return;
                    } else {
                        selectedFile = chooser.getSelectedFile();

                        if (!selectedFile.getName().toLowerCase().endsWith(".csv")) {
                            selectedFile = new File(selectedFile.getAbsolutePath() + ".csv");
                        }
                    }
                } else { // YES option
                    break;
                }
            }
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

            try {
                selectedFile = chooser.getSelectedFile();

                if (!selectedFile.getName().toLowerCase().endsWith(".csv")) {
                    selectedFile = new File(selectedFile.getAbsolutePath() + ".csv");
                }

                if (selectedFile.exists()) {
                    selectedFile.delete();
                }
                selectedFile.createNewFile();
                
                switch (exportType) {
				case PROTEIN:
//					ResultExporter.exportProteins(selectedFile.getPath(), client.getDbSearchResult(), clientFrame.getDbSearchResultPanel().getProteinTable());
					break;
				case PEPTIDE:
					ResultExporter.exportPeptides(selectedFile.getPath(), client.getDbSearchResult());
					break;
				case PSM: 
					ResultExporter.exportPSMs(selectedFile.getPath(), client.getDbSearchResult());
					break;
				default:
					break;
				}

                
                lastSelectedFolder = selectedFile.getPath();

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "An error occured when exporting the fragment ion matches.",
                        "Error Exporting Fragment Ion Matches",
                        JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(this, "Successfully exported the fragment ion matches to the file " + selectedFile.getName() + ".", "Export successful!",
    	            JOptionPane.INFORMATION_MESSAGE);
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }
    
    /**
     * Enables the export functionalities.
     * @param enabled The state of the export menu items.
     */
    public void setExportResultsEnabled(boolean enabled){
    	exportProteinsItem.setEnabled(enabled);
    	exportPeptideItem.setEnabled(enabled);
    	exportPSMItem.setEnabled(enabled);
    	exportSelProteinsItem.setEnabled(enabled);
    	exportSelPeptideItem.setEnabled(enabled);
    	exportSelPSMItem.setEnabled(enabled);
    }
}
