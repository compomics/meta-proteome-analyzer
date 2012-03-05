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

import javax.swing.JButton;
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
import javax.swing.tree.DefaultMutableTreeNode;

import com.jgoodies.forms.layout.FormLayout;

import de.mpa.algorithms.RankedLibrarySpectrum;
import de.mpa.client.ui.ClientFrame;
import de.mpa.io.MascotGenericFile;
import de.mpa.ui.PlotPanel2;

public class ClusterPanel extends JPanel {
	
	// fields or class variables
	private ClientFrame parent;
	protected HashMap<String, Integer> seqMap;
	private JSpinner binSizeSpn;
	private PlotPanel2 mrgdSpcPltPnl;
	protected ArrayList<MascotGenericFile> slctdMGFs = new ArrayList<MascotGenericFile>();
	private JTable specTbl;
	private SpinnerNumberModel sqncCntSpnMdl;
	protected MascotGenericFile mergedMGF;

	public ClusterPanel(ClientFrame clientFrame) {
		this.parent = clientFrame;
		initComponents();												// function call
	}
	
	// void = ohne R�ckgabewert
	private void initComponents() {										// declaration of initComponents, void = bo return value
		ClusterPanel clusterPnl = this;									// Init Panel "Clustering"
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
				final ClusterTableModel tblMdl = new ClusterTableModel();	// final f�r unver�nderliche Referenz
//				tblMdl.addRow(new Object[] {1,"ABCD",true});				// Zeile 1 hart beschreiben (mit String und checkbox)
				specTbl = new JTable(tblMdl);							    // Init 'specTbl'
				specTbl.setAutoCreateRowSorter(true);
//				specTbl.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // disable multiple selections
				JScrollPane specTblScp = new JScrollPane(specTbl);				// Init Scrollbar f�r 'specTbl'
			
				// panel of table options
				JPanel intrActPnl = new JPanel();
				intrActPnl.setBorder(new TitledBorder("Selections"));
				intrActPnl.setLayout(new FormLayout("5dlu, p, 5dlu, r:p:g, 5dlu",
													"5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));

					sqncCntSpnMdl = new SpinnerNumberModel(1, 	// default
														   1, 	// min	
													   	   null, // max unbegrenzt
													       1);	// step  
					JSpinner sqncCntSpn = new JSpinner();							// init spinner
					sqncCntSpn.setModel(sqncCntSpnMdl);								// assign model to spinner 
					sqncCntSpn.addChangeListener(new ChangeListener() {				// add changelistener to spinner
						
						@Override
						public void stateChanged(ChangeEvent evt) {
							
							Integer sqncCnt = (Integer)sqncCntSpnMdl.getValue();	
							for (int row = 0; row < specTbl.getRowCount(); row++) {
								String sequence = (String) tblMdl.getValueAt(row, 1);
	
								if (seqMap.get(sequence) >= sqncCnt) {
									tblMdl.setValueAt(true, row, 2);
								} else {
									tblMdl.setValueAt(false, row, 2);
								}
							}
						}
					});
					
				    JButton getValueBtn = new JButton("Do!"); 					// Init Button 'getValueBtn' mit Titel "Holen"
					getValueBtn.addActionListener(new ActionListener() {		// ActionListener
				
						@Override
						public void actionPerformed(ActionEvent evt) {
							fetchResultsFromTree();
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
													 "5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));
					final SpinnerModel binSizeSpnMdl = new SpinnerNumberModel(1.0, 	// default
																			  1.0, 	// min	
																			  1e4, // max unbegrenzt
																			  0.1);	// step  
					binSizeSpn = new JSpinner();							// init spinner
					binSizeSpn.setModel(binSizeSpnMdl);								// assign model to spinner 
					binSizeSpn.addChangeListener(new ChangeListener() {
						
						@Override
						public void stateChanged(ChangeEvent e) {
							refreshMergePlot();
						}
					});
				
				mrgOptnsPnl.add(new JLabel("Set Bin Size [m/z]"), parent.cc.xy(2,2));	
				mrgOptnsPnl.add(binSizeSpn, parent.cc.xy(4,2));					
			
			setPnl.add(mrgOptnsPnl, parent.cc.xy(6,2));	
				
				intrActPnl.add(new JLabel("Import Spectral Search Results"), parent.cc.xy(2,2));
				intrActPnl.add(getValueBtn, parent.cc.xy(4,2));
				intrActPnl.add(new JLabel("Minimum Repeats of Sequence"), parent.cc.xy(2,4));
				intrActPnl.add(sqncCntSpn, parent.cc.xy(4,4));
				intrActPnl.add(new JLabel("Export Spectra to .mgf"), parent.cc.xy(2,6));
				intrActPnl.add(export, parent.cc.xy(4,6));
					
				// upper plot
				JPanel addSpcPnl = new JPanel();
				addSpcPnl.setBorder(new TitledBorder("Single Spectra"));
				addSpcPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu",
												   "5dlu, f:p:g, 5dlu"));
					final PlotPanel2 addSpcPltPnl = new PlotPanel2(null);
					addSpcPltPnl.setPreferredSize(new Dimension(400, 200));
					addSpcPltPnl.clearSpectrumFile();

				addSpcPnl.add(addSpcPltPnl, parent.cc.xy(2,2));
				
				// lower plot
				JPanel mrgdSpcPnl = new JPanel();
				mrgdSpcPnl.setBorder(new TitledBorder("Merged Spectrum"));
				mrgdSpcPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu",
						  							"5dlu, f:p:g, 5dlu"));
					mrgdSpcPltPnl = new PlotPanel2(null);
					mrgdSpcPltPnl.setPreferredSize(new Dimension(400, 200));
					mrgdSpcPltPnl.clearSpectrumFile();
					
				mrgdSpcPnl.add(mrgdSpcPltPnl, parent.cc.xy(2,2));

				specTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						if (!e.getValueIsAdjusting()) {
							slctdMGFs = new ArrayList<MascotGenericFile>(specTbl.getSelectedRowCount());
							mrgdSpcPltPnl.clearSpectrumFile();
							addSpcPltPnl.clearSpectrumFile();
							int[] rowIndexes = specTbl.getSelectedRows();
							for (int rowIndex : rowIndexes) {
								MascotGenericFile mgf = tblMdl.getSpectrumAt(specTbl.convertRowIndexToModel(rowIndex));
								slctdMGFs.add(mgf);
								addSpcPltPnl.setSpectrumFile(mgf);
							}
							
							refreshMergePlot();
							
							addSpcPltPnl.repaint();
							mrgdSpcPltPnl.repaint();
						}
					}
				});
			
			setPnl.add(intrActPnl, parent.cc.xy(4,2));
			setPnl.add(specTblScp, parent.cc.xywh(2,2,1,5));
			setPnl.add(addSpcPnl, parent.cc.xyw(4,4,3));
			setPnl.add(mrgdSpcPnl, parent.cc.xyw(4,6,3));
			
		clusterPnl.add(setPnl, parent.cc.xy(2,2));
	}
	
	protected void fetchResultsFromTree() {

		Integer sqncCnt = (Integer)sqncCntSpnMdl.getValue();						// Spinnerwert holen
		DefaultTableModel tblMdl = (DefaultTableModel) specTbl.getModel();
		tblMdl.setRowCount(0);														// resetting counter to 0
		DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) parent.getQueryTree().getModel().getRoot();
		DefaultMutableTreeNode leafNode = rootNode.getFirstLeaf();		
		if (rootNode != leafNode) {
			
			seqMap = new HashMap<String, Integer>(rootNode.getLeafCount());			// init size of seqMap euqal to number of leaves
			while (leafNode != null) {
				
				String sequence = "";
				RankedLibrarySpectrum rls = null;
				
				try {
					String specTitle = parent.getQueryTree().getSpectrumAt(leafNode).getTitle();
					ArrayList<RankedLibrarySpectrum> hitList = parent.getResultMap().get(specTitle);
					for (int i = 0; i < hitList.size(); i++) {
						//alternativ: for (RankedLibrarySpectrum hit : hitList) 
						if (Math.round(hitList.get(i).getScore()*1e6) == 1e6) {		// w�hle Spektren mit Korrelation 1
							rls = hitList.get(i);
							sequence = hitList.get(i).getSequence();
							if (seqMap.containsKey(sequence)) {
								seqMap.put(sequence, seqMap.get(sequence)+1);
							} else {
								seqMap.put(sequence, 1);
							}
						}
					}
						
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				if (!sequence.isEmpty()) {
					tblMdl.addRow(new Object[] {"Spectrum " + leafNode.getUserObject(), rls, false});
				}
				leafNode = leafNode.getNextLeaf();
			}

			for (int row = 0; row < specTbl.getRowCount(); row++) {
				String sequence = (String) tblMdl.getValueAt(row, 1);
				if (seqMap.get(sequence) >= sqncCnt) {
					tblMdl.setValueAt(true, row, 2);
				}
			}
		}
	}

	private void refreshMergePlot() {
		mrgdSpcPltPnl.clearSpectrumFile();									// clear plot
		
		mergedMGF = mergeSpectra(slctdMGFs, (Double) binSizeSpn.getValue());
		mrgdSpcPltPnl.setSpectrumFile(mergedMGF, Color.RED);
		mergeSpectra(slctdMGFs, (Double) binSizeSpn.getValue());
		
		mrgdSpcPltPnl.repaint();											// plotten
	}
	
	protected MascotGenericFile mergeSpectra(ArrayList<MascotGenericFile> mgfList, double binSize) {
		
		HashMap<Double, Double> rsltMap = new HashMap<Double, Double>(100);	// init new Hashmap as master
		
		for (MascotGenericFile mgf : mgfList) {								// iterate through provided (former selected) spectrum files
			HashMap<Double, Double> mgfPeaks = mgf.getPeaks();				// import peak list
			for (Double mz : mgfPeaks.keySet()) {							// go through all m/z
				double roundedMz = Math.round(mz/binSize)*binSize;			// rounding m/z
				
//				if (rsltMap.containsKey(roundedMz)) {
//					Double val = rsltMap.get(roundedMz);
//				}
				Double intensity = rsltMap.get(roundedMz);					// try getting intensity out of mastermap
				if (intensity != null) {									// if getting succeeded...
					rsltMap.put(roundedMz, intensity + mgfPeaks.get(mz));	// ...merge both intensities...
				} else {
					rsltMap.put(roundedMz, mgfPeaks.get(mz));				// ...or just add new intensity
				}
			}
		}
		return new MascotGenericFile("", "test", rsltMap, 0.0, 0);
	}

	private class ClusterTableModel extends DefaultTableModel {

		// instance initializer block
		{ setColumnIdentifiers(new Object[] {"Serial Number",
											 "Sequence",
											 "Selected"}); }	// init column header captions
		
		public boolean isCellEditable(int row, int col) {
			return ((col == 2) ? true : false);	// ternary operator
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
		
		public Object getValueAt(int row, int col) {
//			Object res = super.getValueAt(row, col);
//			if (col == 1) {
//				return ((RankedLibrarySpectrum)res).getSequence();
//			}
//			return res;
//				oder kurz:					
			return ((col==1) ? ((RankedLibrarySpectrum)super.getValueAt(row, col)).getSequence() : super.getValueAt(row, col));
		}
		
		public MascotGenericFile getSpectrumAt(int row) {
			return ((RankedLibrarySpectrum)super.getValueAt(row, 1)).getSpectrumFile();
			
		}
	}
	
}
