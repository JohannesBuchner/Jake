package com.jakeapp.gui.swing.worker;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.util.availablelater.AvailableErrorObject;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.callbacks.DataChanged;
import com.jakeapp.gui.swing.exceptions.FileOperationFailedException;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;
import com.jakeapp.gui.swing.xcore.EventCore;

import java.util.EnumSet;

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
		} catch (FileOperationFailedException e) {
			return new AvailableErrorObject<Void>(e);
		}
	}


	@Override
	protected void done() {
		// inform the core that there are new log entries available.
		EventCore.get().fireDataChanged(EnumSet.of(DataChanged.Reason.LogEntries));
	}


	@Override
	public void error(Exception e) {
		ExceptionUtilities.showError(e);
	}
}