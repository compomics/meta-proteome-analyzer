package de.mpa.client.ui.panels;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.model.ExperimentContent;
import de.mpa.client.model.ProjectContent;
import de.mpa.client.ui.ClientFrame;
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
		projectPnl.setLayout(new FormLayout("5dlu, p:g, 10dlu, p:g, 5dlu",
				"5dlu, t:p, 5dlu, p, 5dlu, p, 5dlu, f:p:g, 5dlu"));
		
		// Current project panel
		JPanel curProjectPnl = new JPanel();
		curProjectPnl.setBorder(BorderFactory.createTitledBorder("Current Project"));
		curProjectPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p:g, 5dlu", "5dlu, p, 5dlu, p, 5dlu"));
		
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
		
		// Next button, please
		JPanel nextPnl = new JPanel(new FormLayout("r:p:g", "b:p:g"));
		
		ImageIcon nextIcon = new ImageIcon(getClass().getResource("/de/mpa/resources/icons/next.png"));
		JButton nextBtn = new JButton("Next", nextIcon);
		nextBtn.setHorizontalTextPosition(SwingConstants.LEFT);
		nextBtn.setFont(nextBtn.getFont().deriveFont(
				Font.BOLD, nextBtn.getFont().getSize2D()*1.0f));
		nextBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				clientFrame.getTabPane().setSelectedIndex(1);
			}
		});
		
		nextPnl.add(nextBtn, cc.xy(1,1));
		
		projectPnl.add(experimentTblScp, cc.xy(4, 4));
		projectPnl.add(manageExperimentsPnl,cc.xy(4, 6));
		projectPnl.add(nextPnl, cc.xy(4,8));
	}

	/**
	 * This method sets up the project management buttons.
	 */
	private void setupProjectButtonPnl() {
		
		// Manage the Projects
		manageProjectsPnl = new JPanel();
		manageProjectsPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu, p:g, 5dlu, p:g, 5dlu", "5dlu, p, 5dlu"));
		
		JButton newProjectBtn = new JButton("New Project");
		newProjectBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GeneralDialog projDlg = new GeneralDialog("New Project", clientFrame, DialogType.NEW_PROJECT);
				Long projectID = projDlg.start();
				refreshProjectTable(projectID);
			}
		});
		
		modifyProjectBtn 	= new JButton("Modify Project");
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
		
		deleteProjectbtn	= new JButton("Delete Project");
		deleteProjectbtn.setEnabled(false);
		// Delete experiment
		deleteProjectbtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int choice = JOptionPane.showConfirmDialog(clientFrame, "<html>Are you sure you want to delete the selected project?<br>Changes are irreversible.</html>", "Delete Project", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

				if (choice == JOptionPane.OK_OPTION) {
					try {
						ProjectManager manager = new ProjectManager(clientFrame.getClient().getConnection());
						manager.deleteProject(currentProjContent.getProjectid());
						refreshProjectTable();
						clearExperimentTable();

						// Disable buttons
						modifyProjectBtn.setEnabled(false);
						deleteProjectbtn.setEnabled(false);
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
		manageProjectsPnl.add(newProjectBtn,cc.xy(2, 2));
		manageProjectsPnl.add(modifyProjectBtn,cc.xy(4, 2));
		manageProjectsPnl.add(deleteProjectbtn,cc.xy(6, 2));
	}
	
	/**
	 * This method sets up the experiment management buttons.
	 */
	private void setupExperimentButtonPnl() {
		
		// Manage the Projects
		manageExperimentsPnl = new JPanel();
		manageExperimentsPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu, p:g, 5dlu, p:g, 5dlu", "5dlu, p, 5dlu"));
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
		modifyExperimentBtn = new JButton("Modify Experiment");
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
						ProjectManager manager = new ProjectManager(clientFrame.getClient().getConnection());
						manager.deleteExperiment(currentExperimentContent.getExperimentID());
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
			clearProjectTable();
			ArrayList<Project> projectList;
			projectList = new ArrayList<Project>(Project.findAllProjects(clientFrame.getClient().getConnection()));
			for (int i = 0; i < projectList.size(); i++) {
				Project project = projectList.get(i);
				((DefaultTableModel) projectsTbl.getModel()).addRow(new Object[] {
						project.getProjectid(),
						project.getTitle(),
						project.getCreationdate() });
			}
			// re-select row containing project id
			if (projectID > 0) {
				for (int row = 0; row < projectsTbl.getRowCount(); row++) {
					if (projectsTbl.getValueAt(row, 0) == projectID) {
						projectsTbl.getSelectionModel().setSelectionInterval(row, row);
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
			List<Experiment> experiments = projectManager.getProjectExperiments(projectID);
			//TODO: move this construction in a second method outside
			List<Property> properties = projectManager.getProjectProperties(projectID);
			
			// Fill the current project.
			currentProjContent  = new ProjectContent(projectID, properties, experiments);
			String projectTitle = projectManager.getProjectTitle(projectID);
			selProjectTtf.setText(projectTitle);
			clientFrame.getStatusBar().getProjectTextField().setText(projectTitle);
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
		int selRow = projectsTbl.getSelectedRow();
		
		// Condition if one row is selected.
		if (selRow != -1) {
			refreshExperimentTable((Long) projectsTbl.getValueAt(selRow, 0));
		}
		selExperimentTtf.setText("None");
		clientFrame.getStatusBar().getExperimentTextField().setText("None");
		
		// Enables to change the entries
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
		selExperimentTtf.setText(currentExperimentContent.getExperimentTitle());
		clientFrame.getStatusBar().getExperimentTextField().setText(currentExperimentContent.getExperimentTitle());
		
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
	private void fillCurrentExperimentContent() throws SQLException {
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
