package de.mpa.client.ui.panels;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.event.IIOWriteProgressListener;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXMultiSplitPane;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.MultiSplitLayout;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;
import org.jdesktop.swingx.hyperlink.AbstractHyperlinkAction;
import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.HyperlinkProvider;
import org.jdesktop.swingx.renderer.JXRendererHyperlink;
import org.jdesktop.swingx.table.TableColumnExt;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Client;
import de.mpa.client.model.SpectrumMatch;
import de.mpa.client.model.dbsearch.PeptideHit;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.model.specsim.SpecSimResult;
import de.mpa.client.model.specsim.SpectrumSpectrumMatch;
import de.mpa.client.ui.BarChartHighlighter;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.ConfirmFileChooser;
import de.mpa.client.ui.Constants;
import de.mpa.client.ui.PanelConfig;
import de.mpa.client.ui.TableConfig;
import de.mpa.client.ui.TableConfig.CustomTableCellRenderer;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.io.MascotGenericFile;
import de.mpa.ui.MultiPlotPanel;
import de.mpa.util.ColorUtils;

public class SpecSimResultPanel extends JPanel {

	private JXTable proteinTbl;
	private JXTable peptideTbl;
	private JXTable ssmTbl;
	private JButton getResultsBtn;
	private JPanel specPnl;
	private JPanel matPnl;
	private MultiPlotPanel plotPnl;
	protected SpecSimResult specSimResult;
	private Font chartFont;
	private JLabel matLbl;
	private ColorModel matrixColorModel;
	protected BufferedImage zoomImg;
	protected int zoomX = 3;
	protected int zoomY = 3;
	private boolean busy;
	private JXMultiSplitPane split;
	private JButton saveImgBtn;

	/**
	 * Class constructor defining the parent client frame.
	 * 
	 * @param clientFrame
	 */
	public SpecSimResultPanel() {
		initComponents();
	}

	/**
	 * Initializes the components of the database search results panel
	 */
	private void initComponents() {
		
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
		setupSsmTableProperties();
		setupViewerPanelProperties();
		
		// Scroll panes
		JScrollPane proteinTableScp = new JScrollPane(proteinTbl);
		proteinTableScp.setPreferredSize(new Dimension(800, 180));
		JScrollPane peptideTableScp = new JScrollPane(peptideTbl);
		peptideTableScp.setPreferredSize(new Dimension(350, 130));
		JScrollPane ssmTableScp = new JScrollPane(ssmTbl);
		ssmTableScp.setPreferredSize(new Dimension(350, 130));
		
		getResultsBtn = new JButton("Get Results   ", IconConstants.GO_DB_ICON);
		getResultsBtn.setRolloverIcon(IconConstants.GO_DB_ROLLOVER_ICON);
		getResultsBtn.setPressedIcon(IconConstants.GO_DB_PRESSED_ICON);
		
		getResultsBtn.setEnabled(false);

		getResultsBtn.setPreferredSize(new Dimension(getResultsBtn.getPreferredSize().width, 20));
		getResultsBtn.setFocusPainted(false);
		
		getResultsBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				new SwingWorker() {
					@Override
					protected Object doInBackground() {
						try {
							// appear busy
							setBusy(true);
							// process results
							refreshProteinTable();
						} catch (Exception e) {
							JXErrorPane.showDialog(ClientFrame.getInstance(),
									new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
						} finally {
							// stop appearing busy
							setBusy(false);
						}
						return null;
					}
				}.execute();
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
		final JPanel ssmPanel = new JPanel();
		ssmPanel.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));
		ssmPanel.add(ssmTableScp, CC.xy(2, 2));
		
		JXTitledPanel ssmTtlPnl = new JXTitledPanel("Spectrum-Spectrum-Matches", ssmPanel);
		ssmTtlPnl.setTitleFont(ttlFont);
		ssmTtlPnl.setTitlePainter(ttlPainter);
		ssmTtlPnl.setBorder(ttlBorder);

		// Viewer panel
		final CardLayout cl = new CardLayout();
		final JPanel viewPnl = new JPanel(cl);
		
		// add cards
		viewPnl.add(specPnl, "Spectrum");
		viewPnl.add(matPnl, "Matrix");

		// build control button panel for card layout
		JButton swapBtn = new JButton("\u203A");
		swapBtn.setPreferredSize(new Dimension(19, 18));
		
