package com.jakeapp.gui.swing.worker;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.util.availablelater.AvailableErrorObject;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.callbacks.DataChanged;
import com.jakeapp.gui.swing.exceptions.FileOperationFailedException;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;
import com.jakeapp.gui.swing.xcore.EventCore;

import java.util.EnumSet;
import java.util.List;

public class AnnounceJakeObjectWorker extends SwingWorkerWithAvailableLaterObject<Void> {
	private List<JakeObject> jos;
	private String commitMessage;

	public AnnounceJakeObjectWorker(List<JakeObject> jos, String commitMessage) {
		this.jos = jos;
		this.commitMessage = (commitMessage==null)?"":commitMessage;
	}

	@Override
	protected AvailableLaterObject<Void> calculateFunction() {
		try {
			return JakeMainApp.getCore().announceJakeObjects(jos, commitMessage);
		} catch (FileOperationFailedException e) {
			return new AvailableErrorObject<Void>(e);
		}
	}


	@Override
	protected void done() {
		// inform the core that there are new log entries available.
		EventCore.get().fireDataChanged(EnumSet.of(DataChanged.Reason.Files), null);
		if (this.jos.size()>0) {
			if ((this.jos.get(0)) instanceof FileObject)
				EventCore.get().fireFilesChanged(this.jos.get(0).getProject());
			else
				EventCore.get().fireNotesChanged(this.jos.get(0).getProject());
		}
	}


	@Override
	public void error(Exception e) {
		ExceptionUtilities.showError(e);
	}
}