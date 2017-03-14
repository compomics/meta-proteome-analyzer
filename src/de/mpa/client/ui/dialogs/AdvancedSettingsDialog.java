package de.mpa.client.ui.dialogs;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

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
@SuppressWarnings("serial")
public class AdvancedSettingsDialog extends JDialog {
	
	/** Status flag for cancelled dialog. */
	public static final int DIALOG_CANCELLED = 0;
	/** Status flag for accepted dialog. */
	public static final int DIALOG_ACCEPTED = 1;
	/** Status flag for changed dialog. */
	public static final int DIALOG_CHANGED = 2;
	/** Status flag for changed and accepted dialog. */
	public static final int DIALOG_CHANGED_ACCEPTED = 3;

	/**
	 * The collection of parameters this dialog shall provide interaction with.
	 */
	private final ParameterMap parameterMap;
	
	/**
	 * The status of the dialog indicating for instance whether parameters got changed or if the dialog was cancelled.
	 */
	private int status;

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
        initComponents();
		
		// Configure size and position
        pack();
		Dimension size = getSize();
        setSize(new Dimension(size.width, size.height + 7));
        setResizable(false);
		ScreenConfig.centerInScreen(this);
		
		// Show dialog
        setVisible(true);
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
		return this.status;
	}

	/**
	 * Initializes and lays out all components inside this dialog grouped by sections identifiers.
	 * @param params The ungrouped list of parameters to display
	 */
	private void initComponents() {
		// Define dialog content pane layout
		Container contentPane = getContentPane();
		contentPane.setLayout(new FormLayout("5dlu, p:g, 5dlu",
				"5dlu, f:p:g, 5dlu, p, 5dlu"));
		
		// Init task pane container
		JXTaskPaneContainer tpc = new JXTaskPaneContainer();
		((VerticalLayout) tpc.getLayout()).setGap(10);
		
		// Group parameters by section identifiers
		Map<String, List<Parameter>> sectionMap = createSectionMap(this.parameterMap);
		
		// Iterate sections
		for (Map.Entry<String, List<Parameter>> entry : sectionMap.entrySet()) {
			String key = entry.getKey();
			// Exclude general settings
			if (!"General".equals(key)) {
				// Create collapsible task pane for section
				JXTaskPane taskPane = new JXTaskPane(key);
				taskPane.setUI(new GlossyTaskPaneUI());
				
				// Apply component listener to synchronize task pane size with dialog size
				taskPane.addComponentListener(new ComponentAdapter() {
					@Override
					public void componentResized(ComponentEvent evt) {
                        AdvancedSettingsDialog.this.pack();
					}
				});
				
				// Set up builder for the section
				DefaultFormBuilder builder = new DefaultFormBuilder(
						new FormLayout("p, 5dlu, p:g"), taskPane);
				builder.setDefaultRowSpec(RowSpec.decode("f:p:g"));
				
				// Iterate parameters to build section components
				for (Parameter param : entry.getValue()) {
					JComponent leftComp = param.createLeftComponent();
					JComponent rightComp = param.createRightComponent();
					if (rightComp != null) {
						builder.append(leftComp, rightComp);
					} else {
						builder.append(leftComp, 3);
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
                AdvancedSettingsDialog.this.restoreDefaults();
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
			public void actionPerformed(ActionEvent evt) {
                AdvancedSettingsDialog.this.applyChanges();
                AdvancedSettingsDialog.this.status |= AdvancedSettingsDialog.DIALOG_ACCEPTED;
                AdvancedSettingsDialog.this.dispose();
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
			public void actionPerformed(ActionEvent evt) {
                AdvancedSettingsDialog.this.status = AdvancedSettingsDialog.DIALOG_CANCELLED;
                AdvancedSettingsDialog.this.dispose();
			}
		});
//		cancelBtn.setToolTipText("Discard changes and dismiss dialog.");
		
		// Align label of 'OK' button
		int labelWidth = this.getFontMetrics(okBtn.getFont()).stringWidth("OK");
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
	 * Groups parameters by their section.
	 * @return a map containing lists of parameters mapped to their section key.
	 */
	private Map<String, List<Parameter>> createSectionMap(ParameterMap parameters) {
		Map<String, List<Parameter>> sectionMap = new LinkedHashMap<String, List<Parameter>>();
		for (Parameter p : parameters.values()) {
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
	 * Applies changes made via control components to the underlying parameter
	 * collection.
	 */
	protected void applyChanges() {
		// Update parameters
		for (Parameter param : this.parameterMap.values()) {
			// ignore non-configurable settings
			if (!"General".equals(param.getSection())) {
				if (param.applyChanges()) {
                    status |= AdvancedSettingsDialog.DIALOG_CHANGED;
				}
			}
		}
	}

	/**
	 * Resets all parameters and their respective control components to their
	 * default values.
	 */
	protected void restoreDefaults() {
		// Reset parameters
		for (Parameter param : this.parameterMap.values()) {
			// ignore non-configurable settings
			if (!"General".equals(param.getSection())) {
				param.restoreDefaults();
			}
		}
	}

}
