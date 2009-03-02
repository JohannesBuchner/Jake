package com.jakeapp.gui.swing.worker;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.callbacks.ProjectChanged;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;
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
						.fireProjectChanged(new ProjectChanged.ProjectChangedEvent(project,
										ProjectChanged.ProjectChangedEvent.Reason.StartStopStateChanging));

		return JakeMainApp.getCore().startStopProject(project, start);
	}

	@Override
	protected void done() {
		super.done();
		// generate event
		EventCore.get()
						.fireProjectChanged(new ProjectChanged.ProjectChangedEvent(project,
										ProjectChanged.ProjectChangedEvent.Reason.StartStopStateChanged));
	}

	@Override
	public void error(Exception e) {
		ExceptionUtilities.showError(e);
	}
}