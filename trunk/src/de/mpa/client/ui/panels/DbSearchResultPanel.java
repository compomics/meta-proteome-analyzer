package de.mpa.client.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTitledPanel;
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

import com.compomics.util.gui.spectrum.DefaultSpectrumAnnotation;
import com.compomics.util.gui.spectrum.SpectrumPanel;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.algorithms.quantification.NormalizedSpectralAbundanceFactor;
import de.mpa.analysis.Masses;
import de.mpa.analysis.ProteinAnalysis;
import de.mpa.client.model.SpectrumMatch;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.PeptideSpectrumMatch;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.model.dbsearch.SearchEngineHit;
import de.mpa.client.ui.BarChartHighlighter;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.ComponentHeader;
import de.mpa.client.ui.ComponentHeaderRenderer;
import de.mpa.client.ui.PanelConfig;
import de.mpa.client.ui.TableConfig;
import de.mpa.client.ui.TableConfig.CustomTableCellRenderer;
import de.mpa.client.ui.dialogs.FilterBalloonTip;
import de.mpa.client.ui.dialogs.GeneralExceptionHandler;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.db.accessor.Cruxhit;
import de.mpa.db.accessor.Inspecthit;
import de.mpa.db.accessor.Omssahit;
import de.mpa.db.accessor.XTandemhit;
import de.mpa.fragmentation.FragmentIon;
import de.mpa.fragmentation.Fragmentizer;
import de.mpa.io.MascotGenericFile;

public class DbSearchResultPanel extends JPanel {
	
	private ClientFrame clientFrame;
	private JXTable proteinTbl;
	protected FilterButton lastSelectedFilterBtn = new FilterButton(0, null);
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
	private Vector<DefaultSpectrumAnnotation> currentAnnotations;
	private JPanel spectrumJPanel;
	private JButton getResultsBtn;
	private JEditorPane coverageTxt;
	private Font chartFont;
	
	FilterBalloonTip filterTip;
	
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
	 * Constructor for a database results panel.
	 * @param clientFrame The parent frame.
	 */
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
		
		final JPanel proteinPnl = new JPanel();
		proteinPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));
	
		// Setup tables
		chartFont = UIManager.getFont("Label.font");
		setupProteinTableProperties();
		setupPeptideTableProperties();
		setupPsmTableProperties();
		
		// Scroll panes
		JScrollPane proteinTableScp = new JScrollPane(proteinTbl);
		proteinTableScp.setPreferredSize(new Dimension(800, 200));
		JScrollPane peptideTableScp = new JScrollPane(peptideTbl);
		peptideTableScp.setPreferredSize(new Dimension(350, 150));
		JScrollPane psmTableScp = new JScrollPane(psmTbl);
		psmTableScp.setPreferredSize(new Dimension(350, 150));
		
		getResultsBtn = new JButton("Get Results   ", IconConstants.REFRESH_DB_ICON);
		getResultsBtn.setRolloverIcon(IconConstants.REFRESH_DB_ROLLOVER_ICON);
		getResultsBtn.setPressedIcon(IconConstants.REFRESH_DB_PRESSED_ICON);
		
		getResultsBtn.setEnabled(false);
		
		getResultsBtn.setPreferredSize(new Dimension(getResultsBtn.getPreferredSize().width, 20));
		getResultsBtn.setFocusPainted(false);
		
		getResultsBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dbSearchResult = clientFrame.getClient().getDbSearchResult(clientFrame.getProjectPanel().getCurrentProjectContent(), clientFrame.getProjectPanel().getCurrentExperimentContent());
				// TODO: ADD search engine from runtable
				List<String> searchEngines = new ArrayList<String>(Arrays.asList(new String [] {"Crux", "Inspect", "Xtandem","OMSSA"}));
				dbSearchResult.setSearchEngines(searchEngines);
				
				refreshProteinTable();
				
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
		
