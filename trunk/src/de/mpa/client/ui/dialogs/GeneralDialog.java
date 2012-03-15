package de.mpa.client.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.model.ExperimentContent;
import de.mpa.client.model.ProjectContent;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.ScreenConfig;
import de.mpa.db.ProjectManager;
import de.mpa.db.accessor.ExpProperty;
import de.mpa.db.accessor.Property;

public class GeneralDialog extends JDialog {
	
	private long id = 0L;
	
	// Project property table
	private JTable propertyTbl;
	
	// Textfield with the name of the property 
	private JTextField propNameTtf;
	
	// Textfield with the value of the property
	private JTextField propValueTtf;

	// Button to add project property
	private JButton addPropertyBtn;

	// Button to change a project property
	private JButton changePropertyBtn;
	
	// Button to delete project property
	private JButton deletePropertyBtn;
	
	// Button to save and leave project property table
	private JButton saveBtn;
			
	// Parent client frame.
	private ClientFrame parent;

	private JTextField nameTtf;

	private DialogType type;
	
	// List for all properties.
	private Map<String, String> properties = new LinkedHashMap<String, String>();
	
	private ProjectContent currentProjContent;
	
	private ExperimentContent currentExpContent;
	
	private ArrayList<Operation> operations = new ArrayList<Operation>();
	
	/**
	 * Declares the ProjectDialog as child of the JDialog, being a component of
	 * the ClientFrame
	 * 
	 * @param title
	 * @param parent
	 */
	public GeneralDialog(String title, ClientFrame parent, DialogType type) {
		this(title, parent, type, null);
	}
	
	/**
	 * Calling the main constructor adding projects table
	 * @param title
	 * @param parent
	 * @param type
	 * @param content
	 */
	public GeneralDialog(String title, ClientFrame parent, DialogType type, Object content) {
		super(parent, title, true);
		this.parent = parent;
		this.type = type;
		if (content instanceof ProjectContent) {
			this.currentProjContent = (ProjectContent) content;
		} else if (content instanceof ExperimentContent) {
			this.currentExpContent = (ExperimentContent) content;
		}
		initComponents();
	}
	
