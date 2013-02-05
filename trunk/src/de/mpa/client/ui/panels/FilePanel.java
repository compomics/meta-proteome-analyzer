package de.mpa.client.ui.panels;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.TreePath;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.MultiSplitLayout;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;
import org.jfree.chart.ChartPanel;

import com.compomics.util.gui.spectrum.SpectrumPanel;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.model.ProjectContent;
import de.mpa.client.settings.FilterSettings;
import de.mpa.client.ui.CheckBoxTreeSelectionModel;
import de.mpa.client.ui.CheckBoxTreeTable;
import de.mpa.client.ui.CheckBoxTreeTableNode;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.PanelConfig;
import de.mpa.client.ui.SortableCheckBoxTreeTable;
import de.mpa.client.ui.SortableCheckBoxTreeTableNode;
import de.mpa.client.ui.SortableTreeTableModel;
import de.mpa.client.ui.TableConfig;
import de.mpa.client.ui.chart.HistogramChart;
import de.mpa.client.ui.chart.HistogramChart.HistChartType;
import de.mpa.client.ui.chart.HistogramData;
import de.mpa.db.extractor.SpectrumExtractor;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;
import de.mpa.io.MascotGenericFileReader.LoadMode;

public class FilePanel extends JPanel {
	
	private ClientFrame clientFrame;
	private Client client;	
	private FilterSettings filterSet = new FilterSettings(5, 100.0, 1.0, 2.5);
	private CheckBoxTreeTable treeTbl;
	private JPanel specPnl;	
	private JTextField filesTtf;
	private JButton filterBtn;
	private JButton addBtn;
	private JButton addDbBtn;
	private JButton clrBtn;	
	protected MascotGenericFileReader reader;
	protected static Map<String, ArrayList<Long>> specPosMap = new HashMap<String, ArrayList<Long>>();
	private final static String PATH = "test/de/mpa/resources/";	
	private boolean busy;
	private JXMultiSplitPane split;	
	private JPanel histPnl;
	private ArrayList<Double> ticList = new ArrayList<Double>();
	
	/**
	 * Constructs a spectrum file selection and preview panel.
	 */
	public FilePanel() {
		this.clientFrame = ClientFrame.getInstance();
		this.client = Client.getInstance();
		this.initComponents();
	}