		// wrap viewer panels in titled panel with control buttons in title
		final JXTitledPanel specTtlPnl = new JXTitledPanel("Spectrum Viewer", viewPnl); 
		specTtlPnl.setTitleFont(ttlFont);
		specTtlPnl.setTitlePainter(ttlPainter);
		specTtlPnl.setBorder(ttlBorder);
		specTtlPnl.setRightDecoration(swapBtn);

		swapBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cl.previous(viewPnl);
				Component[] components = viewPnl.getComponents();
				for (Component component : components) {
					if (component.isVisible()) {
						if (component == specPnl) {
							specTtlPnl.setTitle("Spectrum Viewer");
						} else {
							specTtlPnl.setTitle("Score Matrix Viewer");
						}
					}
				}
			}
		});
		
		String layoutDef =
		    "(COLUMN protein (ROW weight=0.0 (COLUMN (LEAF weight=0.5 name=peptide) (LEAF weight=0.5 name=ssm)) plot))";
		MultiSplitLayout.Node modelRoot = MultiSplitLayout.parseModel(layoutDef);
		
		split = new JXMultiSplitPane() {
			@Override
			public void setCursor(Cursor cursor) {
				if (busy) {
					if ((cursor == null) || (cursor.getType() == Cursor.DEFAULT_CURSOR)) {
						cursor = ClientFrame.getInstance().getCursor();
					}
				}
				super.setCursor(cursor);
			}
		};
		split.setDividerSize(12);
		split.getMultiSplitLayout().setModel(modelRoot);
		split.add(protTtlPnl, "protein");
		split.add(pepTtlPnl, "peptide");
		split.add(ssmTtlPnl, "ssm");
		split.add(specTtlPnl, "plot");
		
		this.add(split, CC.xy(2, 2));
	}

	// Protein table column indices
	private final int PROT_SELECTION = -1;
	private final int PROT_INDEX = 0;
	private final int PROT_ACCESSION = 1;
	private final int PROT_DESCRIPTION = 2;
	private final int PROT_COVERAGE = 3;
	private final int PROT_MW = 4;
	private final int PROT_PEPTIDECOUNT = 5;
	private final int PROT_SPECTRALCOUNT = 6;

	/**
	 * This method sets up the protein results table.
	 */
	private void setupProteinTableProperties() {
		// Protein table
		TableModel proteinTblMdl = new DefaultTableModel() {
			// instance initializer block
			{
				setColumnIdentifiers(new Object[] { " ", "Accession",
						"Description", "Coverage [%]", "MW [kDa]",
						"Peptide Count", "Spectral Count" });
			}

			public boolean isCellEditable(int row, int col) {
				return false;
			}

			public Class<?> getColumnClass(int columnIndex) {
				switch (columnIndex) {
				case PROT_SELECTION:
					return Boolean.class;
				case PROT_COVERAGE:
				case PROT_MW:
					return Double.class;
				case PROT_INDEX:
				case PROT_PEPTIDECOUNT:
				case PROT_SPECTRALCOUNT:
					return Integer.class;
				case PROT_ACCESSION:
				case PROT_DESCRIPTION:
				default:
					return String.class;
				}
			}
		};
		proteinTbl = new JXTable(proteinTblMdl) {
			private Border padding = BorderFactory
					.createEmptyBorder(0, 2, 0, 2);

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

		TableConfig.setColumnWidths(proteinTbl, new double[] { 2, 6, 30, 7,
				5.5, 8, 8 });

		TableColumnModel tcm = proteinTbl.getColumnModel();

		tcm.getColumn(PROT_INDEX).setCellRenderer(
				new CustomTableCellRenderer(SwingConstants.RIGHT));

		AbstractHyperlinkAction<URI> linkAction = new AbstractHyperlinkAction<URI>() {
			public void actionPerformed(ActionEvent ev) {
				try {
					Desktop.getDesktop()
							.browse(
									new URI("http://www.uniprot.org/uniprot/"
											+ target));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		tcm.getColumn(PROT_ACCESSION).setCellRenderer(
				new DefaultTableRenderer(new HyperlinkProvider(linkAction)) {
					public Component getTableCellRendererComponent(
							JTable table, Object value, boolean isSelected,
							boolean hasFocus, int row, int column) {
						Component comp = super
								.getTableCellRendererComponent(table, value,
										isSelected, hasFocus, row, column);
						JXRendererHyperlink compLabel = (JXRendererHyperlink) comp;
						compLabel.setHorizontalAlignment(SwingConstants.CENTER);
						return compLabel;
					}
				});
		tcm.getColumn(PROT_DESCRIPTION).setCellRenderer(
				new CustomTableCellRenderer(SwingConstants.LEFT));
		DecimalFormat percentFormatter = new DecimalFormat("0.00");
		percentFormatter.setMultiplier(100);
		((TableColumnExt) tcm.getColumn(PROT_COVERAGE))
				.addHighlighter(new BarChartHighlighter(0.0, 100.0, 50,
						SwingConstants.HORIZONTAL, Color.GREEN.darker()
								.darker(), Color.GREEN, percentFormatter));
		tcm.getColumn(PROT_MW).setCellRenderer(
				new CustomTableCellRenderer(SwingConstants.CENTER, "0.000"));
		((TableColumnExt) tcm.getColumn(PROT_PEPTIDECOUNT))
				.addHighlighter(new BarChartHighlighter());
		((TableColumnExt) tcm.getColumn(PROT_SPECTRALCOUNT))
				.addHighlighter(new BarChartHighlighter());

		proteinTbl.setAutoCreateRowSorter(true);
		proteinTbl.getRowSorter().toggleSortOrder(PROT_SPECTRALCOUNT);
		proteinTbl.getRowSorter().toggleSortOrder(PROT_SPECTRALCOUNT);

		proteinTbl.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
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

	// Peptide table column indices
	// private int PEP_SELECTION = X;
	private final int PEP_INDEX = 0;
	private final int PEP_SEQUENCE = 1;
	private final int PEP_SPECTRALCOUNT = 2;

	/**
	 * Method to set up the peptide results table.
	 */
	private void setupPeptideTableProperties() {
		// Peptide table
		final TableModel peptideTblMdl = new DefaultTableModel() {
			// instance initializer block
			{
				setColumnIdentifiers(new Object[] {
						"#",
						"Sequence",
						"No. Matches" });
			}

			public boolean isCellEditable(int row, int col) {
				return (col == PEP_SEQUENCE) ? true : false;
			}

			public Class<?> getColumnClass(int columnIndex) {
				switch (columnIndex) {
				case PEP_INDEX:
				case PEP_SPECTRALCOUNT:
					return Integer.class;
				default:
					return String.class;
				}
			}
		};
		peptideTbl = new JXTable(peptideTblMdl) {
			private Border padding = BorderFactory
					.createEmptyBorder(0, 2, 0, 2);

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

		TableConfig.setColumnWidths(peptideTbl, new double[] { 3, 24, 12 });

		TableColumnModel tcm = peptideTbl.getColumnModel();

		tcm.getColumn(PEP_INDEX).setCellRenderer(
				new CustomTableCellRenderer(SwingConstants.RIGHT));
		((TableColumnExt) tcm.getColumn(PEP_SPECTRALCOUNT))
				.addHighlighter(new BarChartHighlighter());

		final JTextField editor = new JTextField();
		editor.setEditable(false);
		editor.setBorder(BorderFactory.createLineBorder(UIManager
				.getColor("Table.dropLineColor")));
		editor.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent fe) {
				editor.selectAll();
			}
		});
		tcm.getColumn(PEP_SEQUENCE)
				.setCellEditor(new DefaultCellEditor(editor));

		// Sort the peptide table by the number of peptide hits
		peptideTbl.setAutoCreateRowSorter(true);
		peptideTbl.getRowSorter().toggleSortOrder(PEP_SPECTRALCOUNT);
		peptideTbl.getRowSorter().toggleSortOrder(PEP_SPECTRALCOUNT);

		// register list selection listener
		peptideTbl.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
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

	// PSM table column indices
	// private final int SSM_SELECTION = 0;
	private final int SSM_INDEX = 0;
	private final int SSM_TITLE = 1;
	private final int SSM_SCORE = 2;

	/**
	 * This method sets up the SSM results table.
	 */
	private void setupSsmTableProperties() {
		// SSM table
		TableModel ssmTblMdl = new DefaultTableModel() {
			// instance initializer block
			{
				setColumnIdentifiers(new Object[] { "#", "Spectrum Title",
						"Score" });
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				switch (columnIndex) {
				case SSM_INDEX:
					return Integer.class;
				case SSM_SCORE:
					return Double.class;
				case SSM_TITLE:
				default:
					return String.class;
				}
			}

			public boolean isCellEditable(int row, int col) {
				return false;
			}
		};
		ssmTbl = new JXTable(ssmTblMdl) {
			private Border padding = BorderFactory
					.createEmptyBorder(0, 2, 0, 2);

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

		TableConfig.setColumnWidths(ssmTbl, new double[] { 1, 8, 2 });

		TableColumnModel tcm = ssmTbl.getColumnModel();
		tcm.getColumn(SSM_INDEX).setCellRenderer(
				new CustomTableCellRenderer(SwingConstants.RIGHT));
		((TableColumnExt) tcm.getColumn(SSM_SCORE))
				.addHighlighter(new BarChartHighlighter(0.0, 1.0,
						getFontMetrics(chartFont).stringWidth("0.000"),
						SwingConstants.HORIZONTAL, Color.RED.darker().darker(),
						Color.RED, new DecimalFormat("0.000")));

		// Sort the SSM table by score
		ssmTbl.setAutoCreateRowSorter(true);
		ssmTbl.getRowSorter().toggleSortOrder(SSM_INDEX);
		ssmTbl.getRowSorter().toggleSortOrder(SSM_INDEX);

		// register list selection listener
		ssmTbl.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent evt) {
						refreshPlot();
					}
				});

		// Only one row is selectable
		ssmTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Add nice striping effect
		ssmTbl.addHighlighter(TableConfig.getSimpleStriping());

		// ssmTbl.addHighlighter(TableConfig.createGradientHighlighter(2, 1.0,
		// getFontMetrics(chartFont).stringWidth("1.000"),
		// SwingConstants.HORIZONTAL, Color.RED.darker().darker(), Color.RED,
		// new DecimalFormat("0.000")));

		// Enables column control
		ssmTbl.setColumnControlVisible(true);
	}
	
	private void setupViewerPanelProperties() {
		// Spectrum panel
		specPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu, 39px, 5dlu", "5dlu, f:39px, 5dlu, f:p:g, 5dlu"));

		// Spectrum plot
		plotPnl = new MultiPlotPanel();
		plotPnl.setBorder(BorderFactory.createEtchedBorder());
		
		// Button controls
		final JToggleButton normBtn = new JToggleButton(IconConstants.SIZE_VERT_ICON);
		normBtn.setRolloverIcon(IconConstants.SIZE_VERT_ROLLOVER_ICON);
		normBtn.setPressedIcon(IconConstants.SIZE_VERT_PRESSED_ICON);
		normBtn.setSelectedIcon(IconConstants.createRescaledIcon(IconConstants.SIZE_VERT_ICON, 0.9f));
		normBtn.setRolloverSelectedIcon(IconConstants.SIZE_VERT_ICON);
		normBtn.setMargin(new Insets(3, 4, 1, 4));
		normBtn.setToolTipText("Fit to height");
		
		normBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				plotPnl.repaint(normBtn.isSelected());
			}
		});
		
		// Add components to spectrum panel
		specPnl.add(plotPnl, CC.xywh(2, 2, 1, 3));
		specPnl.add(normBtn, CC.xy(4, 2));
		
		// Score matrix panel
		matPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu, 39px, 5dlu", "5dlu, f:39px, 5dlu, f:p, 5dlu, f:p:g, 5dlu, f:39px, 5dlu"));
		
		// The label whose icon will be used to display the score matrix image
		matLbl = new JLabel();
		
		// Create color model mapping scores to a red-yellow-green-cyan-blue gradient
		matrixColorModel = new ColorModel(32) {
			private int numCols = 256;
			double val2ind = 255.0 / (numCols-1);
			private Color[] colors = ColorUtils.getRainbowGradient(numCols);
			
			public int getAlpha(int pixel) {
				return 255;
			}
			public int getRed(int pixel) {
				int index = (int) (((pixel >> 16) & 0xFF) * val2ind);
				return colors[index].getRed();
			}
			public int getGreen(int pixel) {
				int index = (int) (((pixel >> 16) & 0xFF) * val2ind);
				return colors[index].getGreen();
			}
			public int getBlue(int pixel) {
				int index = (int) (((pixel >> 16) & 0xFF) * val2ind);
				return colors[index].getBlue();
			}
			@Override	// this is pretty hacky, but it works :)
			public boolean isCompatibleRaster(Raster raster) {
				return true;
			}
		};
		
		// Wrap matrix in scroll pane
		JScrollPane matScpn = new JScrollPane(matLbl);
		matScpn.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		matScpn.getHorizontalScrollBar().setUnitIncrement(16);
		matScpn.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		matScpn.getVerticalScrollBar().setUnitIncrement(16);
		matScpn.setPreferredSize(new Dimension(0, 0));
		
		FormLayout layout = new FormLayout("0:g, 1px, p",
				"9px, 9px, 0:g, p, 0:g, p, 0:g, p, 0:g, p, 0:g, 9px, 9px");
		layout.setRowGroups(new int[][] { {1, 2}, {12, 13} });
		
		// Create color bar
		JPanel colBarPnl = new JPanel(layout);
		
		JPanel colBar = new JPanel() {
			NumberFormat formatter = new DecimalFormat("0.000");
			@Override
			protected void paintComponent(Graphics g) {
				int width = getWidth(), height = getHeight();
				// paint gradient
				Color[] colors = ColorUtils.getRainbowGradient(height);
				for (int y = 0; y < height; y++) {
					g.setColor(colors[height - y - 1]);
					g.drawLine(0, y, width, y);
				}
				// paint tick marks
				g.setColor(Color.GRAY);
				int left = getBorder().getBorderInsets(this).left;
				int right = width - getBorder().getBorderInsets(this).right - 1;
				for (int i = 1; i < 5; i++) {
					g.drawLine(left, i * height / 5, left + 2, i * height / 5);
					g.drawLine(right, i * height / 5, right - 2, i * height / 5);
				}
			}
			@Override
			public String getToolTipText(MouseEvent event) {
				return formatter.format(1.0 - event.getY() / (double) getHeight());
			}
			@Override
			public Point getToolTipLocation(MouseEvent event) {
				Point point = event.getPoint();
				point.translate(-24, 24);
				return point;
			}
		};
		colBar.setBorder(matScpn.getBorder());
		colBar.setToolTipText("");
		
		// Add tick labels to color bar
		colBarPnl.add(colBar, CC.xywh(1, 2, 1, 11));
		colBarPnl.add(new JLabel("1.0"), CC.xywh(3, 1, 1, 2));
		for (int i = 4; i > 0; i--) {
			colBarPnl.add(new JLabel("" + (i/5.0)), CC.xy(3, 12-2*i));
		}
		colBarPnl.add(new JLabel("0.0"), CC.xywh(3, 12, 1, 2));
		
		// Create zoom widget
		final JPanel zoomPnl = new JPanel() {
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				if (zoomImg != null) {
					g.drawImage(zoomImg, 2, 2, 35, 35, null);
					Color col = new Color(((BufferedImage) zoomImg).getRGB(zoomX, zoomY));
					g.setColor(new Color(255 - col.getRed(), 255 - col.getGreen(), 255 - col.getBlue()));
					g.drawRect(2 + zoomX * 5, 2 + zoomY * 5, 4, 4);
				}
			}
		};
		zoomPnl.setBorder(matScpn.getBorder());
		
		// Create widget to display score of pixel below mouse cursor
		final JTextField infoTtf = new JTextField("0.000");
		infoTtf.setEditable(false);
		infoTtf.setHorizontalAlignment(SwingConstants.CENTER);
		infoTtf.setBorder(BorderFactory.createCompoundBorder(matScpn.getBorder(),
				BorderFactory.createEmptyBorder(0, 0, 0, -1)));
		
		// Install mouse motion listener controlling zoom-related widgets
		matLbl.addMouseMotionListener(new MouseMotionAdapter() {
			int zoomWidth = 7, zoomHeight = 7;
			NumberFormat formatter = new DecimalFormat("0.000");
			@Override
			public void mouseMoved(MouseEvent me) {
				if (matLbl.getIcon() != null) {
					BufferedImage image = (BufferedImage) ((ImageIcon) matLbl.getIcon()).getImage();
					// Get top left corner coordinates of sub-image
					int x = me.getX() - zoomWidth / 2;
					int y = me.getY() - zoomHeight / 2;
					// Account for borders
					if (x < 0) {
						x = 0;
						zoomX = me.getX() % zoomWidth;
					} else if (x + zoomWidth > image.getWidth()) {
						x = image.getWidth() - zoomWidth;
						zoomX = me.getX() + zoomWidth - image.getWidth();
					} else {
						zoomX = 3;
					}
					if (y < 0) {
						y = 0;
						zoomY = me.getY() % zoomHeight;
					} else if (y + zoomHeight > image.getHeight()) {
						y = image.getHeight() - zoomHeight;
						zoomY = me.getY() + zoomHeight - image.getHeight();
					} else {
						zoomY = 3;
					}
					// Get cropped part of matrix
					zoomImg = image.getSubimage(x, y, zoomWidth, zoomHeight);
					zoomPnl.repaint();
					// Get score value (stored in red color channel of pixel)
					double red = ((specSimResult.getScoreMatrixImage().getRGB(
							me.getX() + 1, me.getY() + 1) >> 16) & 0xFF) / 255.0;
					infoTtf.setText(formatter.format(red));
				}
			}
		});
		
		saveImgBtn = new JButton(IconConstants.SAVE_FILE_ICON);
		saveImgBtn.setRolloverIcon(IconConstants.SAVE_FILE_ROLLOVER_ICON);
		saveImgBtn.setPressedIcon(IconConstants.SAVE_FILE_PRESSED_ICON);
		saveImgBtn.setEnabled(false);
		
		saveImgBtn.addActionListener(new ActionListener() {
			/** Path of the last selected file. */
			private String lastSelected = System.getProperty("user.home");
			/** File descriptor of the file the image shall be written to. */
			private File outputfile;
			/** Progress listener for saving the score matrix image to a file */
			private IIOWriteProgressListener listener = new IIOWriteProgressListener() {
				public void writeAborted(ImageWriter source) { }
				public void thumbnailStarted(ImageWriter source, int imageIndex, int thumbnailIndex) { }
				public void thumbnailProgress(ImageWriter source, float percentageDone) { }
				public void thumbnailComplete(ImageWriter source) { }
				
				public void imageStarted(ImageWriter source, int imageIndex) { 
					Client.getInstance().firePropertyChange("new message", null, "SAVING SCORE MATRIX IMAGE");
					Client.getInstance().firePropertyChange("resetall", -1L, 100L);
					Client.getInstance().firePropertyChange("resetcur", -1L, 100L);
				}
				public void imageProgress(ImageWriter source, float percentageDone) {
					Client.getInstance().firePropertyChange("progress", -1L, (long) percentageDone);
				}
				public void imageComplete(ImageWriter source) {
					Client.getInstance().firePropertyChange("new message", null, "SAVING SCORE MATRIX IMAGE FINISHED");
					Client.getInstance().firePropertyChange("progress", -1L, 100L);
				}
			};
			
			/** Prompts to select a file to save the score matrix image to. */
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new ConfirmFileChooser(lastSelected);
				chooser.setFileFilter(Constants.PNG_FILE_FILTER);
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setMultiSelectionEnabled(false);
				chooser.setDialogTitle("Save Score Matrix Image");

				int returnVal = chooser.showSaveDialog(ClientFrame.getInstance());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					outputfile = chooser.getSelectedFile();
					if (!outputfile.getPath().toLowerCase().endsWith(".png")) {
						outputfile = new File(outputfile.getParentFile(), outputfile.getName() + ".png"); 
					}
					
					new SwingWorker<Object, Object>() {
						@Override
						protected Object doInBackground() throws Exception {
							try {
								setBusy(true);
								
								Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("png");
								ImageWriter writer = writers.next();
								ImageOutputStream ios = ImageIO.createImageOutputStream(outputfile);
								writer.setOutput(ios);
								writer.addIIOWriteProgressListener(listener);
								writer.write(specSimResult.getScoreMatrixImage());
								ios.flush();
								ios.close();
								
//								ImageIO.write(specSimResult.getScoreMatrixImage(), "png", outputfile);
							} catch (IOException ex) {
								Client.getInstance().firePropertyChange("new message", null, "SAVING SCORE MATRIX IMAGE FAILED");
								JXErrorPane.showDialog(ClientFrame.getInstance(),
										new ErrorInfo("Severe Error", ex.getMessage(), null, null, ex, ErrorLevel.SEVERE, null));
							} finally {
								setBusy(false);
							}
							return null;
						}
					}.execute();
					
					lastSelected = outputfile.getParent();
				}
			}
		});
		
		saveImgBtn.setEnabled(false);
		
		// Add components to matrix panel
		matPnl.add(matScpn, CC.xywh(2, 2, 1, 7));
		matPnl.add(zoomPnl, CC.xy(4, 2));
		matPnl.add(infoTtf, CC.xy(4, 4));
		matPnl.add(colBarPnl, CC.xy(4, 6));
		matPnl.add(saveImgBtn, CC.xy(4, 8));
		
	}

	/**
	 * Method to refresh protein table contents.
	 */
	protected void refreshProteinTable() {
		try {
			
		} finally {
			
		}
		specSimResult = Client.getInstance().getSpecSimResult(
				ClientFrame.getInstance().getProjectPanel().getCurrentExperimentContent());

		if (specSimResult != null && !specSimResult.isEmpty()) {
			TableConfig.clearTable(proteinTbl);

			int i = 1, maxPeptideCount = 0, maxSpecCount = 0;
			double maxCoverage = 0.0;
			for (Entry<String, ProteinHit> entry : specSimResult
					.getProteinHits().entrySet()) {
				ProteinHit proteinHit = entry.getValue();
				// if (i == 1) {
				// System.out.println(((SpectrumSpectrumMatch)
				// proteinHit.getPeptideHitList().get(0).getSingleSpectrumMatch()).getLibSpectrumID());
				// }
				maxCoverage = Math.max(maxCoverage, proteinHit.getCoverage());
				maxPeptideCount = Math.max(maxPeptideCount, proteinHit
						.getPeptideCount());
				maxSpecCount = Math.max(maxSpecCount, proteinHit
						.getSpectralCount());
				((DefaultTableModel) proteinTbl.getModel())
						.addRow(new Object[] { i++, proteinHit.getAccession(),
								proteinHit.getDescription(),
								proteinHit.getCoverage(),
								proteinHit.getMolecularWeight(),
								proteinHit.getPeptideCount(),
								proteinHit.getSpectralCount() });
			}

			if (proteinTbl.getRowCount() > 0) {
				FontMetrics fm = getFontMetrics(chartFont);
				TableColumnModel tcm = proteinTbl.getColumnModel();

				BarChartHighlighter highlighter;

				highlighter = (BarChartHighlighter) ((TableColumnExt) tcm
						.getColumn(proteinTbl
								.convertColumnIndexToView(PROT_COVERAGE)))
						.getHighlighters()[0];
				highlighter.setBaseline(1 + fm.stringWidth(highlighter
						.getFormatter().format(maxCoverage)));
				highlighter.setRange(0.0, maxCoverage);

				highlighter = (BarChartHighlighter) ((TableColumnExt) tcm
						.getColumn(proteinTbl
								.convertColumnIndexToView(PROT_PEPTIDECOUNT)))
						.getHighlighters()[0];
				highlighter.setBaseline(1 + fm.stringWidth(highlighter
						.getFormatter().format(maxPeptideCount)));
				highlighter.setRange(0.0, maxPeptideCount);

				highlighter = (BarChartHighlighter) ((TableColumnExt) tcm
						.getColumn(proteinTbl
								.convertColumnIndexToView(PROT_SPECTRALCOUNT)))
						.getHighlighters()[0];
				highlighter.setBaseline(1 + fm.stringWidth(highlighter
						.getFormatter().format(maxSpecCount)));
				highlighter.setRange(0.0, maxSpecCount);

				proteinTbl.getSelectionModel().setSelectionInterval(0, 0);

				BufferedImage scoreMatrix = new BufferedImage(matrixColorModel,
						specSimResult.getScoreMatrixImage().getRaster(), false,
						null);
				matLbl.setIcon(new ImageIcon(scoreMatrix
						.getSubimage(1, 1, scoreMatrix.getWidth() - 1,
								scoreMatrix.getHeight() - 1)));
//				File outputfile = new File("saved.png");
//				try {
//					ImageIO.write(specSimResult.getScoreMatrixImage(), "png", outputfile);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
			}
			
			if (specSimResult.getScoreMatrixImage() != null) {
				saveImgBtn.setEnabled(true);
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
			String accession = (String) proteinTbl.getValueAt(protRow, 1);
			ProteinHit proteinHit = specSimResult.getProteinHits().get(
					accession);
			List<PeptideHit> peptideHits = proteinHit.getPeptideHitList();

			int i = 1, maxSpecCount = 0;
			for (PeptideHit peptideHit : peptideHits) {
				int specCount = peptideHit.getSpectralCount();
				maxSpecCount = Math.max(maxSpecCount, specCount);
				((DefaultTableModel) peptideTbl.getModel())
						.addRow(new Object[] { i++, peptideHit.getSequence(),
								peptideHit.getSpectralCount() });
			}

			FontMetrics fm = getFontMetrics(chartFont);
			TableColumnModel tcm = peptideTbl.getColumnModel();

			BarChartHighlighter highlighter;

			highlighter = (BarChartHighlighter) ((TableColumnExt) tcm
					.getColumn(peptideTbl
							.convertColumnIndexToView(PEP_SPECTRALCOUNT)))
					.getHighlighters()[0];
			highlighter.setBaseline(fm.stringWidth(highlighter.getFormatter()
					.format(maxSpecCount)));
			highlighter.setRange(0.0, maxSpecCount);

			peptideTbl.getSelectionModel().setSelectionInterval(0, 0);
		}
	}

	/**
	 * Method to refresh SSM table contents.
	 */
	protected void refreshSsmTable() {
		TableConfig.clearTable(ssmTbl);

		int protRow = proteinTbl.getSelectedRow();
		if (protRow != -1) {
			String accession = (String) proteinTbl.getValueAt(protRow, 1);
			int pepRow = peptideTbl.getSelectedRow();
			if (pepRow != -1) {
				String sequence = (String) peptideTbl.getValueAt(pepRow, 1);
				PeptideHit peptideHit = specSimResult.getProteinHits().get(
						accession).getPeptideHits().get(sequence);

				List<SpectrumMatch> matches = peptideHit.getSpectrumMatches();
				try {
					Map<Long, String> titles =
						Client.getInstance().getSpectrumTitlesFromMatches(matches);

					int i = 1;
					for (SpectrumMatch sm : matches) {
						SpectrumSpectrumMatch ssm = (SpectrumSpectrumMatch) sm;
						((DefaultTableModel) ssmTbl.getModel())
								.addRow(new Object[] { i++,
										// ssm.getSearchSpectrumID() + " " +
										// ssm.getLibSpectrumID() + " " +
										// titles.get(ssm.getSearchSpectrumID()),
										titles.get(ssm.getSearchSpectrumID()),
										ssm.getSimilarity() });
					}

					ssmTbl.getSelectionModel().setSelectionInterval(0, 0);
				} catch (SQLException e) {
					JXErrorPane.showDialog(ClientFrame.getInstance(),
							new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
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
					String accession = (String) proteinTbl.getValueAt(
							protRow, proteinTbl.convertColumnIndexToView(PROT_ACCESSION));
					String sequence = (String) peptideTbl.getValueAt(pepRow,
							peptideTbl.convertColumnIndexToView(PEP_SEQUENCE));
					int index = ssmTbl.convertRowIndexToModel(ssmRow);
					SpectrumSpectrumMatch ssm =
						(SpectrumSpectrumMatch) specSimResult.getProteinHits().get(accession)
						.getPeptideHits().get(sequence).getSpectrumMatches().get(index);
					try {
						MascotGenericFile mgfQuery =
							Client.getInstance().getSpectrumBySearchSpectrumID(
									ssm.getSearchSpectrumID());
						MascotGenericFile mgfLib = 
							Client.getInstance().getSpectrumByLibSpectrumID(
									ssm.getLibSpectrumID());
						plotPnl.setFirstSpectrum(mgfQuery);
						plotPnl.setSecondSpectrum(mgfLib);
						plotPnl.repaint();
					} catch (SQLException e) {
						JXErrorPane.showDialog(ClientFrame.getInstance(),
								new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
					}
				}
			}
		}
	}

	/**
	 * Sets the enabled state of the get results button.
	 */
	public void setResultsButtonEnabled(boolean enabled) {
		getResultsBtn.setEnabled(enabled);
	}
	
	/**
	 * Holds the previous enable states of the client frame's tabs.
	 */
	private boolean[] tabEnabled;
	
	/**
	 * Makes the frame and this panel appear busy.
	 * @param busy <code>true</code> if busy, <code>false</code> otherwise.
	 */
	public void setBusy(boolean busy) {
		this.busy = busy;
		Cursor cursor = (busy) ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : null;
		ClientFrame clientFrame = ClientFrame.getInstance();
		clientFrame.setCursor(cursor);
		if (split.getCursor().getType() == Cursor.WAIT_CURSOR) split.setCursor(null);
		
		JTabbedPane pane = clientFrame.getTabPane();
		if (tabEnabled == null) {
			tabEnabled = new boolean[pane.getTabCount()];
			tabEnabled[pane.indexOfComponent(clientFrame.getResultsPanel())] = true;
		}
		// Enable/disable tabs
		for (int i = 0; i < tabEnabled.length - 1; i++) {
			boolean temp = pane.isEnabledAt(i);
			pane.setEnabledAt(i, tabEnabled[i]);
			tabEnabled[i] = temp;
		}
		// Enable/disable menu bar
		for (int i = 0; i < clientFrame.getJMenuBar().getMenuCount(); i++) {
			clientFrame.getJMenuBar().getMenu(i).setEnabled(!busy);
		}
		getResultsBtn.setEnabled(!busy);
		saveImgBtn.setEnabled(!busy);
	}
	
}
