package de.mpa.client.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;

import org.apache.log4j.Logger;

import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.client.ui.panels.ComparePanel;
import de.mpa.client.ui.panels.DbSearchResultPanel;
import de.mpa.client.ui.panels.FilePanel;
import de.mpa.client.ui.panels.GraphDatabaseResultPanel;
import de.mpa.client.ui.panels.LoggingPanel;
import de.mpa.client.ui.panels.ProjectPanel;
import de.mpa.client.ui.panels.ResultsPanel;
import de.mpa.client.ui.panels.SettingsPanel;
import de.mpa.client.ui.panels.SpecSimResultPanel;
import de.mpa.io.ExportHeader;
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

	/**
	 * The singleton instance of this client frame.
	 */
	private static ClientFrame frame;

	/**
	 * The menu bar instance.
	 */
	private ClientFrameMenuBar menuBar;

	/**
	 * The tab pane containing the various panel views of the client user
	 * interface.
	 */
	private JTabbedPane tabPane;
	
	/**
	 * The panel containing project selection and management functionalities.
	 */
	private JPanel projectPnl = new JPanel();
	
	/**
	 * The panel containing file input and pre-processing functionalities.
	 */
	private JPanel filePnl = new JPanel();

	/**
	 * The panel containing controls for editing search engine settings and for
	 * launching searches.
	 */
	private JPanel settingsPnl = new JPanel();
	
	/**
	 * The panel containing search result views.
	 */
	private JPanel resultPnl = new JPanel();
	
