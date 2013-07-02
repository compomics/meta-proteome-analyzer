package de.mpa.client.ui.panels;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.Vector;
import java.util.logging.Level;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.FontHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.painter.Painter;

import uk.ac.ebi.kraken.interfaces.uniprot.SecondaryUniProtAccession;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import de.mpa.algorithms.quantification.NormalizedSpectralAbundanceFactor;
import de.mpa.analysis.ProteinAnalysis;
import de.mpa.analysis.UniprotAccessor;
import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.model.dbsearch.ProteinHit;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.DefaultTableHeaderCellRenderer;
import de.mpa.client.ui.PanelConfig;
import de.mpa.client.ui.TableConfig;
import de.mpa.client.ui.TableConfig.CustomTableCellRenderer;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.db.ProjectManager;
import de.mpa.db.accessor.Experiment;
import de.mpa.db.accessor.Project;

/**
 * Prototyp for comparison of different samples.
 * @author R. Heyer, F. Kohrs, A. Behne
 */
public class ComparePanel extends JPanel{

	/**
	 * Parent class clientFrame.
	 */
	private ClientFrame clientFrame;

	/**
	 * Level of comparison.
	 */
	private static final String[] ENTRIES = new String[] {"Accessions","Correct2UniProt"};//, "Metaproteins"};//TODO add Metaproteins

	/**
	 * Quantitative information for comparison for different runs.
	 */
	private static final String[] COMPARE = new String[] {"Proteins", "Peptides", "Spectra", "NSAF", "emPAI"};

	/**
	 * Rule how to fuse the measurements to a group.
	 */
	private static final String[] Pooling = new String[] {"Average", "Max", "Min", "Sum"};

	/**
	 * The map with the compared Objects
	 */
	private Map<String, List<Experiment>> groupMap =  new LinkedHashMap<String, List<Experiment>>();
	
	private boolean experimentsHaveChanged = false;

	/**
	 * The key of the selected group.
	 */
	private String selGroup;

	/**
	 * The compare tab
	 */
	private JXTable compareTbl;

	/**
	 * ComboBox for the quantification spectralCount and so
	 */
	private JComboBox compareCbx;

	/**
	 * ComboBox for the rule of averaging a group
	 */
	private JComboBox groupingCbx;

	/**
	 * List of all groups and their database search result objects
	 */
	private List<List<DbSearchResult>> groupResultList;

	/**
	 * JComboBox for the things you want to compare
	 */
	private JComboBox entryCbx;

	/**
	 * Constructor for compare panel.
	 */
	public ComparePanel() {
		this.clientFrame = ClientFrame.getInstance();

		// Build the panel
		initComponents();
	}

