package de.mpa.client.ui.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.Painter;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.specsim.SpecSimResult;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.CustomTableCellRenderer;
import de.mpa.client.ui.TableConfig;
import de.mpa.ui.MultiPlotPanel;

public class SpecSimResultPanel extends JPanel {
	
	private ClientFrame clientFrame;
	private JXTable proteinTbl;
	private JXTable peptideTbl;
	private JXTable ssmTbl;
	private JButton getResultsBtn;
	private JPanel spectrumJPanel;
	protected SpecSimResult specSimResult;

	/**
	 * Class constructor defining the parent client frame.
	 * @param clientFrame
	 */
	public SpecSimResultPanel(ClientFrame clientFrame) {
		this.clientFrame = clientFrame;
		initComponents();
	}
	
	/**
	 * Initializes the components of the database search results panel
	 */
	private void initComponents() {
		// Cell constraints
		CellConstraints cc = new CellConstraints();
		
		// Titled panel variables
		JXTitledPanel dummyPnl = new JXTitledPanel(" ");
		
		Font ttlFont = dummyPnl.getTitleFont().deriveFont(Font.BOLD);
		Painter ttlPainter = new MattePainter(new GradientPaint(
				0, 0, UIManager.getColor("TabbedPane.focus"),
				0, 20, new Color(107, 147, 193)));
		Border ttlBorder = UIManager.getBorder("TitledBorder.border");
		
		// Scroll panes
		JScrollPane proteinTableScp = new JScrollPane();
		JScrollPane peptideTableScp = new JScrollPane();
		JScrollPane ssmTableScp = new JScrollPane();
		
		this.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu, f:p:g, 5dlu"));
	
		final JPanel proteinPnl = new JPanel();
		proteinPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));