//		coverageTxt = new JTextArea(
//				"        10         20         30         40         50         60 \n" +
//				"MKWVTFISLF FLFSSAYSRG LVRREAYKSE IAHRYNDLGE EHFRGLVLVA FSQYLQQCPF \n" +
//				"        70         80         90        100        110        120 \n" + 
//				"EDHVKLAKEV TEFAKACAAE ESGANCDKSL HTLFGDKLCT VASLRDKYGD MADCCEKQEP \n" +
//				"       130        140        150        160        170        180 \n" +
//				"DRNECFLAHK DDNPGFPPLV APEPDALCAA FQDNEQLFLG KYLYEIARRH PYFYAPELLY \n" +
//				"       190        200        210        220        230        240 \n" +
//				"YAQQYKGVFA ECCQAADKAA CLGPKIEALR EKVLLSSAKE RFKCASLQKF GDRAFKAWSV \n" + 
//				"       250        260        270        280        290        300 \n" +
//				"ARLSQRFPKA DFAEISKVVT DLTKVHKECC HGDLLECADD RADLAKYMCE NQDSISTKLK \n" +
//				"       310        320        330        340        350        360 \n" +
//				"ECCDKPVLEK SQCLAEVERD ELPGDLPSLA ADFVEDKEVC KNYQEAKDVF LGTFLYEYAR \n" + 
//				"       370        380        390        400        410        420 \n" +
//				"RHPEYSVSLL LRLAKEYEAT LEKCCATDDP PTCYAKVLDE FKPLVDEPQN LVKTNCELFE \n" +
//				"       430        440        450        460        470        480 \n" +
//				"KLGEYGFQNA LLVRYTKKAP QVSTPTLVEV SRKLGKVGTK CCKKPESERM SCAEDFLSVV \n" +
//				"       490        500        510        520        530        540 \n" +
//				"LNRLCVLHEK TPVSERVTKC CSESLVNRRP CFSGLEVDET YVPKEFNAET FTFHADLCTL \n" +
//				"       550        560        570        580        590        600 \n" +
//				"PEAEKQVKKQ TALVELLKHK PKATDEQLKT VMGDFGAFVE KCCAAENKEG CFSEEGPKLV \n" +
//				"       610 \n" +
//				"AAAQAALV ");
		coverageTxt = new JEditorPane();
		
		coverageTxt.setEditorKit(PanelConfig.getLetterWrapEditorKit());
		
		coverageTxt.setContentType("text/html");
		String text = "MKWVTFISLFFLFSSAYSRGLVRREAYKSEIAHRYNDLGEEHFRGLVLVAFSQYLQQCPFEDHVKLAKEVTEFAKACAAEESGANCDKSLHTLFGDKLCTVASLRDKYGDMADCCEKQEPDRNECFLAHKDDNPGFPPLVAPEPDALCAAFQDNEQLFLGKYLYEIARRHPYFYAPELLYYAQQYKGVFAECCQAADKAACLGPKIEALREKVLLSSAKERFKCASLQKFGDRAFKAWSVARLSQRFPKADFAEISKVVTDLTKVHKECCHGDLLECADDRADLAKYMCENQDSISTKLKECCDKPVLEKSQCLAEVERDELPGDLPSLAADFVEDKEVCKNYQEAKDVFLGTFLYEYARRHPEYSVSLLLRLAKEYEATLEKCCATDDPPTCYAKVLDEFKPLVDEPQNLVKTNCELFEKLGEYGFQNALLVRYTKKAPQVSTPTLVEVSRKLGKVGTKCCKKPESERMSCAEDFLSVVLNRLCVLHEKTPVSERVTKCCSESLVNRRPCFSGLEVDETYVPKEFNAETFTFHADLCTLPEAEKQVKKQTALVELLKHKPKATDEQLKTVMGDFGAFVEKCCAAENKEGCFSEEGPKLVAAAQAALV";
		coverageTxt.setText(text);
		
		MutableAttributeSet attr = new SimpleAttributeSet();
		StyleConstants.setLineSpacing(attr, 0.25f);
		
		StyledDocument doc = (StyledDocument) coverageTxt.getDocument();

