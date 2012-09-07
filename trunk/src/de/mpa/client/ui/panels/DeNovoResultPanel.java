package de.mpa.client.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import no.uib.jsparklines.renderers.JSparklinesBarChartTableCellRenderer;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;
import org.jfree.chart.plot.PlotOrientation;

import com.compomics.util.experiment.biology.Ion;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.biology.ions.PeptideFragmentIon;
import com.compomics.util.experiment.identification.NeutralLossesMap;
import com.compomics.util.experiment.identification.SpectrumAnnotator;
import com.compomics.util.experiment.identification.matches.IonMatch;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.massspectrometry.Charge;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import com.compomics.util.experiment.massspectrometry.Precursor;
import com.compomics.util.gui.spectrum.SpectrumPanel;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Client;
import de.mpa.client.model.denovo.DenovoSearchResult;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.PanelConfig;
import de.mpa.client.ui.TableConfig;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.db.accessor.Pepnovohit;
import de.mpa.io.MascotGenericFile;

public class DeNovoResultPanel extends JPanel {

	private ClientFrame clientFrame;
	private Client client;
	private JXTable spectraTbl;
	protected Object filePnl;
	private DenovoSearchResult denovoSearchResult;
	private Map<Integer, Pepnovohit> denovoHits = new HashMap<Integer, Pepnovohit>();
	private SpectrumPanel spectrumPanel;
    private JPanel spectrumJPanel;
	private JXTable solutionsTbl;
	private JButton getResultsBtn;
	private MascotGenericFile mgf;
	
	/**
	 * The DeNovoResultPanel.
	 * @param clientFrame The client frame.
	 */
	public DeNovoResultPanel() {
		this.clientFrame = ClientFrame.getInstance();
		this.client = Client.getInstance();
		initComponents();
	}
	
