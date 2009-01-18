package com.jakeapp.core.synchronization;

import com.jakeapp.core.dao.exceptions.NoSuchProjectMemberException;
import com.jakeapp.core.dao.exceptions.NoSuchUserIdException;
import com.jakeapp.core.dao.IUserIdDao;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ProjectMember;
import com.jakeapp.core.domain.ProtocolType;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.exceptions.InvalidUserIdException;
import com.jakeapp.core.services.MsgService;
import com.jakeapp.core.services.exceptions.ProtocolNotSupportedException;
import com.jakeapp.core.util.ApplicationContextFactory;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;


public class UserTranslator {

	private final ApplicationContextFactory applicationContextFactory;

	private final IUserIdDao userIdDao;


	public UserTranslator(ApplicationContextFactory applicationContextFactory,
			IUserIdDao userIdDao) {
		this.applicationContextFactory = applicationContextFactory;
		this.userIdDao = userIdDao;
	}

	/**
	 * TODO I _NEED_ the xmpp id!
	 * 
	 * @param project
	 * @param member
	 * @return
	 */
	public UserId getUserIdFromProjectMember(Project project, ProjectMember member) {
		try {
			return this.userIdDao.get(member.getUserId());
		} catch (InvalidUserIdException e) {
			return null;
		} catch (NoSuchUserIdException e) {
			return null;
		}
	}

	public ProjectMember getProjectMemberFromUserId(Project project, UserId userid)
			throws NoSuchProjectMemberException {
		return this.applicationContextFactory.getProjectMemberDao(project).get(
				userid.getUuid());
	}


	public com.jakeapp.jake.ics.UserId getBackendUserIdFromDomainUserId(UserId userid) {
		if (userid.getProtocolType() == ProtocolType.XMPP) {
			return new XmppUserId(userid.getUserId());
		} else {
			throw new RuntimeException(new ProtocolNotSupportedException());
		}
	}

	public com.jakeapp.jake.ics.UserId getBackendUserIdFromDomainProjectMember(Project p,
			ProjectMember member) {
		return getBackendUserIdFromDomainUserId(getUserIdFromProjectMember(p, member));
	}
}
