package de.mpa.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.apache.log4j.Logger;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.algorithms.RankedLibrarySpectrum;
import de.mpa.client.Client;
import de.mpa.client.DbSearchSettings;
import de.mpa.client.ProcessSettings;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;
import de.mpa.io.Peak;
import de.mpa.job.JobStatus;
import de.mpa.ui.MgfFilter;
import de.mpa.ui.MultiPlotPanel;
import de.mpa.ui.PlotPanel2;
import de.mpa.utils.ExtensionFileFilter;
import de.mpa.webservice.WSPublisher;

@SuppressWarnings("unchecked")
/**
 * <b> ClientFrame </b>
 * <p>
 * 	Represents the main graphical user interface for the MetaProteomeAnalyzer-Client.
 * </p>
 * 
 * @author muth Alex Behne, Thilo Muth
 */

public class ClientFrame extends JFrame {
	
	private final static int PORT = 8080;
	private final static String HOST = "0.0.0.0";
	private Logger log = Logger.getLogger(getClass());
	private final static String PATH = "test/de/mpa/resources/";
	
	private ClientFrame frame;
	
	private Client client;
	
	private JPanel filePnl;

	private JPanel srvPnl;
	
	private JPanel msmsPnl;
	
	private JPanel resPnl;
	
	private LogPanel logPnl;

	private CellConstraints cc;

	private List<File> files = new ArrayList<File>();

	private JTextField filesTtf;

	private JTextField hostTtf;

	private JTextField portTtf;

	private JButton startBtn;
	private JLabel filesLbl = new JLabel("0 of 0 spectra selected") {
		@Override
		public void repaint() {
			if (fileTree != null) {
				this.setText(fileTree.getSelectionModel().getSelectionCount() + " of " +
							 ((DefaultMutableTreeNode)fileTree.getModel().getRoot()).getLeafCount() +
							 " spectra selected");
			}
		}		
	};
    private JButton sendBtn;
	private JMenuBar menuBar;
	private JMenu menu1;
	private JMenuItem exitItem;
	
	private JButton procBtn;
	private JProgressBar procPrg;
	private JProgressBar searchPrg;
	
	private boolean connectedToServer = false;
	
	private JTable libTbl;
	private Map<String, ArrayList<RankedLibrarySpectrum>> resultMap;
	
	private Map<String, ArrayList<Long>> specPosMap = new HashMap<String, ArrayList<Long>>(1);
	
	private JTable fileDetailsTbl;
	private MultiPlotPanel mPlot;
	protected ArrayList<RankedLibrarySpectrum> resultList;
	private JPanel lggPnl;
	private JSpinner tolMzSpn;
	private JSpinner kSpn;
	private JSpinner thMzSpn;
	private JSpinner thScSpn;
	private JSpinner packSpn;
	private JCheckBox annotChk;
	
	private JButton runDbSearchBtn;
	private JPanel specLibPnl;
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
	
	private String projectName = "Placeholder Project";
	private CheckBoxTreeManager fileTree;
	
	// placeholder filter criteria
	private int minPeaks = 5;
	private double minTIC = 100.0;
	private double minSNR = 1.0;
	
	
	/**
	 * Constructor for the ClientFrame
	 */
	public ClientFrame() {
		
		// Application title
		super("Client");
		
		// Frame size
		this.setMinimumSize(new Dimension(Constants.CLIENTFRAME_WIDTH, Constants.CLIENTFRAME_HEIGHT));
		this.setPreferredSize(new Dimension(Constants.CLIENTFRAME_WIDTH, Constants.CLIENTFRAME_HEIGHT));		
		frame = this;
		
		// Init components
		initComponents();
		
		// Get the content pane
		Container cp = this.getContentPane();
		cp.setLayout(new BorderLayout());		
		cp.add(menuBar, BorderLayout.NORTH);

		JTabbedPane tabPane = new JTabbedPane(JTabbedPane.LEFT);
		tabPane.addTab("Input Spectra", filePnl);
		tabPane.addTab("Server Configuration", srvPnl);
		tabPane.addTab("Spectral Library Search", specLibPnl);
		tabPane.addTab("MS/MS Database Search", msmsPnl);
		
		tabPane.addTab("Results", resPnl);
		tabPane.addTab("Logging", lggPnl);
		
		cp.add(tabPane);
		
		// Get the client instance
        client = Client.getInstance();
        
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
		}
	}
	
	/**
	 * Initialize the components.
	 */
	private void initComponents(){
		
		// Cell constraints
		cc = new CellConstraints();
		
		// Menu
		constructMenu();
		
		// File panel
		constuctFilePanel();
		
		// Server panel
		constructServerPanel();
		
		// Spectral Library Search Panel
		constructSpecLibSearchPanel();
		
		// MS/MS Database Search Panel
		constructDatabaseSearchPanel();
		
		// Results Panel
		constructResultsPanel();
		
		// Logging panel		
		constructLogPanel();
	}

	/**
	 * Constructs the menu
	 */
	private void constructMenu() {
		menuBar = new JMenuBar();
		menu1 = new JMenu();
		exitItem = new JMenuItem();
		menu1.setText("File");

		// ---- menuItem7 ----
		exitItem.setText("Exit");
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		menu1.add(exitItem);
		menuBar.add(menu1);
	}

	private PlotPanel2 filePlotPnl;
	
	/**
	 * Construct the file selection panel.
	 */
	private void constuctFilePanel(){
		
		filePnl = new JPanel();
		filePnl.setLayout(new FormLayout("5dlu, p:g, 5dlu",		// col
										 "5dlu, f:p:g, 5dlu"));	// row
		
		JPanel selPnl = new JPanel();
		selPnl.setBorder(new TitledBorder("File selection"));
		selPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p:g, 5dlu, p, 5dlu, p, 5dlu",	// col
										 "p, 5dlu, f:p:g, 5dlu"));						// row
		
		String pathName = "test/de/mpa/resources/";
		JLabel fileLbl = new JLabel("Spectrum Files (MGF):");
		
		final JFileChooser fc = new JFileChooser(pathName);
	    fc.setFileFilter(new MgfFilter());
	    fc.setAcceptAllFileFilterUsed(false);
	    
	    filesTtf = new JTextField(20);
	    filesTtf.setEditable(false);
	    filesTtf.setMaximumSize(new Dimension(filesTtf.getMaximumSize().width, filesTtf.getPreferredSize().height));
	    filesTtf.setText(files.size() + " file(s) selected");
	    
	    JButton addBtn = new JButton("Add...");
	    
	    final JButton clrBtn = new JButton("Clear all");
	    clrBtn.setEnabled(false);
	    
		filePlotPnl = new PlotPanel2(null);
		filePlotPnl.setMinimumSize(new Dimension(200, 150));
		filePlotPnl.clearSpectrumFile();
