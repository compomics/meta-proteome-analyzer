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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXTitledPanel;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Client;
import de.mpa.client.DbSearchSettings;
import de.mpa.client.SearchSettings;
import de.mpa.client.ui.CheckBoxTreeTable;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.PanelConfig;

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
		processBtn.setEnabled(false);
		processBtn.setFont(processBtn.getFont().deriveFont(Font.BOLD, processBtn.getFont().getSize2D()*1.25f));

		processBtn.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {
				new ProcessWorker().execute();
			}
		});
		buttonPnl.add(processBtn, CC.xy(4, 2));
		settingsPnl.add(databasePnl, CC.xy(1, 1));
		settingsPnl.add(buttonPnl, CC.xy(1, 3));
		JXTitledPanel dbTtlPnl = PanelConfig.createTitledPanel("Search Settings", settingsPnl);
		this.add(dbTtlPnl, BorderLayout.CENTER);
	}

	/**
	 * Worker class for packing/sending input files and dispatching search requests to the server instance.
	 * 
	 * @author Thilo Muth, Alex Behne
	 */
	private class ProcessWorker extends SwingWorker<Object, Object> {

		protected Object doInBackground() {
			ProjectPanel projectPanel = ClientFrame.getInstance().getProjectPanel();
			long experimentID = projectPanel.getSelectedExperiment().getID();
			if (experimentID != 0L) {
				CheckBoxTreeTable checkBoxTree = ClientFrame.getInstance().getFilePanel().getCheckBoxTree();
				// reset progress
				Client client = Client.getInstance();
				client.firePropertyChange("resetall", 0L, (long) (checkBoxTree.getCheckBoxTreeSelectionModel()).getSelectionCount());
				// appear busy
				firePropertyChange("progress", null, 0);
				processBtn.setEnabled(false);
				ClientFrame.getInstance().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				
				try {
							
					List<File> mgfFiles = null;
					// Collect search settings.
					DbSearchSettings dbss = (databasePnl.isEnabled()) ? databasePnl.gatherDBSearchSettings() : null;
					SearchSettings settings = new SearchSettings(dbss, experimentID);
							
					client.firePropertyChange("new message", null, "SEARCHES RUNNING");
					
					// Run the searches.
					client.runSearches(mgfFiles, settings);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				return 0;
			} else {
				JOptionPane.showMessageDialog(ClientFrame.getInstance(), "No experiment selected.", "Error", JOptionPane.ERROR_MESSAGE);
				return 404;
			}
		}

		@Override
		public void done() {
			ClientFrame.getInstance().setCursor(null);
			processBtn.setEnabled(true);
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
