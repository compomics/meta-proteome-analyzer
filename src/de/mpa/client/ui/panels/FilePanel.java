package de.mpa.client.ui.panels;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;

import com.compomics.util.gui.spectrum.SpectrumPanel;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.ui.Busyable;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.FileChooserDecorationFactory;
import de.mpa.client.ui.FileChooserDecorationFactory.DecorationType;
import de.mpa.client.ui.MultiExtensionFileFilter;
import de.mpa.client.ui.PanelConfig;
import de.mpa.client.ui.RolloverButtonUI;
import de.mpa.client.ui.SpectrumTableModel;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.io.GenericContainer;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;
import de.mpa.task.TaskManager;
import de.mpa.task.instances.SpectraTask;

/**
 * Panel for importing spectrum files to be used for searches.
 * 
 * @author T.Muth, A. Behne
 */
public class FilePanel extends JPanel implements Busyable {
	
	/**
	 * File text field.
	 */
	private FileTextField filesTtf;
	
	/**
	 * Add from file button.
	 */
	private JButton addFromFileBtn;
	
	/**
	 * Clear spectra from table button.
	 */
	private JButton clearBtn;	
	
	/**
	 * Spectrum panel.
	 */
	private JPanel specPnl;
	
	/**
	 * Splitpane instance.
	 */
	private JSplitPane splitPane;
	
	/**
	 * The panel containing controls for manipulating search settings.
	 */
	private SettingsPanel settingsPnl;
	
	/**
	 * Selected past of the .mgf or .dat File.
	 */
	private String selPath = Constants.DEFAULT_SPECTRA_PATH;
	
	/**
	 *  Selected file of the .mgf or .dat File
	 */
	private List<File> mascotSelFile = new ArrayList<File>();
	
	/**
	 * Button for the user to go to the next tab.
	 */
	private JButton nextBtn;
	
	/**
	 * Button for the user to go to the previous tab.
	 */
	private JButton prevBtn;
	
	/**
	 * Mapping from the spectrum to the byte positions.
	 */
	protected static Map<String, ArrayList<Long>> specPosMap = new HashMap<String, ArrayList<Long>>();
	
	/**
	 * Busy booelan.
	 */
	private boolean busy;
	
	/**
	 * Holds the previous enable states of the client frame's tabs.
	 */
	private boolean[] tabEnabled;
	
	private JCheckBox spectrumTableCbx;

	private JXTable spectraTable;

	private ArrayList<String> spectraTableToolTips;

	public File selectedFile; 
	
	/**
	 * Constructs a spectrum file selection and preview panel.
	 */
	public FilePanel() {
		this.initComponents();
	}
	
