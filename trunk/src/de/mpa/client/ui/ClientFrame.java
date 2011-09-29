package de.mpa.client.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.BusinessBlackSteelSkin;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Client;
import de.mpa.ui.MgfFilter;
import de.mpa.utils.ExtensionFileFilter;

public class ClientFrame extends JFrame {
	
	private final static int PORT = 8080;
	private final static String HOST = "localhost";
	
	private final static String PATH = "test/de/mpa/resources/";
	
	private ClientFrame frame;
	
	private Client client;
	
	private JPanel topPnl;
	
	private JPanel mainPnl;
	
	private JPanel bottomPnl;

	private CellConstraints cc;

	private List<File> files = new ArrayList<File>();

	private JTextField filesTtf;

	private JTextField hostTtf;

	private JTextField portTtf;

	private JButton connectBtn;
	private JMenuBar menuBar;
	private JMenu menu1;
	private JMenuItem exitItem;
	
	
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
		JPanel centerPnl = new JPanel();
		centerPnl.setLayout(new BorderLayout());
		centerPnl.add(topPnl, BorderLayout.NORTH);
		centerPnl.add(mainPnl, BorderLayout.CENTER);
		cp.add(centerPnl, BorderLayout.CENTER);
		cp.add(bottomPnl, BorderLayout.SOUTH);
		
		// Get the client instance
        client = Client.getInstance();
        
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
		
		// Menue
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
	    
	    connectBtn = new JButton("Connect");
	    connectBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					client.connect(hostTtf.getText(), Integer.valueOf(portTtf.getText()));
					JOptionPane.showMessageDialog(frame, "Connection to server@" + hostTtf.getText() + ":" + portTtf.getText() + " established.");
				} catch (NumberFormatException e1) {
					e1.printStackTrace();
				} catch (UnknownHostException e2) {
					JOptionPane.showMessageDialog(frame, e2.getMessage());
					e2.printStackTrace();
				} catch (IOException e3) {
					JOptionPane.showMessageDialog(frame, e3.getMessage());
					e3.printStackTrace();
				}
				
				
			}
		});
	    topPnl.add(connectBtn, cc.xy(6,3));
	}
		
	/**
	 * Construct the main panel.
	 */
	private void constuctMainPanel(){
		mainPnl = new JPanel();
		mainPnl.setBorder(new TitledBorder("Input Spectra"));
		mainPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p", "p"));
		
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
                    filesTtf.setText(files.size() + " file(s) selected");
                    
                    if(files.size() > 0){
                    	sendBtn.setEnabled(true);
                    }
                }
            }
	    } );
	    
	    sendBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO: 				
			}
		});
	    
	    mainPnl.add(fileLbl, cc.xy(2,1));
	    mainPnl.add(filesTtf, cc.xy(4,1));
	    mainPnl.add(addBtn, cc.xy(6,1));
	    mainPnl.add(sendBtn, cc.xy(8,1));
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
					SubstanceLookAndFeel.setSkin(new BusinessBlackSteelSkin());
				} catch (Exception e) {
					e.printStackTrace();
				}
				new ClientFrame();
			}
		});
	}
}

