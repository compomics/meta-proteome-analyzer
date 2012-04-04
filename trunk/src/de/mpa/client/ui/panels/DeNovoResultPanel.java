package de.mpa.client.ui.panels;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import no.uib.jsparklines.renderers.JSparklinesBarChartTableCellRenderer;

import org.jdesktop.swingx.JXTable;
import org.jfree.chart.plot.PlotOrientation;

import com.compomics.util.gui.spectrum.SpectrumPanel;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.model.denovo.DenovoSearchResult;
import de.mpa.client.model.denovo.DenovoTagHit;
import de.mpa.client.model.denovo.SpectrumHit;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.CustomTableCellRenderer;
import de.mpa.db.accessor.Pepnovohit;
import de.mpa.db.accessor.Searchspectrum;
import de.mpa.db.extractor.SpectrumExtractor;
import de.mpa.io.MascotGenericFile;

public class DeNovoResultPanel extends JPanel {

	private ClientFrame clientFrame;
	private JXTable spectraTbl;
	private JXTable peptideTagsTbl;
	protected Object filePnl;
	private DenovoSearchResult denovoSearchResult;
	private JPanel spectrumOverviewPnl;
	private JPanel spectrumJPanel;
	private JXTable solutionsTbl;
	private SpectrumPanel specPnl;
	private HashMap<Integer, SpectrumHit> currentSpectrumHits = new HashMap<Integer, SpectrumHit>();
	
	/**
	 * The DeNovoResultPanel.
	 * @param clientFrame The client frame.
	 */
	public DeNovoResultPanel(ClientFrame clientFrame) {
		this.clientFrame = clientFrame;
		initComponents();
	}
	
