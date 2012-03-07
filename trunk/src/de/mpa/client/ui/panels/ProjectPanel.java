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
import javax.swing.table.DefaultTableModel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.TableConfig;
import de.mpa.db.accessor.Experiment;
import de.mpa.db.accessor.Project;

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
	
	/**
	 * The project panel constructor initializes the basic components for 
	 * the project configuration.
	 */
	public ProjectPanel(ClientFrame clientFrame) {
		this.clientFrame = clientFrame;
		initComponents();
	}
	
	// Build Panel for project and experiment structure
	private void initComponents() {
		CellConstraints cc = new CellConstraints();
		
		// Layout for the project panel
		projectPnl = this;
		projectPnl.setLayout(new FormLayout("5dlu, p, 10dlu, p, 5dlu", "5dlu, t:p, 5dlu, p, 5dlu, p, 5dlu"));
		
		// Current project panel
		JPanel curProjectPnl = new JPanel();
		curProjectPnl.setBorder(BorderFactory.createTitledBorder("Current Project"));
		curProjectPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p, 5dlu", "5dlu, p, 5dlu"));
		
		// Selected project
		JLabel selProjectLbl = new JLabel("Selected Project: ");		
		JTextField selProjectTtf = new JTextField(15);
		selProjectTtf.setEditable(false);
		selProjectTtf.setText("None");
		
		curProjectPnl.add(selProjectLbl, cc.xy(2,2));
		curProjectPnl.add(selProjectTtf, cc.xy(4,2));
		
		// Setup the table
		setupProjectTable();
		
		List<Project> projectList= null; 
		
		try {
			// Initialize the database connection
			clientFrame.getClient().initDBConnection();
				
			// Updates the project table.
			updateProjectTable(projectList);
			
			// Close the connection
			clientFrame.getClient().closeDBConnection();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			
		// Manage the Projects
		JPanel manageProjectsPnl = new JPanel();
		manageProjectsPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p, 5dlu, p, 5dlu,p, 5dlu", "5dlu, p, 5dlu"));
		JButton newProjectBtn 		= new JButton("New Project");
		JButton openProjectBtn		= new JButton("Open Project");
		JButton modifyProjectBtn 	= new JButton("Modify Project");
		JButton deleteProjectbtn	= new JButton("Delete Project");
		
		//Add buttons to panel
		manageProjectsPnl.add(newProjectBtn,cc.xy(2, 2));
		manageProjectsPnl.add(openProjectBtn,cc.xy(4, 2));
		manageProjectsPnl.add(modifyProjectBtn,cc.xy(6, 2));
		manageProjectsPnl.add(deleteProjectbtn,cc.xy(8, 2));
		
		// add manage Projects to ProjectPnl
		projectPnl.add(manageProjectsPnl,cc.xy(2, 6));
		
		
		
		// TODO: Button to delete a projects
		JButton deleteProjectBtn = new JButton("Delete");
		deleteProjectBtn.setPreferredSize(new Dimension(160,25));
		deleteProjectBtn.setSize(new Dimension(30,30));
		//delete Projects
		deleteProjectBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});

		//Add manageExperiemets to project
		projectPnl.add(curProjectPnl,cc.xy(2, 2));
		projectPnl.add(projectTblScp, cc.xy(2, 4));
		
		
	}

	private void setupProjectTable() {
		// Table for projects
		projectsTbl = new JTable(new DefaultTableModel() { 
					{
						setColumnIdentifiers(new Object[] { "#", "Project", "No. Experiments", "Project Created"});
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
							return String.class;
						case 3:
							return Date.class;
						default:
							return getValueAt(0, col).getClass();
						}
					}
				});
		// TODO: Check for sorting --> Add comparator ? 
		projectsTbl.setAutoCreateRowSorter(true);
		
		// Selection model for the list: Select one entry of the table only
		projectsTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		// Set the first column layout
		TableConfig.packColumn(projectsTbl, 0,5);
		
		projectsTbl.getColumn("Project").setMinWidth(300);
		projectsTbl.getColumn("Project").setMaxWidth(300);
		projectsTbl.getColumn("No. Experiments").setMinWidth(150);
		projectsTbl.getColumn("No. Experiments").setMaxWidth(150);
		projectsTbl.getColumn("Project Created").setMinWidth(200);
		projectsTbl.getColumn("Project Created").setMaxWidth(200);
		
		// Add the project table to scroll pane
		projectTblScp = new JScrollPane(projectsTbl);
		projectTblScp.setPreferredSize(new Dimension(650, 300));
		projectTblScp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
	}
	
	/**
	 * This method updates the project table.
	 * @param projectList The list of projects from the database.
	 * @throws SQLException
	 */
	private void updateProjectTable(List<Project> projectList) throws SQLException {
		// TODO: Use this really ?
		clearProjectTable();
		projectList = new ArrayList<Project>(Project.findAllProjects(clientFrame.getClient().getConnection()));
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
	 * This method clears the project table.
	 */
	private void clearProjectTable(){
		((DefaultTableModel)projectsTbl.getModel()).setRowCount(0);
	}

	
}
