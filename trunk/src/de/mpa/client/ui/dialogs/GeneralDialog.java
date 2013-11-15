package de.mpa.client.ui.dialogs;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
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
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Client;
import de.mpa.client.model.ExperimentContent;
import de.mpa.client.model.ProjectContent;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.ScreenConfig;
import de.mpa.client.ui.TableConfig;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.db.ProjectManager;
import de.mpa.db.accessor.ExpProperty;
import de.mpa.db.accessor.Property;

public class GeneralDialog extends JDialog {
	
	private long id = 0L;
			
	// Parent client frame.
	private ClientFrame clientFrame;
			
	// Parent client.
	private Client client;
	
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
	public GeneralDialog(String title, Frame owner, DialogType type) {
		this(title, owner, type, null);
	}
	
	/**
	 * Calling the main constructor adding property contents.
	 * @param title
	 * @param parent
	 * @param type
	 * @param content
	 */
	public GeneralDialog(String title, Frame owner, DialogType type, Object content) {
		super(owner, title, true);
		this.clientFrame = ClientFrame.getInstance();
		this.client = Client.getInstance();
		this.type = type;
		if (content instanceof ProjectContent) {
			this.currentProjContent = (ProjectContent) content;
		} else if (content instanceof ExperimentContent) {
			this.currentExpContent = (ExperimentContent) content;
		}
		initComponents();
		// set frame icon
		switch (type) {
		case NEW_PROJECT:
			this.setIconImage(IconConstants.ADD_FOLDER_ICON.getImage());
			break;
		case MODIFY_PROJECT:
			this.setIconImage(IconConstants.VIEW_FOLDER_ICON.getImage());
			break;
		case NEW_EXPERIMENT:
			this.setIconImage(IconConstants.ADD_PAGE_ICON.getImage());
			break;
		case MODIFY_EXPERIMENT:
			this.setIconImage(IconConstants.VIEW_PAGE_ICON.getImage());
			break;
		}
	}
	
