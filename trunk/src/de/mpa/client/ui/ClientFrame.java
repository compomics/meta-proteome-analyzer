package de.mpa.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
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
import de.mpa.client.model.DbSearchResult;
import de.mpa.client.model.PeptideHit;
import de.mpa.client.model.ProteinHitSet;
import de.mpa.client.ui.SpectrumTree.TreeType;
import de.mpa.client.ui.panels.ClusterPanel;
import de.mpa.client.ui.panels.DBSearchPanel;
import de.mpa.client.ui.panels.DeNovoPanel;
import de.mpa.client.ui.panels.DeNovoResultPanel;
import de.mpa.client.ui.panels.FilePanel;
import de.mpa.client.ui.panels.LogPanel;
import de.mpa.client.ui.panels.SpecLibSearchPanel;
import de.mpa.db.accessor.Cruxhit;
import de.mpa.db.accessor.ExpProperty;
import de.mpa.db.accessor.Experiment;
import de.mpa.db.accessor.Inspecthit;
import de.mpa.db.accessor.Omssahit;
import de.mpa.db.accessor.Project;
import de.mpa.db.accessor.Property;
import de.mpa.db.accessor.Searchspectrum;
import de.mpa.db.accessor.XTandemhit;
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

	private DBSearchPanel msmsPnl;

	private JPanel resPnl;

	private DeNovoPanel denovoPnl;

	private DeNovoResultPanel denovoResPnl;

	private JPanel res2Pnl;

	private LogPanel logPnl;

	private CellConstraints cc;

//	private List<File> files = new ArrayList<File>();
	public JButton sendBtn;
	private JMenuBar menuBar;
	public boolean connectedToServer = false;

	public  JTable libTbl;
	private JTable querySpectraTbl;
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
	private DbSearchResult dbSearchResult;
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
	private Map<String, List<PeptideHit>> peptideHits;
	private Map<String, Integer> voteMap;
	public JComboBox spectraCbx;
	public JComboBox spectraCbx2;
	private JTable proteinResultTbl;
	private JPanel proteinViewPnl;
	private JScrollPane proteinTblScp;
	private JScrollPane peptidesTblScp;
	private JTable projectsTbl;
	private JTable projectPropertiesTbl;
	private JTable experimentsNameTbl;
	private JTable experimentPropertiesTbl;
	private ClusterPanel clusterPnl;
	private JTable peptideResultTbl;
	private ProteinHitSet proteins;
	protected List<File> chunkedFiles;
	
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
//			for (File file : chunkedFiles) {
//				client.getDenovoSearchResult(file);
//				denovoResPnl.updateDenovoResultsTable();
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

		// File panel
		filePnl = new FilePanel(this);

		// Project panel
		constructProjectPanel();

		// Spectral Library Search Panel
		specLibPnl = new SpecLibSearchPanel(this);

		// MS/MS Database Search Panel
		msmsPnl = new DBSearchPanel(this);

		//DeNovo
		denovoPnl = new DeNovoPanel(this);

		// Results Panel
		constructSpecResultsPanel();

		// MS2 Results Panel
		constructMS2ResultsPanel();

		// Protein Panel
		constructProteinPanel();

		// DeNovoResults
		denovoResPnl = new DeNovoResultPanel(this);
//		constructDenovoResultPanel();

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

