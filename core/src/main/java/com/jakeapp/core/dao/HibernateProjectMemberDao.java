package com.jakeapp.core.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.support.DaoSupport;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ProjectMember;

/**
 * A hibernate <code>ProjectMember</code> DAO
 */
public class HibernateProjectMemberDao extends DaoSupport
        implements IProjectMemberDao {
    private static Logger log = Logger.getLogger(HibernateProjectMemberDao.class);
    
    /**
     * {@inheritDoc}
     */
    public ProjectMember persist(Project project, ProjectMember projectMember) {
    	// TODO
    	return projectMember;
    }

    /**
     * {@inheritDoc}
     */
    public List<ProjectMember> getAll(Project project) {
    	// TODO
    	return null;
    }

    /**
     * {@inheritDoc}
     */
	public void makeTransient(Project project, ProjectMember projectMember) {
		//TODO
	}

	@Override
	protected void checkDaoConfig() throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}
}