//		plotPnl.repaint();
		
	    // Tree of loaded spectrum files
	    final DefaultMutableTreeNode treeRoot = new DefaultMutableTreeNode(this.projectName);
	    final DefaultTreeModel treeModel = new DefaultTreeModel(treeRoot);
	    final SpectrumTree tree = new SpectrumTree(treeModel, TreeType.FILE_SELECT);
		fileTree = new CheckBoxTreeManager(tree);
		final CheckBoxTreeSelectionModel selectionModel = fileTree.getSelectionModel();
		
		tree.addMouseListener(new MouseAdapter() {
			private int hotspot = new JCheckBox().getPreferredSize().width;
			
			public void mouseClicked(MouseEvent e) { 
				TreePath path = tree.getPathForLocation(e.getX(), e.getY()); 
				if ((path != null) &&											// tree element got hit
					(e.getX() <= (tree.getPathBounds(path).x + hotspot))) {		// checkbox part got hit

					boolean selected = selectionModel.isPathSelected(path, true);

					try {
						if (selected) {
							selectionModel.removeSelectionPath(path);
						} else {
							selectionModel.addSelectionPath(path);
						}
					} finally {
						tree.treeDidChange();
						filesLbl.repaint();
					}
				}
				return;
			}
		});
		
		JScrollPane treePane = new JScrollPane(tree);
		treePane.setPreferredSize(new Dimension(0, 0));
		
		// left part of outer split pane
		JPanel leftPnl = new JPanel();
		leftPnl.setLayout(new FormLayout("p:g",					// col
										 "p, f:p:g"));	// row
		JTable leftDummyTable = new JTable(null, new Vector<String>(Arrays.asList(new String[] {"Files"})));
		leftDummyTable.getTableHeader().setReorderingAllowed(false);
		leftDummyTable.getTableHeader().setResizingAllowed(false);
		JScrollPane leftDummyScpn = new JScrollPane(leftDummyTable);
		leftDummyScpn.setPreferredSize(leftDummyTable.getTableHeader().getPreferredSize());

		leftPnl.add(leftDummyScpn, cc.xy(1,1));
//		leftPnl.add(new JLabel("Files"), cc.xy(1,1));
		leftPnl.add(treePane, cc.xy(1,2));
		
		// top part of right-hand inner split pane
		JPanel topRightPnl = new JPanel();
		topRightPnl.setLayout(new FormLayout("p:g",					// col
											 "p, f:p:g"));	// row
		final JTable rightDummyTable = new JTable(null, new Vector<String>(Arrays.asList(new String[] {"Details"})));
		rightDummyTable.getTableHeader().setReorderingAllowed(false);
		rightDummyTable.getTableHeader().setResizingAllowed(false);
		JScrollPane rightDummyScpn = new JScrollPane(rightDummyTable);
		rightDummyScpn.setPreferredSize(rightDummyTable.getTableHeader().getPreferredSize());
//		JLabel rightLbl = new JLabel("Details");
		
		String[] columnNames = {null, null};
		Object[][] data = {
			    {"File path",				null},
			    {"Spectrum title",			null},
			    {"Number of peaks",			null},
			    {"Total ion current",		null},
			    {"Signal-to-noise ratio",	null},
			};
		fileDetailsTbl = new JTable(new DefaultTableModel(data, columnNames) {
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		});
		fileDetailsTbl.setIntercellSpacing(new Dimension(3, 3));
		fileDetailsTbl.setAutoscrolls(false);
		fileDetailsTbl.getTableHeader().setVisible(false);
		fileDetailsTbl.getTableHeader().setPreferredSize(new Dimension(0, 0));
		Component comp = fileDetailsTbl.getCellRenderer(4, 0).getTableCellRendererComponent(fileDetailsTbl, fileDetailsTbl.getValueAt(4, 0), false, false, 4, 0);
		fileDetailsTbl.getColumnModel().getColumn(0).setMaxWidth(comp.getPreferredSize().width+10);
		fileDetailsTbl.getColumnModel().getColumn(0).setPreferredWidth(comp.getPreferredSize().width+10);
		
		JScrollPane detailScpn = new JScrollPane(fileDetailsTbl);
		detailScpn.setPreferredSize(new Dimension(0, 0));
		detailScpn.setMinimumSize(new Dimension(0, fileDetailsTbl.getRowHeight()));
		detailScpn.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		detailScpn.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		topRightPnl.add(rightDummyScpn, cc.xy(1,1));
