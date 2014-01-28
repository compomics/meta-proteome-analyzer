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
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXTitledPanel;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.DbSearchSettings;
import de.mpa.client.DenovoSearchSettings;
import de.mpa.client.SearchSettings;
import de.mpa.client.SpecSimSettings;
import de.mpa.client.ui.CheckBoxTreeTable;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.PanelConfig;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.db.storager.MascotStorager;
import de.mpa.io.MascotGenericFile;
import de.mpa.io.MascotGenericFileReader;
import de.mpa.io.MascotGenericFileReader.LoadMode;

// TODO: maybe merge with DatabaseSearchSettingsPanel
public class SettingsPanel extends JPanel {

	/**
	 * TODO: API
	 */
	private DatabaseSearchSettingsPanel databasePnl;
	private SpectralLibrarySettingsPanel specLibPnl;
	private DeNovoSearchSettingsPanel deNovoPnl;

	private JButton processBtn;
	
	public SettingsPanel() {
		initComponents();
		
		// init dummy de novo panel
		deNovoPnl = new DeNovoSearchSettingsPanel();
		deNovoPnl.setEnabled(false);
	}

	/**
	 * TODO: API
	 */
	private void initComponents() {
		
		Set<AWTKeyStroke> forwardKeys = getFocusTraversalKeys(
				KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
		Set<AWTKeyStroke> newForwardKeys = new HashSet<AWTKeyStroke>(forwardKeys);
		newForwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));
		setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS,
				newForwardKeys);

		this.setLayout(new BorderLayout());
				
		// database search settings panel
		JPanel settingsPnl = new JPanel(new FormLayout("p", "f:p:g, 5dlu, p"));
		
		databasePnl = new DatabaseSearchSettingsPanel();
		this.specLibPnl = databasePnl.getSpectralLibrarySettingsPanel();
		
		ImageIcon processIcon = new ImageIcon(getClass().getResource("/de/mpa/resources/icons/search.png"));
		processIcon = new ImageIcon(processIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH));

		FormLayout buttonLyt = new FormLayout("8dlu, p:g, 7dlu, p:g, 8dlu", "2dlu, p, 7dlu");
		JPanel buttonPnl = new JPanel(buttonLyt);
		
		// XXX: just a placeholder, remove or relocate button/functionality
		JButton quickBtn = new JButton("Quick Search File", IconConstants.LIGHTNING_ICON);
		quickBtn.setRolloverIcon(IconConstants.LIGHTNING_ROLLOVER_ICON);
		quickBtn.setPressedIcon(IconConstants.LIGHTNING_PRESSED_ICON);
		quickBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				final JFileChooser fc = new JFileChooser();
				fc.setFileFilter(Constants.MGF_FILE_FILTER);
				fc.setAcceptAllFileFilterUsed(false);
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fc.setMultiSelectionEnabled(false);
				int result = fc.showOpenDialog(ClientFrame.getInstance());
				if (result == JFileChooser.APPROVE_OPTION) {
					new QuickSearchWorker(fc.getSelectedFile()).execute();
				}
			}
		});
		
		processBtn = new JButton("Start searching", processIcon);
		processBtn.setEnabled(false);
		
