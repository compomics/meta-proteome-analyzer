package de.mpa.client.ui.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import no.uib.jsparklines.renderers.JSparklinesBarChartTableCellRenderer;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.MultiSplitLayout;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.PainterHighlighter;
import org.jdesktop.swingx.hyperlink.AbstractHyperlinkAction;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.HyperlinkProvider;
import org.jdesktop.swingx.renderer.JRendererLabel;
import org.jdesktop.swingx.renderer.JXRendererHyperlink;
import org.jfree.chart.plot.PlotOrientation;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.analysis.ProteinAnalysis;
import de.mpa.client.model.SpectrumMatch;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.model.specsim.SpecSimResult;
import de.mpa.client.model.specsim.SpectrumSpectrumMatch;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.PanelConfig;
import de.mpa.client.ui.TableConfig;
import de.mpa.io.MascotGenericFile;
import de.mpa.ui.MultiPlotPanel;

public class SpecSimResultPanel extends JPanel {
	
	private ClientFrame clientFrame;
	private JXTable proteinTbl;
	private JXTable peptideTbl;
	private JXTable ssmTbl;
	private JButton getResultsBtn;
	private MultiPlotPanel spectrumJPanel;
	protected SpecSimResult specSimResult;
	private Font chartFont;

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
		
		this.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));
		
		// Init titled panel variables.
		Font ttlFont = PanelConfig.getTitleFont();
		Border ttlBorder = PanelConfig.getTitleBorder();
		Painter ttlPainter = PanelConfig.getTitlePainter();
		
		final JPanel proteinPnl = new JPanel();
		proteinPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));
		
		// Setup tables
		chartFont = UIManager.getFont("Label.font").deriveFont(12f);
		setupProteinTableProperties();
		setupPeptideTableProperties();
		setupSsmTableProperties();
		
		// Scroll panes
		JScrollPane proteinTableScp = new JScrollPane(proteinTbl);
		proteinTableScp.setPreferredSize(new Dimension(800, 200));
		JScrollPane peptideTableScp = new JScrollPane(peptideTbl);
		peptideTableScp.setPreferredSize(new Dimension(350, 150));
		JScrollPane ssmTableScp = new JScrollPane(ssmTbl);
		ssmTableScp.setPreferredSize(new Dimension(350, 150));
		
		getResultsBtn = new JButton("Get Results");
