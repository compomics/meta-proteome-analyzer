package de.mpa.client.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.BusinessBlackSteelSkin;

import com.compomics.util.gui.spectrum.SpectrumPanel;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Client;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;
import de.mpa.ui.MgfFilter;
import de.mpa.ui.PlotPanel2;
import de.mpa.utils.ExtensionFileFilter;
import de.mpa.webservice.WSPublisher;

public class ClientFrame extends JFrame {
	
	private final static int PORT = 8080;
	private final static String HOST = "localhost";
	
	private final static String PATH = "test/de/mpa/resources/";
	
	private ClientFrame frame;
	
	private Client client;
		
	private JPanel topPnl;

	private JPanel specPnl;
//	private JPanel mainPnl;
	
	private JPanel bottomPnl;

	private CellConstraints cc;

	private List<File> files = new ArrayList<File>();

	private JTextField filesTtf;

	private JTextField hostTtf;

	private JTextField portTtf;

	private JButton startBtn;
	private JMenuBar menuBar;
	private JMenu menu1;
	private JMenuItem exitItem;
	private JButton processBtn;
	
	private ArrayList<MascotGenericFile> spectrumFiles = new ArrayList<MascotGenericFile>();
	
	
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
		tabPane.addTab("Input Spectra", specPnl);
		tabPane.addTab("Server Configuration", topPnl);
		tabPane.addTab("Logging", bottomPnl);
		
		cp.add(tabPane);
		
//		JPanel centerPnl = new JPanel();
//		centerPnl.setLayout(new BorderLayout());
//		centerPnl.add(topPnl, BorderLayout.NORTH);
//		centerPnl.add(mainPnl, BorderLayout.CENTER);
//		cp.add(centerPnl, BorderLayout.CENTER);
//		cp.add(bottomPnl, BorderLayout.SOUTH);
		
		// Get the client instance
        client = Client.getInstance();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
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
		
		// Top panel
		constructTopPanel();
		
		// Main panel
		constuctMainPanel();
		
