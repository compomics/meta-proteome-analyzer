package de.mpa.client.ui.panels;

import static java.lang.Math.max;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SizeRequirements;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.InlineView;
import javax.swing.text.html.ParagraphView;
import javax.swing.text.html.HTMLEditorKit.HTMLFactory;

import no.uib.jsparklines.renderers.JSparklinesBarChartTableCellRenderer;

import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.MultiSplitLayout;
import org.jdesktop.swingx.hyperlink.AbstractHyperlinkAction;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.HyperlinkProvider;
import org.jdesktop.swingx.renderer.JXRendererHyperlink;
import org.jfree.chart.plot.PlotOrientation;

import com.compomics.util.gui.spectrum.DefaultSpectrumAnnotation;
import com.compomics.util.gui.spectrum.SpectrumPanel;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.algorithms.quantification.EmPAIAlgorithm;
import de.mpa.algorithms.quantification.NSAFAlgorithm;
import de.mpa.analysis.Masses;
import de.mpa.analysis.ProteinAnalysis;
import de.mpa.client.model.SpectrumMatch;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.PeptideSpectrumMatch;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.CustomTableCellRenderer;
import de.mpa.client.ui.PanelConfig;
import de.mpa.client.ui.TableConfig;
import de.mpa.client.ui.dialogs.GeneralExceptionHandler;
import de.mpa.db.accessor.Searchspectrum;
import de.mpa.db.extractor.SpectrumExtractor;
import de.mpa.fragmentation.FragmentIon;
import de.mpa.fragmentation.Fragmentizer;
import de.mpa.io.MascotGenericFile;

public class DbSearchResultPanel extends JPanel{
	
	private ClientFrame clientFrame;
	private JXTable proteinTbl;
	private DbSearchResult dbSearchResult;
	private JXTable peptideTbl;
	private TreeMap<String, PeptideHit> currentPeptideHits;
	private JXTable psmTbl;
	private HashMap<Integer, PeptideSpectrumMatch> currentPsms = new HashMap<Integer, PeptideSpectrumMatch>();
	private SpectrumPanel specPnl;
	private String currentSelectedPeptide;
	private JCheckBox aIonsChk;
	private JCheckBox bIonsChk;
	private JCheckBox cIonsChk;
	private JCheckBox yIonsChk;
	private JCheckBox xIonsChk;
	private JCheckBox zIonsChk;
	private JCheckBox waterLossChk;
	private JCheckBox ammoniumLossChk;
	private JCheckBox chargeOneChk;
	private JCheckBox chargeTwoChk;
	private AbstractButton chargeMoreChk;
	private Vector<DefaultSpectrumAnnotation> currentAnnotations;
	private JPanel spectrumJPanel;
	private AbstractButton precursorChk;
	private int maxSpectralCount;
	private int maxPeptideCount;
	private int maxPeptideSpectraCount;
	private JButton getResultsBtn;
	private JEditorPane coverageTxt;

	public DbSearchResultPanel(ClientFrame clientFrame) {
		this.clientFrame = clientFrame;
		initComponents();
	}

