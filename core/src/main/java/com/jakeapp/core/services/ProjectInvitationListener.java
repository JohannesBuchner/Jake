package com.jakeapp.core.services;

import org.apache.log4j.Logger;

import com.jakeapp.core.dao.IInvitationDao;
import com.jakeapp.core.domain.Invitation;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.User;
import com.jakeapp.core.domain.exceptions.InvalidProjectException;
import com.jakeapp.core.domain.logentries.ProjectJoinedLogEntry;
import com.jakeapp.core.domain.logentries.ProjectMemberInvitationRejectedLogEntry;
import com.jakeapp.core.util.ProjectApplicationContextFactory;


public class ProjectInvitationListener implements IProjectInvitationListener {

	private static Logger log = Logger.getLogger(ProjectInvitationListener.class);

	private IInvitationDao invitationDao;

	private ProjectApplicationContextFactory contextFactory;

	public ProjectInvitationListener(IInvitationDao invitationDao, ProjectApplicationContextFactory contextFactory) {
		log.trace("Creating ProjectInvitationListener for Core");
		this.invitationDao = invitationDao;
		this.contextFactory = contextFactory;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void invited(User inviter, Project project) {
		log.info("got invited to Project " + project + " by " + inviter);
		// add Project to the global database
		try {
			Invitation invitation = new Invitation(project, inviter);
			this.invitationDao.create(invitation);
		} catch (InvalidProjectException e) {
			log.warn("Creating the project we were invited to failed: Project was invalid");
			throw new IllegalArgumentException(e);
		}
//		if (this.invitationListener != null)
//			this.invitationListener.invited(inviter, project);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void accepted(User invitee, Project p) {
		log.debug("Invitation for/from invitee " + invitee + " to Project " + p + " accepted ");
		// TODO add some security checks; see redmine bug #92
		ProjectJoinedLogEntry logEntry = new ProjectJoinedLogEntry(p, invitee);
		this.contextFactory.getUnprocessedAwareLogEntryDao(p).create(logEntry);

//		StartTrustingProjectMemberLogEntry logEntry2 = new StartTrustingProjectMemberLogEntry(p.getUserId(), invitee);
//		this.contextFactory.getUnprocessedAwareLogEntryDao(p).create(logEntry2);

//		StartTrustingProjectMemberLogEntry logEntry_other = new StartTrustingProjectMemberLogEntry(invitee, p.getUserId());
//		contextFactory.getUnprocessedAwareLogEntryDao(p).create(logEntry_other);

//		StartTrustingProjectMemberLogEntry logEntry_me = new StartTrustingProjectMemberLogEntry(p.getUserId(), p.getUserId());
//		contextFactory.getUnprocessedAwareLogEntryDao(p).create(logEntry_me);

//		log.debug("finished completing invitation.");

//		if (this.invitationListener != null)
//			this.invitationListener.accepted(invitee, p);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void rejected(User user, Project p) {
		log.debug("Invitation for/from user " + user + " to Project " + p + " rejected ");

		ProjectMemberInvitationRejectedLogEntry logEntry = new ProjectMemberInvitationRejectedLogEntry(user, p.getUserId());
		this.contextFactory.getUnprocessedAwareLogEntryDao(p).create(logEntry);
//		if (this.invitationListener != null)
//			this.invitationListener.rejected(user, p);
	}
}