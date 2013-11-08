package de.mpa.client.ui.dialogs;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.ResultSettings;
import de.mpa.client.ResultSettings.MetaproteinTyp;
import de.mpa.client.ui.ClientFrame;
import de.mpa.client.ui.ScreenConfig;
import de.mpa.client.ui.icons.IconConstants;

public class ResultSettingsDlg extends JDialog {

	private ClientFrame owner;
	
	private ResultSettings resultSettings;

	public ResultSettingsDlg(ClientFrame owner, String title, boolean modal, ResultSettings resultSettings) {
		super(owner, title, modal);
		this.owner = owner;
		this.resultSettings = resultSettings;
		initComponents();
		showDialog();
	}

	public void initComponents() {
		JPanel resultSettingsPnl = new JPanel();
		resultSettingsPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu", "5dlu, f:p:g, 5dlu, p, 5dlu"));
		
		// Panel for metaprotein settings
		JPanel metaproteinPnl = new JPanel();
		metaproteinPnl.setBorder(BorderFactory.createTitledBorder("Metaproteins"));
		metaproteinPnl.setLayout(new FormLayout("5dlu, p:g, 5dlu, f:p:g, 5dlu", "5dlu, p:g, 5dlu, p:g, 5dlu, p:g, 5dlu"));
		final JCheckBox leucinOrIsoleucinCbx = new JCheckBox("Distingush between L/I", resultSettings.leuDistIso);
		leucinOrIsoleucinCbx.setToolTipText("Is the Ms resolution enough to distinguish between leucin and isoleucin");
		final JCheckBox useAlignCbx = new JCheckBox("Use Alignment of Metaproteins", resultSettings.alignment);
		useAlignCbx.setToolTipText("Perform alignment between metaproteins (regard max number of XX proteins for alignment)"); // TODO define number of proteins
		JLabel createMetaProtsLbl = new JLabel("Create Metaproteins: ");
		final JComboBox metaProteinsCox = new JComboBox(new DefaultComboBoxModel(MetaproteinTyp.values()));
		metaProteinsCox.setSelectedItem(resultSettings.metaproteinTyp);
		
		// Add to metaprotein panel
		metaproteinPnl.add(leucinOrIsoleucinCbx, CC.xy(2, 2));
		metaproteinPnl.add(useAlignCbx, CC.xy(2, 4));
		metaproteinPnl.add(createMetaProtsLbl, CC.xy(2, 6));
		metaproteinPnl.add(metaProteinsCox, CC.xy(4, 6));
		
		// Create button panel
		JPanel buttonPnl = new JPanel();
		FormLayout formLayout = new FormLayout("0px:g, p, 5dlu, p, 1px" , "b:p");
		formLayout.setColumnGroups(new int[][] { { 2, 4 } });
		buttonPnl.setLayout(formLayout);
		// Configure 'OK' button
		JButton okBtn = new JButton("OK", IconConstants.CHECK_ICON);
		okBtn.setRolloverIcon(IconConstants.CHECK_ROLLOVER_ICON);
		okBtn.setPressedIcon(IconConstants.CHECK_PRESSED_ICON);
		okBtn.setHorizontalAlignment(SwingConstants.LEFT);
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				// Save settings
				resultSettings.leuDistIso = leucinOrIsoleucinCbx.isSelected();
				resultSettings.alignment = useAlignCbx.isSelected();
				resultSettings.metaproteinTyp = (MetaproteinTyp) metaProteinsCox.getSelectedItem();
				
				// Close the dialog 
				closeDialog();
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
				closeDialog();
			}
		});
		// Add buttons
		buttonPnl.add(okBtn, CC.xy(2, 1));
		buttonPnl.add(cancelBtn, CC.xy(4, 1));
		
		// Add to the result panel
		resultSettingsPnl.add(metaproteinPnl, CC.xy(2, 2));
		resultSettingsPnl.add(buttonPnl, CC.xy(2, 4));
		
		// Add panels to the dialog content pane
		Container contentPane = this.getContentPane();
		contentPane.setPreferredSize(new Dimension(400, 200));
		contentPane.add(resultSettingsPnl);
		
	}


	/**
	 * shows the dialog
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
	 * Method to close the dialog
	 */
	private void closeDialog() {
		// Save the export headers
		dispose();
	}

}
