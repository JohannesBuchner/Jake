package com.jakeapp.core.util;

import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import org.springframework.context.ConfigurableApplicationContext;

import com.jakeapp.core.dao.IFileObjectDao;
import com.jakeapp.core.dao.ILogEntryDao;
import com.jakeapp.core.dao.INoteObjectDao;
import com.jakeapp.core.dao.IProjectMemberDao;
import com.jakeapp.core.dao.exceptions.NoSuchProjectException;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ProjectMember;
import com.jakeapp.core.domain.TrustState;

/**
 * A factory that creates and configures spring application contexts.
 * Application contexts for a certain <code>Project</code>are only created once
 * and then reused.
 * 
 * @author Simon
 */
public class ProjectApplicationContextFactory extends ApplicationContextFactory {


    public ProjectApplicationContextFactory() {
		log.debug("Creating the ProjectApplicationContextFactory");

		this.contextTable = new Hashtable<String, ConfigurableApplicationContext>();
	}

    public ILogEntryDao getLogEntryDao(Project p) {
        return (ILogEntryDao) getApplicationContext(p).getBean("logEntryDao");
    }

    public ILogEntryDao getLogEntryDao(JakeObject jo) {
        return (ILogEntryDao) getApplicationContext(jo.getProject()).getBean(
                "logEntryDao");
    }

    public IProjectMemberDao getProjectMemberDao(Project p) {
        return (IProjectMemberDao) getApplicationContext(p).getBean("projectMemberDao");
    }

    public INoteObjectDao getNoteObjectDao(Project p) {
        return (INoteObjectDao) getApplicationContext(p).getBean("noteObjectDao");
    }

    public IFileObjectDao getFileObjectDao(Project p) {
        return (IFileObjectDao) getApplicationContext(p).getBean("fileObjectDao");
    }

    public Collection<ProjectMember> getProjectMembers(Project p)
            throws NoSuchProjectException {
        return getProjectMemberDao(p).getAll(p);
    }

    public List<ProjectMember> getTrustedProjectMembers(Project p)
            throws NoSuchProjectException {
        List<ProjectMember> allmembers = getProjectMemberDao(p).getAll(p);
        for (ProjectMember member : allmembers) {
            if (member.getTrustState() != TrustState.NO_TRUST)
                allmembers.remove(member);
		}
		return allmembers;
	}
}