//	/**
//	 * The panel containing spectrum clustering functionalities.
//	 */
//	private JPanel clusterPnl = new JPanel();
	
	/**
	 * The panel containing search results comparison functionalities.
	 */
	private JPanel comparePnl = new JPanel();
	
	/**
	 * The panel containing general logging functionalities.
	 */
	private JPanel loggingPnl = new JPanel();
	
	/**
	 * Panel serving as the client frame's status bar.
	 */
	private StatusPanel statusPnl;
	
	/**
	 * The logger instance.
	 */
	public Logger log = Logger.getLogger(getClass());

	/**
	 * Flag indicating whether a connection to a server instance is established.
	 */
	private boolean connectedToServer = false;
	
	/**
	 * The last selected folder.
	 */
	private String lastSelectedFolder = System.getProperty("user.home");
	
	/**
	 * The last selected export headers. Used for the export dialog.
	 */
	private List<ExportHeader> lastSelectedExportHeaders;
	
	/**
	 * Returns the client frame's singleton instance.
	 * 
	 * @return the client frame's singleton instance.
	 */
	public static ClientFrame getInstance() {
		return getInstance(false, false);
	}
	
	/**
	 * Returns the client frame's singleton instance.
	 * @param viewerMode flag indicating whether the client is running in viewer mode.
	 * @return the client frame's singleton instance.
	 */
	public static ClientFrame getInstance(boolean viewerMode, boolean debug) {
		if (frame == null) {
			frame = new ClientFrame(viewerMode, debug);
		}
		return frame;
	}

	public static final int PROJECT_PANEL = 0;
	public static final int INPUT_PANEL = 1;
	public static final int SETTINGS_PANEL = 2;
	public static final int RESULTS_PANEL = 3;
	public static final int COMPARE_PANEL = 4;
	public static final int LOGGING_PANEL = 5;
	
	/**
	 * Constructor for the ClientFrame
	 */
	private ClientFrame(boolean viewerMode, boolean debug) {
		// Configure main frame
		super(Constants.APPTITLE + " " + Constants.VER_NUMBER);
		final Client client = Client.getInstance();
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				client.exit();
			}
		});
		frame = this;
		
		client.setViewer(viewerMode);
		client.setDebug(debug);

		// Frame size
		this.setMinimumSize(new Dimension(Constants.MAINFRAME_WIDTH, Constants.MAINFRAME_HEIGHT));
		this.setPreferredSize(new Dimension(Constants.MAINFRAME_WIDTH, Constants.MAINFRAME_HEIGHT));	
		this.setResizable(true);

		// Build Components
		this.initComponents();
		
		// Get the content pane
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());

		// Store tab icons, titles and corresponding panels in arrays
		ImageIcon[] icons = new ImageIcon[] {
				IconConstants.PROJECT_ICON,
				IconConstants.INPUT_ICON,
				IconConstants.SETTINGS_ICON,
				IconConstants.RESULTS_ICON, 
//				IconConstants.CLUSTERING_ICON,
				IconConstants.COMPARE_ICON,
				IconConstants.LOGGING_ICON
		};
		String[] titles = new String[] {
				"Project",
				"Input Spectra",
				"Search Settings",
				"View Results",
//				"Clustering",
				"Compare Results",
				"Logging"
		};
		Component[] panels = new Component[] {
				projectPnl,
				filePnl,
				settingsPnl,
				resultPnl, 
//				clusterPnl,
				comparePnl,
				loggingPnl
		};

		// Modify tab pane visuals for this single instance, restore defaults afterwards
		Insets tabInsets = UIManager.getInsets("TabbedPane.tabInsets");
		Insets contentBorderInsets = UIManager.getInsets("TabbedPane.contentBorderInsets");
		UIManager.put("TabbedPane.tabInsets", new Insets(2, 6, 1, 7));
		UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, contentBorderInsets.left, 0, 0));
		tabPane = new ButtonTabbedPane(JTabbedPane.LEFT);
		UIManager.put("TabbedPane.tabInsets", tabInsets);
		UIManager.put("TabbedPane.contentBorderInsets", contentBorderInsets); 
		
		// Add tabs with rollover-capable tab components
		int maxWidth = 0, maxHeight = 0;
		for (int i = 0; i < panels.length; i++) {
//			tabPane.addTab(titles[i], icons[i], panels[i]);
//			tabPane.addTab("", panels[i]);
//			JButton tabButton = new TabPaneButton(tabPane, titles[i], icons[i]);
//			tabPane.setTabComponentAt(i, tabButton);
			tabPane.addTab(titles[i], icons[i], panels[i]);
			Component tabButton = tabPane.getTabComponentAt(i);
			maxWidth = Math.max(maxWidth, tabButton.getPreferredSize().width);
			maxHeight = Math.max(maxHeight, tabButton.getPreferredSize().height);
		}
		// Ensure proper tab component alignment by resizing them w.r.t. the largest component
		for (int i = 0; i < panels.length; i++) {
			tabPane.getTabComponentAt(i).setPreferredSize(new Dimension(maxWidth, maxHeight));
		}
		
		// Add discreet little bevel border
		tabPane.setBorder(new ThinBevelBorder(BevelBorder.LOWERED, new Insets(0, 1, 1, 1)));

		for (int i = INPUT_PANEL; i < COMPARE_PANEL; i++) {
			tabPane.setEnabledAt(i, false);
		}

		// Add components to content pane
		this.setJMenuBar(menuBar);
		cp.add(tabPane);
		cp.add(statusPnl, BorderLayout.SOUTH);
		
		// TODO: notify progress bar for loading parameters.
		// TODO: move to Constants class?
		Parameters.getInstance();
		
		// Enables Functions for the Viewer
		if (client.isViewer()) {
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
			settingsPnl = new SettingsPanel();
			// Compare panel
			comparePnl = new ComparePanel();
		}
		// Results Panel
		resultPnl = new ResultsPanel();
		// Logging panel		
		loggingPnl = new LoggingPanel();
		
	}

	/**
	 * Returns the file selection panel.
	 * @return
	 */
	public FilePanel getFilePanel() {
		return (FilePanel) filePnl;
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
		return (SettingsPanel) settingsPnl;
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
	public JTabbedPane getTabbedPane() {
		return tabPane;
	}
	
	/**
	 * General-purpose listener for 'Next' navigation buttons.
	 */
	private ActionListener nextTabListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			nextTab();
		}
	};
	
	/**
	 * General-purpose listener for 'Prev' navigation buttons.
	 */
	private ActionListener prevTabListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			previousTab();
		}
	};
	
	/**
	 * Selects the previous non-disabled tab before the one currently selected
	 * if one exists.
	 */
	public void previousTab() {
		int index = tabPane.getSelectedIndex() - 1;
		while (index >= 0) {
			if (tabPane.isEnabledAt(index)) {
				tabPane.setSelectedIndex(index);
				return;
			}
			index--;
		}
	}

	/**
	 * Selects the next non-disabled tab after the one currently selected if one
	 * exists.
	 */
	public void nextTab() {
		int index = tabPane.getSelectedIndex() + 1;
		while (index < tabPane.getTabCount()) {
			if (tabPane.isEnabledAt(index)) {
				tabPane.setSelectedIndex(index);
				return;
			}
			index++;
		}
	}
	
	/**
	 * Returns the results panel.
	 * @return The results panel.
	 */
	public ResultsPanel getResultsPanel() {
		return (ResultsPanel) resultPnl;
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
	public GraphDatabaseResultPanel getGraphDatabaseResultPanel() {
		return getResultsPanel().getDeNovoSearchResultPanel();
	}
	
	/**
	 * Convenience method to create a navigation button for cycling tab selection.
	 * @param next <code>true</code> if this button shall advance the tab selection 
	 * 				in ascending index order, <code>false</code> otherwise
	 * @param enabled <code>true</code> if this button shall be initially disabled, 
	 * 				<code>false</code> otherwise
	 * @return The created navigation button
	 */
	public JButton createNavigationButton(boolean next, boolean enabled) {
		String text;
		Icon icon, ricon, picon;
		ActionListener al;
		if (next) {
			text = "Next";
			icon = IconConstants.NEXT_ICON;
			ricon = IconConstants.NEXT_ROLLOVER_ICON;
			picon = IconConstants.NEXT_PRESSED_ICON;
			al = nextTabListener;
		} else {
			text = "Prev";
			icon = IconConstants.PREV_ICON;
			ricon = IconConstants.PREV_ROLLOVER_ICON;
			picon = IconConstants.PREV_PRESSED_ICON;
			al = prevTabListener;
		}
		JButton button = new JButton(text, icon);
		button.setRolloverIcon(ricon);
		button.setPressedIcon(picon);
		button.addActionListener(al);
		button.setEnabled(enabled);
		button.setFont(button.getFont().deriveFont(
				Font.BOLD, button.getFont().getSize2D()*1.25f));
		button.setHorizontalTextPosition(SwingConstants.LEFT);
		
		return button;
	}
	
	/**
	 * Returns the last selected folder.
	 * @return The last selected folder
	 */
	public String getLastSelectedFolder() {
		return lastSelectedFolder;
	}
	
	/**
	 * Sets the last selected folder.
	 * @param lastSelectedFolder The last selected folder
	 */
	public void setLastSelectedFolder(String lastSelectedFolder) {
		this.lastSelectedFolder = lastSelectedFolder;
	}
	
	/**
	 * Returns the last selected export headers.
	 * @return The last selected export headers.
	 */
	public List<ExportHeader> getLastSelectedExportHeaders() {
		return lastSelectedExportHeaders;
	}
	
	/**
	 * Sets the last selected export headers.
	 * @param lastSelectedExportHeaders The last selected export headers.
	 */
	public void setLastSelectedExportHeaders(List<ExportHeader> lastSelectedExportHeaders) {
		this.lastSelectedExportHeaders = lastSelectedExportHeaders;
	}
}
