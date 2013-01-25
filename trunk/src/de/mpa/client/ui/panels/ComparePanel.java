package de.mpa.client.ui.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.painter.Painter;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Client;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.PanelConfig;
import de.mpa.client.ui.TableConfig;
import de.mpa.db.ProjectManager;
import de.mpa.db.accessor.Experiment;
import de.mpa.db.accessor.Project;

/**
 * Prototyp for comparison of different samples
 * 
 * @author R. Heyer
 */
public class ComparePanel extends JPanel {

	/**
	 * Parent class clientFrame.
	 */
	private ClientFrame clientFrame;

	/**
	 * Client for db access.
	 */
	private Client client;

	/**
	 * Table for the groups.
	 */
	private JTable groupTbl;

	/**
	 * Table for the measurements of a groups.
	 */
	private JTable measurementTbl;

	/**
	 * Project manager to access to the projects from the database
	 */
	private ProjectManager projectManager;

	/**
	 * List of all projects in the database
	 */
	private List<Project> projects;

	/**
	 * Level of comparison.
	 */
	private static final String[] ENTRIES = new String[] { "Accessions",
			"Metaproteins" };

	/**
	 * Quantitative information for comparison for different runs.
	 */
	private static final String[] COMPARE = new String[] { "Proteins",
			"Peptides", "Spectra", "NSAF", "emPai" };

	/**
	 * Rule how to fuse the measurements to a group.
	 */
	private static final String[] GROUPING = new String[] { "Average", "Max",
			"Min" };

	/**
	 * The map with the compared Objects
	 */
	private Map<String, List<Experiment>> groupMap = new TreeMap<String, List<Experiment>>();

	/**
	 * Button to remove groups.
	 */
	private JButton removeGroupBtn;

	/**
	 * Button to remove experiments.
	 */
	private JButton removeMeasurementBtn;

	/**
	 * Button to add experiments.
	 */
	private JButton addMeasurementBtn;

	/**
	 * Table for the projects
	 */
	private JTable projectTbl;

