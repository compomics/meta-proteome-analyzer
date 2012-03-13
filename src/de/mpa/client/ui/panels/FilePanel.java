package de.mpa.client.ui.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.FilterSettings;
import de.mpa.client.ui.CheckBoxTreeManager;
import de.mpa.client.ui.CheckBoxTreeSelectionModel;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.SpectrumTree;
import de.mpa.client.ui.SpectrumTree.TreeType;
import de.mpa.client.ui.TableConfig;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;
import de.mpa.ui.MgfFilter;
import de.mpa.ui.PlotPanel2;
import de.mpa.utils.ExtensionFileFilter;

public class FilePanel extends JPanel {

	private final static String PATH = "test/de/mpa/resources/";
	
	private ClientFrame clientFrame;
	private JButton clrBtn;
	private JButton noiseEstBtn;
	private JButton addBtn;
	public JTextField filesTtf;
	private JProgressBar filesPrg;
	private FilterSettings filterSet = new FilterSettings(5, 100.0, 1.0, 2.5);
	public List<File> files = new ArrayList<File>();
	private JTable fileDetailsTbl;
	private PlotPanel2 filePlotPnl;

	// TODO: implement spectrum selection count
//	public JLabel filesLbl = new JLabel("0 of 0 spectra selected") {
//		@Override
//		public void repaint() {
//			if ((filePnl != null) && (filePnl.getCheckBoxTree() != null)) {
//				int selCount = 0;
//				int leafCount = 0;
//				DefaultMutableTreeNode fileRoot = (DefaultMutableTreeNode) filePnl.getCheckBoxTree().getModel().getRoot();
//				if (fileRoot.getChildCount() > 0) {
//					selCount = filePnl.getCheckBoxTree().getSelectionModel().getSelectionCount();
//					leafCount = ((DefaultMutableTreeNode) filePnl.getCheckBoxTree().getModel().getRoot()).getLeafCount();
//				}
//				this.setText(selCount + " of " + leafCount + " spectra selected");
//			}
//		}		
//	};

	private CheckBoxTreeManager fileTree;
	private final static String DEFAULT_PROJECT = "Placeholder Project";
	
	public FilePanel(ClientFrame clientFrame) {
		this.clientFrame = clientFrame;
		this.initComponents();
	}

