package de.mpa.client.ui.panels;

import static java.lang.Math.max;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import no.uib.jsparklines.renderers.JSparklinesBarChartTableCellRenderer;

import org.jdesktop.swingx.JXTable;
import org.jfree.chart.plot.PlotOrientation;

import com.compomics.util.gui.spectrum.DefaultSpectrumAnnotation;
import com.compomics.util.gui.spectrum.SpectrumPanel;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.model.ExperimentResult;
import de.mpa.client.model.PeptideHit;
import de.mpa.client.model.PeptideSpectrumMatch;
import de.mpa.client.model.ProteinHit;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.dialogs.GeneralExceptionHandler;
import de.mpa.db.accessor.Searchspectrum;
import de.mpa.db.extractor.SpectrumExtractor;
import de.mpa.fragmentation.FragmentIon;
import de.mpa.fragmentation.Fragmentizer;
import de.mpa.fragmentation.Masses;
import de.mpa.io.MascotGenericFile;

public class DbSearchResultPanel extends JPanel{
	
	private ClientFrame clientFrame;
	private DbSearchResultPanel dbSearchResultPnl;
	private JXTable proteinTbl;
	private ExperimentResult experimentResult;
	private JXTable peptideTbl;
	private TreeMap<String, PeptideHit> currentPeptideHits;
	private JXTable psmTbl;
	private HashMap<Integer, PeptideSpectrumMatch> currentPsms = new HashMap<Integer, PeptideSpectrumMatch>();
	private SpectrumPanel specPnl;
	private String currentSelectedPeptide;
	private JCheckBox aIonsJCheckBox;
	private JCheckBox bIonsJCheckBox;
	private JCheckBox cIonsJCheckBox;
	private JCheckBox yIonsJCheckBox;
	private JCheckBox xIonsJCheckBox;
	private JCheckBox zIonsJCheckBox;
	private JCheckBox waterLossJCheckBox;
	private JCheckBox ammoniumLossJCheckBox;
	private JCheckBox chargeOneJCheckBox;
	private JCheckBox chargeTwoJCheckBox;
	private AbstractButton chargeOverTwoJCheckBox;
	private JPanel spectrumFilterPanel;
	private JPanel spectrumOverviewPnl;
	private Vector<DefaultSpectrumAnnotation> currentAnnotations;
	private JPanel spectrumJPanel;
	private AbstractButton precursorJCheckBox;
	private ListSelectionModel selectionModel;
	private int maxSpectralCount;
	private int maxPeptideCount;
	private int maxPeptideSpectraCount;
	

	public DbSearchResultPanel(ClientFrame clientFrame) {
		this.clientFrame = clientFrame;
		initComponents();
	}