	/**
	 * Initialize the components.
	 */
	private void initComponents() {
		CellConstraints cc = new CellConstraints();
		JScrollPane gappedPeptidesTblScp = new JScrollPane();
		this.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu, f:p:g, 5dlu"));
		
		// Choose your spectra
		JPanel peptideTagsPnl = new JPanel();
		peptideTagsPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, p, 5dlu, f:p:g, 5dlu,"));
		peptideTagsPnl.setBorder(BorderFactory.createTitledBorder("Peptide Tags"));

		// Setup the tables
		setupDenovoSearchResultTableProperties();
		setupSolutionsTableProperties();

		peptideTagsPnl.setOpaque(false);
		gappedPeptidesTblScp.setViewportView(peptideTagsTbl);
		gappedPeptidesTblScp.setPreferredSize(new Dimension(800, 200));

		JButton updateDnBtn = new JButton("Get Results");
		JPanel topPnl = new JPanel(new FormLayout("p", "p"));
		topPnl.add(updateDnBtn, cc.xy(1, 1));
		updateDnBtn.setPreferredSize(new Dimension(150, 20));

		updateDnBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				denovoSearchResult = clientFrame.getClient().getDenovoSearchResult(clientFrame.getProjectPnl().getCurrentProjContent(), clientFrame.getProjectPnl().getCurrentExperimentContent());
				
				// Check if any results are stored in the database.
				if(denovoSearchResult.getTagHits().size() > 0){
					clearDenovoTagResultTable();
					updatePeptideTagsTable();
					peptideTagsTbl.getSelectionModel().setSelectionInterval(0, 0);
					queryPeptideTagsTableMouseClicked(null);
					spectraTbl.getSelectionModel().setSelectionInterval(0, 0);
					querySpectrumTableMouseClicked(null);
					solutionsTbl.getSelectionModel().setSelectionInterval(0, 0);
				}
				// TODO: Signalize the user that there are no results.
			}
		});

		peptideTagsPnl.add(topPnl, cc.xy(2, 2));
		peptideTagsPnl.add(gappedPeptidesTblScp, cc.xy(2, 4));

		// Spectra panel.
		JPanel spectraPnl = new JPanel();
		spectraPnl.setLayout(new FormLayout("5dlu, f:p:g, 5dlu", "5dlu, f:p, 5dlu"));
		spectraPnl.setBorder(new TitledBorder("Spectrum Hits"));

		JScrollPane spectraTblScp = new JScrollPane(spectraTbl);
		spectraTblScp.setPreferredSize(new Dimension(400, 150));
		
		spectraTblScp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		spectraTblScp.setToolTipText("Select spectra");
		spectraPnl.add(spectraTblScp,cc.xy(2, 2));
		
		JScrollPane solutionsTblScp = new JScrollPane();
		solutionsTblScp.setViewportView(solutionsTbl);
		solutionsTblScp.setPreferredSize(new Dimension(400, 150));
		
		final JPanel solutionsPnl = new JPanel();
		solutionsPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p, 5dlu"));
		solutionsPnl.setBorder(BorderFactory.createTitledBorder("De-novo Solutions"));
		solutionsPnl.add(solutionsTblScp, cc.xy(2, 2));

		// Peptide and Psm Panel
		JPanel bottomPnl = new JPanel(new FormLayout("p:g","f:p:g,f:p:g"));
		
		bottomPnl.add(spectraPnl, cc.xy(1, 1));
		bottomPnl.add(solutionsPnl, cc.xy(1, 2));
		
		spectrumOverviewPnl = new JPanel(new BorderLayout());
		spectrumOverviewPnl.setBorder(BorderFactory.createTitledBorder("Spectrum"));
		spectrumJPanel = new JPanel();
		spectrumJPanel.setLayout(new BoxLayout(spectrumJPanel, BoxLayout.LINE_AXIS));
		spectrumJPanel.setBackground(Color.WHITE);
		spectrumOverviewPnl.add(spectrumJPanel, BorderLayout.CENTER);
		
		// SplitPane
		JSplitPane splitPn = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, bottomPnl, spectrumOverviewPnl);
		splitPn.setBorder(null);
		
		// Remove the border from the splitpane divider
		BasicSplitPaneDivider divider = ((BasicSplitPaneUI) splitPn.getUI()).getDivider();
		if (divider != null) {
			divider.setBorder(null);
		}
		
		this.add(peptideTagsPnl,cc.xy(2, 2));
		this.add(splitPn,cc.xy(2, 4));
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
	 * Clears the denovotag result table.
	 */
	private void clearDenovoTagResultTable() {
		// Remove PSMs from all result tables        	 
		while (peptideTagsTbl.getRowCount() > 0) {
			((DefaultTableModel) peptideTagsTbl.getModel()).removeRow(0);
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
	 * Setup the de-novo search result. 
	 */
	private void setupDenovoSearchResultTableProperties(){
		// Query table
		peptideTagsTbl = new JXTable(new DefaultTableModel() {
			// instance initializer block
			{ setColumnIdentifiers(new Object[] {" ", "Sequence", "Tag Count", "Total Mass"}); }

			public boolean isCellEditable(int row, int col) {
				return false;
			}
		});
		peptideTagsTbl.getColumn(" ").setMinWidth(30);
		peptideTagsTbl.getColumn(" ").setMaxWidth(30);
		peptideTagsTbl.getColumn(" ").setCellRenderer(new CustomTableCellRenderer(SwingConstants.RIGHT));
		peptideTagsTbl.getColumn("Tag Count").setMinWidth(100);
		peptideTagsTbl.getColumn("Tag Count").setMaxWidth(100);
		peptideTagsTbl.getColumn("Total Mass").setMinWidth(80);
		peptideTagsTbl.getColumn("Total Mass").setMaxWidth(80);
		
		// Sort the table by the number of peptides
		TableRowSorter<TableModel> sorter  = new TableRowSorter<TableModel>(peptideTagsTbl.getModel());
		List<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
		sortKeys.add(new RowSorter.SortKey(2, SortOrder.DESCENDING));
		
		sorter.setSortKeys(sortKeys);
		peptideTagsTbl.setRowSorter(sorter);
		
		// Only one row is selectable
		peptideTagsTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		// Enables column control
		peptideTagsTbl.setColumnControlVisible(true);
		
		
		peptideTagsTbl.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				queryPeptideTagsTableMouseClicked(evt);
				
				// Select first row of spectra table
				spectraTbl.getSelectionModel().setSelectionInterval(0, 0);	
				
				// Creates spectra table
				querySpectrumTableMouseClicked(evt);
				
				// Select first row of solutions table
				solutionsTbl.getSelectionModel().setSelectionInterval(0, 0);	

			}
		});

		peptideTagsTbl.addKeyListener(new java.awt.event.KeyAdapter() {
			@Override
			public void keyReleased(java.awt.event.KeyEvent evt) {
				queryPeptideTagsTableKeyReleased(evt);
				
				// Select first row of spectra table
				spectraTbl.getSelectionModel().setSelectionInterval(0, 0);	
				
				// Creates spectra table
				querySpectrumTableMouseClicked(null);
				
				// Select first row of solutions table
				solutionsTbl.getSelectionModel().setSelectionInterval(0, 0);	
			}
		});
		
		spectraTbl = new JXTable(new DefaultTableModel() {
			// instance initializer block
			{ setColumnIdentifiers(new Object[] {" ", "Spectrum Title", "No. Solutions"}); }

			public boolean isCellEditable(int row, int col) {
				return false;
			}
		});
		
		spectraTbl.getColumn(" ").setMinWidth(30);
		spectraTbl.getColumn(" ").setMaxWidth(30);
		spectraTbl.getColumn(" ").setCellRenderer(new CustomTableCellRenderer(SwingConstants.RIGHT));
		spectraTbl.getColumn("No. Solutions").setMinWidth(90);
		spectraTbl.getColumn("No. Solutions").setMaxWidth(90);
		spectraTbl.getColumn("No. Solutions").setCellRenderer(new JSparklinesBarChartTableCellRenderer(PlotOrientation.HORIZONTAL,(double) 10, true));
		((JSparklinesBarChartTableCellRenderer) spectraTbl.getColumn("No. Solutions").getCellRenderer()).showNumberAndChart(true, 25, UIManager.getFont("Label.font").deriveFont(12f), SwingConstants.LEFT);
		// Only one row is selectable
		spectraTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		// Enables column control
		spectraTbl.setColumnControlVisible(true);
		
		spectraTbl.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				querySpectrumTableMouseClicked(evt);
			}
		});
	
		spectraTbl.addKeyListener(new java.awt.event.KeyAdapter() {
			@Override
			public void keyReleased(java.awt.event.KeyEvent evt) {
				querySpectrumTableMouseClicked(null);
			}
		});
	}
	
	/**
	 * This method sets up the de-novo solutions table.
	 */
	private void setupSolutionsTableProperties(){
		// Peptide table
		solutionsTbl = new JXTable(new DefaultTableModel() {
			// instance initializer block
			{ setColumnIdentifiers(new Object[] {" ", "Sequence", "N-Gap", "C-Gap", "Score", "z"}); }
	
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		});
		solutionsTbl.getColumn(" ").setMinWidth(30);
		solutionsTbl.getColumn(" ").setMaxWidth(30);
		solutionsTbl.getColumn(" ").setCellRenderer(new CustomTableCellRenderer(SwingConstants.RIGHT));
		solutionsTbl.getColumn("Score").setMinWidth(100);
		solutionsTbl.getColumn("Score").setMaxWidth(100);
		solutionsTbl.getColumn("N-Gap").setMinWidth(60);
		solutionsTbl.getColumn("N-Gap").setMaxWidth(60);
		solutionsTbl.getColumn("C-Gap").setMinWidth(60);
		solutionsTbl.getColumn("C-Gap").setMaxWidth(60);
		solutionsTbl.getColumn("z").setMinWidth(30);
		solutionsTbl.getColumn("z").setMaxWidth(30);
		solutionsTbl.getColumn("z").setCellRenderer(new CustomTableCellRenderer(SwingConstants.CENTER));
		
		// Only one row is selectable
		solutionsTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// Enables column control
		solutionsTbl.setColumnControlVisible(true);
	}

	/**
	 * @see #queryPeptideTagsTableKeyReleased(java.awt.event.MouseEvent)
	 */
	private void queryPeptideTagsTableKeyReleased(KeyEvent evt) {
		queryPeptideTagsTableMouseClicked(null);
	}

	/**
	 * Update the spectrum table based on the peptide tag selected.
	 * 
	 * @param evt
	 */
	private void queryPeptideTagsTableMouseClicked(MouseEvent evt) {
		// Set the cursor into the wait status.
		this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		
		int row = peptideTagsTbl.getSelectedRow();
		// Condition if one row is selected.
		if (row != -1) {
			
			// Empty tables.
			clearSpectrumTable();
			clearDenovoHitResultTable();
			
			// Counter variable
			int i = 1;
			String tagSequence = peptideTagsTbl.getValueAt(row, 1).toString();
			DenovoTagHit tagHit = denovoSearchResult.getTagHit(tagSequence);
			
			// Iterate all spectrum hits.
			for(Entry entry : tagHit.getSpectrumHits().entrySet()){
				
				// Get the spectrum hit
				SpectrumHit spectrumHit = (SpectrumHit) entry.getValue();
				
				// Add the current spectrum hit to the map
				currentSpectrumHits.put(i, spectrumHit);
				((DefaultTableModel) spectraTbl.getModel()).addRow(new Object[]{
						i ,
						spectrumHit.getSpectrumTitle(),
						spectrumHit.getNumberOfSolutions()
						});
				i++;
			}
		}
		// Set the cursor back into the default status.
		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
	
	/**
	 * Update the solutions table based on the spectrum selected.
	 * 
	 * @param evt
	 */
	private void querySpectrumTableMouseClicked(MouseEvent evt) {
		// Set the cursor into the wait status.
		this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		
		int row = spectraTbl.getSelectedRow();
		// Condition if one row is selected.
		if (row != -1) {
			
			// Empty spectrum panel.
            while (spectrumJPanel.getComponents().length > 0) {
                spectrumJPanel.remove(0);
            }
            clearDenovoHitResultTable();
            
			// Counter variable
			int i = 1;
			SpectrumHit spectrumHit = currentSpectrumHits.get(Integer.valueOf(spectraTbl.getValueAt(row, 0).toString()));
			Searchspectrum searchSpectrum;
			try {
				searchSpectrum = Searchspectrum.findFromSearchSpectrumID(spectrumHit.getSpectrumid(), clientFrame.getClient().getConnection());
				MascotGenericFile mgf = SpectrumExtractor.getMascotGenericFile(searchSpectrum.getFk_spectrumid(), clientFrame.getClient().getConnection());
				specPnl = new SpectrumPanel(mgf);
				spectrumJPanel.add(specPnl);
		        spectrumJPanel.validate();
		        spectrumJPanel.repaint();
		        
		        List<Pepnovohit> denovoHits = spectrumHit.getDenovoHits();
				for (Pepnovohit pepnovohit : denovoHits) {
					((DefaultTableModel) solutionsTbl.getModel()).addRow(new Object[]{
							i,
							pepnovohit.getSequence(),
							pepnovohit.getN_gap().doubleValue(),
							pepnovohit.getC_gap().doubleValue(),
							pepnovohit.getPnvscore(),
							"+" + pepnovohit.getCharge()
							});
					i++;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		// Set the cursor back into the default status.
		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}
	
	/**
	 * Updates the peptide tags table.
	 */
	public void updatePeptideTagsTable(){
		int i = 1;
		// Iterate the found protein results
		if(denovoSearchResult != null){
			for (Entry entry : denovoSearchResult.getTagHits().entrySet()){
				
				// Get the de-novo tag hit.
				DenovoTagHit denovoTagHit = (DenovoTagHit) entry.getValue();

				// Determine the number of containing tag hits.
				int tagCount = denovoTagHit.getTagSpecCount();
				
				((DefaultTableModel) peptideTagsTbl.getModel()).addRow(new Object[]{
						i,
						denovoTagHit.getTagSequence(),
						denovoTagHit.getTagSpecCount(),
						denovoTagHit.getTotalMass()});
				i++;
			}
		}
	}

}
