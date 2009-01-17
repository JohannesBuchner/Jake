package com.jakeapp.gui.swing.dialogs;

import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.dialogs.generic.JakeDialog;
import com.jakeapp.gui.swing.helpers.Translator;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author: studpete
 */
public class DeleteProjectDialog extends JakeDialog {
	private static final Logger log = Logger.getLogger(InvitePeopleDialog.class);
	private JCheckBox deleteFilesCheckBox;

	public DeleteProjectDialog(Project project) {
		super(project);

		setResourceMap(org.jdesktop.application.Application.getInstance(
				  com.jakeapp.gui.swing.JakeMainApp.class).getContext()
				  .getResourceMap(DeleteProjectDialog.class));

		initDialog();

		// set custom properties
		setDialogTitle(Translator.get(getResourceMap(), "deleteTitle", getProject().getName()));

		setMessage("deleteHeader");
		// use default picture
	}

	@Override
	protected JButton initComponents() {

		deleteFilesCheckBox = new JCheckBox(getResourceMap().getString("deleteFileCheckbox"));
		this.add(deleteFilesCheckBox, "bottom, growx");

		// create buttons
		this.addCancelBtn();
		JButton deleteBtn = new JButton(getResourceMap().getString("deleteButton"));
		deleteBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				deleteProjectAction();
			}
		});
		return deleteBtn;
	}

	private void deleteProjectAction() {
		// TODO: make async
		JakeMainApp.getCore().deleteProject(getProject(), deleteFilesCheckBox.isSelected());
		closeDialog();
	}

	/**
	 * Shows the Dialog. Static, configures modality and size, shows dialog.
	 *
	 * @param project: project where people will be added.
	 */
	public static void showDialog(Project project) {
		DeleteProjectDialog dlg = new DeleteProjectDialog(project);
		dlg.showDialogSized(400, 210);
	}
}
