package de.mpa.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.AbstractButton;
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
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.algorithms.RankedLibrarySpectrum;
import de.mpa.client.Client;
import de.mpa.client.DbSearchSettings;
import de.mpa.client.ProcessSettings;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;
import de.mpa.ui.MgfFilter;
import de.mpa.ui.MultiPlotPanel;
import de.mpa.ui.PlotPanel2;
import de.mpa.utils.ExtensionFileFilter;
import de.mpa.webservice.WSPublisher;

@SuppressWarnings("unchecked")
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
	private JLabel filesLbl;
    private JButton sendBtn;
	private JMenuBar menuBar;
	private JMenu menu1;
	private JMenuItem exitItem;
	
	private JButton procBtn;
	private JProgressBar procPrg;
	private JProgressBar searchPrg;
	
//	private ArrayList<MascotGenericFile> spectrumFiles = new ArrayList<MascotGenericFile>();
	
	private boolean connectedToServer = false;
	
	private JTable queryTbl;
	private JTable libTbl;
	private Map<String, ArrayList<RankedLibrarySpectrum>> resultMap;
	
	private Map<String, ArrayList<Integer>> specPosMap = new HashMap<String, ArrayList<Integer>>(1);
	
	private JTable fileTbl;
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
	
	private String projectName = "Copy of New_Project(1)";
	private CheckBoxTreeManager fileTree;
	
	
	/**
	 * Constructor for the ClientFrame
	 */
	public ClientFrame(){
		
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
//        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//
//		this.addWindowListener(new WindowAdapter() {
//			public void windowClosing(WindowEvent e) {
//                System.exit(0);
//            }
//        });
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setResizable(true);
		this.pack();
		
		// Center in the screen
		ScreenConfig.centerInScreen(this);
		
		
		
		this.setVisible(true);
				
	}
	
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
	    
		final PlotPanel2 plotPnl = new PlotPanel2(null);
	    
	    // Tree of loaded spectrum files
	    final DefaultMutableTreeNode treeRoot = new DefaultMutableTreeNode(this.projectName);
	    final DefaultTreeModel treeModel = new DefaultTreeModel(treeRoot);
	    final JTree tree = new JTree(treeModel) {
	    	public String convertValueToText(Object value, boolean selected, boolean expanded,
                    						 boolean leaf, int row, boolean hasFocus) {
	    		Object obj = ((DefaultMutableTreeNode)value).getUserObject();
	    		if (obj instanceof File) {
	    			return ((File)obj).getName();
	    		} else {
	    			return value.toString();
	    		}	    			
	    	}
	    };
	    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
	    tree.addTreeSelectionListener(new TreeSelectionListener() {
			
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();

				if (node == null) { return; }	// nothing is selected.

				if (node.isLeaf()) {
					if (node.isRoot()) { return; } // root still has no children, counts as leaf
					// populate file details table
					DefaultMutableTreeNode parent = (DefaultMutableTreeNode)node.getParent();
					File mgfFile = (File)parent.getUserObject();
					int index = (Integer)node.getUserObject();
					int pos = specPosMap.get(mgfFile.getAbsolutePath()).get(index-1);
					try {
						MascotGenericFileReader reader = new MascotGenericFileReader(mgfFile);
						MascotGenericFile mgf = reader.loadNthSpectrum(mgfFile, index, pos);
						fileTbl.setValueAt(mgfFile.getAbsolutePath(), 0, 1);
						fileTbl.setValueAt(mgf.getTitle(), 1, 1);
						fileTbl.setValueAt(mgf.getPeakList().size(), 2, 1);
						fileTbl.setValueAt(mgf.getTotalIntensity(), 3, 1);
						plotPnl.setSpectrumFile(mgf,Color.RED);
	            		plotPnl.repaint();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
//					System.out.println(parent.getUserObject() + " " + nodeInfo);
				} else {
					fileTbl.setValueAt(null, 0, 1);
					fileTbl.setValueAt(null, 1, 1);
					fileTbl.setValueAt(null, 2, 1);
					fileTbl.setValueAt(null, 3, 1);
        			plotPnl.clearSpectrumFile();
        			plotPnl.repaint();
//					System.out.println("illegal selection!");
				}

			}
		});
		fileTree = new CheckBoxTreeManager(tree);
		JScrollPane treePane = new JScrollPane(tree);
		treePane.setPreferredSize(new Dimension(0, 0));
		
		// left part of outer split pane
		JPanel leftPnl = new JPanel();
		leftPnl.setLayout(new FormLayout("p:g",			// col
										 "p, f:p:g"));	// row
		JTable leftDummyTable = new JTable(null, new Vector<String>(Arrays.asList(new String[] {"Files"})));
		leftDummyTable.getTableHeader().setReorderingAllowed(false);
		leftDummyTable.getTableHeader().setResizingAllowed(false);
		JScrollPane leftDummyScpn = new JScrollPane(leftDummyTable);
		leftDummyScpn.setPreferredSize(leftDummyTable.getTableHeader().getPreferredSize());

		leftPnl.add(leftDummyScpn, cc.xy(1,1));
		leftPnl.add(treePane, cc.xy(1,2));
		
		// top part of right-hand inner split pane
		JPanel topRightPnl = new JPanel();
		topRightPnl.setLayout(new FormLayout("p:g",			// col
											 "p, f:p:g"));	// row
		JTable rightDummyTable = new JTable(null, new Vector<String>(Arrays.asList(new String[] {"Details"})));
		rightDummyTable.getTableHeader().setReorderingAllowed(false);
		rightDummyTable.getTableHeader().setResizingAllowed(false);
		JScrollPane rightDummyScpn = new JScrollPane(rightDummyTable);
		rightDummyScpn.setPreferredSize(rightDummyTable.getTableHeader().getPreferredSize());
		
		String[] columnNames = {null, null};
		Object[][] data = {
			    {"File path",			null},
			    {"Spectrum title",		null},
			    {"Number of peaks",		null},
			    {"Total ion current",	null},
			};
		fileTbl = new JTable(new DefaultTableModel(data, columnNames) {
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		});
		fileTbl.getTableHeader().setVisible(false);
		fileTbl.getTableHeader().setPreferredSize(new Dimension(0, 0));
