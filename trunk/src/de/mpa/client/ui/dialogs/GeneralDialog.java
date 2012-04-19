package de.mpa.client.ui.dialogs;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXTable;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.model.ExperimentContent;
import de.mpa.client.model.ProjectContent;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.ScreenConfig;
import de.mpa.client.ui.TableConfig;
import de.mpa.db.ProjectManager;
import de.mpa.db.accessor.ExpProperty;
import de.mpa.db.accessor.Property;

public class GeneralDialog extends JDialog {
	
	private long id = 0L;
	
	// Property table
	private JXTable propertyTbl;
	
	// Textfield with the name of the property 
	private JTextField propNameTtf;
	
	// Textfield with the value of the property
	private JTextField propValueTtf;

	// Button to add property
	private JButton appendPropertyBtn;

	// Button to change a property
	private JButton changePropertyBtn;
	
	// Button to delete property
	private JButton deletePropertyBtn;
	
	// Button to save and leave property table
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
	 * Declares the Dialog as child of the JDialog, being a component of
	 * the ClientFrame
	 * 
	 * @param title
	 * @param parent
	 */
	public GeneralDialog(String title, ClientFrame parent, DialogType type) {
		this(title, parent, type, null);
	}
	
	/**
	 * Calling the main constructor adding property contents.
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
		
		// JGoodies CellConstraints
		CellConstraints cc = new CellConstraints();

		// Description panel
		JPanel descriptionPnl = new JPanel();
		descriptionPnl.setBorder(BorderFactory.createTitledBorder("Description"));
		descriptionPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p:g, 5dlu",
				"0dlu, p, 5dlu"));

		// Selected project/experiment textfield label
		String name = ((type == DialogType.NEW_PROJECT) || (type == DialogType.MODIFY_PROJECT)) ?
				"Project Name:" : "Experiment Name:";
		
		nameTtf = new JTextField();
		nameTtf.setEditable(true);
		
		if (type == DialogType.MODIFY_PROJECT) {
			nameTtf.setText(currentProjContent.getProjectTitle());
		} else if (type == DialogType.MODIFY_EXPERIMENT) {
			nameTtf.setText(currentExpContent.getExperimentTitle());
		}
		
		// ActionListener to enable/disable the 'save' button
		// depending on whether text has been entered
		nameTtf.getDocument().addDocumentListener(new DocumentListener() {
			public void removeUpdate(DocumentEvent evt) { updateSaveButton(); }
			public void insertUpdate(DocumentEvent evt) { updateSaveButton(); }
			public void changedUpdate(DocumentEvent evt) { updateSaveButton(); }
			private void updateSaveButton() { saveBtn.setEnabled(!nameTtf.getText().isEmpty()); }
		});
		
		descriptionPnl.add(new JLabel(name), cc.xy(2, 2));
		descriptionPnl.add(nameTtf, cc.xy(4, 2));
		
		// Properties panel
		JPanel propertyPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu", "f:p:g, 5dlu"));
		
		if (type == DialogType.NEW_PROJECT || type == DialogType.MODIFY_PROJECT) {
			propertyPnl.setBorder(BorderFactory.createTitledBorder("Project Properties"));
		} else {
			propertyPnl.setBorder(BorderFactory.createTitledBorder("Experiment Properties"));
		}
		
		//Creates property table
		setupPropertiesTable();
		
		// Fill property table,in the case of modify
		if ((type == DialogType.MODIFY_PROJECT) || (type == DialogType.MODIFY_EXPERIMENT)) {
			fillPropertyTable();
		}

		// Add table to scroll pane
		JScrollPane tableScp = new JScrollPane(propertyTbl);
		tableScp.setPreferredSize(new Dimension(325, 125));
		tableScp.setMinimumSize(new Dimension(tableScp.getPreferredSize().width/2,
				tableScp.getPreferredSize().height/2));
		tableScp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		tableScp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		// Panel containing text fields and edit buttons
		FormLayout editLyt = new FormLayout("p:g, 5dlu, p:g, 5dlu, p:g",
				"p, 1dlu, p, 5dlu, p, 1dlu, p, 5dlu, t:p:g");
		editLyt.setColumnGroups(new int[][] {{1,3,5}});
		JPanel editPnl = new JPanel(editLyt);
		
		// Input fields for Name/Value
		propNameTtf = new JTextField();
		propNameTtf.getDocument().addDocumentListener(new DocumentListener() {
			public void removeUpdate(DocumentEvent evt) { updateModifyButtons(); }
			public void insertUpdate(DocumentEvent evt) { updateModifyButtons(); }
			public void changedUpdate(DocumentEvent evt) { System.out.println(evt); }
		});
		
		propValueTtf = new JTextField();
		propValueTtf.getDocument().addDocumentListener(new DocumentListener() {
			public void removeUpdate(DocumentEvent evt) { updateModifyButtons(); }
			public void insertUpdate(DocumentEvent evt) { updateModifyButtons(); }
			public void changedUpdate(DocumentEvent evt) { System.out.println(evt); }
		});
		
		// Button to add property 
		appendPropertyBtn = new JButton("Add",
				new ImageIcon(getClass().getResource("/de/mpa/resources/icons/add16.png")));
		// Button is only useable when new name and value is filled
		appendPropertyBtn.setEnabled(false);
		appendPropertyBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				appendPropertyToTable();
			}
		});
		
		// Button to change the property entries
		changePropertyBtn = new JButton("Change",
				new ImageIcon(getClass().getResource("/de/mpa/resources/icons/update16.png")));
		 // ChangeProjPropBtn only visible when entry in table is selected
		// is existing
		changePropertyBtn.setEnabled(false);
		changePropertyBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeSelectedProperty();
			}
		});
		
		// Delete button
		deletePropertyBtn = new JButton("Delete",
				new ImageIcon(getClass().getResource("/de/mpa/resources/icons/cancel16.png")));
		deletePropertyBtn.setEnabled(false);
		// Delete the selected property
		deletePropertyBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				deleteSelectedProperty();
			}
		});
		
		// add text fields and buttons to edit panel
		editPnl.add(new JLabel("Property Name:"), cc.xyw(1,1,5));
		editPnl.add(propNameTtf, cc.xyw(1,3,5));
		editPnl.add(new JLabel("Property Value:"), cc.xyw(1,5,5));
		editPnl.add(propValueTtf, cc.xyw(1,7,5));
		editPnl.add(appendPropertyBtn, cc.xy(1,9));
		editPnl.add(changePropertyBtn, cc.xy(3,9));
		editPnl.add(deletePropertyBtn, cc.xy(5,9));
		
		// add panels into split pane
		JSplitPane propertySpp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScp, editPnl);
		propertySpp.setBorder(null);
		propertySpp.setContinuousLayout(true);
		propertySpp.setDividerSize(10);
		BasicSplitPaneDivider divider = ((BasicSplitPaneUI) propertySpp.getUI()).getDivider();
		if (divider != null) { divider.setBorder(null); }
		
		// Add split pane to property panel
		propertyPnl.add(propertySpp, cc.xy(2,1));
		
		// Save button functionality --> Store to the DB
		saveBtn = new JButton("Save",
				new ImageIcon(getClass().getResource("/de/mpa/resources/icons/database_save.png")));
		saveBtn.setHorizontalAlignment(SwingConstants.LEFT);
		saveBtn.setFont(saveBtn.getFont().deriveFont(Font.BOLD,
				saveBtn.getFont().getSize2D()*1.25f));
		
		saveBtn.setEnabled(type == DialogType.MODIFY_PROJECT || type == DialogType.MODIFY_EXPERIMENT);
		
		saveBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
//				System.out.println(operations);
				
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
		JButton cancelBtn = new JButton("Cancel",
				new ImageIcon(getClass().getResource("/de/mpa/resources/icons/database_delete.png")));
		cancelBtn.setFont(cancelBtn.getFont().deriveFont(Font.BOLD,
				cancelBtn.getFont().getSize2D()*1.25f));
		
		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		
		saveBtn.setPreferredSize(cancelBtn.getPreferredSize());
		
		// Add panels to the dialog content pane
		Container contentPane = this.getContentPane();
		contentPane.setLayout(new FormLayout("5dlu, r:p:g, 5dlu, p, 5dlu",
				"5dlu, p, 5dlu, f:p:g, 5dlu, p, 5dlu"));
		
		// Add Panels to content pane
		contentPane.add(descriptionPnl,cc.xyw(2,2,3));
		contentPane.add(propertyPnl,cc.xyw(2,4,3));
		contentPane.add(saveBtn, cc.xy(2,6));
		contentPane.add(cancelBtn, cc.xy(4,6));
		
		// Set the preferred size
		this.setPreferredSize(new Dimension(640, 310));
		this.setMinimumSize(this.getPreferredSize());
		pack();
	}
	
	/**
	 * Method to set the enabled state of the 'Add' and 'Change' buttons depending on
	 * changes made to the property text fields
	 */
	private void updateModifyButtons() {
		// has the content of the text fields changed?
		boolean changed = !propNameTtf.getText().equals(propNameTtf.getName()) ||
				!propValueTtf.getText().equals(propValueTtf.getName());
		// is there even any text inside the text fields?
		boolean notEmpty = !propNameTtf.getText().isEmpty() &&
				!propValueTtf.getText().isEmpty();
		// is there even any row selected in the table?
		boolean selected = propertyTbl.getSelectedRow() > -1;
		// update button enable states
		appendPropertyBtn.setEnabled(changed && notEmpty);
		changePropertyBtn.setEnabled(changed && selected);
	}

