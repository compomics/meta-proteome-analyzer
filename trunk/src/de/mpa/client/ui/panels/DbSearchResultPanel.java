package de.mpa.client.ui.panels;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.net.URI;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.RowSorter.SortKey;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.MultiSplitLayout;
import org.jdesktop.swingx.hyperlink.AbstractHyperlinkAction;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.HyperlinkProvider;
import org.jdesktop.swingx.renderer.JXRendererHyperlink;
import org.jdesktop.swingx.sort.SortUtils;
import org.jdesktop.swingx.sort.TableSortController;
import org.jdesktop.swingx.table.ColumnControlButton;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;

import com.compomics.util.gui.interfaces.SpectrumAnnotation;
import com.compomics.util.gui.spectrum.DefaultSpectrumAnnotation;
import com.compomics.util.gui.spectrum.SpectrumPanel;
import com.jgoodies.binding.adapter.RadioButtonAdapter;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.algorithms.Interval;
import de.mpa.algorithms.quantification.NormalizedSpectralAbundanceFactor;
import de.mpa.analysis.Masses;
import de.mpa.analysis.ProteinAnalysis;
import de.mpa.client.Client;
import de.mpa.client.model.SpectrumMatch;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.PeptideSpectrumMatch;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.ui.BarChartHighlighter;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.ClientFrameMenuBar;
import de.mpa.client.ui.ComponentHeader;
import de.mpa.client.ui.ComponentHeaderRenderer;
import de.mpa.client.ui.Constants;
import de.mpa.client.ui.PanelConfig;
import de.mpa.client.ui.SortableCheckBoxTreeTable;
import de.mpa.client.ui.SortableCheckBoxTreeTableNode;
import de.mpa.client.ui.SortableTreeTableModel;
import de.mpa.client.ui.TableConfig;
import de.mpa.client.ui.WrapLayout;
import de.mpa.client.ui.TableConfig.CustomTableCellRenderer;
import de.mpa.client.ui.dialogs.FilterBalloonTip;
import de.mpa.client.ui.dialogs.GeneralExceptionHandler;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.db.accessor.Cruxhit;
import de.mpa.db.accessor.Inspecthit;
import de.mpa.db.accessor.Omssahit;
import de.mpa.db.accessor.SearchHit;
import de.mpa.db.accessor.XTandemhit;
import de.mpa.fragmentation.FragmentIon;
import de.mpa.fragmentation.Fragmentizer;
import de.mpa.io.MascotGenericFile;

public class DbSearchResultPanel extends JPanel {
	
	private ClientFrame clientFrame;
	
	private JXTable proteinTbl;
	protected FilterButton lastSelectedFilterBtn = new FilterButton(0, null,0);
	private DbSearchResult dbSearchResult;
	private JXTable peptideTbl;
	private JXTable psmTbl;
	private SpectrumPanel specPnl;
	private JToggleButton aIonsTgl;
	private JToggleButton bIonsTgl;
	private JToggleButton cIonsTgl;
	private JToggleButton yIonsTgl;
	private JToggleButton xIonsTgl;
	private JToggleButton zIonsTgl;
	private JToggleButton waterLossTgl;
	private JToggleButton ammoniumLossTgl;
	private JToggleButton chargeOneTgl;
	private JToggleButton chargeTwoTgl;
	private JToggleButton chargeMoreTgl;
	private JToggleButton precursorTgl;
	private Vector<SpectrumAnnotation> currentAnnotations;
	private JPanel spectrumJPanel;
	private JButton getResultsBtn;
	private Font chartFont;

	private JPanel coveragePnl;
	
	FilterBalloonTip filterTip;

	private JXTitledPanel pepTtlPnl;

	private JXTitledPanel protTtlPnl;

	private JXTitledPanel psmTtlPnl;

	private SortableCheckBoxTreeTable protFlatTreeTbl;
	
	/**
	 * Constructor for a database results panel.
	 * @param clientFrame The parent frame.
	 */
	public DbSearchResultPanel() {
		this.clientFrame = ClientFrame.getInstance();
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
		
		final JPanel proteinPnl = new JPanel();
		proteinPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));
	
		// Setup tables
		chartFont = UIManager.getFont("Label.font");
		setupProteinTableProperties();
		setupPeptideTableProperties();
		setupCoverageViewer();
		setupPsmTableProperties();
		
		// Scroll panes
		JScrollPane proteinTableScp = new JScrollPane(proteinTbl);
		proteinTableScp.setPreferredSize(new Dimension(800, 180));
		JScrollPane peptideTableScpn = new JScrollPane(peptideTbl);
		peptideTableScpn.setPreferredSize(new Dimension(350, 130));
		JScrollPane coverageScpn = new JScrollPane(coveragePnl);
		coverageScpn.setPreferredSize(new Dimension(350, 130));
		coverageScpn.getVerticalScrollBar().setUnitIncrement(16);
		coverageScpn.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		JScrollPane psmTableScp = new JScrollPane(psmTbl);
		psmTableScp.setPreferredSize(new Dimension(350, 130));
		
		getResultsBtn = new JButton("Get Results   ", IconConstants.REFRESH_DB_ICON);
		getResultsBtn.setRolloverIcon(IconConstants.REFRESH_DB_ROLLOVER_ICON);
		getResultsBtn.setPressedIcon(IconConstants.REFRESH_DB_PRESSED_ICON);
		
		getResultsBtn.setEnabled(false);
		
		getResultsBtn.setPreferredSize(new Dimension(getResultsBtn.getPreferredSize().width, 20));
		getResultsBtn.setFocusPainted(false);
		
		getResultsBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getResultsButtonPressed();
			}
		});
//		proteinPnl.add(proteinTableScp, CC.xy(2, 2));
		
		String[] cardLabels = new String[] {"Original", "Flat View", "Taxonomic View", "E.C. View"};
		
		final CardLayout protCardLyt = new CardLayout();
		final JPanel protCardPnl = new JPanel(protCardLyt);
		protCardPnl.add(proteinTableScp, cardLabels[0]);
		
		setupProteinTreeTables();
		JScrollPane protFlatTreeScpn = new JScrollPane(protFlatTreeTbl);
		protFlatTreeScpn.setPreferredSize(new Dimension(800, 180));
		JScrollPane protTaxonTreeScpn = new JScrollPane();	// TODO: insert proper tree tables
		protTaxonTreeScpn.setPreferredSize(new Dimension(800, 180));
		JScrollPane protEnzymeTreeScpn = new JScrollPane();
		protEnzymeTreeScpn.setPreferredSize(new Dimension(800, 180));
		
		protCardPnl.add(protFlatTreeScpn, cardLabels[1]);
		protCardPnl.add(protTaxonTreeScpn, cardLabels[2]);
		protCardPnl.add(protEnzymeTreeScpn, cardLabels[3]);
		
		proteinPnl.add(protCardPnl, CC.xy(2, 2));
		
		JPanel protBtnPnl = new JPanel(new FormLayout("p, 5dlu, p", "p"));
		protBtnPnl.setOpaque(false);
		
		JComboBox swapCbx = new JComboBox(cardLabels);
		swapCbx.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				protCardLyt.show(protCardPnl, (String) e.getItem());
			}
		});
		swapCbx.setPreferredSize(new Dimension(swapCbx.getPreferredSize().width, 20));
		
		protBtnPnl.add(swapCbx, CC.xy(1, 1));
		protBtnPnl.add(getResultsBtn, CC.xy(3, 1));
		
		protTtlPnl = new JXTitledPanel("Proteins", proteinPnl);
