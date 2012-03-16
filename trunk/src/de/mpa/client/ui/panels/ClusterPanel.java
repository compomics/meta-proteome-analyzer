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
import java.util.Map.Entry;

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

import de.mpa.client.ui.ClientFrame;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;
import de.mpa.ui.ExtensionFileFilter;
import de.mpa.ui.PlotPanel2;

public class ClusterPanel extends JPanel {				
	
	// fields or class variables
	private ClientFrame clientFrame;
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
	private ClusterTableModel tblMdl;

	public ClusterPanel(ClientFrame clientFrame) {
		this.clientFrame = clientFrame;
		initComponents();												// function call
	}
	
	// void = ohne R�ckgabewert
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
				tblMdl = new ClusterTableModel();	// final f�r unver�nderliche Referenz
//				tblMdl.addRow(new Object[] {1,"ABCD",true});				// Zeile 1 hart beschreiben (mit String und checkbox)
				specTbl = new JTable(tblMdl);							    // Init 'specTbl' adding tblMdl as model
				specTbl.setAutoCreateRowSorter(true);						// enables alphabetic sorting, done by click on column title 
//				specTbl.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // disable multiple selections
				JScrollPane specTblScp = new JScrollPane(specTbl);			// Init Scrollbar for 'specTbl'
			
