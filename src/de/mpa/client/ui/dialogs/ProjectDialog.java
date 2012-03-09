package de.mpa.client.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.image.ConvolveOp;
import java.sql.Timestamp;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.ui.ClientFrame;

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

	// Number of projects in project properties table
	private int projPropCount = 1;
	
// Experiment table
	private JTable experimentsTbl;
	
	// Button to add an experiment
	private JButton addExperimentBtn;
	
	// Number of experiments in experiments table
	private int experimentCount = 1;

	// Textfield with the name of the experiment
	private JTextField expNameTfd;

	// Button to change an experiment
	private JButton changeExperiementBtn;
	
// Experiment property table
	private JTable expPropsTbl;
	
	// Button to save and leave project property table
	private JButton okBtn;

	

	/**
	 * Declares the ProjectDialog as child of the JDialog, being a component of
	 * the ClientFrame
	 * 
	 * @param title
	 * @param parent
	 */
	public ProjectDialog(String title, ClientFrame parent) {
		super(parent, title, true);
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
		projectDialogPnl.setBorder(BorderFactory.createTitledBorder("Project Dialog"));
		projectDialogPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p, 5dlu",	"5dlu, t:p, 5dlu,p,5dlu, p, 5dlu"));

	// 1. Project Name*******************************************************************	
		JPanel projectDescPnl = new JPanel();
		projectDescPnl.setBorder(BorderFactory.createTitledBorder("Project Description"));
		projectDescPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p, 5dlu","5dlu, p, 5dlu"));

		// Selected project
		JLabel projectNameLbl = new JLabel("Project Name: ");
		final JTextField projectNameTtf = new JTextField("",15);
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
		projectPropPnl.setLayout(new FormLayout("5dlu, l:p, 5dlu, r:p, 5dlu","5dlu, t:p, 5dlu,p,5dlu"));
		projectPropPnl.setBorder(BorderFactory.createTitledBorder("Project Properties"));
		
		// "Modify project property" subpanel
		JPanel propertyModPnl = new JPanel();
		propertyModPnl.setLayout(new FormLayout("p, 5dlu, p, 5dlu","5dlu, p, 5dlu, p,5dlu, p, 5dlu"));
		
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
		propertyModPnl.add(propNameTtf, cc.xy(3, 2));
		propertyModPnl.add(propValueLbl, cc.xy(1, 4));
		propertyModPnl.add(propValueTtf, cc.xy(3, 4));
		
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
				changeProjPropertyTable();
			}
		});
		
		// Add buttons to propButtonPnl 
		JPanel propButtonPnl = new JPanel(new FormLayout("r:p, 5dlu, r:p", "p"));
		propButtonPnl.add(addProjPropBtn, cc.xy(1, 1));
		propButtonPnl.add(changeProjPropBtn, cc.xy(3, 1));
		// Add propButtonPnl to propertyModPnl
		propertyModPnl.add(propButtonPnl, cc.xyw(1, 6, 3));
		// Add "propertyModPnl to projectPropPnl
		projectPropPnl.add(propertyModPnl,cc.xy(2, 2));
		
		//creates project Property table
		setupProjectPropertiesTable();
		// Add the project table to scroll pane
		JScrollPane projectPropertyTblScp = new JScrollPane(projPropsTbl);
		projectPropertyTblScp.setPreferredSize(new Dimension(530, 250));
		projectPropertyTblScp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		// Add ScrollPane to Panel
		projectPropPnl.add(projectPropertyTblScp,cc.xy(4, 2));
		
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
		// Add Delete button to projectPropPanel 
		projectPropPnl.add(deleteProjPropBtn, cc.xy(4, 4));
		
		// Add Panels to ProjectDialogPanel
		projectDialogPnl.add(projectDescPnl,cc.xy(2, 2));
		projectDialogPnl.add(projectPropPnl,cc.xyw(2, 4, 3));
	
	// 3. Experiement Panel**********************************************************************	
		JPanel experimentsPnl = new JPanel(new FormLayout("5dlu, p,5dlu,p, 5dlu", "5dlu, p, 5dlu, p, 5dlu,p,5dlu"));
		experimentsPnl.setBorder(BorderFactory.createTitledBorder("Experiments"));
		
	// Setup experiments table
		setupExperimentsTable();
		
		// Add the experiments table to scroll pane
		JScrollPane experimentsTblScp = new JScrollPane(experimentsTbl);
		experimentsTblScp.setPreferredSize(new Dimension(400, 250));
		experimentsTblScp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		experimentsTblScp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		// Add scroll pane to experiment panel 
		experimentsPnl.add(experimentsTblScp, cc.xyw(2,2,3));
		
	// Delete experiments
		JButton delExpBtn = new JButton("Delete Experiment(s)");
		delExpBtn.setEnabled(false);
		// Delete the selected experiemt
		delExpBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		deleteProjPropBtn.setEnabled(false);
		experimentsPnl.add(delExpBtn, cc.xy(4, 4));
		
	// Panel to add and modify experiments
		JPanel experimentMopPnl = new JPanel();
		experimentMopPnl.setLayout(new FormLayout("5dlu,l:p,5dlu,l:p,5dlu", "5dlu,p,5dlu,p, 5dlu"));
		
		// Lable for the name of the experiment
		JLabel expNameLbl = new JLabel("Experiments Name");
		experimentMopPnl.add(expNameLbl, cc.xy(2, 2));
		
		// Textfield for experiments name
		expNameTfd = new JTextField("",15);
		// Add only possible if textfield contains text
		expNameTfd.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (expNameTfd.getText().isEmpty() == false ){
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
		experimentMopPnl.add(expNameTfd,cc.xy(4, 2));
		
		// Panel for add and change Button
		JPanel expButtonPnl = new JPanel();
		expButtonPnl.setLayout(new FormLayout("5dlu,l:p, 5dlu, l:p, 5dlu", "p"));
		
		// Add Button to add experimentModPnl
		addExperimentBtn = new JButton("Add");
		// Add Button is only enabled if text is in Textfield
		addExperimentBtn.setEnabled(false);
		// Function to add experiment to table
		addExperimentBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				updateExperimentTable();
				// Empty the Textfield
				expNameTfd.setText("");
				addExperimentBtn.setEnabled(false);
			}

			
		});
		
		// Add "Add button" to experimentModPnl
		expButtonPnl.add(addExperimentBtn,cc.xy(2, 1));
		
		// Add Button to change experient
		changeExperiementBtn = new JButton("Change");
		// Add "change button to panel
		expButtonPnl.add(changeExperiementBtn,cc.xy(4, 1));
		
		// Add expButtonnl to experimentModPnl
		experimentMopPnl.add(expButtonPnl,cc.xy(2, 4));
		
		// add experimentModPanel to experiments panel
		experimentsPnl.add(experimentMopPnl,cc.xy(2, 6));
		
		
		
		
		
		// Add experimentPnl to projectDialog
		projectDialogPnl.add(experimentsPnl, cc.xy(2, 6));
		
	// 4. Experiment Property Panel**************************************************************
		JPanel expPropsPnl = new JPanel(new FormLayout("5dlu, l:p, 5dlu", "5dlu, p, 5dlu, p, 5dlu"));
		expPropsPnl.setBorder(BorderFactory.createTitledBorder("Experiment Properties"));
		
		
		// Setup experiment properties table.
		setupExpPropsTable();
		
		// Add the experiments  properties table to scroll pane
		JScrollPane expPropsTblScp = new JScrollPane(expPropsTbl);
		expPropsTblScp.setPreferredSize(new Dimension(400, 250));
		expPropsTblScp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		expPropsTblScp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		expPropsPnl.add(expPropsTblScp, cc.xy(2,2));
		projectDialogPnl.add(expPropsPnl, cc.xy(4, 6));
		
		// Delete experiments
		JButton delExpPropBtn = new JButton("Delete Properties");
		delExpPropBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		delExpPropBtn.setEnabled(false);
		
		expPropsPnl.add(delExpPropBtn, cc.xy(2, 4));
		
		
	// 5. ButtonBar******************************************************************************	
		
		// Ok button functionality
		okBtn = new JButton("OK");
		okBtn.setEnabled(false);
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Save to database.
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
		this.setPreferredSize(new Dimension(1200, 900));
		pack();
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

		// TODO: Check for sorting --> Add comparator ?
		projPropsTbl.setAutoCreateRowSorter(true);

		// Selection model for the list: Select one entry of the table only
		projPropsTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Justify column size
		projPropsTbl.getColumn("#").setMinWidth(30);
		projPropsTbl.getColumn("#").setMaxWidth(30);
		projPropsTbl.getColumn("Property Name").setMinWidth(240);
		projPropsTbl.getColumn("Property Name").setMaxWidth(240);
		projPropsTbl.getColumn("Property Value").setMinWidth(240);
		projPropsTbl.getColumn("Property Value").setMaxWidth(240);
	}

	/**
	 * This method updates the project property table.
	 */
	private void updatePropertyTable() {
		((DefaultTableModel) projPropsTbl.getModel()).addRow(new Object[] {
				projPropCount, propNameTtf.getText(), propValueTtf.getText() });
		// Update the project property counter
		projPropCount++;
	}
	
	/**
	 *  This method fills the textfields for property name and property value 
	 *  with the values in the selected table row
	 */
	protected void queryProjPropertyTableMouseClicked(ListSelectionEvent evt) {
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
	 * This method changes the entry in the table to the once in the Textfields
	 */
	private void changeProjPropertyTable(){
		projPropsTbl.setValueAt(propNameTtf.getText(), projPropsTbl.getSelectedRow(), 1);
		projPropsTbl.setValueAt(propValueTtf.getText(), projPropsTbl.getSelectedRow(), 2);
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
				changeExperiementBtn.setEnabled(true);
				
			}

			
		});

		// TODO: Check for sorting --> Add comparator ? 
		experimentsTbl.setAutoCreateRowSorter(true);

		// Selection model for the list: Select one entry of the table only
		experimentsTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// Adjust the tablesize
		experimentsTbl.getColumn("#").setMinWidth(30);
		experimentsTbl.getColumn("#").setMaxWidth(30);
		experimentsTbl.getColumn("Experiment Created").setMinWidth(150);
		experimentsTbl.getColumn("Experiment Created").setMaxWidth(150);
	}
	
	/**
	 *  This method adds the new experiment   
	 *  to the experiment table row
	 */
	private void updateExperimentTable() {
		// TODO Auto-generated method stub
		((DefaultTableModel) experimentsTbl.getModel()).addRow(new Object[] {
				experimentCount, expNameTfd.getText(), new Date() });
		// Update the project property counter
		experimentCount++;
	}
	
	private void queryExperimentTableMouseClicked(ListSelectionEvent evt) {
		// TODO Auto-generated method stub
		
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

		// TODO: Check for sorting --> Add comparator ? 
		expPropsTbl.setAutoCreateRowSorter(true);

		// Selection model for the list: Select one entry of the table only
		expPropsTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Setup column widths
		expPropsTbl.getColumn("#").setMinWidth(30);
		expPropsTbl.getColumn("#").setMaxWidth(30);
		expPropsTbl.getColumn("Property Value").setMinWidth(150);
		expPropsTbl.getColumn("Property Value").setMaxWidth(150);
	}
	

	

	
	
}
