package de.mpa.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import no.uib.jsparklines.extra.HtmlLinksRenderer;

import org.apache.log4j.Logger;

import com.compomics.util.Util;
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
import de.mpa.client.DbSearchSettings;
import de.mpa.client.DenovoSearchSettings;
import de.mpa.client.model.DbSearchResult;
import de.mpa.client.model.DenovoSearchResult;
import de.mpa.client.model.PeptideHit;
import de.mpa.client.model.ProteinHitSet;
import de.mpa.client.ui.SpectrumTree.TreeType;
import de.mpa.client.ui.panels.ClusterPanel;
import de.mpa.client.ui.panels.FilePanel;
import de.mpa.client.ui.panels.SpecLibSearchPanel;
import de.mpa.db.accessor.Cruxhit;
import de.mpa.db.accessor.ExpProperty;
import de.mpa.db.accessor.Experiment;
import de.mpa.db.accessor.Inspecthit;
import de.mpa.db.accessor.Omssahit;
import de.mpa.db.accessor.Pepnovohit;
import de.mpa.db.accessor.Project;
import de.mpa.db.accessor.Property;
import de.mpa.db.accessor.Searchspectrum;
import de.mpa.db.accessor.XTandemhit;
import de.mpa.db.extractor.SpectralSearchCandidate;
import de.mpa.io.MascotGenericFile;
import de.mpa.job.JobStatus;
import de.mpa.ui.MultiPlotPanel;
import de.mpa.webservice.WSPublisher;


/**
 * <b> ClientFrame </b>
 * <p>
 * 	Represents the main graphical user interface for the MetaProteomeAnalyzer-Client.
 * </p>
 * 
 * @author Alexander Behne, Thilo Muth
 */

public class ClientFrame extends JFrame {

	private final static int PORT = 8080;
	private final static String HOST = "0.0.0.0";
	public Logger log = Logger.getLogger(getClass());

	private ClientFrame frame;

	private JPanel projectPnl;

	private JPanel menubarDbPnl;

	private Client client;

	private FilePanel filePnl;

	private JPanel srvPnl;

	private JPanel msmsPnl;

	private JPanel resPnl;

	private JPanel denovoPnl;

	private JPanel denovoResPnl;

	private JPanel res2Pnl;

	private LogPanel logPnl;

	public CellConstraints cc;

//	private List<File> files = new ArrayList<File>();

	private JTextField hostTtf;

	private JTextField portTtf;

	private JButton startBtn;
	public JLabel filesLbl = new JLabel("0 of 0 spectra selected") {
		@Override
		public void repaint() {
			if ((filePnl != null) && (filePnl.getCheckBoxTree() != null)) {
				int selCount = 0;
				int leafCount = 0;
				DefaultMutableTreeNode fileRoot = (DefaultMutableTreeNode) filePnl.getCheckBoxTree().getModel().getRoot();
				if (fileRoot.getChildCount() > 0) {
					selCount = filePnl.getCheckBoxTree().getSelectionModel().getSelectionCount();
					leafCount = ((DefaultMutableTreeNode) filePnl.getCheckBoxTree().getModel().getRoot()).getLeafCount();
				}
				this.setText(selCount + " of " + leafCount + " spectra selected");
			}
		}		
	};
	public JButton sendBtn;
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem exitItem;
	private JProgressBar searchPrg;

	public boolean connectedToServer = false;

	public  JTable libTbl;
	private JTable querySpectraTbl;
	private JTable queryDnSpectraTbl;
	private Map<String, ArrayList<RankedLibrarySpectrum>> resultMap;

	public Map<String, ArrayList<Long>> specPosMap = new HashMap<String, ArrayList<Long>>(1);

	public MultiPlotPanel mPlot;
	protected ArrayList<RankedLibrarySpectrum> resultList;
	private JPanel lggPnl;

	private JButton runDbSearchBtn;
	private SpecLibSearchPanel specLibPnl;
	private JSpinner precTolSpn;
	private JSpinner fragTolSpn;
	private JSpinner missClvSpn;
	private JComboBox enzymeCbx;
	private JCheckBox xTandemCbx;
	private JButton xTandemSetBtn;
	private JCheckBox omssaCbx;
	private JButton omssaSetBtn;
	private JCheckBox inspectCbx;
	private JButton inspectSetBtn;
	private JCheckBox cruxCbx;
	private JButton cruxSetBtn;
	private JComboBox searchTypeCbx;
	private JTextField xtandemStatTtf;
	private JTextField cruxStatTtf;
	private JTextField inspectStatTtf;
	private JTextField omssaStatTtf;
	private JComboBox fastaFileCbx;

	


	private DbSearchResult dbSearchResult;
	private DenovoSearchResult denovoSearchResult;
	private JProgressBar denovoPrg;
	private JSpinner dnFragTolSpn;
	private JComboBox dnEnzymesCbx;
	private JComboBox dnMSCbx;
	private JMenuItem newProjectItem;
	private JMenuItem openProjectItem;
	private JMenu helpMenu;
	private JMenuItem helpContentsItem;
	private JMenuItem aboutItem;
	private JMenu settingsMenu;
	private JMenuItem databaseItem;
	private JMenuItem serverItem;
	private JSpinner dnNumSolutionsSpn;
	private JCheckBox dnPepknownChx;
	private JSpinner dnThresholdSpn;
	private JButton dnStartBtn;
	private JTable dnPTMtbl;
	private SpectrumTree queryTree;
	private JScrollPane querySpectraTblJScrollPane;
	private JTable xTandemTbl;
	private JScrollPane xTandemTblJScrollPane;
	private JTable omssaTbl;
	private JScrollPane omssaTblJScrollPane;
	private JTable cruxTbl;
	private JScrollPane cruxTblJScrollPane;
	private JTable inspectTbl;
	private JScrollPane inspectTblJScrollPane;
	public JTable protTbl;
	private Map<String, List<Omssahit>> ommsaResults;
	private Map<String, List<XTandemhit>> xTandemResults;
	private Map<String, List<Cruxhit>> cruxResults;
	private Map<String, List<Inspecthit>> inspectResults;
	private Map<String, List<Pepnovohit>> pepnovoResults;
	private Map<String, List<PeptideHit>> peptideHits;
	private Map<String, Integer> voteMap;
	public JComboBox spectraCbx;
	public JComboBox spectraCbx2;
	private JTable pepnovoTbl;
	private JTable proteinResultTbl;
	private JPanel proteinViewPnl;
	private JScrollPane proteinTblScp;
	private JScrollPane peptidesTblScp;
	private JTable projectsTbl;
	private JTable projectPropertiesTbl;
	private JTable experimentsNameTbl;
	private JTable experimentPropertiesTbl;
	private ClusterPanel clusterPnl;
	private JPanel dnRSeqPnl;
	private JPanel dnRBlastPnl;
	private JPanel dnRBlastRPnl;
	private JButton dnRStartBLASTBtn;
	private JComboBox dnRBLastCbx;
	private JProgressBar blastsearchPrg;
	private JPanel dnResSpectrumPnl;
	private JScrollPane queryDnSpectraTblJScrollPane;
	private JTable peptideResultTbl;
	private ProteinHitSet proteins;
	protected List<File> chunkedFiles;
	private JButton chunkBtn;


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
		tabPane.addTab("Project",projectPnl);
		tabPane.addTab("Input Spectra", filePnl);
		tabPane.addTab("Server Configuration", srvPnl);
		tabPane.addTab("Spectral Library Search", specLibPnl);
		tabPane.addTab("MS/MS Database Search", msmsPnl);
		tabPane.addTab("De-novo Search", denovoPnl);
		tabPane.addTab("Spectral Search Results", resPnl);
		JTabbedPane resultsTabPane = new JTabbedPane(JTabbedPane.TOP);
		resultsTabPane.addTab("Search View", res2Pnl);
		resultsTabPane.addTab("Protein View", proteinViewPnl);
		tabPane.addTab("Database Search Results", resultsTabPane);
		tabPane.addTab("De novo Results", denovoResPnl);
		tabPane.addTab("Logging", lggPnl);
		tabPane.addTab("Clustering", clusterPnl);

		cp.add(tabPane);

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
			for (File file : chunkedFiles) {
				denovoSearchResult = client.getDenovoSearchResult(file);
				updateDenovoResultsTable();
			}
		}
	}

	/**
	 * Initialize the components.
	 */
	private void initComponents() {

		// Cell constraints
		cc = new CellConstraints();

		// Menu
		constructMenu();

		// File panel
		filePnl = new FilePanel(this);

		// Project panel
		constructProjectPanel();

		// Server panel
		constructServerPanel();

		// Spectral Library Search Panel
		specLibPnl = new SpecLibSearchPanel(this);

		// MS/MS Database Search Panel
		constructDatabaseSearchPanel();

		//DeNovo
		constructDenovoPanel();

		// Results Panel
		constructSpecResultsPanel();

		// MS2 Results Panel
		constructMS2ResultsPanel();

		// Protein Panel
		constructProteinPanel();

		// DeNovoResults
		constructDenovoResultPanel();

		// Logging panel		
		constructLogPanel();
		
		// fabi's test panel
		clusterPnl = new ClusterPanel(this);
		
	}
