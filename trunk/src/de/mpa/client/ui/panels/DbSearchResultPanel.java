package de.mpa.client.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.net.URI;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

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

import de.mpa.algorithms.quantification.NormalizedSpectralAbundanceFactor;
import de.mpa.analysis.Masses;
import de.mpa.analysis.ProteinAnalysis;
import de.mpa.client.model.SpectrumMatch;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.PeptideSpectrumMatch;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.ComponentHeader;
import de.mpa.client.ui.ComponentHeaderRenderer;
import de.mpa.client.ui.PanelConfig;
import de.mpa.client.ui.TableConfig;
import de.mpa.client.ui.TableConfig.CustomTableCellRenderer;
import de.mpa.client.ui.dialogs.GeneralExceptionHandler;
import de.mpa.fragmentation.FragmentIon;
import de.mpa.fragmentation.Fragmentizer;
import de.mpa.io.MascotGenericFile;

public class DbSearchResultPanel extends JPanel{
	
	private ClientFrame clientFrame;
	private JXTable proteinTbl;
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
	
	// Protein table column indices
	private static int selection 		= 0;
	private static int number 			= 1;
	private static int accession		= 2;
	private static int description		= 3;
	private static int species			= 4;
	private static int coverage			= 5;
	private static int mw				= 6;
	private static int pI				= 7;
	private static int peptideCount		= 8;
	private static int spectralCount	= 9;
	private static int emPAI			=10;
	private static int nSAF				=11;
	
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
		chartFont = UIManager.getFont("Label.font").deriveFont(12f);
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
		
		getResultsBtn = new JButton("Get Results");
		getResultsBtn.setEnabled(false);
		
		getResultsBtn.setPreferredSize(new Dimension(150, 20));
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
		
		JButton yatb = new JButton("hrmpf");
		yatb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ComponentHeader ch = (ComponentHeader) proteinTbl.getTableHeader();
				System.out.println(((ComponentHeaderRenderer) proteinTbl.getColumnModel().getColumn(peptideCount).getHeaderRenderer()).getPanel());
				ch.repaint();
			}
		});
		specTtlPnl.setRightDecoration(yatb);
		
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
		// Protein table
		final DefaultTableModel proteinTblMdl = new DefaultTableModel() {
			// instance initializer block
			{ setColumnIdentifiers(new Object[] {
					" ", 				//0
					"#", 				//1
					"Accession", 		//2
					"Description",  	//3
					"Species", 			//4
					"Coverage [%]", 	//5
					"MW [kDa]",     	//6
					"pI",				//7
					"Peptide Count", 	//8
					"Spectral Count",	//9
					"emPAI",			//10
					"NSAF"}); }			//11
	
			public boolean isCellEditable(int row, int col) {
				return false;
			}
			
			public Class<?> getColumnClass(int columnIndex) {
				switch (columnIndex) {
				case 0: 
				case 1:	
				case 2:
				case 3:	
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
				case 9:
					return Integer.class;
				case 10:
				case 11:
					return Double.class;
				default:
					return String.class;
				}
			}
		};
		proteinTbl = new JXTable(proteinTblMdl);
		
		TableConfig.setColumnWidths(proteinTbl, new double[] { 2, 2, 6, 8, 7, 7, 5.5, 3, 8, 8, 5.5, 6 });
		TableConfig.setColumnMinWidths(proteinTbl, 1);
		
		// Create table model
		final TableColumnModel tcm = proteinTbl.getColumnModel();
		
		// apply component header capabilities
		proteinTbl.setTableHeader(new ComponentHeader(tcm));
		
		// Add checkbox to selection column
		TableColumn column = proteinTbl.getColumnModel().getColumn(selection);
		column.setCellRenderer(proteinTbl.getDefaultRenderer(Boolean.class));
		column.setCellEditor(proteinTbl.getDefaultEditor(Boolean.class));

		
		
		tcm.getColumn(accession).setCellRenderer(new CustomTableCellRenderer(SwingConstants.RIGHT));
		
		AbstractHyperlinkAction<URI> linkAction = new AbstractHyperlinkAction<URI>() {
		    public void actionPerformed(ActionEvent ev) {
		        try {
		            Desktop.getDesktop().browse(new URI("http://www.uniprot.org/uniprot/" + target));
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		    }
		};
		tcm.getColumn(accession).setCellRenderer(new DefaultTableRenderer(new HyperlinkProvider(linkAction)) {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				JXRendererHyperlink compLabel = (JXRendererHyperlink) comp;
				compLabel.setHorizontalAlignment(SwingConstants.CENTER);
				return compLabel;
			}
		});
		tcm.getColumn(description).setCellRenderer(new CustomTableCellRenderer(SwingConstants.LEFT));
		tcm.getColumn(species).setCellRenderer(new CustomTableCellRenderer(SwingConstants.LEFT));
		tcm.getColumn(coverage).setCellRenderer(new JSparklinesBarChartTableCellRenderer(PlotOrientation.HORIZONTAL, 0.0, true));
		tcm.getColumn(mw).setCellRenderer(new CustomTableCellRenderer(SwingConstants.CENTER, "0.000"));
		tcm.getColumn(pI).setCellRenderer(new CustomTableCellRenderer(SwingConstants.CENTER, "#0.00"));
		tcm.getColumn(peptideCount).setCellRenderer(new JSparklinesBarChartTableCellRenderer(PlotOrientation.HORIZONTAL, 0.0, true));
		tcm.getColumn(spectralCount).setCellRenderer(new JSparklinesBarChartTableCellRenderer(PlotOrientation.HORIZONTAL, 0.0, true));
		tcm.getColumn(emPAI).setCellRenderer(new JSparklinesBarChartTableCellRenderer(PlotOrientation.HORIZONTAL, 0.0, 9.0));
		
		final JPopupMenu testPopup = new JPopupMenu();
