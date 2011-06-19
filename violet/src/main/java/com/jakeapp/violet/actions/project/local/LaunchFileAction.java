package com.jakeapp.violet.actions.project.local;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.violet.model.JakeObject;
import com.jakeapp.violet.model.ProjectModel;

/**
 * <code>AvailableLaterObject</code> launching/opening a file.
 */
public class LaunchFileAction extends AvailableLaterObject<Void> {

	private JakeObject file;

	private ProjectModel model;

	public LaunchFileAction(ProjectModel model, JakeObject file) {
		this.model = model;
		this.file = file;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Void calculate() throws Exception {
		model.getFss().launchFile(file.getRelPath());
		return null;
	}
}
