package de.mpa.client.ui.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.painter.Painter;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.model.ExperimentContent;
import de.mpa.client.model.ProjectContent;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.PanelConfig;
import de.mpa.client.ui.TableConfig;
import de.mpa.client.ui.dialogs.DialogType;
import de.mpa.client.ui.dialogs.GeneralDialog;
import de.mpa.client.ui.dialogs.GeneralExceptionHandler;
import de.mpa.db.ProjectManager;
import de.mpa.db.accessor.ExpProperty;
import de.mpa.db.accessor.Experiment;
import de.mpa.db.accessor.Project;
import de.mpa.db.accessor.Property;

public class ProjectPanel extends JPanel {
	
	/**
	 * Parent class client frame.
	 */
	private ClientFrame clientFrame;
	
	private CellConstraints cc;

	private ProjectManager projectManager;
	private ProjectContent currentProjContent;
	private ExperimentContent currentExperimentContent;
	
	private JTextField selProjectTtf;
	private JTextField selExperimentTtf;

	protected JXTable projectTbl;
	
	private JButton addProjectBtn;
	private JButton modifyProjectBtn;
	private JButton deleteProjectBtn;

	protected JXTable experimentTbl;
	
	private JButton addExperimentBtn;
	private JButton modifyExperimentBtn;
	private JButton deleteExperimentBtn;

	
	/**
	 * The project panel constructor initializes the basic components for 
	 * the project configuration.
	 */
	public ProjectPanel(ClientFrame clientFrame) {
		this.clientFrame = clientFrame;
		initComponents();
		initProjectManager();

		try {
			// Initialize the database connection
			clientFrame.getClient().initDBConnection();
				
			// Updates the project table.
			refreshProjectTable();
			
			// Close the connection
			// TODO:	clientFrame.getClient().closeDBConnection();
		} catch (SQLException e) {
			JXErrorPane.showDialog(e);
		}
	}
	
	/**
	 * This method initializes the project manager.
	 */
	private void initProjectManager() {
		try {
			projectManager = new ProjectManager(clientFrame.getClient().getConnection());
		} catch (SQLException e) {
			GeneralExceptionHandler.showSQLErrorDialog(e, clientFrame);
		}
	}