	/**
	 * Initializes the components of the database search results panel
	 */
	private void initComponents() {
		// Define layout
		this.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));
		
		// Init titled panel variables.
		Font ttlFont = PanelConfig.getTitleFont();
		Border ttlBorder = PanelConfig.getTitleBorder();
		Painter ttlPainter = PanelConfig.getTitlePainter();
		
		// Scroll panes
		JScrollPane proteinTableScp = new JScrollPane();
		JScrollPane peptideTableScp = new JScrollPane();
		JScrollPane psmTableScp = new JScrollPane();
		
		final JPanel proteinPnl = new JPanel();
		proteinPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));
	
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
		
		getResultsBtn = new JButton("Get Results");
		getResultsBtn.setEnabled(false);
		
		getResultsBtn.setPreferredSize(new Dimension(150, 20));
		getResultsBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dbSearchResult = clientFrame.getClient().getDbSearchResult(clientFrame.getProjectPnl().getCurrentProjContent(), clientFrame.getProjectPnl().getCurrentExperimentContent());
				// TODO: ADD search engine from runtable
				List<String> searchEngines = new ArrayList<String>(Arrays.asList(new String [] {"Crux", "Inspect", "Xtandem","OMSSA"}));
				dbSearchResult.setSearchEngines(searchEngines);
				
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
		proteinPnl.add(proteinTableScp, CC.xy(2, 2));
		
		JXTitledPanel protTtlPnl = new JXTitledPanel("Proteins", proteinPnl);
		protTtlPnl.setRightDecoration(getResultsBtn);
		protTtlPnl.setTitleFont(ttlFont);
		protTtlPnl.setTitlePainter(ttlPainter);
		protTtlPnl.setBorder(ttlBorder);
				
		// Peptide panel
		final JPanel peptidePnl = new JPanel();
		peptidePnl.setLayout(new FormLayout("5dlu, p:g, 5dlu",  "5dlu, f:p:g, 5dlu"));
		
		peptidePnl.add(peptideTableScp, CC.xy(2, 2));
		
		JXTitledPanel pepTtlPnl = new JXTitledPanel("Peptides", peptidePnl);
		pepTtlPnl.setTitleFont(ttlFont);
		pepTtlPnl.setTitlePainter(ttlPainter);
		pepTtlPnl.setBorder(ttlBorder);
		
		// PSM panel
		final JPanel psmPanel = new JPanel();
		psmPanel.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));
		psmPanel.add(psmTableScp, CC.xy(2, 2));
		
		JXTitledPanel psmTtlPnl = new JXTitledPanel("Peptide-Spectrum-Matches", psmPanel);
		psmTtlPnl.setTitleFont(ttlFont);
		psmTtlPnl.setTitlePainter(ttlPainter);
		psmTtlPnl.setBorder(ttlBorder);

		// Peptide and Psm Panel
		JPanel pepPsmPnl = new JPanel(new FormLayout("p:g","f:p:g, 5dlu, f:p:g"));
		
		pepPsmPnl.add(pepTtlPnl, CC.xy(1, 1));
		pepPsmPnl.add(psmTtlPnl, CC.xy(1, 3));
		
		// Build the spectrum overview panel
		JPanel spectrumOverviewPnl = new JPanel(new BorderLayout());
		
		spectrumJPanel = new JPanel();
		spectrumJPanel.setLayout(new BoxLayout(spectrumJPanel, BoxLayout.LINE_AXIS));
		spectrumJPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 0));
		spectrumJPanel.add(new SpectrumPanel(new double[] { 0.0, 100.0 }, new double[] { 100.0, 0.0 }, 0.0, "", ""));
		spectrumJPanel.setMinimumSize(new Dimension(300, 200));
		
		spectrumOverviewPnl.add(spectrumJPanel, BorderLayout.CENTER);
		spectrumOverviewPnl.add(constructSpectrumFilterPanel(), BorderLayout.EAST);
		
		JXTitledPanel specTtlPnl = new JXTitledPanel("Spectrum Viewer", spectrumOverviewPnl); 
		specTtlPnl.setTitleFont(ttlFont);
		specTtlPnl.setTitlePainter(ttlPainter);
		specTtlPnl.setBorder(ttlBorder);
		
		String layoutDef =
		    "(COLUMN protein (ROW weight=0.0 (COLUMN (LEAF weight=0.5 name=peptide) (LEAF weight=0.5 name=psm)) plot))";
		MultiSplitLayout.Node modelRoot = MultiSplitLayout.parseModel(layoutDef);
		
		final JXMultiSplitPane multiSplitPane = new JXMultiSplitPane();
		multiSplitPane.setDividerSize(12);
		multiSplitPane.getMultiSplitLayout().setModel(modelRoot);
		multiSplitPane.add(protTtlPnl, "protein");
		multiSplitPane.add(pepTtlPnl, "peptide");
		multiSplitPane.add(psmTtlPnl, "psm");
		multiSplitPane.add(specTtlPnl, "plot");
		
		this.add(multiSplitPane, CC.xy(2, 2));
	}
	
	/**
	 * This method sets up the protein results table.
	 */
	private void setupProteinTableProperties(){
		// Protein table
		proteinTbl = new JXTable(new DefaultTableModel() {
			// instance initializer block
			{ setColumnIdentifiers(new Object[] {" ", "Accession", "Description", "Coverage (%)", "Mass (kDa)", "pI", "Peptide Count", "Spectral Count", "emPAI", "NSAF x100"}); }
	
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		});
		proteinTbl.getColumn(" ").setMinWidth(30);
		proteinTbl.getColumn(" ").setMaxWidth(30);
		proteinTbl.getColumn(" ").setCellRenderer(new CustomTableCellRenderer(SwingConstants.RIGHT));
		proteinTbl.getColumn("Accession").setMinWidth(70);
		proteinTbl.getColumn("Accession").setMaxWidth(70);
		
		AbstractHyperlinkAction<URI> linkAction = new AbstractHyperlinkAction<URI>() {
		    public void actionPerformed(ActionEvent ev) {
		        try {
		            Desktop.getDesktop().browse(new URI("http://www.uniprot.org/uniprot/" + target));
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		    }
		};
		proteinTbl.getColumn("Accession").setCellRenderer(new DefaultTableRenderer(new HyperlinkProvider(linkAction)){
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				JXRendererHyperlink compLabel = (JXRendererHyperlink) comp;
				compLabel.setHorizontalAlignment(SwingConstants.CENTER);
				return compLabel;
			}
		});

		proteinTbl.getColumn("Coverage (%)").setMinWidth(90);
		proteinTbl.getColumn("Coverage (%)").setMaxWidth(90);
		proteinTbl.getColumn("Coverage (%)").setCellRenderer(new CustomTableCellRenderer(SwingConstants.CENTER));
		proteinTbl.getColumn("Mass (kDa)").setMinWidth(90);
		proteinTbl.getColumn("Mass (kDa)").setMaxWidth(90);
		proteinTbl.getColumn("Mass (kDa)").setCellRenderer(new CustomTableCellRenderer(SwingConstants.CENTER));
		proteinTbl.getColumn("pI").setMinWidth(90);
		proteinTbl.getColumn("pI").setMaxWidth(90);
		proteinTbl.getColumn("pI").setCellRenderer(new TableConfig.CustomTableCellRenderer(SwingConstants.CENTER, "#0.00"));
		proteinTbl.getColumn("Peptide Count").setMinWidth(110);
		proteinTbl.getColumn("Peptide Count").setMaxWidth(110);
		proteinTbl.getColumn("Spectral Count").setMinWidth(115);
		proteinTbl.getColumn("Spectral Count").setMaxWidth(115);
		proteinTbl.getColumn("emPAI").setMinWidth(115);
		proteinTbl.getColumn("emPAI").setMaxWidth(115);
		proteinTbl.getColumn("emPAI").setCellRenderer(new JSparklinesBarChartTableCellRenderer(PlotOrientation.HORIZONTAL, 0.0, 9.0));
		((JSparklinesBarChartTableCellRenderer) proteinTbl.getColumn("emPAI").getCellRenderer()).showNumberAndChart(true, 40, new DecimalFormat("0.000"));
		proteinTbl.getColumn("NSAF x100").setMinWidth(115);
		proteinTbl.getColumn("NSAF x100").setMaxWidth(115);
		
		
		// Sort the table by the number of peptides
		TableRowSorter<TableModel> sorter  = new TableRowSorter<TableModel>(proteinTbl.getModel());
		List <RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
		// Sort the table by the descending spectral count.
		sortKeys.add(new RowSorter.SortKey(6, SortOrder.DESCENDING));
		
		sorter.setSortKeys(sortKeys);
		proteinTbl.setRowSorter(sorter);
		
		proteinTbl.addMouseListener(new java.awt.event.MouseAdapter() {
			
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				queryProteinTableMouseClicked(evt);
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
		
		TableConfig.setColumnWidths(peptideTbl, new double[] {3, 24, 12});
		
		TableColumnModel tcm = peptideTbl.getColumnModel();
		tcm.getColumn(0).setCellRenderer(new TableConfig.CustomTableCellRenderer(SwingConstants.RIGHT));
		
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
			{ setColumnIdentifiers(new Object[] {" ", "Sequence", "z", "Votes"}); }
	
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		});
//		psmTbl.getColumn(" ").setMinWidth(30);
//		psmTbl.getColumn(" ").setMaxWidth(30);
		psmTbl.getColumn(" ").setCellRenderer(new CustomTableCellRenderer(SwingConstants.RIGHT));
//		psmTbl.getColumn("z").setMinWidth(40);
//		psmTbl.getColumn("z").setMaxWidth(40);
		psmTbl.getColumn("z").setCellRenderer(new CustomTableCellRenderer(SwingConstants.CENTER));
//		psmTbl.getColumn("Votes").setMinWidth(40);
//		psmTbl.getColumn("Votes").setMaxWidth(40);
		TableConfig.setColumnWidths(psmTbl, new double[]{1, 8, 2, 2});
		psmTbl.getColumn("Votes").setCellRenderer(new CustomTableCellRenderer(SwingConstants.CENTER));
		
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
			currentPeptideHits = dbSearchResult.getProteinHit(accession).getPeptideHits();
			Set<Entry<String, PeptideHit>> entrySet = dbSearchResult.getProteinHit(accession).getPeptideHits().entrySet();
			
			// Counter variable
			int i = 1;
			int peptideSpectraCount = 0;
			maxPeptideSpectraCount = 0;
			// Iterate the found peptide results
			for (Entry<String, PeptideHit> entry : entrySet) {
					// Get the peptide hit.
					PeptideHit peptideHit = entry.getValue();
					
					// Get number of spectra
					peptideSpectraCount = peptideHit.getSpectrumMatches().size();
					
					// Get maximum number of spectra
					maxPeptideSpectraCount= max(maxPeptideSpectraCount, peptideSpectraCount);
					
					// Add rows to the table.
					((DefaultTableModel) peptideTbl.getModel()).addRow(new Object[]{
							i,
							peptideHit.getSequence(),
							peptideHit.getSpectrumMatches().size()});
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
		if (row != -1) {

			// Empty tables.
			clearPsmResultTable();

			currentSelectedPeptide = peptideTbl.getValueAt(row, 1).toString();
			Set<Entry<Long, SpectrumMatch>> entrySet = currentPeptideHits.get(currentSelectedPeptide).getSpectrumMatches().entrySet();
			
			// Clear the currentpsms map.
			currentPsms.clear();
			
			
			int i = 1;
			// Iterate the found peptide results
			for (Entry<Long, SpectrumMatch> entry : entrySet) {
				PeptideSpectrumMatch psm = (PeptideSpectrumMatch) entry.getValue();
				
				// Add current PSM to the currentPsms map
				currentPsms.put(i, psm);
				// Add the PSM to the table.
				((DefaultTableModel) psmTbl.getModel()).addRow(new Object[]{
							i,
							currentSelectedPeptide,
							"+"+ psm.getCharge(),
							psm.getVotes()});
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
		if (row != -1) {

			// Empty spectrum panel.
            while (spectrumJPanel.getComponents().length > 0) {
                spectrumJPanel.remove(0);
            }

			PeptideSpectrumMatch psm = currentPsms.get(Integer.valueOf(psmTbl.getValueAt(row, 0).toString()));
			
			// Calculate fragmentIons
			Fragmentizer fragmentizer = new Fragmentizer(currentSelectedPeptide, Masses.getMap(), psm.getCharge());
			
			
			// Get the mascot generic file directly from the database.
			try {
				Searchspectrum searchSpectrum = Searchspectrum.findFromSearchSpectrumID(psm.getSpectrumId(), clientFrame.getClient().getConnection());
				MascotGenericFile mgf = SpectrumExtractor.getMascotGenericFile(searchSpectrum.getFk_spectrumid(), clientFrame.getClient().getConnection());
				specPnl = new SpectrumPanel(mgf);
				spectrumJPanel.add(specPnl, CC.xy(2, 2));
		        spectrumJPanel.validate();
		        spectrumJPanel.repaint();

				addSpectrumAnnotations(fragmentizer.getFragmentIons());
				
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
	private JPanel constructSpectrumFilterPanel() {
		
		JPanel spectrumFilterPanel = new JPanel(new FormLayout("5dlu, p, 5dlu", "5dlu:g(0.25), p, 2dlu, p, 2dlu, p, c:p:g, p, 2dlu, p, 2dlu, p, c:p:g, p, 2dlu, p, c:p:g, p, 2dlu, p, 2dlu, p, c:p:g, p, 5dlu:g(0.25)"));
		
		Dimension size = new Dimension(39, 23);
		Action action = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				updateFilteredAnnotations();
			}
		};
		
		aIonsChk = createFilterCheckBox("a", false, "Show a ions", size, action);
        bIonsChk = createFilterCheckBox("b", true, "Show b ions", size, action);
        cIonsChk = createFilterCheckBox("c", false, "Show c ions", size, action);

        xIonsChk = createFilterCheckBox("x", false, "Show x ions", size, action);
        yIonsChk = createFilterCheckBox("y", true, "Show y ions", size, action);
        zIonsChk = createFilterCheckBox("z", false, "Show z ions", size, action);

        waterLossChk = createFilterCheckBox("\u00B0", false, "<html>Show H<sub>2</sub>0 losses</html>", size, action);
        ammoniumLossChk = createFilterCheckBox("*", false, "<html>Show NH<sub>3</sub> losses</html>", size, action);
        
        chargeOneChk = createFilterCheckBox("+", true, "Show ions with charge 1", size, action);
        chargeTwoChk = createFilterCheckBox("++", false, "Show ions with charge 2", size, action);
        chargeMoreChk = createFilterCheckBox(">2", false, "Show ions with charge >2", size, action);
        
        precursorChk = createFilterCheckBox("MH", true, "Show precursor ion", size, action);

        spectrumFilterPanel.add(aIonsChk, CC.xy(2, 2));
        spectrumFilterPanel.add(bIonsChk, CC.xy(2, 4));
        spectrumFilterPanel.add(cIonsChk, CC.xy(2, 6));
        spectrumFilterPanel.add(new JSeparator(JSeparator.HORIZONTAL), CC.xy(2, 7));
        spectrumFilterPanel.add(xIonsChk, CC.xy(2, 8));
        spectrumFilterPanel.add(yIonsChk, CC.xy(2, 10));
        spectrumFilterPanel.add(zIonsChk, CC.xy(2, 12));
        spectrumFilterPanel.add(new JSeparator(JSeparator.HORIZONTAL), CC.xy(2, 13));
        spectrumFilterPanel.add(waterLossChk, CC.xy(2, 14));
        spectrumFilterPanel.add(ammoniumLossChk, CC.xy(2, 16));
        spectrumFilterPanel.add(new JSeparator(JSeparator.HORIZONTAL), CC.xy(2, 17));
        spectrumFilterPanel.add(chargeOneChk, CC.xy(2, 18));
        spectrumFilterPanel.add(chargeTwoChk, CC.xy(2, 20));
        spectrumFilterPanel.add(chargeMoreChk, CC.xy(2, 22));
        spectrumFilterPanel.add(new JSeparator(JSeparator.HORIZONTAL), CC.xy(2, 23));
        spectrumFilterPanel.add(precursorChk, CC.xy(2, 24));
        
        return spectrumFilterPanel;
	}
	
	private JCheckBox createFilterCheckBox(String title, boolean selected, String toolTip, Dimension size, Action action) {
		JCheckBox checkBox = new JCheckBox(action);
		checkBox.setText(title);
		checkBox.setSelected(selected);
		checkBox.setToolTipText(toolTip);
		checkBox.setMaximumSize(size);
		checkBox.setMinimumSize(size);
		checkBox.setPreferredSize(size);
		return checkBox;
	}

	/**
	 * This method updates the filtered annotations due to the respective selected checkboxes.
	 */
	private void updateFilteredAnnotations() {
		if (currentAnnotations != null) {
	        specPnl.setAnnotations(filterAnnotations(currentAnnotations));
	        specPnl.validate();
	        specPnl.repaint();
		}
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
                if (!aIonsChk.isSelected()) {
                    useAnnotation = false;
                }
            } else if (currentLabel.lastIndexOf("b") != -1) {
                if (!bIonsChk.isSelected()) {
                    useAnnotation = false;
                }
            } else if (currentLabel.lastIndexOf("c") != -1) {
                if (!cIonsChk.isSelected()) {
                    useAnnotation = false;
                }
            } else if (currentLabel.lastIndexOf("x") != -1) {
                if (!xIonsChk.isSelected()) {
                    useAnnotation = false;
                }
            } else if (currentLabel.lastIndexOf("y") != -1) {
                if (!yIonsChk.isSelected()) {
                    useAnnotation = false;
                }
            } else if (currentLabel.lastIndexOf("z") != -1) {
                if (!zIonsChk.isSelected()) {
                    useAnnotation = false;
                }
            } else if (currentLabel.lastIndexOf("M") != -1) {
                if (!precursorChk.isSelected()) {
                    useAnnotation = false;
                }
            }

            // check ion charge + ammonium and water-loss
            if (useAnnotation) {
                if (currentLabel.lastIndexOf("+") == -1) {
                    if (!chargeOneChk.isSelected()) {
                        useAnnotation = false;
                    }
                } else if (currentLabel.lastIndexOf("+++") != -1) {
                    if (!chargeMoreChk.isSelected()) {
                        useAnnotation = false;
                    }
                } else if (currentLabel.lastIndexOf("++") != -1) {
                    if (!chargeTwoChk.isSelected()) {
                        useAnnotation = false;
                    }
                }
                
                if (currentLabel.lastIndexOf("*") != -1) {
                    if (!ammoniumLossChk.isSelected()) {
                        useAnnotation = false;
                    }
                } else if (currentLabel.lastIndexOf("°") != -1) {
                    if (!waterLossChk.isSelected()) {
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
		double maxNSAF = 0.0;
		double minNSAF = Double.MAX_VALUE;
		// Fill the protein results table.
		if (dbSearchResult != null) {
			
			// Iterate the found protein results
			for (Entry entry : dbSearchResult.getProteinHits().entrySet()){
				
				// Get the protein hit
				ProteinHit proteinHit = (ProteinHit) entry.getValue();
				ProteinAnalysis.calculateMolecularWeight(proteinHit);
				ProteinAnalysis.calculatePI(proteinHit);
				ProteinAnalysis.calculateSequenceCoverage(proteinHit);
				ProteinAnalysis.calculateLabelFree(new EmPAIAlgorithm(), proteinHit);
				ProteinAnalysis.calculateLabelFree(new NSAFAlgorithm(), dbSearchResult.getProteinHits(), proteinHit);
				
				// Determine the number of containing peptide hits.
				int peptideCount = proteinHit.getPeptideCount();
				
				// Determine the number of containing psm hits 
				int spectralCount =  proteinHit.getSpecCount();
				
				// Determine the NSAF
				double nSAF = proteinHit.getNSAF();
				
				// Remember maximum number of peptides
				maxPeptideCount = Math.max(maxPeptideCount, peptideCount);
				
				// Remember maximum number of spectra
				maxSpectralCount = Math.max(maxSpectralCount, spectralCount);
				
				// Remember maximum of NSAF
				maxNSAF = Math.max(maxNSAF, nSAF);
				minNSAF = Math.min(minNSAF, nSAF);
				
				// Add row to the table
				((DefaultTableModel) proteinTbl.getModel()).addRow(new Object[]{
						i ,
						proteinHit.getAccession(),
						proteinHit.getDescription(),
						proteinHit.getCoverage(),
						proteinHit.getMolWeight(),
						proteinHit.getpI(),
						peptideCount, 
						spectralCount,
						proteinHit.getEmPAI(),
						proteinHit.getNSAF()}
				);
						
				i++;
			}
			proteinTbl.getColumn("Spectral Count").setCellRenderer(new JSparklinesBarChartTableCellRenderer(PlotOrientation.HORIZONTAL,(double) maxSpectralCount, true));
			((JSparklinesBarChartTableCellRenderer) proteinTbl.getColumn("Spectral Count").getCellRenderer()).showNumberAndChart(true, 20, UIManager.getFont("Label.font").deriveFont(12f), SwingConstants.LEFT);
			
			proteinTbl.getColumn("Peptide Count").setCellRenderer(new JSparklinesBarChartTableCellRenderer(PlotOrientation.HORIZONTAL, (double) maxPeptideCount, true));
			((JSparklinesBarChartTableCellRenderer) proteinTbl.getColumn("Peptide Count").getCellRenderer()).showNumberAndChart(true, 20, UIManager.getFont("Label.font").deriveFont(12f), SwingConstants.LEFT);
		
			JSparklinesBarChartTableCellRenderer renderer = new JSparklinesBarChartTableCellRenderer(PlotOrientation.HORIZONTAL, minNSAF, maxNSAF);
			proteinTbl.getColumn("NSAF x100").setCellRenderer(renderer);
			((JSparklinesBarChartTableCellRenderer) proteinTbl.getColumn("NSAF x100").getCellRenderer()).showNumberAndChart(true, 40, new DecimalFormat("0.000000"));
			
			
		}
	}
	
	/**
	 * This method enables the get results button.
	 */
	public void setResultsBtnEnabled(boolean enabled){
		getResultsBtn.setEnabled(enabled);
	}
}
