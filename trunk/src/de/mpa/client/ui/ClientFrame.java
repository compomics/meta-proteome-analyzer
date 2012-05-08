package de.mpa.client.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;

import com.jgoodies.looks.HeaderStyle;
import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.Options;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.windows.WindowsLookAndFeel;

import de.mpa.algorithms.RankedLibrarySpectrum;
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
import de.mpa.io.MascotGenericFile;
import de.mpa.job.JobStatus;
import de.mpa.ui.MultiPlotPanel;


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
	private JPanel res2Pnl;
	private LoggingPanel logPnl;
	public JButton sendBtn;
	private ClientFrameMenuBar menuBar;
	private boolean connectedToServer = false;
	public  JXTable libTbl;
	private Map<String, ArrayList<RankedLibrarySpectrum>> resultMap;
	public MultiPlotPanel mPlot;
	private ArrayList<RankedLibrarySpectrum> resultList;
	private SpectrumTree queryTree;
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
	private int rolloverIndex = -1;
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
		frame = this;

		// Frame size
		this.setMinimumSize(new Dimension(Constants.MAINFRAME_WIDTH, Constants.MAINFRAME_HEIGHT));
		this.setPreferredSize(new Dimension(Constants.MAINFRAME_WIDTH, Constants.MAINFRAME_HEIGHT));		

		// Get the client instance
		client = Client.getInstance();

		// Init components
		initComponents();
		
		// Get the content pane
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());		
		this.setJMenuBar(menuBar);

		ImageIcon projectIcon = new ImageIcon(getClass().getResource("/de/mpa/resources/icons/project.png"));
		ImageIcon addSpectraIcon = new ImageIcon(getClass().getResource("/de/mpa/resources/icons/addspectra.png"));
		ImageIcon settingsIcon = new ImageIcon(getClass().getResource("/de/mpa/resources/icons/settings.png"));
		ImageIcon resultsIcon = new ImageIcon(getClass().getResource("/de/mpa/resources/icons/results.png"));
		ImageIcon clusteringIcon = new ImageIcon(getClass().getResource("/de/mpa/resources/icons/clustering.png"));
		ImageIcon loggingIcon = new ImageIcon(getClass().getResource("/de/mpa/resources/icons/logging.png"));
		
		final List<Icon> icons = new ArrayList<Icon>();
		icons.add(projectIcon);
		icons.add(addSpectraIcon);
		icons.add(settingsIcon);
		icons.add(resultsIcon);
		icons.add(resultsIcon);
		icons.add(resultsIcon);
		icons.add(clusteringIcon);
		icons.add(loggingIcon);
		
		final List<Icon> rollovers = new ArrayList<Icon>(icons.size());
		for (Icon icon : icons) {
			rollovers.add(IconConstants.createRescaledIcon((ImageIcon) icon, 1.1f));
		}
		
		Insets oldInsets = UIManager.getInsets("TabbedPane.contentBorderInsets");
		Insets newInsets = new Insets(0, oldInsets.left, 0, 0);
		UIManager.put("TabbedPane.contentBorderInsets", newInsets);
		tabPane = new JTabbedPane(JTabbedPane.LEFT);
		UIManager.put("TabbedPane.contentBorderInsets", oldInsets); 
		
		tabPane.addTab("Project", projectIcon, projectPnl);
		tabPane.addTab("Input Spectra", addSpectraIcon, filePnl);
		tabPane.addTab("Search Settings", settingsIcon, setPnl);
		tabPane.addTab("Spectral Search Results", resultsIcon, specSimResPnl);
		JTabbedPane resultsTabPane = new JTabbedPane(JTabbedPane.TOP);
		resultsTabPane.addTab("Search View", res2Pnl);
		tabPane.addTab("Database Search Results", resultsIcon, dbSearchResPnl);
		tabPane.addTab("De novo Results", resultsIcon, denovoResPnl);
		tabPane.addTab("Clustering", clusteringIcon, clusterPnl);
		tabPane.addTab("Logging", loggingIcon, logPnl);
		tabPane.setBorder(new ThinBevelBorder(BevelBorder.LOWERED, new Insets(0, 1, 1, 1)));
		
		tabPane.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent me) {
				int index = tabPane.indexAtLocation(me.getX(), me.getY());
				if (index != rolloverIndex) {
					if (index != -1) {
						tabPane.setIconAt(index, rollovers.get(index));
					}
					if (rolloverIndex != -1) {
						tabPane.setIconAt(rolloverIndex, icons.get(rolloverIndex));
					}
					rolloverIndex = index;
				}
			}
		});
		tabPane.addMouseListener(new MouseAdapter() {
			public void mouseExited(MouseEvent me) {
				if (rolloverIndex != -1) {
					tabPane.setIconAt(rolloverIndex, icons.get(rolloverIndex));
					rolloverIndex = -1;
				}
			}
		});
		
