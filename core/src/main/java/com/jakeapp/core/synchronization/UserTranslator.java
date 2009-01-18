package com.jakeapp.core.synchronization;

import com.jakeapp.core.dao.exceptions.NoSuchProjectMemberException;
import com.jakeapp.core.dao.exceptions.NoSuchUserIdException;
import com.jakeapp.core.dao.IUserIdDao;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ProjectMember;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.exceptions.InvalidUserIdException;
import com.jakeapp.core.services.MsgService;
import com.jakeapp.core.util.ApplicationContextFactory;


public class UserTranslator {

    private final ApplicationContextFactory applicationContextFactory;

    private final IUserIdDao userIdDao;



    public UserTranslator(ApplicationContextFactory applicationContextFactory, IUserIdDao userIdDao) {
        this.applicationContextFactory = applicationContextFactory;
        this.userIdDao = userIdDao;
    }

    /**
     * TODO
     * I _NEED_ the xmpp id!
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
        return applicationContextFactory.getProjectMemberDao(project).get(
                userid.getUuid());
	}
}
