package com.jakeapp.gui.swing.actions.project;

import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.ProjectAction;
import com.jakeapp.gui.swing.globals.JakeContext;
import com.jakeapp.gui.swing.helpers.FileUtilities;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Join a project
 */
public class JoinProjectAction extends ProjectAction {
	private static final Logger log = Logger.getLogger(JoinProjectAction.class);
	private String projectLocation;

	public JoinProjectAction() {
		super();

		putValue(Action.NAME, JakeMainView.getMainView().getResourceMap().
						getString("joinProjectMenuItem"));
	}


	public void actionPerformed(ActionEvent actionEvent) {
		log.info("Joining Project: " + getProject());

		// fixme: add sanity check! (no C:\Windows e.g.) OR if much to many files...

		JakeMainApp.getCore()
						.joinProject(getProjectLocation(), JakeContext.getInvitation());

		// clear current invite
		JakeContext.setInvitation(null);
	}


	@Override
	public void updateAction() {
		setProjectLocation(
						FileUtilities.getDefaultProjectLocation(JakeContext.getInvitation()));
	}

	public String getProjectLocation() {
		return projectLocation;
	}

	public void setProjectLocation(String projectLocation) {
		this.projectLocation = projectLocation;
	}
}