//		protTtlPnl.setRightDecoration(getResultsBtn);
		protTtlPnl.setRightDecoration(protBtnPnl);
		protTtlPnl.setTitleFont(ttlFont);
		protTtlPnl.setTitlePainter(ttlPainter);
		protTtlPnl.setBorder(ttlBorder);
				
		// Peptide panel
		final CardLayout cl = new CardLayout();
		final JPanel peptidePnl = new JPanel(cl);
		
		final JPanel pepTblScpnPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));
		pepTblScpnPnl.add(peptideTableScpn, CC.xy(2, 2));
		final JPanel cvgTblScpnPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));
		cvgTblScpnPnl.add(coverageScpn, CC.xy(2, 2));
		
		// Add cards
		peptidePnl.add(pepTblScpnPnl, "Table");
		peptidePnl.add(cvgTblScpnPnl, "Coverage");

		// build control button panel for card layout
		JButton nextBtn = new JButton("\u203A");
		nextBtn.setPreferredSize(new Dimension(19, 18));
		
		// Wrap peptide panel in titled panel with control buttons in title
		pepTtlPnl = new JXTitledPanel("Peptides", peptidePnl);
		pepTtlPnl.setTitleFont(ttlFont);
		pepTtlPnl.setTitlePainter(ttlPainter);
		pepTtlPnl.setBorder(ttlBorder);
		pepTtlPnl.setRightDecoration(nextBtn);
		
		nextBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cl.previous(peptidePnl);
				Component[] components = peptidePnl.getComponents();
				for (Component component : components) {
					if (component.isVisible()) {
						if (component == pepTblScpnPnl) {
							pepTtlPnl.setTitle("Peptides");
						} else {
							pepTtlPnl.setTitle("Sequence Coverage Viewer");
						}
					}
				}
			}
		});
		
		// PSM panel
		final JPanel psmPanel = new JPanel();
		psmPanel.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));
		psmPanel.add(psmTableScp, CC.xy(2, 2));
		
		psmTtlPnl = new JXTitledPanel("Peptide-Spectrum-Matches", psmPanel);
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
		spectrumJPanel.setMinimumSize(new Dimension(300, 180));
		
		spectrumOverviewPnl.add(spectrumJPanel, BorderLayout.CENTER);
		spectrumOverviewPnl.add(constructSpectrumFilterPanel(), BorderLayout.EAST);
		
		JXTitledPanel specTtlPnl = new JXTitledPanel("Spectrum Viewer", spectrumOverviewPnl); 
		specTtlPnl.setTitleFont(ttlFont);
		specTtlPnl.setTitlePainter(ttlPainter);
		specTtlPnl.setBorder(ttlBorder);
		
		// set up multi split pane
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
//		multiSplitPane.setPreferredSize(new Dimension(0, 0));
		
		this.add(multiSplitPane, CC.xy(2, 2));
	}
	
	protected void getResultsButtonPressed() {
		ResultsTask resultsTask = new ResultsTask();
		resultsTask.execute();
		
	}
	
	private class ResultsTask extends SwingWorker {

		protected Object doInBackground() {
			try {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				// Fetch the database search result.
				DbSearchResult newResult = Client.getInstance().getDbSearchResult(clientFrame.getProjectPanel().getCurrentProjectContent(), clientFrame.getProjectPanel().getCurrentExperimentContent());
				
				if (!newResult.equals(dbSearchResult)) {
					dbSearchResult = newResult;
					finished();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 0;
		}

		/**
		 * Continues when the results retrieval has finished.
		 */
		public void finished() {
			// TODO: ADD search engine from runtable
			List<String> searchEngines = new ArrayList<String>(Arrays.asList(new String [] {"Crux", "Inspect", "Xtandem","OMSSA"}));
			dbSearchResult.setSearchEngines(searchEngines);
			
			refreshProteinTable();
			
			// Enables the export functionality
			((ClientFrameMenuBar) clientFrame.getJMenuBar()).setExportResultsEnabled(true);
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}


	// Protein table column indices
	private final int PROT_SELECTION 		= 0;
	private final int PROT_INDEX 			= 1;
	private final int PROT_ACCESSION 		= 2;
	private final int PROT_DESCRIPTION 		= 3;
	private final int PROT_SPECIES 			= 4;
	private final int PROT_COVERAGE 		= 5;
	private final int PROT_MW 				= 6;
	private final int PROT_PI 				= 7;
	private final int PROT_PEPTIDECOUNT 	= 8;
	private final int PROT_SPECTRALCOUNT 	= 9;
	private final int PROT_EMPAI 			= 10;
	private final int PROT_NSAF 			= 11;

	/**
	 * This method sets up the protein results table.
	 */
	private void setupProteinTableProperties() {
		// Create protein table model
		final TableModel proteinTblMdl = new DefaultTableModel() {
			// instance initializer block
			{
				setColumnIdentifiers(new Object[] { "", "#", "Accession",
						"Description", "Species", "SC", "MW", "pI", "PepC",
						"SpC", "emPAI", "NSAF" });
			}
	
			public boolean isCellEditable(int row, int col) {
				return (col == PROT_SELECTION);
			}
			
			public Class<?> getColumnClass(int columnIndex) {
				switch (columnIndex) {
				case PROT_SELECTION: 
					return Boolean.class;
				case PROT_INDEX:	
				case PROT_PEPTIDECOUNT:
				case PROT_SPECTRALCOUNT:
					return Integer.class;
				case PROT_COVERAGE:
				case PROT_MW:
				case PROT_PI:
				case PROT_EMPAI:
				case PROT_NSAF:
					return Double.class;
				default:
					return String.class;
				}
			}
		};
		proteinTbl = new JXTable(proteinTblMdl) {
			public Border padding = BorderFactory.createEmptyBorder(0, 2, 0, 2);
			@Override
			public Component prepareRenderer(TableCellRenderer renderer,
					int row, int column) {
				Component comp = super.prepareRenderer(renderer, row, column);
				if (comp instanceof JComponent) {
					((JComponent) comp).setBorder(padding);
				}
				return comp;
			}
			@Override
			public void setValueAt(Object aValue, int row, int column) {
				super.setValueAt(aValue, row, column);
				if (column == PROT_SELECTION) {
					dbSearchResult.getProteinHits().get(getValueAt(row,PROT_ACCESSION)).setSelected((Boolean) aValue);
				}
			}
		};
		
		// Adjust column widths
		TableConfig.setColumnWidths(proteinTbl, new double[] { 0, 2.5, 5.5, 15, 14, 5, 4, 3, 4, 4, 4.5, 5 });
		TableConfig.setColumnMinWidths(proteinTbl,
				UIManager.getIcon("Table.ascendingSortIcon").getIconWidth(),
				createFilterButton(0, null, 0).getPreferredSize().width + 8);
		
		// Get table column model
		final TableColumnModel tcm = proteinTbl.getColumnModel();

		// Apply component header capabilities as well as column header tooltips
		String[] columnToolTips = {
			    "Selection for Export",
			    "Result Index",
			    "Protein Accession Number",
			    "Protein Description",
			    "Species",
			    "Sequence Coverage in %",
			    "Molecular Weight in kDa",
			    "Isoelectric Point",
			    "Peptide Count",
			    "Spectral Count",
			    "Exponentially Modified Protein Abundance Index",
			    "Normalized Spectral Abundance Factor"};
		ComponentHeader ch = new ComponentHeader(tcm, columnToolTips);
//		ch.setReorderingAllowed(false, PROT_SELECTION);
		proteinTbl.setTableHeader(ch);
		
		final JCheckBox selChk = new JCheckBox() {
			public void paint(Graphics g) {
				// TODO: make checkbox honor tri-state
				super.paint(g);
//				if (selected == null) {
//					Color col = (isEnabled()) ? Color.BLACK : UIManager.getColor("controlShadow");
//					g.setColor(col);
//					g.fillRect(center, 8, 8, 2);
//				}
			}
		};
		selChk.setPreferredSize(new Dimension(15, 15));
		selChk.setSelected(true);

		// Add filter button and checkbox widgets to column headers
		for (int col = PROT_SELECTION; col < tcm.getColumnCount(); col++) {
			switch (col) {
			case PROT_SELECTION:
				tcm.getColumn(col).setHeaderRenderer(new ComponentHeaderRenderer(selChk, null));
				tcm.getColumn(col).setMinWidth(19);
				tcm.getColumn(col).setMaxWidth(19);
				break;
			case PROT_INDEX:
			case PROT_COVERAGE:
			case PROT_MW:
			case PROT_PI:
			case PROT_PEPTIDECOUNT:
			case PROT_SPECTRALCOUNT:
			case PROT_EMPAI:
			case PROT_NSAF:
				tcm.getColumn(col).setHeaderRenderer(new ComponentHeaderRenderer(createFilterButton(col, proteinTbl, Constants.NUMERICAL)));
				break;
			case PROT_ACCESSION: 
			case PROT_DESCRIPTION:
			case PROT_SPECIES: 
				tcm.getColumn(col).setHeaderRenderer(new ComponentHeaderRenderer(createFilterButton(col, proteinTbl, Constants.ALPHA_NUMERICAL)));
				break;
			}
		}
		
		// Apply custom cell renderers/highlighters to columns 
		tcm.getColumn(PROT_INDEX).setCellRenderer(new CustomTableCellRenderer(SwingConstants.RIGHT));
		
		AbstractHyperlinkAction<URI> linkAction = new AbstractHyperlinkAction<URI>() {
		    public void actionPerformed(ActionEvent ev) {
		        try {
		            Desktop.getDesktop().browse(new URI("http://www.uniprot.org/uniprot/" + target));
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		    }
		};
		tcm.getColumn(PROT_ACCESSION).setCellRenderer(new DefaultTableRenderer(new HyperlinkProvider(linkAction)) {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				JXRendererHyperlink compLabel = (JXRendererHyperlink) comp;
				compLabel.setHorizontalAlignment(SwingConstants.CENTER);
				return compLabel;
			}
		});
		tcm.getColumn(PROT_DESCRIPTION).setCellRenderer(new CustomTableCellRenderer(SwingConstants.LEFT));
		tcm.getColumn(PROT_SPECIES).setCellRenderer(new CustomTableCellRenderer(SwingConstants.LEFT));
		DecimalFormat x100formatter = new DecimalFormat("0.00");
		x100formatter.setMultiplier(100);
		((TableColumnExt) tcm.getColumn(PROT_COVERAGE)).addHighlighter(new BarChartHighlighter(
				0.0, 100.0, 50, SwingConstants.HORIZONTAL, Color.GREEN.darker().darker(), Color.GREEN, x100formatter));
		tcm.getColumn(PROT_MW).setCellRenderer(new CustomTableCellRenderer(SwingConstants.CENTER, "0.000"));
		tcm.getColumn(PROT_PI).setCellRenderer(new CustomTableCellRenderer(SwingConstants.CENTER, "0.00"));
		((TableColumnExt) tcm.getColumn(PROT_PEPTIDECOUNT)).addHighlighter(new BarChartHighlighter());
		((TableColumnExt) tcm.getColumn(PROT_SPECTRALCOUNT)).addHighlighter(new BarChartHighlighter());
		((TableColumnExt) tcm.getColumn(PROT_EMPAI)).addHighlighter(new BarChartHighlighter(
				Color.RED.darker().darker(), Color.RED, new DecimalFormat("0.00")));
		((TableColumnExt) tcm.getColumn(PROT_NSAF)).addHighlighter(new BarChartHighlighter(
				Color.RED.darker().darker(), Color.RED, new DecimalFormat("0.00000")));
		
		// Make table always sort primarily by selection state of selection column
		final SortKey selKey = new SortKey(PROT_SELECTION, SortOrder.DESCENDING);
		
		final TableSortController<TableModel> tsc = new TableSortController<TableModel>(proteinTblMdl) {
			@Override
			public void toggleSortOrder(int column) {
		        List<SortKey> keys = new ArrayList<SortKey>(getSortKeys());
		        SortKey sortKey = SortUtils.getFirstSortKeyForColumn(keys, column);
		        if (keys.indexOf(sortKey) == 1)  {
		            // primary key: cycle sort order
		        	SortOrder so = (sortKey.getSortOrder() == SortOrder.ASCENDING) ?
		        			SortOrder.DESCENDING : SortOrder.ASCENDING;
		            keys.set(1, new SortKey(column, so));
		        } else {
		            // all others: insert new 
		            keys.remove(sortKey);
		            keys.add(1, new SortKey(column, SortOrder.DESCENDING));
		        }
		        if (keys.size() > getMaxSortKeys()) {
		            keys = keys.subList(0, getMaxSortKeys());
		        }
		        setSortKeys(keys);
			}
			@Override
			public void setSortKeys(
					List<? extends SortKey> sortKeys) {
				List<SortKey> newKeys = new ArrayList<SortKey>(sortKeys);
				// make sure selection column sorting always occurs before other columns
				newKeys.remove(selKey);
				newKeys.add(0, selKey);
				super.setSortKeys(newKeys);
			}
		};
		proteinTbl.setRowSorter(tsc);
		
		// register action listener on selection column header checkbox
		selChk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean selected = selChk.isSelected();
				// prevent auto-resorting while iterating rows for performance reasons
				tsc.setSortsOnUpdates(false);
				for (int row = 0; row < proteinTbl.getRowCount(); row++) {
					proteinTbl.setValueAt(selected, row, PROT_SELECTION);
				}
				tsc.setSortsOnUpdates(true);
				// re-sort rows after iteration finished
				tsc.sort();
			}
		});
		
		// Specify initial sort order
		List<SortKey> sortKeys = new ArrayList<SortKey>(2);
		sortKeys.add(0, new SortKey(PROT_SELECTION, SortOrder.DESCENDING));
		sortKeys.add(1, new SortKey(PROT_SPECTRALCOUNT, SortOrder.DESCENDING));
		tsc.setSortKeys(sortKeys);
		
		// Prevent messing with sort order of selection column
		tsc.setSortable(PROT_SELECTION, false);
		
		// Register list selection listener
		proteinTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				refreshPeptideViews();
//				refreshCoverageViewer();
			}
		});
		
		// Only one row is selectable
		proteinTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Add nice striping effect
		proteinTbl.addHighlighter(TableConfig.getSimpleStriping());
		
		// Enables column control
		proteinTbl.setColumnControlVisible(true);
		proteinTbl.getColumnControl().setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(1, 1, 0, 0, Color.WHITE),
				BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY)));
		proteinTbl.getColumnControl().setOpaque(false);
		((ColumnControlButton) proteinTbl.getColumnControl()).setAdditionalActionsVisible(false);
	}
	
	private void setupProteinTreeTables() {

		
		// XXX: protein table as sortable checkbox tree table, lots of test code to be removed later on :)
		SortableCheckBoxTreeTableNode protTreeRoot = new SortableCheckBoxTreeTableNode("root");
		SortableTreeTableModel protTreeTblMdl = new SortableTreeTableModel(protTreeRoot) {
			{ setColumnIdentifiers(Arrays.asList(
					new String[] { "Accession", "Description", "Species", "SC",
							"MW", "pI", "PepC", "SpC", "emPAI", "NSAF" } ));
			}
		};
		protFlatTreeTbl = new SortableCheckBoxTreeTable(protTreeTblMdl) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return (column == getHierarchicalColumn());
			}
		};
		
		BasicTreeUI btui = (BasicTreeUI) ((JXTree) (protFlatTreeTbl.getCellRenderer(0, protFlatTreeTbl.getHierarchicalColumn()))).getUI();
		btui.setLeftChildIndent(5);
		btui.setRightChildIndent(7);
		
