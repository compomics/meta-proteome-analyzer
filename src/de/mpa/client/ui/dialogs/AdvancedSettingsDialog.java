package de.mpa.client.ui.dialogs;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.text.JTextComponent;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.VerticalLayout;
import org.jdesktop.swingx.plaf.misc.GlossyTaskPaneUI;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

import de.mpa.client.settings.Parameter;
import de.mpa.client.settings.ParameterMap;
import de.mpa.client.ui.ScreenConfig;
import de.mpa.client.ui.icons.IconConstants;

/**
 * Dialog displaying advanced search settings dynamically generated from a set of parameters.
 * 
 * @author T. Muth, A. Behne
 */
public class AdvancedSettingsDialog extends JDialog {
	
	public static final int DIALOG_CANCELLED = 0;
	
	public static final int DIALOG_ACCEPTED = 1;
	
	public static final int DIALOG_CHANGED = 2;
	
	public static final int DIALOG_CHANGED_ACCEPTED = 3;
	
	/**
	 * The collection of parameters this dialog shall provide interaction with.
	 */
	private ParameterMap parameterMap;
	
	/**
	 * Map linking parameters to their respective control component.
	 */
	Map<String, JComponent> param2comp = new HashMap<String, JComponent>();
	
	/**
	 * The status of the dialog indicating for instance whether parameters got changed or if the dialog was cancelled.
	 */
	private int status = 0;

	/**
	 * Constructs and displays a dialog dynamically generated from the specified
	 * collection of parameters and provides controls to interact with them.
	 * @param owner the <code>Frame</code> from which the dialog is displayed
	 * @param title the <code>String</code> to display in the dialog's title bar
	 * @param modal specifies whether dialog blocks user input to other top-level windows when shown.
	 * @param parameterMap the collection of parameters upon which the dialog shall be built
	 */
	private AdvancedSettingsDialog(Frame owner, String title, boolean modal, ParameterMap parameterMap) {
		super(owner, title, modal);
		this.parameterMap = parameterMap;
		this.initComponents();
		
		// Configure size and position
		this.pack();
		Dimension size = this.getSize();
		this.setSize(new Dimension(size.width, size.height + 7));
		this.setResizable(false);
		ScreenConfig.centerInScreen(this);
		
		// Show dialog
		this.setVisible(true);
	}
	
	/**
	 * Displays a dialog dynamically generated from the specified collection of
	 * parameters and provides controls to interact with them.
	 * @param owner the <code>Frame</code> from which the dialog is displayed
	 * @param title the <code>String</code> to display in the dialog's title bar
	 * @param modal specifies whether dialog blocks user input to other top-level windows when shown.
	 * @param parameterMap the collection of parameters upon which the dialog shall be built
	 * @return the dialog status
	 */
	public static int showDialog(Frame owner, String title, boolean modal, ParameterMap parameterMap) {
		return new AdvancedSettingsDialog(owner, title, modal, parameterMap).getStatus();
	}
	
	/**
	 * Returns the flag indicating whether the parameters were changed in some way.
	 * @return the dialog status
	 */
	private int getStatus() {
		return status;
	}

	/**
	 * Groups parameters by their section.
	 * @return a map containing lists of parameters mapped to their section key.
	 */
	private Map<String, List<Parameter>> getSectionMap(List<Parameter> params) {
		Map<String, List<Parameter>> sectionMap = new LinkedHashMap<String, List<Parameter>>();
		for (Parameter p : params) {
			List<Parameter> sectionParams = sectionMap.get(p.getSection());
			if (sectionParams == null) {
				sectionParams = new ArrayList<Parameter>();
			}
			sectionParams.add(p);
			sectionMap.put(p.getSection(), sectionParams);
		}
		return sectionMap;
	}
	