	/**
	 * Initializes the UI components.
	 */
	private void initComponents() {
		final ClientFrame clientFrame = ClientFrame.getInstance();
		
		this.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu, p, 5dlu"));
		
		// top panel containing buttons and spectrum tree table
		JPanel topPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu", "5dlu, p, 5dlu, f:p:g, 5dlu"));
		
		// button panel
		JPanel buttonPnl = new JPanel(new FormLayout("p, 5dlu, p:g, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu", "p"));
		
		// Textfield displaying amount of selected files
		filesTtf = new FileTextField();
		
		// button to add spectra from a file
		addFromFileBtn = new JButton("Add Spectrum File(s)...");
		// button for downloading and appending mgf files from remote DB 
		spectrumTableCbx = new JCheckBox("Spectrum Table Enabled");
		
		// button to reset file tree contents
		clearBtn = new JButton("Clear");
		clearBtn.setEnabled(false);
		
		// add components to button panel
		buttonPnl.add(new JLabel("Spectrum Files:"), CC.xy(1, 1));
		buttonPnl.add(filesTtf, CC.xy(3, 1));
		buttonPnl.add(addFromFileBtn, CC.xy(5, 1));
		buttonPnl.add(clearBtn, CC.xy(7, 1));
		buttonPnl.add(spectrumTableCbx, CC.xy(9, 1));
		
		// setup the column header tooltips
        spectraTableToolTips = new ArrayList<String>();
        spectraTableToolTips.add(null);
        spectraTableToolTips.add("Spectrum ID");
        spectraTableToolTips.add("Spectrum Title");
        spectraTableToolTips.add("Precusor m/z");
        spectraTableToolTips.add("Precursor Charge");
        spectraTableToolTips.add("Total Intensity");
        spectraTableToolTips.add("Highest Intensity");
        spectraTableToolTips.add("Number of Peaks");
		
		spectraTable = new JXTable() {
            protected JTableHeader createDefaultTableHeader() {
                return new JTableHeader(columnModel) {
                    public String getToolTipText(MouseEvent e) {
                        java.awt.Point p = e.getPoint();
                        int index = columnModel.getColumnIndexAtX(p.x);
                        int realIndex = columnModel.getColumn(index).getModelIndex();
                        String tip = spectraTableToolTips.get(realIndex);
                        return tip;
                    }
                };
            }
        };
        
        spectraTable.setModel(new SpectrumTableModel());
        setSpectrumTableProperties();
        
        spectraTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		spectraTable.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				refreshSpectrumPanel();
			}
		});
		spectraTable.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent evt) {
				refreshSpectrumPanel();
			}
		});
		
		// wrap tree table in scroll pane
		JScrollPane treeScpn = new JScrollPane(spectraTable);
		// add components to top panel
		topPnl.add(buttonPnl, CC.xy(2, 2));
		topPnl.add(treeScpn, CC.xy(2, 4));
		
		// wrap top panel in titled panel
		JXTitledPanel topTtlPnl = PanelConfig.createTitledPanel("File input", topPnl);
		
		/* Build bottom panels */
		// Spectrum panel containing spectrum viewer
		JPanel specBorderPnl = new JPanel(new FormLayout("5dlu, 0px:g, 5dlu", "5dlu, f:0px:g, 5dlu"));
		specBorderPnl.setName("Spectrum Viewer");
		specPnl = createDefaultSpectrumPanel();
		specBorderPnl.add(specPnl, CC.xy(2, 2));

		// wrap charts in card layout
		final CardLayout chartLyt = new CardLayout();
		final JPanel chartPnl = new JPanel(chartLyt);
		chartPnl.setPreferredSize(new Dimension());
		chartPnl.add(specBorderPnl, "spectrum");
		
		// wrap cards in titled panel
		JButton chartBtn = new JButton(IconConstants.BAR_CHART_ICON);
		chartBtn.setRolloverIcon(IconConstants.BAR_CHART_ROLLOVER_ICON);
		chartBtn.setPressedIcon(IconConstants.BAR_CHART_PRESSED_ICON);
		chartBtn.setPreferredSize(new Dimension(21, 20));
		chartBtn.setUI((RolloverButtonUI) RolloverButtonUI.createUI(chartBtn));
		
		final JXTitledPanel chartTtlPnl = PanelConfig.createTitledPanel(specBorderPnl.getName(), chartPnl, null, chartBtn);
		chartTtlPnl.setMinimumSize(new Dimension(450, 350));

		chartBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				chartLyt.next(chartPnl);
				for (Component comp : chartPnl.getComponents()) {
					if (comp.isVisible()) {
						chartTtlPnl.setTitle(comp.getName());
						return;
					}
				}
			}
		});
		
		JPanel btmPnl = new JPanel(new FormLayout("p:g, 5dlu, p", "f:p:g"));
		btmPnl.add(chartTtlPnl, CC.xy(1, 1));
		settingsPnl = new SettingsPanel();
		btmPnl.add(settingsPnl, CC.xy(3, 1));
		
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, topTtlPnl, btmPnl);
		splitPane.setBorder(null);
		splitPane.setDividerSize(10);
		((BasicSplitPaneUI) splitPane.getUI()).getDivider().setBorder(null);
		
		// create panel containing navigation buttons
		JPanel navPnl = new JPanel(new FormLayout("r:p:g, 5dlu, r:p", "b:p:g"));

		prevBtn = clientFrame.createNavigationButton(false, true);
		nextBtn = clientFrame.createNavigationButton(true, false);
		
		navPnl.add(prevBtn, CC.xy(1, 1));
		navPnl.add(nextBtn, CC.xy(3, 1));
		
		// add everything to main panel
		this.add(splitPane, CC.xy(2, 2));
		this.add(navPnl, CC.xy(2, 4));
		
		// register listeners
		addFromFileBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File startLocation = new File(selPath);
				
				JFileChooser fc = new JFileChooser(startLocation);
				fc.setFileFilter(new MultiExtensionFileFilter(
						"All supported formats (*.mgf)",
						Constants.MGF_FILE_FILTER));
				fc.addChoosableFileFilter(Constants.MGF_FILE_FILTER);
				fc.setAcceptAllFileFilterUsed(false);
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fc.setMultiSelectionEnabled(true);
				FileChooserDecorationFactory.decorate(fc, DecorationType.TEXT_PREVIEW);
				int result = fc.showOpenDialog(clientFrame);
				if (result == JFileChooser.APPROVE_OPTION) {
					new AddFileWorker(fc.getSelectedFiles()).execute();
				}
				
			}
		});		
		
		clearBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				// reset text field
				filesTtf.clear();
				
				if (spectrumTableCbx.isSelected()) {
					spectraTable.setModel(new SpectrumTableModel());
					
					// reset plots
					Container specCont = specPnl.getParent();
					specCont.removeAll();
					specCont.add(createDefaultSpectrumPanel(), CC.xy(2, 2));
					specCont.validate();
				}
				specPosMap.clear();				
				mascotSelFile.clear();
				
				// reset navigation button and search settings tab
				nextBtn.setEnabled(false);
			}
		});
	}
	
	/**
	 * Methods sets up the spectrum table properties.
	 */
    private void setSpectrumTableProperties() {
        spectraTable.getColumn(" ").setMaxWidth(30);
        spectraTable.getColumn(" ").setMinWidth(30);
        spectraTable.getColumn("Title").setMaxWidth(1000);
        spectraTable.getColumn("Title").setMinWidth(400);
        spectraTable.getColumn("m/z").setMaxWidth(60);
        spectraTable.getColumn("m/z").setMaxWidth(60);
        spectraTable.getColumn("Charge").setMaxWidth(60);
        spectraTable.getColumn("Charge").setMaxWidth(60);
        spectraTable.getColumn("Highest Intensity").setMaxWidth(120);
        spectraTable.getColumn("Highest Intensity").setMaxWidth(120);
        spectraTable.getColumn("Total Intensity").setMaxWidth(120);
        spectraTable.getColumn("Total Intensity").setMaxWidth(120);
        spectraTable.getColumn("No. Peaks").setMaxWidth(80);
        spectraTable.getColumn("No. Peaks").setMaxWidth(80);
        
        // Number of rows shown.
        spectraTable.setVisibleRowCount(10);
    }
    
	/**
	 * Method to refresh the spectrum viewer panel.
	 */
	protected void refreshSpectrumPanel() {
		try {
			SpectrumPanel specPnl = null;
			MascotGenericFileReader reader = GenericContainer.MGFReaders.get(selectedFile.getAbsolutePath());
			
			MascotGenericFile mgf = reader.loadSpectrum(spectraTable.getSelectedRow());
			if (mgf != null) {
				specPnl = new SpectrumPanel(mgf, false);
				specPnl.setBorder(UIManager.getBorder("ScrollPane.border"));
				specPnl.setShowResolution(false);
				} else {
					specPnl = createDefaultSpectrumPanel();
				}
				Container specCont = this.specPnl.getParent();
				specCont.removeAll();
				specCont.add(specPnl, CC.xy(2, 2));
				specCont.validate();
				this.specPnl = specPnl;
			} catch (Exception e) {
				JXErrorPane.showDialog(ClientFrame.getInstance(), new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
		}
	}
	
	/**
	 * Convenience method to create a default spectrum panel displaying peaks
	 * following a bell curve shape and displaying a string notifying the user
	 * that no valid spectrum is currently being selected for display.
	 * @return a default spectrum panel
	 */
	public static SpectrumPanel createDefaultSpectrumPanel() {
		// Create bell curve
		int peakCount = 29;
		double mean = 0.5, var = 0.05, stdDev = Math.sqrt(var);
		double[] xData = new double[peakCount], yData = new double[peakCount];
		for (int i = 0; i < peakCount; i++) {
			double x = i / (double) (peakCount - 1);
			xData[i] = x * 100.0;
			yData[i] = Math.round(100.0 * Math.pow(Math.exp(-(((x - mean) * (x - mean)) / 
					((2 * var)))), 1 / (stdDev * Math.sqrt(2 * Math.PI))));
		}
		SpectrumPanel panel = new SpectrumPanel(xData, yData, 0.0, "", "", 50, false, false, false) {
			@Override
			public void paint(Graphics g) {
				Graphics2D g2d = (Graphics2D) g;
				super.paint(g);

				g.setColor(new Color(255, 255, 255, 191));
				Insets insets = getBorder().getBorderInsets(this);
				g.fillRect(insets.left, insets.top,
						getWidth() - insets.right - insets.left,
						getHeight() - insets.top - insets.bottom);
				String str = "no spectrum selected";
				int strWidth = g2d.getFontMetrics().stringWidth(str);
				int strHeight = g2d.getFontMetrics().getHeight();
				int[] xData = iXAxisDataInPixels.get(0);
				float xOffset = xData[0] + (xData[xData.length - 1] - xData[0]) / 2.0f - strWidth / 2.0f;
				float yOffset = getHeight() / 2.0f;
				g2d.fillRect((int) xOffset - 2, (int) yOffset - g2d.getFontMetrics().getAscent() - 1, strWidth + 4, strHeight + 4);
				
				g2d.setColor(Color.BLACK);
				g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g2d.drawString(str, xOffset, yOffset);
			}
		};
		panel.setShowResolution(false);
		for (MouseListener l : panel.getMouseListeners()) {
			panel.removeMouseListener(l);
		}
		for (MouseMotionListener l : panel.getMouseMotionListeners()) {
			panel.removeMouseMotionListener(l);
		}
		panel.setBorder(UIManager.getBorder("ScrollPane.border"));
		return panel;
	}
	
	/**
	 * Returns the settings panel.
	 * @return the settings panel
	 */
	public SettingsPanel getSettingsPanel() {
		return settingsPnl;
	}

	@Override
	public void setBusy(boolean busy) {
		this.busy = busy;
		ClientFrame clientFrame = ClientFrame.getInstance();
		Cursor cursor = (busy) ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : null;
		clientFrame.setCursor(cursor);
		if (splitPane.getCursor().getType() == Cursor.WAIT_CURSOR) splitPane.setCursor(null);
		
		if (tabEnabled == null) {
			tabEnabled = new boolean[4];
			tabEnabled[ClientFrame.INDEX_INPUT_PANEL] = true;
		}
		// Enable/disable tabs
		for (int i = 0; i < tabEnabled.length - 1; i++) {
			boolean temp = clientFrame.isTabEnabledAt(i);
			clientFrame.setTabEnabledAt(i, tabEnabled[i]);
			tabEnabled[i] = temp;
		}
		// Enable/disable menu bar
		for (int i = 0; i < clientFrame.getJMenuBar().getMenuCount(); i++) {
			clientFrame.getJMenuBar().getMenu(i).setEnabled(!busy);
		}
		
		// Enable/disable buttons
		addFromFileBtn.setEnabled(!busy);
		if (busy) {
			clearBtn.setEnabled(false);
		} else {
			clearBtn.setEnabled(true);
		}
		prevBtn.setEnabled(!busy);
		nextBtn.setEnabled(!busy);
	}

	@Override
	public boolean isBusy() {
		return this.busy;
	}

	/**
	 * Worker class to index spectra and add local files to the spectrum table view.
	 */
	private class AddFileWorker extends SwingWorker<Boolean, Void> {
		/**
		 * The spectrum files.
		 */
		private File[] files;
		
		/**
		 * The total number of added spectra.
		 */
		private int specCount;

		/**
		 * Constructs a worker instance for parsing the specified spectrum files
		 * and inserting node representations of them into the spectrum table view.
		 * @param files the spectrum file descriptors
		 */
		public AddFileWorker(File[] files) {
			this.files = files;
		}

		@Override
		protected Boolean doInBackground() throws Exception {
			// appear busy
			FilePanel.this.setBusy(true);
			
			// List of files.
			List<File> filesList = new ArrayList<File>();
			for (File file : files) {
				filesList.add(file);
			}
			
			// Set MGF files to the client.
			Client.getInstance().setMgfFiles(filesList);
			
			// Get instance of job manager.
			TaskManager jobManager = TaskManager.getInstance();
			// Clear the job manager to account for unfinished jobs in the queue.
			jobManager.clear();
			
			SpectraTask spectraJob = new SpectraTask(filesList);
			jobManager.addJob(spectraJob);
			jobManager.run();
			selectedFile = filesList.get(0);
			this.specCount = GenericContainer.numberTotalSpectra;
			
			if (spectrumTableCbx.isSelected()) {
		        spectraTable.setModel(new SpectrumTableModel(selectedFile));
		        setSpectrumTableProperties();
			}
			return true;
		}
		
		@Override
		protected void done() {
			try {
				// check result value
				if (this.get().booleanValue()) {
					Client.getInstance().firePropertyChange("new message", null, "READING SPECTRUM FILE(S) FINISHED");
					
					// update file text field
					filesTtf.addFiles(this.files.length, this.specCount);
					
					// enable navigation buttons and search settings tab
					nextBtn.setEnabled(true);
				} else {
					Client.getInstance().firePropertyChange("new message", null, "READING SPECTRUM FILE(S) ABORTED");
				}
				
				// Update table
				if (spectrumTableCbx.isSelected()) {
					((DefaultTableModel) spectraTable.getModel()).fireTableDataChanged();
				}
				
				// stop appearing busy
				FilePanel.this.setBusy(false);
				if (this.specCount > 0) {
					clearBtn.setEnabled(true);
				}
			} catch (Exception e) {
				JXErrorPane.showDialog(ClientFrame.getInstance(),
						new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
			}
		}
	}
	
	/**
	 * Text field sub-class for displaying specific information about the number
	 * of files and spectra.
	 * @author A. Behne
	 */
	private class FileTextField extends JTextField {
		
		/** The number of files added. */
		private int numFiles = 0;
		/** The total number of spectra. */
		private int numSpectra = 0;
		/** The number of selected spectra. */
		private int numSelected = 0;
		
		/** Constructs a non-editable text field with the default text '0 files added' */
		public FileTextField() {
			super("No File(s) Selected");
			this.setEditable(false);
		}

		/**
		 * Adds the specified number of files and spectra to the displayed amounts.
		 * @param numFiles the number of files added
		 * @param numSpectra the total number of spectra added
		 */
		public void addFiles(int numFiles, int numSpectra) {
			this.numFiles += numFiles;
			this.numSpectra += numSpectra;
			this.clampValues();
			this.setText(this.createText());
		}

		/**
		 * Removes the specified number of files and spectra from the displayed amounts.
		 * @param numFiles the number of files removed
		 * @param numSpectra the total number of spectra removed
		 */
		public void removeFiles(int numFiles, int numSpectra) {
			this.addFiles(-numFiles, -numSpectra);
		}
		
		/** Resets the text field to its default (empty) value. */
		public void clear() {
			this.removeFiles(this.numFiles, this.numSpectra);
		}

		/** Convenience method to sanitize fields */
		private void clampValues() {
			if (this.numFiles < 0) {
				this.numFiles = 0;
			}
			if (this.numSpectra < 0) {
				this.numSpectra = 0;
			}
			if (this.numSelected < 0) {
				this.numSelected = 0;
			}
		}
		
		/** Convenience method to return a string containing file, spectrum and selection counts */
		private String createText() {
			// Return default string when no files were added
			if (this.numFiles == 0) {
				return "No File(s) Selected";
			}
			// Determine plural/singular forms
			String file = (this.numFiles == 1) ? "File" : "Files";
			String spec = (this.numSpectra == 1) ? "Spectrum" : "Spectra";
			// Return concatenated values
			return "" + this.numFiles + " " + file + " Selected / "
					+ this.numSpectra + " " + spec + " Total";
		}
		
	}
	
}