//		protTreeTbl.setShowsRootHandles(false);
		protFlatTreeTbl.setRootVisible(false);
		ImageIcon proteinIcon = new ImageIcon(getClass().getResource("/de/mpa/resources/icons/protein.png"));
		protFlatTreeTbl.setLeafIcon(proteinIcon);

		TableColumnModel tcm = protFlatTreeTbl.getColumnModel();
		tcm.getColumn(1).setCellRenderer(new CustomTableCellRenderer(SwingConstants.LEFT));
		tcm.getColumn(2).setCellRenderer(new CustomTableCellRenderer(SwingConstants.LEFT));
		DecimalFormat x100formatter = new DecimalFormat("0.00");
		x100formatter.setMultiplier(100);
		((TableColumnExt) tcm.getColumn(3)).addHighlighter(new BarChartHighlighter(
				0.0, 100.0, 50, SwingConstants.HORIZONTAL, Color.GREEN.darker().darker(), Color.GREEN, x100formatter));
		tcm.getColumn(4).setCellRenderer(new CustomTableCellRenderer(SwingConstants.CENTER, "0.000"));
		tcm.getColumn(5).setCellRenderer(new CustomTableCellRenderer(SwingConstants.CENTER, "0.00"));
		((TableColumnExt) tcm.getColumn(6)).addHighlighter(new BarChartHighlighter());
		((TableColumnExt) tcm.getColumn(7)).addHighlighter(new BarChartHighlighter());
		((TableColumnExt) tcm.getColumn(8)).addHighlighter(new BarChartHighlighter(
				Color.RED.darker().darker(), Color.RED, new DecimalFormat("0.00")));
		((TableColumnExt) tcm.getColumn(9)).addHighlighter(new BarChartHighlighter(
				Color.RED.darker().darker(), Color.RED, new DecimalFormat("0.00000")));

		TableConfig.setColumnWidths(protFlatTreeTbl, new double[] { 13.25, 15, 14, 5, 4, 3, 4, 4, 4.5, 5 });
		TableConfig.setColumnMinWidths(protFlatTreeTbl,
				UIManager.getIcon("Table.ascendingSortIcon").getIconWidth(),
				createFilterButton(0, null, 0).getPreferredSize().width + 8);
	}
	
	// Peptide table column indices
	private final int PEP_SELECTION		= 0;
	private final int PEP_INDEX			= 1;
	private final int PEP_SEQUENCE		= 2;
	private final int PEP_SPECTRALCOUNT	= 3;

	/**
	 * This method sets up the peptide results table.
	 */
	private void setupPeptideTableProperties() {
		// Peptide table
		final TableModel peptideTblMdl = new DefaultTableModel() {
			// instance initializer block
			{
				setColumnIdentifiers(new Object[] { "", "#", "Sequence",
						"No. Spectra" });
			}
			
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				switch (columnIndex) {
				case PEP_SELECTION:
					return Boolean.class;
				case PEP_INDEX:
				case PEP_SPECTRALCOUNT:
					return Integer.class;
				case PEP_SEQUENCE:
				default:
					return String.class;
				}
			}
	
			public boolean isCellEditable(int row, int col) {
				return (col == PEP_SELECTION);
			}
		};
		peptideTbl = new JXTable(peptideTblMdl) {
			private Border padding = BorderFactory.createEmptyBorder(0, 2, 0, 2);
			@Override
			public Component prepareRenderer(TableCellRenderer renderer,
					int row, int column) {
				Component comp = super.prepareRenderer(renderer, row, column);
				if (comp instanceof JComponent) {
					((JComponent) comp).setBorder(padding);
				}
				return comp;
			}
			@Override
			public void setValueAt(Object aValue, int row, int column) {
				super.setValueAt(aValue, row, column);
				if (column == PEP_SELECTION) {
					ProteinHit proteinHit = dbSearchResult.getProteinHits().get(proteinTbl.getValueAt(proteinTbl.getSelectedRow(), PROT_ACCESSION));
					proteinHit.getPeptideHits().get(getValueAt(row, PEP_SEQUENCE)).setSelected((Boolean) aValue);
				}
			}
		};

		TableConfig.setColumnWidths(peptideTbl, new double[] {0, 1, 4, 3});
		
		TableColumnModel tcm = peptideTbl.getColumnModel();
		
		ComponentHeader ch = new ComponentHeader(tcm);
		peptideTbl.setTableHeader(ch);
		
		final JCheckBox selChk = new JCheckBox();
		selChk.setPreferredSize(new Dimension(15, 15));
		selChk.setSelected(true);

		// Add filter button widgets to column headers
		tcm.getColumn(PEP_SELECTION).setHeaderRenderer(new ComponentHeaderRenderer(selChk, null));
		tcm.getColumn(PEP_SELECTION).setMinWidth(19);
		tcm.getColumn(PEP_SELECTION).setMaxWidth(19);
		for (int col = PEP_INDEX; col < tcm.getColumnCount(); col++) {
			JPanel panel = new JPanel();
			panel.setPreferredSize(new Dimension(13, 13));
			tcm.getColumn(col).setHeaderRenderer(new ComponentHeaderRenderer(panel));
		}

		tcm.getColumn(PEP_INDEX).setCellRenderer(new CustomTableCellRenderer(SwingConstants.RIGHT));
		((TableColumnExt) tcm.getColumn(PEP_SPECTRALCOUNT)).addHighlighter(new BarChartHighlighter());
		
		// Make table always sort primarily by selection state of selection column
		final SortKey selKey = new SortKey(PEP_SELECTION, SortOrder.DESCENDING);
		
		final TableSortController<TableModel> tsc = new TableSortController<TableModel>(peptideTblMdl) {
			@Override
			public void toggleSortOrder(int column) {
		        List<SortKey> keys = new ArrayList<SortKey>(getSortKeys());
		        SortKey sortKey = SortUtils.getFirstSortKeyForColumn(keys, column);
		        if (keys.indexOf(sortKey) == 1)  {
		            // primary key: cycle sort order
		        	SortOrder so = (sortKey.getSortOrder() == SortOrder.ASCENDING) ?
		        			SortOrder.DESCENDING : SortOrder.ASCENDING;
		            keys.set(1, new SortKey(column, so));
		        } else {
		            // all others: insert new 
		            keys.remove(sortKey);
		            keys.add(1, new SortKey(column, SortOrder.DESCENDING));
		        }
		        if (keys.size() > getMaxSortKeys()) {
		            keys = keys.subList(0, getMaxSortKeys());
		        }
		        setSortKeys(keys);
			}
			@Override
			public void setSortKeys(List<? extends SortKey> sortKeys) {
				List<SortKey> newKeys = new ArrayList<SortKey>(sortKeys);
				// make sure selection column sorting always occurs before other columns
				newKeys.remove(selKey);
				newKeys.add(0, selKey);
				super.setSortKeys(newKeys);
			}
		};
		peptideTbl.setRowSorter(tsc);
		
		// register action listener on selection column header checkbox
		selChk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean selected = selChk.isSelected();
				// prevent auto-resorting while iterating rows for performance reasons
				tsc.setSortsOnUpdates(false);
				for (int row = 0; row < peptideTbl.getRowCount(); row++) {
					peptideTbl.setValueAt(selected, row, PEP_SELECTION);
				}
				tsc.setSortsOnUpdates(true);
				// re-sort rows after iteration finished
				tsc.sort();
			}
		});
		
		// Specify initial sort order
		List<SortKey> sortKeys = new ArrayList<SortKey>(2);
		sortKeys.add(0, new SortKey(PEP_SELECTION, SortOrder.DESCENDING));
		sortKeys.add(1, new SortKey(PEP_SPECTRALCOUNT, SortOrder.DESCENDING));
		tsc.setSortKeys(sortKeys);
		
		// register list selection listener
		peptideTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int selRow = peptideTbl.getSelectedRow();
				if (selRow != -1) {
					coverageSelectionModel.setValue(peptideTbl.convertRowIndexToModel(selRow));
				}
				refreshPsmTable();
			}
		});
	
		// Single selection only
		peptideTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		// Add nice striping effect
		peptideTbl.addHighlighter(TableConfig.getSimpleStriping());
		
		// Enables column control
		peptideTbl.setColumnControlVisible(true);
		peptideTbl.getColumnControl().setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(1, 1, 0, 0, Color.WHITE),
				BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY)));
		peptideTbl.getColumnControl().setOpaque(false);
		((ColumnControlButton) peptideTbl.getColumnControl()).setAdditionalActionsVisible(false);
	}
	
	/**
	 * This method sets up the protein peptide coverage viewer panel.
	 */
	private void setupCoverageViewer() {
		
		coveragePnl = new JPanel(new WrapLayout(FlowLayout.LEFT));
		coveragePnl.setBackground(Color.WHITE);
		
	}
	
	// PSM table column indices
	private final int PSM_SELECTION	= 0;
	private final int PSM_INDEX		= 1;
