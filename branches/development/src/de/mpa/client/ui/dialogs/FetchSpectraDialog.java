package de.mpa.client.ui.dialogs;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.analysis.UniProtUtilities;
import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.model.AbstractExperiment;
import de.mpa.client.model.SpectrumMatch;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.ConfirmFileChooser;
import de.mpa.client.ui.ScreenConfig;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.db.DBManager;
import de.mpa.db.accessor.Omssahit;
import de.mpa.db.accessor.SearchHit;
import de.mpa.db.accessor.Searchspectrum;
import de.mpa.db.accessor.Spectrum;
import de.mpa.db.accessor.XTandemhit;
import de.mpa.io.MascotGenericFile;

/**
 * Class for dialog to export spectra directly from database
 */
@SuppressWarnings("serial")
public class FetchSpectraDialog extends JDialog {
	
	/**
	 * The ClientFrame
	 */
	private ClientFrame clntFrame;
	private AbstractExperiment experiment;
	private Connection conn;
	private JTextField expTxt;
	private JTextField lookUpTxt;
	private JCheckBox identCbx;
	private JCheckBox unidentCbx;
	
	/**
	 * @param owner. The owner of the dialog.
	 * @param title. The title of the dialog.
	 */
	public FetchSpectraDialog(ClientFrame owner, String title, AbstractExperiment experiment) {
		super(owner, title);
		this.clntFrame = owner;
		this.experiment = experiment;
		try {
			this.conn = DBManager.getInstance().getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		initComponents();
		showDialog();
	}
	
	/**
	 * Initializes and lays out all components inside this dialog grouped by sections identifiers.
	 */
	private void initComponents() {

		// Create Fetch dialog
		JPanel fetchDlgPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu, f:p:g, 5dlu"));
		JPanel fetchSettingsPnl 	= new JPanel(new FormLayout("5dlu, p:g, 5dlu", "5dlu, p:g, 5dlu, p:g, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));
		JLabel titleLbl 		= new JLabel("Experiment Title");
		expTxt = new JTextField(experiment.getTitle()); 
		expTxt.setEnabled(false);
		JLabel lookUpLbl 			= new JLabel("Filter by specific String in Spectrum Title");
		lookUpTxt = new JTextField("FILE"); 
		identCbx = new JCheckBox();
		identCbx.setText("Get identified spectra");
		identCbx.setSelected(false);
		unidentCbx = new JCheckBox();
		unidentCbx.setText("Get unidentified spectra");
		unidentCbx.setSelected(true);
		
		fetchSettingsPnl.add(titleLbl, 	CC.xy(2, 2));
		fetchSettingsPnl.add(expTxt, 	CC.xy(2, 4));
		fetchSettingsPnl.add(lookUpLbl, CC.xy(2, 6));
		fetchSettingsPnl.add(lookUpTxt, CC.xy(2, 8));
		fetchSettingsPnl.add(identCbx,  CC.xy(2, 10));
		fetchSettingsPnl.add(unidentCbx,  CC.xy(2, 12));
		
		// Configure 'OK' button
		JButton okBtn = new JButton("OK", IconConstants.CHECK_ICON);
		okBtn.setRolloverIcon(IconConstants.CHECK_ROLLOVER_ICON);
		okBtn.setPressedIcon(IconConstants.CHECK_PRESSED_ICON);
		okBtn.setHorizontalAlignment(SwingConstants.LEFT);
		okBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						JFileChooser chooser = new ConfirmFileChooser();
						chooser.setCurrentDirectory(new File(clntFrame.getLastSelectedFolder()));
						chooser.setFileFilter(Constants.MGF_FILE_FILTER);
						chooser.setAcceptAllFileFilterUsed(false);
						int returnVal = chooser.showSaveDialog(clntFrame);
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							File selFile = chooser.getSelectedFile();
							if (selFile != null) {
								String filePath = selFile.getPath();
								clntFrame.setLastSelectedFolder(selFile.getParent());
								if (!filePath.toLowerCase().endsWith(".mgf")) {
									filePath += ".mgf";
								}
								fetchAndExportSpectra(filePath);
							}
						}
						return null;
					}

				}.execute();
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
		
		fetchDlgPnl.add(fetchSettingsPnl, CC.xyw(2, 2,3));
		fetchDlgPnl.add(okBtn,CC.xy(2, 4) );
		fetchDlgPnl.add(cancelBtn,CC.xy(4, 4) );
		Container cp = this.getContentPane();		
		cp.setLayout(new FormLayout("5dlu, r:p, 5dlu", "5dlu, f:p:g, 5dlu"));

		cp.add(fetchDlgPnl, CC.xy(2,  2));
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
		
	/**
	 * fetch requested spectra from the database and write them to the file
	 * @param path where to write the MGF file
	 * @throws SQLException 
	 */
	private void fetchAndExportSpectra(String path) throws SQLException {
		long expID = experiment.getID();

		// gather all spectra for the experiment
		Set<Long> allSearchspectrumIds = new HashSet<Long>();
		for (Searchspectrum searchspectrum : Searchspectrum.findFromExperimentID(expID, conn)) {
			allSearchspectrumIds.add(searchspectrum.getSearchspectrumid());	
		}
		
		// gather all identified spectra for the experiment
		List<XTandemhit> xtandemHits = XTandemhit.getHitsFromExperimentID(expID, conn);
		List<Omssahit> omssaHits = Omssahit.getHitsFromExperimentID(expID, conn);
		List<SearchHit> hits = new ArrayList<SearchHit>();
		hits.addAll(xtandemHits);
		hits.addAll(omssaHits);
		Set<Long> identifiedSpectra = new HashSet<Long>();
		for (SearchHit hit : hits) {
			identifiedSpectra.add(hit.getFk_searchspectrumid());
		}
		
		if (!identCbx.isSelected()) {
			// remove identified spectra
			allSearchspectrumIds.removeAll(identifiedSpectra);
		}
		if (!unidentCbx.isSelected()) {
			// retain only identified spectra
			allSearchspectrumIds.retainAll(identifiedSpectra);
		}
		
		// set up the file stream
		this.firePropertyChange("new message", null, "WRITING REFERENCED SPECTRA");
		this.firePropertyChange("resetall", -1L, (long) allSearchspectrumIds.size());
		this.firePropertyChange("resetcur", -1L, (long) allSearchspectrumIds.size());
		String status = "FINISHED";

		try {
			String prefix = path.substring(0, path.indexOf('.'));
			File mgfFile = new File(prefix + ".mgf");
			FileOutputStream fos = new FileOutputStream(mgfFile);
			// write all relevant MGFs
			for (Long searchSpectrumId : allSearchspectrumIds) {
				long spectrumId = Searchspectrum.findFromSearchSpectrumID(searchSpectrumId, conn).getFk_spectrumid();
				MascotGenericFile mgf = Spectrum.getSpectrumFileFromIdAndTitle(spectrumId, lookUpTxt.getText(), conn);
				if (mgf != null) {
					mgf.writeToStream(fos);
				}
				this.firePropertyChange("progressmade", false, true);	
			}
			fos.flush();
			fos.close();
		} catch (Exception e) {
			JXErrorPane.showDialog(ClientFrame.getInstance(),
					new ErrorInfo("Severe Error", e.getMessage(), null, null, e, ErrorLevel.SEVERE, null));
			status = "FAILED";
		}
		this.firePropertyChange("new message", null, "WRITING FETCHED SPECTRA" + status);
		
	}
	
}