	/**
	 * This method fills the property table
	 */
	private void fillPropertyTable() {
		if (type == DialogType.MODIFY_PROJECT) {
			List<Property> projectProperties = currentProjContent.getProjectProperties();
			for (Property property : projectProperties) {
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
			parent.getProjectPanel().refreshProjectTable();

		} catch (SQLException e) {
			GeneralExceptionHandler.showSQLErrorDialog(e, this);
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
			parent.getProjectPanel().refreshProjectTable();
			
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
			
			// Store the experiment properties
			manager.addExperimentProperties(id, properties);
			
			// Update the experiment table in the panel.
			parent.getProjectPanel().refreshExperimentTable(currentProjContent.getProjectid());
			
		} catch (SQLException e) {
			GeneralExceptionHandler.showSQLErrorDialog(e, this);
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

			// Modify the experiment properties
			manager.modifyExperimentProperties(currentExpContent.getExperimentID(), properties, operations);

			// Update the experiment table in the panel.
			parent.getProjectPanel().refreshExperimentTable(currentExpContent.getProjectID());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method creating the properties table
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
		
		// Table for properties
		propertyTbl = new JXTable(dtm);
		propertyTbl.setColumnControlVisible(true);
		
		//  Shows property name and values of the selected table row and enables editing the entry
		propertyTbl.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent evt) {
				if (!evt.getValueIsAdjusting()) {
					// Shows the entries of the table in the text fields
					queryPropertyTableSelected(evt);
				}
			}
		});
		DefaultCellEditor dce = (DefaultCellEditor) propertyTbl.getDefaultEditor(Object.class);
		final JTextField editor = (JTextField) dce.getComponent();
		editor.getDocument().addDocumentListener(new DocumentListener() {
			public void removeUpdate(DocumentEvent arg0) { updateTextFields(); }
			public void insertUpdate(DocumentEvent arg0) { updateTextFields(); }
			public void changedUpdate(DocumentEvent arg0) { updateTextFields(); }
			private void updateTextFields() {
				int col = propertyTbl.getEditingColumn();
				if (col == 1) {
					propNameTtf.setText(editor.getText());
				} else if (col == 2) {
					propValueTtf.setText(editor.getText());
				}
			}
		});
		dce.addCellEditorListener(new CellEditorListener() {
			public void editingStopped(ChangeEvent evt) {
			}
			public void editingCanceled(ChangeEvent evt) {
				// revert to original values
				propNameTtf.setText(propNameTtf.getName());
				propValueTtf.setText(propValueTtf.getName());
			}
		});

