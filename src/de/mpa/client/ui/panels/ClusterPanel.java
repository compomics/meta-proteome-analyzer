package de.mpa.client.ui.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Constants;
import de.mpa.client.ui.ClientFrame;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;

@SuppressWarnings("serial")
public class ClusterPanel extends JPanel {				
	
	// fields or class variables
	private final ClientFrame clientFrame;
	protected HashMap<String, Integer> seqMap;
	private JSpinner binSizeSpn;
	private JCheckBox meanPeakInt;
	private JCheckBox nrmlzPeakInt;
	private PlotPanel2 mrgdSpcPltPnl;
	private PlotPanel2 addSpcPltPnl;
	protected ArrayList<MascotGenericFile> slctdMGFs = new ArrayList<MascotGenericFile>();
	private JTable specTbl;
	private SpinnerNumberModel sqncCntSpnMdl;
	protected MascotGenericFile mergedMGF;
	private ClusterPanel.ClusterTableModel tblMdl;

	public ClusterPanel() {
        clientFrame = ClientFrame.getInstance();
        this.initComponents();												// function call
	}

	// void = without return value
	private void initComponents() {										// declaration of initComponents, void = bo return value
		ClusterPanel clusterPnl = this;									// Init Panel "Clustering"

		CellConstraints cc = new CellConstraints();

		clusterPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu",			// col
											"5dlu, f:p:g, 5dlu"));		// row

			JPanel setPnl = new JPanel();								// init Panel "Cluster Panel"
			setPnl.setBorder(new TitledBorder("Cluster Panel"));
			FormLayout layout = new FormLayout("5dlu, p, 5dlu, f:p:g, 5dlu, f:p:g, 5dlu",
											   "5dlu, f:p, 5dlu, f:p:g, 5dlu, f:p:g, 5dlu");
			layout.setRowGroups(new int[][]{ {4, 6} });					// forcing size of row 4 and 6 as equal
			layout.setColumnGroups(new int[][]{ {4, 6} });				// forcing size of column 4 and 6 as equal
			setPnl.setLayout(layout);									// editing layout of setPnl

				// table of spectra
        this.tblMdl = new ClusterPanel.ClusterTableModel();						// final for immutable reference
//				tblMdl.addRow(new Object[] {1,"ABCD",true});				// 'force-feed' first line (with String and CheckBox)
        this.specTbl = new JTable(this.tblMdl);							    // Init 'specTbl' adding tblMdl as model
        this.specTbl.setAutoCreateRowSorter(true);						// enables alphabetic sorting, done by click on column title