	/**
	 * Initializes and lays out all components inside this dialog grouped by sections identifiers.
	 * @param params The ungrouped list of parameters to display
	 */
	private void initComponents() {
		List<Parameter> params = parameterMap.getParameters();

		// Define dialog content pane layout
		Container contentPane = this.getContentPane();
		contentPane.setLayout(new FormLayout("5dlu, p:g, 5dlu",
				"5dlu, f:p:g, 5dlu, p, 5dlu"));
		
		// Init task pane container
		final JXTaskPaneContainer tpc = new JXTaskPaneContainer();
		((VerticalLayout) tpc.getLayout()).setGap(10);
		
		// Group parameters by section identifiers
		Map<String, List<Parameter>> sectionMap = this.getSectionMap(params);
		
		// Iterate sections
		for (Entry<String, List<Parameter>> entry : sectionMap.entrySet()) {
			String key = entry.getKey();
			// Exclude general settings
			if (!key.equals("General")) {
				// Create collapsible task pane for section
				JXTaskPane taskPane = new JXTaskPane(key);
				taskPane.setUI(new GlossyTaskPaneUI());
				
				// Apply component listener to synchronize task pane size with dialog size
				taskPane.addComponentListener(new ComponentAdapter() {
					@Override
					public void componentResized(ComponentEvent e) {
						pack();
					}
				});
				
				// Set up builder for the section
				DefaultFormBuilder builder = new DefaultFormBuilder(
						new FormLayout("p, 5dlu, p:g"), taskPane);
				builder.setDefaultRowSpec(RowSpec.decode("f:p:g"));
				
				// Iterate parameters to build section components
				for (Parameter p : entry.getValue()) {
					JComponent comp = this.createParameterControl(p);
					if (comp instanceof JCheckBox) {
						// Special case for checkboxes which come with labels of their own
						builder.append(comp, 3);
					} else if (comp instanceof JPanel) {
						// check sub-components for possible radio buttons
						JRadioButton radioBtn = null;
						ButtonGroup bg = new ButtonGroup();
						for (Component childComp : comp.getComponents()) {
							if (childComp instanceof JRadioButton) {
								// add found radio button to button group
								radioBtn = (JRadioButton) childComp;
								bg.add(radioBtn);
							}
						}
						// add panel to task pane layout
						builder.append(comp, 3);
					} else {
						// Add component, generate label to go with it
						String name = p.getName();
						if ((name != null) && !name.isEmpty()) {
							// parameters with non-null and non-empty names get a label
							JLabel label = new JLabel(name);
							if (comp instanceof JScrollPane) {
								label.setVerticalAlignment(SwingConstants.TOP);
							}
							label.setToolTipText(p.getDescription());
							builder.append(label, comp);
						} else {
							builder.append(comp, 3);
						}
					}
					builder.nextLine();
				}
				tpc.add(taskPane);
			}
		}
		
		// Configure button panel containing 'OK' and 'Cancel' options
		FormLayout layout = new FormLayout("p, 5dlu:g, p, 5dlu, p", "p");
		layout.setColumnGroups(new int[][] { { 3, 5 } });
		JPanel buttonPnl = new JPanel(layout);
		
		// Configure 'Restore Defaults' button
		JButton restoreBtn = new JButton("Restore Defaults", IconConstants.UPDATE_ICON);
		restoreBtn.setRolloverIcon(IconConstants.UPDATE_ROLLOVER_ICON);
		restoreBtn.setPressedIcon(IconConstants.UPDATE_PRESSED_ICON);
		restoreBtn.setHorizontalAlignment(SwingConstants.LEFT);
		restoreBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				restoreDefaults();
			}
		});
//		restoreBtn.setToolTipText("Reset all values to their defaults");
		
		// Configure 'OK' button
		JButton okBtn = new JButton("OK", IconConstants.CHECK_ICON);
		okBtn.setRolloverIcon(IconConstants.CHECK_ROLLOVER_ICON);
		okBtn.setPressedIcon(IconConstants.CHECK_PRESSED_ICON);
		okBtn.setHorizontalAlignment(SwingConstants.LEFT);
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				applyChanges();
				status |= DIALOG_ACCEPTED;
				dispose();
			}
		});
