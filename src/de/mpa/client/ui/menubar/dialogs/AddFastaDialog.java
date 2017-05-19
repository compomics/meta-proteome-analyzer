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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
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
	 * @param owner. The owner of the dialog.
	 * @param title. The title of the dialog.
	 */
	public AddFastaDialog(ClientFrame owner, String title) {
		super(owner, title);
		this.owner = owner;
        this.initComponents();
        this.showDialog();
	}

	/**
	 * Initializes and lays out all components inside this dialog grouped by sections identifiers.
	 */
	private void initComponents() {
		
		// AddFasta-Dialog - set up the main window
		JPanel addFastaDlgPnl = new JPanel(new FormLayout("pref, 5px, pref",
														  "pref, 5px, pref, 5px, pref, 5px, pref, 5px, pref"));
		// add description
		JTextArea descriptionTextArea = new JTextArea();
		descriptionTextArea.setEditable(false);
		descriptionTextArea.setText("You may add a FASTA protein-database, which conforms to the following formatting: \n"
									+ ">[DBID]|[accession]|[description]");
				
		// add textbox for file
		JTextField fileTextField = new JTextField();
		
		// add browse button for file
		JButton browseButton = new JButton("Browse");
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
		JTextField dbNameTextField = new JTextField();
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
		JButton okBtn = new JButton("OK", IconConstants.CHECK_ICON);
		okBtn.setRolloverIcon(IconConstants.CHECK_ROLLOVER_ICON);
		okBtn.setPreferredSize(new Dimension(80, 20));
		okBtn.setPressedIcon(IconConstants.CHECK_PRESSED_ICON);
		okBtn.setHorizontalAlignment(SwingConstants.LEFT);
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AddFastaDialog.this.close();
				if (fastaFile != null && fastaFile.exists()) {
					Client.getInstance().firePropertyChange("indeterminate", false, true);
					// Start generation of a new fasta database
					try {
						Client.getInstance().firePropertyChange("new message", null, "Adding a new *.fasta database");
						Client.getInstance().firePropertyChange("indeterminate", false, true);
						// deal with uninitialized variables
						if (dbName == null || dbName == "") {
							JOptionPane.showMessageDialog(ClientFrame.getInstance(), "No Database Name selected, database name set to 'ProteinDatabase'",
									"About " + Constants.APPTITLE, JOptionPane.INFORMATION_MESSAGE);
							dbName = "ProteinDatabase";
						}
						FastaLoader.addFastaDatabases(fastaFile, dbName, UniProtUtilities.BATCH_SIZE);
						// Show finished message
						StringBuilder message = new StringBuilder();	
						message.append("Database successfully uploaded");
						message.append("\n\n");
						message.append("Please restart the MPA before you can use the new database.");
						message.append("\n\n");
						JOptionPane.showMessageDialog(ClientFrame.getInstance(), message,
								"About " + Constants.APPTITLE, JOptionPane.INFORMATION_MESSAGE);
						Client.getInstance().firePropertyChange("indeterminate", true, false);
						Client.getInstance().firePropertyChange("new message", null, "Adding new *.fasta finished");
					} catch (IOException | SQLException err) {
						err.printStackTrace();
					}
					Client.getInstance().firePropertyChange("indeterminate", true, false);
				} else {
					JOptionPane.showMessageDialog(ClientFrame.getInstance(), "No FASTA file selected",
							"About " + Constants.APPTITLE, JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		
		// cancel button
		JButton cancelBtn = new JButton("Cancel", IconConstants.CROSS_ICON);
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
		
		// add components
		addFastaDlgPnl.add(descriptionTextArea, CC.xy(1, 1));
		addFastaDlgPnl.add(fileTextField, CC.xy(1, 5));
		addFastaDlgPnl.add(browseButton, CC.xy(3, 5));
		addFastaDlgPnl.add(dbNameTextField, CC.xy(1, 7));

		// make button panel 
		JPanel buttonPanel = new JPanel(new FormLayout("pref, 5px, pref",
				  "pref"));
		buttonPanel.add(okBtn, CC.xy(1, 1));
		buttonPanel.add(cancelBtn, CC.xy(3, 1) );
		
		// embed in nice container
		Container cp = getContentPane();
		cp.setLayout(new FormLayout("10px, pref, 10px", "10px, pref, 10px, pref, 10px"));
		cp.add(addFastaDlgPnl, CC.xy(2, 2));
		cp.add(buttonPanel, CC.xy(2, 4));

//
//		// Lable for the description of the dialog
//		JLabel textLbl = new JLabel("<html><p align='justify'>"
//				+ "Please select the *.fasta-databases" +
//				" and enter the name of the new FASTA-databases.</p></html>");
//		textLbl.setFont(new Font("Serif", Font.PLAIN, 14));
//		textLbl.setPreferredSize(new Dimension(360, 30));
//		JPanel lablePnl = new JPanel(new BorderLayout());
//		lablePnl.add(textLbl, BorderLayout.NORTH);
//		lablePnl.setMaximumSize(new Dimension(370, 35));

		// Checkbox for new Mascot-database
		// XXX: This checkbox will be integrated later
//		JCheckBox mascotFastaCbx = new JCheckBox("<html><p align='justify'>" +"Create a new *.fasta-database in the same directory as the input databases for the Mascot searches."+
//				" For later uploaded of Mascot dat-files please use following parsing rules: \n <b> >..|[^|]*|\\([^ ]*\\)     SwissProt</b></p>  </html>");
//		mascotFastaCbx.setFont(new Font("Serif", Font.PLAIN, 14));
//		mascotFastaCbx.setPreferredSize(new Dimension(360, 100));
//		JPanel checkboxPnl = new JPanel(new BorderLayout());
//		checkboxPnl.add(mascotFastaCbx, BorderLayout.NORTH);
//		checkboxPnl.setMaximumSize(new Dimension(370, 100));
//
//		descPnl.add(lablePnl, 	CC.xy(2, 2));
//		descPnl.add(mascotFastaCbx, 	CC.xy(2, 4));
		
		
		
		// Configure 'OK' button




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
	 * 
	 * 
	 * Methode for the selection of new *.fasta databases.
	 * @param mascotFastaCbx. Selection for a new Mascot-database
	 */
	@Deprecated
	private void addFastaDatabases() {
		
		// TODO: what it needs to do
		// 0. calls the Fastaloader which does everything
		// 1. load fasta put proteins into SQL
		// 2. check for redundancy
		// 3. query uniprot in case of uniprot proteins (Swissprot or Trembl)
		// 4. be quick and dont crash ......
		
		// TODO: new quick implementation:
		// ???
		
		// The fasta files
		File fastaFile = null;

		// The output path
		String outpath = null;

		// Get *.fasta files
		// XXX: This is the FIRST of the 2 dialogs
		JFileChooser fastaChooser = new JFileChooser();
		fastaChooser.setFileFilter(Constants.FASTA_FILE_FILTER);
		fastaChooser.setMultiSelectionEnabled(false);
		fastaChooser.setDialogTitle("Choose the FASTA protein database you want to add");
		int returnVal = fastaChooser.showOpenDialog(this.owner);
		fastaChooser.setAcceptAllFileFilterUsed(false);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			fastaFile = fastaChooser.getSelectedFile();
		}

		String dbName = JOptionPane.showInputDialog(new JDialog(), "Select a name for this database"); 
		
//		// Define name for the fasta database.
//		JFileChooser outputChooser = new ConfirmFileChooser();
//				ClientFrame clientFrame = ClientFrame.getInstance();
//		outputChooser.setCurrentDirectory(new File(clientFrame.getLastSelectedFolder()));
//		outputChooser.setFileFilter(Constants.FASTA_FILE_FILTER);
//		outputChooser.setAcceptAllFileFilterUsed(false);
//		int retVal = outputChooser.showSaveDialog(clientFrame);
//		if (retVal == JFileChooser.APPROVE_OPTION) {
//			File selFile = outputChooser.getSelectedFile();
//			if (selFile != null) {
//				outpath = selFile.getPath();
//				clientFrame.setLastSelectedFolder(selFile.getParent());
//				if (!outpath.toLowerCase().endsWith(".fasta")) {
//					outpath += ".fasta";
//				}
//			}
//		}

	}
}
