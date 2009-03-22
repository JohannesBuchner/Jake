package com.jakeapp.gui.swing.worker.tasks;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.gui.swing.actions.file.OpenFileAction;

import java.util.Arrays;
import java.util.List;

public class PullAndLaunchJakeObjectsTask extends PullJakeObjectsTask {
	public PullAndLaunchJakeObjectsTask(List<JakeObject> jakeObjects) {
		super(jakeObjects);
	}

	public PullAndLaunchJakeObjectsTask(FileObject fo) {
		super(Arrays.asList((JakeObject)fo));
	}


	@Override
	protected void onDone() {
		super.onDone();

		// launch it!
		for (JakeObject jo : jakeObjects) {
			OpenFileAction.launchFileDontTryPull((FileObject) jo);
		}
	}
}