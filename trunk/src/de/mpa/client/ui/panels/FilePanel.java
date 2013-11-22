package de.mpa.client.ui.panels;

import java.awt.BorderLayout;
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
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.MultiSplitLayout;
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
import de.mpa.client.model.ProjectContent;
import de.mpa.client.settings.FilterSettings;
import de.mpa.client.settings.Parameter;
import de.mpa.client.settings.ParameterMap;
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
import de.mpa.client.ui.SortableCheckBoxTreeTable;
import de.mpa.client.ui.SortableCheckBoxTreeTableNode;
import de.mpa.client.ui.SortableTreeTableModel;
import de.mpa.client.ui.TableConfig;
import de.mpa.client.ui.chart.HistogramChart;
import de.mpa.client.ui.chart.HistogramChart.HistogramChartType;
import de.mpa.client.ui.chart.HistogramData;
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
	
	private FileTextField filesTtf;
	private JButton filterBtn;
	private JButton addBtn;
	private JButton addDbBtn;
	private JButton clearBtn;	
	private CheckBoxTreeTable treeTbl;
	private JPanel specPnl;	
	private ChartPanel chartPnl;
	private JScrollBar chartBar;
	private JXMultiSplitPane split;
	
	// Selected past of the .mgf or .dat File
	private String selPath = Constants.DEFAULT_SPECTRA_PATH;
	
	// Selected file of the .mgf or .dat File
	private List<File> mascotSelFile = new ArrayList<File>();
	
