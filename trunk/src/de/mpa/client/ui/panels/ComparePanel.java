package de.mpa.client.ui.panels;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.decorator.BorderHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.CompoundHighlighter;
import org.jdesktop.swingx.decorator.FontHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.HighlightPredicate.ColumnHighlightPredicate;
import org.jdesktop.swingx.decorator.HighlightPredicate.NotHighlightPredicate;
import org.jdesktop.swingx.sort.TableSortController;
import org.jdesktop.swingx.table.TableColumnExt;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.plastic.PlasticButtonUI;

import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.Hit;
import de.mpa.client.model.dbsearch.MetaProteinFactory;
import de.mpa.client.model.dbsearch.MetaProteinHit;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.settings.ParameterMap;
import de.mpa.client.settings.ResultParameters;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.DefaultTableHeaderCellRenderer;
import de.mpa.client.ui.PanelConfig;
import de.mpa.client.ui.RolloverButtonUI;
import de.mpa.client.ui.TableConfig;
import de.mpa.client.ui.chart.ChartType;
import de.mpa.client.ui.chart.HeatMapData;
import de.mpa.client.ui.chart.HeatMapData.MatrixSeriesExt;
import de.mpa.client.ui.chart.HeatMapPane.Axis;
import de.mpa.client.ui.chart.HierarchyLevel;
import de.mpa.client.ui.chart.OntologyChart.OntologyChartType;
import de.mpa.client.ui.chart.TaxonomyChart.TaxonomyChartType;
import de.mpa.client.ui.dialogs.AdvancedSettingsDialog;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.db.ProjectManager;
import de.mpa.db.accessor.Experiment;
import de.mpa.db.accessor.Project;

/**
 * This class holds the comparison of certain experiments.
 * @author A. Behne and R. Heyer
 */
public class ComparePanel extends JPanel {
	
//	/**
//	 * Dummy key for meta-result in result map.
//	 */
//	private static final Experiment META_EXPERIMENT = new Experiment();
//
//	/**
//	 * The cache of re-usable result objects.
//	 */
//	private Map<Experiment, DbSearchResult> resultMap;
	
	/**
	 * The list of experiments to compare.
	 */
	private List<Experiment> experiments;
	
	/**
	 * The meta-result object.
	 */
	private DbSearchResult metaResult;
	
	/**
	 * The local map of meta-protein generation-related parameters.
	 */
	private ParameterMap metaParams;

	/**
	 * The scroll pane containing the comparison table.
	 */
	private JScrollPane comparePane;

	/**
	 * Constructs an experiment compare panel.
	 */
	public ComparePanel() {
		super();
		
//		// init map of database search results
//		this.resultMap = new HashMap<Experiment, DbSearchResult>();
//		// initially add dummy entry pointing to non-existent meta-result
//		this.resultMap.put(META_EXPERIMENT, null);
		
		// init meta-result
		this.metaResult = null;
		
		// init local instance of result fetching parameters
		this.metaParams = new ResultParameters();
		
		initComponents();
	}

