
package de.mpa.client.ui.resultspanel;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
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
import javax.swing.SwingConstants;
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
import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.decorator.BorderHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.CompoundHighlighter;
import org.jdesktop.swingx.decorator.FontHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.looks.plastic.PlasticButtonUI;

import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.settings.ResultParameters;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.inputpanel.dialogs.AdvancedSettingsDialog;
import de.mpa.client.ui.sharedelements.PanelConfig;
import de.mpa.client.ui.sharedelements.RolloverButtonUI;
import de.mpa.client.ui.sharedelements.chart.ChartType;
import de.mpa.client.ui.sharedelements.chart.HeatMapData;
import de.mpa.client.ui.sharedelements.chart.HierarchyLevel;
import de.mpa.client.ui.sharedelements.chart.OntologyChart;
import de.mpa.client.ui.sharedelements.chart.TaxonomyChart;
import de.mpa.client.ui.sharedelements.icons.IconConstants;
import de.mpa.client.ui.sharedelements.tables.TableConfig;
import de.mpa.db.mysql.ProjectManager;
import de.mpa.db.neo4j.insert.GraphDatabaseHandler;
import de.mpa.model.MPAExperiment;
import de.mpa.model.MPAProject;
import de.mpa.model.analysis.MetaProteinFactory;
import de.mpa.model.compare.CompareExperiments;
import de.mpa.model.dbsearch.DbSearchResult;
import de.mpa.model.dbsearch.Hit;
import de.mpa.model.dbsearch.MetaProteinHit;

/**
 * This class holds the comparison of certain experiments.
 * @author A. Behne, R. Heyer and T. Muth
 */
public class ComparePanel extends JPanel {

	/**
	 * Default serialization ID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The list of experiments to compare.
	 */
	private ArrayList<MPAExperiment> experiments;

	/**
	 * The local map of meta-protein generation-related parameters.
	 */
	private final ResultParameters metaParams;

	/**
	 * The scroll pane containing the comparison table.
	 */
	private JScrollPane comparePane;

	/**
	 * Compare button instance.
	 */
	private JButton compareBtn;

	/**
	 * Constructs an experiment compare panel.
	 */
	public ComparePanel() {
		// Initialize local instance of result fetching parameters.
		metaParams = new ResultParameters();
		this.initComponents();
	}

