package com.jakeapp.core.dao;

import java.util.List;

import org.springframework.dao.support.DaoSupport;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ProjectMember;

/**
 * A hibernate <code>ProjectMember</code> DAO
 */
public class HibernateProjectMemberDao extends HibernateDaoSupport
        implements IProjectMemberDao {
    /**
     * {@inheritDoc}
     */
    public ProjectMember persist(Project project, ProjectMember projectMember) {
        // TODO various checks

    	this.getHibernateTemplate().save(projectMember);

    	return projectMember;
    }

    /**
     * {@inheritDoc}
     */
    public List<ProjectMember> getAll(Project project) {


        List<ProjectMember> results = this.getHibernateTemplate().find("FROM ProjectMember");

        return results;
    }

    /**
     * {@inheritDoc}
     */
	public void delete(Project project, ProjectMember projectMember) {

        try{

            this.getHibernateTemplate().delete(projectMember);
        }
        catch(DataAccessException e)
        {
            // TODO throw exception
        }
		//TODO
	}


}