		// Bottom panel		
		constructBottomPanel();
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
	 * Construct the top panel.
	 */
	private void constructTopPanel(){
		topPnl = new JPanel();
		topPnl.setBorder(new TitledBorder("Server Configuration"));
		topPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p, 5dlu, p, 5dlu", "p, 3dlu, p, 5dlu"));
		
		final JLabel hostLbl = new JLabel("Hostname:"); 
		hostTtf = new JTextField(8);
		hostTtf.setText(HOST);
		
		final JLabel portLbl = new JLabel("Port:");
		portTtf = new JTextField(8);
		portTtf.setText(Integer.toString(PORT));
		
	    topPnl.add(hostLbl, cc.xy(2,1));
	    topPnl.add(hostTtf, cc.xy(4,1));
	    topPnl.add(portLbl, cc.xy(2,3));
	    topPnl.add(portTtf, cc.xy(4,3));
	    
	    startBtn = new JButton("Start Server");
	    startBtn.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
					WSPublisher.start(hostTtf.getText(), portTtf.getText());					
					JOptionPane.showMessageDialog(frame, "Web Service @" + hostTtf.getText() + ":" + portTtf.getText() + " established.");
					
					client.connect();
			}
		});
	  
	    topPnl.add(startBtn, cc.xy(6,1));
	}
		
	/**
	 * Construct the main panel.
	 */
	private void constuctMainPanel(){
		
		specPnl = new JPanel();
		specPnl.setLayout(new FormLayout("5dlu, p, 5dlu",		// col
										 "5dlu, p, 5dlu, p"));	// row
		
		JPanel mainPnl = new JPanel();
		mainPnl.setBorder(new TitledBorder("File selection"));
		mainPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu",
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
	    
	    final JButton sendBtn = new JButton("Send");
	    sendBtn.setEnabled(false);
	    
	    final JButton clrBtn = new JButton("Clear");
	    sendBtn.setEnabled(false);
		
	    // Table of loaded spectrum files
	    final DefaultTableModel tblMdl = new FileTableModel();
	    tblMdl.setColumnIdentifiers(new String[] {"Filename", ""});

//	    for (int i = 1; i <= 100; i++) {
//			tblMdl.addRow(new Object[] {"test" + Integer.toString(i) + ".mgf",
//										(Math.random() > 0.5)});
//		}

	    final JTable fileTbl = new JTable(tblMdl);
	    fileTbl.setShowVerticalLines(false);

	    TableColumn tCol = fileTbl.getColumnModel().getColumn(1);
	    tCol.setMaxWidth(fileTbl.getRowHeight()+4);
	    
	    class CheckBoxHeaderItemListener implements ItemListener {
			public void itemStateChanged(ItemEvent e) {
				Object source = e.getSource();
				if (source instanceof AbstractButton == false) return;
				boolean checked = (e.getStateChange() == ItemEvent.SELECTED);
				for(int x = 0, y = fileTbl.getRowCount(); x < y; x++) {
					fileTbl.setValueAt(new Boolean(checked),x,1);
				}
			}
		}	    
	    tCol.setHeaderRenderer(new CheckBoxHeader(new CheckBoxHeaderItemListener()));
	    
	    JScrollPane fileScPn = new JScrollPane(fileTbl);
	    fileScPn.setPreferredSize(new Dimension(fileScPn.getPreferredSize().width, 200));
	    
	    mainPnl.add(fileScPn, cc.xyw(2,3,5));
	    
	    // Button action listeners
		addBtn.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
                // First check whether a file has already been selected.
                // If so, start from that file's parent.
				File startLocation = new File(PATH);
                if (files.size() > 0) {
                    File temp = files.get(0);
                    startLocation = temp.getParentFile();
                }
                JFileChooser fc = new JFileChooser(startLocation);
                fc.setFileFilter(new ExtensionFileFilter("mgf", false));
                fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                fc.setMultiSelectionEnabled(true);
                int result = fc.showOpenDialog(ClientFrame.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File[] selFiles = fc.getSelectedFiles();
                    for (int i = 0; i < selFiles.length; i++) {
                        if (selFiles[i].isDirectory()) {
                            File[] currentFiles = selFiles[i].listFiles();
                            for (int k = 0; k < currentFiles.length; k++) {
                                if (fc.getFileFilter().accept(currentFiles[k])) {
                                    files.add(currentFiles[k]);
                                }
                            }
                        } else {
                            files.add(selFiles[i]);
                        }
                    }
                    
                    for (File file : selFiles) {
                    	ArrayList<MascotGenericFile> newSpectrumFiles = new ArrayList<MascotGenericFile>();
						try {
							MascotGenericFileReader reader = new MascotGenericFileReader(file);
							newSpectrumFiles.addAll(reader.getSpectrumFiles());
					    	} catch (Exception x) {
		    	            x.printStackTrace();
		    	        }
					    if (!newSpectrumFiles.isEmpty()) {
				    		spectrumFiles.addAll(newSpectrumFiles);
				    		for (MascotGenericFile spectrum : newSpectrumFiles) {
								tblMdl.addRow(new Object[] {spectrum.getFilename(), true});
							}
					    }
					}
                    
                    filesTtf.setText(files.size() + " file(s) selected");
                    
                    if(files.size() > 0) {
                    	sendBtn.setEnabled(true);
                    	clrBtn.setEnabled(true);
                    }
                }
            }
	    } );
	    
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
	    
	    // Input 
	    mainPnl.add(fileLbl, cc.xy(2,1));
	    mainPnl.add(filesTtf, cc.xy(4,1));
	    mainPnl.add(addBtn, cc.xy(6,1));
	    mainPnl.add(sendBtn, cc.xy(8,3));
	    mainPnl.add(clrBtn, cc.xy(8,1));
	    
//	    processBtn = new JButton("Process");
//	    processBtn.addActionListener(new ActionListener() {
//			
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				ProcessWorker worker = new ProcessWorker();
//				worker.execute();
//			}
//		});
//	    
//	    mainPnl.add(processBtn, cc.xy(8,3));
	    
	    specPnl.add(mainPnl, cc.xy(2,2));
	    
	    JPanel yaPnl = new JPanel();
		yaPnl.setBorder(new TitledBorder("Yet Another Panel"));
		yaPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu",
									   "p:g, 5dlu"));
		
		PlotPanel2 plotPnl = new PlotPanel2(null);
		plotPnl.setPreferredSize(new Dimension(300, 200));
		yaPnl.add(plotPnl, cc.xy(2,1));
		
	    specPnl.add(yaPnl, cc.xy(2,4));
	}
	
		
	/** 
	 * Extended default table model, includes proper CheckBoxes.
	 */
	private class FileTableModel extends DefaultTableModel {
		public Class getColumnClass(int c) {
	        return getValueAt(0, c).getClass();
	    }
	}
	
	/**
	 * Construct the bottom panel.
	 */	
	private void constructBottomPanel(){
		bottomPnl = new LogPanel();		
		bottomPnl.setBorder(BorderFactory.createTitledBorder("Logging"));		
		
		bottomPnl.setMinimumSize(new Dimension(800, 200));
		bottomPnl.setPreferredSize(new Dimension(800, 200));
	}
	
	@SuppressWarnings("rawtypes")
	private class ProcessWorker extends SwingWorker {		
		protected Object doInBackground() throws Exception {
			try {
				for (File file : files) {
						client.process(file.getName());
				}
				files.clear();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 0;
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