	/**
	 * Configures and lays out the panel's components.
	 */
	@SuppressWarnings("serial")
	private void initComponents() {
		setLayout(new FormLayout("5dlu, 0px:g, 5dlu", "5dlu, f:p:g(0.3), 5dlu, f:p:g(0.7), 5dlu"));

		// create panel containing experiment/display settings/controls
		JPanel settingsPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu, m, 5dlu", "5dlu, f:p:g, 5dlu"));

		// create panel containing experiment selection list table
		JPanel experimentPnl = new JPanel();
		experimentPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));
		experimentPnl.setBorder(BorderFactory.createTitledBorder("Experiments"));

		JXTable experimentTbl = new ListTable("Click here to add an experiment...");
		experimentTbl.setHorizontalScrollEnabled(true);

		class MyTableCellRenderer extends JLabel implements TableCellRenderer {

			public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) {

				if (value instanceof MPAExperiment) {
					MPAExperiment exp = (MPAExperiment) value;
					this.setText(exp.getTitle());
					this.setFont(new Font("Dialog", Font.PLAIN, 12));
				}else {
					this.setText((String) value);
				}
				return this;
			}

		}

		experimentTbl.getColumnModel().getColumn(0).setCellRenderer(new MyTableCellRenderer());

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
		List<OntologyChart.OntologyChartType> ontoList = Arrays.asList(OntologyChart.OntologyChartType.values()).subList(0, 3);
		OntologyChart.OntologyChartType[] ontoArray = ontoList.toArray(new OntologyChart.OntologyChartType[ontoList.size()]);

		final JComboBox<OntologyChart.OntologyChartType> ontoCbx = new JComboBox<>(ontoArray);
		ontoCbx.setEnabled(false);
		JRadioButton taxoRbtn = new JRadioButton("Taxonomy", false);
		bg.add(taxoRbtn);
		JComboBox<TaxonomyChart.TaxonomyChartType> taxoCbx = new JComboBox<>(TaxonomyChart.TaxonomyChartType.values());
		taxoCbx.setEnabled(false);
		JRadioButton hieroRbtn = new JRadioButton("Hierarchy", true);
		bg.add(hieroRbtn);
		JComboBox<HierarchyLevel> hieroCbx = new JComboBox<>(HierarchyLevel.values());

		List<HierarchyLevel> countList = Arrays.asList(HierarchyLevel.values()).subList(2, 4);
		HierarchyLevel[] countArray = countList.toArray(new HierarchyLevel[countList.size()]);
		JComboBox<HierarchyLevel> countCbx = new JComboBox<>(countArray);
		countCbx.setSelectedIndex(1);

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
		JButton settingsBtn = new JButton(IconConstants.SETTINGS_SMALL_ICON);
		settingsBtn.setEnabled(true); 
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
						true, ComparePanel.this.metaParams) == AdvancedSettingsDialog.DIALOG_CHANGED_ACCEPTED) {
				}
			}
		});

		this.compareBtn = new JButton("Compare", IconConstants.COMPARE_ICON) {
			@Override
			public void setEnabled(boolean b) {
				super.setEnabled(b);
				settingsBtn.setEnabled(b);
			}
		};
		this.compareBtn.setEnabled(false);
		this.compareBtn.setRolloverIcon(IconConstants.createColorRescaledIcon(IconConstants.COMPARE_ICON, 1.1f));
		this.compareBtn.setPressedIcon(IconConstants.createColorRescaledIcon(IconConstants.COMPARE_ICON, 0.8f));
		this.compareBtn.setIconTextGap(7);
		this.compareBtn.setUI(new PlasticButtonUI() {
			@Override
			protected void paintFocus(Graphics g, AbstractButton b,
					Rectangle viewRect, Rectangle textRect, Rectangle iconRect) {
				int topLeftInset = 3;
				int width = b.getWidth() - 1 - topLeftInset * 2;
				int height = b.getHeight() - 1 - topLeftInset * 2;

				g.setColor(getFocusColor());
				g.drawLine(2, 2, 2, 2 + height + 1);
				g.drawLine(2, 2 + height + 1, 2 + width + 1, 2 + height + 1);
				g.drawLine(2 + width + 1, 2 + height + 1, 2 + width + 1, 24);
				g.drawLine(2 + width + 1, 24, 2 + width - 20, 24);
				g.drawLine(2 + width - 20, 24, 2 + width - 20, 2);
				g.drawLine(2 + width -20, 2, 2, 2);
			}
		});

		this.compareBtn.setLayout(new FormLayout("0px:g, p", "p, 0px:g"));
		this.compareBtn.add(settingsBtn, CC.xy(2, 1));
		this.compareBtn.setMargin(new Insets(-2, -3, -3, -3));

		// install action on compare button to set up comparison background task
		this.compareBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				// fetch experiments from list table
				TableModel model = experimentTbl.getModel();
				ArrayList<MPAExperiment> experiments = new ArrayList<>();
				for (int i = 0; i < model.getRowCount() - 1; i++) {
					experiments.add((MPAExperiment) model.getValueAt(i, 0));
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
				ComparePanel.this.experiments = experiments;
				ComparePanel.CompareTask compareTask = new ComparePanel.CompareTask(yAxisType, zAxisType);
				compareTask.execute();
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
		controlPnl.add(this.compareBtn, CC.xyw(2, 10, 3));

		settingsPnl.add(experimentPnl, CC.xy(2, 2));
		settingsPnl.add(controlPnl, CC.xy(4, 2));

		JXTitledPanel settingsTtlPnl = PanelConfig.createTitledPanel("Settings", settingsPnl);

		// Create panel containing the comparison results table
		JPanel compareTblPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));

		ComparePanelTableModel comparePanelTableModel = new ComparePanelTableModel();

		JXTable compareTbl = new JXTable(comparePanelTableModel) {
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

		JXTable headerTbl = new JXTable(compareTbl.getModel()) {
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
				new HighlightPredicate.NotHighlightPredicate(new HighlightPredicate.ColumnHighlightPredicate(0)),
				TableConfig.getSimpleStriping(), new BorderHighlighter(
						BorderFactory.createMatteBorder(0, 0, 1, 1, UIManager.getColor("Table.gridColor")))));

		// adjust row header view size on column resize
		headerTbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		headerTbl.getColumnModel().addColumnModelListener(new TableColumnModelListener() {
			@Override
			public void columnMarginChanged(ChangeEvent evt) {
				JViewport rowHeader = ComparePanel.this.comparePane.getRowHeader();
				Dimension prefSize = rowHeader.getPreferredSize();
				prefSize.width = headerTbl.getPreferredSize().width;
				rowHeader.setPreferredSize(prefSize);
			}

			public void columnSelectionChanged(ListSelectionEvent evt) { }
			public void columnRemoved(TableColumnModelEvent evt) { }
			public void columnMoved(TableColumnModelEvent evt) { }
			public void columnAdded(TableColumnModelEvent evt) { }
		});

		this.comparePane = new JScrollPane(compareTbl);
		this.comparePane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.comparePane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		this.comparePane.setPreferredSize(new Dimension());

		this.comparePane.setRowHeaderView(headerTbl);
		this.comparePane.setCorner(JScrollPane.UPPER_LEFT_CORNER, header);

		JPanel dummyPnl = new JPanel();
		dummyPnl.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIManager.getColor("controlDkShadow")));
		this.comparePane.setCorner(JScrollPane.LOWER_LEFT_CORNER, dummyPnl);

		compareTblPnl.add(this.comparePane, CC.xy(2, 2));

		// Create button panel
		JPanel buttonPnl = new JPanel(new FormLayout("22px, 2px, 22px, 1px", "f:20px"));
		buttonPnl.setOpaque(false);

		JToggleButton numberTgl = new JToggleButton(IconConstants.TEXTFIELD_ICON);
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
				export2Csv();
			}

			/**
			 * Method to export compare table as CSV format, but tab separated
			 */
			private void export2Csv() {	
				/**
				 * TSV format separator.
				 */
				String SEP = "\t";

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
						TableModel model = ((JTable) ComparePanel.this.comparePane.getViewport().getView()).getModel();

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
									bw.append(value.toString());
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

		add(settingsTtlPnl, CC.xy(2, 2));
		add(compareTblTtlPnl, CC.xy(2, 4));
	}

	/**
	 * Class to handle the choosen experiments and the experiment table.
	 * @author A. Behne und R. Heyer
	 */
	private class ExperimentTableHandler implements ListSelectionListener {

		/**
		 * The table of choosen experiments
		 */
		private final JTable table;

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
				int row = table.getSelectedRow();
				if (row == (table.getRowCount() - 1)) {
					try {
						// create dialog for experiment selection from the database
						List<MPAExperiment> experiments = showExperimentSelectionDialog();
						table.clearSelection();
						if (!experiments.isEmpty()) {
							ComparePanel.this.compareBtn.setEnabled(true);
							for (MPAExperiment experiment : experiments) {
								TableModel model = table.getModel();
								// check whether experiment already exists
								for (int i = 0; i < model.getRowCount() - 1; i++) {
									if (experiment.equals(model.getValueAt(i, 0))) {
										table.getSelectionModel().setSelectionInterval(i, i);
										return;
									}
								}
								int lastRow = table.getRowCount() - 1;
								((DefaultTableModel) model).insertRow(
										lastRow, new Object[] { experiment });
								table.getSelectionModel().setSelectionInterval(lastRow, lastRow);
							}
						} else {
							ComparePanel.this.compareBtn.setEnabled(false);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		/**
		 * Creates and shows a dialog for selecting an experiment from the database.
		 * @return the selected experiment or <code>null</code>
		 * @throws Exception if an error occurs
		 */
		private List<MPAExperiment> showExperimentSelectionDialog() throws Exception {
			JPanel dialogPnl = new JPanel();
			dialogPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p, 5dlu" , "5dlu, p, 5dlu"));

			@SuppressWarnings("serial") JTable projTbl = new JTable(new DefaultTableModel(new Object[] { "Projects" }, 0)) {
				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
			projTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			// Initialize ProjectManager to access projects and experiments in the database
			// Get projects from database.
			List<MPAProject> projects = ProjectManager.getInstance().getProjects();

			List<String> titles = new ArrayList<String>();
			for (MPAProject project : projects) {
				titles.add(project.getTitle());
			}
			fillTable(projTbl, titles);

			@SuppressWarnings("serial") JTable expTbl = new JTable(new DefaultTableModel(new Object[] {"Experiments"}, 0)) {
				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};
			//			expTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			List<MPAExperiment> experiments = new ArrayList<MPAExperiment>();

			projTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					int selRow = projTbl.getSelectedRow();
					MPAProject project = projects.get(selRow);
					experiments.clear();
					experiments.addAll(project.getExperiments());

					if (!experiments.isEmpty()) {
						List<String> titles = new ArrayList<String>();
						for (MPAExperiment experiment : experiments) {
							titles.add(experiment.getTitle());
						}
						fillTable(expTbl, titles);
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
			List<MPAExperiment> selectedExperiments = new ArrayList<MPAExperiment>();
			int ret = JOptionPane.showConfirmDialog(ClientFrame.getInstance(), dialogPnl, "Choose an Experiment", JOptionPane.OK_CANCEL_OPTION);
			if (ret == JOptionPane.OK_OPTION) {
				int[] rows = expTbl.getSelectedRows();
				for (int selRow : rows) {
					int row = expTbl.convertRowIndexToModel(selRow);
					if (row != -1) {
						// Fetch experiment from database
						selectedExperiments.add(experiments.get(row));
					}
				}
			}
			return selectedExperiments;
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
	 * Worker to compare data in a background thread.
	 * @author T. Muth
	 */
	private class CompareTask extends SwingWorker {

		/**
		 * The comparison attribute type.
		 */
		private final ChartType chartType;

		/**
		 * The comparison count type.
		 */
		private final HierarchyLevel countLevel;

		/**
		 * CompareExperiments instance. New method without GraphDB.
		 */
//		private ExperimentComparison expComparison;
		private CompareExperiments cmpExp;

		/**
		 * The complete metaprotein list of all experiments.
		 */
//		private ArrayList<MetaProteinHit> metaProtList;

		/**
		 * Constructs a compare worker from the specified list of experiments to be compared.
		 * @param hierarchyLevel 
		 * @param countLevel
		 */
		public CompareTask(ChartType hierarchyLevel, HierarchyLevel countLevel) {
			chartType = hierarchyLevel;
			this.countLevel = countLevel;
		}

		@Override
		protected Object doInBackground() throws Exception {
			
			// TODO: make this work for big datasets ....
			// Step-by-Step
			// 1. remove graphdb -references
			// 2. decide if direct query or from result set -> use result sets for now
			// 3. implement new classes -> decider class -> subclasses for individual tasks
			// 4. finish implementation and test each 
			// 5. done - you win a rubber ducky
			
			try {
				Client client = Client.getInstance();
				client.firePropertyChange("new message", null, "STARTING COMPARISON AND FETCHING DATA");
//				client.setupGraphDatabase(false);
				GraphDatabaseHandler graphDatabaseHandler = null;
				// Iterate the experiments.
				DbSearchResult dbSearchResult = new DbSearchResult(ComparePanel.this.experiments);
				dbSearchResult.getSearchResultByView();
				dbSearchResult.setFDR((double) ComparePanel.this.metaParams.get("FDR").getValue());
				MetaProteinFactory.determineTaxonomyAndCreateMetaProteins(dbSearchResult, ComparePanel.this.metaParams);
				// Compare the experiments.
				// complete new compare classes
				this.cmpExp = new CompareExperiments(ComparePanel.this.experiments, dbSearchResult, chartType, countLevel);
//				this.expComparison = new ExperimentComparison(ComparePanel.this.experiments, this.metaProtList, graphDatabaseHandler, chartType, countLevel);
				this.refreshCompareTable(this.cmpExp.getResults(), ComparePanel.this.experiments);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void done() {
			if (this.cmpExp != null && this.cmpExp.getResults() != null) {
				// the refresh table uses this input 
				// Map<String, Long[]> dataMap, List<AbstractExperiment> experiments
//				this.refreshCompareTable(this.cmpExp.getResults(), ComparePanel.this.experiments);
			} else {
				Client.getInstance().firePropertyChange("new message", null, "QUERYING COMPARISON DATA FINISHED");
				Client.getInstance().firePropertyChange("indeterminate", true, false);
			}
		}

		/**
		 * Method to refresh the compare table.
		 * @param map Comparison data map
		 * @param List of experiments	 
		 */
		private void refreshCompareTable(Map<String, Long[]> dataMap, ArrayList<MPAExperiment> experiments) {
			String[] columnNames;
			int offset = 1;
			columnNames = new String[experiments.size() + offset];
			columnNames[0] = "Description";

			for (int i = offset; i < columnNames.length; i++) {
				for (MPAExperiment experiment : experiments) {
					columnNames[i] = experiment.getTitle() + " (" + experiment.getID() + ")";
					break;
				}
			}

			ComparePanelTableModel model = new ComparePanelTableModel(dataMap, experiments);

			JXTable compareTbl = (JXTable) ComparePanel.this.comparePane.getViewport().getView();
			compareTbl.setModel(model);
			compareTbl.setAutoCreateRowSorter(true);

			compareTbl.getColumn("Description").setMinWidth(100);

			compareTbl.packAll();
			compareTbl.repaint();
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
		public CompareData(ChartType yAxisType, HierarchyLevel zAxisType) {
			super(CompareData.EXPERIMENT, yAxisType, zAxisType);
		}

	}

	/**
	 * Class to create an editable table, which allows adding and deleting of elements.
	 * @author A. Behne
	 */
	@SuppressWarnings("serial")
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
		private ListTable(DefaultTableModel model, String editorString) {
			super(model);
			model.addRow(new Object[] { editorString });

			// Install highlighter to display last row's text in italics
			Font italicFont = new JLabel().getFont().deriveFont(Font.ITALIC);
			addHighlighter(new FontHighlighter(new HighlightPredicate() {
				public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
					return (adapter.row == (adapter.getRowCount() - 1));
				}
			}, italicFont));
			// Install renderer to first column to remove focus border
			getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
				@Override
				public Component getTableCellRendererComponent(JTable table,
						Object value, boolean isSelected, boolean hasFocus,
						int row, int column) {
					return super.getTableCellRendererComponent(table, value, isSelected, false,
							row, column);
				}
			});
			// Install renderer to second column to display cross icon
			getColumn(1).setCellRenderer(new TableCellRenderer() {
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
			setShowGrid(true, false);
			// Allow only single selection
			setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

			// Fix second column width and disallow reordering/resizing of columns
			getColumn(1).setMaxWidth(18);
			getTableHeader().setReorderingAllowed(false);
			getTableHeader().setResizingAllowed(false);

			// Create text field editor for first column
			JTextField editorTtf = new JTextField();
			editorTtf.setBackground(getSelectionBackground());
			editorTtf.setSelectionColor(getBackground());
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
					String val = (String) this.delegate.getCellEditorValue();
					if (!val.equals(editorString)) {
						// Changed row becomes non-editable, re-add editor row
						boolean res = super.stopCellEditing();
						model.addRow(new Object[] { editorString });
						return res;
					}
					this.cancelCellEditing();
					return false;
				}
			};
			getColumn(0).setCellEditor(textEditor);

			// Create editor for second column, repurpose checkbox editor (checkboxes are basically buttons)
			JCheckBox editorChk = new JCheckBox();
			editorChk.setIcon(IconConstants.CROSS_ICON);
			editorChk.setPressedIcon(IconConstants.CROSS_PRESSED_ICON);
			getColumn(1).setCellEditor(new DefaultCellEditor(editorChk) {
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
					if ((Boolean) this.getCellEditorValue()) {
						// Remove selected table row
						model.removeRow(ListTable.this.getSelectedRow());
					}
					return res;
				}
			});

			// Remove table header to appear like a list
			setTableHeader(null);
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			// only allow editing of last row's first column ('new group' editor)
			// or every but last row's second column ('remove row' buttons)
			return ((column == 0) && (row == (this.getRowCount() - 1))) ||
					((column == 1) && (row < (this.getRowCount() - 1)));
		}
	}

}