	private void initComponents() {

		CellConstraints cc = new CellConstraints();
		
		this.setLayout(new FormLayout("5dlu, p:g, 5dlu",		// col
		"5dlu, f:p:g, 5dlu"));	// row

		JPanel selPnl = new JPanel();
		selPnl.setBorder(new TitledBorder("File selection"));
		selPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p:g, 5dlu, p:g, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu",	// col
		"p, 5dlu, f:p:g, 5dlu"));										// row

		String pathName = "test/de/mpa/resources/";

		final JFileChooser fc = new JFileChooser(pathName);
		fc.setFileFilter(new MgfFilter());
		fc.setAcceptAllFileFilterUsed(false);

		filesTtf = new JTextField(20);
		filesTtf.setEditable(false);
		filesTtf.setMaximumSize(new Dimension(filesTtf.getMaximumSize().width, filesTtf.getPreferredSize().height));
		filesTtf.setText(files.size() + " file(s) selected");

		filesPrg = new JProgressBar(0, 100);
		filesPrg.setStringPainted(true);

		// panel containing filter settings
		final JPanel filterPnl = new JPanel();
		filterPnl.setLayout(new FormLayout("p, 5dlu, f:p:g, 5dlu, f:p:g, 5dlu, p:g", "p, 5dlu, p, 5dlu, p, 5dlu, p"));
		// add labels
		filterPnl.add(new JLabel("min. significant peaks"), cc.xyw(1, 1, 5));
		filterPnl.add(new JLabel("min. total ion current"), cc.xyw(1, 3, 3));
		filterPnl.add(new JLabel("min. signal-to-noise ratio"), cc.xyw(1, 5, 5));
		filterPnl.add(new JLabel("noise level"), cc.xy(1, 7));
		// create other components
		final JSpinner minPeaksSpn = new JSpinner(new SpinnerNumberModel(filterSet.getMinPeaks(), 0, null, 1));
		final JSpinner minTICspn = new JSpinner(new SpinnerNumberModel(filterSet.getMinTIC(), 0.0, null, 1.0));
		minTICspn.setEditor(new JSpinner.NumberEditor(minTICspn, "0.0"));
		final JSpinner minSNRspn = new JSpinner(new SpinnerNumberModel(filterSet.getMinSNR(), 0.0, null, 0.1));
		minSNRspn.setEditor(new JSpinner.NumberEditor(minSNRspn, "0.0"));
		final JSpinner noiseLvlSpn = new JSpinner(new SpinnerNumberModel(filterSet.getNoiseLvl(), 0.0, null, 0.1));
		noiseLvlSpn.setEditor(new JSpinner.NumberEditor(noiseLvlSpn, "0.00"));
		noiseEstBtn = new JButton("Estimate... *");
		noiseEstBtn.setToolTipText("Estimation not yet implemented, will export intensities as .txt file instead");
		noiseEstBtn.setEnabled(false);

		// add to panel
		filterPnl.add(minPeaksSpn, cc.xy(7, 1));
		filterPnl.add(minTICspn, cc.xyw(5, 3, 3));
		filterPnl.add(minSNRspn, cc.xy(7, 5));
		filterPnl.add(noiseLvlSpn, cc.xy(3, 7));
		filterPnl.add(noiseEstBtn, cc.xyw(5, 7, 3));

		JButton filterBtn = new JButton("Filter settings...");
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

		// button for appending local mgf files
		addBtn = new JButton("Add from File...");

		// XXX
		// button for downloading and appending mgf files from remote DB 
		JButton addDbBtn = new JButton("Add from DB...");
		// panel containing conditions for remote mgf fetching
		final JPanel fetchPnl = new JPanel();
		fetchPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p:g, 5dlu",	// col
		"5dlu, p, 5dlu"));		// row
		final JSpinner expIdSpn = new JSpinner(new SpinnerNumberModel(1L, 1L, null, 1L));
		fetchPnl.add(new JLabel("Experiment ID"), cc.xy(2, 2));
		fetchPnl.add(expIdSpn, cc.xy(4, 2));
		// /XXX

		clrBtn = new JButton("Clear all");
		clrBtn.setEnabled(false);

		filePlotPnl = new PlotPanel2(null);
		filePlotPnl.setMinimumSize(new Dimension(200, 150));
		filePlotPnl.clearSpectrumFile();

		// Tree of loaded spectrum files
		final DefaultMutableTreeNode treeRoot = new DefaultMutableTreeNode(DEFAULT_PROJECT);
		final DefaultTreeModel treeModel = new DefaultTreeModel(treeRoot);
		final SpectrumTree tree = new SpectrumTree(treeModel, TreeType.FILE_SELECT, clientFrame);
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
					}
				}
				return;
			}
		});

		// XXX
		addDbBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int res = JOptionPane.showConfirmDialog(clientFrame, fetchPnl, "Fetch from database", 
						JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE);
				if (res == JOptionPane.OK_OPTION) {
					try {
						clientFrame.getClient().initDBConnection();
						List<MascotGenericFile> dlSpec = clientFrame.getClient().downloadSpectra((Long)expIdSpn.getValue());
						clientFrame.getClient().closeDBConnection();
						FileOutputStream fos = new FileOutputStream(new File("experiment_" + expIdSpn.getValue() + ".mgf"));
						for (MascotGenericFile mgf : dlSpec) {
							mgf.writeToStream(fos);
						}
						fos.close();
					} catch (Exception x) {
						x.printStackTrace();
					}
				}
			}
		});
		// /XXX

		JScrollPane treePane = new JScrollPane(tree);
		treePane.setPreferredSize(new Dimension(0, 0));

		// left part of outer split pane
		JPanel leftPnl = new JPanel();
		leftPnl.setLayout(new FormLayout("p:g",					// col
		"p, f:p:g"));	// row
		JTable leftDummyTable = new JTable(null, new Vector<String>(Arrays.asList(new String[] {"Files"})));
		leftDummyTable.getTableHeader().setReorderingAllowed(false);
		leftDummyTable.getTableHeader().setResizingAllowed(false);
		leftDummyTable.getTableHeader().setPreferredSize(new JCheckBox().getPreferredSize());
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
		rightDummyTable.getTableHeader().setPreferredSize(new JCheckBox().getPreferredSize());
		JScrollPane rightDummyScpn = new JScrollPane(rightDummyTable);
		rightDummyScpn.setPreferredSize(rightDummyTable.getTableHeader().getPreferredSize());
		//		JLabel rightLbl = new JLabel("Details");

		String[] columnNames = {null, null};
		Object[][] data = {
				{"File path",				null},
				{"Spectrum title",			null},
				{"Significant peaks",		null},
				{"Total ion current",		null},
				{"Signal-to-noise ratio",	null},
		};
		fileDetailsTbl = new JTable(new DefaultTableModel(data, columnNames) {
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		});
		fileDetailsTbl.setAutoscrolls(false);
		fileDetailsTbl.getTableHeader().setVisible(false);
		fileDetailsTbl.getTableHeader().setPreferredSize(new Dimension(0, 0));
		Component comp = fileDetailsTbl.getCellRenderer(4, 0).getTableCellRendererComponent(fileDetailsTbl, fileDetailsTbl.getValueAt(4, 0), false, false, 4, 0);
		fileDetailsTbl.getColumnModel().getColumn(0).setMaxWidth(comp.getPreferredSize().width+10);
		fileDetailsTbl.getColumnModel().getColumn(0).setPreferredWidth(comp.getPreferredSize().width+10);

		JScrollPane detailScpn = new JScrollPane(fileDetailsTbl);
		detailScpn.setPreferredSize(new Dimension(300, 0));
		detailScpn.setMinimumSize(new Dimension(0, fileDetailsTbl.getRowHeight()));
		detailScpn.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		detailScpn.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		topRightPnl.add(rightDummyScpn, cc.xy(1,1));
		topRightPnl.add(detailScpn, cc.xy(1,2));
		topRightPnl.setMinimumSize(new Dimension(0, rightDummyTable.getTableHeader().getPreferredSize().height + 
				fileDetailsTbl.getRowHeight() + 2));

		final JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topRightPnl, filePlotPnl);
		rightSplit.setBorder(null);
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
		mainSplit.setBorder(null);
		mainSplit.setDividerLocation(200);
		mainSplit.setContinuousLayout(true);
		BasicSplitPaneDivider mainDivider = ((BasicSplitPaneUI) mainSplit.getUI()).getDivider();
		if (mainDivider != null) { mainDivider.setBorder(null); }

		// Button action listeners
		addBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new AddFileWorker().execute();
			}
		} );

		clrBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				files.clear();
				clientFrame.specPosMap.clear();
				clientFrame.getQueryTree().clearSelection();
				((DefaultMutableTreeNode) clientFrame.getQueryTree().getModel().getRoot()).removeAllChildren();
				((DefaultTreeModel) clientFrame.getQueryTree().getModel()).reload();
				((DefaultTableModel) clientFrame.libTbl.getModel()).setRowCount(0);
				((DefaultTableModel) clientFrame.protTbl.getModel()).setRowCount(0);
				clientFrame.mPlot.setFirstSpectrum(null);
				filesTtf.setText("0 file(s) selected");
				//				filesLbl.setText("0 of 0 spectra selected");