	/**
	 * Initialize the components.
	 */
	private void initComponents() {
		CellConstraints cc = new CellConstraints();
		this.setLayout(new FormLayout("5dlu, p:g, 5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu, f:p:g, 5dlu"));
		
        // Build the spectrum overview panel
        JPanel spectrumOverviewPnl = new JPanel(new BorderLayout());
        spectrumJPanel = new JPanel();
        spectrumJPanel.setLayout(new BorderLayout());
        spectrumJPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        spectrumJPanel.add(new SpectrumPanel(new double[]{0.0, 100.0}, new double[]{100.0, 0.0}, 0.0, "", ""));
        spectrumJPanel.setPreferredSize(new Dimension(100, 350));
        spectrumOverviewPnl.add(spectrumJPanel);

		JXTitledPanel specTtlPnl = PanelConfig.createTitledPanel("Spectrum Viewer", spectrumOverviewPnl);
		
		// Setup the tables
		setupSpectraTableProperties();
		setupDenovoTableProperties();

		getResultsBtn = new JButton("Get Results   ", IconConstants.REFRESH_DB_ICON);
		getResultsBtn.setRolloverIcon(IconConstants.REFRESH_DB_ROLLOVER_ICON);
		getResultsBtn.setPressedIcon(IconConstants.REFRESH_DB_PRESSED_ICON);
		
		getResultsBtn.setEnabled(false);

		getResultsBtn.setPreferredSize(new Dimension(getResultsBtn.getPreferredSize().width, 20));
		getResultsBtn.setFocusPainted(false);
		getResultsBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
					getResultsButtonPressed();
			}
		});
		
		// Spectra panel.
		JPanel spectraPnl = new JPanel();
		spectraPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));
		
		JXTitledPanel specHitsTtlPnl = PanelConfig.createTitledPanel("Query Spectra", spectraPnl);
		JScrollPane spectraTblScp = new JScrollPane(spectraTbl);
		spectraTblScp.setPreferredSize(new Dimension(400, 210));
		
		spectraTblScp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		spectraTblScp.setToolTipText("Select spectra");
		spectraPnl.add(spectraTblScp,cc.xy(2, 2));
		
		JScrollPane solutionsTblScp = new JScrollPane();
		solutionsTblScp.setViewportView(solutionsTbl);
		solutionsTblScp.setPreferredSize(new Dimension(550, 210));
		
		final JPanel solutionsPnl = new JPanel();
		solutionsPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));
		solutionsPnl.add(solutionsTblScp, cc.xy(2, 2));
		
		JXTitledPanel solutionsTtlPnl = PanelConfig.createTitledPanel("De Novo Hits", solutionsPnl);
		
		// Peptide and Psm Panel
		JPanel bottomPnl = new JPanel(new FormLayout("p:g","f:p:g,f:p:g"));
		
		bottomPnl.add(specHitsTtlPnl, cc.xy(1, 1));
		bottomPnl.add(solutionsTtlPnl, cc.xy(1, 2));
		

		specTtlPnl.setRightDecoration(getResultsBtn);
	    this.add(specTtlPnl, cc.xyw(2, 2, 3));
	    this.add(specHitsTtlPnl, cc.xy(2, 4));
	    this.add(solutionsTtlPnl, cc.xy(4, 4));
	}

	/**
	 * Clears the hit result table.
	 */
	private void clearDenovoHitResultTable() {
		// Remove solutions from the table.        	 
		while (solutionsTbl.getRowCount() > 0) {
			((DefaultTableModel) solutionsTbl.getModel()).removeRow(0);
		}
	}	
	
	/**
	 * Clears the spectrum table.
	 */
	private void clearSpectrumTable() {
		// Remove spectra from all result tables        	 
		while (spectraTbl.getRowCount() > 0) {
			((DefaultTableModel) spectraTbl.getModel()).removeRow(0);
		}
	}
	

	
    /**
     * This method prepares the denovo hits table.
     */
    private void setupDenovoTableProperties() {
    	solutionsTbl = new JXTable(new DefaultTableModel() {
            // instance initializer block
            {
                setColumnIdentifiers(new Object[]{"#", "Peptide", "Score", "N-Gap", "C-Gap", "m/z", "Charge"});
            }

            public boolean isCellEditable(int row, int col) {
                return false;
            }

            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                    case 6:
                        return Integer.class;
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                        return Double.class;
                    default:
                        return String.class;
                }
            }

        });

        // JSparklines for Scoring
    	solutionsTbl.getColumn("Score").setCellRenderer(new JSparklinesBarChartTableCellRenderer(PlotOrientation.HORIZONTAL, 100.0, new Color(110, 196, 97)));
        ((JSparklinesBarChartTableCellRenderer) solutionsTbl.getColumn("Score").getCellRenderer()).showNumberAndChart(true, 50, new Font("Arial", Font.PLAIN, 12), 0);

        TableConfig.setColumnWidths(solutionsTbl, new double[]{1, 10, 8, 3, 3, 4, 3});
        TableColumnModel tcm = solutionsTbl.getColumnModel();
        tcm.getColumn(0).setCellRenderer(new TableConfig.CustomTableCellRenderer(SwingConstants.RIGHT));

        // Sort the peptide table by the number of peptide hits
        solutionsTbl.setAutoCreateRowSorter(true);

        // register list selection listener
        solutionsTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                refreshSpectrumPanel();
                updateAnnotations();
            }
        });

        // Single selection only
        solutionsTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        solutionsTbl.setSelectionBackground(new Color(130, 207, 250));

        // Add nice striping effect
        solutionsTbl.addHighlighter(TableConfig.getSimpleStriping());

        // Enables column control
        solutionsTbl.setColumnControlVisible(true);
    }

    /**
     * This method sets the spectra table up.
     */
    private void setupSpectraTableProperties() {
        // Query table
        spectraTbl = new JXTable(new DefaultTableModel() {
            // instance initializer block
            {
                setColumnIdentifiers(new Object[]{"#", "Spectrum Title"});
            }

            public boolean isCellEditable(int row, int col) {
                return false;
            }

            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return Integer.class;
                    case 1:
                        return String.class;
                    default:
                        return String.class;
                }
            }
        });

        TableConfig.setColumnWidths(spectraTbl, new double[]{1, 10});
        TableColumnModel tcm = spectraTbl.getColumnModel();
        tcm.getColumn(0).setCellRenderer(new TableConfig.CustomTableCellRenderer(SwingConstants.RIGHT));

        // Sort the peptide table by the number of peptide hits