	private void initComponents() {
		
		this.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu, p, 5dlu"));
		
		// top panel containing buttons and spectrum tree table
		JPanel topPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu", "5dlu, p, 5dlu, f:p:g, 5dlu"));
		
		// button panel
		JPanel buttonPnl = new JPanel(new FormLayout("p, 5dlu, p:g, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p", "p"));
		
		// Textfield displaying amount of selected files
		filesTtf = new JTextField(20);
		filesTtf.setEditable(false);
		filesTtf.setText("0 file(s) added");
		
		// panel containing filter settings
		final JPanel filterPnl = new JPanel(new FormLayout("p, 5dlu, f:p:g, 5dlu, f:p:g, 5dlu, p:g", "p, 5dlu, p, 5dlu, p, 5dlu, p"));
		
		// init filter panel components
		final JSpinner minPeaksSpn = new JSpinner(new SpinnerNumberModel(filterSet.getMinPeaks(), 0, null, 1));
		final JSpinner minTICspn = new JSpinner(new SpinnerNumberModel(filterSet.getMinTIC(), 0.0, null, 1.0));
		minTICspn.setEditor(new JSpinner.NumberEditor(minTICspn, "0.0"));
		final JSpinner minSNRspn = new JSpinner(new SpinnerNumberModel(filterSet.getMinSNR(), 0.0, null, 0.1));
		minSNRspn.setEditor(new JSpinner.NumberEditor(minSNRspn, "0.0"));
		final JSpinner noiseLvlSpn = new JSpinner(new SpinnerNumberModel(filterSet.getNoiseLvl(), 0.0, null, 0.1));
		noiseLvlSpn.setEditor(new JSpinner.NumberEditor(noiseLvlSpn, "0.00"));
		JButton noiseEstBtn = new JButton("Estimate... *");
		noiseEstBtn.setToolTipText("Estimation not yet implemented, will export intensities as .txt file instead");
		noiseEstBtn.setEnabled(false);

		// add filter panel components
		filterPnl.add(new JLabel("min. significant peaks"), CC.xyw(1, 1, 5));
		filterPnl.add(minPeaksSpn, CC.xy(7, 1));
		filterPnl.add(new JLabel("min. total ion current"), CC.xyw(1, 3, 3));
		filterPnl.add(minTICspn, CC.xyw(5, 3, 3));
		filterPnl.add(new JLabel("min. signal-to-noise ratio"), CC.xyw(1, 5, 5));
		filterPnl.add(minSNRspn, CC.xy(7, 5));
		filterPnl.add(new JLabel("noise level"), CC.xy(1, 7));
		filterPnl.add(noiseLvlSpn, CC.xy(3, 7));
		filterPnl.add(noiseEstBtn, CC.xyw(5, 7, 3));
		
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
		// panel containing conditions for remote mgf fetching
		final JPanel fetchPnl = new JPanel();
		fetchPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p:g, 5dlu", "5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));
		final JSpinner expIdSpn = new JSpinner(new SpinnerNumberModel(1L, 1L, null, 1L));
		fetchPnl.add(new JLabel("Database Experiment ID"), CC.xy(2, 2));
		fetchPnl.add(expIdSpn, CC.xy(4, 2));
		final JCheckBox annotChk = new JCheckBox("Fetch only annotated spectra", false);
		fetchPnl.add(annotChk, CC.xyw(2, 4, 3));
		final JCheckBox libChk = new JCheckBox("Fetch from spectral library", false);
		fetchPnl.add(libChk, CC.xyw(2, 6, 3));
		final JCheckBox saveChk = new JCheckBox("Save spectrum contents to file", false);
		fetchPnl.add(saveChk, CC.xyw(2, 8, 3));
		
		// button to reset file tree contents
		clrBtn = new JButton("Clear all");
		clrBtn.setEnabled(false);
		
		// add components to button panel
		buttonPnl.add(new JLabel("Spectrum Files:"), CC.xy(1, 1));
		buttonPnl.add(filesTtf, CC.xy(3, 1));
		buttonPnl.add(filterBtn, CC.xy(5, 1));
		buttonPnl.add(addBtn, CC.xy(7, 1));
		buttonPnl.add(addDbBtn, CC.xy(9, 1));
		buttonPnl.add(clrBtn, CC.xy(11, 1));
		
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
			{ setColumnIdentifiers(Arrays.asList(new String[] { "Spectra", "Path / Title", "Peaks", "TIC", "SNR"})); }
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
//		treeTbl.getRowSorter().allRowsChanged();
//		treeTbl.setAutoCreateRowSorter(true);
		
//		treeTbl.setTableHeader(new ComponentHeader(treeTbl.getColumnModel()));
		treeTbl.getTableHeader().setReorderingAllowed(true);
		
//		treeTbl.getTableHeader().addMouseListener(new MouseAdapter() {
//			public void mouseClicked(MouseEvent e) {
//				int index = treeTbl.getTableHeader().getColumnModel().getColumnIndexAtX(e.getX());
//				treeTbl.toggleSortOrder(index);
//			}
//		});
		
		final JPopupMenu filterPopup = new JPopupMenu();
		filterPopup.add(filterBtn);
		final JToggleButton testBtn = new JToggleButton();
		testBtn.setPreferredSize(new Dimension(15, 12));
		testBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (testBtn.isSelected()) {
					Rectangle rect = treeTbl.getTableHeader().getHeaderRect(treeTbl.getHierarchicalColumn());
					filterPopup.show(treeTbl.getTableHeader(), rect.x - 1,
							treeTbl.getTableHeader().getHeight() - 1);
					filterBtn.requestFocus();
				} else {
					filterPopup.setVisible(false);
					treeTbl.requestFocus();
				}
			}
		});
		filterBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				filterPopup.setVisible(false);
				treeTbl.requestFocus();
				testBtn.setSelected(false);
				treeTbl.getTableHeader().repaint();
			}
		});
		filterBtn.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent fe) {
				filterPopup.setVisible(false);
				if (!testBtn.getModel().isArmed()) {
					testBtn.setSelected(false);
					treeTbl.requestFocus();
					treeTbl.getTableHeader().repaint();
				}
			};
		});
