package de.mpa.client.ui.dialogs;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.analysis.UniProtUtilities;
import de.mpa.client.Constants;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.ScreenConfig;
import de.mpa.client.ui.icons.IconConstants;

/**
 * Class for the BLAST Dialog
 * @author Robert Heyer
 */
public class BlastDialog extends JDialog {
	
	/**
	 * The ClientFrame
	 */
	private ClientFrame owner;
	private JTextField blastTxt;
	private JTextField eValueTxt;
	private JTextField dbTxt;
	
	/**
	 * @param owner. The owner of the dialog.
	 * @param title. The title of the dialog.
	 */
	public BlastDialog(ClientFrame owner, String title) {
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
		JPanel blastDlgPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu, f:p:g, 5dlu"));
		JPanel blastSettingsPnl 	= new JPanel(new FormLayout("5dlu, p:g, 5dlu", "5dlu, p:g, 5dlu, p:g, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu"));
		JLabel blastLbl 		= new JLabel("Path of the BLAST Algorithms");
		blastTxt = new JTextField(Constants.BLAST_FILE); 
		JLabel dbLbl 			= new JLabel("Path of the preformatted db (See BLAST Manual");
		dbTxt = new JTextField(Constants.BLAST_UNIPROT_DB); 
		JLabel eValueLbl 		= new JLabel("E-Value cut-off");
		eValueTxt = new JTextField("" + Constants.BLAST_EVALUE); 
		blastSettingsPnl.add(blastLbl, 	CC.xy(2, 2));
		blastSettingsPnl.add(blastTxt, 	CC.xy(2, 4));
		blastSettingsPnl.add(dbLbl, 	CC.xy(2, 6));
		blastSettingsPnl.add(dbTxt, 	CC.xy(2, 8));
		blastSettingsPnl.add(eValueLbl, CC.xy(2, 10));
		blastSettingsPnl.add(eValueTxt, CC.xy(2, 12));
		// Configure 'OK' button
		JButton okBtn = new JButton("OK", IconConstants.CHECK_ICON);
		okBtn.setRolloverIcon(IconConstants.CHECK_ROLLOVER_ICON);
		okBtn.setPressedIcon(IconConstants.CHECK_PRESSED_ICON);
		okBtn.setHorizontalAlignment(SwingConstants.LEFT);
		okBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
					BlastWorker blastWorker = new BlastWorker();
					blastWorker.execute();
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
		
		blastDlgPnl.add(blastSettingsPnl, CC.xyw(2, 2,3));
		blastDlgPnl.add(okBtn,CC.xy(2, 4) );
		blastDlgPnl.add(cancelBtn,CC.xy(4, 4) );
		Container cp = this.getContentPane();		
		cp.setLayout(new FormLayout("5dlu, r:p, 5dlu", "5dlu, f:p:g, 5dlu"));

		cp.add(blastDlgPnl, CC.xy(2,  2));
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
	
	public class BlastWorker extends SwingWorker<Object, Object> {

		@Override
		protected Object doInBackground() throws Exception {
			// TODO: Separate blast from uniprot queryiing.
			UniProtUtilities.repairEmptyUniProtEntriesAndBLAST(blastTxt.getText(), dbTxt.getText(), Double.parseDouble(eValueTxt.getText()), true);			
			return null;
		}
		
		protected void finished() {
			System.out.println("Do stuff when blast worker has finished.");
		}
		
	}
	
}