//	private final int PSM_SEQUENCE	= 2;
	private final int PSM_CHARGE	= 2;
	private final int PSM_VOTES		= 3;
	private final int PSM_XTANDEM 	= 4;
	private final int PSM_OMSSA 	= 5;
	private final int PSM_CRUX 		= 6;
	private final int PSM_INSPECT 	= 7;

	private ValueHolder coverageSelectionModel = new ValueHolder(-1);
	
	/**
	 * This method sets up the PSM results table.
	 */
	private void setupPsmTableProperties() {
		// PSM table
		final TableModel psmTblMdl = new DefaultTableModel() {
			{
				setColumnIdentifiers(new Object[] { "", "#", "z", "Votes", "X",
						"O", "C", "I" });
			}

			public boolean isCellEditable(int row, int col) {
				return (col == PSM_SELECTION) ? true : false;
			}

			public Class<?> getColumnClass(int columnIndex) {
				switch (columnIndex) {
				case PSM_SELECTION:
					return Boolean.class;
				case PSM_INDEX:
				case PSM_CHARGE:
				case PSM_VOTES:
					return Integer.class;
				case PSM_XTANDEM:
				case PSM_OMSSA:
				case PSM_CRUX:
				case PSM_INSPECT:
					return Double.class;
//				case PSM_SEQUENCE:
				default: 
					return String.class;
				}
			}
		};
		final DecimalFormat df = new DecimalFormat("0.000%");
		psmTbl = new JXTable(psmTblMdl) {
			private Border padding = BorderFactory.createEmptyBorder(0, 2, 0, 2);
			@Override
			public Component prepareRenderer(TableCellRenderer renderer,
					int row, int column) {
				Component comp = super.prepareRenderer(renderer, row, column);
				if (comp instanceof JComponent) {
					((JComponent) comp).setBorder(padding);
				}
				return comp;
			}
			@Override
			public String getToolTipText(MouseEvent me) {
				int col = columnAtPoint(me.getPoint());
				if (col == convertColumnIndexToModel(PSM_XTANDEM) ||
						col == convertColumnIndexToModel(PSM_OMSSA) ||
						col == convertColumnIndexToModel(PSM_CRUX) ||
						col == convertColumnIndexToModel(PSM_INSPECT)) {
					int row = rowAtPoint(me.getPoint());
					if (row != -1) {
						return df.format(getValueAt(row, col));
					}
				}
				return null;
			}
			@Override
			public void setValueAt(Object aValue, int row, int column) {
				super.setValueAt(aValue, row, column);
				if (column == PSM_SELECTION) {
					ProteinHit proteinHit = dbSearchResult.getProteinHits().get(proteinTbl.getValueAt(proteinTbl.getSelectedRow(), PROT_ACCESSION));
					
					Map<String, PeptideHit> peptideHits = proteinHit.getPeptideHits();
					int selectedPeptideRow = peptideTbl.getSelectedRow();
					String sequence = (String) peptideTbl.getValueAt(selectedPeptideRow, PEP_SEQUENCE);
					PeptideHit peptideHit = peptideHits.get(sequence);
					peptideHit.getSpectrumMatches().get((Integer) getValueAt(row, PSM_INDEX)-1).setSelected((Boolean) aValue);
				}
			}
		};
		
		// adjust column widths
		TableConfig.setColumnWidths(psmTbl, new double[] { 0, 1, 1, 2, 1, 1, 1, 1 });
		
		// Get table column model
		final TableColumnModel tcm = psmTbl.getColumnModel();
		
		// Apply component header capabilities as well as column header tooltips
		String[] columnToolTips = {
			    "Selection for Export",
			    "PSM Index",
			    "Peptide Sequence",
			    "Precursor Charge",
			    "Number of Votes",
			    "X!Tandem Confidence",
			    "Omssa Confidence",
			    "Crux Confidence",
			    "InsPecT Confidence" };
		ComponentHeader ch = new ComponentHeader(tcm, columnToolTips);
		psmTbl.setTableHeader(ch);
		
		final JCheckBox selChk = new JCheckBox();
		selChk.setPreferredSize(new Dimension(15, 15));
		selChk.setSelected(true);

		// Add filter button widgets to column headers
		tcm.getColumn(PSM_SELECTION).setHeaderRenderer(new ComponentHeaderRenderer(selChk, null));
		tcm.getColumn(PSM_SELECTION).setMinWidth(19);
		tcm.getColumn(PSM_SELECTION).setMaxWidth(19);
		for (int col = PSM_INDEX; col < tcm.getColumnCount(); col++) {
			JPanel panel = new JPanel();
			panel.setPreferredSize(new Dimension(13, 13));
			tcm.getColumn(col).setHeaderRenderer(new ComponentHeaderRenderer(panel));
		}
		
		// Apply custom cell renderers/highlighters to columns 
		tcm.getColumn(PSM_INDEX).setCellRenderer(new CustomTableCellRenderer(SwingConstants.RIGHT));
		tcm.getColumn(PSM_CHARGE).setCellRenderer(new CustomTableCellRenderer(SwingConstants.CENTER, "+0"));
//		tcm.getColumn(PSM_VOTES).setCellRenderer(new CustomTableCellRenderer(SwingConstants.CENTER));
		((TableColumnExt) tcm.getColumn(PSM_VOTES)).addHighlighter(new BarChartHighlighter(
				Color.RED.darker().darker(), Color.RED));
		((TableColumnExt) tcm.getColumn(PSM_XTANDEM)).addHighlighter(new BarChartHighlighter(
				0.8, 1.0, 0, SwingConstants.VERTICAL, Color.GREEN.darker().darker(), Color.GREEN));
		((TableColumnExt) tcm.getColumn(PSM_OMSSA)).addHighlighter(new BarChartHighlighter(
				0.8, 1.0, 0, SwingConstants.VERTICAL, Color.CYAN.darker().darker(), Color.CYAN));
		((TableColumnExt) tcm.getColumn(PSM_CRUX)).addHighlighter(new BarChartHighlighter(
				0.8, 1.0, 0, SwingConstants.VERTICAL, Color.BLUE.darker().darker(), Color.BLUE));
		((TableColumnExt) tcm.getColumn(PSM_INSPECT)).addHighlighter(new BarChartHighlighter(
				0.8, 1.0, 0, SwingConstants.VERTICAL, Color.MAGENTA.darker().darker(), Color.MAGENTA));
		
		// Make table always sort primarily by selection state of selection column
		final SortKey selKey = new SortKey(PSM_SELECTION, SortOrder.DESCENDING);
		
		final TableSortController<TableModel> tsc = new TableSortController<TableModel>(psmTblMdl) {
			@Override
			public void toggleSortOrder(int column) {
		        List<SortKey> keys = new ArrayList<SortKey>(getSortKeys());
		        SortKey sortKey = SortUtils.getFirstSortKeyForColumn(keys, column);
		        if (keys.indexOf(sortKey) == 1)  {
		            // primary key: cycle sort order
		        	SortOrder so = (sortKey.getSortOrder() == SortOrder.ASCENDING) ?
		        			SortOrder.DESCENDING : SortOrder.ASCENDING;
		            keys.set(1, new SortKey(column, so));
		        } else {
		            // all others: insert new 
		            keys.remove(sortKey);
		            keys.add(1, new SortKey(column, SortOrder.DESCENDING));
		        }
		        if (keys.size() > getMaxSortKeys()) {
		            keys = keys.subList(0, getMaxSortKeys());
		        }
		        setSortKeys(keys);
			}
			@Override
			public void setSortKeys(List<? extends SortKey> sortKeys) {
				List<SortKey> newKeys = new ArrayList<SortKey>(sortKeys);
				// make sure selection column sorting always occurs before other columns
				newKeys.remove(selKey);
				newKeys.add(0, selKey);
				super.setSortKeys(newKeys);
			}
		};
		psmTbl.setRowSorter(tsc);
		
		// register action listener on selection column header checkbox
		selChk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean selected = selChk.isSelected();
				// prevent auto-resorting while iterating rows for performance reasons
				tsc.setSortsOnUpdates(false);
				for (int row = 0; row < psmTbl.getRowCount(); row++) {
					psmTbl.setValueAt(selected, row, PSM_SELECTION);
				}
				tsc.setSortsOnUpdates(true);
				// re-sort rows after iteration finished
				tsc.sort();
			}
		});
		
		// Specify initial sort order
		List<SortKey> sortKeys = new ArrayList<SortKey>(2);
		sortKeys.add(0, new SortKey(PSM_SELECTION, SortOrder.DESCENDING));
		sortKeys.add(1, new SortKey(PSM_VOTES, SortOrder.DESCENDING));
		tsc.setSortKeys(sortKeys);
		
		// Prevent messing with sort order of selection column