//		treeTbl.getColumnModel().getColumn(0).setHeaderRenderer(new ComponentHeaderRenderer(testBtn));
		treeTbl.setPreferredScrollableViewportSize(new Dimension(320, 200));
		
		treeTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent evt) {
				refreshSpectrumPanel();
			}
		});
		
		// wrap tree table in scroll pane
		JScrollPane treeScpn = new JScrollPane(treeTbl);
		
		// add components to top panel
		topPnl.add(buttonPnl, CC.xy(2, 2));
		topPnl.add(treeScpn, CC.xy(2, 4));
		
		// wrap top panel in titled panel
		JXTitledPanel topTtlPnl = PanelConfig.createTitledPanel("File input", topPnl);
		
		// Build bottom panel
		
		// Spectrum panel containing spectrum viewer
		specPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));
		
		// Spectrum viewer
		SpectrumPanel viewer = createDefaultSpectrumPanel();
		specPnl.add(viewer, CC.xy(2, 2));
		
		// wrap spectrum panel in titled panel
		JXTitledPanel spectrumTtlPnl = PanelConfig.createTitledPanel("Spectrum Viewer", specPnl);
		spectrumTtlPnl.setMinimumSize(new Dimension(450, 350));
		
		// Panel containing histogram plot
		histPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));
		histPnl.setMinimumSize(new Dimension(300, 250));
		histPnl.setPreferredSize(new Dimension(300, 250));
		
		ChartPanel chartPanel = createDefaultHistogramPanel();
		histPnl.add(chartPanel, CC.xy(2, 2));
		
		// wrap spectrum panel in titled panel
		JXTitledPanel histTtlPnl = PanelConfig.createTitledPanel("Histogram", histPnl);
		histTtlPnl.setMinimumSize(new Dimension(450, 350));
		
		
		// add titled panels to split pane
		String layoutDef = "(COLUMN top (ROW (LEAF weight=0.65 name=spectrum) (LEAF weight=0.35 name=histogram))";
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
		split.add(histTtlPnl, "histogram");
		
		JPanel navPnl = new JPanel(new FormLayout("r:p:g, 5dlu, r:p", "b:p:g"));
		
		navPnl.add(clientFrame.createNavigationButton(false, true), CC.xy(1, 1));
		navPnl.add(clientFrame.createNavigationButton(true, true), CC.xy(3, 1));
		
		// add everything to main panel
		this.add(split, CC.xy(2, 2));
		this.add(navPnl, CC.xy(2, 4));
		
		// register listeners
		addBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File startLocation = new File(PATH);
				
				JFileChooser fc = new JFileChooser(startLocation);
				fc.setFileFilter(Constants.MGF_FILE_FILTER);
				fc.setAcceptAllFileFilterUsed(false);
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fc.setMultiSelectionEnabled(true);
				int result = fc.showOpenDialog(clientFrame);
				if (result == JFileChooser.APPROVE_OPTION) {
					new AddFileWorker(fc.getSelectedFiles()).execute();
				}
			}
		});
		
		addDbBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int res = JOptionPane.showConfirmDialog(clientFrame, fetchPnl, "Fetch from database", 
						JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE);
				if (res == JOptionPane.OK_OPTION) {
					try {
						client.initDBConnection();
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
					} catch (Exception x) {
						x.printStackTrace();
					}
				}
			}
		});
		
		clrBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				// reset table
				while (treeRoot.getChildCount() > 0) {
					treeModel.removeNodeFromParent((MutableTreeTableNode) treeModel.getChild(treeRoot, treeRoot.getChildCount()-1));
				}
				treeTbl.getRowSorter().allRowsChanged();
				treeTbl.getCheckBoxTreeSelectionModel().clearSelection();
				
				// reset text field
				filesTtf.setText("0 file(s) added");
				
				// reset plots
				SpectrumPanel spec = createDefaultSpectrumPanel();
				specPnl.removeAll();
				specPnl.add(spec, CC.xy(2, 2));
				specPnl.validate();
				ChartPanel hist = createDefaultHistogramPanel();
				histPnl.removeAll();
				histPnl.add(hist, CC.xy(2, 2));
				histPnl.validate();
				
				// clear caches
