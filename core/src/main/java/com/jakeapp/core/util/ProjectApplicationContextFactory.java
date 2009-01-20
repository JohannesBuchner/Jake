package com.jakeapp.core.util;

import java.util.*;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.ApplicationContext;

import com.jakeapp.core.dao.*;
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

    private IProjectDao projectDao;

    private List<UUID> project_uuids = new ArrayList<UUID>();


    public ProjectApplicationContextFactory(IProjectDao projectDao) {
        super();
		log.debug("Creating the ProjectApplicationContextFactory");
        this.projectDao = projectDao;

        loadProjectUUIDs();

	}

    private void loadProjectUUIDs() {
        List<Project> projects = this.projectDao.getAll();
        project_uuids.clear();
        for(Project proj : projects)
        {
            project_uuids.add(UUID.fromString(proj.getProjectId()));
        }
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

    /**
     * Get the <code>ApplicationContext</code> for a given <code>
     * Project</code>. If an
     * <code>ApplicationContext</code> for the given <code>Project</code>
     * already exists, the existing context is returned, otherwise a new context
     * will be created. This method is <code>synchronized</code>
     *
     * @param project the project for which the application context is used
     * @return the <code>ApplicationContext</code>
     */
    public synchronized ApplicationContext getApplicationContext(Project project) {

        UUID identifier = UUID.fromString(project.getProjectId());
        if(project_uuids.contains(identifier))
        {
            return getApplicationContext(identifier);
        }
        else
        {
            loadProjectUUIDs();
            if(!project_uuids.contains(identifier))
            {
                log.warn("opening invalid context!!!!!!!");
                new Exception().printStackTrace();
                System.err.print("FIX ME!");
                System.exit(1);
            }
            return getApplicationContext(identifier);
        }
    }
}
