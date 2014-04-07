package de.mpa.client.settings;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.panels.DatabaseSearchSettingsPanel;

/**
 * Class for storing Mascot search engine-specific settings.
 * 
 * @author T. Muth, F. Kohrs, A. Behne
 */
public class MascotParameters extends ParameterMap {
	
	private JTextField precTolTtf;
	private JTextField fragTolTtf;
	private JTextField missClvTtf;

	/**
	 * Constructs a parameter map initialized with default values.
	 */
	public MascotParameters() {
		initDefaults();
	}

	@Override
	public void initDefaults() {
		JPanel panel = createSettingsViewerPanel();
		
		/* Hidden parameters */
		this.put("precTol", new Parameter(null, "0.0", "General", null));
		this.put("fragTol", new Parameter(null, "0.0", "General", null));
		this.put("missClv", new Parameter(null, "0", "General", null));
		
		/* Visible parameters */
		this.put("filter", new Parameter("Peptide Ion Score|False Discovery Rate", new Object[][] { { true, 15, true }, { false, 0.05, false } }, "Filtering", "Peptide Ion Score Threshold|Maximum False Discovery Rate"));
		
		this.put("settings", new Parameter("", panel, "Search Settings", "tooltip"));
	}
	
	@Override
	public Parameter put(String key, Parameter param) {
		if ("precTol".equals(key)) {
			precTolTtf.setText((String) param.getValue());
		} else if ("fragTol".equals(key)) {
			fragTolTtf.setText((String) param.getValue());
		} else if ("missClv".equals(key)) {
			missClvTtf.setText((String) param.getValue());
		}
		return super.put(key, param);
	}

	/**
	 * Convenience method to generate a panel containing components to display
	 * general search settings derived from an imported .dat file and providing
	 * the ability to copy them to the general settings controls inside the
	 * database search settings panel.
	 * 
	 * @return the .dat settings viewer panel
	 */
	private JPanel createSettingsViewerPanel() {
		JPanel panel = new JPanel(new FormLayout(
				"p, 5dlu, p:g, 5dlu, p",
				"p, 5dlu, p, 5dlu, p, 5dlu, p"));
		
		precTolTtf = new JTextField();
		precTolTtf.setHorizontalAlignment(SwingConstants.RIGHT);
		precTolTtf.setEditable(false);
		
		fragTolTtf = new JTextField();
		fragTolTtf.setHorizontalAlignment(SwingConstants.RIGHT);
		fragTolTtf.setEditable(false);
		
		missClvTtf = new JTextField();
		missClvTtf.setHorizontalAlignment(SwingConstants.RIGHT);
		missClvTtf.setEditable(false);
		
		JButton button = new JButton("Copy to General Settings");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				DatabaseSearchSettingsPanel dbSearchPanel =
						ClientFrame.getInstance().getFilePanel().getSettingsPanel().getDatabaseSearchSettingsPanel();
				
				dbSearchPanel.getPrecursorToleranceSpinner().setValue(Double.parseDouble(precTolTtf.getText()));
				dbSearchPanel.getFragmentToleranceSpinner().setValue(Double.parseDouble(fragTolTtf.getText()));
				dbSearchPanel.getMissedCleavageSpinner().setValue(Integer.parseInt(missClvTtf.getText()));
			}
		});

		panel.add(new JLabel("Precursor Ion Tolerance"), CC.xy(1, 1));
		panel.add(precTolTtf, CC.xy(3, 1));
		panel.add(new JLabel("Da"), CC.xy(5, 1));
		panel.add(new JLabel("Fragment Ion Tolerance"), CC.xy(1, 3));
		panel.add(fragTolTtf, CC.xy(3, 3));
		panel.add(new JLabel("Da"), CC.xy(5, 3));
		panel.add(new JLabel("Missed Cleavages (max)"), CC.xy(1, 5));
		panel.add(missClvTtf, CC.xy(3, 5));
		panel.add(button, CC.xyw(1, 7, 5));
		
		return panel;
	}

	@Override
	public File toFile(String path) throws IOException {
		return null;
	}

}