//		getResultsBtn.setEnabled(false);
		getResultsBtn.setPreferredSize(new Dimension(150, 20));
		getResultsBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshProteinTable();
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
		
		// Build spectrum filter panel
		JPanel specPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));

		spectrumJPanel = new MultiPlotPanel();
		spectrumJPanel.setBorder(BorderFactory.createEtchedBorder());
		
		specPnl.add(spectrumJPanel, cc.xy(2, 2));
		
		JXTitledPanel specTtlPnl = new JXTitledPanel("Spectrum Viewer", specPnl); 
		specTtlPnl.setTitleFont(ttlFont);
		specTtlPnl.setTitlePainter(ttlPainter);
		specTtlPnl.setBorder(ttlBorder);
		
		String layoutDef =
		    "(COLUMN protein (ROW weight=0.0 (COLUMN (LEAF weight=0.5 name=peptide) (LEAF weight=0.5 name=ssm)) plot))";
		MultiSplitLayout.Node modelRoot = MultiSplitLayout.parseModel(layoutDef);
		
		final JXMultiSplitPane multiSplitPane = new JXMultiSplitPane();
		multiSplitPane.setDividerSize(12);
		multiSplitPane.getMultiSplitLayout().setModel(modelRoot);
		multiSplitPane.add(protTtlPnl, "protein");
		multiSplitPane.add(pepTtlPnl, "peptide");
		multiSplitPane.add(ssmTtlPnl, "ssm");
		multiSplitPane.add(specTtlPnl, "plot");
		
		this.add(multiSplitPane, cc.xy(2, 2));
	}

	/**
	 * This method sets up the protein results table.
	 */
	private void setupProteinTableProperties() {
		// Protein table
		proteinTbl = new JXTable(new DefaultTableModel() {
			// instance initializer block
			{ setColumnIdentifiers(new Object[] {" ", "Accession", "Description", "Coverage [%]", "MW [kDa]", "Peptide Count", "Spectral Count"}); }
	
			public boolean isCellEditable(int row, int col) {
				return false;
			}
			
			public Class<?> getColumnClass(int columnIndex) {
				switch (columnIndex) {
				case 3:
				case 4:
					return Double.class;
				case 0:
				case 5:
				case 6:
					return Integer.class;
				default:
					return String.class;
				}
			}
		});
		
		TableConfig.setColumnWidths(proteinTbl, new double[] {1, 6, 30, 7, 5.5, 8, 8});
		
		TableColumnModel tcm = proteinTbl.getColumnModel();
		tcm.getColumn(0).setCellRenderer(new TableConfig.CustomTableCellRenderer(SwingConstants.RIGHT));
		tcm.getColumn(3).setCellRenderer(new JSparklinesBarChartTableCellRenderer(PlotOrientation.HORIZONTAL, 0.0, true));
		tcm.getColumn(4).setCellRenderer(new TableConfig.CustomTableCellRenderer(SwingConstants.RIGHT, "0.000"));
		tcm.getColumn(5).setCellRenderer(new JSparklinesBarChartTableCellRenderer(PlotOrientation.HORIZONTAL, 0.0, true));
		tcm.getColumn(6).setCellRenderer(new JSparklinesBarChartTableCellRenderer(PlotOrientation.HORIZONTAL, 0.0, true));
		
		AbstractHyperlinkAction<URI> linkAction = new AbstractHyperlinkAction<URI>() {
		    public void actionPerformed(ActionEvent ev) {
		        try {
		            Desktop.getDesktop().browse(new URI("http://www.uniprot.org/uniprot/" + target));
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		    }
		};
		tcm.getColumn(1).setCellRenderer(new DefaultTableRenderer(new HyperlinkProvider(linkAction)){
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				JXRendererHyperlink compLabel = (JXRendererHyperlink) comp;
				compLabel.setHorizontalAlignment(SwingConstants.CENTER);
				return compLabel;
			}
		});
        
		proteinTbl.setAutoCreateRowSorter(true);
		proteinTbl.getRowSorter().toggleSortOrder(6);
		proteinTbl.getRowSorter().toggleSortOrder(6);

		ListSelectionModel lsm = proteinTbl.getSelectionModel();
		lsm.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				refreshPeptideTable();
			}
		});
		
		// Only one row is selectable
		proteinTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Add nice striping effect
		proteinTbl.addHighlighter(TableConfig.getSimpleStriping());
		
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
			
			public Class<?> getColumnClass(int columnIndex) {
				switch (columnIndex) {
				case 0:
				case 2:
					return Integer.class;
				default:
					return String.class;
				}
			}
		});
		
		TableConfig.setColumnWidths(peptideTbl, new double[] {3, 24, 12});
		
		TableColumnModel tcm = peptideTbl.getColumnModel();
		tcm.getColumn(0).setCellRenderer(new TableConfig.CustomTableCellRenderer(SwingConstants.RIGHT));
		
		// Sort the table by the number of peptides
		TableRowSorter<TableModel> sorter  = new TableRowSorter<TableModel>(peptideTbl.getModel());
		List <RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
		sortKeys.add(new RowSorter.SortKey(2, SortOrder.DESCENDING));
		sorter.setSortKeys(sortKeys);
		peptideTbl.setRowSorter(sorter);
		
		ListSelectionModel lsm = peptideTbl.getSelectionModel();
		lsm.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				refreshSsmTable();
			}
		});
		
		// Single selection only
		peptideTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Add nice striping effect
		peptideTbl.addHighlighter(TableConfig.getSimpleStriping());
		
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
		
		TableConfig.setColumnWidths(ssmTbl, new double[] { 1, 8, 2 });
		
		TableColumnModel tcm = ssmTbl.getColumnModel();
		tcm.getColumn(0).setCellRenderer(new TableConfig.CustomTableCellRenderer(SwingConstants.RIGHT));
