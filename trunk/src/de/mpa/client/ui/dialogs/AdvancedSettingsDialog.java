package de.mpa.client.ui.dialogs;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.settings.Parameter;
import de.mpa.client.settings.ParameterSet;
import de.mpa.client.ui.ScreenConfig;

/**
 * This dialog dynamically shows the advanced settings.
 * 
 * @author Thilo muth
 */
public class AdvancedSettingsDialog extends JDialog {

	/**
	 * List of parameters.
	 */
	private List<Parameter> params;
	
	/**
	 * Mapping from section to list of parameters.
	 */
	private HashMap<String, List<Parameter>> sectionMap; 
	
	/**
	 * Constructor for the AdvancedSettingsDialog.
	 * @param owner
	 * @param title
	 * @param modal
	 * @param parameterSet
	 */
	public AdvancedSettingsDialog(Frame owner, String title, boolean modal, ParameterSet parameterSet) {
		super(owner, title, modal);
		this.setResizable(false);
		this.params = parameterSet.getParameters();
		initComponents();
	}
	
	/**
	 * Returns the section set.
	 * @return Section set.
	 */
	private Set<String> getSectionSet() {
		Set<String> sectionSet = new HashSet<String>();
		sectionMap = new HashMap<String, List<Parameter>>();
		for (Parameter p : params) {
			sectionSet.add(p.getSection());
			if (sectionMap.containsKey(p.getSection())) {
				List<Parameter> params = sectionMap.get(p.getSection());
				params.add(p);
				sectionMap.put(p.getSection(), params);
			} else {
				List<Parameter> params = new ArrayList<Parameter>();
				params.add(p);
				sectionMap.put(p.getSection(), params);
			}
		}
		return sectionSet;
	}
	
	/**
	 * 
	 */
	private void initComponents() {
		
		JXTaskPaneContainer tpc = new JXTaskPaneContainer();
		
		Set<String> sections = getSectionSet();
		
		// Iterate the sections.
		for (String s : sections) {
			List<Parameter> params = sectionMap.get(s);
			int size = params.size();
			String rowLayout = "";
			for (int i = 0; i < size; i++) {
				rowLayout += "5dlu, p, ";
			}
			rowLayout += "5dlu";
			
			JXTaskPane taskPane = new JXTaskPane(s);
			taskPane.setLayout(new FormLayout("5dlu, p, 5dlu, p:g, 5dlu", rowLayout));

			// Fill the section
			int j = 1;
			for (Parameter p : params) {
				JComponent comp = createValueComponent(p);
				if (comp instanceof JCheckBox) {
					taskPane.add(comp, CC.xyw(2, j*2, 3));
				} else {
					taskPane.add(new JLabel(p.getName()), CC.xy(2, j*2));
					taskPane.add(comp, CC.xy(4, j*2));
				}
				j++;
			}
			tpc.add(taskPane);
		}

			
		// Add panels to the dialog content pane
		Container contentPane = this.getContentPane();
		contentPane.setLayout(new FormLayout("5dlu, p:g, 5dlu",
				"5dlu, f:p:g, 5dlu"));
		
		// Add Panels to content pane
//		contentPane.add(descriptionPnl,CC.xyw(2,2,3));
//		contentPane.add(propertyPnl,CC.xyw(2,4,3));
//		contentPane.add(saveBtn, CC.xy(2,6));
//		contentPane.add(cancelBtn, CC.xy(4,6));
		
		contentPane.add(tpc, CC.xy(2, 2));
		// Set the preferred size
		this.pack();
		Dimension size = this.getSize();
		this.setMinimumSize(new Dimension(size.width, size.height + 6));
		
		ScreenConfig.centerInScreen(this);
		
		this.setVisible(true);
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	private JComponent createValueComponent(Parameter param) {
		Object value = param.getValue();
		Class type = param.getType();
		if (type == Boolean.class) {
			JCheckBox checkBox = new JCheckBox(param.getName(), (Boolean) param.getValue());
			checkBox.setHorizontalAlignment(SwingConstants.RIGHT);
			checkBox.setHorizontalTextPosition(SwingConstants.LEFT);
			checkBox.setIconTextGap(15);
			return checkBox;
		} else if (type == List.class) {
			JComboBox comboBox = new JComboBox((Object[]) value);

			return comboBox;
		} else if (type == Double.class) {
			JSpinner spinner = new JSpinner(new SpinnerNumberModel((Number) value, 0.0, null, 0.01));
			spinner.setEditor(new JSpinner.NumberEditor(spinner, "0.00"));
			return spinner;
		} else if (type == Integer.class) {
			return new JSpinner(new SpinnerNumberModel((Number) value, 0, null, 1));
		} else {
			System.out.println("UH OH " + value.getClass());
			return null;
		}
	}
}
