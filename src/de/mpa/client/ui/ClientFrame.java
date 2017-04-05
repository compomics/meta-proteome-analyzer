package de.mpa.client.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
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
import de.mpa.client.ui.panels.DbSearchResultPanel;
import de.mpa.client.ui.panels.FilePanel;
import de.mpa.client.ui.panels.GraphDatabaseResultPanel;
import de.mpa.client.ui.panels.LoggingPanel;
import de.mpa.client.ui.panels.ProjectPanel;
import de.mpa.client.ui.panels.ResultsPanel;
import de.mpa.io.ExportHeader;

/**
 * <b> ClientFrame </b>
 * <p>
 * 	Represents the main graphical user interface for the MetaProteomeAnalyzer-Client.
 * </p>
 * 
 * @author Alexander Behne, Thilo Muth
 */

@SuppressWarnings("serial")
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
	private final JTabbedPane tabPane;
	
	/**
	 * The panel containing project selection and management functionalities.
	 */
	private JPanel projectPnl = new JPanel();
	
	/**
	 * The panel containing file input and pre-processing functionalities.
	 */
	private JPanel filePnl = new JPanel();

	/**
	 * The panel containing search result views.
	 */
	private JPanel resultPnl = new JPanel();
	
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
	public Logger log = Logger.getLogger(this.getClass());

	/**
	 * Flag indicating whether a connection to a server instance is established.
	 */
	private boolean connectedToServer;
	
	/**
	 * The last selected folder.
	 */
	private String lastSelectedFolder = System.getProperty("user.home");
	
	/**
	 * The last selected export headers. Used for the export dialog.
	 */
	private List<ExportHeader> lastSelectedExportHeaders;
	
	/** Index for the project panel. */
	public static final int INDEX_PROJECT_PANEL = 0;
	/** Index for the input panel */
	public static final int INDEX_INPUT_PANEL = 1;
	/** Index for the results panel. */
	public static final int INDEX_RESULTS_PANEL = 2;
	/** Index for the logging panel */
	public static final int INDEX_LOGGING_PANEL = 3;
	
	/**
	 * Returns the client frame's singleton instance.
	 * @return the client frame's singleton instance.
	 */
	public static ClientFrame getInstance() {
		return ClientFrame.getInstance(false, false, false);
	}
	
	/**
	 * Returns the client frame's singleton instance.
	 * @param viewerMode flag indicating whether the client is running in viewer mode.
	 * @return the client frame's singleton instance.
	 */
	public static ClientFrame getInstance(boolean viewerMode, boolean debug, boolean fast_results) {
		if (ClientFrame.frame == null) {
            ClientFrame.frame = new ClientFrame(viewerMode, debug, fast_results);
		}
		return ClientFrame.frame;
	}
	
	public void restart() {
        dispose();
        ClientFrame.frame = null;
		getInstance();
	}

	/**
	 * Creates the main application frame using the specified viewer and debug flags.
	 * @param viewer <code>true</code> if the application is to be launched in viewer mode
	 * @param debug <code>true</code> if the application is to be launched in debug mode
	 */
	private ClientFrame(boolean viewer, boolean debug, boolean fast_results) {
		// Configure main frame
		super(Constants.APPTITLE + " " + Constants.VER_NUMBER);
		setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
		Client.init(viewer, debug, fast_results);
        addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				Client.exit();
			}
		});
        ClientFrame.frame = this;
		
		// Frame size
        setMinimumSize(new Dimension(Constants.MAINFRAME_WIDTH, Constants.MAINFRAME_HEIGHT));
        setPreferredSize(new Dimension(Constants.MAINFRAME_WIDTH, Constants.MAINFRAME_HEIGHT));
        setResizable(true);

		// Build Components
        initComponents();
		
		// Get the content pane
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());

		// Store tab icons, titles and corresponding panels in arrays
		ImageIcon[] icons = {
				IconConstants.PROJECT_ICON,
				IconConstants.INPUT_ICON,
				IconConstants.RESULTS_ICON, 
//				IconConstants.RESULTS_ICON,
				IconConstants.LOGGING_ICON
		};
		String[] titles = {
				"Project",
				"Input Spectra",
				"View Results",
//				"Spectrum Results",
				"Logging"
		};
		Component[] panels = {
                this.projectPnl,
                this.filePnl,
                this.resultPnl,
//				spectrumResultPnl,
                this.loggingPnl
		};

		// Modify tab pane visuals for this single instance, restore defaults afterwards
		Insets tabInsets = UIManager.getInsets("TabbedPane.tabInsets");
		Insets contentBorderInsets = UIManager.getInsets("TabbedPane.contentBorderInsets");
		UIManager.put("TabbedPane.tabInsets", new Insets(2, 6, 1, 7));
		UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, contentBorderInsets.left, 0, 0));
        this.tabPane = new ButtonTabbedPane(JTabbedPane.LEFT);
		UIManager.put("TabbedPane.tabInsets", tabInsets);
		UIManager.put("TabbedPane.contentBorderInsets", contentBorderInsets); 
		
		// Add tabs with rollover-capable tab components
		int maxWidth = 0, maxHeight = 0;
		for (int i = 0; i < panels.length; i++) {
            this.tabPane.addTab(titles[i], icons[i], panels[i]);
			Component tabButton = this.tabPane.getTabComponentAt(i);
			maxWidth = Math.max(maxWidth, tabButton.getPreferredSize().width);
			maxHeight = Math.max(maxHeight, tabButton.getPreferredSize().height);
		}
		// Ensure proper tab component alignment by resizing them w.r.t. the largest component
		for (int i = 0; i < panels.length; i++) {
            this.tabPane.getTabComponentAt(i).setPreferredSize(new Dimension(maxWidth, maxHeight));
		}
		
		// Add discrete little bevel border
        this.tabPane.setBorder(new ThinBevelBorder(BevelBorder.LOWERED, new Insets(0, 1, 1, 1)));

		for (int i = ClientFrame.INDEX_INPUT_PANEL; i < ClientFrame.INDEX_LOGGING_PANEL; i++) {
            this.tabPane.setEnabledAt(i, false);
		}

		// Add components to content pane
        setJMenuBar(this.menuBar);
		cp.add(this.tabPane);
		cp.add(this.statusPnl, BorderLayout.SOUTH);
		
		// Set application icon
        setIconImage(IconConstants.MPA_ICON.getImage());

		// Move frame to center of the screen
        pack();
		ScreenConfig.centerInScreen(this);
        setVisible(true);
		
		// connect to server
		try {
			Client.getInstance().connectToServer();
		} catch (Exception e1) {
			try {
				Thread.sleep(1000);
				Client.getInstance().connectToServer();
			} catch (Exception e2) {
				e1.printStackTrace();
				e2.printStackTrace();
			}
		}
	}

	/**
	 * Initializes the UI components.
	 */
	private void initComponents() {

		// Menu
        this.menuBar = new ClientFrameMenuBar();
		// Status Bar
        this.statusPnl = new StatusPanel();
		
		// Project panel
        this.projectPnl = new ProjectPanel();
		
		if (!Client.isViewer()) {
			// File panel
            this.filePnl = new FilePanel();
			
//			// Settings Panel
//			settingsPnl = new SettingsPanel();
		}
		// Results Panel
        this.resultPnl = new ResultsPanel();
		// Logging panel		
        this.loggingPnl = new LoggingPanel();
		
		// Spectrum result panel();
//		spectrumResultPnl = new SpectrumResultPanel();
		
	}

	/**
	 * Returns the file selection panel.
	 * @return the file panel
	 */
	public FilePanel getFilePanel() {
		return (FilePanel) this.filePnl;
	}
	
	/**
	 * Returns the status bar panel.
	 * @return the status bar
	 */
	public StatusPanel getStatusBar() {
		return this.statusPnl;
	}
	
	/**
	 * Checks whether the client is connected to the server.
	 * @return true if client is connected to the server else false.
	 */
	public boolean hasServerConnection() {
		return this.connectedToServer;
	}
	
	/**
	 * Sets whether the client is conneted to the server.
	 * @param connectedToServer 
	 */
	public void setServerConnection(boolean connectedToServer) {
		this.connectedToServer = connectedToServer;
	}
	
	/**
	 * Returns the project panel.
	 * @return
	 */
	public ProjectPanel getProjectPanel() {
		return (ProjectPanel) this.projectPnl;
	}

	/**
	 * Sets the enable state of the tab at the specified index.
	 * @param index the tab index
	 * @param enabled the enable state to set
	 */
	public void setTabEnabledAt(int index, boolean enabled) {
        this.tabPane.setEnabledAt(index, enabled);
	}
	
	/**
	 * Returns the enable state of the tab at the specified index.
	 * @param index the tab index
	 * @return <code>true</code> if the tab is enabled, <code>false</code> otherwise
	 */
	public boolean isTabEnabledAt(int index) {
		return this.tabPane.isEnabledAt(index);
	}
	
	/**
	 * Selects the tab at the specified index.
	 * @param index the index of the tab to select
	 */
	public void setTabSelectedIndex(int index) {
        this.tabPane.setSelectedIndex(index);
	}
	
	/**
	 * General-purpose listener for 'Next' navigation buttons.
	 */
	private final ActionListener nextTabListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
            ClientFrame.this.nextTab();
		}
	};
	
	/**
	 * General-purpose listener for 'Prev' navigation buttons.
	 */
	private final ActionListener prevTabListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
            ClientFrame.this.previousTab();
		}
	};
	
	/**
	 * Selects the previous non-disabled tab before the one currently selected
	 * if one exists.
	 */
	public void previousTab() {
		int index = this.tabPane.getSelectedIndex() - 1;
		while (index >= 0) {
			if (this.tabPane.isEnabledAt(index)) {
                this.tabPane.setSelectedIndex(index);
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
		int index = this.tabPane.getSelectedIndex() + 1;
		while (index < this.tabPane.getTabCount()) {
			if (this.tabPane.isEnabledAt(index)) {
                this.tabPane.setSelectedIndex(index);
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
		return (ResultsPanel) this.resultPnl;
	}
	
	/**
	 * Returns the database search result panel.
	 * @return The database search result panel.
	 */
	public DbSearchResultPanel getDbSearchResultPanel() {
		return this.getResultsPanel().getDbSearchResultPanel();
	}
	
	/**
	 * Returns the spectral similarity search result panel.
	 * @return The spectral similarity search result panel.
	 */
//	public SpecSimResultPanel getSpectralSimilarityResultPanel() {
//		return getResultsPanel().getSpectralSimilarityResultPanel();
//	}
	
	/**
	 * Returns the de novo search result panel.
	 * @return The de novo similarity search result panel.
	 */
	public GraphDatabaseResultPanel getGraphDatabaseResultPanel() {
		return this.getResultsPanel().getGraphDatabaseResultPanel();
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
			al = this.nextTabListener;
		} else {
			text = "Prev";
			icon = IconConstants.PREV_ICON;
			ricon = IconConstants.PREV_ROLLOVER_ICON;
			picon = IconConstants.PREV_PRESSED_ICON;
			al = this.prevTabListener;
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
		return this.lastSelectedFolder;
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
		return this.lastSelectedExportHeaders;
	}
	
	/**
	 * Sets the last selected export headers.
	 * @param lastSelectedExportHeaders The last selected export headers.
	 */
	public void setLastSelectedExportHeaders(List<ExportHeader> lastSelectedExportHeaders) {
		this.lastSelectedExportHeaders = lastSelectedExportHeaders;
	}
	
	/**
	 * Returns whether the client application is in viewer mode.
	 * @return <code>true</code> if the client is in viewer mode, <code>false</code> otherwise
	 */
	public boolean isViewer() {
		return Client.isViewer();
	}
	
}