// for construct panel to recreate tables
	public void recreateTable() {
		((DefaultTableModel)projectsTbl.getModel()).setRowCount(0);
		((DefaultTableModel)projectPropertiesTbl.getModel()).setRowCount(0);
		((DefaultTableModel)experimentsNameTbl.getModel()).setRowCount(0);
		((DefaultTableModel)experimentPropertiesTbl.getModel()).setRowCount(0);
		//refill project Table
		ArrayList<Project> projectList= new ArrayList<Project>(); 
		try {
			client.initDBConnection();
			projectList = new ArrayList<Project>(client.getProjects());
			client.clearDBConnection();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		((DefaultTableModel)projectsTbl.getModel()).addRow(new Object[] {null,"<html><b>NEW PROJECT</b></html>", null});
		for (int i = 0; i < projectList.size(); i++) {
			((DefaultTableModel)projectsTbl.getModel()).addRow(new Object[] {projectList.get(i).getProjectid(),projectList.get(i).getTitle(), projectList.get(i).getCreationdate()});
		}
		// justify column width
		packColumn(projectsTbl,0,5);
		projectsTbl.getColumnModel().getColumn(1).setPreferredWidth(1000);
		packColumn(projectsTbl, 2, 5);
	};
	// Build Panel for project and experiment structure
	private void constructProjectPanel(){
		projectPnl = new JPanel();
		projectPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, t:p,5dlu, t:p, 5dlu, t:p:g, 5dlu"));
		//Projects
		JPanel updateProjectsPnl = new JPanel();
		updateProjectsPnl.setBorder(new TitledBorder("Update to database"));
		updateProjectsPnl.setLayout(new FormLayout("5dlu, p, 5dlu,p,p:g",// Spalten
				"5dlu, p, 5dlu"));			// Zeilen
		JButton refreshBtn= new JButton("refresh");
				refreshBtn.addActionListener(new ActionListener() {
			@Override
			
			public void actionPerformed(ActionEvent e) {
				//delete all Tables
				recreateTable();
			}
		});
		refreshBtn.setPreferredSize(new Dimension(80,30));
		updateProjectsPnl.add(refreshBtn,cc.xy(2, 2));
		JButton saveProjectsBtn = new JButton("save changes");
		saveProjectsBtn.setPreferredSize(new Dimension(80,30));
		updateProjectsPnl.add(saveProjectsBtn,cc.xy(4, 2));
		projectPnl.add(updateProjectsPnl,cc.xy(2, 2));

		JPanel manageProjectsPnl = new JPanel();
		manageProjectsPnl.setBorder(new TitledBorder("Manage projects"));
		manageProjectsPnl.setLayout(new FormLayout("5dlu, p,p:g,5dlu,p, p:g, 5dlu","5dlu, t:p:g, 5dlu, t:p, 5dlu"));
		// Tabel for projects
		projectsTbl = new JTable(new DefaultTableModel(){						// instance initializer block
			{ setColumnIdentifiers(new Object[] {"#","project name","creation date"}); }
			public boolean isCellEditable(int row, int col) {
				return ((col == 1) ? true : false);
//				return false;
			}
			public Class<?> getColumnClass(int col) {
				switch (col) {
				case 0:
					return Integer.class;
				case 1:
					return String.class;
				case 2:
					return Date.class;
				default:
					return getValueAt(0,col).getClass();
				}
			}
		});
		projectsTbl.setAutoCreateRowSorter(true);
		projectsTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//  fill project Table
		ArrayList<Project> projectList= new ArrayList<Project>(); 
		try {
			client.initDBConnection();
			projectList = new ArrayList<Project>(client.getProjects());
			client.clearDBConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		((DefaultTableModel)projectsTbl.getModel()).addRow(new Object[] {null,"<html><b>NEW PROJECT</b></html>", null});
		for (int i = 0; i < projectList.size(); i++) {
			((DefaultTableModel)projectsTbl.getModel()).addRow(new Object[] {projectList.get(i).getProjectid(),projectList.get(i).getTitle(), projectList.get(i).getCreationdate()});
		}
		// justify column width
		packColumn(projectsTbl,0,5);
		projectsTbl.getColumnModel().getColumn(1).setPreferredWidth(1000);
		packColumn(projectsTbl, 2, 5);
		
		JTextField editorTtf = new JTextField();
		editorTtf.setBorder(null);
		DefaultCellEditor dce = new DefaultCellEditor(editorTtf);
		dce.setClickCountToStart(1000);
		projectsTbl.getColumnModel().getColumn(1).setCellEditor(dce);
		// Action listener for project Column
		projectsTbl.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				// if component is enabled and left click and one click--> create other tables
				if (e.getComponent().isEnabled() && e.getButton() == MouseEvent.BUTTON1) {

					Point p = e.getPoint();
					int row = projectsTbl.convertRowIndexToModel(projectsTbl.rowAtPoint(p));

					if (e.getClickCount() == 1) {
						// create Property and Experiment Table
						if (row > 0) {
							long fk_projectid = (Long)projectsTbl.getValueAt(row, 0);
							// Use table.convertRowIndexToModel / table.convertColumnIndexToModle to convert to view indices
							//empty properties table
							while (projectPropertiesTbl.getRowCount()>0) {
								((DefaultTableModel)projectPropertiesTbl.getModel()).removeRow(projectPropertiesTbl.getRowCount()-1);
							}
							//refill properties table
							((DefaultTableModel)projectPropertiesTbl.getModel()).addRow(new Object[] {null,"<html><b>NEW PROJECT PROPERTY</b></html>", null});

							// query database for properties
							ArrayList<Property> projectPropertyList= new ArrayList<Property>(); 
							try {
								client.initDBConnection();
								projectPropertyList = new ArrayList<Property>(client.getProjectProperties(fk_projectid));
								client.clearDBConnection();
							} catch (SQLException e1) {
								e1.printStackTrace();
							}
							for (int i = 0; i < projectPropertyList.size(); i++) {
								((DefaultTableModel)projectPropertiesTbl.getModel()).addRow(new Object[]{projectPropertyList.get(i).getPropertyid(),projectPropertyList.get(i).getName(),projectPropertyList.get(i).getValue()});
								// justify column width
								packColumn(projectPropertiesTbl,0,5);
								projectPropertiesTbl.getColumnModel().getColumn(1).setPreferredWidth(1000);
								projectPropertiesTbl.getColumnModel().getColumn(2).setPreferredWidth(1000);
							}
							// fill experiment column
							//empty experiments table
							while (experimentsNameTbl.getRowCount()>0) {
								((DefaultTableModel)experimentsNameTbl.getModel()).removeRow(experimentsNameTbl.getRowCount()-1);
							}
							//refill properties table
							((DefaultTableModel)experimentsNameTbl.getModel()).addRow(new Object[] {null,"<html><b>NEW EXPERIMENT</b></html>", null});
							// query database for properties
							ArrayList<Experiment> projectExperimentList = new ArrayList<Experiment>(); 
							try {
								client.initDBConnection();
								projectExperimentList = new ArrayList<Experiment>(client.getProjectExperiments(fk_projectid));
								client.clearDBConnection();
							} catch (SQLException e1) {
								e1.printStackTrace();
							}
							for (int i = 0; i < projectExperimentList.size(); i++) {
								Object[] test = new Object[] {projectExperimentList.get(i).getExperimentid(),
										projectExperimentList.get(i).getTitle(),
										projectExperimentList.get(i).getCreationdate()};
								((DefaultTableModel)experimentsNameTbl.getModel()).addRow(test);
								// justify column width
								packColumn(experimentsNameTbl,0,5);
								experimentsNameTbl.getColumnModel().getColumn(1).setPreferredWidth(1000);
								packColumn(experimentsNameTbl,2,5);
							}
							//empty experiment properties
							((DefaultTableModel)experimentPropertiesTbl.getModel()).setRowCount(0);
						}	
						// edit cells
					} else if (e.getClickCount() == 2) {
					//	String oldVal = projectsTbl.getValueAt(row, 1).toString();
						if (projectsTbl.getSelectedRow() == 0) {
							projectsTbl.setValueAt("", row, 1);
						}
						projectsTbl.editCellAt(row, 1);
					}
				}
			}
		});

		projectsTbl.getModel().addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				if (e.getType() == TableModelEvent.UPDATE) {
					int row = projectsTbl.convertRowIndexToModel(e.getFirstRow());
					if (projectsTbl.getValueAt(row, 1) != "") {
						try {
							client.initDBConnection();
							if (row == 0) {
								// create new project
								String pTitle= (String) projectsTbl.getValueAt(e.getFirstRow(), 1);
								Timestamp pCreationdate= new Timestamp(new Date().getTime());
								Timestamp pModificationdate = new Timestamp(new Date().getTime());
								client.createNewProject((String)pTitle,(Timestamp)pCreationdate,(Timestamp)pModificationdate);
								recreateTable();
							} else {
								//change project
								client.modifyProject((Long)projectsTbl.getValueAt(e.getFirstRow(), 0),
										projectsTbl.getValueAt(e.getFirstRow(), e.getColumn()).toString());
							}
							recreateTable();
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});
		JScrollPane projectNameScp =new JScrollPane(projectsTbl);
		projectNameScp.setPreferredSize(new Dimension(300,200));
		projectNameScp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		manageProjectsPnl.add(projectNameScp,cc.xyw(2,2,2));
		// Button to delete a projects
		JButton deleteProjectBtn = new JButton("delete project");
		deleteProjectBtn.setPreferredSize(new Dimension(160,25));
		deleteProjectBtn.setSize(new Dimension(30,30));
		//delete Projects
		deleteProjectBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				try {
					client.initDBConnection();
					long projectID= (Long) projectsTbl.getValueAt(projectsTbl.getSelectedRow(),0);
					client.removeProjects(projectID);//projectID);
					recreateTable();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				
				
			
			}
		});
		
		
		
		manageProjectsPnl.add(deleteProjectBtn,cc.xy(2, 4));
		//Table for project properties
		projectPropertiesTbl = new JTable(new DefaultTableModel(){
			{ setColumnIdentifiers(new Object[] {"#","project property","value"}); }
			public boolean isCellEditable(int row, int col) {
				return ((col == 0) ? false :true);
			}
			public Class<?> getColumnClass(int col) {
				switch (col) {
				case 0:
					return Long.class;
				case 1:
					return String.class;
				case 2:
					return String.class;
				default:
					return getValueAt(0,col).getClass();
				}
			}	
		});
		// change and add to the projectProperty Table
		projectPropertiesTbl.getModel().addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				if (e.getType() == TableModelEvent.UPDATE) {
					int row = projectPropertiesTbl.convertRowIndexToModel(e.getFirstRow());
					if (projectPropertiesTbl.getValueAt(row, 1) != "") {
						try {
							client.initDBConnection();
							if (row == 0) {
								// create new project
								String pTitle= (String) projectPropertiesTbl.getValueAt(e.getFirstRow(), 1);
								Timestamp pCreationdate= new Timestamp(new Date().getTime());
								Timestamp pModificationdate = new Timestamp(new Date().getTime());
								client.createNewProject((String)pTitle,(Timestamp)pCreationdate,(Timestamp)pModificationdate);
								recreateTable();
							} else {
								//change project property
								client.modifyProjectProperty((Long)projectPropertiesTbl.getValueAt(e.getFirstRow(), 0),  //propertyid,
										projectPropertiesTbl.getValueAt(e.getFirstRow(), 1).toString(),//propertyName,
										projectPropertiesTbl.getValueAt(e.getFirstRow(), 2).toString());//propertyValue
							}
							recreateTable();
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		

		JScrollPane projectPropertiesScp = new JScrollPane(projectPropertiesTbl);
		projectPropertiesScp.setPreferredSize(new Dimension(300,200));
		projectPropertiesScp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		manageProjectsPnl.add(projectPropertiesScp,cc.xyw(5, 2,2));
		//Button to delete project property
		JButton deleteProjectPropertyBtn = new JButton("delete project property");
		deleteProjectPropertyBtn.setPreferredSize(new Dimension(160,25));
		manageProjectsPnl.add(deleteProjectPropertyBtn,cc.xy(5, 4));
		//Add manage projects to project panel
		projectPnl.add(manageProjectsPnl,cc.xy(2, 4));	
		// Experiments
		JPanel manageExperimentsPnl = new JPanel();
		manageExperimentsPnl.setBorder(new TitledBorder("Manage experiments"));
		manageExperimentsPnl.setLayout(new FormLayout("5dlu, p,p:g, 5dlu, p,p:g, 5dlu","5dlu, t:p:g, 5dlu, t:p, 5dlu"));
		//Experiment overview Table
		experimentsNameTbl= new JTable(new DefaultTableModel(){
			{ setColumnIdentifiers(new Object[] {"#","experiment name","creation date"}); }
			public boolean isCellEditable(int row, int col) {
				return ((col == 1) ? true :false);
			}
			public Class<?> getColumnClass(int col) {
				switch (col) {
				case 0:
					return Long.class;
				case 1:
					return String.class;
				case 2:
					return Date.class;
				default:
					return getValueAt(0,col).getClass();
				}
			}	
		});
		// ActionListener to show Experiment Properties
		experimentsNameTbl.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getComponent().isEnabled() && 
						e.getButton() == MouseEvent.BUTTON1 && 
						e.getClickCount() == 1) {
					Point p = e.getPoint();
					int row = experimentsNameTbl.convertRowIndexToModel(experimentsNameTbl.rowAtPoint(p));
					if (row > 0) {
						long experimentid = (Long)experimentsNameTbl.getValueAt(row, 0);
						// Use table.convertRowIndexToModel / table.convertColumnIndexToModle to convert to view indices
						//empty properties table
						while (experimentPropertiesTbl.getRowCount()>0) {
							((DefaultTableModel)experimentPropertiesTbl.getModel()).removeRow(experimentPropertiesTbl.getRowCount()-1);
						}
						//refill properties table
						((DefaultTableModel)experimentPropertiesTbl.getModel()).addRow(new Object[] {null,"<html><b>NEW EXPERIMENT PROPERTY</b></html>", null});

						// query database for properties
						ArrayList<ExpProperty> experimentPropertyList= new ArrayList<ExpProperty>(); 
						try {
							client.initDBConnection();
							experimentPropertyList = new ArrayList<ExpProperty>(client.getExperimentProperties(experimentid));
							//						projectPropertyList.addAll(propertyListDB);		
							client.clearDBConnection();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						for (int i = 0; i < experimentPropertyList.size(); i++) {
							((DefaultTableModel)experimentPropertiesTbl.getModel()).addRow(new Object[]{experimentPropertyList.get(i).getExppropertyid(),
									experimentPropertyList.get(i).getName(),
									experimentPropertyList.get(i).getValue()});
							// justify column width
							packColumn(experimentPropertiesTbl,0,5);
							experimentPropertiesTbl.getColumnModel().getColumn(1).setPreferredWidth(1000);
							experimentPropertiesTbl.getColumnModel().getColumn(2).setPreferredWidth(1000);
						}
					}
				}
			}
		});
		// change experiments
		experimentsNameTbl.getModel().addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				if (e.getType() == TableModelEvent.UPDATE) {
					try {
						client.initDBConnection();
						client.modifyExperimentsName((Long)experimentsNameTbl.getValueAt(e.getFirstRow(), 0), //experimentid,
								experimentsNameTbl.getValueAt(e.getFirstRow(),1).toString());//propertyValue

						client.clearDBConnection();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		JScrollPane experimentsNameScp= new JScrollPane(experimentsNameTbl);
		experimentsNameScp.setPreferredSize(new Dimension(300,200));
		experimentsNameScp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		manageExperimentsPnl.add(experimentsNameScp,cc.xyw(2, 2,2));
		// Button to delete experiment
		JButton deleteExperimentBtn = new JButton("delete experiment");
		deleteExperimentBtn.setPreferredSize(new Dimension(160,25));
		manageExperimentsPnl.add(deleteExperimentBtn,cc.xy(2, 4));
		// ExperimentProperties Table
		experimentPropertiesTbl= new JTable(new DefaultTableModel(){
			{ setColumnIdentifiers(new Object[] {"#","experiment property","value"}); }
			public boolean isCellEditable(int row, int col) {
				return ((col == 0) ? false :true);
			}
			public Class<?> getColumnClass(int col) {
				switch (col) {
				case 0:
					return Long.class;
				case 1:
					return String.class;
				case 2: 
					return String.class;
				default:
					return getValueAt(0,col).getClass();
				}
			}		
		});
		// change experimentProperties
		experimentPropertiesTbl.getModel().addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				if (e.getType() == TableModelEvent.UPDATE) {
					System.out.println(experimentPropertiesTbl.getValueAt(e.getFirstRow(), 0));
					try {
						client.initDBConnection();
						client.modifyExperimentsProperties((Long)experimentPropertiesTbl.getValueAt(e.getFirstRow(), 0),				//exppropertyid, 
								experimentPropertiesTbl.getValueAt(e.getFirstRow(),1).toString(),			//expProperty,
								experimentPropertiesTbl.getValueAt(e.getFirstRow(), 2).toString());//expPropertyValue);

						client.clearDBConnection();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		JScrollPane experimentPropertiesScp= new JScrollPane(experimentPropertiesTbl);
		experimentPropertiesScp.setPreferredSize(new Dimension(300,200));
		experimentPropertiesScp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		manageExperimentsPnl.add(experimentPropertiesScp,cc.xyw(5, 2,2));
		// Button to delete experiment properties
		JButton deleteExperimentPropertiesBtn= new JButton("delete experiment properties");
		deleteExperimentPropertiesBtn.setPreferredSize(new Dimension(160,25));
		manageExperimentsPnl.add(deleteExperimentPropertiesBtn,cc.xy(5, 4));
		//Add manageExperiemets to project
		projectPnl.add(manageExperimentsPnl,cc.xy(2, 6));
	}

	private void constructProteinPanel() {

		proteinTblScp = new JScrollPane();

		proteinViewPnl = new JPanel();
		proteinViewPnl.setLayout(new FormLayout("5dlu, p, 5dlu", "5dlu, t:p, 5dlu, t:p, 5dlu"));

		final JPanel proteinPnl = new JPanel();
		proteinPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu",	"5dlu, f:p:g, 5dlu"));
		proteinPnl.setBorder(BorderFactory.createTitledBorder("Proteins"));

		// Setup the tables
		setupProteinResultTableProperties();

		// List with loaded query spectra
		//querySpectraLst = new JList()

		proteinResultTbl.addMouseListener(new java.awt.event.MouseAdapter() {

			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				queryProteinTableMouseClicked(evt);
			}
		});

		proteinResultTbl.addKeyListener(new java.awt.event.KeyAdapter() {

			@Override
			public void keyReleased(java.awt.event.KeyEvent evt) {
				queryProteinTableKeyReleased(evt);
			}
		});

		proteinResultTbl.setOpaque(false);
		proteinTblScp.setViewportView(proteinResultTbl);
		proteinTblScp.setPreferredSize(new Dimension(1000, 300));

		proteinPnl.add(proteinTblScp, cc.xy(2, 2));

		// Peptides
		final JPanel peptidesPnl = new JPanel();
		peptidesPnl.setLayout(new FormLayout("5dlu, p, 5dlu",  "5dlu, p, 5dlu,"));
		peptidesPnl.setBorder(BorderFactory.createTitledBorder("Peptides"));

		peptidesTblScp = new JScrollPane();
		peptidesTblScp.setPreferredSize(new Dimension(1000, 250));
		peptidesTblScp.setViewportView(peptideResultTbl);
		peptidesPnl.add(peptidesTblScp, cc.xy(2, 2));

		proteinViewPnl.add(proteinPnl, cc.xy(2,2));
		proteinViewPnl.add(peptidesPnl, cc.xy(2,4));

	}

	private void constructMenu() {
		menuBar = new JMenuBar();
		menuBar.putClientProperty(Options.HEADER_STYLE_KEY, HeaderStyle.SINGLE);

		// File Menu
		fileMenu = new JMenu();
		newProjectItem = new JMenuItem();
		newProjectItem.setText("New Project");
		newProjectItem.setIcon(new ImageIcon(getClass().getResource("/de/mpa/resources/icons/new_project.gif")));

		openProjectItem = new JMenuItem();
		openProjectItem.setText("Open Project");
		openProjectItem.setIcon(new ImageIcon(getClass().getResource("/de/mpa/resources/icons/open_project.gif")));
		exitItem = new JMenuItem();
		fileMenu.setText("File");

		// exitItem
		exitItem.setText("Exit");
		exitItem.setIcon(new ImageIcon(getClass().getResource("/de/mpa/resources/icons/exit.gif")));
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		fileMenu.add(newProjectItem);
		fileMenu.add(openProjectItem);
		fileMenu.addSeparator();
		fileMenu.add(exitItem);
		menuBar.add(fileMenu);

		// Settings Menu
		settingsMenu = new JMenu();

		settingsMenu.setText("Settings");

		databaseItem = new JMenuItem();
		// databaseItem
		databaseItem.setText("Database Connection");

		databaseItem.setIcon(new ImageIcon(getClass().getResource("/de/mpa/resources/icons/database.png")));
		// settings for db connection
		menubarDbPnl= new JPanel();
		menubarDbPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p, 5dlu  ","5dlu, p, 5dlu ,t:p,5dlu, p, 5dlu, t:p,5dlu, p, 5dlu, t:p, 5dlu, t:p, 5dlu"));
		menubarDbPnl.setBorder(new TitledBorder("Database"));
		menubarDbPnl.add(new JLabel("JDBCDriver"),cc.xy(2,2));
		menubarDbPnl.add(new JLabel("URL_Locale"), cc.xy(2,4));
		menubarDbPnl.add(new JLabel("URL_Remote"), cc.xy(2,6));
		menubarDbPnl.add(new JLabel("URL_2"), cc.xy(2,8));
		menubarDbPnl.add(new JLabel("USER"), cc.xy(2,10));
		menubarDbPnl.add(new JLabel("PASS"), cc.xy(2,12));
		final JTextField menubarDBJDBCDriverTxt= new JTextField(client.dbSettings.getJdbcDriver());
		menubarDbPnl.add(menubarDBJDBCDriverTxt,cc.xy(4, 2));
		final JTextField menubarDBURL_LocaleTxt= new JTextField(client.dbSettings.getUrlLocale());
		menubarDbPnl.add(menubarDBURL_LocaleTxt,cc.xy(4, 4));
		final JTextField menubarDBURL_RemoteTxt= new JTextField(client.dbSettings.getUrlRemote());
		menubarDbPnl.add(menubarDBURL_RemoteTxt,cc.xy(4, 6));
		final JTextField menubarDBURL_2Txt= new JTextField(client.dbSettings.getPort());
		menubarDbPnl.add(menubarDBURL_2Txt,cc.xy(4, 8));
		final JTextField menubarDBUserTxt= new JTextField(client.dbSettings.getUsername());
		menubarDbPnl.add(menubarDBUserTxt,cc.xy(4, 10));
		final JPasswordField menubarDBPassTxt= new JPasswordField(client.dbSettings.getPassword());
		menubarDbPnl.add(menubarDBPassTxt,cc.xy(4, 12));
		final JLabel menubarConnectOKLbl = new JLabel("");
		menubarDbPnl.add(menubarConnectOKLbl,cc.xy(4, 14));
		JButton menubarDbBtn= new JButton("Test connection");
		// action listener for button "Test connection"
		menubarDbBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub

				client.dbSettings.setJdbcDriver(menubarDBJDBCDriverTxt.getText());
				client.dbSettings.setUrlLocale(menubarDBURL_LocaleTxt.getText());
				client.dbSettings.setUrlRemote(menubarDBURL_RemoteTxt.getText());
				client.dbSettings.setPort(menubarDBURL_2Txt.getText());
				client.dbSettings.setUsername(menubarDBUserTxt.getText());
				client.dbSettings.setPassword(new String(menubarDBPassTxt.getPassword()));

				// methode closes old connectiom
				try {client.clearDBConnection();				
				} catch (Exception e) {
					// TODO: handle exception
				}
				// try new connection				
				try {
					client.initDBConnection();
					menubarConnectOKLbl.setText("Connection OK");
					menubarConnectOKLbl.setForeground(Color.GREEN);
				} catch (Exception e) {
					// TODO: handle exception
					menubarConnectOKLbl.setText("Connection failed");
					menubarConnectOKLbl.setForeground(Color.RED);
				}
			}
		});

		menubarDbPnl.add(menubarDbBtn,cc.xy(2, 14));

		// action listener for database settings
		databaseItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int res = JOptionPane.showConfirmDialog(frame, menubarDbPnl, "Database Settings", 
						JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE);

				if (res == JOptionPane.OK_OPTION) {
					// update settings

				} else {	// cancel option or window close option
					// revert to old settings
				}		
			}
		});

		settingsMenu.add(databaseItem);
		settingsMenu.addSeparator();

		// serverItem
		serverItem = new JMenuItem();
		serverItem.setText("Server Configuration");
		serverItem.setIcon(new ImageIcon(getClass().getResource("/de/mpa/resources/icons/server.png")));

		settingsMenu.add(serverItem);
		menuBar.add(settingsMenu);

		// Help Menu
		helpMenu = new JMenu();		
		helpMenu.setText("Help");

		helpContentsItem = new JMenuItem();
		// helpContentsItem
		helpContentsItem.setText("Help Contents");

		helpContentsItem.setIcon(new ImageIcon(getClass().getResource("/de/mpa/resources/icons/help.gif")));
		helpMenu.add(helpContentsItem);
		helpContentsItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//helpTriggered();
			}
		});

		helpMenu.addSeparator();

		// aboutItem
		aboutItem = new JMenuItem();
		aboutItem.setText("About");
		aboutItem.setIcon(new ImageIcon(getClass().getResource("/de/mpa/resources/icons/about.gif")));
		aboutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				aboutActionPerformed(e);
			}
		});
		helpMenu.add(aboutItem);
		menuBar.add(helpMenu);


	}

	/**
	 * The method that builds the about dialog.
	 */
	private void aboutActionPerformed(ActionEvent e) {
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
	 * Construct the server configuration panel.
	 */
	private void constructServerPanel() {

		srvPnl = new JPanel();
		srvPnl.setLayout(new FormLayout("5dlu, p, 5dlu",		// col
										"5dlu, p, 5dlu"));		// row

		JPanel setPnl = new JPanel();
		setPnl.setBorder(new TitledBorder("Server Configuration"));
		setPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p, 5dlu, p:g, 5dlu, p, 5dlu, p, 5dlu",
										"p, 3dlu, p, 3dlu, p, 5dlu"));

		final JLabel hostLbl = new JLabel("Hostname:"); 
		hostTtf = new JTextField(8);
		hostTtf.setText(HOST);

		final JLabel portLbl = new JLabel("Port:");
		portTtf = new JTextField(8);
		portTtf.setText(Integer.toString(PORT));

		setPnl.add(hostLbl, cc.xy(2,1));
		setPnl.add(hostTtf, cc.xy(4,1));
		setPnl.add(portLbl, cc.xy(2,3));
		setPnl.add(portTtf, cc.xy(4,3));

		startBtn = new JButton("Connect to server");	    
		startBtn.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				WSPublisher.start(hostTtf.getText(), portTtf.getText());					
				JOptionPane.showMessageDialog(frame, "Web Service @" + hostTtf.getText() + ":" + portTtf.getText() + " established.");

				client.connect();
				connectedToServer = true;
				if (filePnl.files.size() > 0) {
					sendBtn.setEnabled(true);
				}
			}
		});
		chunkBtn = new JButton("Chunk");
		chunkBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ChunkFileWorker chunkWorker = new ChunkFileWorker();
				chunkWorker.execute();
			}
		});
		
		sendBtn = new JButton("Send files");
		sendBtn.setEnabled(false);
		sendBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
					
					
					try {
						client.sendFiles(chunkedFiles);
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
			}
		});

		setPnl.add(startBtn, cc.xyw(6,3,3));
		setPnl.add(chunkBtn, cc.xy(8,5));
		setPnl.add(sendBtn, cc.xy(10,5));
		setPnl.add(filesLbl, cc.xyw(2,5,5,"r,c"));
		srvPnl.add(setPnl, cc.xy(2,2));
	}
	
	/**
	 * SwingWorker class for sending the chunked files to the server.
	 * @author Thilo Muth
	 *
	 */
	private class ChunkFileWorker extends SwingWorker {

		@Override
		protected Object doInBackground() throws Exception {
			String filename = filePnl.files.get(0).getName();
			sendBtn.setEnabled(false);	 
			chunkedFiles = client.packFiles(1000, frame.getFilePanel().getCheckBoxTree(), filename.substring(0, filename.indexOf(".mgf")) + "_");
			return 0;
		}
		
		@Override
		protected void done() {
			sendBtn.setEnabled(true);
		}
	
	}
	
