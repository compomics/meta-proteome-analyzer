package de.mpa.client.ui.menubar.dialogs;


import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.Constants;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.sharedelements.ScreenConfig;
import de.mpa.client.ui.sharedelements.icons.IconConstants;
import de.mpa.model.taxonomy.NcbiTaxonomy;
/**
 * Class for the update NCBI taxonomy 
 * @author R. Heyer
 */
@SuppressWarnings("serial")
public class UpdateNcbiTaxDialog extends JDialog {

	/**
	 * The ClientFrame
	 */
	private final ClientFrame owner;
	
	/**
	 * @param owner. The owner of the dialog.
	 * @param title. The title of the dialog.
	 */
	public UpdateNcbiTaxDialog(ClientFrame owner, String title) {
		super(owner, title);
		this.owner = owner;
        this.initComponents();
        this.showDialog();
	}
	/**
	 * Method to build the dialog
	 */
	private void initComponents() {
		// Create BLAST dialog
				JPanel updateNcbiTaxDlgPnl = new JPanel(new FormLayout("5dlu, p:g, 5dlu, p:g, 5dlu", "5dlu, p, 5dlu, p, 5dlu"));
				JPanel updateNcbiTaxPnl 	= new JPanel(new FormLayout("5dlu, p:g, 5dlu", "5dlu, p, 5dlu"));
				
				JLabel updateNcbiTaxLbl 		= new JLabel("<html>Temporary it is neccessary to update the " +
															       "NCBI Taxonomy. Therefore it is nessary to " +
															       "download (ftp://ftp.ncbi.nlm.nih.gov/pub/taxonomy/taxdmp.zip) " +
															       "and unzip manually the NCBI-Taxonomies " +
															       "and run this script. </html>" );
				updateNcbiTaxLbl.setFont(new Font("Serif", Font.PLAIN, 14));
				updateNcbiTaxLbl.setPreferredSize(new Dimension(395, 100));
				JPanel lablePnl = new JPanel(new BorderLayout());
				lablePnl.add(updateNcbiTaxLbl, BorderLayout.NORTH);
				lablePnl.setMaximumSize(new Dimension(400, 105));
				
				updateNcbiTaxPnl.add(lablePnl, CC.xy(2,  2));
				// Configure 'OK' button
				JButton okBtn = new JButton("OK", IconConstants.CHECK_ICON);
				okBtn.setRolloverIcon(IconConstants.CHECK_ROLLOVER_ICON);
				okBtn.setPressedIcon(IconConstants.CHECK_PRESSED_ICON);
				okBtn.setHorizontalAlignment(SwingConstants.LEFT);
				okBtn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
                        UpdateNcbiTaxDialog.this.updateNcbiTax();
                        UpdateNcbiTaxDialog.this.close();
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
                        UpdateNcbiTaxDialog.this.close();
					}
				});
				
				updateNcbiTaxDlgPnl.add(updateNcbiTaxPnl, CC.xyw(2, 2,3));
				updateNcbiTaxDlgPnl.add(okBtn,CC.xy(2, 4) );
				updateNcbiTaxDlgPnl.add(cancelBtn,CC.xy(4, 4) );
				Container cp = getContentPane();
				cp.setLayout(new FormLayout("5dlu, r:p, 5dlu", "5dlu, f:p:g, 5dlu"));
				cp.add(updateNcbiTaxDlgPnl, CC.xy(2,  2));		
				
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
	 * Method to update the UniProt taxonomy
	 */
	private void updateNcbiTax() {
		
		/**
		 * The names file
		 */
		File namesFile = null;
		
		/**
		 * The nodes file
		 */
		File nodesFile = null;
		
		// Get file of the names.dmp file
		JFileChooser namesChooser = new JFileChooser();
		namesChooser.setFileFilter(Constants.NCBINAMES_FILE_FILTER);
		int returnVal = namesChooser.showOpenDialog(this.owner);
		namesChooser.setAcceptAllFileFilterUsed(false);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			namesFile = namesChooser.getSelectedFile();
		}
		// Get file of the nodes.dmp file
		JFileChooser nodesChooser = new JFileChooser();
		nodesChooser.setFileFilter(Constants.NCBINODES_FILE_FILTER);
		int returnVal2 = nodesChooser.showOpenDialog(this.owner);
		nodesChooser.setAcceptAllFileFilterUsed(false);
		if (returnVal2 == JFileChooser.APPROVE_OPTION) {
			nodesFile = nodesChooser.getSelectedFile();
		}
		// Read NCBI taxonomy and overwrite them into the mysql database
		try {
			NcbiTaxonomy.getInstance(namesFile.getAbsolutePath(), nodesFile.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
