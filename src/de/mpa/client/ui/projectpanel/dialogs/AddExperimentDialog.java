package de.mpa.client.ui.projectpanel.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import de.mpa.client.ui.sharedelements.icons.IconConstants;
import de.mpa.model.MPAExperiment;

@SuppressWarnings("serial")
public class AddExperimentDialog extends GeneralDialog {

	/**
	 * Creates a Add-Project dialog using the specified
	 * dialog type and content object.
	 * @param type the type of dialog
	 * @param project the content object
	 */
	public AddExperimentDialog(MPAExperiment experiment) {
//		super(experiment.getTitle(), IconConstants.ADD_PAGE_ICON.getImage(), experiment.getTitle(), experiment.getProperties());
		super("New Experiment", IconConstants.ADD_PAGE_ICON.getImage(), "New Experiment", experiment.getProperties());
		saveBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				// store experiment
				try {
					experiment.persist(getContentName(), getProperties(), experiment.getProject());
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