//		topRightPnl.add(new JLabel("Details"), cc.xy(1,1));
		topRightPnl.add(detailScpn, cc.xy(1,2));
		topRightPnl.setMinimumSize(new Dimension(0, rightDummyTable.getTableHeader().getPreferredSize().height + 
													fileDetailsTbl.getRowHeight() + 2));
		
		final JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topRightPnl, filePlotPnl);
		rightSplit.setMinimumSize(new Dimension(200, 0));
		rightSplit.setDividerLocation(rightDummyTable.getTableHeader().getPreferredSize().height + 
									  (fileDetailsTbl.getRowCount()+1)*fileDetailsTbl.getRowHeight() + 2);
		rightSplit.setContinuousLayout(true);
		BasicSplitPaneDivider rightDivider = ((BasicSplitPaneUI) rightSplit.getUI()).getDivider();
		if (rightDivider != null) { rightDivider.setBorder(null); }
		rightDivider.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {	// reset divider location on double-click
					rightSplit.setDividerLocation(rightDummyTable.getTableHeader().getPreferredSize().height + 
									  (fileDetailsTbl.getRowCount()+1)*fileDetailsTbl.getRowHeight() + 2);
				}
			}
		});
		
		JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPnl, rightSplit);
		mainSplit.setDividerLocation(200);
		mainSplit.setContinuousLayout(true);
		BasicSplitPaneDivider mainDivider = ((BasicSplitPaneUI) mainSplit.getUI()).getDivider();
		if (mainDivider != null) { mainDivider.setBorder(null); }

	    // Button action listeners
		addBtn.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
                // First check whether a file has already been selected.
                // If so, start from that file's parent.
				File startLocation = new File(PATH);
				if (treeRoot.getChildCount() > 0) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)treeRoot.getChildAt(0);
					File temp = (File)node.getUserObject();
					startLocation = temp.getParentFile();
				}
                JFileChooser fc = new JFileChooser(startLocation);
                fc.setFileFilter(new ExtensionFileFilter("mgf", false));
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fc.setMultiSelectionEnabled(true);
                int result = fc.showOpenDialog(ClientFrame.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File[] selFiles = fc.getSelectedFiles();
                    int newLeaves = 0;
                    
                    for (File file : selFiles) {
                    	// Add the files for the DB search option
                    	files.add(file);
                    	ArrayList<Long> spectrumPositions = new ArrayList<Long>();
                    	try {
                    		MascotGenericFileReader reader = new MascotGenericFileReader(file);
                    		ArrayList<MascotGenericFile> mgfList = (ArrayList<MascotGenericFile>) reader.getSpectrumFiles(false);
                    		spectrumPositions.addAll(reader.getSpectrumPositions());
                        	specPosMap.put(file.getAbsolutePath(), spectrumPositions);
                    		
                    		DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(file);
                    		
                        	// append new file node to root
                        	treeModel.insertNodeInto(fileNode, treeRoot, treeRoot.getChildCount());
                        	selectionModel.removeSelectionPath(new TreePath(fileNode.getPath()));
                        	int index = 1;
                        	ArrayList<TreePath> toBeAdded = new ArrayList<TreePath>();
                    		
                        	for (MascotGenericFile mgf : mgfList) {
                        		// append new spectrum node to file node
                        		DefaultMutableTreeNode spectrumNode = new DefaultMutableTreeNode(index);
                        		treeModel.insertNodeInto(spectrumNode, fileNode, fileNode.getChildCount());
                        		
                        		// examine spectrum regarding filter criteria
                        		int numPeaks = mgf.getPeakList().size();
        						double TIC = mgf.getTotalIntensity();
        						double SNR = 1.0/mgf.getSNR();
                        		TreePath treePath = new TreePath(spectrumNode.getPath());
                        		if ((numPeaks > minPeaks) && (TIC > minTIC) && (SNR > minSNR)) {
                        			toBeAdded.add(treePath);
                        		}
    							newLeaves++;
    							index++;
    						}
                        	selectionModel.addSelectionPaths((TreePath[]) toBeAdded.toArray(new TreePath[0]));
                    	} catch (Exception x) {
                    		x.printStackTrace();
                    	}
					}
                    tree.expandRow(0);
                	
                	procPrg.setValue(0);
                    
                	String str;
                	int numFiles;
                	str = filesTtf.getText();
                	str = str.substring(0, str.indexOf(" "));
                	numFiles = Integer.parseInt(str) + selFiles.length;
                	
                    filesTtf.setText(numFiles + " file(s) selected");
                    
                    filesLbl.repaint();
                    log.info("Added " + newLeaves + " file(s).");
                    if (numFiles > 0) {
                    	if (connectedToServer) {
                    		sendBtn.setEnabled(true);
                    	}
                    	clrBtn.setEnabled(true);
                    }
                }
            }
	    } );
		
		clrBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				files.clear();
				specPosMap.clear();
				filesTtf.setText("0 file(s) selected");
				filesLbl.setText("0 of 0 spectra selected");
				procPrg.setValue(0);
				clrBtn.setEnabled(false);
				treeRoot.removeAllChildren();
				treeModel.reload();
				selectionModel.clearSelection();
				selectionModel.addSelectionPath(new TreePath(treeRoot));
			}
		});
	    
	    // Input 
	    selPnl.add(fileLbl, cc.xy(2,1));
	    selPnl.add(filesTtf, cc.xy(4,1));
	    selPnl.add(addBtn, cc.xy(6,1));
	    selPnl.add(clrBtn, cc.xy(8,1));
	    
	    selPnl.add(mainSplit, cc.xyw(2,3,7));
	    
	    filePnl.add(selPnl, cc.xy(2,2));
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
		setPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p, 5dlu, p:g, 5dlu, p, 5dlu",
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
					if (files.size() > 0) {
						sendBtn.setEnabled(true);
					}
			}
		});

	    sendBtn = new JButton("Send files");
	    sendBtn.setEnabled(false);
	    sendBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File [] fileArray = new File[files.size()];
				files.toArray(fileArray);
				try {
					client.sendFiles(fileArray);
					
					//files.clear();
					filesTtf.setText(files.size() + " file(s) selected");
	                sendBtn.setEnabled(false);	                    
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

	    setPnl.add(startBtn, cc.xyw(6,3,3));
	    setPnl.add(sendBtn, cc.xy(8,5));
	    
//	    filesLbl = new JLabel("0 of 0 file(s) selected");
	    setPnl.add(filesLbl, cc.xyw(2,5,5,"r,c"));
	    
	    srvPnl.add(setPnl, cc.xy(2,2));
	}

	/**
	 * Construct the processing panel.
	 */
	private void constructSpecLibSearchPanel() {
		
		specLibPnl = new JPanel();
		specLibPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p, 5dlu, p, 5dlu",	// col
		 								 	"5dlu, t:p, 5dlu, p, 5dlu"));		// row
		
		// process button and progress bar
		JPanel procPnl = new JPanel();
		procPnl.setLayout(new FormLayout("5dlu, p, 2dlu, p, 2dlu, p, 5dlu",	// col
		 								 "p, 5dlu, p, 5dlu, p, 5dlu"));		// row	
		procPnl.setBorder(BorderFactory.createTitledBorder("Process data"));	
	    
	    procBtn = new JButton("Process");	    
	    procBtn.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				procBtn.setEnabled(false);
				ProcessWorker worker = new ProcessWorker();
				worker.addPropertyChangeListener(new PropertyChangeListener() {
		        	@Override
					public void propertyChange(PropertyChangeEvent evt) {
		        		if ("progress" == evt.getPropertyName()) {
		        			int progress = (Integer) evt.getNewValue();
		        			procPrg.setValue(progress);
		        		} 

		        	}
		        });
				worker.execute();
			}
		});	    
	   
	    
	    procPrg = new JProgressBar(0, 100);
	    procPrg.setStringPainted(true);
	    
	    packSpn = new JSpinner(new SpinnerNumberModel(100, 1, null, 1));
		packSpn.setPreferredSize(new Dimension((int) (packSpn.getPreferredSize().width*1.75),
				 									  packSpn.getPreferredSize().height));
		packSpn.setToolTipText("Number of spectra per transfer package");

		procPnl.add(new JLabel("packageSize ="), cc.xy(2,1));
		procPnl.add(packSpn, cc.xy(4,1));
		procPnl.add(new JLabel("spectra"), cc.xy(6,1));
	    procPnl.add(procBtn, cc.xyw(2,3,5));
	    procPnl.add(procPrg, cc.xyw(2,5,5));
	    
	    // database search parameters
	    JPanel paraDbPnl = new JPanel();
		paraDbPnl.setLayout(new FormLayout("5dlu, p, 2dlu, p, 2dlu, p, 5dlu",	// col
		 								   "p, 5dlu, p, 5dlu"));				// row	
		paraDbPnl.setBorder(BorderFactory.createTitledBorder("Search parameters"));
	    
		tolMzSpn = new JSpinner(new SpinnerNumberModel(5.0, 0.0, null, 0.1));
		tolMzSpn.setPreferredSize(new Dimension((int) (tolMzSpn.getPreferredSize().width*1.75),
													   tolMzSpn.getPreferredSize().height));
		tolMzSpn.setEditor(new JSpinner.NumberEditor(tolMzSpn, "0.00"));
		tolMzSpn.setToolTipText("Precursor mass tolerance");
		
		annotChk = new JCheckBox("Annotated only", true);
		annotChk.setHorizontalTextPosition(JCheckBox.LEFT);
		annotChk.setToolTipText("Search only annotated spectra");

		paraDbPnl.add(new JLabel("tolMz =", JLabel.RIGHT), cc.xy(2,1));
		paraDbPnl.add(tolMzSpn, cc.xy(4,1));
		paraDbPnl.add(new JLabel("Da"), cc.xy(6,1));
		paraDbPnl.add(annotChk, cc.xyw(2,3,5));
		
		// similarity scoring parameters
		JPanel paraScPnl = new JPanel();
		paraScPnl.setLayout(new FormLayout("5dlu, p, 2dlu, p, 2dlu, p, 5dlu",	// col
		   								   "p, 5dlu, p, 5dlu, p, 5dlu"));			// row	
		paraScPnl.setBorder(BorderFactory.createTitledBorder("Scoring parameters"));
		
		kSpn = new JSpinner(new SpinnerNumberModel(20, 1, null, 1));
		kSpn.setPreferredSize(new Dimension((int) (kSpn.getPreferredSize().width*1.75),
				 								   kSpn.getPreferredSize().height));
		kSpn.setToolTipText("Pick up to k highest peaks");

		thMzSpn = new JSpinner(new SpinnerNumberModel(0.5, 0.0, null, 0.1));
		thMzSpn.setPreferredSize(new Dimension((int)(thMzSpn.getPreferredSize().width*1.75),
													 thMzSpn.getPreferredSize().height));
		thMzSpn.setEditor(new JSpinner.NumberEditor(thMzSpn, "0.00"));
		thMzSpn.setToolTipText("Peak mass tolerance");

		thScSpn = new JSpinner(new SpinnerNumberModel(0.5, 0.0, 1.0, 0.1));
		thScSpn.setEditor(new JSpinner.NumberEditor(thScSpn, "0.00"));
		thScSpn.setToolTipText("Score threshold");
		
		paraScPnl.add(new JLabel("k =", JLabel.RIGHT), cc.xy(2,1));
		paraScPnl.add(kSpn, cc.xy(4,1));
		paraScPnl.add(new JLabel("threshMz =", JLabel.RIGHT), cc.xy(2,3));
		paraScPnl.add(thMzSpn, cc.xy(4,3));
		paraScPnl.add(new JLabel("Da"), cc.xy(6,3));
		paraScPnl.add(new JLabel("threshSc =", JLabel.RIGHT), cc.xy(2,5));
		paraScPnl.add(thScSpn, cc.xy(4,5));
		
	    // add everything to parent panel
	    specLibPnl.add(procPnl, cc.xy(2,2));
	    specLibPnl.add(paraDbPnl, cc.xy(4,2));
	    specLibPnl.add(paraScPnl, cc.xy(6,2));
	}
	
	/**
	 * Construct the database search panel.
	 */
	private void constructDatabaseSearchPanel() {
		
		msmsPnl = new JPanel();
		msmsPnl.setLayout(new FormLayout("5dlu, p, 10dlu, p",
										 "5dlu, f:p, 5dlu, p, 5dlu, t:p, 5dlu"));	
		
		// Protein Database Panel
	    final JPanel protDatabasePnl = new JPanel();
	    protDatabasePnl.setLayout(new FormLayout("5dlu, p, 15dlu, p, 5dlu", "5dlu, p, 5dlu"));		
	    protDatabasePnl.setBorder(BorderFactory.createTitledBorder("Protein Database"));	
	    
	    // FASTA file Label
	    final JLabel fastaFileLbl = new JLabel("FASTA File:");
	    protDatabasePnl.add(fastaFileLbl, cc.xy(2, 2));
	    
	    // FASTA file ComboBox
        fastaFileCbx = new JComboBox(Constants.FASTA_DB);
	    protDatabasePnl.add(fastaFileCbx, cc.xy(4, 2));
	    
		// Parameters Panel
	    final JPanel paramsPnl = new JPanel();
	    paramsPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p, 2dlu, p, 5dlu", "5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));		
	    paramsPnl.setBorder(BorderFactory.createTitledBorder("Parameters"));	
	    
	    // Precursor ion tolerance Label
	    final JLabel precTolLbl = new JLabel("Precursor Ion Tolerance:");
	    paramsPnl.add(precTolLbl, cc.xy(2, 2));
	    
	    // Precursor ion tolerance Spinner
		precTolSpn = new JSpinner(new SpinnerNumberModel(1.0, 0.0, 10.0, 0.1));
		precTolSpn.setEditor(new JSpinner.NumberEditor(precTolSpn, "0.0"));
		precTolSpn.setToolTipText("Precursor Ion Tolerance:");	    
		paramsPnl.add(precTolSpn, cc.xy(4, 2));
		paramsPnl.add(new JLabel("Da"), cc.xy(6,2));
		
	    // Fragment ion tolerance Label
	    final JLabel fragTolLbl = new JLabel("Fragment Ion Tolerance:");
	    paramsPnl.add(fragTolLbl, cc.xy(2, 4));
	    
	    // Fragment ion tolerance Spinner
		fragTolSpn = new JSpinner(new SpinnerNumberModel(0.5, 0.0, 10.0, 0.1));
		fragTolSpn.setEditor(new JSpinner.NumberEditor(fragTolSpn, "0.0"));
		fragTolSpn.setToolTipText("Fragment Ion Tolerance:");	    
		paramsPnl.add(fragTolSpn, cc.xy(4, 4));
		paramsPnl.add(new JLabel("Da"), cc.xy(6,4));
		
		// Missed cleavages Label
	    final JLabel missClvLbl = new JLabel("Missed Cleavages (max):");
	    paramsPnl.add(missClvLbl, cc.xy(2, 6));
	    
	    // Missed cleavages Spinner
		missClvSpn = new JSpinner(new SpinnerNumberModel(2, 0, 10, 1));
		//missClvSpn.setEditor(new JSpinner.NumberEditor(fragTolSpn, "0"));
		missClvSpn.setToolTipText("Maximum number of missed cleavages:");	    
		paramsPnl.add(missClvSpn, cc.xy(4, 6));
		
		// Enzyme Label
	    final JLabel enzymeLbl = new JLabel("Enzyme (Protease):");
	    paramsPnl.add(enzymeLbl, cc.xy(2, 8));
	    
	    // Enzyme ComboBox
        enzymeCbx = new JComboBox(Constants.ENZYMES);
        paramsPnl.add(enzymeCbx, cc.xy(4, 8));
	    
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
		        			procPrg.setValue(progress);
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
		if(xTandemCbx.isSelected()) settings.setXTandem(true);
		if(omssaCbx.isSelected()) settings.setOmssa(true);
		if(cruxCbx.isSelected()) settings.setCrux(true);
		if(inspectCbx.isSelected()) settings.setInspect(true);
		return settings;
	}

	private SpectrumTree queryTree;
	
	/**
	 * Construct the results panel.
	 */
	private void constructResultsPanel() {
		
		resPnl = new JPanel();
		resPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu",		// col
		 								"5dlu, f:p:g, 5dlu"));	// row
		
		JPanel dispPnl = new JPanel();
		dispPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu",		// col
		 								 "f:p:g, 5dlu"));		// row
		dispPnl.setBorder(BorderFactory.createTitledBorder("Results"));
		