		propertyTbl.getTableHeader().setReorderingAllowed(false);

		// Selection model for the list: Select one entry of the table only
		propertyTbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Justify column size
		TableConfig.packColumn(propertyTbl, 0, 12);
		propertyTbl.getColumnModel().getColumn(0).setMaxWidth(
				propertyTbl.getColumnModel().getColumn(0).getPreferredWidth());
		propertyTbl.getColumnModel().getColumn(0).setResizable(false);
	}

	/**
	 * This method append a property to the property table.
	 */
	private void appendPropertyToTable() {
		int row = propertyTbl.getRowCount();
		((DefaultTableModel) propertyTbl.getModel()).addRow(new Object[] {
				row+1, propNameTtf.getText(), propValueTtf.getText() });
		propertyTbl.getSelectionModel().setSelectionInterval(
				propertyTbl.getRowCount()-1, propertyTbl.getRowCount()-1);
		
		// disable append button to prevent adding multiple times by accident
		appendPropertyBtn.setEnabled(false);
	}
	
			
	/**
	 *  This method fills the text fields for property name and property value 
	 *  with the values in the selected table row
	 */
	private void queryPropertyTableSelected(ListSelectionEvent evt) {
		// Get the selected row
		int projPropsRow = propertyTbl.getSelectedRow();
		// Condition if one row is selected.
		if (projPropsRow != -1) {
			String propertyName = propertyTbl.getValueAt(projPropsRow, 1).toString();
			String propertyValue = propertyTbl.getValueAt(projPropsRow, 2).toString();
			// Store property name and value for detection of changes
			propNameTtf.setName(propertyName);
			propValueTtf.setName(propertyValue);
			// Display name and value in components
			propNameTtf.setText(propertyName);
			propValueTtf.setText(propertyValue);
			// Update delete button state
			deletePropertyBtn.setEnabled(true);
		} else {
			appendPropertyBtn.setEnabled(false);
			changePropertyBtn.setEnabled(false);
			deletePropertyBtn.setEnabled(false);
		}
	}
	
				
	/**
	 * This method changes the entry in the table to the one in the text fields.
	 */
	private void changeSelectedProperty() {
		// modify table cells
		int selRow = propertyTbl.getSelectedRow();
		propertyTbl.setValueAt(propNameTtf.getText(), selRow, 1);
		propertyTbl.setValueAt(propValueTtf.getText(), selRow, 2);
		// update button states
		appendPropertyBtn.setEnabled(false);
		changePropertyBtn.setEnabled(false);
	}
				
	/**
	 * This	method deletes the selected row of the properties table
	 */
	protected void deleteSelectedProperty() {
		
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
		appendPropertyBtn.setEnabled(false);
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