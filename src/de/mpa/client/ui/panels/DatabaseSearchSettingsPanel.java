package de.mpa.client.ui.panels;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.DbSearchSettings;
import de.mpa.client.settings.CometParameters;
import de.mpa.client.settings.IterativeSearchParams;
import de.mpa.client.settings.MSGFParameters;
import de.mpa.client.settings.Parameter;
import de.mpa.client.settings.ParameterMap;
import de.mpa.client.settings.XTandemParameters;
import de.mpa.client.settings.Parameter.OptionParameter;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.ComponentTitledBorder;
import de.mpa.client.ui.RolloverButtonUI;
import de.mpa.client.ui.dialogs.AdvancedSettingsDialog;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.io.GenericContainer;
import de.mpa.io.fasta.FastaLoader;
import de.mpa.io.fasta.FastaUtilities;
import de.mpa.io.fasta.index.OffHeapIndex;
import de.mpa.task.ResourceProperties;

/**
 * Panel containing control components for database search-related settings.
 * 
 * @author T. Muth, A. Behne
 */
public class DatabaseSearchSettingsPanel extends JPanel {

	/**
	 * Default SerialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Combo box referencing FASTA files available for database searching.
	 */
	private JTextField fastaFileTtf;
	
	/**
	 * Spinner for controlling precursor mass tolerance.
	 */
	private JSpinner precTolSpn;
	
	/**
	 * Spinner for controlling fragment ion tolerance.
	 */
	private JSpinner fragTolSpn;

	/**
	 * Spinner for controlling the amount of missed cleavages to account for.
	 */
	private JSpinner missClvSpn;

	/**
	 * Checkbox for the units of the precursor tolerance.
	 */
	private JComboBox<String> precTolCbx;

	/**
	 * The package size spinner.
	 */
	private JSpinner packSpn;
	
	/**
	 * Parameter map containing advanced settings for the X!Tandem search engine.
	 */
	private ParameterMap xTandemParams = new XTandemParameters();
	
	/**
	 * Parameter map containing advanced settings for the Comet search engine.
	 */
	private ParameterMap cometParams = new CometParameters();

	/**
	 * Parameter map containing advanced settings for the MS-GF+ search engine.
	 */
	private ParameterMap msgfParams = new MSGFParameters();
	
	/**
	 * Parameter map containing the settings for the iterative search.
	 */
	private ParameterMap iterativeSearchParams = new IterativeSearchParams();
	
	/**
	 * Checkbox for using X!Tandem search engine.
	 */
	private JCheckBox xTandemChk;

	/**
	 * Checkbox for using Comet search engine.
	 */
	private JCheckBox cometChk;
	
	/**
	 * Checkbox for using MG-GF+ search engine.
	 */
	private JCheckBox msgfChk;
	
	/**
	 * Button for choosing the FASTA database file.
	 */
	private JButton fastaFileBtn;
	
	/**
	 * Button for starting the MS/MS data processing.
	 */
	private JButton processBtn;
	
	/**
	 * Selected FASTA target database file.
	 */
	private File fastaFile;
	
	/**
	 * Selected FASTA decoy database file.
	 */
	private File decoyFastaFile;
	
	/**
	 * Created FASTA index file.
	 */
	private File fastaIndexFile;
	
	/**
	 * Flag showing whether any error occurred during database formatting.
	 */
	private boolean dbFormattingError = false;
	
	/**
	 * Selected search mode combobox.
	 */
	private JComboBox<String> searchModeCbx;

	/**
	 * The database search settings panel constructor.
	 */
	public DatabaseSearchSettingsPanel() {
		initComponents();
	}

