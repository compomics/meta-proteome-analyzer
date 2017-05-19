package de.mpa.client.ui.projectpanel.dialogs;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

import org.jdesktop.swingx.JXTable;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.sharedelements.ScreenConfig;
import de.mpa.client.ui.sharedelements.icons.IconConstants;
import de.mpa.client.ui.sharedelements.tables.TableConfig;

/**
 * Dialog implementation for manipulating projects and experiments.
 * 
 * @author A. Behne, R. Heyer
 */
@SuppressWarnings("serial")
public class GeneralDialog extends JDialog {
	
//	/**
//	 * Enumeration holding dialog types.
//	 * @author A. Behne
//	 */
//	public enum DialogType {
//		
//		NEW_PROJECT(IconConstants.ADD_FOLDER_ICON.getImage(), "Project", "New") {
//			@Override
//			public void processContent(GeneralDialog dialog) {
//				// store project
//				dialog.getContent().persist(
//						dialog.getContentName(), dialog.getProperties());
//			}
//		},
//		MODIFY_PROJECT(IconConstants.VIEW_FOLDER_ICON.getImage(), "Project", "Modify") {
//			@Override
//			public void processContent(GeneralDialog dialog) {
//				// modify project
//				dialog.getContent().update(
//						dialog.getContentName(), dialog.getProperties(), dialog.getOperations());
//			}
//		},
//		NEW_EXPERIMENT(IconConstants.ADD_PAGE_ICON.getImage(), "Experiment", "New") {
//			@Override
//			public void processContent(GeneralDialog dialog) {
//				// store experiment
//				dialog.getContent().persist(
//						dialog.getContentName(), dialog.getProperties());
//			}
//		},
//		MODIFY_EXPERIMENT(IconConstants.VIEW_PAGE_ICON.getImage(), "Experiment", "Modify") {
//			@Override
//			public void processContent(GeneralDialog dialog) {
//				// modify experiment
//				dialog.getContent().update(
//						dialog.getContentName(), dialog.getProperties(), dialog.getOperations());
//			}
//		};
//		
//		/**
//		 * The icon image to use on the dialog
//		 */
//		private final Image iconImage;
//		
//		/**
//		 * The name of the content object.
//		 */
//		private final String name;
//		
//		/**
//		 * The prefix string.
//		 */
//		private final String prefix;
//		
//		/**
//		 * 
//		 * @param iconImage
//		 * @param name
//		 */
//        DialogType(Image iconImage, String name, String prefix) {
//			this.iconImage = iconImage;
//			this.name = name;
//			this.prefix = prefix;
//		}
//		
//		/**
//		 * Returns the dialog icon image.
//		 * @return the dialog icon image
//		 */
//		public Image getIconImage() {
//			return this.iconImage;
//		}
//		
//		/**
//		 * Returns the name.
//		 * @return the name
//		 */
//		public String getName() {
//			return this.name;
//		}
//		
//		/**
//		 * Returns the title string.
//		 * @return the title
//		 */
//		public String getTitle() {
//			return this.prefix + " " + this.name;
//		}
//
//		/**
//		 * Processes the content wrapped by the specified dialog.
//		 * @param dialog the dialog whose contents shall be processed
//		 */
//		public abstract void processContent(GeneralDialog dialog);
//
//	}
	
	/**
	 * Enumeration holding modification operation-related elements. 
	 * @author A. Behne, R. Heyer
	 */
	public enum Operation {
		NONE, ADD, DELETE, CHANGE
	}

//	/**
//	 * The dialog type.
//	 */
//	private final GeneralDialog.DialogType type;

//	/**
//	 * The object to be manipulated by this dialog.
//	 */
////	private ProjectExperiment content;
//	private Object content;

	/**
	 * The content name text field.
	 */
	private JTextField nameTtf;

	/**
	 * The table containing property name/value pairs.
	 */
	private JXTable propertyTbl;

	/**
	 * The property name text field
	 */
	private JTextField propNameTtf;

	/**
	 * The property value text field
	 */
	private JTextField propValueTtf;

	/**
	 * The button for appending a property.
	 */
	private JButton appendPropertyBtn;

	/**
	 * The button for changing a property.
	 */
	private JButton changePropertyBtn;

	/**
	 * The button for deleting a property.
	 */
	private JButton deletePropertyBtn;

