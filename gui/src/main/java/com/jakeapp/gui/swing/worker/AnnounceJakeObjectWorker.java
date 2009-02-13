package com.jakeapp.gui.swing.worker;

import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.core.util.availablelater.AvailableErrorObject;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.synchronization.exceptions.SyncException;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;

public class AnnounceJakeObjectWorker extends SwingWorkerWithAvailableLaterObject<Void> {
	private JakeObject jo;
	private String commitMessage;

	public AnnounceJakeObjectWorker(JakeObject jo, String commitMessage) {
		this.jo = jo;
		this.commitMessage = commitMessage;
	}

	@Override
	protected AvailableLaterObject<Void> calculateFunction() {
		try {
			return JakeMainApp.getCore().announceJakeObject(jo, commitMessage);
		} catch (SyncException e) {
			return new AvailableErrorObject<Void>(e);
		}
	}

	@Override
	public void error(Exception e) {
		ExceptionUtilities.showError(e);
	}
}
