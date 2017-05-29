package de.mpa.client.ui.menubar.dialogs;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Client;
import de.mpa.client.Constants;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.sharedelements.ScreenConfig;
import de.mpa.client.ui.sharedelements.icons.IconConstants;
import de.mpa.model.blast.RunMultiBlast;

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
	 * Checkbox to set BLAST to "all proteins" or "only proteins identified" 
	 */
	private JCheckBox allProteinsCheckbox;

	/**
	 * BLAST everything checkbox-value
	 */
	private boolean allProteinsCBValue = false;
	
	/**
	 * The result option checkbox
	 */
	private JComboBox<BlastDialog.BlastResultOption> resultOptionCbx;
	
	private JProgressBar progressbar;
	private JButton cancelBtn;
	private JButton okBtn;

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
		JPanel blastSettingsPnl 	= new JPanel(new FormLayout("5dlu, p:g, 5dlu", "5dlu, p:g, 5dlu, p:g, 5dlu, p:g, 5dlu, p:g, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu, p, 5dlu,p, 5dlu, p, 5dlu, p , 5dlu"));
		JLabel blastLbl 		= new JLabel("Path of the BLAST Algorithms");
		blastTxt = new JTextField(Constants.BLAST_FILE);
		// BLAST db path - the database against which you perform the BLAST (must be a uniprot)
		JLabel dbLbl 			= new JLabel("Path of the preformatted db (See BLAST Manual)");
		dbTxt = new JTextField(Constants.BLAST_UNIPROT_DB);
		// Textfield for the userinput: evalue
		JLabel eValueLbl 		= new JLabel("E-Value cut-off");
		eValueTxt = new JTextField("" + Constants.BLAST_EVALUE);
		// Textfield to get user input: experimentID which should be blasted, where -1 means ALL experiments
		JLabel expLbl 		= new JLabel("Choose experiment ID (-1 for all)");
		expTxt = new JTextField("-1");
		// Combobox for the BLAST result-evaluation method + textfield 
		JLabel resultOptionLbl = new JLabel("Select the option for hit selection");
		BlastDialog.BlastResultOption[] items = BlastDialog.BlastResultOption.values();
		resultOptionCbx = new JComboBox<BlastDialog.BlastResultOption>(items);
		// checkbox to denote if all proteins are blasted or only those that were identified by a protein search 
		allProteinsCheckbox = new JCheckBox("Global BLAST");
		allProteinsCheckbox.setSelected(false);
		allProteinsCheckbox.setToolTipText("BLAST all proteins in the database, including those that were not found in a protein search.");
		allProteinsCheckbox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (allProteinsCheckbox.isSelected()) {
					expTxt.setEnabled(false);
					allProteinsCBValue = true;
				} else {
					expTxt.setEnabled(true);
					allProteinsCBValue = false;
				}
			}
		});
		
		this.progressbar = new JProgressBar();
		this.progressbar.setMaximum(100);
		this.progressbar.setMinimum(0);
		this.progressbar.setStringPainted(true);
		this.progressbar.setString("0%");
		this.progressbar.setValue(0);

		// Add Components to the BLAST panel
		blastSettingsPnl.add(blastLbl, 	CC.xy(2, 2));
		blastSettingsPnl.add(blastTxt, 	CC.xy(2, 4));
		blastSettingsPnl.add(dbLbl, 	CC.xy(2, 6));
		blastSettingsPnl.add(dbTxt, 	CC.xy(2, 8));
		blastSettingsPnl.add(eValueLbl, CC.xy(2, 10));
		blastSettingsPnl.add(eValueTxt, CC.xy(2, 12));
		blastSettingsPnl.add(expLbl, 	CC.xy(2, 14));
		blastSettingsPnl.add(expTxt, 	CC.xy(2, 16));
		blastSettingsPnl.add(allProteinsCheckbox, 	CC.xy(2, 18));
		blastSettingsPnl.add(resultOptionLbl,	CC.xy(2, 20));
		blastSettingsPnl.add(resultOptionCbx, 	CC.xy(2, 22));
		blastSettingsPnl.add(progressbar, 	CC.xy(2, 24));

		// Configure 'OK' button
		okBtn = new JButton("OK", IconConstants.CHECK_ICON);
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
		cancelBtn = new JButton("Cancel", IconConstants.CROSS_ICON);
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
			// TODO: Manage malformed user input
			// just run the static method for BLAST searches and finished
			
			BlastDialog.this.okBtn.setEnabled(false);
			BlastDialog.this.cancelBtn.setEnabled(false);

			BlastDialog.this.allProteinsCheckbox.setEnabled(false);
			BlastDialog.this.resultOptionCbx.setEnabled(false);
			
			BlastDialog.this.dbTxt.setEnabled(false);
			BlastDialog.this.expTxt.setEnabled(false);
			BlastDialog.this.eValueTxt.setEnabled(false);
			BlastDialog.this.blastTxt.setEnabled(false);
			
			RunMultiBlast.performBLAST4Experiments(blastTxt.getText(), dbTxt.getText(), Double.parseDouble(eValueTxt.getText()), Long.valueOf(expTxt.getText()), BlastDialog.this.allProteinsCBValue, (BlastDialog.BlastResultOption) BlastDialog.this.resultOptionCbx.getSelectedItem(), progressbar);
			BlastDialog.this.close();
			return null;
		}
		
	}
}
