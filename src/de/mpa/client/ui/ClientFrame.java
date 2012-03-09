package de.mpa.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
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

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.HeaderStyle;
import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.Options;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.windows.WindowsLookAndFeel;

import de.mpa.algorithms.Protein;
import de.mpa.algorithms.RankedLibrarySpectrum;
import de.mpa.client.Client;
import de.mpa.client.ui.SpectrumTree.TreeType;
import de.mpa.client.ui.panels.ClusterPanel;
import de.mpa.client.ui.panels.DBSearchPanel;
import de.mpa.client.ui.panels.DbSearchResultPanel;
import de.mpa.client.ui.panels.DeNovoResultPanel;
import de.mpa.client.ui.panels.DeNovoSearchPanel;
import de.mpa.client.ui.panels.FilePanel;
import de.mpa.client.ui.panels.LoggingPanel;
import de.mpa.client.ui.panels.ProjectPanel;
import de.mpa.client.ui.panels.ProteinResultPanel;
import de.mpa.client.ui.panels.SettingsPanel;
import de.mpa.client.ui.panels.SpecLibSearchPanel;
import de.mpa.db.extractor.SpectralSearchCandidate;
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

	private JPanel projectPnl;

	private Client client;

	private FilePanel filePnl;

	private JPanel resPnl;

	private DeNovoResultPanel denovoResPnl;

	private JPanel res2Pnl;

	private LoggingPanel logPnl;

	private CellConstraints cc;

	public JButton sendBtn;
	private JMenuBar menuBar;
	public boolean connectedToServer = false;

	public  JTable libTbl;
	private Map<String, ArrayList<RankedLibrarySpectrum>> resultMap;

	public Map<String, ArrayList<Long>> specPosMap = new HashMap<String, ArrayList<Long>>(1);

	public MultiPlotPanel mPlot;
	protected ArrayList<RankedLibrarySpectrum> resultList;
	private JPanel lggPnl;

	private SpecLibSearchPanel specLibPnl;
	private JTextField xtandemStatTtf;
	private JTextField cruxStatTtf;
	private JTextField inspectStatTtf;
	private JTextField omssaStatTtf;
	private SpectrumTree queryTree;
	public JTable protTbl;
	public JComboBox spectraCbx;
	public JComboBox spectraCbx2;
	private ClusterPanel clusterPnl;
	protected List<File> chunkedFiles;
	private DbSearchResultPanel dbSearchResultPnl;
	private ProteinResultPanel proteinResultPnl;

	private SettingsPanel setPnl;

	private StatusPanel statusPnl;

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
		cp.add(menuBar, BorderLayout.NORTH);

		JTabbedPane tabPane = new JTabbedPane(JTabbedPane.LEFT);
		tabPane.addTab("Project", projectPnl);
		tabPane.addTab("Input Spectra", filePnl);