//		packColumn(detailTbl, 0, 10);
		
		JScrollPane detailScpn = new JScrollPane(fileTbl);
		detailScpn.setPreferredSize(new Dimension(0, 0));
		detailScpn.setMinimumSize(new Dimension(0, fileTbl.getRowHeight()));

		topRightPnl.add(rightDummyScpn, cc.xy(1,1));
		topRightPnl.add(detailScpn, cc.xy(1,2));
		topRightPnl.setMinimumSize(new Dimension(0, rightDummyTable.getTableHeader().getPreferredSize().height + 
													fileTbl.getRowHeight() + 2));
		
		JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topRightPnl, plotPnl);
		rightSplit.setMinimumSize(new Dimension(100, 0));
		rightSplit.setDividerLocation(rightDummyTable.getTableHeader().getPreferredSize().height + 
									  fileTbl.getRowCount()*fileTbl.getRowHeight() + 2);
		BasicSplitPaneDivider rightDivider = ((BasicSplitPaneUI) rightSplit.getUI()).getDivider();
		if (rightDivider != null) { rightDivider.setBorder(null); }
		
		JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPnl, rightSplit);
		mainSplit.setDividerLocation(200);
		BasicSplitPaneDivider mainDivider = ((BasicSplitPaneUI) mainSplit.getUI()).getDivider();
		if (mainDivider != null) { mainDivider.setBorder(null); }