//		tcm.getColumn(2).setCellRenderer(new TableConfig.CustomTableCellRenderer(SwingConstants.CENTER, "0.000"));
		
		ListSelectionModel lsm = ssmTbl.getSelectionModel();
		lsm.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				refreshPlot();
			}
		});
		
		// Only one row is selectable
		ssmTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Add nice striping effect
		ssmTbl.addHighlighter(TableConfig.getSimpleStriping());
		
		// Enables column control
		ssmTbl.setColumnControlVisible(true);
	}

	/**
	 * Method to refresh protein table contents.
	 */
	protected void refreshProteinTable() {
		specSimResult = clientFrame.getClient().getSpecSimResult(clientFrame.getProjectPnl().getCurrentExperimentContent());
		if (specSimResult != null) {
			((DefaultTableModel) proteinTbl.getModel()).setRowCount(0);
			
			int i = 1, maxPeptideCount = 0, maxSpecCount = 0;
			double maxCoverage = 0.0;
			for (Entry<String, ProteinHit> entry : specSimResult.getProteinHits().entrySet()) {
				ProteinHit hit = entry.getValue();
				ProteinAnalysis.calculateMolecularWeight(hit);
				ProteinAnalysis.calculateSequenceCoverage(hit);
				maxCoverage = Math.max(maxCoverage, hit.getCoverage());
				maxPeptideCount = Math.max(maxPeptideCount, hit.getPeptideCount());
				maxSpecCount = Math.max(maxSpecCount, hit.getSpecCount());
				((DefaultTableModel) proteinTbl.getModel()).addRow(new Object[] {
						i++,
						hit.getAccession(),
						hit.getDescription(),
						hit.getCoverage(),
						hit.getMolWeight(),
						hit.getPeptideCount(),
						hit.getSpecCount()});
			}
			
			Graphics g = getGraphics();
			FontMetrics fm = g.getFontMetrics(chartFont);
			DecimalFormat df = new DecimalFormat("##0.00");
			TableColumnModel tcm = proteinTbl.getColumnModel();
			JSparklinesBarChartTableCellRenderer renderer;
			
			renderer = (JSparklinesBarChartTableCellRenderer) tcm.getColumn(3).getCellRenderer();
			renderer.setMaxValue(maxCoverage);
			renderer.showNumberAndChart(true, fm.stringWidth("   " + df.format(maxCoverage)), chartFont, SwingConstants.RIGHT, df);
			
			renderer = (JSparklinesBarChartTableCellRenderer) tcm.getColumn(5).getCellRenderer();
			renderer.setMaxValue(maxPeptideCount);
			renderer.showNumberAndChart(true, fm.stringWidth("   " + maxPeptideCount), chartFont, SwingConstants.RIGHT);
			
			renderer = (JSparklinesBarChartTableCellRenderer) tcm.getColumn(6).getCellRenderer();
			renderer.setMaxValue(maxSpecCount);
			renderer.showNumberAndChart(true, fm.stringWidth("   " + maxSpecCount), chartFont, SwingConstants.RIGHT);
			
			proteinTbl.getSelectionModel().setSelectionInterval(0, 0);
		}
	}
	
	/**
	 * Method to refresh peptide table contents.
	 */
	protected void refreshPeptideTable() {
		((DefaultTableModel) peptideTbl.getModel()).setRowCount(0);

		int protRow = proteinTbl.getSelectedRow();
		if (protRow != -1) {
			String accession = (String) proteinTbl.getValueAt(protRow, 1);
			ProteinHit proteinHit = specSimResult.getProteinHits().get(accession);
			List<PeptideHit> peptideHits = proteinHit.getPeptideHitList();
			
			int i = 1;
			for (PeptideHit peptideHit : peptideHits) {
				((DefaultTableModel) peptideTbl.getModel()).addRow(new Object[] {
						i++,
						peptideHit.getSequence(),
						peptideHit.getSpectrumMatches().size()});
			}
			
			peptideTbl.getSelectionModel().setSelectionInterval(0, 0);
		}
	}
	
	/**
	 * Method to refresh SSM table contents.
	 */
	protected void refreshSsmTable() {
		((DefaultTableModel) ssmTbl.getModel()).setRowCount(0);
		
		int protRow = proteinTbl.getSelectedRow();
		if (protRow != -1) {
			String accession = (String) proteinTbl.getValueAt(protRow, 1);
			int pepRow = peptideTbl.getSelectedRow();
			if (pepRow != -1) {
				String sequence = (String) peptideTbl.getValueAt(pepRow, 1);
				PeptideHit peptideHit = specSimResult.getProteinHits().get(accession).getPeptideHits().get(sequence);
				
				TreeMap<Long, SpectrumMatch> matches = peptideHit.getSpectrumMatches();
				try {
					List<String> titles = clientFrame.getClient().getSpectrumTitlesFromIDs(matches.keySet());
					
					int i = 0;
					for (Entry<Long, SpectrumMatch> entry : peptideHit.getSpectrumMatches().entrySet()) {
						((DefaultTableModel) ssmTbl.getModel()).addRow(new Object[] {
								i+1,
//								titles.get(i++),
								entry.getValue().getSpectrumId() + " " + ((SpectrumSpectrumMatch) entry.getValue()).getLibspectrumID() + " " + titles.get(i++),
								((SpectrumSpectrumMatch) entry.getValue()).getSimilarity()});
					}
					
					ssmTbl.getSelectionModel().setSelectionInterval(0, 0);
				} catch (SQLException e) {
					JXErrorPane.showDialog(e);
				}
			}
		}
	}
	
	/**
	 * Method to refresh plot panel contents.
	 */
	protected void refreshPlot() {
		int protRow = proteinTbl.getSelectedRow();
		if (protRow != -1) {
			int pepRow = peptideTbl.getSelectedRow();
			if (pepRow != -1) {
				int ssmRow = ssmTbl.getSelectedRow();
				if (ssmRow != -1) {
					String accession = (String) proteinTbl.getValueAt(protRow, 1);
					String sequence = (String) peptideTbl.getValueAt(pepRow, 1);
					Iterator<SpectrumMatch> iter = specSimResult.getProteinHits().get(accession).getPeptideHits().get(sequence).getSpectrumMatches().values().iterator();
					int i = 0, index = (Integer) ssmTbl.getValueAt(ssmRow, 0);
					SpectrumSpectrumMatch ssm = null;
					while (i < index) {
						ssm = (SpectrumSpectrumMatch) iter.next();
						i++;
					}
					try {
						MascotGenericFile mgfQuery = clientFrame.getClient().getSpectrumFromSearchSpectrumID(ssm.getSpectrumId());
						MascotGenericFile mgfLib = clientFrame.getClient().getSpectrumFromLibSpectrumID(ssm.getLibspectrumID());
						spectrumJPanel.setFirstSpectrum(mgfQuery);
						spectrumJPanel.setSecondSpectrum(mgfLib);
						spectrumJPanel.repaint();
					} catch (SQLException e) {
						JXErrorPane.showDialog(e);
					}
				}
			}
		}
	}
	
}
