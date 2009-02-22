package com.jakeapp.core.services;

import org.apache.log4j.Logger;

import com.jakeapp.core.Injected;
import com.jakeapp.core.util.ProjectApplicationContextFactory;

/**
 * Abstract baseclass for services using an ApplicationContext.
 * Primary use is eliminating duplicate code.
 *  
 * @author djinn
 *
 */
public abstract class JakeService {
	private static final Logger log = Logger.getLogger(JakeService.class);
	
	private ProjectApplicationContextFactory applicationContextFactory;
	
	@Injected
	public JakeService(ProjectApplicationContextFactory applicationContextFactory) {
		super();
		this.setApplicationContextFactory(applicationContextFactory);
	}
	
	/**
	 * *********** GETTERS & SETTERS ************
	 */
	public ProjectApplicationContextFactory getApplicationContextFactory() {
		return this.applicationContextFactory;
	}

	public void setApplicationContextFactory(
			  ProjectApplicationContextFactory applicationContextFactory) {
		this.applicationContextFactory = applicationContextFactory;
	}

}