//	    // Table of loaded spectrum files
//	    fileTbl = new JTable(new FileTableModel(TableType.FILE_SELECT));
//	    fileTbl.setShowVerticalLines(false);
//	    fileTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//	    fileTbl.getColumnModel().getColumn(0).setPreferredWidth(1000);
//	    packColumn(fileTbl, 1, 2);
//	    
//		final PlotPanel2 plotPnl = new PlotPanel2(null);
//		
//	    final FileTableModel fileTblMdl = (FileTableModel) fileTbl.getModel();
//	    
//	    fileTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {			
//			@Override
//			public void valueChanged(ListSelectionEvent e) {
//                // If the mouse button has been released
//	            if (!e.getValueIsAdjusting()) {
//		            // If cell selection is enabled, both row and column change events are fired
//		        	if (e.getSource() == fileTbl.getSelectionModel() &&
//		        			fileTbl.getRowSelectionAllowed()) {
//		        		// Column selection changed
//		        		int row = fileTbl.getSelectedRow();
//		        		if (row >= 0) {
//		            		MascotGenericFile mgf = (MascotGenericFile)(fileTblMdl.getMgfAt(row));
//		            		plotPnl.setSpectrumFile(mgf,Color.RED);
//		            		plotPnl.repaint();        			
//		        		} else {
//		        			plotPnl.clearSpectrumFile();
//		        			plotPnl.repaint();
//		        		}
//		        	}
//	            }
//	        }
//		});
//	    
//	    fileTblMdl.addTableModelListener(new TableModelListener() {			
//			@Override
//			public void tableChanged(TableModelEvent e) {
//		        TableModel tblMdl = (TableModel)e.getSource();
//
//		        int row = e.getFirstRow();
//		        if (e.getType() == TableModelEvent.UPDATE) {
//			        // fail-safe against empty table
//			        if (row >= 0) {
//			        	// parse label string
//			        	String text = filesLbl.getText();
//				        int numSelFiles = Integer.parseInt(text.substring(0, text.indexOf(" ")));
//				        // change number of selected files depending on cell content
//				        if ((Boolean)tblMdl.getValueAt(row,2)) {
//							numSelFiles += 1;
//						} else {
//							numSelFiles -= 1;
//						}
//				        // apply label string with updated value
//						filesLbl.setText(numSelFiles + " of " + tblMdl.getRowCount() + " file(s) selected.");
//			        }
//		        }
//		    }
//		});
//	    
//	    TableColumn tCol = fileTbl.getColumnModel().getColumn(2);
////	    tCol.setMinWidth(fileTbl.getRowHeight());
//	    tCol.setMaxWidth((int)(fileTbl.getRowHeight()*1.7));
//	    
//	    class CheckBoxHeaderItemListener implements ItemListener {
//			public void itemStateChanged(ItemEvent e) {
//				Object source = e.getSource();
//				if (source instanceof AbstractButton == false) return;
//				boolean checked = (e.getStateChange() == ItemEvent.SELECTED);
//				for(int x = 0, y = fileTbl.getRowCount(); x < y; x++) {
//					fileTbl.setValueAt(new Boolean(checked),x,2);
//				}
//				
//				int numSelFiles = (checked) ? fileTbl.getRowCount() : 0;
//				filesLbl.setText(numSelFiles + " of " + fileTbl.getRowCount() + " file(s) selected.");
//			}
//		}
//	    tCol.setHeaderRenderer(new CheckBoxHeader(new CheckBoxHeaderItemListener()));
//	    
//	    JScrollPane fileScPn = new JScrollPane(fileTbl);
//	    fileScPn.setPreferredSize(new Dimension(fileScPn.getPreferredSize().width, 200));
//	    
//	    selPnl.add(fileScPn, cc.xyw(2,3,7));
	    
	    // Button action listeners
		addBtn.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
                // First check whether a file has already been selected.
                // If so, start from that file's parent.
				File startLocation = new File(PATH);
//				if (fileTbl.getRowCount() > 0) {
//					File temp = new File((String)fileTblMdl.getTrueValueAt(0,0));
//					startLocation = temp.getParentFile();
//				}
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
                    int newRows = 0;
                    
                    for (File file : selFiles) {
                    	// Add the files for the db search option
                    	files.add(file);
                    	ArrayList<Integer> spectrumPositions = new ArrayList<Integer>();
                    	try {
                    		MascotGenericFileReader reader = new MascotGenericFileReader(file, true);
                    		spectrumPositions.addAll(reader.getSpectrumPositions());
                    	} catch (Exception x) {
                    		x.printStackTrace();
                    	}
                		DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(file);
                    	for (int i = 1; i <= spectrumPositions.size(); i++) {
//							fileTblMdl.addRow(new Object[] {file.getAbsolutePath(), i, true});
                    		DefaultMutableTreeNode spectrumNode = new DefaultMutableTreeNode(i);
                    		fileNode.add(spectrumNode);
							newRows++;
						}
                    	// append new node to root
                    	treeModel.insertNodeInto(fileNode, treeRoot, treeRoot.getChildCount());
                    	specPosMap.put(file.getAbsolutePath(), spectrumPositions);
					}
