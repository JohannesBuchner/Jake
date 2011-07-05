package com.jakeapp.violet.actions.global;

import javax.inject.Inject;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.jake.fss.ProjectDir;
import com.jakeapp.violet.gui.Projects;

/**
 * <code>AvailableLaterObject</code> which is responsible for creating a new
 * <code>Project</code>, or deleting one (not removing its files).
 */
public class CreateDeleteProjectAction extends AvailableLaterObject<Void> {

	private ProjectDir dir;

	@Inject
	private Projects projects;

	private boolean delete;

	public CreateDeleteProjectAction(ProjectDir dir, boolean delete) {
		this.dir = dir;
		this.delete = delete;
	}

	public void setProjects(Projects projects) {
		this.projects = projects;
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