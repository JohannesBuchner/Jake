package com.jakeapp.gui.swing.worker.tasks;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.util.availablelater.AvailableErrorObject;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.callbacks.DataChangedCallback;
import com.jakeapp.gui.swing.exceptions.FileOperationFailedException;
import com.jakeapp.gui.swing.xcore.EventCore;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;


public class PullJakeObjectsTask extends AbstractTask<Void> {
	protected List<JakeObject> jakeObjects;

	public PullJakeObjectsTask(List<JakeObject> jakeObjects) {
		this.jakeObjects = jakeObjects;
	}

	public PullJakeObjectsTask(FileObject fo) {
		this(Arrays.asList((JakeObject)fo));
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
	protected void onDone() {
		// inform the core that there are new log entries available.
		EventCore.get()
						.fireDataChanged(EnumSet.of(DataChangedCallback.DataReason.Files), null);
		if (this.jakeObjects.size() > 0) {
			if ((this.jakeObjects.get(0)) instanceof FileObject)
				EventCore.get().fireFilesChanged(this.jakeObjects.get(0).getProject());
			else
				EventCore.get().fireNotesChanged(this.jakeObjects.get(0).getProject());
		}
	}

	public List<JakeObject> getJakeObjects() {
		return jakeObjects;
	}
}