//				specTbl.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // disable multiple selections
				JScrollPane specTblScp = new JScrollPane(this.specTbl);			// Init Scrollbar for 'specTbl'
			
				// panel of table options
				JPanel intrActPnl = new JPanel();
				intrActPnl.setBorder(new TitledBorder("Selections"));
				intrActPnl.setLayout(new FormLayout("5dlu, p, 5dlu, r:p:g, 5dlu",
													"5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));

        this.sqncCntSpnMdl = new SpinnerNumberModel(1, 					// default
														   1, 					// min	
													   	   null, 				// max unbounded
													       1);					// step  
					JSpinner sqncCntSpn = new JSpinner();							// init spinner with prior defined model
					sqncCntSpn.setModel(this.sqncCntSpnMdl);								// assign model to spinner
					sqncCntSpn.addChangeListener(new ChangeListener() {				// add changelistener to spinner
					
						
						
						@Override
						public void stateChanged(ChangeEvent evt) {
							
							Integer sqncCnt = (Integer) ClusterPanel.this.sqncCntSpnMdl.getValue();			// get spinner value
							for (int row = 0; row < ClusterPanel.this.specTbl.getRowCount(); row++) {			// init loop over size of specTbl
								String sequence = (String) ClusterPanel.this.tblMdl.getValueAt(row, 1);	    // get sequence for current entry
	
								if (ClusterPanel.this.seqMap.get(sequence) >= sqncCnt) {
                                    ClusterPanel.this.tblMdl.setValueAt(true, row, 2);
								} else {
                                    ClusterPanel.this.tblMdl.setValueAt(false, row, 2);
								}
							}
						}
					});
				    JButton getValueBtn = new JButton("Import!"); 							// import spectra file
					getValueBtn.addActionListener(new ActionListener() {		
						@Override
						public void actionPerformed(ActionEvent evt) {
							JFileChooser fc = new JFileChooser();
							fc.setFileFilter(Constants.MGF_FILE_FILTER);
							fc.setAcceptAllFileFilterUsed(false);
							fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
							fc.setMultiSelectionEnabled(true); 							
							int result = fc.showOpenDialog(ClusterPanel.this.clientFrame);
							if (result == (JFileChooser.APPROVE_OPTION)) {
                                ClusterPanel.this.tblMdl.setRowCount(0);
                                ClusterPanel.this.seqMap = new HashMap<String, Integer>();
								File[] slctdFls = fc.getSelectedFiles();
								for (File file : slctdFls) {
									int i = 1;
									try {
										MascotGenericFileReader reader = new MascotGenericFileReader(file);
										List<MascotGenericFile> mgfFiles = reader.getSpectrumFiles();
										for (MascotGenericFile mgf : mgfFiles) {
                                            ClusterPanel.this.tblMdl.addRow(new Object[] {"Spectrum " + i++, mgf, false});
											String title = mgf.getTitle();
											String sequence = title.substring(0, title.indexOf(' '));
											if (ClusterPanel.this.seqMap.containsKey(sequence)) {
                                                ClusterPanel.this.seqMap.put(sequence, ClusterPanel.this.seqMap.get(sequence)+1);
											} else {
                                                ClusterPanel.this.seqMap.put(sequence, 1);
											}
										}
										
										
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
								for (int i = 0; i < ClusterPanel.this.tblMdl.getRowCount(); i++) {
									String sequence = (String) ClusterPanel.this.tblMdl.getValueAt(i, 1);
									if (ClusterPanel.this.seqMap.get(sequence) >= (Integer) ClusterPanel.this.sqncCntSpnMdl.getValue()) {
                                        ClusterPanel.this.tblMdl.setValueAt(true, i, 2);
									}
								}
							}
                        }
					});
				    
				    JButton export = new JButton("Write!"); 
					export.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							try {
								FileOutputStream fos = new FileOutputStream(new File("cluster.mgf"));
								for (int row = 0; row < ClusterPanel.this.specTbl.getRowCount(); row++) {
									if ((Boolean) ClusterPanel.this.tblMdl.getValueAt(row, 2)) {
										MascotGenericFile mgf = ClusterPanel.this.tblMdl.getSpectrumAt(row);
										mgf.writeToStream(fos);
									}
								}
								fos.close();
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
					});

				// panel of options for merging spectra
				JPanel mrgOptnsPnl = new JPanel();
				mrgOptnsPnl.setBorder(new TitledBorder("Merge Options"));
				mrgOptnsPnl.setLayout(new FormLayout("5dlu, p, 5dlu, r:p:g, 5dlu",
													 "5dlu, p, 5dlu, p:g, 5dlu, p:g, 5dlu"));
					SpinnerModel binSizeSpnMdl = new SpinnerNumberModel(1.0, 		// default
																			  0.1, 	// min	
																			  1e4, 		// max unbounded
																			  0.1);		// step  
        this.binSizeSpn = new JSpinner();							// init spinner
        this.binSizeSpn.setModel(binSizeSpnMdl);						// assign model to spinner
        this.binSizeSpn.addChangeListener(new RefreshListener());

        this.meanPeakInt = new JCheckBox();
        this.meanPeakInt.setSelected(true);
        this.meanPeakInt.addActionListener(new RefreshListener());

        this.nrmlzPeakInt = new JCheckBox();
        this.nrmlzPeakInt.setSelected(false);
        this.nrmlzPeakInt.addActionListener(new RefreshListener());
					
				mrgOptnsPnl.add(new JLabel("Set Bin Size [m/z]"), cc.xy(2,2));	
				mrgOptnsPnl.add(this.binSizeSpn, cc.xy(4,2));
				mrgOptnsPnl.add(new JLabel("Normalize Intensity to Highest"), cc.xy(2,4));
				mrgOptnsPnl.add(this.nrmlzPeakInt, cc.xy(4,4));
				mrgOptnsPnl.add(new JLabel("Average Peak Intensity"), cc.xy(2,6));
				mrgOptnsPnl.add(this.meanPeakInt, cc.xy(4,6));
				
			setPnl.add(mrgOptnsPnl, cc.xy(6,2));	
				
				intrActPnl.add(new JLabel("Fetch Spectra from File"), cc.xy(2,2));
				intrActPnl.add(getValueBtn, cc.xy(4,2));
				intrActPnl.add(new JLabel("Minimum Repeats of Sequence"), cc.xy(2,4));
				intrActPnl.add(sqncCntSpn, cc.xy(4,4));
				intrActPnl.add(new JLabel("Export Spectra to .mgf"), cc.xy(2,6));
				intrActPnl.add(export, cc.xy(4,6));
					
				// upper plot
				JPanel addSpcPnl = new JPanel();
				addSpcPnl.setBorder(new TitledBorder("Single Spectra"));
				addSpcPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu",
												   "5dlu, f:p:g, 5dlu"));
        this.addSpcPltPnl = new PlotPanel2(null);
        this.addSpcPltPnl.setPreferredSize(new Dimension(400, 200));
        this.addSpcPltPnl.clearSpectrumFile();

				addSpcPnl.add(this.addSpcPltPnl, cc.xy(2,2));
				
				// lower plot
				JPanel mrgdSpcPnl = new JPanel();
				mrgdSpcPnl.setBorder(new TitledBorder("Merged Spectrum"));
				mrgdSpcPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu",
						  							"5dlu, f:p:g, 5dlu"));
        this.mrgdSpcPltPnl = new PlotPanel2(null);
        this.mrgdSpcPltPnl.setPreferredSize(new Dimension(400, 200));
        this.mrgdSpcPltPnl.clearSpectrumFile();
					
				mrgdSpcPnl.add(this.mrgdSpcPltPnl, cc.xy(2,2));

        this.specTbl.getSelectionModel().addListSelectionListener(new RefreshListener());
			
			setPnl.add(intrActPnl, cc.xy(4,2));
			setPnl.add(specTblScp, cc.xywh(2,2,1,5));
			setPnl.add(addSpcPnl, cc.xyw(4,4,3));
			setPnl.add(mrgdSpcPnl, cc.xyw(4,6,3));
			
		clusterPnl.add(setPnl, cc.xy(2,2));
	}

	private void refreshSinglePlot() {
        this.slctdMGFs = new ArrayList<MascotGenericFile>(this.specTbl.getSelectedRowCount());
        this.addSpcPltPnl.clearSpectrumFile();
		boolean nrmlzPeaks = this.nrmlzPeakInt.isSelected();				// Check, if normalization is wanted
		int[] rowIndexes = this.specTbl.getSelectedRows();
		for (int i = 0; i < rowIndexes.length; i++) {
			MascotGenericFile mgf = this.tblMdl.getSpectrumAt(this.specTbl.convertRowIndexToModel(rowIndexes[i]));
			if (nrmlzPeaks) {
				mgf = this.normalizeMGF(mgf);
			}
            this.slctdMGFs.add(mgf);
            this.addSpcPltPnl.setSpectrumFile(mgf);
		}
        this.addSpcPltPnl.repaint();
	}
	
	private MascotGenericFile normalizeMGF(MascotGenericFile mgf) {
		double maxInt = mgf.getHighestIntensity()/100.0;
		MascotGenericFile mgfNrmlzd = new MascotGenericFile(mgf.getFilename(), mgf.getTitle(), new HashMap<Double, Double>(mgf.getPeaks()), mgf.getPrecursorMZ(), mgf.getIntensity(), mgf.getCharge());
		for (Map.Entry<Double, Double> peak : mgfNrmlzd.getPeaks().entrySet()) {
			peak.setValue(peak.getValue()/maxInt); 
		}
		return mgfNrmlzd;
	}

	private void refreshMergePlot() {
        this.mrgdSpcPltPnl.clearSpectrumFile();										// clear plot
        this.mergedMGF = this.mergeSpectra(this.slctdMGFs, (Double) this.binSizeSpn.getValue()); 	// function call + handover of vars
		if (!this.mergedMGF.getPeaks().isEmpty()) {
            this.mrgdSpcPltPnl.setSpectrumFile(this.mergedMGF, Color.RED);				// plotten
		}
        this.mrgdSpcPltPnl.repaint();
	}

	protected MascotGenericFile mergeSpectra(ArrayList<MascotGenericFile> mgfList, double binSize) {
		
		HashMap<Double, Double> rsltMap = new HashMap<Double, Double>(100);	// init new Hashmap as mastermap
		boolean averagePeakInt = this.meanPeakInt.isSelected();					// get Checkbox value averagePeakInt
//		double divisor = 1.0;
//		if (averagePeakInt) {
//			divisor = mgfList.size();
//		} or in short using ternary notation:
		double divisor = (averagePeakInt) ? mgfList.size() : 1.0;
		boolean nrmlzPeaks = this.nrmlzPeakInt.isSelected();						// get Checkbox value nrmlzPeakInt
		for (MascotGenericFile mgf : mgfList) {								// iterate through provided (former selected) spectrum files
			HashMap<Double, Double> mgfPeaks = mgf.getPeaks();				// import peak list
			double maxInt = (nrmlzPeaks) ? mgf.getHighestIntensity()/100.0 : 1.0;
			for (Double mz : mgfPeaks.keySet()) {							// go through all m/z
				double roundedMz = Math.round(mz/binSize)*binSize;			// rounding m/z
				Double rslInt = rsltMap.get(roundedMz);						// try getting intensity out of mastermap
				double mgfInt = mgfPeaks.get(mz) / (divisor*maxInt);
				if (rslInt != null) {										// if getting succeeded...
					rsltMap.put(roundedMz, rslInt + mgfInt);				// ...merge both intensities...
				} else {
					rsltMap.put(roundedMz, mgfInt);							// ...or just add new intensity	
				}
			}
		}
		return new MascotGenericFile(null, "test", rsltMap, 0.0, 0.0, 0);
	}

	private class ClusterTableModel extends DefaultTableModel {

		// instance initializer block
		{
            this.setColumnIdentifiers(new Object[] {"Serial Number",
											 "Sequence",
											 "Selected"}); }	// init column header captions
		
		public boolean isCellEditable(int row, int col) {
			return ((col == 2));	// ternary operator
												// column 3 is editable, others not
		}
		
		public Class<?> getColumnClass(int col) {
			switch (col) {
			case 0:
				return Long.class;
			case 1:
				return String.class;
			case 2:
				return Boolean.class;
			default:
				return this.getValueAt(0,col).getClass();
			}
		}
		
		@Override
		public Object getValueAt(int row, int col) {
			Object res = super.getValueAt(row, col);
			if (col == 1) {
				MascotGenericFile mgf = (MascotGenericFile) res;
				String title = mgf.getTitle();
				return title.substring(0, title.indexOf(' '));
//				return ((RankedLibrarySpectrum)res).getSequence();
			}
			return res;
//			// oder kurz:					
//			return ((col==1) ? ((MascotGenericFile)super.getValueAt(row, col)).getSequence() : super.getValueAt(row, col));
		}
		
		public MascotGenericFile getSpectrumAt(int row) {
			return ((MascotGenericFile)super.getValueAt(row, 1));
			
		}
	}
	
	private class RefreshListener implements ChangeListener, ActionListener, ListSelectionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
            this.refresh();
		}

		@Override
		public void stateChanged(ChangeEvent arg0) {
            this.refresh();
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
                this.refresh();
			}
		}
		
		private void refresh() {
            ClusterPanel.this.refreshSinglePlot();
            ClusterPanel.this.refreshMergePlot();
		}
	}
