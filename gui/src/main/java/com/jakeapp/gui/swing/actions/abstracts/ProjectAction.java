package com.jakeapp.gui.swing.actions.abstracts;

import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.callbacks.ProjectChanged;
import com.jakeapp.gui.swing.callbacks.ProjectSelectionChanged;
import com.jakeapp.gui.swing.callbacks.PropertyChanged;
import com.jakeapp.gui.swing.xcore.EventCore;

import java.util.EnumSet;

/**
 * ProjectAction is an abstract action class for all project
 * related actions.
 * Implements the changed and selection interface.
 */
public abstract class ProjectAction extends JakeAction
		  implements ProjectSelectionChanged, ProjectChanged, PropertyChanged {
	//private static final Logger log = Logger.getLogger(ProjectAction.class);

	private Project project;

	public ProjectAction() {
		JakeMainApp.getApp().addProjectSelectionChangedListener(this);
		EventCore.get().addProjectChangedCallbackListener(this);
		EventCore.get().addPropertyListener(this);

		// initial load
		setProject(JakeMainApp.getProject());
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;

		updateAction();
	}

	public void updateAction() {
	}

	public void projectChanged(final ProjectChangedEvent ev) {
		updateAction();
	}

	public void propertyChanged(EnumSet<Reason> reason, Project p, Object data) {
		updateAction();
	}
}
