package com.jakeapp.gui.swing.worker.tasks;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.callbacks.ProjectChangedCallback;
import com.jakeapp.gui.swing.xcore.EventCore;

public class StartStopProjectTask extends AbstractTask<Void> {
	private Project project;
	private boolean start;

	public StartStopProjectTask(Project project, boolean start) {
		this.project = project;
		this.start = start;
	}

	@Override
	protected AvailableLaterObject<Void> calculateFunction() {

		// generate event
		EventCore.get()
						.fireProjectChanged(new ProjectChangedCallback.ProjectChangedEvent(project,
										ProjectChangedCallback.ProjectChangedEvent.Reason.StartStopStateChanging));

		return JakeMainApp.getCore().startStopProject(project, start);
	}

	@Override
	protected void onDone() {
		// generate event
		EventCore.get()
						.fireProjectChanged(new ProjectChangedCallback.ProjectChangedEvent(project,
										ProjectChangedCallback.ProjectChangedEvent.Reason.StartStopStateChanged));
	}
}