	/**
	 * Configures and lays out the panel's components.
	 */
	private void initComponents() {
		this.setLayout(new FormLayout("5dlu, 0px:g, 5dlu", "5dlu, f:p:g(0.3), 5dlu, f:p:g(0.7), 5dlu"));
		
		// create panel containing experiment/display settings/controls
		JPanel settingsPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu, m, 5dlu", "5dlu, f:p:g, 5dlu"));
		
		// create panel containing experiment selection list table
		JPanel experimentPnl = new JPanel();
		experimentPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));
		experimentPnl.setBorder(BorderFactory.createTitledBorder("Experiments"));
		
		final JXTable experimentTbl = new ListTable("Click to Add Experiment...");
		experimentTbl.setHorizontalScrollEnabled(true);
		
		experimentTbl.getSelectionModel().addListSelectionListener(
				new ExperimentTableHandler(experimentTbl));

		JScrollPane experimentScp = new JScrollPane(experimentTbl);
		experimentScp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		experimentScp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		experimentScp.setPreferredSize(new Dimension(0, 0));
		
		experimentPnl.add(experimentScp, CC.xy(2, 2));
		
		// create panel containing controls to manipulate experiment/display settings
		JPanel controlPnl = new JPanel(new FormLayout("5dlu, p, 5dlu, p:g, 5dlu",
				"2dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, f:45px:g, 5dlu"));
		controlPnl.setBorder(BorderFactory.createTitledBorder("Comparison Settings"));
		controlPnl.setPreferredSize(new Dimension(200, 50));

		// create radio buttons for various display attributes
		ButtonGroup bg = new ButtonGroup();
		JRadioButton ontoRbtn = new JRadioButton("Ontology", false);
		bg.add(ontoRbtn);
		final JComboBox<OntologyChartType> ontoCbx = new JComboBox<OntologyChartType>(OntologyChartType.values());
		ontoCbx.setEnabled(false);
		JRadioButton taxoRbtn = new JRadioButton("Taxonomy", false);
		bg.add(taxoRbtn);
		final JComboBox<TaxonomyChartType> taxoCbx = new JComboBox<TaxonomyChartType>(TaxonomyChartType.values());
		taxoCbx.setEnabled(false);
		JRadioButton hieroRbtn = new JRadioButton("Hierarchy", true);
		bg.add(hieroRbtn);
		final JComboBox<HierarchyLevel> hieroCbx = new JComboBox<HierarchyLevel>(HierarchyLevel.values());
		
		final JComboBox<HierarchyLevel> countCbx = new JComboBox<HierarchyLevel>(HierarchyLevel.values());
		
		// listen for radio button selection changes and enable/disable combo boxes accordingly
		ontoRbtn.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent evt) {
				ontoCbx.setEnabled(((AbstractButton) evt.getSource()).isSelected());
			}
		});
		taxoRbtn.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent evt) {
				taxoCbx.setEnabled(((AbstractButton) evt.getSource()).isSelected());
			}
		});
		hieroRbtn.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent evt) {
				hieroCbx.setEnabled(((AbstractButton) evt.getSource()).isSelected());
			}
		});

		// create button-in-button component for advanced result fetching settings
		final JButton settingsBtn = new JButton(IconConstants.SETTINGS_SMALL_ICON);
		settingsBtn.setEnabled(!Client.getInstance().isViewer()); 
		settingsBtn.setRolloverIcon(IconConstants.SETTINGS_SMALL_ROLLOVER_ICON);
		settingsBtn.setPressedIcon(IconConstants.SETTINGS_SMALL_PRESSED_ICON);		
		settingsBtn.setPreferredSize(new Dimension(23, 23));
		settingsBtn.setToolTipText("Advanced Settings");
		settingsBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				if (AdvancedSettingsDialog.showDialog(
						ClientFrame.getInstance(),
						"Result Fetching settings",
						true, metaParams) == AdvancedSettingsDialog.DIALOG_CHANGED_ACCEPTED) {
					// invalidate meta-result if parameters were changed
//					resultMap.put(META_EXPERIMENT, null);
					metaResult = null;
				}
			}
		});

		JButton compareBtn = new JButton("Compare", IconConstants.COMPARE_ICON) {
			@Override
			public void setEnabled(boolean b) {
				super.setEnabled(b);
				settingsBtn.setEnabled(b);
			}
		};
		compareBtn.setEnabled(!Client.getInstance().isViewer()); 
		compareBtn.setRolloverIcon(IconConstants.createRescaledIcon(IconConstants.COMPARE_ICON, 1.1f));
		compareBtn.setPressedIcon(IconConstants.createRescaledIcon(IconConstants.COMPARE_ICON, 0.8f));		
		compareBtn.setIconTextGap(7);
		compareBtn.setUI(new PlasticButtonUI() {
			@Override
			protected void paintFocus(Graphics g, AbstractButton b,
					Rectangle viewRect, Rectangle textRect, Rectangle iconRect) {
				int topLeftInset = 3;
		        int width = b.getWidth() - 1 - topLeftInset * 2;
		        int height = b.getHeight() - 1 - topLeftInset * 2;
				
				g.setColor(this.getFocusColor());
				g.drawLine(2, 2, 2, 2 + height + 1);
				g.drawLine(2, 2 + height + 1, 2 + width + 1, 2 + height + 1);
				g.drawLine(2 + width + 1, 2 + height + 1, 2 + width + 1, 24);
				g.drawLine(2 + width + 1, 24, 2 + width - 20, 24);
				g.drawLine(2 + width - 20, 24, 2 + width - 20, 2);
				g.drawLine(2 + width -20, 2, 2, 2);
			}
		});
		
		compareBtn.setLayout(new FormLayout("0px:g, p", "p, 0px:g"));
		compareBtn.add(settingsBtn, CC.xy(2, 1));
		compareBtn.setMargin(new Insets(-2, -3, -3, -3));

		// install action on compare button to set up comparison background task
		compareBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				// fetch experiments from list table
				TableModel model = experimentTbl.getModel();
				List<Experiment> experiments = new ArrayList<Experiment>();
				for (int i = 0; i < model.getRowCount() - 1; i++) {
					experiments.add((Experiment) model.getValueAt(i, 0));
				}
				// fetch comparison attributes
				ChartType yAxisType;
				if (ontoCbx.isEnabled()) {
					yAxisType = (ChartType) ontoCbx.getSelectedItem();
				} else if (taxoCbx.isEnabled()) {
					yAxisType = (ChartType) taxoCbx.getSelectedItem();
				} else {
					yAxisType = (ChartType) hieroCbx.getSelectedItem();
				}
				HierarchyLevel zAxisType = (HierarchyLevel) countCbx.getSelectedItem();
				
				// execute comparison task
				
				boolean recreateMetaResult =
						(metaResult == null) ||
						(experiments.size() != ComparePanel.this.experiments.size()) ||
						(!ComparePanel.this.experiments.containsAll(experiments));
				ComparePanel.this.experiments = experiments;
				
				new CompareTask(yAxisType, zAxisType, recreateMetaResult).execute();
			}
		});

		controlPnl.add(ontoRbtn, CC.xy(2, 2));
		controlPnl.add(ontoCbx, CC.xy(4, 2));
		controlPnl.add(taxoRbtn, CC.xy(2, 4));
		controlPnl.add(taxoCbx, CC.xy(4, 4));
		controlPnl.add(hieroRbtn, CC.xy(2, 6));
		controlPnl.add(hieroCbx, CC.xy(4, 6));
		controlPnl.add(new JLabel("Count", SwingConstants.CENTER), CC.xy(2, 8));
		controlPnl.add(countCbx, CC.xy(4, 8));
		controlPnl.add(compareBtn, CC.xyw(2, 10, 3));
		
		settingsPnl.add(experimentPnl, CC.xy(2, 2));
		settingsPnl.add(controlPnl, CC.xy(4, 2));
		
		JXTitledPanel settingsTtlPnl = PanelConfig.createTitledPanel("Settings", settingsPnl);
		
		// Create panel containing the comparison results table
		JPanel compareTblPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));
		
		final JXTable compareTbl = new JXTable(new DefaultTableModel(new String[] { "header", "empty" }, 0)) {
			@Override
			public Dimension getPreferredSize() {
				Dimension size = super.getPreferredSize();
				// remove 1px from bottom
				size.height--;
				return size;
			}
		};
		compareTbl.setHorizontalScrollEnabled(true);
		compareTbl.addHighlighter(TableConfig.getSimpleStriping());
		
		final JXTable headerTbl = new JXTable(compareTbl.getModel()) {
			@Override
			public Dimension getPreferredSize() {
				Dimension size = super.getPreferredSize();
				// remove 1px from bottom
				size.height--;
				return size;
			}
		};
		JTableHeader header = headerTbl.getTableHeader();
		header.setReorderingAllowed(false);
		
		headerTbl.setIntercellSpacing(new Dimension());
		headerTbl.addHighlighter(new CompoundHighlighter(
				new NotHighlightPredicate(new ColumnHighlightPredicate(0)),
				TableConfig.getSimpleStriping(), new BorderHighlighter(
						BorderFactory.createMatteBorder(0, 0, 1, 1, UIManager.getColor("Table.gridColor")))));
		
		// adjust row header view size on column resize
		headerTbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		headerTbl.getColumnModel().addColumnModelListener(new TableColumnModelListener() {
			@Override
			public void columnMarginChanged(ChangeEvent evt) {
				JViewport rowHeader = comparePane.getRowHeader();
				Dimension prefSize = rowHeader.getPreferredSize();
				prefSize.width = headerTbl.getPreferredSize().width;
				rowHeader.setPreferredSize(prefSize);
			}
			
			public void columnSelectionChanged(ListSelectionEvent evt) { }
			public void columnRemoved(TableColumnModelEvent evt) { }
			public void columnMoved(TableColumnModelEvent evt) { }
			public void columnAdded(TableColumnModelEvent evt) { }
		});
		
		comparePane = new JScrollPane(compareTbl);
		comparePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		comparePane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		comparePane.setPreferredSize(new Dimension());
		
		comparePane.setRowHeaderView(headerTbl);
		comparePane.setCorner(JScrollPane.UPPER_LEFT_CORNER, header);
		
		JPanel dummyPnl = new JPanel();
		dummyPnl.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIManager.getColor("controlDkShadow")));
		comparePane.setCorner(JScrollPane.LOWER_LEFT_CORNER, dummyPnl);
		
		compareTblPnl.add(comparePane, CC.xy(2, 2));
		
		// Create button panel
		JPanel buttonPnl = new JPanel(new FormLayout("22px, 2px, 22px, 1px", "f:20px"));
		buttonPnl.setOpaque(false);
		
		final JToggleButton numberTgl = new JToggleButton(IconConstants.TEXTFIELD_ICON);
		numberTgl.setRolloverIcon(IconConstants.TEXTFIELD_ROLLOVER_ICON);
		numberTgl.setPressedIcon(IconConstants.TEXTFIELD_PRESSED_ICON);
		numberTgl.setUI((RolloverButtonUI) RolloverButtonUI.createUI(numberTgl));
		numberTgl.setToolTipText("Toggle displaying values as numbers or text");
		numberTgl.setSelected(true);
		
		numberTgl.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				compareTbl.repaint();
			}
		});
		
		JButton exportBtn = new JButton(IconConstants.EXCEL_EXPORT_ICON);
		exportBtn.setRolloverIcon(IconConstants.EXCEL_EXPORT_ROLLOVER_ICON);
		exportBtn.setPressedIcon(IconConstants.EXCEL_EXPORT_PRESSED_ICON);
		exportBtn.setUI((RolloverButtonUI) RolloverButtonUI.createUI(exportBtn));
		exportBtn.setToolTipText("Export Table to CSV");
		
		exportBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				this.export2Csv();
			}

			/**
			 * Method to export compare table as CSV format, but tab separated
			 */
			@SuppressWarnings("unchecked")
			private void export2Csv() {	
				/**
				 * TSV format separator.
				 */
				final String SEP = "\t";
		
				// Add Filechooser
				JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(Constants.CSV_FILE_FILTER);
				int returnVal = chooser.showSaveDialog(ClientFrame.getInstance());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File selectedFile = chooser.getSelectedFile();
					if(!selectedFile.getName().endsWith(".csv")){
						selectedFile = new File(selectedFile.getPath()+ ".csv");
					}
		
					BufferedWriter bw = null;
					try {
						bw = new BufferedWriter(new FileWriter(selectedFile));
						
						// Get model
						TableModel model = ((JTable) comparePane.getViewport().getView()).getModel();
						
						// Add header line
						for (int col = 0; col < model.getColumnCount(); col++) {
							if (col > 0) {
								bw.append(SEP);
							}
							bw.append(model.getColumnName(col));
						}
						bw.newLine();
						
						// Add cells to table
						boolean exportNumbers = numberTgl.isSelected();
						for (int row = 0; row < model.getRowCount(); row++) {
							// add first column element (a.k.a. row header)
							Object rowHeaderVal = model.getValueAt(row, 0);
							bw.append(rowHeaderVal.toString());
							for (int col = 1; col < model.getColumnCount(); col++) {
								bw.append(SEP);
								// get value from model
								Object value = model.getValueAt(row, col);
								if (value != null) {
									if (value instanceof String) {
										bw.append(value.toString());
									} else {
										List<Hit> hitList = (List<Hit>) value;
										if (exportNumbers) {
											bw.append("" + hitList.size());
										} else {
											bw.append(hitList.toString());
										}
									}
								} else {
									if (exportNumbers) {
										bw.append("" + 0);
									}
								}
							}
							bw.newLine();
						}
						bw.flush();
						bw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});

		buttonPnl.add(numberTgl, CC.xy(1, 1));
		buttonPnl.add(exportBtn, CC.xy(3, 1));
		
		// install custom renderer on compare table
		compareTbl.setDefaultRenderer(List.class, new DefaultTableCellRenderer() {
			@SuppressWarnings("unchecked")
			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				List<Hit> hitList = (List<Hit>) value;
				boolean showNumbers = numberTgl.isSelected();
				if (hitList == null) {
					value = (showNumbers) ? 0 : null;
				} else {
					value = (showNumbers) ? hitList.size() : hitList.toString();
				}
				JLabel rendererLbl = (JLabel) super.getTableCellRendererComponent(
						table, value, isSelected, hasFocus, row, column);
				rendererLbl.setHorizontalAlignment(SwingConstants.CENTER);
				return rendererLbl;
			}
		});
		
		JXTitledPanel compareTblTtlPnl = PanelConfig.createTitledPanel(
				"Comparison Table", compareTblPnl, null, buttonPnl);
		
		this.add(settingsTtlPnl, CC.xy(2, 2));
		this.add(compareTblTtlPnl, CC.xy(2, 4));
	}
	
	/**
	 * Class to handle the choosen experiments and the experiment table.
	 * @author A. Behne und R. Heyer
	 */
	private class ExperimentTableHandler implements ListSelectionListener {

		/**
		 * The table of choosen experiments
		 */
		private JTable table;
		
		/**
		 * Creates a selection handler for the specified table.
		 * @param table. The experiment table.
		 */
		public ExperimentTableHandler(JTable table) {
			this.table = table;
		}
		
		@Override
		public void valueChanged(ListSelectionEvent evt) {
			if (!evt.getValueIsAdjusting()) {
				int row = this.table.getSelectedRow();
				if (row == (this.table.getRowCount() - 1)) {
					try {
						// create dialog for experiment selection from the database
						List<Experiment> experiments = this.showExperimentSelectionDialog();
						this.table.clearSelection();
						if (!experiments.isEmpty()) {
							for (Experiment experiment : experiments) {
								TableModel model = this.table.getModel();
								// check whether experiment already exists
								for (int i = 0; i < model.getRowCount() - 1; i++) {
									if (experiment.equals(model.getValueAt(i, 0))) {
										this.table.getSelectionModel().setSelectionInterval(i, i);
										return;
									}
								}
								int lastRow = this.table.getRowCount() - 1;
								((DefaultTableModel) model).insertRow(
										lastRow, new Object[] { experiment });
								this.table.getSelectionModel().setSelectionInterval(lastRow, lastRow);
							}
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}

		/**
		 * Creates and shows a dialog for selecting an experiment from the database.
		 * @return the selected experiment or <code>null</code>
		 * @throws SQLException if a database error occurs
		 */
		private List<Experiment> showExperimentSelectionDialog() throws SQLException {
			JPanel dialogPnl = new JPanel();
			dialogPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p, 5dlu" , "5dlu, p, 5dlu"));
		
			final JTable projTbl = new JTable(new DefaultTableModel(
					new Object[] { "Projects" }, 0)) {
				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
			projTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
			// Initialize ProjectManager to access projects and experiments in the database
			final ProjectManager projectManager = new ProjectManager(Client.getInstance().getDatabaseConnection());
			// Get projects from database.
			final List<Project> projects = projectManager.getProjects();
		
			List<String> titles = new ArrayList<String>();
			for (Project project : projects) {
				titles.add(project.getTitle());
			}
			this.fillTable(projTbl, titles);
		
			final JTable expTbl = new JTable(new DefaultTableModel(new Object[] {"Experiments"}, 0)){
				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
//			expTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
			final List<Experiment> experiments = new ArrayList<Experiment>();
		
			projTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					int selRow = projTbl.getSelectedRow();
					long projectID = projects.get(selRow).getProjectid();
					try {
						experiments.clear();
						experiments.addAll(projectManager.getProjectExperiments(projectID));
						if (!experiments.isEmpty()) {
							List<String> titles = new ArrayList<String>();
							for (Experiment experiment : experiments) {
								titles.add(experiment.getTitle());
							}
							ExperimentTableHandler.this.fillTable(expTbl, titles);
						}
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			});
		
			JScrollPane projScp = new JScrollPane(projTbl);
			projScp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			projScp.setPreferredSize(new Dimension(200, 250));
		
			JScrollPane expScp = new JScrollPane(expTbl);
			expScp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			expScp.setPreferredSize(new Dimension(200, 250));
		
			dialogPnl.add(projScp, CC.xy(2, 2));
			dialogPnl.add(expScp, CC.xy(4, 2));
		
			// Get selected experiments
			List<Experiment> res = new ArrayList<Experiment>();
			int ret = JOptionPane.showConfirmDialog(
					ClientFrame.getInstance(), dialogPnl, "Choose an Experiment", JOptionPane.OK_CANCEL_OPTION);
			if (ret == JOptionPane.OK_OPTION) {
				int[] rows = expTbl.getSelectedRows();
				for (int selRow : rows) {
					int row = expTbl.convertRowIndexToModel(selRow);
					if (row != -1) {
						// Fetch experiment from database
						try {
							long experimentID = experiments.get(row).getExperimentid();
							Experiment experiment = projectManager.getExperiment(experimentID);
							res.add(experiment);
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
			return res;
		}

		/**
		 * Convenience method to fill a list table with rows containing the provided data.
		 * @param table the table to fill
		 * @param data the list of data to be distributed among rows
		 */
		private void fillTable(JTable table, List<String> data) {
			TableConfig.clearTable(table);
			for (String str : data) {
				((DefaultTableModel) table.getModel()).addRow(new Object [] { str });
			}
		}
		
	}

	/**
	 * Worker to create compare data in a background threat
	 * @author R. Heyer
	 */
	private class CompareTask extends SwingWorker {
		
		/**
		 * Flag indicating whether the meta-result needs to be re-created.
		 */
		private boolean recreateMetaResult;
		
		/**
		 * The comparison attribute type.
		 */
		private ChartType yAxisType;
		
		/**
		 * The comparison count type.
		 */
		private HierarchyLevel zAxisType;

		/**
		 * Constructs a compare worker from the specified list of experiments to
		 * compare.
		 * @param experiments the experiments to compare
		 */
		public CompareTask(ChartType yAxisType, HierarchyLevel zAxisType, boolean recreateMetaResult) {
			this.yAxisType = yAxisType;
			this.zAxisType = zAxisType;
			this.recreateMetaResult = recreateMetaResult;
		}

		@Override
		protected Object doInBackground() throws Exception {
			try {
				Client client = Client.getInstance();
				
				if (this.recreateMetaResult) {
					// fetch results
					List<DbSearchResult> results = new ArrayList<DbSearchResult>();
					for (Experiment experiment : experiments) {
						DbSearchResult result =
								client.retrieveDatabaseSearchResult(null, null, experiment.getExperimentid());
						results.add(result);
					}
					
					// Create metaResult object, containing all result objects
					metaResult = new DbSearchResult(null, null, null);
					for (DbSearchResult result : results) {
						if (result != null) {
							for (ProteinHit proteinHit : result.getProteinHitList()) {
								metaResult.addProtein(proteinHit);
							}
						}
					}
					// Fuse to meta-proteins
					MetaProteinFactory.determineTaxonomyAndCreateMetaProteins(metaResult, metaParams);
				}
				
				// Create compare table
				final CompareData data = new CompareData(metaResult, this.yAxisType, this.zAxisType);

				// Refresh compare table
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						refreshCompareTable(data, experiments);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
		protected void done() {
//			compareTbl.getTableHeader().repaint();
		}

		/**
		 * Method to refresh the compare table.
		 * @param data. The data matrix for the comparison 
		 */
		private void refreshCompareTable(CompareData data, List<Experiment> experiments) {
			// Re-associate experiment titles with their IDs, use as column headers
			String[] xLabels = data.getXLabels();
			// Use attribute type as first column header
			ChartType axisType = data.getAxisType(Axis.Y_AXIS);
			final boolean isProtein = ((axisType == HierarchyLevel.META_PROTEIN_LEVEL) || 
					(axisType == HierarchyLevel.PROTEIN_LEVEL));
			String[] columnNames;
			final int offset = (isProtein) ? 2 : 1;
			columnNames = new String[xLabels.length + offset];
			columnNames[0] = axisType.toString();
			columnNames[1] = "Description";		// will be replaced if offset == 1
			
			for (int i = offset; i < columnNames.length; i++) {
				long l = Long.parseLong(xLabels[i - offset]);
				for (Experiment experiment : experiments) {
					if (l == experiment.getExperimentid()) {
						columnNames[i] = experiment.getTitle() + " (" + l + ")";
						break;
					}
				}
			}
			
			final String[] yLabels = data.getYLabels();
			final MatrixSeriesExt series = (MatrixSeriesExt) data.getMatrix();
			final DbSearchResult metaResult = data.getResult();
			DefaultTableModel model = new DefaultTableModel(columnNames, yLabels.length) {
				@Override
				public Class<?> getColumnClass(int columnIndex) {
					if (columnIndex >= offset) {
						return List.class;
					}
					return super.getColumnClass(columnIndex);
				}
				
				@Override
				public Object getValueAt(int row, int column) {
					if (column == 0) {
						return yLabels[row];
					} else if ((column == 1) && isProtein) {
						ProteinHit ph = metaResult.getProteinHit(yLabels[row]);
						if (ph != null) {
							return ph.getDescription();
						} else {
							for (ProteinHit mph : metaResult.getMetaProteins()) {
								if (mph.getAccession() == yLabels[row]) {
									return ((MetaProteinHit) mph).getProteinHitList().get(0).getDescription();
								}
							}
							// we shouldn't get here
							return null;
						}
					} else {
						column -= offset;
						List<List<List<Hit>>> matrix = series.getMatrix();
						List<List<Hit>> matrixRow = matrix.get(row);
						if (column < matrixRow.size()) {
							return matrixRow.get(column);
						} else {
							return null;
						}
					}
				}
			};
			
			final JXTable headerTbl = (JXTable) comparePane.getRowHeader().getView();
			headerTbl.setModel(model);
			List<TableColumn> headColumns = headerTbl.getColumns();
			for (int i = 1; i < headColumns.size(); i++) {
				((TableColumnExt) headColumns.get(i)).setVisible(false);
			}
			headerTbl.getColumn(0).setCellRenderer(new DefaultTableHeaderCellRenderer() {
				@Override
				public Icon getIcon() {
					return null;
				}
			});
			
			final JXTable compareTbl = (JXTable) comparePane.getViewport().getView();
			compareTbl.setModel(model);
			List<TableColumn> compColumns = compareTbl.getColumns();
			for (int i = 0; i < 1; i++) {
				((TableColumnExt) compColumns.get(i)).setVisible(false);
			}
			
			// TODO: synchronize sorting
//			headerTbl.setRowSorter(compareTbl.getRowSorter());
			
			TableSortController<TableModel> sorter = new TableSortController<TableModel>(model) {
				RowSorter<? extends TableModel> delegate = compareTbl.getRowSorter();
				
				@Override
				public int convertRowIndexToModel(int viewIndex) {
					return delegate.convertRowIndexToModel(viewIndex);
				}
				
				@Override
				public int convertRowIndexToView(int modelIndex) {
					return delegate.convertRowIndexToView(modelIndex);
				}
			};
			headerTbl.setRowSorter(sorter);
			
			headerTbl.packColumn(0, 6);
			compareTbl.packAll();
		}
		
	}
	
	/**
	 * Class to compare experiments.
	 * @author A. Behne und R. Heyer
	 */
	public static class CompareData extends HeatMapData {
		
		/**
		 * ChartType for experiments
		 */
		public static final ChartType EXPERIMENT = new ChartType() {
			@Override
			public String getTitle() {
				return "Experiment";
			}
		};

		/**
		 * Default constructor for the compare data.
		 * @param result. The result object.
		 * @param yAxisType. The type of the y-axis for the comparison.
		 * @param zAxisType. The type of the z-axis for the comparison.
		 */
		public CompareData(DbSearchResult result,
				ChartType yAxisType, HierarchyLevel zAxisType) {
			super(result, EXPERIMENT, yAxisType, zAxisType);
		}
		
	}

	/**
	 * Class to create an editable table, which allows adding and deleting of elements.
	 * @author A. Behne
	 */
	private class ListTable extends JXTable {

		/**
		 * Constructor for ListTable, requiring a string for the add/ change element.
		 * @param editorString
		 */
		public ListTable(String editorString) {
			this(new DefaultTableModel(0, 2), editorString);
		}

		/**
		 * Constructor for ListTable, requiring a string for the add/ change element and a table model.
		 * @param model
		 * @param editorString
		 */
		private ListTable(final DefaultTableModel model, final String editorString) {
			super(model);
			model.addRow(new Object[] { editorString });

			// Install highlighter to display last row's text in italics
			Font italicFont = new JLabel().getFont().deriveFont(Font.ITALIC);
			this.addHighlighter(new FontHighlighter(new HighlightPredicate() {
				public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
					return (adapter.row == (adapter.getRowCount() - 1));
				}
			}, italicFont));
			// Install renderer to first column to remove focus border
			this.getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
				@Override
				public Component getTableCellRendererComponent(JTable table,
						Object value, boolean isSelected, boolean hasFocus,
						int row, int column) {
					return super.getTableCellRendererComponent(table, value, isSelected, false,
							row, column);
				}
			});
			// Install renderer to second column to display cross icon
			this.getColumn(1).setCellRenderer(new TableCellRenderer() {
				@Override
				public Component getTableCellRendererComponent(JTable table, Object value,
						boolean isSelected, boolean hasFocus, int row, int column) {
					JLabel label = new JLabel();
					if (row < (table.getRowCount() - 1)) {
						label.setIcon(IconConstants.CROSS_ICON);
					}
					label.setOpaque(true);
					label.setBackground((isSelected) ?
							table.getSelectionBackground() :
								table.getBackground());
					return label;
				}
			});

			// Hide vertical grid lines
			this.setShowGrid(true, false);
			// Allow only single selection
			this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			// Fix second column width and disallow reordering/resizing of columns
			this.getColumn(1).setMaxWidth(18);
			this.getTableHeader().setReorderingAllowed(false);
			this.getTableHeader().setResizingAllowed(false);

			// Create text field editor for first column
			JTextField editorTtf = new JTextField();
			editorTtf.setBackground(this.getSelectionBackground());
			editorTtf.setSelectionColor(this.getBackground());
			editorTtf.setBorder(null);
			editorTtf.addFocusListener(new FocusAdapter() {
				public void focusGained(FocusEvent e) {
					JTextField src = (JTextField) e.getSource();
					src.selectAll();
				}
			});
			DefaultCellEditor textEditor = new DefaultCellEditor(editorTtf) {
				@Override
				public boolean stopCellEditing() {
					// Check whether editor contents were changed
					String val = (String) delegate.getCellEditorValue();
					if (!val.equals(editorString)) {
						// Changed row becomes non-editable, re-add editor row
						boolean res = super.stopCellEditing();
						model.addRow(new Object[] { editorString });
						return res;
					}
					cancelCellEditing();
					return false;
				}
			};
			this.getColumn(0).setCellEditor(textEditor);

			// Create editor for second column, repurpose checkbox editor (checkboxes are basically buttons)
			JCheckBox editorChk = new JCheckBox();
			editorChk.setIcon(IconConstants.CROSS_ICON);
			editorChk.setPressedIcon(IconConstants.CROSS_PRESSED_ICON);
			this.getColumn(1).setCellEditor(new DefaultCellEditor(editorChk) {
				@Override
				public Component getTableCellEditorComponent(JTable table,
						Object value, boolean isSelected, int row, int column) {
					// Make checkbox background always appear as if on selected row
					return super.getTableCellEditorComponent(table, value, true, row, column);
				}
				@Override
				public boolean stopCellEditing() {
					// Check whether editor value got changed (from 'false' to 'true')
					boolean res = super.stopCellEditing();
					if ((Boolean) getCellEditorValue()) {
						// Remove selected table row
						model.removeRow(getSelectedRow());
					}
					return res;
				}
			});

			// Remove table header to appear like a list
			this.setTableHeader(null);
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			// only allow editing of last row's first column ('new group' editor)
			// or every but last row's second column ('remove row' buttons)
			return ((column == 0) && (row == (getRowCount() - 1))) ||
					((column == 1) && (row < (getRowCount() - 1)));
		}
	}

}