//	
//	// Selected path of .mgf Files
//	private String mascotSelPath;
	
	private JButton nextBtn;
	private JButton prevBtn;
	
	protected InputFileReader reader;
	protected static Map<String, ArrayList<Long>> specPosMap = new HashMap<String, ArrayList<Long>>();
	private FilterSettings filterSet = new FilterSettings(5, 100.0, 1.0, 2.5);
	private boolean busy;
	private ArrayList<Double> ticList = new ArrayList<Double>();
	
	/**
	 * Constructs a spectrum file selection and preview panel.
	 */
	public FilePanel() {
		this.initComponents();
	}

	private void initComponents() {
		final ClientFrame clientFrame = ClientFrame.getInstance();
		
		this.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu, p, 5dlu"));
		
		// top panel containing buttons and spectrum tree table
		JPanel topPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu", "5dlu, p, 5dlu, f:p:g, 5dlu"));
		
		// button panel
		JPanel buttonPnl = new JPanel(new FormLayout("p, 5dlu, p:g, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p", "p"));
		
		// Textfield displaying amount of selected files
		filesTtf = new FileTextField();
		
		// panel containing filter settings
		final JPanel filterPnl = new JPanel(new FormLayout("p, 5dlu, p:g, 5dlu, p", "p, 5dlu, p, 5dlu, p, 5dlu, p"));
		
		// init filter popup panel components
		final JSpinner minPeaksSpn = new JSpinner(new SpinnerNumberModel(filterSet.getMinPeaks(), 0, null, 1));
		final JSpinner minTICspn = new JSpinner(new SpinnerNumberModel(filterSet.getMinTIC(), 0.0, null, 1.0));
		minTICspn.setEditor(new JSpinner.NumberEditor(minTICspn, "0.0"));
		final JSpinner minSNRspn = new JSpinner(new SpinnerNumberModel(filterSet.getMinSNR(), 0.0, null, 0.1));
		minSNRspn.setEditor(new JSpinner.NumberEditor(minSNRspn, "0.0"));
		final JSpinner noiseLvlSpn = new JSpinner(new SpinnerNumberModel(filterSet.getNoiseLvl(), 0.0, null, 0.1));
		noiseLvlSpn.setEditor(new JSpinner.NumberEditor(noiseLvlSpn, "0.0"));
		JButton noiseEstBtn = new JButton("Estimate...");
		noiseEstBtn.setToolTipText("Not yet implemented");
		noiseEstBtn.setEnabled(false);

		// add filter panel components
		filterPnl.add(new JLabel("min. significant peaks"), CC.xyw(1, 1, 3));
		filterPnl.add(minPeaksSpn, CC.xy(5, 1));
		filterPnl.add(new JLabel("min. total ion current"), CC.xyw(1, 3, 3));
		filterPnl.add(minTICspn, CC.xy(5, 3));
		filterPnl.add(new JLabel("min. signal/noise ratio"), CC.xyw(1, 5, 3));
		filterPnl.add(minSNRspn, CC.xy(5, 5));
		filterPnl.add(new JLabel("noise level"), CC.xy(1, 7));
		filterPnl.add(noiseLvlSpn, CC.xy(3, 7));
		filterPnl.add(noiseEstBtn, CC.xy(5, 7));
		
		// button to display filter settings panel
		filterBtn = new JButton("Filter settings...");
		filterBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int res = JOptionPane.showConfirmDialog(clientFrame, filterPnl, "Filter Settings", 
						JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE);
				if (res == JOptionPane.OK_OPTION) {
					// update settings
					filterSet = new FilterSettings((Integer) minPeaksSpn.getValue(),
							(Double) minTICspn.getValue(),
							(Double) minSNRspn.getValue(),
							(Double) noiseLvlSpn.getValue());
				} else {	// cancel option or window close option
					// revert to old settings
					minPeaksSpn.setValue(filterSet.getMinPeaks());
					minTICspn.setValue(filterSet.getMinTIC());
					minSNRspn.setValue(filterSet.getMinSNR());
					noiseLvlSpn.setValue(filterSet.getNoiseLvl());
				}
			}
		});
		
		// button to add spectra from a file
		addBtn = new JButton("Add from File...");
		// button for downloading and appending mgf files from remote DB 
		addDbBtn = new JButton("Add from DB...");
		
		// configure popup panel containing settings for remote mgf fetching
		final JPanel fetchPnl = new JPanel(new FormLayout(
				"5dlu, p, 5dlu, p:g, 5dlu",
				"5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));
		
		final JSpinner expIdSpn = new JSpinner(new SpinnerNumberModel(1L, 1L, null, 1L));
		final JCheckBox annotChk = new JCheckBox("Fetch only annotated spectra", false);
		final JCheckBox libChk = new JCheckBox("Fetch from spectral library", false);
		final JCheckBox saveChk = new JCheckBox("Save spectrum contents to file", false);
		
		fetchPnl.add(new JLabel("Database Experiment ID"), CC.xy(2, 2));
		fetchPnl.add(expIdSpn, CC.xy(4, 2));
		fetchPnl.add(annotChk, CC.xyw(2, 4, 3));
		fetchPnl.add(libChk, CC.xyw(2, 6, 3));
		fetchPnl.add(saveChk, CC.xyw(2, 8, 3));
		
		// button to reset file tree contents
		clearBtn = new JButton("Clear all");
		clearBtn.setEnabled(false);
		
		// add components to button panel
		buttonPnl.add(new JLabel("Spectrum Files:"), CC.xy(1, 1));
		buttonPnl.add(filesTtf, CC.xy(3, 1));
		buttonPnl.add(filterBtn, CC.xy(5, 1));
		buttonPnl.add(addBtn, CC.xy(7, 1));
		buttonPnl.add(addDbBtn, CC.xy(9, 1));
		buttonPnl.add(clearBtn, CC.xy(11, 1));
		
		// tree table containing spectrum details
		final SortableCheckBoxTreeTableNode treeRoot = new SortableCheckBoxTreeTableNode(
				"no project selected", null, null, null, null) {
			public String toString() {
				ProjectContent pj =  clientFrame.getProjectPanel().getCurrentProjectContent();
				return (pj != null) ? pj.getProjectTitle() : "no project selected";
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
		
		treeTbl = new SortableCheckBoxTreeTable(treeModel) {
			@Override
			protected void initializeColumnWidths() {
				TableConfig.setColumnWidths(this, new double[] { 2.5, 6.5, 1, 1, 1 });
			}
		};
		treeTbl.setRootVisible(true);
		treeTbl.getTableHeader().setReorderingAllowed(true);
		treeTbl.setPreferredScrollableViewportSize(new Dimension(320, 200));
		
		treeTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent evt) {
				refreshSpectrumPanel();
			}
		});
		
		final CheckBoxTreeSelectionModel cbtsm = treeTbl.getCheckBoxTreeSelectionModel();
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
		IconCheckBox editorBox = (IconCheckBox) treeTbl.getCellEditor(0, 0).getTableCellEditorComponent(
				treeTbl, null, true, 0, 0);
		editorBox.add(removePnl, BorderLayout.CENTER);
		treeTbl.setTreeCellEditor(treeTbl.new CheckBoxTreeCellEditor(editorBox) {
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
				if (((TreeNode) treeTbl.getTreeTableModel().getRoot()).getChildCount() == 1) {
					// let clear button action handle the rest
					clearBtn.doClick();
				} else {
					// get coordinates of button and convert them to table coordinates
					Point point = SwingUtilities.convertPoint(
							(Component) evt.getSource(), new Point(), treeTbl);
					// determine tree path of cell containing the button
					TreePath path = treeTbl.getPathForLocation(point.x, point.y);

					treeTbl.collapsePath(path);

					// determine table row of cell containing the button
					int row = treeTbl.getRowForPath(path);
					if (row == treeTbl.getSelectedRow()) {
						// select next row if clicked row was selected
						treeTbl.getSelectionModel().setSelectionInterval(row + 1, row + 1);
					}
					MutableTreeTableNode node = (MutableTreeTableNode) path.getLastPathComponent();

					CheckBoxTreeSelectionModel cbtsm = treeTbl.getCheckBoxTreeSelectionModel();

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
						chartBar.setValues(1, 0, 1, 1);
					} else {
						int size = ticList.size();
						chartBar.setValues(size, size / 10, 1, size);
					}

					// forcibly end editing mode
					TableCellEditor editor = treeTbl.getCellEditor();
					if (editor != null) {
						editor.cancelCellEditing();
					}
				}
			}
		});
		
		// modify column control button appearance
		TableConfig.configureColumnControl(treeTbl);
		
		// wrap tree table in scroll pane
		JScrollPane treeScpn = new JScrollPane(treeTbl);
		
		// add components to top panel
		topPnl.add(buttonPnl, CC.xy(2, 2));
		topPnl.add(treeScpn, CC.xy(2, 4));
		
		// wrap top panel in titled panel
		JXTitledPanel topTtlPnl = PanelConfig.createTitledPanel("File input", topPnl);
		
		/* Build bottom panels */
		// Spectrum panel containing spectrum viewer
		specPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));
		
		// Spectrum viewer
		SpectrumPanel viewer = createDefaultSpectrumPanel();
		specPnl.add(viewer, CC.xy(2, 2));
		
		// wrap spectrum panel in titled panel
		JXTitledPanel spectrumTtlPnl = PanelConfig.createTitledPanel("Spectrum Viewer", specPnl);
		spectrumTtlPnl.setMinimumSize(new Dimension(450, 350));
		
		// Panel containing histogram plot
		chartPnl = createDefaultHistogramPanel();
		
		JScrollPane chartScp = new JScrollPane(chartPnl,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		for (ChangeListener cl : chartScp.getViewport().getChangeListeners()) {
			chartScp.getViewport().removeChangeListener(cl);
		}
		chartScp.getViewport().setBackground(Color.WHITE);

		chartBar = chartScp.getVerticalScrollBar();
		chartBar.setValues(1, 0, 1, 1);
		chartBar.setBlockIncrement(36);
		DefaultBoundedRangeModel chartBarMdl =
				(DefaultBoundedRangeModel) chartBar.getModel();
		ChangeListener[] cbcl = chartBarMdl.getChangeListeners();
		chartBarMdl.removeChangeListener(cbcl[0]);

		chartBar.addAdjustmentListener(new AdjustmentListener() {
			public void adjustmentValueChanged(AdjustmentEvent evt) {
				JFreeChart chart = chartPnl.getChart();
				if (chart != null) {
					if (ticList.isEmpty()) {
						Container cont = chartPnl.getParent();
						chartPnl = createDefaultHistogramPanel();
						cont.removeAll();
						cont.add(chartPnl, CC.xy(2, 2));
						cont.validate();
					} else {
						int value = evt.getValue();
						
						Container cont = chartPnl.getParent();
						chartPnl = createHistogramPanel(ticList, value);
						cont.removeAll();
						cont.add(chartPnl, CC.xy(2, 2));
						cont.validate();
					}
				}
			}
		});

		// wrap scroll pane in panel with 5dlu margin around it
		JPanel chartMarginPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu",
				"5dlu, f:p:g, 5dlu"));
		chartMarginPnl.add(chartScp, CC.xy(2, 2));
		
		// wrap chart panel in titled panel
		JXTitledPanel chartTtlPnl = PanelConfig.createTitledPanel(
				"Histogram", chartMarginPnl);
		chartTtlPnl.setMinimumSize(new Dimension(450, 350));
		
		// add titled panels to split pane
		String layoutDef = "(COLUMN top (ROW (LEAF weight=0.5 name=spectrum) (LEAF weight=0.5 name=histogram))";
		MultiSplitLayout.Node modelRoot = MultiSplitLayout.parseModel(layoutDef);
		
		split = new JXMultiSplitPane() {
			@Override
			public void setCursor(Cursor cursor) {
				if (busy) {
					if ((cursor == null) || (cursor.getType() == Cursor.DEFAULT_CURSOR)) {
						cursor = clientFrame.getCursor();
					}
				}
				super.setCursor(cursor);
			}
		};
		split.setDividerSize(12);
		split.getMultiSplitLayout().setModel(modelRoot);
		split.add(topTtlPnl, "top");
		split.add(spectrumTtlPnl, "spectrum");
		split.add(chartTtlPnl, "histogram");
		
		// create panel containing navigation buttons
		JPanel navPnl = new JPanel(new FormLayout("r:p:g, 5dlu, r:p", "b:p:g"));

		prevBtn = clientFrame.createNavigationButton(false, true);
		nextBtn = clientFrame.createNavigationButton(true, false);
		
		navPnl.add(prevBtn, CC.xy(1, 1));
		navPnl.add(nextBtn, CC.xy(3, 1));
		
		// add everything to main panel
		this.add(split, CC.xy(2, 2));
		this.add(navPnl, CC.xy(2, 4));
		
		// register listeners
		addBtn.addActionListener(new ActionListener() {
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
					ClientFrame.getInstance().getSettingsPanel().getDatabaseSearchSettingsPanel().getMascotChk().setSelected(false);
					ClientFrame.getInstance().getSettingsPanel().getDatabaseSearchSettingsPanel().getMascotChk().setEnabled(false);
					new AddFileWorker(fc.getSelectedFiles()).execute();
				}
				
			}
		});
		
		addDbBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				int res = JOptionPane.showConfirmDialog(clientFrame, fetchPnl, "Fetch from database", 
						JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE);
				if (res == JOptionPane.OK_OPTION) {
					try {
						Client client = Client.getInstance();
						client .initDBConnection();
						List<MascotGenericFile> dlSpec = client.downloadSpectra(
								(Long) expIdSpn.getValue(), annotChk.isSelected(), libChk.isSelected(), saveChk.isSelected());
					//	clientFrame.getClient().closeDBConnection();
						File file = new File("experiment_" + expIdSpn.getValue() + ".mgf");
						FileOutputStream fos = new FileOutputStream(file);
						for (MascotGenericFile mgf : dlSpec) {
							mgf.writeToStream(fos);
						}
						fos.close();
						// Add downloaded file contents to tree
						new AddFileWorker(new File[] { file }).execute();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		clearBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				TableConfig.clearTable(treeTbl);
				treeTbl.getRowSorter().allRowsChanged();
				treeTbl.getCheckBoxTreeSelectionModel().clearSelection();
				
				// reset text field
				filesTtf.clear();
				
				// reset plots
				SpectrumPanel spec = createDefaultSpectrumPanel();
				specPnl.removeAll();
				specPnl.add(spec, CC.xy(2, 2));
				specPnl.validate();

				chartBar.setValues(1, 0, 1, 1);
				Container cont = chartPnl.getParent();
				chartPnl = createDefaultHistogramPanel();
				cont.removeAll();
				cont.add(chartPnl, CC.xy(2, 2));
				cont.validate();
				
				// clear caches
//				totalSpectraList.clear();
				ticList.clear();
				specPosMap.clear();
				
				// reset navigation button and search settings tab
				nextBtn.setEnabled(false);
				ClientFrame.getInstance().getTabPane().setEnabledAt(ClientFrame.SETTINGS_PANEL, false);
			}
		});
	}

	/**
	 * Method to refresh the spectrum viewer panel.
	 */
	protected void refreshSpectrumPanel() {
		try {
			TreePath path = treeTbl.getPathForRow(treeTbl.getSelectedRow());
			if (path != null) {
				CheckBoxTreeTableNode node = (CheckBoxTreeTableNode) path.getLastPathComponent();
				SpectrumPanel viewer = null;
				if (node.isLeaf() && (node.getParent() != null)) {
						MascotGenericFile mgf = getSpectrumForNode(node);
						viewer = new SpectrumPanel(mgf, false);
						viewer.setShowResolution(false);
				} else {
					viewer = createDefaultSpectrumPanel();
				}
				specPnl.removeAll();
				specPnl.add(viewer, CC.xy(2, 2));
				specPnl.validate();
			}
		} catch (Exception e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),
					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
		}
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
//			reader = new MascotGenericFileReader(file, LoadMode.SURVEY);
			reader = InputFileReader.createInputFileReader(file);
			reader.survey();
		}
		int spectrumIndex = (Integer) spectrumNode.getValueAt(0) - 1;
