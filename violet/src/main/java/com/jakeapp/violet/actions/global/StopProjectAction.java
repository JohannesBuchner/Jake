package com.jakeapp.violet.actions.global;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.violet.model.ProjectModel;

/**
 * <code>AvailableLaterObject</code> which is responsible for starting or
 * stoping a given <code>Project</code>.
 */
public class StopProjectAction extends AvailableLaterObject<Void> {

	private ProjectModel model;

	public StopProjectAction(ProjectModel model) {
		this.model = model;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Void calculate() throws Exception {
		model.getFss().unsetRootPath();
		model.getLog().disconnect();
		return null;
	}
}