//		tsc.setSortable(PSM_SELECTION, false);
		
		// Register list selection listener
		psmTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				refreshPlotAndShowPopup();
			}
		});
		
		// Only one row is selectable
		psmTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		// Add nice striping effect
		psmTbl.addHighlighter(TableConfig.getSimpleStriping());
		
		// Enables column control
		psmTbl.setColumnControlVisible(true);
		psmTbl.getColumnControl().setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(1, 1, 0, 0, Color.WHITE),
				BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY)));
		psmTbl.getColumnControl().setOpaque(false);
		((ColumnControlButton) psmTbl.getColumnControl()).setAdditionalActionsVisible(false);
	}

	/**
	 * This method constructs the spectrum filter panel.
	 */
	private JPanel constructSpectrumFilterPanel() {
		
		JPanel spectrumFilterPanel = new JPanel(new FormLayout("5dlu, p, 5dlu", "5dlu, f:p:g, 2dlu, f:p:g, 2dlu, f:p:g, 5dlu, p, 5dlu, f:p:g, 2dlu, f:p:g, 2dlu, f:p:g, 5dlu, p, 5dlu, f:p:g, 2dlu, f:p:g, 5dlu, p, 5dlu, f:p:g, 2dlu, f:p:g, 2dlu, f:p:g, 5dlu, p, 5dlu, f:p:g, 5dlu"));
		
		Dimension size = new Dimension(39, 23);
		Action action = new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				updateFilteredAnnotations();
			}
		};
		
		aIonsTgl = createFilterToggleButton("a", false, "Show a ions", size, action);
        bIonsTgl = createFilterToggleButton("b", true, "Show b ions", size, action);
        cIonsTgl = createFilterToggleButton("c", false, "Show c ions", size, action);

        xIonsTgl = createFilterToggleButton("x", false, "Show x ions", size, action);
        yIonsTgl = createFilterToggleButton("y", true, "Show y ions", size, action);
        zIonsTgl = createFilterToggleButton("z", false, "Show z ions", size, action);

        waterLossTgl = createFilterToggleButton("\u00B0", false, "<html>Show H<sub>2</sub>0 losses</html>", size, action);
        ammoniumLossTgl = createFilterToggleButton("*", false, "<html>Show NH<sub>3</sub> losses</html>", size, action);
        
        chargeOneTgl = createFilterToggleButton("+", true, "Show ions with charge 1", size, action);
        chargeTwoTgl = createFilterToggleButton("++", false, "Show ions with charge 2", size, action);
        chargeMoreTgl = createFilterToggleButton(">2", false, "Show ions with charge >2", size, action);
        
        precursorTgl = createFilterToggleButton("MH", true, "Show precursor ion", size, action);

        spectrumFilterPanel.add(aIonsTgl, CC.xy(2, 2));
        spectrumFilterPanel.add(bIonsTgl, CC.xy(2, 4));
        spectrumFilterPanel.add(cIonsTgl, CC.xy(2, 6));
        spectrumFilterPanel.add(new JSeparator(JSeparator.HORIZONTAL), CC.xy(2, 8));
        spectrumFilterPanel.add(xIonsTgl, CC.xy(2, 10));
        spectrumFilterPanel.add(yIonsTgl, CC.xy(2, 12));
        spectrumFilterPanel.add(zIonsTgl, CC.xy(2, 14));
        spectrumFilterPanel.add(new JSeparator(JSeparator.HORIZONTAL), CC.xy(2, 16));
        spectrumFilterPanel.add(waterLossTgl, CC.xy(2, 18));
        spectrumFilterPanel.add(ammoniumLossTgl, CC.xy(2, 20));
        spectrumFilterPanel.add(new JSeparator(JSeparator.HORIZONTAL), CC.xy(2, 22));
        spectrumFilterPanel.add(chargeOneTgl, CC.xy(2, 24));
        spectrumFilterPanel.add(chargeTwoTgl, CC.xy(2, 26));
        spectrumFilterPanel.add(chargeMoreTgl, CC.xy(2, 28));
        spectrumFilterPanel.add(new JSeparator(JSeparator.HORIZONTAL), CC.xy(2, 30));
        spectrumFilterPanel.add(precursorTgl, CC.xy(2, 32));
        
        return spectrumFilterPanel;
	}
	
	/**
	 * Helper method to ease checkbox construction.
	 * @param title
	 * @param selected
	 * @param toolTip
	 * @param size
	 * @param action
	 * @return A JCheckBox with the defined parameters.
	 */
	private JToggleButton createFilterToggleButton(String title, boolean selected, String toolTip, Dimension size, Action action) {
		JToggleButton toggleButton = new JToggleButton(action);
		toggleButton.setText(title);
		toggleButton.setSelected(selected);
		toggleButton.setToolTipText(toolTip);
		toggleButton.setMaximumSize(size);
		toggleButton.setMinimumSize(size);
		toggleButton.setPreferredSize(size);
		return toggleButton;
	}

	/**
	 * Method to refresh protein table contents.
	 */
	protected void refreshProteinTable() {
		
				dbSearchResult = Client.getInstance().getDbSearchResult(
				clientFrame.getProjectPanel().getCurrentProjectContent(),
				clientFrame.getProjectPanel().getCurrentExperimentContent());
		
		if (dbSearchResult != null && !dbSearchResult.isEmpty()) {
			TableConfig.clearTable(proteinTbl);
//			boolean selected = ((AbstractButton) ((ComponentHeaderRenderer) proteinTbl.getColumnModel().getColumn(PROT_SELECTION).getHeaderRenderer()).getComponent()).isSelected();

			DefaultTableModel proteinTblMdl = (DefaultTableModel) proteinTbl.getModel();
			DefaultTreeTableModel protTreeTblMdl = (DefaultTreeTableModel) protFlatTreeTbl.getTreeTableModel();
			
			int i = 1, maxPeptideCount = 0, maxSpecCount = 0;
			double maxCoverage = 0.0, maxNSAF = 0.0, max_emPAI = 0.0, min_emPAI = Double.MAX_VALUE;
			
			// Number of proteins
			int numProteins = dbSearchResult.getProteinHits().size();
			protTtlPnl.setTitle("Proteins (" + numProteins + ")");
			
			for (Entry<String, ProteinHit> entry : dbSearchResult.getProteinHits().entrySet()) {
				
				ProteinHit proteinHit = entry.getValue();

				// Protein species
				String desc = proteinHit.getDescription();
				String[] split = desc.split(" OS=");
				if (split.length > 1) {
					proteinHit.setDescription(split[0]);
					String species = (split[1].contains(" GN=")) ?
							split[1].substring(0, split[1].indexOf(" GN=")) : split[1];
					proteinHit.setSpecies(species);
				}
				
				double nsaf = ProteinAnalysis.calculateLabelFree(new NormalizedSpectralAbundanceFactor(), dbSearchResult.getProteinHits(), proteinHit);
				proteinHit.setNSAF(nsaf);
				maxCoverage = Math.max(maxCoverage, proteinHit.getCoverage());
				maxPeptideCount = Math.max(maxPeptideCount, proteinHit.getPeptideCount());
				maxSpecCount = Math.max(maxSpecCount, proteinHit.getSpectralCount());
				max_emPAI = Math.max(max_emPAI, proteinHit.getEmPAI());
				min_emPAI = Math.min(min_emPAI, proteinHit.getEmPAI());
				maxNSAF = Math.max(maxNSAF, nsaf);
				
				proteinTblMdl.addRow(new Object[] {
						proteinHit.isSelected(),
						i++,
						proteinHit.getAccession(),
						proteinHit.getDescription(),
						proteinHit.getSpecies(),
						proteinHit.getCoverage(),
						proteinHit.getMolecularWeight(),
						proteinHit.getPI(),
						proteinHit.getPeptideCount(), 
						proteinHit.getSpectralCount(),
						proteinHit.getEmPAI(),
						nsaf});
				
				protTreeTblMdl.insertNodeInto(new SortableCheckBoxTreeTableNode(
						proteinHit.getAccession(),
						proteinHit.getDescription(),
						proteinHit.getSpecies(),
						proteinHit.getCoverage(),
						proteinHit.getMolecularWeight(),
						proteinHit.getPI(),
						proteinHit.getPeptideCount(), 
						proteinHit.getSpectralCount(),
						proteinHit.getEmPAI(),
						nsaf),
						(MutableTreeTableNode) protTreeTblMdl.getRoot(),
						protTreeTblMdl.getRoot().getChildCount());
			}
			
			if (proteinTbl.getRowCount() > 0) {
				FontMetrics fm = getFontMetrics(chartFont);
				TableColumnModel tcm = proteinTbl.getColumnModel();
				
				BarChartHighlighter highlighter;
				
				highlighter = (BarChartHighlighter) ((TableColumnExt) tcm.getColumn(proteinTbl.convertColumnIndexToView(PROT_COVERAGE))).getHighlighters()[0];
				highlighter.setBaseline(1 + fm.stringWidth(highlighter.getFormatter().format(maxCoverage)));
				highlighter.setRange(0.0, maxCoverage);

				highlighter = (BarChartHighlighter) ((TableColumnExt) tcm.getColumn(proteinTbl.convertColumnIndexToView(PROT_PEPTIDECOUNT))).getHighlighters()[0];
				highlighter.setBaseline(1 + fm.stringWidth(highlighter.getFormatter().format(maxPeptideCount)));
				highlighter.setRange(0.0, maxPeptideCount);

				highlighter = (BarChartHighlighter) ((TableColumnExt) tcm.getColumn(proteinTbl.convertColumnIndexToView(PROT_SPECTRALCOUNT))).getHighlighters()[0];
				highlighter.setBaseline(1 + fm.stringWidth(highlighter.getFormatter().format(maxSpecCount)));
				highlighter.setRange(0.0, maxSpecCount);

				highlighter = (BarChartHighlighter) ((TableColumnExt) tcm.getColumn(proteinTbl.convertColumnIndexToView(PROT_EMPAI))).getHighlighters()[0];
				highlighter.setBaseline(1 + fm.stringWidth(highlighter.getFormatter().format(max_emPAI)));
				highlighter.setRange(min_emPAI, max_emPAI);

				highlighter = (BarChartHighlighter) ((TableColumnExt) tcm.getColumn(proteinTbl.convertColumnIndexToView(PROT_NSAF))).getHighlighters()[0];
				highlighter.setBaseline(1 + fm.stringWidth(highlighter.getFormatter().format(maxNSAF)));
				highlighter.setRange(0.0, maxNSAF);
				
				proteinTbl.getSelectionModel().setSelectionInterval(0, 0);
				
				// XXX: repeat for tree table
				tcm = protFlatTreeTbl.getColumnModel();
				
				highlighter = (BarChartHighlighter) ((TableColumnExt) tcm.getColumn(3)).getHighlighters()[0];
				highlighter.setBaseline(1 + fm.stringWidth(highlighter.getFormatter().format(maxCoverage)));
				highlighter.setRange(0.0, maxCoverage);

				highlighter = (BarChartHighlighter) ((TableColumnExt) tcm.getColumn(6)).getHighlighters()[0];
				highlighter.setBaseline(1 + fm.stringWidth(highlighter.getFormatter().format(maxPeptideCount)));
				highlighter.setRange(0.0, maxPeptideCount);

				highlighter = (BarChartHighlighter) ((TableColumnExt) tcm.getColumn(7)).getHighlighters()[0];
				highlighter.setBaseline(1 + fm.stringWidth(highlighter.getFormatter().format(maxSpecCount)));
				highlighter.setRange(0.0, maxSpecCount);

				highlighter = (BarChartHighlighter) ((TableColumnExt) tcm.getColumn(8)).getHighlighters()[0];
				highlighter.setBaseline(1 + fm.stringWidth(highlighter.getFormatter().format(max_emPAI)));
				highlighter.setRange(min_emPAI, max_emPAI);

				highlighter = (BarChartHighlighter) ((TableColumnExt) tcm.getColumn(9)).getHighlighters()[0];
				highlighter.setBaseline(1 + fm.stringWidth(highlighter.getFormatter().format(maxNSAF)));
				highlighter.setRange(0.0, maxNSAF);
			}
		}
	}

	/**
	 * Method to refresh peptide table contents and sequence coverage viewer.
	 */
	protected void refreshPeptideViews() {
		// Clear existing contents
		TableConfig.clearTable(peptideTbl);
		coveragePnl.removeAll();

		int protRow = proteinTbl.getSelectedRow();
		if (protRow != -1) {
			// Get protein and peptide information 
			String accession = (String) proteinTbl.getValueAt(protRow, proteinTbl.convertColumnIndexToView(PROT_ACCESSION));
			ProteinHit proteinHit = dbSearchResult.getProteinHits().get(accession);
			String sequence = proteinHit.getSequence();
			List<PeptideHit> peptideHits = proteinHit.getPeptideHitList();
			pepTtlPnl.setTitle("Peptides (" + peptideHits.size() + ")");
			
			// Iterate peptide hit list to fill table and build coverage view
			List<Interval> peptideIntervals = new ArrayList<Interval>(peptideHits.size());
			DefaultTableModel peptideTblMdl = (DefaultTableModel) peptideTbl.getModel();
			int row = 0, maxSpecCount = 0;
			for (PeptideHit peptideHit : peptideHits) {
				// Find occurences of peptide sequences in protein sequence
				String pepSeq = peptideHit.getSequence();
				int index = -1;
				while (true) {
					index = sequence.indexOf(pepSeq, index + 1);
					if (index == -1) break;
					// Store position of peptide sequence match in interval object
					Interval interval = new Interval(index, index + pepSeq.length(), row++);
					peptideIntervals.add(interval);
				}
				// Determine maximum spectral count
				int specCount = peptideHit.getSpectralCount();
				maxSpecCount = Math.max(maxSpecCount, specCount);
				// Add table row
				peptideTblMdl.addRow(new Object[] {
						peptideHit.isSelected(),
						row,
						peptideHit.getSequence(),
						specCount});
			}

			// Update table highlighters
			FontMetrics fm = getFontMetrics(chartFont);
			TableColumnModel tcm = peptideTbl.getColumnModel();

			BarChartHighlighter highlighter;
			
			highlighter = (BarChartHighlighter) ((TableColumnExt) tcm.getColumn(peptideTbl.convertColumnIndexToView(PEP_SPECTRALCOUNT))).getHighlighters()[0];
			highlighter.setBaseline(fm.stringWidth(highlighter.getFormatter().format(maxSpecCount)));
			highlighter.setRange(0.0, maxSpecCount);
			
			// Build coverage view
			RadioButtonAdapter rba = null;
			List<HoverLabel> hovers = null;
			
			// Iterate protein sequence blocks
			int blockSize = 10, length = sequence.length(), openIntervals = 0;
			for (int start = 0; start < length; start += blockSize) {
				int end = start + blockSize;
				end = (end > length) ? length : end;
				// Create upper label containing position index on light green background
				StringBuilder indexRow = new StringBuilder("<html><code>");
				int spaces = blockSize - 2 - (int) Math.floor(Math.log10(end));
				for (int i = 0; i < spaces; i++) {
					indexRow.append("&nbsp");
				}
				indexRow.append(" " + (start + blockSize));
				indexRow.append("</html></code>");
				
				JPanel blockPnl = new JPanel(new BorderLayout());
				blockPnl.setOpaque(false);
				JLabel indexLbl = new JLabel(indexRow.toString());
				indexLbl.setBackground(new Color(0, 255, 0, 32));
				indexLbl.setOpaque(true);
				blockPnl.add(indexLbl, BorderLayout.NORTH);

				// Create lower panel containing label-like buttons and plain labels
				JPanel subBlockPnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
				subBlockPnl.setOpaque(false);
				
				JComponent label = null;
				
				// Iterate characters inside block to find upper/lower interval boundaries
				String blockSeq = sequence.substring(start, end);
				int blockLength = blockSeq.length(), subIndex = 0;
				for (int i = 0; i < blockLength; i++) {
					int pos = start + i;
					boolean isBorder = false;
					// Compare all intervals' bounds with current absolute character position
					for (Interval interval : peptideIntervals) {
						if ((pos == (int) interval.getLeftBorder())) {
							// Highlightable part begins here, store contents up to this point in label
							label = new JLabel("<html><code>" + blockSeq.substring(subIndex, i) + "</code></html>");

							// Init data binding adapter to synchronize selection state of hover labels
							rba = new RadioButtonAdapter(coverageSelectionModel,
									((Integer) interval.getUserObject()).intValue());
							hovers = new ArrayList<HoverLabel>();
							
							isBorder = true;
							openIntervals++;
						}
						if ((pos == (int) interval.getRightBorder())) {
							// Highlightable part ends here, store contents in hover label
							label = new HoverLabel(blockSeq.substring(subIndex, i));
							((HoverLabel) label).setModel(rba);
							hovers.add((HoverLabel) label);
							// Make hover label aware of its surrounding siblings for synchronized rollover effects
							for (HoverLabel hl : hovers) {
								hl.setSiblings(hovers);
							}
							
							isBorder = true;
							openIntervals--;
						}
						if (isBorder) {
							// Add new label to panel, move index pointer forward
							subBlockPnl.add(label);
							subIndex = i;
						}
					}
				}
				// Store any remaining subsequences in (hover) label
				if (openIntervals > 0) {
					label = new HoverLabel(blockSeq.substring(subIndex));
					((HoverLabel) label).setModel(rba);
					hovers.add((HoverLabel) label);
				} else {
					label = new JLabel("<html><code>" + blockSeq.substring(subIndex) + "</code></html>");
				}
				subBlockPnl.add(label);

				blockPnl.add(subBlockPnl, BorderLayout.SOUTH);
				
				coveragePnl.add(blockPnl);
			}

			// Select first row in table
			peptideTbl.getSelectionModel().setSelectionInterval(0, 0);
		}
		coveragePnl.revalidate();
	}
	
	/**
	 * Method to refresh PSM table contents.
	 */
	protected void refreshPsmTable() {
		TableConfig.clearTable(psmTbl);
		
		int protRow = proteinTbl.getSelectedRow();
		if (protRow != -1) {
			String accession = (String) proteinTbl.getValueAt(protRow, proteinTbl.convertColumnIndexToView(PROT_ACCESSION));
			int pepRow = peptideTbl.getSelectedRow();
			if (pepRow != -1) {
				String sequence = (String) peptideTbl.getValueAt(pepRow, peptideTbl.convertColumnIndexToView(PEP_SEQUENCE));
				PeptideHit peptideHit = dbSearchResult.getProteinHits().get(accession).getPeptideHits().get(sequence);
				
				DefaultTableModel psmTblMdl = (DefaultTableModel) psmTbl.getModel();
				int i = 1, maxVotes = 0;
				List<SpectrumMatch> spectrumMatches = peptideHit.getSpectrumMatches();
				psmTtlPnl.setTitle("Peptide-Spectrum-Matches (" + spectrumMatches.size() + ")");
				
				for (SpectrumMatch sm : spectrumMatches) {
					PeptideSpectrumMatch psm = (PeptideSpectrumMatch) sm;
					List<SearchHit> searchHits = psm.getSearchHits();
					double[] qValues = { 0.0, 0.0, 0.0, 0.0 };
					for (SearchHit searchHit : searchHits) {
						switch (searchHit.getType()) {
						case XTANDEM:
							qValues[0] = 1.0 - searchHit.getQvalue().doubleValue(); break;
						case OMSSA:
							qValues[1] = 1.0 - searchHit.getQvalue().doubleValue(); break;
						case CRUX:
							qValues[2] = 1.0 - searchHit.getQvalue().doubleValue(); break;
						case INSPECT:
							qValues[3] = 1.0 - searchHit.getQvalue().doubleValue(); break;
						}
					}
					maxVotes = Math.max(maxVotes, psm.getVotes());
					psmTblMdl.addRow(new Object[] {
							sm.isSelected(),
							i++,
							psm.getCharge(),
							psm.getVotes(),
							qValues[0],
							qValues[1],
							qValues[2],
							qValues[3]});
				}

				FontMetrics fm = getFontMetrics(chartFont);
				TableColumnModel tcm = psmTbl.getColumnModel();

				BarChartHighlighter highlighter;
				
				highlighter = (BarChartHighlighter) ((TableColumnExt) tcm.getColumn(psmTbl.convertColumnIndexToView(PSM_VOTES))).getHighlighters()[0];
				highlighter.setBaseline(1 + fm.stringWidth(highlighter.getFormatter().format(maxVotes)));
				highlighter.setRange(0.0, maxVotes);
				
				psmTbl.getSelectionModel().setSelectionInterval(0, 0);
			}
		}
	}
	
	protected void refreshPlotAndShowPopup() {
		int protRow = proteinTbl.getSelectedRow();
		if (protRow != -1) {
			int pepRow = peptideTbl.getSelectedRow();
			if (pepRow != -1) {
				int psmRow = psmTbl.getSelectedRow();
				if (psmRow != -1) {
					// clear the spectrum panel
					while (spectrumJPanel.getComponents().length > 0) {
		                spectrumJPanel.remove(0);
		            }
					// get the list of spectrum matches
					String actualAccession = (String) proteinTbl.getValueAt(protRow, proteinTbl.convertColumnIndexToView(PROT_ACCESSION));
					String sequence = (String) peptideTbl.getValueAt(pepRow, peptideTbl.convertColumnIndexToView(PEP_SEQUENCE));
					int index = psmTbl.convertRowIndexToModel(psmRow);
					PeptideSpectrumMatch psm = (PeptideSpectrumMatch) dbSearchResult.getProteinHits().get(actualAccession).getPeptideHits().get(sequence).getSpectrumMatches().get(index);
					// grab corresponding spectrum from the database and display it
					try {
						MascotGenericFile mgf = Client.getInstance().getSpectrumFromSearchSpectrumID(psm.getSearchSpectrumID());
						
						specPnl = new SpectrumPanel(mgf);
//						specPnl.showAnnotatedPeaksOnly(true);	// pretty slow!
						
						spectrumJPanel.add(specPnl, CC.xy(2, 2));
						spectrumJPanel.validate();
						spectrumJPanel.repaint();
						
						Fragmentizer fragmentizer = new Fragmentizer(sequence, Masses.getInstance(), psm.getCharge());
						addSpectrumAnnotations(fragmentizer.getFragmentIons());
						
					} catch (SQLException sqe) {
						GeneralExceptionHandler.showSQLErrorDialog(sqe, clientFrame);
					}
					// show popup
					showPopup(psm);
				}
			}
		}
	}

	/**
	 * Method to generate and show a popup containing PSM-specific details.
	 * @param psm
	 */
	private void showPopup(PeptideSpectrumMatch psm) {
		JPopupMenu popup = new JPopupMenu();
		
		JPanel panel = new JPanel(new FormLayout("p, 5dlu, p", "p, 2dlu, p, 2dlu, p, 2dlu, p"));
		panel.setOpaque(true);
		panel.setBackground(new Color(240,240,240));
		
		JLabel xTandemLbl = new JLabel("");
		xTandemLbl.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(0, 0, 2, 0),
				BorderFactory.createLineBorder(Color.GREEN)));

		JLabel omssaLbl = new JLabel("");
		omssaLbl.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(2, 0, 2, 0),
				BorderFactory.createLineBorder(Color.CYAN)));
		JLabel cruxLbl = new JLabel("");
		cruxLbl.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(2, 0, 2, 0),
				BorderFactory.createLineBorder(Color.BLUE)));
		JLabel inspectLbl = new JLabel("");
		inspectLbl.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(2, 0, 0, 0),
				BorderFactory.createLineBorder(Color.MAGENTA)));
		DecimalFormat df = new DecimalFormat("0.00");
		int yIndex = 1;
		for (SearchHit hit : psm.getSearchHits()) {
			switch (hit.getType()) {
			case XTANDEM:
				XTandemhit xtandemhit = (XTandemhit) hit;
				xTandemLbl.setText("Conf: " + df.format((1.0 - hit.getQvalue().doubleValue())*100.0) + "%" +
						" | HScore: " + df.format(xtandemhit.getHyperscore().doubleValue()) + 
						" | E-value: "  + df.format(xtandemhit.getEvalue().doubleValue()));
				panel.add(new JLabel("<html><font color='#00FF00'>\u25cf</font> X!Tandem</html>"), CC.xy(1,yIndex));
				panel.add(xTandemLbl, CC.xy(3, yIndex));
				yIndex += 2;
				break;
			case OMSSA:
				Omssahit omssahit = (Omssahit) hit;
				omssaLbl.setText("Conf: " + df.format((1.0 - hit.getQvalue().doubleValue())*100.0) + "%" +
						" | P-value: " + df.format(omssahit.getPvalue().doubleValue()) + 
						" | E-value: "  + df.format(omssahit.getEvalue().doubleValue()));
				panel.add(new JLabel("<html><font color='#00FFFF'>\u25cf</font> OMSSA</html>"), CC.xy(1,yIndex));
				panel.add(omssaLbl, CC.xy(3,yIndex));
				yIndex += 2;
				break;
			case CRUX:
				Cruxhit cruxhit = (Cruxhit) hit;
				cruxLbl.setText("Conf: " + df.format((1.0 - hit.getQvalue().doubleValue())*100.0) + "%" +
						" | XCorr: " + df.format(cruxhit.getXcorr_score().doubleValue()));
				panel.add(new JLabel("<html><font color='#0000FF'>\u25cf</font> Crux</html>"), CC.xy(1,yIndex));
				panel.add(cruxLbl, CC.xy(3,yIndex));
				yIndex += 2;
				break;
			case INSPECT:
				Inspecthit inspecthit = (Inspecthit) hit;
				inspectLbl.setText("Conf: " + df.format((1.0 - hit.getQvalue().doubleValue())*100.0) + "%" +
						" | FScore: " + df.format(inspecthit.getF_score().doubleValue()) + 
						" | DeltaScore: "  + df.format(inspecthit.getDeltascore().doubleValue()));
				panel.add(new JLabel("<html><font color='#FF00FF'>\u25cf</font> InsPecT</html>"), CC.xy(1,yIndex));
				panel.add(inspectLbl, CC.xy(3,yIndex));
				yIndex += 2;
				break;
			default:
				break;
			}
		}
		popup.add(panel);
