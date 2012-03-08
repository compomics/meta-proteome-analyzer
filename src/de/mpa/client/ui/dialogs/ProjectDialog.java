package de.mpa.client.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.ui.ClientFrame;
import de.mpa.db.accessor.Experiment;
import de.mpa.db.accessor.Pepnovohit;
import de.mpa.db.accessor.Project;
import de.mpa.db.accessor.Property;

public class ProjectDialog extends JDialog {
	private JTable projPropsTbl;
	private JTable experimentsTbl;
	private JTable expPropsTbl;
	private JTextField propNameTtf;
	private JTextField propValueTtf;
	private int projPropCount = 1;
	private JButton deleteBtn;

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

		// Current project panel
		JPanel projectDescPnl = new JPanel();
		projectDescPnl.setBorder(BorderFactory.createTitledBorder("Project Description"));
		projectDescPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p, 5dlu","5dlu, p, 5dlu"));

		// Selected project
		JLabel projectNameLbl = new JLabel("Project Name: ");
		JTextField projectNameTtf = new JTextField(15);
		projectNameTtf.setEditable(true);
		projectNameTtf.setEnabled(true);

		projectDescPnl.add(projectNameLbl, cc.xy(2, 2));
		projectDescPnl.add(projectNameTtf, cc.xy(4, 2));

		// Project Properties
		JPanel projectPropPnl = new JPanel();
		projectPropPnl.setLayout(new FormLayout("5dlu, l:p, 5dlu, r:p, 5dlu","5dlu, t:p, 5dlu,p,5dlu"));
		projectPropPnl.setBorder(BorderFactory.createTitledBorder("Project Properties"));
		setupProjectPropertiesTable();
		
		// Project property modifications
		JPanel propertyModPnl = new JPanel();
		propertyModPnl.setLayout(new FormLayout("p, 5dlu, p, 5dlu","5dlu, p, 5dlu, p,5dlu, p, 5dlu"));
		
		JLabel propNameLbl = new JLabel("Property Name:");
		JLabel propValueLbl = new JLabel("Property Value:");
		
		propNameTtf = new JTextField(15);
		propValueTtf = new JTextField(15);
		
		propertyModPnl.add(propNameLbl, cc.xy(1, 2));
		propertyModPnl.add(propNameTtf, cc.xy(3, 2));
		propertyModPnl.add(propValueLbl, cc.xy(1, 4));
		propertyModPnl.add(propValueTtf, cc.xy(3, 4));
		
		// Add project property button
		JButton addProjPropBtn = new JButton("Add");
		//TODO: setup listener for name: addProjPropBtn.setEnabled(false);
		
		addProjPropBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				updatePropertyTable();
				propNameTtf.setText("");
				propValueTtf.setText("");
			}
		});
		
		// Change project property button
		JButton changeProjPropBtn = new JButton("Change");
		changeProjPropBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		JPanel propButtonPnl = new JPanel(new FormLayout("r:p, 5dlu, r:p", "p"));
		propButtonPnl.add(addProjPropBtn, cc.xy(1, 1));
		propButtonPnl.add(changeProjPropBtn, cc.xy(3, 1));
		propertyModPnl.add(propButtonPnl, cc.xyw(1, 6, 3));
		
		
		// Add the project table to scroll pane
		JScrollPane projectPropertyTblScp = new JScrollPane(projPropsTbl);
		projectPropertyTblScp.setPreferredSize(new Dimension(530, 250));
		projectPropertyTblScp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		// Delete button
		deleteBtn = new JButton("Delete");
		deleteBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				deletePropertyFromSelectedRow();
			}
		});
		
		projectPropPnl.add(propertyModPnl,cc.xy(2, 2));
		
		// Add ScrollPane to Panel
		projectPropPnl.add(projectPropertyTblScp,cc.xy(4, 2));
		projectPropPnl.add(deleteBtn, cc.xy(4, 4));
		
		// Add Panels to ProjectDialogPanel
		projectDialogPnl.add(projectDescPnl,cc.xy(2, 2));
		projectDialogPnl.add(projectPropPnl,cc.xyw(2, 4, 3));
		
		
		JPanel experimentsPnl = new JPanel(new FormLayout("5dlu, l:p, 5dlu", "5dlu, p, 5dlu, p, 5dlu"));
		experimentsPnl.setBorder(BorderFactory.createTitledBorder("Experiments"));
		
		JPanel expPropsPnl = new JPanel(new FormLayout("5dlu, l:p, 5dlu", "5dlu, p, 5dlu, p, 5dlu"));
		expPropsPnl.setBorder(BorderFactory.createTitledBorder("Experiment Properties"));
		
		// Setup experiments table
		setupExperimentsTable();
		
		// Add the experiments table to scroll pane
		JScrollPane experimentsTblScp = new JScrollPane(experimentsTbl);
		experimentsTblScp.setPreferredSize(new Dimension(400, 250));
		experimentsTblScp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		experimentsTblScp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		experimentsPnl.add(experimentsTblScp, cc.xy(2,2));
		projectDialogPnl.add(experimentsPnl, cc.xy(2, 6));
		
		// Delete experiments
		JButton delExpBtn = new JButton("Delete Experiment(s)");
		delExpBtn.setEnabled(false);
		
		delExpBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		deleteBtn.setEnabled(false);
		
		experimentsPnl.add(delExpBtn, cc.xy(2, 4));
		
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
		
		//Add Panels to Dialog
		Container contentPane = this.getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(projectDialogPnl, BorderLayout.CENTER);
		
		
		// Ok button: Saves the project.
		JButton okBtn = new JButton("OK");
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
		JPanel buttonPnl = ButtonBarFactory.buildOKCancelBar(okBtn, cancelBtn);
		contentPane.add(buttonPnl, BorderLayout.SOUTH);
		// Set the preferred size
		this.setPreferredSize(new Dimension(900, 800));
		pack();
	}
	
	protected void deletePropertyFromSelectedRow() {
		// TODO: Implement functionality.
	}

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
		
		projPropsTbl.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				queryProjPropertyTableMouseClicked(evt);
			}
		});

		// TODO: Check for sorting --> Add comparator ?
		projPropsTbl.setAutoCreateRowSorter(true);

		// Selection model for the list: Select one entry of the table only
		projPropsTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		
		// Column
		projPropsTbl.getColumn("#").setMinWidth(30);
		projPropsTbl.getColumn("#").setMaxWidth(30);
		projPropsTbl.getColumn("Property Name").setMinWidth(240);
		projPropsTbl.getColumn("Property Name").setMaxWidth(240);
		projPropsTbl.getColumn("Property Value").setMinWidth(240);
		projPropsTbl.getColumn("Property Value").setMaxWidth(240);
			}

	
	protected void queryProjPropertyTableMouseClicked(MouseEvent evt) {

		int row = projPropsTbl.getSelectedRow();

		// Condition if one row is selected.
		if (row != -1) {
			String propertyName = projPropsTbl.getValueAt(row, 1).toString();
			String propertyValue = projPropsTbl.getValueAt(row, 2).toString();
			propNameTtf.setText(propertyName);
			propValueTtf.setText(propertyValue);
			deleteBtn.setEnabled(true);
		} 
		
	}

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

		// TODO: Check for sorting --> Add comparator ? 
		experimentsTbl.setAutoCreateRowSorter(true);

		// Selection model for the list: Select one entry of the table only
		experimentsTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		experimentsTbl.getColumn("#").setMinWidth(30);
		experimentsTbl.getColumn("#").setMaxWidth(30);
		//experimentsTbl.getColumn("Experiment").setMinWidth(150);
		//experimentsTbl.getColumn("Experiment").setMaxWidth(150);
		experimentsTbl.getColumn("Experiment Created").setMinWidth(150);
		experimentsTbl.getColumn("Experiment Created").setMaxWidth(150);
	}
	
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
	
	/**
	 * This method updates the project property table.
	 */
	private void updatePropertyTable() {
		((DefaultTableModel) projPropsTbl.getModel()).addRow(new Object[] {
				projPropCount, propNameTtf.getText(), propValueTtf.getText() });
		// Update the project property counter
		projPropCount++;
	}
	
	
}
