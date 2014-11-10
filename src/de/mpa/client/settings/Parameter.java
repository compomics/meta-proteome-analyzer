package de.mpa.client.settings;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.text.JTextComponent;

import org.apache.commons.lang3.ObjectUtils;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Class for storing search engine parameter-related variables and for providing
 * access to UI controls to manipulate them.
 * 
 * @author T. Muth, F. Kohrs, A. Behne
 */
public abstract class Parameter {
	
	/**
	 * The value of the parameter.
	 */
	private Object value;
	
	/**
	 * The default value of the parameter.
	 */
	private Object defaultValue;
	
	/**
	 * The name of the parameter.
	 */
	private String name;
	
	/**
	 * The description string of the parameter. Used for tooltips.
	 */
	private String description;

	/**
	 * The section identifier of the parameter.
	 */
	private String section;
	
	/**
	 * Constructs a configuration parameter object instance from the specified variables.
	 * @param value the value of the parameter
	 * @param name the name of the parameter
	 * @param description the description string of the parameter
	 * @param section the section identifier of the parameter
	 */
	public Parameter(Object value, String name, String description, String section) {
		this.value = value;
		this.defaultValue = value;
		this.name = name;
		this.description = description;
		this.section = section;
	}

	/**
	 * Returns the name of the parameter.
	 * @return the name of the parameter
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the value of the parameter.
	 * @return the value of the parameter
	 */
	public Object getValue() {
		return value;
	}
	
	/**
	 * Returns the value with which the parameter has been initialized.
	 * @return the default value of the parameter
	 */
	public Object getDefaultValue() {
		return defaultValue;
	}
	
	/**
	 * Sets the value of the parameter.
	 * @param value the value to set
	 * @return <code>true</code> if the value was changed, <code>false</code> otherwise
	 */
	public boolean setValue(Object value) {
		if (ObjectUtils.notEqual(this.value, value)) {
			this.value = value;
			return true;
		}
		return false;
	}

	/**
	 * Returns the section identifier of the parameter.
	 * @return the section identifier of the parameter
	 */
	public String getSection() {
		return section;
	}

