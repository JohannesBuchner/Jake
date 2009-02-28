package com.jakeapp.gui.swing.worker;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.callbacks.ProjectChanged;
import com.jakeapp.gui.swing.xcore.EventCore;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;

public class StartStopProjectWorker
				extends SwingWorkerWithAvailableLaterObject<Void> {
	private Project project;
	private boolean start;

	public StartStopProjectWorker(Project project, boolean start) {
		this.project = project;
		this.start = start;
	}

	@Override
	protected AvailableLaterObject<Void> calculateFunction() {

		// generate event
		EventCore.get().fireProjectChanged(
						new ProjectChanged.ProjectChangedEvent(project,
										ProjectChanged.ProjectChangedEvent.Reason.StartStopStateChanging));

		if (start) {
			return JakeMainApp.getCore().startProject(project);
		} else {
			// FIXME: stop
			return null;
		}
	}

	@Override
	protected void done() {
		// generate event
		EventCore.get().fireProjectChanged(
						new ProjectChanged.ProjectChangedEvent(project,
										ProjectChanged.ProjectChangedEvent.Reason.StartStopStateChanged));
	}

	@Override
	public void error(Exception e) {
		ExceptionUtilities.showError(e);
	}
}