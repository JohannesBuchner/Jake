package com.jakeapp.violet.actions.global;

import java.util.Collection;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.jake.fss.ProjectDir;
import com.jakeapp.violet.di.DI;
import com.jakeapp.violet.gui.Projects;

/**
 * <code>AvailableLaterObject</code> returning a list of available
 * <code>Project</code>s.
 */
public class GetProjectsAction extends
		AvailableLaterObject<Collection<ProjectDir>> {

	private static final Logger log = Logger.getLogger(GetProjectsAction.class);

	private Projects projects = DI.getImpl(Projects.class);

	public GetProjectsAction() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<ProjectDir> calculate() throws Exception {
		return projects.getAll();
	}
}