package com.jakeapp.gui.swing.worker;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.util.availablelater.AvailableErrorObject;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.callbacks.DataChanged;
import com.jakeapp.gui.swing.exceptions.FileOperationFailedException;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;
import com.jakeapp.gui.swing.xcore.EventCore;

import java.util.EnumSet;
import java.util.List;


public class PullJakeObjectsTask extends AbstractTask<Void> {

	private List<JakeObject> jakeObjects;

	public PullJakeObjectsTask(List<JakeObject> jakeObjects) {
		this.jakeObjects = jakeObjects;
	}

	@Override
	protected AvailableLaterObject<Void> calculateFunction() throws RuntimeException {
		try {
			return JakeMainApp.getCore().pullJakeObjects(this.jakeObjects);
		} catch (FileOperationFailedException e) {
			return new AvailableErrorObject<Void>(e);
		}
	}

	@Override
	protected void done() {
		super.done();
		
		// inform the core that there are new log entries available.
		EventCore.get().fireDataChanged(EnumSet.of(DataChanged.Reason.Files), null);
		if (this.jakeObjects.size() > 0) {
			if ((this.jakeObjects.get(0)) instanceof FileObject)
				EventCore.get().fireFilesChanged(this.jakeObjects.get(0).getProject());
			else
				EventCore.get().fireNotesChanged(this.jakeObjects.get(0).getProject());
		}
	}


	@Override
	public void error(Exception e) {
		ExceptionUtilities.showError(e);
	}
}