//				clientFrame.getSpecLibSearchPanel().setProgress(0);
				clrBtn.setEnabled(false);
				noiseEstBtn.setEnabled(false);
				treeRoot.removeAllChildren();
				treeModel.reload();
				selectionModel.clearSelection();
				selectionModel.addSelectionPath(new TreePath(treeRoot));
			}
		});

		noiseEstBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO: generate intensity histogram, noise level is suspected to be in low-intensity area with high abundance
				try {
					// for now: write intensities to file, examine using MATLAB
					FileOutputStream fos = new FileOutputStream(new File("intensities.txt"));
					OutputStreamWriter osw = new OutputStreamWriter(fos);
					for (int i = 0; i < treeRoot.getChildCount(); i++) {
						DefaultMutableTreeNode fileNode = (DefaultMutableTreeNode) treeRoot.getChildAt(i);
						for (int j = 0; j < fileNode.getChildCount(); j++) {
							DefaultMutableTreeNode spectrumNode = (DefaultMutableTreeNode) fileNode.getChildAt(j);
							MascotGenericFile spectrum = tree.getSpectrumAt(spectrumNode);
							for (double intensity : spectrum.getPeaks().values()) {
								osw.write(intensity + "\n");
							}
						}
					}
					osw.close();
					fos.close();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		// Input 
		selPnl.add(new JLabel("Spectrum Files (MGF):"), cc.xy(2,1));
		selPnl.add(filesTtf, cc.xy(4,1));
		selPnl.add(filesPrg, cc.xy(6,1));
		selPnl.add(filterBtn, cc.xy(8,1));
		selPnl.add(addBtn, cc.xy(10,1));
		selPnl.add(addDbBtn, cc.xy(12, 1));
		selPnl.add(clrBtn, cc.xy(14,1));

		selPnl.add(mainSplit, cc.xyw(2,3,13));

		this.add(selPnl, cc.xy(2,2));

	}
	
	public void refreshFileTable(MascotGenericFile mgf, DefaultMutableTreeNode selNode) {
		if (mgf != null) {
			DefaultMutableTreeNode parent = (DefaultMutableTreeNode) selNode.getParent();
			File parentFile = (File) parent.getUserObject();
			fileDetailsTbl.setValueAt(parentFile.getAbsolutePath(), 0, 1);
			fileDetailsTbl.setValueAt(mgf.getTitle(), 1, 1);

			int numPeaks = 0;
			HashMap<Double, Double> peakMap = mgf.getPeaks();
			for (double intensity : peakMap.values()) {
				if (intensity > filterSet.getNoiseLvl()) {
					numPeaks++;
				}
			}
			fileDetailsTbl.setValueAt("<html><font color=#" + ((numPeaks < filterSet.getMinPeaks()) ? "FF" : "00") +
					"0000>" + numPeaks + " of " + peakMap.size() + "</font></html>", 2, 1);

			double TIC = mgf.getTotalIntensity();
			fileDetailsTbl.setValueAt("<html><font color=#" + ((TIC < filterSet.getMinTIC()) ? "FF" : "00") +
					"0000>" + TIC + "</font></html>", 3, 1);

			double SNR = mgf.getSNR(filterSet.getNoiseLvl());
			fileDetailsTbl.setValueAt("<html><font color=#" + ((SNR < filterSet.getMinSNR()) ? "FF" : "00") +
					"0000>" + SNR + "</font></html>", 4, 1);

			filePlotPnl.setSpectrumFile(mgf,Color.RED);
			filePlotPnl.repaint();
			clientFrame.getSpecLibSearchPanel().refreshPlot(mgf);
			fileDetailsTbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			TableConfig.packColumn(fileDetailsTbl, 1, 5);
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
		TableConfig.packColumn(fileDetailsTbl, 0, 10);

	}

	private class AddFileWorker extends SwingWorker {

		private long maxProgress;

		@Override
		protected Object doInBackground() throws Exception {

			// appear busy
			setProgress(0);
			addBtn.setEnabled(false);
			clrBtn.setEnabled(false);
			noiseEstBtn.setEnabled(false);

			// First check whether a file has already been selected.
			// If so, start from that file's parent.
			File startLocation = new File(PATH);
			DefaultMutableTreeNode treeRoot = (DefaultMutableTreeNode) fileTree.getModel().getRoot();
			DefaultTreeModel treeModel = (DefaultTreeModel) fileTree.getModel();
			CheckBoxTreeSelectionModel selectionModel = fileTree.getSelectionModel();
			SpectrumTree tree = (SpectrumTree) fileTree.getTree();
			if (treeRoot.getChildCount() > 0) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)treeRoot.getChildAt(0);
				File temp = (File)node.getUserObject();
				startLocation = temp.getParentFile();
			}
			JFileChooser fc = new JFileChooser(startLocation);
			fc.setFileFilter(new ExtensionFileFilter("mgf", false));
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setMultiSelectionEnabled(true);
			int result = fc.showOpenDialog(clientFrame);
			if (result == JFileChooser.APPROVE_OPTION) {
				// appear busy
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

				File[] selFiles = fc.getSelectedFiles();
				int newLeaves = 0;
				
				filesPrg.setValue(0);
//				maxProgress = 0L;
//				for (File file : selFiles) {
//					maxProgress += file.length();
//				}

				for (File file : selFiles) {
					maxProgress = file.length();
					// Add the files for the DB search option
					files.add(file);
					ArrayList<Long> spectrumPositions = new ArrayList<Long>();
					try {
						MascotGenericFileReader reader = new MascotGenericFileReader(file, MascotGenericFileReader.NONE);
						reader.addPropertyChangeListener(new PropertyChangeListener() {
							@Override
							public void propertyChange(PropertyChangeEvent evt) {
								if (evt.getPropertyName() == "progress") {
									double progress = (Long) evt.getNewValue();
									filesPrg.setValue((int) (progress/maxProgress*100.0));
								} 
							}
						});
						reader.load();
						ArrayList<MascotGenericFile> mgfList = (ArrayList<MascotGenericFile>) reader.getSpectrumFiles(false);
						spectrumPositions.addAll(reader.getSpectrumPositions());
						clientFrame.specPosMap.put(file.getAbsolutePath(), spectrumPositions);

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
							int numPeaks = 0;
							for (double intensity : mgf.getPeaks().values()) {
								if (intensity > filterSet.getNoiseLvl()) {
									numPeaks++;
								}
							}
							double TIC = mgf.getTotalIntensity();
							double SNR = mgf.getSNR(filterSet.getNoiseLvl());
							TreePath treePath = new TreePath(spectrumNode.getPath());
							if ((numPeaks > filterSet.getMinPeaks()) &&
									(TIC > filterSet.getMinTIC()) &&
									(SNR > filterSet.getMinSNR())) {
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

				try {
					clientFrame.getSpecLibSearchPanel().refreshPlot(tree.getSpectrumAt(treeRoot.getFirstLeaf()));
				} catch (IOException x) {
					x.printStackTrace();
				}

//				clientFrame.getSpecLibSearchPanel().setProgress(0);

				String str;
				int numFiles;
				str = filesTtf.getText();
				str = str.substring(0, str.indexOf(" "));
				numFiles = Integer.parseInt(str) + selFiles.length;

				filesTtf.setText(numFiles + " file(s) selected");

				clientFrame.log.info("Added " + newLeaves + " file(s).");
				if (numFiles > 0) {
					if (clientFrame.connectedToServer) {
						clientFrame.sendBtn.setEnabled(true);
					}
					clrBtn.setEnabled(true);
					noiseEstBtn.setEnabled(true);
				}

				// Get selected index for spectra combobox
				int comboIndex = clientFrame.spectraCbx.getSelectedIndex();
				clientFrame.spectraCbx.removeAllItems();
				clientFrame.spectraCbx2.removeAllItems();

				for (int i = 0; i < files.size(); i++) {
					clientFrame.spectraCbx.addItem(files.get(i).getName());
					clientFrame.spectraCbx2.addItem(files.get(i).getName());
				}

				if(comboIndex >= 0)	{
					clientFrame.spectraCbx.setSelectedIndex(comboIndex);
					clientFrame.spectraCbx2.setSelectedIndex(comboIndex);
				}
			}
			return 0;
		}

		@Override
		public void done() {
			addBtn.setEnabled(true);
			setCursor(null);	//turn off the wait cursor
		}

	}

	/**
	 * Method to return the file panel's tree component.
	 * @return The panel's SpectrumTree.
	 */
	public CheckBoxTreeManager getCheckBoxTree() {
		return fileTree;
	}
}