	/**
	 * Method to initialize the panel's components.
	 */
	private void initComponents() {

		this.setLayout(new FormLayout("7dlu, p:g, 5dlu, p, 7dlu", "5dlu, p, 5dlu, f:p:g, 5dlu"));

		// Protein Database Panel
		final JPanel protDatabasePnl = new JPanel();
		protDatabasePnl.setLayout(new FormLayout("5dlu, p, 5dlu, p:g, 5dlu, p, 5dlu", "5dlu, p, 5dlu, p, 5dlu"));
		protDatabasePnl.setBorder(new ComponentTitledBorder(new JLabel("Protein Database Search"), protDatabasePnl));

		// FASTA file ComboBox
		fastaFileTtf = new JTextField(20);
		fastaFileTtf.setEditable(false);
		
		fastaFileBtn = new JButton("Choose...");
		fastaFileBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				fastaFileBtnTriggered();
			}
		});
		
		protDatabasePnl.add(new JLabel("FASTA File:"), CC.xy(2, 2));
		protDatabasePnl.add(fastaFileTtf, CC.xy(4, 2));
		protDatabasePnl.add(fastaFileBtn, CC.xy(6, 2));

		// Normal vs. iterative search.
		String[] searchOptions = { "Normal Search", "Protein-based Two-Step Search", "Taxon-based Two-Step Search"};

		searchModeCbx = new JComboBox<String>(searchOptions);
		
		final JButton iterativeSearchSetBtn = this.createSettingsButton();
		iterativeSearchSetBtn.setEnabled(false);
		iterativeSearchSetBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				AdvancedSettingsDialog.showDialog(ClientFrame.getInstance(), "Iterative Search Parameters", true, iterativeSearchParams);
			}
		});
		
		// TODO: Use taxonomy-based search.
		searchModeCbx.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent evt) {
				
				// Check for selection state of combobox item.
				if (evt.getStateChange() == ItemEvent.SELECTED) {
					if (searchModeCbx.getSelectedIndex() == 1) {
						msgfChk.setEnabled(false);
						msgfChk.setSelected(false);
						
						Parameter param = iterativeSearchParams.get("method");
						if (param instanceof OptionParameter) {
							((OptionParameter) param).setIndex(0);
						}
						iterativeSearchParams.setValue("method", param);
					} if (searchModeCbx.getSelectedIndex() == 2) {
						msgfChk.setEnabled(false);
						msgfChk.setSelected(false);
						Parameter param = iterativeSearchParams.get("method");
						if (param instanceof OptionParameter) {
							((OptionParameter) param).setIndex(1);
						}
						iterativeSearchParams.put("method", param);
					}else {
						msgfChk.setEnabled(true);
					}
				}
			}
	    });
	    
		protDatabasePnl.add(new JLabel("Search Type:"), CC.xy(2, 4));
		protDatabasePnl.add(searchModeCbx, CC.xy(4, 4));
		protDatabasePnl.add(iterativeSearchSetBtn, CC.xy(6, 4));
		
		// General Settings Panel
		final JPanel paramsPnl = new JPanel();
		paramsPnl.setLayout(new FormLayout(
				"5dlu, p, 5dlu, p:g, 5dlu, p, 2dlu, p, 5dlu",
				"0dlu, p, 5dlu, p, 5dlu, p, 5dlu, 2px:g, 5dlu"));
		paramsPnl.setBorder(new ComponentTitledBorder(new JLabel("Search Settings"), paramsPnl));

		// Precursor ion tolerance Spinner
		precTolSpn = new JSpinner(new SpinnerNumberModel(1.0, 0.0, null, 0.1));
		precTolSpn.setEditor(new JSpinner.NumberEditor(precTolSpn, "0.00"));
		precTolSpn.setToolTipText("The precursor mass tolerance.");
		precTolCbx = new JComboBox<String>(Constants.TOLERANCE_UNITS);

		// Fragment ion tolerance Spinner
		fragTolSpn = new JSpinner(new SpinnerNumberModel(0.5, 0.0, null, 0.1));
		fragTolSpn.setEditor(new JSpinner.NumberEditor(fragTolSpn, "0.00"));
		fragTolSpn.setToolTipText("The fragment mass tolerance.");
		
		// Missed cleavages Spinner
		missClvSpn = new JSpinner(new SpinnerNumberModel(1, 0, null, 1));
		missClvSpn.setToolTipText("The maximum number of missed cleavages.");
		
		paramsPnl.add(new JLabel("Precursor Ion Tolerance:"), CC.xyw(2, 2, 3));
		paramsPnl.add(precTolSpn, CC.xy(6, 2));
		paramsPnl.add(precTolCbx, CC.xy(8, 2));
		paramsPnl.add(new JLabel("Fragment Ion Tolerance:"), CC.xyw(2, 4, 3));
		paramsPnl.add(fragTolSpn, CC.xy(6, 4));
		paramsPnl.add(new JLabel("Da"), CC.xy(8, 4));
		paramsPnl.add(new JLabel("Missed Cleavages (max):"), CC.xyw(2, 6, 3));
		paramsPnl.add(missClvSpn, CC.xy(6, 6));
		
		// Search Engine Settings Panel
		final JPanel searchEngPnl = new JPanel();
		searchEngPnl.setLayout(new FormLayout("5dlu, p, 5dlu, p:g, 5dlu", "5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));
		searchEngPnl.setBorder(new ComponentTitledBorder(new JLabel("Search Engines"), searchEngPnl));

		xTandemChk = new JCheckBox("X!Tandem", true);
		xTandemChk.setIconTextGap(10);
		final JButton xTandemSetBtn = this.createSettingsButton();
		xTandemSetBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				showAdvancedSettings("X!Tandem Advanced Parameters", xTandemParams);
			}
		});
		xTandemChk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				xTandemSetBtn.setEnabled(xTandemChk.isSelected());
			}
		});
		
		cometChk = new JCheckBox("Comet", false);
		cometChk.setIconTextGap(10);
		final JButton cometSetBtn = this.createSettingsButton();
		cometSetBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				AdvancedSettingsDialog.showDialog(ClientFrame.getInstance(), "Comet Advanced Parameters", true, cometParams);
			}
		});
		cometChk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				cometSetBtn.setEnabled(cometChk.isSelected());
			}
		});	
		
		msgfChk = new JCheckBox("MS-GF+", false);
		msgfChk.setIconTextGap(10);
		final JButton msgfSetBtn = this.createSettingsButton();
		msgfSetBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				AdvancedSettingsDialog.showDialog(
						ClientFrame.getInstance(), "MS-GF+ Advanced Parameters", true, msgfParams);
			}
		});
		
		msgfChk.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				msgfSetBtn.setEnabled(msgfChk.isSelected());
			}
		});
		
		searchEngPnl.add(xTandemChk, CC.xy(2, 2));
		searchEngPnl.add(xTandemSetBtn, CC.xy(4, 2));
		searchEngPnl.add(cometChk, CC.xy(2, 4));
		searchEngPnl.add(cometSetBtn, CC.xy(4, 4));
		searchEngPnl.add(msgfChk, CC.xy(2, 6));
		searchEngPnl.add(msgfSetBtn, CC.xy(4, 6));

		// add everything to main panel
		this.add(protDatabasePnl, CC.xywh(2, 2, 3, 1));
		this.add(paramsPnl, CC.xy(2, 4));
		this.add(searchEngPnl, CC.xy(4, 4));

	}
	
	protected void fastaFileBtnTriggered() {
		processBtn.setEnabled(false);
        File startLocation = new File(ResourceProperties.getInstance().getProperty("path.fasta"));
        JFileChooser fc = new JFileChooser(startLocation);
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File fastaFile) {
            	if (fastaFile.getName().toLowerCase().endsWith("decoy.fasta")) 
            		return false;
            	
                return fastaFile.getName().toLowerCase().endsWith(".fasta") || fastaFile.getName().toLowerCase().endsWith(".fasta") || fastaFile.isDirectory();
            }

            @Override
            public String getDescription() {
                return "Protein FASTA database (*.fasta)";
            }
        };
        fc.setFileFilter(filter);
        int result = fc.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            fastaFile = fc.getSelectedFile();
            if (fastaFile.getName().indexOf(" ") != -1) {
                renameFastaFileName(fastaFile);
            } else {
            	 fastaFileTtf.setText(fastaFile.getAbsolutePath());
            }
            
            try {	
            	if (fastaFile.isFile()) {
            		// Check whether decoy FASTA file already exists - if not: create one!
                	decoyFastaFile = new File(fastaFile.getAbsolutePath().substring(0, fastaFile.getAbsolutePath().indexOf(".fasta")) + "_decoy.fasta");
                	if (!decoyFastaFile.exists()) {
                		new DecoyFastaFileWorker().execute();
                	}
                	
            		fastaIndexFile = new File(fastaFile.getAbsolutePath().substring(0, fastaFile.getAbsolutePath().indexOf(".fasta")) + ".fasta.fb");
                	if (!fastaIndexFile.exists()) {
                		new IndexFastaFileWorker().execute();
                	}
                	new PeptideIndexWorker().execute();
            	}
            } catch (Exception e) {
            	dbFormattingError = true;
            	JXErrorPane.showDialog(ClientFrame.getInstance(), new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
            }
            if (fastaIndexFile.exists() && fastaFile.exists() && fastaFile.isFile() && !dbFormattingError) {
            	processBtn.setEnabled(true);
            }
        }
	}
	
    private void renameFastaFileName(File file) {
        String tempName = file.getName();
        tempName = tempName.replaceAll(" ", "_");

        File renamedFile = new File(file.getParentFile().getAbsolutePath() + File.separator + tempName);

        boolean success = false;

        try {
            success = renamedFile.createNewFile();

            if (success) {

                FileReader r = new FileReader(file);
                BufferedReader br = new BufferedReader(r);

                FileWriter w = new FileWriter(renamedFile);
                BufferedWriter bw = new BufferedWriter(w);

                String line = br.readLine();
                
                while (line != null) {
                    bw.write(line + "\n");
                    line =  br.readLine();
                }
                
                bw.close();
                w.close();
                br.close();
                r.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (success) {
            JOptionPane.showMessageDialog(this, "Your FASTA file name contained white space and has been renamed to:\n"
                    + file.getParentFile().getAbsolutePath() + File.separator + tempName, "Renamed File", JOptionPane.WARNING_MESSAGE);
            fastaFile = new File(file.getParentFile().getAbsolutePath() + File.separator + tempName);
            fastaFileTtf.setText(file.getParentFile().getAbsolutePath() + File.separator + tempName);
        } else {
            JOptionPane.showMessageDialog(this, "Your FASTA file name contains white space and has to been renamed.",
                    "Please Rename File", JOptionPane.WARNING_MESSAGE);
        }
    }

	/**
	 * Class to a write the decoy FASTA file in a background thread.
	 * 
	 * @author T. Muth
	 */
	private class DecoyFastaFileWorker extends SwingWorker<Integer, Object> {
		Client client = Client.getInstance();
		
		@Override
		protected Integer doInBackground() {

			try {
				// Create the decoy database by reversing the FASTA protein sequences.
	    		client.firePropertyChange("new message", null, "CREATING DECOY FASTA FILE");
	    		client.firePropertyChange("indeterminate", false, true);
	    		decoyFastaFile = FastaUtilities.createDecoyDatabase(fastaFile);
			} catch (Exception e) {
				dbFormattingError = true;
				JXErrorPane.showDialog(ClientFrame.getInstance(), new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
	    		client.firePropertyChange("indeterminate", true, false);
	    		client.firePropertyChange("new message", null, "CREATING DECOY FASTA FILE FAILED");
			}
			return 1;
		}
	
		@Override
		protected void done() {
			// Get worker result
			int res = 0;
			try {
				res = this.get().intValue();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// If new results have been fetched...
			if (res == 1) {
        		client.firePropertyChange("indeterminate", true, false);
        		client.firePropertyChange("new message", null, "CREATING DECOY FASTA FILE FINISHED");
			}
		}
	}
	
	/**
	 * Class to create (or load) the peptide index in a background thread.
	 * 
	 * @author Thilo Muth
	 */
	private class PeptideIndexWorker extends SwingWorker<Integer, Object> {
		Client client = Client.getInstance();
		
		@Override
		protected Integer doInBackground() {
			
			try {
				// Create a peptide index from
        		client.firePropertyChange("new message", null, "CREATING PEPTIDE AND SPECIES INDEX");
        		client.firePropertyChange("indeterminate", false, true);

        		OffHeapIndex offHeapIndex = new OffHeapIndex(fastaFile, 2);
				GenericContainer.PeptideIndex = offHeapIndex.getPeptideIndex();
				GenericContainer.SpeciesIndex = offHeapIndex.getSpeciesIndex();

        		return 1;
			} catch (Exception e) {
				JXErrorPane.showDialog(ClientFrame.getInstance(), new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
				client.firePropertyChange("new message", null, "CREATING PEPTIDE AND SPECIES INDEX FAILED");
				client.firePropertyChange("indeterminate", true, false);
			}
			return 0;
		}
	
		@Override
		protected void done() {
			// Get worker result
			int res = 0;
			try {
				res = this.get().intValue();
			} catch (Exception e) {
				JXErrorPane.showDialog(ClientFrame.getInstance(),
						new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
			}
			
			// If new results have been fetched...
			if (res == 1) {
				client.firePropertyChange("indeterminate", true, false);
				client.firePropertyChange("new message", null, "LOADING PEPTIDE INDEX FINISHED");

				if (GenericContainer.PeptideIndex != null && fastaIndexFile.exists() && fastaFile.exists() & !dbFormattingError) {
					processBtn.setEnabled(true);
				}
			}
		}
	}
	
	/**
	 * Class to a index FASTA file in a background thread.
	 * 
	 * @author T. Muth
	 */
	private class IndexFastaFileWorker extends SwingWorker<Integer, Object> {
		Client client = Client.getInstance();
		private FastaLoader fastaLoader;
		
		@Override
		protected Integer doInBackground() {
			
			try {
				// Index the FASTA protein database.
    			fastaLoader = FastaLoader.getInstance();
    			fastaLoader.setFastaFile(fastaFile);
        		client.firePropertyChange("new message", null, "CREATING INDEX FASTA FILES");
        		client.firePropertyChange("indeterminate", false, true);
				fastaLoader.loadFastaFile();
				fastaLoader.writeIndexFile();
        		return 1;
			} catch (Exception e) {
				JXErrorPane.showDialog(ClientFrame.getInstance(), new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
				client.firePropertyChange("new message", null, "CREATING INDEX FASTA FILES FAILED");
				client.firePropertyChange("indeterminate", true, false);
			}
			return 0;
		}
	
		@Override
		protected void done() {
			// Get worker result
			int res = 0;
			try {
				res = this.get().intValue();
			} catch (Exception e) {
				JXErrorPane.showDialog(ClientFrame.getInstance(),
						new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
			}
			
			// If new results have been fetched...
			if (res == 1) {
				client.firePropertyChange("indeterminate", true, false);
				client.firePropertyChange("new message", null, "CREATING INDEX FASTA FILE FINISHED");
			}
		}
	}

	/**
	 * Convenience method to return a settings button.
	 * @return a settings button
	 */
	private static JButton createSettingsButton() {
		JButton button = new JButton(IconConstants.SETTINGS_SMALL_ICON);
		button.setRolloverIcon(IconConstants.SETTINGS_SMALL_ROLLOVER_ICON);
		button.setPressedIcon(IconConstants.SETTINGS_SMALL_PRESSED_ICON);
		button.setUI((RolloverButtonUI) RolloverButtonUI.createUI(button));
		return button;
	}
	
	/**
	 * Utility method to collect and consolidate all relevant database search settings.
	 * @return the database search settings object instance.
	 */
	public DbSearchSettings collectSearchSettings() {
		DbSearchSettings searchSettings = new DbSearchSettings();
		searchSettings.setFragIonTol((Double) fragTolSpn.getValue());
		searchSettings.setPrecIonTol((Double) precTolSpn.getValue());
		searchSettings.setPrecIonTolPPM(precTolCbx.getSelectedIndex()==1);
		searchSettings.setMissedCleavages((Integer) missClvSpn.getValue());
		
		if (xTandemChk.isSelected()) {
			searchSettings.setXTandem(true);
			searchSettings.setXTandemParams(xTandemParams.toString());
		}
		
		if (cometChk.isSelected()) {
			searchSettings.setComet(true);
			searchSettings.setCometParams(cometParams.toString());
		}
		
		if (msgfChk.isSelected()) {
			searchSettings.setMSGF(true);
			searchSettings.setMSGFParams(msgfParams.toString());
		}
		
		if (searchModeCbx.getSelectedIndex() == 1 || searchModeCbx.getSelectedIndex() == 2) {
			searchSettings.setIterativeSearch(true);;
			searchSettings.setIterativeSearchSettings(iterativeSearchParams.toString());
		}
		
		if (fastaFileTtf.getText().length() > 0) {
			searchSettings.setFastaFilePath(fastaFileTtf.getText());
		}
		
		// Set the current experiment id for the database search settings.
//		TODO: dbSettings.setExperimentid(ClientFrame.getInstance().getProjectPanel().getSelectedExperiment().getID());
		return searchSettings;
	}
	
	/**
	 * Utility method to display an advanced settings dialog.
	 * @param title
	 * @param params
	 */
	protected void showAdvancedSettings(String title, ParameterMap params) {
		collectSearchSettings();
		AdvancedSettingsDialog.showDialog(ClientFrame.getInstance(), title, true, params);
	}

	/**
	 * Method to recursively iterate a component's children and set their enabled state.
	 * @param parent
	 * @param enabled
	 */
	public void setChildrenEnabled(JComponent parent, boolean enabled) {
		for (Component child : parent.getComponents()) {
			if (child instanceof JComponent) {
				setChildrenEnabled((JComponent) child, enabled);
			}
		}
		if (!(parent instanceof DatabaseSearchSettingsPanel)) { // don't mess with DBSearchPanels
			parent.setEnabled(enabled);
			Border border = parent.getBorder();
			if (border instanceof ComponentTitledBorder) {
				((ComponentTitledBorder) border).setEnabled(enabled);
			}
		}
	}

	/**
	 * Returns the precursor tolerance spinner.
	 * @return the precursor tolerance spinner
	 */
	public JSpinner getPrecursorToleranceSpinner() {
		return precTolSpn;
	}

	/**
	 * Returns the fragment tolerance spinner.
	 * @return the fragment tolerance spinner
	 */
	public JSpinner getFragmentToleranceSpinner() {
		return fragTolSpn;
	}

	/**
	 * Returns the missed cleavages spinner.
	 * @return the missed cleavages spinner
	 */
	public JSpinner getMissedCleavageSpinner() {
		return missClvSpn;
	}

	/**
	 * Returns the X!Tandem paramter map.
	 * @return ParameterMap for X!Tandem.
	 */
	public ParameterMap getXTandemParameterMap() {
		return xTandemParams;
	}
	
	/**
	 * Returns the OMSSA parameter map.
	 * @return ParameterMap for OMSSA.
	 */
	public ParameterMap getOmssaParameterMap() {
		return cometParams;
	}
	
	/**
	 * Returns the MS-GF+ parameter map.
	 * @return ParameterMap for  MS-GF+.
	 */
	public ParameterMap getMSGFParameterMap() {
		return msgfParams;
	}
	
	/**
	 * Returns the iterative search parameter map.
	 * @return ParameterMap for iterative search settings.
	 */
	public ParameterMap getIterativeSearchParams() {
		return iterativeSearchParams;
	}

	/**
	 * Sets the enable state of the MS-GF+ search engine selector.
	 * @param enabled
	 */
	public void setMSGFEnabled(boolean enabled) {
		msgfChk.setEnabled(enabled);
		msgfChk.setSelected(enabled);
	}
	
	/**
	 * Returns the package size.
	 * @return the package size
	 */
	public long getPackageSize() {
		return ((Number) packSpn.getValue()).longValue();
	}
	
	/**
	 * Sets the process button.
	 * @param processBtn
	 */
	public void setProcessBtn(JButton processBtn) {
		this.processBtn = processBtn;
	}
}