//		coverageTxt.setParagraphAttributes(attr, true);
		coverageTxt.setFont(new Font("Monospaced", Font.PLAIN, 12));
		coverageTxt.getCaret().setSelectionVisible(false);
		coverageTxt.getCaret().setVisible(false);
		
		SimpleAttributeSet sas = new SimpleAttributeSet();
	    StyleConstants.setForeground(sas, Color.RED);
		
		String[] peptides = new String[] {
				"SEIAHRYNDLGE",
				"AAEESGANCD",
				"AECCQAADKAACLGPKIEALREKVLLS",
				"KLGEYGFQNALLVRYTKKAPQVS"};
		for (String string : peptides) {
			int startPos = text.indexOf(string);
			if (startPos != -1) {
				doc.setCharacterAttributes(startPos, string.length(), sas, false);
			}
		}

		JPanel noWrapPnl = new JPanel(new BorderLayout());
		noWrapPnl.add(coverageTxt);
		
		JScrollPane coverageScpn = new JScrollPane(coverageTxt);
		coverageScpn.setPreferredSize(new Dimension(350, 150));
		coverageScpn.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//		peptidePnl.add(coverageScpn, CC.xy(2, 2));
		
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
		
		this.add(multiSplitPane, CC.xy(2, 2));
	}
	
	/**
	 * This method sets up the protein results table.
	 */
	private void setupProteinTableProperties(){
		// Create protein table model
		final TableModel proteinTblMdl = new DefaultTableModel() {
			// instance initializer block
			{ setColumnIdentifiers(new Object[] {
					"", 				//0
					"#", 				//1
					"Accession", 		//2
					"Description",  	//3
					"Species", 			//4
					"SC", 				//5
					"MW",     			//6
					"pI",				//7
					"PepC", 			//8
					"SpC",				//9
					"emPAI",			//10
					"NSAF"}); }			//11
	
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
		};
		
		// adjust column widths
		TableConfig.setColumnWidths(proteinTbl, new double[] { 0, 2.5, 5.5, 15, 14, 5, 4, 3, 4, 4, 4.5, 5 });
		TableConfig.setColumnMinWidths(proteinTbl, 1);
		
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

		// Add filter button widgets to column headers
		tcm.getColumn(PROT_SELECTION).setHeaderRenderer(new ComponentHeaderRenderer(selChk, null));
		tcm.getColumn(PROT_SELECTION).setMinWidth(19);
		tcm.getColumn(PROT_SELECTION).setMaxWidth(19);
		for (int col = 1; col < tcm.getColumnCount(); col++) {
			tcm.getColumn(col).setHeaderRenderer(new ComponentHeaderRenderer(createFilterButton(col, proteinTbl)));
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
		((TableColumnExt) tcm.getColumn(PROT_COVERAGE)).addHighlighter(new BarChartHighlighter(
				0.0, 100.0, 50, SwingConstants.HORIZONTAL, Color.GREEN.darker().darker(), Color.GREEN, new DecimalFormat("0.00")));
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
					proteinTblMdl.setValueAt(selected, row, PROT_SELECTION);
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
				refreshPeptideTable();
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
	
	/**
	 * This method sets up the peptide results table.
	 */
	
	// Peptide table column indices
//	private int PEP_SELECTION		= X;
	private final int PEP_INDEX			= 0;
	private final int PEP_SEQUENCE		= 1;
	private final int PEP_SPECTRALCOUNT	= 2;
	
	private void setupPeptideTableProperties() {
		// Peptide table
		TableModel peptideTblMdl = new DefaultTableModel() {
			// instance initializer block
			{ setColumnIdentifiers(new Object[] {"#", "Sequence", "No. Spectra"}); }
			
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				switch (columnIndex) {
				case PEP_INDEX:
				case PEP_SPECTRALCOUNT:
					return Integer.class;
				case PEP_SEQUENCE:
				default:
					return String.class;
				}
			}
	
			public boolean isCellEditable(int row, int col) {
				return false;
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
		};
		
		TableConfig.setColumnWidths(peptideTbl, new double[] {3, 24, 12});
		
		TableColumnModel tcm = peptideTbl.getColumnModel();

		tcm.getColumn(PEP_INDEX).setCellRenderer(new CustomTableCellRenderer(SwingConstants.RIGHT));
		((TableColumnExt) tcm.getColumn(PEP_SPECTRALCOUNT)).addHighlighter(new BarChartHighlighter());
		
		// Sort the peptide table by the number of peptide hits
		peptideTbl.setAutoCreateRowSorter(true);
		peptideTbl.getRowSorter().toggleSortOrder(PEP_SPECTRALCOUNT);
		peptideTbl.getRowSorter().toggleSortOrder(PEP_SPECTRALCOUNT);
		
		// register list selection listener
		peptideTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
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
	
	// PSM table column indices
	private final int PSM_SELECTION	= 0;
	private final int PSM_INDEX		= 1;
	private final int PSM_SEQUENCE	= 2;
	private final int PSM_CHARGE	= 3;
	private final int PSM_VOTES		= 4;
	private final int PSM_XTANDEM 	= 5;
	private final int PSM_OMSSA 	= 6;
	private final int PSM_CRUX 		= 7;
	private final int PSM_INSPECT 	= 8;
	
	/**
	 * This method sets up the PSM results table.
	 */
	private void setupPsmTableProperties() {
		// PSM table
		final TableModel psmTblMdl = new DefaultTableModel() {
			{ setColumnIdentifiers(new Object[] {"", "#", "Sequence", "z", "Votes", "X", "O", "C", "I"}); }

			public boolean isCellEditable(int row, int col) {
				return (col == PSM_SELECTION) ? true : false;
			}

			public Class<?> getColumnClass(int columnIndex) {
				switch (columnIndex) {
				case PSM_INDEX:
				case PSM_CHARGE:
				case PSM_VOTES:
					return Integer.class;
				case PSM_XTANDEM:
				case PSM_OMSSA:
				case PSM_CRUX:
				case PSM_INSPECT:
					return Double.class;
				case PSM_SELECTION:
					return Boolean.class;
				case PSM_SEQUENCE:
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
		};
		
		// adjust column widths
		TableConfig.setColumnWidths(psmTbl, new double[] { 0, 1, 6, 1.5, 2.5 });
		
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

		// Add filter button widgets to column headers
		tcm.getColumn(PSM_SELECTION).setHeaderRenderer(new ComponentHeaderRenderer(selChk, null));
		tcm.getColumn(PSM_SELECTION).setMinWidth(19);
		tcm.getColumn(PSM_SELECTION).setMaxWidth(19);
		for (int col = PSM_INDEX; col < tcm.getColumnCount(); col++) {
			tcm.getColumn(col).setHeaderRenderer(new ComponentHeaderRenderer(new JLabel()));
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
					psmTblMdl.setValueAt(selected, row, PSM_SELECTION);
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
		
		dbSearchResult = clientFrame.getClient().getDbSearchResult(
				clientFrame.getProjectPanel().getCurrentProjectContent(),
				clientFrame.getProjectPanel().getCurrentExperimentContent());
		
		if (dbSearchResult != null && !dbSearchResult.isEmpty()) {
			TableConfig.clearTable(proteinTbl);
			boolean selected = ((AbstractButton) ((ComponentHeaderRenderer) proteinTbl.getColumnModel().getColumn(PROT_SELECTION).getHeaderRenderer()).getComponent()).isSelected();

			DefaultTableModel proteinTblMdl = (DefaultTableModel) proteinTbl.getModel();
			int i = 1, maxPeptideCount = 0, maxSpecCount = 0;
			double maxCoverage = 0.0, maxNSAF = 0.0, max_emPAI = 0.0, min_emPAI = Double.MAX_VALUE;
			for (Entry<String, ProteinHit> entry : dbSearchResult.getProteinHits().entrySet()) {
				
				ProteinHit proteinHit = entry.getValue();

				// Protein species
				String desc = proteinHit.getDescription();
				String[] split = desc.split(" OS=");
				if (split.length > 1) {
					proteinHit.setDescription(split[0]);
					String species = split[1].substring(0, split[1].indexOf(" GN="));
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
						selected,
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
			}
		}
	}

	/**
	 * Method to refresh peptide table contents.
	 */
	protected void refreshPeptideTable() {
		TableConfig.clearTable(peptideTbl);

		int protRow = proteinTbl.getSelectedRow();
		if (protRow != -1) {
			String actualAccession = (String) proteinTbl.getValueAt(protRow, proteinTbl.convertColumnIndexToView(PROT_ACCESSION));
			ProteinHit proteinHit = dbSearchResult.getProteinHits().get(actualAccession);
			List<PeptideHit> peptideHits = proteinHit.getPeptideHitList();

			DefaultTableModel peptideTblMdl = (DefaultTableModel) peptideTbl.getModel();
			int i = 1, maxSpecCount = 0;
			for (PeptideHit peptideHit : peptideHits) {
				int specCount = peptideHit.getSpectrumMatches().size();
				maxSpecCount = Math.max(maxSpecCount, specCount);
				peptideTblMdl.addRow(new Object[] {
						i++,
						peptideHit.getSequence(),
						specCount});
			}

			FontMetrics fm = getFontMetrics(chartFont);
			TableColumnModel tcm = peptideTbl.getColumnModel();

			BarChartHighlighter highlighter;
			
			highlighter = (BarChartHighlighter) ((TableColumnExt) tcm.getColumn(peptideTbl.convertColumnIndexToView(PEP_SPECTRALCOUNT))).getHighlighters()[0];
			highlighter.setBaseline(fm.stringWidth(highlighter.getFormatter().format(maxSpecCount)));
			highlighter.setRange(0.0, maxSpecCount);
			
			peptideTbl.getSelectionModel().setSelectionInterval(0, 0);
		}
	}
	
	/**
	 * Method to refresh PSM table contents.
	 */
	protected void refreshPsmTable() {
		TableConfig.clearTable(psmTbl);
		
		int protRow = proteinTbl.getSelectedRow();
		if (protRow != -1) {
			String actualAccession = (String) proteinTbl.getValueAt(protRow, proteinTbl.convertColumnIndexToView(PROT_ACCESSION));
			int pepRow = peptideTbl.getSelectedRow();
			if (pepRow != -1) {
				String sequence = (String) peptideTbl.getValueAt(pepRow, peptideTbl.convertColumnIndexToView(PEP_SEQUENCE));
				PeptideHit peptideHit = dbSearchResult.getProteinHits().get(actualAccession).getPeptideHits().get(sequence);
				
				DefaultTableModel psmTblMdl = (DefaultTableModel) psmTbl.getModel();
				int i = 1, maxVotes = 0;
				for (SpectrumMatch sm : peptideHit.getSpectrumMatches()) {
//					PeptideSpectrumMatch psm = (PeptideSpectrumMatch) entry.getValue();
					PeptideSpectrumMatch psm = (PeptideSpectrumMatch) sm;
					List<SearchEngineHit> searchEngineHits = psm.getSearchEngineHits();
					double[] qValues = { 0.0, 0.0, 0.0, 0.0 };
					for (SearchEngineHit searchEngineHit : searchEngineHits) {
						switch (searchEngineHit.getType()) {
						case XTANDEM:
							qValues[0] = 1.0 - searchEngineHit.getQvalue(); break;
						case OMSSA:
							qValues[1] = 1.0 - searchEngineHit.getQvalue(); break;
						case CRUX:
							qValues[2] = 1.0 - searchEngineHit.getQvalue(); break;
						case INSPECT:
							qValues[3] = 1.0 - searchEngineHit.getQvalue(); break;
						}
					}
					maxVotes = Math.max(maxVotes, psm.getVotes());
					psmTblMdl.addRow(new Object[] {
							true,
							i++,
							peptideHit.getSequence(),
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
						MascotGenericFile mgf = clientFrame.getClient().getSpectrumFromSearchSpectrumID(psm.getSearchSpectrumID());
						
						specPnl = new SpectrumPanel(mgf);
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
		for (SearchEngineHit hit : psm.getSearchEngineHits()) {
			switch (hit.getType()) {
			case XTANDEM:
				XTandemhit xtandemhit = (XTandemhit) hit.getSearchhit();
				xTandemLbl.setText("Conf: " + df.format((1.0 - hit.getQvalue())*100.0) + "%" +
						" | HScore: " + df.format(xtandemhit.getHyperscore().doubleValue()) + 
						" | E-value: "  + df.format(xtandemhit.getEvalue().doubleValue()));
				panel.add(new JLabel("<html><font color='#00FF00'>\u25cf</font> X!Tandem</html>"), CC.xy(1,yIndex));
				panel.add(xTandemLbl, CC.xy(3, yIndex));
				yIndex += 2;
				break;
			case OMSSA:
				Omssahit omssahit = (Omssahit) hit.getSearchhit();
				omssaLbl.setText("Conf: " + df.format((1.0 - hit.getQvalue())*100.0) + "%" +
						" | P-value: " + df.format(omssahit.getPvalue().doubleValue()) + 
						" | E-value: "  + df.format(omssahit.getEvalue().doubleValue()));
				panel.add(new JLabel("<html><font color='#00FFFF'>\u25cf</font> OMSSA</html>"), CC.xy(1,yIndex));
				panel.add(omssaLbl, CC.xy(3,yIndex));
				yIndex += 2;
				break;
			case CRUX:
				Cruxhit cruxhit = (Cruxhit) hit.getSearchhit();
				cruxLbl.setText("Conf: " + df.format((1.0 - hit.getQvalue())*100.0) + "%" +
						" | XCorr: " + df.format(cruxhit.getXcorr_score().doubleValue()));
				panel.add(new JLabel("<html><font color='#0000FF'>\u25cf</font> Crux</html>"), CC.xy(1,yIndex));
				panel.add(cruxLbl, CC.xy(3,yIndex));
				yIndex += 2;
				break;
			case INSPECT:
				Inspecthit inspecthit = (Inspecthit) hit.getSearchhit();
				inspectLbl.setText("Conf: " + df.format((1.0 - hit.getQvalue())*100.0) + "%" +
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
		String sequence = (String) peptideTbl.getValueAt(peptideTbl.getSelectedRow(),peptideTbl.convertColumnIndexToView(PEP_SEQUENCE) );
		int[][] ionCoverage = new int[sequence.length() + 1][12];

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
                if (!aIonsTgl.isSelected()) {
                    useAnnotation = false;
                }
            } else if (currentLabel.lastIndexOf("b") != -1) {
                if (!bIonsTgl.isSelected()) {
                    useAnnotation = false;
                }
            } else if (currentLabel.lastIndexOf("c") != -1) {
                if (!cIonsTgl.isSelected()) {
                    useAnnotation = false;
                }
            } else if (currentLabel.lastIndexOf("x") != -1) {
                if (!xIonsTgl.isSelected()) {
                    useAnnotation = false;
                }
            } else if (currentLabel.lastIndexOf("y") != -1) {
                if (!yIonsTgl.isSelected()) {
                    useAnnotation = false;
                }
            } else if (currentLabel.lastIndexOf("z") != -1) {
                if (!zIonsTgl.isSelected()) {
                    useAnnotation = false;
                }
            } else if (currentLabel.lastIndexOf("M") != -1) {
                if (!precursorTgl.isSelected()) {
                    useAnnotation = false;
                }
            }

            // check ion charge + ammonium and water-loss
            if (useAnnotation) {
                if (currentLabel.lastIndexOf("+") == -1) {
                    if (!chargeOneTgl.isSelected()) {
                        useAnnotation = false;
                    }
                } else if (currentLabel.lastIndexOf("+++") != -1) {
                    if (!chargeMoreTgl.isSelected()) {
                        useAnnotation = false;
                    }
                } else if (currentLabel.lastIndexOf("++") != -1) {
                    if (!chargeTwoTgl.isSelected()) {
                        useAnnotation = false;
                    }
                }
                
                if (currentLabel.lastIndexOf("*") != -1) {
                    if (!ammoniumLossTgl.isSelected()) {
                        useAnnotation = false;
                    }
                } else if (currentLabel.lastIndexOf("°") != -1) {
                    if (!waterLossTgl.isSelected()) {
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
	public JPanel createFilterButton(int column, JTable table){
		// button widget with little black triangle
		final FilterButton button = new FilterButton(column, table);
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
		public FilterButton(final int column, final JTable table) {
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
						filterTip = new FilterBalloonTip(th, rect);
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
			List<Number> greater = new ArrayList<Number>();
			List<Number> greaterEqual = new ArrayList<Number>();
			List<Number> less = new ArrayList<Number>();
			List<Number> lessEqual = new ArrayList<Number>();
			// parse filter strings
			String[] filterStrings = filterString.split(",");
			for (int i = 0; i < filterStrings.length; i++) {
				String s = filterStrings[i].trim();
				// TODO: maybe add more symbols, e.g. !=
				if (s.startsWith(">")) {
					s = s.substring(1);
					if (s.startsWith("=")) {
						greaterEqual.add(Double.parseDouble(s.substring(1)));
					} else {
						greater.add(Double.parseDouble(s));
					}
				} else if (s.startsWith("<")){
					s = s.substring(1);
					if (s.startsWith("=")) {
						lessEqual.add(Double.parseDouble(s.substring(1)));
					} else {
						less.add(Double.parseDouble(s));
					}
				}
			}
//			List<Interval> intervals = new ArrayList<Interval>();
			// iterate table rows and check intervals
			for (int row = 0; row < proteinTbl.getRowCount(); row++) {
				// TODO: extend interval class to contain inclusive/exclusive border capabilities
				// TODO: build intervals
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
				String value = proteinTbl.getValueAt(row, column).toString();
				boolean selected = true;
				// check excludes
				for (String s : restrictive) {
					selected &= !value.contains(s);
					if (!selected) break;
				}
				// check includes
				for (String s : permissive) {
					if (selected) break;
					selected |= value.contains(s);
				}
				proteinTbl.setValueAt(selected, row, proteinTbl.convertColumnIndexToModel(PROT_SELECTION));
			}
		}
		((TableSortController) proteinTbl.getRowSorter()).setSortsOnUpdates(true);
		// re-sort rows after iteration finished
		((TableSortController) proteinTbl.getRowSorter()).sort();
	}
}
