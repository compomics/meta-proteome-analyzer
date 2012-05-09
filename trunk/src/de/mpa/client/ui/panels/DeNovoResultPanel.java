package de.mpa.client.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import no.uib.jsparklines.renderers.JSparklinesBarChartTableCellRenderer;

import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.MultiSplitLayout;
import org.jdesktop.swingx.painter.Painter;
import org.jfree.chart.plot.PlotOrientation;

import com.compomics.util.gui.spectrum.SpectrumPanel;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Client;
import de.mpa.client.model.denovo.DenovoSearchResult;
import de.mpa.client.model.denovo.SpectrumHit;
import de.mpa.client.model.denovo.Tag;
import de.mpa.client.model.denovo.TagHit;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.PanelConfig;
import de.mpa.client.ui.TableConfig.CustomTableCellRenderer;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.db.accessor.Pepnovohit;
import de.mpa.db.accessor.Searchspectrum;
import de.mpa.db.extractor.SpectrumExtractor;
import de.mpa.io.MascotGenericFile;

public class DeNovoResultPanel extends JPanel {

	private ClientFrame clientFrame;
	private Client client;
	private JXTable spectraTbl;
	private JXTable peptideTagsTbl;
	protected Object filePnl;
	private DenovoSearchResult denovoSearchResult;
	private JPanel spectrumJPanel;
	private JXTable solutionsTbl;
	private SpectrumPanel specPnl;
	private HashMap<Integer, SpectrumHit> currentSpectrumHits = new HashMap<Integer, SpectrumHit>();
	private JButton getResultsBtn;
	
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
		this.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));
		
		// Init titled panel variables.
		Font ttlFont = PanelConfig.getTitleFont();
		Border ttlBorder = PanelConfig.getTitleBorder();
		Painter ttlPainter = PanelConfig.getTitlePainter();
		
		JScrollPane denovoTagsScp = new JScrollPane();

		final JPanel denovoTagsPnl = new JPanel();
		denovoTagsPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));
		
		// Setup the tables
		setupDenovoSearchResultTableProperties();
		setupSolutionsTableProperties();

		denovoTagsScp.setViewportView(peptideTagsTbl);
		denovoTagsScp.setPreferredSize(new Dimension(800, 200));
		
		getResultsBtn = new JButton("Get Results   ", IconConstants.REFRESH_DB_ICON);
		getResultsBtn.setRolloverIcon(IconConstants.REFRESH_DB_ROLLOVER_ICON);
		getResultsBtn.setPressedIcon(IconConstants.REFRESH_DB_PRESSED_ICON);
		
		getResultsBtn.setEnabled(false);

		getResultsBtn.setPreferredSize(new Dimension(getResultsBtn.getPreferredSize().width, 20));
		getResultsBtn.setFocusPainted(false);

		getResultsBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				denovoSearchResult = client.getDenovoSearchResult(clientFrame.getProjectPanel().getCurrentProjectContent(), clientFrame.getProjectPanel().getCurrentExperimentContent());
				
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
		
		denovoTagsPnl.add(denovoTagsScp, cc.xy(2, 2));
		
		JXTitledPanel denovoTagsTtlPnl = new JXTitledPanel("De-novo Tags", denovoTagsPnl);
		denovoTagsTtlPnl.setRightDecoration(getResultsBtn);
		denovoTagsTtlPnl.setTitleFont(ttlFont);
		denovoTagsTtlPnl.setTitlePainter(ttlPainter);
		denovoTagsTtlPnl.setBorder(ttlBorder);

		
		// Spectra panel.
		JPanel spectraPnl = new JPanel();
		spectraPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));
		
		
		JXTitledPanel specHitsTtlPnl = new JXTitledPanel("Spectrum Hits", spectraPnl);
		specHitsTtlPnl.setTitleFont(ttlFont);
		specHitsTtlPnl.setTitlePainter(ttlPainter);
		specHitsTtlPnl.setBorder(ttlBorder);
		
		JScrollPane spectraTblScp = new JScrollPane(spectraTbl);
		spectraTblScp.setPreferredSize(new Dimension(400, 150));
		
		spectraTblScp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		spectraTblScp.setToolTipText("Select spectra");
		spectraPnl.add(spectraTblScp,cc.xy(2, 2));
		
		JScrollPane solutionsTblScp = new JScrollPane();
		solutionsTblScp.setViewportView(solutionsTbl);
		solutionsTblScp.setPreferredSize(new Dimension(400, 150));
		
		final JPanel solutionsPnl = new JPanel();
		solutionsPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));
		solutionsPnl.add(solutionsTblScp, cc.xy(2, 2));
		
		JXTitledPanel solutionsTtlPnl = new JXTitledPanel("Solutions", solutionsPnl);
		solutionsTtlPnl.setTitleFont(ttlFont);
		solutionsTtlPnl.setTitlePainter(ttlPainter);
		solutionsTtlPnl.setBorder(ttlBorder);
		
		// Peptide and Psm Panel
		JPanel bottomPnl = new JPanel(new FormLayout("p:g","f:p:g,f:p:g"));
		
		bottomPnl.add(specHitsTtlPnl, cc.xy(1, 1));
		bottomPnl.add(solutionsTtlPnl, cc.xy(1, 2));

		JPanel spectrumOverviewPnl = new JPanel(new BorderLayout(5,5));
		
		spectrumJPanel = new JPanel();
		spectrumJPanel.setLayout(new BoxLayout(spectrumJPanel, BoxLayout.LINE_AXIS));
		spectrumJPanel.setBackground(Color.WHITE);
		spectrumJPanel.setBorder(BorderFactory.createEtchedBorder());
		spectrumJPanel.setOpaque(false);

		spectrumOverviewPnl.add(spectrumJPanel, BorderLayout.CENTER);
				
		JXTitledPanel specTtlPnl = new JXTitledPanel("Spectrum Viewer", spectrumOverviewPnl); 
		specTtlPnl.setTitleFont(ttlFont);
		specTtlPnl.setTitlePainter(ttlPainter);
		specTtlPnl.setBorder(ttlBorder);
		
		String layoutDef =
		    "(COLUMN denovotags (ROW weight=0.0 (COLUMN (LEAF weight=0.5 name=spechits) (LEAF weight=0.5 name=solutions)) plot))";
		MultiSplitLayout.Node modelRoot = MultiSplitLayout.parseModel(layoutDef);
		
		final JXMultiSplitPane multiSplitPane = new JXMultiSplitPane();
		multiSplitPane.setDividerSize(12);
		multiSplitPane.getMultiSplitLayout().setModel(modelRoot);
		multiSplitPane.add(denovoTagsTtlPnl, "denovotags");
		multiSplitPane.add(specHitsTtlPnl, "spechits");
		multiSplitPane.add(solutionsTtlPnl, "solutions");
		multiSplitPane.add(specTtlPnl, "plot");
		
		this.add(multiSplitPane, cc.xy(2, 2));
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
			{ setColumnIdentifiers(new Object[] {" ", "Formatted Sequence", "Gapped Sequence", "Tag Count", "Total Mass"}); }

			public boolean isCellEditable(int row, int col) {
				return false;
			}
		});
		
		TableColumnModel tcm = peptideTagsTbl.getColumnModel();
		tcm.getColumn(0).setCellRenderer(new CustomTableCellRenderer(SwingConstants.RIGHT));
		
		peptideTagsTbl.getColumn(" ").setMinWidth(30);
		peptideTagsTbl.getColumn(" ").setMaxWidth(30);
		peptideTagsTbl.getColumn("Tag Count").setMinWidth(100);
		peptideTagsTbl.getColumn("Tag Count").setMaxWidth(100);
		peptideTagsTbl.getColumn("Total Mass").setMinWidth(80);
		peptideTagsTbl.getColumn("Total Mass").setMaxWidth(80);
		
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
		
		TableColumnModel spectraTblMdl = spectraTbl.getColumnModel();
		spectraTblMdl.getColumn(0).setCellRenderer(new CustomTableCellRenderer(SwingConstants.RIGHT));
		
		spectraTbl.getColumn(" ").setMinWidth(30);
		spectraTbl.getColumn(" ").setMaxWidth(30);
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
		
		
		TableColumnModel tcm = solutionsTbl.getColumnModel();
		tcm.getColumn(0).setCellRenderer(new CustomTableCellRenderer(SwingConstants.RIGHT));
		
		solutionsTbl.getColumn(" ").setMinWidth(30);
		solutionsTbl.getColumn(" ").setMaxWidth(30);
		solutionsTbl.getColumn("Score").setMinWidth(100);
		solutionsTbl.getColumn("Score").setMaxWidth(100);
		solutionsTbl.getColumn("N-Gap").setMinWidth(60);
		solutionsTbl.getColumn("N-Gap").setMaxWidth(60);
		solutionsTbl.getColumn("C-Gap").setMinWidth(60);
		solutionsTbl.getColumn("C-Gap").setMaxWidth(60);
		solutionsTbl.getColumn("z").setMinWidth(30);
		solutionsTbl.getColumn("z").setMaxWidth(30);
		
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
			String tagSequence = peptideTagsTbl.getValueAt(row, 2).toString();
			TagHit tagHit = denovoSearchResult.getTagHit(tagSequence);
			
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
				searchSpectrum = Searchspectrum.findFromSearchSpectrumID(spectrumHit.getSpectrumid(), client.getConnection());
				MascotGenericFile mgf = SpectrumExtractor.getMascotGenericFile(searchSpectrum.getFk_spectrumid(), client.getConnection());
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
				
				// Get the de novo tag hit.
				TagHit denovoTagHit = (TagHit) entry.getValue();
				Tag tag = denovoTagHit.getTag();
				((DefaultTableModel) peptideTagsTbl.getModel()).addRow(new Object[] {
						i++,
						tag.getFormattedSeq(),
						tag.getGappedSeq(),
						denovoTagHit.getTagSpecCount(),
						tag.getTotalMass()});
			}
		}
	}
	
	/**
	 * This method sets the enabled state of the get results button.
	 */
	public void setResultsButtonEnabled(boolean enabled) {
		getResultsBtn.setEnabled(enabled);
	}

}