//				totalSpectraList.clear();
				ticList.clear();
				specPosMap.clear();
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
			reader = new MascotGenericFileReader(file);
		}
		int spectrumIndex = (Integer) spectrumNode.getValueAt(0) - 1;
		long spectrumPosition = specPosMap.get(file.getAbsolutePath()).get(spectrumIndex);
		
		MascotGenericFile spectrum = reader.loadNthSpectrum(spectrumIndex, spectrumPosition);
		Long spectrumID = spectrum.getSpectrumID();
		if (spectrumID != null) {
			// this is just a dummy spectrum, fetch from database
			spectrum = new SpectrumExtractor(client.getConnection()).getSpectrumBySpectrumID(spectrumID);
		}
		return spectrum;
	}
	
	/**
	 * Worker class to add local files to the spectrum tree view.
	 * 
	 * @author behne
	 */
	private class AddFileWorker extends SwingWorker {
		
		private File[] selFiles;
		
		public AddFileWorker(File[] files) {
			this.selFiles = files;
		}

		@Override
		protected Object doInBackground() throws Exception {
			// appear busy
			setBusy(true);
			
			DefaultTreeTableModel treeModel = (DefaultTreeTableModel) treeTbl.getTreeTableModel();
			CheckBoxTreeTableNode treeRoot = (CheckBoxTreeTableNode) treeModel.getRoot();
			CheckBoxTreeSelectionModel selectionModel = treeTbl.getCheckBoxTreeSelectionModel();

			long totalSize = 0L;
			for (File file : selFiles) {
				totalSize += file.length();
			}
			client.firePropertyChange("resetall", 0, totalSize);
			
			int i = 0;
			for (File file : selFiles) {
				client.firePropertyChange("new message", null, "READING SPECTRUM FILE " + ++i + "/" + selFiles.length);
				
				ArrayList<Long> spectrumPositions = new ArrayList<Long>();
				try {
					reader = new MascotGenericFileReader(file, LoadMode.NONE);

					client.firePropertyChange("resetcur", 0, file.length());
					
					reader.addPropertyChangeListener(new PropertyChangeListener() {
						@Override
						public void propertyChange(PropertyChangeEvent pce) {
							if (pce.getPropertyName() == "progress") {
								client.firePropertyChange("progress", pce.getOldValue(), pce.getNewValue());
							}
						}
					});
					reader.survey();
//					List<MascotGenericFile> mgfList = (ArrayList<MascotGenericFile>) reader.getSpectrumFiles(false);
//					totalSpectraList.addAll(mgfList);
					
					spectrumPositions.addAll(reader.getSpectrumPositions(false));
					specPosMap.put(file.getAbsolutePath(), spectrumPositions);

					client.firePropertyChange("indeterminate", false, true);
					client.firePropertyChange("new message", null, "BUILDING TREE NODE " + i + "/" + selFiles.length);
					
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

					int index = 1;
					List<TreePath> toBeAdded = new ArrayList<TreePath>();
					for (Long specPos : spectrumPositions) {
						MascotGenericFile mgf = reader.loadNthSpectrum(index, specPos);
						
						Long spectrumID = mgf.getSpectrumID();
						if (spectrumID != null) {
							// this is just a dummy spectrum, fetch from database
							mgf = new SpectrumExtractor(client.getConnection()).getSpectrumBySpectrumID(spectrumID);
						}
//						totalSpectraList.add(mgf);
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
//								return getParent().toString() + " " + super.toString();
							}
						};
//						treeModel.insertNodeInto(spectrumNode, fileNode, fileNode.getChildCount());
						fileNode.add(spectrumNode);

						if ((numPeaks > filterSet.getMinPeaks()) &&
								(TIC > filterSet.getMinTIC()) &&
								(SNR > filterSet.getMinSNR())) {
//							toBeAdded.add(spectrumNode.getPath());
							toBeAdded.add(new TreePath(new Object[] {treeRoot, fileNode, spectrumNode}));
						}
						index++;
					}
					
					// append new file node to root and initially deselect it
					if (fileNode.getChildCount() > 0) {
						treeModel.insertNodeInto(fileNode, treeRoot, treeRoot.getChildCount());
						selectionModel.removeSelectionPath(fileNode.getPath());
						// reselect spectrum nodes that meet filter criteria
						selectionModel.addSelectionPaths(toBeAdded);
//						treeTbl.getRowSorter().allRowsChanged();
					}
					
					client.firePropertyChange("indeterminate", true, false);
					
				} catch (Exception ex) {
					client.firePropertyChange("new message", null, "READING SPECTRUM FILE(S) ABORTED");
					JXErrorPane.showDialog(ClientFrame.getInstance(),
							new ErrorInfo("Severe Error", ex.getMessage(), null, null, ex, ErrorLevel.SEVERE, null));
				}
				
				treeTbl.expandRow(0);
			}

			return null;
		}
		
		@Override
		protected void done() {
			client.firePropertyChange("new message", null, "READING SPECTRUM FILE(S) FINISHED");
			
			// stop appearing busy
			setBusy(false);

			String str = filesTtf.getText();
			str = str.substring(0, str.indexOf(" "));
			filesTtf.setText((Integer.parseInt(str) + selFiles.length) + " file(s) added - " + ticList.size() + " spectra in total");
			
			// Show histogram
			if (!ticList.isEmpty()) {
				// TODO: Currently the histogram is shown for all files... would be better to show only for selected files (ticked spectra?).
				histPnl.removeAll();
				
				HistogramChart histogram = new HistogramChart(new HistogramData(ticList, 40), HistChartType.TOTAL_ION_HIST);
				
				// Create chart panel
				ChartPanel chartPanel = new ChartPanel(histogram.getChart());
				chartPanel.setPreferredSize(new Dimension(0, 0));
				chartPanel.setBorder(BorderFactory.createEtchedBorder());
				histPnl.add(chartPanel, CC.xy(2, 2));
			}
		}
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
	
	/**
	 * Makes the frame and this panel appear busy.
	 * @param busy <code>true</code> if busy, <code>false</code> otherwise.
	 */
	public void setBusy(boolean busy) {
		this.busy = busy;
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
			clrBtn.setEnabled(false);
		} else {
			CheckBoxTreeTableNode treeRoot = (CheckBoxTreeTableNode) ((DefaultTreeTableModel) treeTbl.getTreeTableModel()).getRoot();
			clrBtn.setEnabled(treeRoot.getChildCount() > 0);
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
				ticList.add(x); 
			}
		}
		
		// TODO: determine bin size via formula
		HistogramChart histogram = new HistogramChart(new HistogramData(ticList, binCount), HistChartType.TOTAL_ION_HIST);
		ChartPanel panel = new ChartPanel(histogram.getChart()) {
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
		panel.setPreferredSize(new Dimension(0, 0));
		panel.setBorder(BorderFactory.createEtchedBorder());
		
		return panel;
	}
	
}
