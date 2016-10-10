package de.mpa.client.ui.dialogs;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.apache.commons.lang3.StringUtils;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Constants;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.ConfirmFileChooser;
import de.mpa.client.ui.ScreenConfig;
import de.mpa.client.ui.icons.IconConstants;


/**
 * Class for the BLAST Dialog
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
				+ "Please select the new *.fasta databases." +
				" For formatting reason the names of the fasta databases " +
				"<b>may comprise maximal 6 characters or numbers</b>.</p>"
				+ "</html>");
		textLbl.setFont(new Font("Serif", Font.PLAIN, 14));
		textLbl.setPreferredSize(new Dimension(340, 80));
		JPanel lablePnl = new JPanel(new BorderLayout());
		lablePnl.add(textLbl, BorderLayout.NORTH);
		lablePnl.setMaximumSize(new Dimension(350, 85));

		// Checkbox for new Mascot-database
		final JCheckBox mascotFastaCbx = new JCheckBox("<html><p align='justify'>" +"Create a new *.fasta database in the same directory as input for the Mascot Searches"+ "</p>  </html>");
		mascotFastaCbx.setFont(new Font("Serif", Font.PLAIN, 14));
		mascotFastaCbx.setPreferredSize(new Dimension(340, 60));
		JPanel checkboxPnl = new JPanel(new BorderLayout());
		checkboxPnl.add(mascotFastaCbx, BorderLayout.NORTH);
		checkboxPnl.setMaximumSize(new Dimension(350, 70));


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
				addFastaDatabases(mascotFastaCbx.isSelected());
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
	 * Methode for the selection of new *.fasta databases.
	 * @param mascotFastaCbx. Selection for a new Mascot-database
	 */
	private void addFastaDatabases(boolean mascotFlag) {
		
		// The fasta files
		File [] fastaFiles = null;
		
		// The output path
		String outpath = null;

		// Get *.fasta files
		JFileChooser fastaChooser = new JFileChooser();
		fastaChooser.setFileFilter(Constants.FASTA_FILE_FILTER);
		fastaChooser.setMultiSelectionEnabled(true);
		int returnVal = fastaChooser.showOpenDialog(owner);
		fastaChooser.setAcceptAllFileFilterUsed(false);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			fastaFiles = fastaChooser.getSelectedFiles();
		}

		// Check for maximum 6 chars
		for (int j = 0; j < fastaFiles.length; j++) {
			String filename = fastaFiles[j].getName().toString();
			filename = filename.split("[.]")[0];
			if (filename.length()>6 || !StringUtils.isAlphanumeric(filename)) {
				JXErrorPane.showDialog(ClientFrame.getInstance(),
						new ErrorInfo("Error", "The names of the fasta databases may comprise maximal 6 " +
								"characters or numbers", null, null, null, ErrorLevel.SEVERE, null));
				close();
			}
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

			// Start creation of new fasta database
//			try {
//				Client.getInstance().firePropertyChange("new message", null, "Adding a new *.fasta database");
//				Client.getInstance().firePropertyChange("indeterminate", false, true);
//				FastaUtilities.addFastaDatabases(fastaFiles, new File(outpath), mascotFlag);
//				
//				// Show finished message
//				StringBuilder message = new StringBuilder();	
//				message.append("Database successfully upload");
//				message.append("\n\n");
//				message.append("Please restart the MPA before you can use the new database.");
//				message.append("\n\n");
//				JOptionPane.showMessageDialog(ClientFrame.getInstance(), message,
//						"About " + Constants.APPTITLE, JOptionPane.INFORMATION_MESSAGE);
//				Client.getInstance().firePropertyChange("indeterminate", true, false);
//				Client.getInstance().firePropertyChange("new message", null, "Adding new *.fasta finished");
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
	}		
}
