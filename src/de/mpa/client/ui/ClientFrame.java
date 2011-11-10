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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
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
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.algorithms.RankedLibrarySpectrum;
import de.mpa.client.Client;
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
	
	private final static String PATH = "test/de/mpa/resources/";
	
	private ClientFrame frame;
	
	private Client client;
	
	private JPanel filePnl;

	private JPanel srvPnl;
	
	private JPanel msmsPnl;
	
	private JPanel resPnl;
	
	private JPanel logPnl;

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
	
	private ArrayList<MascotGenericFile> spectrumFiles = new ArrayList<MascotGenericFile>();
	
	private boolean connectedToServer = false;
	
	private JTable queryTbl;
	private JTable libTbl;
	private Map<String, ArrayList<RankedLibrarySpectrum>> resultMap;
	
	private Map<String, ArrayList<Integer>> specPosMap = new HashMap<String, ArrayList<Integer>>(1);
	private JButton runDbSearchBtn;
	
	
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
		tabPane.addTab("MS/MS Identification", msmsPnl);
		tabPane.addTab("Results", resPnl);
		tabPane.addTab("Logging", logPnl);
		
		cp.add(tabPane);
		
//		JPanel centerPnl = new JPanel();
//		centerPnl.setLayout(new BorderLayout());
//		centerPnl.add(topPnl, BorderLayout.NORTH);
//		centerPnl.add(mainPnl, BorderLayout.CENTER);
//		cp.add(centerPnl, BorderLayout.CENTER);
//		cp.add(bottomPnl, BorderLayout.SOUTH);
		
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
		
		// Process Panel
		constructProcessPanel();
		
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
		filePnl.setLayout(new FormLayout("5dlu, p, 5dlu",		// col
										 "5dlu, p, 5dlu, p"));	// row
		
		JPanel selPnl = new JPanel();
		selPnl.setBorder(new TitledBorder("File selection"));
		selPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu",
										 "p, 5dlu, b:p, 5dlu"));
		
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
	    
	    final JButton clrBtn = new JButton("Clear");
	    clrBtn.setEnabled(false);
		
	    // Table of loaded spectrum files
	    final JTable fileTbl = new JTable(new FileTableModel(TableType.FILE_SELECT));
	    fileTbl.setShowVerticalLines(false);
	    fileTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    packColumn(fileTbl, 1, 2);
	    
		final PlotPanel2 plotPnl = new PlotPanel2(null);
		
	    final FileTableModel tblMdl = (FileTableModel) fileTbl.getModel();
	    
	    fileTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {			
			@Override
			public void valueChanged(ListSelectionEvent e) {
                // If the mouse button has been released
	            if (!e.getValueIsAdjusting()) {
		            // If cell selection is enabled, both row and column change events are fired
		        	if (e.getSource() == fileTbl.getSelectionModel() &&
		        			fileTbl.getRowSelectionAllowed()) {
		        		// Column selection changed
		        		int row = fileTbl.getSelectedRow();
		        		if (row >= 0) {
		            		MascotGenericFile mgf = (MascotGenericFile)(tblMdl.getMgfAt(row));
		            		plotPnl.setSpectrumFile(mgf,Color.RED);
		            		plotPnl.repaint();        			
		        		} else {
		        			plotPnl.clearSpectrumFile();
		        			plotPnl.repaint();
		        		}
		        	}
	            }
	        }
		});
	    
	    tblMdl.addTableModelListener(new TableModelListener() {			
			@Override
			public void tableChanged(TableModelEvent e) {
		        TableModel tblMdl = (TableModel)e.getSource();

		        int row = e.getFirstRow();
		        if (e.getType() == TableModelEvent.UPDATE) {
			        // fail-safe against empty table
			        if (row >= 0) {
			        	// parse label string
			        	String text = filesLbl.getText();
				        int numSelFiles = Integer.parseInt(text.substring(0, text.indexOf(" ")));
				        // change number of selected files depending on cell content
				        if ((Boolean)tblMdl.getValueAt(row,2)) {
							numSelFiles += 1;
						} else {
							numSelFiles -= 1;
						}
				        // apply label string with updated value
						filesLbl.setText(numSelFiles + " of " + tblMdl.getRowCount() + " file(s) selected.");
			        }
		        }
		    }
		});
	    
	    TableColumn tCol = fileTbl.getColumnModel().getColumn(2);