//		Rectangle cellRect = psmTbl.getCellRect(psmTbl.getSelectedRow(), psmTbl.getColumnCount()-1, true);
//		popup.show(psmTbl, cellRect.x + cellRect.width, cellRect.y - popup.getPreferredSize().height/2 + cellRect.height/2);
	}

	/**
	 * Method to add annotations to spectrum plot.
	 * @param fragmentIons
	 */
	private void addSpectrumAnnotations(Map<String, FragmentIon[]> fragmentIons) {

        currentAnnotations = new Vector<SpectrumAnnotation>();
       	Set<Entry<String, FragmentIon[]>> entrySet = fragmentIons.entrySet();
       	int i = 0;
       	for (Entry<String, FragmentIon[]> entry : entrySet) {
        		FragmentIon[] ions = entry.getValue();
        		
	            for (FragmentIon ion : ions) {
	                int ionNumber = ion.getNumber();
	                double mzValue = ion.getMZ();
	                Color color;
	                if (i % 2 == 0) {
	                    color = Color.BLUE;
	                } else {
	                    color = Color.BLACK;
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
    private Vector<SpectrumAnnotation> filterAnnotations(Vector<SpectrumAnnotation> annotations) {

        Vector<SpectrumAnnotation> filteredAnnotations = new Vector<SpectrumAnnotation>();
        
		JToggleButton[] ionToggles = new JToggleButton[] { aIonsTgl, bIonsTgl,
				cIonsTgl, xIonsTgl, yIonsTgl, zIonsTgl, precursorTgl };
        String ionTokens = "abcxyzM";
        
		JToggleButton[] miscToggles = new JToggleButton[] { chargeOneTgl,
				chargeMoreTgl, chargeTwoTgl, ammoniumLossTgl, waterLossTgl };
		String[] miscTokens = new String[]  { "+", "+++", "++", "*", "\u00B0" };

		for (SpectrumAnnotation annotation : annotations) {
            String currentLabel = annotation.getLabel();
            boolean useAnnotation = true;
            // check ion type
            for (int i = 0; i < ionToggles.length; i++) {
            	if (currentLabel.lastIndexOf(ionTokens.charAt(i)) != -1) {
    				useAnnotation &= ionToggles[i].isSelected();
            	}
            	if (!useAnnotation) break;
			}

            // check ion charge + ammonium and water-loss
            if (useAnnotation) {
            	for (int i = 0; i < miscToggles.length; i++) {
					if (currentLabel.lastIndexOf(miscTokens[i]) != -1) {
						useAnnotation &= miscToggles[i].isSelected();
					}
					if (!useAnnotation) break;
				}
            }
            
            // If not used, don't add the annotation.
            if (useAnnotation) {
                filteredAnnotations.add(annotation);
            }
		}

        return filteredAnnotations;
    }
	
	/**
	 * This method sets the enabled state of the get results button.
	 */
	public void setResultsButtonEnabled(boolean enabled) {
		getResultsBtn.setEnabled(enabled);
	}
	
	/**
	 * This methode adds the Filter
	 * @param column
	 * @return 
	 */
	public JPanel createFilterButton(int column, JTable table, int filterType) {
		// button widget with little black triangle
		final FilterButton button = new FilterButton(column, table, filterType);
		button.setPreferredSize(new Dimension(11, 11));
	
		// wrap button in panel to apply padding
		final JPanel panel = new JPanel(new FormLayout("0px, p, 3px", "2px, p, 2px"));
		panel.setOpaque(false);
		panel.add(button, CC.xy(2, 2));
		
		// forward mouse events on panel to button
		panel.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				forwardEvent(me);
			}
			public void mouseReleased(MouseEvent me) {
				forwardEvent(me);
			}
			public void mouseClicked(MouseEvent me) {
				forwardEvent(me);
			}
			private void forwardEvent(MouseEvent me) {
				button.dispatchEvent(SwingUtilities.convertMouseEvent(panel, me, button));
			}
		});
		return panel;
	}
	
	private class FilterButton extends JToggleButton {
		
		private FilterBalloonTip filterTip;
		private FilterButton button;
		
		/**
		 * Constructs a filter toggle button widget which shows its own balloon
		 * tip pop-up containing filtering-related components for the specified
		 * <code>column</code>.
		 * 
		 * @param column
		 */
		public FilterButton(final int column, final JTable table, final int filterType) {
			super();
			this.button = this;
			this.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					if (button != lastSelectedFilterBtn) {
//						lastSelectedFilterBtn.setSelected(false);
						lastSelectedFilterBtn.setFilterTipVisible(false);
						lastSelectedFilterBtn = button;
					}
					if (button.isSelected()) {
						JTableHeader th = table.getTableHeader();
						String filterString = (filterTip != null) ? filterTip.getFilterString() : "";	
						
						Rectangle rect = th.getHeaderRect(table.convertColumnIndexToView(column));
						rect.x += rect.width - 9;
						rect.y += 1;
						filterTip = new FilterBalloonTip(th, rect, filterType);
						filterTip.addComponentListener(new ComponentAdapter() {
							public void componentHidden(ComponentEvent ce) {
								button.setSelected(false);
								table.getTableHeader().repaint();
								// TODO: maybe store and restore original focus instead of shifting it always onto the table?
								table.requestFocus();
								if (filterTip.isConfirmed()) {
									updateSelections(column, filterTip.getFilterString());
								}
							};
						});
						filterTip.setFilterString(filterString);
						filterTip.requestFocus();
					} else {
						filterTip.setVisible(false);
					}
				}
			});
		}
		
		/**
		 * Method to remotely hide/show the filter balloon tip.
		 * @param visible
		 */
		protected void setFilterTipVisible(boolean visible) {
			if (filterTip != null) {
				filterTip.setVisible(visible);
			}
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.BLACK);
			g.fillPolygon(new int[] { 3, 8, 8 }, new int[] { 8, 8, 3 }, 3);
		}
		
	}


	/**
	 * Method to update the checkbox column values depending on pattern 
	 * matching in the specified column using the specified pattern string.
	 * @param column The column to check.
	 * @param filterString Comma-separated string patterns.
	 */
	private void updateSelections(int column, String filterString) {
		column = proteinTbl.convertColumnIndexToView(column);
		// prevent auto-resorting while iterating rows for performance reasons
		((TableSortController) proteinTbl.getRowSorter()).setSortsOnUpdates(false);
		// Check whether we're dealing with numerical values or with text
		if (Number.class.isAssignableFrom(proteinTbl.getColumnClass(column))) {
//			List<Number> greater = new ArrayList<Number>();
//			List<Number> less = new ArrayList<Number>();
			// parse filter strings
			String[] filterStrings = filterString.split(",");
			double left = Double.MIN_VALUE, right = Double.MAX_VALUE;
			for (int i = 0; i < filterStrings.length; i++) {
				String s = filterStrings[i].trim();
				if (s.startsWith(">")) {
					double val = Double.parseDouble(s.substring(1));
					if (left == Double.MIN_VALUE) {
						left = val;
					} else {
						left = Math.min(left, val);
					}
					// greater.add(val);
				} else if (s.startsWith("<")) {
					double val = Double.parseDouble(s.substring(1));
					if (right == Double.MAX_VALUE) {
						right = val;
					} else {
						right = Math.max(right, val);
					}
				}
			}
			Interval interval = new Interval(left, right);
			// iterate table rows and check intervals
			for (int row = 0; row < proteinTbl.getRowCount(); row++) {
				if (!(Boolean) proteinTbl.getValueAt(row, PROT_SELECTION)) {
					break;
				}
				proteinTbl.setValueAt(	interval.containsExclusive(((Number) proteinTbl.getValueAt(row, column)).doubleValue()),
										row, proteinTbl.convertColumnIndexToView(PROT_SELECTION));
			}
		} else {
			List<String> restrictive = new ArrayList<String>();
			List<String> permissive = new ArrayList<String>();
			// parse filter strings
			String[] filterStrings = filterString.split(",");
			for (int i = 0; i < filterStrings.length; i++) {
				String s = filterStrings[i].trim();
				if (s.startsWith("-")) {
					restrictive.add(s.substring(1));
				} else {
					permissive.add(s);
				}
			}
			// iterate table rows and check filter patterns
			for (int row = 0; row < proteinTbl.getRowCount(); row++) {
				if (!(Boolean) proteinTbl.getValueAt(row, PROT_SELECTION)) {
					break;
				}
				String value = proteinTbl.getValueAt(row, column).toString();
				boolean selected;
				// check includes
				if (!permissive.isEmpty()) {
					selected = false;
					for (String s : permissive) {
						selected |= value.contains(s);
					}
				} else {
					// check excludes
					selected = true;
					for (String s : restrictive) {
						selected &= !value.contains(s);
						if (!selected) break;
					}
				}
				proteinTbl.setValueAt(selected, row, proteinTbl.convertColumnIndexToView(PROT_SELECTION));
			}
		}
		((TableSortController) proteinTbl.getRowSorter()).setSortsOnUpdates(true);
		// re-sort rows after iteration finished
		((TableSortController) proteinTbl.getRowSorter()).sort();
	}

	/**
	 * A clickable label with rollover effect.
	 * 
	 * @author A. Behne
	 */
	private class HoverLabel extends JToggleButton {
		
		private Color selected;
		private List<HoverLabel> siblings;

		public HoverLabel(String text) {
			this(text, Color.RED);
		}

		public HoverLabel(String text, Color foreground) {
			this(text, foreground, new Color(foreground.getRed(), foreground.getGreen(),
					foreground.getBlue(), (foreground.getAlpha() + 1) / 8));
		}

		public HoverLabel(String text, Color foreground, Color selected) {
			super(text);
			this.selected = selected;
			this.siblings = new ArrayList<HoverLabel>();
			setForeground(foreground);
			setFocusPainted(false);
			setContentAreaFilled(false);
			setBorder(null);
			setFont(new Font("Monospaced", Font.PLAIN, 12));
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			addMouseListener(new MouseAdapter() {
				private Font originalFont, underlineFont;
				{
					originalFont = getFont();
					Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
					attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
					underlineFont = originalFont.deriveFont(attributes);
				}
				public void mouseEntered(MouseEvent me) {
					for (HoverLabel sibling : siblings) {
						sibling.setFont(underlineFont);
					}
				}
				public void mouseExited(MouseEvent me) {
					for (HoverLabel sibling : siblings) {
						sibling.setFont(originalFont);
					}
				}
			});
			// TODO: use binding to synchronize selection state with table
			addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int index = peptideTbl.convertRowIndexToView((Integer) coverageSelectionModel.getValue());
					peptideTbl.getSelectionModel().setSelectionInterval(index, index);
				}
			});
		}
		
		public void setSiblings(List<HoverLabel> siblings) {
			this.siblings = siblings;
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			if (isSelected()) {
				g.setColor(selected);
				g.fillRect(0, 0, getWidth(), getHeight());
			}
			super.paintComponent(g);
		}
	}	
}