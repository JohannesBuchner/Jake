package com.jakeapp.gui.swing.actions;

import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.actions.abstracts.ProjectAction;
import com.jakeapp.gui.swing.helpers.ProjectHelper;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * User: studpete
 * Date: Dec 29, 2008
 * Time: 12:20:54 AM
 */
public class StartStopProjectAction extends ProjectAction {
	private static final Logger log = Logger.getLogger(StartStopProjectAction.class);

	public StartStopProjectAction() {
		putValue(Action.NAME, ProjectHelper.getProjectStartStopString(getProject()));

		updateAction();
	}

	public void actionPerformed(ActionEvent actionEvent) {
		//log.info("Start/Stop Project: " + getProject());

		// do nothing if we don't have a project
		if (getProject() == null) {
			return;
		}

		if (!getProject().isStarted()) {
			// TODO: exception handling

			JakeMainApp.getCore().startProject(getProject());
		} else {
			JakeMainApp.getCore().stopProject(getProject());
		}
	}


	@Override
	public void updateAction() {
		//log.debug("update startstopprojectaction with " + getProject());
		String oldName = (String) getValue(Action.NAME);
		String newName = ProjectHelper.getProjectStartStopString(getProject());
		setEnabled(getProject() != null);

		//log.debug("old: " + oldName + " new: " + newName);

		putValue(Action.NAME, newName);
		firePropertyChange(Action.NAME, oldName, newName);
	}
}