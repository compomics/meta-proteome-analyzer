package de.mpa.client.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;

import de.mpa.client.Client;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.client.ui.panels.ClusterPanel;
import de.mpa.client.ui.panels.DbSearchResultPanel;
import de.mpa.client.ui.panels.DeNovoResultPanel;
import de.mpa.client.ui.panels.FilePanel;
import de.mpa.client.ui.panels.LoggingPanel;
import de.mpa.client.ui.panels.ProjectPanel;
import de.mpa.client.ui.panels.SettingsPanel;
import de.mpa.client.ui.panels.SpecLibSearchPanel;
import de.mpa.client.ui.panels.SpecSimResultPanel;
import de.mpa.job.JobStatus;


/**
 * <b> ClientFrame </b>
 * <p>
 * 	Represents the main graphical user interface for the MetaProteomeAnalyzer-Client.
 * </p>
 * 
 * @author Alexander Behne, Thilo Muth
 */

public class ClientFrame extends JFrame {

	public Logger log = Logger.getLogger(getClass());
	private ProjectPanel projectPnl;
	private Client client;
	private FilePanel filePnl;
	private DeNovoResultPanel denovoResPnl;
	private LoggingPanel logPnl;
	public JButton sendBtn;
	private ClientFrameMenuBar menuBar;
	private boolean connectedToServer = false;
	public JXTable protTbl;
	public JComboBox spectraCbx;
	public JComboBox spectraCbx2;
	private ClusterPanel clusterPnl;
	protected List<File> chunkedFiles;
	private DbSearchResultPanel dbSearchResPnl;
	private SpecSimResultPanel specSimResPnl;
	private SettingsPanel setPnl;
	private StatusPanel statusPnl;
	private JTabbedPane tabPane;
	private static ClientFrame frame;

	/**
	 * Returns a client singleton object.
	 * 
	 * @return client Client singleton object
	 */
	public static ClientFrame getInstance() {
		if (frame == null) {
			frame = new ClientFrame();
		}
		return frame;
	}
	
	/**
	 * Constructor for the ClientFrame
	 */
	private ClientFrame() {

		// Application title
		super(Constants.APPTITLE + " " + Constants.VER_NUMBER);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame = this;

		// Frame size
		this.setMinimumSize(new Dimension(Constants.MAINFRAME_WIDTH, Constants.MAINFRAME_HEIGHT));
		this.setPreferredSize(new Dimension(Constants.MAINFRAME_WIDTH, Constants.MAINFRAME_HEIGHT));	
		this.setResizable(true);

		// Get the client instance
		client = Client.getInstance();

		// Init components
		initComponents();
		
		// Get the content pane
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());

		// Store tab icons, titles and corresponding panels in arrays
		ImageIcon projectIcon = new ImageIcon(getClass().getResource("/de/mpa/resources/icons/project.png"));
		ImageIcon addSpectraIcon = new ImageIcon(getClass().getResource("/de/mpa/resources/icons/addspectra.png"));
		ImageIcon settingsIcon = new ImageIcon(getClass().getResource("/de/mpa/resources/icons/settings.png"));
		ImageIcon resultsIcon = new ImageIcon(getClass().getResource("/de/mpa/resources/icons/results.png"));
		ImageIcon clusteringIcon = new ImageIcon(getClass().getResource("/de/mpa/resources/icons/clustering.png"));
		ImageIcon loggingIcon = new ImageIcon(getClass().getResource("/de/mpa/resources/icons/logging.png"));
		
		ImageIcon[] icons = new ImageIcon[] { projectIcon, addSpectraIcon, settingsIcon, resultsIcon, 
				resultsIcon, resultsIcon, clusteringIcon, loggingIcon };
		String[] titles = new String[] { "Project", "Input Spectra","Search Settings", "Spectral Search Results",
				"Database Search Results", "De novo Results", "Clustering", "Logging"};
		Component[] panels = new Component[] { projectPnl, filePnl, setPnl, specSimResPnl, 
				dbSearchResPnl, denovoResPnl, clusterPnl, logPnl};

		// Modify tab pane visuals for this single instance, restore defaults afterwards
		Insets tabInsets = UIManager.getInsets("TabbedPane.tabInsets");
		Insets contentBorderInsets = UIManager.getInsets("TabbedPane.contentBorderInsets");
		UIManager.put("TabbedPane.tabInsets", new Insets(2, 6, 1, 7));
		UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, contentBorderInsets.left, 0, 0));
		tabPane = new JTabbedPane(JTabbedPane.LEFT);
		UIManager.put("TabbedPane.tabInsets", tabInsets);
		UIManager.put("TabbedPane.contentBorderInsets", contentBorderInsets); 
		
		// Add tabs with rollover-capable tab components
		int maxWidth = 0, maxHeight = 0;
		for (int i = 0; i < panels.length; i++) {
//			tabPane.addTab(titles[i], icons[i], panels[i]);
			tabPane.addTab("", panels[i]);
			JButton tabButton = createTabButton(titles[i], icons[i]);
			tabPane.setTabComponentAt(i, tabButton);
			maxWidth = Math.max(maxWidth, tabButton.getPreferredSize().width);
			maxHeight = Math.max(maxHeight, tabButton.getPreferredSize().height);
		}
		// Ensure proper tab component alignment by resizing them w.r.t. the largest component
		for (int i = 0; i < panels.length; i++) {
			tabPane.getTabComponentAt(i).setPreferredSize(new Dimension(maxWidth, maxHeight));
		}
		
		// Add discreet little bevel border
		tabPane.setBorder(new ThinBevelBorder(BevelBorder.LOWERED, new Insets(0, 1, 1, 1)));
		