	/**
	 * Constructor for compare panel.
	 */
	public ComparePanel() {
		this.clientFrame = ClientFrame.getInstance();
		this.client = Client.getInstance();
		// Initialize ProjectManager to access to projects and experiments in
		// the database
		try {
			projectManager = new ProjectManager(Client.getInstance()
					.getConnection());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		initComponents();
	}

	/**
	 * Init components of the compare panel.
	 */
	private void initComponents() {
		this.setLayout(new FormLayout("5dlu,p:g, 5dlu",
				"5dlu, p, 5dlu, p, 5dlu, p:g, 5dlu")); // Column, Row
		// Get settings for coloured title border PanelConig class.
		Border ttlBorder = PanelConfig.getTitleBorder();
		Painter ttlPainter = PanelConfig.getTitlePainter();
		Font ttlFont = PanelConfig.getTitleFont();
		Color ttlForeground = PanelConfig.getTitleForeground();

		// 1. Create samples panel
		JPanel samplesPnl = new JPanel();
		samplesPnl.setLayout(new FormLayout("5dlu,p:g, 5dlu, p:g, 5dlu",
				"5dlu, p, 5dlu")); // Column, Row
		// Create titled border
		JXTitledPanel samplesTtlPnl = new JXTitledPanel("Samples", samplesPnl);
		samplesTtlPnl.setBorder(ttlBorder);
		samplesTtlPnl.setTitlePainter(ttlPainter);
		samplesTtlPnl.setTitleFont(ttlFont);
		samplesTtlPnl.setTitleForeground(ttlForeground);
		// 1a.) Group panel
		JPanel groupPnl = new JPanel();
		groupPnl.setLayout(new FormLayout(" 5dlu, p:g, 5dlu,p:g, 5dlu",
				"5dlu, p, 5dlu, p, 5dlu")); // Column, Row
		groupPnl.setBorder(BorderFactory.createTitledBorder("Group"));

		groupTbl = createGroupTable();
		JScrollPane groupScp = new JScrollPane(groupTbl);
		groupScp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		groupScp.setPreferredSize(new Dimension(100, 100));
		JButton addGroupBtn = new JButton("Add Group");
		addGroupBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String newGroup = (String) JOptionPane.showInputDialog(
						"Enter some text : ", "");
				if (newGroup != null) {
					groupMap.put(newGroup, null);
					refreshGroupTable();
				}
			}
		});
		removeGroupBtn = new JButton("Remove Group");
		removeGroupBtn.setEnabled(false);
		removeGroupBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Remove selected row
				Object key = groupTbl.getValueAt(groupTbl.getSelectedRow(), 1);
				groupMap.remove(key);
				refreshGroupTable();
			}
		});
		groupPnl.add(groupScp, CC.xyw(2, 2, 3));
		groupPnl.add(addGroupBtn, CC.xy(2, 4));
		groupPnl.add(removeGroupBtn, CC.xy(4, 4));
		samplesPnl.add(groupPnl, CC.xy(2, 2));

		// 1b.) Measurement panel
		JPanel measurementPnl = new JPanel();
		measurementPnl.setLayout(new FormLayout(" 5dlu, p:g, 5dlu,p:g, 5dlu",
				"5dlu, p, 5dlu, p, 5dlu")); // Column, Row
		measurementPnl.setBorder(BorderFactory
				.createTitledBorder("Measurement"));
		measurementTbl = createMeasurementTable();
		JScrollPane measurementScp = new JScrollPane(measurementTbl);
		measurementScp
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		measurementScp.setPreferredSize(new Dimension(100, 100));
		addMeasurementBtn = new JButton("Add Measurement");
		addMeasurementBtn.setEnabled(false);
		addMeasurementBtn.addActionListener(new ActionListener() {

			private JTable experimentTable;

			public void actionPerformed(ActionEvent e) {

				// first get group
				String key = (String) groupTbl.getValueAt(
						groupTbl.getSelectedRow(), 1);
				System.out.println(groupMap.get(key));
				// get via Dialog the experiments
				JPanel chooseExpPnl = new JPanel();
				chooseExpPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p, 5dlu",
						"5dlu, p, 5dlu"));
				projectTbl = createProjectTable();
				projectTbl.getSelectionModel().addListSelectionListener(
						new ListSelectionListener() {

							@Override
							public void valueChanged(ListSelectionEvent e) {
								int selRow = projectTbl.getSelectedRow();
								long projectID = projects.get(selRow)
										.getProjectid();
								try {
									List<Experiment> projectExperiments = projectManager
											.getProjectExperiments(projectID);
									int index = 1;
									for (int i = 0; i < projectExperiments
											.size(); i++) {
										String title = projectExperiments
												.get(i).getTitle();
										((DefaultTableModel) experimentTable
												.getModel())
												.addRow(new Object[] { index++,
														title });
									}
								} catch (SQLException e1) {
									e1.printStackTrace();
								}
							}
						});
				JScrollPane projectsScp = new JScrollPane(projectTbl);
				experimentTable = createExpTable();
				JScrollPane expScp = new JScrollPane(experimentTable);
				chooseExpPnl.add(projectsScp, CC.xy(2, 2));
				chooseExpPnl.add(expScp, CC.xy(4, 2));
				try {
					projects = projectManager.getProjects();
					refreshProjectsTable();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// TODO Project and Experiment Dialog
				int ret = JOptionPane.showConfirmDialog(clientFrame,
						chooseExpPnl, "Choose an Experiment",
						JOptionPane.OK_CANCEL_OPTION);
				if (ret == JOptionPane.YES_OPTION) {

				}
				try {
					projects = projectManager.getProjects();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				// Add Experiments to Dialog

			}

			private void refreshProjectsTable() {
				int index = 1; // row index
				TableConfig.clearTable(projectTbl); // Clears the table
				for (int i = 0; i < projects.size(); i++) {
					Project project = projects.get(i);
					String title = project.getTitle();
					((DefaultTableModel) projectTbl.getModel())
							.addRow(new Object[] { index++, title });
				}

			}
		});
		removeMeasurementBtn = new JButton("Remove Measurement");
		removeMeasurementBtn.setEnabled(false);
		removeMeasurementBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		});
		measurementPnl.add(measurementScp, CC.xyw(2, 2, 3));
		measurementPnl.add(addMeasurementBtn, CC.xy(2, 4));
		measurementPnl.add(removeMeasurementBtn, CC.xy(4, 4));
		samplesPnl.add(measurementPnl, CC.xy(4, 2));

		// 2. Create settings panel
		JPanel settingsPnl = new JPanel();
		settingsPnl.setLayout(new FormLayout("5dlu,p:g, 5dlu",
				"5dlu,p, 5dlu, p ,5dlu")); // Columns, Rows
		JXTitledPanel settingsTtlPnl = new JXTitledPanel("Settings",
				settingsPnl);
		settingsTtlPnl.setBorder(ttlBorder);
		settingsTtlPnl.setTitlePainter(ttlPainter);
		settingsTtlPnl.setTitleFont(ttlFont);
		settingsTtlPnl.setTitleForeground(ttlForeground);

		// 2b. Parameter
		JPanel parameterPnl = new JPanel();
		parameterPnl.setBorder(BorderFactory.createTitledBorder("Parameters"));
		parameterPnl.setLayout(new FormLayout("5dlu,p, 5dlu,p,5dlu",
				"5dlu, p, 5dlu, p, 5dlu, p ,5dlu")); // Column, Rows
		JLabel entryLbl = new JLabel("Entry");
		JComboBox entryCbx = new JComboBox(ENTRIES); // TODO: Move to Constants
		JLabel compareLbl = new JLabel("Compare");
		JComboBox compareCbx = new JComboBox(COMPARE); // TODO: Move to Con
		JLabel groupingLbl = new JLabel("Grouping");
		JComboBox groupingCbx = new JComboBox(GROUPING); // TODO: Move to Con
		parameterPnl.add(entryLbl, CC.xy(2, 2));
		parameterPnl.add(entryCbx, CC.xy(4, 2));
		parameterPnl.add(compareLbl, CC.xy(2, 4));
		parameterPnl.add(compareCbx, CC.xy(4, 4));
		parameterPnl.add(groupingLbl, CC.xy(2, 6));
		parameterPnl.add(groupingCbx, CC.xy(4, 6));
		settingsPnl.add(parameterPnl, CC.xy(2, 2));
		JButton createTabelBtn = new JButton("Create Table");
		createTabelBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
			}
		});
		settingsPnl.add(createTabelBtn, CC.xy(2, 4));

		// 3. Create table
		JPanel comparePnl = new JPanel();
		JXTitledPanel compareTtlPnl = new JXTitledPanel("Compare Table",
				comparePnl);
		compareTtlPnl.setBorder(ttlBorder);
		compareTtlPnl.setTitlePainter(ttlPainter);
		compareTtlPnl.setTitleFont(ttlFont);
		compareTtlPnl.setTitleForeground(ttlForeground);
		JTable compareTbl = new JTable();
		JScrollPane compareScp = new JScrollPane(compareTbl);
		compareScp
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		compareScp.setPreferredSize(new Dimension(1100, 250));
		comparePnl.add(compareScp);

		// add all subPanels
		this.add(samplesTtlPnl, CC.xy(2, 2));
		this.add(settingsTtlPnl, CC.xy(2, 4));
		this.add(compareTtlPnl, CC.xy(2, 6));
	}

	// Write method to fill the table

	/**
	 * Create table for the groups
	 * 
	 * @return Group table
	 */
	private JTable createGroupTable() {
		JTable table = new JTable(new DefaultTableModel() {
			{
				setColumnIdentifiers(new Object[] { "#", "Group" });
			}
		});
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		return table;
	}

	/**
	 * Create table for the measurements
	 * 
	 * @return the measurementTable
	 */
	private JTable createMeasurementTable() {
		JTable table = new JTable(new DefaultTableModel() {
			{
				setColumnIdentifiers(new Object[] { "#", "Measurement" });
			}
		});
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		return table;
	}

	/**
	 * Create table for the experiments
	 * 
	 * @return the experiments Table
	 */
	private JTable createExpTable() {
		JTable table = new JTable(new DefaultTableModel() {
			{
				setColumnIdentifiers(new Object[] { "#", "Experiment" });
			}
		});
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		return table;
	}

	/**
	 * Create table for the projects
	 * 
	 * @return the projects Table
	 */
	private JTable createProjectTable() {
		JTable table = new JTable(new DefaultTableModel() {
			{
				setColumnIdentifiers(new Object[] { "#", "Project" });
			}
		});
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		return table;
	}

	/**
	 * Refresh the group table
	 */
	private void refreshGroupTable() {
		int index = 1; // row index
		TableConfig.clearTable(groupTbl); // Clears the table
		for (Entry<String, List<Experiment>> entry : groupMap.entrySet()) {
			((DefaultTableModel) groupTbl.getModel()).addRow(new Object[] {
					index++, entry.getKey() });
		}
		// Add checks for the remove button and that the group table is selected
		if (groupTbl.getRowCount() == 0) {
			removeGroupBtn.setEnabled(false);
			addMeasurementBtn.setEnabled(false);
		} else {
			groupTbl.getSelectionModel().setSelectionInterval(0, 0);
			removeGroupBtn.setEnabled(true);
			addMeasurementBtn.setEnabled(true);
		}

	}
}