//                	packColumn(fileTbl, 1, 2);
                    tree.expandRow(0);
                	
                	procPrg.setValue(0);
                    
                	String str;
                	int numFiles;
                	str = filesTtf.getText();
                	str = str.substring(0, str.indexOf(" "));
                	numFiles = Integer.parseInt(str) + selFiles.length;
                	
                    filesTtf.setText(numFiles + " file(s) selected");
                    
                	str = filesLbl.getText();
                	str = str.substring(0, str.indexOf(" "));
                	numFiles = Integer.parseInt(str) + newRows;
//                    filesLbl.setText(numFiles + " of " + fileTblMdl.getRowCount() + " file(s) selected.");
                    log.info("Added " + numFiles + " file(s).");
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
				treeRoot.removeAllChildren();
				treeModel.reload();
//				fileTbl.clearSelection();
//				for (int row = 0; row < fileTblMdl.getRowCount(); ) {
//					fileTblMdl.removeRow(fileTblMdl.getRowCount()-1);
//				}
////				files.clear();
				specPosMap.clear();
				filesTtf.setText("0 file(s) selected");
				filesLbl.setText("0 of 0 file(s) selected");
				procPrg.setValue(0);
				clrBtn.setEnabled(false);
			}
		});
	    
	    // Input 
	    selPnl.add(fileLbl, cc.xy(2,1));
	    selPnl.add(filesTtf, cc.xy(4,1));
	    selPnl.add(addBtn, cc.xy(6,1));
	    selPnl.add(clrBtn, cc.xy(8,1));
	    
	    selPnl.add(mainSplit, cc.xyw(2,3,7));
	    
	    filePnl.add(selPnl, cc.xy(2,2));
	    
