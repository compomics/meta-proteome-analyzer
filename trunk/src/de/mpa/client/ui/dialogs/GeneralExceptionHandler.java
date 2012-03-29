package de.mpa.client.ui.dialogs;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.mpa.client.ui.ScreenConfig;

public class GeneralExceptionHandler {

	private static boolean detailsOpen;
	
	private static final String HTML_HEADER = "<html><p>";
	private static final String HTML_FOOTER = "</p></html>";
	/**
	 * This method shows an error with the database connection.
	 * 
	 * @param sqe The SQL exception that was thrown. 
	 * @param window The parent window, could be dialog or frame.
	 */
	public static void showSQLErrorDialog(SQLException sqe, Window window) {

		// Create dialog
		final JDialog dialog = new JDialog(window, "Error", Dialog.ModalityType.APPLICATION_MODAL);
		dialog.setPreferredSize(new Dimension(360, 150));
		dialog.setResizable(false);

		// Create message panel
		JPanel errorPnl = new JPanel();
		CellConstraints cc = new CellConstraints();
		errorPnl.setLayout(new FormLayout("5dlu, p:g, p, 5dlu, p, 5dlu",
				"5dlu, p, 5dlu, p, 5dlu, f:p:g, 5dlu"));
		
		String message = HTML_HEADER + sqe.getMessage() + HTML_FOOTER;
		
		JLabel errorMessageLbl = new JLabel(
				message,
				new ImageIcon(ClassLoader.getSystemResource("de/mpa/resources/icons/error.png")),SwingConstants.LEFT);
		errorMessageLbl.setPreferredSize(new Dimension(errorMessageLbl.getPreferredSize().width,
				48));
		JButton okBtn = new JButton("Ok");
		// Close message
		okBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dialog.dispose();
			}
		});

		JButton detailsBtn = new JButton("Details >");
		okBtn.setPreferredSize(detailsBtn.getPreferredSize());
		String stackTrace = "";
		for (StackTraceElement ste : sqe.getStackTrace()) {
			stackTrace += ste + "\n";
		}
		JTextArea detailsTxA = new JTextArea(stackTrace);
		detailsTxA.setEditable(false);
		final JScrollPane detailsScp = new JScrollPane(detailsTxA);
		detailsScp.setPreferredSize(new Dimension(100, 100));
		detailsScp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		detailsScp.setVisible(false);
		detailsOpen = false;

		detailsBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				detailsOpen = !detailsOpen;
				detailsScp.setVisible(detailsOpen);
				dialog.setSize(360, 120 + ((detailsOpen) ? 180 : 0));
			}
		});

		// Build Panel
		errorPnl.add(errorMessageLbl, cc.xyw(2, 2, 4));
		errorPnl.add(okBtn, cc.xy(3, 4));
		errorPnl.add(detailsBtn, cc.xy(5, 4));
		errorPnl.add(detailsScp, cc.xyw(2, 6, 4));
		// Build dialog
		dialog.add(errorPnl);

		dialog.pack();
		ScreenConfig.centerInScreen(dialog);
		dialog.setVisible(true);

	}

	/**
	 * This method pops up to show an error
	 * 
	 * @param exception
	 * @param object
	 */
	public static void showErrorDialog(Exception e, Window window) {

		// Create dialog
		final JDialog dialog = new JDialog(window, "Error", Dialog.ModalityType.APPLICATION_MODAL);
		dialog.setPreferredSize(new Dimension(360, 120));
		dialog.setResizable(false);

		// Create message panel
		JPanel errorPnl = new JPanel();
		CellConstraints cc = new CellConstraints();
		errorPnl.setLayout(new FormLayout("5dlu, p:g, p, 5dlu, p, 5dlu",
				"5dlu, p, 5dlu, p, 5dlu, f:p:g, 5dlu"));
		
		String message = "<html><p>" + e.getMessage() + "</p></html>";
		
		JLabel errorMessageLbl = new JLabel(
				message,
				new ImageIcon(ClassLoader.getSystemResource("de/mpa/resources/icons/error.png")),
				SwingConstants.LEFT);
		errorMessageLbl.setPreferredSize(new Dimension(errorMessageLbl.getPreferredSize().width,
				48));
		JButton okBtn = new JButton("Ok");
		// Close message
		okBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dialog.dispose();
			}
		});

		JButton detailsBtn = new JButton("Details >");
		okBtn.setPreferredSize(detailsBtn.getPreferredSize());
		String stackTrace = "";
		for (StackTraceElement ste : e.getStackTrace()) {
			stackTrace += ste + "\n";
		}
		JTextArea detailsTxA = new JTextArea(stackTrace);
		detailsTxA.setEditable(false);
		final JScrollPane detailsScp = new JScrollPane(detailsTxA);
		detailsScp.setPreferredSize(new Dimension(100, 100));
		detailsScp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		detailsScp.setVisible(false);
		detailsOpen = false;

		detailsBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				detailsOpen = !detailsOpen;
				detailsScp.setVisible(detailsOpen);
				dialog.setSize(360, 120 + ((detailsOpen) ? 180 : 0));
			}
		});

		// Build Panel
		errorPnl.add(errorMessageLbl, cc.xyw(2, 2, 4));
		errorPnl.add(okBtn, cc.xy(3, 4));
		errorPnl.add(detailsBtn, cc.xy(5, 4));
		errorPnl.add(detailsScp, cc.xyw(2, 6, 4));
		// Build dialog
		dialog.add(errorPnl);

		dialog.pack();
		ScreenConfig.centerInScreen(dialog);
		dialog.setVisible(true);

	}
}