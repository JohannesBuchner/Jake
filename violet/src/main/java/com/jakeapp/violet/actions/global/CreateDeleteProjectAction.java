package com.jakeapp.violet.actions.global;

import java.io.IOException;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.fss.ProjectDir;
import com.jakeapp.violet.actions.Actions;
import com.jakeapp.violet.di.DI;
import com.jakeapp.violet.gui.Projects;
import com.jakeapp.violet.model.Context;
import com.jakeapp.violet.model.ProjectModel;

/**
 * <code>AvailableLaterObject</code> which is responsible for creating a new
 * <code>Project</code>, or deleting one (not removing its files).
 */
public class CreateDeleteProjectAction extends AvailableLaterObject<Void> {

	private ProjectDir dir;

	private Projects projects = DI.getImpl(Projects.class);

	private boolean delete;

	public CreateDeleteProjectAction(ProjectDir dir, boolean delete) {
		this.dir = dir;
		this.delete = delete;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Void calculate() throws Exception {
		if (this.delete) {
			projects.remove(dir);
		} else {
			projects.add(dir);
		}
		return null;
	}
}