//	    tCol.setMinWidth(fileTbl.getRowHeight());
	    tCol.setMaxWidth((int)(fileTbl.getRowHeight()*1.5));
	    
	    class CheckBoxHeaderItemListener implements ItemListener {
			public void itemStateChanged(ItemEvent e) {
				Object source = e.getSource();
				if (source instanceof AbstractButton == false) return;
				boolean checked = (e.getStateChange() == ItemEvent.SELECTED);
				for(int x = 0, y = fileTbl.getRowCount(); x < y; x++) {
					fileTbl.setValueAt(new Boolean(checked),x,2);
				}
				
				int numSelFiles = (checked) ? fileTbl.getRowCount() : 0;
				filesLbl.setText(numSelFiles + " of " + fileTbl.getRowCount() + " file(s) selected.");
			}
		}
	    tCol.setHeaderRenderer(new CheckBoxHeader(new CheckBoxHeaderItemListener()));
	    	    
	    JScrollPane fileScPn = new JScrollPane(fileTbl);
	    fileScPn.setPreferredSize(new Dimension(fileScPn.getPreferredSize().width, 200));
	    
	    selPnl.add(fileScPn, cc.xyw(2,3,5));
	    
	    // Button action listeners
		addBtn.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
                // First check whether a file has already been selected.
                // If so, start from that file's parent.
				File startLocation = new File(PATH);
//                if (files.size() > 0) {
//                    File temp = files.get(0);
//                    startLocation = temp.getParentFile();
//                }
				if (fileTbl.getRowCount() > 0) {
					File temp = new File(tblMdl.getStringAt(0,0));
					startLocation = temp.getParentFile();
				}
                JFileChooser fc = new JFileChooser(startLocation);
                fc.setFileFilter(new ExtensionFileFilter("mgf", false));
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fc.setMultiSelectionEnabled(true);
                int result = fc.showOpenDialog(ClientFrame.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File[] selFiles = fc.getSelectedFiles();
                    
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
                    	for (int i = 1; i <= spectrumPositions.size(); i++) {
							tblMdl.addRow(new Object[] {file.getAbsolutePath(), i, true});
						}
                    	specPosMap.put(file.getAbsolutePath(), spectrumPositions);
					}
                	packColumn(fileTbl, 1, 2);
                    
                	String str = filesTtf.getText();
                	str = str.substring(0, str.indexOf(" "));
                	
                	int numFiles = Integer.parseInt(str) + selFiles.length;
                	
                    filesTtf.setText(numFiles + " file(s) selected");
                    
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
				fileTbl.clearSelection();
				for (int row = 0; row < tblMdl.getRowCount(); ) {
					tblMdl.removeRow(tblMdl.getRowCount()-1);
				}
//				files.clear();
				specPosMap.clear();
				filesTtf.setText("0 file(s) selected");
				clrBtn.setEnabled(false);
			}
		});
	    
	    // Input 
	    selPnl.add(fileLbl, cc.xy(2,1));
	    selPnl.add(filesTtf, cc.xy(4,1));
	    selPnl.add(addBtn, cc.xy(6,1));
	    selPnl.add(clrBtn, cc.xy(8,1));
	    
	    filePnl.add(selPnl, cc.xy(2,2));
	    
	    JPanel detPnl = new JPanel();
		detPnl.setBorder(new TitledBorder("File Details"));
		detPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu",
									   "p:g, 5dlu"));
		
		plotPnl.setPreferredSize(new Dimension(300, 200));
		detPnl.add(plotPnl, cc.xy(2,1));
		
	    filePnl.add(detPnl, cc.xy(2,4));
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
					client.sendMessage("Test the west!");
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
	private void constructProcessPanel() {
		
		msmsPnl = new JPanel();
		msmsPnl.setLayout(new FormLayout("5dlu, p, 5dlu",		// col
		 								 "5dlu, p, 5dlu, p, 5dlu"));		// row
		
		JPanel procPnl = new JPanel();
		procPnl.setLayout(new FormLayout("5dlu, p, 5dlu",		// col
		 								 "5dlu, p, 5dlu"));		// row	
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
	    
	    JPanel dbSearchPnl = new JPanel();
	    dbSearchPnl.setLayout(new FormLayout("10dlu, p, 10dlu",		// col
		 								 "5dlu, p, 5dlu"));		// row	
	    dbSearchPnl.setBorder(BorderFactory.createTitledBorder("DB-Search"));	
	    
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
	    
	    procPrg = new JProgressBar(0, 100);
	    procPrg.setStringPainted(true);
	    
	    procPnl.add(procBtn, cc.xy(2,2));
	    dbSearchPnl.add(runDbSearchBtn, cc.xy(2,2));
	    msmsPnl.add(procPnl, cc.xy(2,2));
	    msmsPnl.add(dbSearchPnl, cc.xy(2,4));
	    
	}
	
	/**
	 * Construct the results panel.
	 */
	private void constructResultsPanel() {
		
		resPnl = new JPanel();
		resPnl.setLayout(new FormLayout("5dlu, p, 5dlu",		// col
		 								 "5dlu, p, 5dlu"));		// row
		
		JPanel dispPnl = new JPanel();
		dispPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p, 5dlu",		// col
		 								 "3dlu, p, 5dlu, p, 5dlu, p, 5dlu"));	// row	
		dispPnl.setBorder(BorderFactory.createTitledBorder("Results"));
		
