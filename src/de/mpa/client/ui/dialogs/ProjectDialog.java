package de.mpa.client.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
import javax.swing.table.DefaultTableModel;

import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.ui.ClientFrame;
import de.mpa.db.ProjectManager;

public class ProjectDialog extends JDialog {
// Project property table
	private JTable projPropsTbl;
	
	// Textfield with the name of the property 
	private JTextField propNameTtf;
	
	// Textfield with the value of the property
	private JTextField propValueTtf;

	// Button to add project property
	private JButton addProjPropBtn;

	// Button to change a project property
	private JButton changeProjPropBtn;
	
	// Button to delete project property
	private JButton deleteProjPropBtn;

	
	// Experiment table
	private JTable experimentsTbl;
	
	// Button to add an experiment
	private JButton addExperimentBtn;
	
	// Textfield with the name of the experiment
	private JTextField expNameTtf;

	// Button to change an experiment
	private JButton changeExperimentBtn;
	
// Experiment property table
	private JTable expPropsTbl;
	
	// Button to save and leave project property table
	private JButton okBtn;

	private JButton deleteExperimentBtn;

	private JTextField propNameTtf2;

	private JTextField propValueTtf2;

	private JButton addExpPropBtn;

	private JButton changeExpPropBtn;

	private JButton delExpPropBtn;

	// Map of experiment and their properties
	private Map<Long,Map<String,String>> expPropAllExp = new HashMap<Long, Map<String,String>>();
	
	// List for all properties of one experiment
	private Map<String, String> expProperties = new HashMap<String, String>();

	// Current selected experiment row
	protected long selRow;
	
	// Parent client frame.
	private ClientFrame parent;

	private JTextField projectNameTtf;
	
	/**
	 * Declares the ProjectDialog as child of the JDialog, being a component of
	 * the ClientFrame
	 * 
	 * @param title
	 * @param parent
	 */
	public ProjectDialog(String title, ClientFrame parent) {
		super(parent, title, true);
		this.parent = parent;
		initComponents();
	}
	