//		tabPane.setEnabledAt(6, false);

		cp.add(tabPane);
		
		cp.add(statusPnl, BorderLayout.SOUTH);

		// Register the property change listener.
		client.addPropertyChangeListener(new PropertyChangeListener() {

			// Update the 
			public void propertyChange(PropertyChangeEvent evt) {
				updateSearchEngineUI(evt.getNewValue().toString());
			}
		});

		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setResizable(true);
		this.pack();

		// Center in the screen
		ScreenConfig.centerInScreen(this);
		this.setVisible(true);
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
	 * Initialize the components.
	 */
	private void initComponents() {

		// Menu
		menuBar = new ClientFrameMenuBar(this);
		
		// Status Bar
		statusPnl = new StatusPanel(this);

		// Project panel
		projectPnl = new ProjectPanel(this);

		// File panel
		filePnl = new FilePanel(this);

		// Spectral Library Search Panel
//		specLibPnl = new SpecLibSearchPanel(this);

		// MS/MS Database Search Panel
//		msmsPnl = new DBSearchPanel(this);

		//DeNovo
//		denovoPnl = new DeNovoSearchPanel(this);
		
		// Settings Panel
		setPnl = new SettingsPanel(this);

//		// Results Panel
//		constructSpecResultsPanel();

		// Database search result panel
		dbSearchResPnl = new DbSearchResultPanel(this);
		
		// Spectral similarity search result panel
		specSimResPnl = new SpecSimResultPanel(this);

		// DeNovoResults		
		denovoResPnl = new DeNovoResultPanel(this);

		// Logging panel		
		logPnl = new LoggingPanel();
		
		// fabi's test panel
		clusterPnl = new ClusterPanel(this);
		
	}
	
