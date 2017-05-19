package de.mpa.client.ui.inputpanel.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Constants;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.inputpanel.SettingsPanel;
import de.mpa.client.ui.sharedelements.MultiExtensionFileFilter;
import de.mpa.client.ui.sharedelements.ScreenConfig;
import de.mpa.client.ui.sharedelements.icons.IconConstants;
import de.mpa.util.PropertyLoader;

public class SearchFileDialog extends JDialog {

	/**
	 * variables 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The parent (ClientFrame)
	 */
	private final ClientFrame owner;

	/**
	 * The settings panel that started this dialog
	 */
	private final SettingsPanel settingsPanel; 
	
	private File[] selectedFiles;
	private boolean singleExperiment = false;
	private String mascotFile;
	
	
	/**
	 * @param owner. The owner of the dialog.
	 * @param title. The title of the dialog.
	 */
	public SearchFileDialog(ClientFrame owner, String title, SettingsPanel stgsPnl) {
		super(owner, title);
		this.owner = owner;
		this.settingsPanel = stgsPnl;
		initComponents();
		showDialog();
	}

	/**
	 * Initializes and lays out all components inside this dialog grouped by sections identifiers.
	 */
	private void initComponents() {
		
		// TODO: Implement a nice Panel here
		
		/* CONTAINS:
		 * 1. Textfield (not editable) + Browse button to select files
		 * 2. DropDown-Menu for MASCOT-Database
		 * 3. Checkbox to chosse between: new experiment for each file, all files into single experiment
		 * 4. BONUS: method to select an experiment
		 *   
		 */
		
		// set up for input
		JPanel searchDlgPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu, f:p:g, 5dlu"));
		JPanel searchFileParameterPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu, p:g, 5dlu", "5dlu, p:g, 5dlu, p:g, 5dlu, p:g, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu,p, 5dlu, p, 5dlu, p , 5dlu"));
		JLabel filesLabel = new JLabel("Select .dat or .mgf files");
		JLabel mascotTxt = new JLabel("Database for Mascot-Searches");
		JCheckBox newExperimentPerFile = new JCheckBox("Single experiment.");
		JCheckBox allInOneExperiment = new JCheckBox("New Experiment for each selected File.");
		// init checkboxes
		newExperimentPerFile.setSelected(false);
		allInOneExperiment.setSelected(true);
		
		// Load the resources settings via input stream.
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream(PropertyLoader.getProperty("base_path") + PropertyLoader.getProperty("path.fasta") + File.separator + PropertyLoader.getProperty("file.fastalist"));
			prop.load(input);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String[] items = prop.get("files.fasta").toString().split(",");
		JComboBox<String> mascotFastaFileCbx = new JComboBox<String> (items);
		mascotFile = mascotFastaFileCbx.getSelectedItem().toString();
		
		// add browse button for file
		JButton browseButton = new JButton("Browse");
		browseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setFileFilter(new MultiExtensionFileFilter(
						"All supported formats (*.mgf, *.dat)",
						Constants.MGF_FILE_FILTER,
						Constants.DAT_FILE_FILTER));
				fc.setAcceptAllFileFilterUsed(false);
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fc.setMultiSelectionEnabled(true);
				int result = fc.showOpenDialog(ClientFrame.getInstance());
				if (result == JFileChooser.APPROVE_OPTION) {
					selectedFiles = fc.getSelectedFiles();
				} else {
					// do nothing
				}
				
				int datNumber = 0;
				int mgfNumber = 0;
				for (File file : selectedFiles) {
					 if (file.getAbsolutePath().endsWith(".dat")) {
						 datNumber = datNumber + 1;
					 }
					 if (file.getAbsolutePath().endsWith(".mgf")) {
						mgfNumber = mgfNumber + 1; 
					 }
				}
				filesLabel.setText(mgfNumber + " '.mgf'-files and " + datNumber + " '.dat'-files selected");
			}
		});
		
		// add actionlisteners
		newExperimentPerFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (newExperimentPerFile.isSelected()) {
					SearchFileDialog.this.singleExperiment = true;
					allInOneExperiment.setSelected(false);
				} else {
					SearchFileDialog.this.singleExperiment = false;
					allInOneExperiment.setSelected(true);
				}
			}
		});
		allInOneExperiment.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (allInOneExperiment.isSelected()) {
					SearchFileDialog.this.singleExperiment = false;
					newExperimentPerFile.setSelected(false);
				} else {
					SearchFileDialog.this.singleExperiment = true;
					newExperimentPerFile.setSelected(true);
				}
			}
		});
		mascotFastaFileCbx.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SearchFileDialog.this.mascotFile = mascotFastaFileCbx.getSelectedItem().toString();
			}
		});
		
		
		searchFileParameterPnl.add(filesLabel, 	CC.xy(2, 2));
		searchFileParameterPnl.add(mascotTxt, 	CC.xy(2, 4));
		searchFileParameterPnl.add(mascotFastaFileCbx, 	CC.xy(4, 4));
		searchFileParameterPnl.add(newExperimentPerFile, 	CC.xy(2, 8));
		searchFileParameterPnl.add(allInOneExperiment, 	CC.xy(2, 10));
		searchFileParameterPnl.add(browseButton, 	CC.xy(4, 2));
		
		
		// Configure 'OK' button
		JButton okBtn = new JButton("Start Search", IconConstants.CHECK_ICON);
		okBtn.setRolloverIcon(IconConstants.CHECK_ROLLOVER_ICON);
		okBtn.setPressedIcon(IconConstants.CHECK_PRESSED_ICON);
		okBtn.setHorizontalAlignment(SwingConstants.LEFT);
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// processing in the settings panel and close
				try {
					SearchFileDialog.this.settingsPanel.performBatchSearch(selectedFiles, singleExperiment, mascotFile);
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				close();
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
			close();
			}
		});
		
		searchDlgPnl.add(searchFileParameterPnl, CC.xyw(2, 2,3));
		searchDlgPnl.add(okBtn,CC.xy(2, 4) );
		searchDlgPnl.add(cancelBtn,CC.xy(4, 4) );
		
		this.add(searchDlgPnl);
	}

	
	/**
	 * This method shows the dialog.
	 */
	private void showDialog() {
		// Configure size and position
		this.pack();
		this.setResizable(false);
		ScreenConfig.centerInScreen(this);

		// Show dialog
		this.setVisible(true);
	}

	/**
	 * Close method for the dialog.
	 */
	private void close() {
		dispose();
	}


}