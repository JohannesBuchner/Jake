package com.jakeapp.violet.actions.project;

import org.apache.log4j.Logger;

import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.violet.model.ProjectModel;

/**
 * Logs the project out
 */
public class LogoutAction extends AvailableLaterObject<Void> {

	private static final Logger log = Logger.getLogger(LogoutAction.class);

	private ProjectModel model;

	public LogoutAction(ProjectModel model) {
		this.model = model;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Void calculate() throws Exception {
		log.debug("logging out ...");
		model.getIcs().getStatusService().logout();
		log.debug("logged out ...");
		return null;
	}
}