//	private class ExportResultsWorker extends SwingWorker {
//
//		private int maxProgress;
//		private long oldTime;
//		private double meanTimeDelta;
//
//		@Override
//		protected Object doInBackground() throws Exception {
//			DefaultMutableTreeNode queryRoot = (DefaultMutableTreeNode) queryTree.getModel().getRoot();
//			if (queryRoot.getChildCount() > 0) {
//				// appear busy
//				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//				// build first row in CSV-to-be containing library peptide sequence strings
//				// grab list of annotated library spectra belonging to experiment
//				try {
//					List<SpectralSearchCandidate> candidates = client.getCandidatesFromExperiment(getSettingsPanel().getSpecLibSearchPanel().getExperimentID());
//					// substitute 'sequence + spectrum id' with integer indexing
//					HashMap<String, Integer> seq2index = new HashMap<String, Integer>(candidates.size());
//					// substitute 'sequence + precursor charge' with integer indexing
//					HashMap<String, Integer> seq2id = new HashMap<String, Integer>(candidates.size());
//					maxProgress = candidates.size() * resultMap.size();
//					int curProgress = 0;
//					oldTime = System.currentTimeMillis();
//					progressMade(curProgress);
//					StringBuilder sb = new StringBuilder(maxProgress*2);
//					FileOutputStream fos = new FileOutputStream(new File("scores.csv"));
////					GZIPOutputStream gzos = new GZIPOutputStream(fos);
////					ObjectOutputStream oos = new ObjectOutputStream(gzos);
//					OutputStreamWriter osw = new OutputStreamWriter(fos);
////					oos.writeDouble(candidates.size()+1.0);
//					int index = 0;
//					int id = 0;
//					for (SpectralSearchCandidate candidate : candidates) {
//						seq2index.put(candidate.getSequence() + candidate.getLibpectrumID(), index);
//						String seq = candidate.getSequence() + candidate.getPrecursorCharge();
//						if (!seq2id.containsKey(seq)) { seq2id.put(seq, id++); }
//						sb.append("\t" + seq2id.get(seq));
////						oos.writeDouble(seq2id.get(seq));
//						index++;
//					}
//					sb.append("\n");
//					// traverse query tree
//					DefaultMutableTreeNode leafNode = queryRoot.getFirstLeaf();
//					while (leafNode != null) {
//						MascotGenericFile mgf = queryTree.getSpectrumAt(leafNode);
//						String seq = mgf.getTitle();
//						seq = seq.substring(0, seq.indexOf(" "));
//						Integer id2 = seq2id.get(seq + mgf.getCharge());
//						sb.append(String.valueOf((id2 == null) ? -1 : id2));
////						oos.writeDouble((id2 == null) ? -1 : id2);
//						resultList = resultMap.get(mgf.getTitle());
//						int oldIndex = 0;
//						for (RankedLibrarySpectrum rankedSpec : resultList) {
//							index = seq2index.get(rankedSpec.getSequence() + rankedSpec.getSpectrumID());
//							for (int i = oldIndex; i < index; i++) {
//								sb.append("\t" + 0.0);
////								oos.writeDouble(0.0);
//								progressMade(++curProgress);
//							}
//							sb.append("\t" + rankedSpec.getScore());
////							oos.writeDouble(rankedSpec.getScore());
//							progressMade(++curProgress);
//							oldIndex = index+1;
//						}
//						for (int i = oldIndex; i < candidates.size(); i++) {
//							sb.append("\t" + 0.0);	// pad with zeros for filtered results
////							oos.writeDouble(0.0);
//							progressMade(++curProgress);
//						}
//						sb.append("\n");
//						osw.append(sb);
//						osw.flush();
//						sb.setLength(0);
//						leafNode = leafNode.getNextLeaf();
//					}
//					osw.close();
////					oos.close();
//					progressMade(maxProgress);
//				} catch (Exception ex) {
//					GeneralExceptionHandler.showErrorDialog(ex, frame);
//				}
//			}
//			return 0;
//		}
//		
//		@Override
//		protected void done() {
//			// restore cursor
//			setCursor(null);
//		}
//
//		private void progressMade(int curProgress) {
////			double relProgress = curProgress * 100.0 / maxProgress;
////			getStatusBar().getCurrentProgressBar().setValue((int) relProgress);
////			
////			long elapsedTime = System.currentTimeMillis() - startTime;
////			long remainingTime = 0L;
////			if (relProgress > 0.0) {
////				remainingTime = (long) (elapsedTime/relProgress*(100.0-relProgress)/1000.0);
////			}
//			
//			getStatusBar().getCurrentProgressBar().setValue((int) (curProgress*100.0/maxProgress));
//			
//			int remProgress = maxProgress - curProgress;
//			long timeDelta = System.currentTimeMillis() - oldTime;
//			// calculate running mean
//			meanTimeDelta = (curProgress > 0) ?
//					meanTimeDelta*(curProgress-1.0)/curProgress + timeDelta/(double)curProgress : 0.0;
//			long remainingTime = ((long) (remProgress*meanTimeDelta) + 999L) / 1000L;
//			
//			getStatusBar().getTimeLabel().setText(
//					String.format("%02d:%02d:%02d", remainingTime/3600,
//					(remainingTime%3600)/60, remainingTime%60));
//			oldTime += timeDelta;
//		}
//		
//	}
	
	protected void refreshResultsTables(MascotGenericFile mgf) {
		// clear library table
		libTbl.clearSelection();
		DefaultTableModel libTblMdl = (DefaultTableModel) libTbl.getModel();
//		while (libTblMdl.getRowCount() > 0) {
//			libTblMdl.removeRow(0);
//		}
		libTblMdl.setRowCount(0);
		if (mgf != null) {
			// re-populate library table
			resultList = resultMap.get(mgf.getTitle());
			if (resultList != null) {
//				for (int index = 0; index < resultList.size(); index++) {
//					libTblMdl.addRow(new Object[] { index+1,
//							resultList.get(index).getSequence(),
//							resultList.get(index).getScore() } );
//				}
				for (RankedLibrarySpectrum rls : resultList) {
					libTblMdl.addRow(new Object[] { rls.getSpectrumID(),
													rls.getSequence(),
													rls.getScore() } );
				}
			}
			TableConfig.packColumn(libTbl, 0, 10);
			TableConfig.packColumn(libTbl, 2, 10);
		}
		// clear protein annotation table
		DefaultTableModel protTblMdl = (DefaultTableModel) protTbl.getModel();
		protTbl.clearSelection();
		while (protTbl.getRowCount() > 0) {
			protTblMdl.removeRow(0);
		}
		// plot selected spectrum
		mPlot.setFirstSpectrum(mgf);
		mPlot.setSecondSpectrum(null);
		mPlot.repaint();
	}

	/**
	 * This method sets the look&feel for the application.
	 */
	private static void setLookAndFeel() {
		UIManager.put(Options.USE_SYSTEM_FONTS_APP_KEY, Boolean.TRUE);
		Options.setUseSystemFonts(true);
		//Options.setDefaultIconSize(new Dimension(18, 18));
		UIManager.put(Options.HEADER_STYLE_KEY, HeaderStyle.BOTH);
		Options.setPopupDropShadowEnabled(true);
		String lafName = LookUtils.IS_OS_WINDOWS 
				? WindowsLookAndFeel.class.getName()
						: Plastic3DLookAndFeel.class.getName();
				try {
					UIManager.setLookAndFeel(lafName);
				} catch (Exception e) {
					e.printStackTrace();
				}
	}
	
	/**
	 * Method to append text to the logging panel.
	 * @param str The String to append.
	 */
	public void appendToLog(String str) {
		logPnl.append(str);
	}
	
	/**
	 * Method to get client instance.
	 * @return
	 */
	public Client getClient() {
		return client;
	}
	
	/**
	 * Method to get file panel.
	 * @return
	 */
	public FilePanel getFilePanel() {
		return filePnl;
	}
	
	/**
	 * Method to get the spectral library search settings panel.
	 * @return
	 */
	public SpecLibSearchPanel getSpecLibSearchPanel() {
		return getSettingsPanel().getSpecLibSearchPanel();
	}
	
	/**
	 * Returns the query spectrum tree object.
	 * @return queryTree The query spectrum tree object.
	 */
	public SpectrumTree getQueryTree() {
		return queryTree;
	}
	
	/**
	 * Returns the result map for the spectral library search.
	 * @return
	 */
	public Map<String, ArrayList<RankedLibrarySpectrum>> getResultMap() {
		return resultMap;
	}
	
	/**
	 * Sets the result map for the spectral library search.
	 * @return
	 */
	public void setResultMap(Map<String, ArrayList<RankedLibrarySpectrum>> resultMap) {
		this.resultMap = resultMap;
	}
	
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
	 * Returns the menu bar of the client frame.
	 * @return The client frame menubar.
	 */
	public ClientFrameMenuBar getClienFrameMenuBar(){
		return menuBar;
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
}