//		queryTbl = new JTable(new FileTableModel(TableType.FILE_LIST));
//		queryTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//		queryTbl.setShowVerticalLines(false);
//		queryTbl.setAutoCreateRowSorter(true);
//		queryTbl.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
//		queryTbl.getColumnModel().getColumn(0).setPreferredWidth(1000);
//	    packColumn(queryTbl, 1, 10);
//	    packColumn(queryTbl, 2, 10);
//		
//		JScrollPane queryScpn = new JScrollPane(queryTbl);

		DefaultMutableTreeNode queryRoot = new DefaultMutableTreeNode(((DefaultMutableTreeNode) fileTree.getModel().getRoot()).getUserObject());
//		queryTree = new JTree(new DefaultTreeModel(queryRoot));
		queryTree = new SpectrumTree(new DefaultTreeModel(queryRoot), TreeType.RESULT_LIST);
		queryTree.setRowHeight(new JCheckBox().getPreferredSize().height);
		
		JScrollPane queryScpn = new JScrollPane(queryTree);
		queryScpn.setPreferredSize(new Dimension(200, 400));
		
		JPanel leftPnl = new JPanel();
		leftPnl.setLayout(new FormLayout("p:g",					// col
										 "p, 5dlu, p, f:p:g"));	// row
		JTable leftDummyTable = new JTable(null, new Vector<String>(Arrays.asList(new String[] {"Files"})));
		leftDummyTable.getTableHeader().setReorderingAllowed(false);
		leftDummyTable.getTableHeader().setResizingAllowed(false);
		JScrollPane leftDummyScpn = new JScrollPane(leftDummyTable);
		leftDummyScpn.setPreferredSize(leftDummyTable.getTableHeader().getPreferredSize());

		JLabel leftLbl = new JLabel("Query spectra");
		leftLbl.setPreferredSize(new Dimension(leftLbl.getPreferredSize().width,
											   new JButton(" ").getPreferredSize().height));
		leftPnl.add(leftLbl, cc.xy(1,1));
		leftPnl.add(leftDummyScpn, cc.xy(1,3));
		leftPnl.add(queryScpn, cc.xy(1,4));
				
		libTbl = new JTable(new DefaultTableModel() {
			// instance initializer block
			{ setColumnIdentifiers(new Object[] {"#","Sequence","Accession","Score"}); }
			
			public boolean isCellEditable(int row, int col) {
				return false;
		    }
		});
		libTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		libTbl.setAutoCreateRowSorter(true);
		libTbl.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
	    packColumn(libTbl,0,10);
		libTbl.getColumnModel().getColumn(1).setPreferredWidth(1000);
		libTbl.getColumnModel().getColumn(2).setPreferredWidth(1000);
	    packColumn(libTbl,3,10);
	    
	    libTbl.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
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
	    
		JScrollPane libScpn = new JScrollPane(libTbl);
		libScpn.setPreferredSize(new Dimension(300, 200));
		
		mPlot = new MultiPlotPanel();
		mPlot.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		mPlot.setPreferredSize(new Dimension(350, 200));
		ArrayList<Color> colors = new ArrayList<Color>();
		colors.add(Color.RED);
		colors.add(Color.BLUE);
		mPlot.setLineColors(colors);
		mPlot.setK(20);
		
		libTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					if (libTbl.getSelectedRowCount() > 0) {
						int row = libTbl.convertRowIndexToModel(libTbl.getSelectedRow());
						DefaultTableModel libTblMdl = (DefaultTableModel) libTbl.getModel();
						int index = (Integer) libTblMdl.getValueAt(row, 0);
						mPlot.setSecondSpectrum(resultList.get(index-1).getSpectrumFile());
						mPlot.repaint(true);
					}
				}
			}
		});
		
		JButton expBtn = new JButton("export scores");
