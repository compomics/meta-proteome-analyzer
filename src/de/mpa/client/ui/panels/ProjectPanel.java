package de.mpa.client.ui.panels;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.model.ExperimentContent;
import de.mpa.client.model.ProjectContent;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.ScreenConfig;
import de.mpa.client.ui.TableConfig;
import de.mpa.client.ui.dialogs.DialogType;
import de.mpa.client.ui.dialogs.GeneralDialog;
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
	
	/**
	 * The projects table containing the projects. 
	 */
	private JTable projectsTbl;
	
	/**
	 * The project panel.
	 */
	private ProjectPanel projectPnl;

	private JScrollPane projectTblScp;

	private JTable experimentTbl;

	private JScrollPane experimentTblScp;

	private CellConstraints cc;

	private JPanel manageProjectsPnl;

	private JPanel manageExperimentsPnl;

	private JButton openProjectBtn;

	private JButton modifyProjectBtn;

	private JButton deleteProjectbtn;

	private JButton addExperimentBtn;

	private ProjectManager projectManager;

	private JButton modifyExperimentBtn;

	private JButton deleteExperimentBtn;
	
	private ProjectContent currentProjContent;

	private JTextField selProjectTtf;

	private JTextField selExperimentTtf;
	
	private ExperimentContent currentExperimentContent; 
	
	/**
	 * The project panel constructor initializes the basic components for 
	 * the project configuration.
	 */
	public ProjectPanel(ClientFrame clientFrame) {
		this.clientFrame = clientFrame;
		initComponents();
		initProjectManager();
	}
	
	/**
	 * This method initializes the project manager.
	 */
	private void initProjectManager() {
		try {
			projectManager = new ProjectManager(clientFrame.getClient().getConnection());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initializes the components.
	 */
	private void initComponents() {
		cc = new CellConstraints();
		
		// Layout for the project panel
		projectPnl = this;
		projectPnl.setLayout(new FormLayout("5dlu, p, 10dlu, p, 5dlu", "5dlu, t:p, 5dlu, p, 5dlu, p, 5dlu"));
		
		// Current project panel
		JPanel curProjectPnl = new JPanel();
		curProjectPnl.setBorder(BorderFactory.createTitledBorder("Current Project"));
		curProjectPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p, 5dlu", "5dlu, p, 5dlu, p, 5dlu"));
		
		// Selected project
		JLabel selProjectLbl = new JLabel("Selected Project: ");		
		selProjectTtf = new JTextField(15);
		selProjectTtf.setEditable(false);
		selProjectTtf.setText("None");
		
		// Selected experiment
		JLabel selExperimentLbl = new JLabel("Selected Experiment: ");		
		selExperimentTtf = new JTextField(15);
		selExperimentTtf.setEditable(false);
		selExperimentTtf.setText("None");
		
		curProjectPnl.add(selProjectLbl, cc.xy(2,2));
		curProjectPnl.add(selProjectTtf, cc.xy(4,2));
		
		curProjectPnl.add(selExperimentLbl, cc.xy(2,4));
		curProjectPnl.add(selExperimentTtf, cc.xy(4,4));
		
		// Setup the table
		setupProjectTable();

		try {
			// Initialize the database connection
			clientFrame.getClient().initDBConnection();
				
			// Updates the project table.
			refreshProjectTable();
			
			// Close the connection
			clientFrame.getClient().closeDBConnection();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		// Setup the project management buttons.
		setupProjectButtonPnl();

		// Header panel
		projectPnl.add(curProjectPnl,cc.xy(2, 2));
		
		// Projects table
		projectPnl.add(projectTblScp, cc.xy(2, 4));
		projectPnl.add(manageProjectsPnl,cc.xy(2, 6));
		
		// Setup the experiment management buttons.
		setupExperimentButtonPnl();
		
		// Experiment table
		setupExperimentTable();
		
		projectPnl.add(experimentTblScp, cc.xy(4, 4));
		projectPnl.add(manageExperimentsPnl,cc.xy(4, 6));
	}

	/**
	 * This method sets up the project management buttons.
	 */
	private void setupProjectButtonPnl() {
		
		// Manage the Projects
		manageProjectsPnl = new JPanel();
		manageProjectsPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p, 5dlu, p, 5dlu,p, 5dlu", "5dlu, p, 5dlu"));
		JButton newProjectBtn 		= new JButton("New Project");
		newProjectBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				GeneralDialog projDlg = new GeneralDialog("New Project", clientFrame, DialogType.NEW_PROJECT);
				ScreenConfig.centerInScreen(projDlg);
				projDlg.setVisible(true);
			}
		});
		// TODO: Add action listeners!
		openProjectBtn		= new JButton("Open Project");
		openProjectBtn.setEnabled(false);
		modifyProjectBtn 	= new JButton("Modify Project");
		modifyProjectBtn.setEnabled(false);
		modifyProjectBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (currentProjContent!=null){
					GeneralDialog projDlg = new GeneralDialog("Modify Project", clientFrame, DialogType.MODIFY_PROJECT, currentProjContent);
					ScreenConfig.centerInScreen(projDlg);
					projDlg.setVisible(true);
					try {
						refreshProjectTable();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		deleteProjectbtn	= new JButton("Delete Project");
		deleteProjectbtn.setEnabled(false);
		
		//Add buttons to panel
		manageProjectsPnl.add(newProjectBtn,cc.xy(2, 2));
		manageProjectsPnl.add(openProjectBtn,cc.xy(4, 2));
		manageProjectsPnl.add(modifyProjectBtn,cc.xy(6, 2));
		manageProjectsPnl.add(deleteProjectbtn,cc.xy(8, 2));
	}
	
	/**
	 * This method sets up the experiment management buttons.
	 */
	private void setupExperimentButtonPnl() {
		
		// Manage the Projects
		manageExperimentsPnl = new JPanel();
		manageExperimentsPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p, 5dlu, p, 5dlu", "5dlu, p, 5dlu"));
		addExperimentBtn = new JButton("Add Experiment");
		addExperimentBtn.setEnabled(false);
		addExperimentBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				GeneralDialog projDlg = new GeneralDialog("New Experiment", clientFrame, DialogType.NEW_EXPERIMENT, currentProjContent);
				ScreenConfig.centerInScreen(projDlg);
				projDlg.setVisible(true);
			}
		});
		modifyExperimentBtn = new JButton("Modify Experiment");
		modifyExperimentBtn.setEnabled(false);
		modifyExperimentBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				GeneralDialog projDlg = new GeneralDialog("Modify Experiment", clientFrame, DialogType.MODIFY_EXPERIMENT, currentExperimentContent);
				ScreenConfig.centerInScreen(projDlg);
				projDlg.setVisible(true);
			}
		});
		deleteExperimentBtn = new JButton("Delete Experiment");
		deleteExperimentBtn.setEnabled(false);
		
		//Add buttons to panel
		manageExperimentsPnl.add(addExperimentBtn,cc.xy(2, 2));
		manageExperimentsPnl.add(modifyExperimentBtn,cc.xy(4, 2));
		manageExperimentsPnl.add(deleteExperimentBtn,cc.xy(6, 2));
	}
	
	/**
	 * Method for creating the project table.
	 */
	private void setupProjectTable() {
		// Table for projects
		projectsTbl = new JTable(new DefaultTableModel() { 
					{
						setColumnIdentifiers(new Object[] { "#", "Project", "Project Created"});
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
		projectsTbl.setAutoCreateRowSorter(true);
		
		// Selection model for the list: Select one entry of the table only
		projectsTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		// Enables some relevant buttons.
		projectsTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent evt) {
				// This stuff happens when the project table is being selected.
				try {
					
					queryProjectTableMouseClicked(evt);
				} catch (SQLException e) {
					// TODO: Add user dialog for failure in database connection. 
					e.printStackTrace();
				}

			}
		});
		
		// Set the first column layout
		TableConfig.packColumn(projectsTbl, 0,5);
		
		projectsTbl.getColumn("Project").setMinWidth(250);
		projectsTbl.getColumn("Project").setMaxWidth(250);
		projectsTbl.getColumn("Project Created").setMinWidth(150);
		projectsTbl.getColumn("Project Created").setMaxWidth(150);
		
		// Add the project table to scroll pane
		projectTblScp = new JScrollPane(projectsTbl);
		projectTblScp.setPreferredSize(new Dimension(450, 350));
		projectTblScp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	}
	
	/**
	 * Method for creating the experiment table.
	 */
	private void setupExperimentTable() {
		// Table for projects
		experimentTbl = new JTable(new DefaultTableModel() { 
					{
						setColumnIdentifiers(new Object[] { "#", "Experiment", "Experiment Created"});
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
		experimentTbl.setAutoCreateRowSorter(true);
		
		// Selection model for the list: Select one entry of the table only
		experimentTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		// Add Selection Listener
		experimentTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent evt) {
				try {
					queryExperimentTableMouseClicked(evt);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		// Columns layout
		experimentTbl.getColumn("#").setMinWidth(30);
		experimentTbl.getColumn("#").setMaxWidth(30);
		experimentTbl.getColumn("Experiment").setMinWidth(250);
		experimentTbl.getColumn("Experiment").setMaxWidth(250);
		experimentTbl.getColumn("Experiment Created").setMinWidth(150);
		experimentTbl.getColumn("Experiment Created").setMaxWidth(150);
		
		// Add the project table to scroll pane
		experimentTblScp = new JScrollPane(experimentTbl);
		experimentTblScp.setPreferredSize(new Dimension(450, 350));
		experimentTblScp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	}
	
	/**
	 * This method updates the project table.
	 * @param projectList The list of projects from the database.
	 * @throws SQLException
	 */
	public void refreshProjectTable() throws SQLException {
		clearProjectTable();
		ArrayList<Project> projectList = new ArrayList<Project>(Project.findAllProjects(clientFrame.getClient().getConnection()));
		for (int i = 0; i < projectList.size(); i++) {
			Project project = projectList.get(i);
			int nExperiments = Experiment.findAllExperimentsOfProject(project.getProjectid(), clientFrame.getClient().getConnection()).size();
			((DefaultTableModel) projectsTbl.getModel()).addRow(new Object[] {
					project.getProjectid(),
					project.getTitle(),
					nExperiments,
					project.getCreationdate() });
		}
	}
	
	/**
	 * This method updates the experiment table.
	 * @throws SQLException 
	 */
	public void updateExperimentTable(Long projectid) throws SQLException{
		List<Experiment> experiments = projectManager.getProjectExperiments(projectid);
		//TODO: move this construction in a second method outside
		List<Property> properties = projectManager.getProjectProperties(projectid);
		
		// Fill the current project.
		currentProjContent  = new ProjectContent(projectid, properties, experiments);
		String projectTitle = projectManager.getProjectTitle(projectid);
		selProjectTtf.setText(projectTitle);
		currentProjContent.setProjectTitle(projectTitle); 

		// Clear the experiment table
		clearExperimentTable();
		
		// Fill the experiment table with the values retrieved from the database.
		for (Experiment experiment : experiments) {
			((DefaultTableModel) experimentTbl.getModel()).addRow(new Object[] {
					experiment.getExperimentid(),
					experiment.getTitle(),
					experiment.getCreationdate()
					});
		}
	}
	
	/**
	 *  This method retrieves the experiments from the database for an selected project.
	 * @throws SQLException 
	 */
	private void queryProjectTableMouseClicked(ListSelectionEvent evt) throws SQLException {
		// Get the selected row
		int selRow = projectsTbl.getSelectedRow();
		
		// Condition if one row is selected.
		if (selRow != -1) {
			updateExperimentTable((Long) projectsTbl.getValueAt(selRow, 0));
		} 
		
		// Enables to change the entries
		openProjectBtn.setEnabled(true);
		modifyProjectBtn.setEnabled(true);
		deleteProjectbtn.setEnabled(true);
		addExperimentBtn.setEnabled(true);
	}
	
	/**
	 *  This method retrieves the experiments from the database for an selected project.
	 * @throws SQLException 
	 */
	private void queryExperimentTableMouseClicked(ListSelectionEvent evt) throws SQLException {
		fillCurrentExperimentContent();
		
		// Enable buttons
		modifyExperimentBtn.setEnabled(true);
		deleteExperimentBtn.setEnabled(true);
	}
	
	/**
	 * This method clears the project table.
	 * TODO: Add this to the TableConfig class and make the method static. :)
	 */
	private void clearProjectTable(){
		((DefaultTableModel)projectsTbl.getModel()).setRowCount(0);
	}
	
	/**
	 * This method clears the project table.
	 * TODO: Add this to the TableConfig class and make the method static. :)
	 */
	private void clearExperimentTable(){
		((DefaultTableModel) experimentTbl.getModel()).setRowCount(0);
	}
	
	/**
	 * This method gets the selected experiment and retrieves the actual content via table
	 * and database interaction.
	 * @throws SQLException 
	 * 
	 */
	private void fillCurrentExperimentContent() throws SQLException{
		// Get the selected row
		int selRow = experimentTbl.getSelectedRow();
		// Condition if one row is selected.
		if (selRow != -1) {
			long projectid = currentProjContent.getProjectid();
			long experimentid = (Long) experimentTbl.getValueAt(selRow, 0);
			Experiment experiment = projectManager.getProjectExperiment(projectid, experimentid);
			List<ExpProperty> expProperties = projectManager.getExperimentProperties(experiment.getExperimentid());
			currentExperimentContent = new ExperimentContent(projectid, experiment.getExperimentid(), expProperties);
			currentExperimentContent.setExperimentTitle(experiment.getTitle());
		} 
	}
	
}
