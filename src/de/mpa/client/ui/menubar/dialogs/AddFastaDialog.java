package de.mpa.client.ui.menubar.dialogs;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.sharedelements.ScreenConfig;
import de.mpa.client.ui.sharedelements.icons.IconConstants;
import de.mpa.io.fasta.FastaLoader;
import de.mpa.model.analysis.UniProtUtilities;

// TODO: all of this needs to be more performant

/**
 * 
 * Class for the FASTA Dialog
 * 
 * @author Robert Heyer
 */
@SuppressWarnings("serial")
public class AddFastaDialog extends JDialog {

	/**
	 * The ClientFrame
	 */
	private final ClientFrame owner;

	/**
	 * FASTA file-String selected
	 */
	private File fastaFile;

	/**
	 * DB-Name
	 */
	private String dbName;

	/**
	 * Ui Elements
	 */
	private JProgressBar progressbar;
	private JButton browseButton;
	private JTextField fileTextField;
	private JTextField dbNameTextField;
	private JButton okBtn;
	private JButton cancelBtn;

	/**
	 * @param owner.
	 *            The owner of the dialog.
	 * @param title.
	 *            The title of the dialog.
	 */
	public AddFastaDialog(ClientFrame owner, String title) {
		super(owner, title);
		this.owner = owner;
		this.initComponents();
		this.showDialog();
	}

