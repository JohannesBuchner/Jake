package com.jakeapp.gui.swing.worker;

import com.jakeapp.core.domain.InvitationState;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.util.availablelater.AvailableErrorObject;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;
import com.jakeapp.gui.swing.xcore.ObjectCache;

import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class GetProjectsTask extends AbstractTask<List<Project>> {
	private EnumSet<InvitationState> filter;

	public GetProjectsTask(EnumSet<InvitationState> filter) {
		this.filter = filter;
	}

	@Override
	protected AvailableLaterObject<List<Project>> calculateFunction() {

		if (JakeMainApp.isCoreInitialized()) {
			return JakeMainApp.getCore().getProjects(filter);
		} else {
			// return an error, but fail silently (core just needs more time for init)
			return new AvailableErrorObject<List<Project>>(null);
		}
	}

	@Override
	protected void done() {
		super.done();

		try {
			if (filter.contains(InvitationState.ACCEPTED)) {
				ObjectCache.get().setMyProjects(get());
			} else if (filter.contains(InvitationState.INVITED)) {
				ObjectCache.get().setInvitedProjects(get());
			}
		} catch (InterruptedException e) {
			ExceptionUtilities.showError(e);
		} catch (ExecutionException e) {
			ExceptionUtilities.showError(e);
		}
	}


	@Override
	public void error(Exception e) {
		ExceptionUtilities.showError(e);
	}
}