//	    JPanel detPnl = new JPanel();
//		detPnl.setBorder(new TitledBorder("File Details"));
//		detPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu",
//									   "f:p:g, 5dlu"));
//		
//		plotPnl.setPreferredSize(new Dimension(300, 200));
//		detPnl.add(plotPnl, cc.xy(2,1));
//		
//	    filePnl.add(detPnl, cc.xy(2,4));
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
	    
	    filesLbl = new JLabel("0 of 0 file(s) selected");
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
	    
	    // FASTA file label
	    final JLabel fastaFileLbl = new JLabel("FASTA File:");
	    protDatabasePnl.add(fastaFileLbl, cc.xy(2, 2));
	    
	    // Fasta file combobox
        fastaFileCbx = new JComboBox(Constants.FASTA_DB);
	    protDatabasePnl.add(fastaFileCbx, cc.xy(4, 2));
	    
		// Parameters Panel
	    final JPanel paramsPnl = new JPanel();
	    paramsPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p, 2dlu, p, 5dlu", "5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));		
	    paramsPnl.setBorder(BorderFactory.createTitledBorder("Parameters"));	
	    
	    // Precursor ion tolerance label
	    final JLabel precTolLbl = new JLabel("Precursor Ion Tolerance:");
	    paramsPnl.add(precTolLbl, cc.xy(2, 2));
	    
	    // Precursor ion tolerance field
		precTolSpn = new JSpinner(new SpinnerNumberModel(1.0, 0.0, 10.0, 0.1));
		precTolSpn.setEditor(new JSpinner.NumberEditor(precTolSpn, "0.0"));
		precTolSpn.setToolTipText("Precursor Ion Tolerance:");	    
		paramsPnl.add(precTolSpn, cc.xy(4, 2));
		paramsPnl.add(new JLabel("Da"), cc.xy(6,2));
		
	    // Fragment ion tolerance label
	    final JLabel fragTolLbl = new JLabel("Fragment Ion Tolerance:");
	    paramsPnl.add(fragTolLbl, cc.xy(2, 4));
	    
	    // Fragment ion tolerance field
		fragTolSpn = new JSpinner(new SpinnerNumberModel(0.5, 0.0, 10.0, 0.1));
		fragTolSpn.setEditor(new JSpinner.NumberEditor(fragTolSpn, "0.0"));
		fragTolSpn.setToolTipText("Fragment Ion Tolerance:");	    
		paramsPnl.add(fragTolSpn, cc.xy(4, 4));
		paramsPnl.add(new JLabel("Da"), cc.xy(6,4));
		
		// Missed cleavages label
	    final JLabel missClvLbl = new JLabel("Missed Cleavages (max):");
	    paramsPnl.add(missClvLbl, cc.xy(2, 6));
	    
	    // Missed cleavages field
		missClvSpn = new JSpinner(new SpinnerNumberModel(2, 0, 10, 1));
		//missClvSpn.setEditor(new JSpinner.NumberEditor(fragTolSpn, "0"));
		missClvSpn.setToolTipText("Maximum number of missed cleavages:");	    
		paramsPnl.add(missClvSpn, cc.xy(4, 6));
		
		// Enzyme Label
	    final JLabel enzymeLbl = new JLabel("Enzyme (Protease):");
	    paramsPnl.add(enzymeLbl, cc.xy(2, 8));
	    
	    // Enzyme Combobox
        enzymeCbx = new JComboBox(Constants.ENZYMES);
        paramsPnl.add(enzymeCbx, cc.xy(4, 8));
	    
        // Search Engine Panel
	    final JPanel searchEngPnl = new JPanel();
	    searchEngPnl.setLayout(new FormLayout("5dlu, p, 10dlu, p, 10dlu, p, 5dlu", "5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));		
	    searchEngPnl.setBorder(BorderFactory.createTitledBorder("Search Engines"));	
	    
	    // X!Tandem label
	    final JLabel xTandemLbl = new JLabel("X!Tandem:");
	    searchEngPnl.add(xTandemLbl, cc.xy(2, 2));
	    
	    // X!Tandem checkbox
	    xTandemCbx = new JCheckBox();
	    xTandemCbx.setSelected(true);
	    searchEngPnl.add(xTandemCbx, cc.xy(4, 2));
	    // TODO: Add action listener
	    xTandemSetBtn = new JButton("Advanced Settings");
	    xTandemSetBtn.setEnabled(false);
	    searchEngPnl.add(xTandemSetBtn, cc.xy(6, 2));
	    
	    // OMSSA label
	    final JLabel omssaLbl = new JLabel("OMSSA:");
	    searchEngPnl.add(omssaLbl, cc.xy(2, 4));
	    
	    // OMSSA checkbox
	    omssaCbx = new JCheckBox();
	    omssaCbx.setSelected(true);
	    searchEngPnl.add(omssaCbx, cc.xy(4, 4));
	    // TODO: Add action listener
	    omssaSetBtn = new JButton("Advanced Settings");
	    omssaSetBtn.setEnabled(false);
	    searchEngPnl.add(omssaSetBtn, cc.xy(6, 4));
	    
	    // Crux label
	    final JLabel cruxLbl = new JLabel("Crux:");
	    searchEngPnl.add(cruxLbl, cc.xy(2, 6));
	    
	    // InsPecT label
	    final JLabel inspectLbl = new JLabel("InsPecT:");
	    searchEngPnl.add(inspectLbl, cc.xy(2, 8));
	    
	    // Crux checkbox
	    cruxCbx = new JCheckBox();
	    cruxCbx.setSelected(true);
	    searchEngPnl.add(cruxCbx, cc.xy(4, 6));
	    // TODO: Add action listener
	    cruxSetBtn = new JButton("Advanced Settings");
	    cruxSetBtn.setEnabled(false);
	    searchEngPnl.add(cruxSetBtn, cc.xy(6, 6));
	    
	    // InsPecT checkbox
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
	    
	    // Search type label
	    final JLabel searchTypeLbl = new JLabel("Type:");
	    runPnl.add(searchTypeLbl, cc.xy(2, 2));
	    
	    // Search type combobox
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
	    
	    // Status panel
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
	    
	    // Status details panel
	    final JPanel statusDetailsPnl = new JPanel();
	    statusDetailsPnl.setLayout(new FormLayout("5dlu, p, 10dlu, p, 5dlu", "5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));		
	    statusDetailsPnl.setBorder(BorderFactory.createTitledBorder("Search Details"));	
	    
	    // X!Tandem status label
	    final JLabel xtandemStatLbl = new JLabel("X!Tandem:");
	    statusDetailsPnl.add(xtandemStatLbl, cc.xy(2, 2));
	    
	    // X!Tandem status textfield
	    xtandemStatTtf = new JTextField(15);
	    xtandemStatTtf.setEditable(false);
	    xtandemStatTtf.setEnabled(false);
	    statusDetailsPnl.add(xtandemStatTtf, cc.xy(4, 2));
	    
	    // OMSSA status label
	    final JLabel omssaStatLbl = new JLabel("OMSSA:");
	    statusDetailsPnl.add(omssaStatLbl, cc.xy(2, 4));
	    
	    // OMSSA status textfield
	    omssaStatTtf = new JTextField(15);
	    omssaStatTtf.setEditable(false);
	    omssaStatTtf.setEnabled(false);
	    statusDetailsPnl.add(omssaStatTtf, cc.xy(4, 4));
	    
	    // Crux status label
	    final JLabel cruxStatLbl = new JLabel("Crux:");
	    statusDetailsPnl.add(cruxStatLbl, cc.xy(2, 6));
	    
	    // Crux status textfield
	    cruxStatTtf = new JTextField(15);
	    cruxStatTtf.setEditable(false);
	    cruxStatTtf.setEnabled(false);
	    statusDetailsPnl.add(cruxStatTtf, cc.xy(4, 6));
	    
	    // InsPecT status label
	    final JLabel inspectStatLbl = new JLabel("InsPecT:");
	    statusDetailsPnl.add(inspectStatLbl, cc.xy(2, 8));
	    
	    // InsPecT status textfield
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
	
	/**
	 * Construct the results panel.
	 */
	private void constructResultsPanel() {
		
		resPnl = new JPanel();
		resPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu",		// col
		 								"5dlu, f:p:g, 5dlu"));	// row
		
		JPanel dispPnl = new JPanel();
		dispPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu",	// col
		 								 "f:p:g, 5dlu"));		// row
		dispPnl.setBorder(BorderFactory.createTitledBorder("Results"));
		
		queryTbl = new JTable(new FileTableModel(TableType.FILE_LIST));
		queryTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		queryTbl.setShowVerticalLines(false);
		queryTbl.setAutoCreateRowSorter(true);
		queryTbl.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		queryTbl.getColumnModel().getColumn(0).setPreferredWidth(1000);
	    packColumn(queryTbl, 1, 10);
	    packColumn(queryTbl, 2, 10);
		
		JScrollPane queryScpn = new JScrollPane(queryTbl);
		queryScpn.setPreferredSize(new Dimension(200, 400));
		
		queryTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
	            if (!e.getValueIsAdjusting()) {
	            	// clear library table
	            	libTbl.clearSelection();
	            	DefaultTableModel libTblMdl = (DefaultTableModel) libTbl.getModel();
					for (int row = 0; row < libTblMdl.getRowCount(); ) {
						libTblMdl.removeRow(libTblMdl.getRowCount()-1);
					}
					// re-populate library table
					int row = queryTbl.convertRowIndexToModel(queryTbl.getSelectedRow());
					FileTableModel queryTblMdl = (FileTableModel) queryTbl.getModel();
					MascotGenericFile mgf = queryTblMdl.getMgfAt(row);
					resultList = resultMap.get(mgf.getTitle());
					if (resultList != null) {
						for (int index = 0; index < resultList.size(); index++) {
							libTblMdl.addRow(new Object[] {index+1, resultList.get(index).getSequence(),
															  		resultList.get(index).getAnnotation(),
															  		resultList.get(index).getScore()});
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
		});
		
		libTbl = new JTable(new FileTableModel(TableType.RESULT_LIST));
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
						FileTableModel libTblMdl = (FileTableModel) libTbl.getModel();
						MascotGenericFile mgfLib = libTblMdl.getMgfAt(row);
						mPlot.setSecondSpectrum(mgfLib);
						mPlot.repaint(true);
					}
				}
			}
		});
		
		JButton expBtn = new JButton("export scores");
		expBtn.setPreferredSize(new Dimension(expBtn.getPreferredSize().width,
											  new JLabel(" ").getPreferredSize().height));
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
		BasicSplitPaneDivider divider = ((BasicSplitPaneUI) rightSplit.getUI()).getDivider();
		if (divider != null) { divider.setBorder(null); }
		
		JPanel leftPnl = new JPanel();
		leftPnl.setLayout(new FormLayout("f:p:g",				// col
		 								  "p, 5dlu, f:p:g"));	// row
		leftPnl.add(new JLabel("Query spectra"), cc.xy(1,1));
		leftPnl.add(queryScpn, cc.xy(1,3));	
		
		JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPnl, rightSplit);
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

	private enum TableType { FILE_SELECT, FILE_LIST, RESULT_LIST }
	
	/** 
	 * Extended default table model, includes proper CheckBoxes.
	 */
	private class FileTableModel extends DefaultTableModel {
		
		private TableType tblType;
		
		public FileTableModel(TableType tblType) {
			this.tblType = tblType;
			
			switch (this.tblType) {
			case FILE_SELECT:
				this.setColumnIdentifiers(new Object[] {"Filename","#",""});
				break;
			case FILE_LIST:
				this.setColumnIdentifiers(new Object[] {"Filename","#","hits"});
				break;
			case RESULT_LIST:
				this.setColumnIdentifiers(new Object[] {"#","Sequence","Accession","Score"});
				break;
			default:
				break;
			}
		}
		
		public Class<? extends Object> getColumnClass(int c) {
			return getValueAt(0,c).getClass();				
	    }
		
		public Object getValueAt(int row, int col) {
			Vector dataVec = (Vector)getDataVector();
			Object val = "";
			if (dataVec.size() > 0) {
				val = ((Vector)dataVec.elementAt(row)).elementAt(col);
				switch (tblType) {
				case FILE_SELECT:
					if (col == 0) {
						val = ((String)val).substring(((String)val).lastIndexOf("/")+1);
					}
					break;
				case FILE_LIST:
					if (col < 2) {
						val = fileTbl.getValueAt((Integer)val, col);					
					}
					break;
				case RESULT_LIST:
					// TODO: handle result list elements
					break;
				default:
					break;
				}
			}
			return val;
		}
		
		public Object getTrueValueAt(int row, int col) {
			return ((Vector)getDataVector().elementAt(row)).elementAt(col);
		}
		
		public MascotGenericFile getMgfAt(int row) {
			Vector dataVec = (Vector)getDataVector();
			if (!dataVec.isEmpty() && (dataVec.size() >= row)) {
				Vector colVec = (Vector)dataVec.elementAt(row);
				if (!colVec.isEmpty() && (dataVec.size() >= 0)) {
					int index;
					switch (this.tblType) {
					case FILE_SELECT:
						String fileName = (String)this.getTrueValueAt(row,0);
						index = (Integer)this.getValueAt(row,1);
						int pos = specPosMap.get(fileName).get(index-1);
						MascotGenericFileReader reader = new MascotGenericFileReader();
						try {
							return reader.loadNthSpectrum(new File(fileName), index, pos);
						} catch (IOException e) {
							e.printStackTrace();
						}
					case FILE_LIST:
						return ((FileTableModel)fileTbl.getModel()).getMgfAt((Integer)this.getTrueValueAt(row,0));
					case RESULT_LIST:
						index = (Integer)this.getValueAt(row, 0);
						return resultList.get(index-1).getSpectrumFile();
					default:
						break;
					}
				}
			}
			return null;
		}
		
		public void setValueAt(Object newVal, int row, int column) {
			Vector<Object> rowVector = (Vector<Object>)dataVector.elementAt(row);
			Object oldVal = rowVector.elementAt(column);
			// check whether new value actually differs from old one
			if (!oldVal.equals(newVal)) {
				rowVector.setElementAt(newVal, column);
				fireTableCellUpdated(row, column);
			}
	    }
		
		public boolean isCellEditable(int row, int col) {
			switch (tblType) {
			case FILE_SELECT:
				if (col == 2) {
		            return true;
		        }
			default:
				return false;
			}
	    }
		
	}
	
	
	
	// Sets the preferred width of the visible column specified by vColIndex. The column
	// will be just wide enough to show the column head and the widest cell in the column.
	// margin pixels are added to the left and right
	// (resulting in an additional width of 2*margin pixels).
	public void packColumn(JTable table, int vColIndex, int margin) {
//	    TableModel model = table.getModel();
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
			int packageSize = (Integer)packSpn.getValue();

			FileTableModel fileTblMdl = (FileTableModel) fileTbl.getModel();
			FileTableModel queryTblMdl = (FileTableModel) queryTbl.getModel();
			// clear query table inside results panel
			queryTbl.clearSelection();
			for (int row = 0; row < queryTblMdl.getRowCount(); ) {
				queryTblMdl.removeRow(queryTblMdl.getRowCount()-1);
			}

			// consolidate selected spectra into files
			FileOutputStream fos = null;
			
			files = new ArrayList<File>();
			
			logPnl.append("Packing files... ");
			int numSpectra = 0;
			for (int row = 0; row < fileTbl.getRowCount(); row++) {
				if ((Boolean)fileTbl.getValueAt(row, 2)) {
					if ((numSpectra % packageSize) == 0) {
						if (fos != null) { fos.close(); }
						File file = new File("batch_" + (numSpectra/packageSize) + ".mgf");
						files.add(file);
						fos = new FileOutputStream(file);
					}
					numSpectra++;
					MascotGenericFile mgf = fileTblMdl.getMgfAt(row);
					mgf.writeToStream(fos);
					fos.flush();
				}
			}
			fos.close();
			logPnl.append("done.\n");
			
			queryTbl.clearSelection();
			
			// process files
			double progress = 0;
			double max = files.size();
			setProgress(0);
			logPnl.append("Processing files... ");
			
			ProcessSettings procSet = new ProcessSettings((Double)tolMzSpn.getValue(),
														  (Double)thMzSpn.getValue(),
														  (Integer)kSpn.getValue(),
														  (Double)thScSpn.getValue(),
														  (Boolean)annotChk.isSelected());
			
			resultMap = new HashMap<String,ArrayList<RankedLibrarySpectrum>>();
			for (File file : files) {
				resultMap.putAll(client.process(file,procSet));
				progress++;
				setProgress((int)(progress/max*100));
			}
			logPnl.append("done.\n");
//				if (queryTbl.getRowCount() > 0) {
//					queryTbl.setRowSelectionInterval(0, 0);
//				}
			
			// populate query table inside results panel
			for (int row = 0; row < fileTbl.getRowCount(); row++) {
				if ((Boolean)fileTbl.getValueAt(row, 2)) {
					int numHits = 0;
					ArrayList<RankedLibrarySpectrum> result = resultMap.get(fileTblMdl.getMgfAt(row).getTitle());
					if (result != null) {
						numHits = result.size();
					}
					queryTblMdl.addRow(new Object[] {row, row, numHits});
				}
			}
		    packColumn(queryTbl, 1, 6);
		    packColumn(queryTbl, 2, 6);
			
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
						// TODO: run the db search with the settings
						System.out.println(settings.getFastaFile());
						client.runDbSearch(file, settings);
						progress++;
						setProgress((int)(progress/max*100));
				}
				System.out.println("done");
				if (queryTbl.getRowCount() > 0) {
					queryTbl.setRowSelectionInterval(0, 0);	
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

