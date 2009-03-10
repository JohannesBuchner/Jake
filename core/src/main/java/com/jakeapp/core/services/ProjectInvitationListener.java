package com.jakeapp.core.services;

import org.apache.log4j.Logger;

import com.jakeapp.core.dao.IInvitationDao;
import com.jakeapp.core.domain.Invitation;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.User;
import com.jakeapp.core.domain.exceptions.InvalidProjectException;
import com.jakeapp.core.domain.logentries.ProjectJoinedLogEntry;
import com.jakeapp.core.domain.logentries.ProjectMemberInvitationRejectedLogEntry;
import com.jakeapp.core.domain.logentries.StartTrustingProjectMemberLogEntry;
import com.jakeapp.core.util.ProjectApplicationContextFactory;


public class ProjectInvitationListener implements IProjectInvitationListener {
	
	private static Logger log = Logger.getLogger(ProjectInvitationListener.class);

	private IInvitationDao invitationDao;

    private ProjectApplicationContextFactory contextFactory;

	public ProjectInvitationListener(IInvitationDao invitationDao, ProjectApplicationContextFactory contextFactory)
	{
		log.trace("Creating ProjectInvitationListener for Core");
		this.invitationDao = invitationDao;
		this.contextFactory = contextFactory;
	}


	@Override
	/**
	 * This method gets called on the client who gets invited
	 */
	public void invited(User user, Project project) {
		log.info("got invited to Project " + project + " by " + user);
		// add Project to the global database
		try {
			Invitation invitation = new Invitation(project,  user);
			this.invitationDao.create(invitation);
		} catch (InvalidProjectException e) {
			log.warn("Creating the project we were invited to failed: Project was invalid");
			throw new IllegalArgumentException(e);
		}
//		if (this.invitationListener != null)
//			this.invitationListener.invited(user, project);
	}

	@Override
	/* this is on the side of the client that sent the invitation */
	public void accepted(User user, Project p) {
		log.debug("Invitation for/from user " + user  + " to Project " + p  + " accepted ");
		// TODO add some security checks
		ProjectJoinedLogEntry logEntry = new ProjectJoinedLogEntry(p,  user);
		this.contextFactory.getUnprocessedAwareLogEntryDao(p).create(logEntry);

		StartTrustingProjectMemberLogEntry logEntry2 = new StartTrustingProjectMemberLogEntry(p.getUserId(), user);
		this.contextFactory.getUnprocessedAwareLogEntryDao(p).create(logEntry2);
		
//		StartTrustingProjectMemberLogEntry logEntry_other = new StartTrustingProjectMemberLogEntry(user, p.getUserId());
//		contextFactory.getUnprocessedAwareLogEntryDao(p).create(logEntry_other);

//		StartTrustingProjectMemberLogEntry logEntry_me = new StartTrustingProjectMemberLogEntry(p.getUserId(), p.getUserId());
//		contextFactory.getUnprocessedAwareLogEntryDao(p).create(logEntry_me);

//		log.debug("finished completing invitation.");

//		if (this.invitationListener != null)
//			this.invitationListener.accepted(user, p);
	}

	@Override
	public void rejected(User user, Project p) {
		log.debug("Invitation for/from user " + user  + " to Project " + p  + " rejected ");

		ProjectMemberInvitationRejectedLogEntry logEntry = new ProjectMemberInvitationRejectedLogEntry(user, p.getUserId());
		this.contextFactory.getUnprocessedAwareLogEntryDao(p).create(logEntry);
//		if (this.invitationListener != null)
//			this.invitationListener.rejected(user, p);
	}
}