//	protected void fetchResultsFromTree() {
//
//		Integer sqncCnt = (Integer)sqncCntSpnMdl.getValue();						// get spinner value
//		DefaultTableModel tblMdl = (DefaultTableModel) specTbl.getModel();			// import table model
//		tblMdl.setRowCount(0);														// resetting counter to 0
//		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) clientFrame.queryTree.getModel().getRoot();// look for root of tree
//		DefaultMutableTreeNode leafNode = rootNode.getFirstLeaf();					// look for first leaf
//		if (rootNode != leafNode) {								
//			
//			seqMap = new HashMap<String, Integer>(rootNode.getLeafCount());			// init size of seqMap euqal to number of leaves
//			while (leafNode != null) {
//				
//				String sequence = "";
//				RankedLibrarySpectrum rls = null;
//				
//				try {
//					String specTitle = clientFrame.queryTree.getSpectrumAt(leafNode).getTitle();
//					ArrayList<RankedLibrarySpectrum> hitList = clientFrame.resultMap.get(specTitle);
//					for (int i = 0; i < hitList.size(); i++) {
//						//alternativ: for (RankedLibrarySpectrum hit : hitList) 
//						if (Math.round(hitList.get(i).getScore()*1e6) == 1e6) {		// choose spectra with correlation == 1
//							rls = hitList.get(i);
//							sequence = hitList.get(i).getSequence();
//							if (seqMap.containsKey(sequence)) {
//								seqMap.put(sequence, seqMap.get(sequence)+1);
//							} else {
//								seqMap.put(sequence, 1);
//							}
//						}
//					}
//						
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				
//				if (!sequence.isEmpty()) {
//					tblMdl.addRow(new Object[] {"Spectrum " + leafNode.getUserObject(), rls, false});
//				}
//				leafNode = leafNode.getNextLeaf();
//			}
//			for (int row = 0; row < specTbl.getRowCount(); row++) {
//				String sequence = (String) tblMdl.getValueAt(row, 1);
//				if (seqMap.get(sequence) >= sqncCnt) {					// if sequence appears more often than spin count demands...
//					tblMdl.setValueAt(true, row, 2);					// ...add mark to checkbox in table
//				}
//			}
//		}
//	}
}
