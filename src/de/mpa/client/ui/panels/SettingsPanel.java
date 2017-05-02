package de.mpa.client.ui.panels;

import java.awt.AWTKeyStroke;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXTitledPanel;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.DbSearchSettings;
import de.mpa.client.model.FileExperiment;
import de.mpa.client.model.dbsearch.DbSearchResult;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.PanelConfig;
import de.mpa.io.GenericContainer;
import de.mpa.io.MascotGenericFileReader;

public class SettingsPanel extends JPanel {
	
	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Database search settings panel.
	 */
	private DatabaseSearchSettingsPanel databasePnl;
	
	/**
	 * Processing button.
	 */
	private JButton processBtn;
	
	public SettingsPanel() {
		initComponents();
	}

	/**
	 * Initialize the UI components.
	 */
	private void initComponents() {
		
		Set<AWTKeyStroke> forwardKeys = getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
		Set<AWTKeyStroke> newForwardKeys = new HashSet<AWTKeyStroke>(forwardKeys);
		newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
		setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, newForwardKeys);
		this.setLayout(new BorderLayout());
				
		// database search settings panel
		JPanel settingsPnl = new JPanel(new FormLayout("p", "f:p:g, 5dlu, p"));
		
		databasePnl = new DatabaseSearchSettingsPanel();
		
		ImageIcon processIcon = new ImageIcon(getClass().getResource("/de/mpa/resources/icons/search.png"));
		processIcon = new ImageIcon(processIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH));

		FormLayout buttonLyt = new FormLayout("8dlu, p:g, 7dlu, p:g, 8dlu", "2dlu, p, 7dlu");
		JPanel buttonPnl = new JPanel(buttonLyt);		
	
		processBtn = new JButton("Start searching", processIcon);
		processBtn.setFont(processBtn.getFont().deriveFont(Font.BOLD, processBtn.getFont().getSize2D()*1.25f));
		processBtn.setEnabled(false);
		processBtn.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {
				new ProcessWorker().execute();
			}
		});
		databasePnl.setProcessBtn(processBtn);
		buttonPnl.add(processBtn, CC.xy(4, 2));
		settingsPnl.add(databasePnl, CC.xy(1, 1));
		settingsPnl.add(buttonPnl, CC.xy(1, 3));
		JXTitledPanel dbTtlPnl = PanelConfig.createTitledPanel("Search Settings", settingsPnl);
		this.add(dbTtlPnl, BorderLayout.CENTER);
	}

	/**
	 * Worker class for starting the identification searches.
	 * 
	 * @author T. Muth
	 */
	private class ProcessWorker extends SwingWorker<Object, Object> {

		protected Object doInBackground() {
			ProjectPanel projectPanel = ClientFrame.getInstance().getProjectPanel();
//			TODO: long experimentID = projectPanel.getSelectedExperiment().getID();
//			if (experimentID != 0L) {
//				CheckBoxTreeTable checkBoxTree = ClientFrame.getInstance().getFilePanel().getCheckBoxTree();
				// reset progress
				Client client = Client.getInstance();
//				client.firePropertyChange("resetall", 0L, (long) (checkBoxTree.getCheckBoxTreeSelectionModel()).getSelectionCount());
				// appear busy
				firePropertyChange("progress", null, 0);
				processBtn.setEnabled(false);
				ClientFrame.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		
				try {
					// Collect search settings.
					DbSearchSettings searchSettings = databasePnl.collectSearchSettings();
					FileExperiment selectedExperiment = projectPanel.getSelectedExperiment();
					selectedExperiment.setSpectrumFilePaths(client.getMgfFilePaths());
					
					// Run the searches.
					client.runSearches(searchSettings);
					
				} catch (Exception e) {
					Client.getInstance().firePropertyChange("new message", null, "DATABASE SEARCH FAILED");
					e.printStackTrace();
				}
				return 0;
		}

		@Override
		public void done() {
			ClientFrame clientFrame = ClientFrame.getInstance();
			clientFrame.setCursor(null);
			clientFrame.setTabEnabledAt(ClientFrame.INDEX_RESULTS_PANEL, true);
			
			Client client = Client.getInstance();
			FileExperiment selectedExperiment = ClientFrame.getInstance().getProjectPanel().getSelectedExperiment();
			if (selectedExperiment.getSearchResult() != null) {
				DbSearchResult searchResult = selectedExperiment.getSearchResult();
				clientFrame.getProjectPanel().setResultsButtonState(true);
				
				String experimentPath = Constants.PROJECTS_PATH + File.separator + selectedExperiment.getTitle();
				GenericContainer.CurrentExperimentPath = experimentPath;
				File experimentDir = new File(experimentPath);
				if (!experimentDir.exists()) {
					experimentDir.mkdir();
				}
				
				File experimentFile = new File(experimentPath + File.separator + selectedExperiment.getTitle() + ".mpa");
				List<String> newSpectrumFilePaths = new ArrayList<>();
				try {
					// Add MGF files to experiment/projects file structure
					for (String filePath : selectedExperiment.getSpectrumFilePaths()) {
						File file = new File(filePath);
						File createdFile = new File(experimentPath + File.separator + file.getName());
						
						// Check whether spectrum file is not already existing
						if (!createdFile.exists()) {
							Files.copy(file.toPath(), createdFile.toPath());
						}
						// Update the current reader mapping.
						MascotGenericFileReader currentMGFReader = GenericContainer.MGFReaders.get(file.getAbsolutePath());
						GenericContainer.MGFReaders.put(createdFile.getAbsolutePath(), currentMGFReader);
						GenericContainer.MGFReaders.remove(file.getAbsolutePath());
						newSpectrumFilePaths.add(createdFile.getAbsolutePath());
					}
					// Update the spectrum file paths accordingly.
					selectedExperiment.setSpectrumFilePaths(newSpectrumFilePaths);
					searchResult.setSpectrumFilePaths(newSpectrumFilePaths);
					client.dumpDatabaseSearchResult(searchResult, experimentFile.getAbsolutePath());
					
					selectedExperiment.setResultFile(experimentFile);
					selectedExperiment.serialize();
				} catch (Exception e) {
					Client.getInstance().firePropertyChange("new message", null, "DATABASE SEARCH FAILED");
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Returns the process button reference.
	 * @return The process button reference.
	 */
	public JButton getProcessButton() {
		return processBtn;
	}

	/**
	 * Returns the database search settings panel.
	 * @return the database search settings panel
	 */
	public DatabaseSearchSettingsPanel getDatabaseSearchSettingsPanel() {
		return databasePnl;
	}
}