//		processBtn.setHorizontalAlignment(SwingConstants.LEFT);
		processBtn.setFont(processBtn.getFont().deriveFont(
				Font.BOLD, processBtn.getFont().getSize2D()*1.25f));

		processBtn.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {
				new ProcessWorker().execute();
			}
		});

		buttonPnl.add(quickBtn, CC.xy(2, 2));
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
	private class ProcessWorker extends SwingWorker {

		protected Object doInBackground() {
			ProjectPanel projectPanel = ClientFrame.getInstance().getProjectPanel();
			long experimentID = projectPanel.getCurrentExperimentId();
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
					// pack and send files
					client.firePropertyChange("new message", null, "PACKING AND SENDING FILES");
					long packSize = databasePnl.getPackageSize();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
					List<String> filenames = null;
					// collect search settings
					DbSearchSettings dbss = (databasePnl.isEnabled()) ? databasePnl.gatherDBSearchSettings() : null;
					SpecSimSettings sss = (specLibPnl.isEnabled()) ? specLibPnl.gatherSpecSimSettings() : null;
					DenovoSearchSettings dnss = (deNovoPnl.isEnabled()) ? deNovoPnl.collectDenovoSettings() : null;
					
					SearchSettings settings = new SearchSettings(dbss, sss, dnss, experimentID);
					
					// FIXME: Please change that and get files from file tree.
					if (!ClientFrame.getInstance().getFilePanel().getSelectedMascotFiles().toString().contains(".dat")) {
						filenames = client.packAndSend(packSize, checkBoxTree, projectPanel.getCurrentExperimentContent().getExperimentTitle() + "_" + sdf.format(new Date()) + "_");
					} else {
						if (dbss.isMascot()) {
							List<File> files = ClientFrame.getInstance().getFilePanel().getSelectedMascotFiles();
							client.firePropertyChange("resetall", 0, files.size());
							int i = 0;
							for (File file : files) {
								client.firePropertyChange("new message", null, "STORING MASCOT FILE " + ++i + "/" + files.size());
								MascotStorager storager = new MascotStorager(Client.getInstance().getDatabaseConnection(), file, settings, databasePnl.getMascotParameterMap());
								storager.run();
								client.firePropertyChange("new message", null, "FINISHED STORING MASCOT FILE " + i + "/" + files.size());
							}
						}
					}
					client.firePropertyChange("new message", null, "SEARCHES RUNNING");
					// dispatch search request
					client.runSearches(filenames, settings);
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
	 * Convenience worker to process a file without loading its contents into the tree table first.
	 * @author A. Behne
	 */
	private class QuickSearchWorker extends SwingWorker<Object, Object> {
		
		/**
		 * The spectrum file.
		 */
		private File file;
		
		/**
		 * Constructs a quick search worker using the specified file.
		 * @param file the spectrum file
		 */
		public QuickSearchWorker(File file) {
			this.file = file;
		}
		
		@Override
		protected Object doInBackground() throws Exception {
			List<String> filenames = new ArrayList<String>();
			FileOutputStream fos = null;
			Client client = Client.getInstance();
			client.firePropertyChange("indeterminate", false, true);
			client.firePropertyChange("new message", null, "READING SPECTRUM FILE");
			MascotGenericFileReader reader = new MascotGenericFileReader(this.file, LoadMode.SURVEY);
			client.firePropertyChange("indeterminate", true, false);
			client.firePropertyChange("new message", null, "READING SPECTRUM FILE FINISHED");
			List<Long> positions = reader.getSpectrumPositions(false);
			long numSpectra = 0L;
			long maxSpectra = (long) positions.size();
			long packageSize = databasePnl.getPackageSize();
			client.firePropertyChange("resetall", 0L, maxSpectra);
			client.firePropertyChange("new message", null, "PACKING AND SENDING FILES");
			// iterate over all spectra
			File batchFile = null;
			for (int j = 0; j < positions.size(); j++) {
				
				if ((numSpectra % packageSize) == 0) {
					if (fos != null) {
						fos.close();
						client.uploadFile(batchFile.getName(), client.getBytesFromFile(batchFile));
						batchFile.delete();
					}
					batchFile = new File("quick_batch" + (numSpectra/packageSize) + ".mgf");
					filenames.add(batchFile.getName());
					fos = new FileOutputStream(batchFile);
					long remaining = maxSpectra - numSpectra;
					firePropertyChange("resetcur", 0L, (remaining > packageSize) ? packageSize : remaining);
				}
				
				MascotGenericFile mgf = reader.loadSpectrum((int) numSpectra);
				mgf.writeToStream(fos);
				fos.flush();
				firePropertyChange("progressmade", 0L, ++numSpectra);
			}
			fos.close();
			client.uploadFile(batchFile.getName(), client.getBytesFromFile(batchFile));
			batchFile.delete();
			client.firePropertyChange("new message", null, "PACKING AND SENDING FILES FINISHED");
			
			// collect search settings
			DbSearchSettings dbss = (databasePnl.isEnabled()) ? databasePnl.gatherDBSearchSettings() : null;
			SpecSimSettings sss = (specLibPnl.isEnabled()) ? specLibPnl.gatherSpecSimSettings() : null;
			DenovoSearchSettings dnss = (deNovoPnl.isEnabled()) ? deNovoPnl.collectDenovoSettings() : null;
			
			SearchSettings settings = new SearchSettings(dbss, sss, dnss, ClientFrame.getInstance().getProjectPanel().getCurrentExperimentId());
			
			client.firePropertyChange("new message", null, "SEARCHES RUNNING");
			// dispatch search request
			client.runSearches(filenames, settings);
			
			return null;
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
