package de.mpa.client.ui.panels;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import de.mpa.client.settings.MascotParameters;
import de.mpa.client.settings.OmssaParameters;
import de.mpa.client.settings.ParameterMap;
import de.mpa.client.settings.XTandemParameters;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.ComponentTitledBorder;
import de.mpa.client.ui.RolloverButtonUI;
import de.mpa.client.ui.dialogs.AdvancedSettingsDialog;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.io.fasta.FastaLoader;
import de.mpa.io.fasta.FastaUtilities;
import de.mpa.job.ResourceProperties;
import de.mpa.job.instances.MakeBlastdbJob;

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
	 * Parameter map containing advanced settings for the OMSSA search engine.
	 */
	private ParameterMap omssaParams = new OmssaParameters();

	/**
	 * Parameter map containing advanced settings for uploading imported Mascot search engine results.
	 */
	private ParameterMap mascotParams = new MascotParameters();
	
	/**
	 * Checkbox for using X!Tandem search engine.
	 */
	private JCheckBox xTandemChk;

	/**
	 * Checkbox for using OMSSA search engine.
	 */
	private JCheckBox omssaChk;
	
	/**
	 * Checkbox for using MASCOT search engine.
	 */
	private JCheckBox mascotChk;

	private JButton fastaFileBtn;
	
	private JButton processBtn;
	
	private File fastaFile;
	
	private File decoyFastaFile;
	
	private boolean hasError = false;

	private File indexFile;

	/**
	 * The default database search panel constructor.
	 */
	public DatabaseSearchSettingsPanel() {
		initComponents();
	}

	/**
	 * Method to initialize the panel's components.
	 */
	private void initComponents() {
		
		this.setLayout(new FormLayout("7dlu, p:g, 5dlu, p, 7dlu",
									  "5dlu, p, 5dlu, f:p:g, 0dlu"));

		// Protein Database Panel
		final JPanel protDatabasePnl = new JPanel();
		protDatabasePnl.setLayout(new FormLayout("5dlu, p, 5dlu, p:g, 5dlu, p, 5dlu", "5dlu, p, 5dlu"));
		protDatabasePnl.setBorder(new ComponentTitledBorder(new JLabel("Protein Database"), protDatabasePnl));

		// FASTA file ComboBox
		fastaFileTtf = new JTextField(20);
		fastaFileTtf.setEditable(false);
		
		protDatabasePnl.add(new JLabel("FASTA File:"), CC.xy(2, 2));
		protDatabasePnl.add(fastaFileTtf, CC.xy(4, 2));
		
		fastaFileBtn = new JButton("Choose...");
		fastaFileBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				fastaFileBtnTriggered();
			}
		});
		protDatabasePnl.add(fastaFileBtn, CC.xy(6, 2));
		
		// General Settings Panel
		final JPanel paramsPnl = new JPanel();
		paramsPnl.setLayout(new FormLayout(
				"5dlu, p, 5dlu, p:g, 5dlu, p, 2dlu, p, 5dlu",
				"0dlu, p, 5dlu, p, 5dlu, p, 5dlu, 2px:g, 5dlu, p, 5dlu"));
		paramsPnl.setBorder(new ComponentTitledBorder(new JLabel("General Settings"), paramsPnl));

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
		
		omssaChk = new JCheckBox("OMSSA", true);
		omssaChk.setIconTextGap(10);
		final JButton omssaSetBtn = this.createSettingsButton();
		omssaSetBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				AdvancedSettingsDialog.showDialog(ClientFrame.getInstance(), "OMSSA Advanced Parameters", true, omssaParams);
			}
		});
		omssaChk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				omssaSetBtn.setEnabled(omssaChk.isSelected());
			}
		});	

		
		mascotChk = new JCheckBox("Mascot", false);
		mascotChk.setIconTextGap(10);
		//TODO: Enable MASCOT
		mascotChk.setEnabled(false);
		final JButton mascotSetBtn = this.createSettingsButton();
		mascotSetBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				AdvancedSettingsDialog.showDialog(
						ClientFrame.getInstance(), "Mascot Advanced Parameters", true, mascotParams);
			}
		});
		mascotChk.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				mascotSetBtn.setEnabled(mascotChk.isSelected());
			}
		});
		
		// MASCOT functionality is initially disabled unless a .dat file is imported
		mascotChk.setEnabled(false);
		mascotSetBtn.setEnabled(false);
				
		searchEngPnl.add(xTandemChk, CC.xy(2, 2));
		searchEngPnl.add(xTandemSetBtn, CC.xy(4, 2));
		searchEngPnl.add(omssaChk, CC.xy(2, 4));
		searchEngPnl.add(omssaSetBtn, CC.xy(4, 4));
		searchEngPnl.add(mascotChk, CC.xy(2, 6));
		searchEngPnl.add(mascotSetBtn, CC.xy(4, 6));

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
            	
                return fastaFile.getName().toLowerCase().endsWith(".fasta")
                        || fastaFile.getName().toLowerCase().endsWith(".fasta")
                        || fastaFile.isDirectory();
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
            	// Check whether decoy FASTA file already exists - if not: create one!
            	decoyFastaFile = new File(fastaFile.getAbsolutePath().substring(0, fastaFile.getAbsolutePath().indexOf(".fasta")) + "_decoy.fasta");
            	if (!decoyFastaFile.exists()) {
            		new DecoyFastaFileWorker().execute();
            	} else {
            	  	// Optional formatting
               		new FormatFastaFileWorker().execute();
            	}
            	
            	indexFile = new File(fastaFile.getAbsolutePath().substring(0, fastaFile.getAbsolutePath().indexOf(".fasta")) + ".fasta.fb");
            	if (!indexFile.exists()) {
            		new IndexFastaFileWorker().execute();
            	}    
         
                
                   
            } catch (Exception e) {
            	hasError = true;
            	JXErrorPane.showDialog(ClientFrame.getInstance(), new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
            }
            if (indexFile.exists() && fastaFile.exists() & !hasError) {
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
				hasError = true;
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
        		
            	// Optional formatting after decoy creation.
           		new FormatFastaFileWorker().execute();
			}
		}
	}
	
	/**
	 * Class to format the FASTA file (for OMSSA) in a background thread.
	 * 
	 * @author T. Muth
	 */
	private class FormatFastaFileWorker extends SwingWorker<Integer, Object> {
		Client client = Client.getInstance();
		
		@Override
		protected Integer doInBackground() {

			try {
            	boolean formatTargetFile = checkForFormatting(fastaFile);
            	boolean formatDecoyFile = checkForFormatting(decoyFastaFile);

            	if (formatTargetFile) {
            		client.firePropertyChange("indeterminate", false, true);
            		MakeBlastdbJob formatTargetJob = new MakeBlastdbJob(fastaFile);
            		formatTargetJob.run();
            		client.firePropertyChange("new message", null, "FORMATTING TARGET FASTA FILE");
            	}
	            	
            	if (formatDecoyFile) {
            		client.firePropertyChange("indeterminate", false, true);
            		MakeBlastdbJob formatDecoyJob = new MakeBlastdbJob(decoyFastaFile);
            		formatDecoyJob.run();
            		client.firePropertyChange("new message", null, "FORMATTING DECOY FASTA FILE");
            	}
            	// Already formatted target and decoy: no message is provided
            	if (!formatTargetFile && !formatDecoyFile) return 2;
	    		
			} catch (Exception e) {
				hasError = true;
				JXErrorPane.showDialog(ClientFrame.getInstance(), new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
	    		client.firePropertyChange("indeterminate", true, false);
	    		client.firePropertyChange("new message", null, "FORMATTING FASTA FILE FAILED");
	    		return 0;
			}
			return 1;
		}

		private boolean checkForFormatting(File fastaFile) {
			File[] files = fastaFile.getParentFile().listFiles();
			String name = fastaFile.getName();
			boolean formatFile = true;
			boolean phr;
			boolean pin;
			boolean psq;
			// Find all three processed files.
			phr = false;
			pin = false;
			psq = false;
			
			for (File file : files) {
			    if (file.isFile()) {
			    	String fileName = file.getName();
			        if (fileName.startsWith(name) && fileName.endsWith(".phr")) {
			            phr = true;
			        }
			        if (fileName.startsWith(name) && fileName.endsWith(".pin")) {
			        	pin = true;
			        }
			        if (fileName.startsWith(name) && fileName.endsWith(".psq")) {
			        	psq = true;
			        }
			    	
			    }
			}
			if (phr && pin && psq) {
				formatFile = false;
			}
			return formatFile;
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
        		client.firePropertyChange("new message", null, "FORMATTING FASTA FILE FINISHED");
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
				// Create the decoy database by reversing the FASTA protein sequences.
    			fastaLoader = FastaLoader.getInstance();
    			fastaLoader.setFastaFile(fastaFile);
        		client.firePropertyChange("new message", null, "CREATING INDEX FASTA FILE");
        		client.firePropertyChange("indeterminate", false, true);
				fastaLoader.loadFastaFile();
				fastaLoader.writeIndexFile();

        		return 1;
			} catch (Exception e) {
				JXErrorPane.showDialog(ClientFrame.getInstance(),
						new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
				client.firePropertyChange("new message", null, "CREATING INDEX FASTA FILE FAILED");
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

				if (indexFile.exists() && fastaFile.exists() & !hasError) {
					processBtn.setEnabled(true);
				}
			}
		}
	}

	/**
	 * Convenience method to return a settings button.
	 * @return a settings button
	 */
	private JButton createSettingsButton() {
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
	public DbSearchSettings gatherDBSearchSettings() {
		DbSearchSettings dbSettings = new DbSearchSettings();
		dbSettings.setFragIonTol((Double) fragTolSpn.getValue());
		dbSettings.setPrecIonTol((Double) precTolSpn.getValue());
		dbSettings.setPrecIonTolPpm(precTolCbx.getSelectedIndex()==1);
		dbSettings.setMissedCleavages((Integer) missClvSpn.getValue());
		
		dbSettings.setMascot(mascotChk.isSelected());
		if (xTandemChk.isSelected()) {
			dbSettings.setXTandem(true);
			dbSettings.setXTandemParams(xTandemParams.toString());
		}
		
		if (omssaChk.isSelected()) {
			dbSettings.setOmssa(true);
			dbSettings.setOmssaParams(omssaParams.toString());
		}
		
		if (fastaFileTtf.getText().length() > 0) {
			dbSettings.setFastaFile(fastaFileTtf.getText());
		}
		
		
		// Set the current experiment id for the database search settings.
//		TODO: dbSettings.setExperimentid(ClientFrame.getInstance().getProjectPanel().getSelectedExperiment().getID());
		return dbSettings;
	}
	
	/**
	 * Utility method to display an advanced settings dialog.
	 * @param title
	 * @param params
	 */
	protected void showAdvancedSettings(String title, ParameterMap params) {
		gatherDBSearchSettings();
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
		return omssaParams;
	}
	
	/**
	 * Returns the MASCOT parameter map.
	 * @return ParameterMap for MASCOT.
	 */
	public ParameterMap getMascotParameterMap() {
		return mascotParams;
	}
	
	/**
	 * Sets the enable state of the Mascot search engine selector.
	 * @param enabled
	 */
	public void setMascotEnabled(boolean enabled) {
		mascotChk.setEnabled(enabled);
		mascotChk.setSelected(enabled);
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
