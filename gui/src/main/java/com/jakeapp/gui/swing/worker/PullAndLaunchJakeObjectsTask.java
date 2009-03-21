package com.jakeapp.gui.swing.worker;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.gui.swing.actions.file.OpenFileAction;

import java.util.List;

public class PullAndLaunchJakeObjectsTask extends PullJakeObjectsTask {
	public PullAndLaunchJakeObjectsTask(List<JakeObject> jakeObjects) {
		super(jakeObjects);
	}


	@Override
	protected void onDone() {
		super.onDone();

		// launch it!
		for (JakeObject jo : jakeObjects) {
			OpenFileAction.launchFile((FileObject) jo);
		}
	}
}