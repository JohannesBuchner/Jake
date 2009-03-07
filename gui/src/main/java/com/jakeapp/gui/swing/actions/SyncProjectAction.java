package com.jakeapp.gui.swing.actions;

import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.actions.abstracts.ProjectAction;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Syncronize Project Action. Starts a manual sync for log entries.
 */
public class SyncProjectAction extends ProjectAction {
	private static final Logger log = Logger.getLogger(SyncProjectAction.class);

	public SyncProjectAction() {
		putValue(Action.NAME, JakeMainView.getMainView().getResourceMap().getString("projectTreeSyncProject"));

		updateAction();
	}

	public void actionPerformed(ActionEvent actionEvent) {
		log.info("Syncronize Project: " + getProject());

		// do nothing if we don't have a project
		if (getProject() == null) {
			log.warn("syncronize called without project");
			return;
		}

		if (!getProject().isStarted()) {
			log.warn("syncronize cannot be startet without project started.");
			return;
		}

		try {
			JakeMainApp.getCore().syncProject(getProject(), null);
		} catch (Exception e) {
			ExceptionUtilities.showError(e);
		}
	}


	@Override
	public void updateAction() {
		setEnabled(getProject() != null && getProject().isStarted());

		// TODO: change name to syncronizing while in process, and disable while working!
		// LABEL = projectTreeSyncActiveProject

/*
		log.debug("update startstopprojectaction with " + getProject());
		String oldName = (String) getValue(Action.NAME);
		String newName = ProjectHelper.getProjectStartStopString(getProject());
		setEnabled(getProject() != null);

		log.debug("old: " + oldName + " new: " + newName);

		putValue(Action.NAME, newName);
		firePropertyChange(Action.NAME, oldName, newName);
		*/
	}
}