	/**
	 * Initializes the components.
	 */
	private void initComponents() {

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
		
		descriptionPnl.add(new JLabel(name), CC.xy(2, 2));
		descriptionPnl.add(nameTtf, CC.xy(4, 2));
		
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
		appendPropertyBtn = new JButton("Add", IconConstants.ADD_ICON);
		appendPropertyBtn.setRolloverIcon(IconConstants.ADD_ROLLOVER_ICON);
		appendPropertyBtn.setPressedIcon(IconConstants.ADD_PRESSED_ICON);
		// Button is only useable when new name and value is filled
		appendPropertyBtn.setEnabled(false);
		appendPropertyBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				appendPropertyToTable();
			}
		});
		
		// Button to change the property entries
		changePropertyBtn = new JButton("Change", IconConstants.UPDATE_ICON);
		changePropertyBtn.setRolloverIcon(IconConstants.UPDATE_ROLLOVER_ICON);
		changePropertyBtn.setPressedIcon(IconConstants.UPDATE_PRESSED_ICON);
		// ChangeProjPropBtn only visible when entry in table is selected
		changePropertyBtn.setEnabled(false);
		changePropertyBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				changeSelectedProperty();
			}
		});
		
		// Delete button
		deletePropertyBtn = new JButton("Delete", IconConstants.CANCEL_ICON);
		deletePropertyBtn.setRolloverIcon(IconConstants.CANCEL_ROLLOVER_ICON);
		deletePropertyBtn.setPressedIcon(IconConstants.CANCEL_PRESSED_ICON);
		deletePropertyBtn.setEnabled(false);
		// Delete the selected property
		deletePropertyBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				deleteSelectedProperty();
			}
		});
		
		// add text fields and buttons to edit panel
		editPnl.add(new JLabel("Property Name:"), CC.xyw(1,1,5));
		editPnl.add(propNameTtf, CC.xyw(1,3,5));
		editPnl.add(new JLabel("Property Value:"), CC.xyw(1,5,5));
		editPnl.add(propValueTtf, CC.xyw(1,7,5));
		editPnl.add(appendPropertyBtn, CC.xy(1,9));
		editPnl.add(changePropertyBtn, CC.xy(3,9));
		editPnl.add(deletePropertyBtn, CC.xy(5,9));
		
		// add panels into split pane
		JSplitPane propertySpp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, tableScp, editPnl);
		propertySpp.setBorder(null);
		propertySpp.setDividerSize(10);
		((BasicSplitPaneUI) propertySpp.getUI()).getDivider().setBorder(null);
		
		// Add split pane to property panel
		propertyPnl.add(propertySpp, CC.xy(2,1));
		
		// Save button functionality -> store values in DB
		saveBtn = new JButton("Save", IconConstants.SAVE_DB_ICON);
		saveBtn.setRolloverIcon(IconConstants.SAVE_DB_ROLLOVER_ICON);
		saveBtn.setPressedIcon(IconConstants.SAVE_DB_PRESSED_ICON);
		saveBtn.setMargin(new Insets(2, 2, 2, 2));
		saveBtn.setIconTextGap(10);
		saveBtn.setHorizontalAlignment(SwingConstants.LEFT);
		saveBtn.setFont(saveBtn.getFont().deriveFont(Font.BOLD,
				saveBtn.getFont().getSize2D()*1.25f));
		
		saveBtn.setEnabled(type == DialogType.MODIFY_PROJECT || type == DialogType.MODIFY_EXPERIMENT);
		
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
		
		// Cancel button functionality -> dispose dialog without storing values in DB
		JButton cancelBtn = new JButton("Cancel", IconConstants.DELETE_DB_ICON);
		cancelBtn.setRolloverIcon(IconConstants.DELETE_DB_ROLLOVER_ICON);
		cancelBtn.setPressedIcon(IconConstants.DELETE_DB_PRESSED_ICON);
		cancelBtn.setMargin(new Insets(3, 2, 2, 5));
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
		contentPane.add(descriptionPnl,CC.xyw(2,2,3));
		contentPane.add(propertyPnl,CC.xyw(2,4,3));
		contentPane.add(saveBtn, CC.xy(2,6));
		contentPane.add(cancelBtn, CC.xy(4,6));
		
		// Set the preferred size
		this.pack();
		this.setMinimumSize(this.getSize());
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
			ProjectManager manager = new ProjectManager(client.getDatabaseConnection());

			// Store the project name
			id = manager.createNewProject(nameTtf.getText());
			
			// Collect properties
			collectProperties();
			
			// Store the project properties
			manager.addProjectProperties(id, properties);
			
			// Update the project table in the project panel.
			clientFrame.getProjectPanel().refreshProjectTable();

		} catch (SQLException e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),
					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
		}
	}
	
	/**
	 * Modifies an existing project.
	 */
	protected void modifyProject() {
		try {
			ProjectManager manager = new ProjectManager(client.getDatabaseConnection());

			// Modify the project name
			manager.modifyProjectName(currentProjContent.getProjectid(), nameTtf.getText());
			
			// Collect properties
			collectProperties();
			
			// Modify the project properties
			manager.modifyProjectProperties(currentProjContent.getProjectid(), properties, operations);
			
			// Update the project table in the project panel.
			clientFrame.getProjectPanel().refreshProjectTable();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Stores the experiment
	 */
	protected void storeExperiment() {
		try {
			ProjectManager manager = new ProjectManager(client.getDatabaseConnection());

			// Store the experiment name
			id = manager.createNewExperiment(currentProjContent.getProjectid(), nameTtf.getText());
			
			// Collect properties
			collectProperties();
			
			// Store the experiment properties
			manager.addExperimentProperties(id, properties);
			
			// Update the experiment table in the panel.
			clientFrame.getProjectPanel().refreshExperimentTable(currentProjContent.getProjectid());
			
		} catch (SQLException e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),
					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
		}
	}

	/**
	 * Modifies or deletes experiment properties.
	 */
	protected void modifyExperiment() {
		try {
			ProjectManager manager = new ProjectManager(client.getDatabaseConnection());

			// Modify the experiment name
			// TODO: some foreign key constraint regarding the 'settings' table breaks updating, need to investigate
			manager.modifyExperimentName(currentExpContent.getExperimentID(), nameTtf.getText());

			// Collect properties
			collectProperties();

			// Modify the experiment properties
			manager.modifyExperimentProperties(currentExpContent.getExperimentID(), properties, operations);

			// Update the experiment table in the panel.
			clientFrame.getProjectPanel().refreshExperimentTable(currentExpContent.getProjectID());
			
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
		TableConfig.configureColumnControl(propertyTbl);
		
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