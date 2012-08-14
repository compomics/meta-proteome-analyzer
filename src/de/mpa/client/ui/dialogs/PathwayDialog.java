package de.mpa.client.ui.dialogs;

import java.awt.Container;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.ui.ClientFrame;

/**
 * Thi class initialize dialog for KEGG database
 * @author R. Heyer
 *
 */

public class PathwayDialog {

	private PathwayType type;

	/**
	 * Constructor for different pathway types
	 * @param type The type of pathway database
	 * @param clientFrame The parent clientFrame
	 */
	public PathwayDialog(PathwayType type, ClientFrame clientFrame) {
		switch (type) {
		case KEGG:
			initKeggDialog(clientFrame);
			break;
		default:
			break;
		}
	}

	/**
	 * Methode to create dialog to Kegg Database
	 */
	private void initKeggDialog( ClientFrame clientFrame) {
		JPanel pathwayPnl = new JPanel();
		pathwayPnl.setBorder(BorderFactory.createTitledBorder("Choice Pathway"));
		String []	pathways		= {"map00981",  "map00010"};
		JComboBox pathwaysCbx = new JComboBox(pathways);	
		pathwayPnl.add(pathwaysCbx);
		// Description panel
		int dialogRes = JOptionPane.showConfirmDialog(clientFrame, pathwayPnl, "KEGG Database",  JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		// cancel == 2 & ok == 0
		if (dialogRes == 0) {
			// Create Webpage with Kegg database
			try {
				String map				= pathwaysCbx.getSelectedItem().toString();
				String ecString			= "ec%3A1.1.3.16%0D%0Aec%3A3.1.1.59";
				String htmlFilePath 	= "http://www.kegg.jp/kegg-bin/show_pathway?scale=1.0&query=&map=" + map+ "&scale=1.0&show_description=show&multi_query=" + ecString;	
				URI uri 				= URI.create(htmlFilePath);
				Desktop.getDesktop().browse(uri);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
}