	/**
	 * Init components of the compare panel.
	 */
	private void initComponents() {
		this.setLayout(new FormLayout("5dlu, p:g, 5dlu, p, 5dlu", "5dlu, f:p, 5dlu, f:p:g, 5dlu")); // Column, Row
		// Get settings for colored title border PanelConig class.
		Border ttlBorder 	= PanelConfig.getTitleBorder();
		Painter ttlPainter 	= PanelConfig.getTitlePainter();
		Font ttlFont 		= PanelConfig.getTitleFont();
		Color ttlForeground = PanelConfig.getTitleForeground();

		// 1. Create samples panel
		JPanel samplesPnl = new JPanel();
		// Equals size of the panels for groups and measurements
		FormLayout samplesLyt = new FormLayout("5dlu, p:g, 5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu");
		samplesLyt.setColumnGroups(new int[][] { {2, 4} });
		samplesPnl.setLayout(samplesLyt); // Column, Row
		// Create titled border
		JXTitledPanel samplesTtlPnl = new JXTitledPanel("Samples", samplesPnl);
		samplesTtlPnl.setBorder(ttlBorder);
		samplesTtlPnl.setTitlePainter(ttlPainter);
		samplesTtlPnl.setTitleFont(ttlFont);
		samplesTtlPnl.setTitleForeground(ttlForeground);
		//1a.) Group panel
		JPanel groupPnl  = new JPanel();
		groupPnl.setLayout(new FormLayout(" 5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu")); // Column, Row
		groupPnl.setBorder(BorderFactory.createTitledBorder("Group"));
		// Create list-like table
		final JXTable groupTbl = new ListTable("Click to Add New Group...") {
			@Override
			public void editingStopped(ChangeEvent e) {
				if (editingColumn == 1) {
					groupMap.remove(this.getValueAt(this.getSelectedRow(), 0));
					selGroup = null;
					experimentsHaveChanged = true;
				} else {
					groupMap.put(getCellEditor().getCellEditorValue().toString(), new ArrayList<Experiment>());
				}
				super.editingStopped(e);
			}
		};

		// Create text field editor for first column
		JTextField editorTtf = new JTextField();
		editorTtf.setBackground(groupTbl.getSelectionBackground());
		editorTtf.setSelectionColor(groupTbl.getBackground());
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
				if (!val.equals("Click to Add New Group...") && !groupMap.containsKey(val)) {
					// Changed row becomes non-editable, re-add editor row
					boolean res = super.stopCellEditing();
					((DefaultTableModel) groupTbl.getModel()).addRow(new Object[] { "Click to Add New Group..." });
					return res;
				}
				cancelCellEditing();
				groupTbl.clearSelection();
				return false;
			}
		};
		groupTbl.getColumn(0).setCellEditor(textEditor);

		final JXTable measurementTbl = new ListTable(null){
			@Override
			public void editingStopped(ChangeEvent e) {
				if (editingColumn == 1) {
					List<Experiment> list = groupMap.get(selGroup);
					if (list != null) {
						list.remove(editingRow);
						groupMap.put(selGroup, list);
						experimentsHaveChanged = true;
					}
				}
				super.editingStopped(e);
			}
		};
		// Remove table header to appear like a list
		groupTbl.setTableHeader(null);
		groupTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					int row = groupTbl.getSelectedRow();
					if (row == (groupTbl.getRowCount() - 1)) {
						TableConfig.clearTable(measurementTbl);
						groupTbl.editCellAt(row, 0);
						((DefaultCellEditor) groupTbl.getCellEditor()).getComponent().requestFocus();
					} else {
						if (row > -1) {
							selGroup = (String) groupTbl.getValueAt(row, 0);
							if (selGroup != null) {
								refreshMeasurementTable(measurementTbl);
							}
						} else {
							TableConfig.clearTable(measurementTbl);
							selGroup = null;
						}
					}
				}
			}
		});

		groupTbl.getModel().addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				selGroup = (String) groupTbl.getValueAt(groupTbl.getSelectedRow(), 0);
				refreshMeasurementTable(measurementTbl);
			}
		});

		// Wrap table in scroll pane, always display vertical scroll bar
		JScrollPane groupScp = new JScrollPane(groupTbl);
		groupScp.setPreferredSize(new Dimension(0,0));
		groupScp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		// Add elements to superior panel
		groupPnl.add(groupScp, CC.xy(2, 2));
		samplesPnl.add(groupPnl, CC.xy(2, 2));

		// 1b.) Measurement panel
		JPanel measurementPnl  = new JPanel();
		measurementPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu")); // Column, Row
		measurementPnl.setBorder(BorderFactory.createTitledBorder("Measurement"));
		measurementTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					int row = measurementTbl.getSelectedRow();
					if (row == (measurementTbl.getRowCount() - 1)) {
						Experiment experiment;
						try {
							experiment = showExperimentSelectionDialog(measurementTbl);
							// Add the experiment to the chosen group
							measurementTbl.clearSelection();
							if (experiment != null) {
								List<Experiment> list = groupMap.get(selGroup);
								if (list == null) {
									list = new ArrayList<Experiment>();
								}
								list.add(experiment);
								groupMap.put(selGroup, list);
								experimentsHaveChanged = true;
								int lastRow = measurementTbl.getRowCount() - 1;
								((DefaultTableModel) measurementTbl.getModel()).insertRow(
										lastRow, new Object[] { experiment.getTitle() });
								measurementTbl.getSelectionModel().setSelectionInterval(lastRow, lastRow);
							}
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});

		JScrollPane measurementScp = new JScrollPane(measurementTbl);
		measurementScp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		measurementScp.setPreferredSize(new Dimension(0,0));
		measurementPnl.add(measurementScp, CC.xy(2, 2));

		samplesPnl.add(measurementPnl, CC.xy(4, 2));

		// 2. Create settings panel
		JPanel settingsPnl = new JPanel();
		settingsPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, p, 5dlu, p, 5dlu")); // Columns, Rows
		JXTitledPanel settingsTtlPnl = new JXTitledPanel("Settings", settingsPnl);
		settingsTtlPnl.setBorder(ttlBorder);
		settingsTtlPnl.setTitlePainter(ttlPainter);
		settingsTtlPnl.setTitleFont(ttlFont);
		settingsTtlPnl.setTitleForeground(ttlForeground);

		// 2b. Parameter 
		JPanel parameterPnl = new JPanel();
		parameterPnl.setBorder(BorderFactory.createTitledBorder("Parameters"));
		parameterPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p, 5dlu", "5dlu, p, 5dlu, p, 5dlu, p ,5dlu")); // Column, Rows
		JLabel entryLbl 		= new JLabel("Entry");
		entryCbx 		= new JComboBox(ENTRIES); 
		entryCbx.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				experimentsHaveChanged = true;
			}
		});
		entryCbx.setToolTipText("Select the criterion to compare the experiments.");
		JLabel compareLbl 		= new JLabel("Compare");
		compareCbx	= new JComboBox(COMPARE); 
		compareCbx.setToolTipText("<html>Quantitative criterion to compare:" +
				"<ol><li>Proteins: non-redundant identified proteins</li>" +
				"<li>Peptides: peptide count/PepC (non-redundant peptides)</li>" +
				"<li>Spectra: spectral count/SpC (non-redundant spectra)</li>" +
				"<li>NSAF: normalized spectrum abundance factor</li>" +
				"<li>emPAI: exponentially modified protein abundance index</li></ol></html>");

		JLabel groupingLbl = new JLabel("Pooling");
		groupingCbx = new JComboBox(Pooling); 
		groupingCbx.setToolTipText("<html>Pool expermiments by:" +
				"<ol><li>Average: arithmetic mean</li>" +
				"<li>Max: take maximum value of selected group</li>" +
				"<li>Min: take minimum value of selected group</li>" +
				"<li>Sum: sum up all values of selected group (redundant list)</li></ol></html>");
		parameterPnl.add(entryLbl, 		CC.xy(2, 2));
		parameterPnl.add(entryCbx, 		CC.xy(4, 2));
		parameterPnl.add(compareLbl, 	CC.xy(2, 4));
		parameterPnl.add(compareCbx, 	CC.xy(4, 4));
		parameterPnl.add(groupingLbl, 	CC.xy(2, 6));
		parameterPnl.add(groupingCbx, 	CC.xy(4, 6));
		settingsPnl.add(parameterPnl,	CC.xy(2, 2));
		JButton createTableBtn = new JButton(" Create Table ", new ImageIcon(getClass().getResource("/de/mpa/resources/icons/scull16.png")));
		final Font boldFont = new JLabel().getFont().deriveFont(Font.BOLD);
		createTableBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				compareTbl.setModel(createCompareTableModel());
				refreshCompareTable(compareTbl);
				experimentsHaveChanged = false;
				int colIndex = 4;
				for (Entry<String, List<Experiment>> groupEntry : groupMap.entrySet()) {
					colIndex += groupEntry.getValue().size();
					compareTbl.getColumnExt(colIndex++).addHighlighter(new FontHighlighter(boldFont));
				}
				compareTbl.packAll(); // Resize the table
				compareTbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Necessary for srollBar
			}
		});
		settingsPnl.add(createTableBtn, CC.xy(2, 4));

		// 3. Create table
		JPanel comparePnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu"));
		JXTitledPanel compareTtlPnl = new JXTitledPanel("Compare Table", comparePnl);
		compareTtlPnl.setBorder(ttlBorder);
		compareTtlPnl.setTitlePainter(ttlPainter);
		compareTtlPnl.setTitleFont(ttlFont);
		compareTtlPnl.setTitleForeground(ttlForeground);

		compareTbl = new JXTable(createCompareTableModel()) {
			// Overright table column
			public Class<?> getColumnClass(int column) {
				if (convertColumnIndexToModel(column) == 0) {
					return Boolean.class; // Checkbox in table
				}
				return super.getColumnClass(column);
			}
		};
		// Allows header with line breaks
		compareTbl.getTableHeader().setDefaultRenderer(new DefaultTableHeaderCellRenderer(){
			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				FormLayout layout = new FormLayout("12px:g, p, 2dlu, 12px:g");
				JPanel panel = new JPanel(layout);
				String title = value.toString();
				int rows = 1;
				if (title.contains("\n")) {
					String[] lines = title.split("\n");
					for (String line : lines) {
						layout.appendRow(RowSpec.decode("f:p:g"));
						JLabel label = new JLabel(line, SwingConstants.CENTER);
						label.setVerticalAlignment(SwingConstants.CENTER);
						panel.add(label, CC.xy(2, rows++));
					}
					rows--;
				} else {
					layout.appendRow(RowSpec.decode("f:p:g"));
					JLabel label = new JLabel(title, SwingConstants.CENTER);
					panel.add(label, CC.xy(2, 1));
				}
				panel.setBorder(((JComponent) comp).getBorder());
				panel.add(new JLabel(((JLabel) comp).getIcon()), CC.xywh(4, 1, 1, rows));
				return panel;
			}
		});
		JScrollPane compareScp = new JScrollPane(compareTbl);
		compareScp.setPreferredSize(new Dimension());
		compareScp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		compareScp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		// Add buttons for data analysis and export
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		buttonPanel.setOpaque(false);

		final String[] cardLabels = new String[] {"Original", "Flat View"};
		final CardLayout cardLyt = new CardLayout();
		final JPanel cardPnl = new JPanel(cardLyt);
		cardPnl.add(compareScp, cardLabels[0]);
		cardPnl.add(new JButton(), cardLabels[1]);

		final JButton viewBtn = new JButton(IconConstants.HIERARCHY_ICON);

		final JPopupMenu hierarchyPop = new JPopupMenu();
		ActionListener hierarchyListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cardLyt.show(cardPnl, ((AbstractButton) e.getSource()).getText());
			}
		};
		ButtonGroup bg = new ButtonGroup();
		for (int j = 0; j < cardLabels.length; j++) {
			JMenuItem item = new JRadioButtonMenuItem(cardLabels[j], (j == 0));
			item.addActionListener(hierarchyListener);
			bg.add(item);
			hierarchyPop.add(item);
		}
		hierarchyPop.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				viewBtn.setSelected(false);
			}
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
			public void popupMenuCanceled(PopupMenuEvent e) {}
		});

		viewBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				hierarchyPop.show(viewBtn, 0, viewBtn.getHeight());
			}
		});

		// Export button
		JButton exportBtn = new JButton("",IconConstants.EXCEL_EXPORT_ICON);
		exportBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				export2Csv(); 
			}
		});
		exportBtn.setToolTipText("<html>Export table as <b>tab separated values</b> .csv file</html>");

		buttonPanel.add(viewBtn);
		buttonPanel.add(exportBtn);
		compareTtlPnl.setRightDecoration(buttonPanel);

		comparePnl.add(cardPnl, CC.xy(2, 2));

		// add all subPanels
		this.add(samplesTtlPnl, CC.xy(2, 2));
		this.add(settingsTtlPnl, CC.xy(4, 2));
		this.add(compareTtlPnl, CC.xyw(2, 4, 3));
	}

	/**
	 * Create dialog to fetch Experiments from the database
	 * @param table the measurement table instance
	 * @throws SQLException 
	 */
	private Experiment showExperimentSelectionDialog(JTable table) throws SQLException {
		JPanel dialogPnl = new JPanel();
		dialogPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p, 5dlu" , "5dlu, p, 5dlu"));

		final JTable projTbl = new JTable(new DefaultTableModel(new Object[] {"Projects"}, 0)){
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		projTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Initialize ProjectManager to access projects and experiments in the database
		final ProjectManager projectManager = new ProjectManager(Client.getInstance().getConnection());
		// Get projects from database.
		final List<Project> projects = projectManager.getProjects();

		List<String> titles = new ArrayList<String>();
		for (Project project : projects) {
			titles.add(project.getTitle());
		}
		fillTable(projTbl, titles);

		final JTable expTbl = new JTable(new DefaultTableModel(new Object[] {"Experiments"}, 0)){
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		expTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

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
						//						refreshExperimentTable(experimentTable);
						fillTable(expTbl, titles);
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

		// Second get associated experiments
		Experiment experiment = null;
		int ret = JOptionPane.showConfirmDialog(clientFrame, dialogPnl, "Choose an Experiment", JOptionPane.OK_CANCEL_OPTION);
		if (ret == JOptionPane.OK_OPTION) {
			int selExpRow = expTbl.convertRowIndexToModel(expTbl.getSelectedRow());
			if (selExpRow != -1) {
				// Get choosen experiment from the db.
				try {
					long experimentid = experiments.get(selExpRow).getExperimentid();
					experiment = projectManager.getExperiment(experimentid);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return experiment;
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

	/**
	 * Refresh the measurement table.
	 * @param measurementTbl
	 */
	private void refreshMeasurementTable(JXTable table) {
		TableConfig.clearTable(table);
		List<Experiment> expList = groupMap.get(selGroup);
		table = new ListTable((DefaultTableModel) table.getModel(), "Click to Add Measurement...");

		if (expList != null) {
			for (int i = 0; i < expList.size(); i++) {
				Experiment experiment = expList.get(i);
				String title = experiment.getTitle();
				((DefaultTableModel) table.getModel()).insertRow(table.getRowCount() - 1, new Object[] { title });
			}
		}
	}

	/**
	 * Method to create the table model for the compare table.
	 * @return The default table model.
	 */
	private DefaultTableModel createCompareTableModel() {
		DefaultTableModel compareTableMdl = new DefaultTableModel(){
			{
				// Create List of table Objects
				Vector<Object> tableHeaderVector = new Vector<Object>();
				tableHeaderVector.add("");
				tableHeaderVector.add("#");
				tableHeaderVector.add("Accession");
				tableHeaderVector.add("Description");

				// Go through groups and add their experiments columns for groups
				if (groupMap != null) {
					for (Entry entry : groupMap.entrySet()) {
						// Iterate through experiments
						@SuppressWarnings("unchecked")
						List<Experiment> expList = ((List<Experiment>) entry.getValue());
						if (expList != null) {
							for (int i = 0; i < expList.size(); i++) {
								tableHeaderVector.add(entry.getKey() + "\n" + expList.get(i).getTitle());
							}
							tableHeaderVector.add(entry.getKey());
						}
					}
				}
				// Set header to table model
				setColumnIdentifiers(tableHeaderVector);
			}
			// Set variable types of columns for row sorting
			public Class<?> getColumnClass(int columnIndex) {
				switch (columnIndex) {
				case 0: 
					return Boolean.class;
				case 1:
					return Double.class;
				case 2: 
				case 3:
					return String.class;
				default:
					return Long.class;
				}
			}
		};
		
		
		return compareTableMdl;
	}

	/**
	 * Method to refresh the compare table.
	 * @param compareTable
	 */
	private void refreshCompareTable(JXTable table) {

		// Get model and clear table
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		TableConfig.clearTable(table);

		CustomTableCellRenderer renderer;
		switch (compareCbx.getSelectedIndex()) {
		case 0: // Proteins
		case 1: // Peptides
		case 2: // SpectralCount
		case 4: // emPai
			renderer = new CustomTableCellRenderer(SwingConstants.RIGHT, "0.00");
			for (int col = 4; col < table.getColumnCount(); col++) {
				compareTbl.getColumnModel().getColumn(col).setCellRenderer(renderer);
			}
			break;
		case 3: // NSAF
			renderer = new CustomTableCellRenderer(SwingConstants.RIGHT, "0.00E00");
			for (int col = 4; col < table.getColumnCount(); col++) {
				compareTbl.getColumnModel().getColumn(col).setCellRenderer(renderer);
			}
			break;
		}

		// 1. Create proteinHitSet
		Map<String,ProteinHit> proteinMap = new TreeMap<String,ProteinHit>();
		if (experimentsHaveChanged) {
			groupResultList = new ArrayList<List<DbSearchResult>>();
			for (Entry<String, List<Experiment>> groupEntry: groupMap.entrySet()) {
				ArrayList<DbSearchResult> resultList = new ArrayList<DbSearchResult>();
				// Get Experiment
				List<Experiment> experimentList = groupEntry.getValue();
				if (experimentList != null ) {
					Client client = Client.getInstance();
					for (Experiment experiment : experimentList) {
						Client.getInstance().retrieveDbSearchResult(groupEntry.getKey(),experiment.getTitle(),experiment.getExperimentid() );
						DbSearchResult dbSearchResult = client.getDbSearchResult();

						if (!dbSearchResult.isEmpty()) {
							// Retrieve UniProt data
							client.firePropertyChange("new message", null, "QUERYING UNIPROT ENTRIES");
							client.firePropertyChange("indeterminate", false, true);
							try {
								UniprotAccessor.retrieveUniProtEntries(dbSearchResult);
							} catch (Exception e) {
								JXErrorPane.showDialog(clientFrame, new ErrorInfo("Error",
										"UniProt access failed. Please try again later.",
										e.getMessage(), null, e, Level.SEVERE, null));
							}
							
							client.firePropertyChange("new message", null, "QUERYING UNIPROT ENTRIES FINISHED");
							client.firePropertyChange("indeterminate", true, false);
						}
						
						//TODO remove double entries
						for (ProteinHit protHit : dbSearchResult.getProteinHitList()) {
							if (!proteinMap.containsKey(protHit.getAccession())) {
								proteinMap.put(protHit.getAccession(), protHit);
							}
						}
						resultList.add(dbSearchResult);
					}
				}
				groupResultList.add(resultList);
			}
		} else {
			if (groupResultList != null) {
				for (List<DbSearchResult> resultList : groupResultList) {
					for (DbSearchResult dbSearchResult : resultList) {
						for (ProteinHit protHit : dbSearchResult.getProteinHitList()) {
							if (!proteinMap.containsKey(protHit.getAccession())) {
								proteinMap.put(protHit.getAccession(), protHit);
							}
						}
					}
				}
			}
		}
		List<ProteinHit> removeList = new ArrayList<ProteinHit>();
		// List for mapping back from UniProt2NCBI
		Map<String,String> uniProt2NCBI = new TreeMap<String, String>(); 
		// For NCBI2UniProt correction
		if (!entryCbx.getSelectedItem().equals("Accessions")) {
			for (ProteinHit proteinHit : proteinMap.values()) {
				String accession = proteinHit.getAccession();
				UniProtEntry uniprotEntry = proteinHit.getUniprotEntry();
				// Case that NCBI accession with UniProt Mapping
				if (uniprotEntry != null) {
					// Build list of UniProt accessions
					List<String> uniProtAccs = new ArrayList<String>();
					uniProtAccs.add(uniprotEntry.getPrimaryUniProtAccession().getValue());
					ListIterator<SecondaryUniProtAccession> secondaryUniProtAccessions = uniprotEntry.getSecondaryUniProtAccessions().listIterator();
					while (secondaryUniProtAccessions.hasNext()) {
						uniProtAccs.add(secondaryUniProtAccessions.next().getValue());
					}
					if (!uniProtAccs.contains(accession)) { // Check for non UNIProt entries
						removeList.add(proteinHit);
						uniProt2NCBI.put(uniProtAccs.get(0), accession);
					}
				}
			}
			// Remove redundant entries
			for (ProteinHit proteinHit : removeList) {
				proteinMap.remove(proteinHit.getAccession());
			}
		}
		
		// 2. Create table
		int rowIndex = 1;
		for (ProteinHit proteinEntry : proteinMap.values()) {
			Vector<Object> row = new Vector<Object>();
			row.add(true);
			row.add(rowIndex++);
			row.add(proteinEntry.getAccession());
			row.add(proteinEntry.getDescription());

			for (List<DbSearchResult> resultList : groupResultList) {
				List<Double> groupValues = new ArrayList<Double>(); 			// For grouping
				for (DbSearchResult dbSearchResult : resultList) {
					ProteinHit proteinHit = null;
					if (!entryCbx.getSelectedItem().equals("Accessions")) { 	// Try to correct to UniProt
					String accSet = proteinEntry.getAccession();
						proteinHit = dbSearchResult.getProteinHits().get(accSet);
						if (proteinHit == null) { 								// No UniProt identifier
							String ncbiAcc = uniProt2NCBI.get(accSet);
							if (ncbiAcc == null) {								// UniProt
								proteinHit = dbSearchResult.getProteinHits().get(proteinEntry.getAccession());
							} else {
								proteinHit = dbSearchResult.getProteinHits().get(ncbiAcc);
							}
						}
					}
					else{ // Use original identifier
						proteinHit = dbSearchResult.getProteinHits().get(proteinEntry.getAccession());
					}
					
					if (proteinHit == null) {
						row.add(0); // If protein is not included in the experiment
					} else {
						switch (compareCbx.getSelectedIndex()) {
						case 0: // Proteins
							row.add(1);
							break;
						case 1: // Peptides
							row.add(proteinHit.getPeptideCount());
							break;
						case 2: // SpectralCount
							row.add(proteinHit.getSpectralCount());
							break;
						case 3: // NSAF
							double nsaf = ProteinAnalysis.calculateLabelFree(new NormalizedSpectralAbundanceFactor(), dbSearchResult.getProteinHits(), proteinHit);
							row.add(nsaf);
							break;
						case 4: // emPai
							row.add(proteinHit.getEmPAI());
							break;
						}
					}
					groupValues.add(((Number)row.lastElement()).doubleValue());
				}

				// Group Level
				switch (groupingCbx.getSelectedIndex()) {
				case 0: // Average
					double average = 0.0;
					for (Double val : groupValues) {
						average += val;
					}
					row.add(average/ groupValues.size());
					break;
				case 1: // Max
					double max = -Double.MAX_VALUE;
					for (Double val : groupValues) {
						max = (max < val) ? val : max;
					}
					row.add(max);
					break;
				case 2: // Min
					double min = Double.MAX_VALUE;
					for (Double val : groupValues) {
						min = Math.min(min, val);
					}
					row.add(min);
					break;
				case 3: // Sum
					double sum = 0.0;
					for (Double val : groupValues) {
						sum = sum + val;
					}
					row.add(sum);
					break;
				}
			}
			// Add row to table model
			model.addRow(row);
		}
	}

	/**
	 * Method to export compare table as csv. format, but tab separated
	 */
	private void export2Csv() {	
		/**
		 * TSV format separator.
		 */
		final String SEP = "\t";

		JFileChooser chooser = new JFileChooser();
		int returnVal = chooser.showOpenDialog(clientFrame);
		chooser.setFileFilter(Constants.CSV_FILE_FILTER);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			File selectedFile = chooser.getSelectedFile();
			if(!selectedFile.getName().endsWith(".csv")){
				selectedFile = new File(selectedFile.getPath()+ ".csv");
			}

			BufferedWriter bw = null;
			try {
				bw = new BufferedWriter(new FileWriter(selectedFile));
				// Add table header
				for (int col = 0; col < compareTbl.getColumnCount(); col++) {
					if (compareTbl.convertColumnIndexToModel(col) == 0) {
						continue;
					}
					// Remove html tags
					String columnName = compareTbl.getColumnName(col);
					columnName.replaceAll("<.>", "");
					bw.append(compareTbl.getColumnName(col));
					bw.append(SEP);
				}
				bw.append("\n");

				// Add table
				for (int row = 0; row < compareTbl.getRowCount(); row++) {
					if ((Boolean) compareTbl.getValueAt(row, compareTbl.convertColumnIndexToView(0))) {
						for (int col = 0; col < compareTbl.getColumnCount(); col++) {
							if (compareTbl.convertColumnIndexToModel(col) == 0) {
								continue;
							}
							bw.append(compareTbl.getStringAt(row, col));
							bw.append(SEP);
						}
						bw.append("\n");
					}
				}
				bw.flush();
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
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