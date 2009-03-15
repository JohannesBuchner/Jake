package com.jakeapp.gui.swing.worker;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.util.availablelater.AvailableErrorObject;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.globals.JakeContext;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;
import com.jakeapp.gui.swing.listener.FileWatcherListenerMultiton;
import com.jakeapp.gui.swing.models.FileTableModel;
import com.jakeapp.gui.swing.xcore.ObjectCache;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class GetMyProjectsTask extends AbstractTask<List<Project>> {

	@Override
	protected AvailableLaterObject<List<Project>> calculateFunction() {

		if (JakeContext.isCoreInitialized()) {
			return JakeMainApp.getCore().getProjects();
		} else {
			// return an error, but fail silently (core just needs more time for init)
			return new AvailableErrorObject<List<Project>>(null);
		}
	}

	@Override
	protected void onDone() {
		try {
			for(Project p : get()) {
				JakeMainApp.getCore().registerFileWatcher(p,
						FileWatcherListenerMultiton.get(p));
			}
			ObjectCache.get().setMyProjects(get());
		} catch (InterruptedException e) {
			ExceptionUtilities.showError(e);
		} catch (ExecutionException e) {
			ExceptionUtilities.showError(e);
		}
	}
}