//		queryTbl = new JTable(new QueryTableModel());
		queryTbl = new JTable(new FileTableModel(TableType.FILE_LIST));
		queryTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		queryTbl.setShowVerticalLines(false);
		
		JScrollPane queryScpn = new JScrollPane(queryTbl);
		queryScpn.setPreferredSize(new Dimension(250, 400));
		
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
					// get query spectrum
//					QueryTableModel queryTblMdl = (QueryTableModel) queryTbl.getModel();
					FileTableModel queryTblMdl = (FileTableModel) queryTbl.getModel();
					MascotGenericFile mgfQuery = (MascotGenericFile)(queryTblMdl.getMgfAt(queryTbl.getSelectedRow()));
					
					String key = mgfQuery.getFilename() + mgfQuery.getTitle() + mgfQuery.getTotalIntensity();
					if (resultMap.containsKey(key)) {
						
						// TODO: populate libTbl
						
//						ArrayList<Pair<LibrarySpectrum, Double>> resultList = resultMap.get(key);
//						for (Pair<LibrarySpectrum, Double> result : resultList) {
//							libTblMdl.addRow(new Object[] {result.getLeft(), 					// library spectrum
//														   result.getLeft().getAnnotation(),	// accession
//														   result.getRight()});					// score
//						}
					}
	            }				
			}
		});
		
		libTbl = new JTable(new FileTableModel(TableType.RESULT_LIST));
		libTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane libScpn = new JScrollPane(libTbl);
		libScpn.setPreferredSize(new Dimension(350, 200));
		
		final MultiPlotPanel mPlot = new MultiPlotPanel();
		mPlot.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		mPlot.setPreferredSize(new Dimension(350, 200));
		
		libTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					if (libTbl.getSelectedRowCount() > 0) {
						FileTableModel queryTblMdl = (FileTableModel) queryTbl.getModel();
						MascotGenericFile mgfQuery = (MascotGenericFile)(queryTblMdl.getMgfAt(queryTbl.getSelectedRow()));
						FileTableModel libTblMdl = (FileTableModel) libTbl.getModel();
						MascotGenericFile mgfLib = (MascotGenericFile)(libTblMdl.getMgfAt(libTbl.getSelectedRow()));
						if ((mgfQuery != null) && (mgfLib != null)) {
							ArrayList<MascotGenericFile> mgfs = new ArrayList<MascotGenericFile>();
							mgfs.add(mgfQuery);
							mgfs.add(mgfLib);
							mPlot.setSpectra(mgfs);
							mPlot.repaint();							
						}
					}
				}
			}
		});
		
		dispPnl.add(new JLabel("Query spectra"), cc.xy(2,2));
		dispPnl.add(queryScpn, cc.xywh(2, 4, 1, 3));
		dispPnl.add(new JLabel("Library spectra"), cc.xy(4,2));
		dispPnl.add(libScpn, cc.xy(4,4));
		dispPnl.add(mPlot, cc.xy(4,6));
	    
	    resPnl.add(dispPnl, cc.xy(2,2));
	}
	
//	private class QueryTableModel extends DefaultTableModel {
//		
//		public QueryTableModel() {
//			this.setColumnIdentifiers(new Object[] {"Filename"});
//		}
//
//		public Object getValueAt(int row, int col) {
//			Object val = ((Vector)getDataVector().elementAt(row)).elementAt(col);
//			if (col == 0) {
//				val = ((MascotGenericFile)val).getFilename();
//			}
//			return val;
//		}
//		public MascotGenericFile getMgfAt(int row) {
//			Vector dataVec = (Vector)getDataVector();
//			if (!dataVec.isEmpty() && (dataVec.size() >= row)) {
//				Vector colVec = (Vector)dataVec.elementAt(row);
//				if (!colVec.isEmpty() && (dataVec.size() >= 1)) {
//					return (MascotGenericFile)colVec.elementAt(0);
//				}
//			}
//			return null;
//		}
//		public void setValueAt(Object newVal, int row, int col) {
//			Vector<Object> rowVector = (Vector<Object>)dataVector.elementAt(row);
//			Object oldVal = rowVector.elementAt(col);
//			// check whether new value actually differs from old one
//			if (!oldVal.equals(newVal)) {
//				rowVector.setElementAt(newVal, col);
//				fireTableCellUpdated(row, col);
//			}
//		}
//		public boolean isCellEditable(int row, int col) {
//			return false;
//		}
//
//	}
	
	private enum TableType { FILE_SELECT, FILE_LIST, RESULT_LIST }
	