//        spectraTbl.setAutoCreateRowSorter(true);
//        spectraTbl.getRowSorter().toggleSortOrder(0);

        // register list selection listener
        spectraTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                refreshSpectrumPanel();
                refreshDenovoTable();
            }
        });

        // Single selection only
        spectraTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        spectraTbl.setSelectionBackground(new Color(130, 207, 250));

        // Add nice striping effect
        spectraTbl.addHighlighter(TableConfig.getSimpleStriping());

        // Enables column control
        spectraTbl.setColumnControlVisible(true);
    }

    /**
     * Updates the spectrum panel.
     */
    private void refreshSpectrumPanel() {
        
        int row = spectraTbl.getSelectedRow();
        
        if (row != -1) {
        	// TODO: Exception handling!
        	String title = (String) spectraTbl.getValueAt(row, spectraTbl.convertColumnIndexToView(1));
        	try {
				mgf = Client.getInstance().getSpectrumFromSearchSpectrumID(denovoSearchResult.getSpectrumIdfromTitle(title));
				// convert the spectrum
	            ArrayList<Charge> precursorCharges = new ArrayList<Charge>();
	            precursorCharges.add(new Charge(mgf.getCharge(), Charge.PLUS));
	            HashMap<Double,Peak> peakMap = new HashMap<Double, Peak>();
	            HashMap<Double,Double> mgfPeaks = mgf.getPeaks();
	            Iterator<Double> iterator = mgfPeaks.keySet().iterator();
	            while (iterator.hasNext()) {
	                Double mass = iterator.next();
	                peakMap.put(mass, new Peak(mass, mgfPeaks.get(mass)));
	            }
	            MSnSpectrum currentSpectrum = new MSnSpectrum(2, new Precursor(0.0, mgf.getPrecursorMZ(), precursorCharges), mgf.getTitle(), peakMap, mgf.getFilename());

	            spectrumPanel = new SpectrumPanel(
	                            currentSpectrum.getMzValuesAsArray(), currentSpectrum.getIntensityValuesAsArray(),
	                            mgf.getPrecursorMZ(), mgf.getCharge() + "+",
	                            "", 40, false, false, false, 2, false);
	            
	            spectrumJPanel.removeAll();
	            spectrumJPanel.add(spectrumPanel);
	            spectrumJPanel.revalidate();
	            spectrumJPanel.repaint();
				
			} catch (SQLException e) {
				JXErrorPane.showDialog(ClientFrame.getInstance(),
						new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
			}
        }
    }

    /**
     * Updates the annotations.
     */
    private void updateAnnotations() {
        int row = solutionsTbl.getSelectedRow();
        if (row != -1) {
            Pepnovohit hit = denovoHits.get(solutionsTbl.convertRowIndexToModel(row)); // @TODO: this only works if the table is no sorted!!
            if (hit != null) {
                addAnnotations(hit);
            }
        }
    }
    
    /**
     * Adds spectrum annotations based on the selected de novo hit.
     * 
     * @param hit 
     */
    private void addAnnotations(Pepnovohit hit) {
        
        int row = spectraTbl.getSelectedRow();
        
        if (row != -1) {
            // convert the spectrum
            ArrayList<Charge> precursorCharges = new ArrayList<Charge>();
            precursorCharges.add(new Charge(mgf.getCharge(), Charge.PLUS));
            HashMap<Double,Peak> peakMap = new HashMap<Double, Peak>();
            HashMap<Double,Double> mgfPeaks = mgf.getPeaks();
            Iterator<Double> iterator = mgfPeaks.keySet().iterator();
            while (iterator.hasNext()) {
                Double mass = iterator.next();
                peakMap.put(mass, new Peak(mass, mgfPeaks.get(mass)));
            }
            MSnSpectrum currentSpectrum = new MSnSpectrum(2, new Precursor(0.0, mgf.getPrecursorMZ(), precursorCharges), "no title", peakMap, "no filename");

            // add the annotations
            SpectrumAnnotator spectrumAnnotator = new SpectrumAnnotator();

            HashMap<Ion.IonType, ArrayList<Integer>> ionTypes = new HashMap<Ion.IonType, ArrayList<Integer>>();
            ArrayList<Integer> ions = new ArrayList<Integer>();
            ions.add(PeptideFragmentIon.B_ION); // @TODO: ion types should not be hard-coded but rather based on the annotation menu bar!!
            ions.add(PeptideFragmentIon.Y_ION);
            ionTypes.put(Ion.IonType.PEPTIDE_FRAGMENT_ION, ions);

            ArrayList<Integer> charges = new ArrayList<Integer>();
            charges.add(1); // @TODO: charge should not be hard-coded but rather be based on the user selection in the annotation menu bar!!

            // cut out non-alphabetic symbols
            String sequence = hit.getSequence().replaceAll("[^a-zA-Z]", "");
            Peptide currentPeptide = new Peptide(sequence, new ArrayList<String>(), new ArrayList<ModificationMatch>()); // @TODO: add PTMs!!

            ArrayList<IonMatch> annotations = spectrumAnnotator.getSpectrumAnnotation(
                            ionTypes,
                            new NeutralLossesMap(), // @TODO: extend this to support neutral losses in the annotations
                            charges,
                            (int) hit.getCharge(),
                            currentSpectrum, currentPeptide,
                            currentSpectrum.getIntensityLimit(0.0), // @TODO: should not be hard-coded?
                            0.5, // @TODO: get fragment ion mass error from the search parameters! 
                            false);

            spectrumPanel.setAnnotations(SpectrumAnnotator.getSpectrumAnnotation(annotations));
            
            // add de novo sequencing
            spectrumPanel.addAutomaticDeNovoSequencing(currentPeptide, annotations, 
                    PeptideFragmentIon.B_ION, // @TODO: choose the forward fragment ion type from the annotation menu bar!!
                    PeptideFragmentIon.Y_ION, // @TODO: choose the reverse fragment ion type from the annotation menu bar!!
                    1, // @TODO: get the charge from the annotation menu bar!!
                    true, // @TODO: get if forward ions are to be shown or not from the annotation menu bar!!
                    true); // @TODO: get if reverse ions are to be shown or not from the annotation menu bar!!
        }
    }

    /**
     * Update the denovo hit table based on the spectrum selected via mouse click.
     */
    private void refreshDenovoTable() {
        // Set the cursor into the wait status.
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));

        TableConfig.clearTable(solutionsTbl);

        int row = spectraTbl.getSelectedRow();
        
        // Condition if one row is selected.
        if (row != -1) {

            String title = spectraTbl.getValueAt(row, spectraTbl.convertColumnIndexToView(1)).toString();
            Map<String, List<Pepnovohit>> hits = denovoSearchResult.getDenovoHits();
            if (hits.containsKey(title)) {
                List<Pepnovohit> pepnovoList = hits.get(title);

                for (int i = 0; i < pepnovoList.size(); i++) {
                	Pepnovohit hit = pepnovoList.get(i);
                    if (hit != null) {
                        denovoHits.put(i, hit);
                        ((DefaultTableModel) solutionsTbl.getModel()).addRow(new Object[]{
                                i + 1,
                                hit.getSequence(),
                                hit.getPnvscore().doubleValue(),
                                hit.getN_gap().doubleValue(),
                                hit.getC_gap().doubleValue(),
                                hit.getPrecursor_mh().doubleValue(),
                                hit.getCharge()
                        });
                    }
                }
            }
            solutionsTbl.getSelectionModel().setSelectionInterval(0, 0);
        }
        // Set the cursor back into the default status.
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * Method invoked when the Get Results button is pressed.
     */
    protected void getResultsButtonPressed() {
		ResultsTask resultsTask = new ResultsTask();
		resultsTask.execute();
		
	}
	
    /**
     * Task for fetching the results from the database.
     * @author T. Muth
     *
     */
	private class ResultsTask extends SwingWorker {

		protected Object doInBackground() throws Exception {
			try {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				// Fetch the database search result.
				denovoSearchResult = client.getDenovoSearchResult(clientFrame.getProjectPanel().getCurrentProjectContent(), clientFrame.getProjectPanel().getCurrentExperimentContent());
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			finished();
			return 0;
		}

		/**
		 * Continues when the results retrieval has finished.
		 */
		public void finished() {
			// Check if any results are stored in the database.
			if(denovoSearchResult.getDenovoHits().size() > 0){
				refreshSpectraTable();
				refreshDenovoTable();
			}
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}
    /**
     * This method updates the spectra table.
     */
    private void refreshSpectraTable() {
        TableConfig.clearTable(spectraTbl);
        Set<String> titleSet = denovoSearchResult.getDenovoHits().keySet();
        int count = 1;
        if (titleSet != null) {
	        for (String title : titleSet) {
	        	((DefaultTableModel) spectraTbl.getModel()).addRow(new Object[]{
	                    count++,
	                    title,
	            });
			}
            spectraTbl.getSelectionModel().setSelectionInterval(0, 0);
        }
    }

	
	/**
	 * This method sets the enabled state of the get results button.
	 */
	public void setResultsButtonEnabled(boolean enabled) {
		getResultsBtn.setEnabled(enabled);
	}

}
