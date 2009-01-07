package com.jakeapp.core.dao;

import java.util.List;
import java.util.UUID;

import org.springframework.dao.support.DaoSupport;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.hibernate.EntityMode;
import org.hibernate.LockMode;
import org.apache.log4j.Logger;

import com.jakeapp.core.domain.Project;
import com.jakeapp.core.domain.ProjectMember;
import com.jakeapp.core.dao.exceptions.NoSuchProjectMemberException;

/**
 * A hibernate <code>ProjectMember</code> DAO
 */
public class HibernateProjectMemberDao extends HibernateDaoSupport
        implements IProjectMemberDao {
    private static Logger log = Logger.getLogger(HibernateProjectMemberDao.class);

    /**
     * {@inheritDoc}
     */
    public ProjectMember persist(Project project, ProjectMember projectMember) {
        // TODO various checks

    log.debug("transaction: " + this.getHibernateTemplate().getSessionFactory().getCurrentSession().getTransaction());

    	
        this.getHibernateTemplate().getSessionFactory().getCurrentSession().save(projectMember);

    	return projectMember;
    }

    @Override
    public ProjectMember get(UUID memberId) throws NoSuchProjectMemberException {

        List<ProjectMember> results = this.getHibernateTemplate().getSessionFactory().getCurrentSession().
                createQuery("FROM ProjectMember WHERE memberId = ?").setString(0, memberId.toString()).list();

        if(results == null)
            throw new NoSuchProjectMemberException("Results are null");

        if(results.isEmpty())
            throw new NoSuchProjectMemberException("Results are empty");

        ProjectMember result = results.get(0);
        if(result != null)
            return result;

        throw new NoSuchProjectMemberException("No ProjectMember found by this id");
    }

    /**
     * {@inheritDoc}
     */
    public List<ProjectMember> getAll(Project project) {
        List<ProjectMember> results = this.getHibernateTemplate().getSessionFactory().getCurrentSession().
                createQuery("FROM ProjectMember").list();

        return results;
    }

    /**
     * {@inheritDoc}
     */
	public void delete(Project project, ProjectMember projectMember) throws NoSuchProjectMemberException {

        ProjectMember member = this.get(projectMember.getUserId());

        try{
            log.debug("Deleting ProejctMember with ID " + projectMember.getUserId().toString());
            this.getHibernateTemplate().getSessionFactory().getCurrentSession().delete(member);
        }
        catch(DataAccessException e)
        {
            log.debug("catched DataAccessException meaning User does not exist");
            throw new NoSuchProjectMemberException(e.getMessage());
            // TODO throw exception
        }
		//TODO
	}


}