	/**
	 * Initializes the components of the database search results panel
	 */
	private void initComponents() {
		// Cell constraints
		CellConstraints cc = new CellConstraints();
		
		// Scroll panes
		JScrollPane proteinTableScp = new JScrollPane();
		JScrollPane peptideTableScp = new JScrollPane();
		JScrollPane psmTableScp = new JScrollPane();
		
		dbSearchResultPnl = this;
		dbSearchResultPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu, f:p:g, 5dlu"));
	
		final JPanel proteinPnl = new JPanel();
		proteinPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, p, 5dlu, f:p:g, 5dlu"));
		proteinPnl.setBorder(BorderFactory.createTitledBorder("Proteins"));
	
		// Setup the tables
		setupProteinTableProperties();
		setupPeptideTableProperties();
		setupPsmTableProperties();
		
		proteinTableScp.setViewportView(proteinTbl);
		proteinTableScp.setPreferredSize(new Dimension(800, 200));
		
		peptideTableScp.setViewportView(peptideTbl);
		peptideTableScp.setPreferredSize(new Dimension(350, 150));
		
		psmTableScp.setViewportView(psmTbl);
		psmTableScp.setPreferredSize(new Dimension(350, 150));
		
		JButton updateBtn = new JButton("Get results");
		JPanel topPnl = new JPanel(new FormLayout("p", "p:g"));
		
		topPnl.add(updateBtn, cc.xy(1, 1));
		updateBtn.setPreferredSize(new Dimension(150, 20));
	
		updateBtn.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent e) {
				
					experimentResult = clientFrame.getClient().getExperimentResult(clientFrame.getProjectPnl().getCurrentProjContent(), clientFrame.getProjectPnl().getCurrentExperimentContent());
					//TODO ADD search engine from runtable
					List<String> searchEngines = new ArrayList<String>(Arrays.asList(new String [] {"Crux", "Inspect", "Xtandem","OMSSA"}));
					experimentResult.setSearchEngines(searchEngines);
					
					
					updateProteinResultsTable();
					proteinTbl.getSelectionModel().setSelectionInterval(0, 0);
					queryProteinTableMouseClicked(null);
					peptideTbl.getSelectionModel().setSelectionInterval(0, 0);
					queryPeptideTableMouseClicked(null);
					psmTbl.getSelectionModel().setSelectionInterval(0, 0);
					queryPsmTableMouseClicked(null);
					
					// Enables the export functionality
					clientFrame.getClienFrameMenuBar().setExportResultsEnabled(true);
					
			}
		});
		proteinPnl.add(topPnl, cc.xy(2, 2));
		proteinPnl.add(proteinTableScp, cc.xy(2, 4));
	
				
		// Peptide panel
		final JPanel peptidePnl = new JPanel();
		peptidePnl.setLayout(new FormLayout("5dlu, p:g, 5dlu",  "5dlu, f:p, 5dlu"));
		peptidePnl.setBorder(BorderFactory.createTitledBorder("Peptides"));
		peptidePnl.add(peptideTableScp, cc.xy(2, 2));
		
		// PSM panel
		final JPanel psmPanel = new JPanel();
		psmPanel.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p, 5dlu"));
		psmPanel.setBorder(BorderFactory.createTitledBorder("Peptide-Spectrum-Matches"));
		psmPanel.add(psmTableScp, cc.xy(2, 2));

		// Peptide and Psm Panel
		JPanel pepPsmPnl = new JPanel(new FormLayout("p:g","f:p:g,f:p:g"));
		
		pepPsmPnl.add(peptidePnl, cc.xy(1, 1));
		pepPsmPnl.add(psmPanel, cc.xy(1, 2));
		
		// Build the spectrum filter panel
		constructSpectrumFilterPanel();
		spectrumOverviewPnl = new JPanel(new BorderLayout());
		spectrumOverviewPnl.setBorder(BorderFactory.createTitledBorder("Spectrum"));
		spectrumJPanel = new JPanel();
		spectrumJPanel.setLayout(new BoxLayout(spectrumJPanel, BoxLayout.LINE_AXIS));
		spectrumJPanel.setBackground(Color.WHITE);
		
		spectrumOverviewPnl.add(spectrumJPanel, BorderLayout.CENTER);
		spectrumOverviewPnl.add(spectrumFilterPanel, BorderLayout.EAST);
		
		// SplitPane
		JSplitPane splitPn = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, pepPsmPnl, spectrumOverviewPnl);
		splitPn.setBorder(null);
		
		// Remove the border from the splitpane divider
		BasicSplitPaneDivider divider = ((BasicSplitPaneUI) splitPn.getUI()).getDivider();
		if (divider != null) {
			divider.setBorder(null);
		}
		dbSearchResultPnl.add(proteinPnl, cc.xy(2,2));
		dbSearchResultPnl.add(splitPn, cc.xy(2, 4));
	}
	
	/**
	 * This method sets up the protein results table.
	 */
	private void setupProteinTableProperties(){
		// Protein table
		proteinTbl = new JXTable(new DefaultTableModel() {
			// instance initializer block
			{ setColumnIdentifiers(new Object[] {" ", "Accession", "Description", "Peptide Count", "Spectral Count"}); }
	
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		});
		proteinTbl.getColumn(" ").setMinWidth(30);
		proteinTbl.getColumn(" ").setMaxWidth(30);
		proteinTbl.getColumn("Accession").setMinWidth(70);
		proteinTbl.getColumn("Accession").setMaxWidth(70);
		proteinTbl.getColumn("Peptide Count").setMinWidth(110);
		proteinTbl.getColumn("Peptide Count").setMaxWidth(110);
		proteinTbl.getColumn("Spectral Count").setMinWidth(115);
		proteinTbl.getColumn("Spectral Count").setMaxWidth(115);
		
		// Sort the table by the number of peptides
		TableRowSorter<TableModel> sorter  = new TableRowSorter<TableModel>(proteinTbl.getModel());
		List <RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
		sortKeys.add(new RowSorter.SortKey(4, SortOrder.DESCENDING));
		
		sorter.setSortKeys(sortKeys);
		proteinTbl.setRowSorter(sorter);
		
		proteinTbl.addMouseListener(new java.awt.event.MouseAdapter() {
			
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				queryProteinTableMouseClicked(evt);
				//TODO
				// Select first row of peptide table
				peptideTbl.getSelectionModel().setSelectionInterval(0, 0);	
				// Creates PSM Table
				queryPeptideTableMouseClicked(null);
				// Select first row of psm table
				psmTbl.getSelectionModel().setSelectionInterval(0, 0);	
				// Paint the first psm
				queryPsmTableMouseClicked(null);
			}
		});
	
		proteinTbl.addKeyListener(new java.awt.event.KeyAdapter() {
	
			@Override
			public void keyReleased(java.awt.event.KeyEvent evt) {
				queryProteinTableKeyReleased(evt);
				//TODO
				// Select first row of peptide table
				peptideTbl.getSelectionModel().setSelectionInterval(0, 0);	
				// Creates PSM Table
				queryPeptideTableMouseClicked(null);
				// Select first row of psm table
				psmTbl.getSelectionModel().setSelectionInterval(0, 0);	
				// Paint the first psm
				queryPsmTableMouseClicked(null);
			}
		});
		// Only one row is selectable
		proteinTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		// Enables column control
		proteinTbl.setColumnControlVisible(true);
		
	}
	
	/**
	 * This method sets up the peptide results table.
	 */
	private void setupPeptideTableProperties(){
		// Peptide table
		peptideTbl = new JXTable(new DefaultTableModel() {
			// instance initializer block
			{ setColumnIdentifiers(new Object[] {" ", "Sequence", "No. Spectra"}); }
	
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		});
		peptideTbl.getColumn(" ").setMinWidth(30);
		peptideTbl.getColumn(" ").setMaxWidth(30);
		peptideTbl.getColumn("Sequence").setMinWidth(70);
//		peptideTbl.getColumn("Sequence").setMaxWidth(70);
		peptideTbl.getColumn("No. Spectra").setMinWidth(100);
		peptideTbl.getColumn("No. Spectra").setMaxWidth(100);
		
		
		// Sort the table by the number of peptides
		TableRowSorter<TableModel> sorter  = new TableRowSorter<TableModel>(peptideTbl.getModel());
		List <RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
		sortKeys.add(new RowSorter.SortKey(2, SortOrder.DESCENDING));
		sorter.setSortKeys(sortKeys);
		peptideTbl.setRowSorter(sorter);
		
		peptideTbl.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				queryPeptideTableMouseClicked(evt);
				// Select first row of psm table
				psmTbl.getSelectionModel().setSelectionInterval(0, 0);	
				queryPsmTableMouseClicked(null);
			}
		});
	
		peptideTbl.addKeyListener(new java.awt.event.KeyAdapter() {
			@Override
			public void keyReleased(java.awt.event.KeyEvent evt) {
				queryPeptideTableKeyReleased(evt);
				// Select first row of psm table
				psmTbl.getSelectionModel().setSelectionInterval(0, 0);	
				queryPsmTableMouseClicked(null);
			}
		});
		
		// Single selection only
		peptideTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		// Enables column control
		peptideTbl.setColumnControlVisible(true);
	}
	
	/**
	 * This method sets up the PSM results table.
	 */
	private void setupPsmTableProperties(){
		// Peptide table
		psmTbl = new JXTable(new DefaultTableModel() {
			// instance initializer block
			{ setColumnIdentifiers(new Object[] {" ", "Sequence", "z"}); }
	
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		});
		psmTbl.getColumn(" ").setMinWidth(30);
		psmTbl.getColumn(" ").setMaxWidth(30);
//		psmTbl.getColumn("Sequence").setMinWidth(70);
//		psmTbl.getColumn("Sequence").setMaxWidth(70);
		psmTbl.getColumn("z").setMinWidth(40);
		psmTbl.getColumn("z").setMaxWidth(40);
//		psmTbl.getColumn("Score").setMinWidth(100);
//		psmTbl.getColumn("Score").setMaxWidth(100);
		
		
		psmTbl.addMouseListener(new java.awt.event.MouseAdapter() {
			
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				queryPsmTableMouseClicked(evt);
			}
		});
	
		psmTbl.addKeyListener(new java.awt.event.KeyAdapter() {
	
			@Override
			public void keyReleased(java.awt.event.KeyEvent evt) {
				queryPsmTableKeyReleased(evt);
			}
		});
		// Only one row is selectable
		psmTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// Enables column control
		psmTbl.setColumnControlVisible(true);
	}
	
	/**
	 * Update the peptide table based on the protein selected.
	 * 
	 * @param evt
	 */
	private void queryProteinTableMouseClicked(MouseEvent evt) {
		// Set the cursor into the wait status.
		this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

		int row = proteinTbl.getSelectedRow();

		// Condition if one row is selected.
		if (row != -1) {

			// Empty tables.
			clearPeptideResultTable();

			String accession = proteinTbl.getValueAt(row, 1).toString();
			currentPeptideHits = experimentResult.getProteinHit(accession).getPeptideHits();
			Set<Entry<String, PeptideHit>> entrySet = experimentResult.getProteinHit(accession).getPeptideHits().entrySet();
			
			// Counter variable
			int i = 1;
			int peptideSpectraCount = 0;
			maxPeptideSpectraCount = 0;
			// Iterate the found peptide results
			for (Entry<String, PeptideHit> entry : entrySet) {
					// Get the peptide hit.
					PeptideHit peptideHit = entry.getValue();
					
					// Get number of spectra
					peptideSpectraCount = peptideHit.getPeptideSpectrumMatches().size();
					
					// Get maximum number of spectra
					maxPeptideSpectraCount= max(maxPeptideSpectraCount, peptideSpectraCount);
					
					// Add rows to the table.
					((DefaultTableModel) peptideTbl.getModel()).addRow(new Object[]{
							i,
							peptideHit.getSequence(),
							peptideHit.getPeptideSpectrumMatches().size()});
					i++;
				}
			
			}
		
		
		// Set the cursor back into the default status.
		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		
		peptideTbl.getColumn("No. Spectra").setCellRenderer(new JSparklinesBarChartTableCellRenderer(PlotOrientation.HORIZONTAL,(double) maxPeptideSpectraCount, true));
		((JSparklinesBarChartTableCellRenderer) peptideTbl.getColumn("No. Spectra").getCellRenderer()).showNumberAndChart(true, 20, UIManager.getFont("Label.font").deriveFont(12f), SwingConstants.LEFT);
	}
		

	/**
	 * @see #queryProteinTableMouseClicked(MouseEvent)
	 */
	private void queryProteinTableKeyReleased(KeyEvent evt) {
		queryProteinTableMouseClicked(null);
	}
	
	/**
	 * @see #queryPeptideTableMouseClicked(MouseEvent)
	 */
	private void queryPeptideTableKeyReleased(KeyEvent evt) {
		queryPeptideTableMouseClicked(null);
	}
	
	/**
	 * @see #queryPsmTableMouseClicked(MouseEvent)
	 */
	private void queryPsmTableKeyReleased(KeyEvent evt) {
		queryPsmTableMouseClicked(null);
	}
	
	/**
	 * Update the peptide table based on the protein selected.
	 * 
	 * @param evt
	 */
	private void queryPeptideTableMouseClicked(MouseEvent evt) {
		// Set the cursor into the wait status.
		this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

		int row = peptideTbl.getSelectedRow();

		// Condition if one row is selected.
		if (row > 0) {

			// Empty tables.
			clearPsmResultTable();

			currentSelectedPeptide = peptideTbl.getValueAt(row, 1).toString();
			Set<Entry<Long, PeptideSpectrumMatch>> entrySet = currentPeptideHits.get(currentSelectedPeptide).getPeptideSpectrumMatches().entrySet();
			
			// Clear the currentpsms map.
			currentPsms.clear();
			
			
			int i = 1;
			// Iterate the found peptide results
			for (Entry<Long, PeptideSpectrumMatch> entry : entrySet) {
				PeptideSpectrumMatch psm = entry.getValue();
				
				
				// Add current PSM to the currentPsms map
				currentPsms.put(i, psm);
				// Add the PSM to the table.
				((DefaultTableModel) psmTbl.getModel()).addRow(new Object[]{
							i,
							currentSelectedPeptide,
							"+"+ psm.getCharge(),
							""});
				i++;
				}
			}
			
		// Set the cursor back into the default status.
		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
	
	/**
	 * Update the spectrum based on the PSM selected.
	 * 
	 * @param evt
	 * @throws IOException 
	 * @throws SQLException 
	 */
	private void queryPsmTableMouseClicked(MouseEvent evt) {
		// Set the cursor into the wait status.
		this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

		int row = psmTbl.getSelectedRow();
		
		// Condition if one row is selected.
		if (row > 0) {

			// Empty spectrum panel.
            while (spectrumJPanel.getComponents().length > 0) {
                spectrumJPanel.remove(0);
            }

			PeptideSpectrumMatch psm = currentPsms.get(Integer.valueOf(psmTbl.getValueAt(row, 0).toString()));
			
			// Calculate fragmentIons
			Fragmentizer insilicoDigester = new Fragmentizer(currentSelectedPeptide, Masses.getMap(), psm.getCharge());
			
			
			// Get the mascot generic file directly from the database.
			try {
				Searchspectrum searchSpectrum = Searchspectrum.findFromSearchSpectrumID(psm.getSpectrumId(), clientFrame.getClient().getConnection());
				MascotGenericFile mgf = SpectrumExtractor.getMascotGenericFile(searchSpectrum.getFk_spectrumid(), clientFrame.getClient().getConnection());
				specPnl = new SpectrumPanel(mgf);
				spectrumJPanel.add(specPnl);
		        spectrumJPanel.validate();
		        spectrumJPanel.repaint();

				addSpectrumAnnotations(insilicoDigester.getFragmentIons());
				
			} catch (SQLException sqe) {
				GeneralExceptionHandler.showSQLErrorDialog(sqe, clientFrame);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// Set the cursor back into the default status.
		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
	
	private void addSpectrumAnnotations(Map<String, FragmentIon[]> fragmentIons) {
		int[][] ionCoverage = new int[currentSelectedPeptide.length() + 1][12];

        currentAnnotations = new Vector<DefaultSpectrumAnnotation>();
       	Set<Entry<String, FragmentIon[]>> entrySet = fragmentIons.entrySet();
       	int i = 0;
       	for (Entry<String, FragmentIon[]> entry : entrySet) {
        		FragmentIon[] ions = entry.getValue();
        		
	            for (FragmentIon ion : ions) {
	                int ionNumber = ion.getNumber();
	                int ionType = ion.getType();
	                double mzValue = ion.getMZ();
	                Color color;
	                if (i % 2 == 0) {
	                    color = Color.BLUE;
	                } else {
	                    color = Color.BLACK;
	                }
	                if (ionType == FragmentIon.A_ION) {
	                    ionCoverage[ionNumber][0]++;
	                }
	                if (ionType == FragmentIon.AH2O_ION) {
	                    ionCoverage[ionNumber][1]++;
	                }
	                if (ionType == FragmentIon.ANH3_ION) {
	                    ionCoverage[ionNumber][2]++;
	                }
	                if (ionType == FragmentIon.B_ION) {
	                    ionCoverage[ionNumber][3]++;
	                }
	                if (ionType == FragmentIon.BH2O_ION) {
	                    ionCoverage[ionNumber][4]++;
	                }
	                if (ionType == FragmentIon.BNH3_ION) {
	                    ionCoverage[ionNumber][5]++;
	                }
	                if (ionType == FragmentIon.C_ION) {
	                    ionCoverage[ionNumber][6]++;
	                }
	                if (ionType == FragmentIon.X_ION) {
	                    ionCoverage[ionNumber][7]++;
	                }
	                if (ionType == FragmentIon.Y_ION) {
	                    ionCoverage[ionNumber][8]++;
	                }
	                if (ionType == FragmentIon.YH2O_ION) {
	                    ionCoverage[ionNumber][9]++;
	                }
	                if (ionType == FragmentIon.YNH3_ION) {
	                    ionCoverage[ionNumber][10]++;
	                }
	                if (ionType == FragmentIon.Z_ION) {
	                    ionCoverage[ionNumber][11]++;
	                }
	                // Use standard ion type names, such as y5++
	                String ionDesc = ion.getLetter();
	                if (ionNumber > 0) {
	                    ionDesc += ionNumber;
	                }
	                if (ion.getCharge() > 1) {
	                    for (int j = 0; j < ion.getCharge(); j++) {
	                        ionDesc += "+";
	                    }
	                }
	                // TODO: Get fragment ion mass error from db or settings file!
	                currentAnnotations.add(new DefaultSpectrumAnnotation(mzValue, 0.5, color, ionDesc));
	            }
	            i++;
       	}
        //allAnnotations.put(currentSelectedPeptide, currentAnnotations);
        
       	if(currentAnnotations.size() > 0 ){
       		specPnl.setAnnotations(filterAnnotations(currentAnnotations));
       		specPnl.validate();
    		specPnl.repaint();
       	}

	}
	
	/**
	 * This method constructs the spectrum filter panel.
	 */
	private void constructSpectrumFilterPanel(){
		
			spectrumFilterPanel = new JPanel(new FormLayout("5dlu, p, 5dlu", "5dlu, p, 2dlu, p, 2dlu, p, 5dlu, p, 5dlu, p, 2dlu, p, 2dlu, p, 5dlu, p, 5dlu, p, 2dlu, p, 5dlu, p, 5dlu, p, 2dlu, p, 2dlu, p, 5dlu, p, 5dlu, p, 5dlu"));
			aIonsJCheckBox = new JCheckBox();
		  	aIonsJCheckBox.setSelected(false);
	        aIonsJCheckBox.setText("a");
	        aIonsJCheckBox.setToolTipText("Show a-ions");
	        aIonsJCheckBox.setMaximumSize(new Dimension(39, 23));
	        aIonsJCheckBox.setMinimumSize(new Dimension(39, 23));
	        aIonsJCheckBox.setPreferredSize(new Dimension(39, 23));
	        aIonsJCheckBox.addActionListener(new ActionListener() {

	            public void actionPerformed(ActionEvent evt) {
	            	updateFilteredAnnotations();
	            }
	        });
	        
	        bIonsJCheckBox = new JCheckBox();
	        bIonsJCheckBox.setSelected(true);
	        bIonsJCheckBox.setText("b");
	        bIonsJCheckBox.setToolTipText("Show b-ions");
	        bIonsJCheckBox.setMaximumSize(new Dimension(39, 23));
	        bIonsJCheckBox.setMinimumSize(new Dimension(39, 23));
	        bIonsJCheckBox.setPreferredSize(new Dimension(39, 23));
	        bIonsJCheckBox.addActionListener(new ActionListener() {

	            public void actionPerformed(ActionEvent evt) {
	            	updateFilteredAnnotations();
	            }
	        });
	        
	        cIonsJCheckBox = new JCheckBox();
	        cIonsJCheckBox.setSelected(false);
	        cIonsJCheckBox.setText("c");
	        cIonsJCheckBox.setToolTipText("Show c-ions");
	        cIonsJCheckBox.setMaximumSize(new Dimension(39, 23));
	        cIonsJCheckBox.setMinimumSize(new Dimension(39, 23));
	        cIonsJCheckBox.setPreferredSize(new Dimension(39, 23));
	        cIonsJCheckBox.addActionListener(new ActionListener() {

	            public void actionPerformed(ActionEvent evt) {
	            	updateFilteredAnnotations();
	            }
	        });
	        
	        yIonsJCheckBox = new JCheckBox();
	        yIonsJCheckBox.setSelected(true);
	        yIonsJCheckBox.setText("y");
	        yIonsJCheckBox.setToolTipText("Show y-ions");
	        yIonsJCheckBox.setMaximumSize(new Dimension(39, 23));
	        yIonsJCheckBox.setMinimumSize(new Dimension(39, 23));
	        yIonsJCheckBox.setPreferredSize(new Dimension(39, 23));
	        yIonsJCheckBox.addActionListener(new ActionListener() {

	            public void actionPerformed(ActionEvent evt) {
	            	updateFilteredAnnotations();
	            }
	        });
	        
	        xIonsJCheckBox = new JCheckBox();
	        xIonsJCheckBox.setSelected(false);
	        xIonsJCheckBox.setText("x");
	        xIonsJCheckBox.setToolTipText("Show x-ions");
	        xIonsJCheckBox.setMaximumSize(new Dimension(39, 23));
	        xIonsJCheckBox.setMinimumSize(new Dimension(39, 23));
	        xIonsJCheckBox.setPreferredSize(new Dimension(39, 23));
	        xIonsJCheckBox.addActionListener(new ActionListener() {

	            public void actionPerformed(ActionEvent evt) {
	            	updateFilteredAnnotations();
	            }
	        });
	        
	        zIonsJCheckBox = new JCheckBox();
	        zIonsJCheckBox.setSelected(false);
	        zIonsJCheckBox.setText("z");
	        zIonsJCheckBox.setToolTipText("Show z-ions");
	        zIonsJCheckBox.setMaximumSize(new Dimension(39, 23));
	        zIonsJCheckBox.setMinimumSize(new Dimension(39, 23));
	        zIonsJCheckBox.setPreferredSize(new Dimension(39, 23));
	        zIonsJCheckBox.addActionListener(new ActionListener() {

	            public void actionPerformed(ActionEvent evt) {
	            	updateFilteredAnnotations();
	            }
	        });
	        
	        waterLossJCheckBox = new JCheckBox();
	        waterLossJCheckBox.setSelected(false);
	        waterLossJCheckBox.setText("°");
	        waterLossJCheckBox.setToolTipText("Show H20-ions");
	        waterLossJCheckBox.setMaximumSize(new Dimension(39, 23));
	        waterLossJCheckBox.setMinimumSize(new Dimension(39, 23));
	        waterLossJCheckBox.setPreferredSize(new Dimension(39, 23));
	        waterLossJCheckBox.addActionListener(new ActionListener() {

	            public void actionPerformed(ActionEvent evt) {
	            	updateFilteredAnnotations();
	            }
	        });
	        
	        ammoniumLossJCheckBox = new JCheckBox();
	        ammoniumLossJCheckBox.setSelected(false);
	        ammoniumLossJCheckBox.setText("*");
	        ammoniumLossJCheckBox.setToolTipText("Show NH3-ions");
	        ammoniumLossJCheckBox.setMaximumSize(new Dimension(39, 23));
	        ammoniumLossJCheckBox.setMinimumSize(new Dimension(39, 23));
	        ammoniumLossJCheckBox.setPreferredSize(new Dimension(39, 23));
	        ammoniumLossJCheckBox.addActionListener(new ActionListener() {

	            public void actionPerformed(ActionEvent evt) {
	            	updateFilteredAnnotations();
	            }
	        });
	        
	        chargeOneJCheckBox = new JCheckBox();
	        chargeOneJCheckBox.setSelected(true);
	        chargeOneJCheckBox.setText("+");
	        chargeOneJCheckBox.setToolTipText("Show ions with charge 1");
	        chargeOneJCheckBox.setMaximumSize(new Dimension(39, 23));
	        chargeOneJCheckBox.setMinimumSize(new Dimension(39, 23));
	        chargeOneJCheckBox.setPreferredSize(new Dimension(39, 23));
	        chargeOneJCheckBox.addActionListener(new ActionListener() {

	            public void actionPerformed(ActionEvent evt) {
	            	updateFilteredAnnotations();
	            }
	        });
	        	
	        chargeTwoJCheckBox = new JCheckBox();
	        chargeTwoJCheckBox.setSelected(false);
	        chargeTwoJCheckBox.setText("++");
	        chargeTwoJCheckBox.setToolTipText("Show ions with charge 2");
	        chargeTwoJCheckBox.setMaximumSize(new java.awt.Dimension(39, 23));
	        chargeTwoJCheckBox.setMinimumSize(new java.awt.Dimension(39, 23));
	        chargeTwoJCheckBox.setPreferredSize(new java.awt.Dimension(39, 23));
	        chargeTwoJCheckBox.addActionListener(new java.awt.event.ActionListener() {

	            public void actionPerformed(ActionEvent evt) {
	            	updateFilteredAnnotations();
	            }
	        });
	        
	        chargeOverTwoJCheckBox = new JCheckBox();
	        chargeOverTwoJCheckBox.setSelected(false);
	        chargeOverTwoJCheckBox.setText(">2");
	        chargeOverTwoJCheckBox.setToolTipText("Show ions with charge >2");
	        chargeOverTwoJCheckBox.addActionListener(new java.awt.event.ActionListener() {

	            public void actionPerformed(ActionEvent evt) {
	            	updateFilteredAnnotations();
	            }
	        });
	        
	        precursorJCheckBox = new JCheckBox();
	        precursorJCheckBox.setSelected(true);
	        precursorJCheckBox.setText("MH");
	        precursorJCheckBox.setToolTipText("Show precursor ions");
	        precursorJCheckBox.addActionListener(new java.awt.event.ActionListener() {

	            public void actionPerformed(ActionEvent evt) {
	            	updateFilteredAnnotations();
	            }
	        });
	        
	        spectrumFilterPanel.add(aIonsJCheckBox, CC.xy(2, 2));
	        spectrumFilterPanel.add(bIonsJCheckBox, CC.xy(2, 4));
	        spectrumFilterPanel.add(cIonsJCheckBox, CC.xy(2, 6));
	        spectrumFilterPanel.add(new JSeparator(JSeparator.HORIZONTAL), CC.xy(2, 8));

	        spectrumFilterPanel.add(xIonsJCheckBox, CC.xy(2, 10));
	        spectrumFilterPanel.add(yIonsJCheckBox, CC.xy(2, 12));
	        spectrumFilterPanel.add(zIonsJCheckBox, CC.xy(2, 14));
	        spectrumFilterPanel.add(new JSeparator(JSeparator.HORIZONTAL), CC.xy(2, 16));
	        spectrumFilterPanel.add(waterLossJCheckBox, CC.xy(2, 18));
	        spectrumFilterPanel.add(ammoniumLossJCheckBox, CC.xy(2, 20));
	        
	        spectrumFilterPanel.add(new JSeparator(JSeparator.HORIZONTAL), CC.xy(2, 22));
	        spectrumFilterPanel.add(chargeOneJCheckBox, CC.xy(2, 24));
	        spectrumFilterPanel.add(chargeTwoJCheckBox, CC.xy(2, 26));
	        spectrumFilterPanel.add(chargeOverTwoJCheckBox, CC.xy(2, 28));
	        
	        spectrumFilterPanel.add(new JSeparator(JSeparator.HORIZONTAL), CC.xy(2, 30));
	        spectrumFilterPanel.add(precursorJCheckBox, CC.xy(2, 32));
	}
	
	/**
	 * This method updates the filtered annotations due to the respective selected checkboxes.
	 */
	private void updateFilteredAnnotations(){
        specPnl.setAnnotations(filterAnnotations(currentAnnotations));
        specPnl.validate();
        specPnl.repaint();
	}
	
	 /**
     * This method filters the annotations.
     *
     * @param annotations the annotations to be filtered
     * @return the filtered annotations
     */
    private Vector<DefaultSpectrumAnnotation> filterAnnotations(Vector<DefaultSpectrumAnnotation> annotations) {

        Vector<DefaultSpectrumAnnotation> filteredAnnotations = new Vector<DefaultSpectrumAnnotation>();

        for (int i = 0; i < annotations.size(); i++) {
            String currentLabel = annotations.get(i).getLabel();
            boolean useAnnotation = true;
            // check ion type
            if (currentLabel.lastIndexOf("a") != -1) {
                if (!aIonsJCheckBox.isSelected()) {
                    useAnnotation = false;
                }
            } else if (currentLabel.lastIndexOf("b") != -1) {
                if (!bIonsJCheckBox.isSelected()) {
                    useAnnotation = false;
                }
            } else if (currentLabel.lastIndexOf("c") != -1) {
                if (!cIonsJCheckBox.isSelected()) {
                    useAnnotation = false;
                }
            } else if (currentLabel.lastIndexOf("x") != -1) {
                if (!xIonsJCheckBox.isSelected()) {
                    useAnnotation = false;
                }
            } else if (currentLabel.lastIndexOf("y") != -1) {
                if (!yIonsJCheckBox.isSelected()) {
                    useAnnotation = false;
                }
            } else if (currentLabel.lastIndexOf("z") != -1) {
                if (!zIonsJCheckBox.isSelected()) {
                    useAnnotation = false;
                }
            } else if (currentLabel.lastIndexOf("M") != -1) {
                if (!precursorJCheckBox.isSelected()) {
                    useAnnotation = false;
                }
            }

            // check ion charge + ammonium and water-loss
            if (useAnnotation) {
                if (currentLabel.lastIndexOf("+") == -1) {
                    if (!chargeOneJCheckBox.isSelected()) {
                        useAnnotation = false;
                    }
                } else if (currentLabel.lastIndexOf("+++") != -1) {
                    if (!chargeOverTwoJCheckBox.isSelected()) {
                        useAnnotation = false;
                    }
                } else if (currentLabel.lastIndexOf("++") != -1) {
                    if (!chargeTwoJCheckBox.isSelected()) {
                        useAnnotation = false;
                    }
                }
                
                if (currentLabel.lastIndexOf("*") != -1) {
                    if (!ammoniumLossJCheckBox.isSelected()) {
                        useAnnotation = false;
                    }
                } else if (currentLabel.lastIndexOf("°") != -1) {
                    if (!waterLossJCheckBox.isSelected()) {
                        useAnnotation = false;
                    }
                }
            }
            
            // If not used, don't add the annotation.
            if (useAnnotation) {
                filteredAnnotations.add(annotations.get(i));
            }
        }

        return filteredAnnotations;
    }


	/**
	 * Clears the peptide result table.
	 */
	private void clearPeptideResultTable(){
		// Remove peptides from all result tables        	 
		while (peptideTbl.getRowCount() > 0) {
			((DefaultTableModel) peptideTbl.getModel()).removeRow(0);
		}
	}
	
	/**
	 * Clears the PSM result table.
	 */
	private void clearPsmResultTable(){
		// Remove PSMs from all result tables        	 
		while (psmTbl.getRowCount() > 0) {
			((DefaultTableModel) psmTbl.getModel()).removeRow(0);
		}
	}
	
	/**
	 * This method updates the protein results table.
	 */
	private void updateProteinResultsTable(){
		int i = 1;
		maxSpectralCount = 0;
		maxPeptideCount = 0;
		// Fill the protein results table.
		if (experimentResult != null) {
			
			// Iterate the found protein results
			for (Entry entry : experimentResult.getProteinHits().entrySet()){
				
				// Get the protein hit.
				ProteinHit proteinHit = (ProteinHit) entry.getValue();
				
				// Determine the number of containing peptide hits.
				int peptideCount = proteinHit.getPeptideCount();
				
				// Determine the number of containing psm hits 
				int spectralCount =  proteinHit.getSpecCount();
				
				// Remember maximum number of peptides
				maxPeptideCount= max(maxPeptideCount, peptideCount);
				
				// Remember maximum number of spectra
				maxSpectralCount= max(maxSpectralCount, spectralCount);
				
				// Add row to the table
				((DefaultTableModel) proteinTbl.getModel()).addRow(new Object[]{
						i ,
						proteinHit.getAccession(),
						proteinHit.getDescription(),
						peptideCount, 
						spectralCount});
				i++;
			}
			proteinTbl.getColumn("Spectral Count").setCellRenderer(new JSparklinesBarChartTableCellRenderer(PlotOrientation.HORIZONTAL,(double) maxSpectralCount, true));
			((JSparklinesBarChartTableCellRenderer) proteinTbl.getColumn("Spectral Count").getCellRenderer()).showNumberAndChart(true, 20, UIManager.getFont("Label.font").deriveFont(12f), SwingConstants.LEFT);
			
			proteinTbl.getColumn("Peptide Count").setCellRenderer(new JSparklinesBarChartTableCellRenderer(PlotOrientation.HORIZONTAL, (double) maxPeptideCount, true));
			((JSparklinesBarChartTableCellRenderer) proteinTbl.getColumn("Peptide Count").getCellRenderer()).showNumberAndChart(true, 20, UIManager.getFont("Label.font").deriveFont(12f), SwingConstants.LEFT);
		}
	}

}
