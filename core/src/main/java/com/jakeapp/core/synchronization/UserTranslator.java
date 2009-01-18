package com.jakeapp.core.synchronization;

import com.jakeapp.core.dao.exceptions.NoSuchProjectMemberException;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ProjectMember;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.services.MsgService;
import com.jakeapp.core.util.ApplicationContextFactory;


public class UserTranslator {

	private ApplicationContextFactory applicationContextFactory;

	public UserTranslator(ApplicationContextFactory applicationContextFactory) {
		this.applicationContextFactory = applicationContextFactory;
	}

	/** TODO
	 * I _NEED_ the xmpp id!
	 * @param project
	 * @param member
	 * @return
	 */
	public UserId getUserIdFromProjectMember(Project project, ProjectMember member) {
		return null;
	}

	public ProjectMember getProjectMemberFromUserId(Project project, UserId userid)
			throws NoSuchProjectMemberException {
		return applicationContextFactory.getProjectMemberDao(project).get(
				userid.getUuid());
	}
}
