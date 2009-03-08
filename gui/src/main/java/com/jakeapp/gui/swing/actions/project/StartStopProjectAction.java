package com.jakeapp.gui.swing.actions.project;

import com.jakeapp.gui.swing.actions.abstracts.ProjectAction;
import com.jakeapp.gui.swing.worker.JakeExecutor;
import com.jakeapp.gui.swing.helpers.ProjectHelper;
import com.jakeapp.gui.swing.worker.StartStopProjectTask;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Starts or stops a project
 */
public class StartStopProjectAction extends ProjectAction {
	private static final Logger log = Logger.getLogger(StartStopProjectAction.class);

	public StartStopProjectAction() {
		putValue(Action.NAME, ProjectHelper.getProjectStartStopString(getProject()));

		updateAction();
	}

	public void actionPerformed(ActionEvent actionEvent) {
		log.trace("Start/Stop Project: " + getProject());

		// do nothing if we don't have a project
		if (getProject() == null) {
			log.warn("Attemted to Start/Stop without a project");
			return;
		}

		JakeExecutor.exec(new StartStopProjectTask(getProject(), !getProject().isStarted()));
	}


	@Override
	public void updateAction() {
		log.trace("update startstopprojectaction with " + getProject());
		String oldName = (String) getValue(Action.NAME);
		String newName = ProjectHelper.getProjectStartStopString(getProject());
		setEnabled(getProject() != null);

		putValue(Action.NAME, newName);
		firePropertyChange(Action.NAME, oldName, newName);
	}
}