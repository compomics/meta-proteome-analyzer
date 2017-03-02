package de.mpa.client.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.analysis.UniProtUtilities;
import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.ConfirmFileChooser;
import de.mpa.client.ui.ScreenConfig;
import de.mpa.client.ui.icons.IconConstants;
import de.mpa.io.fasta.FastaLoader;



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
	private ClientFrame owner;

	/**
	 * @param owner. The owner of the dialog.
	 * @param title. The title of the dialog.
	 */
	public AddFastaDialog(ClientFrame owner, String title) {
		super(owner, title);
		this.owner = owner;
		initComponents();
		showDialog();
	}

	/**
	 * Initializes and lays out all components inside this dialog grouped by sections identifiers.
	 */
	private void initComponents() {

		// Create BLAST dialog
		JPanel addFastaDlgPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu, p:g, 5dlu", "5dlu, p, 5dlu, p, 5dlu"));

		JPanel descPnl	 	= new JPanel(new FormLayout("5dlu, p:g, 5dlu", "5dlu, p, 5dlu, p, 5dlu"));

		// Lable for the description of the dialog
		JLabel textLbl = new JLabel("<html><p align='justify'>"
				+ "Please select the *.fasta-databases" +
				" and enter the name of the new FASTA-databases.</p></html>");
		textLbl.setFont(new Font("Serif", Font.PLAIN, 14));
		textLbl.setPreferredSize(new Dimension(360, 30));
		JPanel lablePnl = new JPanel(new BorderLayout());
		lablePnl.add(textLbl, BorderLayout.NORTH);
		lablePnl.setMaximumSize(new Dimension(370, 35));

		// Checkbox for new Mascot-database
		final JCheckBox mascotFastaCbx = new JCheckBox("<html><p align='justify'>" +"Create a new *.fasta-database in the same directory as the input databases for the Mascot searches."+ 
				" For later uploaded of Mascot dat-files please use following parsing rules: \n <b> >..|[^|]*|\\([^ ]*\\)     SwissProt</b></p>  </html>");
		mascotFastaCbx.setFont(new Font("Serif", Font.PLAIN, 14));
		mascotFastaCbx.setPreferredSize(new Dimension(360, 100));
		JPanel checkboxPnl = new JPanel(new BorderLayout());
		checkboxPnl.add(mascotFastaCbx, BorderLayout.NORTH);
		checkboxPnl.setMaximumSize(new Dimension(370, 100));

		descPnl.add(lablePnl, 	CC.xy(2, 2));
		descPnl.add(mascotFastaCbx, 	CC.xy(2, 4));
		// Configure 'OK' button
		JButton okBtn = new JButton("OK", IconConstants.CHECK_ICON);
		okBtn.setRolloverIcon(IconConstants.CHECK_ROLLOVER_ICON);
		okBtn.setPressedIcon(IconConstants.CHECK_PRESSED_ICON);
		okBtn.setHorizontalAlignment(SwingConstants.LEFT);
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				close();
				Client.getInstance().firePropertyChange("indeterminate", false, true);
				// TODO: make sure clientFrame gives correct feedback 
				addFastaDatabases(mascotFastaCbx.isSelected());
				Client.getInstance().firePropertyChange("indeterminate", true, false);
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

		addFastaDlgPnl.add(descPnl, CC.xyw(2, 2,3));
		addFastaDlgPnl.add(okBtn,CC.xy(2, 4) );
		addFastaDlgPnl.add(cancelBtn,CC.xy(4, 4) );
		Container cp = this.getContentPane();		
		cp.setLayout(new FormLayout("5dlu, r:p, 5dlu", "5dlu, f:p:g, 5dlu"));

		cp.add(addFastaDlgPnl, CC.xy(2,  2));

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
	 * 
	 * 
	 * Methode for the selection of new *.fasta databases.
	 * @param mascotFastaCbx. Selection for a new Mascot-database
	 */
	private void addFastaDatabases(boolean mascotFlag) {
		
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
		JFileChooser fastaChooser = new JFileChooser();
		fastaChooser.setFileFilter(Constants.FASTA_FILE_FILTER);
		fastaChooser.setMultiSelectionEnabled(false);
		int returnVal = fastaChooser.showOpenDialog(owner);
		fastaChooser.setAcceptAllFileFilterUsed(false);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			fastaFile = fastaChooser.getSelectedFile();
		}

		// Define name for the fasta database.
		JFileChooser outputChooser = new ConfirmFileChooser();
		ClientFrame clientFrame = ClientFrame.getInstance();
		outputChooser.setCurrentDirectory(new File(clientFrame.getLastSelectedFolder()));
		outputChooser.setFileFilter(Constants.FASTA_FILE_FILTER);
		outputChooser.setAcceptAllFileFilterUsed(false);
		int retVal = outputChooser.showSaveDialog(clientFrame);
		if (retVal == JFileChooser.APPROVE_OPTION) {
			File selFile = outputChooser.getSelectedFile();
			if (selFile != null) {
				outpath = selFile.getPath();
				clientFrame.setLastSelectedFolder(selFile.getParent());
				if (!outpath.toLowerCase().endsWith(".fasta")) {
					outpath += ".fasta";
				}
			}
		}

		// Start generation of a new fasta database
		try {
			Client.getInstance().firePropertyChange("new message", null, "Adding a new *.fasta database");
			Client.getInstance().firePropertyChange("indeterminate", false, true);
			System.out.println(fastaFile.toString());
			FastaLoader.addFastaDatabases(fastaFile, new File(outpath), mascotFlag, UniProtUtilities.BATCH_SIZE);

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
		} catch (IOException | SQLException e) {
			e.printStackTrace();
		}
	}		
}
