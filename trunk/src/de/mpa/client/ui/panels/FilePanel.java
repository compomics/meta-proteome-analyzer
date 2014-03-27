package de.mpa.client.ui.panels;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;
import org.jdesktop.swingx.treetable.TreeTableNode;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;

import com.compomics.mascotdatfile.util.mascot.MascotDatfile;
import com.compomics.mascotdatfile.util.mascot.Parameters;
import com.compomics.util.gui.spectrum.SpectrumPanel;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.model.AbstractExperiment;
import de.mpa.client.settings.Parameter;
import de.mpa.client.settings.ParameterMap;
import de.mpa.client.settings.SpectrumFetchParameters;
import de.mpa.client.settings.SpectrumFetchParameters.AnnotationType;
import de.mpa.client.settings.SpectrumFilterParameters;
import de.mpa.client.ui.Busyable;
import de.mpa.client.ui.CheckBoxTreeSelectionModel;
import de.mpa.client.ui.CheckBoxTreeTable;
import de.mpa.client.ui.CheckBoxTreeTable.IconCheckBox;
import de.mpa.client.ui.CheckBoxTreeTableNode;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.FileChooserDecorationFactory;
import de.mpa.client.ui.FileChooserDecorationFactory.DecorationType;
import de.mpa.client.ui.MultiExtensionFileFilter;
import de.mpa.client.ui.PanelConfig;
import de.mpa.client.ui.RolloverButtonUI;
import de.mpa.client.ui.SortableCheckBoxTreeTable;
import de.mpa.client.ui.SortableCheckBoxTreeTableNode;
import de.mpa.client.ui.SortableTreeTableModel;
import de.mpa.client.ui.TableConfig;
import de.mpa.client.ui.chart.HistogramChart;
import de.mpa.client.ui.chart.HistogramChart.HistogramChartType;
import de.mpa.client.ui.chart.HistogramData;
import de.mpa.client.ui.dialogs.AdvancedSettingsDialog;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.db.extractor.SpectrumExtractor;
import de.mpa.io.InputFileReader;
import de.mpa.io.MascotGenericFile;

/**
 * Panel for importing spectrum files to be used for searches.
 * 
 * @author A. Behne
 */
public class FilePanel extends JPanel implements Busyable {
	
	/**
	 * File text field.
	 */
	private FileTextField filesTtf;
	
	/**
	 * Filter button.
	 */
	private JButton filterBtn;
	
	/**
	 * Add from file button.
	 */
	private JButton addFromFileBtn;
	
	/**
	 * Add from database button.
	 */
	private JButton addFromDbBtn;
	
	/**
	 * Clear spectra from table button.
	 */
	private JButton clearBtn;	
	
	/**
	 * Tree table for the spectra.
	 */
	private CheckBoxTreeTable spectrumTreeTable;
	
	/**
	 * Spectrum panel.
	 */
	private JPanel specPnl;
	
	/**
	 * Spectrum histogram panel.
	 */
	private ChartPanel histPnl;
	
	/**
	 * Spectrum histogram scroll bar.
	 */
	private JScrollBar histBar;
	
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
	 * Input file reader instance.
	 */
	protected InputFileReader reader;
	
	/**
	 * Mapping from the spectrum to the byte positions.
	 */
	protected static Map<String, ArrayList<Long>> specPosMap = new HashMap<String, ArrayList<Long>>();
	
	/**
	 * Busy booelan.
	 */
	private boolean busy;
	
	/**
	 * List of spectrum TIC values.
	 */
	private ArrayList<Double> ticList = new ArrayList<Double>();

	/**
	 * Holds the previous enable states of the client frame's tabs.
	 */
	private boolean[] tabEnabled;
	
	/**
	 * Spectrum filter parameters.
	 */
	private ParameterMap filterParams = new SpectrumFilterParameters(); 
	
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
		JPanel buttonPnl = new JPanel(new FormLayout("p, 5dlu, p:g, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p", "p"));
		
		// Textfield displaying amount of selected files
		filesTtf = new FileTextField();
		