//		tabPane.addTab("Spectral Library Search", specLibPnl);
//		tabPane.addTab("MS/MS Database Search", msmsPnl);
//		tabPane.addTab("De-novo Search", denovoPnl);
		tabPane.addTab("Search Settings", setPnl);
		tabPane.addTab("Spectral Search Results", resPnl);
		JTabbedPane resultsTabPane = new JTabbedPane(JTabbedPane.TOP);
		resultsTabPane.addTab("Search View", res2Pnl);
		resultsTabPane.addTab("Protein View", proteinResultPnl);
		tabPane.addTab("Database Search Results", dbSearchResultPnl);
		tabPane.addTab("De novo Results", denovoResPnl);
		tabPane.addTab("Logging", lggPnl);
		tabPane.addTab("Clustering", clusterPnl);
		
		tabPane.setBorder(new ThinBevelBorder(BevelBorder.LOWERED));

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
		if(message.startsWith("X!TANDEM")){
			xtandemStatTtf.setEnabled(true);
			if(message.contains(running)){
				xtandemStatTtf.setText(running);
			} else if(message.contains(finished)){
				xtandemStatTtf.setText(finished);
			}

		} else if(message.startsWith("OMSSA")){
			omssaStatTtf.setEnabled(true);
			if(message.contains(running)){
				omssaStatTtf.setText(running);
			} else if(message.contains(finished)){
				omssaStatTtf.setText(finished);
			}
		} else if(message.startsWith("CRUX")){
			cruxStatTtf.setEnabled(true);
			if(message.contains(running)){
				cruxStatTtf.setText(running);
			} else if(message.contains(finished)){
				cruxStatTtf.setText(finished);
			}
		} else if(message.startsWith("INSPECT")){
			inspectStatTtf.setEnabled(true);
			if(message.contains(running)){
				inspectStatTtf.setText(running);
			} else if(message.contains(finished)){
				inspectStatTtf.setText(finished);
			}
		} else if(message.startsWith("DBSEARCH")){
//			for (File file : chunkedFiles) {
//				dbSearchResult = client.getDbSearchResult(file);
//				updateDbResultsTable();
//			}
		} else if(message.startsWith("DENOVOSEARCH")){
//			for (File file : chunkedFiles) {
//				denovoSearchResult = client.getDenovoSearchResult(file);
//				updateDenovoResultsTable();
//			}
		}
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

		// Results Panel
		constructSpecResultsPanel();

		// Database search result panel
		dbSearchResultPnl = new DbSearchResultPanel(this);

		// Protein Panel
		proteinResultPnl = new ProteinResultPanel(this);

		// DeNovoResults		
		denovoResPnl = new DeNovoResultPanel(this);

		// Logging panel		
		constructLogPanel();
		
		// fabi's test panel
		clusterPnl = new ClusterPanel(this);
		
	}
	
	/**
	 * Construct the spectral search results panel.
	 */
	private void constructSpecResultsPanel() {

		resPnl = new JPanel();
		resPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu",		// col
										"5dlu, f:p:g, 5dlu"));	// row

		JPanel dispPnl = new JPanel();
		dispPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu",		// col
										 "f:p:g, 5dlu"));		// row
		dispPnl.setBorder(BorderFactory.createTitledBorder("Results"));

		final DefaultMutableTreeNode queryRoot = new DefaultMutableTreeNode(((DefaultMutableTreeNode) filePnl.getCheckBoxTree().getModel().getRoot()).getUserObject());
		queryTree = new SpectrumTree(new DefaultTreeModel(queryRoot), TreeType.RESULT_LIST, frame);
		queryTree.setRowHeight(new JCheckBox().getPreferredSize().height);

		JScrollPane queryScpn = new JScrollPane(queryTree);
		queryScpn.setPreferredSize(new Dimension(200, 400));
		queryScpn.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		JPanel leftPnl = new JPanel();
		leftPnl.setLayout(new FormLayout("p:g",					// col
										 "p, 5dlu, p, f:p:g"));	// row

		JTable leftDummyTable = new JTable(null, new Vector<String>(Arrays.asList(new String[] {"Files"})));
		leftDummyTable.getTableHeader().setReorderingAllowed(false);
		leftDummyTable.getTableHeader().setResizingAllowed(false);
		JScrollPane leftDummyScpn = new JScrollPane(leftDummyTable);
		leftDummyScpn.setPreferredSize(leftDummyTable.getTableHeader().getPreferredSize());

		JLabel leftLbl = new JLabel("<html><font color=#ff0000>Query spectra</font></html>");
		leftLbl.setPreferredSize(new Dimension(leftLbl.getPreferredSize().width,
				new JButton(" ").getPreferredSize().height));
		leftPnl.add(leftLbl, cc.xy(1,1));
		leftPnl.add(leftDummyScpn, cc.xy(1,3));
		leftPnl.add(queryScpn, cc.xy(1,4));

		libTbl = new JTable(new DefaultTableModel() {
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
		
		TableConfig.packColumn(libTbl, 0, 10);
		libTbl.getColumnModel().getColumn(1).setPreferredWidth(1000);
		TableConfig.packColumn(libTbl, 2, 10);

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
								dtm.addRow(new Object[] {++protIndex, annotation.getAccession(), annotation.getDescription()});
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
				if (queryRoot.getChildCount() > 0) {
					// appear busy
					setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					// build first row in CSV-to-be containing library peptide sequence strings
					// grab list of annotated library spectra belonging to experiment
					try {
						ArrayList<SpectralSearchCandidate> candidates = client.getCandidatesFromExperiment(specLibPnl.getExperimentID());
						// substitute 'sequence + spectrum id' with integer indexing
						HashMap<String, Integer> seq2index = new HashMap<String, Integer>(candidates.size());
						// substitute 'sequence + precursor charge' with integer indexing
						HashMap<String, Integer> seq2id = new HashMap<String, Integer>(candidates.size());
						String row = "";
						int index = 0;
						int id = 0;
						for (SpectralSearchCandidate candidate : candidates) {
							seq2index.put(candidate.getSequence() + candidate.getSpectrumID(), index);
							String seq = candidate.getSequence() + candidate.getPrecursorCharge();
							if (!seq2id.containsKey(seq)) { seq2id.put(seq, id++); }
//							row += "\t" + candidate.getSequence() + candidate.getPrecursorCharge();
							row += "\t" + seq2id.get(seq);
							index++;
						}
						FileOutputStream fos = new FileOutputStream(new File("scores.csv"));
						OutputStreamWriter osw = new OutputStreamWriter(fos);
						osw.write(row + "\n");
						// traverse query tree
						DefaultMutableTreeNode leafNode = queryRoot.getFirstLeaf();
						while (leafNode != null) {
							MascotGenericFile mgf = queryTree.getSpectrumAt(leafNode);
							String seq = mgf.getTitle();
							seq = seq.substring(0, seq.indexOf(" "));
//							row = mgf.getTitle();
//							row = row.substring(0, row.indexOf(" "));
//							row += mgf.getCharge();
							Integer id2 = seq2id.get(seq + mgf.getCharge());
							row = String.valueOf((id2 == null) ? -1 : id2);
							resultList = resultMap.get(mgf.getTitle());
							int oldIndex = 0;
							for (RankedLibrarySpectrum rankedSpec : resultList) {
								index = seq2index.get(rankedSpec.getSequence() + rankedSpec.getSpectrumID());
								for (int i = oldIndex; i < index; i++) {
									row += "\t" + 0.0;	// pad with zeros for filtered results
								}
								row += "\t" + rankedSpec.getScore();
								oldIndex = index+1;
							}
							for (int i = oldIndex; i < candidates.size(); i++) {
								row += "\t" + 0.0;	// pad with zeros for filtered results
							}
							
							osw.write(row + "\n");
							leafNode = leafNode.getNextLeaf();
						}
						osw.close();
						fos.close();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					// restore cursor
					setCursor(null);
				}
			}
		});

		JPanel libPnl = new JPanel();
		libPnl.setLayout(new FormLayout("l:p:g, r:p",		// col
										"p, 5dlu, f:p:g"));	// row
		libPnl.add(new JLabel("<html><font color=#0000ff>Library spectra</font></html>"), cc.xy(1,1));
		libPnl.add(expBtn, cc.xy(2,1));
		libPnl.add(libScpn, cc.xyw(1,3,2));

		protTbl = new JTable(new DefaultTableModel() {
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
		TableConfig.packColumn(protTbl, 0, 10);
		TableConfig.packColumn(protTbl, 1, 10);
		protTbl.getColumnModel().getColumn(2).setPreferredWidth(1000);

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

		resPnl.add(dispPnl, cc.xy(2,2));
	}
		
	/**
	 * Construct the logging panel.
	 */	
	private void constructLogPanel(){

		// main container for tabbed pane
		lggPnl = new JPanel();
		lggPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu",		// col
				"5dlu, f:p:g, 5dlu"));	// row
		// container for titled border
		JPanel brdPnl = new JPanel();
		brdPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu",		// col
				"3dlu, f:p:g, 5dlu"));	// row
		brdPnl.setBorder(BorderFactory.createTitledBorder("Logging"));

		// actual logging panel
		logPnl = new LoggingPanel();
		logPnl.setPreferredSize(new Dimension(300, 200));

		brdPnl.add(logPnl, cc.xy(2, 2));
		lggPnl.add(brdPnl, cc.xy(2, 2));

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
	 * Method to get spectral library search settings panel.
	 * @return
	 */
	public SpecLibSearchPanel getSpecLibSearchPanel() {
		return specLibPnl;
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
	
}