				// panel of table options
				JPanel intrActPnl = new JPanel();
				intrActPnl.setBorder(new TitledBorder("Selections"));
				intrActPnl.setLayout(new FormLayout("5dlu, p, 5dlu, r:p:g, 5dlu",
													"5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));

					sqncCntSpnMdl = new SpinnerNumberModel(1, 					// default
														   1, 					// min	
													   	   null, 				// max unbegrenzt
													       1);					// step  
					JSpinner sqncCntSpn = new JSpinner();							// init spinner with prior definded model
					sqncCntSpn.setModel(sqncCntSpnMdl);								// assign model to spinner 
					sqncCntSpn.addChangeListener(new ChangeListener() {				// add changelistener to spinner
					
						
						
						@Override
						public void stateChanged(ChangeEvent evt) {
							
							Integer sqncCnt = (Integer)sqncCntSpnMdl.getValue();			// get spinner value
							for (int row = 0; row < specTbl.getRowCount(); row++) {			// init loop over size of specTbl
								String sequence = (String) tblMdl.getValueAt(row, 1);	    // get sequence for current entry 
	
								if (seqMap.get(sequence) >= sqncCnt) {						
									tblMdl.setValueAt(true, row, 2);
								} else {
									tblMdl.setValueAt(false, row, 2);
								}
							}
						}
					});
				    JButton getValueBtn = new JButton("Import!"); 							// import spectra file
					getValueBtn.addActionListener(new ActionListener() {		
						@Override
						public void actionPerformed(ActionEvent evt) {
							final JFileChooser fc = new JFileChooser("C:/Documents and Settings/kohrs/My Documents/Workspace/MetaProteomeAnalyzer");
							fc.setFileFilter(new ExtensionFileFilter("mgf", false));
							fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
							fc.setMultiSelectionEnabled(true); 							
							int result = fc.showOpenDialog(clientFrame);
							if (result == (JFileChooser.APPROVE_OPTION)) {
								tblMdl.setRowCount(0);
								seqMap = new HashMap<String, Integer>();
								File[] slctdFls = fc.getSelectedFiles();
								for (File file : slctdFls) {
									int i = 1;
									try {
										MascotGenericFileReader reader = new MascotGenericFileReader(file);
										List<MascotGenericFile> mgfFiles = reader.getSpectrumFiles();
										for (MascotGenericFile mgf : mgfFiles) {
											tblMdl.addRow(new Object[] {"Spectrum " + i++, mgf, false});
											String title = mgf.getTitle();
											String sequence = title.substring(0, title.indexOf(' '));
											if (seqMap.containsKey(sequence)) {
												seqMap.put(sequence, seqMap.get(sequence)+1);
											} else {
												seqMap.put(sequence, 1);	
											}
										}
										
										
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
								for (int i = 0; i < tblMdl.getRowCount(); i++) {
									String sequence = (String) tblMdl.getValueAt(i, 1);
									if (seqMap.get(sequence) >= (Integer)sqncCntSpnMdl.getValue()) {
										tblMdl.setValueAt(true, i, 2);
									}
								}
							};
						}
					});
				    
				    JButton export = new JButton("Write!"); 
					export.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							try {
								FileOutputStream fos = new FileOutputStream(new File("cluster.mgf"));
								for (int row = 0; row < specTbl.getRowCount(); row++) {
									if ((Boolean) tblMdl.getValueAt(row, 2)) {
										MascotGenericFile mgf = tblMdl.getSpectrumAt(row);
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
					final SpinnerModel binSizeSpnMdl = new SpinnerNumberModel(1.0, 		// default
																			  0.1, 	// min	
																			  1e4, 		// max unbegrenzt
																			  0.1);		// step  
					binSizeSpn = new JSpinner();							// init spinner
					binSizeSpn.setModel(binSizeSpnMdl);						// assign model to spinner 
					binSizeSpn.addChangeListener(new RefreshListener());

					meanPeakInt = new JCheckBox();
					meanPeakInt.setSelected(true);
					meanPeakInt.addActionListener(new RefreshListener());
					
					nrmlzPeakInt = new JCheckBox();
					nrmlzPeakInt.setSelected(false);
					nrmlzPeakInt.addActionListener(new RefreshListener());
					
				mrgOptnsPnl.add(new JLabel("Set Bin Size [m/z]"), cc.xy(2,2));	
				mrgOptnsPnl.add(binSizeSpn, cc.xy(4,2));
				mrgOptnsPnl.add(new JLabel("Normalize Intensity to Highest"), cc.xy(2,4));
				mrgOptnsPnl.add(nrmlzPeakInt, cc.xy(4,4));
				mrgOptnsPnl.add(new JLabel("Average Peak Intensity"), cc.xy(2,6));
				mrgOptnsPnl.add(meanPeakInt, cc.xy(4,6));
				
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
					addSpcPltPnl = new PlotPanel2(null);
					addSpcPltPnl.setPreferredSize(new Dimension(400, 200));
					addSpcPltPnl.clearSpectrumFile();

				addSpcPnl.add(addSpcPltPnl, cc.xy(2,2));
				
				// lower plot
				JPanel mrgdSpcPnl = new JPanel();
				mrgdSpcPnl.setBorder(new TitledBorder("Merged Spectrum"));
				mrgdSpcPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu",
						  							"5dlu, f:p:g, 5dlu"));
					mrgdSpcPltPnl = new PlotPanel2(null);
					mrgdSpcPltPnl.setPreferredSize(new Dimension(400, 200));
					mrgdSpcPltPnl.clearSpectrumFile();
					
				mrgdSpcPnl.add(mrgdSpcPltPnl, cc.xy(2,2));

				specTbl.getSelectionModel().addListSelectionListener(new RefreshListener());
			
			setPnl.add(intrActPnl, cc.xy(4,2));
			setPnl.add(specTblScp, cc.xywh(2,2,1,5));
			setPnl.add(addSpcPnl, cc.xyw(4,4,3));
			setPnl.add(mrgdSpcPnl, cc.xyw(4,6,3));
			
		clusterPnl.add(setPnl, cc.xy(2,2));
	}

	private void refreshSinglePlot() {
		slctdMGFs = new ArrayList<MascotGenericFile>(specTbl.getSelectedRowCount());
		addSpcPltPnl.clearSpectrumFile();
		boolean nrmlzPeaks = nrmlzPeakInt.isSelected();				// Check, if normalization is wanted
		int[] rowIndexes = specTbl.getSelectedRows();
		for (int i = 0; i < rowIndexes.length; i++) {
			MascotGenericFile mgf = tblMdl.getSpectrumAt(specTbl.convertRowIndexToModel(rowIndexes[i]));	
			if (nrmlzPeaks) {
				mgf = normalizeMGF(mgf);
			}
			slctdMGFs.add(mgf);
			addSpcPltPnl.setSpectrumFile(mgf);
		}
		addSpcPltPnl.repaint();
	}
	
	private MascotGenericFile normalizeMGF(MascotGenericFile mgf) {
		double maxInt = mgf.getHighestIntensity()/100.0;
		MascotGenericFile mgfNrmlzd = new MascotGenericFile(mgf.getFilename(), mgf.getTitle(), new HashMap<Double, Double>(mgf.getPeaks()), mgf.getPrecursorMZ(), mgf.getCharge());
		for (Entry<Double, Double> peak : mgfNrmlzd.getPeaks().entrySet()) {
			peak.setValue(peak.getValue()/maxInt); 
		}
		return mgfNrmlzd;
	}

	private void refreshMergePlot() {
		mrgdSpcPltPnl.clearSpectrumFile();										// clear plot
		mergedMGF = mergeSpectra(slctdMGFs, (Double) binSizeSpn.getValue()); 	// function call + handover of vars
		if (!mergedMGF.getPeaks().isEmpty()) {
			mrgdSpcPltPnl.setSpectrumFile(mergedMGF, Color.RED);				// plotten
		}
		mrgdSpcPltPnl.repaint();
	}

	protected MascotGenericFile mergeSpectra(ArrayList<MascotGenericFile> mgfList, double binSize) {
		
		HashMap<Double, Double> rsltMap = new HashMap<Double, Double>(100);	// init new Hashmap as mastermap
		boolean averagePeakInt = meanPeakInt.isSelected();					// get Checkbox value averagePeakInt
//		double divisor = 1.0;
//		if (averagePeakInt) {
//			divisor = mgfList.size();
//		} oder kurz tern�r:
		double divisor = (averagePeakInt) ? mgfList.size() : 1.0;
		boolean nrmlzPeaks = nrmlzPeakInt.isSelected();						// get Checkbox value nrmlzPeakInt
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
		return new MascotGenericFile(null, "test", rsltMap, 0.0, 0);
	}

	private class ClusterTableModel extends DefaultTableModel {

		// instance initializer block
		{ setColumnIdentifiers(new Object[] {"Serial Number",
											 "Sequence",
											 "Selected"}); }	// init column header captions
		
		public boolean isCellEditable(int row, int col) {
			return ((col == 2) ? true : false);	// ternary operator
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
				return getValueAt(0,col).getClass();
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
			refresh();
		}

		@Override
		public void stateChanged(ChangeEvent arg0) {
			refresh();
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting()) {
				refresh();
			}
		}
		
		private void refresh() {
			refreshSinglePlot();
			refreshMergePlot();
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
//						if (Math.round(hitList.get(i).getScore()*1e6) == 1e6) {		// w�hle Spektren mit Korrelation 1
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
//				if (seqMap.get(sequence) >= sqncCnt) {					// if sequence appears more often than spincount demands...
//					tblMdl.setValueAt(true, row, 2);					// ...add mark to checkbox in table
//				}
//			}
//		}
//	}
}