//		ArrayList<Long> positions = specPosMap.get(file.getAbsolutePath());
//		long pos1 = positions.get(spectrumIndex);
////		long pos2 = (spectrumIndex == (positions.size() - 1)) ? file.length() : positions.get(spectrumIndex + 1);
//		long pos2 = positions.get(spectrumIndex + 1);
		
		MascotGenericFile spectrum = reader.loadSpectrum(spectrumIndex);
		Long spectrumID = spectrum.getSpectrumID();
		if (spectrumID != null) {
			// this is just a dummy spectrum, fetch from database
			spectrum = new SpectrumExtractor(Client.getInstance().getDatabaseConnection()).getSpectrumBySpectrumID(spectrumID);
		}
		return spectrum;
	}
	
	/**
	 * Method to return the file panel's tree component.
	 * @return The panel's SpectrumTree.
	 */
	public CheckBoxTreeTable getCheckBoxTree() {
		return treeTbl;
	}
	
	/**
	 * Holds the previous enable states of the client frame's tabs.
	 */
	private boolean[] tabEnabled;
	
	@Override
	public void setBusy(boolean busy) {
		this.busy = busy;
		ClientFrame clientFrame = ClientFrame.getInstance();
		Cursor cursor = (busy) ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : null;
		clientFrame.setCursor(cursor);
		if (split.getCursor().getType() == Cursor.WAIT_CURSOR) split.setCursor(null);
		
		JTabbedPane pane = clientFrame.getTabPane();
		if (tabEnabled == null) {
			tabEnabled = new boolean[pane.getComponentCount()];
			tabEnabled[pane.indexOfComponent(this)] = true;
		}
		// Enable/disable tabs
		for (int i = 0; i < tabEnabled.length - 1; i++) {
			boolean temp = pane.isEnabledAt(i);
			pane.setEnabledAt(i, tabEnabled[i]);
			tabEnabled[i] = temp;
		}
		// Enable/disable menu bar
		for (int i = 0; i < clientFrame.getJMenuBar().getMenuCount(); i++) {
			clientFrame.getJMenuBar().getMenu(i).setEnabled(!busy);
		}
		
		// Enable/disable buttons
		addBtn.setEnabled(!busy);
		addDbBtn.setEnabled(!busy);
		if (busy) {
			clearBtn.setEnabled(false);
		} else {
			CheckBoxTreeTableNode treeRoot = (CheckBoxTreeTableNode) ((DefaultTreeTableModel) treeTbl.getTreeTableModel()).getRoot();
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
				super.paint(g);

//				g.setColor(new Color(255, 255, 255, 160));
				g.setColor(new Color(255, 255, 255, 191));
				Insets insets = getBorder().getBorderInsets(this);
				g.fillRect(insets.left, insets.top,
						getWidth() - insets.right - insets.left,
						getHeight() - insets.top - insets.bottom);
				
				Graphics2D g2d = (Graphics2D) g;
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

//	/**
//	 * Gets the selected path of the .dat file
//	 * @return The selected path of the .mgf or .dat file
//	 */
//	public String getMascotSelPath() {
//		return mascotSelPath;
//	}

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
		if (treeTbl != null) {
			TreeTableNode root = (TreeTableNode) treeTbl.getTreeTableModel().getRoot();
			return (root.getChildCount() > 0);
		}
		return false;
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
				
				DefaultTreeTableModel treeModel = (DefaultTreeTableModel) treeTbl.getTreeTableModel();
				CheckBoxTreeTableNode treeRoot = (CheckBoxTreeTableNode) treeModel.getRoot();
				CheckBoxTreeSelectionModel cbtsm = treeTbl.getCheckBoxTreeSelectionModel();
	
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
						// DO NOTHING.
					} else if (file.getName().toLowerCase().endsWith(".dat")) {
						
						JCheckBox mascotChk = ClientFrame.getInstance().getSettingsPanel().getDatabaseSearchSettingsPanel().getMascotChk();
						mascotChk.setEnabled(true);
						mascotChk.setSelected(true);
						MascotDatfile mascotDatfile = new MascotDatfile(new BufferedReader(new FileReader(file)));
						Parameters parameters = mascotDatfile.getParametersSection();
						ParameterMap mascotParams =
								ClientFrame.getInstance().getSettingsPanel().getDatabaseSearchSettingsPanel().getMascotParameterMap();
						
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
	//							long startPos = positions.get(j);
	//							long endPos = (j == (positions.size() - 1)) ? file.length() : positions.get(j + 1);
							
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
							for (double intensity : mgf.getPeaks().values()) {
								if (intensity > filterSet.getNoiseLvl()) {
									numPeaks++;
								}
							}
							double TIC = mgf.getTotalIntensity();
							double SNR = mgf.getSNR(filterSet.getNoiseLvl());
	
							// append new spectrum node to file node
							SortableCheckBoxTreeTableNode spectrumNode = new SortableCheckBoxTreeTableNode(
									index, mgf.getTitle(), numPeaks, TIC, SNR) {
								public String toString() {
									return "Spectrum " + super.toString();
	//									return getParent().toString() + " " + super.toString();
								}
							};
	//							treeModel.insertNodeInto(spectrumNode, fileNode, fileNode.getChildCount());
							fileNode.add(spectrumNode);
	
							if ((numPeaks > filterSet.getMinPeaks()) &&
									(TIC > filterSet.getMinTIC()) &&
									(SNR > filterSet.getMinSNR())) {
	//								toBeAdded.add(spectrumNode.getPath());
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
	//							treeTbl.getRowSorter().allRowsChanged();
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
							chartBar.setValues(size, size / 10, 1, size);
						}
						
						// enable navigation buttons and search settings tab
						nextBtn.setEnabled(true);
//						ClientFrame.getInstance().getTabbedPane().setEnabledAt(ClientFrame.SETTINGS_PANEL, true);
						tabEnabled[ClientFrame.SETTINGS_PANEL] = true;
					} else {
						Client.getInstance().firePropertyChange("new message", null, "READING SPECTRUM FILE(S) ABORTED");
					}
					
					// expand tree table root
					treeTbl.expandRow(0);
					
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