	/**
	 * Initializes the components.
	 */
	private void initComponents() {
		
		// JGoodies Cellconstrains
		CellConstraints cc = new CellConstraints();

		// Dialog panel
		JPanel dialogPnl = new JPanel();
		dialogPnl.setLayout(new FormLayout("5dlu, l:p, 5dlu", "5dlu, t:p, 5dlu,p, 5dlu"));

		JPanel projectDescPnl = new JPanel();
		projectDescPnl.setBorder(BorderFactory.createTitledBorder("Description"));
		projectDescPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p, 5dlu","5dlu, p, 5dlu"));

		// Selected project
		JLabel nameLbl = new JLabel();
		if(type == DialogType.NEW_PROJECT || type == DialogType.MODIFY_PROJECT){
			nameLbl.setText("Project Name:");
		} else {
			nameLbl.setText("Experiment Name:");
		}
		
		nameTtf = new JTextField("",15);
		nameTtf.setEditable(true);
		
		
		nameTtf.setEnabled(true);
		
		if(type == DialogType.MODIFY_PROJECT){
			nameTtf.setText(currentProjContent.getProjectTitle());
		}
		
		if(type == DialogType.MODIFY_EXPERIMENT){
			nameTtf.setText(currentExpContent.getExperimentTitle());
		}
		
		// ActionListener, enables the okBtn, when text is in the TextField
		nameTtf.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				if (nameTtf.getText().isEmpty()){
					saveBtn.setEnabled(false);
				}
				else {
					saveBtn.setEnabled(true);
				}
			}
			@Override
			public void keyReleased(KeyEvent arg0) {

			}
			@Override
			public void keyPressed(KeyEvent arg0) {

			}
		});
		
		projectDescPnl.add(nameLbl, cc.xy(2, 2));
		projectDescPnl.add(nameTtf, cc.xy(4, 2));
		
		// Properties panel
		JPanel propertyPnl = new JPanel();
		propertyPnl.setLayout(new FormLayout("5dlu, l:p, 5dlu, p, 5dlu","5dlu, t:p, 5dlu"));
		
		if(type == DialogType.NEW_PROJECT || type == DialogType.MODIFY_PROJECT){
			propertyPnl.setBorder(BorderFactory.createTitledBorder("Project Properties"));
		} else {
			propertyPnl.setBorder(BorderFactory.createTitledBorder("Experiment Properties"));
		}
		
		// "Modify property" subpanel
		JPanel propertyModPnl = new JPanel();
		propertyModPnl.setLayout(new FormLayout("p, 5dlu, p, 5dlu","5dlu, t:p, 1dlu, p, 5dlu, p, 1dlu, p, 5dlu, p, 5dlu"));
		
		// Name and Value of the Property
		JLabel propNameLbl = new JLabel("Property Name:");
		JLabel propValueLbl = new JLabel("Property Value:");
		
		// Input Fields for Name/Value
		propNameTtf = new JTextField(15);
		// When propNameTtf and propValueTtf is filled addProjPropBtn is useable
		propNameTtf.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				if ((propNameTtf.getText().isEmpty() == false) && (propValueTtf.getText().isEmpty() == false)		){
					addPropertyBtn.setEnabled(true);
				}
				else {
					addPropertyBtn.setEnabled(false);
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
			}
			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
		propValueTtf = new JTextField(15);
		propValueTtf.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				if ((propNameTtf.getText().isEmpty() == false) && (propValueTtf.getText().isEmpty() == false)){
					addPropertyBtn.setEnabled(true);
				} else {
					addPropertyBtn.setEnabled(false);
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
			}
			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
		
		// Add elements to "Modify project property" subpanel
		propertyModPnl.add(propNameLbl, cc.xy(1, 2));
		propertyModPnl.add(propNameTtf, cc.xy(1, 4));
		propertyModPnl.add(propValueLbl, cc.xy(1, 6));
		propertyModPnl.add(propValueTtf, cc.xy(1, 8));
		
		// Button to add project property 
		addPropertyBtn = new JButton("Add");
		// Button is only useable when new name and value is filled
		addPropertyBtn.setEnabled(false);
		addPropertyBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				appendPropertyToTable();
			}
		});
		
		// Button to change the project property entries
		changePropertyBtn = new JButton("Change");
		 // ChangeProjPropBtn only visible when entry in table is selected
		// is existing
		changePropertyBtn.setEnabled(false);
		changePropertyBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changePropertyTableBtnTriggered();
			}
		});
		
		// Add buttons to propButtonPnl 
		JPanel propButtonPnl = new JPanel(new FormLayout("p, 3dlu, p, 3dlu, p", "p"));
		propButtonPnl.add(addPropertyBtn, cc.xy(1, 1));
		propButtonPnl.add(changePropertyBtn, cc.xy(3, 1));
	
		// Add propButtonPnl to propertyModPnl
		propertyModPnl.add(propButtonPnl, cc.xyw(1, 10, 3));
		
		//Creates project Property table
		setupPropertiesTable();
		
		// Fill project property table,in the case of modify
		if ((type == DialogType.MODIFY_PROJECT) || (type == DialogType.MODIFY_EXPERIMENT)) {
			fillPropertyTable();
		}

		// Add the project table to scroll pane
		JScrollPane projectPropertyTblScp = new JScrollPane(propertyTbl);
		projectPropertyTblScp.setPreferredSize(new Dimension(325, 125));
		projectPropertyTblScp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		projectPropertyTblScp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		// Add ScrollPane to Panel
		propertyPnl.add(projectPropertyTblScp,cc.xy(2, 2));
		// Add "propertyModPnl to projectPropPnl
		propertyPnl.add(propertyModPnl,cc.xy(4, 2));
		
		// Delete button
		deletePropertyBtn = new JButton("Delete");
		deletePropertyBtn.setEnabled(false);
		// Delete the selected project property
		deletePropertyBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				deletePropertyFromSelectedRow();
			}
		});
		propButtonPnl.add(deletePropertyBtn, cc.xy(5, 1));
		
		// Add Panels to ProjectDialogPanel
		dialogPnl.add(projectDescPnl,cc.xy(2, 2));
		dialogPnl.add(propertyPnl,cc.xy(2, 4));
		
		// Save button functionality --> Store to the DB
		saveBtn = new JButton("Save");
		
		// Disable save button for new project/experiment.
		if(type == DialogType.NEW_PROJECT || type == DialogType.NEW_EXPERIMENT){
			saveBtn.setEnabled(false);
		}
		
		saveBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				switch (type) {
				case NEW_PROJECT:
					storeProject();
					break;
				case MODIFY_PROJECT:
					modifyProject();
					break;
				case NEW_EXPERIMENT:
					storeExperiment();
					break;
				case MODIFY_EXPERIMENT:
					modifyExperiment();
					break;
				}

				dispose();	
			}
		});
		
		// Cancel button functionality: Dispose the dialog without saving the the values to the database.
		JButton cancelBtn = new JButton("Cancel"); 
		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		
		//Add panels to the dialog content pane
		Container contentPane = this.getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(dialogPnl, BorderLayout.CENTER);
		
		// Create Button panel
		JPanel buttonPnl = new JPanel(new FormLayout("250dlu, r:p, 5dlu, r:p", "3dlu, p, 3dlu"));
		buttonPnl.add(saveBtn, cc.xy(2, 2));
		buttonPnl.add(cancelBtn, cc.xy(4, 2));
		
		contentPane.add(buttonPnl, BorderLayout.SOUTH);
		// Set the preferred size
		this.setPreferredSize(new Dimension(580, 310));
		this.setMinimumSize(new Dimension(580, 310));
		pack();
	}

	/**
	 * This method fills the project property table
	 */
	private void fillPropertyTable() {
		if (type == DialogType.MODIFY_PROJECT) {
			List<Property> projectproperties = currentProjContent.getProjectProperties();
			for (Property property : projectproperties) {
				// Fills vector for database operations
				int row = propertyTbl.getRowCount();
				((DefaultTableModel) propertyTbl.getModel()).addRow(new Object[] { row + 1, property.getName(), property.getValue() });
			}
			// reset operations vector
			for (int i = 0; i < operations.size(); i++) { operations.set(i, Operation.NONE); }
		} else if (type == DialogType.MODIFY_EXPERIMENT) {
			List<ExpProperty> experimentProperties = currentExpContent.getExperimentProperties();
			for (ExpProperty expProperty : experimentProperties) {
				// Fills vector for database operations
				operations.add(Operation.NONE);
				int row = propertyTbl.getRowCount();
				((DefaultTableModel) propertyTbl.getModel()).addRow(new Object[] { row + 1, expProperty.getName(), expProperty.getValue() });
			}
			// reset operations vector
			for (int i = 0; i < operations.size(); i++) { operations.set(i, Operation.NONE); }
		}			
	}

	/**
	 * Stores the project.
	 */
	protected void storeProject() {
		try {
			ProjectManager manager = new ProjectManager(parent.getClient().getConnection());

			// Store the project name
			id = manager.createNewProject(nameTtf.getText());
			
			// Collect properties
			collectProperties();
			
			// Store the project properties
			manager.addProjectProperties(id, properties);
			
			// Update the project table in the project panel.
			parent.getProjectPnl().refreshProjectTable();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Modifies an existing project.
	 */
	protected void modifyProject() {
		try {
			ProjectManager manager = new ProjectManager(parent.getClient().getConnection());

			// Modify the project name
			manager.modifyProjectName(currentProjContent.getProjectid(), nameTtf.getText());
			id = currentProjContent.getProjectid();
			
			// Collect properties
			collectProperties();
			
			// Modify the project properties
			manager.modifyProjectProperties(currentProjContent.getProjectid(), properties, operations);
			
			// Update the project table in the project panel.
			parent.getProjectPnl().refreshProjectTable();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Stores the experiment
	 */
	protected void storeExperiment() {
		try {
			ProjectManager manager = new ProjectManager(parent.getClient().getConnection());

			// Store the experiment name
			id = manager.createNewExperiment(currentProjContent.getProjectid(), nameTtf.getText());
			
			// Collect properties
			collectProperties();
			
			// Store the project properties
			manager.addExperimentProperties(id, properties);
			
			// Update the experiment table in the project panel.
			parent.getProjectPnl().refreshExperimentTable(currentProjContent.getProjectid());
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method modify and delete experiment properties.
	 */
	protected void modifyExperiment() {
		try {
			ProjectManager manager = new ProjectManager(parent.getClient().getConnection());

			// Modify the experiment name
			manager.modifyExperimentName(currentExpContent.getExperimentID(), nameTtf.getText());
			id = currentExpContent.getProjectID();

			// Collect properties
			collectProperties();

			// Modify the project properties
			manager.modifyExperimentProperties(currentExpContent.getExperimentID(), properties, operations);

			// Update the experiment table in the project panel.
			parent.getProjectPnl().refreshExperimentTable(currentExpContent.getProjectID());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method creating the projectProperties table
	 */
	private void setupPropertiesTable() {
		DefaultTableModel dtm = new DefaultTableModel() { 
			{
				setColumnIdentifiers(new Object[] { "#", "Property Name", "Property Value"});
			}
			public boolean isCellEditable(int row, int col) {
				return (col == 0) ? false : true;
			}
			// Variable types for columns
			public Class<?> getColumnClass(int col) {
				switch (col) {
				case 0:
					return Integer.class;
				case 1:
					return String.class;
				case 2: 
					return String.class;
				default:
					return getValueAt(0, col).getClass();
				}
			}
		};
		dtm.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent tme) {
				int row = tme.getFirstRow();
				switch (tme.getType()) {
				case TableModelEvent.UPDATE:
					if (operations.get(row) != Operation.ADD)
						operations.set(row, Operation.CHANGE);
					break;
				case TableModelEvent.INSERT:
					if (operations.contains(Operation.DELETE)) {
						operations.set(operations.indexOf(Operation.DELETE), Operation.CHANGE);
					} else {
						operations.add(Operation.ADD);
					}
					break;
				case TableModelEvent.DELETE:
					for (int i = row + 1; i < operations.size(); i++) {
						if (operations.get(i) == Operation.NONE) {
							operations.set(i, Operation.CHANGE);
						} else if (operations.get(i) == Operation.ADD) {
							operations.set(i, Operation.CHANGE);
							break;
						}
					}
					operations.remove(row);
					break;
				}
			}
		});
		
		// Table for projects
		propertyTbl = new JTable(dtm);
		//  Shows property name and values of the selected table row and enables edting the entry
		propertyTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent evt) {
				// Shows the entries of the table in the textfiels
				queryProjPropertyTableMouseClicked(evt);
				// Enables to change the entries
				changePropertyBtn.setEnabled(true);
				
			}
		});

		propertyTbl.setAutoCreateRowSorter(true);

		// Selection model for the list: Select one entry of the table only
		propertyTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Justify column size
		propertyTbl.getColumn("#").setMinWidth(30);
		propertyTbl.getColumn("#").setMaxWidth(30);
		propertyTbl.getColumn("Property Name").setMinWidth(140);
		propertyTbl.getColumn("Property Name").setMaxWidth(140);
		propertyTbl.getColumn("Property Value").setMinWidth(140);
		propertyTbl.getColumn("Property Value").setMaxWidth(140);
	}

	/**
	 * This method append a property to the project property table.
	 */
	private void appendPropertyToTable() {
		int row = propertyTbl.getRowCount();
		((DefaultTableModel) propertyTbl.getModel()).addRow(new Object[] {
				row+1, propNameTtf.getText(), propValueTtf.getText() });
		
		// Clear textfields
		propNameTtf.setText("");
		propValueTtf.setText("");
		addPropertyBtn.setEnabled(false);
	}
	
			
	/**
	 *  This method fills the textfields for property name and property value 
	 *  with the values in the selected table row
	 */
	private void queryProjPropertyTableMouseClicked(ListSelectionEvent evt) {
		// Get the selected row
		int projPropsRow = propertyTbl.getSelectedRow();
		// Condition if one row is selected.
		if (projPropsRow != -1) {
			String propertyName = propertyTbl.getValueAt(projPropsRow, 1).toString();
			String propertyValue = propertyTbl.getValueAt(projPropsRow, 2).toString();
			propNameTtf.setText(propertyName);
			propValueTtf.setText(propertyValue);
			// Allows to delete the table row
			deletePropertyBtn.setEnabled(true);
		} 
	}
	
				
	/**
	 * This method changes the entry in the table to the one in the textfields.
	 */
	private void changePropertyTableBtnTriggered(){		
		int selRow = propertyTbl.getSelectedRow();
		propertyTbl.setValueAt(propNameTtf.getText(), selRow, 1);
		propertyTbl.setValueAt(propValueTtf.getText(), selRow, 2);
	}
				
	/**
	 * This	method deletes the selected row of the projProptable
	 */
	protected void deletePropertyFromSelectedRow() {
		
		int selRow = propertyTbl.convertRowIndexToModel(propertyTbl.getSelectedRow());
		// Deletes selected Row
		((DefaultTableModel)propertyTbl.getModel()).removeRow(selRow);
		// Empty TextFields
		propNameTtf.setText("");
		propValueTtf.setText("");
		// Enables add and change button after the last entry is deleted
		// If table is empty change and add is disabled
		if (propertyTbl.getRowCount()==0){
			deletePropertyBtn.setEnabled(false);
		}
		addPropertyBtn.setEnabled(false);
		changePropertyBtn.setEnabled(false);
		
		int numProps = 0;	// number of properties before modification
		if (type == DialogType.MODIFY_PROJECT) {
			numProps = currentProjContent.getProjectProperties().size();
		} else if (type == DialogType.MODIFY_EXPERIMENT) {
			numProps = currentExpContent.getExperimentProperties().size();
		}
		if (operations.size() < numProps) {
			operations.add(Operation.DELETE);
		}
	}	
	
	/**
	 * This method collects the properties from the table and fills the properties map.
	 */
	private void collectProperties(){
		// read from property table
		for (int i = 0; i < propertyTbl.getModel().getRowCount(); i++){
			properties.put(propertyTbl.getValueAt(i, 1).toString(), propertyTbl.getValueAt(i, 2).toString());
		}
	}

	public Long start() {
		ScreenConfig.centerInScreen(this);
		this.setVisible(true);
		return id;
	}
	

}