//		expBtn.setPreferredSize(new Dimension(expBtn.getPreferredSize().width,
//											  new JLabel(" ").getPreferredSize().height));
		expBtn.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (libTbl.getRowCount() > 0) {
					try {
						FileOutputStream fos = new FileOutputStream(new File("scores.txt"));
						OutputStreamWriter osw = new OutputStreamWriter(fos);

						for (int row = 0; row < libTbl.getRowCount(); row++) {
							osw.write(String.valueOf((Double)libTbl.getValueAt(row,3)) + "\n");
						}
						osw.close();
						fos.close();
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		JPanel rightPnl = new JPanel();
		rightPnl.setLayout(new FormLayout("l:p:g, r:p",			// col
		 								  "p, 5dlu, f:p:g"));	// row
		rightPnl.add(new JLabel("Library spectra"), cc.xy(1,1));
		rightPnl.add(expBtn, cc.xy(2,1));
		rightPnl.add(libScpn, cc.xyw(1,3,2));	
		
		JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, rightPnl, mPlot);
		rightSplit.setContinuousLayout(true);
		BasicSplitPaneDivider divider = ((BasicSplitPaneUI) rightSplit.getUI()).getDivider();
		if (divider != null) { divider.setBorder(null); }
		
//		JPanel leftPnl = new JPanel();
//		leftPnl.setLayout(new FormLayout("f:p:g",				// col
//		 								  "p, 5dlu, f:p:g"));	// row
//		leftPnl.add(new JLabel("Query spectra"), cc.xy(1,1));
//		leftPnl.add(queryScpn, cc.xy(1,3));	
		
		JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPnl, rightSplit);
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
		logPnl = new LogPanel();
		logPnl.setPreferredSize(new Dimension(300, 200));
		
		brdPnl.add(logPnl, cc.xy(2, 2));
		lggPnl.add(brdPnl, cc.xy(2, 2));
		
	}
	
	
	private enum TreeType { FILE_SELECT, RESULT_LIST }
	
	private class SpectrumTree extends JTree 
							   implements TreeSelectionListener {
		
		private TreeType treeType;
		private File lastSelectedFile;
		private MascotGenericFileReader reader;
		
		public SpectrumTree(TreeModel treeModel, TreeType treeType) {
			super(treeModel);
			this.treeType = treeType;
			this.addTreeSelectionListener(this);
		}
		
		public String convertValueToText(Object value, boolean selected, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {
			Object obj = ((DefaultMutableTreeNode)value).getUserObject();
			if (obj instanceof File) {
				return ((File)obj).getName();
			} else if (leaf && !((DefaultMutableTreeNode)value).isRoot()) {
				return ("Spectrum " + obj);
			} else {
				return value.toString();
			}	    			
		}
		
		public MascotGenericFile getSpectrumAt(DefaultMutableTreeNode node) throws IOException {
			if (!node.isRoot() && node.isLeaf()) {
				DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
				File mgfFile = (File) parent.getUserObject();
				if (!mgfFile.equals(this.lastSelectedFile)) {	// check whether new file has been selected
					this.lastSelectedFile = mgfFile;			// and open new reader if true
					if (this.reader != null) {
						this.reader.close();
					}
					this.reader = new MascotGenericFileReader(mgfFile,MascotGenericFileReader.NONE);
				}
				int index = (Integer) node.getUserObject();
				long pos = specPosMap.get(mgfFile.getAbsolutePath()).get(index-1);
				return reader.loadNthSpectrum(index, pos);
			}
			return null;
		}

		@Override
		public void valueChanged(TreeSelectionEvent tse) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) this.getLastSelectedPathComponent();
			MascotGenericFile mgf = null;

			if (node == null) {
				return;	// nothing is selected.
			} else {
				try {
					mgf = getSpectrumAt(node);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			switch (treeType) {
			case FILE_SELECT:
				refreshFileTable(mgf, node);
				break;
			case RESULT_LIST:
				refreshResultsTable(mgf);
				break;
			}
			
		}
	}
	
	protected void refreshFileTable(MascotGenericFile mgf, DefaultMutableTreeNode selNode) {
		if (mgf != null) {
			DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selNode.getParent();
			File parentFile = (File) parent.getUserObject();
			fileDetailsTbl.setValueAt(parentFile.getAbsolutePath(), 0, 1);
			fileDetailsTbl.setValueAt(mgf.getTitle(), 1, 1);

			ArrayList<Peak> peakList = mgf.getPeakList();

			int numPeaks = peakList.size();
			fileDetailsTbl.setValueAt("<html><font color=#" + ((numPeaks < minPeaks) ? "FF" : "00") +
					"0000>" + numPeaks + "</font></html>", 2, 1);

			double TIC = mgf.getTotalIntensity();
			fileDetailsTbl.setValueAt("<html><font color=#" + ((TIC < minTIC) ? "FF" : "00") +
					"0000>" + TIC + "</font></html>", 3, 1);

			//			double SNR = mgf.getHighestIntensity() / (TIC / numPeaks);
			double SNR = 1.0/mgf.getSNR();
			fileDetailsTbl.setValueAt("<html><font color=#" + ((SNR < minSNR) ? "FF" : "00") +
					"0000>" + SNR + "</font></html>", 4, 1);

			filePlotPnl.setSpectrumFile(mgf,Color.RED);
			filePlotPnl.repaint();
			fileDetailsTbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			packColumn(fileDetailsTbl, 1, 5);
		} else {
			fileDetailsTbl.setValueAt(null, 0, 1);
			fileDetailsTbl.setValueAt(null, 1, 1);
			fileDetailsTbl.setValueAt(null, 2, 1);
			fileDetailsTbl.setValueAt(null, 3, 1);
			fileDetailsTbl.setValueAt(null, 4, 1);
			filePlotPnl.clearSpectrumFile();
			filePlotPnl.repaint();
			fileDetailsTbl.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		}
		packColumn(fileDetailsTbl, 0, 10);
		
	}

	protected void refreshResultsTable(MascotGenericFile mgf) {
		if (mgf != null) {
			// clear library table
			libTbl.clearSelection();
			DefaultTableModel libTblMdl = (DefaultTableModel) libTbl.getModel();
			for (int row = 0; row < libTblMdl.getRowCount(); ) {
				libTblMdl.removeRow(libTblMdl.getRowCount()-1);
			}
			// re-populate library table
			resultList = resultMap.get(mgf.getTitle());
			if (resultList != null) {
				for (int index = 0; index < resultList.size(); index++) {
					libTblMdl.addRow(new Object[] { index+1,
													resultList.get(index).getSequence(),
													resultList.get(index).getAnnotation(),
													resultList.get(index).getScore() } );
				}
			}
			packColumn(libTbl, 0, 10);
			packColumn(libTbl, 3, 10);

			// plot selected spectrum
			mPlot.setFirstSpectrum(mgf);
			mPlot.setSecondSpectrum(null);
			mPlot.repaint();
		}
	}
		
	
	// Sets the preferred width of the visible column specified by vColIndex. The column
	// will be just wide enough to show the column head and the widest cell in the column.
	// margin pixels are added to the left and right
	// (resulting in an additional width of 2*margin pixels).
	protected void packColumn(JTable table, int vColIndex, int margin) {
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



	private class ProcessWorker extends SwingWorker {		
		protected Object doInBackground() throws Exception {			
			// clone file selection tree, discard unselected branches/leaves
			CheckBoxTreeSelectionModel selectionModel = fileTree.getSelectionModel();
			DefaultMutableTreeNode fileRoot = (DefaultMutableTreeNode) fileTree.getModel().getRoot();
			DefaultTreeModel queryModel = (DefaultTreeModel) queryTree.getModel();
			DefaultMutableTreeNode queryRoot = (DefaultMutableTreeNode) queryModel.getRoot();
			queryRoot.removeAllChildren();
			
			for (int i = 0; i < fileRoot.getChildCount(); i++) {
				DefaultMutableTreeNode fileNode = (DefaultMutableTreeNode) fileRoot.getChildAt(i);
				TreePath filePath = new TreePath(fileNode.getPath());
				if ((selectionModel.isPathSelected(filePath)) || 
					(selectionModel.isPartiallySelected(filePath))) {
					// create a new node containing only the file tree node's selected sub-nodes
					DefaultMutableTreeNode fileNodeClone = new DefaultMutableTreeNode(fileNode.getUserObject());
					queryModel.insertNodeInto(fileNodeClone, queryRoot, queryRoot.getChildCount());
					for (int j = 0; j < fileNode.getChildCount(); j++) {
						DefaultMutableTreeNode spectrumNode = (DefaultMutableTreeNode) fileNode.getChildAt(j);
						TreePath spectrumPath = new TreePath(spectrumNode.getPath());
						if (selectionModel.isPathSelected(spectrumPath, true)) {
							DefaultMutableTreeNode spectrumNodeClone = new DefaultMutableTreeNode(spectrumNode.getUserObject());
							queryModel.insertNodeInto(spectrumNodeClone, fileNodeClone, fileNodeClone.getChildCount());
						}
					}
				}
			}
			queryModel.reload();

			// consolidate selected spectra into files
			int packageSize = (Integer) packSpn.getValue();
			FileOutputStream fos = null;
			
			ArrayList<File> files = new ArrayList<File>();
			
			logPnl.append("Packing files... ");
			int numSpectra = 0;

			for (int i = 0; i < queryRoot.getChildCount(); i++) {
				DefaultMutableTreeNode fileNode = (DefaultMutableTreeNode) queryRoot.getChildAt(i);
				for (int j = 0; j < fileNode.getChildCount(); j++) {
					DefaultMutableTreeNode spectrumNode = (DefaultMutableTreeNode) fileNode.getChildAt(j);
					if ((numSpectra % packageSize) == 0) {			// create a new package every x files
						if (fos != null) { fos.close(); }
						File file = new File("batch_" + (numSpectra/packageSize) + ".mgf");
						files.add(file);
						fos = new FileOutputStream(file);
					}
					numSpectra++;
					MascotGenericFile mgf = queryTree.getSpectrumAt(spectrumNode);
					mgf.writeToStream(fos);
					fos.flush();
				}
			}
			fos.close();
			logPnl.append("done.\n");
			
			// process files
			double progress = 0;
			double max = files.size();
			setProgress(0);
			logPnl.append("Processing files...");
			
			ProcessSettings procSet = new ProcessSettings((Double) tolMzSpn.getValue(),
														  (Double) thMzSpn.getValue(),
														  (Integer) kSpn.getValue(),
														  (Double) thScSpn.getValue(),
														  (Boolean) annotChk.isSelected());
			
			resultMap = new HashMap<String, ArrayList<RankedLibrarySpectrum>>();
			for (File file : files) {
				resultMap.putAll(client.process(file,procSet));
				progress++;
				setProgress((int)(progress/max*100));
			}
			logPnl.append("done.\n");
			
			return 0;
		}
		
		@Override
        public void done() {
            procBtn.setEnabled(true);
        }
	}
	
	private class RunDbSearchWorker	extends SwingWorker {
		
		protected Object doInBackground() throws Exception {
			double progress = 0;
			double max = files.size();
			setProgress(0);
			DbSearchSettings settings = collectDBSearchSettings();
			try {
				for (File file : files) {
						// TODO: run the DB search with the settings
						client.runDbSearch(file, settings);
						progress++;
						setProgress((int)(progress/max*100));
				}
				files.clear();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 0;
		}

		@Override
        public void done() {
            procBtn.setEnabled(true);
        }
	}
	
	/**
	 * Main method ==> Entry point to the application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame.setDefaultLookAndFeelDecorated(true);
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
				}
				catch (Exception e) { e.printStackTrace(); }
//				try {
//					SubstanceLookAndFeel.setSkin(new BusinessBlackSteelSkin());
//				}
//				catch (Exception e) { e.printStackTrace(); }
				
				new ClientFrame();
			}
		});
	}
}