//		tabPane.setEnabledAt(6, false);

		// Add components to content pane
		this.setJMenuBar(menuBar);
		cp.add(tabPane);
		cp.add(statusPnl, BorderLayout.SOUTH);

		// Register property change listener
		client.addPropertyChangeListener(new PropertyChangeListener() {
			// Update the 
			public void propertyChange(PropertyChangeEvent evt) {
				updateSearchEngineUI(evt.getNewValue().toString());
			}
		});

		// Move frame to center of the screen
		this.pack();
		ScreenConfig.centerInScreen(this);
		this.setVisible(true);
	}

	/**
	 * Initialize the components.
	 */
	private void initComponents() {

		// Menu
		menuBar = new ClientFrameMenuBar();
		
		// Status Bar
		statusPnl = new StatusPanel();

		// Project panel
		projectPnl = new ProjectPanel();

		// File panel
		filePnl = new FilePanel();

		// Settings Panel
		setPnl = new SettingsPanel();

		// Database search result panel
		dbSearchResPnl = new DbSearchResultPanel();
		
		// Spectral similarity search result panel
		specSimResPnl = new SpecSimResultPanel();

		// DeNovoResults		
		denovoResPnl = new DeNovoResultPanel();

		// Logging panel		
		logPnl = new LoggingPanel();
		
		// fabi's test panel
		clusterPnl = new ClusterPanel();
		
	}

	/**
	 * Update the search engine user interface 
	 * whenever a new message comes in.
	 * @param message
	 */
	public void updateSearchEngineUI(String message){
		String finished = JobStatus.FINISHED.toString();
		String running = JobStatus.RUNNING.toString();
		if (message.contains(running)) {
			statusPnl.getCurrentStatusTextField().setText(running);
		} else if (message.contains(finished)) {
			statusPnl.getCurrentStatusTextField().setText(finished);
		}
		
		//appendToLog(message);
//		} else if(message.startsWith("DBSEARCH")){
//			for (File file : chunkedFiles) {
//				dbSearchResult = client.getDbSearchResult(file);
//				updateDbResultsTable();
//			}
//		} else if(message.startsWith("DENOVOSEARCH")){
//			for (File file : chunkedFiles) {
//				denovoSearchResult = client.getDenovoSearchResult(file);
//				updateDenovoResultsTable();
//			}
//		}
	}
	
	/**
	 * Method to append text to the logging panel.
	 * @param str The String to append.
	 */
	public void appendToLog(String str) {
		logPnl.append(str);
	}
	
	/**
	 * Returns the file selection panel.
	 * @return
	 */
	public FilePanel getFilePanel() {
		return filePnl;
	}
	
	/**
	 * Returns the spectral library search settings panel.
	 * @return
	 */
	public SpecLibSearchPanel getSpecLibSearchPanel() {
		return getSettingsPanel().getSpecLibSearchPanel();
	}
	
	/**
	 * Returns the status bar panel.
	 * @return
	 */
	public StatusPanel getStatusBar() {
		return statusPnl;
	}

	public boolean isConnectedToServer() {
		return connectedToServer;
	}

	public void setConnectedToServer(boolean connectedToServer) {
		this.connectedToServer = connectedToServer;
	}

	public SettingsPanel getSettingsPanel() {
		return setPnl;
	}
	
	/**
	 * Returns the project panel.
	 * @return
	 */
	public ProjectPanel getProjectPanel() {
		return projectPnl;
	}

	/**
	 * Returns the tabbed pane.
	 * @return
	 */
	public JTabbedPane getTabPane() {
		return tabPane;
	}
	
	/**
	 * Returns the spectral similarity search result panel.
	 * @return The spectral similarity search result panel.
	 */
	public SpecSimResultPanel getSpectralSimilarityResultPanel() {
		return specSimResPnl;
	}
	
	/**
	 * Returns the database search result panel.
	 * @return The database search result panel.
	 */
	public DbSearchResultPanel getDbSearchResultPanel() {
		return dbSearchResPnl;
	}
	
	/**
	 * Returns the de novo search result panel.
	 * @return The de novo similarity search result panel.
	 */
	public DeNovoResultPanel getDeNovoSearchResultPanel() {
		return denovoResPnl;
	}
	
	/**
	 * Creates a button to be used inside JTabbedPane tabs for rollover effects.	
	 * @param title
	 * @param icon
	 * @return
	 */
	private JButton createTabButton(String title, ImageIcon icon) {
		JButton button = new JButton(title, icon);
		button.setRolloverIcon(IconConstants.createRescaledIcon(icon, 1.1f));
		button.setPressedIcon(IconConstants.createRescaledIcon(icon, 0.8f));
		button.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 5));
		button.setContentAreaFilled(false);
		button.setFocusable(false);
		button.setHorizontalAlignment(SwingConstants.LEFT);
		button.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				forwardEvent(me);
			}
			public void mouseReleased(MouseEvent me) {
				forwardEvent(me);
			}
			public void mouseClicked(MouseEvent me) {
				forwardEvent(me);
			}
			private void forwardEvent(MouseEvent me) {
				tabPane.dispatchEvent(SwingUtilities.convertMouseEvent((Component) me.getSource(), me, tabPane));
			}
		});
		return button;
	}
	
}
