package com.jakeapp.core.services;

import org.apache.log4j.Logger;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.exceptions.InvalidProjectException;
import com.jakeapp.core.dao.IProjectDao;

public class ProjectInvitationListener implements com.jakeapp.core.services.IProjectInvitationListener {
//	private final com.jakeapp.core.services.ProjectsManagingServiceImpl projectsManagingServiceImpl;
	private static Logger log = Logger.getLogger(ProjectInvitationListener.class);


	private IProjectDao projectDao;


	public ProjectInvitationListener(IProjectDao projectDao)
	{
		this.projectDao = projectDao;
	}


	@Override
	public void invited(UserId user, Project project) {
		log.info("got invited to Project " + project + " by " + user);
		// add Project to the global database
		try {
			project = projectDao.create(project);
		} catch (InvalidProjectException e) {
			log.error("Creating the project we were invited to failed: Project was invalid");
			throw new IllegalArgumentException(e);
		}

//		if (this.invitationListener != null)
//			this.invitationListener.invited(user, project);
	}

	@Override
	public void accepted(UserId user, Project p) {
		log.debug("Invitation for/from user " + user  + " to Project " + p  + " accepted ");


//		if (this.invitationListener != null)
//			this.invitationListener.accepted(user, p);
	}

	@Override
	public void rejected(UserId user, Project p) {
		log.debug("Invitation for/from user " + user  + " to Project " + p  + " rejected ");

//		if (this.invitationListener != null)
//			this.invitationListener.rejected(user, p);
	}
}