	/**
	 * Returns the description string of the parameter.
	 * @return the description string of the parameter
	 */
	public String getDescription() {
		return description;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Parameter) {
			Parameter that = (Parameter) obj;
			return (this.getValue().equals(that.getValue()));
		}
		return false;
	}
	
	/**
	 * Creates and returns the left-hand side component.
	 * @return the left-hand side component
	 */
	public JComponent createLeftComponent() {
		JLabel label = new JLabel(name);
		label.setToolTipText(description);
		return label;
	}
	
	/**
	 * Creates and returns the right-hand side component.
	 * @return the right-hand side component
	 */
	public JComponent createRightComponent() {
		return null;
	}
	
	/**
	 * Applies the changes made to the parameter UI controls to the stored
	 * value.
	 */
	public abstract boolean applyChanges();
	
	/**
	 * Restores the default value of the parameter.
	 */
	public void restoreDefaults() {
		this.setValue(this.getDefaultValue());
	}
	
	/**
	 * Parameter implementation for numeric values. Editable via spinner.
	 * @author A. Behne
	 */
	public static class NumberParameter extends Parameter {
		
		/**
		 * The lower bound of the value range.
		 */
		private Comparable minimum;
		
		/**
		 * The upper bound of the value range.
		 */
		private Comparable maximum;
		
		/**
		 * The spinner component.
		 */
		private JSpinner spinner;
		
		/**
		 * Flag indicating whether the spinner component is editable.
		 */
		private boolean editable;
		
		/**
		 * Creates a number parameter using the specified value inside the range
		 * defined by the provided minimum and maximum values.
		 * @param value the numeric value
		 * @param minimum the lower bound of the value range
		 * @param maximum the upper bound of the value range
		 * @param name the parameter name
		 * @param description the parameter description
		 * @param section the section identifier
		 */
		public NumberParameter(Number value, Comparable minimum, Comparable maximum, 
				String name, String description, String section) {
			this(value, minimum, maximum, true, name, description, section);
		}
		
		/**
		 * Creates a number parameter using the specified value inside the range
		 * defined by the provided minimum and maximum values. The <code>editable</code>
		 * flag determines whether the associated spinner can be interacted with.
		 * @param value the numeric value
		 * @param minimum the lower bound of the value range
		 * @param maximum the upper bound of the value range
		 * @param editable <code>true</code> if editable, <code>false</code> otherwise
		 * @param name the parameter name
		 * @param description the parameter description
		 * @param section the section identifier
		 */
		public NumberParameter(Number value, Comparable minimum, Comparable maximum, 
				boolean editable, String name, String description, String section) {
			super(value, name, description, section);
			this.minimum = minimum;
			this.maximum = maximum;
			this.editable = editable;
		}
		
		/**
		 * Returns whether this parameter's text component is editable.
		 * @return <code>true</code> if editable, <code>false</code> otherwise
		 */
		public boolean isEditable() {
			return editable;
		}
		
		@Override
		public boolean setValue(Object value) {
			Number number = null;
			if (value instanceof Number) {
				number = (Number) value;
			} else {
				if (value instanceof String) {
					try {
						number = Double.valueOf((String) value);
					} catch (Exception e) {
						// do nothing, silently fail
					}
				}
			}
			if (number != null) {
				if (spinner != null) {
					spinner.setValue(number);
				}
				return super.setValue(number);
			}
			return false;
		}
		
		@Override
		public JComponent createRightComponent() {
			Number value = (Number) this.getValue();
			Number stepSize = (value instanceof Integer) ? Integer.valueOf(1) : Double.valueOf(0.01);
			spinner = new JSpinner(new SpinnerNumberModel(
					value, minimum, maximum, stepSize));
			if (value instanceof Double) {
				spinner.setEditor(new JSpinner.NumberEditor(spinner, "0.00"));
			}
			spinner.setToolTipText(this.getDescription());
			if (!this.isEditable()) {
				spinner.setEnabled(false);
				JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner.getEditor();
				editor.getTextField().setEnabled(true);
				editor.getTextField().setEditable(false);
			}
			return spinner;
		}

		@Override
		public boolean applyChanges() {
			Object oldValue = this.getValue();
			Object newValue = spinner.getValue();
			this.setValue(newValue);
			return !oldValue.equals(newValue);
		}
		
		@Override
		public void restoreDefaults() {
			if (this.isEditable()) {
				super.restoreDefaults();
				spinner.setValue(this.getValue());
			}
		}

	}
	
	/**
	 * Parameter implementation for logical values. Editable via checkbox.
	 * @author A. Behne
	 */
	public static class BooleanParameter extends Parameter {
		
		/**
		 * The checkbox component.
		 */
		private JCheckBox checkBox;
		
		/**
		 * Creates a boolean parameter using the provided state.
		 * @param value the boolean state
		 * @param name the parameter name
		 * @param description the parameter description
		 * @param section the section identifier
		 */
		public BooleanParameter(boolean value, String name, String description, String section) {
			super(value, name, description, section);
		}
		
		@Override
		public JComponent createLeftComponent() {
			checkBox = new JCheckBox(this.getName(), (Boolean) this.getValue());
			checkBox.setToolTipText(this.getDescription());
			
			checkBox.setHorizontalTextPosition(SwingConstants.LEFT);
			checkBox.setHorizontalAlignment(SwingConstants.RIGHT);
			checkBox.setIconTextGap(7);
			
			return checkBox;
		}

		@Override
		public boolean applyChanges() {
			Object oldValue = this.getValue();
			boolean newValue = checkBox.isSelected();
			this.setValue(newValue);
			return !oldValue.equals(newValue);
		}

		@Override
		public void restoreDefaults() {
			super.restoreDefaults();
			checkBox.setSelected((Boolean) this.getValue());
		}
		
	}
	
	/**
	 * Parameter implementation for a matrix of logical values. Editable via
	 * multiple checkboxes.
	 * @author A. Behne
	 */
	// TODO: generalize class to arrange arbitrary parameters in matrix
	public static class BooleanMatrixParameter extends Parameter {
		
		/**
		 * The number of columns of the logical matrix.
		 */
		private int width;
		
		/**
		 * The number of rows of the logical matrix.
		 */
		private int height;
		
		/**
		 * The logical value parameters.
		 */
		private BooleanParameter[] params;
		
		/**
		 * Creates a boolean matrix parameter with the specified dimensions and
		 * states.
		 * @param width the number of columns of the logical matrix
		 * @param height the number of rows of the logical matrix
		 * @param selected the logical states
		 * @param names the sub-parameter names
		 * @param descriptions the sub-parameter descriptions
		 * @param section the section identifier
		 */
		public BooleanMatrixParameter(int width, int height, Boolean[] selected,
				String[] names, String[] descriptions, String section) {
			super(selected, null, null, section);
			this.width = width;
			this.height = height;

			params = new BooleanParameter[selected.length];
			for (int i = 0; i < selected.length; i++) {
				params[i] = new BooleanParameter(selected[i],
						names[i], descriptions[i], null);
			}
		}
		
		/**
		 * Returns the value of the sub-parameter at the specified coordinates.
		 * @param row the row index
		 * @param col the column index
		 * @return the value
		 */
		public Object getValue(int row, int col) {
			return params[row * width + col].getValue();
		}
		
		@Override
		public JComponent createLeftComponent() {
			// Matrix of checkboxes
			FormLayout layout = new FormLayout("p:g");
//			layout.addGroupedColumn(1);
			for (int i = 1; i < width; i++) {
				layout.appendColumn(ColumnSpec.decode("5dlu"));
				layout.appendColumn(ColumnSpec.decode("p:g"));
//				layout.addGroupedColumn(2 * i + 1);
			}
			
			DefaultFormBuilder builder = new DefaultFormBuilder(layout);
			int i = 0;
			outer:
			for (int row = 0; row < height; row++) {
				for (int col = 0; col < width; col++) {
					if (i >= params.length) {
						break outer;
					}
					builder.append(params[i].createLeftComponent());
					i++;
				}
				builder.nextLine();
			}
			
			return builder.getPanel();
		}

		@Override
		public boolean applyChanges() {
			Boolean[] selected = (Boolean[]) this.getValue();
			boolean res = false;
			for (int i = 0; i < params.length; i++) {
				res |= params[i].applyChanges();
				selected[i] = (Boolean) params[i].getValue();
			}
			this.setValue(selected);
			return res;
		}

		@Override
		public void restoreDefaults() {
			super.restoreDefaults();
			for (BooleanParameter param : params) {
				param.restoreDefaults();
			}
		}
		
	}
	
	/**
	 * Parameter implementation for textual values. Editable via textfield.
	 * @author A. Behne
	 */
	public static class TextParameter extends Parameter {
		
		/**
		 * The text component.
		 */
		protected JTextComponent textComp;
		
		/**
		 * Flag indicating whether the text component is editable.
		 */
		private boolean editable;
		
		/**
		 * Creates a text parameter using the specified value.
		 * @param value the text value
		 * @param name the parameter name
		 * @param description the parameter description
		 * @param section the section identifier
		 */
		public TextParameter(String value, String name, String description, String section) {
			this(value, true, name, description, section);
		}
		
		/**
		 * Creates a text parameter using the specified value. The <code>editable</code>
		 * flag determines whether the associated text component can be interacted with.
		 * @param value the text value
		 * @param editable <code>true</code> if editable, <code>false</code> otherwise
		 * @param name the parameter name
		 * @param description the parameter description
		 * @param section the section identifier
		 */
		public TextParameter(String value, boolean editable, String name, String description, String section) {
			super(value, name, description, section);
			this.editable = editable;
		}
		
		/**
		 * Returns whether this parameter's text component is editable.
		 * @return <code>true</code> if editable, <code>false</code> otherwise
		 */
		public boolean isEditable() {
			return editable;
		}
		
		@Override
		public JComponent createRightComponent() {
			textComp = new JTextField((String) this.getValue());
			textComp.setToolTipText(this.getDescription());
			textComp.setEditable(this.isEditable());
			return textComp;
		}

		@Override
		public boolean applyChanges() {
			Object oldValue = this.getValue();
			String newValue = textComp.getText();
			this.setValue(newValue);
			return !oldValue.equals(newValue);
		}

		@Override
		public void restoreDefaults() {
			super.restoreDefaults();
			textComp.setText((String) this.getValue());
		}
		
	}
	
	/**
	 * Text parameter implementation for multi-line textual values. Editable via
	 * text area.
	 * @author A. Behne
	 */
	public static class TextAreaParameter extends TextParameter {

		/**
		 * Creates a multi-line text parameter using the specified value.
		 * @param value the text value
		 * @param name the parameter name
		 * @param description the parameter description
		 * @param section the section identifier
		 */
		public TextAreaParameter(String value, String name, String description,
				String section) {
			super(value, name, description, section);
		}
		
		@Override
		public JComponent createRightComponent() {
			textComp = new JTextArea((String) this.getValue());
			textComp.setToolTipText(this.getDescription());
			textComp.setEditable(this.isEditable());
			JScrollPane scrollPane = new JScrollPane(textComp,
					JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			return scrollPane;
		}
		
	}
	
	/**
	 * Text parameter implementation for password texts. Editable via password
	 * field.
	 * @author A. Behne
	 */
	public static class PasswordParameter extends TextParameter {
		
		/**
		 * Creates a password parameter using the specified value.
		 * @param value the password string
		 * @param name the parameter name
		 * @param description the parameter description
		 * @param section the section identifier
		 */
		public PasswordParameter(String value, String name, String description,
				String section) {
			super(value, name, description, section);
		}

		@Override
		public JComponent createRightComponent() {
			textComp = new JPasswordField((String) this.getValue());
			textComp.setToolTipText(this.getDescription());
			textComp.setEditable(this.isEditable());
			return textComp;
		}
		
		@Override
		public boolean applyChanges() {
			Object oldValue = this.getValue();
			String newValue = new String(((JPasswordField) textComp).getPassword());
			this.setValue(newValue);
			return !oldValue.equals(newValue);
		}
		
	}
	
	/**
	 * Parameter implementation for arbitrary values. Editable via combobox.
	 * @author A. Behne
	 */
	public static class OptionParameter extends Parameter {
		
		/**
		 * The available options.
		 */
		private Object[] options;
		
		/**
		 * The selected index.
		 */
		private int index;
		
		/**
		 * The combobox component.
		 */
		private JComboBox<Object> comboBox;
		
		/**
		 * Creates an option parameter from the specified options and selection index.
		 * @param options the available options
		 * @param index the selected option index
		 * @param name the parameter name
		 * @param description the parameter description
		 * @param section the section identifier
		 */
		public OptionParameter(Object[] options, int index,
				String name, String description, String section) {
			super(options[index], name, description, section);
			this.options = options;
			this.index = index;
		}
		
		@Override
		public JComponent createRightComponent() {
			comboBox = new JComboBox<>(options);
			comboBox.setSelectedIndex(index);
			comboBox.setToolTipText(this.getDescription());
			return comboBox;
		}
		
		@Override
		public boolean applyChanges() {
			Object oldValue = this.getValue();
			index = comboBox.getSelectedIndex();
			Object newValue = comboBox.getItemAt(index);
			this.setValue(newValue);
			return !oldValue.equals(newValue);
		}
		
		/**
		 * Returns the selected index.
		 * @return the index
		 */
		public int getIndex() {
			return index;
		}
	
		@Override
		public void restoreDefaults() {
			super.restoreDefaults();
			comboBox.setSelectedItem(this.getValue());
		}
		
	}

	/**
	 * Parameter implementation for displaying a button with a specified action.
	 * Does not actually store a value.
	 * @author A. Behne
	 */
	public static class ButtonParameter extends Parameter {
		
		/**
		 * The button action
		 */
		private Action action;

		/**
		 * Creates a button parameter using the specified action.
		 * @param action the action
		 * @param section the section identifier
		 */
		public ButtonParameter(Action action, String section) {
			super(null, null, null, section);
			this.action = action;
		}
		
		@Override
		public JComponent createLeftComponent() {
			return new JButton(action);
		}

		@Override
		public boolean applyChanges() {
			// do nothing
			return false;
		}
		
	}

}
