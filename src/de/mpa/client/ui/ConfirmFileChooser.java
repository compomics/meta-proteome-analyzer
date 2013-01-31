package de.mpa.client.ui;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * File chooser which asks for confirmation when selecting an existing file in
 * the save dialog.
 * 
 * @author A. Behne
 */
public class ConfirmFileChooser extends JFileChooser {

	/**
	 * Constructs a <code>ConfirmFileChooser</code> pointing to the user's
	 * default directory.
	 */
	public ConfirmFileChooser() {
		super();
	}

	/**
	 * Constructs a <code>ConfirmFileChooser</code> using the given path.
	 * Passing in a null string causes the file chooser to point to the user's
	 * default directory.
	 * @param currentDirectoryPath
	 *            a <code>String</code> giving the path to a file or directory
	 */
	public ConfirmFileChooser(String currentDirectoryPath) {
		super(currentDirectoryPath);
	}

	@Override
	public void approveSelection() {
		if (getDialogType() == SAVE_DIALOG) {
			File selFile = getSelectedFile();
			// check whether selected file descriptor points to an existing file
			if (selFile.exists()) {
				// ask for confirmation to overwrite
				int result = JOptionPane.showConfirmDialog(this, "A file with the selected name already exists. Do you want to replace it?", "Confirm overwrite", JOptionPane.YES_NO_CANCEL_OPTION);
				// check whether approve option has been chosen
				switch (result) {
				case 0:
					break;
				case 1:
					return;
				case 2:
					super.cancelSelection();
					return;
				}
			}
		}
		super.approveSelection();
	}
	
}
