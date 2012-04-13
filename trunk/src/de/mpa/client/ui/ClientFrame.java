package de.mpa.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;

import com.compomics.util.protein.Header;
import com.compomics.util.protein.Protein;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.HeaderStyle;
import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.Options;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.windows.WindowsLookAndFeel;

import de.mpa.algorithms.RankedLibrarySpectrum;
import de.mpa.client.Client;
import de.mpa.client.model.specsim.SpectralSearchCandidate;
import de.mpa.client.ui.SpectrumTree.TreeType;
import de.mpa.client.ui.dialogs.GeneralExceptionHandler;
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
	private ClientFrame frame;
	private ProjectPanel projectPnl;
	private Client client;
	private FilePanel filePnl;
	private JPanel specSimResPnl;
	private DeNovoResultPanel denovoResPnl;
	private JPanel res2Pnl;
	private LoggingPanel logPnl;
	private CellConstraints cc;
	public JButton sendBtn;
	private ClientFrameMenuBar menuBar;
	private boolean connectedToServer = false;
	public  JXTable libTbl;
	private Map<String, ArrayList<RankedLibrarySpectrum>> resultMap;
	public Map<String, ArrayList<Long>> specPosMap = new HashMap<String, ArrayList<Long>>(1);
	public MultiPlotPanel mPlot;
	private ArrayList<RankedLibrarySpectrum> resultList;
	private SpectrumTree queryTree;
	public JXTable protTbl;
	public JComboBox spectraCbx;
	public JComboBox spectraCbx2;
	private ClusterPanel clusterPnl;
	protected List<File> chunkedFiles;
	private DbSearchResultPanel dbSearchResPnl;
	private SettingsPanel setPnl;
	private StatusPanel statusPnl;
	private JTabbedPane tabPane;

	/**
	 * Constructor for the ClientFrame
	 */
	public ClientFrame() {

		// Application title
		super(Constants.APPTITLE + " " + Constants.VER_NUMBER);

		// Frame size
		this.setMinimumSize(new Dimension(Constants.MAINFRAME_WIDTH, Constants.MAINFRAME_HEIGHT));
		this.setPreferredSize(new Dimension(Constants.MAINFRAME_WIDTH, Constants.MAINFRAME_HEIGHT));		
		frame = this;

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
		ImageIcon loggingIcon = new ImageIcon(getClass().getResource("/de/mpa/resources/icons/logging.png"));
		ImageIcon resultsIcon = new ImageIcon(getClass().getResource("/de/mpa/resources/icons/results.png"));
		ImageIcon clusteringIcon = new ImageIcon(getClass().getResource("/de/mpa/resources/icons/clustering.png"));
		
		Insets oldInsets = UIManager.getInsets("TabbedPane.contentBorderInsets");
		Insets newInsets = new Insets(0, oldInsets.left, 0, 0);
		UIManager.put("TabbedPane.contentBorderInsets", newInsets); 
		tabPane = new JTabbedPane(JTabbedPane.LEFT);
		UIManager.put("TabbedPane.contentBorderInsets", oldInsets); 
		
		tabPane.addTab("Project", projectIcon, projectPnl);
		tabPane.addTab("Input Spectra", addSpectraIcon, filePnl);
		tabPane.addTab("Search Settings", settingsIcon, getSettingsPanel());
		tabPane.addTab("Spectral Search Results", resultsIcon, specSimResPnl);
		JTabbedPane resultsTabPane = new JTabbedPane(JTabbedPane.TOP);
		resultsTabPane.addTab("Search View", res2Pnl);
		tabPane.addTab("Database Search Results", resultsIcon, dbSearchResPnl);
		tabPane.addTab("De novo Results", resultsIcon, denovoResPnl);
		tabPane.addTab("Clustering", clusteringIcon, clusterPnl);
		tabPane.addTab("Logging", loggingIcon, logPnl);
		tabPane.setBorder(new ThinBevelBorder(BevelBorder.LOWERED, new Insets(0, 1, 1, 1)));
		
		tabPane.setEnabledAt(6, false);

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

		// Cell constraints
		cc = new CellConstraints();

		// Menu
		menuBar = new ClientFrameMenuBar(this);
		
		// Status Bar
		statusPnl = new StatusPanel(this);

		// File panel
		filePnl = new FilePanel(this);

		// Project panel
		projectPnl = new ProjectPanel(this);

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
	
	/**
	 * Construct the spectral search results panel.
	 */
	private void constructSpecResultsPanel() {

		specSimResPnl = new JPanel();
		specSimResPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu",		// col
										"5dlu, f:p:g, 5dlu"));	// row

		JPanel dispPnl = new JPanel();
		dispPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu",		// col
										 "f:p:g, 5dlu"));		// row
		dispPnl.setBorder(BorderFactory.createTitledBorder("Results"));

		final DefaultMutableTreeNode queryRoot = new DefaultMutableTreeNode(((DefaultMutableTreeNode) filePnl.getCheckBoxTree().getModel().getRoot()).getUserObject());
		queryTree = new SpectrumTree(new DefaultTreeModel(queryRoot), TreeType.RESULT_LIST, frame);
		queryTree.setRowHeight(new JCheckBox().getPreferredSize().height);

		JScrollPane queryScpn = new JScrollPane(queryTree);
		queryScpn.setBorder(null);
		queryScpn.setPreferredSize(new Dimension(200, 400));
		queryScpn.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		JPanel leftPnl = new JPanel();
		leftPnl.setLayout(new FormLayout("p:g",					// col
										 "p, 5dlu, f:p:g"));	// row

		JLabel leftLbl = new JLabel("<html><font color=#ff0000>Query spectra</font></html>");
		leftLbl.setPreferredSize(new Dimension(leftLbl.getPreferredSize().width,
				new JButton(" ").getPreferredSize().height));
		
		JPanel leftInnerPnl = new JPanel(new FormLayout("p", "p, f:p:g"));
		leftInnerPnl.setBorder(new JScrollPane().getBorder());

		JLabel leftHeaderLbl = new JLabel("Files", SwingConstants.CENTER);
		leftHeaderLbl.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(0, 0, 0, (Integer) UIManager.get("ScrollBar.width")), 
				UIManager.getBorder("TableHeader.cellBorder")));
		
		leftInnerPnl.add(leftHeaderLbl, cc.xy(1,1));
		leftInnerPnl.add(queryScpn, cc.xy(1,2));
		
		leftPnl.add(leftLbl, cc.xy(1,1));
		leftPnl.add(leftInnerPnl, cc.xy(1,3));

		libTbl = new JXTable(new DefaultTableModel() {
			// instance initializer block
			{ setColumnIdentifiers(new Object[] {"#","Sequence","Score"}); }
			public boolean isCellEditable(int row, int col) {
				return ((col == 1) ? true : false);
			}
			public Class<?> getColumnClass(int col) {
				switch (col) {
				case 0:
					return Long.class;
				case 1:
					return String.class;
				case 2:
					return Double.class;
				default:
					return getValueAt(0,col).getClass();
				}
			}
		});
		libTbl.setColumnControlVisible(true);
		libTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		libTbl.setAutoCreateRowSorter(true);
		libTbl.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		
		// set sequence column font to mono-spaced for easier fragment length comparison
		libTbl.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
			Font font = new Font(Font.MONOSPACED, Font.PLAIN, 12);
			{ setFont(font); }
			@Override
			public Font getFont() { return font; }
		});

		TableConfig.setColumnWidths(libTbl, new double[] {42, 163, 75});

		// helper text field for use in table cell editor
		JTextField editorTtf = new JTextField();
		editorTtf.setEditable(false);
		Border editorBorder = BorderFactory.createLineBorder(Color.BLACK);
		Border border = UIManager.getBorder("Table.cellNoFocusBorder");
		if (border == null) {
			border = editorBorder;
		} else {
			// use compound with LAF to reduce "jump" text when starting edits
			border = BorderFactory.createCompoundBorder(editorBorder, border);
		}
		editorTtf.setBorder(border);
		libTbl.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(editorTtf));

		// specify number format in score column
		libTbl.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
			private final DecimalFormat formatter = new DecimalFormat( "0.000" );

			public Component getTableCellRendererComponent(
					JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				// First format the cell value as required
				value = formatter.format((Number)value);
				// And pass it on to parent class
				return super.getTableCellRendererComponent(
						table, value, isSelected, hasFocus, row, column );
			}

		});

		libTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					if (libTbl.getSelectedRowCount() > 0) {
						// grab mgf
						int row = libTbl.convertRowIndexToModel(libTbl.getSelectedRow());
//						DefaultTableModel libTblMdl = (DefaultTableModel) libTbl.getModel();
//						int index = (Integer) libTblMdl.getValueAt(row, 0);
						// plot second spectrum
//						mPlot.setSecondSpectrum(resultList.get(index-1).getSpectrumFile());
						mPlot.setSecondSpectrum(resultList.get(row).getSpectrumFile());
						mPlot.repaint();
						// clear protein annotation table
						DefaultTableModel dtm = (DefaultTableModel) protTbl.getModel();
						protTbl.clearSelection();
						while (protTbl.getRowCount() > 0) {
							dtm.removeRow(0);
						}
						// repopulate protein annotation table
//						List<Protein> annotations = resultList.get(index-1).getAnnotations();
						List<Protein> annotations = resultList.get(row).getAnnotations();
						if (annotations != null) {
							int protIndex = 0;
							for (Protein annotation : annotations) {
								Header header = annotation.getHeader();
								dtm.addRow(new Object[] {++protIndex, header.getAccession(), header.getDescription()});
							}
						}
						TableConfig.packColumn(protTbl, 0, 10);
						TableConfig.packColumn(protTbl, 1, 10);
					}
				}
			}
		});

		JScrollPane libScpn = new JScrollPane(libTbl);
		libScpn.setPreferredSize(new Dimension(300, 200));
		libScpn.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
	    // multi-plot panel
		mPlot = new MultiPlotPanel();
		mPlot.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		mPlot.setPreferredSize(new Dimension(350, 200));
		ArrayList<Color> colors = new ArrayList<Color>();
		colors.add(Color.RED);
		colors.add(Color.BLUE);
		mPlot.setLineColors(colors);
		mPlot.setK(20);

		// context menu for multi-plot panel
		final JPopupMenu mPlotPopup = new JPopupMenu();
		final JCheckBoxMenuItem normSepItem = new JCheckBoxMenuItem("Normalize separately", false);
		normSepItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				mPlot.repaint(normSepItem.getState());
			}
		});
		final JCheckBoxMenuItem highlightItem = new JCheckBoxMenuItem("Highlight only 20 most intensive peaks", true);
		highlightItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				mPlot.setK((mPlot.getK() > 0) ? 0 : 20);
				mPlot.repaint();
			}
		});
	    mPlotPopup.add(normSepItem);
	    mPlotPopup.add(highlightItem);
	    
		mPlot.addMouseListener(new MouseAdapter() {
		    public void mousePressed(MouseEvent e) {
		        maybeShowPopup(e);
		    }

		    public void mouseReleased(MouseEvent e) {
		        maybeShowPopup(e);
		    }
			private void maybeShowPopup(MouseEvent e) {
		        if (e.isPopupTrigger()) {
		            mPlotPopup.show(e.getComponent(),
		                       e.getX(), e.getY());
		        }
		    }
		});

		// button to export search result scores
		JButton expBtn = new JButton("export scores");
		expBtn.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				new ExportResultsWorker().execute();
			}
		});

		JPanel libPnl = new JPanel();
		libPnl.setLayout(new FormLayout("l:p:g, r:p",		// col
										"p, 5dlu, f:p:g"));	// row
		libPnl.add(new JLabel("<html><font color=#0000ff>Library spectra</font></html>"), cc.xy(1,1));
		libPnl.add(expBtn, cc.xy(2,1));
		libPnl.add(libScpn, cc.xyw(1,3,2));

		protTbl = new JXTable(new DefaultTableModel() {
			// instance initializer block
			{ setColumnIdentifiers(new Object[] {"#","Accession","Description"}); }
			public boolean isCellEditable(int row, int col) {
				return ((col == 0) ? false : true);
			}
			public Class<?> getColumnClass(int col) {
				switch (col) {
				case 0:
					return Integer.class;
				case 1:
					return String.class;
				case 2:
					return String.class;
				default:
					return getValueAt(0,col).getClass();
				}
			}
		});
		protTbl.setColumnControlVisible(true);
		TableConfig.setColumnWidths(protTbl, new double[] {42, 75, 388});
