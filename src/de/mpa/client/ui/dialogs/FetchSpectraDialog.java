package de.mpa.client.ui.dialogs;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.model.AbstractExperiment;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.ConfirmFileChooser;
import de.mpa.client.ui.ScreenConfig;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.db.DBManager;

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
								Client.getInstance().fetchAndExportSpectra(filePath, 
																experiment.getID(),
																identCbx.isSelected(),
																unidentCbx.isSelected(),
																lookUpTxt.getText());
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
	
}