//		testPopup.add(new JMenuItem("here be text"));
		final JTextField testTtf = new JTextField(10);
		testPopup.add(testTtf);
		final JToggleButton testBtn = new JToggleButton();
		testBtn.setPreferredSize(new Dimension(15, 12));
		testBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (testBtn.isSelected()) {
					Rectangle rect = proteinTbl.getTableHeader().getHeaderRect(
							proteinTbl.convertColumnIndexToView(9));
					testPopup.show(proteinTbl.getTableHeader(), rect.x - 1,
							proteinTbl.getTableHeader().getHeight() - 1);
					testTtf.requestFocus();
				} else {
					testPopup.setVisible(false);
					proteinTbl.requestFocus();
				}
			}
		});
		testTtf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				testPopup.setVisible(false);
				proteinTbl.requestFocus();
				testBtn.setSelected(false);
				proteinTbl.getTableHeader().repaint();
			}
		});
		testTtf.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent fe) {
				testPopup.setVisible(false);
				if (!testBtn.getModel().isArmed()) {
					testBtn.setSelected(false);
					proteinTbl.requestFocus();
					proteinTbl.getTableHeader().repaint();
				}
			};
		});
		tcm.getColumn(emPAI).setHeaderRenderer(new ComponentHeaderRenderer(testBtn));
		
		FontMetrics fm = getFontMetrics(chartFont);
		String pattern = "0.000";
		((JSparklinesBarChartTableCellRenderer) tcm.getColumn(emPAI).getCellRenderer()).showNumberAndChart(true, fm.stringWidth("   " + pattern), chartFont, SwingConstants.CENTER, new DecimalFormat(pattern));
		
		// TODO: this is just a hack to work around JSparklinesBarChartTableCellRenderer failing to render properly here, find a proper solution
		pattern = "0.00000";
		final DecimalFormat formatter = new DecimalFormat(pattern);
		JSparklinesBarChartTableCellRenderer renderer = new JSparklinesBarChartTableCellRenderer(PlotOrientation.HORIZONTAL, 0.0, 1.0) {
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
						row, column);
				JLabel valueLabel = (JLabel) this.getComponent(0);
				valueLabel.setText(formatter.format(Double.valueOf(valueLabel.getText()) / 100.0));
				return comp;
			}
		};
		renderer.showNumberAndChart(true, fm.stringWidth("   " + pattern), chartFont, SwingConstants.CENTER);
		tcm.getColumn(nSAF).setCellRenderer(renderer);
		
		// Sort the protein table by spectral count
		proteinTbl.setAutoCreateRowSorter(true);
		proteinTbl.getRowSorter().toggleSortOrder(spectralCount);
		proteinTbl.getRowSorter().toggleSortOrder(spectralCount);
		
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
		
		tcm.getColumn(2).setCellRenderer(new CustomTableCellRenderer(SwingConstants.RIGHT));
		tcm.getColumn(2).setCellRenderer(new JSparklinesBarChartTableCellRenderer(PlotOrientation.HORIZONTAL, 1.0, true));
		
		// Sort the peptide table by the number of peptide hits
		peptideTbl.setAutoCreateRowSorter(true);
		peptideTbl.getRowSorter().toggleSortOrder(2);
		peptideTbl.getRowSorter().toggleSortOrder(2);
		
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
	}
	
	/**
	 * This method sets up the PSM results table.
	 */
	private void setupPsmTableProperties() {
		// PSM table
		psmTbl = new JXTable(new DefaultTableModel() {
			{ setColumnIdentifiers(new Object[] {" ", "Sequence", "z", "Votes"}); }
	
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		});
		
		TableConfig.setColumnWidths(psmTbl, new double[] { 1, 6, 1.5, 2.5 });
		
		TableColumnModel tcm = psmTbl.getColumnModel();
		
		tcm.getColumn(0).setCellRenderer(new CustomTableCellRenderer(SwingConstants.RIGHT));
		tcm.getColumn(2).setCellRenderer(new CustomTableCellRenderer(SwingConstants.CENTER, "+0"));
		tcm.getColumn(3).setCellRenderer(new CustomTableCellRenderer(SwingConstants.CENTER));;
		
		// Sort the PSM table by the number of votes
		psmTbl.setAutoCreateRowSorter(true);
		psmTbl.getRowSorter().toggleSortOrder(3);
		psmTbl.getRowSorter().toggleSortOrder(3);

		// register list selection listener
		psmTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				refreshPlot();
			}
		});
	
		// Only one row is selectable
		psmTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		// Add nice striping effect
		psmTbl.addHighlighter(TableConfig.getSimpleStriping());
		
		// Enables column control
		psmTbl.setColumnControlVisible(true);
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
		
		if (dbSearchResult != null) {
			TableConfig.clearTable(proteinTbl);
			
			int i = 1, maxPeptideCount = 0, maxSpecCount = 0;
			double maxCoverage = 0.0, maxNSAF = 0.0, max_emPAI = 0.0, min_emPAI = Double.MAX_VALUE;
			for (Entry<String, ProteinHit> entry : dbSearchResult.getProteinHits().entrySet()) {
				
				ProteinHit proteinHit = entry.getValue();

				// Protein species
				String desc = proteinHit.getDescription();
				String[] split = desc.split(" OS=");
				proteinHit.setDescription(split[0]);
				String species = split[1].substring(0, split[1].indexOf(" GN="));
				proteinHit.setSpecies(species);
				
				double nsaf = ProteinAnalysis.calculateLabelFree(new NormalizedSpectralAbundanceFactor(), dbSearchResult.getProteinHits(), proteinHit);
				proteinHit.setNSAF(nsaf);
				maxCoverage = Math.max(maxCoverage, proteinHit.getCoverage());
				maxPeptideCount = Math.max(maxPeptideCount, proteinHit.getPeptideCount());
				maxSpecCount = Math.max(maxSpecCount, proteinHit.getSpectralCount());
				maxNSAF = Math.max(maxNSAF, nsaf);
				max_emPAI = Math.max(max_emPAI, proteinHit.getEmPAI());
				min_emPAI = Math.min(min_emPAI, proteinHit.getEmPAI());
				
				((DefaultTableModel) proteinTbl.getModel()).addRow(new Object[] {
						true,
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
						proteinHit.getNSAF()});
			}
			
			FontMetrics fm = getFontMetrics(chartFont);
			DecimalFormat df = new DecimalFormat("##0.00");
			TableColumnModel tcm = proteinTbl.getColumnModel();
			JSparklinesBarChartTableCellRenderer renderer;
			
			renderer = (JSparklinesBarChartTableCellRenderer) tcm.getColumn(coverage).getCellRenderer();
			renderer.setMaxValue(maxCoverage);
			renderer.showNumberAndChart(true, fm.stringWidth("   " + df.format(maxCoverage)), chartFont, SwingConstants.RIGHT, df);
			
			renderer = (JSparklinesBarChartTableCellRenderer) tcm.getColumn(peptideCount).getCellRenderer();
			renderer.setMaxValue(maxPeptideCount);
			renderer.showNumberAndChart(true, fm.stringWidth("   " + maxPeptideCount), chartFont, SwingConstants.RIGHT);
			
			renderer = (JSparklinesBarChartTableCellRenderer) tcm.getColumn(spectralCount).getCellRenderer();
			renderer.setMaxValue(maxSpecCount);
			renderer.showNumberAndChart(true, fm.stringWidth("   " + maxSpecCount), chartFont, SwingConstants.RIGHT);
			
			renderer = (JSparklinesBarChartTableCellRenderer) tcm.getColumn(emPAI).getCellRenderer();
			renderer.setMinValue(min_emPAI);
			renderer.setMaxValue(max_emPAI);
			
			renderer = (JSparklinesBarChartTableCellRenderer) tcm.getColumn(nSAF).getCellRenderer();
			renderer.setMaxValue(maxNSAF);
			
			proteinTbl.getSelectionModel().setSelectionInterval(0, 0);
		}
	}

	/**
	 * Method to refresh peptide table contents.
	 */
	protected void refreshPeptideTable() {
		TableConfig.clearTable(peptideTbl);

		int protRow = proteinTbl.getSelectedRow();
		if (protRow != -1) {
			String actualAccession = (String) proteinTbl.getValueAt(protRow, accession);
			ProteinHit proteinHit = dbSearchResult.getProteinHits().get(actualAccession);
			List<PeptideHit> peptideHits = proteinHit.getPeptideHitList();
			
			int i = 1, maxSpecCount = 0;
			for (PeptideHit peptideHit : peptideHits) {
				int specCount = peptideHit.getSpectrumMatches().size();
				maxSpecCount = Math.max(maxSpecCount, specCount);
				((DefaultTableModel) peptideTbl.getModel()).addRow(new Object[] {
						i++,
						peptideHit.getSequence(),
						specCount});
			}

			FontMetrics fm = getFontMetrics(chartFont);
			TableColumnModel tcm = peptideTbl.getColumnModel();
			JSparklinesBarChartTableCellRenderer renderer;

			renderer = (JSparklinesBarChartTableCellRenderer) tcm.getColumn(2).getCellRenderer();
			renderer.setMaxValue(maxSpecCount);
			renderer.showNumberAndChart(true, fm.stringWidth("   " + maxSpecCount), chartFont, SwingConstants.RIGHT);

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
			String actualAccession = (String) proteinTbl.getValueAt(protRow, accession);
			int pepRow = peptideTbl.getSelectedRow();
			if (pepRow != -1) {
				String sequence = (String) peptideTbl.getValueAt(pepRow, 1);
				PeptideHit peptideHit = dbSearchResult.getProteinHits().get(actualAccession).getPeptideHits().get(sequence);
				
				int i = 1;
				for (Entry<Long, SpectrumMatch> entry : peptideHit.getSpectrumMatches().entrySet()) {
					PeptideSpectrumMatch psm = (PeptideSpectrumMatch) entry.getValue();
					((DefaultTableModel) psmTbl.getModel()).addRow(new Object[] {
							i++,
							peptideHit.getSequence(),
							psm.getCharge(),
							psm.getVotes()});
				}
				psmTbl.getSelectionModel().setSelectionInterval(0, 0);
			}
		}
	}
	
	protected void refreshPlot() {
		int protRow = proteinTbl.getSelectedRow();
		if (protRow != -1) {
			int pepRow = peptideTbl.getSelectedRow();
			if (pepRow != -1) {
				int ssmRow = psmTbl.getSelectedRow();
				if (ssmRow != -1) {
					// clear the spectrum panel
					while (spectrumJPanel.getComponents().length > 0) {
		                spectrumJPanel.remove(0);
		            }
					// get the list of spectrum matches
					String actualAccession = (String) proteinTbl.getValueAt(protRow, accession);
					String sequence = (String) peptideTbl.getValueAt(pepRow, 1);
					Iterator<SpectrumMatch> iter = dbSearchResult.getProteinHits().get(actualAccession).getPeptideHits().get(sequence).getSpectrumMatches().values().iterator();
					// find the n-th spectrum match
					int i = 0, index = (Integer) psmTbl.getValueAt(ssmRow, 0);
					PeptideSpectrumMatch psm = null;
					while (i < index) {
						psm = (PeptideSpectrumMatch) iter.next();
						i++;
					}
					// grab corresponding spectrum from the database and display it
					try {
						MascotGenericFile mgf = clientFrame.getClient().getSpectrumFromSearchSpectrumID(psm.getSpectrumID());
						
						specPnl = new SpectrumPanel(mgf);
						spectrumJPanel.add(specPnl, CC.xy(2, 2));
						spectrumJPanel.validate();
						spectrumJPanel.repaint();
						
						Fragmentizer fragmentizer = new Fragmentizer(sequence, Masses.getInstance(), psm.getCharge());
						addSpectrumAnnotations(fragmentizer.getFragmentIons());
						
					} catch (SQLException sqe) {
						GeneralExceptionHandler.showSQLErrorDialog(sqe, clientFrame);
					}
				}
			}
		}
	}

	/**
	 * Method to add annotations to spectrum plot.
	 * @param fragmentIons
	 */
	private void addSpectrumAnnotations(Map<String, FragmentIon[]> fragmentIons) {
		String sequence = (String) peptideTbl.getValueAt(peptideTbl.getSelectedRow(), 1);
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
	 * This method enables the get results button.
	 */
	public void setResultsBtnEnabled(boolean enabled) {
		getResultsBtn.setEnabled(enabled);
	}
}