		// button to display filter settings panel
		filterBtn = new JButton("Filter settings...");
		filterBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				int res = AdvancedSettingsDialog.showDialog(clientFrame, "Filter Settings", true, filterParams);
				if (res == AdvancedSettingsDialog.DIALOG_CHANGED_ACCEPTED) {

				}
			}
		});
		
		// button to add spectra from a file
		addFromFileBtn = new JButton("Add from File...");
		// button for downloading and appending mgf files from remote DB 
		addFromDbBtn = new JButton("Add from DB...");
		
		// button to reset file tree contents
		clearBtn = new JButton("Clear all");
		clearBtn.setEnabled(false);
		
		// add components to button panel
		buttonPnl.add(new JLabel("Spectrum Files:"), CC.xy(1, 1));
		buttonPnl.add(filesTtf, CC.xy(3, 1));
		buttonPnl.add(filterBtn, CC.xy(5, 1));
		buttonPnl.add(addFromFileBtn, CC.xy(7, 1));
		buttonPnl.add(addFromDbBtn, CC.xy(9, 1));
		buttonPnl.add(clearBtn, CC.xy(11, 1));
		
		// tree table containing spectrum details
		final SortableCheckBoxTreeTableNode treeRoot = new SortableCheckBoxTreeTableNode(
				"no experiment selected", null, null, null, null) {
			public String toString() {
				AbstractExperiment experiment = clientFrame.getProjectPanel().getSelectedExperiment();
				if (experiment != null) {
					return experiment.getTitle();
				}
				return "no experiment selected";
			}
			@Override
			public Object getValueAt(int column) {
				if (column >= 2) {
					double sum = 0.0;
					for (int j = 0; j < getChildCount(); j++) {
						sum += ((Number) getChildAt(j).getValueAt(column)).doubleValue();
					}
					return sum;
				} else {
					return super.getValueAt(column);
				}
			}
		};
		final SortableTreeTableModel treeModel = new SortableTreeTableModel(treeRoot) {
			{ 
				this.setColumnIdentifiers(Arrays.asList(new String[] {
						"Spectra", "Path / Title", "Peaks", "TIC", "SNR"}));
			}
			@Override
			public Class<?> getColumnClass(int column) {
				switch (column) {
				case 2:
					return Integer.class;
				case 3:
				case 4:
					return Double.class;
				default:
					return String.class;
				}
			}
		};
		
		spectrumTreeTable = new SortableCheckBoxTreeTable(treeModel) {
			@Override
			protected void initializeColumnWidths() {
				TableConfig.setColumnWidths(this, new double[] { 2.5, 6.5, 1, 1, 1 });
			}
		};
		spectrumTreeTable.setRootVisible(true);
		spectrumTreeTable.getTableHeader().setReorderingAllowed(true);
		spectrumTreeTable.setPreferredScrollableViewportSize(new Dimension(320, 200));
		
		spectrumTreeTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent evt) {
				refreshSpectrumPanel();
			}
		});
		
		final CheckBoxTreeSelectionModel cbtsm = spectrumTreeTable.getCheckBoxTreeSelectionModel();
		cbtsm.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent evt) {
				// TODO: iterating through all nodes might be slow, test using large spectrum file
				int selLeaves = 0;
				Enumeration<TreeNode> dfe = treeRoot.depthFirstEnumeration();
				while (dfe.hasMoreElements()) {
					TreeNode treeNode = (TreeNode) dfe.nextElement();
					if (treeNode.isLeaf()) {
						if (cbtsm.isPathSelected(((CheckBoxTreeTableNode) treeNode).getPath(), true)) {
							selLeaves++;
						}
					}
				}
				filesTtf.setSelectedCount(selLeaves);
			}
		});
		
		// hack editor to contain additional button for removing tree nodes
		JPanel removePnl = new JPanel(new BorderLayout());
		removePnl.setOpaque(false);
		// create remove button
		JButton removeBtn = new JButton(IconConstants.CROSS_ICON);
		removeBtn.setRolloverIcon(IconConstants.CROSS_ROLLOVER_ICON);
		removeBtn.setPressedIcon(IconConstants.CROSS_PRESSED_ICON);
		removeBtn.setOpaque(false);
		removeBtn.setContentAreaFilled(false);
		removeBtn.setBorder(BorderFactory.createEmptyBorder(0, 0, 1, -1));
		removeBtn.setFocusPainted(false);
		// add remove button to panel, right-align
		removePnl.add(removeBtn, BorderLayout.EAST);
		
		// install new tree cell editor, overlay remove button on top when highlighting file node
		IconCheckBox editorBox = (IconCheckBox) spectrumTreeTable.getCellEditor(0, 0).getTableCellEditorComponent(
				spectrumTreeTable, null, true, 0, 0);
		editorBox.add(removePnl, BorderLayout.CENTER);
		spectrumTreeTable.setTreeCellEditor(spectrumTreeTable.new CheckBoxTreeCellEditor(editorBox) {
			@Override
			public Component getTableCellEditorComponent(JTable table,
					Object value, boolean isSelected, int row, int column) {
				// get original editor component
				Container editorComp = (Container) super.getTableCellEditorComponent(
						table, value, isSelected, row, column);
				// determine whether the editing row contains a file node
				JXTreeTable treeTbl = (JXTreeTable) table;
				boolean isFileNode = treeTbl.getPathForRow(row).getPathCount() == 2;
				// hide/show remove button panel accordingly
				editorComp.getComponent(editorComp.getComponentCount() - 1).setVisible(isFileNode);
				return editorComp;
			}
		});
		// install action listener to remove file node on press
		removeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				// check whether the clicked file node is the last remaining one
				if (((TreeNode) spectrumTreeTable.getTreeTableModel().getRoot()).getChildCount() == 1) {
					// let clear button action handle the rest
					clearBtn.doClick();
				} else {
					// get coordinates of button and convert them to table coordinates
					Point point = SwingUtilities.convertPoint(
							(Component) evt.getSource(), new Point(), spectrumTreeTable);
					// determine tree path of cell containing the button
					TreePath path = spectrumTreeTable.getPathForLocation(point.x, point.y);

					spectrumTreeTable.collapsePath(path);

					// determine table row of cell containing the button
					int row = spectrumTreeTable.getRowForPath(path);
					if (row == spectrumTreeTable.getSelectedRow()) {
						// select next row if clicked row was selected
						spectrumTreeTable.getSelectionModel().setSelectionInterval(row + 1, row + 1);
					}
					MutableTreeTableNode node = (MutableTreeTableNode) path.getLastPathComponent();

					CheckBoxTreeSelectionModel cbtsm = spectrumTreeTable.getCheckBoxTreeSelectionModel();

					// remove TICs from cache, count selected children
					int childCount = node.getChildCount();
					for (int i = 0; i < childCount; i++) {
						TreeTableNode childAt = node.getChildAt(i);
						// remove value located in TIC column
						ticList.remove(childAt.getValueAt(3));
					}

					// change selection
					if (cbtsm.areSiblingsSelected(path)) {
						cbtsm.addSelectionPath(path);
					} else {
						cbtsm.removeSelectionPath(path);
					}

					// remove node
					treeModel.removeNodeFromParent(node);

					// refresh files text field
					filesTtf.removeFiles(1, childCount);

					// refresh histogram
					if (ticList.isEmpty()) {
						histBar.setValues(1, 0, 1, 1);
					} else {
						int size = ticList.size();
						histBar.setValues(size, size / 10, 1, size);
					}

					// forcibly end editing mode
					TableCellEditor editor = spectrumTreeTable.getCellEditor();
					if (editor != null) {
						editor.cancelCellEditing();
					}
				}
			}
		});
		
		// modify column control button appearance
		TableConfig.configureColumnControl(spectrumTreeTable);
		
		// wrap tree table in scroll pane
		JScrollPane treeScpn = new JScrollPane(spectrumTreeTable);
		
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
		
		// Panel containing histogram plot
		JPanel histBorderPnl = new JPanel(new FormLayout("5dlu, 0px:g, 5dlu", "5dlu, f:0px:g, 5dlu"));
		histBorderPnl.setName("Total Ion Current Histogram");
		histPnl = createDefaultHistogramPanel();
		
		JScrollPane histScp = new JScrollPane(histPnl,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		for (ChangeListener cl : histScp.getViewport().getChangeListeners()) {
			histScp.getViewport().removeChangeListener(cl);
		}
		histScp.getViewport().setBackground(Color.WHITE);
		histScp.setPreferredSize(new Dimension());

		histBar = histScp.getVerticalScrollBar();
		histBar.setValues(1, 0, 1, 1);
		histBar.setBlockIncrement(36);
		DefaultBoundedRangeModel histBarMdl =
				(DefaultBoundedRangeModel) histBar.getModel();
		ChangeListener[] cbcl = histBarMdl.getChangeListeners();
		histBarMdl.removeChangeListener(cbcl[0]);

		histBar.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent evt) {
				JFreeChart chart = histPnl.getChart();
				if (chart != null) {
					if (ticList.isEmpty()) {
						Container cont = histPnl.getParent();
						histPnl = createDefaultHistogramPanel();
						cont.removeAll();
						cont.add(histPnl, CC.xy(2, 2));
						cont.validate();
					} else {
						int value = evt.getValue();
						
						Container cont = histPnl.getParent();
						histPnl = createHistogramPanel(ticList, value);
						cont.removeAll();
						cont.add(histPnl, CC.xy(2, 2));
						cont.validate();
					}
				}
			}
		});
		
		histBorderPnl.add(histScp, CC.xy(2, 2));
		
		// wrap charts in card layout
		final CardLayout chartLyt = new CardLayout();
		final JPanel chartPnl = new JPanel(chartLyt);
		chartPnl.setPreferredSize(new Dimension());
		chartPnl.add(specBorderPnl, "spectrum");
		chartPnl.add(histBorderPnl, "histogram");
		
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
						"All supported formats (*.mgf, *.dat)",
						Constants.MGF_FILE_FILTER,
						Constants.DAT_FILE_FILTER));
				fc.addChoosableFileFilter(Constants.MGF_FILE_FILTER);
				fc.addChoosableFileFilter(Constants.DAT_FILE_FILTER);
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
		
		addFromDbBtn.addActionListener(new ActionListener() {
			/** The spectrum database retrieval parameters. */
			private ParameterMap fetchParams = new SpectrumFetchParameters();
			
			@Override
			public void actionPerformed(ActionEvent evt) {
				int res = AdvancedSettingsDialog.showDialog(
						clientFrame, "Fetch Spectra from Database", true, fetchParams);
				if (res == AdvancedSettingsDialog.DIALOG_CHANGED_ACCEPTED) {
					new FetchWorker().execute();
				}
			}
			/** Convenience worker for fetching spectra from the database */
			class FetchWorker extends SwingWorker<File, Object> {
				@Override
				protected File doInBackground() throws Exception {
					FilePanel.this.setBusy(true);
					
					Client client = Client.getInstance();
					client.firePropertyChange("indeterminate", false, true);
					
					// extract settings from parameter map
					Integer expIDval = ((Integer[]) fetchParams.get("expID").getValue())[0];
					DefaultComboBoxModel annotMdl =	(DefaultComboBoxModel) fetchParams.get("annotated").getValue();
					Boolean s2fVal = (Boolean) fetchParams.get("saveToFile").getValue();
					client.getConnection();
					
					List<MascotGenericFile> dlSpec = client.downloadSpectra(
							expIDval.longValue(), (AnnotationType) annotMdl.getSelectedItem(),
							false, s2fVal);
					
					File file = new File("experiment_" + expIDval + ".mgf");
					FileOutputStream fos = new FileOutputStream(file);
					try {
						for (MascotGenericFile mgf : dlSpec) {
							mgf.writeToStream(fos);
						}
					} finally {
						fos.close();
					}
					
					return file;
				}
				
				@Override
				protected void done() {
					try {
						// Add downloaded file contents to tree
						File file = this.get();
						new AddFileWorker(new File[] { file }).execute();
					} catch (Exception e) {
						e.printStackTrace();
					}
					FilePanel.this.setBusy(false);
					Client.getInstance().firePropertyChange("indeterminate", true, false);
				}
			}
		});
		
		clearBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				TableConfig.clearTable(spectrumTreeTable);
				spectrumTreeTable.getRowSorter().allRowsChanged();
				spectrumTreeTable.getCheckBoxTreeSelectionModel().clearSelection();
				
				// reset text field
				filesTtf.clear();
				
				// reset plots
				Container specCont = specPnl.getParent();
				// FIXME: NPE thrown here!
				specCont.removeAll();
				specCont.add(createDefaultSpectrumPanel(), CC.xy(2, 2));
				specCont.validate();

				// XXX
				histBar.setValues(1, 0, 1, 1);
				Container cont = histPnl.getParent();
				histPnl = createDefaultHistogramPanel();
				cont.removeAll();
				cont.add(histPnl, CC.xy(2, 2));
				cont.validate();
				
				ticList.clear();
				specPosMap.clear();
				
				mascotSelFile.clear();
				
				// reset navigation button and search settings tab
				nextBtn.setEnabled(false);
			}
		});
	}

	/**
	 * Returns a spectrum file encoded in the specified leaf node of the file panel's tree table.
	 * @param spectrumNode The leaf node containing information pertaining to the whereabouts of its corresponding spectrum file.
	 * @return The spectrum file.
	 * @throws IOException if reading the spectrum file fails
	 * @throws SQLException if fetching spectrum data from the database fails
	 */
	public MascotGenericFile getSpectrumForNode(CheckBoxTreeTableNode spectrumNode) throws IOException, SQLException {
		CheckBoxTreeTableNode fileNode = (CheckBoxTreeTableNode) spectrumNode.getParent();
		File file = (File) fileNode.getValueAt(0);
		if ((reader != null) && (!reader.getFilename().equals(file.getName()))) {
			// TODO: process file switching using background worker linked to progress bar
			reader = InputFileReader.createInputFileReader(file);
			reader.survey();
		}
		int spectrumIndex = (Integer) spectrumNode.getValueAt(0) - 1;
		
		MascotGenericFile spectrum = reader.loadSpectrum(spectrumIndex);
		Long spectrumID = spectrum.getSpectrumID();
		if (spectrumID != null) {
			// this is just a dummy spectrum, fetch from database
			spectrum = new SpectrumExtractor(Client.getInstance().getDatabaseConnection()).getSpectrumBySpectrumID(spectrumID);
		}
		return spectrum;
	}
	
	/**
	 * Method to refresh the spectrum viewer panel.
	 */
	protected void refreshSpectrumPanel() {
		try {
			TreePath path = spectrumTreeTable.getPathForRow(spectrumTreeTable.getSelectedRow());
			if (path != null) {
				CheckBoxTreeTableNode node = (CheckBoxTreeTableNode) path.getLastPathComponent();
				SpectrumPanel specPnl = null;
				if (node.isLeaf() && (node.getParent() != null)) {
						MascotGenericFile mgf = getSpectrumForNode(node);
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
			}
		} catch (Exception e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),
					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
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
	 * Convenience method to create a histogram panel from the provided values
	 * and an index specifying the upper boundary of the range of values to be
	 * considered.
	 * @param values the values to bin
	 * @param toIndex the upper boundary index of the values to bin
	 * @return a histogram panel
	 */
	private ChartPanel createHistogramPanel(Collection<Double> values, int toIndex) {
		return new HistogramPanel(new HistogramChart(
				new HistogramData(values, 40, 0, toIndex), HistogramChartType.TOTAL_ION_HIST));
	}
	
	/**
	 * Convenience method to create a default histogram panel displaying bars
	 * following a bell curve shape and displaying a string notifying the user
	 * that no files were added yet.
	 * @return a default histogram panel
	 */
	private ChartPanel createDefaultHistogramPanel() {
		// Create bell curve
		int binCount = 40;
		double mean = 0.5, variance = 0.05, stdDeviation = Math.sqrt(variance);
		List<Double> ticList = new ArrayList<Double>();
		for (int i = 0; i < binCount; i++) {
			double x = i / (double) (binCount - 1);
			long y = Math.round(100.0 * Math.pow(Math.exp(-(((x - mean) * (x - mean)) / 
					((2 * variance)))), 1 / (stdDeviation * Math.sqrt(2 * Math.PI))));
			for (int j = 0; j < y; j++) {
				ticList.add(x * binCount); 
			}
		}
		
		return new HistogramPanel(new HistogramChart(new HistogramData(
				ticList, binCount, 0, ticList.size()), HistogramChartType.TOTAL_ION_HIST)) {
			@Override
			public void paint(Graphics g) {
				super.paint(g);

				g.setColor(new Color(255, 255, 255, 191));
				Insets insets = getBorder().getBorderInsets(this);
				g.fillRect(insets.left, insets.top,
						getWidth() - insets.right - insets.left,
						getHeight() - insets.top - insets.bottom);
				
				Graphics2D g2d = (Graphics2D) g;
				String str = "no file(s) added";
				int strWidth = g2d.getFontMetrics().stringWidth(str);
				int strHeight = g2d.getFontMetrics().getHeight();
				Rectangle2D dataArea = getChartRenderingInfo().getPlotInfo().getDataArea();
				float xOffset = (float) (dataArea.getX() + dataArea.getWidth() / 2.1) - strWidth / 2.0f;
				float yOffset = getHeight() / 2.0f;
				g2d.fillRect((int) xOffset - 2, (int) yOffset - g2d.getFontMetrics().getAscent() - 1, strWidth + 4, strHeight + 4);
				
				g2d.setColor(Color.BLACK);
				g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g2d.drawString(str, xOffset, yOffset);
			}
		};
	}

	/**
	 * Gets the selected MASCOT files.
	 * @return The selected files of the .mgf or .dat file
	 */
	public List<File> getSelectedMascotFiles() {
		return mascotSelFile;
	}
	
	/**
	 * Returns whether any files have been added to the panel.
	 * @return
	 */
	public boolean hasFiles() {
		if (spectrumTreeTable != null) {
			TreeTableNode root = (TreeTableNode) spectrumTreeTable.getTreeTableModel().getRoot();
			return (root.getChildCount() > 0);
		}
		return false;
	}

	/**
	 * Method to return the file panel's tree component.
	 * @return The panel's SpectrumTree.
	 */
	public CheckBoxTreeTable getCheckBoxTree() {
		return spectrumTreeTable;
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
		addFromDbBtn.setEnabled(!busy);
		if (busy) {
			clearBtn.setEnabled(false);
		} else {
			CheckBoxTreeTableNode treeRoot = (CheckBoxTreeTableNode) ((DefaultTreeTableModel) spectrumTreeTable.getTreeTableModel()).getRoot();
			clearBtn.setEnabled(treeRoot.getChildCount() > 0);
		}
		prevBtn.setEnabled(!busy);
		nextBtn.setEnabled(!busy);
		
	}

	@Override
	public boolean isBusy() {
		return this.busy;
	}

	/**
		 * Worker class to add local files to the spectrum tree view.
		 * 
		 * @author behne
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
			 * and inserting node representations of them into the spectrum tree
			 * view.
			 * @param files the spectrum file descriptors
			 */
			public AddFileWorker(File[] files) {
				this.files = files;
			}
	
			@Override
			protected Boolean doInBackground() throws Exception {
				// appear busy
				FilePanel.this.setBusy(true);
				
				final Client client = Client.getInstance();
				
				DefaultTreeTableModel treeModel = (DefaultTreeTableModel) spectrumTreeTable.getTreeTableModel();
				CheckBoxTreeTableNode treeRoot = (CheckBoxTreeTableNode) treeModel.getRoot();
				CheckBoxTreeSelectionModel cbtsm = spectrumTreeTable.getCheckBoxTreeSelectionModel();
	
				long totalSize = 0L;
				for (File file : files) {
					totalSize += file.length();
				}
				client.firePropertyChange("resetall", 0, totalSize);
				
				int i = 0;
				for (File file : files) {
					client.firePropertyChange("new message", null, "READING SPECTRUM FILE " + ++i + "/" + files.length);
	
					reader = InputFileReader.createInputFileReader(file);
					
					if (file.getName().toLowerCase().endsWith(".mgf")) {
						// do nothing
					} else if (Constants.DAT_FILE_FILTER.accept(file)) {
						
						DatabaseSearchSettingsPanel dbSettingsPnl =
								ClientFrame.getInstance().getFilePanel().getSettingsPanel().getDatabaseSearchSettingsPanel();
						dbSettingsPnl.setMascotEnabled(true);
						MascotDatfile mascotDatfile = new MascotDatfile(new BufferedReader(new FileReader(file)));
						Parameters parameters = mascotDatfile.getParametersSection();
						ParameterMap mascotParams =
								dbSettingsPnl.getMascotParameterMap();
						
						mascotParams.put("precTol",
								new Parameter(null, parameters.getTOL(), "General", null));
						mascotParams.put("fragTol",
								new Parameter(null, parameters.getITOL(), "General", null));
						mascotParams.put("missClv",
								new Parameter(null, parameters.getPFA(), "General", null));
						
						boolean decoy = mascotDatfile.getDecoyQueryToPeptideMap() != null;
						mascotParams.put("filter", new Parameter("Peptide Ion Score|False Discovery Rate", new Object[][] { { true, 15, true }, { false, 0.05, decoy } }, "Filtering", "Peptide Ion Score Threshold|Maximum False Discovery Rate"));
						
						mascotSelFile.add(file);
					
					} else {
						System.out.println("If you got here something went horribly wrong!");
					}
					
					ArrayList<Long> positions = new ArrayList<Long>();
					try {
	//						reader = new MascotGenericFileReader(file, LoadMode.NONE);
	
						client.firePropertyChange("resetcur", -1L, file.length());
						
						reader.addPropertyChangeListener(new PropertyChangeListener() {
							@Override
							public void propertyChange(PropertyChangeEvent pce) {
								if (pce.getPropertyName() == "progress") {
									client.firePropertyChange("progress", pce.getOldValue(), pce.getNewValue());
								}
							}
						});
						reader.survey();
	//						List<MascotGenericFile> mgfList = (ArrayList<MascotGenericFile>) reader.getSpectrumFiles(false);
	//						totalSpectraList.addAll(mgfList);
						
						positions.addAll(reader.getSpectrumPositions(false));
						specPosMap.put(file.getAbsolutePath(), positions);
						this.specCount += positions.size();
	
						client.firePropertyChange("new message", null, "BUILDING TREE NODE " + i + "/" + files.length);
						
						File parentFile = file.getParentFile();
						if (parentFile == null) {
							parentFile = file;
						}
						SortableCheckBoxTreeTableNode fileNode = new SortableCheckBoxTreeTableNode(
								file, parentFile.getPath(), null, null, null) {
							public String toString() {
								return ((File) userObject).getName();
							};
							@Override
							public Object getValueAt(int column) {
								if (column >= 2) {
									double sum = 0.0;
									for (int j = 0; j < getChildCount(); j++) {
										sum += ((Number) getChildAt(j).getValueAt(column)).doubleValue();
									}
									return sum;
								} else {
									return super.getValueAt(column);
								}
							}
						};
						
						client.firePropertyChange("resetcur", -1L, positions.size());
	
						int index = 1;
						List<TreePath> toBeAdded = new ArrayList<TreePath>();
						for (int j = 0; j < positions.size(); j++) {
							MascotGenericFile mgf = reader.loadSpectrum(index - 1);
							
							Long spectrumID = mgf.getSpectrumID();
							if (spectrumID != null) {
								// this is just a dummy spectrum, fetch from database
								mgf = new SpectrumExtractor(client.getDatabaseConnection()).getSpectrumBySpectrumID(spectrumID);
							}
	//							totalSpectraList.add(mgf);
							ticList.add(mgf.getTotalIntensity());
	
							// examine spectrum regarding filter criteria
							int numPeaks = 0;
							double noiseLvl = (Double) filterParams.get("noiselvl").getValue();
							for (double intensity : mgf.getPeaks().values()) {
								if (intensity > noiseLvl) {
									numPeaks++;
								}
							}
							double TIC = mgf.getTotalIntensity();
							double SNR = mgf.getSNR(noiseLvl);
	
							// append new spectrum node to file node
							SortableCheckBoxTreeTableNode spectrumNode = new SortableCheckBoxTreeTableNode(
									index, mgf.getTitle(), numPeaks, TIC, SNR) {
								public String toString() {
									return "Spectrum " + super.toString();
								}
							};
							fileNode.add(spectrumNode);
							
							// Get the minimum of signifikant peaks from the filter parameters.
							Integer[] minPeaks = (Integer[]) filterParams.get("minpeaks").getValue(); 
							Integer[] minTIC = (Integer[]) filterParams.get("mintic").getValue();
							double minSNR = (Double) filterParams.get("minsnr").getValue();
	
							if ((numPeaks >= minPeaks[0]) && (TIC >= minTIC[0]) && (SNR >= minSNR)) {
								toBeAdded.add(new TreePath(new Object[] {treeRoot, fileNode, spectrumNode}));
							}
							index++;
							
							client.firePropertyChange("progressmade", false, true);
						}
	
						client.firePropertyChange("indeterminate", false, true);
						client.firePropertyChange("new message", null, "INSERTING TREE NODE " + i + "/" + files.length);
						
						// append new file node to root and initially deselect it
						if (fileNode.getChildCount() > 0) {
							treeModel.insertNodeInto(fileNode, treeRoot, treeRoot.getChildCount());
							cbtsm.removeSelectionPath(fileNode.getPath());
							// reselect spectrum nodes that meet filter criteria
							cbtsm.addSelectionPaths(toBeAdded);
						}
						client.firePropertyChange("indeterminate", true, false);
					} catch (Exception e) {
						JXErrorPane.showDialog(ClientFrame.getInstance(),
								new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
						return false;
					}
					
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
						
						// show histogram
						if (!ticList.isEmpty()) {
							int size = ticList.size();
	//						// TODO: Currently the histogram is shown for all files... would be better to show only for selected files/spectra.
							histBar.setValues(size, size / 10, 1, size);
						}
						
						// enable navigation buttons and search settings tab
						nextBtn.setEnabled(true);
//						ClientFrame.getInstance().getTabbedPane().setEnabledAt(ClientFrame.SETTINGS_PANEL, true);
//						tabEnabled[ClientFrame.SETTINGS_PANEL] = true;
					} else {
						Client.getInstance().firePropertyChange("new message", null, "READING SPECTRUM FILE(S) ABORTED");
					}
					
					// expand tree table root
					spectrumTreeTable.expandRow(0);
					
					// stop appearing busy
					FilePanel.this.setBusy(false);
					
				} catch (Exception e) {
					JXErrorPane.showDialog(ClientFrame.getInstance(),
							new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
				}
			}
		}

	/**
	 * Convenience class for wrapping histogram charts in pre-configured panels.
	 * 
	 * @author A. Behne
	 */
	private class HistogramPanel extends ChartPanel {
	
		public HistogramPanel(HistogramChart histogram) {
			super(histogram.getChart());
			
			this.setPreferredSize(new Dimension(0, 0));
			this.setBorder(BorderFactory.createEmptyBorder());
			
			addLogarithmicAxisWidget(histogram);
		}
	
		/**
		 * Convenience method to add widget to panel to choose between
		 * logarithmic and linear y axis presentation
		 * @param histogram the histogram reference
		 */
		private void addLogarithmicAxisWidget(final HistogramChart histogram) {
			
			this.setLayout(new FormLayout("3dlu, l:p, 0px:g", "0px:g, b:p, 2dlu"));
			
			JCheckBox logChk = new JCheckBox("logarithmic y axis", true);
			logChk.setOpaque(false);
			logChk.setFocusPainted(false);
			
			logChk.addItemListener(new ItemListener() {
				private XYPlot plot = (XYPlot) histogram.getChart().getPlot();
				@Override
				public void itemStateChanged(ItemEvent evt) {
					ValueAxis yAxis;
					if (evt.getStateChange() == ItemEvent.SELECTED) {
						yAxis = new LogarithmicAxis(HistogramChartType.TOTAL_ION_HIST.getYLabel());
				        ((LogarithmicAxis) yAxis).setAllowNegativesFlag(true);
					} else {
						yAxis = new NumberAxis(HistogramChartType.TOTAL_ION_HIST.getYLabel());
						yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
					}
					yAxis.setLabelFont(plot.getRangeAxis().getLabelFont());
			        yAxis.setTickLabelFont(plot.getRangeAxis().getTickLabelFont());
					plot.setRangeAxis(yAxis);
				}
			});
			
			this.add(logChk, CC.xy(2, 2));
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
			super("0 files added");
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
				return "0 files added";
			}
			// Determine plural/singular forms
			String file = (this.numFiles == 1) ? "file" : "files";
			String spec = (this.numSpectra == 1) ? "spectrum" : "spectra";
			String sel = (this.numSelected == 1) ? "spectrum" : "spectra";
			// Return concatenated values
			return "" + this.numFiles + " " + file + " added, "
					+ this.numSpectra + " " + spec + " total, "
					+ this.numSelected + " " + sel + " selected";
		}
		
		/** Setter for the selection count. */
		public void setSelectedCount(int numSelected) {
			this.numSelected = numSelected;
			this.setText(this.createText());
		}
		
	}
	
}