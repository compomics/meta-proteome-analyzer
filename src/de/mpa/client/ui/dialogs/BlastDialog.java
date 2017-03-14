package de.mpa.client.ui.dialogs;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Constants;
import de.mpa.client.blast.RunMultiBlast;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.ScreenConfig;
import de.mpa.client.ui.icons.IconConstants;

/**
 * Class for the BLAST Dialog
 * @author Robert Heyer
 */
public class BlastDialog extends JDialog {
	
	/**
	 * The serial number
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The parent (ClientFrame)
	 */
	private final ClientFrame owner;
	
	/**
	 * File of the BLAST algorithm
	 */
	private JTextField blastTxt;
	
	/**
	 * The evalue for the BLAST algorithm
	 */
	private JTextField eValueTxt;
	
	/**
	 * The database for the BLAST
	 */
	private JTextField dbTxt;
	
	/**
	 * The experiment ID for the BLAST
	 */
	private JTextField expTxt;

	/**
	 * The result option checkbox
	 */
	private JComboBox<BlastDialog.BlastResultOption> resultOptionCbx;

	/**
	 * ENUM for the BLAST options
	 * @author Robert H. and Kay. S
	 *
	 */
	public enum BlastResultOption {
		BEST_EVALUES("Best E-value"),
		BEST_IDENTITIES("Best Identity"),
		BEST_BITSCORE("Best BitScore"),
		FIRST_EVALUE("First E-value"),
		FIRST_IDENTITY("First Identity"),
		FIRST_BITSCORE("First BitScore"),
		ALL_HITS("All Hits");

		/**
		 * The String behind the ENUM
		 */
		private String val;

		/**
		 * Method to return the String of the ENUM
		 * @param value
		 */
        BlastResultOption(String value) {
			this.val = value;
		}

		@Override
		public String toString() {
			return val;
		}
	}

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
		JPanel blastSettingsPnl 	= new JPanel(new FormLayout("5dlu, p:g, 5dlu", "5dlu, p:g, 5dlu, p:g, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu,p, 5dlu, p, 5dlu, p , 5dlu"));
		JLabel blastLbl 		= new JLabel("Path of the BLAST Algorithms");
		blastTxt = new JTextField(Constants.BLAST_FILE);
		JLabel dbLbl 			= new JLabel("Path of the preformatted db (See BLAST Manual");
		dbTxt = new JTextField(Constants.BLAST_UNIPROT_DB);
		JLabel eValueLbl 		= new JLabel("E-Value cut-off");
		eValueTxt = new JTextField("" + Constants.BLAST_EVALUE);
		JLabel expLbl 		= new JLabel("Choose experiment ID (-1 for all)");
		expTxt = new JTextField("-1");
		JLabel resultOptionLbl = new JLabel("Select the option for hit selection");
		BlastDialog.BlastResultOption[] items = BlastDialog.BlastResultOption.values();
		resultOptionCbx = new JComboBox<BlastDialog.BlastResultOption>(items);

		// Add Components to the BLAST panel
		blastSettingsPnl.add(blastLbl, 	CC.xy(2, 2));
		blastSettingsPnl.add(blastTxt, 	CC.xy(2, 4));
		blastSettingsPnl.add(dbLbl, 	CC.xy(2, 6));
		blastSettingsPnl.add(dbTxt, 	CC.xy(2, 8));
		blastSettingsPnl.add(eValueLbl, CC.xy(2, 10));
		blastSettingsPnl.add(eValueTxt, CC.xy(2, 12));
		blastSettingsPnl.add(expLbl, 	CC.xy(2, 14));
		blastSettingsPnl.add(expTxt, 	CC.xy(2, 16));
		blastSettingsPnl.add(resultOptionLbl,	CC.xy(2, 18));
		blastSettingsPnl.add(resultOptionCbx, 	CC.xy(2, 20));

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
			RunMultiBlast.performBLAST4Experiments(blastTxt.getText(), dbTxt.getText(), Double.parseDouble(eValueTxt.getText()), Long.valueOf(expTxt.getText()), (BlastDialog.BlastResultOption) BlastDialog.this.resultOptionCbx.getSelectedItem());
//			UniProtUtilities uniprotweb = new UniProtUtilities();
			// filter list to proteins for blast
			
			
//			
//			List<ProteinAccessor> blast_list = uniprotweb.find_proteins_for_blast(proteins);
//			// do blast from proteinlist
//			Map<String, List<Long>> new_proteins = uniprotweb.perform_blast(blast_list, blastTxt.getText(), dbTxt.getText(), Double.parseDouble(eValueTxt.getText()));
//			// make uniprotentries from proteinlist
//			uniprotweb.make_uniprot_entries(new_proteins);
			return null;
		}
		
	}
}
