package de.mpa.client.ui.dialogs;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.VerticalLayout;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.plaf.misc.GlossyTaskPaneUI;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.settings.Parameter;
import de.mpa.client.settings.ParameterSet;
import de.mpa.client.ui.PanelConfig;
import de.mpa.client.ui.ScreenConfig;
import de.mpa.client.ui.icons.IconConstants;

/**
 * Dialog displaying advanced search settings dynamically generated from a set of parameters.
 * 
 * @author T. Muth
 */
public class AdvancedSettingsDialog extends JDialog {

	/**
	 * Constructor for the AdvancedSettingsDialog.
	 * @param owner
	 * @param title
	 * @param modal
	 * @param parameterSet
	 */
	public AdvancedSettingsDialog(Frame owner, String title, boolean modal, ParameterSet parameterSet) {
		super(owner, title, modal);
		
		// TODO: find better place to store these values (and do this only once preferably)
		UIManager.put("TaskPane.titleForeground", PanelConfig.getTitleForeground());
		GradientPaint paint = (GradientPaint) (((MattePainter) PanelConfig.getTitlePainter()).getFillPaint());
		UIManager.put("TaskPane.titleBackgroundGradientStart", paint.getColor1());
		UIManager.put("TaskPane.titleBackgroundGradientEnd", paint.getColor2());
		UIManager.put("TaskPane.titleOver", paint.getColor2().darker());
		UIManager.put("TaskPane.borderColor", paint.getColor2());
		
		initComponents(parameterSet.getParameters());
	}
	
	/**
	 * Returns the section set.
	 * @return Section set.
	 */
	private Map<String, List<Parameter>> getSectionMap(List<Parameter> params) {
		HashMap<String, List<Parameter>> sectionMap = new HashMap<String, List<Parameter>>();
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
	 * 
	 * @param params The ungrouped list of parameters to display
	 */
	private void initComponents(List<Parameter> params) {

		// Define dialog content pane layout
		Container contentPane = this.getContentPane();
		contentPane.setLayout(new FormLayout("5dlu, p:g, 5dlu",
				"5dlu, f:p:g, 5dlu, p, 5dlu"));
		
		// Init task pane container
		final JXTaskPaneContainer tpc = new JXTaskPaneContainer();
		((VerticalLayout) tpc.getLayout()).setGap(10);
		tpc.setBackground(UIManager.getColor("ProgressBar.foreground"));
		
		// Group parameters by section identifiers
		Map<String, List<Parameter>> sectionMap = getSectionMap(params);
		
		// Iterate sections
		for (Entry<String, List<Parameter>> entry : sectionMap.entrySet()) {
			// Create collapsible task pane for section
			JXTaskPane taskPane = new JXTaskPane(entry.getKey());
			taskPane.setUI(new GlossyTaskPaneUI());
			
			taskPane.addComponentListener(new ComponentAdapter() {
				private Dimension size = null;
				@Override
				public void componentResized(ComponentEvent e) {
					Dimension newSize = e.getComponent().getSize();
					if (size != null) {
						int delta = newSize.height - size.height;
						Dimension dialogSize = new Dimension(getSize());
						dialogSize.height += delta;
						setSize(dialogSize);
					}
					size = newSize;
				}
			});
			
			// Set up builder for the section
			DefaultFormBuilder builder = new DefaultFormBuilder(
					new FormLayout("p, 5dlu, p:g"), taskPane);

			// Iterate parameters to build section components
			for (Parameter p : entry.getValue()) {
				JComponent comp = createParameterControl(p);
				if (comp instanceof JCheckBox) {
					// Special case for checkboxes which come with labels of their own
					builder.append(comp, 3);
				} else {
					// Add component, auto-generate label to go with it
					builder.append(p.getName(), comp);
				}
				builder.nextLine();
			}
			tpc.add(taskPane);
		}
		
		// Configure button panel containing 'OK' and 'Cancel' options
		FormLayout layout = new FormLayout("0px:g, p, 5dlu, p", "p");
		layout.setColumnGroups(new int[][] { { 2, 4 } });
		JPanel buttonPnl = new JPanel(layout);
		
		// Configure 'OK' button
		JButton okBtn = new JButton("OK", IconConstants.CHECK_ICON);
		okBtn.setRolloverIcon(IconConstants.CHECK_ROLLOVER_ICON);
		okBtn.setPressedIcon(IconConstants.CHECK_PRESSED_ICON);
		okBtn.setHorizontalAlignment(SwingConstants.LEFT);
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO: Get the new settings
				dispose();
			}
		});
		// Configure 'Cancel' button
		JButton cancelBtn = new JButton("Cancel", IconConstants.CROSS_ICON);
		cancelBtn.setRolloverIcon(IconConstants.CROSS_ROLLOVER_ICON);
		cancelBtn.setPressedIcon(IconConstants.CROSS_PRESSED_ICON);
		cancelBtn.setHorizontalAlignment(SwingConstants.LEFT);
		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		// Align label of 'OK' button
		int labelWidth = getFontMetrics(okBtn.getFont()).stringWidth("OK");
		okBtn.setIconTextGap(cancelBtn.getPreferredSize().width/2 -
				okBtn.getIcon().getIconWidth() - labelWidth/2);
		
		// Lay out button panel
		buttonPnl.add(okBtn, CC.xy(2, 1));
		buttonPnl.add(cancelBtn, CC.xy(4, 1));
		
		// Add final components to content pane
		contentPane.add(tpc, CC.xy(2, 2));
		contentPane.add(buttonPnl, CC.xy(2, 4));
		
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
	 * Creates and returns a GUI component to interact with the specified parameter.<br>
	 * The type of the parameter value determines the kind of control that will be generated:<ul>
	 * <li>JCheckBox for <code>Boolean</code></li>
	 * <li>JSpinner for <code>Integer</code> or <code>Double</code></li>
	 * <li>JComboBox for <code>Object[]</code></li></ul>
	 * @param param the parameter
	 * @return the parameter control component
	 */
	private JComponent createParameterControl(Parameter param) {
		Object value = param.getValue();
		// Determine type of value and generate appropriate control component
		if (value instanceof Boolean) {
			// Checkbox
			JCheckBox checkBox = new JCheckBox(param.getName(), (Boolean) param.getValue());
			checkBox.setHorizontalAlignment(SwingConstants.RIGHT);
			checkBox.setHorizontalTextPosition(SwingConstants.LEFT);
			checkBox.setIconTextGap(15);
			return checkBox;
		} else if (value instanceof Object[]) {
			// Combobox
			JComboBox comboBox = new JComboBox((Object[]) value);
			return comboBox;
		} else if (value instanceof Double) {
			// Spinner with 2 decimal places
			JSpinner spinner = new JSpinner(new SpinnerNumberModel((Double) value, 0.0, null, 0.01));
			spinner.setEditor(new JSpinner.NumberEditor(spinner, "0.00"));
			return spinner;
		} else if (value instanceof Integer) {
			// Spinner without decimal places
			return new JSpinner(new SpinnerNumberModel((Integer) value, 0, null, 1));
		} else {
			// When we get here something went wrong - investigate!
			System.out.println("UH OH " + value.getClass());
			return null;
		}
	}
}
