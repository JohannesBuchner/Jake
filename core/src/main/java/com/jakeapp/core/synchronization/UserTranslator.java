package com.jakeapp.core.synchronization;

import com.jakeapp.core.dao.exceptions.NoSuchUserException;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ProtocolType;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.services.exceptions.ProtocolNotSupportedException;
import com.jakeapp.core.util.ProjectApplicationContextFactory;
import com.jakeapp.jake.ics.impl.xmpp.XmppUserId;


public class UserTranslator {

	private final ProjectApplicationContextFactory applicationContextFactory;

	public UserTranslator(ProjectApplicationContextFactory applicationContextFactory) {
		this.applicationContextFactory = applicationContextFactory;
	}

	public UserId getUserIdFromProjectMember(Project project, UserId member) {
		return null;
	}

	public UserId getProjectMemberFromUserId(Project project, UserId userid)
			throws NoSuchUserException {
		return null;
	}


	com.jakeapp.jake.ics.UserId getBackendUserIdFromDomainUserId(UserId userid) {
		if (userid.getProtocolType() == ProtocolType.XMPP) {
			return new XmppUserId(userid.getUserId());
		} else {
			throw new RuntimeException(new ProtocolNotSupportedException());
		}
	}

	com.jakeapp.jake.ics.UserId getBackendUserIdFromDomainProjectMember(Project p,
			UserId member) {
		return null; // TODO
	}


	public UserId getDomainUserIdFromBackendUserId(com.jakeapp.jake.ics.UserId userid) {
		return null; // TODO
	}

}
