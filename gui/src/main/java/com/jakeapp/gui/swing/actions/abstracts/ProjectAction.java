package com.jakeapp.gui.swing.actions.abstracts;

import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.globals.JakeContext;
import com.jakeapp.gui.swing.callbacks.ContextChangedCallback;
import com.jakeapp.gui.swing.callbacks.ProjectChangedCallback;
import com.jakeapp.gui.swing.xcore.EventCore;

import java.util.EnumSet;

/**
 * ProjectAction is an abstract action class for all project
 * related actions.
 * Implements the changed and selection interface.
 */
public abstract class ProjectAction extends JakeAction
		  implements ProjectChangedCallback, ContextChangedCallback {
	//private static final Logger log = Logger.getLogger(ProjectAction.class);

	private Project project;

	public ProjectAction() {
		EventCore.get().addProjectChangedCallbackListener(this);
		EventCore.get().addContextChangedListener(this);
	}

	public Project getProject() {
		return JakeContext.getProject();
	}

	public void updateAction() {
		setEnabled(JakeContext.getMsgService() != null);
	}

	public void projectChanged(final ProjectChangedEvent ev) {
		updateAction();
	}

	public void contextChanged(EnumSet<Reason> reason, Object context) {
		updateAction();
	}
}
