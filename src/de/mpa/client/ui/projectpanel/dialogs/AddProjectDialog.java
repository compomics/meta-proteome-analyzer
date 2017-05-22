package de.mpa.client.ui.projectpanel.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import de.mpa.client.ui.sharedelements.icons.IconConstants;
import de.mpa.model.MPAProject;

@SuppressWarnings("serial")
public class AddProjectDialog extends GeneralDialog {

	/**
	 * Creates a Add-Project dialog using the specified
	 * dialog type and content object.
	 * @param type the type of dialog
	 * @param project the content object
	 */
	public AddProjectDialog(MPAProject project) {
		super(project.getTitle(), IconConstants.ADD_FOLDER_ICON.getImage(), project.getTitle(), project.getProperties());
		saveBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
//				// store project
				try {
					project.persist(getContentName(), getProperties());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				result = RESULT_SAVED;
				dispose();
			}
		});
	}
	
}