	/**
	 * Initializes and lays out all components inside this dialog grouped by
	 * sections identifiers.
	 */
	private void initComponents() {

		// AddFasta-Dialog - set up the main window
		JPanel addFastaDlgPnl = new JPanel(
				new FormLayout("pref, 5px, pref", "pref, 5px, pref, 5px, pref, 5px, pref, 5px, pref"));
		// add description
		JTextArea descriptionTextArea = new JTextArea();
		descriptionTextArea.setEditable(false);
		descriptionTextArea
				.setText("You may add a FASTA protein-database, which conforms to the following formatting: \n"
						+ ">[DBID]|[accession]|[description]");

		// add textbox for file
		fileTextField = new JTextField();

		// add browse button for file
		browseButton = new JButton("Browse");
		browseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fastaChooser = new JFileChooser();
				fastaChooser.setFileFilter(Constants.FASTA_FILE_FILTER);
				fastaChooser.setMultiSelectionEnabled(false);
				fastaChooser.setDialogTitle("Choose the FASTA protein database you want to add");
				int returnVal = fastaChooser.showOpenDialog(owner);
				fastaChooser.setAcceptAllFileFilterUsed(false);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					fastaFile = fastaChooser.getSelectedFile();
				}
				if (fastaFile != null) {
					fileTextField.setText(fastaFile.getAbsolutePath().toString());
				}
			}
		});

		// add textbox for name
		dbNameTextField = new JTextField();
		dbNameTextField.setText("ProteinDatabase");
		dbNameTextField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				dbName = dbNameTextField.getText();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				dbName = dbNameTextField.getText();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				dbName = dbNameTextField.getText();
			}
		});

		// ok button
		okBtn = new JButton("OK", IconConstants.CHECK_ICON);
		okBtn.setRolloverIcon(IconConstants.CHECK_ROLLOVER_ICON);
		okBtn.setPreferredSize(new Dimension(80, 20));
		okBtn.setPressedIcon(IconConstants.CHECK_PRESSED_ICON);
		okBtn.setHorizontalAlignment(SwingConstants.LEFT);
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (fastaFile != null && fastaFile.exists()) {
					Client.getInstance().firePropertyChange("indeterminate", false, true);
					new AddFastaTask().execute();
					// Start generation of a new fasta database
				
				} else {
					JOptionPane.showMessageDialog(ClientFrame.getInstance(), "No FASTA file selected",
							"About " + Constants.APPTITLE, JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});

		// cancel button
		cancelBtn = new JButton("Cancel", IconConstants.CROSS_ICON);
		cancelBtn.setRolloverIcon(IconConstants.CROSS_ROLLOVER_ICON);
		cancelBtn.setPreferredSize(new Dimension(80, 20));
		cancelBtn.setPressedIcon(IconConstants.CROSS_PRESSED_ICON);
		cancelBtn.setHorizontalAlignment(SwingConstants.LEFT);
		cancelBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AddFastaDialog.this.close();
			}
		});

		this.progressbar = new JProgressBar();
		progressbar.setStringPainted(true);

		// add components
		addFastaDlgPnl.add(descriptionTextArea, CC.xy(1, 1));
		addFastaDlgPnl.add(fileTextField, CC.xy(1, 5));
		addFastaDlgPnl.add(browseButton, CC.xy(3, 5));
		addFastaDlgPnl.add(dbNameTextField, CC.xy(1, 7));
		addFastaDlgPnl.add(progressbar, CC.xy(1, 9));

		// make button panel
		JPanel buttonPanel = new JPanel(new FormLayout("pref, 5px, pref", "pref"));
		buttonPanel.add(okBtn, CC.xy(1, 1));
		buttonPanel.add(cancelBtn, CC.xy(3, 1));

		// embed in nice container
		Container cp = getContentPane();
		cp.setLayout(new FormLayout("10px, pref, 10px", "10px, pref, 10px, pref, 10px"));
		cp.add(addFastaDlgPnl, CC.xy(2, 2));
		cp.add(buttonPanel, CC.xy(2, 4));

	}

	/**
	 * This method shows the dialog.
	 */
	private void showDialog() {
		// Configure size and position
		pack();
		setResizable(false);
		ScreenConfig.centerInScreen(this);
		// Show dialog
		setVisible(true);
	}

	/**
	 * Close method for the dialog.
	 */
	private void close() {
		this.dispose();
	}

	/**
	 * Class to fetch protein database search results in a background thread.
	 * It's possible to fetch from the remote SQL database or from a local file instance.
	 * 
	 * @author A. Behne, R. Heyer
	 */
	private class AddFastaTask extends SwingWorker<Integer, Object> {
		
		/**
		 * Constructor for the FetchResultTask. The experiment list ask which experiments 
		 * should be fetched. If not given it uses the selected experiment into the project panel/ 
		 * @param experimentList. the list of experiment titles
		 */
		public AddFastaTask() {
			// do nothing
		}

		@Override
		protected Integer doInBackground() {
			try {
				AddFastaDialog.this.browseButton.setEnabled(false);
				AddFastaDialog.this.fileTextField.setEditable(false);
				AddFastaDialog.this.dbNameTextField.setEditable(false);
				AddFastaDialog.this.okBtn.setEnabled(false);
				AddFastaDialog.this.cancelBtn.setEnabled(false);
				
				Client.getInstance().firePropertyChange("new message", null, "Adding a new *.fasta database");
				Client.getInstance().firePropertyChange("indeterminate", false, true);
				// deal with uninitialized variables
				if (dbName == null || dbName == "") {
					JOptionPane.showMessageDialog(ClientFrame.getInstance(),
							"No Database Name selected, database name set to 'ProteinDatabase'",
							"About " + Constants.APPTITLE, JOptionPane.INFORMATION_MESSAGE);
					dbName = "ProteinDatabase";
				}
				// progress bar reset
				progressbar.setMinimum(0);
				progressbar.setMaximum(100);
				progressbar.setValue(5);
				progressbar.setString("0%");
				
				// add fasta
				// TODO: Client Feedback during add Fasta
				FastaLoader.addFastaDatabases(fastaFile, dbName, UniProtUtilities.BATCH_SIZE, progressbar);
				
				// progress bar finished
				progressbar.setValue(100);
				progressbar.setString("100%");

				// Show finished message
				StringBuilder message = new StringBuilder();
				message.append("Fasta Upload successful");
				message.append("\n");
				JOptionPane.showMessageDialog(ClientFrame.getInstance(), message, "About " + Constants.APPTITLE,
						JOptionPane.INFORMATION_MESSAGE);
				
				Client.getInstance().firePropertyChange("indeterminate", true, false);
				Client.getInstance().firePropertyChange("new message", null, "FASTA UPLOAD FINISHED");
				
			} catch (IOException | SQLException err) {
				err.printStackTrace();
			}
			Client.getInstance().firePropertyChange("indeterminate", true, false);
			ClientFrame.getInstance().restart();
			AddFastaDialog.this.close();
			return 0;
		}

	}
}