	/**
	 * Calling the main constructor adding projects table
	 * @param title
	 * @param parent
	 * @param projectsTbl
	 */
	public ProjectDialog(String title, ClientFrame parent, JTable projectsTbl) {
		this(title, parent);
	}
	
	
// Methods***************************************************************************	
	/**
	 * Initializes the components.
	 */
	private void initComponents() {

		// JGoodies Cellconstrains
		CellConstraints cc = new CellConstraints();

		// Project dialog panel
		JPanel projectDialogPnl = new JPanel();
		//projectDialogPnl.setBorder(BorderFactory.createTitledBorder("Project Dialog"));
		projectDialogPnl.setLayout(new FormLayout("5dlu, l:p, 5dlu", "5dlu, t:p, 5dlu,p,5dlu, p, 5dlu, p, 5dlu"));

	// 1. Project Name*******************************************************************	
		JPanel projectDescPnl = new JPanel();
		projectDescPnl.setBorder(BorderFactory.createTitledBorder("Project Description"));
		projectDescPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p, 5dlu","5dlu, p, 5dlu"));

		// Selected project
		JLabel projectNameLbl = new JLabel("Project Name: ");
		projectNameTtf = new JTextField("",15);
		projectNameTtf.setEditable(true);
		projectNameTtf.setEnabled(true);
		
		// ActionListener, enables the okBtn, when text is in the TextField
		projectNameTtf.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				if (projectNameTtf.getText().isEmpty()){
					okBtn.setEnabled(false);
				}
				else {
					okBtn.setEnabled(true);
				}
			}
			@Override
			public void keyReleased(KeyEvent arg0) {

			}
			@Override
			public void keyPressed(KeyEvent arg0) {

			}
		});
		
		projectDescPnl.add(projectNameLbl, cc.xy(2, 2));
		projectDescPnl.add(projectNameTtf, cc.xy(4, 2));
		
	// 2. Project Properties Panel*********************************************************
		
		// Project Properties
		JPanel projectPropPnl = new JPanel();
		projectPropPnl.setLayout(new FormLayout("5dlu, l:p, 5dlu, p, 5dlu","5dlu, t:p, 5dlu"));
		projectPropPnl.setBorder(BorderFactory.createTitledBorder("Project Properties"));
		
		// "Modify project property" subpanel
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
					addProjPropBtn.setEnabled(true);
				}
				else {
					addProjPropBtn.setEnabled(false);
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
					addProjPropBtn.setEnabled(true);
				}
					else {
						addProjPropBtn.setEnabled(false);
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
		addProjPropBtn = new JButton("Add");
		// Button is only useable when new name and value is filled
		addProjPropBtn.setEnabled(false);
		addProjPropBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				updatePropertyTable();
				propNameTtf.setText("");
				propValueTtf.setText("");
				addProjPropBtn.setEnabled(false);
			}
		});
		
		// Button to change the project property entries
		changeProjPropBtn = new JButton("Change");
		 // ChangeProjPropBtn only visible when entry in table is selected
		// is existing
		changeProjPropBtn.setEnabled(false);
		changeProjPropBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeProjPropertyTableBtnTriggered();
			}
		});
		
		// Add buttons to propButtonPnl 
		JPanel propButtonPnl = new JPanel(new FormLayout("p, 3dlu, p, 3dlu, p", "p"));
		propButtonPnl.add(addProjPropBtn, cc.xy(1, 1));
		propButtonPnl.add(changeProjPropBtn, cc.xy(3, 1));
	
		// Add propButtonPnl to propertyModPnl
		propertyModPnl.add(propButtonPnl, cc.xyw(1, 10, 3));
		
		//creates project Property table
		setupProjectPropertiesTable();
		// Add the project table to scroll pane
		JScrollPane projectPropertyTblScp = new JScrollPane(projPropsTbl);
		projectPropertyTblScp.setPreferredSize(new Dimension(325, 125));
		projectPropertyTblScp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		projectPropertyTblScp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		// Add ScrollPane to Panel
		projectPropPnl.add(projectPropertyTblScp,cc.xy(2, 2));
		// Add "propertyModPnl to projectPropPnl
		projectPropPnl.add(propertyModPnl,cc.xy(4, 2));
		
		// Delete button
		deleteProjPropBtn = new JButton("Delete");
		// Delete the selected project property
		deleteProjPropBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				deletePropertyFromSelectedRow();
				addProjPropBtn.setEnabled(false);
				changeProjPropBtn.setEnabled(false);
			}
		});
		propButtonPnl.add(deleteProjPropBtn, cc.xy(5, 1));
		
		// Add Panels to ProjectDialogPanel
		projectDialogPnl.add(projectDescPnl,cc.xy(2, 2));
		projectDialogPnl.add(projectPropPnl,cc.xy(2, 4));
	
		// 3. Experiment Panel**********************************************************************	
		JPanel experimentsPnl = new JPanel(new FormLayout("5dlu, p,5dlu,p, 5dlu", "5dlu, p, 5dlu, p, 5dlu,p,5dlu"));
		experimentsPnl.setBorder(BorderFactory.createTitledBorder("Experiments"));
		
		// Setup experiments table
		setupExperimentsTable();
		
		// Add the experiments table to scroll pane
		JScrollPane experimentsTblScp = new JScrollPane(experimentsTbl);
		experimentsTblScp.setPreferredSize(new Dimension(460, 100));
		experimentsTblScp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		experimentsTblScp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		// Add scroll pane to experiment panel 
		experimentsPnl.add(experimentsTblScp, cc.xyw(2,4,3));
		
		deleteProjPropBtn.setEnabled(false);
		
		// Panel to add and modify experiments
		JPanel experimentMopPnl = new JPanel();
		experimentMopPnl.setLayout(new FormLayout("l:p,5dlu,l:p,5dlu, r:p, 3dlu, r:p, 3dlu, r:p, 5dlu", "5dlu,p"));
		
		// Lable for the name of the experiment
		JLabel expNameLbl = new JLabel("Experiment Name:");
		experimentMopPnl.add(expNameLbl, cc.xy(1, 2));
		
		// Textfield for experiments name
		expNameTtf = new JTextField(10);
		// Add only possible if textfield contains text
		expNameTtf.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (expNameTtf.getText().isEmpty() == false ){
					addExperimentBtn.setEnabled(true);
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
			}
			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
		
		// Add Textfield to Panel
		experimentMopPnl.add(expNameTtf,cc.xy(3, 2));
		
		
		// Add Button to add experimentModPnl
		addExperimentBtn = new JButton("Add");
		// Add Button is only enabled if text is in Textfield
		addExperimentBtn.setEnabled(false);
		// Function to add experiment to table
		addExperimentBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				updateExperimentTable();
				// Empty the Textfield
				expNameTtf.setText("");
				addExperimentBtn.setEnabled(false);
			}
		});
		
		// Add Button to change experient
		changeExperimentBtn = new JButton("Change");
		changeExperimentBtn.setEnabled(false);
		changeExperimentBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeExperimentTableBtnTriggered();
			}
		});
		
		// Delete Button to delete the selected experiment
		deleteExperimentBtn = new JButton("Delete");
		deleteExperimentBtn.setEnabled(false);
		
		// Function to add experiment to table
		deleteExperimentBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				deleteExperimentFromSelectedRow();
				// Empty the Textfield
				expNameTtf.setText("");
			}
		});
		
		// Add expButtonnl to experimentModPnl
		experimentMopPnl.add(addExperimentBtn,cc.xy(5, 2));
		experimentMopPnl.add(changeExperimentBtn,cc.xy(7, 2));
		experimentMopPnl.add(deleteExperimentBtn,cc.xy(9, 2));
		
		// add experimentModPanel to experiments panel
		experimentsPnl.add(experimentMopPnl,cc.xy(2, 2));
		
		
		// Add experimentPnl to projectDialog
		projectDialogPnl.add(experimentsPnl, cc.xy(2, 6));
		
	// 4. Experiment Property Panel**************************************************************
		JPanel expPropsPnl = new JPanel(new FormLayout("5dlu, l:p, 5dlu, p, 5dlu", "5dlu, t:p, 5dlu"));
		expPropsPnl.setBorder(BorderFactory.createTitledBorder("Experiment Properties"));
		
		
		// Setup experiment properties table.
		setupExpPropsTable();
		
		// Add the experiments  properties table to scroll pane
		JScrollPane expPropsTblScp = new JScrollPane(expPropsTbl);
		expPropsTblScp.setPreferredSize(new Dimension(325, 120));
		expPropsTblScp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		expPropsTblScp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		expPropsPnl.add(expPropsTblScp, cc.xy(2,2));
		// "Modify experiment property" subpanel
		JPanel expPropertyModPnl = new JPanel();
		expPropertyModPnl.setLayout(new FormLayout("p, 5dlu, p, 5dlu","0dlu, t:p, 1dlu, p, 5dlu, p, 1dlu, p, 5dlu, p, 5dlu"));
		
		// Name and Value of the Property
		JLabel propNameLbl2 = new JLabel("Property Name:");
		JLabel propValueLbl2 = new JLabel("Property Value:");
		
		// Input Fields for Name/Value
		propNameTtf2 = new JTextField(15);
		// Only useable when experiment is selected
		propNameTtf2.setEditable(false);
		// When propNameTtf and propValueTtf is filled addProjPropBtn is useable
		propNameTtf2.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				if ((propNameTtf2.getText().isEmpty() == false) && (propValueTtf.getText().isEmpty() == false)		){
					addProjPropBtn.setEnabled(true);
				}
				else {
					addProjPropBtn.setEnabled(false);
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
			}
			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
		propValueTtf2 = new JTextField(15);
		propValueTtf2.setEditable(false);
		propValueTtf2.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				if ((propValueTtf2.getText().isEmpty() == false) && (propValueTtf2.getText().isEmpty() == false)){
					addExpPropBtn.setEnabled(true);
				}
					else {
						addExpPropBtn.setEnabled(false);
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
		expPropertyModPnl.add(propNameLbl2, cc.xy(1, 2));
		expPropertyModPnl.add(propNameTtf2, cc.xy(1, 4));
		expPropertyModPnl.add(propValueLbl2, cc.xy(1, 6));
		expPropertyModPnl.add(propValueTtf2, cc.xy(1, 8));
		
		// Button to add project property 
		addExpPropBtn = new JButton("Add");
		
		// Button is only useable when new name and value is filled
		addExpPropBtn.setEnabled(false);
		addExpPropBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				updateExpPropertyMap();
				updateExpPropertyTable();
				
				propNameTtf2.setText("");
				propValueTtf2.setText("");
				addExpPropBtn.setEnabled(false);
			}
		});
		
		// Button to change the project property entries
		changeExpPropBtn = new JButton("Change");
		 // ChangeProjPropBtn only visible when entry in table is selected
		// is existing
		changeExpPropBtn.setEnabled(false);
		changeExpPropBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeExpPropertyTableBtnTriggered();
			}
		});
		
		// Add buttons to propButtonPnl 
		JPanel expPropButtonPnl = new JPanel(new FormLayout("p, 3dlu, p, 3dlu, p", "p"));
		expPropButtonPnl.add(addExpPropBtn, cc.xy(1, 1));
		expPropButtonPnl.add(changeExpPropBtn, cc.xy(3, 1));
	
		// Add propButtonPnl to propertyModPnl
		expPropertyModPnl.add(expPropButtonPnl, cc.xyw(1, 10, 3));
		expPropsPnl.add(expPropertyModPnl, cc.xy(4, 2));
		
		projectDialogPnl.add(expPropsPnl, cc.xy(2, 8));
		
		// Delete experiments
		delExpPropBtn = new JButton("Delete");
		delExpPropBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteExpPropertyFromSelectedRow();
				addExpPropBtn.setEnabled(false);
				changeExpPropBtn.setEnabled(false);
			}
		});
		delExpPropBtn.setEnabled(false);
		expPropButtonPnl.add(delExpPropBtn, cc.xy(5, 1));
		
		// 5. ButtonBar******************************************************************************	
		
		// Ok button functionality
		okBtn = new JButton("OK");
		okBtn.setEnabled(false);
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				storeProject();
			}
		});
		
		// Cancel button functionality
		JButton cancelBtn = new JButton("Cancel"); 
		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		
	// 6. Create JDialog**************************************************************************	
		//Add Panels to Dialog
		Container contentPane = this.getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(projectDialogPnl, BorderLayout.CENTER);
		// Create Button panel
		JPanel buttonPnl = ButtonBarFactory.buildOKCancelBar(okBtn, cancelBtn);
		contentPane.add(buttonPnl, BorderLayout.SOUTH);
		// Set the preferred size
		this.setPreferredSize(new Dimension(600, 710));
		this.setMinimumSize(new Dimension(600, 710));
		pack();
	}

	/**
	 * Stores the project.
	 */
	protected void storeProject() {
		try {
			ProjectManager manager = new ProjectManager(parent.getClient().getConnection());

			// Store the project name
			manager.createNewProject(projectNameTtf.getText());

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// SubMethods*************************************************************************		
	// a. ProjectPropertyTable________________________________________________________
	/**
	 * Methode creating the projectProperties table
	 */
	private void setupProjectPropertiesTable() {
		// Table for projects
		projPropsTbl = new JTable(new DefaultTableModel() { 
			{
				setColumnIdentifiers(new Object[] { "#", "Property Name", "Property Value"});
			}
			public boolean isCellEditable(int row, int col) {
				if (col == 0){
					return false;
				}
				else{
					return true;	
				}
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
		});
		
		//  Shows property name and values of the selected table row and enables edting the entry
		projPropsTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent evt) {
				// Shows the entries of the table in the textfiels
				queryProjPropertyTableMouseClicked(evt);
				// Enables to change the entries
				changeProjPropBtn.setEnabled(true);
				
			}
		});

		projPropsTbl.setAutoCreateRowSorter(true);

		// Selection model for the list: Select one entry of the table only
		projPropsTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Justify column size
		projPropsTbl.getColumn("#").setMinWidth(30);
		projPropsTbl.getColumn("#").setMaxWidth(30);
		projPropsTbl.getColumn("Property Name").setMinWidth(140);
		projPropsTbl.getColumn("Property Name").setMaxWidth(140);
		projPropsTbl.getColumn("Property Value").setMinWidth(140);
		projPropsTbl.getColumn("Property Value").setMaxWidth(140);
	}

	/**
	 * This method updates the project property table.
	 */
	private void updatePropertyTable() {
		int row = projPropsTbl.getRowCount();
		((DefaultTableModel) projPropsTbl.getModel()).addRow(new Object[] {
				row+1, propNameTtf.getText(), propValueTtf.getText() });
	}
	
	/**
	 * Clears the experiment property table..
	 */
	private void clearExpPropertyTable(){
		while (expPropsTbl.getRowCount() > 0) {
			((DefaultTableModel) expPropsTbl.getModel()).removeRow(0);
		}
	}
	
	/**
	 *  This method fills the textfields for property name and property value 
	 *  with the values in the selected table row
	 */
	private void queryProjPropertyTableMouseClicked(ListSelectionEvent evt) {
		// Get the selected row
		int projPropsRow = projPropsTbl.getSelectedRow();
		// Condition if one row is selected.
		if (projPropsRow != -1) {
			String propertyName = projPropsTbl.getValueAt(projPropsRow, 1).toString();
			String propertyValue = projPropsTbl.getValueAt(projPropsRow, 2).toString();
			propNameTtf.setText(propertyName);
			propValueTtf.setText(propertyValue);
			// Allows to delete the table row
			deleteProjPropBtn.setEnabled(true);
		} 
	}
	
	/**
	 *  This method fills the textfields for property name and property value 
	 *  with the values in the selected table row
	 */
	private void queryExpPropertyTableMouseClicked(ListSelectionEvent evt) {
		// Get the selected row
		int expPropsRow = expPropsTbl.getSelectedRow();
		// Condition if one row is selected.
		if (expPropsRow != -1) {
			String propertyName = expPropsTbl.getValueAt(expPropsRow, 1).toString();
			String propertyValue = expPropsTbl.getValueAt(expPropsRow, 2).toString();
			propNameTtf2.setText(propertyName);
			propValueTtf2.setText(propertyValue);
			// Allows to delete the table row
			delExpPropBtn.setEnabled(true);
		} 
	}
	
	/**
	 * This method changes the entry in the table to the one in the textfields.
	 */
	private void changeProjPropertyTableBtnTriggered(){
		projPropsTbl.setValueAt(propNameTtf.getText(), projPropsTbl.getSelectedRow(), 1);
		projPropsTbl.setValueAt(propValueTtf.getText(), projPropsTbl.getSelectedRow(), 2);
	}
	
	
	
	/**
	 * This method changes the entry in the experiments table to the one in the textfield.
	 */
	private void changeExperimentTableBtnTriggered(){
		experimentsTbl.setValueAt(expNameTtf.getText(), experimentsTbl.getSelectedRow(), 1);
	}
	/**
	 * This	method deletes the selected row of the experiment table.
	 */
	protected void deleteExperimentFromSelectedRow() {
		
		// Remove entries from the map
		long selRow = experimentsTbl.getSelectedRow();
		if(expPropAllExp.containsKey(selRow)){
			expPropAllExp.remove(selRow);
		}
		
		// Update experiment map
		Set<Long> keySet = expPropAllExp.keySet();
		for (Long key : keySet) {
			if(key > selRow){
				Map<String, String> expProperties = expPropAllExp.get(key);
				expPropAllExp.remove(key);
				expPropAllExp.put(key-1, expProperties);
			}
		}
		
		
		// Deletes selected Row
		((DefaultTableModel)experimentsTbl.getModel()).removeRow(experimentsTbl.convertRowIndexToModel(experimentsTbl.getSelectedRow()));
		
		// Update experiment table
		for (int i = 0; i < experimentsTbl.getModel().getRowCount(); i++){
			experimentsTbl.setValueAt(i+1, i, 0);
		}
		
		// Empty TextFields
		expNameTtf.setText("");
		
		// Enables add and change button after the last entry is deleted
		// If table is empty change and add is disabled
		if (experimentsTbl.getRowCount()==0){
			addExperimentBtn.setEnabled(false);
			changeExperimentBtn.setEnabled(false);
			deleteExperimentBtn.setEnabled(false);
		}
	}	
	
	/**
	 * This	method deletes the selected row of the projProptable
	 */
	protected void deletePropertyFromSelectedRow() {
		// Deletes selected Row
		((DefaultTableModel)projPropsTbl.getModel()).removeRow(projPropsTbl.convertRowIndexToModel(projPropsTbl.getSelectedRow()));
		// Empty TextFields
		propNameTtf.setText("");
		propValueTtf.setText("");
		// Enables add and change button after the last entry is deleted
		// If table is empty change and add is disabled
		if (projPropsTbl.getRowCount()==0){
			addProjPropBtn.setEnabled(false);
			changeProjPropBtn.setEnabled(false);
			deleteProjPropBtn.setEnabled(false);
		}
	}	
	
		
	// b. Experiment Table_____________________________________________________
	/**
	 * Method creating the experiments table
	 */
	private void setupExperimentsTable() {
		// Table for projects
		experimentsTbl = new JTable(new DefaultTableModel() { 
			{
				setColumnIdentifiers(new Object[] { "#", "Experiment", "Experiment Created"});
			}
			public boolean isCellEditable(int row, int col) {
				if (col == 0){
					return false;
				}
				else{
					return true;	
				}
			}
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
		});
		//  Shows property name and values of the selected table row and enables edting the entry
		experimentsTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent evt) {
				// Shows the entries of the table in the textfiels
				queryExperimentTableMouseClicked(evt);
				// Enables to change the entries
				changeExperimentBtn.setEnabled(true);
				deleteExperimentBtn.setEnabled(true);
				propNameTtf2.setEditable(true);
				propValueTtf2.setEditable(true);
				
				clearExpPropertyTable();
				
				selRow = (long) experimentsTbl.getSelectedRow();
				
				// Check for entries in map.
				if(expPropAllExp.get(selRow) != null){
					
					expProperties = expPropAllExp.get(selRow);
					System.out.println("size keyset "+ expProperties.size());
					for (String key : expProperties.keySet()){
						addEntriesToExpPropertyTable(key, expProperties.get(key));
					}
				}
			}	
				
		});

		experimentsTbl.setAutoCreateRowSorter(true);

		// Selection model for the list: Select one entry of the table only
		experimentsTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// Adjust the tablesize
		experimentsTbl.getColumn("#").setMinWidth(30);
		experimentsTbl.getColumn("#").setMaxWidth(30);
		experimentsTbl.getColumn("Experiment Created").setMinWidth(140);
		experimentsTbl.getColumn("Experiment Created").setMaxWidth(140);
	}
	
	/**
	 *  This method adds the new experiment to the experiment table row.
	 */
	private void updateExperimentTable() {
		int row = experimentsTbl.getRowCount();
		
		((DefaultTableModel) experimentsTbl.getModel()).addRow(new Object[] {row+1, expNameTtf.getText(), new Date() });
	}
	
	/**
	 *  This method fills the textfields for the experiment name with the value in the selected table row.
	 */
	private void queryExperimentTableMouseClicked(ListSelectionEvent evt) {
		// Get the selected row
		int experimentsRow = experimentsTbl.getSelectedRow();
		// Condition if one row is selected.
		if (experimentsRow != -1) {
			String experimentName = experimentsTbl.getValueAt(experimentsRow, 1).toString();
			expNameTtf.setText(experimentName);
			// Allows to delete the table row
			deleteExperimentBtn.setEnabled(true);
		} 
		
	}
	
	
	// c. ExpPropertyTable________________________________________________________
	/**
	 * Method creating the experiment properties table
	 */
	private void setupExpPropsTable() {
		// Table for projects
		expPropsTbl = new JTable(new DefaultTableModel() { 
			{
				setColumnIdentifiers(new Object[] { "#", "Property Name", "Property Value"});
			}
			public boolean isCellEditable(int row, int col) {
				if (col == 0){
					return false;
				}
				else{
					return true;	
				}
			}
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
		});
		
		//  Shows property name and values of the selected table row and enables editing the entry
		expPropsTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent evt) {
				// Shows the entries of the table in the textfields
				queryExpPropertyTableMouseClicked(evt);
				// Enables to change the entries
				changeExpPropBtn.setEnabled(true);
				
			}
		});
		
		expPropsTbl.setAutoCreateRowSorter(true);

		// Selection model for the list: Select one entry of the table only
		expPropsTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Setup column widths
		expPropsTbl.getColumn("#").setMinWidth(30);
		expPropsTbl.getColumn("#").setMaxWidth(30);
		expPropsTbl.getColumn("Property Name").setMinWidth(140);
		expPropsTbl.getColumn("Property Name").setMaxWidth(140);
		expPropsTbl.getColumn("Property Value").setMinWidth(140);
		expPropsTbl.getColumn("Property Value").setMaxWidth(140);
	}

	
	/**
	 * This method changes the entry in the table to the one in the textfields.
	 */
	protected void changeExpPropertyTableBtnTriggered() {
		expPropsTbl.setValueAt(propNameTtf2.getText(), expPropsTbl.getSelectedRow(), 1);
		expPropsTbl.setValueAt(propValueTtf2.getText(), expPropsTbl.getSelectedRow(), 2);
	}
	
	/**
	 * This	method deletes the selected row of the ExpprojProptable
	 */
	protected void deleteExpPropertyFromSelectedRow() {
		// Deletes selected Row
		((DefaultTableModel)expPropsTbl.getModel()).removeRow(expPropsTbl.convertRowIndexToModel(expPropsTbl.getSelectedRow()));
		// Empty TextFields
		propNameTtf2.setText("");
		propValueTtf2.setText("");
		// Enables add and change button after the last entry is deleted
		// If table is empty change and add is disabled
		if (expPropsTbl.getRowCount()==0){
			addExpPropBtn.setEnabled(false);
			changeExpPropBtn.setEnabled(false);
			delExpPropBtn.setEnabled(false);
		}
	}	
	/**
	 * This method updates the project property table.
	 */
	private void updateExpPropertyTable() {	
		String propertyName = propNameTtf2.getText();
		String propertyValue = propValueTtf2.getText();
		addEntriesToExpPropertyTable(propertyName, propertyValue);
	}
	
	private void updateExpPropertyMap() {
		System.out.println("sel row: " + selRow);
		if(expPropAllExp.containsKey(selRow)){
			
			expProperties = expPropAllExp.get(selRow);
			
		} else {
			expProperties = new HashMap<String, String>();
		}
		
		expProperties.put(propNameTtf2.getText(), propValueTtf2.getText());
		Long selExperiment = (long) selRow;
		expPropAllExp.put(selExperiment, expProperties);
	}
	
	/**
	 * Add entries to the experiment property table.
	 * @param propertyName
	 * @param propertyValue
	 */
	private void addEntriesToExpPropertyTable(String propertyName, String propertyValue){
		int row = expPropsTbl.getRowCount();
		((DefaultTableModel) expPropsTbl.getModel()).addRow(new Object[] {
				row+1, propertyName, propertyValue });		
	}

}