//		okBtn.setToolTipText("Accept changes and dismiss dialog.");
		
		// Configure 'Cancel' button
		JButton cancelBtn = new JButton("Cancel", IconConstants.CROSS_ICON);
		cancelBtn.setRolloverIcon(IconConstants.CROSS_ROLLOVER_ICON);
		cancelBtn.setPressedIcon(IconConstants.CROSS_PRESSED_ICON);
		cancelBtn.setHorizontalAlignment(SwingConstants.LEFT);
		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				status = DIALOG_CANCELLED;
				dispose();
			}
		});
//		cancelBtn.setToolTipText("Discard changes and dismiss dialog.");
		
		// Align label of 'OK' button
		int labelWidth = getFontMetrics(okBtn.getFont()).stringWidth("OK");
		okBtn.setIconTextGap(cancelBtn.getPreferredSize().width/2 -
				okBtn.getIcon().getIconWidth() - labelWidth/2);
		
		// Lay out button panel
		buttonPnl.add(restoreBtn, CC.xy(1, 1));
		buttonPnl.add(okBtn, CC.xy(3, 1));
		buttonPnl.add(cancelBtn, CC.xy(5, 1));
		
		// Add final components to content pane
		contentPane.add(tpc, CC.xy(2, 2));
		contentPane.add(buttonPnl, CC.xy(2, 4));
	}

	/**
	 * Applies changes made via control components to the underlying parameter
	 * collection.
	 */
	protected void applyChanges() {
		// Update parameters
		for (Parameter param : parameterMap.values()) {
			JComponent comp = param2comp.get(param.getName());
			if (comp == null) {
				// skip default values
				continue;
			}
			if (comp instanceof JCheckBox) {
				if (param.setValue(((JCheckBox) comp).isSelected())) {
					this.status |= DIALOG_CHANGED;
				}
			} else if (comp instanceof JPanel) {
				// assume we are dealing with a matrix of checkboxes
				Object[][] values = (Object[][]) param.getValue();
				int k = 0;
				for (int i = 0; i < values.length; i++) {
					for (int j = 0; j < 2; j++) {
						Component subComp = comp.getComponent(k++);
						if (subComp instanceof AbstractButton) {
							values[i][j] = ((AbstractButton) subComp).isSelected();
						} else if (subComp instanceof JSpinner) {
							values[i][j] = ((JSpinner) subComp).getValue();
						}
					}
				}
				if (param.setValue(values)) {
					this.status |= DIALOG_CHANGED;
				}
				
				Object[][] rows = (Object[][]) param.getValue();
				int counter = 0;
				if (rows instanceof Boolean[][]) {
					// matrix of checkboxes
					for (Boolean[] row : (Boolean[][]) rows) {
						for (Boolean sel : row) {
							((AbstractButton) comp.getComponent(counter++)).setSelected(sel);
						}
					}
				} else {
					// stack of radio button-and-other component pairs
					for (Object[] row : rows) {
						Component rightComp = comp.getComponent(++counter);
						if (rightComp instanceof JSpinner) {
							((JSpinner) rightComp).setValue(row[1]);
						}
						counter++;
					}
				}
			} else if (comp instanceof JComboBox) {
				ComboBoxModel model = ((JComboBox) comp).getModel();
//				param.setValue(model);
				if (!model.getSelectedItem().equals(comp.getClientProperty("initialValue"))) {
					this.status |= DIALOG_CHANGED;
				}
			} else if (comp instanceof JSpinner) {
				Object newValue = ((JSpinner) comp).getValue();
				if (param.getValue() instanceof Number[]) {
					Number[] value = (Number[]) param.getValue();
					if (!value[0].equals(newValue)) {
						this.status |= DIALOG_CHANGED;
					}
					value[0] = (Number) newValue;
				} else {
					if (param.setValue(newValue)) {
						this.status |= DIALOG_CHANGED;
					}
				}
			} else if (comp instanceof JScrollPane) {
				if (param.setValue(((JTextComponent) ((JScrollPane) comp).getViewport().getView()).getText())) {
					this.status |= DIALOG_CHANGED;
				}
			} else {
				// When we get here something went wrong - investigate!
				System.err.println("AdvancedSettingsDialog error:" + comp.getClass());
			}
		}
	}

	/**
	 * Resets all parameters and their respective control components to their
	 * default values.
	 */
	protected void restoreDefaults() {
		// Reset parameters
		this.parameterMap.initDefaults();
		this.status = DIALOG_CHANGED;
		// Update controls
		for (Parameter param : parameterMap.values()) {
			JComponent comp = param2comp.get(param.getName());
			if (comp == null) {
				// skip default values
				continue;
			}
			if (comp instanceof JCheckBox) {
				((JCheckBox) comp).setSelected((Boolean) param.getValue());
			} else if (comp instanceof JPanel) {
				Object[][] rows = (Object[][]) param.getValue();
				int counter = 0;
				if (rows instanceof Boolean[][]) {
					// matrix of checkboxes
					for (Boolean[] row : (Boolean[][]) rows) {
						for (Boolean sel : row) {
							((AbstractButton) comp.getComponent(counter++)).setSelected(sel);
						}
					}
				} else {
					// stack of radio button-and-other component pairs
					for (Object[] row : rows) {
						Component rightComp = comp.getComponent(++counter);
						if (rightComp instanceof JSpinner) {
							((JSpinner) rightComp).setValue(row[1]);
						}
						counter++;
					}
				}
			} else if (comp instanceof JComboBox) {
				JComboBox comboBox = (JComboBox) comp;
				comboBox.setSelectedItem(comboBox.getClientProperty("initialValue"));
			} else if (comp instanceof JSpinner) {
				if (param.getValue() instanceof Number[]) {
					((JSpinner) comp).setValue(((Number[]) param.getValue())[0]);
				} else {
					((JSpinner) comp).setValue(param.getValue());
				}
			} else if (comp instanceof JScrollPane) {
				((JTextComponent) ((JScrollPane) comp).getViewport().getView()).setText(param.getValue().toString());
			} else {
				// When we get here something went wrong - investigate!
				System.out.println("UH OH " + comp);
			}
		}
	}

	/**
	 * Creates and returns a GUI component to interact with the specified parameter.<br>
	 * The type of the parameter value determines the kind of control that will be generated:<ul>
	 * <li>JCheckBox for <code>Boolean</code></li>
	 * <li>JSpinner for <code>Integer</code> or <code>Double</code></li>
	 * <li>JComboBox for <code>Object[]</code></li></ul>
	 * @param param the parameter
	 * @return the parameter control component
	 */
	private JComponent createParameterControl(final Parameter param) {
		JComponent comp = null;
		Object value = param.getValue();
		// Determine type of value and generate appropriate control component
		if (value instanceof JComponent) {
			return (JComponent) value;
		} else if (value instanceof Boolean) {
			// Checkbox
			JCheckBox checkBox = new JCheckBox(param.getName(), null, (Boolean) param.getValue());
			checkBox.setHorizontalAlignment(SwingConstants.RIGHT);
			checkBox.setHorizontalTextPosition(SwingConstants.LEFT);
			checkBox.setIconTextGap(15);
			checkBox.setToolTipText(param.getDescription());
			Insets margin = checkBox.getMargin();
			margin.left = -11;
			checkBox.setMargin(margin);
			comp = checkBox;
		} else if (value instanceof Boolean[][]) {
			// Matrix of checkboxes
			Boolean[][] values = (Boolean[][]) value;
			String[] rows = param.getName().split("\\|\\|");
			String[] trows = param.getDescription().split("\\|\\|");
			String encodedColumnSpecs = "";
			int len = rows[0].split("\\|").length;
			for (int i = 1; i < len; i++) {
				encodedColumnSpecs += "p:g, 5dlu, ";
			}
			encodedColumnSpecs += "p";
			DefaultFormBuilder builder = new DefaultFormBuilder(
					new FormLayout(encodedColumnSpecs));
			for (int i = 0; i < rows.length; i++) {
				String[] cols = rows[i].split("\\|");
				String[] tcols = trows[i].split("\\|");
				for (int j = 0; j < cols.length; j++) {
					JCheckBox checkbox = new JCheckBox(cols[j], values[i][j]);
					checkbox.setToolTipText(tcols[j]);
					builder.append(checkbox);
				}
				builder.nextLine();
			}
			comp = builder.getPanel();
		} else if (value instanceof Object[][]) {
			Object[][] values = (Object[][]) value;
			String[] names = param.getName().split("\\|");
			String[] tooltips = param.getDescription().split("\\|");
			String encodedColumnSpecs = "";
			for (int i = 1; i < values[0].length; i++) {
				encodedColumnSpecs += "l:p:g, 5dlu, ";
			}
			encodedColumnSpecs += "p:g";
			DefaultFormBuilder builder = new DefaultFormBuilder(
					new FormLayout(encodedColumnSpecs));
			for (int i = 0; i < values.length; i++) {
				JRadioButton radioButton = null;
				for (int j = 0; j < values[0].length; j++) {
					Object val = values[i][j];
//					if (val instanceof Boolean) {
					switch (j) {
					case 0:
						radioButton = new JRadioButton(names[i], null, (Boolean) val);
						radioButton.setIconTextGap(15);
						radioButton.setToolTipText(tooltips[i]);
						comp = radioButton;
						break;
					case 1:
						if (val instanceof Number) {
							comp = createParameterControl(new Parameter(param.getName(), val, param.getSection(), tooltips[i]));
						}
						comp.setEnabled((Boolean) values[i][0]);
						final Component temp = comp;
						radioButton.addItemListener(new ItemListener() {
							@Override
							public void itemStateChanged(ItemEvent evt) {
								boolean sel = evt.getStateChange() == ItemEvent.SELECTED;
								temp.setEnabled(sel);
							}
						});
						break;
					case 2:
						radioButton.setEnabled((Boolean) values[i][j]);
						break;
					}
					builder.append(comp);
				}
				builder.nextLine();
			}
			comp = builder.getPanel();
		} else if (value instanceof ComboBoxModel<?>) {
			// Combobox
			JComboBox<Object> comboBox = new JComboBox<>((ComboBoxModel<Object>) value);
			comboBox.putClientProperty("initialValue", comboBox.getSelectedItem());
			comboBox.setToolTipText(param.getDescription());
			comp = comboBox;
		} else if (value instanceof Double) {
			// Spinner with 2 decimal places
			JSpinner spinner = new JSpinner(new SpinnerNumberModel((Double) value, 0.0, null, 0.01));
			spinner.setEditor(new JSpinner.NumberEditor(spinner, "0.00"));
			spinner.setToolTipText(param.getDescription());
			comp = spinner;
		} else if (value instanceof Integer) {
			// Spinner without decimal places
			JSpinner spinner = new JSpinner(new SpinnerNumberModel((Integer) value, 0, null, 1));
			spinner.setToolTipText(param.getDescription());
			comp = spinner;
		} else if (value instanceof Integer[]) {
			// Spinner without decimal places with defined min and max
			Integer[] values = (Integer[]) value;
			JSpinner spinner = new JSpinner(new SpinnerNumberModel(
					values[0].intValue(), values[1].intValue(), values[2].intValue(), 1));
			spinner.setToolTipText(param.getDescription());
			comp = spinner;
		} else if (value instanceof Double[]) {
			// Spinner without decimal places with defined min and max
			Double[] values = (Double[]) value;
			JSpinner spinner = new JSpinner(new SpinnerNumberModel(
					values[0].doubleValue(), values[1].doubleValue(), values[2].doubleValue(), 0.01));
			spinner.setToolTipText(param.getDescription());
			comp = spinner;
		} else if (value instanceof String) {
			// Text field or text area
			if (value.toString().contains("\n")) {
				comp = new JScrollPane(new JTextArea(value.toString()),
						JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
						JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			} else {
				comp = new JTextField(value.toString());
			}
		} else {
			// When we get here something went wrong - investigate!
			System.out.println("UH OH " + value.getClass());
		}
		param2comp.put(param.getName(), comp);
		return comp;
	}
	
}