	/**
	 * Initializes the components.
	 */
	private void initComponents() {
		cc = new CellConstraints();
		
		// Layout for the project panel
		this.setLayout(new FormLayout("5dlu, p:g, 10dlu, p:g, 5dlu",
				"5dlu, t:p, 10dlu, p, 10dlu, f:p:g, 5dlu"));
				
		Border ttlBorder = PanelConfig.getTitleBorder();
		Painter ttlPainter = PanelConfig.getTitlePainter();
		Font ttlFont = PanelConfig.getTitleFont();
		Color ttlForeground = PanelConfig.getTitleForeground();
		
		// Current project panel
		JPanel curProjectPnl = new JPanel();
		curProjectPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p:g, 5dlu", "5dlu, p, 5dlu, p, 5dlu"));
		
		// Selected project
		selProjectTtf = new JTextField(15);
		selProjectTtf.setEditable(false);
		selProjectTtf.setText("None");
		
		// Selected experiment
		selExperimentTtf = new JTextField(15);
		selExperimentTtf.setEditable(false);
		selExperimentTtf.setText("None");
		
		curProjectPnl.add(new JLabel("Selected Project:"), cc.xy(2,2));
		curProjectPnl.add(selProjectTtf, cc.xy(4,2));
		
		curProjectPnl.add(new JLabel("Selected Experiment:"), cc.xy(2,4));
		curProjectPnl.add(selExperimentTtf, cc.xy(4,4));
		
		JXTitledPanel curProjTtlPnl = new JXTitledPanel("Current Project", curProjectPnl);
		curProjTtlPnl.setBorder(ttlBorder);
		curProjTtlPnl.setTitlePainter(ttlPainter);
		curProjTtlPnl.setTitleFont(ttlFont);
		curProjTtlPnl.setTitleForeground(ttlForeground);
		
		// Setup the table
		JScrollPane projectTblScp = setupProjectTable();
		
		// Setup the project management buttons.
		JPanel projectBtnPnl = setupProjectButtonPnl();
		
		JPanel projectPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p, 5dlu, p, 5dlu"));
		projectPnl.add(projectTblScp, cc.xy(2, 2));
		projectPnl.add(projectBtnPnl, cc.xy(2, 4));
		
		JXTitledPanel projTtlPnl = new JXTitledPanel("Project Viewer", projectPnl);
		projTtlPnl.setBorder(ttlBorder);
		projTtlPnl.setTitlePainter(ttlPainter);
		projTtlPnl.setTitleFont(ttlFont);
		projTtlPnl.setTitleForeground(ttlForeground);
		
		// Experiment table
		JScrollPane experimentTblScp = setupExperimentTable();

		// Setup the experiment management buttons.
		JPanel experimentBtnPnl = setupExperimentButtonPnl();
		
		JPanel experimentPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p, 5dlu, p, 5dlu"));
		experimentPnl.add(experimentTblScp, cc.xy(2, 2));
		experimentPnl.add(experimentBtnPnl, cc.xy(2, 4));
		
		JXTitledPanel expTtlPnl = new JXTitledPanel("Experiment Viewer", experimentPnl);
		expTtlPnl.setBorder(ttlBorder);
		expTtlPnl.setTitlePainter(ttlPainter);
		expTtlPnl.setTitleFont(ttlFont);
		expTtlPnl.setTitleForeground(ttlForeground);
		
		// Next button
		JPanel nextPnl = new JPanel(new FormLayout("r:p:g", "b:p:g"));
		
		JButton nextBtn = new JButton("Next",
				new ImageIcon(getClass().getResource("/de/mpa/resources/icons/next.png")));
		nextBtn.setHorizontalTextPosition(SwingConstants.LEFT);
		nextBtn.setFont(nextBtn.getFont().deriveFont(
				Font.BOLD, nextBtn.getFont().getSize2D()*1.25f));
		nextBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				clientFrame.getTabPane().setSelectedIndex(1);
			}
		});
		
		nextPnl.add(nextBtn, cc.xy(1,1));

		this.add(curProjTtlPnl, cc.xy(2, 2));
		this.add(projTtlPnl, cc.xy(2, 4));
		this.add(expTtlPnl, cc.xy(4, 4));
		this.add(nextPnl, cc.xy(4, 6));
	}

	/**
	 * This method sets up the project management buttons.
	 */
	private JPanel setupProjectButtonPnl() {
		
		// Manage the Projects
		JPanel manageProjectsPnl = new JPanel();
		manageProjectsPnl.setLayout(new FormLayout("p:g, 5dlu, p:g, 5dlu, p:g", "5dlu, p, 5dlu"));
		
		addProjectBtn = new JButton("New Project");
		addProjectBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GeneralDialog projDlg = new GeneralDialog("New Project", clientFrame, DialogType.NEW_PROJECT);
				Long projectID = projDlg.start();
				refreshProjectTable(projectID);
			}
		});
		
		modifyProjectBtn = new JButton("View/Edit Details");
		modifyProjectBtn.setEnabled(false);
		modifyProjectBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (currentProjContent != null) {
					GeneralDialog expDlg = new GeneralDialog("Modify Project", clientFrame, DialogType.MODIFY_PROJECT, currentProjContent);
					Long projectID = expDlg.start();
					refreshProjectTable(projectID);
				}
			}
		});
		
		deleteProjectBtn = new JButton("Delete Project");
		deleteProjectBtn.setEnabled(false);
		// Delete experiment
		deleteProjectBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int choice = JOptionPane.showConfirmDialog(clientFrame, "<html>Are you sure you want to delete the selected project?<br>Changes are irreversible.</html>", "Delete Project", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

				if (choice == JOptionPane.OK_OPTION) {
					try {
						projectManager.revalidate(clientFrame.getClient().getConnection());
						projectManager.deleteProject(currentProjContent.getProjectid());
						refreshProjectTable();
						TableConfig.clearTable(experimentTbl);

						// Disable buttons
						modifyProjectBtn.setEnabled(false);
						deleteProjectBtn.setEnabled(false);
						addExperimentBtn.setEnabled(false);
						modifyExperimentBtn.setEnabled(false);
						deleteExperimentBtn.setEnabled(false);

						// Reset textfields
						selProjectTtf.setText("None");
						selExperimentTtf.setText("None");
						clientFrame.getStatusBar().getProjectTextField().setText("None");
						clientFrame.getStatusBar().getExperimentTextField().setText("None");

					} catch (Exception e2) {
						e2.printStackTrace();
						
					}
				}
			}
		});
		
		//Add buttons to panel
		manageProjectsPnl.add(addProjectBtn,cc.xy(1, 2));
		manageProjectsPnl.add(modifyProjectBtn,cc.xy(3, 2));
		manageProjectsPnl.add(deleteProjectBtn,cc.xy(5, 2));
		
		return manageProjectsPnl;
	}
	
	/**
	 * This method sets up the experiment management buttons.
	 */
	private JPanel setupExperimentButtonPnl() {
		
		// Manage the Projects
		JPanel manageExperimentsPnl = new JPanel();
		manageExperimentsPnl.setLayout(new FormLayout("p:g, 5dlu, p:g, 5dlu, p:g", "5dlu, p, 5dlu"));
		addExperimentBtn = new JButton("Add Experiment");
		addExperimentBtn.setEnabled(false);
		addExperimentBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {				
				GeneralDialog expDlg = new GeneralDialog("New Experiment", clientFrame, DialogType.NEW_EXPERIMENT, currentProjContent);
				Long experimentID = expDlg.start();
				if (experimentID > 0L) {
					for (int row = 0; row < experimentTbl.getRowCount(); row++) {
						if (experimentTbl.getValueAt(row, 0) == experimentID) {
							experimentTbl.getSelectionModel().setSelectionInterval(row, row);
							break;
						}
					}
				}
			}
		});
		modifyExperimentBtn = new JButton("View/Edit Details");
		modifyExperimentBtn.setEnabled(false);
		modifyExperimentBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				GeneralDialog expDlg = new GeneralDialog("Modify Experiment", clientFrame, DialogType.MODIFY_EXPERIMENT, currentExperimentContent);
				expDlg.start();
				refreshExperimentTable(currentExperimentContent.getProjectID(), currentExperimentContent.getExperimentID());
			}
		});
		deleteExperimentBtn = new JButton("Delete Experiment");
		deleteExperimentBtn.setEnabled(false);
		// Delete experiment
		deleteExperimentBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int choice = JOptionPane.showConfirmDialog(clientFrame, "Are you sure you want to delete the selected experiment? Changes are irreversible.", "Delete Experiment", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			
				if (choice == JOptionPane.OK_OPTION) {
					try {
						projectManager.deleteExperiment(currentExperimentContent.getExperimentID());
						refreshExperimentTable(currentExperimentContent.getProjectID());
						
						// Disable buttons
						modifyExperimentBtn.setEnabled(false);
						deleteExperimentBtn.setEnabled(false);

						// Reset textfields
						selExperimentTtf.setText("None");
						clientFrame.getStatusBar().getExperimentTextField().setText("None");
						
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		});
		
		//Add buttons to panel
		manageExperimentsPnl.add(addExperimentBtn,cc.xy(1, 2));
		manageExperimentsPnl.add(modifyExperimentBtn,cc.xy(3, 2));
		manageExperimentsPnl.add(deleteExperimentBtn,cc.xy(5, 2));
		
		return manageExperimentsPnl;
	}
	
	/**
	 * Method for creating the project table.
	 * @return 
	 */
	private JScrollPane setupProjectTable() {
		// Table for projects
		projectTbl = new JXTable(new DefaultTableModel() { 
					{
						setColumnIdentifiers(new Object[] { "#", "Project Title", "Creation Date"});
					}

					public boolean isCellEditable(int row, int col) {
						return false;
					}

					public Class<?> getColumnClass(int col) {
						switch (col) {
						case 0:
							return Integer.class;
						case 1:
							return String.class;
						case 2:
							return Date.class;
						default:
							return getValueAt(0, col).getClass();
						}
					}
				});
		projectTbl.setColumnControlVisible(true);
		
		// Selection model for the list: Select one entry of the table only
		projectTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		// Enables some relevant buttons.
		projectTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent evt) {
				// This stuff happens when the project table is being selected.
				try {
					queryProjectTableMouseClicked(evt);
				} catch (SQLException e) {
					GeneralExceptionHandler.showSQLErrorDialog(e, clientFrame);
					e.printStackTrace();
				}
			}
		});
		
		// Set the column layout
		TableConfig.setColumnWidths(projectTbl, new double[] { 1, 10, 4 });

		// Add nice striping effect
		projectTbl.addHighlighter(TableConfig.getSimpleStriping());
		
		// Add the project table to scroll pane
		JScrollPane projectTblScp = new JScrollPane(projectTbl);
		projectTblScp.setPreferredSize(new Dimension(450, 350));
		projectTblScp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		return projectTblScp;
	}
	
	/**
	 * Method for creating the experiment table.
	 */
	private JScrollPane setupExperimentTable() {
		// Table for projects
		experimentTbl = new JXTable(new DefaultTableModel() { 
					{
						setColumnIdentifiers(new Object[] { "#", "Experiment Title", "Creation Date"});
					}

					public boolean isCellEditable(int row, int col) {
						return false;
					}

					public Class<?> getColumnClass(int col) {
						switch (col) {
						case 0:
							return Integer.class;
						case 1:
							return String.class;
						case 2:
							return Date.class;
						default:
							return getValueAt(0, col).getClass();
						}
					}
				});
		experimentTbl.setColumnControlVisible(true);
		
		// Selection model for the list: Select one entry of the table only
		experimentTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		// Add Selection Listener
		experimentTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent evt) {
				try {
					queryExperimentTableMouseClicked(evt);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		});

		// Set the column layout
		TableConfig.setColumnWidths(experimentTbl, new double[] { 1, 10, 4 });

		// Add nice striping effect
		experimentTbl.addHighlighter(TableConfig.getSimpleStriping());
		
		// Add the project table to scroll pane
		JScrollPane experimentTblScp = new JScrollPane(experimentTbl);
		experimentTblScp.setPreferredSize(new Dimension(450, 350));
		experimentTblScp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		return experimentTblScp;
	}

	/**
	 * This method updates the project table.
	 * @throws SQLException
	 */
	public void refreshProjectTable() {
		refreshProjectTable(0L);
	}
	
	/**
	 * This method updates the project table and re-selects a specific project.
	 * @param projectID 
	 * @throws SQLException
	 */
	public void refreshProjectTable(Long projectID) {
		try {
			TableConfig.clearTable(projectTbl);
			ArrayList<Project> projectList;
			projectList = new ArrayList<Project>(Project.findAllProjects(clientFrame.getClient().getConnection()));
			for (int i = 0; i < projectList.size(); i++) {
				Project project = projectList.get(i);
				((DefaultTableModel) projectTbl.getModel()).addRow(new Object[] {
						project.getProjectid(),
						project.getTitle(),
						project.getCreationdate() });
			}
			// re-select row containing project id
			if (projectID > 0) {
				for (int row = 0; row < projectTbl.getRowCount(); row++) {
					if (projectTbl.getValueAt(row, 0) == projectID) {
						projectTbl.getSelectionModel().setSelectionInterval(row, row);
						break;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method updates the experiment table.
	 * @param projectID
	 */
	public void refreshExperimentTable(Long projectID) {
		refreshExperimentTable(projectID, 0L);
	}
	
	/**
	 * This method updates the experiment table and re-selects a specific experiment.
	 * @param projectID
	 * @param experimentID
	 */
	public void refreshExperimentTable(Long projectID, Long experimentID) {
		try {
			projectManager.revalidate(clientFrame.getClient().getConnection());
			
			List<Experiment> experiments = projectManager.getProjectExperiments(projectID);
			List<Property> properties = projectManager.getProjectProperties(projectID);
			
			// Fill the current project.
			currentProjContent  = new ProjectContent(projectID, properties, experiments);
			String projectTitle = projectManager.getProjectTitle(projectID);
			selProjectTtf.setText(projectTitle);
			clientFrame.getStatusBar().getProjectTextField().setText(projectTitle);
			
			currentProjContent.setProjectTitle(projectTitle); 

			// Clear the experiment table
			TableConfig.clearTable(experimentTbl);
			
			// Fill the experiment table with the values retrieved from the database.
			for (Experiment experiment : experiments) {
				((DefaultTableModel) experimentTbl.getModel()).addRow(new Object[] {
						experiment.getExperimentid(),
						experiment.getTitle(),
						experiment.getCreationdate()
						});
			}
			// re-select row containing experiment id
			if (experimentID > 0) {
				for (int row = 0; row < experimentTbl.getRowCount(); row++) {
					if (experimentTbl.getValueAt(row, 0) == experimentID) {
						experimentTbl.getSelectionModel().setSelectionInterval(row, row);
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 *  This method retrieves the experiments from the database for an selected project.
	 * @throws SQLException 
	 */
	private void queryProjectTableMouseClicked(ListSelectionEvent evt) throws SQLException {
		// Get the selected row
		int selRow = projectTbl.getSelectedRow();
		
		// Condition if one row is selected.
		if (selRow != -1) {
			refreshExperimentTable((Long) projectTbl.getValueAt(selRow, 0));
		}
		selExperimentTtf.setText("None");
		clientFrame.getStatusBar().getExperimentTextField().setText("None");
		
		// Enables to change the entries
		modifyProjectBtn.setEnabled(true);
		deleteProjectBtn.setEnabled(true);
		addExperimentBtn.setEnabled(true);
	}
	
	/**
	 *  This method retrieves the experiments from the database for an selected project.
	 * @throws SQLException 
	 */
	private void queryExperimentTableMouseClicked(ListSelectionEvent evt) throws SQLException {
		
		fillCurrentExperimentContent();
		selExperimentTtf.setText(currentExperimentContent.getExperimentTitle());
		clientFrame.getStatusBar().getExperimentTextField().setText(currentExperimentContent.getExperimentTitle());
		clientFrame.getDbSearchResultPnl().setResultsBtnEnabled(true);
		// Enable buttons
		modifyExperimentBtn.setEnabled(true);
		deleteExperimentBtn.setEnabled(true);
	}
	
	

	
	/**
	 * This method gets the selected experiment and retrieves the actual content via table
	 * and database interaction.
	 * @throws SQLException 
	 * 
	 */
	private void fillCurrentExperimentContent() throws SQLException {
		// Get the selected row
		int selRow = experimentTbl.getSelectedRow();
		// Condition if one row is selected.
		if (selRow != -1) {
			long projectid = currentProjContent.getProjectid();
			long experimentid = (Long) experimentTbl.getValueAt(selRow, 0);

			projectManager.revalidate(clientFrame.getClient().getConnection());
			
			Experiment experiment = projectManager.getProjectExperiment(projectid, experimentid);
			List<ExpProperty> expProperties = projectManager.getExperimentProperties(experiment.getExperimentid());
			currentExperimentContent = new ExperimentContent(projectid, experiment.getExperimentid(), expProperties);
			currentExperimentContent.setExperimentTitle(experiment.getTitle());
		} 
	}
	
	/**
	 * Returns the id of the current (user-selected) experiment.
	 * @return The experiment id of an selected experiment.
	 */
	public long getCurrentExperimentId(){
		return currentExperimentContent.getExperimentID();
	}

	/**
	 * Returns the project content
	 * @return The project content 
	 */
	public ProjectContent getCurrentProjectContent() {
		return currentProjContent;
	}

	/**
	 * Returns the experiment content
	 * @return The experiment content
	 */
	public ExperimentContent getCurrentExperimentContent() {
		return currentExperimentContent;
	}
	
	
}