//		proteinPnl.setBorder(BorderFactory.createTitledBorder("Proteins"));
		
		// Setup the tables
		setupProteinTableProperties();
		setupPeptideTableProperties();
		setupSsmTableProperties();
		
		proteinTableScp.setViewportView(proteinTbl);
		proteinTableScp.setPreferredSize(new Dimension(800, 200));
		
		peptideTableScp.setViewportView(peptideTbl);
		peptideTableScp.setPreferredSize(new Dimension(350, 150));
		
		ssmTableScp.setViewportView(ssmTbl);
		ssmTableScp.setPreferredSize(new Dimension(350, 150));
		
		getResultsBtn = new JButton("Get Results");
		getResultsBtn.setEnabled(false);
		getResultsBtn.setPreferredSize(new Dimension(150, 20));
		getResultsBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				specSimResult = clientFrame.getClient().getSpecSimResult(clientFrame.getProjectPnl().getCurrentExperimentContent());
			}
		});
		proteinPnl.add(proteinTableScp, cc.xy(2, 2));
	
		JXTitledPanel protTtlPnl = new JXTitledPanel("Proteins", proteinPnl);
		protTtlPnl.setRightDecoration(getResultsBtn);
		protTtlPnl.setTitleFont(ttlFont);
		protTtlPnl.setTitlePainter(ttlPainter);
		protTtlPnl.setBorder(ttlBorder);
				
		// Peptide panel
		final JPanel peptidePnl = new JPanel();
		peptidePnl.setLayout(new FormLayout("5dlu, p:g, 5dlu",  "5dlu, f:p:g, 5dlu"));
		peptidePnl.add(peptideTableScp, cc.xy(2, 2));
		
		JXTitledPanel pepTtlPnl = new JXTitledPanel("Peptides", peptidePnl);
		pepTtlPnl.setTitleFont(ttlFont);
		pepTtlPnl.setTitlePainter(ttlPainter);
		pepTtlPnl.setBorder(ttlBorder);
		
		// PSM panel
		final JPanel ssmPanel = new JPanel();
		ssmPanel.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));
		ssmPanel.add(ssmTableScp, cc.xy(2, 2));
		
		JXTitledPanel ssmTtlPnl = new JXTitledPanel("Spectrum-Spectrum-Matches", ssmPanel);
		ssmTtlPnl.setTitleFont(ttlFont);
		ssmTtlPnl.setTitlePainter(ttlPainter);
		ssmTtlPnl.setBorder(ttlBorder);

		// Peptide and Psm Panel
		JPanel pepPsmPnl = new JPanel(new FormLayout("p:g","f:p:g, 5dlu, f:p:g"));
		
		pepPsmPnl.add(pepTtlPnl, cc.xy(1, 1));
		pepPsmPnl.add(ssmTtlPnl, cc.xy(1, 3));
		
		// Build the spectrum filter panel
		JPanel specPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));

		spectrumJPanel = new MultiPlotPanel();
		spectrumJPanel.setBorder(BorderFactory.createEtchedBorder());
		
		specPnl.add(spectrumJPanel, cc.xy(2, 2));
		
		JXTitledPanel specTtlPnl = new JXTitledPanel("Spectrum Viewer", specPnl); 
		specTtlPnl.setTitleFont(ttlFont);
		specTtlPnl.setTitlePainter(ttlPainter);
		specTtlPnl.setBorder(ttlBorder);
		
		// SplitPane
		JSplitPane splitPn = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, pepPsmPnl, specTtlPnl);
		splitPn.setBorder(null);
		
		// Remove the border from the splitpane divider
		BasicSplitPaneDivider divider = ((BasicSplitPaneUI) splitPn.getUI()).getDivider();
		if (divider != null) {
			divider.setBorder(null);
		}
		
		this.add(protTtlPnl, cc.xy(2,2));
		this.add(splitPn, cc.xy(2, 4));
	}
	
	/**
	 * This method sets up the protein results table.
	 */
	private void setupProteinTableProperties() {
		// Protein table
		proteinTbl = new JXTable(new DefaultTableModel() {
			// instance initializer block
			{ setColumnIdentifiers(new Object[] {" ", "Accession", "Description", "Coverage (%)", "Mass (kDa)", "Peptide Count", "Spectral Count"}); }
	
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		});
		
		TableConfig.setColumnWidths(proteinTbl, new double[] {30, 70, 90, 90, 110, 115});
		
		TableColumnModel tcm = proteinTbl.getColumnModel();
		tcm.getColumn(0).setCellRenderer(new CustomTableCellRenderer(SwingConstants.RIGHT));
		tcm.getColumn(2).setCellRenderer(new CustomTableCellRenderer(SwingConstants.CENTER));
		tcm.getColumn(3).setCellRenderer(new CustomTableCellRenderer(SwingConstants.CENTER));
		
		// Sort the table by the number of peptides
		TableRowSorter<TableModel> sorter  = new TableRowSorter<TableModel>(proteinTbl.getModel());
		List <RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
		// Sort the table by the descending spectral count.
		sortKeys.add(new RowSorter.SortKey(6, SortOrder.DESCENDING));
		
		sorter.setSortKeys(sortKeys);
		proteinTbl.setRowSorter(sorter);
		
		ListSelectionModel lsm = proteinTbl.getSelectionModel();
		lsm.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				// FIXME proteinTbl
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
	private void setupPeptideTableProperties() {
		// Peptide table
		peptideTbl = new JXTable(new DefaultTableModel() {
			// instance initializer block
			{ setColumnIdentifiers(new Object[] {" ", "Sequence", "No. Spectra"}); }
	
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		});
		
		TableConfig.setColumnWidths(peptideTbl, new double[] {30, 70, 100});
		
		TableColumnModel tcm = peptideTbl.getColumnModel();
		tcm.getColumn(0).setCellRenderer(new CustomTableCellRenderer(SwingConstants.RIGHT));
		
		// Sort the table by the number of peptides
		TableRowSorter<TableModel> sorter  = new TableRowSorter<TableModel>(peptideTbl.getModel());
		List <RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
		sortKeys.add(new RowSorter.SortKey(2, SortOrder.DESCENDING));
		sorter.setSortKeys(sortKeys);
		peptideTbl.setRowSorter(sorter);
		
		
		ListSelectionModel lsm = peptideTbl.getSelectionModel();
		lsm.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				// FIXME peptide table
			}
		});
		
		// Single selection only
		peptideTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		// Enables column control
		peptideTbl.setColumnControlVisible(true);
	}
	
	/**
	 * This method sets up the SSM results table.
	 */
	private void setupSsmTableProperties(){
		// Peptide table
		ssmTbl = new JXTable(new DefaultTableModel() {
			// instance initializer block
			{ setColumnIdentifiers(new Object[] {"#", "Spectrum Title", "Score"}); }
	
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		});
		
		TableConfig.setColumnWidths(ssmTbl, new double[]{1, 8, 2});
		
		TableColumnModel tcm = ssmTbl.getColumnModel();
		tcm.getColumn(0).setCellRenderer(new CustomTableCellRenderer(SwingConstants.RIGHT));
		// TODO: create static method to generate table cell renderers with specific formatters
		tcm.getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
			private final DecimalFormat formatter = new DecimalFormat( "0.000" );

			public Component getTableCellRendererComponent(
					JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				// First format the cell value as required
				value = formatter.format((Number)value);
				// And pass it on to parent class
				return super.getTableCellRendererComponent(
						table, value, isSelected, hasFocus, row, column );
			}

		});
		
		ListSelectionModel lsm = ssmTbl.getSelectionModel();
		lsm.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				// FIXME ssm table
			}
		});
		
		// Only one row is selectable
		ssmTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// Enables column control
		ssmTbl.setColumnControlVisible(true);
	}
	
}
