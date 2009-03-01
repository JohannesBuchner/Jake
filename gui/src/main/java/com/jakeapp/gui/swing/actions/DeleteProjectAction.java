package com.jakeapp.gui.swing.actions;

import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.ProjectAction;
import com.jakeapp.gui.swing.dialogs.DeleteProjectDialog;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * User: studpete
 * Date: Dec 29, 2008
 * Time: 12:20:54 AM
 */
public class DeleteProjectAction extends ProjectAction {
	private static final Logger log = Logger.getLogger(DeleteProjectAction.class);

	public DeleteProjectAction() {
		super();

		putValue(Action.NAME, JakeMainView.getMainView().getResourceMap().
				  getString("deleteProjectMenuItem.text"));
		updateAction();
	}


	public void actionPerformed(ActionEvent actionEvent) {
		log.info("Delete Project: " + getProject());

		DeleteProjectDialog.showDialog(getProject());
	}


	@Override
	public void updateAction() {
		this.setEnabled(getProject() != null);
	}
}