	/**
	 * The button for saving and closing the dialog.
	 */
	protected JButton saveBtn;

	/**
	 * The list of atomic operations to modify the content properties.
	 */
	private List<GeneralDialog.Operation> operations = new ArrayList<GeneralDialog.Operation>();

	/**
	 * The result code for when the dialog is closed by pressing the 'Save' button.
	 */
	public static final int RESULT_SAVED = 1;

	/**
	 * The result code for when the dialog is closed by pressing the 'Cancel' or 'X' button.
	 */
	public static final int RESULT_CANCELED = 0;

	/**
	 * The result code returned upon closing the dialog.
	 */
	protected int result = RESULT_CANCELED;

	private Map<String, String> propertyMap;

	/**
	 * Creates a project/experiment manipulation dialog using the specified
	 * dialog type and content object.
	 * @param propertyMap 
	 * @param titleString 
	 * @param type the type of dialog
	 * @param project the content object
	 */
	public GeneralDialog(String title, Image iconImage, String titleString, Map<String, String> propertyMap) {
		super(ClientFrame.getInstance(), title, true);
		this.propertyMap = propertyMap;
		this.initComponents();
		this.setIconImage(iconImage);
	}

	/**
	 * Initializes the components.
	 */
	private void initComponents() {

		// Description panel
		JPanel descriptionPnl = new JPanel();
		descriptionPnl.setBorder(BorderFactory.createTitledBorder("Description"));
		descriptionPnl.setLayout(new FormLayout(
				"5dlu, p, 5dlu, p:g, 5dlu", "2dlu, p, 5dlu"));

		// Selected project/experiment textfield label
		String name = this.getTitle() + " Name:";

//		nameTtf = new JTextField(content.getTitle());
		nameTtf = new JTextField(this.getTitle());
		nameTtf.setEditable(true);

		// ActionListener to enable/disable the 'save' button
		// depending on whether text has been entered
		nameTtf.getDocument().addDocumentListener(new DocumentListener() {
			public void removeUpdate(DocumentEvent evt) { this.updateSaveButton(); }
			public void insertUpdate(DocumentEvent evt) { this.updateSaveButton(); }
			public void changedUpdate(DocumentEvent evt) { this.updateSaveButton(); }
			private void updateSaveButton() { saveBtn.setEnabled(!nameTtf.getText().isEmpty()); }
		});

		descriptionPnl.add(new JLabel(name), CC.xy(2, 2));
		descriptionPnl.add(nameTtf, CC.xy(4, 2));

		// Properties panel
		JPanel propertyPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu", "2dlu, f:p:g, 5dlu"));

		propertyPnl.setBorder(BorderFactory.createTitledBorder(this.getTitle() + " Properties"));

		//Creates property table
		this.setupPropertiesTable();

		// Fill property table
		this.populatePropertyTable();

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
			public void changedUpdate(DocumentEvent evt) { }
		});

		propValueTtf = new JTextField();
		propValueTtf.getDocument().addDocumentListener(new DocumentListener() {
			public void removeUpdate(DocumentEvent evt) { updateModifyButtons(); }
			public void insertUpdate(DocumentEvent evt) { updateModifyButtons(); }
			public void changedUpdate(DocumentEvent evt) { }
		});

		// Button to add property
		appendPropertyBtn = new JButton("Add", IconConstants.ADD_ICON);
		appendPropertyBtn.setRolloverIcon(IconConstants.ADD_ROLLOVER_ICON);
		appendPropertyBtn.setPressedIcon(IconConstants.ADD_PRESSED_ICON);
		// Button is only useable when new name and value is filled
		appendPropertyBtn.setEnabled(false);
		appendPropertyBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
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
			public void actionPerformed(ActionEvent evt) {
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
			public void actionPerformed(ActionEvent evt) {
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
		propertyPnl.add(propertySpp, CC.xy(2, 2));

		// Save button functionality -> store values in DB
		saveBtn = new JButton("Save", IconConstants.SAVE_DB_ICON);
		this.getRootPane().setDefaultButton(saveBtn);
		saveBtn.setRolloverIcon(IconConstants.SAVE_DB_ROLLOVER_ICON);
		saveBtn.setPressedIcon(IconConstants.SAVE_DB_PRESSED_ICON);
		saveBtn.setMargin(new Insets(2, 2, 2, 2));
		saveBtn.setIconTextGap(10);
		saveBtn.setHorizontalAlignment(SwingConstants.LEFT);
		saveBtn.setFont(saveBtn.getFont().deriveFont(Font.BOLD,
				saveBtn.getFont().getSize2D() * 1.25f));

		saveBtn.setEnabled((nameTtf.getText() != null) && !nameTtf.getText().isEmpty());

//		saveBtn.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent evt) {
//				type.processContent(GeneralDialog.this);
//				result = RESULT_SAVED;
//				dispose();
//			}
//		});

		// Cancel button functionality -> dispose dialog without storing values in DB
		JButton cancelBtn = new JButton("Cancel", IconConstants.DELETE_DB_ICON);
		cancelBtn.setRolloverIcon(IconConstants.DELETE_DB_ROLLOVER_ICON);
		cancelBtn.setPressedIcon(IconConstants.DELETE_DB_PRESSED_ICON);
		cancelBtn.setMargin(new Insets(3, 2, 2, 5));
		cancelBtn.setFont(cancelBtn.getFont().deriveFont(Font.BOLD,
				cancelBtn.getFont().getSize2D()*1.25f));

		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				result = RESULT_CANCELED;
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
	 * Method creating the properties table
	 */
	private void setupPropertiesTable() {
		DefaultTableModel dtm = new DefaultTableModel() {
			{
				setColumnIdentifiers(new Object[] { "#", "Property Name", "Property Value"});
			}
			public boolean isCellEditable(int row, int col) {
				return col != 0;
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
					if (operations.get(row) != GeneralDialog.Operation.ADD)
						operations.set(row, GeneralDialog.Operation.CHANGE);
					break;
				case TableModelEvent.INSERT:
					if (operations.contains(GeneralDialog.Operation.DELETE)) {
						operations.set(operations.indexOf(GeneralDialog.Operation.DELETE), GeneralDialog.Operation.CHANGE);
					} else {
						operations.add(GeneralDialog.Operation.ADD);
					}
					break;
				case TableModelEvent.DELETE:
					for (int i = row + 1; i < operations.size(); i++) {
						if (operations.get(i) == GeneralDialog.Operation.NONE) {
							operations.set(i, GeneralDialog.Operation.CHANGE);
						} else if (operations.get(i) == GeneralDialog.Operation.ADD) {
							operations.set(i, GeneralDialog.Operation.CHANGE);
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
	 * Populates the property table using the content properties.
	 */
	private void populatePropertyTable() {
		TableConfig.clearTable(propertyTbl);

		Map<String, String> properties = this.propertyMap;
		if (properties != null) {
			int row = 1;
			for (Entry<String, String> entry : properties.entrySet()) {
				((DefaultTableModel) propertyTbl.getModel()).addRow(
						new Object[] { row++, entry.getKey(), entry.getValue() });
			}
		}

		// reset operations vector
		for (int i = 0; i < operations.size(); i++) {
			operations.set(i, GeneralDialog.Operation.NONE);
		}
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

		int numProps = this.getProperties().size();	// number of properties before modification
		if (operations.size() < numProps) {
			operations.add(GeneralDialog.Operation.DELETE);
		}
	}

	/**
	 * Displays the dialog.
	 * @return the result code
	 */
	public int showDialog() {
		ScreenConfig.centerInScreen(this);
		this.setVisible(true);
		return result;
	}

//	/**
//	 * Returns the content object wrapped by this dialog.
//	 * @return the content object
//	 */
//	public ProjectExperiment getContent() {
//		return content;
//	}

	/**
	 * Returns the content name.
	 * @return the content name
	 */
	public String getContentName() {
		return nameTtf.getText();
	}

	/**
	 * Collects the properties from the table and fills the properties map.
	 * @return the map of properties
	 */
	public Map<String, String> getProperties() {
		Map<String, String> properties = new LinkedHashMap<>();
		// read data from property table
		for (int i = 0; i < propertyTbl.getModel().getRowCount(); i++){
			properties .put(propertyTbl.getValueAt(i, 1).toString(), propertyTbl.getValueAt(i, 2).toString());
		}
		return properties;
	}

	/**
	 * Returns the list of modification operations.
	 * @return the list of operations
	 */
	public List<GeneralDialog.Operation> getOperations() {
		return this.operations;
	}

}