//	/**
//	 * Construct the processing panel.
//	 */
//	private void constructSpecLibSearchPanel() {
//
//		specLibPnl = new JPanel();
//		specLibPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p, 5dlu, p, 5dlu",	// col
//		   									"5dlu, f:p, 5dlu"));				// row;
//
//		// process button and progress bar
//		JPanel procPnl = new JPanel();
//		procPnl.setLayout(new FormLayout("5dlu, p, 2dlu, p, 2dlu, p, 5dlu",	// col
//										 "p, 5dlu, p, 5dlu, p, 5dlu"));		// row	
//		procPnl.setBorder(BorderFactory.createTitledBorder("Process data"));	
//		
//		final JLabel procLbl = new JLabel("0:00:00");	// for displaying remaining time
//		procLbl.setHorizontalAlignment(SwingConstants.RIGHT);
//
//		procBtn = new JButton("Process");	    
//		procBtn.addActionListener(new ActionListener() {			
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				final long startTime = System.currentTimeMillis();
//				
//				ProcessWorker worker = new ProcessWorker();
//				worker.addPropertyChangeListener(new PropertyChangeListener() {
//					@Override
//					public void propertyChange(PropertyChangeEvent evt) {
//						if ("progress" == evt.getPropertyName()) {
//							int progress = (Integer) evt.getNewValue();
//							procPrg.setValue(progress);
//							long elapsedTime = System.currentTimeMillis() - startTime;
//							long remainingTime = 0L;
//							if (progress > 0.0) {
//								remainingTime = (long) (elapsedTime/progress*(100-progress)/1000);
//							}
//							procLbl.setText(String.format("%d:%02d:%02d", remainingTime/3600,
//									(remainingTime%3600)/60, (remainingTime%60)));
//						} 
//
//					}
//				});
//				worker.execute();
//			}
//		});
//
//		procPrg = new JProgressBar(0, 100);
//		procPrg.setStringPainted(true);
//
//		packSpn = new JSpinner(new SpinnerNumberModel(100, 1, null, 1));
//		packSpn.setPreferredSize(new Dimension((int) (packSpn.getPreferredSize().width*1.75),
//				packSpn.getPreferredSize().height));
//		packSpn.setToolTipText("Number of spectra per transfer package");
//
//		procPnl.add(new JLabel("packageSize ="), cc.xy(2,1));
//		procPnl.add(packSpn, cc.xy(4,1));
//		procPnl.add(new JLabel("spectra"), cc.xy(6,1));
//		procPnl.add(procBtn, cc.xyw(2,3,3));
//		procPnl.add(procLbl, cc.xy(6,3));
//		procPnl.add(procPrg, cc.xyw(2,5,5));
//
//		// database search parameters
//		JPanel paraDbPnl = new JPanel();
//		paraDbPnl.setLayout(new FormLayout("5dlu, p, 2dlu, p:g, 2dlu, p, 5dlu",	// col
//										   "p, 5dlu, p, 5dlu, p, 5dlu"));		// row	
//		paraDbPnl.setBorder(BorderFactory.createTitledBorder("Search parameters"));
//
//		tolMzSpn = new JSpinner(new SpinnerNumberModel(10.0, 0.0, null, 0.1));
//		tolMzSpn.setPreferredSize(new Dimension((int) (tolMzSpn.getPreferredSize().width*1.75),
//													   tolMzSpn.getPreferredSize().height));
//		tolMzSpn.setEditor(new JSpinner.NumberEditor(tolMzSpn, "0.00"));
//		tolMzSpn.setToolTipText("Precursor mass tolerance");
//
//		annotChk = new JCheckBox("Annotated only", true);
//		annotChk.setHorizontalTextPosition(JCheckBox.LEFT);
//		annotChk.setToolTipText("Search only annotated spectra");
//		
//		// XXX
//		expIdSpn = new JSpinner(new SpinnerNumberModel(1L, 0L, null, 1L));
//		expIdSpn.setEnabled(false);
//		
//		final JCheckBox expIdChk = new JCheckBox("ExperimentID", false);
//		expIdChk.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				expIdSpn.setEnabled(((JCheckBox)e.getSource()).isSelected());
//			}
//		});
//		// /XXX
//
//		paraDbPnl.add(new JLabel("tolMz =", JLabel.RIGHT), cc.xy(2,1));
//		paraDbPnl.add(tolMzSpn, cc.xy(4,1));
//		paraDbPnl.add(new JLabel("Da"), cc.xy(6,1));
//		paraDbPnl.add(annotChk, cc.xyw(2,3,5));
//		paraDbPnl.add(expIdChk, cc.xyw(2,5,3));
//		paraDbPnl.add(expIdSpn, cc.xy(6,5));
//
//		// similarity scoring parameters
//		JPanel paraScPnl = new JPanel();
//		paraScPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p, 2dlu, p:g, 5dlu",	// col
//										   "p, 5dlu, p, 5dlu"));				// row
//		paraScPnl.setBorder(BorderFactory.createTitledBorder("Scoring parameters"));
//		
//		// combo box for method selection
//		JComboBox methodCbx = new JComboBox(new Object[] {"Normalized dot product",
//														  "Cross-correlation"});
//		// spinner for mass bin size
//		JSpinner binSizeSpn = new JSpinner(new SpinnerNumberModel(1.0, 0.0, null, 0.1));
//		
//		paraScPnl.add(new JLabel("Method"), cc.xy(2,1));
//		paraScPnl.add(methodCbx, cc.xyw(4,1,3));
//		paraScPnl.add(new JLabel("Bin size"), cc.xy(2,3));
//		paraScPnl.add(binSizeSpn, cc.xy(4,3));
//		paraScPnl.add(new JLabel("Da"), cc.xy(6,3));
//		
////		paraScPnl.setLayout(new FormLayout("5dlu, p, 2dlu, p, 2dlu, p, 5dlu",	// col
////				"p, 5dlu, p, 5dlu, p, 5dlu"));			// row	
////		paraScPnl.setBorder(BorderFactory.createTitledBorder("Scoring parameters"));
////
////		kSpn = new JSpinner(new SpinnerNumberModel(20, 1, null, 1));
////		kSpn.setPreferredSize(new Dimension((int) (kSpn.getPreferredSize().width*1.75),
////				kSpn.getPreferredSize().height));
////		kSpn.setToolTipText("Pick up to k highest peaks");
////
////		thMzSpn = new JSpinner(new SpinnerNumberModel(0.5, 0.0, null, 0.1));
////		thMzSpn.setPreferredSize(new Dimension((int)(thMzSpn.getPreferredSize().width*1.75),
////				thMzSpn.getPreferredSize().height));
////		thMzSpn.setEditor(new JSpinner.NumberEditor(thMzSpn, "0.0"));
////		thMzSpn.setToolTipText("Peak mass tolerance");
////
////		thScSpn = new JSpinner(new SpinnerNumberModel(0.5, null, null, 0.1));
////		thScSpn.setEditor(new JSpinner.NumberEditor(thScSpn, "0.00"));
////		thScSpn.setToolTipText("Score threshold");
////
////		paraScPnl.add(new JLabel("k =", JLabel.RIGHT), cc.xy(2,1));
////		paraScPnl.add(kSpn, cc.xy(4,1));
////		paraScPnl.add(new JLabel("threshMz =", JLabel.RIGHT), cc.xy(2,3));
////		paraScPnl.add(thMzSpn, cc.xy(4,3));
////		paraScPnl.add(new JLabel("Da"), cc.xy(6,3));
////		paraScPnl.add(new JLabel("threshSc =", JLabel.RIGHT), cc.xy(2,5));
////		paraScPnl.add(thScSpn, cc.xy(4,5));
//
//		// add everything to parent panel
//		specLibPnl.add(procPnl, cc.xy(2,2));
//		specLibPnl.add(paraDbPnl, cc.xy(4,2));
//		specLibPnl.add(paraScPnl, cc.xy(6,2));
//	}

	/**
	 * Construct the database search panel.
	 */
	private void constructDatabaseSearchPanel() {

		msmsPnl = new JPanel();
		msmsPnl.setLayout(new FormLayout("5dlu, p, 10dlu, p",
				"5dlu, f:p, 5dlu, p, 5dlu, t:p, 5dlu"));	

		// Protein Database Panel
		final JPanel protDatabasePnl = new JPanel();
		protDatabasePnl.setLayout(new FormLayout("5dlu, p, 15dlu, p:g, 5dlu", "5dlu, p, 5dlu"));		
		protDatabasePnl.setBorder(BorderFactory.createTitledBorder("Protein Database"));	

		// FASTA file Label
		final JLabel fastaFileLbl = new JLabel("FASTA File:");
		protDatabasePnl.add(fastaFileLbl, cc.xy(2, 2));

		// FASTA file ComboBox
		fastaFileCbx = new JComboBox(Constants.FASTA_DB);
		protDatabasePnl.add(fastaFileCbx, cc.xy(4, 2));

		// Parameters Panel
		final JPanel paramsPnl = new JPanel();
		paramsPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p:g, 5dlu, p, 2dlu, p, 5dlu", "5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));		
		paramsPnl.setBorder(BorderFactory.createTitledBorder("Parameters"));	

		// Precursor ion tolerance Label
		final JLabel precTolLbl = new JLabel("Precursor Ion Tolerance:");
		paramsPnl.add(precTolLbl, cc.xyw(2, 2, 3));

		// Precursor ion tolerance Spinner
		precTolSpn = new JSpinner(new SpinnerNumberModel(1.0, 0.0, 10.0, 0.1));
		precTolSpn.setEditor(new JSpinner.NumberEditor(precTolSpn, "0.0"));
		precTolSpn.setToolTipText("Precursor Ion Tolerance:");	    
		paramsPnl.add(precTolSpn, cc.xy(6, 2));
		paramsPnl.add(new JLabel("Da"), cc.xy(8,2));

		// Fragment ion tolerance Label
		final JLabel fragTolLbl = new JLabel("Fragment Ion Tolerance:");
		paramsPnl.add(fragTolLbl, cc.xyw(2, 4, 3));

		// Fragment ion tolerance Spinner
		fragTolSpn = new JSpinner(new SpinnerNumberModel(0.5, 0.0, 10.0, 0.1));
		fragTolSpn.setEditor(new JSpinner.NumberEditor(fragTolSpn, "0.0"));
		fragTolSpn.setToolTipText("Fragment Ion Tolerance:");	    
		paramsPnl.add(fragTolSpn, cc.xy(6, 4));
		paramsPnl.add(new JLabel("Da"), cc.xy(8,4));

		// Missed cleavages Label
		final JLabel missClvLbl = new JLabel("Missed Cleavages (max):");
		paramsPnl.add(missClvLbl, cc.xyw(2, 6, 3));

		// Missed cleavages Spinner
		missClvSpn = new JSpinner(new SpinnerNumberModel(2, 0, 10, 1));
		//missClvSpn.setEditor(new JSpinner.NumberEditor(fragTolSpn, "0"));
		missClvSpn.setToolTipText("Maximum number of missed cleavages:");	    
		paramsPnl.add(missClvSpn, cc.xy(6, 6));

		// Enzyme Label
		final JLabel enzymeLbl = new JLabel("Enzyme (Protease):");
		paramsPnl.add(enzymeLbl, cc.xy(2, 8));

		// Enzyme ComboBox
		enzymeCbx = new JComboBox(Constants.ENZYMES);
		paramsPnl.add(enzymeCbx, cc.xyw(4, 8, 5));

		// Search Engine Panel
		final JPanel searchEngPnl = new JPanel();
		searchEngPnl.setLayout(new FormLayout("5dlu, p, 10dlu, p, 10dlu, p, 5dlu", "5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));		
		searchEngPnl.setBorder(BorderFactory.createTitledBorder("Search Engines"));	

		// X!Tandem Label
		final JLabel xTandemLbl = new JLabel("X!Tandem:");
		searchEngPnl.add(xTandemLbl, cc.xy(2, 2));

		// X!Tandem CheckBox
		xTandemCbx = new JCheckBox();
		xTandemCbx.setSelected(true);
		searchEngPnl.add(xTandemCbx, cc.xy(4, 2));
		// TODO: Add action listener
		xTandemSetBtn = new JButton("Advanced Settings");
		xTandemSetBtn.setEnabled(false);
		searchEngPnl.add(xTandemSetBtn, cc.xy(6, 2));

		// OMSSA Label
		final JLabel omssaLbl = new JLabel("OMSSA:");
		searchEngPnl.add(omssaLbl, cc.xy(2, 4));

		// OMSSA CheckBox
		omssaCbx = new JCheckBox();
		omssaCbx.setSelected(true);
		searchEngPnl.add(omssaCbx, cc.xy(4, 4));
		// TODO: Add action listener
		omssaSetBtn = new JButton("Advanced Settings");
		omssaSetBtn.setEnabled(false);
		searchEngPnl.add(omssaSetBtn, cc.xy(6, 4));

		// Crux Label
		final JLabel cruxLbl = new JLabel("Crux:");
		searchEngPnl.add(cruxLbl, cc.xy(2, 6));

		// InsPecT Label
		final JLabel inspectLbl = new JLabel("InsPecT:");
		searchEngPnl.add(inspectLbl, cc.xy(2, 8));

		// Crux CheckBox
		cruxCbx = new JCheckBox();
		cruxCbx.setSelected(true);
		searchEngPnl.add(cruxCbx, cc.xy(4, 6));
		// TODO: Add action listener
		cruxSetBtn = new JButton("Advanced Settings");
		cruxSetBtn.setEnabled(false);
		searchEngPnl.add(cruxSetBtn, cc.xy(6, 6));

		// InsPecT CheckBox
		inspectCbx = new JCheckBox();
		inspectCbx.setSelected(true);
		searchEngPnl.add(inspectCbx, cc.xy(4, 8));
		// TODO: Add action listener
		inspectSetBtn = new JButton("Advanced Settings");
		inspectSetBtn.setEnabled(false);
		searchEngPnl.add(inspectSetBtn, cc.xy(6, 8));
		// Search Start Panel
		final JPanel runPnl = new JPanel();
		runPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p, 10dlu, p", "5dlu, p, 5dlu"));		
		runPnl.setBorder(BorderFactory.createTitledBorder("Search Start"));

		// Search type Label
		final JLabel searchTypeLbl = new JLabel("Type:");
		runPnl.add(searchTypeLbl, cc.xy(2, 2));

		// Search type ComboBox
		searchTypeCbx = new JComboBox(new String[] {"Target only", "Target-decoy"});
		runPnl.add(searchTypeCbx, cc.xy(4, 2));

		// Search Run Button
		runDbSearchBtn = new JButton("Run");	    
		runDbSearchBtn.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				runDbSearchBtn.setEnabled(false);
				RunDbSearchWorker worker = new RunDbSearchWorker();
				worker.addPropertyChangeListener(new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if ("progress" == evt.getPropertyName()) {
							int progress = (Integer) evt.getNewValue();
							specLibPnl.setProgress(progress);
						} 

					}
				});
				worker.execute();
			}
		});	    
		runPnl.add(runDbSearchBtn, cc.xy(6, 2));

		// Status Panel
		JPanel statusPnl = new JPanel();
		statusPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p:g, 5dlu",
				"5dlu, p, 5dlu"));		
		statusPnl.setBorder(BorderFactory.createTitledBorder("Search Status"));

		// Progress Label
		final JLabel progressLbl = new JLabel("Progress:");
		statusPnl.add(progressLbl, cc.xy(2, 2));

		// Progress bar
		searchPrg = new JProgressBar(0, 100);
		searchPrg.setStringPainted(true);
		searchPrg.setValue(0);
		statusPnl.add(searchPrg, cc.xy(4, 2));

		// Status details Panel
		final JPanel statusDetailsPnl = new JPanel();
		statusDetailsPnl.setLayout(new FormLayout("5dlu, p, 10dlu, p, 5dlu", "5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));		
		statusDetailsPnl.setBorder(BorderFactory.createTitledBorder("Search Details"));	

		// X!Tandem status Label
		final JLabel xtandemStatLbl = new JLabel("X!Tandem:");
		statusDetailsPnl.add(xtandemStatLbl, cc.xy(2, 2));

		// X!Tandem status TextField
		xtandemStatTtf = new JTextField(15);
		xtandemStatTtf.setEditable(false);
		xtandemStatTtf.setEnabled(false);
		statusDetailsPnl.add(xtandemStatTtf, cc.xy(4, 2));

		// OMSSA status Label
		final JLabel omssaStatLbl = new JLabel("OMSSA:");
		statusDetailsPnl.add(omssaStatLbl, cc.xy(2, 4));

		// OMSSA status TextField
		omssaStatTtf = new JTextField(15);
		omssaStatTtf.setEditable(false);
		omssaStatTtf.setEnabled(false);
		statusDetailsPnl.add(omssaStatTtf, cc.xy(4, 4));

		// Crux status Label
		final JLabel cruxStatLbl = new JLabel("Crux:");
		statusDetailsPnl.add(cruxStatLbl, cc.xy(2, 6));

		// Crux status TextField
		cruxStatTtf = new JTextField(15);
		cruxStatTtf.setEditable(false);
		cruxStatTtf.setEnabled(false);
		statusDetailsPnl.add(cruxStatTtf, cc.xy(4, 6));

		// InsPecT status Label
		final JLabel inspectStatLbl = new JLabel("InsPecT:");
		statusDetailsPnl.add(inspectStatLbl, cc.xy(2, 8));

		// InsPecT status TextField
		inspectStatTtf = new JTextField(15);
		inspectStatTtf.setEditable(false);
		inspectStatTtf.setEnabled(false);
		statusDetailsPnl.add(inspectStatTtf, cc.xy(4, 8));

		msmsPnl.add(protDatabasePnl, cc.xy(2, 2));	    
		msmsPnl.add(statusPnl, cc.xy(4, 2));
		msmsPnl.add(paramsPnl, cc.xy(2, 4));
		msmsPnl.add(statusDetailsPnl, cc.xy(4, 4));
		msmsPnl.add(searchEngPnl, cc.xy(2, 6));
		msmsPnl.add(runPnl, cc.xy(4, 6));
	}

	private DbSearchSettings collectDBSearchSettings() {
		DbSearchSettings settings = new DbSearchSettings();
		settings.setFastaFile(fastaFileCbx.getSelectedItem().toString());
		settings.setFragmentIonTol((Double) fragTolSpn.getValue());
		settings.setPrecursorIonTol((Double) precTolSpn.getValue());
		settings.setNumMissedCleavages((Integer) missClvSpn.getValue());
		//TODO: Enzyme: settings.setEnzyme(value)
		settings.setXTandem(xTandemCbx.isSelected());
		settings.setOmssa(omssaCbx.isSelected());
		settings.setCrux(cruxCbx.isSelected());
		settings.setInspect(inspectCbx.isSelected());
		if (searchTypeCbx.getSelectedIndex() == 0) {
			settings.setDecoy(false);
			System.out.println(settings.isDecoy());
		}
		else if (searchTypeCbx.getSelectedIndex() == 1) {
			settings.setDecoy(true);
			System.out.println(settings.isDecoy());
		}
		return settings;
	}

	private void constructDenovoPanel() {
		denovoPnl = new JPanel();
		denovoPnl.setLayout(new FormLayout("5dlu, p, 5dlu, f:p, 5dlu",	// col
										   "5dlu, f:p, 5dlu, p, 5dlu"));	// row

		// Parameters		
		JPanel parPnl = new JPanel();
		parPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p:g, 5dlu, p, 2dlu, p, 5dlu",				// col
										"p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));	// row
		parPnl.setBorder(new TitledBorder("Parameters"));

		// Enzymes
		parPnl.add(new JLabel("Protease"), cc.xy(2, 1));
		dnEnzymesCbx = new JComboBox(Constants.DN_ENZYMES);
		dnEnzymesCbx.setToolTipText("Choose the enzyme of the protein digest");
		//dnEnzymesCbx.setRenderer(new RightAlignListCellRenderer());
		parPnl.add(dnEnzymesCbx,cc.xyw(4, 1, 5));

		// MS
		parPnl.add(new JLabel("Spectrometer"),cc.xy(2, 3));
		dnMSCbx=new JComboBox(Constants.MASS_SPECTROMETERS);
		dnMSCbx.setToolTipText("Select your mass spectrometer");
		//dnMS.setRenderer(new RightAlignListCellRenderer());
		parPnl.add(dnMSCbx,cc.xyw(4, 3, 5));

		// Fragment tolerance
		parPnl.add(new JLabel("Fragment mass tolerance"), cc.xyw(2, 5, 3));
		dnFragTolSpn = new JSpinner(new SpinnerNumberModel(0.3, 0.0, null, 0.05));
		dnFragTolSpn.setEditor(new JSpinner.NumberEditor(dnFragTolSpn, "0.00"));
		dnFragTolSpn.setToolTipText("Choose your fragment mass tolerance");
		parPnl.add(dnFragTolSpn,cc.xy(6, 5));
		parPnl.add(new JLabel("Da"),cc.xy(8, 5));

		// for right aligned text in spinners in windows LAF
		//	private class RightAlignListCellRenderer extends JLabel implements ListCellRenderer<String> {
		//		@Override
		//		public Component getListCellRendererComponent(
		//				JList<? extends String> list, String value, int index,
		//				boolean isSelected, boolean cellHasFocus) {
		//			this.setText(value);
		//			this.setHorizontalAlignment(JLabel.RIGHT);
		//			return this;
		//		}
		//	}

		// Threshold peptides
		parPnl.add(new JLabel("Peptide intensity threshold"),cc.xyw(2, 7, 3));
		dnThresholdSpn = new JSpinner(new SpinnerNumberModel(1000, 0, null, 1));
		dnThresholdSpn.setToolTipText("Apply peptide threshold");
		parPnl.add(dnThresholdSpn,cc.xy(6, 7));

		// Maximum number of peptides
		parPnl.add(new JLabel("Number of peptides"),cc.xyw(2, 9, 3));
		dnNumSolutionsSpn = new JSpinner(new SpinnerNumberModel(10, 0, null, 1));
		dnNumSolutionsSpn.setToolTipText("Select the maximum number of peaks for de novo sequencing ");
		parPnl.add(dnNumSolutionsSpn,cc.xy(6, 9));

		dnPepknownChx = new JCheckBox("Remove known peptides");
		dnPepknownChx.setToolTipText("Remove all identified peptides");
		//		parPnl.add(dnPepknownChx,cc.xyw(2, 11, 5));

		// Panel PTMs
		JPanel ptmsPnl = new JPanel();
		ptmsPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu",	// col
										 "f:p:g, 5dlu,"));	// row
		ptmsPnl.setBorder(new TitledBorder("PTMs"));

		DefaultTableModel model = new DefaultTableModel(new Object[] {"PTM", ""}, 0) {
			public Class<?> getColumnClass(int c) {	// method allows checkboxes in table
				return getValueAt(0,c).getClass();
			}
			public boolean isCellEditable(int row, int col) { // only allow editing of checkboxes
				return ((col == 1) ? true : false);
			}
		};
		for (int i = 0; i < Constants.PTMS.length; i++) {
			model.addRow(new Object[] {Constants.PTMS[i], false});
		}
		dnPTMtbl = new JTable(model);
		dnPTMtbl.getColumnModel().getColumn(1).setMaxWidth(dnPTMtbl.getColumnModel().getColumn(1).getMinWidth());
		dnPTMtbl.setShowVerticalLines(false);
		JScrollPane dnPTMscp = new JScrollPane(dnPTMtbl);
		//		dnPTMscp.setPreferredSize(new Dimension(0, 0));
		dnPTMscp.setPreferredSize(new Dimension(200, 0));
		dnPTMscp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		dnPTMscp.setToolTipText("Choose possible PTMs");
		ptmsPnl.add(dnPTMscp,cc.xy(2,1));

		// Start panel
		JPanel statusPnl = new JPanel();
		statusPnl.setLayout(new FormLayout("5dlu, p, 15dlu, p, 5dlu, p:g, 5dlu",	// col
				"p, 5dlu,"));							// row
		statusPnl.setBorder(new TitledBorder("Search status"));
		// Start button
		dnStartBtn = new JButton("Run de-novo search");
		dnStartBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dnStartBtn.setEnabled(false);
				RunDenovoSearchWorker worker = new RunDenovoSearchWorker();
				worker.execute();

				// Easter egg stuff
				Image image = null;
				try {
					image = ImageIO.read(new File("docu/Nerds.jpg"));

				} catch (IOException e1) {
					e1.printStackTrace();
				}
				JLabel label = new JLabel("Das passiert wenn man wahllos Knoepfe drueckt!!!", new ImageIcon(image),JLabel.CENTER);
				label.setVerticalTextPosition(JLabel.BOTTOM);
				label.setHorizontalTextPosition(JLabel.CENTER);
				JOptionPane.showMessageDialog(frame, label,"Erwischt!!", JOptionPane.PLAIN_MESSAGE);
			}
		});
		statusPnl.add(dnStartBtn,cc.xy(2, 1));
		// Progress bar
		statusPnl.add(new JLabel("Progress"),cc.xy(4, 1));
		denovoPrg = new JProgressBar(0,100);
		denovoPrg.setStringPainted(true);
		denovoPrg.setValue(0);
		statusPnl.add(denovoPrg, cc.xy(6, 1));

		// add panels
		denovoPnl.add(parPnl, cc.xy(2, 2));
		denovoPnl.add(ptmsPnl, cc.xy(4, 2));
		denovoPnl.add(statusPnl, cc.xyw(2, 4, 3));
	}

	private DenovoSearchSettings collectDenovoSettings(){
		DenovoSearchSettings settings = new DenovoSearchSettings();
		settings.setDnEnzyme(dnEnzymesCbx.getSelectedItem().toString());
		settings.setDnMS(dnMSCbx.getSelectedItem().toString());
		settings.setDnFragmentTolerance((Double) dnFragTolSpn.getValue());
		settings.setDnPeptideIntThresh((Integer)dnThresholdSpn.getValue() );
		settings.setDnNumSolutions((Integer) dnNumSolutionsSpn.getValue());
		settings.setDnRemoveAllPep((boolean) dnPepknownChx.isSelected());
		String mods = "";
		for (int row = 0; row < dnPTMtbl.getRowCount(); row++) {
			if ((Boolean) dnPTMtbl.getValueAt(row, 1)){
				mods += (String) dnPTMtbl.getValueAt(row, 0);
			}
		}
		settings.setDnPTMs(mods);
		return settings;
	}

	private void constructDenovoResultPanel() {
		queryDnSpectraTblJScrollPane = new JScrollPane();

		denovoResPnl = new JPanel();
		denovoResPnl.setLayout(new FormLayout("5dlu, f:p:g, 5dlu, f:p:g, 5dlu",					// col
											  "5dlu, f:p:g,5dlu, f:p:g, 5dlu, f:p:g, 5dlu"));	// row
		// Choose your spectra
		dnResSpectrumPnl = new JPanel();
		dnResSpectrumPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu",				// col
												  "5dlu, p, 5dlu, f:p:g, 5dlu,"));	// row
		dnResSpectrumPnl.setBorder(BorderFactory.createTitledBorder("Query Spectra"));

		// Setup the tables
		setupDenovoSearchResultTableProperties();

		queryDnSpectraTbl.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				queryDnSpectraTableMouseClicked(evt);
			}
		});

		queryDnSpectraTbl.addKeyListener(new java.awt.event.KeyAdapter() {

			@Override
			public void keyReleased(java.awt.event.KeyEvent evt) {
				queryDnSpectraTableKeyReleased(evt);
			}
		});

		queryDnSpectraTbl.setOpaque(false);
		queryDnSpectraTblJScrollPane.setViewportView(queryDnSpectraTbl);
		queryDnSpectraTblJScrollPane.setPreferredSize(new Dimension(500, 300));

		spectraCbx2 = new JComboBox();
		JButton updateDnBtn = new JButton("Get results");
		JPanel topPnl = new JPanel(new FormLayout("p:g, 40dlu, p", "p:g"));
		topPnl.add(spectraCbx2, cc.xy(1, 1));
		topPnl.add(updateDnBtn, cc.xy(3, 1));
		updateDnBtn.setPreferredSize(new Dimension(150, 20));

		updateDnBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for(File file : filePnl.files){
					denovoSearchResult = client.getDenovoSearchResult(file);
					updateDenovoResultsTable();
				}
			}
		});

		dnResSpectrumPnl.add(topPnl, cc.xy(2, 2));
		dnResSpectrumPnl.add(queryDnSpectraTblJScrollPane, cc.xy(2, 4));

		// De novo results
		dnRSeqPnl = new JPanel();
		dnRSeqPnl.setLayout(new FormLayout("5dlu, f:p:g, 5dlu",	// col
										   "5dlu, f:p, 5dlu"));	// row
		dnRSeqPnl.setBorder(new TitledBorder("PepNovo results"));

		JScrollPane dnRseqScp = new JScrollPane(pepnovoTbl);
		dnRseqScp.setPreferredSize(new Dimension(100,200));
		dnRseqScp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		dnRseqScp.setToolTipText("Select spectra");
		dnRSeqPnl.add(dnRseqScp,cc.xy(2, 2));

		// Use BLAST
		dnRBlastPnl = new JPanel();
		dnRBlastPnl.setLayout(new FormLayout("5dlu, f:p:g, 5dlu",											// col
				"5dlu, f:p, 5dlu, f:p, 5dlu,f:p, 5dlu,f:p, 5dlu,f:p, 5dlu"));	// row
		dnRBlastPnl.setBorder(new TitledBorder("BLAST search"));
		dnRBlastPnl.add(new JLabel("Database for BLAST search:"),cc.xy(2, 2));
		dnRBLastCbx = new JComboBox(Constants.DNBLAST_DB);
		dnRBLastCbx.setToolTipText("Choose databse for BLAST search");
		dnRBlastPnl.add(dnRBLastCbx,cc.xy(2, 4));
		dnRStartBLASTBtn= new JButton("BLAST search");

		dnRStartBLASTBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JLabel dnRlabel = new JLabel("<html> Ein 22-jaehriger Mann lernt in einer " +
						"Bar eine aeltere Frau kennen. Trotz ihrem Alter von 57 Jahren, sind sich die " +
						"beiden sehr sympathisch. Sie unterhalten sich lange, beginnen zu fummeln und zu <br>" +
						"knutschen.Dann meint sie: Hast du es schon einmal mit Mutter und Tochter zusammen  " +
						"gemacht? Er antwortet: Nein, aber das waere sicher ein geiles Erlebnis! Sie sagt: " +
						"Komm mit mir nach Hause; das wird deine Nacht!Er denkt: So geil, bis morgen frueh <br>" +
						" durchhoekern und das mit 2 Frauen - ein Traum. Als sie zu Hause die Tuere oeffnet " +
						"und sie beide in den Flur treten, ruft sie: Mutti, bist du noch wach?!!!!!</html>",JLabel.CENTER);
				dnRlabel.setVerticalTextPosition(JLabel.BOTTOM);
				dnRlabel.setHorizontalTextPosition(JLabel.CENTER);
				JOptionPane.showMessageDialog(frame, dnRlabel,"Erwischt!!", JOptionPane.PLAIN_MESSAGE);
			}});

		// ShowBLASTResults
		dnRBlastPnl.add(dnRStartBLASTBtn,cc.xy(2, 6));
		// Progress Bar
		dnRBlastPnl.add(new JLabel("Progress"),cc.xy(2, 8));
		blastsearchPrg = new JProgressBar(0, 100);
		blastsearchPrg.setStringPainted(true);
		blastsearchPrg.setValue(0);
		dnRBlastPnl.add(blastsearchPrg, cc.xy(2, 10));

		//See BLAST results
		dnRBlastRPnl = new JPanel();
		dnRBlastRPnl.setBorder(new TitledBorder("BLAST results"));

		// Spectra Plot

		//			dnRPlotPnl = new JPanel();
		//			dnRPlotPnl.setLayout(new FormLayout("5dlu, f:p:g, 5dlu",
		//											"5dlu, f:p:g, 5dlu"));
		//			
		//			dnRPlotPnl.setBorder(new TitledBorder("Spectra"));
		//			dnRPlotPnl2= new PlotPanel2(null);
		//			dnRPlotPnl.add(dnRPlotPnl2,cc.xy(2, 2));

		// Add panelsdenovoResSpectrumPnl
		denovoResPnl.add(dnResSpectrumPnl,cc.xyw(2, 2,3));
		denovoResPnl.add(dnRSeqPnl,cc.xy(2, 4));
		denovoResPnl.add(dnRBlastPnl,cc.xy(4, 4));
		//denovoResPnl.add(dnRBlastRPnl,cc.xy(6, 4));
		//denovoResPnl.add(dnRPlotPnl,cc.xyw(2, 6, 5));
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
		
		packColumn(libTbl, 0, 10);
		libTbl.getColumnModel().getColumn(1).setPreferredWidth(1000);
		packColumn(libTbl, 2, 10);

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
						packColumn(protTbl, 0, 10);
						packColumn(protTbl, 1, 10);
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
		packColumn(protTbl, 0, 10);
		packColumn(protTbl, 1, 10);
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
	 * Construct the MS2 results panel.
	 */
	private void constructMS2ResultsPanel() {

		querySpectraTblJScrollPane = new JScrollPane();

		res2Pnl = new JPanel();
		res2Pnl.setLayout(new FormLayout("5dlu, p, 5dlu", "5dlu, p:g, 5dlu, p:g, 5dlu, p"));

		final JPanel spectraPnl = new JPanel();
		spectraPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu",
											"5dlu, p, 5dlu, f:p:g, 5dlu"));
		spectraPnl.setBorder(BorderFactory.createTitledBorder("Query Spectra"));

		// Setup the tables
		setupDbSearchResultTableProperties();

		// List with loaded query spectra
		//querySpectraLst = new JList()

		querySpectraTbl.addMouseListener(new java.awt.event.MouseAdapter() {

			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				querySpectraTableMouseClicked(evt);
			}
		});

		querySpectraTbl.addKeyListener(new java.awt.event.KeyAdapter() {

			@Override
			public void keyReleased(java.awt.event.KeyEvent evt) {
				querySpectraTableKeyReleased(evt);
			}
		});

		querySpectraTbl.setOpaque(false);
		querySpectraTblJScrollPane.setViewportView(querySpectraTbl);
		querySpectraTblJScrollPane.setPreferredSize(new Dimension(500, 300));

		spectraCbx = new JComboBox();
		JButton updateBtn = new JButton("Get results");
		JPanel topPnl = new JPanel(new FormLayout("p:g, 40dlu, p", "p:g"));
		topPnl.add(spectraCbx, cc.xy(1, 1));
		topPnl.add(updateBtn, cc.xy(3, 1));
		updateBtn.setPreferredSize(new Dimension(150, 20));

		updateBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for(File file : filePnl.files){
					dbSearchResult = client.getDbSearchResult(file);
					updateDbResultsTable();
				}
			}
		});

		spectraPnl.add(topPnl, cc.xy(2, 2));
		spectraPnl.add(querySpectraTblJScrollPane, cc.xy(2, 4));

		// PSMs
		final JPanel psmPnl = new JPanel();
		psmPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p, 5dlu",  "5dlu, p, 5dlu, p, 5dlu"));
		psmPnl.setBorder(BorderFactory.createTitledBorder("Peptide-to-spectrum Matches"));

		// X!Tandem
		final JPanel xtandemPnl = new JPanel(new FormLayout("p:g", "p:g"));
		xtandemPnl.setBorder(BorderFactory.createTitledBorder("X!Tandem"));
		xTandemTblJScrollPane = new JScrollPane();
		xTandemTblJScrollPane.setPreferredSize(new Dimension(500, 100));
		xTandemTblJScrollPane.setViewportView(xTandemTbl);
		xtandemPnl.add(xTandemTblJScrollPane, cc.xy(1, 1));
		psmPnl.add(xtandemPnl, cc.xy(2, 2));

		// Omssa
		final JPanel omssaPnl = new JPanel(new FormLayout("p:g", "p:g"));
		omssaPnl.setBorder(BorderFactory.createTitledBorder("Omssa"));
		omssaTblJScrollPane = new JScrollPane();
		omssaTblJScrollPane.setPreferredSize(new Dimension(500, 100));
		omssaTblJScrollPane.setViewportView(omssaTbl);
		omssaPnl.add(omssaTblJScrollPane, cc.xy(1, 1));
		psmPnl.add(omssaPnl, cc.xy(4, 2));

		// Crux
		final JPanel cruxPnl = new JPanel(new FormLayout("p:g", "p:g"));
		cruxPnl.setBorder(BorderFactory.createTitledBorder("Crux"));
		cruxTblJScrollPane = new JScrollPane();
		cruxTblJScrollPane.setPreferredSize(new Dimension(500, 100));
		cruxTblJScrollPane.setViewportView(cruxTbl);
		cruxPnl.add(cruxTblJScrollPane, cc.xy(1, 1));
		psmPnl.add(cruxPnl, cc.xy(2, 4));

		// Inspect
		final JPanel inspectPnl = new JPanel(new FormLayout("p:g", "p:g"));
		inspectPnl.setBorder(BorderFactory.createTitledBorder("Inspect"));
		inspectTblJScrollPane = new JScrollPane();
		inspectTblJScrollPane.setPreferredSize(new Dimension(500, 100));
		inspectTblJScrollPane.setViewportView(inspectTbl);
		inspectPnl.add(inspectTblJScrollPane, cc.xy(1, 1));
		psmPnl.add(inspectPnl, cc.xy(4, 4));
		res2Pnl.add(spectraPnl, cc.xy(2,2));
		res2Pnl.add(psmPnl, cc.xy(2,4));

	}

	private void setupProteinResultTableProperties(){
		// Query table
		proteinResultTbl = new JTable(new DefaultTableModel() {
			// instance initializer block
			{ setColumnIdentifiers(new Object[] {" ", "Accession", "Description", "No. Peptides", "Coverage", "Spectral Count", "NSAF"}); }

			public boolean isCellEditable(int row, int col) {
				return false;
			}
		});
		proteinResultTbl.getColumn(" ").setMinWidth(30);
		proteinResultTbl.getColumn(" ").setMaxWidth(30);
		proteinResultTbl.getColumn("Accession").setMinWidth(100);
		proteinResultTbl.getColumn("Accession").setMaxWidth(100);

		proteinResultTbl.getColumn("No. Peptides").setMinWidth(90);
		proteinResultTbl.getColumn("No. Peptides").setMaxWidth(90);
		proteinResultTbl.getColumn("Coverage").setMinWidth(90);
		proteinResultTbl.getColumn("Coverage").setMaxWidth(90);
		proteinResultTbl.getColumn("Spectral Count").setMinWidth(90);
		proteinResultTbl.getColumn("Spectral Count").setMaxWidth(90);
		proteinResultTbl.getColumn("NSAF").setMinWidth(90);
		proteinResultTbl.getColumn("NSAF").setMaxWidth(90);

		peptideResultTbl = new JTable(new DefaultTableModel() {
			// instance initializer block
			{ setColumnIdentifiers(new Object[] {" ", "Sequence", "Modification", "Unique", "PSM Votes", "No. Spectra", "Start", "End"}); }

			public boolean isCellEditable(int row, int col) {
				return false;
			}
		});

		peptideResultTbl.getColumn(" ").setMinWidth(30);
		peptideResultTbl.getColumn(" ").setMaxWidth(30);			
		peptideResultTbl.getColumn("Modification").setMinWidth(90);
		peptideResultTbl.getColumn("Modification").setMaxWidth(90);
		peptideResultTbl.getColumn("Unique").setMinWidth(90);
		peptideResultTbl.getColumn("Unique").setMaxWidth(90);
		peptideResultTbl.getColumn("PSM Votes").setMinWidth(90);
		peptideResultTbl.getColumn("PSM Votes").setMaxWidth(90);
		peptideResultTbl.getColumn("No. Spectra").setMinWidth(90);
		peptideResultTbl.getColumn("No. Spectra").setMaxWidth(90);
		peptideResultTbl.getColumn("Start").setMinWidth(90);
		peptideResultTbl.getColumn("Start").setMaxWidth(90);
		peptideResultTbl.getColumn("End").setMinWidth(90);
		peptideResultTbl.getColumn("End").setMaxWidth(90);
	}

	private void setupDenovoSearchResultTableProperties(){
		// Query table
		queryDnSpectraTbl = new JTable(new DefaultTableModel() {
			// instance initializer block
			{ setColumnIdentifiers(new Object[] {" ", "Title", "m/z", "Charge", "Identified"}); }

			public boolean isCellEditable(int row, int col) {
				return false;
			}
		});
		queryDnSpectraTbl.getColumn(" ").setMinWidth(30);
		queryDnSpectraTbl.getColumn(" ").setMaxWidth(30);
		queryDnSpectraTbl.getColumn("m/z").setMinWidth(100);
		queryDnSpectraTbl.getColumn("m/z").setMaxWidth(100);
		queryDnSpectraTbl.getColumn("Charge").setMinWidth(100);
		queryDnSpectraTbl.getColumn("Charge").setMaxWidth(100);
		queryDnSpectraTbl.getColumn("Identified").setMinWidth(80);
		queryDnSpectraTbl.getColumn("Identified").setMaxWidth(80);

		pepnovoTbl = new JTable(new DefaultTableModel() {
			// instance initializer block
			{ setColumnIdentifiers(new Object[] {" ", "Peptide", "N-Gap", "C-Gap", "Score"}); }

			public boolean isCellEditable(int row, int col) {
				return false;
			}
		});

		pepnovoTbl.getColumn(" ").setMinWidth(30);
		pepnovoTbl.getColumn(" ").setMaxWidth(30);
		pepnovoTbl.getColumn("N-Gap").setMinWidth(90);
		pepnovoTbl.getColumn("N-Gap").setMaxWidth(90);
		pepnovoTbl.getColumn("C-Gap").setMinWidth(90);
		pepnovoTbl.getColumn("C-Gap").setMaxWidth(90);
		pepnovoTbl.getColumn("Score").setMinWidth(90);
		pepnovoTbl.getColumn("Score").setMaxWidth(90);
	}

	private void setupDbSearchResultTableProperties(){
		// Query table
		querySpectraTbl = new JTable(new DefaultTableModel() {
			// instance initializer block
			{ setColumnIdentifiers(new Object[] {" ", "Title", "m/z", "Charge", "Identified"}); }

			public boolean isCellEditable(int row, int col) {
				return false;
			}
		});
		querySpectraTbl.getColumn(" ").setMinWidth(30);
		querySpectraTbl.getColumn(" ").setMaxWidth(30);
		querySpectraTbl.getColumn("m/z").setMinWidth(100);
		querySpectraTbl.getColumn("m/z").setMaxWidth(100);
		querySpectraTbl.getColumn("Charge").setMinWidth(100);
		querySpectraTbl.getColumn("Charge").setMaxWidth(100);
		querySpectraTbl.getColumn("Identified").setMinWidth(80);
		querySpectraTbl.getColumn("Identified").setMaxWidth(80);
		xTandemTbl = new JTable(new DefaultTableModel() {
			// instance initializer block
			{ setColumnIdentifiers(new Object[] {" ", "Peptide", "Accession", "e-value", "hyperscore", "PEP", "q-value"}); }

			public boolean isCellEditable(int row, int col) {
				return false;
			}
		});

		xTandemTbl.getColumn(" ").setMinWidth(30);
		xTandemTbl.getColumn(" ").setMaxWidth(30);
		xTandemTbl.getColumn("Accession").setCellRenderer(new HtmlLinksRenderer(Constants.SELECTED_ROW_HTML_FONT_COLOR, Constants.NOT_SELECTED_ROW_HTML_FONT_COLOR));
		xTandemTbl.getColumn("e-value").setMinWidth(80);
		xTandemTbl.getColumn("e-value").setMaxWidth(80);
		xTandemTbl.getColumn("hyperscore").setMinWidth(90);
		xTandemTbl.getColumn("hyperscore").setMaxWidth(90);
		xTandemTbl.getColumn("PEP").setMinWidth(80);
		xTandemTbl.getColumn("PEP").setMaxWidth(80);
		xTandemTbl.getColumn("q-value").setMinWidth(80);
		xTandemTbl.getColumn("q-value").setMaxWidth(80);

		omssaTbl = new JTable(new DefaultTableModel() {
			// instance initializer block
			{ setColumnIdentifiers(new Object[] {" ", "Peptide", "Accession", "e-value", "p-value", "PEP", "q-value"}); }

			public boolean isCellEditable(int row, int col) {
				return false;
			}
		});

		omssaTbl.getColumn(" ").setMinWidth(30);
		omssaTbl.getColumn(" ").setMaxWidth(30);
		omssaTbl.getColumn("Accession").setCellRenderer(new HtmlLinksRenderer(Constants.SELECTED_ROW_HTML_FONT_COLOR, Constants.NOT_SELECTED_ROW_HTML_FONT_COLOR));
		omssaTbl.getColumn("e-value").setMinWidth(80);
		omssaTbl.getColumn("e-value").setMaxWidth(80);
		omssaTbl.getColumn("p-value").setMinWidth(80);
		omssaTbl.getColumn("p-value").setMaxWidth(80);
		omssaTbl.getColumn("PEP").setMinWidth(80);
		omssaTbl.getColumn("PEP").setMaxWidth(80);
		omssaTbl.getColumn("q-value").setMinWidth(80);
		omssaTbl.getColumn("q-value").setMaxWidth(80);

		cruxTbl = new JTable(new DefaultTableModel() {
			// instance initializer block
			{ setColumnIdentifiers(new Object[] {" ", "Peptide", "Accession", "xCorr", "q-value"}); }

			public boolean isCellEditable(int row, int col) {
				return false;
			}
		});

		cruxTbl.getColumn(" ").setMinWidth(30);
		cruxTbl.getColumn(" ").setMaxWidth(30);
		cruxTbl.getColumn("xCorr").setMinWidth(90);
		cruxTbl.getColumn("xCorr").setMaxWidth(90);
		cruxTbl.getColumn("q-value").setMinWidth(90);
		cruxTbl.getColumn("q-value").setMaxWidth(90);

		inspectTbl = new JTable(new DefaultTableModel() {
			// instance initializer block
			{ setColumnIdentifiers(new Object[] {" ", "Peptide", "Accession", "f-score", "p-value"}); }

			public boolean isCellEditable(int row, int col) {
				return false;
			}
		});

		inspectTbl.getColumn(" ").setMinWidth(30);
		inspectTbl.getColumn(" ").setMaxWidth(30);
		inspectTbl.getColumn("f-score").setMinWidth(90);
		inspectTbl.getColumn("f-score").setMaxWidth(90);
		inspectTbl.getColumn("p-value").setMinWidth(90);
		inspectTbl.getColumn("p-value").setMaxWidth(90);

		// Reordering not allowed
		querySpectraTbl.getTableHeader().setReorderingAllowed(false);
		xTandemTbl.getTableHeader().setReorderingAllowed(false);
		omssaTbl.getTableHeader().setReorderingAllowed(false);
		cruxTbl.getTableHeader().setReorderingAllowed(false);
		inspectTbl.getTableHeader().setReorderingAllowed(false);
	}

	private void updateDbResultsTable(){
		List<Searchspectrum> querySpectra = dbSearchResult.getQuerySpectra();
		xTandemResults = dbSearchResult.getxTandemResults();
		ommsaResults = dbSearchResult.getOmssaResults();      
		cruxResults = dbSearchResult.getCruxResults();        	
		inspectResults = dbSearchResult.getInspectResults();   
		voteMap = dbSearchResult.getVoteMap();
		proteins = dbSearchResult.getProteins();
		peptideHits = new HashMap<String, List<PeptideHit>>();

		if (querySpectra != null) {
			for (int i = 0; i < querySpectra.size(); i++) {
				Searchspectrum spectrum = querySpectra.get(i);
				String title = spectrum.getSpectrumname();

				((DefaultTableModel) querySpectraTbl.getModel()).addRow(new Object[]{
						i + 1,
						title,
						spectrum.getPrecursor_mz(),
						spectrum.getCharge(), 
						voteMap.get(title) + " / 4"});
			}
		}

		if (proteins != null) {
			Set<String> accessions = proteins.getPeptideHits().keySet();

			Iterator<String> accIter = accessions.iterator();
			int i = 0;
			while(accIter.hasNext()){
				String accession = accIter.next();	
				List<PeptideHit> peptides = proteins.getPeptideHits(accession);
				peptideHits.put(accession, peptides);
				int nPeptides = peptides.size();
				((DefaultTableModel) proteinResultTbl.getModel()).addRow(new Object[]{
						i + 1,
						accession,
						proteins.getProteinHit(accession).getDescription(),
						nPeptides, 
						"",
						"", 
				""});
				i++;
			}
		}
	}

	private void updateDenovoResultsTable(){
		List<Searchspectrum> querySpectra = denovoSearchResult.getQuerySpectra();
		pepnovoResults = denovoSearchResult.getPepnovoResults();
		String identified;

		if (querySpectra != null) {
			for (int i = 0; i < querySpectra.size(); i++) {
				Searchspectrum spectrum = querySpectra.get(i);
				String title = spectrum.getSpectrumname();
				if(pepnovoResults.containsKey(title)){
					identified = "yes";
				} else {
					identified = "no";
				}

				((DefaultTableModel) queryDnSpectraTbl.getModel()).addRow(new Object[]{
						i + 1,
						title,
						spectrum.getPrecursor_mz(),
						spectrum.getCharge(), 
						identified});
			}
		}
	}

	/**
	 * Update the PSM tables based on the spectrum selected.
	 * 
	 * @param evt
	 */
	private void querySpectraTableMouseClicked(MouseEvent evt) {
		// Set the cursor into the wait status.
		this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

		int row = querySpectraTbl.getSelectedRow();

		// Condition if one row is selected.
		if (row != -1) {

			// Empty tables.
			clearDbResultTables();

			String spectrumName = querySpectraTbl.getValueAt(row, 1).toString();
			if (xTandemResults.containsKey(spectrumName)) {
				List<XTandemhit> xTandemList = xTandemResults.get(spectrumName);
				for (int i = 0; i < xTandemList.size(); i++) {
					XTandemhit hit = xTandemList.get(i);
					((DefaultTableModel) xTandemTbl.getModel()).addRow(new Object[]{
							i + 1,
							hit.getSequence(),
							hit.getAccession(),
							Util.roundDouble(hit.getEvalue().doubleValue(), 5), 
							Util.roundDouble(hit.getHyperscore().doubleValue(), 5),
							Util.roundDouble(hit.getPep().doubleValue(), 5),
							Util.roundDouble(hit.getQvalue().doubleValue(), 5)});
				}
			}

			if (ommsaResults.containsKey(spectrumName)) {
				List<Omssahit> omssaList = ommsaResults.get(spectrumName);
				for (int i = 0; i < omssaList.size(); i++) {
					Omssahit hit = omssaList.get(i);
					((DefaultTableModel) omssaTbl.getModel()).addRow(new Object[]{
							i + 1,
							hit.getSequence(),
							hit.getAccession(),
							Util.roundDouble(hit.getEvalue().doubleValue(), 5), 
							Util.roundDouble(hit.getPvalue().doubleValue(), 5), 
							Util.roundDouble(hit.getPep().doubleValue(), 5),
							Util.roundDouble(hit.getQvalue().doubleValue(), 5)});
				}
			}

			if (cruxResults.containsKey(spectrumName)) {
				List<Cruxhit> cruxList = cruxResults.get(spectrumName);
				for (int i = 0; i < cruxList.size(); i++) {
					Cruxhit hit = cruxList.get(i);
					((DefaultTableModel) cruxTbl.getModel()).addRow(new Object[]{
							i + 1,
							hit.getSequence(),
							hit.getAccession(),
							Util.roundDouble(hit.getXcorr_score().doubleValue(), 5), 
							Util.roundDouble(hit.getQvalue().doubleValue(), 5)});
				}
			}


			if (inspectResults.containsKey(spectrumName)) {
				List<Inspecthit> inspectList = inspectResults.get(spectrumName);
				for (int i = 0; i < inspectList.size(); i++) {
					Inspecthit hit = inspectList.get(i);
					((DefaultTableModel) inspectTbl.getModel()).addRow(new Object[]{
							i + 1,
							hit.getSequence(),
							hit.getAccession(),
							Util.roundDouble(hit.getF_score().doubleValue(), 5), 
							Util.roundDouble(hit.getP_value().doubleValue(), 5)});
				}
			}
		}
		// Set the cursor back into the default status.
		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	/**
	 * Update the PSM tables based on the spectrum selected.
	 * 
	 * @param evt
	 */
	private void queryDnSpectraTableMouseClicked(MouseEvent evt) {
		// Set the cursor into the wait status.
		this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

		int row = queryDnSpectraTbl.getSelectedRow();

		// Condition if one row is selected.
		if (row != -1) {

			// Empty tables.
			clearDenovoResultTables();

			String spectrumName = queryDnSpectraTbl.getValueAt(row, 1).toString();
			if (pepnovoResults.containsKey(spectrumName)) {
				List<Pepnovohit> pepnovoList = pepnovoResults.get(spectrumName);
				for (int i = 0; i < pepnovoList.size(); i++) {
					Pepnovohit hit = pepnovoList.get(i);
					((DefaultTableModel) pepnovoTbl.getModel()).addRow(new Object[]{
							i + 1,
							hit.getSequence(),
							hit.getN_gap(),
							hit.getC_gap(), 
							hit.getPnvscore()});
				}
			}
		}
		// Set the cursor back into the default status.
		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	/**
	 * Update the peptides tables based on the protein selected.
	 * 
	 * @param evt
	 */
	private void queryProteinTableMouseClicked(MouseEvent evt) {
		// Set the cursor into the wait status.
		this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

		int row = proteinResultTbl.getSelectedRow();

		// Condition if one row is selected.
		if (row != -1) {

			// Empty tables.
			clearPeptideHitsTable();

			String accession = proteinResultTbl.getValueAt(row, 1).toString();
			if (peptideHits.containsKey(accession)) {
				List<PeptideHit> peptideList = peptideHits.get(accession);
				for (int i = 0; i < peptideList.size(); i++) {
					PeptideHit hit = peptideList.get(i);
					((DefaultTableModel) peptideResultTbl.getModel()).addRow(new Object[]{
							i + 1,
							hit.getSequence(),
							"",
							"", 
							"", 
							"",
							hit.getStart(),
							hit.getEnd()});
				}
			}
		}
		// Set the cursor back into the default status.
		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

	}

	/**
	 * Clears the result tables.
	 */
	private void clearDbResultTables(){
		// Remove PSMs from all result tables        	 
		while (xTandemTbl.getRowCount() > 0) {
			((DefaultTableModel) xTandemTbl.getModel()).removeRow(0);
		}
		while (omssaTbl.getRowCount() > 0) {
			((DefaultTableModel) omssaTbl.getModel()).removeRow(0);
		}
		while (cruxTbl.getRowCount() > 0) {
			((DefaultTableModel) cruxTbl.getModel()).removeRow(0);
		}
		while (inspectTbl.getRowCount() > 0) {
			((DefaultTableModel) inspectTbl.getModel()).removeRow(0);
		}
	}

	/**
	 * Clears the result tables.
	 */
	private void clearDenovoResultTables(){
		// Remove PSMs from all result tables        	 
		while (pepnovoTbl.getRowCount() > 0) {
			((DefaultTableModel) pepnovoTbl.getModel()).removeRow(0);
		}
	}

	/**
	 * Clears the peptide result table.
	 */
	private void clearPeptideHitsTable(){
		// Remove peptides from all result tables        	 
		while (peptideResultTbl.getRowCount() > 0) {
			((DefaultTableModel) peptideResultTbl.getModel()).removeRow(0);
		}
	}

	/**
	 * @see #querySpectraTableKeyReleased(java.awt.event.MouseEvent)
	 */
	private void querySpectraTableKeyReleased(KeyEvent evt) {
		querySpectraTableMouseClicked(null);
	}

	/**
	 * @see #queryDnSpectraTableKeyReleased(java.awt.event.MouseEvent)
	 */
	private void queryDnSpectraTableKeyReleased(KeyEvent evt) {
		queryDnSpectraTableMouseClicked(null);
	}

	/**
	 * @see #queryDnSpectraTableKeyReleased(java.awt.event.MouseEvent)
	 */
	private void queryProteinTableKeyReleased(KeyEvent evt) {
		queryProteinTableMouseClicked(null);
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
		logPnl = new LogPanel();
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
			packColumn(libTbl, 0, 10);
			packColumn(libTbl, 2, 10);
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


	// Sets the preferred width of the visible column specified by vColIndex. The column
	// will be just wide enough to show the column head and the widest cell in the column.
	// margin pixels are added to the left and right
	// (resulting in an additional width of 2*margin pixels).
	public void packColumn(JTable table, int vColIndex, int margin) {
		DefaultTableColumnModel colModel = (DefaultTableColumnModel)table.getColumnModel();
		TableColumn col = colModel.getColumn(vColIndex);
		int width = 0;

		// Get width of column header
		TableCellRenderer renderer = col.getHeaderRenderer();
		if (renderer == null) {
			renderer = table.getTableHeader().getDefaultRenderer();
		}
		Component comp = renderer.getTableCellRendererComponent(
				table, col.getHeaderValue(), false, false, 0, 0);
		width = comp.getPreferredSize().width;

		// Get maximum width of column data
		for (int r=0; r<table.getRowCount(); r++) {
			renderer = table.getCellRenderer(r, vColIndex);
			comp = renderer.getTableCellRendererComponent(
					table, table.getValueAt(r, vColIndex), false, false, r, vColIndex);
			width = Math.max(width, comp.getPreferredSize().width);
		}

		// Add margin
		width += 2*margin;

		// Set the width
		col.setMinWidth(width);
		col.setPreferredWidth(width);
	}
	
	/**
	 * RunDBSearchWorker class extending SwingWorker.
	 * @author Thilo Muth
	 *
	 */
	private class RunDbSearchWorker	extends SwingWorker {

		protected Object doInBackground() throws Exception {
			DbSearchSettings settings = collectDBSearchSettings();
			try {
				client.runDbSearch(chunkedFiles, settings);
				filePnl.files.clear();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 0;
		}

		@Override
		public void done() {
			specLibPnl.setButtonEnabled(true);
		}
	}

	/**
	 * RunDBSearchWorker class extending SwingWorker.
	 * @author Thilo Muth
	 *
	 */
	private class RunDenovoSearchWorker	extends SwingWorker {

		protected Object doInBackground() throws Exception {
			DenovoSearchSettings settings = collectDenovoSettings();
			client.runDenovoSearch(chunkedFiles, settings);
			filePnl.files.clear();
			return 0;
		}

		@Override
		public void done() {
			specLibPnl.setButtonEnabled(true);
		}
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
	
	
}