//	private class LibraryTableModel extends DefaultTableModel {
//
//		public Object getValueAt(int row, int col) {
//			Object val = ((Vector)getDataVector().elementAt(row)).elementAt(col);
//			if (col == 0) {
//				val = ((LibrarySpectrum)val).getSequence();
//			}
//			return val;
//		}
//		public MascotGenericFile getMgfAt(int row) {
//			Vector dataVec = (Vector)getDataVector();
//			if (!dataVec.isEmpty() && (dataVec.size() >= row)) {
//				Vector colVec = (Vector)dataVec.elementAt(row);
//				if (!colVec.isEmpty() && (dataVec.size() >= 1)) {
//					return ((LibrarySpectrum)colVec.elementAt(0)).getSpectrumFile();
//				}
//			}
//			return null;
//		}
//		public void setValueAt(Object newVal, int row, int col) {
//			Vector rowVector = (Vector)dataVector.elementAt(row);
//			Object oldVal = rowVector.elementAt(col);
//			// check whether new value actually differs from old one
//			if (!oldVal.equals(newVal)) {
//				rowVector.setElementAt(newVal, col);
//				fireTableCellUpdated(row, col);
//			}
//		}
//		public boolean isCellEditable(int row, int col) {
//			return false;
//		}
//
//	}
	
	
	/**
	 * Construct the logging panel.
	 */	
	private void constructLogPanel(){
		
		logPnl = new JPanel();
		logPnl.setLayout(new FormLayout("5dlu, p, 5dlu",		// col
		 								"5dlu, p, 5dlu"));		// row
		JPanel lg2Pnl = new JPanel();
		lg2Pnl.setLayout(new FormLayout("5dlu, p, 5dlu",		// col
		 								"5dlu, p, 5dlu"));		// row
		lg2Pnl.setBorder(BorderFactory.createTitledBorder("Logging"));
		
		JPanel lg3Pnl = new LogPanel();
		lg3Pnl.setPreferredSize(new Dimension(600, 200));
		
		lg2Pnl.add(lg3Pnl, cc.xy(2, 2));
		logPnl.add(lg2Pnl, cc.xy(2, 2));
		
	}
		
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
				this.setColumnIdentifiers(new Object[] {"Filename"});
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
			Object val = ((Vector)getDataVector().elementAt(row)).elementAt(col);
			switch (tblType) {
			case FILE_SELECT:
			case FILE_LIST:
			if (col == 0) {
				val = ((String)val).substring(((String)val).lastIndexOf("/")+1);
			}
			default:
				break;
			}
			return val;
		}
		
		public String getStringAt(int row, int col) {
			Object val = ((Vector)getDataVector().elementAt(row)).elementAt(col);
			return (String)val;
		}
		
		public MascotGenericFile getMgfAt(int row) {
			Vector dataVec = (Vector)getDataVector();
			if (!dataVec.isEmpty() && (dataVec.size() >= row)) {
				Vector colVec = (Vector)dataVec.elementAt(row);
				if (!colVec.isEmpty() && (dataVec.size() >= 0)) {
					int index;
					switch (this.tblType) {
					case RESULT_LIST:
						FileTableModel queryTblMdl = (FileTableModel)queryTbl.getModel();
						MascotGenericFile mgfQuery = (queryTblMdl).getMgfAt(queryTbl.getSelectedRow());
						index = (Integer)this.getValueAt(row, 0);
						return resultMap.get(mgfQuery.getTitle()).get(index-1).getSpectrumFile();
					default:
						String fileName = this.getStringAt(row,0);
						index = (Integer)this.getValueAt(row,1);
						int pos = specPosMap.get(fileName).get(index-1);
						MascotGenericFileReader reader = new MascotGenericFileReader();
						try {
							return reader.loadNthSpectrum(new File(fileName), index, pos);
						} catch (IOException e) {
							e.printStackTrace();
						}
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
	    col.setMaxWidth(width);
	}

	
	
	private class ProcessWorker extends SwingWorker {
		
		protected Object doInBackground() throws Exception {
			double progress = 0;
			double max = files.size();
			setProgress(0);
			try {
				for (File file : files) {
						resultMap = client.process(file);
						DefaultTableModel tblMdl = (DefaultTableModel) queryTbl.getModel();
						for (MascotGenericFile mgf : spectrumFiles) {
							if (resultMap.containsKey(mgf.getFilename() + mgf.getTitle() + mgf.getTotalIntensity())) {
								tblMdl.addRow(new Object[] {mgf});
							}
						}
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
	
	private class RunDbSearchWorker	extends SwingWorker {
		
		protected Object doInBackground() throws Exception {
			double progress = 0;
			double max = files.size();
			setProgress(0);
			try {
				for (File file : files) {
						client.runDbSearch(file);
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

