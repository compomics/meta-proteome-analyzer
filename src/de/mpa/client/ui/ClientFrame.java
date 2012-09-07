package de.mpa.client.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;

import org.apache.log4j.Logger;

import de.mpa.client.Client;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.client.ui.panels.DbSearchResultPanel;
import de.mpa.client.ui.panels.DeNovoResultPanel;
import de.mpa.client.ui.panels.FilePanel;
import de.mpa.client.ui.panels.LoggingPanel;
import de.mpa.client.ui.panels.ProjectPanel;
import de.mpa.client.ui.panels.ResultsPanel;
import de.mpa.client.ui.panels.SettingsPanel;
import de.mpa.client.ui.panels.SpecLibSearchPanel;
import de.mpa.client.ui.panels.SpecSimResultPanel;
import de.mpa.main.Parameters;

/**
 * <b> ClientFrame </b>
 * <p>
 * 	Represents the main graphical user interface for the MetaProteomeAnalyzer-Client.
 * </p>
 * 
 * @author Alexander Behne, Thilo Muth
 */

public class ClientFrame extends JFrame {

	private static ClientFrame frame;

	/**
	 * The menu bar instance.
	 */
	private ClientFrameMenuBar menuBar;

	private JTabbedPane tabPane;
	
	private JPanel projectPnl = new JPanel();
	private JPanel filePnl = new JPanel();
	private JPanel setPnl = new JPanel();
	private JPanel resPnl = new JPanel();
	private JPanel clusterPnl = new JPanel();
	private JPanel logPnl = new JPanel();
	
	private StatusPanel statusPnl;
	
	public Logger log = Logger.getLogger(getClass());
	
	
	private boolean connectedToServer = false;
	
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
//		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				Client.getInstance().exit();
			}
		});
		frame = this;
		
		// Question to start Viewer or complete Version
		// TODO: export project using different run configurations instead of using this selection dialog
		Object[] options = {"Full MPA","Viewer MPA"};
		int buttonIndex = JOptionPane.showOptionDialog(frame,
				"Which Type of MPA you want to use",
				"MPA", JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				options[1]);
		
		switch (buttonIndex) {
		case -1:
			System.exit(1);
			break;
		case 0:
			Client.getInstance();
			break;
		case 1: // Viewer
			Client.getInstance().setViewer(true);
		}
		

		// Frame size
		this.setMinimumSize(new Dimension(Constants.MAINFRAME_WIDTH, Constants.MAINFRAME_HEIGHT));
		this.setPreferredSize(new Dimension(Constants.MAINFRAME_WIDTH, Constants.MAINFRAME_HEIGHT));	
		this.setResizable(true);

		// Build Components
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
//		ImageIcon treeIcon = new ImageIcon(getClass().getResource("/de/mpa/resources/icons/tree.png"));
		
		ImageIcon[] icons = new ImageIcon[] { projectIcon, addSpectraIcon, settingsIcon, resultsIcon, 
				clusteringIcon, loggingIcon };
		String[] titles = new String[] { "Project", "Input Spectra","Search Settings", "View Results",
				"Clustering", "Logging"};
		Component[] panels = new Component[] { projectPnl, filePnl, setPnl, resPnl, 
				clusterPnl, logPnl};

		// Modify tab pane visuals for this single instance, restore defaults afterwards
		Insets tabInsets = UIManager.getInsets("TabbedPane.tabInsets");
		Insets contentBorderInsets = UIManager.getInsets("TabbedPane.contentBorderInsets");
		UIManager.put("TabbedPane.tabInsets", new Insets(2, 6, 1, 7));
		UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, contentBorderInsets.left, 0, 0));
		tabPane = new JTabbedPane(JTabbedPane.LEFT) {
			@Override
			public void setEnabledAt(int index, boolean enabled) {
				super.setEnabledAt(index, enabled);
				getTabComponentAt(index).setEnabled(enabled);
			}
		};
		UIManager.put("TabbedPane.tabInsets", tabInsets);
		UIManager.put("TabbedPane.contentBorderInsets", contentBorderInsets); 
		
		// Add tabs with rollover-capable tab components
		int maxWidth = 0, maxHeight = 0;
		for (int i = 0; i < panels.length; i++) {
//			tabPane.addTab(titles[i], icons[i], panels[i]);
			tabPane.addTab("", panels[i]);
			JButton tabButton = createTabButton(titles[i], icons[i], tabPane);
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
		
		tabPane.setEnabledAt(4, false);

		// Add components to content pane
		this.setJMenuBar(menuBar);
		cp.add(tabPane);
		cp.add(statusPnl, BorderLayout.SOUTH);
		
		// TODO: notify progress bar for loading paramters.
		Parameters.getInstance();
		
		// Enables Functions for the Viewer
		if (Client.getInstance().isViewer()) {
			// Enables Parts 
			String[] enabledItems = Parameters.getInstance().getEnabledItemsForViewer();
			
			for (int i = 0; i < panels.length; i++) {
				tabPane.setEnabledAt(i, false);
				for (int j = 0; j < enabledItems.length; j++) {
					if (((JButton) tabPane.getTabComponentAt(i)).getText().equals(enabledItems[j])) {
						tabPane.setEnabledAt(i, true);
						break;
					}
				}
			}
			// Enables Server Connections
			menuBar.getSettingsMenu().setEnabled(false);

			tabPane.setSelectedIndex(3);
		}
		
		// Set application icon
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/de/mpa/resources/icons/mpa01.png")));

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
		
		if (!Client.getInstance().isViewer()) {
			// Project panel
			projectPnl = new ProjectPanel();

			// File panel
			filePnl = new FilePanel();

			// Settings Panel
			setPnl = new SettingsPanel();

			// Logging panel		
			logPnl = new LoggingPanel();
		}
		
		// Results Panel
		resPnl = new ResultsPanel();
		
		// Fabi's test panel
//		clusterPnl = new ClusterPanel();
		
	}

	/**
	 * Returns the file selection panel.
	 * @return
	 */
	public FilePanel getFilePanel() {
		return (FilePanel) filePnl;
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
		return (SettingsPanel) setPnl;
	}
	
	/**
	 * Returns the project panel.
	 * @return
	 */
	public ProjectPanel getProjectPanel() {
		return (ProjectPanel) projectPnl;
	}

	/**
	 * Returns the tabbed pane.
	 * @return
	 */
	public JTabbedPane getTabPane() {
		return tabPane;
	}
	
	/**
	 * Returns the results panel.
	 * @return The results panel.
	 */
	public ResultsPanel getResultsPanel() {
		return (ResultsPanel) resPnl;
	}
	
	/**
	 * Returns the database search result panel.
	 * @return The database search result panel.
	 */
	public DbSearchResultPanel getDbSearchResultPanel() {
		return getResultsPanel().getDbSearchResultPanel();
	}
	
	/**
	 * Returns the spectral similarity search result panel.
	 * @return The spectral similarity search result panel.
	 */
	public SpecSimResultPanel getSpectralSimilarityResultPanel() {
		return getResultsPanel().getSpectralSimilarityResultPanel();
	}
	
	/**
	 * Returns the de novo search result panel.
	 * @return The de novo similarity search result panel.
	 */
	public DeNovoResultPanel getDeNovoSearchResultPanel() {
		return getResultsPanel().getDeNovoSearchResultPanel();
	}
	
	/**
	 * Creates a button to be used inside JTabbedPane tabs for rollover effects.	
	 * @param title
	 * @param icon
	 * @return
	 */
	public JButton createTabButton(String title, ImageIcon icon, final JTabbedPane tabPane) {
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