//		TableConfig.packColumn(protTbl, 0, 10);
//		TableConfig.packColumn(protTbl, 1, 10);
//		protTbl.getColumnModel().getColumn(2).setPreferredWidth(1000);

		protTbl.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(editorTtf));
		protTbl.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(editorTtf));

		JScrollPane protScpn = new JScrollPane(protTbl);
		protScpn.setPreferredSize(new Dimension(300, 200));
		protScpn.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		JPanel protPnl = new JPanel();
		protPnl.setLayout(new FormLayout("p:g",					// col
				"p, 5dlu, f:p:g"));	// row
		JLabel topRightLbl = new JLabel("Protein annotations");
		topRightLbl.setPreferredSize(new Dimension(topRightLbl.getPreferredSize().width,
				new JButton(" ").getPreferredSize().height));
		protPnl.add(topRightLbl, cc.xy(1,1));
		protPnl.add(protScpn, cc.xy(1, 3));

		JSplitPane topRightSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, libPnl, protPnl);
		topRightSplit.setBorder(null);
		topRightSplit.setContinuousLayout(true);
		BasicSplitPaneDivider divider = ((BasicSplitPaneUI) topRightSplit.getUI()).getDivider();
		if (divider != null) { divider.setBorder(null); }

		JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topRightSplit, mPlot);
		rightSplit.setBorder(null);
		rightSplit.setContinuousLayout(true);
		divider = ((BasicSplitPaneUI) rightSplit.getUI()).getDivider();
		if (divider != null) { divider.setBorder(null); }

		JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPnl, rightSplit);
		mainSplit.setBorder(null);
		mainSplit.setContinuousLayout(true);
		divider = ((BasicSplitPaneUI) mainSplit.getUI()).getDivider();
		if (divider != null) { divider.setBorder(null); }

		dispPnl.add(mainSplit, cc.xy(2,1));

		specSimResPnl.add(dispPnl, cc.xy(2,2));
	}
	
	private class ExportResultsWorker extends SwingWorker {

		private int maxProgress;
		private long oldTime;
		private double meanTimeDelta;

		@Override
		protected Object doInBackground() throws Exception {
			DefaultMutableTreeNode queryRoot = (DefaultMutableTreeNode) queryTree.getModel().getRoot();
			if (queryRoot.getChildCount() > 0) {
				// appear busy
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				// build first row in CSV-to-be containing library peptide sequence strings
				// grab list of annotated library spectra belonging to experiment
				try {
					List<SpectralSearchCandidate> candidates = client.getCandidatesFromExperiment(getSettingsPanel().getSpecLibSearchPanel().getExperimentID());
					// substitute 'sequence + spectrum id' with integer indexing
					HashMap<String, Integer> seq2index = new HashMap<String, Integer>(candidates.size());
					// substitute 'sequence + precursor charge' with integer indexing
					HashMap<String, Integer> seq2id = new HashMap<String, Integer>(candidates.size());
					maxProgress = candidates.size() * resultMap.size();
					int curProgress = 0;
					oldTime = System.currentTimeMillis();
					progressMade(curProgress);
					StringBuilder sb = new StringBuilder(maxProgress*2);
					FileOutputStream fos = new FileOutputStream(new File("scores.csv"));
//					GZIPOutputStream gzos = new GZIPOutputStream(fos);
//					ObjectOutputStream oos = new ObjectOutputStream(gzos);
					OutputStreamWriter osw = new OutputStreamWriter(fos);
//					oos.writeDouble(candidates.size()+1.0);
					int index = 0;
					int id = 0;
					for (SpectralSearchCandidate candidate : candidates) {
						seq2index.put(candidate.getSequence() + candidate.getLibpectrumID(), index);
						String seq = candidate.getSequence() + candidate.getPrecursorCharge();
						if (!seq2id.containsKey(seq)) { seq2id.put(seq, id++); }
						sb.append("\t" + seq2id.get(seq));
//						oos.writeDouble(seq2id.get(seq));
						index++;
					}
					sb.append("\n");
					// traverse query tree
					DefaultMutableTreeNode leafNode = queryRoot.getFirstLeaf();
					while (leafNode != null) {
						MascotGenericFile mgf = queryTree.getSpectrumAt(leafNode);
						String seq = mgf.getTitle();
						seq = seq.substring(0, seq.indexOf(" "));
						Integer id2 = seq2id.get(seq + mgf.getCharge());
						sb.append(String.valueOf((id2 == null) ? -1 : id2));
//						oos.writeDouble((id2 == null) ? -1 : id2);
						resultList = resultMap.get(mgf.getTitle());
						int oldIndex = 0;
						for (RankedLibrarySpectrum rankedSpec : resultList) {
							index = seq2index.get(rankedSpec.getSequence() + rankedSpec.getSpectrumID());
							for (int i = oldIndex; i < index; i++) {
								sb.append("\t" + 0.0);
//								oos.writeDouble(0.0);
								progressMade(++curProgress);
							}
							sb.append("\t" + rankedSpec.getScore());
//							oos.writeDouble(rankedSpec.getScore());
							progressMade(++curProgress);
							oldIndex = index+1;
						}
						for (int i = oldIndex; i < candidates.size(); i++) {
							sb.append("\t" + 0.0);	// pad with zeros for filtered results
//							oos.writeDouble(0.0);
							progressMade(++curProgress);
						}
						sb.append("\n");
						osw.append(sb);
						osw.flush();
						sb.setLength(0);
						leafNode = leafNode.getNextLeaf();
					}
					osw.close();
//					oos.close();
					progressMade(maxProgress);
				} catch (Exception ex) {
					GeneralExceptionHandler.showErrorDialog(ex, frame);
				}
			}
			return 0;
		}
		
		@Override
		protected void done() {
			// restore cursor
			setCursor(null);
		}

		private void progressMade(int curProgress) {
//			double relProgress = curProgress * 100.0 / maxProgress;
//			getStatusBar().getCurrentProgressBar().setValue((int) relProgress);
//			
//			long elapsedTime = System.currentTimeMillis() - startTime;
//			long remainingTime = 0L;
//			if (relProgress > 0.0) {
//				remainingTime = (long) (elapsedTime/relProgress*(100.0-relProgress)/1000.0);
//			}
			
			getStatusBar().getCurrentProgressBar().setValue((int) (curProgress*100.0/maxProgress));
			
			int remProgress = maxProgress - curProgress;
			long timeDelta = System.currentTimeMillis() - oldTime;
			// calculate running mean
			meanTimeDelta = (curProgress > 0) ?
					meanTimeDelta*(curProgress-1.0)/curProgress + timeDelta/(double)curProgress : 0.0;
			long remainingTime = ((long) (remProgress*meanTimeDelta) + 999L) / 1000L;
			
			getStatusBar().getTimeLabel().setText(
					String.format("%02d:%02d:%02d", remainingTime/3600,
					(remainingTime%3600)/60, remainingTime%60));
			oldTime += timeDelta;
		}
		
	}

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
	 * Main method ==> Entry point to the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Set the look&feel
		setLookAndFeel();

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new ClientFrame();
			}
		});
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
	public ProjectPanel getProjectPnl() {
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
	 * Returns the database search result panel.
	 * @return The database search result panel.
	 */
	public DbSearchResultPanel getDbSearchResultPnl() {
		return dbSearchResPnl;
	}
}
